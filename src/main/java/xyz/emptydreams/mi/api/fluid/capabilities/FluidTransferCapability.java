package xyz.emptydreams.mi.api.fluid.capabilities;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import xyz.emptydreams.mi.api.register.others.AutoLoader;

/**
 * 流体管道的Cap
 * @author EmptyDreams
 */
@AutoLoader
public class FluidTransferCapability {
	
	@CapabilityInject(IFluidTransfer.class)
	public static Capability<IFluidTransfer> TRANSFER;
	
	static {
		CapabilityManager.INSTANCE.register(IFluidTransfer.class, new FluidTransferStore(),
				() -> new IFluidTransfer() {
			
					{ throwException(); }
			
					@Override
					public int fluidAmount() {
						return -1;
					}
					@Override
					public Fluid fluid() {
						return null;
					}
					@Override
					public void setFluid(FluidStack stack) { }
					@Override
					public int extract(int amount, boolean simulate) {
						return 0;
					}
					@Override
					public int insert(int amount, boolean simulate) {
						return 0;
					}
					@Override
					public int getMaxAmount() {
						return 0;
					}
					@Override
					public IFluidTransfer getLinkedTransfer(EnumFacing facing) {
						return null;
					}
					@Override
					public boolean link(EnumFacing facing) {
						return false;
					}
					@Override
					public void unlink(EnumFacing facing) { }
					@Override
					public boolean isLinkedUp() {
						return false;
					}
					@Override
					public boolean isLinkedDown() {
						return false;
					}
					@Override
					public boolean isLinkedEast() {
						return false;
					}
					@Override
					public boolean isLinkedWest() {
						return false;
					}
					@Override
					public boolean isLinkedSouth() {
						return false;
					}
					@Override
					public boolean isLinkedNorth() {
						return false;
					}
					
				});
	}
	
	private static void throwException() {
		throw new UnsupportedOperationException();
	}
	
}