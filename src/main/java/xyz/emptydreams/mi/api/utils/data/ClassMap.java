package xyz.emptydreams.mi.api.utils.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author EmptyDreams
 */
public final class ClassMap extends WorldSavedData {

	private final Map<Integer, Class<?>> MAP = new HashMap<>();
	private static final AtomicInteger count = new AtomicInteger(1);

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

	/** 装载一个类 */
	public int loadClass(Class<?> clazz) {
		for (Map.Entry<Integer, Class<?>> entry : MAP.entrySet()) {
			if (entry.getValue() == clazz) return entry.getKey();
		}
		int k = count.getAndAdd(1);
		MAP.put(k, clazz);
		markDirty();
		return k;
	}

	/** 读取一个类 */
	public Class<?> readClass(int key) {
		return MAP.get(key);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		try {
			int size = nbt.getInteger("size");
			for (int key = 1; key <= size; ++key) {
				MAP.put(key, Class.forName(nbt.getString(String.valueOf(key))));
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("ClassMap读取错误", e);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setInteger("size", MAP.size());
		for (Map.Entry<Integer, Class<?>> entry : MAP.entrySet()) {
			compound.setString(String.valueOf(entry.getKey()), entry.getValue().getName());
		}
		return compound;
	}

}
