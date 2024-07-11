package ee.omnifish.logbookexample;

import static jakarta.servlet.DispatcherType.ASYNC;
import static jakarta.servlet.DispatcherType.REQUEST;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.EnumSet;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.servlet.LogbookFilter;

/**
 * Configures and registers the Logbook Servlet filter.
 *
 * This is a Servlet listener, which creates, configures and registers LogbookFilter to the web app.
 */
@WebListener
public class LogbookConfiguration implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final ServletContext context = sce.getServletContext();
        Logbook logbook = Logbook.create();
        context.addFilter("LogbookFilter", new LogbookFilter(logbook))
                .addMappingForUrlPatterns(EnumSet.of(REQUEST, ASYNC), true, "/*");
    }

}
