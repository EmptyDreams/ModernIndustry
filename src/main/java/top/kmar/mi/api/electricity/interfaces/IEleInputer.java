package top.kmar.mi.api.electricity.interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import top.kmar.mi.api.electricity.EleWorker;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.utils.WorldUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 可以输入电能的方块托管
 * @author EmptyDreams
 */
public interface IEleInputer extends IRegister {
	
	/**
	 * 获取指定方块的能量需求
	 * @return 能量（单位：VC）
	 */
	int getEnergyDemand(TileEntity now);
	
	/**
	 * 根据输入的电能判断电器可消耗的电能
	 * @param now 当前方块
	 * @param energy 能量值
	 * @return 返回值 ≤ energy
	 * @throws ClassCastException 如果不支持输入的TE
	 */
	EleEnergy getEnergy(TileEntity now, EleEnergy energy);
	
	/**
	 * 使方块使用电能
	 * @param now 当前方块
	 * @param energy 输入的能量
	 */
	EleEnergy useEnergy(TileEntity now, EleEnergy energy);
	
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