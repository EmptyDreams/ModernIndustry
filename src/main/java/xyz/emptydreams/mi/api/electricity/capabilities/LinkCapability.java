package xyz.emptydreams.mi.api.electricity.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import xyz.emptydreams.mi.register.AutoLoader;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoLoader
public class LinkCapability {
	
	@CapabilityInject(ILink.class)
	public static Capability<ILink> LINK;
	
	static {
		CapabilityManager.INSTANCE.register(ILink.class, new Capability.IStorage<ILink>() {
			@Nullable
			@Override
			public NBTBase writeNBT(Capability<ILink> capability, ILink instance, EnumFacing side) {
				return null;
			}
			
			@Override
			public void readNBT(Capability<ILink> capability, ILink instance, EnumFacing side, NBTBase nbt) {
			}
		}, () -> new ILink() {
			@Override
			public boolean canLink(EnumFacing facing) { return false; }
			@Override
			public boolean link(BlockPos pos) { return false; }
			@Override
			public boolean unLink(BlockPos pos) { return false; }
			@Override
			public boolean isLink(BlockPos pos) { return false; }
			@Nonnull
			@Override
			public Collection<BlockPos> getLinks() { return new HashSet<>(0); }
		});
	}
	
}
