package ua.onlinecourses.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.onlinecourses.model.myModule;
import ua.onlinecourses.repository.ModuleRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class ModuleServlet extends BaseServlet {

    private ModuleRepository repository;

    @Override
    public void init() throws jakarta.servlet.ServletException {
        super.init();
        repository = RepositoryManager.getInstance().getModuleRepository();
        logger.log(Level.INFO, "ModuleServlet initialized with {0} modules", repository.size());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            String id = extractId(pathInfo);

            if (id == null || id.isEmpty()) {
                List<myModule> modules = repository.getAll();
                logger.log(Level.INFO, "GET all modules, count: {0}", modules.size());
                sendJsonResponse(response, HttpServletResponse.SC_OK, modules);
            } else {
                Optional<myModule> module = repository.findByIdentity(id);
                if (module.isPresent()) {
                    logger.log(Level.INFO, "GET module by id: {0}", id);
                    sendJsonResponse(response, HttpServletResponse.SC_OK, module.get());
                } else {
                    logger.log(Level.WARNING, "Module not found: {0}", id);
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Module not found: " + id);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing GET request: {0}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String body = readRequestBody(request);
            ModuleDTO dto = objectMapper.readValue(body, ModuleDTO.class);
            
            myModule module = new myModule(
                    dto.title,
                    dto.content
            );

            if (repository.add(module)) {
                RepositoryManager.getInstance().saveModulesToFile();
                logger.log(Level.INFO, "POST created module: {0}", module.title());
                sendJsonResponse(response, HttpServletResponse.SC_CREATED, module);
            } else {
                logger.log(Level.WARNING, "POST failed - module already exists: {0}", module.title());
                sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Module already exists: " + module.title());
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing POST request: {0}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String id = extractId(request.getPathInfo());
            if (id == null || id.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Module ID required in path");
                return;
            }

            Optional<myModule> existing = repository.findByIdentity(id);
            if (existing.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Module not found: " + id);
                return;
            }

            String body = readRequestBody(request);
            ModuleDTO dto = objectMapper.readValue(body, ModuleDTO.class);

            repository.remove(existing.get());

            myModule updated = new myModule(
                    dto.title != null ? dto.title : existing.get().title(),
                    dto.content != null ? dto.content : existing.get().content()
            );

            repository.add(updated);
            RepositoryManager.getInstance().saveModulesToFile();
            logger.log(Level.INFO, "PUT updated module: {0}", updated.title());
            sendJsonResponse(response, HttpServletResponse.SC_OK, updated);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing PUT request: {0}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String id = extractId(request.getPathInfo());
            if (id == null || id.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Module ID required in path");
                return;
            }

            if (repository.removeByIdentity(id)) {
                RepositoryManager.getInstance().saveModulesToFile();
                logger.log(Level.INFO, "DELETE removed module: {0}", id);
                java.util.Map<String, String> result = new java.util.HashMap<>();
                result.put("message", "Module deleted");
                sendJsonResponse(response, HttpServletResponse.SC_OK, result);
            } else {
                logger.log(Level.WARNING, "DELETE failed - module not found: {0}", id);
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Module not found: " + id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing DELETE request: {0}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public static class ModuleDTO {
        public String title;
        public String content;
    }
}
