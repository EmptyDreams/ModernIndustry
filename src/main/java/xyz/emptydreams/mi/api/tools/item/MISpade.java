package xyz.emptydreams.mi.api.tools.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import xyz.emptydreams.mi.ModernIndustry;

/**
 * 铲子
 * @author EmptyDreams
 */
public class MISpade extends ItemSpade implements IToolMaterial {
	
	public MISpade(ToolMaterial material) {
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
