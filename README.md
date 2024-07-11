# Example Logbook application on GlassFish 7

This example application will log information about HTTP requests and responses into the log file `requests.log` in the `log` directory in the GlassFish domain.

## Logbook configuration

### Maven dependencies in pom.xml

* `logbook-core` - core Loogbook functionality
* `logbook-servlet` - Jakarta Servlet HTTP filter

### Programmatic configuration

Logbook is configured and registered as a Jakarta Servlet Filter in `LogbookConfiguration.java`. This is `ServletContext` listener (it's annotated with `@WebListener` and implements `ServletContextListener`). The method `contextInitialized` is called right after the servlet context for the application is initialized. It provides a programmatic way to configure the servlet container instead of using `web.xml` declaratively. Registering Logbook this way provides more flexibility to configure Logbook used by the Servlet Filter.

## Logging configuration

Logbook logs information using the slf4j logging facade. A logging implementation like log4j is needed to customize logging.

The name of the logger is always `org.zalando.logbook.Logbook`. This applies even if the log messages are logged from the Servlet Filter. It means that the logger's name is not derived from the name of the class that produces the messages.

The level of the logged messages is always `TRACE`.

### Maven dependencies

* log4j-slf4j2-impl

### Log4j2 configuration

In this example, the configuration is provided via the `log4j2.xml` file in the application (in the classpath).

This file configures the logger `org.zalando.logbook.Logbook` to log messages up to the `trace` level. The messages are logged via the `httpLog` appender into the `requests.log` file.

### JDK logging configuration

Instead of using log4j, it's possible to use JDK logging and control the level of the Logbook logger from GlassFish. It's possible to log the Logbook messages into a separate file.

### Maven dependencies

* `slf4j-jdk14` (in this example it's commented out in `pom.xml`)

### Configure the log level

With the `slf4j-jdk14` dependency, it's enough to set the log level of the `org.zalando.logbook.Logbook` logger to `FINEST` (which equals to the `TRACE` level of slf4j). This can be done at runtime, using Admin Console or Asadmin CLI with the `set-log-levels` command, without restart.

With this, Logbook messages will be logged into the main `server.log` file. To log into a different file, continue to the next section.

### Log into a custom file

To log into a different file than `server.log`, you can edit the `logging.properties` file in the `config` directory in the domain directory.

Add the following lines into the file:

```
org.zalando.logbook.Logbook.useParentHandlers=false
org.zalando.logbook.Logbook.handlers=java.util.logging.FileHandler

# FileHandler configuration
java.util.logging.FileHandler.level=ALL
java.util.logging.FileHandler.pattern=${com.sun.aas.instanceRoot}/logs/requests_%g.log
java.util.logging.FileHandler.limit=10000000
java.util.logging.FileHandler.count=10
java.util.logging.FileHandler.formatter=java.util.logging.SimpleFormatter
java.util.logging.SimpleFormatter.format=%1$tY-%1$tm-%1$tdT%1$tH:%1$tM:%1$tS.%1$tL%1$tz\t%4$s\t%5$s%n
```

Explanation:

The first 2 lines will configure the Logbook logger to log using the FileHandler and not use any standard GlassFish log handlers.

The FileHandler configuration does the following:

* log into the logs directory in the domain, into files that start with requests_ and contain an index for file rolling. When the log file grows to 20 MB, it's rolled to a new index
* Max 10 rolled log files are kept
* The format of the log file will be similar to the default log4j RollingFile format. You can also provide the format using the system property `java.util.logging.SimpleFormatter.format`, for example by setting the system property in the GlassFish configuration or for a specic server instance. You can find mor e info about the pattern definition in the Java documentation: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Formatter.html#conversions-heading 

