package org.exam.dorisPlugin;

import java.util.logging.Logger;

public final class Debug {
    private static Logger logger;

    private Debug() {} // 인스턴스 생성 방지

    public static void init(Logger pluginLogger) {
        logger = pluginLogger;
    }

    public static void log(String msg) {
        if (logger != null) {
            logger.info(msg);
        }
    }
    public static void warn(String msg) {
        if (logger != null) {
            logger.warning(msg);
        }
    }
    public static void error(String msg) {
        if (logger != null) {
            logger.severe(msg);
        }
    }
    public static void log(char c){
        log(String.valueOf(c));
    }
    public static void log(int i){
        log(String.valueOf(i));
    }
}
