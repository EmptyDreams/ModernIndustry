package xyz.emptydreams.mi.api.electricity.src.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.emptydreams.mi.api.electricity.capabilities.EleCapability;
import xyz.emptydreams.mi.api.electricity.capabilities.ILink;
import xyz.emptydreams.mi.api.electricity.capabilities.LinkCapability;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.capabilities.EnergyRange;
import xyz.emptydreams.mi.api.electricity.capabilities.IStorage;
import xyz.emptydreams.mi.api.electricity.clock.OverloadCounter;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.event.EnergyEvent;
import xyz.emptydreams.mi.api.utils.TEHelper;

/**
 * 机器的父类，其中包含了机器的一些默认实现
 * @author EmptyDreams
 * @version V2.1
 */
@Mod.EventBusSubscriber
public abstract class EleTileEntity extends TileEntity implements TEHelper {
	
	/** 空的能量 */
	private static final EleEnergy EMPTY_ENERGY = new EleEnergy();
	
	/** 存储需要的能量 */
	private final EnergyRange energyRange = new EnergyRange();
	/** 当前包含的能量 */
	@Storage private int nowEnergy;
	/** 当前输出电压 */
	@Storage private IVoltage exVoltage;
	/** 当前输入电压 */
	@Storage private IVoltage reVoltage;
	/** 1Tick接受的最大能量 */
	private int maxReceive;
	/** 1Tick输出的最大能量 */
	private int maxExtract;
	/** 超载计时器 */
	private OverloadCounter counter;
	/** 是否可以接收能量 */
	private boolean isReceive = false;
	/** 是否可以输出能量 */
	private boolean isExtract = false;
	/** 存储已连接的方块 */
	@Storage
	private final Set<BlockPos> linkedBlocks = new HashSet<>(3);
	
	/**
	 * @param minEnergy 可接收/输出的能量最小值
	 * @param maxEnergy 可接收/输出的能量最大值
	 * @param minVoltage 可接收/输出的电压最小值
	 * @param maxVoltage 可接收/输出的电压最大值
	 */
	public EleTileEntity(int minEnergy, int maxEnergy, IVoltage minVoltage, IVoltage maxVoltage) {
		energyRange.setMinEnergy(minEnergy);
		energyRange.setMaxEnergy(maxEnergy);
		energyRange.setMinVoltage(minVoltage);
		energyRange.setMaxVoltage(maxVoltage);
	}
	
	@Override
	public void validate() {
		super.validate();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
	}
	
	/**
	 * 当电器接收电能时调用，该方法只在客户端调用
	 * @param energy 这次接收的能量
	 * @return 返回false可以阻止此次电能的消耗
	 */
	public boolean onReceive(EleEnergy energy) { return true; }
	
	/**
	 * 当电器输出电能时调用，该方法只在客户端调用
	 * @param energy 这次输出的能量
	 * @return 返回false可以组织此次电能的输出
	 */
	public boolean onExtract(EleEnergy energy) { return true; }
	
	/** @see IStorage#isReAllowable(EnumFacing) */
	public abstract boolean isReAllowable(EnumFacing facing);
	/** @see IStorage#isExAllowable(EnumFacing) */
	public abstract boolean isExAllowable(EnumFacing facing);
	
