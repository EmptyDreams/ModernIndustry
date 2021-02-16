package xyz.emptydreams.mi.api.gui.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author EmptyDreams
 */
public final class ChildFrame {
	
	/** 存储玩家打开的GUI */
	private static final Map<EntityPlayer, BlockPos> INSTANCE = new WeakHashMap<>();
	
	/** 设置打开的GUI的属性 */
	public static void setGui(BlockPos pos, EntityPlayer player) {
		INSTANCE.put(player, pos);
	}
	
	/** 获取打开的GUI对应的TE */
	@Nullable
	public static TileEntity getGuiTileEntity(EntityPlayer player) {
		BlockPos pos = INSTANCE.getOrDefault(player, null);
		if (pos == null) return null;
		return player.world.getTileEntity(pos);
	}
}