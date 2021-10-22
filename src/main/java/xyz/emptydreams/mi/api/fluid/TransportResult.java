package xyz.emptydreams.mi.api.fluid;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import xyz.emptydreams.mi.api.fluid.data.FluidData;

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
	private int realTransport = 0;
	/** 被排出管道的流体 */
	private final List<FluidData> out = new LinkedList<>();
	
	public int getRealTransport() {
		return realTransport;
	}
	
	public void plusRealTransport(int amount) {
		realTransport += amount;
	}
	
	public void plusRealTransport(FluidStack stack) {
		if (stack == null) return;
		realTransport += stack.amount;
	}
	
	/** 增加指定方向上的运输量 */
	public void plus(EnumFacing facing, FluidStack stack) {
		if (stack == null) return;
		plus(facing, stack.getFluid(), stack.amount);
	}
	
	/** 增加指定方向上的运输量 */
	public void plus(EnumFacing facing, FluidData data) {
		plus(facing, data.getFluid(), data.getAmount());
	}
	
	/** 增加指定方向上的运输量 */
	public void plus(EnumFacing facing, Fluid fluid, int amount) {
		if (fluid == null) return;
		getNode(fluid).plus(facing, amount);
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
		o : for (FluidData in : out) {
			for (FluidData data : this.out) {
				if (data.getFluid() == in.getFluid()) {
					data.plusAmount(in.getAmount());
					continue o;
				}
			}
			out.add(in.copy());
		}
	}
	
	/**
	 * 判断是否运输且只运输了指定的流体
	 * @param fluid 指定的流体
	 */
	public boolean isPure(Fluid fluid) {
		return list.size() == 1 && list.get(0).getFluid() == fluid;
	}
	
	/** 获取总运输量 */
	public int getAllAmount() {
		return list.stream().mapToInt(Node::getAllAmount).sum();
	}
	
	/**
	 * 将另一个TransportResult中的数据合并到该对象中
	 * @return 本对象
	 */
	@SuppressWarnings("UnusedReturnValue")
	public TransportResult combine(TransportResult other) {
		out.addAll(other.out);
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