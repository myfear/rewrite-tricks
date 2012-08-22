/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eisele.example.rewritetricks;

import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Response;

/**
 *
 * @author eiselem
 */
public class TricksProvider extends HttpConfigurationProvider {

    @Override
    public Configuration getConfiguration(final ServletContext context) {

        Configuration config = ConfigurationBuilder.begin()
                // Inbound Rule
                .defineRule()
                .when(Path.matches("/test").and(Direction.isInbound()))
                .perform(Forward.to("/test.xhtml")
                .and(Response.withOutputBufferedBy(new ZipOutputBuffer())
                .and(Response.addHeader("Content-Encoding", "gzip"))
                .and(Response.addHeader("Content-Type", "text/html"))))
                // refine welcome-page
                .defineRule()
                .when(Path.matches("/")).perform(Forward.to("/test.xhtml"));
                
                ;
               
        //.addRule(
        //Join.path("/test").to("/test.jsp")
        //.perform(Response.withOutputBufferedBy(new ZipOutputBuffer())));

        return config;

    }

    /**
     *
     * @return
     */
    @Override
    public int priority() {
        return 10;
    }
}
