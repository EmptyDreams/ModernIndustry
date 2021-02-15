package xyz.emptydreams.mi.api.gui.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author EmptyDreams
 */
public final class ChildFrame {
	
	/** 存储玩家打开的GUI */
	private static final Map<EntityPlayer, Node> INSTANCE = new WeakHashMap<>();
	
	/** 设置打开的GUI的属性 */
	public static void setGui(World world, BlockPos pos, EntityPlayer player) {
		INSTANCE.put(player, new Node(world, pos));
	}
	
	/** 获取打开的GUI对应的TE */
	@Nullable
	public static TileEntity getGuiTileEntity(EntityPlayer player) {
		Node node = INSTANCE.getOrDefault(player, null);
		if (node == null) return null;
		return node.getTileEntity();
	}
	
	private static final class Node {
		
		private final World world;
		private final BlockPos pos;
		
		Node(World world, BlockPos pos) {
			this.world = world;
			this.pos = pos;
		}
		
		public World getWorld() { return world; }
		public BlockPos getPos() { return pos; }
		
		public TileEntity getTileEntity() {
			return world.getTileEntity(pos);
		}
		
		@Override
		public String toString() {
			return "Node{" +
					"world=" + world.getProviderName() +
					", pos=" + pos +
					'}';
		}
		
	}
	
}