package ua.onlinecourses.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.onlinecourses.model.Instructor;
import ua.onlinecourses.repository.InstructorRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class InstructorServlet extends BaseServlet {

    private InstructorRepository repository;

    @Override
    public void init() throws jakarta.servlet.ServletException {
        super.init();
        repository = RepositoryManager.getInstance().getInstructorRepository();
        logger.log(Level.INFO, "InstructorServlet initialized with {0} instructors", repository.size());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            String id = extractId(pathInfo);

            if (id == null || id.isEmpty()) {
                List<Instructor> instructors = repository.getAll();
                logger.log(Level.INFO, "GET all instructors, count: {0}", instructors.size());
                sendJsonResponse(response, HttpServletResponse.SC_OK, instructors);
            } else {
                Optional<Instructor> instructor = repository.findByIdentity(id);
                if (instructor.isPresent()) {
                    logger.log(Level.INFO, "GET instructor by id: {0}", id);
                    sendJsonResponse(response, HttpServletResponse.SC_OK, instructor.get());
                } else {
                    logger.log(Level.WARNING, "Instructor not found: {0}", id);
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Instructor not found: " + id);
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
            InstructorDTO dto = objectMapper.readValue(body, InstructorDTO.class);
            
            Instructor instructor = new Instructor(
                    dto.firstName,
                    dto.lastName,
                    dto.expertise != null ? dto.expertise : 1
            );

            if (repository.add(instructor)) {
                RepositoryManager.getInstance().saveInstructorsToFile();
                logger.log(Level.INFO, "POST created instructor: {0} {1}",
                        new Object[]{instructor.firstName(), instructor.lastName()});
                sendJsonResponse(response, HttpServletResponse.SC_CREATED, instructor);
            } else {
                logger.log(Level.WARNING, "POST failed - instructor already exists: {0} {1}",
                        new Object[]{instructor.firstName(), instructor.lastName()});
                sendErrorResponse(response, HttpServletResponse.SC_CONFLICT,
                        "Instructor already exists: " + instructor.firstName() + " " + instructor.lastName());
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
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Instructor ID required in path");
                return;
            }

            Optional<Instructor> existing = repository.findByIdentity(id);
            if (existing.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Instructor not found: " + id);
                return;
            }

            String body = readRequestBody(request);
            InstructorDTO dto = objectMapper.readValue(body, InstructorDTO.class);

            repository.remove(existing.get());

            Instructor updated = new Instructor(
                    dto.firstName != null ? dto.firstName : existing.get().firstName(),
                    dto.lastName != null ? dto.lastName : existing.get().lastName(),
                    dto.expertise != null ? dto.expertise : existing.get().expertise()
            );

            repository.add(updated);
            RepositoryManager.getInstance().saveInstructorsToFile();
            logger.log(Level.INFO, "PUT updated instructor: {0} {1}",
                    new Object[]{updated.firstName(), updated.lastName()});
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
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Instructor ID required in path");
                return;
            }

            if (repository.removeByIdentity(id)) {
                RepositoryManager.getInstance().saveInstructorsToFile();
                logger.log(Level.INFO, "DELETE removed instructor: {0}", id);
                java.util.Map<String, String> result = new java.util.HashMap<>();
                result.put("message", "Instructor deleted");
                sendJsonResponse(response, HttpServletResponse.SC_OK, result);
            } else {
                logger.log(Level.WARNING, "DELETE failed - instructor not found: {0}", id);
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Instructor not found: " + id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing DELETE request: {0}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public static class InstructorDTO {
        public String firstName;
        public String lastName;
        public Integer expertise;
    }
}
