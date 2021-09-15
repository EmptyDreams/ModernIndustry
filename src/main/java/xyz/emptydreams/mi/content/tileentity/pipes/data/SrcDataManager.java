package xyz.emptydreams.mi.content.tileentity.pipes.data;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * @author EmptyDreams
 */
public class SrcDataManager extends DataManager {
	
	/** 存储流体内容，列表头为起点 */
	protected final LinkedList<FluidData> content = new LinkedList<>();
	/** 正方向 */
	protected EnumFacing facing;
	
	/**
	 * @param facing 正方向
	 * @param max 最大容量
	 */
	public SrcDataManager(EnumFacing facing, int max) {
		super(max);
		this.facing = facing;
		content.add(new FluidData(null, max));
	}
	
	protected SrcDataManager(SrcDataManager manager, EnumFacing facing) {
		super(manager.max);
		this.facing = facing;
		manager.content.forEach(it -> content.addLast(it.copy()));
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
		return content.stream().allMatch(data -> data.isAir() || data.isEmpty());
	}
	
	@Override
	public int getBusySpace() {
		return content.stream().filter(data -> !data.isAir()).mapToInt(FluidData::getAmount).sum();
	}
	
	@Override
	public int getVoidSpace(boolean isPositive) {
		FluidData data = isPositive ? content.getFirst() : content.getLast();
		return data.isAir() ? data.getAmount() : 0;
	}
	
	@Override
	@Nonnull
	public DataManager rotate(EnumFacing facing) {
		this.facing = facing;
		return this;
	}
	
	@Nonnull
	@Override
	public SrcDataManager copy() {
		return new SrcDataManager(this, facing);
	}
	
	@Override
	public boolean isPure(Fluid fluid) {
		return content.size() == 1 && content.getFirst().getFluid() == fluid;
	}
	
	@Override
	public boolean isHorizontal() {
		return true;
	}
	
	@Override
	public boolean isVertical() {
		return true;
	}
	
	/**
	 * 插入新的流体数据
	 * @param data 要插入的数据
	 * @return 被挤出来的数据
	 */
	protected LinkedList<FluidData> addFluid(FluidData data, boolean isPositive, boolean simulate) {
		LinkedList<FluidData> result = new LinkedList<>();
		int amount = data.getAmount();
		if (isPositive) {
			if (!simulate) content.addFirst(data);
			ListIterator<FluidData> it = content.listIterator(content.size());
			while (it.hasPrevious() && amount > 0) {
				int old = data.getAmount();
				int minus = task(it.previous(), amount, result, simulate);
				amount -= minus;
				if (minus == old) it.remove();
			}
		} else {
			content.addLast(data);
			Iterator<FluidData> it = content.iterator();
			while (it.hasNext() && amount > 0) {
				int old = data.getAmount();
				int minus = task(it.next(), amount, result, simulate);
				amount -= minus;
				if (minus == old) it.remove();
			}
		}
		return result;
	}
	
	/**
	 * @param data 进行计算的数据
	 * @param amount 需要减去的量
	 * @param result 被挤出的数据
	 * @param simulate 是否为模拟，为true时不修改data内容
	 * @return 实际减去的量
	 */
	private int task(FluidData data, int amount, LinkedList<FluidData> result, boolean simulate) {
		if (data.getAmount() > amount) {
			if (!simulate) data.plusAmount(-amount);
			result.addLast(new FluidData(data.getFluid(), amount));
			return amount;
		} else if (data.getAmount() == amount) {
			result.addLast(data.copy());
			return amount;
		} else {
			result.addLast(data.copy());
			return data.getAmount();
		}
	}

}