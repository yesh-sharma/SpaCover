package zasyasolutions.SpaCover.TestCases;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import zasyasolutions.SpaCover.APIHelper;
import zasyasolutions.SpaCover.BaseTest;
import zasyasolutions.SpaCover.ConfigReader;
import zasyasolutions.SpaCover.SharedData;
import zasyasolutions.SpaCover.TestDataProvider;

public class ConfirmOrderApiWithInventory extends BaseTest {
	String webhookkey = ConfigReader.getProperty("webhook.key");

	String orderNumber;
	String InventoryLineItemId;
	String proNumber;
	private static String expectedQuantity;
	private static String expectedAllocatedQuantity = "0";
	private static String expectedInHandQuantity;
	
	
	
	@Test(priority = 1, description = "Get sku detail", dataProvider = "sku", dataProviderClass = TestDataProvider.class)
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	 @Test(priority = 2, description = "Confirm order with inventtory ")
	public void confirmOrder() {
		logInfo("Starting test: confirming order");

		// ===== Create order item =====
		Map<String, Object> orderItem1 = new HashMap<>();
		orderItem1.put("sku", "E4E4-117-M1-1244");
		orderItem1.put("qty", "1");
		orderItem1.put("type", "inventory");
		InventoryLineItemId = "lineItemId-" + UUID.randomUUID().toString().substring(0, 8);
		orderItem1.put("order_item_id",InventoryLineItemId );
		
		Map<String, Object> orderItem2 = new HashMap<>();
		orderItem2.put("sku", "N4N4-87-M1-3132");
		orderItem2.put("qty", "2");
		orderItem2.put("type", "inventory");
		orderItem2.put("order_item_id","lineItemId-" + UUID.randomUUID().toString().substring(0, 8) );
		
		
		

		List<Map<String, Object>> orderItems = new ArrayList<>();
		orderItems.add(orderItem1);
		orderItems.add(orderItem2);
		// ===== Build final request body =====
		Map<String, Object> requestBody = new HashMap<>();
		orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
		requestBody.put("order_number", orderNumber);
		requestBody.put("orderItems", orderItems);

		logInfo("Request Body: " + requestBody);

		// ===== Send POST request =====
		response = given().spec(request).header("X-Webhook-Key", webhookkey).body(requestBody).when()
				.post("/inventory/order-confirmation");

	//	String Quantity = APIHelper.extractJsonPath(response, "data.quantity");
		
		
		String inHandQuantity = APIHelper.extractJsonPath(response, "inventoryUpdates[1].inHandQuantity");
		String BookedQuantity = APIHelper.extractJsonPath(response, "inventoryUpdates[1].allocatedQuantity");
		
		int convertedStringToIntInHand = Integer.parseInt(inHandQuantity);
		int covertedStringToBookedQuantity= Integer.parseInt(BookedQuantity);
		
		Assert.assertEquals(convertedStringToIntInHand +2 , Integer.parseInt(expectedInHandQuantity), "In-hand quantity mismatch!");
		
		Assert.assertEquals(covertedStringToBookedQuantity -2, Integer.parseInt(expectedAllocatedQuantity), "bookedquantity quantity mismatch!");
		
		
		System.out.println("Status Code: " + response.getStatusCode());
		System.out.println("Response Body:");
		response.prettyPrint();

		// ===== Assert response =====
		Assert.assertEquals(response.getStatusCode(), 201, "API failed! Expected 200 but got "
				+ response.getStatusCode() + ". Response: " + response.getBody().asString());
	}

