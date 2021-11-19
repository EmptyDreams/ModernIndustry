package xyz.emptydreams.mi.api.fluid.data;

import net.minecraft.util.EnumFacing;
import xyz.emptydreams.mi.api.fluid.TransportContent;
import xyz.emptydreams.mi.api.fluid.TransportResult;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import static net.minecraft.util.EnumFacing.*;

/**
 * @author EmptyDreams
 */
public class DataManagerGroup implements Iterable<Map.Entry<EnumFacing, DataManager>> {
	
	private final Map<EnumFacing, DataManager> managers = new EnumMap<>(EnumFacing.class);
	
	public void setManager(EnumFacing facing, DataManager manager) {
		managers.put(facing, manager);
	}
	
	public DataManager getManager(EnumFacing facing) {
		return managers.get(facing);
	}
	
	public boolean hasManager(EnumFacing facing) {
		return managers.containsKey(facing);
	}
	
	@Nonnull
	public TransportResult extract(int amount, EnumFacing facing, boolean simulate) {
		TransportResult result = new TransportResult();
		if (!hasManager(facing)) return result;    //判断输出方向上能否操作
		DataManager fromManager = getManager(facing);
		FluidData fromExtract = fromManager.extract(amount, true, true);
		if (facing != UP && hasManager(UP)) {
		
		}
		return result;
	}
	
	/**
	 * <p>从指定方向的管理器中插入数据
	 * <p>该方法只适用于
	 * @param data 要插入的流体数据
	 * @param facing 方向
	 * @param simulate 是否为模拟
	 * @return 运算结果，存储各个方向被挤出的数据以及输入总量
	 */
	@Nonnull
	public TransportResult insert(FluidData data, EnumFacing facing, boolean simulate) {
		TransportResult result = new TransportResult();
		if (!hasManager(facing)) return result;    //判断输入方向能否进行操作
		DataManager fromManager = getManager(facing);
		//模拟输入操作获取被输入方向挤出来的流体
		TransportContent fromInsert = fromManager.insert(data, true, true);
		int amount = 0;      //存储运输距离
		//如果管道在下方可以进行操作并且输入方向不为下方则优先对下方进行运算
		if (facing != DOWN && hasManager(DOWN))
			amount += transportData(result, DOWN, getManager(DOWN), fromInsert, simulate);
		if (!fromInsert.isEmpty()) {
			//遍历水平方向
			for (EnumFacing horizontal : HORIZONTALS) {
				if (horizontal == facing || !hasManager(horizontal)) continue;
				DataManager horizontalManager = getManager(horizontal);
				amount += transportData(result, horizontal, horizontalManager, fromInsert, simulate);
				if (fromInsert.isEmpty()) break;
			}
			//如果管道在上方可以进行操作并且输入方向不为上方则对上方进行运算
			if (facing != UP && hasManager(UP)) {
				amount += transportData(result, UP, getManager(UP), fromInsert, simulate);
			}
		}
		result.setRealTransport(amount);
		//如果不是模拟则修改输入方向的数据
		if (!simulate) fromManager.insert(data.copy(amount), true, false);
		return result;
	}
	
	/**
	 * @param result 存储结果的对象
	 * @param facing 运输方向
	 * @param manager 数据管理类对象
	 * @param list 要插入的流体数据
	 * @param simulate 是否为模拟
	 * @return 运输量
	 */
	protected static int transportData(TransportResult result, EnumFacing facing,
	                            DataManager manager, Iterable<FluidData> list, boolean simulate) {
		int sum = 0;
		Iterator<FluidData> iterator = list.iterator();
		while (iterator.hasNext()) {
			FluidData fluidData = iterator.next();
			TransportContent content = manager.insert(fluidData, false, simulate);
			result.add(facing, content);
			fluidData.minusAmount(content.getTransportAmount());
			sum += content.getTransportAmount();
			if (fluidData.isEmpty()) iterator.remove();
		}
		return sum;
	}
	
	@Override
	public Iterator<Map.Entry<EnumFacing, DataManager>> iterator() {
		return managers.entrySet().iterator();
	}
	
}