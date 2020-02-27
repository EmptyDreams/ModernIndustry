package minedreams.mi.api.electricity.interfaces;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface IVoltage {
	
	int getVoltage();
	
	int getLossIndex();
	
	static IVoltage create(int voltage, int lossIndex) {
		return new IVoltage() {
			@Override
			public int getVoltage() {
				return voltage;
			}
			
			@Override
			public int getLossIndex() {
				return lossIndex;
			}
		};
	}
	
}
