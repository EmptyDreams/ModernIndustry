package minedreams.mi.tools;

import org.apache.logging.log4j.Logger;

/**
 * 此类用于输出mod发出的系统信息
 * @author EmptyDremas
 * @version V1.0
 */
public final class MISysInfo {

	public static Logger LOGGER;
	
	/**
	 * 输出
	 */
	public static void print(Object... objects) {
		LOGGER.info(linkObjects(objects));
	}
	
	public static void err(Object... objects) {
		LOGGER.error(linkObjects(objects));
	}
	
	public static void err(Object object) {
		LOGGER.error(object);
	}
	
	/**
	 * 输出
	 */
	public static void print(Object object) {
		LOGGER.info(object);
	}

	public static String linkObjects(Object... objects) {
		StringBuilder sb = new StringBuilder();
		for (Object o : objects)
			sb.append(o);
		return sb.toString();
	}
	
}
