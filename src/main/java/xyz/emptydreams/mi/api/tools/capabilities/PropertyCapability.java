package xyz.emptydreams.mi.api.tools.capabilities;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import xyz.emptydreams.mi.api.register.AutoLoader;
import xyz.emptydreams.mi.api.tools.property.PropertyManager;

/**
 * @author EmptyDreams
 */
@AutoLoader
public class PropertyCapability  {

	@CapabilityInject(PropertyManager.class)
	public static Capability<PropertyManager> PROPERTY;
	
	static {
		CapabilityManager.INSTANCE.register(PropertyManager.class, new ProStorage(), PropertyManager::new);
	}
	
}
