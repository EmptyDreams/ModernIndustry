package top.kmar.mi.coremod.mixin.cap;

import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Mixin;
import top.kmar.mi.coremod.other.ICapStorageType;

/**
 * @author EmptyDreams
 */
@Mixin(Capability.class)
public class MixinCapability implements ICapStorageType {

    private Class<?> storageType;

    @Override
    public Class<?> getStorageType() {
        return storageType;
    }

    @Override
    public void setStorageType(Class<?> type) {
        if (storageType != null)
            throw new IllegalStateException("类型已经完成初始化");
        storageType = type;
    }

}