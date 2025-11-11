package zasyasolutions.SpaCover.Auth;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import zasyasolutions.SpaCover.ConfigReader;

public class AuthManager {
    private static String authToken;
    
    public static String login(String email, String password) {
        try {
            String baseUrl = ConfigReader.getProperty("base.url");
            String loginEndpoint = ConfigReader.getProperty("login.endpoint");
            
            Response response = RestAssured.given()
                    .baseUri(baseUrl)
                    .contentType("application/json")
                    .body("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}")
                    .when()
                    .post(loginEndpoint);
            
            System.out.println("=== LOGIN RESPONSE ===");
            response.prettyPrint();
            
            if (response.getStatusCode() != 200 && response.getStatusCode() != 201) {
                throw new RuntimeException("Login failed: " + response.getStatusLine());
            }
            
            // Try different JSON paths to extract token
            authToken = response.jsonPath().getString("token");
            if (authToken == null || authToken.isEmpty()) {
                authToken = response.jsonPath().getString("data.token");
            }
            if (authToken == null || authToken.isEmpty()) {
                authToken = response.jsonPath().getString("access_token");
            }
            
            if (authToken == null || authToken.isEmpty()) {
                throw new RuntimeException("Token not found in response!");
            }
            
            return authToken;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
    
    // Overloaded method for convenience
    public static String loginWithEmail(String email, String password) {
        return login(email, password);
    }
    
    public static String getAuthToken() {
        if (authToken == null || authToken.isEmpty()) {
            // Auto-login with default credentials if token is not available
            String email = ConfigReader.getProperty("login.email");
            String password = ConfigReader.getProperty("login.password");
            if (email != null && password != null) {
                return login(email, password);
            }
        }
        return authToken;
    }
    
    public static void setAuthToken(String token) {
        authToken = token;
    }
    
    public static void clearAuthToken() {
        authToken = null;
    }
    
    public static String getBearerToken() {
        return "Bearer " + getAuthToken();
    }
    
    public static String refreshAuthToken() {
        String baseUrl = ConfigReader.getProperty("base.url");
        String refreshEndpoint = ConfigReader.getProperty("refresh.endpoint");
        
        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .post(refreshEndpoint);
        
        if (response.getStatusCode() == 200) {
            authToken = response.jsonPath().getString("token");
            if (authToken == null || authToken.isEmpty()) {
                authToken = response.jsonPath().getString("data.token");
            }
        } else {
            throw new RuntimeException("Token refresh failed: " + response.getStatusLine());
        }
        
        return authToken;
    }
    
    public static void logout() {
        String baseUrl = ConfigReader.getProperty("base.url");
        String logoutEndpoint = ConfigReader.getProperty("logout.endpoint");
        
        if (authToken == null || authToken.isEmpty()) {
            System.out.println("Logout request failed: No valid token available");
            return;
        }
        
        Response response = RestAssured.given()
                .baseUri(baseUrl)
                .header("Authorization", "Bearer " + authToken)
                .when()
                .post(logoutEndpoint);
        
        System.out.println("=== LOGOUT RESPONSE ===");
        response.prettyPrint();
        
        if (response.getStatusCode() != 200) {
            System.out.println("Logout failed: " + response.getStatusLine());
        } else {
            System.out.println("Logout successful.");
            authToken = null;
        }
    }
}
