package top.kmar.mi.api.gui.craft.handle;

import top.kmar.mi.api.craftguide.CraftGuide;
import top.kmar.mi.api.craftguide.IShape;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.gui.component.group.SlotGroup;
import top.kmar.mi.api.gui.craft.HandleRegister;
import top.kmar.mi.api.utils.data.math.Size2D;
import top.kmar.mi.api.craftguide.only.OrderedShapeOnly;
import top.kmar.mi.api.craftguide.sol.ItemList;
import top.kmar.mi.api.register.others.AutoLoader;

import javax.annotation.Nonnull;

/**
 * 匹配{@link OrderedShapeOnly}的Handle
 * @author EmptyDreams
 */
@AutoLoader
public final class OrderedShapeOnlyHandle extends CraftHandle<ItemList, ItemElement> {
	
	static {
		HandleRegister.registry(new HandleRegister.HandleInfo(
				ItemList.class, ItemElement.class, OrderedShapeOnly.class), OrderedShapeOnlyHandle::new);
	}
	
	/** 原料栏大小 */
	private final Size2D rawSize;
	/** 产物栏大小 */
	public static final Size2D proSize = new Size2D(1, 1);
	
	@SuppressWarnings("rawtypes")
	public OrderedShapeOnlyHandle(CraftGuide craft) {
		check(craft, ItemList.class, ItemElement.class);
		rawSize = craft.getShapeSize();
	}
	
	@Nonnull
	@Override
	public Node createGroup() {
		return HandleUtils.createGroup(rawSize, proSize, this::createNode);
	}
	
	@Override
	public void update(Node group, IShape<ItemList, ItemElement> shape) {
		SlotGroup raw = group.input;
		shape.getInput().forEach(node -> raw.setItem(node.getX(), node.getY(), node.getElement()));
		group.output.setItem(0, 0, shape.getOutput());
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
		return OrderedShapeOnly.class;
	}
	
}