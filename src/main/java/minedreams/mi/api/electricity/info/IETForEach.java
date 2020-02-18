package minedreams.mi.api.electricity.info;

import javax.annotation.Nonnull;

import minedreams.mi.api.electricity.ElectricityTransfer;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface IETForEach {
	
	boolean run(@Nonnull ElectricityTransfer et);
	
}
