package minedreams.mi.api.electricity.interfaces;

import java.util.HashMap;
import java.util.Map;

import minedreams.mi.api.electricity.EleWorker;
import minedreams.mi.tools.Tools;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * 可以输入电能的方块托管
 * @author EmptyDreams
 * @version V1.0
 */
public interface IEleInputer extends IRegister {
	
	/**
	 * 向指定方块输入指定的电能
	 * @param te 对应方块的TE
	 * @param energy 电能
	 * @param voltage 电压
	 * @throws NullPointerException if te == null || voltage == null
	 * @throws ClassCastException 如果不支持输入的TE
	 */
	void input(TileEntity te, int energy, IVoltage voltage);
	
	/** 获取方块最小需要的电能 */
	int getEnergyMin(TileEntity te);
	
	/**
	 * 获取方块需要的正常电能
	 * @param te 对应方块的TE
	 * @throws ClassCastException 如果不支持输入的TE
	 */
	int getEnergy(TileEntity te);
	
	/**
	 * 获取用电器需要的电压
	 * @param te 对应方块的TE
	 * @throws ClassCastException 如果不支持输入的TE
	 */
	IVoltage getVoltage(TileEntity te);
	
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
		Tools.forEachAroundTE(now.getWorld(), now.getPos(), (te, facing) -> {
			IEleTransfer et = EleWorker.getTransfer(te);
			if (et != null && et.isLink(te, now)) list.put(te,et);
		});
		return list;
	}
	
	/**
	 * 获取当前方块周围的可供电发电机方块
	 * @param now 当前方块
	 */
	default Map<TileEntity, IEleOutputer> getOutputerAround(TileEntity now) {
		Map<TileEntity, IEleOutputer> list = new HashMap<>(3);
		Tools.forEachAroundTE(now.getWorld(), now.getPos(), (te, facing) -> {
			IEleOutputer out = EleWorker.getOutputer(te);
			if (out != null && isAllowable(now, facing) && out.isAllowable(te, Tools.upsideDown(facing)))
				list.put(te, out);
		});
		return list;
	}
	
	/**
	 * 判断当前方块是否已经连接指定传输方块
	 * @param now 当前方块
	 * @param transfer 传输方块
	 */
	static boolean isLink(TileEntity now, TileEntity transfer) {
		IEleTransfer et = EleWorker.getTransfer(transfer);
		if (et == null) return false;
		return et.isLink(transfer, now);
	}
	
}
