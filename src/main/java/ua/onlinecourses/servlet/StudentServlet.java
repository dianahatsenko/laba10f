package ua.onlinecourses.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ua.onlinecourses.model.Student;
import ua.onlinecourses.repository.StudentRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

public class StudentServlet extends BaseServlet {

    private StudentRepository repository;

    @Override
    public void init() throws jakarta.servlet.ServletException {
        super.init();
        repository = RepositoryManager.getInstance().getStudentRepository();
        logger.log(Level.INFO, "StudentServlet initialized with {0} students", repository.size());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            String id = extractId(pathInfo);

            if (id == null || id.isEmpty()) {
                List<Student> students = repository.getAll();
                logger.log(Level.INFO, "GET all students, count: {0}", students.size());
                sendJsonResponse(response, HttpServletResponse.SC_OK, students);
            } else {
                Optional<Student> student = repository.findByIdentity(id);
                if (student.isPresent()) {
                    logger.log(Level.INFO, "GET student by email: {0}", id);
                    sendJsonResponse(response, HttpServletResponse.SC_OK, student.get());
                } else {
                    logger.log(Level.WARNING, "Student not found: {0}", id);
                    sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Student not found: " + id);
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
            StudentDTO dto = objectMapper.readValue(body, StudentDTO.class);
            
            Student student = new Student(
                    dto.firstName,
                    dto.lastName,
                    dto.email,
                    dto.enrollmentDate != null ? LocalDate.parse(dto.enrollmentDate) : LocalDate.now()
            );

            if (repository.add(student)) {
                RepositoryManager.getInstance().saveStudentsToFile();
                logger.log(Level.INFO, "POST created student: {0}", student.email());
                sendJsonResponse(response, HttpServletResponse.SC_CREATED, student);
            } else {
                logger.log(Level.WARNING, "POST failed - student already exists: {0}", student.email());
                sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, "Student already exists: " + student.email());
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
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Email required in path");
                return;
            }

            Optional<Student> existing = repository.findByIdentity(id);
            if (existing.isEmpty()) {
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Student not found: " + id);
                return;
            }

            String body = readRequestBody(request);
            StudentDTO dto = objectMapper.readValue(body, StudentDTO.class);

            repository.remove(existing.get());

            Student updated = new Student(
                    dto.firstName != null ? dto.firstName : existing.get().firstName(),
                    dto.lastName != null ? dto.lastName : existing.get().lastName(),
                    dto.email != null ? dto.email : existing.get().email(),
                    dto.enrollmentDate != null ? LocalDate.parse(dto.enrollmentDate) : existing.get().enrollmentDate()
            );

            repository.add(updated);
            RepositoryManager.getInstance().saveStudentsToFile();
            logger.log(Level.INFO, "PUT updated student: {0}", updated.email());
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
                sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Email required in path");
                return;
            }

            if (repository.removeByIdentity(id)) {
                RepositoryManager.getInstance().saveStudentsToFile();
                logger.log(Level.INFO, "DELETE removed student: {0}", id);
                java.util.Map<String, String> result = new java.util.HashMap<>();
                result.put("message", "Student deleted");
                sendJsonResponse(response, HttpServletResponse.SC_OK, result);
            } else {
                logger.log(Level.WARNING, "DELETE failed - student not found: {0}", id);
                sendErrorResponse(response, HttpServletResponse.SC_NOT_FOUND, "Student not found: " + id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error processing DELETE request: {0}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public static class StudentDTO {
        public String firstName;
        public String lastName;
        public String email;
        public String enrollmentDate;
    }
}
