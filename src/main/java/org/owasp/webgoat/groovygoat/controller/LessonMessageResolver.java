/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.owasp.webgoat.groovygoat.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import org.thymeleaf.Arguments;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;

/**
 *
 * @author rlawson
 */
public class LessonMessageResolver extends AbstractMessageResolver {

    private final File lessonFolder;
    private final ClassLoader loader;
    private static final String GROOVYGOAT_MESSAGES_NAME = "messages";

    private static final Object[] EMPTY_MESSAGE_PARAMETERS = new Object[0];

    public LessonMessageResolver(String lessonFolderPath) throws MalformedURLException {
        lessonFolder = new File(lessonFolderPath);
        URL[] urls = {lessonFolder.toURI().toURL()};
        System.out.println("Loading bundles from: " + urls);
        loader = new URLClassLoader(urls);
    }

    @Override
    public MessageResolution resolveMessage(Arguments arguments, String key, Object[] messageParameters) {
        // use standard resource bundle logic
        // this needs to be made reloadable so we never cache
        Locale locale = arguments.getContext().getLocale();
        System.out.println("Looking up message for " + key + " locale: " + locale);
        ResourceBundle rb = ResourceBundle.getBundle(GROOVYGOAT_MESSAGES_NAME, locale, loader);
        String messageValue = rb.getString(key);

        if (messageValue == null) {
            return null;
        }

        MessageFormat messageFormat = new MessageFormat(messageValue, locale);
        messageValue = messageFormat.format((messageParameters != null ? messageParameters : EMPTY_MESSAGE_PARAMETERS));
        return new MessageResolution(messageValue);

    }

}
