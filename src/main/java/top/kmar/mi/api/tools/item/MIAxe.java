package top.kmar.mi.api.tools.item;

import net.minecraft.item.ItemAxe;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.utils.StringUtil;

/**
 * 斧子
 * @author EmptyDreams
 */
public class MIAxe extends ItemAxe implements IToolMaterial {
	
	public MIAxe(ToolMaterial material, float damage, float speed) {
		super(material, damage, speed);
		setCreativeTab(ModernIndustry.TAB_TOOL);
	}

	public MIAxe setRegistry(String modid, String name) {
		setRegistryName(modid, name).setUnlocalizedName(StringUtil.getUnlocalizedName(modid, name));
		return this;
	}

	@Override
	public String toString() {
		return getRegistryName().toString();
	}
	
}