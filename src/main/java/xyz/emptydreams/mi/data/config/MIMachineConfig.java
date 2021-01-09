package xyz.emptydreams.mi.data.config;

import net.minecraftforge.common.config.Config;
import xyz.emptydreams.mi.ModernIndustry;

/**
 * @author EmptyDreams
 */
@Config(modid = ModernIndustry.MODID, name = "mi.config.machine")
@Config.LangKey("mi.config.machine")
public class MIMachineConfig {
	
	@Config.Comment("压缩机每Tick消耗的电能")
	@Config.RangeInt(min = 0, max = Integer.MAX_VALUE)  //MAX_VALUE-8是ArrayList的最大大小，保险起见取小一些
	@Config.LangKey("mi.config.machine.compressorEnergy")
	public static int compressorEnergy = 10;
	
}
