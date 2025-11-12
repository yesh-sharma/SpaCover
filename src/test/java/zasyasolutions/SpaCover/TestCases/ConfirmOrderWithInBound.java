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

public class ConfirmOrderWithInBound extends BaseTest{
	String webhookkey = ConfigReader.getProperty("webhook.key");

	String orderNumber;
	String InventoryLineItemId;
	String proNumber;

	

	 @Test(priority = 1, description = "Confirm order with inventtory ")
	public void confirmOrderFromInbound() {
		logInfo("Starting test: confirming order");

		// ===== Create order item =====
		Map<String, Object> orderItem1 = new HashMap<>();
		orderItem1.put("sku", "E4S4-95-M1-1104");
		orderItem1.put("qty", "1");
		orderItem1.put("type", "inbound");
		InventoryLineItemId = "lineItemId-" + UUID.randomUUID().toString().substring(0, 8);
		orderItem1.put("order_item_id",InventoryLineItemId );
		orderItem1.put("id", "af2bfe6e-be8f-46c8-898e-dba83ef26859");
		orderItem1.put("eta", "2025-11-05");
		
	
		
	
		List<Map<String, Object>> orderItems = new ArrayList<>();
		orderItems.add(orderItem1);
		// ===== Build final request body =====
		Map<String, Object> requestBody = new HashMap<>();
		orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
		requestBody.put("order_number", orderNumber);
		requestBody.put("orderItems", orderItems);

		logInfo("Request Body: " + requestBody);

		// ===== Send POST request =====
		response = given().spec(request).header("X-Webhook-Key", webhookkey).body(requestBody).when()
				.post("/inventory/order-confirmation");

		String InBoundid = APIHelper.extractJsonPath(response, "inventoryUpdates[0].inboundId");
		System.out.println("yash dsfsf"+InBoundid);
		 Assert.assertEquals(InBoundid , "af2bfe6e-be8f-46c8-898e-dba83ef26859", "inboundid Is Not Valid");
		
		System.out.println("Status Code: " + response.getStatusCode());
		System.out.println("Response Body:" +response.prettyPrint());
		

		// ===== Assert response =====
		Assert.assertEquals(response.getStatusCode(), 201, "API failed! Expected 201 but got "
				+ response.getStatusCode() + ". Response: " + response.getBody().asString());
	}

	 @Test(priority = 2, description = "Scan in the sku from inbound")
	 public void scanInTheSku() {
	     logInfo("Starting test: scan in");

	     // ===== Create request body list =====
	     List<Map<String, Object>> requestBodyList = new ArrayList<>();

	     Map<String, Object> item = new HashMap<>();
	     item.put("sku", "E4S4-95-M1-1104");
	     item.put("binNumber", "BIN-124");
	     item.put("location", "CA");
	     item.put("quantity", "1");
	     item.put("containerNumber", "containerEarly");

	     requestBodyList.add(item);

	     logInfo("Request Body: " + requestBodyList);

	     // ===== Send POST request =====
	     response = given()
	             .spec(request)
	             .header("X-Webhook-Key", webhookkey)
	             .body(requestBodyList)
	             .when()
	             .post("/inventory-locations/bulk-scan-in");

	     // ===== Print response details =====
	     System.out.println("Status Code: " + response.getStatusCode());
	     System.out.println("Response Body:");
	     response.prettyPrint();
	 }

	 
	 @Test(priority = 3, description = "Assigning pro number to order")
	 public void addProNumberToInboundLineItem() {
			logInfo("Starting test: pro number assignmnet "); 
		 
		 
		 Map<String, Object> lineItem1 = new HashMap<>();
		 lineItem1.put("type", "PRO");
		 lineItem1.put("sku", "E4S4-95-M1-1104");
		 proNumber = "SH" + String.format("%06d", (int)(Math.random() * 1000000));
		 SharedData.proNumber = proNumber;
		 lineItem1.put("number", proNumber);
		 lineItem1.put("orderNumber", orderNumber);
		 lineItem1.put("qty", 1);
		 
	
		 
		 List<Map<String,Object>> ArrayOfLineItems = new  ArrayList<>();
		 
		 ArrayOfLineItems.add(lineItem1);
		
		 
		 response = given().spec(request).header("X-Webhook-Key", webhookkey).body(ArrayOfLineItems).when()
					.post("/inventory-reference");

		 
		 
		 String ProNumber = APIHelper.extractJsonPath(response, "[0].number");
		 Assert.assertEquals(proNumber , ProNumber, "Pro Number Is Not Valid");
		 
	 
			// ===== Print response details =====
			System.out.println("Status Code: " + response.getStatusCode());
			System.out.println("Response Body:" );
			response.prettyPrint();
		 
	 }
	 
	 
	 @Test(  priority = 4,
		     dataProvider = "proDataForInbound",
		     dataProviderClass = TestDataProvider.class,
		     description = "scan out")
		    public void scanOutTheOrderHavingLineItemFromInbound(String inventoryLocationId, String quantity) {
		 
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
