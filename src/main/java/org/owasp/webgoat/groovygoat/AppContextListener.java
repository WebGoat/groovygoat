/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.owasp.webgoat.groovygoat;

import java.util.logging.Level;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.log4j.Logger;

/**
 * Web application lifecycle listener.
 *
 * @author rlawson
 */
@WebListener()
public class AppContextListener implements ServletContextListener {

    private static final Logger log = Logger.getLogger(AppContextListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.warn(String.format("*** {} starting up ***", sce.getServletContext().getContextPath()));
        App app = App.getInstance();
        try {
            app.start();
        } catch (Throwable ex) {
            java.util.logging.Logger.getLogger(AppContextListener.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.warn(String.format("*** {} shutting up ***", sce.getServletContext().getContextPath()));
        App app = App.getInstance();
        app.stop();
    }
}
