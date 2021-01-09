package xyz.emptydreams.mi.api.tools.item;

import net.minecraft.item.ItemSpade;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.register.AutoRegister;

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
		setRegistryName(modid, name).setUnlocalizedName(AutoRegister.getUnlocalizedName(modid, name));
		return this;
	}
	
	@Override
	public String toString() {
		return getRegistryName().toString();
	}
	
}
