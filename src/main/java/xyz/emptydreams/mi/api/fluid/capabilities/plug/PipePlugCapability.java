package xyz.emptydreams.mi.api.fluid.capabilities.plug;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * @author EmptyDreams
 */
public class PipePlugCapability {
	
	@CapabilityInject(IPipePlug.class)
	public static Capability<IPipePlug> PLUG;
	
	static {
		CapabilityManager.INSTANCE.register(IPipePlug.class, new PipeStore(),
				() -> new IPipePlug() {
		});
	}
	
}