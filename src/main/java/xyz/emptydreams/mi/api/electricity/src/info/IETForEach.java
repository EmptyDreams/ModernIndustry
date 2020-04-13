package xyz.emptydreams.mi.api.electricity.src.info;

import javax.annotation.Nonnull;

import xyz.emptydreams.mi.api.electricity.src.tileentity.EleSrcCable;
import net.minecraft.tileentity.TileEntity;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface IETForEach {
	
	/**
	 * @param et 当前线缆
	 * @param isEnd 是否为结尾线缆
	 * @param next 下一根线缆，在isEnd==fasle和没有下一根时一定为null
	 * @return 是否继续遍历
	 */
	boolean run(@Nonnull EleSrcCable et, boolean isEnd, TileEntity next);
	
}
