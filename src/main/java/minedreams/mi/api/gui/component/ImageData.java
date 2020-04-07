package minedreams.mi.api.gui.component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author EmptyDreams
 * @version V1.0
 */
public final class ImageData {
	
	public final static BufferedImage BACKGROUND;
	public final static BufferedImage BACKPACK;
	public final static BufferedImage INPUT;
	public final static BufferedImage OUTPUT;
	public final static BufferedImage PROGRESS_BAR;
	
	private ImageData() { }
	
	private static byte[] _background = { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 1, 0, 0, 0, 1, 0, 8, 6, 0, 0, 0, 92, 114, -88, 102, 0, 0, 3, -112, 73, 68, 65, 84, 120, -100, -19, -41, -79, 113, -37, 64, 16, 64, -47, 61, 13, 91, 100, 93, 40, -124, 125, -128, 101, -99, 3, 91, 26, -103, 99, -121, 34, 103, -8, -33, -117, 14, -120, 54, -63, -57, -34, -102, -65, -19, 1, -34, -39, -6, -2, -16, -15, -86, 41, -128, -41, -5, -84, -63, -98, -103, -39, -37, 2, 0, -17, 108, -83, 53, -13, 109, 11, -8, -104, -103, -67, -9, -10, -15, 67, -64, 121, -98, 51, -65, 127, -8, 123, -58, 21, 0, -46, -42, -4, -39, 0, 30, -35, -17, -9, -25, 79, 3, -4, -88, -29, 56, -66, -50, -73, -37, -51, 6, 0, 101, -105, -57, 23, -2, -4, -48, 97, 3, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, 48, 1, -128, -80, -53, -85, 7, 0, 126, -34, 113, 28, -1, 124, 111, 3, -128, -80, -53, -52, -84, -75, -42, -98, -103, 57, -49, -13, -65, -91, 0, -34, -113, 13, 0, -62, -42, -61, -13, -66, 94, -81, 47, 25, 4, 120, -114, -37, -19, -10, 121, 92, -113, 1, -104, -103, -39, 79, -100, 5, 120, -66, -81, -17, -34, 21, 0, -62, 126, 1, 46, 25, 42, 78, -125, 4, -67, 79, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};
	private static byte[] _backpack = { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -94, 0, 0, 0, 76, 8, 6, 0, 0, 0, -18, -68, 108, -23, 0, 0, 1, 51, 73, 68, 65, 84, 120, -100, -19, -37, -79, 13, -125, 64, 20, 5, 65, -20, -90, -96, 16, 74, -95, 11, 90, -95, -57, -77, -20, 18, 124, 72, 94, -55, 51, 17, -47, -117, 86, -126, -32, -13, 88, -41, 117, 44, -109, -10, 125, 95, -82, -21, -102, -99, -79, -13, -57, 59, -97, 16, -33, 15, 51, -114, -29, 88, -74, 109, 91, -20, -40, -7, 118, -25, 57, -75, 0, 55, 17, 34, 9, 66, 36, 65, -120, 36, 8, -111, 4, 33, -110, 32, 68, 18, -124, 72, -126, 16, 73, 16, 34, 9, 66, 36, 65, -120, 36, 8, -111, 4, 33, -110, 32, 68, 26, -50, -13, 28, 119, -80, 99, 103, -122, 11, 109, 59, -119, 29, 23, -38, 118, 18, 59, -66, 17, 73, 16, 34, 9, 66, 36, 65, -120, 36, 8, -111, 4, 33, -110, 32, 68, 18, -124, 72, -126, 16, 73, 16, 34, 9, 66, 36, 65, -120, 36, 8, -111, 4, 33, -110, 32, 68, 26, 92, 32, -37, 41, -20, -72, -48, -74, -109, -40, 113, -95, 109, 39, -79, -29, 27, -111, 4, 33, -110, 32, 68, 18, -124, 72, -126, 16, 73, 16, 34, 9, 66, 36, 65, -120, 36, 8, -111, 4, 33, -110, 32, 68, 18, -124, 72, -126, 16, 73, 16, 34, 9, 66, -92, -63, 37, -77, -99, -62, -50, 99, -116, -23, 3, 109, -104, -26, -43, 76, -126, 16, 73, 16, 34, 9, -2, -30, -77, -109, -40, -15, 23, -97, -99, -60, -114, 87, 51, 9, 66, 36, 65, -120, 36, 8, -111, 4, 33, -110, 32, 68, 18, -124, 72, -126, 16, 73, 16, 34, 9, 66, 36, 65, -120, 36, 8, -111, 4, 33, -110, 32, 68, 18, -124, 72, -125, -65, -35, -20, -4, 124, 103, -116, -15, 2, 58, 17, -61, 123, -95, -8, 117, 42, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126 };
	private static byte[] _input = { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 18, 0, 0, 0, 18, 8, 6, 0, 0, 0, 86, -50, -114, 87, 0, 0, 0, 54, 73, 68, 65, 84, 56, -115, 99, 52, 55, 55, -1, -49, 64, 33, 8, 9, 9, 97, 96, 97, -128, 50, 40, 1, 37, 37, 37, 12, 76, -108, -70, 6, 6, 70, 13, 26, 53, 104, -44, -96, -111, 106, 16, 67, 119, 119, -9, 127, -118, -63, -1, -1, -1, 1, 108, 118, 75, 120, 110, 121, 94, 73, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126 };
	private static byte[] _output = { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, 26, 0, 0, 0, 26, 8, 6, 0, 0, 0, -87, 74, 76, -50, 0, 0, 0, 64, 73, 68, 65, 84, 72, -119, -19, -49, 49, 17, 0, 49, 12, 3, 65, -27, 39, -100, 76, -60, 80, -52, -62, -96, -107, 38, -127, -32, -22, -17, 42, 117, 59, 90, 17, 97, 13, -105, -103, -38, -70, 99, -78, -86, -46, 55, -3, -26, 5, 4, 4, 4, 4, 4, 4, 4, -12, 79, 72, -35, -19, -15, 108, 31, -10, -40, 107, 104, -81, 24, -18, 71, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126 };
	private static byte[] _progressBar = { -119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82, 0, 0, 0, -128, 0, 0, 0, -128, 8, 6, 0, 0, 0, -61, 62, 97, -53, 0, 0, 2, 7, 73, 68, 65, 84, 120, -100, -19, -37, 97, 110, -22, 48, 12, 0, -32, -10, 105, 71, -122, 83, -64, 1, -72, 28, 103, -23, -124, -60, -45, -90, -86, 32, 68, 28, 86, -57, -33, -9, 115, 19, -120, 36, -58, 113, -36, 48, 47, -53, 50, -67, -29, 124, 62, 47, -121, -61, 97, 126, -12, -46, -37, -1, -33, 122, 99, 94, 118, 60, 30, 31, -50, -1, -85, -2, -75, -68, -40, 34, -25, -41, 20, 0, -109, 32, 72, -17, 43, 98, 0, 91, -37, -63, -11, 122, 45, 57, -95, -39, 52, -43, 0, -21, -65, -83, -126, 64, 102, -24, 108, -98, -25, -65, -83, 1, -42, 108, 7, -7, -52, -89, -45, 41, 124, -47, -18, -103, 64, 48, 116, 22, -111, 1, 66, 106, -128, -75, 91, 38, 80, 3, -28, -48, 37, 0, -98, -11, 7, -10, -30, -123, 26, 102, -9, 46, -105, 75, -13, 71, 12, 45, 2, -89, 68, -109, -8, -84, 94, -55, 50, -122, -128, 29, 32, 54, 3, 100, -5, 6, 61, -14, 59, 56, 70, 25, -45, 35, 97, -89, -128, 81, 39, -22, 22, 12, 35, -97, 110, 66, 2, 96, -12, 111, -55, 52, 112, 32, 52, 111, 1, 21, 22, -1, -73, 117, 16, 100, 31, 127, 83, 0, 84, 91, -4, 45, -39, -21, -123, -73, 79, 1, -9, 70, -113, -57, -63, 27, 62, 21, 8, 17, -89, -128, -42, 26, 64, -73, 111, 67, -90, 122, 33, -30, 24, -8, 52, 19, 84, -106, 97, 123, -120, -22, 3, 8, -126, 13, 25, 106, -126, -56, 70, -112, 32, -72, -53, 84, 12, -74, 22, -127, -101, -17, -7, -2, -57, -7, -100, 30, 123, -12, -89, 23, 62, -86, 21, 28, 61, 17, -91, 50, 65, -23, 62, -64, 19, -61, 6, -63, 104, -67, -113, 94, 1, 48, -36, -30, -113, -38, -12, -6, 106, 88, -84, -44, 53, -64, -85, 70, -17, 118, 70, 103, -128, 33, 38, -85, 82, -117, 59, 50, 0, -46, 79, 90, -59, 103, 27, 81, -57, -64, -116, 19, -73, 53, -16, 84, -29, -40, -61, -77, -128, 105, -80, 61, 127, -87, -10, 124, -93, 117, 11, 24, 53, 101, 102, -49, 110, 47, 107, -55, 0, 85, -10, -53, -95, -77, 66, -24, 47, -125, 6, 55, 100, 32, -12, 106, 4, -115, 108, 29, 4, 90, -63, -59, -91, -82, 23, 122, 6, 64, -59, -37, 66, -1, -57, -100, 38, 16, -44, 0, 125, -92, -87, 23, 108, 1, 125, -19, 126, 123, 16, 0, 125, -107, -70, 18, -58, -113, 52, 53, 64, -49, 0, -40, -5, 36, -12, -40, -93, -99, 2, 10, -46, 7, 40, -58, -107, -80, -94, -122, -67, 18, -58, 115, -82, -124, 21, -28, 74, 88, 81, 126, -39, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 117, 77, -45, -12, 13, 69, 87, -101, -1, -107, -118, 19, 57, 0, 0, 0, 0, 73, 69, 78, 68, -82, 66, 96, -126};
	
