package xyz.emptydreams.mi.api.dor;

import net.minecraft.nbt.NBTTagCompound;

/**
 * @author EmptyDreams
 */
public class ClassDataOperator extends ByteDataOperator {

	private final IClassData object;
	
	public ClassDataOperator(IClassData object) {
		this.object = object;
	}

	public ClassDataOperator(IClassData object, NBTTagCompound nbt) {
		this(object);
		readFromNBT(nbt);
	}
	
	public void writeAll() {
		object.writeAll(this);
	}
	
	public void readAll() {
		object.readAll(this);
	}
	
}