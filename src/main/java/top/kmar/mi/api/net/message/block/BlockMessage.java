package top.kmar.mi.api.net.message.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.net.IAutoNetwork;
import top.kmar.mi.api.net.ParseResultEnum;
import top.kmar.mi.api.net.message.IMessageHandle;
import top.kmar.mi.api.net.message.ParseAddition;
import top.kmar.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;

/**
 * 方块信息处理<br>
 * <pre>附加信息：
 *  处理端：服务端、客户端</pre>
 *
 * @author EmptyDreams
 */
public final class BlockMessage implements IMessageHandle<BlockAddition, ParseAddition> {
	
	private static final BlockMessage instance = new BlockMessage();
	
	public static BlockMessage instance() {
		return instance;
	}
	
	private BlockMessage() { }
	
	@Override
	@SideOnly(Side.CLIENT)
	public ParseAddition parseOnClient(@Nonnull NBTTagCompound message, ParseAddition result) {
		BlockAddition addition = new BlockAddition();
		addition.readFrom(message.getTag("add"));
		World world = addition.getWorld();
		BlockPos pos = addition.getPos();
		if (!world.isBlockLoaded(pos)) {
			MISysInfo.print("区块未加载");
		}
		TileEntity te = world.getTileEntity(pos);
		if (te == null && result.getAmount() <= 1200) return result.setParseResult(ParseResultEnum.RETRY);
		if (!(te instanceof IAutoNetwork)) {
			MISysInfo.err("目标方块" + pos + "的TE没有实现IAutoNetwork接口");
			return result.setParseResult(ParseResultEnum.THROW);
		}
		((IAutoNetwork) te).receive(message.getTag("data"));
		return result.setParseResult(ParseResultEnum.SUCCESS);
	}
	
	@Override
	public ParseAddition parseOnServer(@Nonnull NBTTagCompound message, ParseAddition result) {
		BlockAddition addition = new BlockAddition();
		addition.readFrom(message.getTag("add"));
		TileEntity te = addition.getWorld().getTileEntity(addition.getPos());
		if (!(te instanceof IAutoNetwork)) {
			MISysInfo.err("目标方块" + addition.getPos() + "的TE没有实现IAutoNetwork接口");
			return result.setParseResult(ParseResultEnum.THROW);
		}
		((IAutoNetwork) te).receive(message.getTag("data"));
		return result.setParseResult(ParseResultEnum.SUCCESS);
	}
	
	@Override
	public boolean match(@Nonnull Side side) {
		return true;
	}
	
}