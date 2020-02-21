package minedreams.mi.api.electricity;

import minedreams.mi.api.electricity.info.IEleInfo;
import minedreams.mi.api.electricity.info.LinkInfo;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 电力系统工具类，其中包含了所有可用的静态函数
 * @author EmptyDremas
 * @version V1.0
 */
public final class EleUtils {
	
	/**
	 * 判断一个原版方块是否可以连接
	 */
	public static boolean canLinkMinecraft(Block block) {
		switch (block.getRegistryName().getResourcePath()) {
			case "gravel": case "gold_ore": case "iron_ore": case "lapis_ore":
			case "lapis_block": case "gold_block": case "iron_block": case "diamond_ore":
			case "diamond_block": case "iron_door": case "redstone_ore": case "lit_redstone_ore":
			case "iron_bars": case "redstone_lamp": case "lit_redstone_lamp": case "emerald_ore":
			case "emerald_block": case "anvil": case "redstone_block": case "quartz_ore": case "hopper":
			case "iron_trapdoor": case "sea_lantern": return true;
		}
		return false;
	}
	
	/**
	 * 判断方块是否可以连接电线，
	 * <b>注意：此方法不保证fromPos/nowPos在此时已经在世界存在，所以提供了额外的参数</b>
	 * @param info 附加信息
	 * @param nowIsExist 当前方块是否存在
	 * @param fromIsExist 调用方块是否存在
	 * @param isInsulation 是否绝缘，一般情况下非电力传输设备都为绝缘设备，电力传输设备中只有一部分是绝缘设备
	 */
	public static boolean canLink(LinkInfo info, boolean nowIsExist, boolean fromIsExist) {
		if (info.nowBlock instanceof IEleInfo) {
			return ((IEleInfo) info.nowBlock).canLink(info, nowIsExist, fromIsExist);
		}
		return false;
	}
	
	/**
	 * 获取一个方块消耗的电能
	 * @param world 所在世界
	 * @param pos 方块坐标
	 * @param block 方块种类(可以为null)
	 * @param te 方块TE(可以为null)
	 */
	public static double energy(World world, BlockPos pos, Block block, TileEntity te) {
		if (block == null) block = world.getBlockState(pos).getBlock();
		if (block.getRegistryName().getResourceDomain().equals("minecraft")) {
			switch (block.getRegistryName().getResourcePath()) {
				case "gravel":
				case "quartz_ore":
				case "emerald_ore":
					return 5; case "gold_ore":
				case "lit_redstone_lamp":
				case "diamond_block":
				case "lapis_block":
					return 20;
				case "iron_ore":
				case "redstone_lamp":
					return 15; case "lapis_ore":
				case "sea_lantern":
				case "emerald_block":
				case "redstone_ore":
					return 10;
				case "gold_block":
				case "redstone_block":
				case "anvil":
					return 100;
				case "iron_block":
				case "iron_door":
					return 80; case "diamond_ore": return 9;
				case "lit_redstone_ore": return 11;
				case "iron_bars":
				case "iron_trapdoor":
					return 40;
				case "hopper": return 50;
			}
		} else if (block instanceof IEleInfo) {/*
			Electricity ele;
			if (te == null) ele = (Electricity) world.getTileEntity(pos);
			else ele = (Electricity) te;
			if (ele instanceof ElectricityTransfer) {
				ElectricityTransfer et = (ElectricityTransfer) ele;
				return ElectricityTransfer.EETransfer.calculationLoss(et);
			} else if (ele instanceof ElectricityUser) {
				ElectricityUser user = (ElectricityUser) ele;
				return user.getEnergy();
			} else {
				return 0;
			}*/
		}
		return 0;
	}
	
}
