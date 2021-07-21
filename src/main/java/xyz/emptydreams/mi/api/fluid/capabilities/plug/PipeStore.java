package xyz.emptydreams.mi.api.fluid.capabilities.plug;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * @author EmptyDreams
 */
public class PipeStore implements Capability.IStorage<IPipePlug> {
	
	@Nullable
	@Override
	public NBTBase writeNBT(Capability<IPipePlug> capability, IPipePlug instance, EnumFacing side) {
		return null;
	}
	
	@Override
	public void readNBT(Capability<IPipePlug> capability, IPipePlug instance, EnumFacing side, NBTBase nbt) {
	
	}
	
}