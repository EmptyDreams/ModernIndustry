package xyz.emptydreams.mi.api.fluid.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.util.EnumFacing;

import java.util.Iterator;
import java.util.Map;

/**
 * 流体分路数据
 * @author EmptyDreams
 */
public class FluidShuntData implements Iterable<Map.Entry<EnumFacing, FluidData>> {

	private final Map<EnumFacing, FluidData> dataMap;
	
	/**
	 * 将指定数据
	 * @param source
	 * @param values
	 */
	public FluidShuntData(FluidData source, EnumFacing... values) {
		dataMap = new Object2ObjectArrayMap<>(values.length);
		int amount = source.getAmount() / values.length;
		for (EnumFacing value : values) dataMap.put(value, source.copy(amount));
		int mod = source.getAmount() % values.length;
		dataMap.get(values[0]).plusAmount(mod);
	}
	
	@Override
	public Iterator<Map.Entry<EnumFacing, FluidData>> iterator() {
		return dataMap.entrySet().iterator();
	}
}