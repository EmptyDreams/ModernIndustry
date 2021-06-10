package xyz.emptydreams.mi.api.register.machines;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraftforge.oredict.OreDictionary;
import xyz.emptydreams.mi.api.register.AutoRegisterMachine;
import xyz.emptydreams.mi.api.register.block.AutoBlockRegister;
import xyz.emptydreams.mi.api.register.block.WorldCreater;
import xyz.emptydreams.mi.api.register.sorter.BlockSorter;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.assignField;
import static xyz.emptydreams.mi.api.register.machines.RegisterHelp.newInstance;

/**
 * 方块注册机
 * @author EmptyDreams
 */
public class BlockRegistryMachine extends AutoRegisterMachine<AutoBlockRegister, Object> {
	
	/** 添加一个方块 */
	public static void addBlock(Block block) {
		Blocks.blocks.add(block);
	}
	
	/** 添加一个自动注册的方块 */
	public static void addAutoBlock(Block block) {
		addBlock(block);
		Blocks.autoRegister.add(block);
	}
	
	/**
	 * 设置注册customModel的方法名称
	 * @param methodName 方法名称
	 */
	public static void setCustomModelRegister(Block block, String methodName) {
		Blocks.customModelBlocks.put(block, methodName);
	}
	
	@Nonnull
	@Override
	public Class<AutoBlockRegister> getTargetClass() {
		return AutoBlockRegister.class;
	}
	
	@Override
	public void registry(Class<?> clazz, AutoBlockRegister annotation, Object data) {
		@SuppressWarnings("unchecked")
		Block block = newInstance((Class<? extends Block>) clazz, (Object[]) null);
		if (block == null) return;
		String field = annotation.field();
		String[] ores = annotation.oreDic();
		String reName = annotation.registryName();
		String modid = annotation.modid();
		
		block.setRegistryName(modid, reName);
		block.setUnlocalizedName(getUnlocalizedName(annotation));
		
		Blocks.blocks.add(block);
		if (ores.length != 0)
			for (String ore : ores)
				OreDictionary.registerOre(ore, block);
			
		if (!assignField(block, field, block)) return;
		Class<?> register = annotation.register();
		if (AutoBlockRegister.REGISTER.equals(register)) Blocks.autoRegister.add(block);
		else Blocks.selfRegister.put(register, block);
		if (!annotation.model().equals("")) setCustomModelRegister(block, annotation.model());
	}
	
	@Override
	public void atEnd() {
		Blocks.autoRegister.sort(BlockSorter::compare);
	}
	
	public static final class Blocks {
		
		/** 所有方块 */
		public static final List<Block> blocks = new LinkedList<>();
		/** 需要自动注册的方块 */
		public static final List<Block> autoRegister = new LinkedList<>();
		/** 不需要自动注册的方块的注册地址 */
		public static final Map<Class<?>, Block> selfRegister = new Object2ObjectArrayMap<>();
		/** 地图生成方块 */
		public static final Map<Block, WorldCreater> worldCreate = new Object2ObjectArrayMap<>();
		/** 不注册物品的方块 */
		public static final Set<Block> noItem = new TreeSet<>(BlockSorter::compare);
		/** 手动注册模型的方块 */
		public static final Map<Block, String> customModelBlocks = new Object2ObjectOpenHashMap<>();
		
	}
	
	@Nonnull
	private static String getUnlocalizedName(AutoBlockRegister annotation) {
		if (annotation.unlocalizedName().length() == 0)
			return annotation.modid() + "." + annotation.registryName();
		return annotation.unlocalizedName();
	}
	
}