	static {
		try {
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(_background)) {
				BACKGROUND = ImageIO.read(inputStream);
				_background = null;
			}
			try (ByteArrayInputStream input = new ByteArrayInputStream(_backpack)) {
				BACKPACK = ImageIO.read(input);
				_backpack = null;
			}
			try (ByteArrayInputStream input = new ByteArrayInputStream(_input)) {
				INPUT = ImageIO.read(input);
				_input = null;
			}
			try (ByteArrayInputStream input = new ByteArrayInputStream(_output)) {
				OUTPUT = ImageIO.read(input);
				_output = null;
			}
			try (ByteArrayInputStream input = new ByteArrayInputStream(_progressBar)) {
				PROGRESS_BAR = ImageIO.read(input);
				_progressBar = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		/*
		try (InputStream input = new FileInputStream("C:\\Users\\EmptyDreams\\Desktop\\download\\0.png")) {
			byte[] b = new byte[input.available()];
			int len = input.read(b);
			if (len == b.length) {
				System.out.println("\n" + Arrays.toString(b));
			} else {
				byte[] bs = new byte[len];
				System.arraycopy(b, 0, bs, 0, len);
				System.out.println("\n" + Arrays.toString(bs));
			}
		} catch (IOException ignore) { }
		// */
	}
	
	public static void main(String[] args) {
	}
	
}
