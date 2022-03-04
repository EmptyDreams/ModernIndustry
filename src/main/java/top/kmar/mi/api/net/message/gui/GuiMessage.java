package top.kmar.mi.api.net.message.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import top.kmar.mi.api.dor.ByteDataOperator;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.net.ParseResultEnum;
import top.kmar.mi.api.utils.MISysInfo;
import top.kmar.mi.api.gui.common.MIFrame;
import top.kmar.mi.api.net.message.IMessageHandle;
import top.kmar.mi.api.net.message.ParseAddition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
			return result.setParseResult(ParseResultEnum.THROW);
		}
		GuiAddition addition = new GuiAddition();
		addition.readFrom(message);
		MIFrame frame = (MIFrame) container;
		if (!frame.getID().equals(addition.getGuiID())) return result.setParseResult(ParseResultEnum.SUCCESS);
		frame.receive(message.readData(), addition.getId());
		return result.setParseResult(ParseResultEnum.SUCCESS);
	}
	
	@Override
	public ParseAddition parseOnServer(@Nonnull IDataReader message, ParseAddition result) {
		GuiAddition addition = new GuiAddition();
		addition.readFrom(message);
		Container container = addition.getPlayer().openContainer;
		if (!(container instanceof MIFrame)) {
			MISysInfo.err("玩家(" + addition.getPlayer().getName() + ")打开的GUI不继承自MIFrame");
			return result.setParseResult(ParseResultEnum.THROW);
		}
		MIFrame frame = (MIFrame) container;
		if (!frame.getID().equals(addition.getGuiID())) return result.setParseResult(ParseResultEnum.SUCCESS);
		frame.receive(message.readData(), addition.getId());
		return result.setParseResult(ParseResultEnum.SUCCESS);
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