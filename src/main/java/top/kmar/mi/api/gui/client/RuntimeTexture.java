package top.kmar.mi.api.gui.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import top.kmar.mi.api.utils.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * 运行时加载资源
 * @author EmptyDreams
 */
public class RuntimeTexture extends AbstractTexture {
	
	private static final Map<String, RuntimeTexture> instances = new Object2ObjectOpenHashMap<>();
	
	/**
	 * 根据资源名称获取材质资源
	 * @param name 资源名称
	 * @return 返回已有的或新的资源
	 */
	@Nonnull
	public static RuntimeTexture instance(String name) {
		return instances.computeIfAbsent(name, it -> {
			RuntimeTexture texture = new RuntimeTexture(name);
			texture.loadTexture(null);
			return texture;
		});
	}
	
	/**
	 * 根据图像生成一个材质资源
	 * @param name 资源名称
	 * @param image 图像资源
	 * @return 若资源已存在则返回已有实例
	 */
	@Nonnull
	public static RuntimeTexture instance(String name, BufferedImage image) {
		return instances.computeIfAbsent(name, it -> {
			RuntimeTexture texture = new RuntimeTexture(image);
			texture.loadTexture(null);
			return texture;
		});
	}
	
	/**
	 * 强制为指定资源名称设置资源
	 * @param name 名称
	 * @param image 图像资源
	 */
	public static RuntimeTexture setInstance(String name, BufferedImage image) {
		RuntimeTexture texture = new RuntimeTexture(image);
		texture.loadTexture(null);
		instances.put(name, texture);
		return texture;
	}
	
	/**
	 * 根据名称获取一个已存在材质
	 * @param name 名称
	 * @return 若资源不存在则返回null
	 */
	@Nullable
	public static RuntimeTexture getInstance(String name) {
		return instances.getOrDefault(name, null);
	}
	
	/**
	 * 判断指定资源名称是否已经被注册
	 * @param name 资源名称
	 */
	@SuppressWarnings("unused")
	public static boolean containName(String name) {
		return instances.containsKey(name);
	}
	
	private final String name;
	private int width, height;
	private final BufferedImage image;
	
	private RuntimeTexture(String name) {
		this.name = StringUtil.checkNull(name, "name");
		image = null;
	}
	
	private RuntimeTexture(BufferedImage image) {
		name = null;
		this.image = StringUtil.checkNull(image, "image");
	}
	
	@Override
	public void loadTexture(@Nullable IResourceManager resourceManager) {
		deleteGlTexture();
		BufferedImage image = name == null ? this.image : ImageData.getImage(name);
		width = image.getWidth(null);
		height = image.getHeight(null);
		TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), image, false, true);
	}
	
	/** 获取材质宽度 */
	public int getTextureWidth() {
		return width;
	}
	
	/** 获取材质高度 */
	public int getTextureHeight() {
		return height;
	}
	
	/** 装载材质到MC */
	public RuntimeTexture bindTexture() {
		GlStateManager.bindTexture(getGlTextureId());
		return this;
	}
	
	/**
	 * <p>将该材质中的部分图像绘制到窗口中.
	 * <p>该方法不会自动装载材质到MC，用户需要手动调用{@link #bindTexture()}
	 * @param x 窗体中的绘制起点坐标
	 * @param y 窗体中的绘制起点坐标
	 * @param tX 要绘制的内容在材质中的坐标
	 * @param tY 要绘制的内容在材质中的坐标
	 * @param width 要绘制的材质的宽度
	 * @param height 要绘制的材质的高度
	 */
	public void drawToFrame(int x, int y, int tX, int tY, int width, int height) {
		Gui.drawModalRectWithCustomSizedTexture(x, y, tX, tY,
				width, height, getTextureWidth(), getTextureHeight());
	}
	
}