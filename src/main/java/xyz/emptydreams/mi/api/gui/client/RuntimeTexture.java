package xyz.emptydreams.mi.api.gui.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import net.minecraft.client.Minecraft;
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
	
	private static final Map<String, RuntimeTexture> instances = new HashMap<>();
	
	public static RuntimeTexture instance(String name) {
		return instances.computeIfAbsent(name, it -> {
			RuntimeTexture texture = new RuntimeTexture(name);
			try {
				texture.loadTexture(Minecraft.getMinecraft().getResourceManager());
			} catch (IOException e) {
				throw new RuntimeException("资源加载异常：" + name, e);
			}
			return texture;
		});
	}
	
	private final String name;
	
	private RuntimeTexture(String name) {
		this.name = name;
	}
	
	public String getLocation() { return name; }
	
	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException {
		deleteGlTexture();
		
		BufferedImage image = ImageData.getImage(name);
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		int size = 256;
		
		BufferedImage real = new BufferedImage(size, size, 6);
		real.getGraphics().drawImage(image, 0, 0, null);
		
		TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), real, false, true);
		
	}
	
	public static int getSize(int min) {
		for (int i = 3; i < 32; ++i) {
			if (1 << i >= min) return 1 << i;
		}
		return -1;
	}
	
}
