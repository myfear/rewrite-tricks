/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.eisele.example.rewritetricks;

import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Constraint;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Redirect;
import org.ocpsoft.rewrite.servlet.config.Response;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 *
 * @author eiselem
 */
public class TricksProvider extends HttpConfigurationProvider {

    Constraint<String> selectedCharacters = new Constraint<String>() {
        @Override
        public boolean isSatisfiedBy(Rewrite event,
                EvaluationContext context, String value) {
            return value.matches("[a-zA-Z/]+");
        }
    };

    @Override
    public Configuration getConfiguration(final ServletContext context) {

        Configuration config = ConfigurationBuilder.begin()
                // malicious attacks prevention selectedCharacters for folders and redirection to index.html
                .defineRule()
                .when(Direction.isInbound()
                .and(Path.matches("{path}").where("path").matches("^(.+)/$")
                .and(Path.captureIn("checkChar").where("checkChar").constrainedBy(selectedCharacters))))
                .perform(Redirect.permanent(context.getContextPath() + "{path}index.html"))
                // redirect the / to index.html
                .addRule(Join.path("/").to("/index.html"))
                // GZIP compression test
                .defineRule()
                .when(Path.matches("/gziptest").and(Direction.isInbound()))
                .perform(Forward.to("test.xhtml")
                .and(Response.withOutputBufferedBy(new ZipOutputBuffer())
                .and(Response.addHeader("Content-Encoding", "gzip"))
                .and(Response.addHeader("Content-Type", "text/html"))))
                // outbound rule for pages with test.xhtml to test.html
                .defineRule()
                .when(Path.matches("test.xhtml").and(Direction.isOutbound()))
                .perform(Substitute.with("test.html"))
                //save rewriting all urls not matching [a-zA-Z/]+ aren't forwarded to FacesServlet
                // only inbound
                .defineRule()
                .when(Direction.isInbound().and(Path.matches("{name}.html").where("name").matches("[a-zA-Z/]+")))
                .perform(Forward.to("{name}.xhtml"));


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
