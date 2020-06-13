package xyz.emptydreams.mi.api.electricity.clock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.emptydreams.mi.api.electricity.src.info.BiggerVoltage;

/**
 * 普通的通用计数器，因为导线有独立的计数器、发电机不会过载所以该计数器只支持用电器使用
 *
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
		bigger.EBV.overload(world.getTileEntity(pos), bigger);
		clean();
	}
	
	@Override
	public void plus(int amount) {
		super.plus(amount);
		if (getTime() >= getMaxTime()) overload();
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public void setPos(BlockPos pos) {
		this.pos = pos;
	}
	
	public World getWorld() {
		return world;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public BiggerVoltage getBigger() {
		return bigger;
	}
	
	public void setBigger(BiggerVoltage bigger) {
		this.bigger = bigger;
	}
	
}
