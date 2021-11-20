package xyz.emptydreams.mi.api.utils.data.io.instance;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import xyz.emptydreams.mi.api.dor.ByteDataOperator;
import xyz.emptydreams.mi.api.dor.ClassDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IClassData;
import xyz.emptydreams.mi.api.dor.interfaces.IDataReader;
import xyz.emptydreams.mi.api.dor.interfaces.IDataWriter;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.api.utils.data.io.Storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Object数据读写<b>（只应用于被{@link Storage}注释的变量，主要用于TileEntity）</b>
 * @author EmptyDreams
 */
public class ObjectData implements IClassData {
	
	private static final ObjectData instance = new ObjectData();
	
	@Nonnull
	public static ObjectData instance() {
		return instance;
	}
	
	/** 将数据写入到NBT */
	public static NBTTagCompound write(Object obj, NBTTagCompound tag, String key) {
		return instance.writeToNBT(tag, obj, key);
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
	
	protected ObjectData() { }
	
	@Override
	public boolean suspend(Class<?> clazz) {
		return clazz == null || clazz == TileEntity.class || clazz == Object.class;
	}
	
	@Override
	public boolean needOperate(Field field) {
		return (!Modifier.isStatic(field.getModifiers())) && field.isAnnotationPresent(Storage.class);
	}
	
	/**
	 * 写入数据到writer
	 * @param writer 要进行写入的writer，如果为null则内部自动创建
	 * @param obj 需要进行读取的对象
	 * @return 如果writer不为null则返回writer，否则创建一个{@link ByteDataOperator}并返回
	 */
	public IDataWriter writeToData(IDataWriter writer, Object obj) {
		ClassDataOperator operator = new ClassDataOperator(this);
		operator.writeAll(obj);
		if (writer == null) writer = new ByteDataOperator(operator.size() + 3);
		writer.writeData(operator);
		return writer;
	}
	
	/**
	 * 读取数据到类
	 * @param reader 数据
	 * @param obj 要进行写入的对象
	 */
	public void readFromData(IDataReader reader, Object obj) {
		IDataReader data = reader.readData();
		readAll(data, obj);
	}
	
	/**
	 * 向指定NBT写入需要自动写入的数据
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound data, Object object, String key) {
		if (WorldUtil.isClient()) return data;
		ClassDataOperator dor = new ClassDataOperator(this);
		dor.writeAll(object);
		dor.readToNBT(data, key);
		return data;
	}
	
	/** 读取数据 */
	public void readFromNBT(NBTTagCompound data, Object object, String key) {
		ClassDataOperator dor = new ClassDataOperator(this, data, key);
		dor.readAll(object);
	}
	
	@Nullable
	@Override
	public Class<?> cast(Field field) {
		Storage storage = field.getAnnotation(Storage.class);
		if (storage.value() == Object.class) return null;
		return storage.value();
	}

}