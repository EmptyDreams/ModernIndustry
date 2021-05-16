package xyz.emptydreams.mi.api.register.machines;

import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.net.message.player.IPlayerHandle;
import xyz.emptydreams.mi.api.net.message.player.PlayerHandleRegistry;
import xyz.emptydreams.mi.api.register.AutoRegisterMachine;
import xyz.emptydreams.mi.api.register.others.AutoPlayerHandle;

import javax.annotation.Nonnull;

import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.newInstance;

/**
 * PlayerHandle注册机
 * @author EmptyDreams
 */
public class PlayerHandleRegistryMachine extends AutoRegisterMachine<AutoPlayerHandle, Object> {
	
	@Nonnull
	@Override
	public Class<AutoPlayerHandle> getTargetClass() {
		return AutoPlayerHandle.class;
	}
	
	@Override
	public void registry(Class<?> clazz, AutoPlayerHandle annotation, Object data) {
		@SuppressWarnings("unchecked")
		IPlayerHandle instance = newInstance((Class<? extends IPlayerHandle>) clazz, (Object[]) null);
		ResourceLocation key = new ResourceLocation(annotation.modid(), annotation.value());
		PlayerHandleRegistry.registry(key, instance);
	}
	
}