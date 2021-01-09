package xyz.emptydreams.mi.api.register.sorter;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 为物品排序，保证注册顺序
 * @author EmptyDreams
 */
public class ItemSorter {
	
	private ItemSorter() { throw new UnsupportedOperationException("不应该被调用的构造函数"); }
	
	private static final List<Class<?>> INS = new LinkedList<>();
	
	public static int compare(Item arg0, Item arg1) {
		Class<?> c0 = getRealClass(arg0);
		Class<?> c1 = getRealClass(arg1);
		if (c0 == c1) {
			if (c0 == ItemArmor.class) {
				ItemArmor a0 = (ItemArmor) arg0;
				ItemArmor a1 = (ItemArmor) arg1;
				return Integer.compare(a0.getArmorMaterial().ordinal(), a1.getArmorMaterial().ordinal());
			}
			return arg0.getRegistryName().compareTo(arg1.getRegistryName());
		}
		return Integer.compare(INS.indexOf(c0), INS.indexOf(c1));
	}
	
	/** 获取真实的Class */
	public static Class<?> getRealClass(Item item) {
		Class<?> result;
		if (item instanceof ItemTool || item instanceof ItemSword ||
				item instanceof ItemHoe) result = ItemTool.class;
		else if (item instanceof ItemArmor) result = ItemArmor.class;
		else {
			String name = item.getRegistryName().getResourcePath();
			if (name.contains("ingot")) result = Comparable.class;
			else if (name.contains("powder")) result = Comparator.class;
			else if (name.contains("crush")) result = List.class;
			else result = Item.class;
		}
		if (!INS.contains(result)) INS.add(result);
		return result;
	}
	
}
