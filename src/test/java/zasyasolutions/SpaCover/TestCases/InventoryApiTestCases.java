package zasyasolutions.SpaCover.TestCases;

import org.testng.Assert;
import org.testng.annotations.Test;

import zasyasolutions.SpaCover.APIHelper;
import zasyasolutions.SpaCover.BaseTest;
import zasyasolutions.SpaCover.ConfigReader;
import zasyasolutions.SpaCover.TestDataProvider;
import zasyasolutions.SpaCover.Auth.AuthManager;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InventoryApiTestCases extends BaseTest {

	//Shared variables
	private static String expectedQuantity;
	private static String expectedAllocatedQuantity = "0";
	private static String expectedInHandQuantity;
	String webhookkey = ConfigReader.getProperty("webhook.key");

//@Test(priority = 1, description = "Get sku detail", dataProvider = "skuData", dataProviderClass = TestDataProvider.class)
	public void getAllInventoryBySKU(String sku) {
		logInfo("Starting test: Get inventory by SKU " + sku);// Define the SKU

		logInfo("Starting test: Get inventory by SKU" + sku);
		logInfo(authToken);

		// Log the base URL for debugging
		logInfo("Base URL: " + io.restassured.RestAssured.baseURI);
		logInfo("Full URL will be: " + io.restassured.RestAssured.baseURI + "/inventory/by-sku/" + sku);

		response = given()

				.spec(request)
				// .header("Authorization", "Bearer " + authToken)
				.when().get("/inventory/by-sku/" + sku);

		// Extract dynamic values
		expectedQuantity = APIHelper.extractJsonPath(response, "data.quantity");
		expectedAllocatedQuantity = APIHelper.extractJsonPath(response, "data.allocatedQuantity");
		expectedInHandQuantity = APIHelper.extractJsonPath(response, "data.inHandQuantity");
		// Validations
		APIHelper.validateStatusCode(response, 200);
		APIHelper.validateContentType(response, "application/json; charset=utf-8");
		APIHelper.validateResponseTime(response, 4000L);

		// logInfo("Response: " + response.getBody().asString());
		//

		logInfo("Response:\n" + response.getBody().asPrettyString());
		// logInfo("Response:\n" + response.getBody().prettyPrint());
		logPass("Successfully retrieved the sku sku detail in inventory");
	}

	@Test(
		    priority = 2,
		    description = "Get SKU list from inventory and inbound",
		    dataProvider = "skuData",
		    dataProviderClass = TestDataProvider.class
		)
		public void getSkuDetailsInOurRecords(Map<String, Object> data) {
		    logInfo("Starting test: Get SKU list from inventory and inbound");

		    // ✅ Convert data map to JSON body
		    String requestBody = new org.json.JSONObject(data).toString();
		    logInfo("Request Body: " + requestBody);

		    // ✅ Send API request
		    response = given()
		        .spec(request)
		        .header("X-Webhook-Key", webhookkey)
		        .body(requestBody)
		        .when()
		        .post("/inventory/inhand-quantity");

		    // ✅ Validations
		    APIHelper.validateStatusCode(response, 201);
		    APIHelper.validateContentType(response, "application/json");

		    // ✅ Print Response
		    logInfo("Response:\n" + response.getBody().asPrettyString());

		    // ✅ Example: Validate that response includes all SKUs
		    List<String> requestedSkus = (List<String>) data.get("sku");
		    for (String sku : requestedSkus) {
		        APIHelper.validateJsonFieldValue(response, "inventory.sku", sku);
		    }

		    logPass("All validations passed for bulk SKU inventory details");
		}

//@Test(priority = 3, description = "confirm")
	public void confirmSkuAddInBooked() {
		logInfo("Starting test: Confirm sku and booked sku");
		logInfo("Base URL: " + io.restassured.RestAssured.baseURI);
		String requestBody = "{\n" + "  \"sku\": \"N2N2-45-T2-1104\",\n" + "  \"qty\": \"1\",\n"
				+ "  \"type\": \"inventory\"\n" + "}";

		response = given().spec(request).header("X-Webhook-Key", webhookkey).body(requestBody).when()
				.post("/inventory/order-confirmation");

		// Validations
		APIHelper.validateStatusCode(response, 201);
		// ✅ Extract the updated allocatedQuantity from response
		String updatedAllocatedQuantity = APIHelper.extractJsonPath(response, "updated.allocatedQuantity");
		// ✅ Handle null/empty gracefully
		int previousAllocated = (expectedAllocatedQuantity == null || expectedAllocatedQuantity.isEmpty()) ? 0
				: Integer.parseInt(expectedAllocatedQuantity);
		// ✅ Convert both to int and compare
		// int previousAllocated = Integer.parseInt(expectedAllocatedQuantity);
		int updatedAllocated = Integer.parseInt(updatedAllocatedQuantity);

		logInfo("prevoious" + previousAllocated);
		logInfo("updated" + updatedAllocated);

		int qty = 1;
		if (updatedAllocated != previousAllocated + qty) {
			throw new AssertionError("Allocated quantity mismatch: expected " + (previousAllocated + qty) + " but got "
					+ updatedAllocated);
		}

		logInfo("Response:\n" + response.getBody().asPrettyString());

	}

//
//	@Test(priority = 4, description = "Create user using AuthManager")
//    public void reverseSkuBookedQuantity() {
//        logInfo("Starting test: reversing");
//           // Create request body
//        Map<String, Object> requestBody = new HashMap<>();
//        Map<String, Object> updated = new HashMap<>();
//        updated.put("sku", "N2N2-45-T2-1104");
//        updated.put("qty", "1");
//        updated.put("type", "inventory");
//        Map<String, Object> newItem = new HashMap<>();  // E4E4-05-M1-3132   //S4E0-55-M1-1239
//        newItem.put("sku", "E4E4-05-M1-3132");
//        newItem.put("qty", "1");
//        newItem.put("id", "82593b7b-8fb7-4a8d-b32b-7f7a939d040e");
//        newItem.put("type", "inbound");
//        requestBody.put("updated", updated);
//        requestBody.put("new", newItem);
//        logInfo("requestBody"+ requestBody);
//        response = given().spec(request).header("X-Webhook-Key", webhookkey).body(requestBody).when()
//                .post("/inventory/order-update");
//        System.out.println("Status Code: " + response.getStatusCode());
//        System.out.println("Response Body: ");
//        response.prettyPrint();
//    }

	
	
	String orderNumber;
	
	//@Test(priority = 4, description = "Confirm order using AuthManager")
	public void confirmOrder() {
	    logInfo("Starting test: confirming order");

	    // ===== Create order item =====
	    Map<String, Object> orderItem = new HashMap<>();
	    orderItem.put("sku", "N2N2-45-T2-1104");
	    orderItem.put("qty", "1");
	    orderItem.put("type", "inventory");
	    orderItem.put("order_item_id", "shopify-item-001");

	    List<Map<String, Object>> orderItems = new ArrayList<>();
	    orderItems.add(orderItem);

	    // ===== Build final request body =====
	    Map<String, Object> requestBody = new HashMap<>();
	    orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
	    requestBody.put("order_number",orderNumber);
	    requestBody.put("orderItems", orderItems);
           
	    logInfo("Request Body: " + requestBody);

	    // ===== Send POST request =====
	    response = given()
	            .spec(request)
	            .header("X-Webhook-Key", webhookkey)
	            .body(requestBody)
	            .when()
	            .post("/inventory/order-confirmation");

	    // ===== Print response details =====
	    System.out.println("Status Code: " + response.getStatusCode());
	    System.out.println("Response Body:");
	    response.prettyPrint();

	    // ===== Assert response =====
	    Assert.assertEquals(response.getStatusCode(), 201,
	            "API failed! Expected 200 but got " + response.getStatusCode() + ". Response: " + response.getBody().asString());
	}

	
	
	//@Test(priority = 5, description = "Reverse SKU Booked Quantity using AuthManager")
	public void orderUpdateChangingTheSku() {
	    logInfo("Starting test: reversing booked SKU quantity");

	    // ===== Create old order item =====
	    Map<String, Object> oldItem = new HashMap<>();
	    oldItem.put("sku", "N2N2-45-T2-1104");
	    oldItem.put("qty", "1");
	    oldItem.put("type", "inventory");
	    oldItem.put("order_item_id", "shopify-item-001");

	    List<Map<String, Object>> oldOrderItems = new ArrayList<>();
	    oldOrderItems.add(oldItem);

	    Map<String, Object> oldOrder = new HashMap<>();
	    oldOrder.put("orderItems", oldOrderItems);

	    // ===== Create new order item =====
	    Map<String, Object> newItem = new HashMap<>();
	    newItem.put("sku", "E4E4-45-M1-3221");
	    newItem.put("qty", "1");
	    newItem.put("type", "inventory");
	    newItem.put("order_item_id", "shopify-item-001");

	    List<Map<String, Object>> newOrderItems = new ArrayList<>();
	    newOrderItems.add(newItem);

	    Map<String, Object> newOrder = new HashMap<>();
	    newOrder.put("orderItems", newOrderItems);

	    // ===== Build final request body =====
	    Map<String, Object> requestBody = new HashMap<>();
	    requestBody.put("order_number", orderNumber);
	    requestBody.put("oldOrder", oldOrder);
	    requestBody.put("newOrder", newOrder);

	    logInfo("Request Body: " + requestBody);

	    // ===== Send POST request =====
	    response = given()
	            .spec(request)
	            .header("X-Webhook-Key", webhookkey)
	            .body(requestBody)
	            .when()
	            .post("/inventory/order-update");

	    // ===== Print response details =====
	    System.out.println("Status Code: " + response.getStatusCode());
	    System.out.println("Response Body:");
	    response.prettyPrint();
	}

	
	
	
	
	
	
	
	
}
