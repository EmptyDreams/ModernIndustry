package minedreams.mi.blocks.te.maker;

import minedreams.mi.api.electricity.src.tileentity.EleMaker;
import minedreams.mi.register.te.AutoTileEntity;

/**
 * @author EmptyDreams
 * @version V1.0
 */
@AutoTileEntity("perpetual")
public class EMPerpetual extends EleMaker {
	
	{
		meBox = Integer.MAX_VALUE;
		meBoxMax = Integer.MAX_VALUE;
		setOutputMax(Integer.MAX_VALUE);
	}
	
	@Override
	public void input(int ee) {
		setMeBox(Integer.MAX_VALUE);
	}
}
