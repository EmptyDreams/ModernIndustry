package minedreams.mi.tools;

import java.util.Collection;

import static minedreams.mi.api.net.WaitList.checkNull;

import minedreams.mi.api.electricity.ElectricityTransfer;

/**
 * 用于各个类的调试
 *
 * @author EmptyDreams
 * @version V1.0
 */
public final class DebugHelper {
	
	/**
	 * 检查数组中是否含有true
	 *
	 * @throws NullPointerException 如果bools==null
	 */
	public static boolean hasTrue(Collection<Boolean> bools) {
		checkNull(bools, "bools");
		
		for (boolean b : bools)
			if (b) return true;
		return false;
	}
	
	/**
	 * 检查数组中是否含有true
	 *
	 * @throws NullPointerException 如果bools==null
	 */
	public static boolean hasTrue(boolean... bools) {
		checkNull(bools, "bools");
		
		for (boolean b : bools)
			if (b) return true;
		return false;
	}
	
	/**
	 * 打印et中连接的方向
	 * @param et 要打印的对象
	 * @param isPrintFalse 是否打印没有连接的方向
	 *
	 * @throws NullPointerException 如果et==null
	 */
	public static void printETShow(ElectricityTransfer et, boolean isPrintFalse) {
		checkNull(et, "et");
		
		StringBuilder sb = new StringBuilder();
		if (isPrintFalse) {
			sb.append("up=").append(et.getUp()).append(';')
			  .append("down=").append(et.getDown()).append(';')
			  .append("east=").append(et.getEast()).append(';')
			  .append("west=").append(et.getEast()).append(';')
			  .append("north=").append(et.getNorth()).append(';')
			  .append("south=").append(et.getSouth()).append(';');
		} else {
			if (et.getUp()) sb.append("up=true;");
			if (et.getDown()) sb.append("down=true;");
			if (et.getEast()) sb.append("east=true;");
			if (et.getWest()) sb.append("west=true;");
			if (et.getNorth()) sb.append("north=true;");
			if (et.getSouth()) sb.append("south=true;");
		}
		MISysInfo.print(sb);
	}
	
}
