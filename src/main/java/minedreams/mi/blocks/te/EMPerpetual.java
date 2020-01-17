package minedreams.mi.blocks.te;

import minedreams.mi.api.electricity.ElectricityMaker;
import minedreams.mi.api.electricity.ElectricityTransfer;
import minedreams.mi.api.electricity.info.ElectricityEnergy;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public class EMPerpetual extends ElectricityMaker {
	
	{
		meBox = Integer.MAX_VALUE;
		meBoxMax = Integer.MAX_VALUE;
	}
	
	@Override
	public OutPutResult output(Energy e, boolean isTrue) {
		if (isTrue) e.me = 0;
		return OutPutResult.YES;
	}
	
	@Override
	public int getVoltage_max() {
		return Integer.MAX_VALUE;
	}
	
	@Override
	public int getVoltage_min() {
		return 0;
	}
	
	@Override
	public boolean checkOutput(ElectricityTransfer.EETransfer ee) {
		return true;
	}
	
	@Override
	public boolean input(int ee) {
		return true;
	}
	
	@Override
	public boolean run() {
		return true;
	}
	
	@Override
	public boolean isOverload(ElectricityEnergy now) {
		return false;
	}
	
	@Override
	public int getOutputMax() {
		return Integer.MAX_VALUE;
	}
	
}
