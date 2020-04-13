package xyz.emptydreams.mi.api.electricity.src.info;

/**
 * 存储电压过大时操作
 * @author EmptyDremas
 * @version V1.0
 */
@SuppressWarnings("unused")
public final class BiggerVoltage {
	
	/** 效果强度 */
	public final float intensity;
	/** 过载后的操作 */
	public final EnumBiggerVoltage EBV;
	
	/* 一下设置爆炸专属 */
	/** 爆炸伤害 */
	private int damage = 1;
	/** 爆炸伤害范围，不包括中心方块 */
	private int radius = 1;
	
	/**
	 * @param intensity 强度，对于爆炸是爆炸强度，对于火灾是火灾块数
	 * @param ebv 操作
	 */
	public BiggerVoltage(float intensity, EnumBiggerVoltage ebv) {
		this.intensity = (intensity > 0) ? intensity : 1;
		this.EBV = ebv;
	}
	
	/**
	 * 设置爆炸伤害
	 * @param damage 伤害
	 *
	 * @throws IllegalArgumentException 如果 damage < 0
	 */
	public void setBoomDamage(int damage) {
		if (damage < 0) throw new IllegalArgumentException("damage < 0 : " + damage);
		this.damage = damage;
	}
	
	/**
	 * 设置爆炸伤害半径
	 * @param radius 半径
	 *
	 * @throws IllegalArgumentException 如果 radius < 0
	 */
	public void setBoomRadius(int radius) {
		if (radius < 0) throw new IllegalArgumentException("radius < 0 : " + radius);
		this.radius = radius;
	}
	
	public int getBoomDamage() { return damage; }
	public int getBoomRadius() { return radius; }
	
}
