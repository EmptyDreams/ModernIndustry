package xyz.emptydreams.mi.api.electricity.capabilities;

import javax.annotation.Nonnull;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;
import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

/**
 * 表示能量
 * @author EmptyDreams
 * @version V1.0
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
	IVoltage getVoltage(EnumEleState state, IVoltage voltage);
	
	/**
	 * 尝试输出能量，当需求输出的能量的电压不在可输出电压范围内时，输出最适电压而非不输出
	 * @param energy 输出的能量值
	 * @param simulate 是否为模拟，若为true则该操作不修改实际内容
	 * @return 实际输出的能量
	 */
	@Nonnull
	EleEnergy extractEnergy(EleEnergy energy, boolean simulate);
	
	/** @see xyz.emptydreams.mi.api.electricity.interfaces.IEleOutputer#fallback(TileEntity, int)  */
	void fallback(int energy);
	
	/** 是否可以从指定方向输入电能 */
	boolean isReAllowable(EnumFacing facing);
	
	/** 是否可以从指定方向输出电能 */
	boolean isExAllowable(EnumFacing facing);
	
}
