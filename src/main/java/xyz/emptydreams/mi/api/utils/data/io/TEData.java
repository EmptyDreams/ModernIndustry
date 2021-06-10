package xyz.emptydreams.mi.api.utils.data.io;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import xyz.emptydreams.mi.api.dor.ClassDataOperator;
import xyz.emptydreams.mi.api.dor.interfaces.IClassData;
import xyz.emptydreams.mi.api.utils.WorldUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

/**
 * TE数据读写
 * @author EmptyDreams
 */
public class TEData implements IClassData {
	
	private static final TEData instance = new TEData();
	
	@Nonnull
	public static TEData instance() {
		return instance;
	}
	
	/** 将TE写入到NBT */
	public static NBTTagCompound write(TileEntity te, NBTTagCompound tag, String key) {
		return instance.writeToNBT(tag, te, key);
	}
	
	/** 从NBT中读取数据到TE */
	public static void read(TileEntity te, NBTTagCompound tag, String key) {
		instance.readFromNBT(tag, te, key);
	}
	
	private TEData() { }
	
	@Override
	public boolean suspend(Class<?> clazz) {
		return clazz == TileEntity.class;
	}
	
	@Override
	public boolean needOperate(Field field) {
		return field.isAnnotationPresent(Storage.class);
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
		if (WorldUtil.isClient()) return;
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