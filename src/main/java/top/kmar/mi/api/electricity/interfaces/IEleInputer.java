package top.kmar.mi.api.electricity.interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.electricity.EleWorker;
import top.kmar.mi.api.utils.WorldUtil;
import top.kmar.mi.api.electricity.info.VoltageRange;

import java.util.HashMap;
import java.util.Map;

/**
 * 可以输入电能的方块托管
 * @author EmptyDreams
 */
public interface IEleInputer extends IRegister {
	
	/**
	 * 获取方块需要的正常电能
	 * @param te 对应方块的TE
	 * @throws ClassCastException 如果不支持输入的TE
	 */
	int getEnergy(TileEntity te);
	
	/**
	 * 根据输入的电能判断电器可消耗的电能
	 * @param now 当前方块
	 * @param energy 能量值
	 * @return 返回值 ≤ energy
	 * @throws ClassCastException 如果不支持输入的TE
	 */
	int getEnergy(TileEntity now, int energy);
	
	/**
	 * 使方块使用电能
	 * @param now 当前方块
	 * @param energy 能量
	 * @param voltage 电压
	 */
	int useEnergy(TileEntity now, int energy, IVoltage voltage);
	
	/**
	 * 获取用电器需要的电压
	 * @param now 对应方块的TE
	 * @throws ClassCastException 如果不支持输入的TE
	 */
	IVoltage getVoltage(TileEntity now, IVoltage voltage);
	
	/**
	 * 获取用电器可接受的电压范围
	 * @param now 对应方块的TE
	 * @throws ClassCastException 如果不支持输入的TE
	 */
	VoltageRange getVoltageRange(TileEntity now);
	
	/**
	 * 判断当前方块能否从指定方向获取电能
	 * @param now 当前方块
	 * @param facing 指定方向
	 * @throws ClassCastException 如果不支持输入的TE
	 */
	boolean isAllowable(TileEntity now, EnumFacing facing);
	
	/**
	 * 获取当前方块周围的已连接的传输方块
	 * @param now 当前方块
	 */
	default Map<TileEntity, IEleTransfer> getTransferAround(TileEntity now) {
		Map<TileEntity, IEleTransfer> list = new HashMap<>(4);
		WorldUtil.forEachAroundTE(now.getWorld(), now.getPos(), (te, facing) -> {
			IEleTransfer et = EleWorker.getTransfer(te);
			if (et != null && et.isLink(te, now)) list.put(te,et);
		});
		return list;
	}
	
}