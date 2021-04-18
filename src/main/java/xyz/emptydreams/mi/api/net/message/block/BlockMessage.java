package xyz.emptydreams.mi.api.net.message.block;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.net.IAutoNetwork;
import xyz.emptydreams.mi.api.net.ParseResultEnum;
import xyz.emptydreams.mi.api.net.message.IMessageHandle;
import xyz.emptydreams.mi.api.utils.MISysInfo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static xyz.emptydreams.mi.api.net.ParseResultEnum.SUCCESS;
import static xyz.emptydreams.mi.api.net.ParseResultEnum.THROW;

/**
 * 方块信息处理<br>
 * <pre>附加信息：
 *  处理端：服务端、客户端</pre>
 *
 * @author EmptyDreams
 */
public final class BlockMessage implements IMessageHandle<BlockAddition> {
	
	private static final BlockMessage instance = new BlockMessage();
	
	public static BlockMessage instance() {
		return instance;
	}
	
	private BlockMessage() { }
	
	@Override
	@SideOnly(Side.CLIENT)
	public ParseResultEnum parseOnClient(@Nonnull IDataReader message) {
		BlockAddition addition = new BlockAddition();
		addition.readFrom(message);
		TileEntity te = addition.getWorld().getTileEntity(addition.getPos());
		if (!(te instanceof IAutoNetwork)) {
			MISysInfo.err("目标方块" + addition.getPos() + "的TE没有实现IAutoNetwork接口");
			return THROW;
		}
		((IAutoNetwork) te).receive(message.readData());
		return SUCCESS;
	}
	
	@Override
	public ParseResultEnum parseOnServer(@Nonnull IDataReader message) {
		BlockAddition addition = new BlockAddition();
		addition.readFrom(message);
		TileEntity te = addition.getWorld().getTileEntity(addition.getPos());
		if (!(te instanceof IAutoNetwork)) {
			MISysInfo.err("目标方块" + addition.getPos() + "的TE没有实现IAutoNetwork接口");
			return THROW;
		}
		((IAutoNetwork) te).receive(message.readData());
		return SUCCESS;
	}
	
	@Override
	public boolean match(@Nonnull Side side) {
		return true;
	}
	
	@Nonnull
	@Override
	public IDataReader packaging(@Nonnull IDataReader data, @Nullable BlockAddition addition) {
		ByteDataOperator operator = new ByteDataOperator(data.size() + 10);
		//noinspection ConstantConditions
		addition.writeTo(operator);
		operator.writeData(data);
		return operator;
	}
	
}