package minedreams.mi.api.electricity.src.info;

import minedreams.mi.api.electricity.Electricity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 存储连接信息
 * @author EmptyDremas
 * @version V1.0
 */
public final class LinkInfo {
	
	/* 必填信息 */
	/** 所在世界 */
	public final World world;
	/** 调用方块的坐标 */
	public final BlockPos fromPos;
	/** 当前方块坐标 */
	public final BlockPos nowPos;
	/** 调用方块的种类 */
	public final Block fromBlock;
	/** 当前方块的种类 */
	public final Block nowBlock;
	
	/* 可选信息 */
	/** 调用方块的TE */
	public TileEntity fromUser;
	/** 当前方块的TE */
	public Electricity nowUser;
	/** 调用方块的IBlockState */
	public IBlockState fromState;
	/** 当前方块的IBlockState */
	public IBlockState nowState;
	
	public LinkInfo(World world, BlockPos fromPos, BlockPos nowPos, Block fromBlock, Block nowBlock) {
		this.world = world;
		this.fromPos = fromPos;
		this.nowPos = nowPos;
		this.fromBlock = fromBlock;
		this.nowBlock = nowBlock;
	}
	
	/**
	 * 从指定类中复制信息
	 */
	public void cloneFrom(LinkInfo info) {
		fromUser = info.fromUser;
		nowUser = info.nowUser;
		fromState = info.fromState;
		nowState = info.nowState;
	}
	
	/**
	 * 从指定类中复制信息，复制时将now与from替换位置
	 *
	 * @throws ClassCastException 类自动将info.fromUser强制转换为{@link Electricity}对象
	 */
	public void cloneFromDown(LinkInfo info) throws ClassCastException {
		fromUser = info.nowUser;
		nowUser = (Electricity) info.fromUser;
		fromState = info.nowState;
		nowState = info.fromState;
	}
	
}
