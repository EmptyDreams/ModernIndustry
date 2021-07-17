package xyz.emptydreams.mi.api.net.message.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.gui.common.MIFrame;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;
import xyz.emptydreams.mi.api.net.message.ParseAddition;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static xyz.emptydreams.mi.api.net.ParseResultEnum.SUCCESS;
import static xyz.emptydreams.mi.api.net.ParseResultEnum.THROW;

/**
 * GUI网络通信<br>
 * <pre>附加信息：
 *  处理端：服务端、客户端</pre>
 * @author EmptyDreams
 */
public class GuiMessage implements IMessageHandle<GuiAddition, ParseAddition> {
	
	private static final GuiMessage INSTANCE = new GuiMessage();
	
	public static GuiMessage instance() {
		return INSTANCE;
	}
	
	private GuiMessage() {}
	
	@Override
	public ParseAddition parseOnClient(@Nonnull IDataReader message, ParseAddition result) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		Container container = player.openContainer;
		if (!(container instanceof MIFrame)) {
			MISysInfo.err("玩家(" + player.getName() + ")打开的GUI不继承自MIFrame");
			return result.setParseResult(THROW);
		}
		GuiAddition addition = new GuiAddition();
		addition.readFrom(message);
		MIFrame frame = (MIFrame) container;
		if (!frame.getID().equals(addition.getGuiID())) return result.setParseResult(SUCCESS);
		frame.receive(message.readData(), addition.getId());
		return result.setParseResult(SUCCESS);
	}
	
	@Override
	public ParseAddition parseOnServer(@Nonnull IDataReader message, ParseAddition result) {
		GuiAddition addition = new GuiAddition();
		addition.readFrom(message);
		Container container = addition.getPlayer().openContainer;
		if (!(container instanceof MIFrame)) {
			MISysInfo.err("玩家(" + addition.getPlayer().getName() + ")打开的GUI不继承自MIFrame");
			return result.setParseResult(THROW);
		}
		MIFrame frame = (MIFrame) container;
		if (!frame.getID().equals(addition.getGuiID())) return result.setParseResult(SUCCESS);
		frame.receive(message.readData(), addition.getId());
		return result.setParseResult(SUCCESS);
	}
	
	@Override
	public boolean match(@Nonnull Side side) {
		return true;
	}
	
	@SuppressWarnings("ConstantConditions")
	@Nonnull
	@Override
	public IDataReader packaging(@Nonnull IDataReader data, @Nullable GuiAddition addition) {
		ByteDataOperator result = new ByteDataOperator(data.size() + 10);
		addition.writeTo(result);
		result.writeData(data);
		return result;
	}
	
}