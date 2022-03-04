package top.kmar.mi.content.capabilities.nonburn;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author EmptyDreams
 */
public class NonBurnProvider implements ICapabilityProvider {

	public static final NonBurnProvider SRC = new NonBurnProvider();

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == NonBurnCapability.NON_BURN;
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		return null;
	}
}