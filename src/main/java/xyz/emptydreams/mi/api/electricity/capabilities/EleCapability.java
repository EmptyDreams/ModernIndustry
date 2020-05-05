package xyz.emptydreams.mi.api.electricity.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.src.info.EnumVoltage;
import xyz.emptydreams.mi.register.AutoLoader;

/**
 * @author EmptyDreams
 * @version V1.0
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
			@Nonnull
			@Override
			public EnergyRange getEnergyRange() { return new EnergyRange(); }
			@Override
			public int receiveEnergy(EleEnergy energy, boolean simulate) { return 0; }
			@Override
			public EleEnergy extractEnergy(EleEnergy energy, boolean simulate) {
				return new EleEnergy(0, EnumVoltage.NON);
			}
			@Override
			public boolean isReAllowable(EnumFacing facing) { return false; }
			@Override
			public boolean isExAllowable(EnumFacing facing) { return false; }
		});
	}
	
}
