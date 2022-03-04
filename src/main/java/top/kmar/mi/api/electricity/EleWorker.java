package top.kmar.mi.api.electricity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.Mod;
import top.kmar.mi.data.info.EnumVoltage;
import top.kmar.mi.api.electricity.info.EleEnergy;
import top.kmar.mi.api.electricity.info.PathInfo;
import top.kmar.mi.api.electricity.interfaces.IEleInputer;
import top.kmar.mi.api.electricity.interfaces.IEleOutputer;
import top.kmar.mi.api.electricity.interfaces.IEleTransfer;
import top.kmar.mi.api.electricity.interfaces.IVoltage;
import top.kmar.mi.api.utils.MISysInfo;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static top.kmar.mi.api.utils.StringUtil.checkNull;

/**
 * 关于电力系统的工作都在这里进行
 * @author EmptyDreams
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
		OUTPUTERS.add(checkNull(outputer, "outputer"));
	}
	
	/**
	 * 注册一个Inputer
	 * @throws NullPointerException 如果inputer == null
	 */
	public static void registerInputer(IEleInputer inputer) {
		INPUTERS.add(checkNull(inputer, "inputer"));
	}
	
	/**
	 * 注册一个Transfer
	 * @throws NullPointerException 如果transfer == null
	 */
	public static void registerTransfer(IEleTransfer transfer) {
		TRANSFERS.add(checkNull(transfer, "transfer"));
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
	
	/**
	 * 获取指定线缆方块的托管
	 * @param te 指定方块的TE
	 * @return 若不存在则返回null
	 */
	@Nullable
	public static IEleTransfer getTransfer(TileEntity te) {
		for (IEleTransfer transfer : TRANSFERS) {
			if (transfer.contains(te)) return transfer;
		}
		return null;
	}
	
	/**
	 * 获取指定发电机的托管
	 * @param te 指定方块的TE
	 * @return 若不存在则返回null
	 */
	@Nullable
	public static IEleOutputer getOutputer(TileEntity te) {
		for (IEleOutputer outputer : OUTPUTERS) {
			if (outputer.contains(te)) return outputer;
		}
		return null;
	}
	
	/**
	 * 获取指定用电器的托管
	 * @param te 指定方块的TE
	 * @return 若不存在则返回null
	 */
	@Nullable
	public static IEleInputer getInputer(TileEntity te) {
		for (IEleInputer inputer : INPUTERS) {
			if (inputer.contains(te)) return inputer;
		}
		return null;
	}
	
	/**
	 * 让指定方块使用电能，如果该方块没有对应的托管的话只会输出一个错误
	 * @param te 指定方块
	 * @return 是否成功
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static EleEnergy useEleEnergy(TileEntity te) {
		for (IEleInputer inputer : INPUTERS) {
			if (inputer.contains(te)) {
				return useEleEnergy(te, inputer);
			}
		}
		MISysInfo.err("[EleWorker]该方块没有找到可用的托管：" + te);
		return null;
	}
	
	/**
	 * 让指定方块使用电能
	 * @param te 指定方块
	 * @param inputer 支持该方块的托管
	 * @return 是否成功
	 */
	public static EleEnergy useEleEnergy(TileEntity te, IEleInputer inputer) {
		if (inputer.getEnergy(te) <= 0) return new EleEnergy(0, EnumVoltage.NON);
		Map<TileEntity, IEleTransfer> transfers = inputer.getTransferAround(te);
		if (transfers.isEmpty()) return null;
		
		PathInfo realPath = null;
		for (Map.Entry<TileEntity, IEleTransfer> entry : transfers.entrySet()) {
			PathInfo pathInfo = entry.getValue().findPath(entry.getKey(), te, inputer);
			if (pathInfo == null || pathInfo.getOuter() == null) continue;
			if (realPath == null || realPath.getLossEnergy() > pathInfo.getLossEnergy())
				realPath = pathInfo;
		}
		
		if (realPath == null) return null;
		lineTransfer(realPath.getPath(),
				realPath.getMachineEnergy() + realPath.getLossEnergy(), realPath.getVoltage());
		return realPath.invoke();
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
				//noinspection ConstantConditions
				infos.put(transfer, transfer.transfer(
						entity, energy, voltage, infos.getOrDefault(transfer, null)));
			}
		} catch (NullPointerException e) {
			NullPointerException rte = new NullPointerException("线路中有至少一个电缆方块没有托管！");
			rte.initCause(e);
			throw rte;
		}
	}
	
}