package zasyasolutions.SpaCover;
import com.aventstack.extentreports.ExtentTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import zasyasolutions.SpaCover.Auth.AuthManager;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.lang.reflect.Method;

public class BaseTest {
    
    protected static RequestSpecification request;
    protected static Response response;
    protected static ExtentTest test;
    protected static String authToken;
    
    @BeforeSuite
    public void setUp() {
        // Initialize ExtentReports
        ExtentManager.createInstance();
        // Create a setup test instance for suite-level logging
        test = ExtentManager.createTest("Suite Setup");
        // Set base URI from config
        RestAssured.baseURI = ConfigReader.getProperty("base.url");
       // RestAssured.port = Integer.parseInt(ConfigReader.getProperty("port", "80"));
        
        // Enable request and response logging
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
//       AuthenticationTest login = new   AuthenticationTest();
//       login.performLogin();
//        // Perform login once for the entire suite if auto-login is enabled
        if (ConfigReader.getBooleanProperty("auto.login")) {
            performLogin();
        }
    }
    
    @BeforeMethod
    public void beforeMethod(Method method) {
        // Create test instance for reporting
        test = ExtentManager.createTest(method.getName());
        
        // Initialize request specification
        request = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
        
        // Add auth token if available
        if (authToken != null) {
            addAuthToken(authToken);
        }
    }
//    
//    @AfterMethod
//    public void afterMethod() {
//        // Flush the extent reports
//        ExtentManager.getExtent().flush();
//    }
    
//    @AfterSuite
//    public void tearDown() {
//        // Logout if auto-logout is enabled
//        if (ConfigReader.getBooleanProperty("auto.logout")) {
//            AuthManager.logout();
//        }
//        
//        // Close extent reports
//        ExtentManager.getExtent().flush();
//    }
    
    /**
     * Perform login and store auth token
     */
    protected void performLogin() {
        try {
            String email = ConfigReader.getProperty("test.email");
            String password = ConfigReader.getProperty("test.password");
            
            logInfo("Performing login with username: " + email);
            authToken = AuthManager.login(email, password);
            logPass("Login successful. Token obtained.");
        } catch (Exception e) {
            logFail("Login failed: " + e.getMessage());
            throw new RuntimeException("Failed to login: " + e.getMessage());
        }
    }
    
    /**
     * Perform login with email
     */
    protected void performEmailLogin(String email, String password) {
        try {
            logInfo("Performing login with email: " + email);
            authToken = AuthManager.loginWithEmail(email, password);
            logPass("Email login successful. Token obtained.");
        } catch (Exception e) {
            logFail("Email login failed: " + e.getMessage());
            throw new RuntimeException("Failed to login with email: " + e.getMessage());
        }
    }
    
    /**
     * Add authentication token to request
     */
    protected void addAuthToken(String token) {
        request = request.header("Authorization", "Bearer " + token);
    }
    
    /**
     * Add authentication token from AuthManager
     */
    protected void addAuthFromManager() {
        String token = AuthManager.getAuthToken();
        addAuthToken(token);
    }
    
    /**
     * Get authenticated request specification
     */
    protected RequestSpecification getAuthenticatedRequest() {
        return request.header("Authorization", AuthManager.getBearerToken());
    }
    
    /**
     * Add custom header to request
     */
    protected void addHeader(String key, String value) {
        request = request.header(key, value);
    }
    
    /**
     * Refresh authentication token
     */
    protected void refreshToken() {
        try {
            logInfo("Refreshing authentication token");
            authToken = AuthManager.refreshAuthToken();
            logPass("Token refreshed successfully");
        } catch (Exception e) {
            logFail("Token refresh failed: " + e.getMessage());
            // Try re-login
            performLogin();
        }
    }
    
    /**
     * Log info to extent reports
     */
    protected void logInfo(String message) {
        test.info(message);
        System.out.println("INFO: " + message);
    }
    
    /**
     * Log pass to extent reports
     */
    protected void logPass(String message) {
        test.pass(message);
        System.out.println("PASS: " + message);
    }
    
    /**
     * Log fail to extent reports
     */
    protected void logFail(String message) {
        test.fail(message);
        System.out.println("FAIL: " + message);
    }
}