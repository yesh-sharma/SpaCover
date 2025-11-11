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
    
    

    @DataProvider(name = "proData")
    public Object[][] provideProData() {
        // We won't bind SharedData.proNumber here (it may still be null)
        return new Object[][] {
            { "1a120f46-b08d-496f-8b92-6c98937c69a2", "1" },
            { "6d6321a0-dc63-47a9-b250-b7a15a0dd844", "1" },
            { "6d6321a0-dc63-47a9-b250-b7a15a0dd844", "1" },
        };
    }
    
    @DataProvider(name = "sku")
    public Object[][] skuData() {
        return new Object[][] {
            {"N4N4-87-M1-3132"},
           
        };
    }
    
    @DataProvider(name = "proDataForInbound")
    public Object[][] provideProDataForInbound() {
        // We won't bind SharedData.proNumber here (it may still be null)
        return new Object[][] {
            { "1c6411ac-13a2-48ea-b34f-ef5d1a749f47", "1" }     
        };
    }
    
    @DataProvider(name = "skuInbound")
    public Object[][] skuDataforInbound() {
        return new Object[][] {
            {"E4S4-95-M1-1104"},
           
        };
    }
    
    
    @DataProvider(name = "skuInventoryInboundCustom")
    public Object[][] skuDataforInventoryInboundCustom() {
        return new Object[][] {
            {"E4E4-117-M1-1244"},
           
        };
    }
    
    @DataProvider(name = "proDataForInboundInventoryCustom")
    public Object[][] provideProDataForInboundInventoryCustom() {
        // We won't bind SharedData.proNumber here (it may still be null)
        return new Object[][] {
            { "1a120f46-b08d-496f-8b92-6c98937c69a2", "1" },
            { "1c6411ac-13a2-48ea-b34f-ef5d1a749f47", "1" }
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
