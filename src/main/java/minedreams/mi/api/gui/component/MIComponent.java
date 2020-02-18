package minedreams.mi.api.gui.component;

import minedreams.mi.api.net.WaitList;
import net.minecraft.util.ResourceLocation;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public abstract class MIComponent {
	
	/** X轴坐标 */
	private int x;
	/** Y轴坐标 */
	private int y;
	/** 宽度 */
	private int width;
	/** 高度 */
	private int height;
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * 获取一个渲染数据，可以不重写
	 *
	 * @return 如果为null则不绘制图像
	 */
	public RenderTexture getRender() { return null; }
	
	public static final class RenderString {
	
		private final String text;
		
		/**
		 *
		 * @param text
		 */
		public RenderString(String text) {
			WaitList.checkNull(text, "text");
			this.text = text;
		}
	
	}
	
	/**
	 * 保存图像渲染数据
	 */
	public static final class RenderTexture {
		
		private final ResourceLocation rl;
		private final int x, y;
		
		public RenderTexture(ResourceLocation texture, int x, int y) {
			rl = texture;
			this.x = x;
			this.y = y;
		}
		
		public ResourceLocation getTexture() {
			return rl;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
	}
	
}
