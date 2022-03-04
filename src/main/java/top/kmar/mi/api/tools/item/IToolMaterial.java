package top.kmar.mi.api.tools.item;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

/**
 * 获取工具的材质
 * @author EmptyDreams
 */
public interface IToolMaterial {

	@Nonnull
	default String getMaterial() {
		String name;
		if (this instanceof ItemSword) {
			name = ((ItemSword) this).getToolMaterialName();
		} else if (this instanceof ItemHoe) {
			name = ((ItemHoe) this).getMaterialName();
		} else if (this instanceof ItemTool) {
			name = ((ItemTool) this).getToolMaterialName();
		} else {
			name = ((ItemArmor) this).getArmorMaterial().toString();
		}
		switch (name) {
			case "WOOD": return "logWood";
			case "STONE": return "cobblestone";
			case "IRON": return "ingotIron";
			case "DIAMOND": return "gemDiamond";
			case "GOLD": return "ingotGold";
			case "COPPER": return "ingotCopper";
			case "TIN": return "ingotTin";
			default: throw new NoSuchElementException("没有找到材质种类！");
		}
	}

}