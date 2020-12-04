package xyz.emptydreams.mi.api.gui.craft.handle;

import xyz.emptydreams.mi.api.craftguide.CraftGuide;
import xyz.emptydreams.mi.api.craftguide.IShape;
import xyz.emptydreams.mi.api.craftguide.sol.ItemSol;
import xyz.emptydreams.mi.api.gui.group.SlotGroup;

import javax.annotation.Nonnull;

/**
 * 处理合成表的类
 * @author EmptyDreams
 */
public abstract class CraftHandle<T extends ItemSol, R> {
	
	/**
	 * 检查{@link CraftHandle}对象的原料栏和输出栏是否为指定的{@link Class}
	 * @param craft 要检查的CraftHandle对象
	 * @param rawClass 原料栏的Class
	 * @param proClass 输出栏的Class
	 */
	@SuppressWarnings("rawtypes")
	public static void check(CraftGuide craft, Class rawClass, Class proClass) {
		if (craft.getRawClass() == rawClass && craft.getProtectClass() == proClass) return;
		throw new IllegalArgumentException("传入的CraftHandle对象的原料栏[" + craft.getRawClass().getSimpleName()
							+ "]不为：[" + rawClass.getSimpleName()
						+ "]，输出栏[" + craft.getProtectClass().getSimpleName()
							+ "]不为：[" + proClass.getSimpleName() + "]");
	}
	
	/**
	 * 在创建窗口时会通过该方法创建Slot对象.<br>
	 * 返回的两个{@link SlotGroup}不需要设置坐标，只需设置尺寸等信息即可，具体坐标由MI自动计算
	 * @return 返回的任何组件都不得依赖玩家对象，组件初始化时传入的EntityPlayer对象永远为null
	 */
	@Nonnull
	abstract public Node createGroup();
	
	/**
	 * 更新Group中显示的内容
	 * @param group 要更新的Group
	 * @param shape 当前合成表
	 */
	abstract public void update(Node group, IShape<T, R> shape);
	
	/** 获取ItemSol(T)的{@link Class} */
	abstract public Class<T> getSolClass();
	
	/** 获取产物(R)的{@link Class} */
	abstract public Class<R> getProtectClass();
	
	/** 获取支持的合成表的{@link Class} */
	abstract public Class<? extends IShape<T, R>> getShapeClass();
	
	/**
	 * 判断两个{@link CraftHandle}支持的类型是否一致
	 * @param that 目标CraftHandle
	 */
	public boolean isEquals(CraftHandle<?, ?> that) {
		return getSolClass() == that.getSolClass() && getProtectClass() == that.getProtectClass();
	}
	
	/** 构建一个{@link Node} */
	protected Node createNode(SlotGroup raw, SlotGroup pro) {
		return new Node(raw, pro);
	}
	
	/** 存储原料列表和产品列表 */
	public static final class Node {
		
		/** 原料列表 */
		public final SlotGroup raw;
		/** 产物列表 */
		public final SlotGroup pro;
		
		private Node(SlotGroup raw, SlotGroup pro) {
			this.raw = raw;
			this.pro = pro;
		}
		
	}
	
}
