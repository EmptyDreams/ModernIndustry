package xyz.emptydreams.mi.api.electricity.capabilities;

import javax.annotation.Nonnull;
import java.util.Collection;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * 方块的连接的信息接口
 * @author EmptyDreams
 * @version V1.0
 */
public interface ILink {
	
	/**
	 * 是否可以连接指定方向的方块
	 * @param facing 指定方向
	 */
	boolean canLink(EnumFacing facing);
	
	/**
	 * 连接指定方块
	 * @return 是否连接成功
	 */
	boolean link(BlockPos pos);
	
	/**
	 * 取消连接
	 * @return 是否取消成功
	 */
	@SuppressWarnings("UnusedReturnValue")
	boolean unLink(BlockPos pos);
	
	/** 判断方块是否连接指定方块 */
	@SuppressWarnings("unused")
	boolean isLink(BlockPos pos);
	
	/**
	 * 获取已经连接的所有方块
	 * @return 该方法返回实际数据的拷贝（可以是浅拷贝也可以是深拷贝）
	 */
	@SuppressWarnings("unused")
	@Nonnull
	Collection<BlockPos> getLinks();
	
}
