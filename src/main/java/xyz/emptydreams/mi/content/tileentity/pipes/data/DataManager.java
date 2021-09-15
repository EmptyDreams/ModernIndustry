package xyz.emptydreams.mi.content.tileentity.pipes.data;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.LinkedList;

/**
 * 管理流体数据
 * @author EmptyDreams
 */
abstract public class DataManager {
	
	public static DataManager instance(EnumFacing facing, int max) {
		return facing.getAxis().isVertical() ? new VerticalManager(facing, max) : new HorizontalManager(facing, max);
	}
	
	/** 最大容量 */
	protected final int max;
	
	protected DataManager(int max) {
		this.max = max;
	}
	
	/**
	 * 向容器中插入数据
	 * @param data 数据内容
	 * @param facing 从哪个方向插入数据
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 真实插入的数量
	 */
	public final LinkedList<FluidData> insert(FluidData data, EnumFacing facing, boolean simulate) {
		return insert(data, isPositive(facing), simulate);
	}
	
	/**
	 * <p>从容器中取出流体
	 * <p>列表中越靠近头部代表越早取出的流体
	 * @param amount 数量
	 * @param facing 从哪个方向插入数据
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 真实取出的流体数据
	 */
	public final LinkedList<FluidData> extract(int amount, EnumFacing facing, boolean simulate) {
		return extract(amount, isPositive(facing), simulate);
	}
	
	/**
	 * 向容器中插入数据
	 * @param data 数据内容
	 * @param isPositive 是否从正方向插入
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 真实插入的数量
	 */
	abstract public LinkedList<FluidData> insert(FluidData data, boolean isPositive, boolean simulate);
	
	/**
	 * <p>从容器中取出流体
	 * <p>列表中越靠近头部代表越早取出的流体
	 * @param amount 数量
	 * @param isPositive 是否从正向取出
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 真实取出的流体数据
	 */
	@Nonnull
	abstract public LinkedList<FluidData> extract(int amount, boolean isPositive, boolean simulate);
	
	/**
	 * <p>判断指定方向是否为正方向
	 * @param facing 流体输入方向
	 */
	abstract public boolean isPositive(EnumFacing facing);
	
	/** 判断容器死否为空 */
	abstract public boolean isEmpty();
	
	/** 获取全部已占用的空间 */
	abstract public int getBusySpace();
	
	/**
	 * 获取指定方向上的空闲空间
	 * @param isPositive 是否从正方向开始计算
	 */
	abstract public int getVoidSpace(boolean isPositive);
	
	/**
	 * 将该管理类的正方向旋转到指定方向
	 * @param facing 要旋转到的方向
	 * @return 若可以不创建新对象就修改方向则返回当前对象，否则返回新的对象
	 */
	@Nonnull
	abstract public DataManager rotate(EnumFacing facing);
	
	/** 拷贝自身 */
	@Nonnull
	abstract public DataManager copy();
	
	/** 判断是否只包含一种流体 */
	abstract public boolean isPure(Fluid fluid);
	
	/** 管道是否支持水平方向输入输出 */
	abstract public boolean isHorizontal();
	
	/** 数据是否支持垂直方向输入输出 */
	abstract public boolean isVertical();
	
	/** 判断容器是否已满 */
	public boolean isFull() {
		return getBusySpace() == max;
	}
	
	/** 获取最大容量 */
	public int getMax() {
		return max;
	}
	
}