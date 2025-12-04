package ua.onlinecourses.servlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WebServer {

    private static final Logger logger = Logger.getLogger(WebServer.class.getName());
    private static final int DEFAULT_PORT = 8080;

    private final Server server;
    private final int port;

    public WebServer() {
        this(DEFAULT_PORT);
    }

    public WebServer(int port) {
        this.port = port;
        this.server = new Server(port);
        configureServlets();
    }

    private void configureServlets() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/api");

        context.addServlet(new ServletHolder(new StudentServlet()), "/students/*");
        context.addServlet(new ServletHolder(new CourseServlet()), "/courses/*");
        context.addServlet(new ServletHolder(new InstructorServlet()), "/instructors/*");
        context.addServlet(new ServletHolder(new ModuleServlet()), "/modules/*");

        server.setHandler(context);
        logger.log(Level.INFO, "Servlets configured at /api context path");
    }

    public void start() throws Exception {
        logger.log(Level.INFO, "Starting web server on port {0}...", port);
        server.start();
        logger.log(Level.INFO, "Web server started successfully");
        logger.log(Level.INFO, "REST API endpoints:");
        logger.log(Level.INFO, "  Students:    http://localhost:{0}/api/students", port);
        logger.log(Level.INFO, "  Courses:     http://localhost:{0}/api/courses", port);
        logger.log(Level.INFO, "  Instructors: http://localhost:{0}/api/instructors", port);
        logger.log(Level.INFO, "  Modules:     http://localhost:{0}/api/modules", port);
    }

    public void stop() throws Exception {
        logger.log(Level.INFO, "Stopping web server...");
        server.stop();
        logger.log(Level.INFO, "Web server stopped");
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public boolean isRunning() {
        return server.isRunning();
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Invalid port number: {0}, using default: {1}",
                        new Object[]{args[0], DEFAULT_PORT});
            }
        }

        WebServer webServer = new WebServer(port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                webServer.stop();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error stopping server: {0}", e.getMessage());
            }
        }));

        try {
            webServer.start();
            webServer.join();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting server: {0}", e.getMessage());
            System.exit(1);
        }
    }
}