	 @Test(priority = 3, description = "Order Update changing the order line item sku")
	public void orderUpdateChangingTheSku() {
		logInfo("Starting test: reversing booked SKU quantity");

		// ===== Create old order item =====
		Map<String, Object> oldItem = new HashMap<>();
		oldItem.put("sku", "E4E4-117-M1-1244");
		oldItem.put("qty", "1");
		oldItem.put("type", "inventory");
		oldItem.put("order_item_id", InventoryLineItemId);

		List<Map<String, Object>> oldOrderItems = new ArrayList<>();
		oldOrderItems.add(oldItem);

		Map<String, Object> oldOrder = new HashMap<>();
		oldOrder.put("orderItems", oldOrderItems);

		// ===== Create new order item =====
		Map<String, Object> newItem = new HashMap<>();
		newItem.put("sku", "E0X2-55-M1-3218");
		newItem.put("qty", "1");
		newItem.put("type", "inventory");
		newItem.put("order_item_id", InventoryLineItemId);

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
		response = given().spec(request).header("X-Webhook-Key", webhookkey).body(requestBody).when()
				.post("/inventory/order-update");

		// ===== Print response details =====
		System.out.println("Status Code: " + response.getStatusCode());
		System.out.println("Response Body:");
		response.prettyPrint();
	}
	 
	 @Test(priority = 4, description = "Assigning pro number to order")
	 public void addProNumber() {
			logInfo("Starting test: pro number assignmnet "); 
		 
		 
		 Map<String, Object> lineItem1 = new HashMap<>();
		 lineItem1.put("type", "PRO");
		 lineItem1.put("sku", "E0X2-55-M1-3218");
		 proNumber = "SH" + String.format("%06d", (int)(Math.random() * 1000000));
		 SharedData.proNumber = proNumber;
		 lineItem1.put("number", proNumber);
		 lineItem1.put("orderNumber", orderNumber);
		 lineItem1.put("qty", 1);
		 
		 
		 Map<String, Object> lineItem2 = new HashMap<>();
		 lineItem2.put("type", "PRO");
		 lineItem2.put("sku", "N4N4-87-M1-3132");
		 lineItem2.put("number", proNumber);
		 lineItem2.put("orderNumber", orderNumber);
		 lineItem2.put("qty", 2);
		 
		 
		 List<Map<String,Object>> ArrayOfLineItems = new  ArrayList<>();
		 
		 ArrayOfLineItems.add(lineItem1);
		 ArrayOfLineItems.add(lineItem2);
		 
		 response = given().spec(request).header("X-Webhook-Key", webhookkey).body(ArrayOfLineItems).when()
					.post("/inventory-reference");

		 
		 
		 String ProNumber = APIHelper.extractJsonPath(response, "[0].number");
		 Assert.assertEquals(proNumber , ProNumber, "Pro Number Is Not Valid");
		 
		 String Sku1 = APIHelper.extractJsonPath(response, "[0].sku");
		 Assert.assertEquals(Sku1 , "E0X2-55-M1-3218", "sku does not matched");
		 
		 String Sku2 = APIHelper.extractJsonPath(response, "[1].sku");
		 Assert.assertEquals(Sku2 , "N4N4-87-M1-3132", "sku does not matched");
		 
		 
		 
			// ===== Print response details =====
			System.out.println("Status Code: " + response.getStatusCode());
			System.out.println("Response Body:" );
			response.prettyPrint();
		 
	 }
	 
	 
	 @Test(  priority = 5,
		     dataProvider = "proData",
		     dataProviderClass = TestDataProvider.class,
		     description = "Assigning pro number to inventory")
		    public void scanOut(String inventoryLocationId, String quantity) {
		 
			logInfo("Starting test: starting scan out"); 
		
		    Map<String, Object> data = new HashMap<>();
		    data.put("inventoryLocationId", inventoryLocationId);
		    data.put("quantity", quantity);
		    data.put("proNumber", proNumber);

		     
		 response = given().spec(request).header("X-Webhook-Key", webhookkey).body(data).when()
					.post("/inventory-locations/remove-quantity");

			// ===== Print response details =====
			System.out.println("Status Code: " + response.getStatusCode());
			System.out.println("Response Body:" );
			response.prettyPrint();
		 
		        // Example: send API request or perform logic
		        // sendRequest(data);
		    }
	 
	 
	 
	 
	 
	 
	 
	 
	 

}
