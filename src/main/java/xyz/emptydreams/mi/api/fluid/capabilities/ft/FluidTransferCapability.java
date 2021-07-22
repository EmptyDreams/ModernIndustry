package xyz.emptydreams.mi.api.fluid.capabilities.ft;

import net.minecraft.item.Item;
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
					public void setFacing(EnumFacing facing) { }
					@Override
					public int getMaxAmount() {
						return 0;
					}
					@Override
					public EnumFacing getFacing() {
						return null;
					}
					@Override
					public IFluidTransfer getLinkedTransfer(EnumFacing facing) {
						return null;
					}
					@Override
					public boolean hasAperture(EnumFacing facing) {
						return false;
					}
					@Override
					public boolean canLink(EnumFacing facing) {
						return false;
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
					@Override
					public boolean setPlugUp(Item plug) {
						return false;
					}
					@Override
					public boolean setPlugDown(Item plug) {
						return false;
					}
					@Override
					public boolean setPlugNorth(Item plug) {
						return false;
					}
					@Override
					public boolean setPlugSouth(Item plug) {
						return false;
					}
					@Override
					public boolean setPlugWest(Item plug) {
						return false;
					}
					@Override
					public boolean setPlugEast(Item plug) {
						return false;
					}
					@Override
					public boolean hasPlugUp() {
						return false;
					}
					@Override
					public boolean hasPlugDown() {
						return false;
					}
					@Override
					public boolean hasPlugNorth() {
						return false;
					}
					@Override
					public boolean hasPlugSouth() {
						return false;
					}
					@Override
					public boolean hasPlugWest() {
						return false;
					}
					@Override
					public boolean hasPlugEast() {
						return false;
					}
				});
	}
	
}