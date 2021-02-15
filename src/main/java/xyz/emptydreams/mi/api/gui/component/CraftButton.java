package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.gui.common.ChildFrame;
import xyz.emptydreams.mi.api.gui.common.IFrame;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.gui.component.interfaces.IComponent;
import xyz.emptydreams.mi.api.gui.craft.CraftShower;
import xyz.emptydreams.mi.api.utils.StringUtil;

import java.util.function.Function;

/**
 * 触发显示合成表的按钮
 * @author EmptyDreams
 */
public class CraftButton extends InvisibleButton {
	
	private final EntityPlayer player;
	private final CraftGuide<?, ?> craft;
	private final Function<TileEntity, SlotGroup> slotGroupGetter;
	
	public CraftButton(CraftGuide<?, ?> craft, IComponent component, EntityPlayer player,
	                   Function<TileEntity, SlotGroup> slotGroupGetter) {
		super(component.getWidth(), component.getHeight());
		this.craft = StringUtil.checkNull(craft, "craft");
		this.player = StringUtil.checkNull(player, "player");
		this.slotGroupGetter = slotGroupGetter;
		setLocation(component.getX(), component.getY());
		setAction(this::onAction);
	}
	
	public void onAction(IFrame gui, boolean isClient) {
		CraftShower.show(craft, ChildFrame.getGuiTileEntity(player), slotGroupGetter);
	}
	
}