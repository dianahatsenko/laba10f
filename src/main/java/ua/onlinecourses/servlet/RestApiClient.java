package ua.onlinecourses.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestApiClient {

    private static final Logger logger = Logger.getLogger(RestApiClient.class.getName());
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public RestApiClient() {
        this(DEFAULT_BASE_URL);
    }

    public RestApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String get(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        logger.log(Level.INFO, "GET {0} - Status: {1}", new Object[]{endpoint, response.statusCode()});
        return response.body();
    }

    public String post(String endpoint, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        logger.log(Level.INFO, "POST {0} - Status: {1}", new Object[]{endpoint, response.statusCode()});
        return response.body();
    }

    public String put(String endpoint, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        logger.log(Level.INFO, "PUT {0} - Status: {1}", new Object[]{endpoint, response.statusCode()});
        return response.body();
    }

    public String delete(String endpoint) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        logger.log(Level.INFO, "DELETE {0} - Status: {1}", new Object[]{endpoint, response.statusCode()});
        return response.body();
    }

    public void printFormattedJson(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            String formatted = objectMapper.writeValueAsString(node);
            System.out.println(formatted);
        } catch (Exception e) {
            System.out.println(json);
        }
    }

    public static void main(String[] args) {
        RestApiClient client = new RestApiClient();

        System.out.println("=".repeat(60));
        System.out.println("REST API Client Demo");
        System.out.println("=".repeat(60));

        try {
            System.out.println("\n1. GET all students:");
            String students = client.get("/students");
            client.printFormattedJson(students);

            System.out.println("\n2. POST - Create new student:");
            String newStudent = String.format(
                    "{\"firstName\": \"Test\", \"lastName\": \"User\", \"email\": \"test.user@demo.com\", \"enrollmentDate\": \"%s\"}",
                    LocalDate.now().toString()
            );
            String createdStudent = client.post("/students", newStudent);
            client.printFormattedJson(createdStudent);

            System.out.println("\n3. GET student by email:");
            String student = client.get("/students/test.user@demo.com");
            client.printFormattedJson(student);

            System.out.println("\n4. PUT - Update student:");
            String updateStudent = "{\"firstName\": \"Updated\", \"lastName\": \"User\", \"email\": \"test.user@demo.com\", \"enrollmentDate\": \"" + LocalDate.now().toString() + "\"}";
            String updatedStudent = client.put("/students/test.user@demo.com", updateStudent);
            client.printFormattedJson(updatedStudent);

            System.out.println("\n5. DELETE student:");
            String deleteResult = client.delete("/students/test.user@demo.com");
            client.printFormattedJson(deleteResult);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("Courses CRUD Demo");
            System.out.println("=".repeat(60));

            System.out.println("\n6. GET all courses:");
            String courses = client.get("/courses");
            client.printFormattedJson(courses);

            System.out.println("\n7. POST - Create new course:");
            String newCourse = String.format(
                    "{\"title\": \"Demo Course\", \"description\": \"Demo Description\", \"credits\": 3, \"startDate\": \"%s\"}",
                    LocalDate.now().plusMonths(1).toString()
            );
            String createdCourse = client.post("/courses", newCourse);
            client.printFormattedJson(createdCourse);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("Instructors CRUD Demo");
            System.out.println("=".repeat(60));

            System.out.println("\n8. GET all instructors:");
            String instructors = client.get("/instructors");
            client.printFormattedJson(instructors);

            System.out.println("\n9. POST - Create new instructor:");
            String newInstructor = "{\"firstName\": \"Demo\", \"lastName\": \"Teacher\", \"expertise\": 10}";
            String createdInstructor = client.post("/instructors", newInstructor);
            client.printFormattedJson(createdInstructor);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("Modules CRUD Demo");
            System.out.println("=".repeat(60));

            System.out.println("\n10. GET all modules:");
            String modules = client.get("/modules");
            client.printFormattedJson(modules);

            System.out.println("\n11. POST - Create new module:");
            String newModule = "{\"title\": \"Demo Module\", \"content\": \"Demo module content for testing\"}";
            String createdModule = client.post("/modules", newModule);
            client.printFormattedJson(createdModule);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("REST API Client Demo Complete");
            System.out.println("=".repeat(60));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during API demo: {0}", e.getMessage());
            e.printStackTrace();
        }
    }
}
