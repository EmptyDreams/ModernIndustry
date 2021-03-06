package xyz.emptydreams.mi.api.nbt;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.register.AutoLoader;

import java.util.function.Supplier;

/**
 * NBT注册类
 * @author EmptyDreams
 */
@AutoLoader
@Mod.EventBusSubscriber
public final class NBTRegister {

	/** 存储注册的NBT列表 */
	private static final Byte2ObjectMap<Supplier<? extends NBTBase>>
									INSTANCES = new Byte2ObjectOpenHashMap<>();
	/** 存储当前modid分配位点 */
	private static byte index = 13;
	
	/**
	 * <p>注册一个NBT ID
	 * @param id 要注册的ID
	 * @param getter 创建对应的NBT对象
	 */
	private static void registry(byte id, Supplier<? extends NBTBase> getter) {
		INSTANCES.put(id, getter);
	}
	
	
	/**
	 * 根据id创建一个NBTBase对象
	 * @throws NullPointerException 如果id不存在
	 */
	public static NBTBase createNBTBase(byte id) {
		return INSTANCES.get(id).get();
	}
	
	static {
		INSTANCES.defaultReturnValue(null);
		MinecraftForge.EVENT_BUS.post(new NBTRegistryEvent());
	}
	
	@SubscribeEvent
	public static void registryModId(NBTRegistryEvent event) {
	
	}
	
	/**
	 * 注册modid使用的事件
	 */
	public static final class NBTRegistryEvent extends Event {
		
		/**
		 * <p>注册一个NBT ID
		 * <p><b>注册必须保证有序性（两端顺序一致）</b>
		 * @param getter 创建对应的NBT对象
		 */
		public byte registry(Supplier<? extends NBTBase> getter) {
			NBTRegister.registry(index, getter);
			return index++;
		}
		
	}
	
}