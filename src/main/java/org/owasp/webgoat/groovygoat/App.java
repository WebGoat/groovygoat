/*
 * 
 * 
 */
package org.owasp.webgoat.groovygoat;

import org.apache.log4j.Logger;

/**
 * Singleton class that represents the Application
 *
 * @author rlawson
 */
public class App {

    private static final Logger log = Logger.getLogger(App.class.getName());
    private static App instance = new App();

    private App() {
    }

    public static synchronized App getInstance() {
        return instance;
    }

    public synchronized void start() throws Throwable {
        try {
            log.warn("");
        } catch (Throwable e) {
            log.error(e);
            throw e;
        }
    }

    public synchronized void stop() {
        try {

        } catch (Throwable e) {
            log.error(e);
        }
    }

}
