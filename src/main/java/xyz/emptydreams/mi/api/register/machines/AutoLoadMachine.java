package xyz.emptydreams.mi.api.register.machines;

import xyz.emptydreams.mi.api.register.AutoRegisterMachine;
import xyz.emptydreams.mi.api.register.others.AutoLoader;

import javax.annotation.Nonnull;
import java.util.Collection;

import static xyz.emptydreams.mi.ModernIndustry.MODID;
import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.*;

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
		return listOf(MODID,
				AgentRegistryMachine.class, AutoManagerRegistryMachine.class,
				BlockRegistryMachine.class, FluidRegistryMachine.class,
				ItemRegistryMachine.class, ManagerRegistryMachine.class,
				OreCreateRegistryMachine.class, PlayerHandleRegistryMachine.class,
				TileEntityRegistryMachine.class);
	}
	
}