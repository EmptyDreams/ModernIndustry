package xyz.emptydreams.mi.api.gui.craft.handle;

import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.multi.OrderlyShape;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;
import xyz.emptydreams.mi.api.gui.craft.HandleRegister;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.register.others.AutoLoader;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.data.math.Point2D;
import xyz.emptydreams.mi.api.utils.data.math.Size2D;

import javax.annotation.Nonnull;

/**
 * 匹配{@link OrderlyShape}的Handle
 * @author EmptyDreams
 */
@AutoLoader
public final class OrderlyShapeHandle extends CraftHandle<ItemList, ItemSet> {
	
	static {
		HandleRegister.registry(new HandleRegister.HandleInfo(
						ItemList.class, ItemSet.class, OrderlyShape.class), OrderlyShapeHandle::new);
	}
	
	/** 原料栏大小 */
	private final Size2D rawSize;
	/** 产物栏大小 */
	private final Size2D proSize;
	
	/**
	 * 创建一个对于{@link OrderlyShape}的处理类
	 * @param craft 必须为{@code CraftGuide<? extends OrderlyShape, ItemSet>}
	 */
	@SuppressWarnings({"rawtypes"})
	public OrderlyShapeHandle(CraftGuide craft) {
		CraftHandle.check(craft, ItemList.class, ItemSet.class);
		rawSize = craft.getShapeSize();
		proSize = craft.getProtectSize();
	}
	
	@Nonnull
	@Override
	public Node createGroup() {
		return HandleUtils.createGroup(rawSize, proSize, this::createNode);
	}
	
	@Override
	public void update(Node group, IShape<ItemList, ItemSet> shape) {
		SlotGroup raw = group.input;
		SlotGroup pro = group.output;
		shape.getInput().forEach(node -> raw.setItem(node.getX(), node.getY(), node.getElement()));
		shape.getOutput().forEachIndex((element, index) -> {
			Point2D point = MathUtil.indexToMatrix(index, pro.getXSize());
			pro.setItem(point.getX(), point.getY(), element);
		});
	}
	
	@Override
	public Class<ItemList> getSolClass() {
		return ItemList.class;
	}
	
	@Override
	public Class<ItemSet> getProtectClass() {
		return ItemSet.class;
	}
	
	@Override
	public Class<? extends IShape<ItemList, ItemSet>> getShapeClass() {
		return OrderlyShape.class;
	}
	
}