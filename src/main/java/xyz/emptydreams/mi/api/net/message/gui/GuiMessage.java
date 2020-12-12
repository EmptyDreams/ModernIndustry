package xyz.emptydreams.mi.api.net.message.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * GUI网络通信<br>
 * <pre>附加信息：
 *  处理端：服务端、客户端</pre>
 * @author EmptyDreams
 */
public class GuiMessage implements IMessageHandle<GuiAddition> {
	
	private static final GuiMessage INSTANCE = new GuiMessage();
	
	public static GuiMessage instance() {
		return INSTANCE;
	}
	
	private GuiMessage() {}
	
	@Override
	public boolean parseOnClient(@Nonnull NBTTagCompound message) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		Container container = player.openContainer;
		if (!(container instanceof MIFrame)) {
			message.setBoolean("cast", false);
			return false;
		}
		GuiAddition addition = new GuiAddition();
		addition.readFrom(message);
		MIFrame frame = (MIFrame) container;
		if (!frame.getID().equals(addition.getGuiID())) return true;
		frame.receive(message.getCompoundTag("data"), addition.getId());
		return true;
	}
	
	@Override
	public boolean parseOnServer(@Nonnull NBTTagCompound message) {
		GuiAddition addition = new GuiAddition();
		addition.readFrom(message);
		Container container = addition.getPlayer().openContainer;
		if (!(container instanceof MIFrame)) {
			message.setBoolean("cast", false);
			return false;
		}
		MIFrame frame = (MIFrame) container;
		if (!frame.getID().equals(addition.getGuiID())) return true;
		frame.receive(message.getCompoundTag("data"), addition.getId());
		return true;
	}
	
	@Override
	public boolean match(@Nonnull NBTTagCompound message) {
		return message.hasKey("type_gui");
	}
	
	@Override
	public boolean match(@Nonnull Side side) {
		return true;
	}
	
	@Nonnull
	@Override
	public NBTTagCompound packaging(@Nonnull NBTTagCompound data, @Nullable GuiAddition addition) {
		NBTTagCompound result = new NBTTagCompound();
		result.setBoolean("type_gui", false);
		result.setTag("data", data);
		addition.writeTo(result);
		return result;
	}
	
	@Nonnull
	@Override
	public String getInfo(NBTTagCompound message) {
		if (message.hasKey("cast")) return "玩家(" + message.getString("player") + ")打开的GUI不继承自MIFrame";
		return "GuiMessage解析发生错误！(" + message.getString("player") + ")";
	}
	
}
