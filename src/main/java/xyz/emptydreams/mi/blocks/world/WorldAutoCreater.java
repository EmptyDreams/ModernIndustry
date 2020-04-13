package xyz.emptydreams.mi.blocks.world;

import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.register.AutoRegister;

/**
 * @author EmptyDremas
 * @version V1.0
 */
public final class WorldAutoCreater {
	
	static {
		MinecraftForge.ORE_GEN_BUS.register(WorldAutoCreater.class);
	}
	
	@SubscribeEvent
	public static void onOreGenPost(OreGenEvent.Post event) {
		if (!event.getWorld().isRemote) {
			for (WorldGenerator generator : AutoRegister.Blocks.worldCreater.values()) {
				generator.generate(event.getWorld(), event.getRand(), event.getPos());
			}
		}
	}
	
}