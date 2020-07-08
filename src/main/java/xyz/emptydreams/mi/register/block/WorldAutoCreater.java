package xyz.emptydreams.mi.register.block;

import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.register.AutoLoader;
import xyz.emptydreams.mi.register.AutoRegister;

/**
 * @author EmptyDremas
 * @version V1.0
 */
@AutoLoader
public final class WorldAutoCreater {
	
	static {
		MinecraftForge.ORE_GEN_BUS.register(WorldAutoCreater.class);
	}
	
	@SubscribeEvent
	public static void onOreGenPost(OreGenEvent.Post event) {
		if (!event.getWorld().isRemote) {
			for (WorldGenerator generator : AutoRegister.Blocks.worldCreate.values()) {
				generator.generate(event.getWorld(), event.getRand(), event.getPos());
			}
		}
	}
	
}