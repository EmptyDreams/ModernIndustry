package xyz.emptydreams.mi.api.capabilities.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import xyz.emptydreams.mi.api.fluid.TransportResult;
import xyz.emptydreams.mi.api.register.others.AutoLoader;
import xyz.emptydreams.mi.content.tileentity.pipes.data.FluidData;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 流体管道的Cap
 * @author EmptyDreams
 */
@SuppressWarnings("ConstantConditions")
@AutoLoader
public class FluidCapability {
	
	@CapabilityInject(IFluid.class)
	public static Capability<IFluid> TRANSFER;
	
	static {
		CapabilityManager.INSTANCE.register(IFluid.class, new FluidStore(),
				() -> new IFluid() {
					@Override
					public boolean isEmpty() {
						return false;
					}
					@Override
					public TransportResult extract(int amount, EnumFacing facing, boolean simulate) {
						return new TransportResult();
					}
					@Override
					public TransportResult insert(FluidData data, EnumFacing facing, boolean simulate) {
						return new TransportResult();
					}
					@Override
					public void setSource(EnumFacing facing) {}
					@Override
					public EnumFacing getSource() {
						return null;
					}
					@Nonnull
					@Override
					public List<EnumFacing> next(EnumFacing facing) {
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
					public boolean setPlugUp(ItemStack plug) {
						return false;
					}
					@Override
					public boolean setPlugDown(ItemStack plug) {
						return false;
					}
					@Override
					public boolean setPlugNorth(ItemStack plug) {
						return false;
					}
					@Override
					public boolean setPlugSouth(ItemStack plug) {
						return false;
					}
					@Override
					public boolean setPlugWest(ItemStack plug) {
						return false;
					}
					@Override
					public boolean setPlugEast(ItemStack plug) {
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