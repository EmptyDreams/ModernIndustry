package minedreams.mi.api.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * {@link GuiLoader}同过该类为方块创建UI对象
 *
 * @author EmptyDreams
 * @version V1.0
 */
public interface IContainerCreater {
	
	/**
	 * 创建一个MIFrame对象
	 * @param world 所在世界
	 * @param player 玩家对象
	 * @param pos 方块坐标
	 * @return 一个完成创建的完整对象
	 */
	MIFrame createService(World world, EntityPlayer player, BlockPos pos);
	
	/**
	 * 创建一个MIFrameClient的客户端对象
	 * @param world 所在世界
	 * @param player 玩家对象
	 * @param pos 方块坐标
	 * @return 一个完成创建的完整对象
	 */
	MIFrameClient createClient(World world, EntityPlayer player, BlockPos pos);
	
}
