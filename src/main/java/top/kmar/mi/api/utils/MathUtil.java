package top.kmar.mi.api.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import top.kmar.mi.api.utils.data.math.Point2D;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * 有关数学的计算
 * @author EmptyDreams
 */
public final class MathUtil {
	
	private static final Random RANDOM = new Random();
	
	/**
	 * 获取玩家朝向
	 * @param player 玩家
	 * @param pos 被放置的方块的坐标（也可以理解为被玩家点击的方块向被点击的方向的反方向移动一格的方块坐标）
	 */
	@SuppressWarnings("DuplicateExpressions")
	@Nonnull
	public static EnumFacing getPlayerFacing(EntityPlayer player, BlockPos pos) {
		double x = player.posX - pos.getX();
		double y = player.posZ - pos.getZ();
		if (pos.getY() < player.posY || player.posY + player.height < pos.getY()) {
			if (Math.sqrt(x * x + y * y) <= 1.8) {
				//如果玩家和方块间的水平距离小于1.8
				return pos.getY() < player.posY ? EnumFacing.DOWN : EnumFacing.UP;
			} else {
				//如果玩家和方块间的水平距离大于1.8
				return player.getHorizontalFacing();
			}
		} else if (pos.getY() == (int) player.posY || pos.getY() == (int) player.posY + 1) {
			//如果玩家和方块在同一水平面上
			if (Math.sqrt(x * x + y * y) > 1.8 || Math.abs(player.rotationPitch) < 40) {
				return player.getHorizontalFacing();
			}
			if (player.rotationPitch < -8.3) return EnumFacing.UP;
			if (player.rotationPitch > 8.3) return EnumFacing.DOWN;
		}
		//如果玩家和方块大致处于同一平面
		return player.getHorizontalFacing();
	}
	
	/** 获取随机数 */
	@Nonnull
	public static Random random() {
		return RANDOM;
	}
	
	/**
	 * 计算出一个点数大于等于amount的正方形矩阵的边长
	 * @param amount 点数
	 * @return 正方形矩阵的边长
	 */
	public static int amount2Rec(int amount) {
		for (int i = 1; i < amount; ++i) {
			int result = i * i;
			if (result >= amount) return i;
		}
		return -1;
	}
	
	/**
	 * 将一维下标转换为二维矩阵坐标
	 * @param index 一维下标
	 * @param width 矩阵宽度
	 * @return 二维矩阵坐标
	 */
	@Nonnull
	public static Point2D indexToMatrix(int index, int width) {
		int x = index % width;
		int y = index / width;
		return new Point2D(x, y);
	}
	
}