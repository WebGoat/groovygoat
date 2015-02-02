/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/**
 *
 * @author rlawson
 */
public class TestThymeleaf {

    public TestThymeleaf() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() {
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setTemplateMode("XHTML");
        templateResolver.setPrefix("C:/code/groovygoat/webgoat_home/lesson_1/views/");
        templateResolver.setSuffix(".html");
        TemplateEngine tplEngine = new TemplateEngine();
        tplEngine.setTemplateResolver(templateResolver);
        Context context = new Context();

        PrintWriter writer = new PrintWriter(System.out);
        tplEngine.process("home", context, writer);

    }
}
