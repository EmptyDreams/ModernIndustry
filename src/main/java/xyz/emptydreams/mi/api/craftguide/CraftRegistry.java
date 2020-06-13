package xyz.emptydreams.mi.api.craftguide;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.net.WaitList;

/**
 * 注册表
 * @author EmptyDreams
 * @version V1.0
 */
public class CraftRegistry {
	
	private static final Set<CraftRegistry> INSTANCES = new HashSet<>();
	
	/**
	 * 获取一个实例，实例不存在时自动创建新的实例并返回
	 * @param name 资源名称
	 */
	@Nonnull
	public static CraftRegistry instance(ResourceLocation name) {
		WaitList.checkNull(name, "name");
		Optional<CraftRegistry> any = INSTANCES.stream().filter(it -> it.getName().equals(name)).findAny();
		return any.orElseGet(() -> {
			CraftRegistry registry = new CraftRegistry(name);
			INSTANCES.add(registry);
			return registry;
		});
	}
	
	private final Set<ICraftGuide> CRAFTS = new HashSet<>();
	
	private final ResourceLocation NAME;
	
	private CraftRegistry(ResourceLocation name) {
		NAME = name;
	}
	
	/**
	 * 注册一个合成表
	 * @throws NullPointerException 如果 craft == null
	 */
	public void register(ICraftGuide craft) {
		WaitList.checkNull(craft, "craft");
		CRAFTS.add(craft);
	}
	
	public void register(ICraftGuide... crafts) {
		for (ICraftGuide guide : crafts) {
			register(guide);
		}
	}
	
	/**
	 * 判断注册表中是否有指定的合成表
	 * @return 若没有查找到对应元素，返回空的{@link Optional<ICraftGuide>}
	 */
	public Optional<ICraftGuide> apply(Iterable<ItemStack> stacks) {
		return CRAFTS.stream().filter(it -> it.apply(stacks)).findAny();
	}
	
	/**
	 * 判断注册表中是否有指定的合成表
	 * @return 若没有查找到对应元素，返回空的{@link Optional<ICraftGuide>}
	 */
	public Optional<ICraftGuide> apply(ItemStack... stacks) {
		return CRAFTS.stream().filter(it -> it.apply(stacks)).findAny();
	}
	
	/**
	 * 判断注册表中是否有指定的合成表
	 * @return 若没有查找到对应元素，返回空的{@link Optional<ICraftGuide>}
	 */
	public Optional<ICraftGuide> apply(ICraftGuide craft) {
		return CRAFTS.stream().filter(it -> it.apply(craft)).findAny();
	}
	
	/**
	 * 判断列表中是否包含指定物品
	 * @param item 指定物品
	 * @throws NullPointerException 当item == null时可能抛出该异常，
	 *          是否抛出异常取决于列表中的合成表是否支持传入null值，
	 *          用户应当尽量避免传入null
	 */
	public boolean hasItem(Item item) {
		return CRAFTS.stream().anyMatch(it -> it.hasItem(item));
	}
	
	/** 获取名称 */
	public ResourceLocation getName() { return NAME; }
	
}