	/** @see ILink#canLink(EnumFacing) */
	public boolean canLink(EnumFacing facing) { return hasCapability(EleCapability.ENERGY, facing); }
	/** @see ILink#link(BlockPos) */
	public boolean link(BlockPos pos) { return linkedBlocks.add(pos); }
	/** @see ILink#unLink(BlockPos) */
	public boolean unLink(BlockPos pos) { return linkedBlocks.remove(pos); }
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		TEHelper.super.writeToNBT(compound);
		return super.writeToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		TEHelper.super.readFromNBT(compound);
	}
	
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
		return capability == EleCapability.ENERGY || capability == LinkCapability.LINK;
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
		} else if (capability == LinkCapability.LINK) {
			return LinkCapability.LINK.cast(linkInfo);
		}
		return super.getCapability(capability, facing);
	}
	
	/** 获取能量范围 */
	public EnergyRange getEnergyRange() { return energyRange.copy(); }
	/** 获取现在的能量 */
	public int getNowEnergy() { return nowEnergy; }
	/** 设置现在的能量 */
	public void setNowEnergy(int nowEnergy) { this.nowEnergy = nowEnergy; }
	/** 获取现在输出的电压 */
	public IVoltage getExVoltage() { return exVoltage; }
	/** 设置现在输出的电压 */
	public void setExVoltage(IVoltage exVoltage) { this.exVoltage = exVoltage; }
	/** 获取现在接收的电压 */
	public IVoltage getReVoltage() { return reVoltage; }
	/** 设置现在就收的电压 */
	public void setReVoltage(IVoltage reVoltage) { this.reVoltage = reVoltage; }
	/** 获取1Tick中可接收的最大能量 */
	public int getMaxReceive() { return maxReceive; }
	/** 设置1Tick中可接收的最大能量 */
	protected void setMaxReceive(int maxReceive) { this.maxReceive = maxReceive; }
	/** 获取1Tick中可输出的最大能量 */
	public int getMaxExtract() { return maxExtract; }
	/** 设置1Tick中可输出的最大能量 */
	protected void setMaxExtract(int maxExtract) { this.maxExtract = maxExtract; }
	/** 获取计数器 */
	public OverloadCounter getCounter() { return counter; }
	/** 设置计数器 */
	protected void setCounter(OverloadCounter counter) { this.counter = counter; }
	/** 是否可以接收能量 */
	public boolean isReceive() { return isReceive; }
	/** 设置是否可以接收能量 */
	protected void setReceive(boolean receive) { isReceive = receive; }
	/** 是否可以输出能量 */
	public boolean isExtract() { return isExtract; }
	/** 设置是否可以输出能量 */
	protected void setExtract(boolean extract) { isExtract = extract; }
	/** 获取能量接口 */
	public IStorage getStorage() { return storage; }
	/** 设置能量接口 */
	protected void setStorage(IStorage storage) { this.storage = storage; }
	/** 获取连接限制接口 */
	public ILink getLinkInfo() { return linkInfo; }
	
	/** 能量接口 */
	private IStorage storage = new IStorage() {
		@Override
		public boolean canReceive() { return isReceive; }
		@Override
		public boolean canExtract() { return isExtract; }
		@Nonnull
		@Override
		public EnergyRange getEnergyRange() { return energyRange.copy(); }
		
		@Override
		public int receiveEnergy(EleEnergy energy, boolean simulate) {
			if (world.isRemote) return 0;
			if (canReceive()) {
				//若当前储存能量已满则不再接收能量
				if (nowEnergy < maxReceive) {
					if (simulate) {
						return Math.min(maxReceive - nowEnergy, energy.getEnergy());
					} else {
						int k = Math.min(maxReceive - nowEnergy, energy.getEnergy());
						IVoltage voltage = energyRange.getOptimalVoltage(energy.getVoltage());
						if (!onReceive(new EleEnergy(k, voltage))) return 0;
						//若输入电压不在适用电压范围内，则增加计数器
						if (voltage.getVoltage() != energy.getVoltage().getVoltage()) {
							getCounter().plus();
						}
						nowEnergy += k;
						if (reVoltage == null) reVoltage = energy.getVoltage().copy();
						//触发事件
						MinecraftForge.EVENT_BUS.post(
								new EnergyEvent.Receive(new EleEnergy(k, voltage), EleTileEntity.this));
						return k;
					}
				}
			}
			return 0;
		}
		
		@Override
		public EleEnergy extractEnergy(EleEnergy energy, boolean simulate) {
			if (world.isRemote) return EMPTY_ENERGY.copy();
			if (canExtract()) {
				//若存储能量或需要输出的能量小于等于0则直接返回
				if (nowEnergy <= 0 || energy.getEnergy() <= 0) return EMPTY_ENERGY.copy();
				
				//计算应该输出的电能
				int k = Math.min(nowEnergy, energy.getEnergy());
				//计算最适电压
				IVoltage voltage = energyRange.getOptimalVoltage(energy.getVoltage());
				//若需要输出的电压不在可输出的范围则输出最适电压
				EleEnergy reEnergy = new EleEnergy(k, voltage);
				if (!simulate) {
					if (!onExtract(new EleEnergy(k, voltage))) return EMPTY_ENERGY.copy();
					if (exVoltage == null) exVoltage = energy.getVoltage().copy();
					nowEnergy -= k;
					//触发事件
					MinecraftForge.EVENT_BUS.post(
							new EnergyEvent.Extract(reEnergy.copy(), EleTileEntity.this));
				}
				return reEnergy;
			}
			return EMPTY_ENERGY.copy();
		}
		
		@Override
		public boolean isReAllowable(EnumFacing facing) {
			return EleTileEntity.this.isReAllowable(facing);
		}
		
		@Override
		public boolean isExAllowable(EnumFacing facing) {
			return EleTileEntity.this.isExAllowable(facing);
		}
	};
	private final ILink linkInfo = new ILink() {
		@Override
		public boolean canLink(EnumFacing facing) {
			return EleTileEntity.this.canLink(facing);
		}
		@Override
		public boolean link(BlockPos pos) {
			return EleTileEntity.this.link(pos);
		}
		@Override
		public boolean unLink(BlockPos pos) { return EleTileEntity.this.unLink(pos); }
		@Nonnull
		@Override
		public Collection<BlockPos> getLinks() {
			return new HashSet<>(linkedBlocks);
		}
		@Override
		public boolean isLink(BlockPos pos) {
			return linkedBlocks.contains(pos);
		}
	};
	
	@Override
	public String toString() {
		return "EleTileEntity{ pos=" + pos + '}';
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}
	
	/** 在每Tick结尾将类中临时数据清空 */
	@SubscribeEvent
	public static void onTickEnd(TickEvent.ServerTickEvent event) {
		World[] worlds = FMLCommonHandler.instance().getMinecraftServerInstance().worlds;
		EleTileEntity entity;
		for (World world : worlds) {
			for (TileEntity te : world.loadedTileEntityList) {
				if (te instanceof EleTileEntity) {
					entity = (EleTileEntity) te;
					entity.reVoltage = entity.exVoltage = null;
				}
			}
		}
	}
	
}
