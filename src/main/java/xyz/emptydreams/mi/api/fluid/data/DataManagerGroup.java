package xyz.emptydreams.mi.api.fluid.data;

import net.minecraft.util.EnumFacing;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

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
	
	/** 遍历水平方向上的管理类 */
	public void foreachHorizontal(BiConsumer<? super EnumFacing, ? super DataManager> consumer) {
		managers.forEach((facing, manager) -> {
			if (facing.getAxis().isHorizontal()) consumer.accept(facing, manager);
		});
	}
	
	@Override
	public Iterator<Map.Entry<EnumFacing, DataManager>> iterator() {
		return managers.entrySet().iterator();
	}
}