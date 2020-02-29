package minedreams.mi.api.electricity;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import minedreams.mi.api.electricity.info.UseOfInfo;
import minedreams.mi.api.electricity.info.PathInfo;
import minedreams.mi.api.electricity.interfaces.IEleInputer;
import minedreams.mi.api.electricity.interfaces.IEleOutputer;
import minedreams.mi.api.electricity.interfaces.IEleTransfer;
import minedreams.mi.api.electricity.interfaces.IVoltage;
import minedreams.mi.api.net.WaitList;
import minedreams.mi.tools.MISysInfo;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Mod;

/**
 * 关于电力系统的工作都在这里进行<br>
 *
 * @author EmptyDreams
 * @version V1.0
 */
@Mod.EventBusSubscriber
public final class EleWorker {
	
	private static final Collection<IEleOutputer> OUTPUTERS = new HashSet<>();
	private static final Collection<IEleInputer> INPUTERS = new HashSet<>();
	private static final Collection<IEleTransfer> TRANSFERS = new HashSet<>();
	
	/**
	 * 注册一个Outputer
	 * @throws NullPointerException 如果outputer == null
	 */
	public static void registerOutputer(IEleOutputer outputer) {
		WaitList.checkNull(outputer, "outputer");
		OUTPUTERS.add(outputer);
	}
	
	/**
	 * 注册一个Inputer
	 * @throws NullPointerException 如果inputer == null
	 */
	public static void registerInputer(IEleInputer inputer) {
		WaitList.checkNull(inputer, "inputer");
		INPUTERS.add(inputer);
	}
	
	/**
	 * 注册一个Transfer
	 * @throws NullPointerException 如果transfer == null
	 */
	public static void registerTransfer(IEleTransfer transfer) {
		WaitList.checkNull(transfer, "transfer");
		TRANSFERS.add(transfer);
	}
	
	/**
	 * 判断指定TE是否支持电力传输.
	 * 传入null虽然不会报错但是会造成不必要的性能损耗，所以尽量不要传入null。
	 */
	public static boolean isTransfer(TileEntity te) {
		return getTransfer(te) != null;
	}
	
	/**
	 * 判断指定TE是否支持电力输出.
	 * 传入null虽然不会报错但是会造成不必要的性能损耗，所以尽量不要传入null。
	 */
	public static boolean isOutputer(TileEntity te) {
		return getOutputer(te) != null;
	}
	
	/**
	 * 判断指定TE是否支持电力输入.
	 * 传入null虽然不会报错但是会造成不必要的性能损耗，所以尽量不要传入null。
	 */
	public static boolean isInputer(TileEntity te) {
		return getInputer(te) != null;
	}
	
	public static IEleTransfer getTransfer(TileEntity te) {
		for (IEleTransfer transfer : TRANSFERS) {
			if (transfer.contains(te)) return transfer;
		}
		return null;
	}
	
	public static IEleOutputer getOutputer(TileEntity te) {
		for (IEleOutputer outputer : OUTPUTERS) {
			if (outputer.contains(te)) return outputer;
		}
		return null;
	}
	
	public static IEleInputer getInputer(TileEntity te) {
		for (IEleInputer inputer : INPUTERS) {
			if (inputer.contains(te)) return inputer;
		}
		return null;
	}
	
	/**
	 * 让指定方块使用电能，如果该方块没有对应的托管的话只会输出一个错误
	 * @param te 指定方块
	 */
	public static boolean useEleEnergy(TileEntity te) {
		for (IEleInputer inputer : INPUTERS) {
			if (inputer.contains(te)) {
				return useEleEnergy(te, inputer);
			}
		}
		MISysInfo.err("该方块没有找到可用的托管：" + te);
		return false;
	}
	
	/**
	 * 让指定方块使用电能
	 * @param te 指定方块
	 * @param inputer 支持该方块的托管
	 */
	public static boolean useEleEnergy(TileEntity te, IEleInputer inputer) {
		Map<TileEntity, IEleOutputer> outs = inputer.getOutputerAround(te);
		if (outs.isEmpty()) {
			Map<TileEntity, IEleTransfer> transfers = inputer.getTransferAround(te);
			if (transfers.isEmpty()) return false;
			
			PathInfo realPath = null;
			for (Map.Entry<TileEntity, IEleTransfer> entry : transfers.entrySet()) {
				PathInfo pathInfo = entry.getValue().findPath(entry.getKey(), te, inputer);
				if (pathInfo.getOutput() == null) continue;
				if (realPath == null || realPath.getLossEnergy() > pathInfo.getLossEnergy())
					realPath = pathInfo;
			}
			
			if (realPath == null) return false;
			realPath.invoke(te, inputer);
			lineTransfer(realPath.getPath(),
					realPath.getEnergy() + realPath.getLossEnergy(), realPath.getVoltage());
		} else {
			IVoltage voltage = inputer.getVoltage(te);
			int allEnergy = inputer.getEnergy(te);
			int minEnergy = inputer.getEnergyMin(te);
			
			int realEnergy = 0;
			TileEntity realOut = null;
			IEleOutputer realOutputer = null;
			IVoltage realVoltage = null;
			
			for (Map.Entry<TileEntity, IEleOutputer> enerty : outs.entrySet()) {
				IEleOutputer outputer = enerty.getValue();
				TileEntity out = enerty.getKey();
				UseOfInfo outputInfo = outputer.output(out, Integer.MAX_VALUE, voltage, true);
				if (outputInfo.getEnergy() >= allEnergy) {
					realOut = out;
					realOutputer = outputer;
					realVoltage = outputInfo.getVoltage();
					break;
				} else if (outputInfo.getEnergy() >= minEnergy && outputInfo.getEnergy() > realEnergy) {
					realEnergy = outputInfo.getEnergy();
					realOut = out;
					realOutputer = outputer;
					realVoltage = outputInfo.getVoltage();
				}
			}
			
			if (realOut == null) return false;
			realOutputer.output(realOut, realEnergy, realVoltage, false);
			inputer.input(te, realEnergy, realVoltage);
			return true;
		}
		return false;
	}
	
	/**
	 * 让指定线路运输电能
	 * @param line 线路
	 * @param energy 电能
	 * @param voltage 电压
	 * @throws NullPointerException 如果line中的某个电缆没有对应的托管
	 */
	public static void lineTransfer(Iterable<TileEntity> line, int energy, IVoltage voltage) {
		IEleTransfer transfer;
		Map<IEleTransfer, Object> infos = new HashMap<>(2);
		try {
			for (TileEntity entity : line) {
				transfer = getTransfer(entity);
				infos.put(transfer, transfer.transfer(
						entity, energy, voltage, infos.getOrDefault(transfer, null)));
			}
		} catch (NullPointerException e) {
			throw new NullPointerException("线路中有至少一个电缆方块没有托管！");
		}
	}
	
}
