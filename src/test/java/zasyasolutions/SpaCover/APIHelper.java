package zasyasolutions.SpaCover;

import io.restassured.response.Response;
import org.hamcrest.Matchers;



public class APIHelper {
    
    /**
     * Validate status code
     */
    public static void validateStatusCode(Response response, int expectedStatusCode) {
        response.then().assertThat().statusCode(expectedStatusCode);
    }
    
    /**
     * Validate response time
     */
    public static void validateResponseTime(Response response, long maxTimeInMs) {
        response.then().assertThat().time(Matchers.lessThan(maxTimeInMs));
    }
    
    
//    /**
//     * Validate JSON schema
//     */
////    public static void validateJsonSchema(Response response, String schemaPath) {
////        response.then().assertThat().body(matchesJsonSchemaInClasspath(schemaPath));
////    }
    
    /**
     * Validate header exists
     */
    public static void validateHeaderExists(Response response, String headerName) {
        response.then().assertThat().header(headerName, Matchers.notNullValue());
    }
    
    /**
     * Validate header value
     */
    public static void validateHeaderValue(Response response, String headerName, String expectedValue) {
        response.then().assertThat().header(headerName, expectedValue);
    }
    
    /**
     * Validate content type
     */
    public static void validateContentType(Response response, String expectedContentType) {
        response.then().assertThat().contentType(expectedContentType);
    }
    
    /**
     * Extract value from JSON response using JSONPath
     */
    public static String extractJsonPath(Response response, String jsonPath) {
        return response.jsonPath().getString(jsonPath);
    }
    
    /**
     * Extract value from XML response using XMLPath
     */
    public static String extractXmlPath(Response response, String xmlPath) {
        return response.xmlPath().getString(xmlPath);
    }
    
    /**
     * Validate JSON field value
     */
    public static void validateJsonFieldValue(Response response, String jsonPath, Object expectedValue) {
        response.then().assertThat().body(jsonPath, Matchers.equalTo(expectedValue));
    }
    
    /**
     * Validate JSON array size
     */
    public static void validateJsonArraySize(Response response, String jsonPath, int expectedSize) {
        response.then().assertThat().body(jsonPath, Matchers.hasSize(expectedSize));
    }
    
    /**
     * Print response body
     */
    public static void printResponseBody(Response response) {
        System.out.println("Response Body: " + response.getBody().asString());
    }
    
    /**
     * Print response headers
     */
    public static void printResponseHeaders(Response response) {
        System.out.println("Response Headers: " + response.getHeaders().toString());
    }
}