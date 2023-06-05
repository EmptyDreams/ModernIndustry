package top.kmar.mi.api.utils;

import org.apache.logging.log4j.LogManager;
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
    public static void print(Object object) {
        LOGGER.info(object);
    }

    /**
     * 输出
     */
    public static void print(Object... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : objects) {
            sb.append(obj.toString());
        }
        LOGGER.info(sb);
    }

    private static Logger getErrorLogger() {
        String name = Thread.currentThread().getStackTrace()[3].getClassName();
        try {
            return LogManager.getFormatterLogger(Class.forName(name));
        } catch (ClassNotFoundException e) {
            throw new AssertionError();
        }
    }

    /** 输出错误信息 */
    public static void err(Object object, Throwable throwable) {
        getErrorLogger().error(object, throwable);
    }

    /** 输出错误信息 */
    public static void err(Object object) {
        getErrorLogger().error(object);
    }

}