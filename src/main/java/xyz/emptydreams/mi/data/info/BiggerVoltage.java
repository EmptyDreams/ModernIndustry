package xyz.emptydreams.mi.data.info;

/**
 * 存储电压过大时操作
 * @author EmptyDremas
 */
public final class BiggerVoltage {
	
	/** 效果强度 */
	public final float intensity;
	/** 过载后的操作 */
	public final EnumBiggerVoltage EBV;
	/** 爆炸强度 */
	private float boomStrength = 1F;
	/** 火焰半径 */
	private int fireRadius;
	
	/**
	 * @param intensity 强度，对于爆炸是爆炸强度，对于火灾是火灾块数
	 * @param ebv 操作
	 */
	public BiggerVoltage(float intensity, EnumBiggerVoltage ebv) {
		this.intensity = (intensity > 0) ? intensity : 1;
		this.EBV = ebv;
	}
	
	/** 获取爆炸强度 */
	public float getBoomStrength() {
		return boomStrength;
	}
	/** 设置爆炸强度 */
	public void setBoomStrength(float boomStrength) {
		this.boomStrength = boomStrength;
	}
	/** 获取火焰半径 */
	public int getFireRadius() {
		return fireRadius;
	}
	/** 设置火焰半径 */
	public void setFireRadius(int fireRadius) {
		this.fireRadius = fireRadius;
	}
	/** 获取火焰生成几率 */
	public float getFirePer() { return boomStrength; }
	/** 设置火焰生成几率 */
	public void setFirePer(float per) { boomStrength = per; }
	
}
