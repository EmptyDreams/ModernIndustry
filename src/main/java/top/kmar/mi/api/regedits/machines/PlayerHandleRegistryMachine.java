package top.kmar.mi.api.regedits.machines;

import net.minecraft.util.ResourceLocation;
import top.kmar.mi.api.net.message.player.IPlayerHandle;
import top.kmar.mi.api.net.message.player.PlayerHandleRegistry;
import top.kmar.mi.api.regedits.AutoRegisterMachine;
import top.kmar.mi.api.regedits.others.AutoPlayerHandle;

import javax.annotation.Nonnull;

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
        IPlayerHandle instance = RegisterHelp.newInstance((Class<? extends IPlayerHandle>) clazz, (Object[]) null);
		ResourceLocation key = new ResourceLocation(annotation.modid(), annotation.value());
		PlayerHandleRegistry.registry(key, instance);
	}
	
}