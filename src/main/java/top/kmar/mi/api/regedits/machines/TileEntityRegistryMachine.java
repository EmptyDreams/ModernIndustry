package top.kmar.mi.api.regedits.machines;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import top.kmar.mi.api.regedits.AutoRegisterMachine;
import top.kmar.mi.api.regedits.block.annotations.AutoTileEntity;

import javax.annotation.Nonnull;

/**
 * TileEntity注册机
 * @author EmptyDreams
 */
public class TileEntityRegistryMachine extends AutoRegisterMachine<AutoTileEntity, Object> {

    @Nonnull
    @Override
    public Class<AutoTileEntity> getTargetClass() {
        return AutoTileEntity.class;
    }

    @Override
    public void registry(Class<?> clazz, AutoTileEntity annotation, Object data) {
        //noinspection unchecked
        GameRegistry.registerTileEntity((Class<? extends TileEntity>) clazz,
                new ResourceLocation(annotation.modid(), annotation.value()));
    }

}