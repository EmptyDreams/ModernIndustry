package xyz.emptydreams.mi.capabilities.nonburn;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import xyz.emptydreams.mi.api.register.AutoLoader;

import javax.annotation.Nullable;

/**
 * 表明物品是燃烧后生成的可燃物
 * @author EmptyDreams
 */
@AutoLoader
public class NonBurnCapability {

	@CapabilityInject(INonBurn.class)
	public static Capability<INonBurn> NON_BURN;

	static {
		CapabilityManager.INSTANCE.register(INonBurn.class, new Capability.IStorage<INonBurn>() {
			@Nullable
			@Override
			public NBTBase writeNBT(Capability<INonBurn> capability, INonBurn instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<INonBurn> capability, INonBurn instance, EnumFacing side, NBTBase nbt) {
			}
		}, () -> INonBurn.SRC);
	}

}
