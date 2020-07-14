package xyz.emptydreams.mi.api.tools.item;

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

	public MIHoe setRegistry(String modid, String name) {
		setRegistryName(modid, name).setUnlocalizedName(name);
		return this;
	}
	
	@Override
	public String toString() {
		return getRegistryName().toString();
	}
	
}
