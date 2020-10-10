package xyz.emptydreams.mi.api.net.message.gui;

import net.minecraft.entity.player.EntityPlayer;
import xyz.emptydreams.mi.api.net.message.IMessageAddition;

/**
 * GUI的Addition
 * @author EmptyDreams
 */
public class GuiAddition implements IMessageAddition {
	
	private final EntityPlayer player;
	
	public GuiAddition(EntityPlayer player) {
		this.player = player;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
}
