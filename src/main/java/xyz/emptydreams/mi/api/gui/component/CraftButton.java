package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.entity.player.EntityPlayer;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.gui.common.IFrame;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.craft.CraftShower;
import xyz.emptydreams.mi.api.utils.StringUtil;

/**
 * @author EmptyDreams
 */
public class CraftButton extends InvisibleButton {
	
	private final CraftGuide<?, ?> craft;
	private final EntityPlayer player;
	
	public CraftButton(CraftGuide<?, ?> craft, IComponent component, EntityPlayer player) {
		super(component.getWidth(), component.getHeight());
		this.craft = StringUtil.checkNull(craft, "craft");
		this.player = StringUtil.checkNull(player, "player");
		setLocation(component.getX(), component.getY());
		setAction(this::onAction);
	}
	
	public void onAction(IFrame gui, boolean isClient) {
		CraftShower.show(craft, player);
	}
	
}
