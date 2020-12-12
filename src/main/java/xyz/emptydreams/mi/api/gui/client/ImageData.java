package xyz.emptydreams.mi.api.gui.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xyz.emptydreams.mi.ModernIndustry;
import xyz.emptydreams.mi.api.gui.component.interfaces.IProgressBar;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
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
	 * @return 没有找到元素则返回null
	 */
	public static Image getImage(String name, int width, int height) {
		Node node = resourceInfo.getOrDefault(name, null);
		if (node == null) return null;
		if (node.image.getWidth(null) == width && node.image.getHeight(null) == height) {
			return node.image;
		}
		return node.image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
	}
	
	/**
	 * 获取指定名称的图像资源，不进行缩放
	 * @param name 名称（一般存储在{@link xyz.emptydreams.mi.api.gui.component}包中的某个类中）
	 * @return 没有找到元素则返回null
	 */
	public static BufferedImage getImage(String name) {
		Node node = resourceInfo.getOrDefault(name, null);
		if (node == null) return null;
		return node.image;
	}
	
	/** 存储资源 */
	private final static Map<String, Node> resourceInfo = new HashMap<String, Node>() {
		{
			put("background",                   new Node(162, 0, 256, 256));
			put("slot",                         new Node(0, 76, 26, 26));
			put("backpack",                     new Node(0, 0, 162, 76));
			put(IProgressBar.RESOURCE_NAME,     new Node(0, 102, 111, 74));
			put("button",                       new Node(162, 256, 15, 20));
			put("buttonClicked",                new Node(162, 276, 15, 20));
		}
	};
	
	static {
		try {
			updateResource(new ResourceLocation(ModernIndustry.MODID, "textures/gui/tools.png"));
		} catch (IOException e) {
			throw new RuntimeException("MI GuiHelper读取材质文件时出现异常！", e);
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
