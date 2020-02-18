package minedreams.mi.api.electricity;

import javax.annotation.Nonnull;

import minedreams.mi.api.net.MessageBase;
import minedreams.mi.api.net.message.MessageList;
import minedreams.mi.api.net.WaitList;
import minedreams.mi.register.te.AutoTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 所有于电有关的设备的父级TE
 *
 * @author EmptyDremas
 * @version V2.0
 */
@AutoTileEntity("IN_FATHER_ELECTRICITY")
public abstract class Electricity extends TileEntity implements ITickable {
	
	/** 没有信息 */
	public static final Object NO_HAVE_INFO = new Object();
	
	/**
	 * 处理内部事务和网络传输.
	 * 运行机制：<br>
	 * <pre>调用{@link #send(boolean)} ---->> 发送信息 ----->> 调用{@link #sonRun()}</pre>
	 */
	@Override
	public final void update() {
		if (world.isRemote) {
			MessageList list = send(true);
			if (list != null) {
				MessageBase mb = new MessageBase();
				mb.setMessageList(list);
				mb.setPos(getPos());
				mb.setDimension(world.provider.getDimension());
				WaitList.sendToService(mb);
			}
			
		} else {
			MessageList list = send(false);
			if (list != null) {
				MessageBase mb = new MessageBase();
				mb.setMessageList(list);
				mb.setPos(getPos());
				mb.setDimension(world.provider.getDimension());
				WaitList.sendToClient(mb, mb.getMessageList().getPlayers());
			}
		}
		sonRun();
	}
	
	/**
	 * 留给子类的运行接口，第一个子类重写该方法时应使他变为final的
	 */
	protected abstract void sonRun();
	
	/**
	 * 发送信息，若该方法在客户端运行则发送给服务端，否则发送给客户端.<br>
	 * <b>注意：重写该方法必须重写{@link #reveive(MessageList)}方法！</b>
	 *
	 * @return 返回值为null时表示不需要发送信息T
	 */
	public MessageList send(boolean isClient) {
		return null;
	}
	
	/**
	 * 接收信息，接收从客户端/服务端发送的信息.<br>
	 * <b>注意：重写该方法前必须重写{@link #send(boolean)}方法！</b>
	 *
	 * @param list 接收到的信息，该对象不会为null
	 *
	 * @throws AssertionError 如果用户重写了{@link #send(boolean)}方法却没有重写该方法
	 */
	public void reveive(@Nonnull MessageList list) {
		//防止折叠
		throw new AssertionError("重写了send()没有重写reveive()方法或者重reveive()写后再次调用了原始版本");
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
	/** 过载最长时间 */
	protected int biggerMaxTime = 50;
	
	/** 设置过载最长时间(单位：tick，默认值：50tick)，当设置时间小于0时保持原设置不变 */
	protected final Electricity setBiggerMaxTime(int bvt) {
		biggerMaxTime = (bvt >= 0) ? bvt : biggerMaxTime;
		return this;
	}
	/** 获取最长过载时间 */
	public final int getBiggerMaxTime() {
		return biggerMaxTime;
	}
	
	/** 设置方块类型 */
	public final void setBlockType(Block block) {
		blockType = block;
	}
	
	@Override
	public String toString() {
		return "Electricity{ pos=" + pos + '}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Electricity e = (Electricity) o;
		if (world == null) {
			return e.getWorld() == null;
		}
		return world == e.getWorld() && pos.equals(e.getPos());
	}
	
	@Override
	public int hashCode() {
		return pos.hashCode();
	}
}