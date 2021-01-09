package xyz.emptydreams.mi.data.config;

import net.minecraftforge.common.config.Config;
import xyz.emptydreams.mi.ModernIndustry;

/**
 * @author EmptyDreams
 */
@SuppressWarnings("unused")
@Config(modid = ModernIndustry.MODID, name = "mi.config.system")
@Config.LangKey("mi.config.system")
@Config.RequiresMcRestart
public final class MISystemConfig {
	
	@Config.Comment("自动化注册系统中注册方块的数量，不需要人为修改")
	@Config.RangeInt(min = 1, max = Integer.MAX_VALUE - 500)  //MAX_VALUE-8是ArrayList的最大大小，保险起见取小一些
	@Config.LangKey("mi.config.system.blockSize")
	public static int blockSize = 10;
	
	@Config.Comment("自动化注册系统中注册物品的数量，不需要人为修改")
	@Config.RangeInt(min = 1, max = Integer.MAX_VALUE - 1000)  //MAX_VALUE-8是ArrayList的最大大小，保险起见取小一些
	@Config.LangKey("mi.config.system.itemSize")
	public static int itemSize = 10;
	
	@Config.Comment("自动化注册系统中自动注册物品的数量，不需要人为修改")
	@Config.RangeInt(min = 1, max = Integer.MAX_VALUE - 1000)  //MAX_VALUE-8是ArrayList的最大大小，保险起见取小一些
	@Config.LangKey("mi.config.system.autoItemSize")
	public static int autoItemSize = 10;
	
	@Config.Comment("自动化注册系统中自动注册方块的数量，不需要人为修改")
	@Config.RangeInt(min = 1, max = Integer.MAX_VALUE - 1000)  //MAX_VALUE-8是ArrayList的最大大小，保险起见取小一些
	@Config.LangKey("mi.config.system.autoBlockSize")
	public static int autoBlockSize = 10;
	
}
