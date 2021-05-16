package xyz.emptydreams.mi.api.tools.item;

import net.minecraft.item.ItemPickaxe;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.utils.StringUtil;

/**
 * 镐子
 * @author EmptyDreams
 */
public class MIPickaxe extends ItemPickaxe implements IToolMaterial {
	
	public MIPickaxe(ToolMaterial material) {
		super(material);
		setCreativeTab(ModernIndustry.TAB_TOOL);
	}

	public MIPickaxe setRegistry(String modid, String name) {
		setRegistryName(modid, name).setUnlocalizedName(StringUtil.getUnlocalizedName(modid, name));
		return this;
	}
	
	@Override
	public String toString() {
		return getRegistryName().toString();
	}
	
}