package xyz.emptydreams.mi.items.common;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.src.block.MachineBlock;
import xyz.emptydreams.mi.api.utils.timetask.TaskTable;
import xyz.emptydreams.mi.api.utils.timetask.TimeTask;
import xyz.emptydreams.mi.register.item.AutoItemRegister;

/**
 * 扳手，用于拆卸机器
 * @author EmptyDreams
 * @version V1.0
 */
@AutoItemRegister("spanner")
public class SpannerItem extends Item {
	
	private final Map<EntityPlayer, Data> useHis = new HashMap<>(1);
	
	public SpannerItem() {
		setMaxStackSize(64);
		setMaxDamage(256);
		setCreativeTab(ModernIndustry.TAB_TOOL);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                  EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		IBlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
		Data data = useHis.getOrDefault(player, null);
		if (data == null) {
			data = new Data();
			useHis.put(player, data);
			if (worldIn.isRemote) {
				TaskTable.registerClientTask(data);
			} else {
				TaskTable.registerTask(data);
			}
		}
		if (block instanceof MachineBlock) {
			if (pos.equals(data.pos)) {
				data.cleanTime();
				++data.amount;
				if (data.amount > 3) {
					data.pos = null;
					block.onBlockHarvested(worldIn, pos, state, player);
					Block.spawnAsEntity(worldIn, pos, new ItemStack(block));
					worldIn.setBlockToAir(pos);
				}
			} else {
				data.cleanTime();
				data.pos = pos;
				data.amount = 1;
			}
			return EnumActionResult.SUCCESS;
		} else {
			data.pos = null;
		}
		return EnumActionResult.PASS;
	}
	
	private final static class Data extends TimeTask {
		
		public BlockPos pos;
		public int amount;
		
		public Data() {
			super(30);
		}
		
		@Override
		public boolean accept() {
			pos = null;
			amount = 0;
			return false;
		}
		
		public void cleanTime() {
			setTime(0);
		}
		
	}
	
}
