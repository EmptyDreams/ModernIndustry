package xyz.emptydreams.mi.register.block;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.register.AutoRegister;

import javax.annotation.Nonnull;
import java.util.Map;
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
	
	public WorldCreater(ASMData asm, Block block) {
		Map<String, Object> map = asm.getAnnotationInfo();
		INFO.count = (int) map.getOrDefault("count", OreCreate.COUNT);
		INFO.time = (int) map.getOrDefault("time", OreCreate.TIME);
		INFO.yMin = (int) map.getOrDefault("yMin", OreCreate.Y_MIN);
		INFO.yRange = (int) map.getOrDefault("yRange", OreCreate.Y_RANGE);
		INFO.probability = (float) map.getOrDefault("probability", OreCreate.PROBABILITY);
		creater = new WorldGenMinable(block.getDefaultState(), INFO.count);
	}
	
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
			for (WorldCreater generator : AutoRegister.Blocks.worldCreate.values()) {
				generator.generate(event.getWorld(), event.getRand(), event.getPos());
			}
		}
	}
	
}
