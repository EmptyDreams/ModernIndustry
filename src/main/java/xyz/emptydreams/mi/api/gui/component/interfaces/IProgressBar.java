package xyz.emptydreams.mi.api.gui.component.interfaces;

import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;

import static xyz.emptydreams.mi.api.gui.client.ImageData.PROGRESS_BAR;

/**
 * 进度条接口
 * @author EmptyDreams
 */
public interface IProgressBar extends IComponent {
	
	/** 获取当前的进度时间 */
	int getNow();
	
	/** 获取总进度 */
	int getMax();
	
	/** 设置总进度 */
	void setMax(int max);
	
	/** 设置当前进度 */
	void setNow(int now);
	
	/** 判断进度条是否翻转 */
	boolean isReverse();
	
	/**
	 * 获取百分比进度
	 * @return 0 <= result <= 1
	 */
	default double getPer() {
		int max = Math.max(0, getMax());
		if (isReverse()) return ((double) max - getNow()) / max;
		return ((double) getNow()) / max;
	}
	
	static RuntimeTexture getTexture() {
		return RuntimeTexture.instance(PROGRESS_BAR).bindTexture();
	}
	
}