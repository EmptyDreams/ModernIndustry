package top.kmar.mi.api.capabilities.fluid;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import top.kmar.mi.api.fluid.data.FluidQueue;
import top.kmar.mi.api.fluid.data.TransportReport;
import top.kmar.mi.api.regedits.others.AutoLoader;

import javax.annotation.Nonnull;

/**
 * 流体管道的Cap
 *
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
                        return true;
                    }
                    
                    @Override
                    public boolean isFull() {
                        return false;
                    }
                    
                    @Override
                    public int insert(FluidQueue queue, EnumFacing facing, boolean simulate, TransportReport report) {
                        return 0;
                    }
                    
                    @Nonnull
                    @Override
                    public FluidQueue extract(int amount, EnumFacing facing, boolean simulate, TransportReport report) {
                        return null;
                    }
                    
                    @Override
                    public boolean canLinkFluid(EnumFacing facing) {
                        return false;
                    }
                    
                    @Override
                    public boolean linkFluid(EnumFacing facing) {
                        return false;
                    }
                    
                    @Override
                    public void unlink(EnumFacing facing) {
                    }
                    
                    @Override
                    public boolean isLinked(EnumFacing facing) {
                        return false;
                    }
                });
    }
    
}