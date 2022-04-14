package top.kmar.mi.api.electricity;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import top.kmar.mi.api.araw.interfaces.AutoSave;
import top.kmar.mi.api.capabilities.ele.EleCapability;
import top.kmar.mi.api.capabilities.ele.IStorage;
import top.kmar.mi.api.electricity.clock.OverloadCounter;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.tools.BaseTileEntity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * 机器的父类，其中包含了机器的一些默认实现
 * @author EmptyDreams
 */
public abstract class EleTileEntity extends BaseTileEntity {
	
	/** 空的能量 */
	private static final EleEnergy EMPTY_ENERGY = new EleEnergy(0, EleEnergy.ZERO);
	
	/** 当前包含的能量 */
	@AutoSave private int nowEnergy;
	/** 可存储的最大能量 */
	private int maxEnergy = 0;
	/** 超载计时器 */
	private OverloadCounter counter;
	/** 存储已连接的方块 */
	@AutoSave
	private final Set<BlockPos> linkedBlocks = new HashSet<>(3);
	
	/**
	 * <p>当电器接收电能时调用，该方法只在服务端调用
	 * <p>该方法还用来处理当接受到超过电器可承受范围的电压时增加计数器的操作
	 * @param energy 这次接收的能量
	 * @return 返回false可以阻止此次电能的消耗
	 */
	public abstract boolean onReceive(EleEnergy energy);
	
	/**
	 * 当电器输出电能时调用，该方法只在服务端调用
	 * @param energy 这次输出的能量
	 * @return 返回false可以组织此次电能的输出
	 */
	public boolean onExtract(EleEnergy energy) {
		return true;
	}
	
	/**
	 * 是否可以从指定方向输入电能
	 * @param facing 相对当前方块的方向，为null表示任意方向
	 */
	public abstract boolean isReceiveAllowable(EnumFacing facing);
	
	/**
	 * 是否可以从指定方向输出电
	 * @param facing 相对当前方块的方向，为null表示任意方向
	 */
	public abstract boolean isExtractAllowable(EnumFacing facing);
	
	/**
	 * 获取输出电压
	 * @return 如果不能输出则返回 0
	 */
	public abstract int getExVoltage();
	
	/** 获取当前所需能量 */
	public int getDemandEnergy() {
		return getMaxEnergy() - getNowEnergy();
	}
	
	/** 是否可以连接指定方向的方 */
	public boolean canLinkEle(EnumFacing facing) {
		return hasCapability(EleCapability.ENERGY, facing);
	}
	/** 连接指定方块 */
	public boolean linkEle(BlockPos pos) {
		if (linkedBlocks.contains(pos)) return true;
		return linkedBlocks.add(pos);
	}
	/** 取消链接指定方块 */
	public boolean unLink(BlockPos pos) { return linkedBlocks.remove(pos); }
	
	/**
	 * 判断方块某个方向是否含有指定能力<br>
	 * 要求：{@link #getCapability(Capability, EnumFacing)}返回非null值时该方法必须返回true，
	 * 该方法返回true时{@link #getCapability(Capability, EnumFacing)}必须返回非null值
	 * @param capability 能力
	 * @param facing 方向
	 */
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		if (super.hasCapability(capability, facing)) return true;
		return capability == EleCapability.ENERGY;
	}
	
	/**
	 * 获取方块某个方向的能力，能力不存在时返回null
	 * @param capability 指定的能力
	 * @param facing 方向
	 * @param <T> 能力类型
	 * @see #hasCapability(Capability, EnumFacing) 
	 */
	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == EleCapability.ENERGY) {
			return EleCapability.ENERGY.cast(storage);
		}
		return super.getCapability(capability, facing);
	}

	/** 获取现在的能量 */
	public int getNowEnergy() { return nowEnergy; }
	/** 设置现在的能量 */
	public void setNowEnergy(int nowEnergy) { this.nowEnergy = Math.min(Math.max(nowEnergy, 0), getMaxEnergy()); }
	/** 增加能量 */
	@SuppressWarnings("UnusedReturnValue")
	public int growEnergy(int grow) {
		int value = getNowEnergy() + grow;
		setNowEnergy(value);
		return value - getNowEnergy();
	}
	/**
	 * 减少能量
	 * @param shrink 要减少的数量
	 * @return 减少操作是否成功，若剩余能量小于shrink则返回false
	 */
	public boolean shrinkEnergy(int shrink) {
		int value = getNowEnergy() - shrink;
		if (value < 0) return false;
		setNowEnergy(value);
		return true;
	}
	/** 获取计数器 */
	public OverloadCounter getCounter() { return counter; }
	/** 设置计数器 */
	protected void setCounter(OverloadCounter counter) { this.counter = counter; }
	/** 获取可存储的能量值 */
	public int getMaxEnergy() { return maxEnergy; }
	/** 设置可存储的最大能量值 */
	public void setMaxEnergy(int max) { maxEnergy = max; }
	
	/** 能量接口 */
	private final IStorage storage = new IStorage() {
		
		@Override
		public boolean canReceive() {
			return isReceiveAllowable(null);
		}
		
		@Override
		public boolean canExtract() {
			return isExtractAllowable(null);
		}
		
		@Override
		public int getEnergyDemand() {
			return getDemandEnergy();
		}
		
		@Override
		public EleEnergy receiveEnergy(EleEnergy energy, boolean simulate) {
			if (world.isRemote || !canReceive()) return EMPTY_ENERGY.copy();
			EleEnergy real = energy.min(energy.copy(getMaxEnergy() - nowEnergy));
			if (simulate) return real;
			if (!onReceive(real)) return EMPTY_ENERGY.copy();
			nowEnergy += real.getCapacity();
			return real;
		}
		
		@Override
		public EleEnergy extractEnergy(int energy, boolean simulate) {
			if (world.isRemote) return EMPTY_ENERGY.copy();
			if (canExtract()) {
				//若存储能量或需要输出的能量小于等于0则直接返回
				if (nowEnergy <= 0 || energy <= 0) return EMPTY_ENERGY.copy();
				
				//计算应该输出的电能
				int real = Math.min(nowEnergy, energy);
				//若需要输出的电压不在可输出的范围则输出最适电压
				EleEnergy reEnergy = new EleEnergy(real, getExVoltage());
				if (!simulate) {
					if (!onExtract(reEnergy)) return EMPTY_ENERGY.copy();
					nowEnergy -= real;
				}
				return reEnergy;
			}
			return EMPTY_ENERGY.copy();
		}
		
		@Override
		public boolean isReAllowable(EnumFacing facing) {
			return EleTileEntity.this.isReceiveAllowable(facing);
		}
		
		@Override
		public boolean isExAllowable(EnumFacing facing) {
			return EleTileEntity.this.isExtractAllowable(facing);
		}
		
		@Override
		public boolean canLink(EnumFacing facing) {
			return EleTileEntity.this.canLinkEle(facing);
		}
		@Override
		public boolean link(BlockPos pos) {
			return EleTileEntity.this.linkEle(pos);
		}
		@Override
		public boolean unLink(BlockPos pos) { return EleTileEntity.this.unLink(pos); }
		@Override
		public boolean isLink(BlockPos pos) {
			return linkedBlocks.contains(pos);
		}
		
	};
	
}