package xyz.emptydreams.mi.api.craftguide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

/**
 * 代表一个物品
 * @author EmptyDreams
 * @version V1.0
 */
public final class ItemElement implements INBTSerializable<NBTTagCompound> {
	
	private final static Set<ItemElement> instances = new HashSet<>(10);
	
	public static ItemElement empty() {
		return instance(ItemStack.EMPTY);
	}
	
	public static ItemElement instance(ItemStack stack) {
		return instance(stack.getItem(), stack.getCount(), stack.getMetadata());
	}
	
	public static ItemElement instance(Item item, int amount) {
		return instance(item, amount, 0);
	}
	
	public static ItemElement instance(Item item, int amount, int meta) {
		for (ItemElement element : instances) {
			if (element.element == item && element.amount == amount && element.meta == meta) {
				return element;
			}
		}
		ItemElement element = new ItemElement(item, amount, meta);
		instances.add(element);
		return element;
	}
	
	public static ItemElement instance(NBTTagCompound tag) {
		ItemElement element = new ItemElement();
		element.deserializeNBT(tag);
		for (ItemElement instance : instances) {
			if (instance.element == element.element &&
					instance.amount == element.amount &&
					instance.meta == element.meta) {
				return instance;
			}
		}
		instances.add(element);
		return element;
	}
	
	/** 物品类型 */
	private Item element;
	/** 数量 */
	private int amount;
	/** 字典ID */
	private int[] dic;
	/** meta */
	private int meta;
	
	private ItemElement() { }
	
	private ItemElement(Item item, int amount, int meta) {
		element = item;
		this.amount = amount;
		ItemStack stack = item.getDefaultInstance();
		dic = stack.isEmpty() ? new int[0] : OreDictionary.getOreIDs(stack);
		this.meta = meta;
	}
	
	public Item getElement() { return element; }
	public int getAmount() { return amount; }
	public int[] getDic() { return dic.clone(); }
	public int getMeta() { return meta; }
	
	/**
	 * 判断指定物品是否和该元素内的物品等价.
	 * 该方法与{@link #contrastWith(ItemElement)}不同，忽视数量区别
	 */
	public boolean contrastWith(@Nullable Item item) {
		if (item == null) return false;
		if (element == item) return true;
		int[] other = OreDictionary.getOreIDs(item.getDefaultInstance());
		if (dic.length <= other.length) {
			for (int i : other) {
				if (Arrays.binarySearch(dic, i) != -1) return true;
			}
		} else {
			for (int i : dic) {
				if (Arrays.binarySearch(other, i) != -1) return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断两个元素是否等价.
	 * 该方法与{@link #equals(Object)}不同，只要矿物词典判断相等且输入的数量大于等于当前数量即返回true
	 */
	public boolean contrastWith(@Nullable ItemElement ele) {
		if (ele == null || ele.meta != meta) return false;
		if (ele == this) return true;
		if (element == ele.element) {
			return amount <= ele.amount;
		}
		if (dic.length <= ele.dic.length) {
			for (int i : ele.dic) {
				if (Arrays.binarySearch(dic, i) != -1) return true;
			}
		} else {
			for (int i : dic) {
				if (Arrays.binarySearch(ele.dic, i) != -1) return true;
			}
		}
		return false;
	}
	
	/**
	 * 尝试将指定的元素合并到当前元素中.
	 * 合并完毕后不会修改指定元素的信息
	 * @param element 指定元素
	 * @return 是否合并成功
	 */
	public boolean merge(@Nonnull ItemElement element) {
		if (contrastWith(element.element)) {
			amount += element.amount;
			return true;
		}
		return false;
	}
	
	/** 获取{@link ItemStack}对象 */
	public ItemStack getStack() {
		return new ItemStack(element, amount, meta);
	}
	
	@Override
	public String toString() {
		return "ItemElement{" +
					   "element=" + element.getRegistryName() + "," +
					   "amount=" + amount +
					   "dic=" + Arrays.toString(dic) +
					   "meta=" + meta +
				       "}";
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("amount", amount);
		compound.setInteger("meta", meta);
		compound.setString("name", element.getRegistryName().getResourcePath());
		compound.setString("modid", element.getRegistryName().getResourceDomain());
		dic = OreDictionary.getOreIDs(element.getDefaultInstance());
		return compound;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		element = ForgeRegistries.ITEMS.getValue(new ResourceLocation(
				nbt.getString("modid"), nbt.getString("name")));
		meta = nbt.getInteger("meta");
		amount = nbt.getInteger("amount");
	}
}
