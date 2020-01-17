package minedreams.mi.api.electricity.info;

import java.util.ArrayList;
import java.util.List;

import minedreams.mi.api.electricity.ElectricityMaker;
import minedreams.mi.api.electricity.ElectricityTransfer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 存储一条电缆线路的信息，该类支持离线存储部分数据，
 * 在玩家切断电线时该类可能被销毁。<br>
 * 此类在预定方案中只供{@link minedreams.mi.api.electricity.ElectricityTransfer}使用，
 * 用户也无法获取该类对象，所以类中没有做数据封装。
 * @author EmptyDreams
 * @version V1.0
 */
public class WireLinkInfo {

	/** 是否需要写入本地，一条线路只写入一次 */
	public volatile boolean needWrite = true;
	
	/** 存储所有线缆 */
	public List<ElectricityTransfer> transfers = new ArrayList<>();
	/** 存储所有发电机 */
	public List<ElectricityMaker> makers = new ArrayList<>();
	
	private List<BlockPos> transfersPos;
	private List<BlockPos> makersPos;
	
	/**
	 * 更新数据到信息库
	 * @param world 所在世界
	 */
	public void update(World world) {
		if (world.isRemote) return;
		if (transfersPos != null) {
			transfers = new ArrayList<>(transfersPos.size());
			transfersPos.forEach(pos -> {
				ElectricityTransfer et = (ElectricityTransfer) world.getTileEntity(pos);
				transfers.add(et);
				et.setInfos(this);
			});
			transfersPos = null;
		}
		if (makersPos != null) {
			makers = new ArrayList<>(makersPos.size());
			makersPos.forEach(pos -> makers.add((ElectricityMaker) world.getTileEntity(pos)));
			makersPos = null;
		}
	}
	
	/** 从{@link NBTTagCompound}中读取数据 */
	public void read(NBTTagCompound compound) {
		if (compound.getBoolean("infos_transfers")) {
			int size = compound.getInteger("infos_transfer_size");
			transfersPos = new ArrayList<>(size);
			int[] pos;
			for (int i = 0; i < size; ++i) {
				pos = compound.getIntArray("infos_transfer_" + i);
				transfersPos.add(new BlockPos(pos[0], pos[1], pos[2]));
			}
		}
		if (compound.getBoolean("infos_makers")) {
			int size = compound.getInteger("infos_maker_size");
			makersPos = new ArrayList<>(size);
			int[] pos;
			for (int i = 0; i < size; ++i) {
				pos = compound.getIntArray("infos_maker_" + i);
				makersPos.add(new BlockPos(pos[0], pos[1], pos[2]));
			}
		}
	}
	
	/** 写入数据到{@link NBTTagCompound}中 */
	public NBTTagCompound write(NBTTagCompound compound) {
		compound.setBoolean("infos_transfers", transfers != null);
		if (transfers != null) {
			compound.setInteger("infos_transfer_size", transfers.size());
			ElectricityTransfer transfer;
			BlockPos pos;
			for (int i = 0, transfersSize = transfers.size(); i < transfersSize; ++i) {
				transfer = transfers.get(i);
				pos = transfer.getPos();
				compound.setIntArray("infos_transfer_" + i, new int[] { pos.getX(), pos.getY(), pos.getZ() });
			}
		}
		compound.setBoolean("infos_makers", makers != null);
		if (makers != null) {
			compound.setInteger("infos_maker_size", makers.size());
			BlockPos pos;
			ElectricityMaker maker;
			for (int i = 0, makersSize = makers.size(); i < makersSize; ++i) {
				maker = makers.get(i);
				pos = maker.getPos();
				compound.setIntArray("infos_maker_" + i, new int[] { pos.getX(), pos.getY(), pos.getZ() });
			}
		}
		return compound;
	}
	
}
