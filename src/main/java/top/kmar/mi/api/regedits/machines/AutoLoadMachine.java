package top.kmar.mi.api.regedits.machines;

import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.regedits.AutoRegisterMachine;
import top.kmar.mi.api.regedits.others.AutoLoader;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * 触发类加载
 * @author EmptyDreams
 */
public class AutoLoadMachine extends AutoRegisterMachine<AutoLoader, Object> {
    
    @Nonnull
    @Override
    public Class<AutoLoader> getTargetClass() {
        return AutoLoader.class;
    }
    
    @Override
    public void registry(Class<?> clazz, AutoLoader annotation, Object data) { }
    
    @Nonnull
    @Override
    public Collection<String> getDependency() {
        //保证该注册机最后执行
        return RegisterHelp.listOf(ModernIndustry.MODID,
                AutoManagerRegistryMachine.class,
                BlockRegistryMachine.class, FluidRegistryMachine.class,
                ItemRegistryMachine.class, ManagerRegistryMachine.class,
                OreCreateRegistryMachine.class, PlayerHandlerRegistryMachine.class,
                TileEntityRegistryMachine.class);
    }
    
}