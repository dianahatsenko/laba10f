package ua.onlinecourses.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.onlinecourses.model.Course;
import ua.onlinecourses.repository.CourseRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class CourseServlet extends BaseServlet {

    private CourseRepository repository;

    @Override
    public void init() throws jakarta.servlet.ServletException {
        super.init();
        repository = RepositoryManager.getInstance().getCourseRepository();
        logger.log(Level.INFO, "CourseServlet initialized with {0} courses", repository.size());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            String id = extractId(pathInfo);

            if (id == null || id.isEmpty()) {
                List<Course> courses = repository.getAll();
                logger.log(Level.INFO, "GET all courses, count: {0}", courses.size());
                sendJsonResponse(response, HttpServletResponse.SC_OK, courses);
            } else {
                Optional<Course> course = repository.findByIdentity(id);
                if (course.isPresent()) {
                    logger.log(Level.INFO, "GET course by id: {0}", id);
                    sendJsonResponse(response, HttpServletResponse.SC_OK, course.get());
                } else {
                    logger.log(Level.WARNING, "Course not found: {0}", id);
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Course not found: " + id);
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
            CourseDTO dto = objectMapper.readValue(body, CourseDTO.class);
            
            Course course = new Course(
                    dto.title,
                    dto.description,
                    dto.credits != null ? dto.credits : 1,
                    dto.startDate != null ? LocalDate.parse(dto.startDate) : LocalDate.now().plusMonths(1)
            );

            if (repository.add(course)) {
                RepositoryManager.getInstance().saveCoursesToFile();
                logger.log(Level.INFO, "POST created course: {0}", course.title());
                sendJsonResponse(response, HttpServletResponse.SC_CREATED, course);
            } else {
                logger.log(Level.WARNING, "POST failed - course already exists: {0}", course.title());
                sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Course already exists: " + course.title());
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
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Course ID required in path");
                return;
            }

            Optional<Course> existing = repository.findByIdentity(id);
            if (existing.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Course not found: " + id);
                return;
            }

            String body = readRequestBody(request);
            CourseDTO dto = objectMapper.readValue(body, CourseDTO.class);

            repository.remove(existing.get());

            Course updated = new Course(
                    dto.title != null ? dto.title : existing.get().title(),
                    dto.description != null ? dto.description : existing.get().description(),
                    dto.credits != null ? dto.credits : existing.get().credits(),
                    dto.startDate != null ? LocalDate.parse(dto.startDate) : existing.get().startDate()
            );

            repository.add(updated);
            RepositoryManager.getInstance().saveCoursesToFile();
            logger.log(Level.INFO, "PUT updated course: {0}", updated.title());
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
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Course ID required in path");
                return;
            }

            if (repository.removeByIdentity(id)) {
                RepositoryManager.getInstance().saveCoursesToFile();
                logger.log(Level.INFO, "DELETE removed course: {0}", id);
                java.util.Map<String, String> result = new java.util.HashMap<>();
                result.put("message", "Course deleted");
                sendJsonResponse(response, HttpServletResponse.SC_OK, result);
            } else {
                logger.log(Level.WARNING, "DELETE failed - course not found: {0}", id);
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Course not found: " + id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing DELETE request: {0}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public static class CourseDTO {
        public String title;
        public String description;
        public Integer credits;
        public String startDate;
    }
}
