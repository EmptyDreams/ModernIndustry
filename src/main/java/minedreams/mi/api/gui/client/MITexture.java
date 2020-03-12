package minedreams.mi.api.gui.client;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

/**
 * 材质类.
 * 使用后必须调用{@link #invalidate()}来使当前类恢复可重用状态
 * @author EmptyDreams
 * @version V1.0
 */
public class MITexture extends AbstractTexture {
	
	private final int WIDTH;
	private final int HEIGHT;
	private final BufferedImage IMAGE;
	private final boolean BLUR;
	private final boolean CLAMP;
	/** 是否需要装载 */
	private boolean needLoad = true;
	private boolean isInvalid = false;
	private static final List<MITexture> TEXTURES = new ArrayList<>(10);
	
	/**
	 * 获取一个对象，该对象可能是复用以前的对象.<br>
	 * 对象自动会被调用{@link #cleanImage()}方法，无需手动调用。
	 * @param width 宽度
	 * @param height 高度
	 */
	@Nonnull
	public static MITexture getInstance(int width, int height) {
		MITexture text;
		//noinspection ForLoopReplaceableByForEach
		for (int i = 0; i < TEXTURES.size(); i++) {
			text = TEXTURES.get(i);
			if (text.isInvalid() && text.WIDTH == width && text.HEIGHT == height) {
				text.cleanImage();
				text.validate();
				return text;
			}
		}
		return new MITexture(width, height);
	}
	
	public MITexture(int width, int height) {
		this(width, height, true, false);
	}
	
	public MITexture(int width, int height, boolean isBlur, boolean isClamp) {
		this(width, height, BufferedImage.TYPE_4BYTE_ABGR, isBlur, isClamp);
	}
	
	public MITexture(int width, int height, int imageType, boolean isBlur, boolean isClamp) {
		WIDTH = width;
		HEIGHT = height;
		IMAGE = new BufferedImage(width, height, imageType);
		BLUR = isBlur;
		CLAMP = isClamp;
		TEXTURES.add(this);
	}
	
	public int getWidth() { return WIDTH; }
	public int getHeight() { return HEIGHT; }
	public Graphics getGraphics() { return IMAGE.getGraphics(); }
	public Graphics getGraphics(int x, int y, int width, int height) {
		return IMAGE.getGraphics().create(x, y, width, height);
	}
	public void cleanImage() {
		Graphics2D g2 = (Graphics2D) IMAGE.getGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
	}
	/** 使类可重用 */
	public void invalidate() { isInvalid = true; }
	/** 使类不可重用 */
	public void validate() { isInvalid = false; }
	/** 判断该对象是否可重用 */
	public boolean isInvalid() { return isInvalid; }
	
	@Override
	public void loadTexture(IResourceManager resourceManager) {
		if (needLoad) {
			TextureUtil.uploadTextureImageAllocate(getGlTextureId(), IMAGE, BLUR, CLAMP);
			needLoad = false;
		}
	}
}
