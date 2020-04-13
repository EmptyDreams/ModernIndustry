package xyz.emptydreams.mi.api.electricity.interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface IRegister {
	
	/**
	 * 获取该托管的名称
	 */
	ResourceLocation getName();
	
	/**
	 * 判断指定方块是否在托管名单中
	 * @param te 指定方块的TE
	 * @return true-在，false-不在
	 */
	boolean contains(TileEntity te);
	
	default boolean contains(TileEntity... tes) {
		for (TileEntity te : tes) {
			if (!contains(te)) return false;
		}
		return true;
	}
	
	default boolean contains(Iterable<? extends TileEntity> tes) {
		for (TileEntity te : tes) {
			if (!contains(te)) return false;
		}
		return true;
	}
	
	/**
	 * 判断两个对象是否相等
	 */
	static boolean equals(IRegister arg0, IRegister arg1) {
		if (arg0 == arg1) return true;
		if (arg0 == null || arg1 == null) return false;
		return arg0.getName().equals(arg1.getName());
	}
	
}
