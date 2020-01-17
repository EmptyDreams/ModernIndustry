package minedreams.mi.api.electricity;

import javax.annotation.Nonnull;
import java.util.Objects;

import minedreams.mi.api.electricity.info.BiggerVoltage;
import minedreams.mi.api.electricity.info.ElectricityEnergy;
import minedreams.mi.api.exception.RepeatSettingException;
import minedreams.mi.api.net.MessageBase;
import minedreams.mi.api.net.message.MessageList;
import minedreams.mi.api.net.WaitList;
import minedreams.mi.blocks.te.AutoTileEntity;
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
	
	/** 正在通过的电能 */
	protected ElectricityEnergy nowEE = ElectricityEnergy.craet(0, 0);
	/** 是否请求电能 */
	protected boolean isInput = false;
	
	/**
	 * 处理内部事务和网络传输
	 */
	@Override
	public final void update() {
		if (world.isRemote) {
			if (isOverload(nowEE)) {
				++biggerTime;
			} else {
				biggerTime = 0;
			}
			MessageList list = send(true);
			if (list != null) {
				MessageBase mb = new MessageBase();
				mb.setMessageList(list);
				mb.setPos(getPos());
				mb.setDimension(world.provider.getDimension());
				WaitList.sendToService(mb);
			}
			
		} else {
			run();
			MessageList list = send(false);
			if (list != null) {
				MessageBase mb = new MessageBase();
				mb.setMessageList(list);
				mb.setPos(getPos());
				mb.setDimension(world.provider.getDimension());
				WaitList.sendToClient(mb, mb.getMessageList().getPlayers());
			}
		}
	}
	
	/**
	 * 设置方块类型，注意：如果在blocktype为null时调用{@link #getBlockType()}
	 * 则会自动调用该方法
	 *
	 * @throws RepeatSettingException 如果重复设置方块类型则抛出该错误
	 */
	public final void setBlockType(Block block) throws RepeatSettingException {
		if (this.block != null) throw new RepeatSettingException("方块种类只允许设置一次！");
		this.block = block;
	}
	
	private Block block;
	@Override
	public final Block getBlockType() {
		if (block == null) {
			block = super.getBlockType();
			return block;
		}
		return block;
	}
	
	/**
	 * 机器运行的接口，该接口不应该处理电器运行内容，
	 * 应当处理电器运行前的例行检查
	 *
	 * @return boolean 是否成功完成运行，如果返回false，系统将自动播放中断音效
	 */
	public abstract boolean run();
	
	/**
	 * 发送信息，若该方法在客户端运行则发送给服务端，否则发送给客户端.<br>
	 * <b>注意：重写该方法必须重写{@link #reveive(MessageList)}方法！</b>
	 *
	 * @return 返回值为null时表示不需要发送信息
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
		throw new AssertionError("重写了send()没有重写reveive()方法，或者重写后再次调用了原始版本");
	}
	
	/** 判断电器是否过载 */
	abstract public boolean isOverload(ElectricityEnergy now);
	
	/** 更新能量记录 */
	public void updateEleEnergy(ElectricityEnergy ee) {
		nowEE.setEnergy(ee.getEnergy());
		nowEE.setVoltage(ee.getVoltage());
	}
	
	/**
	 * 添加取电标记
	 */
	protected final void markCollection() {
		isInput = true;
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
	
	/** 获取需要的电压/电能 */
	public ElectricityEnergy getEnergy() {
		return ElectricityEnergy.craet(me, (getMinVoltage() + getMaxVoltage()) / 2);
	}
	
	/** 获取额定电能 */
	public final int getMe() {
		return me;
	}
	
	/** 获取最小电压 */
	public final int getMinVoltage() {
		return minVoltage;
	}
	
	/** 获取最大电压 */
	public final int getMaxVoltage() {
		return maxVoltage;
	}
	
	/** 获取额定电压 */
	public final int getVoltage() { return (getMinVoltage() + getMaxVoltage()) / 2; }
	
	/** 获取最长过载时间 */
	public final int getBiggerMaxTime() {
		return biggerMaxTime;
	}
	
	/** 获取过载超时后的操作 */
	public final BiggerVoltage getBiggerVoltageOperate() {
		return biggerVoltageOperate;
	}
	
	/** 电能 */
	protected int me = 1000;
	/** 可以承受的最大电压 */
	protected int maxVoltage = 120;
	/** 运行最低电压 */
	protected int minVoltage = 80;
	/** 过载最长时间 */
	protected int biggerMaxTime = 5000;
	/** 过载超时后的操作 */
	protected BiggerVoltage biggerVoltageOperate = new BiggerVoltage(3);
	
	/** 已过载时间 */
	protected int biggerTime = 0;
	
	/** 设置过载超时后的操作 */
	protected final Electricity setBiggerVoltageOperate(BiggerVoltage bv) {
		biggerVoltageOperate = bv;
		return this;
	}
	
	/** 设置过载最长时间(单位：tick，默认值：5000tick)，当设置时间小于0时保持原设置不变 */
	protected final Electricity setBiggerMaxTime(int bvt) {
		biggerMaxTime = (bvt >= 0) ? bvt : biggerMaxTime;
		return this;
	}
	
	/** 设置最大承受电压 */
	protected final Electricity setMaxVolte(int max) {
		maxVoltage = (max > 0) ? max : maxVoltage;
		return this;
	}
	
	/** 设置所需电能（单位：me，默认值：1000me），如果能量小于0，则保持原设置不变 */
	protected final Electricity setEnergy(int me) {
		if (me >= 0) this.me = me;
		return this;
	}
	
	/**
	 * 设置所需电压（单位：V，默认值：100V），如果电压小于等于0，则保持原设置不变
	 */
	protected final Electricity setMinVoltage(int v) {
		if (v >= 0) minVoltage = v;
		return this;
	}
	
	@Override
	public String toString() {
		return "Electricity{ world=" + world.provider.getDimensionType() +
				            "，pos=" + pos +
					        "，block=" + block +
					        "，nowEE=" + nowEE +
					        '}';
	}
	
	/**
	 * 深度比较两对象是否相等，当两对象所在世界都为null时比较结果可能不准确，
	 * 切记不可使用内存地址比较该对象，因为TE对象在显示更新时可能会重新建立，
	 * 从而导致内存地址变动。
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof Electricity) {
			return equals((Electricity) obj);
		}
		return false;
	}
	
	/**
	 * 深度比较两对象是否相等，当两对象所在世界都为null时比较结果可能不准确
	 */
	public boolean equals(Electricity user) {
		if (world == null) {
			if (user.world != null) return false;
			return (biggerVoltageOperate == user.biggerVoltageOperate) &&
					       (maxVoltage == user.maxVoltage) &&
					       (biggerMaxTime == user.biggerMaxTime) &&
					       pos.equals(user.pos) && minVoltage == user.minVoltage &&
					       me == user.me;
		}
		return world.equals(user.world) &&
				       (biggerVoltageOperate == user.biggerVoltageOperate) &&
				       (maxVoltage == user.maxVoltage) &&
				       (biggerMaxTime == user.biggerMaxTime) &&
				       pos.equals(user.pos) && minVoltage == user.minVoltage &&
				       me == user.me;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(pos, me, minVoltage, maxVoltage);
	}
	
}
