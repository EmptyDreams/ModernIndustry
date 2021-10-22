package xyz.emptydreams.mi.api.capabilities.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.fluid.TransportContent;
import xyz.emptydreams.mi.api.fluid.data.FluidData;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 流体管道信息
 * @author EmptyDreams
 */
public interface IFluid {
	
	/**
	 * <p>获取可存储的最大量
	 * <p>该方法可能会在对象构造函数中调用，必须保证构造过程中也可以返回正确的值
	 */
	default int getMaxAmount() {
		return 1000;
	}
	
	/** 判断容器是否为空 */
	boolean isEmpty();
	
	/**
	 * 取出指定数额的流体
	 * @param amount 需求的数量
	 * @param facing 取出的方向
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 运算结果
	 */
	@Nonnull
	TransportContent extract(int amount, EnumFacing facing, boolean simulate);
	
	/**
	 * 放入指定数额的流体
	 * @param data 输入的流体
	 * @param facing 流体输入的方向在方块的方向
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 被挤出的流体量
	 */
	@Nonnull
	TransportContent insert(FluidData data, EnumFacing facing, boolean simulate);
	
	/**
	 * 获取下一个可用的流体去向
	 * @param facing 来源方向
	 * @throws IllegalArgumentException 如果来源没有与管道连接
	 * @return 返回结果无序且允许更改（更改返回结果不影响内部数据）
	 */
	@Nonnull
	List<EnumFacing> next(EnumFacing facing);
	
	/** 判断指定方向是否含有开口 */
	boolean hasAperture(EnumFacing facing);
	
	/** 判断是否可以连接指定方向 */
	boolean canLink(EnumFacing facing);
	
	/** 判断指定方向上是否可以设置管塞 */
	default boolean canSetPlug(EnumFacing facing) {
		return hasPlug(facing) && !isLinked(facing);
	}
	
	/**
	 * 连接指定方向上的设备
	 * @param facing 方向
	 * @return 连接是否成功，若该方向原本已经有连接则返回true
	 */
	boolean link(EnumFacing facing);
	
	/**
	 * 断开与指定方向上的设备的连接
	 * @param facing 方向
	 */
	void removeLink(EnumFacing facing);
	
	/**
	 * <p>是否连接指定方向
	 * <p>如果不是管道一类的对连接敏感的方块，该方法可以返回{@link #isOpen(EnumFacing)}的值
	 */
	default boolean isLinked(EnumFacing facing) {
		switch (facing) {
			case DOWN: return isLinkedDown();
			case UP: return isLinkedUp();
			case NORTH: return isLinkedNorth();
			case SOUTH: return isLinkedSouth();
			case WEST: return isLinkedWest();
			case EAST: return isLinkedEast();
			default: throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
		}
	}
	/** 是否连接上方 */
	boolean isLinkedUp();
	/** 是否连接下方 */
	boolean isLinkedDown();
	/** 是否连接东方 */
	boolean isLinkedEast();
	/** 是否连接西方 */
	boolean isLinkedWest();
	/** 是否连接南方 */
	boolean isLinkedSouth();
	/** 是否连接北方 */
	boolean isLinkedNorth();
	
	/**
	 * 在指定方向上设置管塞
	 * @param plug 管塞物品对象
	 * @param facing 方向
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	default boolean setPlug(EnumFacing facing, ItemStack plug) {
		switch (facing) {
			case DOWN: return setPlugDown(plug);
			case UP: return setPlugUp(plug);
			case NORTH: return setPlugNorth(plug);
			case SOUTH: return setPlugSouth(plug);
			case WEST: return setPlugWest(plug);
			default: return setPlugEast(plug);
		}
	}
	/**
	 * 在管道上方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugUp(ItemStack plug);
	/**
	 * 在管道下方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugDown(ItemStack plug);
	/**
	 * 在管道北方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugNorth(ItemStack plug);
	/**
	 *在管道南方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugSouth(ItemStack plug);
	/**
	 * 在管道西方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugWest(ItemStack plug);
	/**
	 * 在管道东方设置管塞
	 * @param plug 管塞物品对象，为null表示去除管塞
	 * @return 是否设置成功（若管塞已经被设置或无法设置管塞则设置失败）
	 */
	boolean setPlugEast(ItemStack plug);
	
	/**
	 * 判定指定方向上是否有管塞
	 * @param facing 指定方向
	 */
	default boolean hasPlug(EnumFacing facing) {
		switch (facing) {
			case DOWN: return hasPlugDown();
			case UP: return hasPlugUp();
			case NORTH: return hasPlugNorth();
			case SOUTH: return hasPlugSouth();
			case WEST: return hasPlugWest();
			case EAST: return hasPlugEast();
			default: throw new IllegalArgumentException("facing[" + facing + "]不属于任何一个方向");
		}
	}
	/** 判断管道上方是否含有管塞 */
	boolean hasPlugUp();
	/** 判断管道下方是否含有管塞 */
	boolean hasPlugDown();
	/** 判断管道北方是否含有管塞 */
	boolean hasPlugNorth();
	/** 判断管道南方是否含有管塞 */
	boolean hasPlugSouth();
	/** 判断管道西方是否含有管塞 */
	boolean hasPlugWest();
	/** 判断管道东方是否含有管塞 */
	boolean hasPlugEast();
	
	/** 判断指定方向上能否通过流体 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	default boolean isOpen(EnumFacing facing) {
		return hasAperture(facing) && !hasPlug(facing);
	}
	
}