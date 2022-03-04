package top.kmar.mi.api.gui.craft.handle;

import top.kmar.mi.api.craftguide.CraftGuide;
import top.kmar.mi.api.craftguide.IShape;
import top.kmar.mi.api.craftguide.ItemElement;
import top.kmar.mi.api.gui.component.group.SlotGroup;
import top.kmar.mi.api.gui.craft.HandleRegister;
import top.kmar.mi.api.utils.MISysInfo;
import top.kmar.mi.api.utils.MathUtil;
import top.kmar.mi.api.utils.data.math.Point2D;
import top.kmar.mi.api.utils.data.math.Size2D;
import top.kmar.mi.api.craftguide.only.UnorderedShapeOnly;
import top.kmar.mi.api.craftguide.sol.ItemSet;
import top.kmar.mi.api.register.others.AutoLoader;

import javax.annotation.Nonnull;

/**
 * 匹配{@link UnorderedShapeOnly}的Handle
 * @author EmptyDreams
 */
@AutoLoader
public class UnorderedShapeOnlyHandle extends CraftHandle<ItemSet, ItemElement> {
	
	static {
		HandleRegister.registry(new HandleRegister.HandleInfo(
				ItemSet.class, ItemElement.class, UnorderedShapeOnly.class), UnorderedShapeOnlyHandle::new);
	}
	
	/** 原料栏大小 */
	private final Size2D rawSize;
	/** 产物栏大小 */
	public static final Size2D proSize = OrderedShapeOnlyHandle.proSize;
	
	@SuppressWarnings("rawtypes")
	public UnorderedShapeOnlyHandle(CraftGuide craft) {
		try {
			check(craft, ItemSet.class, ItemElement.class);
		} catch (IllegalArgumentException e) {
			MISysInfo.err("[UnorderedShapeOnlyHandle]MI加载了一个空的CraftGuide，" +
					"可能会导致运行异常，如果实际运行没有问题则可以忽略该警告");
		} finally {
			rawSize = craft.getShapeSize();
		}
	}
	
	@Nonnull
	@Override
	public Node createGroup() {
		return HandleUtils.createGroup(rawSize, proSize, this::createNode);
	}
	
	@Override
	public void update(Node group, IShape<ItemSet, ItemElement> shape) {
		SlotGroup slots = group.input;
		shape.getInput().forEachIndex((element, index) -> {
			Point2D point = MathUtil.indexToMatrix(index, slots.getXSize());
			slots.setItem(point.getX(), point.getY(), element);
		});
		group.output.setItem(0, 0, shape.getOutput());
	}
	
	@Override
	public Class<ItemSet> getSolClass() {
		return ItemSet.class;
	}
	
	@Override
	public Class<ItemElement> getProtectClass() {
		return ItemElement.class;
	}
	
	@Override
	public Class<? extends IShape<ItemSet, ItemElement>> getShapeClass() {
		return UnorderedShapeOnly.class;
	}
	
}