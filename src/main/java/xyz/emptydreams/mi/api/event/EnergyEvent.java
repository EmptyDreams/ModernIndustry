package xyz.emptydreams.mi.api.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.Event;
import xyz.emptydreams.mi.api.electricity.info.EleEnergy;

/**
 * 与电能有关的事件
 * @author EmptyDreams
 * @version V1.0
 */
public class EnergyEvent extends Event {
	
	/** 此次涉及到的电能 */
	public final EleEnergy energy;
	/** 进行此次操作的方块 */
	public final TileEntity te;
	
	private EnergyEvent(EleEnergy energy, TileEntity te) {
		this.energy = energy;
		this.te = te;
	}
	
	/** 接收电能后 */
	public static class Receive extends EnergyEvent {
		
		public Receive(EleEnergy energy, TileEntity te) {
			super(energy, te);
		}
		
	}
	
	/** 输出电能后 */
	public static class Extract extends EnergyEvent {
		
		public Extract(EleEnergy energy, TileEntity te) {
			super(energy, te);
		}
		
	}
	
}
