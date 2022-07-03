package at.kurumi;

import jakarta.enterprise.context.Dependent;

import java.util.logging.Logger;

/**
 * Tiny class so I can inject Loggers.
 */
@Dependent
public class LoggerFacade {

    private final Logger logger;

    private LoggerFacade(String name) {
        logger = Logger.getLogger(name);
    }

    public static LoggerFacade getLogger(String name) {
        return new LoggerFacade(name);
    }

    public static LoggerFacade getLogger(Class<?> name) {
        return getLogger(name.getName());
    }

    public void info(String s) {
        logger.info(s);
    }

    public void info(String s, Object... params) {
        logger.info(String.format(s, params));
    }

    public void warn(String s) {
        logger.warning(s);
    }

    public void warn(String s, Object... params) {
        logger.warning(String.format(s, params));
    }

    public void error(String s) {
        logger.severe(s);
    }

    public void error(String s, Object... params) {
        logger.severe(String.format(s, params));
    }

    public void debug(String s) {
        logger.fine(s);
    }

    public void debug(String s, Object... params) {
        logger.fine(String.format(s, params));
    }

    public void trace(String s) {
        logger.finer(s);
    }

    public void trace(String s, Object... params) {
        logger.finer(String.format(s, params));
    }


}