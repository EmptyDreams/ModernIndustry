package xyz.emptydreams.mi.coremod;

import java.util.Map;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@SuppressWarnings("unused")
public class MIMixinFMLPlugin implements IFMLLoadingPlugin {
	
	public static void initMixin() {
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.mi.json");
	}
	
	@Override
	public void injectData(Map<String, Object> data) {
		try {
			ClassLoader appClassLoader = Launch.class.getClassLoader();
			MethodUtils.invokeMethod(appClassLoader, true, "addURL",
					this.getClass().getProtectionDomain().getCodeSource().getLocation());
			MethodUtils.invokeStaticMethod(appClassLoader.loadClass(
					this.getClass().getName()), "initMixin");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String[] getASMTransformerClass() { return null; }
	
	@Override
	public String getModContainerClass() { return null; }
	
	@Override
	public String getSetupClass() { return null; }
	
	@Override
	public String getAccessTransformerClass() { return null; }
	
}
