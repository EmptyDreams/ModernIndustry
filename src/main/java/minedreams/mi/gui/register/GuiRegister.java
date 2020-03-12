package minedreams.mi.gui.register;

import minedreams.mi.api.gui.GuiLoader;
import minedreams.mi.register.AutoLoader;
import minedreams.mi.tools.MISysInfo;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoLoader
public class GuiRegister {

	static {
		MISysInfo.print("类加载了");
		GuiLoader.register(new CompressorCreater());
	}

}
