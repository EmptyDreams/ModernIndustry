package xyz.emptydreams.mi.api.gui.craft.handle;

import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.ItemElement;
import xyz.emptydreams.mi.api.craftguide.only.UnorderlyShapeOnly;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSet;
import xyz.emptydreams.mi.api.gui.craft.HandleRegister;
import xyz.emptydreams.mi.api.gui.group.SlotGroup;
import xyz.emptydreams.mi.api.utils.MISysInfo;
import xyz.emptydreams.mi.api.utils.MathUtil;
import xyz.emptydreams.mi.api.utils.data.Point2D;
import xyz.emptydreams.mi.api.utils.data.Size2D;
import xyz.emptydreams.mi.register.AutoLoader;

import javax.annotation.Nonnull;

/**
 * 匹配{@link UnorderlyShapeOnly}的Handle
 * @author EmptyDreams
 */
@AutoLoader
public class UnorderlyShapeOnlyHandle extends CraftHandle<ItemSet, ItemElement> {
	
	static {
		HandleRegister.registry(new HandleRegister.HandleInfo(
				ItemSet.class, ItemElement.class, UnorderlyShapeOnly.class), UnorderlyShapeOnlyHandle::new);
	}
	
	/** 原料栏大小 */
	private final Size2D rawSize;
	/** 产物栏大小 */
	public static final Size2D proSize = OrderlyShapeOnlyHandle.proSize;
	
	@SuppressWarnings("rawtypes")
	public UnorderlyShapeOnlyHandle(CraftGuide craft) {
		try {
			check(craft, ItemSet.class, ItemElement.class);
		} catch (IllegalArgumentException e) {
			MISysInfo.err("[UnorderlyShapeOnlyHandle]MI加载了一个空的CraftGuide，" +
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
		SlotGroup slots = group.raw;
		shape.getRawSol().forEachIndex((element, index) -> {
			Point2D point = MathUtil.indexToMatrix(index, slots.getXSize());
			slots.setItem(point.getX(), point.getY(), element);
		});
		group.pro.setItem(0, 0, shape.getProduction());
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
		return UnorderlyShapeOnly.class;
	}
	
}
