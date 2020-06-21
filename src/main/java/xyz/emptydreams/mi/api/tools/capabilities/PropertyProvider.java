package xyz.emptydreams.mi.api.tools.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.api.tools.property.IProperty;
import xyz.emptydreams.mi.api.tools.property.PropertyManager;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class PropertyProvider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {
	
	private PropertyManager property;
	
	public PropertyProvider() { this(null); }
	
	public PropertyProvider(PropertyManager manager) { property = manager; }
	
	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == PropertyCapability.PROPERTY;
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return (T) property;
	}
	
	/** @see PropertyManager#addProperty(IProperty)  */
	public void addProperty(IProperty property) {
		WaitList.checkNull(this.property, "this.property");
		this.property.addProperty(property);
	}
	
	/** @see PropertyManager#getProperty(String)  */
	public IProperty getProperty(String name) {
		return property == null ? null : property.getProperty(name);
	}
	
	/** @see PropertyManager#hasProperty(String)  */
	public boolean hasProperty(String name) {
		return property != null && property.hasProperty(name);
	}
	
	public void setProperty(PropertyManager property) {
		this.property = property;
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound data = new NBTTagCompound();
		if (property != null) {
			int k = 0;
			for (IProperty pro : property) {
				NBTTagCompound tag = new NBTTagCompound();
				pro.write(tag);
				tag.setString("class", pro.getClass().getName());
				data.setTag("property:" + k++, tag);
			}
			data.setInteger("size", k);
		}
		return data;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		int size = nbt.getInteger("size");
		try {
			for (int i = 0; i < size; ++i) {
				NBTTagCompound tag = nbt.getCompoundTag("property:" + i);
				IProperty pro = (IProperty) Class.forName(tag.getString("class")).newInstance();
				pro.read(tag);
				addProperty(pro);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
