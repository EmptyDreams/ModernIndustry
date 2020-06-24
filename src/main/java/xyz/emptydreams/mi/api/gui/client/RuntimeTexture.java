package xyz.emptydreams.mi.api.gui.client;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import xyz.emptydreams.mi.api.gui.component.ImageData;

/**
 * 运行时加载资源
 * @author EmptyDreams
 * @version V1.0
 */
public class RuntimeTexture extends AbstractTexture {
	
	private static final Map<String, RuntimeTexture> instances = new LinkedHashMap<>();
	private static final Map<BufferedImage, RuntimeTexture> image_instances = new LinkedHashMap<>();
	
	/**
	 * 根据资源名称获取材质资源
	 * @param name 资源名称
	 * @return 返回已有的或新的资源
	 */
	public static RuntimeTexture instance(String name) {
		return instances.computeIfAbsent(name, it -> {
			RuntimeTexture texture = new RuntimeTexture(name);
			texture.loadTexture(null);
			return texture;
		});
	}
	
	/**
	 * 根据图像生成一个材质资源
	 * @param image 图像资源，用户必须保证image内容不会变化，否则第二次调用方法可能返回错误结果
	 * @return 若资源已存在则返回已有实例
	 */
	public static RuntimeTexture instance(BufferedImage image) {
		return image_instances.computeIfAbsent(image, it -> {
			RuntimeTexture texture = new RuntimeTexture(image);
			texture.loadTexture(null);
			return texture;
		});
	}
	
	private final String name;
	private int width, height;
	private final BufferedImage image;
	
	private RuntimeTexture(String name) {
		this.name = name;
		image = null;
	}
	
	private RuntimeTexture(BufferedImage image) {
		name = null;
		this.image = image;
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
	 * 将该材质中的部分图像绘制到窗口中.
	 * 该方法不会自动装载材质到MC，用户需要手动调用{@link #bindTexture()}
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
