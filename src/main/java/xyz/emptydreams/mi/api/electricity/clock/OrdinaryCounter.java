package xyz.emptydreams.mi.api.electricity.clock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.data.info.BiggerVoltage;

/**
 * 普通的通用计数器，因为导线有独立的计数器、发电机不会过载所以该计数器只支持用电器使用
 * @author EmptyDreams
 * @version V1.0
 */
public class OrdinaryCounter extends OverloadCounter {
	
	private  BlockPos pos;
	private  World world;
	private BiggerVoltage bigger;
	
	public OrdinaryCounter(int maxTime) {
		super(maxTime);
	}
	
	@Override
	public void overload() {
		getBigger().EBV.overload(world.getTileEntity(pos), bigger);
		clean();
	}

	/** 计数器增加指定数量，当数量超过指定数值时自动触发{@link #overload()} */
	@Override
	public void plus(int amount) {
		super.plus(amount);
		if (getTime() >= getMaxTime()) overload();
	}

	/** 获取方块坐标 */
	public BlockPos getPos() { return pos; }
	/** 设置方块坐标 */
	public void setPos(BlockPos pos) { this.pos = pos; }
	/** 获取方块所在世界 */
	public World getWorld() { return world; }
	/** 设置方块所在世界 */
	public void setWorld(World world) { this.world = world; }
	/** 获取电压过大时的操作 */
	public BiggerVoltage getBigger() { return bigger; }
	/** 设置电压过大时的操作 */
	public void setBigger(BiggerVoltage bigger) { this.bigger = bigger; }
	
}
