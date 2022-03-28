package top.kmar.mi.api.capabilities.fluid;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import top.kmar.mi.api.utils.ExpandFunctionKt;

import javax.annotation.Nullable;

/**
 * 存储
 * @author EmptyDreams
 */
public class FluidStore implements Capability.IStorage<IFluid> {
	
	@Nullable
	@Override
	public NBTBase writeNBT(Capability<IFluid> capability, IFluid instance, EnumFacing side) {
		NBTTagCompound result = new NBTTagCompound();
		ExpandFunctionKt.writeObject(result, instance, ".");
		return result;
	}
	
	@Override
	public void readNBT(Capability<IFluid> capability,
	                    IFluid instance, EnumFacing side, NBTBase nbt) {
		if (nbt == null) return;
		ExpandFunctionKt.readObject((NBTTagCompound) nbt, instance, ".");
	}
	
}