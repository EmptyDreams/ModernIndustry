package xyz.emptydreams.mi.api.utils.data.io.instance;

import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Object数据读写<b>（应用于所有变量，主要用于TileEntity）</b>
 * @author EmptyDreams
 */
public class AllObjectData extends ObjectData {
	
	private static final AllObjectData instance = new AllObjectData();
	
	/** 将数据写入到NBT */
	public static NBTTagCompound writeToNBT(Object obj, NBTTagCompound nbt, String key) {
		return instance().writeToNBT(nbt, obj, key);
	}
	
	/** 从NBT中读取数据到类 */
	public static void read(Object obj, NBTTagCompound tag, String key) {
		instance.readFromNBT(tag, obj, key);
	}
	
	/** 将数据写入到writer */
	public static <T extends IDataWriter> T write(T writer, Object obj) {
		if (writer == null)
			throw new IllegalArgumentException("writer不应该等于null，若writer == null则应该调用#write(Object)");
		//noinspection unchecked
		return (T) instance.writeToData(writer, obj);
	}
	
	/** 将数据写入到writer */
	public static ByteDataOperator write(Object obj) {
		return (ByteDataOperator) instance.writeToData(null, obj);
	}
	
	/** 从reader读取数据 */
	public static void read(IDataReader reader, Object obj) {
		instance.readFromData(reader, obj);
	}
	
	public static AllObjectData instance() {
		return instance;
	}
	
	protected AllObjectData() { }
	
	@Override
	public boolean needOperate(Field field) {
		int mod = field.getModifiers();
		return !(Modifier.isStatic(mod) || Modifier.isFinal(mod));
	}
	
}