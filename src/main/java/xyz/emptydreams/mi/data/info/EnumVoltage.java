package xyz.emptydreams.mi.data.info;

import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

import javax.annotation.Nonnull;

/**
 * 存储支持的电压等级
 *
 * @author EmptyDreams
 * @version V1.0
 */
public enum EnumVoltage implements IVoltage {

	NON(0, Double.MAX_VALUE),
	A(10, 0.05),
	B(50, 0.04),
	C(100, 0.03),
	D(200, 0.01),
	E(500, 0.007),
	F(1000, 0.005),
	G(2000, 0.003),
	H(5000, 0.001);
	
	private final int VOLTAGE;
	private final double LOSS;
	
	EnumVoltage(int voltage, double loss) {
		VOLTAGE = voltage;
		LOSS = loss;
	}
	
	@Override
	public int getVoltage() {
		return VOLTAGE;
	}
	
	@Override
	public double getLossIndex() {
		return LOSS;
	}
	
	@Nonnull
	@Override
	public IVoltage copy() {
		return this;
	}
	
}
