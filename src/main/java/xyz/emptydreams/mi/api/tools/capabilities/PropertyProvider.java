package xyz.emptydreams.mi.api.tools.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import xyz.emptydreams.mi.api.net.WaitList;
import xyz.emptydreams.mi.api.tools.property.IProperty;
import xyz.emptydreams.mi.api.tools.property.PropertyManager;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class PropertyProvider implements ICapabilityProvider {
	
	private PropertyManager property;
	
	public PropertyProvider() { this(null); }
	
	public PropertyProvider(PropertyManager manager) { property = manager; }
	
	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == PropertyCapability.PROPERTY && property != null;
	}
	
	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		//noinspection unchecked
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
}
