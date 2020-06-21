package xyz.emptydreams.mi.api.gui.component;

import net.minecraft.client.renderer.GlStateManager;
import xyz.emptydreams.mi.api.gui.client.RuntimeTexture;
import xyz.emptydreams.mi.api.utils.DebugHelper;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public interface IProgressBar extends IComponent {
	
	String RESOURCE_NAME = "progress";
	
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
		if (isReverse()) return ((double) getMax() - getNow()) / getMax();
		return ((double) getNow()) / getMax();
	}
	
	static void bindTexture() {
		GlStateManager.bindTexture(RuntimeTexture.instance(RESOURCE_NAME).getGlTextureId());
	}
	
}
