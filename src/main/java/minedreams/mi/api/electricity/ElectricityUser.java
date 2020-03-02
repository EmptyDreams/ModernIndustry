package minedreams.mi.api.electricity;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import minedreams.mi.api.electricity.block.MachineBlock;
import minedreams.mi.api.electricity.cache.MachineCache;
import minedreams.mi.api.electricity.clock.OrdinaryCounter;
import minedreams.mi.api.electricity.clock.OverloadCounter;
import minedreams.mi.api.electricity.info.BiggerVoltage;
import minedreams.mi.api.electricity.info.EnumBiggerVoltage;
import minedreams.mi.api.electricity.info.EnumVoltage;
import minedreams.mi.api.electricity.interfaces.IVoltage;
import minedreams.mi.api.net.WaitList;
import minedreams.mi.register.te.AutoTileEntity;
import minedreams.mi.tools.Tools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * 所有电力设备的父级TE，其中包含了最为基础的方法和循环接口
 * @author EmptyDremas
 * @version V1.0
 */
@AutoTileEntity("IN_FATHER_ELECTRICITY_USER")
public abstract class ElectricityUser extends Electricity {
	
	/** 保存电器需要的电压 */
	private EnumVoltage voltage = EnumVoltage.ORDINARY;
	/** 保存所需电能最小值 */
	private int energyMin = 0;
	/** 保存所需电能正常值 */
	private int energy = 0;
	/** 该方块连接的发电机 */
	private final Map<EnumFacing, ElectricityMaker> linkedMaker = new HashMap<>(6);
	/** 连接电线的数量 */
	private final Map<EnumFacing, ElectricityTransfer> linkedWire = new HashMap<>(6);
	/** 计数器 */
	public final OverloadCounter COUNTER = new OrdinaryCounter(this);
	/** 过载超时后的操作 */
	protected BiggerVoltage biggerVoltageOperate = new BiggerVoltage(3, EnumBiggerVoltage.BOOM);
	/** 机器缓存 */
	private final MachineCache CACHE = new MachineCache(this);
	
	final MachineCache getCache() { return CACHE; }
	
	/** 设置过载超时后的操作 */
	protected final Electricity setBiggerVoltageOperate(BiggerVoltage bv) {
		biggerVoltageOperate = bv;
		return this;
	}
	/** 获取过载超时后的操作 */
	public final BiggerVoltage getBiggerVoltageOperate() {
		return biggerVoltageOperate;
	}
	
	/**
	 * 连接一个电线
	 * @throws NullPointerException 如果 et == null
	 */
	public void link(ElectricityTransfer et) {
		WaitList.checkNull(et, "et");
		linkedWire.put(Tools.whatFacing(pos, et.getPos()), et);
	}
	
	/**
	 * 获取已连接的电线
	 * @return 返回值为源数据的副本，可以随意修改
	 */
	public final Map<EnumFacing, ElectricityTransfer> getLinkedWire() {
		return new HashMap<>(linkedWire);
	}
	
	/** 是否连接电线 */
	public final boolean isLinkWire() { return !linkedWire.isEmpty(); }
	
	/**
	 * 获取已连接的发电机
	 * @return 返回值为源数据的副本，可以随意修改
	 */
	@Nonnull
	public final Map<EnumFacing, ElectricityMaker> getLinkedMaker() {
		return linkedMaker;
	}
	
	/**
	 * 连接一个发电机，这个方法一般由{@link MachineBlock}调用
	 * @throws NullPointerException 如果 maker == null
	 */
	public void link(ElectricityMaker maker) {
		WaitList.checkNull(maker, "maker");
		linkedMaker.put(Tools.whatFacing(pos, maker.getPos()), maker);
	}
	
	/**
	 * 删除一个连接，这个方法一般由{@link MachineBlock}调用
	 * @throws NullPointerException 如果 fromPos == null
	 */
	public final void removeLink(BlockPos fromPos) {
		WaitList.checkNull(fromPos, "fromPos");
		EnumFacing facing = Tools.whatFacing(pos, fromPos);
		linkedMaker.remove(facing);
		linkedWire.remove(facing);
	}
	
