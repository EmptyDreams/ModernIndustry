package xyz.emptydreams.mi.api.gui.craft.handle;

import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.only.OrderlyShapeOnly;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.gui.craft.HandleRegister;
import xyz.emptydreams.mi.api.gui.group.SlotGroup;
import xyz.emptydreams.mi.api.register.AutoLoader;
import xyz.emptydreams.mi.api.utils.data.Size2D;

import javax.annotation.Nonnull;

/**
 * 匹配{@link OrderlyShapeOnly}的Handle
 * @author EmptyDreams
 */
@AutoLoader
public final class OrderlyShapeOnlyHandle extends CraftHandle<ItemList, ItemElement> {
	
	static {
		HandleRegister.registry(new HandleRegister.HandleInfo(
				ItemList.class, ItemElement.class, OrderlyShapeOnly.class), OrderlyShapeOnlyHandle::new);
	}
	
	/** 原料栏大小 */
	private final Size2D rawSize;
	/** 产物栏大小 */
	public static final Size2D proSize = new Size2D(1, 1);
	
	@SuppressWarnings("rawtypes")
	public OrderlyShapeOnlyHandle(CraftGuide craft) {
		CraftHandle.check(craft, ItemList.class, ItemElement.class);
		rawSize = craft.getShapeSize();
	}
	
	@Nonnull
	@Override
	public Node createGroup() {
		return HandleUtils.createGroup(rawSize, proSize, this::createNode);
	}
	
	@Override
	public void update(Node group, IShape<ItemList, ItemElement> shape) {
		SlotGroup raw = group.raw;
		shape.getInput().forEach(node -> raw.setItem(node.getX(), node.getY(), node.getElement()));
		group.pro.setItem(0, 0, shape.getOutput());
	}
	
	@Override
	public Class<ItemList> getSolClass() {
		return ItemList.class;
	}
	
	@Override
	public Class<ItemElement> getProtectClass() {
		return ItemElement.class;
	}
	
	@Override
	public Class<? extends IShape<ItemList, ItemElement>> getShapeClass() {
		return OrderlyShapeOnly.class;
	}
	
}
