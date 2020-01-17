package minedreams.mi.blocks.world;

import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

/**
 * @author EmptyDremas
 * @version V1.0
 */
public class WorldCreater {
	
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
		INFO.count = (int) map.getOrDefault("count", OreCreat.COUNT);
		INFO.time = (int) map.getOrDefault("time", OreCreat.TIME);
		INFO.yMin = (int) map.getOrDefault("yMin", OreCreat.YMIN);
		INFO.yRange = (int) map.getOrDefault("yRange", OreCreat.YRANGE);
		INFO.probability = (float) map.getOrDefault("probability", OreCreat.PROBABILITY);
		creater = new WorldGenMinable(block.getDefaultState(), INFO.count);
	}
	
	public WorldCreater(OreCreat info, IBlockState block) {
		INFO.count = info.count();
		INFO.time = info.time();
		INFO.yMin = info.yMin();
		INFO.yRange = info.yRange();
		INFO.probability = info.probability();
		creater = new WorldGenMinable(block, info.count());
	}
	
	public boolean generate(World world, Random rand, BlockPos pos) {
		for (int i = 0; i < INFO.time; ++i) {
			int x = pos.getX() + rand.nextInt(16);
			int y = pos.getY() + INFO.yMin;
				y = y + rand.nextInt(INFO.yRange - y);
			int z = pos.getZ() + rand.nextInt(16);
			//float r = rand.nextFloat();
			//if (r <= INFO.probability) {
				creater.generate(world, rand, new BlockPos(x, y, z));
			//}
		}
		return true;
	}
	
}
