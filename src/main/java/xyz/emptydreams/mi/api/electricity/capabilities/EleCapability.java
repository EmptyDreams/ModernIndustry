package xyz.emptydreams.mi.api.electricity.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.info.EnumEleState;
import xyz.emptydreams.mi.api.electricity.info.VoltageRange;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.register.AutoLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author EmptyDreams
 */
@AutoLoader
public class EleCapability {

	@CapabilityInject(IStorage.class)
	public static Capability<IStorage> ENERGY;
	
	static {
		CapabilityManager.INSTANCE.register(IStorage.class, new Capability.IStorage<IStorage>() {
			@Nullable
			@Override
			public NBTBase writeNBT(Capability<IStorage> capability, IStorage instance, EnumFacing side) {
				return null;
			}
			@Override
			public void readNBT(Capability<IStorage> capability, IStorage instance, EnumFacing side, NBTBase nbt) { }
		}, () -> new IStorage() {
			@Override
			public boolean canReceive() { return false; }
			@Override
			public boolean canExtract() { return false; }
			@Override
			public int receiveEnergy(EleEnergy energy, boolean simulate) { return 0; }
			@Override
			public IVoltage getVoltage(EnumEleState state, IVoltage voltage) { return null; }
			@Override
			public VoltageRange getReceiveVoltageRange() { return null; }
			@Override
			public EleEnergy extractEnergy(int energy, VoltageRange range, boolean simulate) { return null; }
			@Override
			public boolean isReAllowable(EnumFacing facing) { return false; }
			@Override
			public boolean isExAllowable(EnumFacing facing) { return false; }
			@Override
			public boolean canLink(EnumFacing facing) { return false; }
			@Override
			public boolean link(BlockPos pos) { return false; }
			@Override
			public boolean unLink(BlockPos pos) { return false; }
			@Override
			public boolean isLink(BlockPos pos) { return false; }
			@Nonnull
			@Override
			public Collection<BlockPos> getLinks() { return null; }
		});
	}
	
}
