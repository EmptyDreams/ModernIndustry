package top.kmar.mi.api.utils;

import org.apache.logging.log4j.Logger;

/**
 * 此类用于输出mod发出的系统信息
 * @author EmptyDremas
 */
public final class MISysInfo {

    public static Logger LOGGER;

    /**
     * 输出
     */
    public static void print(Object... objects) {
        LOGGER.info(linkObjects(objects));
    }

    /** 输出错误信息 */
    public static void err(Object object, Throwable throwable) {
        LOGGER.error(object, throwable);
    }

    /** 输出错误信息 */
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