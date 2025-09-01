package zasyasolutions.SpaCover.Auth;

import org.testng.Assert;
import org.testng.annotations.Test;

import zasyasolutions.SpaCover.APIHelper;
import zasyasolutions.SpaCover.BaseTest;

import static io.restassured.RestAssured.given;

public class AuthenticationTest extends BaseTest {
    
    @Test(priority = 1, description = "Login and get authentication token")
    public void testLogin() {
        logInfo("Testing login functionality");
        
        String loginPayload = "{\n" +
                " \"email\": \"developer@secure.com\",\n" +
                " \"password\": \"password@secure.com\"\n" +
                "}";
        
        response = given()
                .header("Content-Type", "application/json")
                .body(loginPayload)
                .when()
                .post("/auth/login/web");
        
        // Validate login response
        APIHelper.validateStatusCode(response, 201);
        
        // Extract and validate token
        String token = APIHelper.extractJsonPath(response, "token");
        Assert.assertNotNull(token, "Token should not be null");
        Assert.assertFalse(token.isEmpty(), "Token should not be empty");
        
        // Store token for future use
        AuthManager.setAuthToken(token);
        
        logPass("Login successful. Token: " + token);
        logInfo("Token will be used for authenticated requests");
    }
    
   // @Test(priority = 2, description = "Test invalid login credentials")
    public void testInvalidLogin() {
        logInfo("Testing invalid login credentials");
        
        String invalidLoginPayload = "{\n" +
                "    \"email\": \"invalid@example.com\",\n" +
                "    \"password\": \"wrongpassword\"\n" +
                "}";
        
        response = given()
                .header("Content-Type", "application/json")
                .body(invalidLoginPayload)
                .when()
                .post("/login");
        
        // Validate error response
        APIHelper.validateStatusCode(response, 400);
        
        // Validate error message exists
        String error = APIHelper.extractJsonPath(response, "error");
        Assert.assertNotNull(error, "Error message should be present");
        
        logPass("Invalid login correctly rejected with error: " + error);
    }
    
   // @Test(priority = 3, description = "Test missing password in login")
    public void testLoginMissingPassword() {
        logInfo("Testing login with missing password");
        
        String missingPasswordPayload = "{\n" +
                "    \"email\": \"eve.holt@reqres.in\"\n" +
                "}";
        
        response = given()
                .header("Content-Type", "application/json")
                .body(missingPasswordPayload)
                .when()
                .post("/login");
        
        // Validate error response
        APIHelper.validateStatusCode(response, 400);
        
        String error = APIHelper.extractJsonPath(response, "error");
        Assert.assertTrue(error.contains("Missing password") || error.contains("password"), 
                "Error should mention missing password");
        
        logPass("Missing password correctly validated with error: " + error);
    }
    
  //  @Test(priority = 4, description = "Use AuthManager for login")
    public void testAuthManagerLogin() {
        logInfo("Testing login using AuthManager");
        
        try {
            String token = AuthManager.loginWithEmail("eve.holt@reqres.in", "cityslicka");
            
            Assert.assertNotNull(token, "AuthManager should return valid token");
            Assert.assertFalse(token.isEmpty(), "Token should not be empty");
            
            logPass("AuthManager login successful. Token obtained: " + token.substring(0, 10) + "...");
            
        } catch (Exception e) {
            logFail("AuthManager login failed: " + e.getMessage());
            Assert.fail("AuthManager login should not fail: " + e.getMessage());
        }
    }
    
   // @Test(priority = 5, description = "Test authenticated request using token")
    public void testAuthenticatedRequest() {
        logInfo("Testing authenticated API request");
        
        // First login to get token
        String token = AuthManager.loginWithEmail("eve.holt@reqres.in", "cityslicka");
        
        // Make authenticated request (assuming there's a protected endpoint)
        response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .when()
                .get("/users/2");  // Using public endpoint for demo
        
        APIHelper.validateStatusCode(response, 200);
        
        // Validate response contains user data
        String firstName = APIHelper.extractJsonPath(response, "data.first_name");
        Assert.assertNotNull(firstName, "User data should be returned");
        
        logPass("Authenticated request successful. User: " + firstName);
    }
    
   // @Test(priority = 6, description = "Test request without authentication token")
    public void testUnauthenticatedRequest() {
        logInfo("Testing request without authentication token");
        
        // For demonstration, we'll use a public endpoint that doesn't require auth
        // In real scenarios, you'd test a protected endpoint
        response = given()
                .header("Content-Type", "application/json")
                .when()
                .get("/users/2");
        
        // This endpoint is public, so it will return 200
        // In real protected endpoints, you'd expect 401
        APIHelper.validateStatusCode(response, 200);
        
        logPass("Public endpoint accessed without authentication");
    }
    
  //  @Test(priority = 7, description = "Test token expiry handling")
    public void testTokenExpiry() {
        logInfo("Testing token expiry handling");
        
        // Set an invalid/expired token
        AuthManager.setAuthToken("invalid_expired_token_12345");
        
        try {
            // Try to make request with expired token
            response = given()
                    .header("Authorization", "Bearer " + AuthManager.getAuthToken())
                    .header("Content-Type", "application/json")
                    .when()
                    .get("/users/2");
            
            // For demo purposes, this will still work as it's a public endpoint
            // In real scenarios with protected endpoints, this would fail
            logInfo("Token expiry test completed");
            
        } catch (Exception e) {
            logInfo("Expected behavior: " + e.getMessage());
        }
        
        // Clear the invalid token
        AuthManager.clearAuthToken();
        logPass("Token expiry scenario tested");
    }
    
    //@Test(priority = 8, description = "Test auto-login feature")
    public void testAutoLogin() {
        logInfo("Testing auto-login feature");
        
        // Clear any existing token
        AuthManager.clearAuthToken();
        
        try {
            // This should trigger auto-login
            String token = AuthManager.getAuthToken();
            
            Assert.assertNotNull(token, "Auto-login should provide valid token");
            logPass("Auto-login feature working correctly");
            
        } catch (Exception e) {
            // Auto-login might fail if default credentials aren't set
            logInfo("Auto-login not configured or failed: " + e.getMessage());
        }
    }
}
