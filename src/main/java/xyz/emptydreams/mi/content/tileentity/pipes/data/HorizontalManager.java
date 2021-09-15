package xyz.emptydreams.mi.content.tileentity.pipes.data;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * 水平方向的数据存储器
 * @author EmptyDreams
 */
public class HorizontalManager extends DataManager {
	
	/** 渲染格数划分 */
	public static final int LENGTH = 5;
	/**
	 * <p>存储流体数据
	 * <p>该集合将管道分割为<b>{@code LENGTH}</b>个水平面，每个水平面的最大容量为<b>{@code max/LENGTH}</b>
	 */
	private final List<SrcDataManager> content = new ArrayList<>(LENGTH);
	/** 正方向 */
	private EnumFacing facing;
	/** 每个水平面的最大容量 */
	private final int nodeMax;
	
	public HorizontalManager(EnumFacing facing, int max) {
		super(max);
		if (max % LENGTH != 0)
			throw new IllegalArgumentException("max[" + max + "]必须是LENGTH[" + LENGTH + "]的整数倍");
		if (facing.getAxis().isVertical())
			throw new IllegalArgumentException("输入的方向不在水平方向上：" + facing);
		this.facing = facing;
		this.nodeMax = max / LENGTH;
		for (int i = 0; i < LENGTH; ++i) content.add(new SrcDataManager(facing, nodeMax));
	}
	
	private HorizontalManager(HorizontalManager manager, EnumFacing facing) {
		super(manager.max);
		this.facing = facing;
		this.nodeMax = manager.nodeMax;
		manager.content.forEach(it -> content.add(it.copy()));
	}
	
	@Override
	public LinkedList<FluidData> insert(FluidData data, boolean isPositive, boolean simulate) {
		return addFluid(data, isPositive, simulate);
	}
	
	@Nonnull
	@Override
	public LinkedList<FluidData> extract(int amount, boolean isPositive, boolean simulate) {
		int busySpace = getMax() - getVoidSpace(!isPositive);
		amount = Math.min(busySpace, amount);
		return addFluid(new FluidData(null, amount), !isPositive, simulate);
	}
	
	@Override
	public boolean isPositive(EnumFacing facing) {
		return facing == this.facing;
	}
	
	@Override
	public boolean isEmpty() {
		return content.stream().allMatch(SrcDataManager::isEmpty);
	}
	
	@Override
	public int getBusySpace() {
		return content.stream().mapToInt(SrcDataManager::getBusySpace).sum();
	}
	
	@Override
	public int getVoidSpace(boolean isPositive) {
		return content.stream().mapToInt(manager -> manager.getVoidSpace(isPositive)).sum();
	}
	
	@Nonnull
	@Override
	public DataManager rotate(EnumFacing facing) {
		if (facing.getAxis().isHorizontal()) {
			this.facing = facing;
			return this;
		} else {
			VerticalManager result = new VerticalManager(facing, getMax());
			for (int i = content.size() - 1; i >= 0; i--) {
				SrcDataManager manager = content.get(i);
				ListIterator<FluidData> it = manager.content.listIterator(manager.content.size());
				while (it.hasPrevious()) {
					result.insert(it.previous(), true, false);
				}
			}
			return result;
		}
	}
	
	@Nonnull
	@Override
	public HorizontalManager copy() {
		return new HorizontalManager(this, facing);
	}
	
	@Override
	public boolean isPure(Fluid fluid) {
		return content.stream().allMatch(it -> it.isPure(fluid));
	}
	
	@Override
	public boolean isHorizontal() {
		return true;
	}
	
	@Override
	public boolean isVertical() {
		return false;
	}
	
	protected LinkedList<FluidData> addFluid(FluidData data, boolean isPositive, boolean simulate) {
		LinkedList<FluidData> result = new LinkedList<>();
		int amount = data.getAmount();
		Fluid fluid = data.getFluid();
		for (SrcDataManager manager : content) {
			amount -= taskInAdvance(manager, fluid, amount, isPositive, simulate);
			if (amount == 0) break;
		}
		if (amount == 0) return result;
		for (SrcDataManager manager : content) {
			if (taskAfter(manager, fluid, amount, result, isPositive, simulate).isEmpty()) break;
		}
		return result;
	}
	
	private static int taskInAdvance(SrcDataManager manager, Fluid fluid, int amount, boolean isPositive, boolean simulate) {
		int voidSpace = manager.getVoidSpace(false);
		int real = Math.min(amount, voidSpace);
		manager.insert(new FluidData(fluid, real), isPositive, simulate);
		return real;
	}
	
	private FluidData taskAfter(SrcDataManager manager, Fluid fluid, int amount,
	                            LinkedList<FluidData> result, boolean isPositive, boolean simulate) {
		int real = Math.min(amount, nodeMax);
		LinkedList<FluidData> in = manager.insert(new FluidData(fluid, real), isPositive, simulate);
		if (in.size() != 1) throw new IllegalArgumentException("意料之外的数据异常！");
		FluidData putIn = new FluidData(fluid, in.getFirst().getAmount());
		if (!putIn.isEmpty()) result.addLast(putIn);
		return putIn;
	}
	
}