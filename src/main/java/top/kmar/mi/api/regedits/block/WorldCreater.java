package top.kmar.mi.api.regedits.block;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.kmar.mi.api.regedits.block.annotations.OreCreate;
import top.kmar.mi.api.regedits.machines.BlockRegistryMachine;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * 生成矿物
 * @author EmptyDremas
 */
public class WorldCreater {
	
	static {
		MinecraftForge.ORE_GEN_BUS.register(WorldCreater.class);
	}
	
	private static final class Infos {
		/** 生成规模 */
		int count = 8;
		/** 生成次数 */
		int time = 4;
		/** 最低高度 */
		int yMin = 16;
		/** 高度范围 */
		int yRange = 64;
		/** 生成成功几率 */
		float probability = 0.8F;
	}
	
	private final WorldGenMinable creater;
	private final Infos INFO = new Infos();
	
	public WorldCreater(OreCreate annotation, Block block) {
		INFO.count = annotation.count();
		INFO.time = annotation.time();
		INFO.yMin = annotation.yMin();
		INFO.yRange = annotation.yRange();
		INFO.probability = annotation.probability();
		creater = new WorldGenMinable(block.getDefaultState(), annotation.count());
	}
	
	@SuppressWarnings("UnusedReturnValue")
	public boolean generate(@Nonnull World world, @Nonnull Random rand, @Nonnull BlockPos pos) {
		for (int i = 0; i < INFO.time; ++i) {
			int x = pos.getX() + rand.nextInt(16);
			int y = pos.getY() + INFO.yMin + rand.nextInt(INFO.yRange);
			int z = pos.getZ() + rand.nextInt(16);
			float r = rand.nextFloat();
			if (r <= INFO.probability) {
				creater.generate(world, rand, new BlockPos(x, y, z));
			}
		}
		return true;
	}
	
	@SubscribeEvent
	public static void onOreGenPost(OreGenEvent.Post event) {
		if (!event.getWorld().isRemote) {
			for (WorldCreater generator : BlockRegistryMachine.Blocks.worldCreate.values()) {
				generator.generate(event.getWorld(), event.getRand(), event.getPos());
			}
		}
	}
	
}