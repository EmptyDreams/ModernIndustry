package top.kmar.mi.api.gui.craft.handle;

import top.kmar.mi.api.craftguide.CraftGuide;
import top.kmar.mi.api.craftguide.IShape;
import top.kmar.mi.api.gui.component.group.SlotGroup;
import top.kmar.mi.api.gui.craft.HandleRegister;
import top.kmar.mi.api.utils.MathUtil;
import top.kmar.mi.api.utils.data.math.Point2D;
import top.kmar.mi.api.utils.data.math.Size2D;
import top.kmar.mi.api.craftguide.multi.OrderedShape;
import top.kmar.mi.api.craftguide.sol.ItemList;
import top.kmar.mi.api.craftguide.sol.ItemSet;
import top.kmar.mi.api.register.others.AutoLoader;

import javax.annotation.Nonnull;

/**
 * 匹配{@link OrderedShape}的Handle
 * @author EmptyDreams
 */
@AutoLoader
public final class OrderedShapeHandle extends CraftHandle<ItemList, ItemSet> {
	
	static {
		HandleRegister.registry(new HandleRegister.HandleInfo(
						ItemList.class, ItemSet.class, OrderedShape.class), OrderedShapeHandle::new);
	}
	
	/** 原料栏大小 */
	private final Size2D rawSize;
	/** 产物栏大小 */
	private final Size2D proSize;
	
	/**
	 * 创建一个对于{@link OrderedShape}的处理类
	 * @param craft 必须为{@code CraftGuide<? extends OrderedShape, ItemSet>}
	 */
	@SuppressWarnings({"rawtypes"})
	public OrderedShapeHandle(CraftGuide craft) {
		check(craft, ItemList.class, ItemSet.class);
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
		return OrderedShape.class;
	}
	
}