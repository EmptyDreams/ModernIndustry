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

	public ClassDataOperator(IClassData object, NBTTagCompound nbt, String key) {
		this(object);
		writeFromNBT(nbt, key);
	}
	
	public void writeAll(Object object) {
		this.object.writeAll(this, object);
	}
	
	public void readAll(Object object) {
		this.object.readAll(this, object);
	}
	
}