	public final int getEnergyMin() {
		return energyMin;
	}
	public final void setEnergyMin(int energyMin) {
		this.energyMin = energyMin;
	}
	
	public final int getEnergy() {
		return energy;
	}
	public final void setEnergy(int energy) {
		this.energy = energy;
	}
	
	/**
	 * 设置电器所需电压
	 * @throws NullPointerException 如果 voltage == null
	 */
	public final void setVoltage(EnumVoltage voltage) {
		WaitList.checkNull(voltage, "voltage");
		this.voltage = voltage;
	}
	/** 获取电器所需电压 */
	public final EnumVoltage getVoltage() { return voltage; }
	
	@Override
	public void update() {
		updateInfo();
		Object o = run();
		if (o != null) {
			EleWorker.useEleEnergy(this);
		}
	}
	
	/**
	 * 机器运行的接口，该接口不应该处理电器运行内容，
	 * 应当处理电器运行前的例行检查，机器运行操作应放入{@link #useElectricity(Object, int, EnumVoltage)}
	 *
	 * @return 返回值意义由子类定义，以确定的是返回null表示不运行，
	 *          当需要运行但是不需要传递信息时返回{@link #NO_HAVE_INFO}
	 */
	public abstract Object run();
	
	/**
	 * 使用电能，该方法在用户设置取电标志后调用。该方法用来处理电器运行时的操作，
	 * 例如：计算工作进度；更新数据等操作。<br>
	 *
	 * <pre>该方法在设置标志后在一下情况不一定被调用：
	 * 1.电力供给不足；
	 * 2.电器运行前因电力供给错误而损坏；
	 * 3.用户(或其他用户)手动跳过了该电器的运行
	 * 4.TE中存储的world对象为null
	 * 5.TE中其它与方块相关的信息错误</pre>
	 *
	 * <b>调用该方法时电压不一定为用电器需求电压</b>
	 *
	 * @param energy 实际输入电能
	 * @param voltage 实际输入电压
	 *
	 * @return boolean 电力是否被消耗，若返回false则表示电器没有消耗电力，将返回电力损耗
	 */
	public abstract boolean useElectricity(int energy, IVoltage voltage);
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		compound.setInteger("makers_size", linkedMaker.size());
		compound.setInteger("transfers_size", linkedWire.size());
		
		int i = 0;
		for (Map.Entry<EnumFacing, ElectricityMaker> entry : linkedMaker.entrySet()) {
			Tools.writeBlockPos(compound, entry.getValue().getPos(), "maker_pos_" + i);
			++i;
		}
		
		i = 0;
		for (Map.Entry<EnumFacing, ElectricityTransfer> entry : linkedWire.entrySet()) {
			Tools.writeBlockPos(compound, entry.getValue().getPos(), "transfer_pos_" + i);
			++i;
		}
		
		return compound;
	}
	
	private BlockPos[] _makers;
	private BlockPos[] _transfers;
	
	private void updateInfo() {
		if (_makers == null) return;
		for (BlockPos pos : _makers) {
			linkedMaker.put(Tools.whatFacing(getPos(), pos), (ElectricityMaker) world.getTileEntity(pos));
		}
		for (BlockPos pos : _transfers) {
			linkedWire.put(Tools.whatFacing(getPos(), pos), (ElectricityTransfer) world.getTileEntity(pos));
		}
		_makers = null;
		_transfers = null;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		int size = compound.getInteger("makers_size");
		_makers = new BlockPos[size];
		for (int i = 0; i < size; ++i) {
			_makers[i] = Tools.readBlockPos(compound, "maker_pos_" + i);
		}
		
		size = compound.getInteger("transfers_size");
		_transfers = new BlockPos[size];
		for (int i = 0; i < size; ++i) {
			_transfers[i] = Tools.readBlockPos(compound, "transfer_pos_" + i);
		}
	}
}
