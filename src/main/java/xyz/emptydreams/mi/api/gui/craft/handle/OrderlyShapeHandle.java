package xyz.emptydreams.mi.api.gui.craft.handle;

import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.multi.OrderlyShape;
import xyz.emptydreams.mi.api.craftguide.sol.ItemList;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;
import xyz.emptydreams.mi.api.gui.group.SlotGroup;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.data.Point2D;
import xyz.emptydreams.mi.api.utils.data.Size2D;

import javax.annotation.Nonnull;

/**
 * @author EmptyDreams
 */
public class OrderlyShapeHandle extends CraftHandle<ItemList, ItemSet> {
	
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
		rawSize = craft.getShapeSize();
		proSize = craft.getProtectSize();
	}
	
	@Nonnull
	@Override
	public Node createGroup() {
		SlotGroup raw = new SlotGroup(rawSize.getWidth(), rawSize.getHeight(), 18, 0);
		SlotGroup pro = new SlotGroup(proSize.getWidth(), proSize.getHeight(), 18, 0);
		return createNode(raw, pro);
	}
	
	@Override
	public void update(Node group, IShape<ItemList, ItemSet> shape) {
		SlotGroup raw = group.raw;
		SlotGroup pro = group.pro;
		shape.getRawSol().forEach(node -> raw.setItem(node.getX(), node.getY(), node.getElement()));
		shape.getProduction().forEachIndex((element, index) -> {
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
