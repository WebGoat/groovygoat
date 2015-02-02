/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.owasp.webgoat.groovygoat.controller;

import groovy.lang.Closure;
import groovy.servlet.AbstractHttpServlet;
import groovy.servlet.ServletBinding;
import groovy.servlet.ServletCategory;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/**
 *
 * @author rlawson
 */
@WebServlet(name = "LessonServlet", urlPatterns = {"/lesson"})
public class LessonServlet extends AbstractHttpServlet {

    //@TODO move this stuff to pciking up from environment variable after lesson loader is working
    private static final String GROOVYGOAT_HOME = "C:/code/groovygoat/webgoat_home";
    private static final String LESSON_1 = "lesson_1";

    /**
     * The script engine executing the Groovy scripts for this servlet
     */
    private GroovyScriptEngine gse;
    private TemplateEngine templateEngine;
    FileTemplateResolver templateResolver;

    // @TODO we need a unique template resolver and engine per lesson so they don't interfere with each other
    private void initializeTemplateEngine() throws IOException {

        templateResolver = new FileTemplateResolver();
        File home = new File(GROOVYGOAT_HOME);
        File lesson = new File(home, LESSON_1);
        File views = new File(lesson, "views");
        File i18n = new File(lesson, "i18n");
        templateResolver.setPrefix(views.getCanonicalPath() + "/");
        templateResolver.setSuffix(".html");
        // XHTML is the default mode, but we set it anyway for better understanding of code
        templateResolver.setTemplateMode("XHTML");
        // Template cache TTL=1h. If not set, entries would be cached until expelled by LRU
        templateResolver.setCacheable(false);
        //templateResolver.setCacheTTLMs(3600000L);

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        LessonMessageResolver lmr = new LessonMessageResolver(i18n.getCanonicalPath());
        templateEngine.setMessageResolver(lmr);

    }

    /**
     * Initialize the GroovyServlet.
     *
     * @throws ServletException if this method encountered difficulties
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Set up the scripting engine
        try {
            gse = createGroovyScriptEngine();
            initializeTemplateEngine();
        } catch (IOException ioe) {

        }
        servletContext.log("Groovy servlet initialized on " + gse + ".");
    }

    /**
     * Handle web requests to the GroovyServlet
     *
     * @param request
     * @param response
     * @throws java.io.IOException
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

        
        //@TODO we need to pull the lesson and the controller from the URL
        final String scriptUri = "controllers/out.groovy";

        // Set it to HTML by default
        response.setContentType("text/html; charset=" + encoding);

        // Set up the script context
        final ServletBinding binding = new ServletBinding(request, response, servletContext);
        setVariables(binding);

        // Run the script
        try {
            Closure closure = new Closure(gse) {

                public Object call() {
                    try {
                        return ((GroovyScriptEngine) getDelegate()).run(scriptUri, binding);
                    } catch (ResourceException e) {
                        throw new RuntimeException(e);
                    } catch (ScriptException e) {
                        throw new RuntimeException(e);
                    }
                }

            };
            GroovyCategorySupport.use(ServletCategory.class, closure);
        } catch (RuntimeException runtimeException) {
            StringBuilder error = new StringBuilder("GroovyServlet Error: ");
            error.append(" script: '");
            error.append(scriptUri);
            error.append("': ");
            Throwable e = runtimeException.getCause();
            /*
             * Null cause?!
             */
            if (e == null) {
                error.append(" Script processing failed.\n");
                error.append(runtimeException.getMessage());
                if (runtimeException.getStackTrace().length > 0) {
                    error.append(runtimeException.getStackTrace()[0].toString());
                }
                servletContext.log(error.toString());
                System.err.println(error.toString());
                runtimeException.printStackTrace(System.err);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, error.toString());
                return;
            }
            /*
             * Resource not found.
             */
            if (e instanceof ResourceException) {
                error.append(" Script not found, sending 404.");
                servletContext.log(error.toString());
                System.err.println(error.toString());
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            /*
             * Other internal error. Perhaps syntax?!
             */
            servletContext.log("An error occurred processing the request", runtimeException);
            error.append(e.getMessage());
            if (e.getStackTrace().length > 0) {
                error.append(e.getStackTrace()[0].toString());
            }
            servletContext.log(e.toString());
            System.err.println(e.toString());
            runtimeException.printStackTrace(System.err);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString());
        }
        // check if view is bound
        String view = binding.getVariable("view") + "";
        System.out.println(view);

        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        templateEngine.process(view, ctx, response.getWriter());
    }

    /**
     * Hook method to setup the GroovyScriptEngine to use.<br>
     * Subclasses may override this method to provide a custom engine.
     *
     * @return Groovy Script Engine
     */
    // @TODO this needs a different script engine per lesson 
    // so they don't interfere with each other
    protected GroovyScriptEngine createGroovyScriptEngine() throws IOException {
        File home = new File(GROOVYGOAT_HOME);
        File lesson = new File(home, LESSON_1);
        return new GroovyScriptEngine(lesson.getCanonicalPath());
    }
}
