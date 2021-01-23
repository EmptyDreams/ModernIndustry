package xyz.emptydreams.mi.api.tools.property;

import com.google.common.base.Throwables;
import net.minecraft.nbt.NBTTagCompound;
import xyz.emptydreams.mi.api.exception.IntransitException;
import xyz.emptydreams.mi.api.utils.StringUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author EmptyDreams
 */
public class PropertyManager implements Iterable<IProperty> {
	
	private final List<IProperty> PROS = new ArrayList<>();
	
	/**
	 * 添加一个属性
	 * @param pro 要添加的属性，不能为null
	 */
	public void addProperty(IProperty pro) {
		PROS.add(StringUtil.checkNull(pro, "pro"));
	}
	
	/**
	 * 获取指定属性
	 * @param name 属性名称
	 * @return 返回null表示不包含该属性
	 */
	@Nullable
	public IProperty getProperty(String name) {
		for (IProperty property : PROS) {
			if (property.equalsName(name)) return property;
		}
		return null;
	}
	
	/**
	 * 判断是否包含指定属性
	 * @param name 属性名称
	 */
	public boolean hasProperty(String name) {
		return getProperty(name) != null;
	}
	
	public void write(NBTTagCompound compound) {
		IProperty property;
		compound.setInteger("manager:size", PROS.size());
		for (int i = 0; i < PROS.size(); i++) {
			property = PROS.get(i);
			NBTTagCompound tag = new NBTTagCompound();
			property.write(tag);
			tag.setString("manager:class", property.getClass().getName());
			compound.setTag("manager:" + i, tag);
		}
	}
	
	public void read(NBTTagCompound compound) {
		int size = compound.getInteger("manager:size");
		Class<?> propertyClass;
		IProperty property;
		NBTTagCompound tag;
		try {
			for (int i = 0; i < size; ++i) {
				tag = compound.getCompoundTag("manager:" + i);
				propertyClass = Class.forName(tag.getString("manager:class"));
				property = (IProperty) propertyClass.newInstance();
				property.read(tag);
				PROS.add(property);
			}
		} catch (Exception e) {
			Throwables.throwIfUnchecked(e);
			throw new IntransitException(e);
		}
	}
	
	@Override
	public Iterator<IProperty> iterator() {
		return PROS.iterator();
	}
	
}