package minedreams.mi.api.electricity.info;

import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.api.exception.ProtocolErrorException;
import net.minecraft.tileentity.TileEntity;

/**
 * 存储电力方块的信息，所有可以连接电线的方块都应该实现这个接口
 * @author EmptyDremas
 * @version 1.0
 */
public interface IEleInfo {

	/**
	 * 判断方块是否可以连接电线，
	 * <b>
	 *     注意：此方法不保证fromPos/nowPos在此时已经在世界存在，所以提供了额外的参数，</b>
	 * 同时这个类不允许调用{@link ElectricityTransfer#canLink(TileEntity)}，
	 * 因为ET的canLink此方法可能依赖{@link ElectricityTransfer#canLink(TileEntity)}
	 *
	 * @param info 附加信息
	 * @param nowIsExist 当前方块是否存在
	 * @param fromIsExist 调用方块是否存在
	 *
	 * @throws ProtocolErrorException 当调用方法没有遵循类的继承结构时可能抛出此异常
	 */
	boolean canLink(LinkInfo info, boolean nowIsExist, boolean fromIsExist);
	
}
