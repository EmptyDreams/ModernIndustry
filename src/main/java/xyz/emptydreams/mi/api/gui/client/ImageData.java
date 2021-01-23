package xyz.emptydreams.mi.api.gui.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.exception.IntransitException;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * 存储GUI相关的图像资源
 * @author EmptyDreams
 */
@SideOnly(Side.CLIENT)
public final class ImageData {
	
	/** 不应该调用的构造函数 */
	private ImageData() { throw new AssertionError(); }
	
	/**
	 * 替换已有的材质资源
	 * @param location 材质路径
	 * @throws IOException 若读取时发生错误
	 */
	public static void updateResource(ResourceLocation location) throws IOException {
		BufferedImage image = ImageIO.read(
				Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream());
		for (Node value : resourceInfo.values()) {
			value.image = image.getSubimage(value.x, value.y, value.w, value.h);
		}
	}
	
	/**
	 * 获取指定名称的图像资源
	 * @param name 名称（一般存储在{@link xyz.emptydreams.mi.api.gui.component}包中的某个类中）
	 * @param width 目标宽度
	 * @param height 目标高度
	 * @throws IllegalArgumentException 如果输入的名称不存在
	 */
	@Nonnull
	public static Image getImage(String name, int width, int height) {
		Node node = resourceInfo.getOrDefault(name, null);
		if (node == null) throw new IllegalArgumentException("名称不存在：" + name);
		if (node.image.getWidth(null) == width && node.image.getHeight(null) == height) {
			return node.image;
		}
		return node.image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
	}
	
	/**
	 * 获取指定名称的图像资源，不进行缩放
	 * @param name 名称（一般存储在{@link xyz.emptydreams.mi.api.gui.component}包中的某个类中）
	 * @throws IllegalArgumentException 如果输入的名称不存在
	 */
	@Nonnull
	public static BufferedImage getImage(String name) {
		Node node = resourceInfo.getOrDefault(name, null);
		if (node == null) throw new IllegalArgumentException("名称不存在：" + name);
		return node.image;
	}
	
	/**
	 * 创建一个RuntimeTexture
	 * @param name 材质名称
	 * @param textureName 资源名称
	 */
	@SuppressWarnings("unused")
	@Nonnull
	public static RuntimeTexture createTexture(String name, String textureName) {
		return RuntimeTexture.instance(textureName, getImage(name));
	}
	
	/**
	 * 创建一个RuntimeTexture
	 * @param name 材质名称
	 * @param width 宽度
	 * @param height 长度
	 * @param textureName 资源名称
	 */
	@Nonnull
	public static RuntimeTexture createTexture(String name, int width, int height, String textureName) {
		Image image = getImage(name, width, height);
		BufferedImage buffered = new BufferedImage(width, height, 6);
		Graphics g = buffered.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return RuntimeTexture.instance(textureName, buffered);
	}
	
	/** GUI背景 */
	public static final String BACKGROUND = "background";
	/** 物品框 */
	public static final String SLOT = "slot";
	/** 玩家背包 */
	public static final String BACKPACK = "backpack";
	/** 进度条 */
	public static final String PROGRESS_BAR = "progress";
	/** 矩形按钮 */
	public static final String BUTTON_REC = "button";
	/** 矩形按钮（点击） */
	public static final String BUTTON_REC_CLICK = "buttonClicked";
	/** 三角形向右按钮 */
	public static final String BUTTON_TRIANGLE_RIGHT = "rightButton";
	/** 三角形向右按钮（点击） */
	public static final String BUTTON_TRIANGLE_RIGHT_CLICK = "rightButtonClicked";
	/** 三角形向左按钮 */
	public static final String BUTTON_TRIANGLE_LEFT = "leftButton";
	/** 三角形向左按钮（点击） */
	public static final String BUTTON_TRIANGLE_LEFT_CLICK = "leftButtonClicked";
	
	/** 存储资源 */
	private final static Map<String, Node> resourceInfo = new Object2ObjectArrayMap<String, Node>(10) {
		{
			put(BACKGROUND,                             new Node(162, 0, 256, 256));
			put(SLOT,                                   new Node(0, 76, 26, 26));
			put(BACKPACK,                               new Node(0, 0, 162, 76));
			put(PROGRESS_BAR,                           new Node(0, 102, 111, 74));
			put(BUTTON_REC,                             new Node(162, 256, 15, 20));
			put(BUTTON_REC_CLICK,                       new Node(162, 276, 15, 20));
			put(BUTTON_TRIANGLE_RIGHT,                  new Node(177, 256, 14, 22));
			put(BUTTON_TRIANGLE_RIGHT_CLICK,            new Node(177, 278, 14, 22));
			put(BUTTON_TRIANGLE_LEFT,                   new Node(191, 256, 14, 22));
			put(BUTTON_TRIANGLE_LEFT_CLICK,             new Node(191, 278, 14, 22));
		}
	};
	
	static {
		try {
			updateResource(new ResourceLocation(ModernIndustry.MODID, "textures/gui/tools.png"));
		} catch (IOException e) {
			throw new IntransitException("MI GuiHelper读取材质文件时出现异常！", e);
		}
	}
	
	private final static class Node {
		
		BufferedImage image;
		int x, y, w, h;
		
		Node(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			w = width;
			h = height;
		}
		
	}
	
}