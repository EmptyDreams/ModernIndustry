package xyz.emptydreams.mi.data.config;

import net.minecraftforge.common.config.Config;
import xyz.emptydreams.mi.ModernIndustry;

/**
 * @author EmptyDreams
 */
@SuppressWarnings("unused")
@Config(modid = ModernIndustry.MODID, name = "mi.config.machine")
@Config.LangKey("mi.config.machine")
public class MachineConfig {
	
	@Config.Comment("压缩机每Tick消耗的电能")
	@Config.RangeInt(min = 0)
	@Config.LangKey("mi.config.machine.compressorEnergy")
	public static int compressorEnergy = 10;
	
}