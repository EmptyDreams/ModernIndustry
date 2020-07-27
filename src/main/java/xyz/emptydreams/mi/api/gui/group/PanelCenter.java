package xyz.emptydreams.mi.api.gui.group;

import xyz.emptydreams.mi.api.gui.IFrame;
import xyz.emptydreams.mi.api.gui.component.IComponent;

/**
 * 居中对齐
 * @author EmptyDreams
 */
public final class PanelCenter implements IControlPanel {

	private static final PanelCenter INSTANCE = new PanelCenter();

	/** 获取实例 */
	public static PanelCenter getInstance() {
		return INSTANCE;
	}

	private PanelCenter() { }

	@Override
	public void accept(IFrame frame, Group group) {
		int size = group.size();
		int width = group.getWidth();
		if (size <= 0) return;

		int allWidth = 0;
		int allHeight = 0;
		for (IComponent component : group) {
			allWidth += component.getWidth();
			allHeight = Math.max(allHeight, component.getHeight());
		}
		group.setSize(group.getWidth(), allHeight);

		int remain = width - allWidth;
		if (remain < 0) remain = 0;
		int interval = remain / (size + 1);
		interval = Math.max(interval, group.getMinDistance());
		interval = Math.min(interval, group.getMaxDistance());

		int past = (width - (allWidth + interval * (size + 1))) / 2;
		for (IComponent component : group) {
			component.setLocation(past + interval + group.getX(),
					(allHeight - component.getHeight()) / 2 + group.getY());
			past += interval + component.getWidth();
		}
	}

}
