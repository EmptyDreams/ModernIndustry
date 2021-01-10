package xyz.emptydreams.mi.items.common;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.register.item.AutoItemRegister;
import xyz.emptydreams.mi.api.utils.BlockUtil;
import xyz.emptydreams.mi.api.utils.WorldUtil;
import xyz.emptydreams.mi.blocks.base.MachineBlock;
import xyz.emptydreams.mi.blocks.machine.user.MuffleFurnaceBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.lwjgl.opengl.GL11.*;

/**
 * 扳手，用于拆卸机器
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
@AutoItemRegister(value = "spanner", field = "ITEM")
public class SpannerItem extends Item {

	//该字段会通过反射赋值
	@SuppressWarnings("unused")
	private static SpannerItem ITEM;

	@Nullable
	public static SpannerItem getInstance() { return ITEM; }
	
	public SpannerItem() {
		setMaxStackSize(64);
		setMaxDamage(256);
		setFull3D();
		setCreativeTab(ModernIndustry.TAB_TOOL);
	}

	@Override
	public boolean canItemEditBlocks() {
		return true;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos,
	                                  EnumHand hand, EnumFacing facing,
	                                  float hitX, float hitY, float hitZ) {
		if (!player.capabilities.allowEdit) return EnumActionResult.FAIL;

		IBlockState state = worldIn.getBlockState(pos);
		if (player.isSneaking()) {
			if (!isSupportRemove(worldIn, pos, state)) return EnumActionResult.FAIL;
			if (worldIn.isRemote) return EnumActionResult.SUCCESS;
			worldIn.setBlockToAir(pos);
			worldIn.markBlockRangeForRenderUpdate(pos, pos);
			Block.spawnAsEntity(worldIn, pos, new ItemStack(state.getBlock()));
			return EnumActionResult.SUCCESS;
		}

		EnumFacing decide = decideFacing(facing, hitX, hitY, hitZ);
		PropertyDirection property = getPropertyDirection(worldIn, pos, state);
		if (property == null || !property.getAllowedValues().contains(decide)) return EnumActionResult.PASS;
		WorldUtil.setBlockState(worldIn, pos, state, state.withProperty(property, decide));

		return EnumActionResult.SUCCESS;
	}

	/** 获取{@link PropertyDirection}的对象 */
	public static PropertyDirection getPropertyDirection(World world, BlockPos pos) {
		return getPropertyDirection(world, pos, world.getBlockState(pos));
	}

	/**
	 * 获取{@link PropertyDirection}的对象
	 * @param state 方块state
	 * @return 若state中不包含方向则返回null
	 */
	@Nullable
	public static PropertyDirection getPropertyDirection(World world, BlockPos pos, IBlockState state) {
		if (!BlockUtil.isFullBlock(world, pos)) return null;
		for (IProperty<?> property : state.getProperties().keySet()) {
			if (property instanceof PropertyDirection) return (PropertyDirection) property;
		}
		return null;
	}

	/**
	 * 根据信息计算旋转后的朝向
	 * @param facing 右键的方向
	 * @param x 右键的部位在方块中的坐标
	 * @param y 右键的部位在方块中的坐标
	 * @param z 右键的部位在方块中的坐标
	 * @return 计算后的朝向
	 */
	@Nonnull
	public static EnumFacing decideFacing(EnumFacing facing, double x, double y, double z) {
		switch (facing) {
			case NORTH: case SOUTH:
				if ((x < 0.25 || x > 0.75) && (y < 0.25 || y > 0.75)) {
					return facing.getOpposite();
				} else if (x >= 0.25 && x <= 0.75 && y >= 0.25 && y <= 0.75) {
					return facing;
				} else if (x <= 0.25) {
					return EnumFacing.WEST;
				} else if (x >= 0.75) {
					return EnumFacing.EAST;
				} else {
					return y <= 0.25 ? EnumFacing.DOWN : EnumFacing.UP;
				}
			case DOWN: case UP:
				if (x >= 0.25 && x <= 0.75 && z >= 0.25 && z <= 0.75) {
					return facing;
				} else if ((x < 0.25 || x > 0.75) && (z < 0.25 || z > 0.75)) {
					return facing.getOpposite();
				} else if (z < 0.25) {
					return EnumFacing.NORTH;
				} else if (z > 0.75) {
					return EnumFacing.SOUTH;
				} else return x < 0.25 ? EnumFacing.WEST : EnumFacing.EAST;
			default:
				if ((z < 0.25 || z > 0.75) && (y < 0.25 || y > 0.75)) {
					return facing.getOpposite();
				} else if (z >= 0.25 && z <= 0.75 && y >= 0.25 && y <= 0.75) {
					return facing;
				} else if (z <= 0.25) {
					return EnumFacing.NORTH;
				} else if (z >= 0.75) {
					return EnumFacing.SOUTH;
				} else {
					return y <= 0.25 ? EnumFacing.DOWN : EnumFacing.UP;
				}
		}
	}

	@SubscribeEvent
	public static void stopGUI(PlayerInteractEvent.RightClickBlock event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		if ((player.getHeldItemMainhand().getItem() == getInstance() ||
				player.getHeldItemOffhand().getItem() == getInstance()) &&
				getPropertyDirection(world, pos) != null)
			event.setUseBlock(Event.Result.DENY);
	}

	/** 当玩家手持扳手指向可旋转方块时绘制线条 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void whenPlayerSelectBlock(RenderWorldLastEvent event) {
		RayTraceResult mouseOver = Minecraft.getMinecraft().objectMouseOver;
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (!check(mouseOver, player)) return;
		BlockPos pos = mouseOver.getBlockPos();

		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		glLineWidth(2.0F);
		glBegin(GL_LINES);
		GlStateManager.color(1.0F, 0.85F,0.05F);

		double partialTicks = event.getPartialTicks();
		double x = pos.getX() - (player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks);
		double y = pos.getY() - (player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks);
		double z = pos.getZ() - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks);
		drawLine(mouseOver.sideHit, x, y, z);

		glEnd();
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
	}

	/** 判断是否可以进行绘制 */
	@SideOnly(Side.CLIENT)
	private static boolean check(RayTraceResult mouseOver, EntityPlayer player) {
		if (mouseOver == null) return false;
		BlockPos pos = mouseOver.getBlockPos();
		if (player.getHeldItemMainhand().getItem() != getInstance()) return false;
		return getPropertyDirection(Minecraft.getMinecraft().world, pos) != null;
	}

	/** 绘制线条 */
	@SideOnly(Side.CLIENT)
	private static void drawLine(EnumFacing facing, double x, double y, double z) {
		switch (facing) {
			case UP: y += 1.004;
			case DOWN:
				glVertex3d(x + 0.25, y,    z);
				glVertex3d(x + 0.25, y, z + 1);
				glVertex3d(x + 0.75, y,    z);
				glVertex3d(x + 0.75, y, z + 1);
				glVertex3d(x,           y, z + 0.25);
				glVertex3d(x + 1,    y, z + 0.25);
				glVertex3d(x,           y, z + 0.75);
				glVertex3d(x + 1,    y, z + 0.75);
				break;
			case EAST: x += 1.004;
			case WEST:
				glVertex3d(x, y + 0.25,    z);
				glVertex3d(x, y + 0.25, z + 1);
				glVertex3d(x, y + 0.75,    z);
				glVertex3d(x, y + 0.75, z + 1);
				glVertex3d(x,    y,        z + 0.25);
				glVertex3d(x, y + 1,    z + 0.25);
				glVertex3d(x,    y,        z + 0.75);
				glVertex3d(x, y + 1,    z + 0.75);
				break;
			case SOUTH: z += 1.004;
			case NORTH:
				glVertex3d(   x,        y + 0.25, z);
				glVertex3d(x + 1,    y + 0.25, z);
				glVertex3d(   x,        y + 0.75, z);
				glVertex3d(x + 1,    y + 0.75, z);
				glVertex3d(x + 0.25,    y,        z);
				glVertex3d(x + 0.25, y + 1,    z);
				glVertex3d(x + 0.75,    y,        z);
				glVertex3d(x + 0.75, y + 1,    z);
				break;
		}
	}

	/** 判断扳手是否支持拆除该方块 */
	public static boolean isSupportRemove(World world, BlockPos pos, IBlockState state) {
		return state.getBlock() instanceof MachineBlock ||
				state.getBlock() instanceof MuffleFurnaceBlock ||
				getPropertyDirection(world, pos, state) != null;
	}
	
}