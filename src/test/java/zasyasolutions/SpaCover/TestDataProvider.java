package zasyasolutions.SpaCover;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.DataProvider;


public class TestDataProvider {
    
    @DataProvider(name = "userTestData")
    public Object[][] getUserTestData() {
        return new Object[][] {
            {"John Doe", "john.doe@example.com", "password123"},
            {"Jane Smith", "jane.smith@example.com", "password456"},
            {"Bob Johnson", "bob.johnson@example.com", "password789"}
        };
    }
    
    @DataProvider(name = "skuData")
    public Object[][] provideSku() {

        // Create the map structure similar to your JSON
        Map<String, Object> skuData = new HashMap<>();
        skuData.put("sku", Arrays.asList(
                "E4E4-1",
                "E5E4-1",
                "E3E4-1",
                "E6E4-1",
                "E4E5-1",
                "E4E3-1",
                "E4E6-1",
                "E5E5-1",
                "E3E3-1",
                "E6E6-1",
                "E5E3-1",
                "E5E6-1",
                "E3E5-1",
                "E6E5-1"
        ));

        // Wrap in Object[][] because TestNG DataProvider requires that
        return new Object[][] {
            { skuData }
        };
    }

    
    
    
    
    @DataProvider(name = "invalidUserData")
    public Object[][] getInvalidUserData() {
        return new Object[][] {
            {"", "john.doe@example.com", "password123"},
            {"John Doe", "", "password123"},
            {"John Doe", "john.doe@example.com", ""},
            {"John Doe", "invalid-email", "password123"}
        };
    }
    
    @DataProvider(name = "statusCodes")
    public Object[][] getStatusCodes() {
        return new Object[][] {
            {200, "OK"},
            {201, "Created"},
            {400, "Bad Request"},
            {401, "Unauthorized"},
            {404, "Not Found"},
            {500, "Internal Server Error"}
        };
    }
    
    /**
     * Load test data from JSON file
     */
    public static Object[][] loadDataFromJson(String filePath) {
        String jsonData = JsonUtils.readJsonFromFile(filePath);
        // Parse JSON and return as Object[][]
        // Implementation depends on your JSON structure
        return new Object[][] {{jsonData}};
    }
}
