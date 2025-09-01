package zasyasolutions.SpaCover.Auth;
import io.restassured.response.Response;
import zasyasolutions.SpaCover.ConfigReader;

import static io.restassured.RestAssured.given;

public class AuthManager {
    
    private static String authToken = null;
    private static String refreshToken = null;
    private static long tokenExpiryTime = 0;
    
    /**
     * Login and get authentication token
     */
    public static String login(String email, String password) {
        String loginEndpoint = ConfigReader.getProperty("login.endpoint");
        
        String loginPayload = "{\n" +
                "  \"email\": \"" + email + "\",\n" +
                "  \"password\": \"" + password + "\"\n" +
                "}";
        
        Response response = given()
                .header("Content-Type", "application/json")
                .body(loginPayload)
                .when()
                .post(loginEndpoint);
        
        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
            // Extract token from response (adjust JSONPath based on your API response structure)
            authToken = response.jsonPath().getString("token");
            
            // Optional: Extract refresh token if available
            try {
                refreshToken = response.jsonPath().getString("refresh_token");
            } catch (Exception e) {
                // Refresh token might not be available
            }
            
            // Optional: Extract token expiry time if available
            try {
                int expiresIn = response.jsonPath().getInt("expires_in");
                tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000L);
            } catch (Exception e) {
                // Set default expiry time (1 hour)
                tokenExpiryTime = System.currentTimeMillis() + (3600 * 1000L);
            }
            
            System.out.println("Login successful. Token: " + authToken.substring(0, 10) + "...");
            return authToken;
        } else {
            throw new RuntimeException("Login failed with status code: " + response.getStatusCode() 
                    + ", Response: " + response.getBody().asString());
        }
    }
    
    /**
     * Login with email and password
     */
    public static String loginWithEmail(String email, String password) {
        String loginEndpoint = ConfigReader.getProperty("login.endpoint", "/auth/login/web");
        
        String loginPayload = "{\n" +
                "  \"email\": \"" + email + "\",\n" +
                "  \"password\": \"" + password + "\"\n" +
                "}";
        
        Response response = given()
                .header("Content-Type", "application/json")
                .body(loginPayload)
                .when()
                .post(loginEndpoint);
        
        if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
            authToken = response.jsonPath().getString("token");
            System.out.println("Login successful with email. Token obtained.");
            return authToken;
        } else {
            throw new RuntimeException("Email login failed with status code: " + response.getStatusCode());
        }
    }
    
    /**
     * Get current authentication token
     */
    public static String getAuthToken() {
        if (authToken == null || isTokenExpired()) {
            // Auto-login with default credentials if token is null or expired
            String defaultUsername = ConfigReader.getProperty("default.username", "");
            String defaultPassword = ConfigReader.getProperty("default.password", "");
            
            if (!defaultUsername.isEmpty() && !defaultPassword.isEmpty()) {
                System.out.println("Token expired or null. Auto-logging in...");
                return login(defaultUsername, defaultPassword);
            } else {
                throw new RuntimeException("No valid token available and no default credentials configured");
            }
        }
        return authToken;
    }
    
    /**
     * Set authentication token manually
     */
    public static void setAuthToken(String token) {
        authToken = token;
    }
    
    /**
     * Clear authentication token
     */
    public static void clearAuthToken() {
        authToken = null;
        refreshToken = null;
        tokenExpiryTime = 0;
    }
    
    /**
     * Check if token is expired
     */
    public static boolean isTokenExpired() {
        return System.currentTimeMillis() > tokenExpiryTime;
    }
    
    /**
     * Refresh authentication token
     */
    public static String refreshAuthToken() {
        if (refreshToken == null) {
            throw new RuntimeException("No refresh token available");
        }
        
        String refreshEndpoint = ConfigReader.getProperty("refresh.endpoint", "/auth/refresh");
        
        String refreshPayload = "{\n" +
                "  \"refresh_token\": \"" + refreshToken + "\"\n" +
                "}";
        
        Response response = given()
                .header("Content-Type", "application/json")
                .body(refreshPayload)
                .when()
                .post(refreshEndpoint);
        
        if (response.getStatusCode() == 200) {
            authToken = response.jsonPath().getString("token");
            System.out.println("Token refreshed successfully");
            return authToken;
        } else {
            throw new RuntimeException("Token refresh failed with status code: " + response.getStatusCode());
        }
    }
    
    /**
     * Get Bearer token format
     */
    public static String getBearerToken() {
        return "Bearer " + getAuthToken();
    }
    
    /**
     * Logout and clear token
     */
    public static void logout() {
        try {
            String logoutEndpoint = ConfigReader.getProperty("logout.endpoint", "/auth/logout");
            
            given()
                .header("Authorization", getBearerToken())
                .when()
                .post(logoutEndpoint);
            
            System.out.println("Logout successful");
        } catch (Exception e) {
            System.out.println("Logout request failed: " + e.getMessage());
        } finally {
            clearAuthToken();
        }
    }
}