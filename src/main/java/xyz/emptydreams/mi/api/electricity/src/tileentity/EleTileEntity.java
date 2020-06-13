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
import xyz.emptydreams.mi.api.electricity.capabilities.IStorage;
import xyz.emptydreams.mi.api.electricity.capabilities.LinkCapability;
import xyz.emptydreams.mi.api.electricity.clock.OverloadCounter;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.info.EnergyRange;
import xyz.emptydreams.mi.api.electricity.info.EnumEleState;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;
import xyz.emptydreams.mi.api.electricity.src.info.EnumVoltage;
import xyz.emptydreams.mi.api.event.EnergyEvent;
import xyz.emptydreams.mi.api.utils.data.TEHelper;

/**
 * 机器的父类，其中包含了机器的一些默认实现
 * @author EmptyDreams
 * @version V2.1
 */
@SuppressWarnings("unused")
@Mod.EventBusSubscriber
public abstract class EleTileEntity extends TileEntity implements TEHelper {
	
	/** 空的能量 */
	private static final EleEnergy EMPTY_ENERGY = new EleEnergy(0, EnumVoltage.NON);
	
	/** 可输出的能量范围 */
	private final EnergyRange extractRange = new EnergyRange();
	/** 可输入的能量范围 */
	private final EnergyRange reciveRange = new EnergyRange();
	/** 当前包含的能量 */
	@Storage private int nowEnergy;
	/** 当前输出电压 */
	@Storage private IVoltage exVoltage;
	/** 当前输入电压 */
	@Storage private IVoltage reVoltage;
	/** 可存储的最大能量 */
	private int maxEnergy = 0;
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
	 * 设定方块可以输出的能量范围
	 * @param minEnergy 最低能量值
	 * @param maxEnergy 最高能量值
	 * @param minVoltage 最低电压
	 * @param maxVoltage 最高电压
	 */
	public void setExtractRange(int minEnergy, int maxEnergy, IVoltage minVoltage, IVoltage maxVoltage) {
		extractRange.setMinEnergy(minEnergy);
		extractRange.setMaxEnergy(maxEnergy);
		extractRange.setMinVoltage(minVoltage);
		extractRange.setMaxVoltage(maxVoltage);
	}
	
	public void setReciveRange(int minEnergy, int maxEnergy, IVoltage minVoltage, IVoltage maxVoltage) {
		reciveRange.setMinEnergy(minEnergy);
		reciveRange.setMaxEnergy(maxEnergy);
		reciveRange.setMinVoltage(minVoltage);
		reciveRange.setMaxVoltage(maxVoltage);
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
	
	/** 获取输出能量范围 */
	public EnergyRange getExtractRange() { return extractRange.copy(); }
	/** 获取输入能量范围 */
	public EnergyRange getReciveRange() { return reciveRange.copy(); }
	/** 获取现在的能量 */
	public int getNowEnergy() { return nowEnergy; }
	/** 设置现在的能量 */
	public void setNowEnergy(int nowEnergy) { this.nowEnergy = Math.min(Math.max(nowEnergy, 0), getMaxEnergy()); }
	/** 获取现在输出的电压 */
	public IVoltage getExVoltage() { return exVoltage; }
	/** 设置现在输出的电压 */
	public void setExVoltage(IVoltage exVoltage) { this.exVoltage = exVoltage; }
	/** 获取现在接收的电压 */
	public IVoltage getReVoltage() { return reVoltage; }
	/** 设置现在就收的电压 */
	public void setReVoltage(IVoltage reVoltage) { this.reVoltage = reVoltage; }
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
	/** 获取可存储的能量值 */
	public int getMaxEnergy() { return maxEnergy; }
	/** 设置可存储的最大能量值 */
	public void setMaxEnergy(int max) { maxEnergy = max; }
	
	/** 能量接口 */
	private IStorage storage = new IStorage() {
		@Override
		public boolean canReceive() { return isReceive; }
		@Override
		public boolean canExtract() { return isExtract; }
		
		@Override
		public int receiveEnergy(EleEnergy energy, boolean simulate) {
			if (world.isRemote) return 0;
			if (canReceive()) {
				//若当前储存能量已满则不再接收能量
				if (nowEnergy < getMaxEnergy()) {
					if (simulate) {
						return reciveRange.getOptimalEnergy(Math.min(getMaxEnergy() - nowEnergy, energy.getEnergy()));
					} else {
						int k = reciveRange.getOptimalEnergy(Math.min(getMaxEnergy() - nowEnergy, energy.getEnergy()));
						IVoltage voltage = reciveRange.getOptimalVoltage(energy.getVoltage());
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
		public IVoltage getVoltage(EnumEleState state, IVoltage voltage) {
			return state == EnumEleState.IN ? getReciveRange().getOptimalVoltage(voltage) :
					       getExtractRange().getOptimalVoltage(voltage);
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
				IVoltage voltage = extractRange.getOptimalVoltage(energy.getVoltage());
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
		public void fallback(int energy) {
			nowEnergy += energy;
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
