package xyz.emptydreams.mi.coremod.mixin.cap;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import xyz.emptydreams.mi.api.exception.TransferException;
import xyz.emptydreams.mi.coremod.other.ICapManagerCheck;
import xyz.emptydreams.mi.coremod.other.ICapStorageType;

import java.util.IdentityHashMap;
import java.util.function.Consumer;

/**
 * @author EmptyDreams
 */
@Mixin(CapabilityManager.class)
public class MixinCapabilityManager implements ICapManagerCheck {
	
	@Redirect(method = "register(Ljava/lang/Class;" +
								"Lnet/minecraftforge/common/capabilities/Capability$IStorage;" +
								"Ljava/util/concurrent/Callable;)V",
			  at = @At(value = "INVOKE",
					  target = "Ljava/util/IdentityHashMap;" +
							   "put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private <K, V> V register_put(IdentityHashMap<K, V> identityHashMap, K key, V value) {
		try {
			ICapStorageType cap = (ICapStorageType) value;
			Class<?> clazz = Class.forName((String) key);
			cap.setStorageType(clazz);
		} catch (Exception e) {
			throw TransferException.instance("MixinCapabilityManager", e);
		}
		return identityHashMap.put(key, value);
	}
	
	@Shadow
	private IdentityHashMap<String, Capability<?>> providers;
	
	@Override
	public void forEachCaps(Consumer<Capability<?>> consumer) {
		providers.values().forEach(consumer);
	}
	
}