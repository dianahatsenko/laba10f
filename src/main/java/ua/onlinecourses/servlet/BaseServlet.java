package ua.onlinecourses.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class BaseServlet extends HttpServlet {

    protected static final Logger logger = Logger.getLogger(BaseServlet.class.getName());
    protected ObjectMapper objectMapper;
    protected int requestCount = 0;

    @Override
    public void init() throws ServletException {
        super.init();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        logger.log(Level.INFO, "{0} initialized", getServletName());
    }

    @Override
    public void destroy() {
        logger.log(Level.INFO, "{0} destroyed. Total requests processed: {1}",
                new Object[]{getServletName(), requestCount});
        super.destroy();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        requestCount++;
        String method = request.getMethod();
        String path = request.getPathInfo();
        logger.log(Level.INFO, "{0} - Request #{1}: {2} {3}",
                new Object[]{getServletName(), requestCount, method, path != null ? path : "/"});
        
        long startTime = System.currentTimeMillis();
        try {
            super.service(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.log(Level.INFO, "{0} - Response: status={1}, duration={2}ms",
                    new Object[]{getServletName(), response.getStatus(), duration});
        }
    }

    protected void sendJsonResponse(HttpServletResponse response, int status, Object data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        objectMapper.writeValue(writer, data);
    }

    protected void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        java.util.Map<String, String> errorMap = new java.util.HashMap<>();
        errorMap.put("error", message);
        objectMapper.writeValue(writer, errorMap);
    }

    protected String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    protected String extractId(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        String id = pathInfo.substring(1);
        int slashIndex = id.indexOf('/');
        if (slashIndex > 0) {
            id = id.substring(0, slashIndex);
        }
        return id;
    }
}
