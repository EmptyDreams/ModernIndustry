package xyz.emptydreams.mi.data.info;

import xyz.emptydreams.mi.api.electricity.interfaces.IVoltage;

import javax.annotation.Nonnull;

/**
 * 存储支持的电压等级
 * @author EmptyDreams
 */
public enum EnumVoltage implements IVoltage {

	NON(0, Double.MAX_VALUE),
	A(10, 0.0012),
	B(50, 0.001),
	C(100, 0.008),
	D(200, 0.006),
	E(500, 0.005),
	F(1000, 0.004),
	G(10000, 0.003),
	H(50000, 0.001),
	MAX(Integer.MAX_VALUE, 0);
	
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