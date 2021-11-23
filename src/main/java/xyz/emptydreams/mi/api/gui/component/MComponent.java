package xyz.emptydreams.mi.api.gui.component;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.ChildFrame;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponentManager;
import xyz.emptydreams.mi.api.gui.craft.CraftShower;
import xyz.emptydreams.mi.api.gui.listener.IListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.IMouseClickListener;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 一般组件的父类
 * @author EmptyDreams
 */
public abstract class MComponent implements IComponent {
	
	/** 基本信息 */
	protected int x, y, width, height;
	/** 网络传输ID */
	protected int code;
	/** 存储事件列表 */
	protected final List<IListener> listeners = new LinkedList<>();
	/** 是否支持CraftShower */
	protected CraftGuide<?, ?> craftGuide = null;
	/** CraftShower用到的填充表 */
	protected Function<TileEntity, SlotGroup> slotGroupGetter = null;
	/** 存储加载过的窗体 */
	protected final List<String> LOADED = new LinkedList<>();
	
	@Override
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void setSize(int width, int height) {
		if (width < 0) throw new IllegalArgumentException("width[" + width + "] < 0");
		if (height < 0) throw new IllegalArgumentException("height[" + height + "] < 0");
		this.width = width;
		this.height = height;
	}
	
	@Override
	public IListener getListener(int index) {
		return listeners.get(index);
	}
	
	@Override
	public <T extends IListener> void activateListener(MIFrame frame, Class<T> name, Consumer<T> consumer) {
		activateListener(frame, name, consumer, 0);
	}
	
	public <T extends IListener> void activateListener(
			MIFrame frame, Class<T> name, Consumer<T> consumer, int indexStart) {
		boolean send = false;
		IntList indexs = new IntArrayList(listeners.size() / 2);
		ByteDataOperator operator = new ByteDataOperator();
		for (IListener listener : listeners) {
			if (name.isAssignableFrom(listener.getClass())) {
				//noinspection unchecked
				consumer.accept((T) listener);
				if (WorldUtil.isClient()) {
					if (listener.writeTo(operator)) {
						send = true;
						indexs.add(indexStart);
					}
				}
			}
			++indexStart;
		}
		//如果事件在客户端触发并且需要进行网络传输则发送消息给服务端
		//如果事件在服务端触发不需要发送给客户端，因为在服务端触发的事件大部分在客户端也可以触发
		if (send) {
			ByteDataOperator data = new ByteDataOperator(operator.size() + indexs.size() * 2);
			data.writeVarIntArray(indexs.toIntArray());
			data.writeData(operator);
			sendToServer(frame, data);
		}
	}
	
	@Override
	public void receive(IDataReader reader) {
		try {
			int[] indexs = reader.readVarIntArray();
			IDataReader data = reader.readData();
			for (int i : indexs) {
				IListener listener = getListener(i);
				listener.readFrom(data);
			}
		} catch (IndexOutOfBoundsException e) {
			MISysInfo.err("[MComponent]事件网络通讯异常，key值超出范围：" + e.getMessage());
		}
	}
	
	@Override
	public boolean registryListener(IListener listener) {
		return listeners.add(StringUtil.checkNull(listener, "listener"));
	}
	
	@Override
	public int getY() { return y; }
	@Override
	public int getX() { return x; }
	@Override
	public int getHeight() { return height; }
	@Override
	public int getWidth() { return width; }
	
	/** 为当前按钮设置合成表按钮 */
	public void setCraftButton(CraftGuide<?, ?> craft, Function<TileEntity, SlotGroup> slotGroupGetter) {
		craftGuide = craft;
		this.slotGroupGetter = slotGroupGetter;
	}
	/** 移除合成表按钮，<b>仅在添加到GUI前有效</b> */
	@SuppressWarnings("unused")
	public void deleteCraftButton() {
		craftGuide = null;
		slotGroupGetter = null;
	}
	
	private boolean isInit = false;
	
	/**
	 * <p>在服务端或客户端第一次将控件添加到窗体时调用
	 * <p>注意：该方法对于每一个manager都会运行
	 * @param manager 管理类
	 */
	protected void initForManager(IComponentManager manager) {
		if (!isInit) {
			isInit = true;
			initSelfResources(manager.getFrame().getPlayer());
		}
	}
	
	/**
	 * <p>初始化控件的自身资源
	 * <p>与{@link #initForManager(IComponentManager)}不同的是该方法仅运行一次
	 */
	protected void initSelfResources(EntityPlayer player) {
		if (craftGuide != null) {
			//noinspection ConstantConditions
			registryListener((IMouseClickListener) (mouseX, mouseY, mouseButton) ->
					CraftShower.show(craftGuide, ChildFrame.getGuiTileEntity(player).getPos(), slotGroupGetter));
		}
	}
	
	/**
	 * <p>{@inheritDoc}
	 * <b>子类重写该方法时务必使用{@code super.onAdd2Manager}调用该方法，
	 *      否则会导致部分功能无法正常工作</b>
	 */
	@Override
	public void onAdd2Manager(IComponentManager manager) {
		MIFrame frame = manager.getFrame();
		if (LOADED.contains(frame.getID())) return;
		LOADED.add(frame.getID());
		initForManager(manager);
	}
	
	@Override
	public void onAdd2ClientFrame(StaticFrameClient frame) { }
	
	@Override
	public int getCode() {
		return code;
	}
	
	@Override
	public void setCodeStart(int code) {
		this.code = code;
	}
	
}