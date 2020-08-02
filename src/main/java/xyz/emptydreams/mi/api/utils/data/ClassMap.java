package xyz.emptydreams.mi.api.utils.data;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author EmptyDreams
 */
public final class ClassMap extends WorldSavedData {

	private final Int2ObjectOpenHashMap<Class<?>> MAP = new Int2ObjectOpenHashMap<>();

	public ClassMap(String name) {
		super(name);
	}

	/** 装载到世界 */
	public static ClassMap loadWorld() {
		return loadWorld(FMLCommonHandler.instance().getMinecraftServerInstance().worlds[0]);
	}

	/** 装载到世界 */
	public static ClassMap loadWorld(World world) {
		WorldSavedData data = world.loadData(ClassMap.class, "mi_class_map");
		if (data == null) {
			data = new ClassMap("mi_class_map");
			world.setData("mi_class_map", data);
		}
		return (ClassMap) data;
	}

	/**
	 * 装载一个类<br>
	 * <b>因为匿名类的特殊性，所以不建议用户存储匿名类，如果需要这类功能，可以考虑动态生成默认值</b>
	 * @throws IllegalArgumentException 如果clazz是一个抽象类，或clazz是一个直接从抽象类继承的匿名类
	 */
	public int loadClass(Class<?> clazz) {
		clazz = getRealClass(clazz);
		for (Map.Entry<Integer, Class<?>> entry : MAP.entrySet()) {
			if (entry.getValue() == clazz) return entry.getKey();
		}
		MAP.put(clazz.hashCode(), clazz);
		markDirty();
		return clazz.hashCode();
	}

	/** 读取一个类 */
	public Class<?> readClass(int key) {
		return MAP.get(key);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		try {
			int size = nbt.getInteger("size");
			for (int key = 0; key < size; ++key) {
				int hash = nbt.getInteger(String.valueOf(key));
				MAP.put(hash, Class.forName(nbt.getString(key + ":name")));
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("ClassMap读取错误", e);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("size", MAP.size());
		int i = 0;
		for (Map.Entry<Integer, Class<?>> entry : MAP.entrySet()) {
			compound.setInteger(String.valueOf(i), entry.getKey());
			compound.setString(i + ":name", entry.getValue().getName());
			++i;
		}
		return compound;
	}

	private static Class<?> getRealClass(Class<?> clazz) {
		if (clazz.getName().contains("$")) {
			return getRealClass(clazz.getSuperclass());
		}
		if (Modifier.isAbstract(clazz.getModifiers()))
			throw new IllegalArgumentException("ClassMap不支持存储抽象类");
		return clazz;
	}

}
