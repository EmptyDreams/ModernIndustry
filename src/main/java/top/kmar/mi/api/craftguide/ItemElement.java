package top.kmar.mi.api.craftguide;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import top.kmar.mi.api.dor.interfaces.IDataReader;
import top.kmar.mi.api.dor.interfaces.IDataWriter;
import top.kmar.mi.api.utils.ExpandFunctionKt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 代表一个物品
 * @author EmptyDreams
 */
public final class ItemElement {
	
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

	public static ItemElement instance(Block block, int amount) {
		return instance(new ItemStack(block, amount));
	}

	public static ItemElement instance(NBTTagCompound tag) {
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(
				tag.getString("modid"), tag.getString("name")));
		int meta = tag.getInteger("meta");
		int amount = tag.getInteger("amount");
		ItemElement element = new ItemElement(item, amount, meta);
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
	
	public static ItemElement instance(ByteBuf buf) {
		int amount = buf.readInt();
		int meta = buf.readInt();
		String itemName = ExpandFunctionKt.readString(buf);
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
		ItemElement element = new ItemElement(item, amount, meta);
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
	
	public static ItemElement instance(IDataReader reader) {
		int amount = reader.readVarInt();
		int meta = reader.readVarInt();
		String itemName = reader.readString();
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
		ItemElement element = new ItemElement(item, amount, meta);
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
	private final Item element;
	/** 数量 */
	private final int amount;
	/** 字典ID */
	private final int[] dic;
	/** meta */
	private final int meta;
	
	private ItemElement(Item item, int amount, int meta) {
		element = item;
		this.amount = amount;
		ItemStack stack = new ItemStack(item, amount, meta);
		dic = stack.isEmpty() ? new int[0] : OreDictionary.getOreIDs(stack);
		this.meta = meta;
	}
	
	/** 获取物品 */
	public Item getItem() { return element; }
	/** 获取数量 */
	public int getAmount() { return amount; }
	/** 获取矿物词典 */
	public int[] getDic() { return dic.clone(); }
	/** 获取物品的Meta */
	public int getMeta() { return meta; }
	/** 是否为空 */
	public boolean isEmpty() { return element == Items.AIR || getAmount() <= 0; }
	
	/**
	 * 判断当前元素是否包含指定元素<br>
	 * 该方法与{@link #contain(ItemElement)}不同，忽视数量区别
	 */
	public boolean contain(@Nullable ItemStack stack) {
		if (stack == null) return false;
		if (getItem() == stack.getItem() && getMeta() == stack.getMetadata()) return true;
		if (getAmount() < stack.getCount()) return false;
		int[] other = OreDictionary.getOreIDs(stack);
		if (getDic().length <= other.length) {
			for (int i : other) {
				if (Arrays.binarySearch(getDic(), i) != -1) return true;
			}
		} else {
			for (int i : getDic()) {
				if (Arrays.binarySearch(other, i) != -1) return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断当前元素是否包含指定元素<br>
	 * 该方法与{@link #equals(Object)}不同，
	 * 只要矿物词典和meta相等同时该元素的数量大于等于目标元素的数量即返回true
	 */
	public boolean contain(@Nullable ItemElement ele) {
		if (ele == this) return true;
		if (ele == null) {
			return isEmpty();
		}
		if (ele.meta != meta) return false;
		if (getAmount() < ele.getAmount()) return false;
		if (ele.getItem() == getItem()) return true;
		if (dic.length <= ele.dic.length) {
			for (int i : ele.dic) {
				if (ArrayUtils.contains(dic, i)) return true;
			}
		} else {
			for (int i : dic) {
				if (ArrayUtils.contains(ele.dic, i)) return true;
			}
		}
		return false;
	}
	
	/**
	 * 尝试将指定的元素合并到当前元素中.
	 * 合并完毕后不会修改指定元素的信息
	 * @param element 指定元素
	 * @return 合并失败则返回null
	 */
	@SuppressWarnings("unused")
	@Nullable
	public ItemElement merge(@Nonnull ItemElement element) {
		if (contain(element.getStack())) {
			return new ItemElement(getItem(), getAmount() + element.getAmount(), getMeta());
		}
		return null;
	}
	
	/** 获取{@link ItemStack}对象 */
	public ItemStack getStack() {
		return new ItemStack(element, amount, meta);
	}
	
	@Override
	public String toString() {
		return "element=" + element.getRegistryName() + "," +
					   "amount=" + amount +
					   "dic=" + Arrays.toString(dic) +
					   "meta=" + meta;
	}
	
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("amount", amount);
		compound.setInteger("meta", meta);
		compound.setString("name", element.getRegistryName().getResourcePath());
		compound.setString("modid", element.getRegistryName().getResourceDomain());
		return compound;
	}
	
	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(amount);
		buf.writeInt(meta);
		ExpandFunctionKt.writeString(buf, element.getRegistryName().toString());
	}
	
	public void writeToData(IDataWriter writer) {
		writer.writeVarInt(amount);
		writer.writeVarInt(meta);
		writer.writeString(element.getRegistryName().toString());
	}
	
}