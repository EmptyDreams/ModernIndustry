package xyz.emptydreams.mi.api.register;

import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.net.message.player.PlayerHandle;

/**
 * 自动注册有该注解的{@link PlayerHandle}
 * @author EmptyDreams
 */
public @interface AutoPlayerHandle {
	
	String modid() default ModernIndustry.MODID;
	
	/** 名称 */
	String value();
	
}