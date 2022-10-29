package top.kmar.mi.api.regedits.sorter;

import net.minecraft.block.Block;
import top.kmar.mi.content.blocks.base.MachineBlock;
import top.kmar.mi.content.blocks.common.OreBlock;

import java.util.LinkedList;
import java.util.List;

/**
 * 为方块排序，保证注册顺序
 * @author EmptyDreams
 */
public final class BlockSorter {
	
	private BlockSorter() { throw new UnsupportedOperationException("不应该被调用的构造函数"); }
	
	private static final List<Class<?>> INS = new LinkedList<>();
	
	public static int compare(Block arg0, Block arg1) {
		Class<?> c0 = getRealClass(arg0);
		Class<?> c1 = getRealClass(arg1);
		if (c0 == c1) return arg0.getRegistryName().compareTo(arg1.getRegistryName());
		return Integer.compare(INS.indexOf(c0), INS.indexOf(c1));
	}
	
	/** 获取真实的Class */
	public static Class<?> getRealClass(Block block) {
		Class<?> result;
		if (block instanceof OreBlock) result = OreBlock.class;
		else if (block instanceof MachineBlock) result = MachineBlock.class;
		else result = Block.class;
		if (!INS.contains(result)) INS.add(result);
		return result;
	}
	
}