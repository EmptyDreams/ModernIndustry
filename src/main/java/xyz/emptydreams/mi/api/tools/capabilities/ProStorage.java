package xyz.emptydreams.mi.api.tools.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import xyz.emptydreams.mi.api.tools.property.PropertyManager;

import javax.annotation.Nullable;

/**
 * @author EmptyDreams
 */
public class ProStorage implements Capability.IStorage<PropertyManager> {
	
	@Nullable
	@Override
	public NBTBase writeNBT(Capability<PropertyManager> capability, PropertyManager instance, EnumFacing side) {
		NBTTagCompound tag = new NBTTagCompound();
		instance.write(tag);
		return tag;
	}
	
	@Override
	public void readNBT(Capability<PropertyManager> capability, PropertyManager instance,
	                    EnumFacing side, NBTBase nbt) {
		instance.read((NBTTagCompound) nbt);
	}
	
}
