package zasyasolutions.SpaCover;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;
import zasyasolutions.SpaCover.Auth.AuthManager;
import zasyasolutions.SpaCover.utils.ExtentReportManager;

public class BaseTest {
    
    public Response response;
    public RequestSpecification request;
    protected String authToken;
    
    @BeforeSuite
    public void beforeSuite() {
        System.out.println("=================================");
        System.out.println("  API TEST SUITE STARTING");
        System.out.println("=================================");
        
        // Initialize Extent Reports
        ExtentReportManager.createInstance();
    }
    
    @BeforeClass
    public void setup() {
        // Load configuration
        String baseUrl = ConfigReader.getProperty("base.url");
        RestAssured.baseURI = baseUrl;
        
        logInfo("Base URL configured: " + baseUrl);
        
        // Authenticate and get token
        try {
            String email = ConfigReader.getProperty("login.email");
            String password = ConfigReader.getProperty("login.password");
            
            authToken = AuthManager.login(email, password);
            logInfo("Authentication successful");
            logInfo("Token obtained: " + authToken.substring(0, Math.min(20, authToken.length())) + "...");
            
        } catch (Exception e) {
            logFail("Authentication failed: " + e.getMessage());
            throw new RuntimeException("Failed to authenticate: " + e.getMessage());
        }
        
        // Setup default request specification
        request = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + authToken);
        
        logInfo("Request specification configured");
    }
    
    @AfterClass
    public void tearDown() {
        logInfo("Test class completed");
    }
    
    @AfterSuite
    public void afterSuite() {
        System.out.println("=================================");
        System.out.println("  API TEST SUITE COMPLETED");
        System.out.println("=================================");
        
        // Flush reports
        ExtentReportManager.flush();
    }
    
    // Logging methods with Extent Reports integration
    protected void logInfo(String message) {
        System.out.println("[INFO] " + message);
        try {
            ExtentTest test = ExtentReportManager.getTest();
            if (test != null) {
                test.info(message);
            }
        } catch (Exception e) {
            // Silently ignore if test context not available
        }
    }
    
    protected void logPass(String message) {
        System.out.println("[PASS] " + message);
        try {
            ExtentTest test = ExtentReportManager.getTest();
            if (test != null) {
                test.pass(message);
            }
        } catch (Exception e) {
            // Silently ignore if test context not available
        }
    }
    
    protected void logFail(String message) {
        System.err.println("[FAIL] " + message);
        try {
            ExtentTest test = ExtentReportManager.getTest();
            if (test != null) {
                test.fail(message);
            }
        } catch (Exception e) {
            // Silently ignore if test context not available
        }
    }
    
    protected void logWarning(String message) {
        System.out.println("[WARNING] " + message);
        try {
            ExtentTest test = ExtentReportManager.getTest();
            if (test != null) {
                test.warning(message);
            }
        } catch (Exception e) {
            // Silently ignore if test context not available
        }
    }
    
    protected void logRequest(String endpoint, String method, Object requestBody) {
        try {
            ExtentTest test = ExtentReportManager.getTest();
            if (test != null) {
                test.info("<b>Endpoint:</b> " + method + " " + endpoint);
                if (requestBody != null) {
                    test.info("<b>Request Body:</b>");
                    test.info(MarkupHelper.createCodeBlock(requestBody.toString(), CodeLanguage.JSON));
                }
            }
        } catch (Exception e) {
            // Silently ignore if test context not available
        }
        System.out.println("[REQUEST] " + method + " " + endpoint);
    }
    
    protected void logResponse(Response response) {
        if (response == null) return;
        
        try {
            ExtentTest test = ExtentReportManager.getTest();
            if (test != null) {
                test.info("<b>Response Status Code:</b> " + response.getStatusCode());
                test.info("<b>Response Time:</b> " + response.getTime() + " ms");
                
                String contentType = response.getContentType();
                if (contentType != null && contentType.contains("json")) {
                    test.info("<b>Response Body:</b>");
                    test.info(MarkupHelper.createCodeBlock(
                        response.getBody().asPrettyString(), 
                        CodeLanguage.JSON
                    ));
                } else {
                    test.info("<b>Response Body:</b> " + response.getBody().asString());
                }
            }
        } catch (Exception e) {
            // Silently ignore if test context not available
        }
        
        System.out.println("[RESPONSE] Status: " + response.getStatusCode() + 
                         " | Time: " + response.getTime() + "ms");
    }
    
    protected void logStep(String stepDescription) {
        logInfo("STEP: " + stepDescription);
    }
}
