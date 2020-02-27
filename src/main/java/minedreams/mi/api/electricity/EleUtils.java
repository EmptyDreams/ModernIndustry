package minedreams.mi.api.electricity;

import minedreams.mi.api.electricity.info.IEleInfo;
import minedreams.mi.api.electricity.info.LinkInfo;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 电力系统工具类，其中包含了所有可用的静态函数
 * @author EmptyDremas
 * @version V1.0
 */
public final class EleUtils {
	
	/**
	 * 判断方块是否可以连接电线，
	 * <b>注意：此方法不保证fromPos/nowPos在此时已经在世界存在，所以提供了额外的参数</b>
	 * @param info 附加信息
	 * @param nowIsExist 当前方块是否存在
	 * @param fromIsExist 调用方块是否存在
	 */
	public static boolean canLink(LinkInfo info, boolean nowIsExist, boolean fromIsExist) {
		if (info.nowBlock instanceof IEleInfo) {
			return ((IEleInfo) info.nowBlock).canLink(info, nowIsExist, fromIsExist);
		}
		return false;
	}
}
