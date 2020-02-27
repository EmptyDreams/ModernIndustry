package minedreams.mi.api.electricity.cache;

import java.util.Iterator;
import java.util.List;

import minedreams.mi.api.electricity.ElectricityMaker;
import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.api.electricity.ElectricityUser;
import minedreams.mi.api.electricity.info.EnumVoltage;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class MachineInformation implements Comparable<MachineInformation>, Iterable<ElectricityTransfer> {
	
	private ElectricityMaker maker;
	private ElectricityTransfer start, end;
	private List<ElectricityTransfer> path;
	
	public MachineInformation(ElectricityMaker maker, List<ElectricityTransfer> path) {
		this.maker = maker;
		this.path = path;
		start = path.get(0);
		end = path.get(path.size() - 1);
	}
	
	public ElectricityMaker getMaker() { return maker; }
	public ElectricityTransfer getStart() { return start; }
	public ElectricityTransfer getEnd() { return end; }
	public List<ElectricityTransfer> getPath() { return path; }
	public int getLoss(EnumVoltage voltage) { return start.getCache().readInfo(start, end, voltage); }
	
	/**
	 * 判断当前缓存是否可用
	 * @param user 用电器
	 * @return true表示可用
	 */
	public boolean isValid(ElectricityUser user) {
		return user.getEnergyMin() <= maker.getOutputMax();
	}
	
	/**
	 * 判断当前缓存的电压输出是否符合用电器要求
	 */
	public boolean isValidVoltage(ElectricityUser user) {
		return maker.getVoltage_max().getVoltage() >= user.getVoltage().getVoltage() &&
				       maker.getVoltage_min().getVoltage() <= user.getVoltage().getVoltage();
	}
	
	/**
	 * 判断当前缓存是否可用，若对应方块已经被替换成新方块则更新数据，
	 * 若对应方块被删除则返回false
	 */
	public boolean exist() {
		World world = maker.getWorld();
		if (start.isInvalid()) {
			TileEntity te = world.getTileEntity(start.getPos());
			if (te instanceof ElectricityTransfer) {
				start = (ElectricityTransfer) te;
			} else {
				return false;
			}
		}
		if (end.isInvalid()) {
			TileEntity te = world.getTileEntity(end.getPos());
			if (te instanceof ElectricityTransfer) {
				end = (ElectricityTransfer) te;
			} else {
				return false;
			}
		}
		if (start.getCache() != end.getCache()) {
			return false;
		}
		if (maker.isInvalid()) {
			TileEntity te = maker.getWorld().getTileEntity(maker.getPos());
			if (te instanceof ElectricityMaker) {
				maker = (ElectricityMaker) te;
				return true;
			}
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		MachineInformation that = (MachineInformation) o;
		
		if (!maker.equals(that.maker)) return false;
		return path.equals(that.path);
	}
	
	@Override
	public int hashCode() {
		return maker.hashCode();
	}
	
	@Override
	public int compareTo(@NotNull MachineInformation o) {
		return Integer.compare(start.getCache().readInfo(start, end, EnumVoltage.ORDINARY),
				o.start.getCache().readInfo(start, end, EnumVoltage.ORDINARY));
	}
	
	@NotNull
	@Override
	public Iterator<ElectricityTransfer> iterator() {
		return path.iterator();
	}
}
