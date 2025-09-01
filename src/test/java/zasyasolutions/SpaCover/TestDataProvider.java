package zasyasolutions.SpaCover;

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
    	
    	return new Object[][] {
    		//{"E2E2-115-M1-1104"}
    		{"S8-3605-M1-1239"}
    		
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
