package ua.onlinecourses.serializer;

import ua.onlinecourses.exception.DataSerializationException;
import java.util.List;

public interface DataSerializer<T> {

    void serialize(List<T> items, String filePath) throws DataSerializationException;

    List<T> deserialize(String filePath, Class<T> clazz) throws DataSerializationException;

    String getFormat();
}