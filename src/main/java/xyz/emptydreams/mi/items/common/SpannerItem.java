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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.electricity.src.block.MachineBlock;
import xyz.emptydreams.mi.api.utils.timetask.TaskTable;
import xyz.emptydreams.mi.api.utils.timetask.TimeTask;
import xyz.emptydreams.mi.blocks.base.MIProperty;
import xyz.emptydreams.mi.register.item.AutoItemRegister;

/**
 * 扳手，用于拆卸机器
 * @author EmptyDreams
 * @version V1.0
 */
@AutoItemRegister(value = "spanner", object = "ITEM")
public class SpannerItem extends Item {
	
	@SuppressWarnings("unused")
	private static SpannerItem ITEM;
	
	public static SpannerItem getInstance() { return ITEM; }
	
	private final Map<EntityPlayer, Data> useHis = new HashMap<>(1);
	@SideOnly(Side.CLIENT)
	private final Map<EntityPlayer, Data> clientHis = new HashMap<>(1);
	
	public SpannerItem() {
		setMaxStackSize(64);
		setMaxDamage(256);
		setCreativeTab(ModernIndustry.TAB_TOOL);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                  EnumHand hand, EnumFacing facing,
	                                  float hitX, float hitY, float hitZ) {
		IBlockState state = worldIn.getBlockState(pos);
		Block block = state.getBlock();
		Data data;
		if (worldIn.isRemote) {
			data = clientHis.getOrDefault(player, null);
		} else {
			data = useHis.getOrDefault(player, null);
		}
		
		if (data == null) {
			data = new Data();
			if (worldIn.isRemote) {
				clientHis.put(player, data);
				TaskTable.registerClientTask(data);
			} else {
				this.useHis.put(player, data);
				TaskTable.registerTask(data);
			}
		}
		if (block instanceof MachineBlock) {
			if (!pos.equals(data.pos)) {
				data.pos = pos;
				data.amount = 0;
			}
			data.cleanTime();
			if (++data.amount > 3) {
				data.pos = null;
				data.amount = 0;
				block.onBlockHarvested(worldIn, pos, state, player);
				if (!player.isCreative()) Block.spawnAsEntity(worldIn, pos, new ItemStack(block));
				worldIn.setBlockToAir(pos);
			} else {
				Comparable<?> comparable = state.getProperties().get(MIProperty.FACING);
				if (comparable != null) {
					EnumFacing fac = MIProperty.FACING.getValueClass().cast(comparable);
					switch (fac) {
						case EAST: fac = EnumFacing.SOUTH; break;
						case SOUTH: fac = EnumFacing.WEST; break;
						case WEST: fac = EnumFacing.NORTH; break;
						case NORTH: fac = EnumFacing.EAST; break;
						default: break;
					}
					worldIn.setBlockState(pos, state.withProperty(MIProperty.FACING, fac));
					worldIn.markBlockRangeForRenderUpdate(pos, pos);
				}
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
			super(50);
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
