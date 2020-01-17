package minedreams.mi.blocks.te;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import minedreams.mi.api.electricity.EleUtils;
import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.api.electricity.info.LinkInfo;
import minedreams.mi.api.net.info.InfoBooleans;
import minedreams.mi.api.net.message.MessageList;
import minedreams.mi.tools.Tools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import static minedreams.mi.blocks.wire.Wire.*;

/**
 * 普通电线的TE
 * @author EmptyDreams
 * @version V1.1
 */
@AutoTileEntity("wire")
public class TileEntityWire extends ElectricityTransfer {
	
	/**
	 * 存储已经更新过的玩家列表，因为作者认为单机时长会更多，所以选择1作为默认值。<br>
	 * 	不同方块不共用此列表且此列表不会离线存储，当玩家离开方块过远或退出游戏等操作导致
	 * 		方块暂时“删除”后此列表将重置以保证所有玩家可以正常渲染电线方块
	 */
	public final List<String> players = new ArrayList<>(1);
	
	private boolean donnotSend() {
		return players.size() == world.playerEntities.size();
	}
	
	@Override
	public MessageList send(boolean isClient) {
		super.send(isClient);
		if (isClient || donnotSend()) return null;
		
		//新建消息
		MessageList ml = new MessageList();
		{
			/* 存储电线的连接方向 */
			InfoBooleans bools = new InfoBooleans();
			
			bools.add(getUp());
			bools.add(getDown());
			bools.add(getEast());
			bools.add(getWest());
			bools.add(getSouth());
			bools.add(getNorth());
			ml.writeInfo("bools", bools);
		}
		
		//遍历所有玩家
		for (EntityPlayer player : world.playerEntities) {
			//如果玩家已经更新过则跳过
			if (!EntitySelectors.NOT_SPECTATING.apply(player) || (players.contains(player.getName())))
				continue;
			
			//判断玩家是否在范围之内（判断方法借用World中的代码）
			double d = player.getDistance(pos.getX(), pos.getY(), pos.getZ());
			if (d < 4096) {
				if (player instanceof EntityPlayerMP) {
					players.add(player.getName());
					ml.addPlayer((EntityPlayerMP) player);
				}
			}
		}
		return ml;
	}
	
	@Override
	public void reveive(@Nonnull MessageList list) {
		InfoBooleans info = (InfoBooleans) list.readInfo("bools");
		List<Boolean> bools = info.getInfos();
		up = bools.get(0);
		down = bools.get(1);
		east = bools.get(2);
		west = bools.get(3);
		south = bools.get(4);
		north = bools.get(5);
		world.markBlockRangeForRenderUpdate(pos, pos);
	}
	
	@Override
	public boolean run() {
		return true;
	}
	
	@Override
	public void updateLink() {
		super.updateLink();
		players.clear();
	}
	
	@Override
	public boolean canLink(TileEntity ele) {
		if (ele == null) return false;
		boolean exET = ele instanceof ElectricityTransfer;
		if (!exET) {
			if (!(EleUtils.canLink(new LinkInfo(world, getPos(), ele.getPos(),
					getBlockType(), ele.getBlockType()),
					true, false, isInsulation()))) return false;
			EnumFacing facing = Tools.whatFacing(getPos(), ele.getPos());
			if (linkBlock.containsKey(facing)) {
				return linkBlock.get(facing) == null;
			}
			return true;
		}
		if (ele.equals(next) || ele.equals(prev)) return true;
		return next == null || prev == null;
	}
	
	@Override
	public ElectricityTransfer next(ElectricityTransfer ele) {
		if (next != null && next.equals(ele)) return prev;
		if (prev != null && prev.equals(ele)) return next;
		return null;
	}
	
	@Override
	public boolean linkForce(TileEntity ele) {
		if (ele == null) return false;
		boolean exET = ele instanceof ElectricityTransfer;
		if (exET) {
			ElectricityTransfer et = (ElectricityTransfer) ele;
			if (ele.equals(next) || ele.equals(prev)) return true;
			if (next == null) {
				next = et;
				super.linkForce(next);
			} else if (prev == null) {
				prev = et;
				super.linkForce(prev);
			} else {
				return false;
			}
		} else {
			if (!(EleUtils.canLink(new LinkInfo(world, getPos(), ele.getPos(),
					getBlockType(), ele.getBlockType()),
					true, false, isInsulation()))) return false;
			EnumFacing facing = Tools.whatFacing(getPos(), ele.getPos());
			if (linkBlock.containsKey(facing)) {
				if (linkBlock.get(facing) != null) return false;
			}
			linkBlock.put(facing, ele);
			switch (facing) {
				case DOWN: down = true; break;
				case UP: up = true; break;
				case SOUTH: south = true; break;
				case NORTH: north = true; break;
				case WEST: west = true; break;
				default: east = true; break;
			}
			updateLinkInfo(ele);
		}
		return true;
	}
	
	/**
	 * 连接一个电线
	 * @param blockPos 需要连接的电线所在方块
	 * @return 是否连接成功
	 */
	public boolean linkForce(BlockPos blockPos) {
		return linkForce(blockPos == null ? null : (TileEntityWire) world.getTileEntity(blockPos));
	}
	
	/**
	 * 通过IBlockState更新内部数据
	 */
	@Override
	public void update(@Nonnull IBlockState state) {
		up = state.getValue(UP);
		down = state.getValue(DOWN);
		east = state.getValue(EAST);
		west = state.getValue(WEST);
		south = state.getValue(SOUTH);
		north = state.getValue(NORTH);
		markDirty();
		if (!world.isRemote) players.clear();
	}
	
	@Override
	public IBlockState updateState() {
		return getBlockType().getDefaultState()
				       .withProperty(UP, up).withProperty(DOWN, down)
				       .withProperty(EAST, east).withProperty(WEST, west)
				       .withProperty(SOUTH, south).withProperty(NORTH, north);
	}
	
}
