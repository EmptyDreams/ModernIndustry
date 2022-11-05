package top.kmar.mi.api.capabilities.ele;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.jetbrains.annotations.NotNull;
import top.kmar.mi.api.electricity.EleEnergy;
import top.kmar.mi.api.regedits.others.AutoLoader;

import javax.annotation.Nullable;

/**
 * 电能CAP
 *
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
            public void readNBT(Capability<IStorage> capability,
                                IStorage instance, EnumFacing side, NBTBase nbt) { }
        }, () -> new IStorage() {
            @Override
            public boolean canReceive() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean canExtract() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public int getEnergyDemand() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public EleEnergy receiveEnergy(EleEnergy energy, boolean simulate) {
                throw new UnsupportedOperationException();
            }
            
            @NotNull
            @Override
            public EleEnergy extractEnergy(int energy, boolean simulate) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean isReAllowable(EnumFacing facing) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean isExAllowable(EnumFacing facing) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean canLink(EnumFacing facing) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean link(BlockPos pos) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean unLink(BlockPos pos) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean isLink(BlockPos pos) {
                throw new UnsupportedOperationException();
            }
        });
    }
    
}