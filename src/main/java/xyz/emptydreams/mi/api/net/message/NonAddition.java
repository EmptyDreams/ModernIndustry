package xyz.emptydreams.mi.api.net.message;

import net.minecraft.nbt.NBTTagCompound;

/**
 * 空信息
 * @author EmptyDreams
 */
public final class NonAddition implements IMessageAddition {
	
	@Override
	public void writeTo(NBTTagCompound tag) { }
	
	@Override
	public void readFrom(NBTTagCompound tag) { }
	
}
