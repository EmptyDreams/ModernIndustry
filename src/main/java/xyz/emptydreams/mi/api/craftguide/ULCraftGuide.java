package xyz.emptydreams.mi.api.craftguide;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xyz.emptydreams.mi.api.net.WaitList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * 无序合成表类
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings("unused")
public class ULCraftGuide implements ICraftGuide, Iterable<ItemElement>, INBTSerializable<NBTTagCompound> {
	
	/** 原料 */
	private HashSet<ItemElement> elements;
	/** 产物 */
	private final HashSet<ItemElement> outs = new HashSet<ItemElement>(1) {
		@Override
		public boolean add(ItemElement o) {
			for (ItemElement element : this) {
				if (element.merge(o)) return true;
			}
			return super.add(o);
		}
	};
	
	/**
	 * 创建一个预测大小的合成表.
	 * 这个数值不会影响运行效果，但是正确的预测大小可以减少内存浪费和提升运行速度
	 */
	public ULCraftGuide(int size) {
		elements = new HashSet<ItemElement>(size) {
			@Override
			public boolean add(ItemElement o) {
				for (ItemElement element : this) {
					if (element.merge(o)) return true;
				}
				return super.add(o);
			}
		};
	}
	
	/** 以默认大小（4）创建合成表 */
	public ULCraftGuide() { this(4); }
	
	/**
	 * 向列表中添加一个元素
	 * @param element 要添加的元素
	 * @throws NullPointerException 如果 element == null
	 */
	public ULCraftGuide addElement(ItemElement element) {
		WaitList.checkNull(element, "element");
		elements.add(element);
		return this;
	}
	
	/**
	 * 向列表添加一个产物
	 * @param element 产物
	 * @throws NullPointerException 如果 element == null
	 */
	public ULCraftGuide addOutElement(ItemElement element) {
		WaitList.checkNull(element, "element");
		outs.add(element);
		return this;
	}
	
	/**
	 * 从合成表中删除一个产物
	 * @param item 指定的物品
	 * @return 是否删除成功
	 */
	public boolean removeOutItem(Item item) {
		ItemElement element;
		Iterator<ItemElement> it = outs.iterator();
		while (it.hasNext()) {
			element = it.next();
			if (element.contrastWith(item)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 从合成表中删除一个物品
	 * @param item 指定的物品
	 * @return 是否删除成功
	 */
	public boolean removeItem(Item item) {
		ItemElement element;
		Iterator<ItemElement> it = elements.iterator();
		while (it.hasNext()) {
			element = it.next();
			if (element.contrastWith(item)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 从合成表中删除一个产物
	 * @param element 指定的产物
	 * @return 是否删除成功
	 */
	public boolean removeOutElement(ItemElement element) {
		ItemElement itemElement;
		Iterator<ItemElement> it = outs.iterator();
		while (it.hasNext()) {
			itemElement = it.next();
			if (itemElement.contrastWith(element)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 从合成表中删除一个元素
	 * @param element 指定的元素
	 * @return 是否删除成功
	 */
	public boolean removeElement(ItemElement element) {
		ItemElement itemElement;
		Iterator<ItemElement> it = elements.iterator();
		while (it.hasNext()) {
			itemElement = it.next();
			if (itemElement.contrastWith(element)) {
				it.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 判断目标列表是否与合成表相符（比较时忽视产物）
	 * @param craft 目标列表
	 * @return 返回产物，若两者不相符返回null
	 */
	@Override
	public boolean apply(Object craft) {
		if (!(craft instanceof ULCraftGuide)) return false;
		ULCraftGuide guide = (ULCraftGuide) craft;
		if (craft == this) return true;
		return elements.equals(guide.elements);
	}
	
	/**
	 * 判断目标列表是否与合成表相符
	 * @param stacks 物品列表
	 * @return 返回产物，若两者不相符返回null
	 */
	@Override
	public boolean apply(ItemStack... stacks) {
		if (stacks == null || stacks.length != elements.size()) return false;
		o : for (ItemElement element : elements) {
			for (ItemStack stack : stacks) {
				if (element.contrastWith(ItemElement.instance(stack))) {
					continue o;
				}
			}
			return false;
		}
		return true;
	}
	
	@Override
	public boolean apply(Iterable<ItemStack> stacks) {
		if (stacks == null) return false;
		o : for (ItemElement element : elements) {
			for (ItemStack stack : stacks) {
				if (element.contrastWith(ItemElement.instance(stack))) {
					continue o;
				}
			}
			return false;
		}
		return true;
	}
	
	@Override
	public boolean hasItem(Item item) {
		for (ItemElement element : this) {
			if (element.contrastWith(item)) return true;
		}
		return false;
	}
	
	@Nonnull
	@Override
	public List<ItemElement> getOuts() {
		return new ArrayList<>(outs);
	}

	@Override
	public ItemElement getFirstOut() {
		return outs.iterator().next();
	}

	/** 将合成表转换为ItemStack[] */
	public ItemStack[] toItemStack() {
		ItemStack[] stacks = new ItemStack[elements.size()];
		int i = 0;
		for (ItemElement element : elements) {
			stacks[i] = element.getStack();
			++i;
		}
		return stacks;
	}
	
	/**
	 * 将合成表注册到MC中.
	 * 当产物数量不为1或原料数量大于9时会注册失败
	 * @return 是否注册成功
	 * @see GameRegistry#addShapelessRecipe(ResourceLocation, ResourceLocation, ItemStack, Ingredient...)
	 */
	public boolean registryToShapelessRecipe(ResourceLocation name, ResourceLocation group) {
		if (elements.size() <= 9 && outs.size() == 1) {
			ItemElement out = null;
			for (ItemElement element : outs) {
				out = element;
			}
			GameRegistry.addShapelessRecipe(name, group, out.getStack(), Ingredient.fromStacks(toItemStack()));
			return true;
		}
		return false;
	}
	
	@Override
	public Iterator<ItemElement> iterator() {
		return elements.iterator();
	}
	
	@Override
	public String toString() {
		return "ULCraftGuide{" +
				       "elementsSize=" + elements.size() + "," +
					   "outsSize=" + outs.size() +
				       '}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		ULCraftGuide that = (ULCraftGuide) o;
		
		if (!elements.equals(that.elements)) return false;
		return outs.equals(that.outs);
	}
	
	@Override
	public int hashCode() {
		return elements.hashCode();
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("ele", elements.size());
		tag.setInteger("out", outs.size());
		int i = 0;
		for (ItemElement element : elements) {
			tag.setTag(String.valueOf(i), element.serializeNBT());
			++i;
		}
		i = 0;
		for (ItemElement element : outs) {
			tag.setTag("o" + i, element.serializeNBT());
			++i;
		}
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		int size = nbt.getInteger("ele");
		elements = new HashSet<ItemElement>(size) {
			@Override
			public boolean add(ItemElement o) {
				for (ItemElement element : this) {
					if (element.merge(o)) return true;
				}
				return super.add(o);
			}
		};
		for (int i = 0; i < size; ++i) {
			elements.add(ItemElement.instance(nbt.getCompoundTag(String.valueOf(i))));
		}
		size += nbt.getInteger("out");
		for (int i = 0; i < size; ++i) {
			outs.add(ItemElement.instance(nbt.getCompoundTag("o" + i)));
		}
	}
}
