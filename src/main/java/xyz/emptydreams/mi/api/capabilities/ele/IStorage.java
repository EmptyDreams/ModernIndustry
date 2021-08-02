package xyz.emptydreams.mi.api.capabilities.ele;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.info.VoltageRange;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * 表示能量
 * @author EmptyDreams
 */
public interface IStorage {
	
	/**
	 * 是否可以输入能量.
	 * 该方法进行判定时，不考虑除方块类型外其它信息，
	 * 只要方块有可能可以输入能量即返回true
	 */
	boolean canReceive();
	
	/**
	 * 是否可以输出能量.
	 * 该方法进行判定时，不考虑除方块类型外其它信息，
	 * 只要方块有可能可以输出能量即返回true
	 */
	boolean canExtract();
	
	/**
	 * 尝试输入能量
	 * @param energy 输入的能量值
	 * @param simulate 是否为模拟，若为true则该操作不修改实际内容
	 * @return 实际消耗/需要的能量值，若输入的能量值大于需要的，
	 *              则返回消耗的，若输入的能量值小于需要的，则返回需要的最小能量值
	 */
	int receiveEnergy(EleEnergy energy, boolean simulate);
	
	/** 获取最适电压. 若voltage在允许的电压范围内则返回voltage，否则返回最接近voltage的电压 */
	@Nonnull
	IVoltage getVoltage(EleStateEnum state, IVoltage voltage);
	
	/** 获取电压范围 */
	VoltageRange getReceiveVoltageRange();
	
	/**
	 * 尝试输出能量，当需求输出的能量的电压不在可输出电压范围内时，输出最适电压而非不输出
	 * @param energy 输出的能量值
	 * @param simulate 是否为模拟，若为true则该操作不修改实际内容
	 * @return 实际输出的能量
	 */
	@Nonnull
	EleEnergy extractEnergy(int energy, VoltageRange voltage, boolean simulate);
	
	/** 是否可以从指定方向输入电能 */
	boolean isReAllowable(EnumFacing facing);
	
	/** 是否可以从指定方向输出电能 */
	boolean isExAllowable(EnumFacing facing);
	
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