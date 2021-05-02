package xyz.emptydreams.mi.data.info;

import net.minecraft.tileentity.TileEntity;
import xyz.emptydreams.mi.content.blocks.tileentity.EleSrcCable;

import javax.annotation.Nonnull;

/**
 * @author EmptyDreams
 */
public interface IETForEach {
	
	/**
	 * @param et 当前线缆
	 * @param isEnd 是否为结尾线缆
	 * @param next 下一根线缆，在isEnd==false和没有下一根时一定为null
	 * @return 是否继续遍历
	 */
	boolean run(@Nonnull EleSrcCable et, boolean isEnd, TileEntity next);
	
}