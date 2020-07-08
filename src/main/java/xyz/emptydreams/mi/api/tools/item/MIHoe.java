package xyz.emptydreams.mi.api.tools.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import xyz.emptydreams.mi.ModernIndustry;

/**
 * 锄头
 * @author EmptyDreams
 */
public class MIHoe extends ItemHoe implements IToolMaterial {
	
	public MIHoe(ToolMaterial material) {
		super(material);
		setCreativeTab(ModernIndustry.TAB_TOOL);
	}
	
	public Item setRegistry(String name) {
		setRegistryName(name).setUnlocalizedName(name);
		return this;
	}
	
	@Override
	public String toString() {
		return getRegistryName().toString();
	}
	
}
