package xyz.emptydreams.mi.data.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import xyz.emptydreams.mi.ModernIndustry;

import java.util.Collections;
import java.util.Set;

/**
 * @author EmptyDreams
 */
@SuppressWarnings("unused")
public final class ConfigFactory implements IModGuiFactory {
	
	@Override
	public void initialize(Minecraft minecraftInstance) {
	}
	
	@Override
	public boolean hasConfigGui() {
		return true;
	}
	
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new GuiConfig(parentScreen, ConfigElement.from(SystemConfig.class).getChildElements(),
				ModernIndustry.MODID, false, false,
				"MI配置", "系统配置");
	}
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return Collections.emptySet();
	}
}