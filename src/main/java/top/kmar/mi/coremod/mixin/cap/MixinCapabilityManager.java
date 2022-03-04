package top.kmar.mi.coremod.mixin.cap;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.kmar.mi.api.exception.TransferException;
import top.kmar.mi.coremod.other.ICapManagerCheck;
import top.kmar.mi.coremod.other.ICapStorageType;

import java.util.IdentityHashMap;
import java.util.function.Predicate;

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
							   "put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
	          remap = false)
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
	
	@Shadow(remap = false)
	private IdentityHashMap<String, Capability<?>> providers;
	
	@Override
	public void forEachCaps(Predicate<Capability<?>> test) {
		for (Capability<?> cap : providers.values()) {
			if (test.test(cap)) break;
		}
	}
	
}