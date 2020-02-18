package minedreams.mi.register.block;

import net.minecraft.item.Item;

/**
 * 方块物品辅助注册工具，其中实现方法来返回Item对象来减少
 * Item构造函数的调用
 * @author EmptyDreams
 * @version V1.0
 */
public interface BlockItemHelper {
	
	/**
	 * 获取该类的物品对象，需要自动注册的类建议实现该方法
	 */
	Item getBlockItem();
	
}
