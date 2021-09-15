package xyz.emptydreams.mi.api.capabilities.fluid;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.fluid.TransportResult;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.content.tileentity.pipes.data.FluidData;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

import static net.minecraft.util.EnumFacing.*;

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
	
	/** 获取容器最大流量 */
	default int getMaxCirculation() {
		return 100;
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
	TransportResult extract(int amount, EnumFacing facing, boolean simulate);
	
	/**
	 * 放入指定数额的流体
	 * @param data 输入的流体
	 * @param facing 流体输入的方向在方块的方向
	 * @param simulate 是否为模拟，为true时不修改内部数据
	 * @return 运算结果
	 */
	@Nonnull
	TransportResult insert(FluidData data, EnumFacing facing, boolean simulate);
	
	/** 设置流体来源方向 */
	void setSource(EnumFacing facing);
	
	/** 获取流体来源方向 */
	EnumFacing getSource();
	
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
	
	/** 获取容器已经连接的数量 */
	default int getLinkAmount() {
		int result = 0;
		for (EnumFacing value : values()) {
			if (isLinked(value)) ++result;
		}
		return result;
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
	void unlink(EnumFacing facing);
	
	/** 是否连接指定方向 */
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
	default boolean isOpen(EnumFacing facing) {
		return hasAperture(facing) && !hasPlug(facing);
	}
	
	/** 按照传输优先级对可选方向进行排序 */
	static void sortFacing(List<EnumFacing> facings) {
		if (facings.isEmpty()) return;
		facings.sort((o1, o2) -> {
			if (o1 == o2) return 0;
			if (o1.getAxis() == Axis.Y) return o1 == UP ? -1 : 1;
			else return o2.getAxis() == Axis.Y ? -1 : 0;
		});
	}
	
	/**
	 * 将列表中的同种元素合并，不修改传入的列表
	 * @param list 列表
	 * @return 整合后的列表
	 */
	@Nonnull
	static List<FluidData> integrate(List<FluidData> list) {
		List<FluidData> result = new LinkedList<>();
		o : for (FluidData data : list) {
			for (FluidData inner : result) {
				if (inner.getFluid() == data.getFluid()) {
					inner.plusAmount(data.getAmount());
					continue o;
				}
			}
			result.add(data.copy());
		}
		return result;
	}
	
	/**
	 * 将流体释放到世界中
	 * @param world 世界对象
	 * @param pos 当前坐标
	 * @param target 目标坐标
	 * @param out 要释放的流体
	 * @return 一个列表，包含被释放的流体
	 */
	static List<FluidData> putFluid2World(World world, BlockPos pos, BlockPos target,
	                                      List<FluidData> out, boolean simulate) {
		List<FluidData> result = new LinkedList<>();
		Block block = world.getBlockState(pos).getBlock();
		if (!block.isReplaceable(world, pos)) return result;
		out = integrate(out);
		for (FluidData data : out) {
			if ((!data.isAir()) && data.getAmount() >= 1000) {
				result.add(data);
				if (!simulate) WorldUtil.putFluid(world, pos, target, data.getFluid());
			}
		}
		return result;
	}
	
}