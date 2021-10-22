package xyz.emptydreams.mi.api.capabilities.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import xyz.emptydreams.mi.api.fluid.TransportContent;
import xyz.emptydreams.mi.api.register.others.AutoLoader;
import xyz.emptydreams.mi.api.fluid.data.FluidData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
	
	/**
	 * 判断指定方块能否在指定方向上进行流体操作
	 * @param te 指定方块的TE
	 * @param facing 指定方向
	 * @return 如果可以则返回IFluid对象，否则返回null
	 */
	@Nullable
	public static IFluid canOperate(TileEntity te, EnumFacing facing) {
		if (te == null) return null;
		IFluid cap = te.getCapability(TRANSFER, facing);
		return cap != null && cap.isLinked(facing) ? cap : null;
	}
	
	static {
		CapabilityManager.INSTANCE.register(IFluid.class, new FluidStore(),
				() -> new IFluid() {
					@Override
					public boolean isEmpty() {
						return false;
					}
					@Override
					public TransportContent extract(int amount, EnumFacing facing, boolean simulate) {
						return new TransportContent();
					}
					@Override
					public TransportContent insert(FluidData data, EnumFacing facing, boolean simulate) {
						return new TransportContent();
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
					public void removeLink(EnumFacing facing) { }
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