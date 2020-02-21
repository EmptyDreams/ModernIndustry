package minedreams.mi.api.electricity.clock;

import minedreams.mi.api.electricity.ElectricityUser;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 普通的通用计数器，因为导线有独立的计数器、发电机不会过载所以该计数器只支持用电器使用
 *
 * @author EmptyDreams
 * @version V1.0
 */
public class OrdinaryCounter extends OverloadCounter {
	
	private final ElectricityUser ele;
	
	public OrdinaryCounter(ElectricityUser user) {
		ele = user;
		if (temp == null) temp = new Temp(user.getWorld());
	}
	
	@Override
	public void overload() {
		switch (ele.getBiggerVoltageOperate().EBV) {
			case NON:
				break;
			case BOOM:
				BlockPos pos = ele.getPos();
				ele.getWorld().createExplosion(temp, pos.getX(), pos.getY(), pos.getZ(),
						ele.getBiggerVoltageOperate().intensity, true);
				break;
			case FIRE:
				break;
		}
	}
	
	@Override
	public void plus(int amount) {
		super.plus(amount);
		if (getTime() >= ele.getBiggerMaxTime()) overload();
	}
	
	private static Temp temp;
	private static final class Temp extends Entity {
		
		public Temp(World world) {
			super(world);
		}
		@Override
		protected void entityInit() {
		}
		@Override
		protected void readEntityFromNBT(NBTTagCompound compound) {
		}
		@Override
		protected void writeEntityToNBT(NBTTagCompound compound) {
		}
	}
	
}
