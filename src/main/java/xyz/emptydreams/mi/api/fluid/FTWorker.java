package xyz.emptydreams.mi.api.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import xyz.emptydreams.mi.api.fluid.capabilities.ft.FluidTransferCapability;
import xyz.emptydreams.mi.api.fluid.capabilities.ft.IFluidTransfer;
import xyz.emptydreams.mi.api.utils.BlockUtil;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * 有关流体运输的操作的封装类
 * @author EmptyDreams
 */
public final class FTWorker {
	
	/**
	 * <p>向某个方块输出流体
	 * <p>该方法不保证输入哪一种流体
	 * @param world 当前世界
	 * @param pos 方块坐标
	 * @param facing 接收水的方向，null表示所有方向
	 * @param amount 需要的流体量
	 * @return 获取的流体量（为null表示没有获取到流体）
	 */
	@Nullable
	public static FluidStack applyFluid(World world, BlockPos pos, EnumFacing facing, int amount) {
		if (facing == null) {
			TileEntity te = world.getTileEntity(pos);
			if (te == null) return applyFluidToBlockFromAllFacing(world, pos, amount);
			IFluidTransfer ft = te.getCapability(FluidTransferCapability.TRANSFER, null);
			if (ft == null) return applyFluidToBlockFromAllFacing(world, pos, amount);
			int now = amount;
			FluidStack result = null;
			for (EnumFacing value : EnumFacing.values()) {
				FluidStack stack = applyFluid(world, pos, value, now);
				if (stack == null) continue;
				if (result == null) result = stack;
				else if (stack.getFluid() != result.getFluid()) continue;
				result.amount += stack.amount;
				if (result.amount == amount) break;
				now -= stack.amount;
			}
			return result;
		} else {
			BlockPos target = pos.offset(facing);
			TileEntity targetTe = world.getTileEntity(target);
			if (targetTe == null) return null;
			IFluidTransfer ft = targetTe.getCapability(FluidTransferCapability.TRANSFER, facing.getOpposite());
			if (ft == null || ft.fluid() == null || ft.fluidAmount() == 0) return null;
			FluidStack transport = ft.transport(facing.getOpposite(), amount, false);
			if (transport != null) {
				//noinspection ConstantConditions
				world.getTileEntity(pos).markDirty();
				targetTe.markDirty();
				return transport;
			}
			IBlockState state =  world.getBlockState(pos);
			transport = applyFluidToBlock(world, pos, state, ft, amount);
			if (transport == null || transport.amount < 1000) return null;
			BlockUtil.setFluid(world, pos, transport.getFluid(), target);
			targetTe.markDirty();
			return transport;
		}
	}
	
	/**
	 * 将流体从指定方块的六个方向上输出到该方块
	 * @param world 世界
	 * @param pos 指定方块的坐标
	 * @return 运算结果
	 */
	@Nullable
	private static FluidStack applyFluidToBlockFromAllFacing(World world, BlockPos pos, int amount) {
		if (amount < 1000) return null;
		amount = 1000;
		FluidStack result = null;
		IBlockState state = world.getBlockState(pos);
		List<TileEntity> teList = new LinkedList<>();
		for (EnumFacing value : EnumFacing.values()) {
			BlockPos target = pos.offset(value);
			TileEntity targetTe = world.getTileEntity(target);
			if (targetTe == null) continue;
			IFluidTransfer ft = targetTe.getCapability(
					FluidTransferCapability.TRANSFER, value.getOpposite());
			if (ft == null) continue;
			FluidStack stack = applyFluidToBlock(world, pos, state, ft, amount);
			if (stack == null || stack.amount == 0) continue;
			if (result == null) result = stack;
			else if (stack.getFluid() != result.getFluid()) continue;
			result.amount += stack.amount;
			teList.add(targetTe);
			if (result.amount == 1000) break;
			amount -= stack.amount;
		}
		if (result == null || result.amount != 1000 || teList.isEmpty()) return null;
		BlockUtil.setFluid(world, pos, result.getFluid(), teList.get(0).getPos());
		teList.forEach(TileEntity::markDirty);
		return result;
	}
	
	/**
	 * 将流体从指定方块输出到世界中
	 * @param world 世界
	 * @param pos 输出流体的方块的坐标
	 * @param state 输出流体的方块的blockState
	 * @param ft 输出流体的方块的IFluidTransfer
	 * @param amount 最大允许输出的流体量
	 * @return 真实输出量
	 */
	@Nullable
	private static FluidStack applyFluidToBlock(World world, BlockPos pos,
	                                            IBlockState state, IFluidTransfer ft, int amount) {
		Block block = state.getBlock();
		if (!block.isReplaceable(world, pos)) return null;
		Fluid fluid = ft.fluid();
		if (ft.fluidAmount() == 0 || fluid == null) return null;
		FluidStack result = new FluidStack(fluid, Math.min(ft.fluidAmount(), amount));
		ft.extract(result, false);
		return result;
	}
	
}