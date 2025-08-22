package zasyasolutions.SpaCover;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Read JSON from file
     */
    public static String readJsonFromFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }
    
    /**
     * Convert object to JSON string
     */
    public static String convertObjectToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }
    
    /**
     * Convert JSON string to object
     */
    public static <T> T convertJsonToObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JSON to object", e);
        }
    }
    
    /**
     * Update JSON field value
     */
    public static String updateJsonField(String json, String fieldName, Object newValue) {
        try {
            ObjectNode objectNode = (ObjectNode) objectMapper.readTree(json);
            if (newValue instanceof String) {
                objectNode.put(fieldName, (String) newValue);
            } else if (newValue instanceof Integer) {
                objectNode.put(fieldName, (Integer) newValue);
            } else if (newValue instanceof Boolean) {
                objectNode.put(fieldName, (Boolean) newValue);
            } else if (newValue instanceof Double) {
                objectNode.put(fieldName, (Double) newValue);
            }
            return objectMapper.writeValueAsString(objectNode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update JSON field", e);
        }
    }
    
    /**
     * Validate if string is valid JSON
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Pretty print JSON
     */
    public static String prettyPrintJson(String json) {
        try {
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Failed to pretty print JSON", e);
        }
    }
}