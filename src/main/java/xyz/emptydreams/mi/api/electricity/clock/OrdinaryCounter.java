package xyz.emptydreams.mi.api.electricity.clock;

import xyz.emptydreams.mi.api.electricity.src.info.BiggerVoltage;
import xyz.emptydreams.mi.api.electricity.src.tileentity.EleSrcUser;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 普通的通用计数器，因为导线有独立的计数器、发电机不会过载所以该计数器只支持用电器使用
 *
 * @author EmptyDreams
 * @version V1.0
 */
public class OrdinaryCounter extends OverloadCounter {
	
	private final BlockPos pos;
	private final World world;
	private final BiggerVoltage bigger;
	
	public OrdinaryCounter(EleSrcUser user) {
		this(user.getWorld(), user.getPos(), user.getBiggerVoltageOperate(), user.getBiggerMaxTime());
	}
	
	public OrdinaryCounter(World world, BlockPos pos, BiggerVoltage bigger, int maxTime) {
		super(maxTime);
		this.pos = pos;
		this.bigger = bigger;
		this.world = world;
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
	
}
