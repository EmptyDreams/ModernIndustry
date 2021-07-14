package xyz.emptydreams.mi.api.utils;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

/**
 * 有关图形的工具
 * @author EmptyDreams
 */
public final class ImageUtil {
	
	/**
	 * 根据图像生成一个唯一的字符串
	 * @param image 图像
	 */
	@Nonnull
	public static String createString(BufferedImage image) {
		int[] rgb = image.getRGB(0, 0,
				image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		return MathUtil.compressArray2String(rgb);
	}
	
}