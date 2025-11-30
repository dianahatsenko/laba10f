package ua.onlinecourses.config;

public final class ConfigKeys {

    private ConfigKeys() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String DATA_PATH_BASE = "data.path.base";

    public static final String DATA_PATH_STUDENTS_JSON = "data.path.students.json";
    public static final String DATA_PATH_STUDENTS_YAML = "data.path.students.yaml";

    public static final String DATA_PATH_COURSES_JSON = "data.path.courses.json";
    public static final String DATA_PATH_COURSES_YAML = "data.path.courses.yaml";

    public static final String DATA_PATH_INSTRUCTORS_JSON = "data.path.instructors.json";
    public static final String DATA_PATH_INSTRUCTORS_YAML = "data.path.instructors.yaml";

    public static final String DATA_PATH_MODULES_JSON = "data.path.modules.json";
    public static final String DATA_PATH_MODULES_YAML = "data.path.modules.yaml";

    public static final String DATA_PATH_ASSIGNMENTS_JSON = "data.path.assignments.json";
    public static final String DATA_PATH_ASSIGNMENTS_YAML = "data.path.assignments.yaml";

    public static final String TEST_DATA_COUNT = "test.data.count";
}