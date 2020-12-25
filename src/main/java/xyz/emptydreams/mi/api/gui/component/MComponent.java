package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.listener.IListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseActionListener;
import xyz.emptydreams.mi.api.gui.listener.mouse.MouseListenerTrigger;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.StringUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 一般组件的父类
 * @author EmptyDreams
 */
public abstract class MComponent implements IComponent {
	
	/** 基本信息 */
	protected int x, y, width, height;
	/** 网络传输ID */
	private int code;
	/** 存储事件列表 */
	private final List<IListener> listeners = new LinkedList<>();
	/** 是否支持CraftShower */
	private CraftGuide<?, ?> craftGuide = null;
	/** 存储加载过的窗体 */
	private final List<MIFrame> LOADED = new LinkedList<>();
	
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
	public List<IListener> getListeners() {
		return new ArrayList<>(listeners);
	}
	
	@Override
	public <T extends IListener> void activateListener(MIFrame frame, Class<T> name, Consumer<T> consumer) {
		int index = 0;
		NBTTagCompound data = new NBTTagCompound();
		for (IListener listener : listeners) {
			if (name.isAssignableFrom(listener.getClass())) {
				//noinspection unchecked
				consumer.accept((T) listener);
				if (WorldUtil.isClient()) {
					NBTTagCompound info = listener.writeTo();
					if (info != null)
						data.setTag(String.valueOf(index), info);
				}
			}
			++index;
		}
		//如果事件在客户端触发并且需要进行网络传输则发送消息给服务端
		//如果事件在服务端触发不需要发送给客户端，因为在服务端触发的事件大部分在客户端也可以触发
		if (data.getSize() > 0) sendToServer(frame, data);
	}
	
	@Override
	public void receive(NBTTagCompound data) {
		try {
			for (String key : data.getKeySet()) {
				int index = Integer.parseInt(key);
				IListener listener = listeners.get(index);
				listener.readFrom(data.getCompoundTag(key));
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
	public boolean removeListenerIf(Predicate<IListener> test) {
		return listeners.removeIf(test);
	}
	
	@Override
	public boolean removeListener(IListener listener) {
		return listeners.remove(listener);
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
	public void setCraftButton(CraftGuide<?, ?> craft) {
		craftGuide = craft;
	}
	/** 移除合成表按钮，<b>仅在添加到GUI前有效</b> */
	@SuppressWarnings("unused")
	public void deleteCraftButton() {
		craftGuide = null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public abstract void paint(@Nonnull Graphics g);
	
	/**
	 * 在服务端或客户端第一次将控件添加到窗体时调用.
	 * @param frame 窗体对象
	 * @param player 玩家对象
	 */
	protected void init(MIFrame frame, EntityPlayer player, CraftButton button) {
		if (craftGuide != null) {
			registryListener((MouseActionListener) (mouseX, mouseY) ->
					MouseListenerTrigger.activateAction(frame, button, mouseX, mouseY));
		}
	}
	
	/**
	 * {@inheritDoc}<br>
	 * <b>子类重写该方法时务必使用{@code super.onAddToGUI}调用该方法，
	 *      否则会导致部分功能无法正常工作</b>
	 */
	@Override
	public void onAddToGUI(MIFrame con, EntityPlayer player) {
		CraftButton button = null;
		if (craftGuide != null) {
			button = new CraftButton(craftGuide, this, player);
			con.add(button, player);
		}
		for (MIFrame frame : LOADED) {
			if (frame.getID().hashCode() == con.getID().hashCode()
				&& frame.getID().equals(con.getID())) return;
		}
		LOADED.add(con);
		if (button != null) init(con, player, button);
	}
	
	/**
	 * {@inheritDoc}<br>
	 * <b>子类重写该方法时务必使用{@code super.onAddToGUI}调用该方法，
	 *      否则会导致部分功能无法正常工作</b>
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void onAddToGUI(StaticFrameClient con, EntityPlayer player) {
		MIFrame client = con.getInventorySlots();
		for (MIFrame frame : LOADED) {
			if (frame.getID().hashCode() == client.getID().hashCode()
					&& frame.getID().equals(client.getID())) return;
		}
		LOADED.add(client);
	}
	
	@Override
	public void onRemoveFromGUI(Container con) { }
	
	@Override
	public int getCode() {
		return code;
	}
	
	@Override
	public void setCodeStart(int code) {
		this.code = code;
	}
	
}
