package xyz.emptydreams.mi.api.gui.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.emptydreams.mi.api.event.PlayerOpenGuiEvent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * 存储子GUI打开时需要的信息
 * @author EmptyDreams
 */
@Mod.EventBusSubscriber
public final class ChildFrame {
	
	/** 存储玩家打开的GUI */
	private static final Map<UUID, BlockPos> INSTANCE = new WeakHashMap<>();
	
	/** 设置打开的GUI的属性 */
	public static void setGui(BlockPos pos, EntityPlayer player) {
		INSTANCE.put(player.getUniqueID(), pos);
	}
	
	/** 获取打开的GUI对应的TE */
	@Nullable
	public static TileEntity getGuiTileEntity(EntityPlayer player) {
		BlockPos pos = INSTANCE.getOrDefault(player.getUniqueID(), null);
		if (pos == null) return null;
		return player.world.getTileEntity(pos);
	}
	
	@SubscribeEvent
	public static void onPlayerOpenGui(PlayerOpenGuiEvent event) {
		setGui(event.getPos(), event.getPlayer());
	}
	
}