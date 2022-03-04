package top.kmar.mi.api.tools.item;

import net.minecraft.item.ItemSpade;
import top.kmar.mi.ModernIndustry;
import top.kmar.mi.api.utils.StringUtil;

/**
 * 铲子
 * @author EmptyDreams
 */
public class MISpade extends ItemSpade implements IToolMaterial {
	
	public MISpade(ToolMaterial material) {
		super(material);
		setCreativeTab(ModernIndustry.TAB_TOOL);
	}

	public MISpade setRegistry(String modid, String name) {
		setRegistryName(modid, name).setUnlocalizedName(StringUtil.getUnlocalizedName(modid, name));
		return this;
	}
	
	@Override
	public String toString() {
		return getRegistryName().toString();
	}
	
}