package xyz.emptydreams.mi.api.gui.craft.handle;

import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.multi.UnorderlyShape;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;
import xyz.emptydreams.mi.api.gui.craft.HandleRegister;
import xyz.emptydreams.mi.api.gui.component.group.SlotGroup;
import xyz.emptydreams.mi.api.register.AutoLoader;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.container.Wrapper;
import xyz.emptydreams.mi.api.utils.data.math.Point2D;
import xyz.emptydreams.mi.api.utils.data.math.Size2D;

import javax.annotation.Nonnull;
import java.util.function.ObjIntConsumer;

/**
 * 匹配{@link UnorderlyShape}的Handle
 * @author EmptyDreams
 */
@AutoLoader
public class UnorderlyShapeHandle extends CraftHandle<ItemSet, ItemSet> {
	
	static {
		HandleRegister.registry(new HandleRegister.HandleInfo(
				ItemSet.class, ItemSet.class, UnorderlyShape.class), UnorderlyShapeHandle::new);
	}
	
	/** 原料栏大小 */
	private final Size2D rawSize;
	/** 产物栏大小 */
	private final Size2D proSize;
	
	/**
	 * 创建一个对于{@link UnorderlyShape}的处理类
	 * @param craft 必须为{@code CraftGuide<? extends UnorderlyShape, ItemSet>}
	 */
	@SuppressWarnings("rawtypes")
	public UnorderlyShapeHandle(CraftGuide craft) {
		CraftHandle.check(craft, ItemSet.class, ItemSet.class);
		rawSize = craft.getShapeSize();
		proSize = craft.getProtectSize();
	}
	
	@Nonnull
	@Override
	public Node createGroup() {
		return HandleUtils.createGroup(rawSize, proSize, this::createNode);
	}
	
	@Override
	public void update(Node group, IShape<ItemSet, ItemSet> shape) {
		Wrapper<SlotGroup> slots = new Wrapper<>(group.input);
		ObjIntConsumer<ItemElement> consumer = (element, index) -> {
			@SuppressWarnings("ConstantConditions")
			Point2D point = MathUtil.indexToMatrix(index, slots.get().getXSize());
			slots.get().setItem(point.getX(), point.getY(), element);
		};
		shape.getInput().forEachIndex(consumer);
		slots.set(group.output);
		shape.getOutput().forEachIndex(consumer);
	}
	
	@Override
	public Class<ItemSet> getSolClass() {
		return ItemSet.class;
	}
	
	@Override
	public Class<ItemSet> getProtectClass() {
		return ItemSet.class;
	}
	
	@Override
	public Class<? extends IShape<ItemSet, ItemSet>> getShapeClass() {
		return UnorderlyShape.class;
	}
	
}