package xyz.emptydreams.mi.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * 玩家打开GUI时触发的事件
 * @author EmptyDreams
 */
public class PlayerOpenGuiEvent extends Event {
	
	private final ModContainer mc;
	private final EntityPlayer player;
	private final int modGuiId;
	private final World world;
	private final int x;
	private final int y;
	private final int z;
	
	public PlayerOpenGuiEvent(ModContainer mc,
	                          EntityPlayer player, int modGuiId,
	                          World world, int x, int y, int z) {
		this.mc = mc;
		this.player = player;
		this.modGuiId = modGuiId;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public ModContainer getModContainer() { return mc; }
	public EntityPlayer getPlayer() { return player; }
	public int getModGuiId() { return modGuiId; }
	public World getWorld() { return world; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getZ() { return z; }
	public BlockPos getPos() { return new BlockPos(x, y , z); }
	
}