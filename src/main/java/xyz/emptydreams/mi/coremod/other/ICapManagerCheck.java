package xyz.emptydreams.mi.coremod.other;

import net.minecraftforge.common.capabilities.Capability;

import java.util.function.Consumer;

/**
 * @author EmptyDreams
 */
public interface ICapManagerCheck {
	
	void forEachCaps(Consumer<Capability<?>> consumer);
	
}