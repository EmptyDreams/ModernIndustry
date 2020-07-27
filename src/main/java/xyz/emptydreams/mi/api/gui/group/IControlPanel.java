package xyz.emptydreams.mi.api.gui.group;

import xyz.emptydreams.mi.api.gui.IFrame;

/**
 * 在组被添加到GUI时该类负责修改所有控件的坐标
 * @author EmptyDreams
 */
public interface IControlPanel {

	void accept(IFrame frame, Group group);

}
