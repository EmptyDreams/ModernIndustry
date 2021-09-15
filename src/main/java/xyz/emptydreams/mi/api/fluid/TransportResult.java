package xyz.emptydreams.mi.api.fluid;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import xyz.emptydreams.mi.content.tileentity.pipes.data.CommonDataManager;
import xyz.emptydreams.mi.content.tileentity.pipes.data.FluidData;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 用于表示运输运算的结果
 * @author EmptyDreams
 */
public final class TransportResult {
	
	private final List<Node> list = new LinkedList<>();
	/** 实际运输量 */
	private int now = 0;
	/** 被排出管道的流体 */
	private CommonDataManager out;
	
	public int getNow() {
		return now;
	}
	
	public void plusNow(int amount) {
		now += amount;
	}
	
	public void plusNow(FluidStack stack) {
		if (stack == null) return;
		now += stack.amount;
	}
	
	/** 增加指定方向上的运输量 */
	public void plus(EnumFacing facing, FluidStack stack) {
		if (stack == null) return;
		getNode(stack.getFluid()).plus(facing, stack.amount);
	}
	
	@Nonnull
	public Node getNode(Fluid fluid) {
		for (Node node : list) {
			if (node.getFluid() == fluid) return node;
		}
		Node result = new Node(fluid);
		list.add(result);
		return result;
	}
	
	/** 设置最终被挤出管道的流体 */
	public void setFinal(Collection<FluidData> out, EnumFacing facing) {
		if (out.isEmpty()) {
			this.out = null;
			return;
		}
		int size = out.stream().mapToInt(FluidData::getAmount).sum();
		this.out = new CommonDataManager(facing, size);
		out.forEach(it -> this.out.insert(it, facing, false));
	}
	
	/** 获取最终被挤出管道的流体 */
	public CommonDataManager getFinal() {
		return out;
	}
	
	/** 获取总运输量 */
	public int getAllAmount() {
		return list.stream().mapToInt(Node::getAllAmount).sum();
	}
	
	/**
	 * 将另一个TransportResult中的数据合并到该对象中
	 * @return 本对象
	 */
	public TransportResult combine(TransportResult other) {
		if (out == null) {
			out = other.out;
		} else if (other.out != null) {
			CommonDataManager newOut = new CommonDataManager(
					EnumFacing.NORTH, out.getMax() + other.out.getMax());
			for (FluidData data : out.extract(out.getMax(), false, true)) {
				newOut.insert(data, true, false);
			}
			for (FluidData data : other.out.extract(other.out.getMax(), false, true)) {
				newOut.insert(data, true, false);
			}
			out = newOut;
		}
		for (Node node : other.list) {
			Node now = getNode(node.getFluid());
			now.up += node.up;
			now.down += node.down;
			now.hor += node.hor;
		}
		return this;
	}
	
	public static final class Node {
		
		/** 流体种类 */
		private final Fluid fluid;
		/** 向上运输量 */
		private int up;
		/** 向下运输量 */
		private int down;
		/** 水平运输量 */
		private int hor;
		
		Node(Fluid fluid) {
			this.fluid = fluid;
		}
		
		public void plus(EnumFacing facing, int amount) {
			switch (facing) {
				case UP: up += amount;
				case DOWN: down += amount;
				default: hor += amount;
			}
		}
		
		/** 获取向上运输量 */
		public int getUp() {
			return up;
		}
		
		/** 获取向下运输量 */
		public int getDown() {
			return down;
		}
		
		/** 获取水平运输量 */
		public int getHor() {
			return hor;
		}
		
		public Fluid getFluid() {
			return fluid;
		}
		
		/** 获取运输总量 */
		public int getAllAmount() {
			return up + down + hor;
		}
		
		/** 判断是否没有进行运输 */
		public boolean isEmpty() {
			return getAllAmount() == 0;
		}
		
	}
	
}