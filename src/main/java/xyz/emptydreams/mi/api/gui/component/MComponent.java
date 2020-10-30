package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.gui.client.StaticFrameClient;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.listener.IListener;
import xyz.emptydreams.mi.api.net.handler.MessageSender;
import xyz.emptydreams.mi.api.net.message.gui.GuiAddition;
import xyz.emptydreams.mi.api.net.message.gui.GuiMessage;
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
	public <T extends IListener> void activateListener(Class<T> name, Consumer<T> consumer) {
		int index = 0;
		NBTTagCompound data = new NBTTagCompound();
		for (IListener listener : listeners) {
			if (name.isAssignableFrom(listener.getClass())) {
				//noinspection unchecked
				consumer.accept((T) listener);
				if (WorldUtil.isClient(null)) {
					NBTTagCompound info = listener.writeTo();
					if (info != null) data.setTag(String.valueOf(index), info);
				}
			}
			++index;
		}
		if (WorldUtil.isClient(null) && data.getSize() > 0) {
			GuiAddition addition = new GuiAddition(getClientPlayer());
			IMessage message = GuiMessage.instance().create(data, addition);
			MessageSender.sendToServer(message);
		}
	}
	
	@Override
	public void receive(NBTTagCompound data) {
		for (String key : data.getKeySet()) {
			int index = Integer.parseInt(key);
			IListener listener = listeners.get(index);
			listener.readFrom(data.getCompoundTag("key"));
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
	
	@Override
	@SideOnly(Side.CLIENT)
	public abstract void paint(@Nonnull Graphics g);
	
	@Override
	public void onAddToGUI(MIFrame con, EntityPlayer player) { }

	@Override
	@SideOnly(Side.CLIENT)
	public void onAddToGUI(StaticFrameClient con, EntityPlayer player) { }

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
	
	private static EntityPlayer getClientPlayer() {
		try {
			Class<?> clazz = Class.forName("net.minecraft.client.Minecraft");
			Object mc = clazz.getMethod("getMinecraft").invoke(null, (Object[]) null);
			return (EntityPlayer) clazz.getField("player").get(mc);
		} catch (Throwable e) {
			throw new RuntimeException("获取客户端玩家对象时出错", e);
		}
	}
	
}
