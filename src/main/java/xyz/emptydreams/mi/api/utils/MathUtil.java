package xyz.emptydreams.mi.api.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.utils.data.math.Point2D;

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
		double y = player.posY - pos.getY();
		if (pos.getY() < player.posY || player.posY + player.height < pos.getY()) {
			if (Math.sqrt(x * x + y * y) <= 1.8) {
				//如果玩家和方块间的水平距离小于1.8
				return pos.getY() < player.posY ? EnumFacing.DOWN : EnumFacing.UP;
			} else {
				//如果玩家和方块间的水平距离大于1.8
				return player.getHorizontalFacing();
			}
		} else if (pos.getY() == player.posY) {
			//如果玩家和方块在同一水平面上
			if (Math.sqrt(x * x + y * y) < 1.3) {
				return pos.getY() < player.posY ? EnumFacing.DOWN : EnumFacing.UP;
			}
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
	 * 将int[]压缩成字符串
	 * @param ints 要压缩的int[]
	 * @return  <p>格式："[数据1]*[数量]"（如果数量为1则没有"*[数量]"）
	 *          <p>例如：{0, 0, 0, 5, 1, 1} -> "0*3,5,1*2"
	 *          <p>如果int[]为null或int[]长度为0，则返回"null"
	 */
	@Nonnull
	public static String compressArray2String(int[] ints) {
		if (ints == null || ints.length == 0) return "null";
		StringBuilder sb = new StringBuilder();
		int pre = ints[0];
		int amount = 1;
		sb.append(pre);
		for (int i = 1; i < ints.length; i++) {
			if (ints[i] == pre) {
				++amount;
				continue;
			}
			if (amount != 1) sb.append('*').append(amount);
			sb.append(ints[i]);
			amount = 1;
			pre = ints[i];
		}
		if (amount != 1) sb.append('*').append(amount);
		return sb.toString();
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
	 * 判断鼠标是否在指定的组件内，鼠标坐标必须与组件坐标使用同一坐标系
	 * @param mouseX 鼠标X轴坐标
	 * @param mouseY 鼠标Y轴坐标
	 * @param component 组件
	 */
	public static boolean checkMouse2DRec(float mouseX, float mouseY, IComponent component) {
		return checkMouse2DRec(mouseX, mouseY, component.getX(), component.getY(),
				component.getWidth(), component.getHeight());
	}
	
	/**
	 * 判断鼠标是否在指定矩形内
	 * @param mouseX 鼠标X轴坐标
	 * @param mouseY 鼠标Y轴坐标
	 * @param recX 矩形X轴起始坐标
	 * @param recY 矩形Y轴起始坐标
	 * @param width 矩形宽度
	 * @param height 矩形高度
	 */
	public static boolean checkMouse2DRec(float mouseX, float mouseY,
	                                      int recX, int recY, int width, int height) {
		return mouseX >= recX && (mouseX <= recX + width) &&
				mouseY >= recY && (mouseY <= recY + height);
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