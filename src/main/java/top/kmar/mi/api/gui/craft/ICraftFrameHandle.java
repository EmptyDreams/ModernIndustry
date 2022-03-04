package top.kmar.mi.api.gui.craft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.kmar.mi.api.gui.client.StaticFrameClient;

import javax.annotation.Nullable;

/**
 * @author EmptyDreams
 */
public interface ICraftFrameHandle {
	
	/**
	 * 构建一个客户端的窗体对象
	 * @param world 所在世界
	 * @param player 打开GUI的玩家
	 * @param pos 方块所在坐标
	 * @return 返回null则不做反应
	 */
	@Nullable
	@SideOnly(Side.CLIENT)
    StaticFrameClient createFrame(World world, EntityPlayer player, BlockPos pos);
	
}