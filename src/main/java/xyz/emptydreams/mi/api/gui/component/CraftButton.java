package xyz.emptydreams.mi.api.gui.component;

import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.gui.common.IFrame;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.craft.CraftShower;
import xyz.emptydreams.mi.api.utils.StringUtil;

/**
 * 触发显示合成表的按钮
 * @author EmptyDreams
 */
public class CraftButton extends InvisibleButton {
	
	private final CraftGuide<?, ?> craft;
	private final SlotGroup slots;
	
	public CraftButton(CraftGuide<?, ?> craft, IComponent component, SlotGroup slots) {
		super(component.getWidth(), component.getHeight());
		this.craft = StringUtil.checkNull(craft, "craft");
		this.slots = slots;
		setLocation(component.getX(), component.getY());
		setAction(this::onAction);
	}
	
	public void onAction(IFrame gui, boolean isClient) {
		CraftShower.show(craft, slots);
	}
	
}