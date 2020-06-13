package xyz.emptydreams.mi.api.gui.client;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import xyz.emptydreams.mi.api.net.WaitList;

/**
 * 材质类
 * @author EmptyDreams
 * @version V2.0
 */
public class MITexture extends AbstractTexture {
	
	private final boolean flag;
	private final boolean flag1;
	private final ResourceLocation rl;
	
	public MITexture(ResourceLocation rl, boolean flag, boolean flag1) {
		WaitList.checkNull(rl, "rl");
		this.rl = rl;
		this.flag = flag;
		this.flag1 = flag1;
	}
	
	public MITexture(ResourceLocation rl) {
		this(rl, false, true);
	}
	
	@Override
	public void loadTexture(@Nullable IResourceManager resourceManager) throws IOException {
		deleteGlTexture();
		String root = "mods/cache/" + rl.getResourceDomain() + "/gui/";
		File path = new File(root);
		checkFile(path);
		path = new File(root + rl.getResourcePath());
		
		try (InputStream input = new FileInputStream(path)) {
			BufferedImage buffered = TextureUtil.readBufferedImage(input);
			TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), buffered, flag, flag1);
		}
	}
	
	private static void checkFile(File file) throws IOException {
		if (!file.exists()) {
			boolean right = file.mkdirs();
			if(!right) throw new FileNotFoundException("[" + file.getAbsolutePath() + "]目录创建失败");
		}
		if (!file.canRead())
			throw new FileNotFoundException("[" + file.getAbsolutePath() + "]无访问权限");
	}
	
	public static void writeFile(ResourceLocation path, BufferedImage image) throws IOException {
		String root = "mods/cache/" + path.getResourceDomain() + "/gui";
		File file = new File(root);
		checkFile(file);
		file = new File(root + "/" + path.getResourcePath());
		if (file.exists()) {
			//noinspection ResultOfMethodCallIgnored
			file.delete();
		}
		ImageIO.write(image, "png", file);
	}
	
}
