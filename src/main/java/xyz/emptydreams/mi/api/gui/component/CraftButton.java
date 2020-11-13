package xyz.emptydreams.mi.api.gui.component;

import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.gui.common.IFrame;
import xyz.emptydreams.mi.api.utils.StringUtil;

/**
 * @author EmptyDreams
 */
public class CraftButton extends InvisibleButton {
	
	private final CraftGuide<?, ?> craft;
	
	public CraftButton(CraftGuide<?, ?> craft, int width, int height) {
		super(width, height);
		this.craft = StringUtil.checkNull(craft, "craft");
		setAction(this::onAction);
	}
	
	public void onAction(IFrame gui, boolean isClient) {
		
	}
	
}
