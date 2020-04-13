package xyz.emptydreams.mi.api.electricity.info;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;

import xyz.emptydreams.mi.api.electricity.interfaces.IEleInputer;
import net.minecraft.tileentity.TileEntity;

/**
 * 线路缓存信息存储.<br>
 * 为了保证不同模组的缓存可以相互合并、通讯，类中一部分方法使用final修饰防止重写。
 * @author EmptyDreams
 * @version V1.0
 */
abstract public class EleLineCache {
	
	private final Collection<EleLineCache> CACHES = new LinkedList<EleLineCache>() {
		{
			add(EleLineCache.this);
		}
	};
	
	abstract public int getOutputerAmount();
	
	/**
	 * 读取缓存中的线路信息
	 * @param start 起点
	 * @param user 需求电能的方块
	 * @param inputer 需求电能的方块的托管
	 * @return 返回读取结果，没有则返回null
	 */
	@Nullable
	public final PathInfo read(TileEntity start, TileEntity user, IEleInputer inputer) {
		PathInfo info;
		for (EleLineCache cach : CACHES) {
			info = cach.readInfo(start, user, inputer);
			if (info != null) return info;
		}
		return null;
	}
	
	/**
	 * 将目标缓存信息合并到当前缓存，传入的参数可能来自其它模组
	 * @param cache 目标缓存
	 */
	public final void merge(EleLineCache cache) {
		CACHES.add(cache);
	}
	
	/**
	 * 将目标缓存信息从当前缓存中取消合并，传入参数可能来自其它模组
	 * @param cache 目标缓存
	 * @return 是否移除成功
	 */
	@SuppressWarnings("unused")
	public final boolean disperse(EleLineCache cache) { return CACHES.remove(cache); }
	
	/**
	 * 读取缓存中的线路信息
	 * @param start 起点
	 * @param user 需求电能的方块
	 * @param inputer 需求电能的方块的托管
	 * @return 返回读取结果，没有则返回null
	 */
	@Nullable
	abstract protected PathInfo readInfo(TileEntity start, TileEntity user, IEleInputer inputer);
	
	/**
	 * 写入缓存数据
	 * @param info 线路信息
	 */
	abstract public void writeInfo(PathInfo info);
	
	
}
