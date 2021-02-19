package com.looseboxes.service.auth.ext.lifecycle.startup;

/**
 * @author hp
 */
public class StartupException extends RuntimeException {

    public static StartupException notFound(Class type) {
        return notFound(type.getSimpleName());
    }
    
    public static StartupException notFound(String name) {
        return new StartupException("Required " + name + " not found");
    }

    /**
     * Creates a new instance of <code>StartupException</code> without detail
     * message.
     */
    public StartupException() {
    }

    /**
     * Constructs an instance of <code>StartupException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public StartupException(String msg) {
        super(msg);
    }

    public StartupException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }
}
