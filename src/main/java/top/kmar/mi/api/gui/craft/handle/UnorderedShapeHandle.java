package top.kmar.mi.api.gui.craft.handle;

import top.kmar.mi.api.craftguide.CraftGuide;
import top.kmar.mi.api.craftguide.IShape;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.gui.component.group.SlotGroup;
import top.kmar.mi.api.gui.craft.HandleRegister;
import top.kmar.mi.api.utils.MathUtil;
import top.kmar.mi.api.utils.data.math.Point2D;
import top.kmar.mi.api.utils.data.math.Size2D;
import top.kmar.mi.api.craftguide.multi.UnorderedShape;
import top.kmar.mi.api.craftguide.sol.ItemSet;
import top.kmar.mi.api.register.others.AutoLoader;
import top.kmar.mi.api.utils.container.Wrapper;

import javax.annotation.Nonnull;
import java.util.function.ObjIntConsumer;

/**
 * 匹配{@link UnorderedShape}的Handle
 * @author EmptyDreams
 */
@AutoLoader
public class UnorderedShapeHandle extends CraftHandle<ItemSet, ItemSet> {
	
	static {
		HandleRegister.registry(new HandleRegister.HandleInfo(
				ItemSet.class, ItemSet.class, UnorderedShape.class), UnorderedShapeHandle::new);
	}
	
	/** 原料栏大小 */
	private final Size2D rawSize;
	/** 产物栏大小 */
	private final Size2D proSize;
	
	/**
	 * 创建一个对于{@link UnorderedShape}的处理类
	 * @param craft 必须为{@code CraftGuide<? extends UnorderedShape, ItemSet>}
	 */
	@SuppressWarnings("rawtypes")
	public UnorderedShapeHandle(CraftGuide craft) {
		check(craft, ItemSet.class, ItemSet.class);
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
		return UnorderedShape.class;
	}
	
}