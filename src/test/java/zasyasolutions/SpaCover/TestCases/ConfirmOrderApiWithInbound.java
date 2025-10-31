package zasyasolutions.SpaCover.TestCases;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.testng.Assert;

import zasyasolutions.SpaCover.BaseTest;
import zasyasolutions.SpaCover.ConfigReader;

public class ConfirmOrderApiWithInbound extends BaseTest {
	String webhookkey = ConfigReader.getProperty("webhook.key");

	String orderNumber;

	// @Test(priority = 4, description = "Confirm order using AuthManager")
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
		requestBody.put("order_number", orderNumber);
		requestBody.put("orderItems", orderItems);

		logInfo("Request Body: " + requestBody);

		// ===== Send POST request =====
		response = given().spec(request).header("X-Webhook-Key", webhookkey).body(requestBody).when()
				.post("/inventory/order-confirmation");

		// ===== Print response details =====
		System.out.println("Status Code: " + response.getStatusCode());
		System.out.println("Response Body:");
		response.prettyPrint();

		// ===== Assert response =====
		Assert.assertEquals(response.getStatusCode(), 201, "API failed! Expected 200 but got "
				+ response.getStatusCode() + ". Response: " + response.getBody().asString());
	}

	// @Test(priority = 5, description = "Reverse SKU Booked Quantity using
	// AuthManager")
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
		response = given().spec(request).header("X-Webhook-Key", webhookkey).body(requestBody).when()
				.post("/inventory/order-update");

		// ===== Print response details =====
		System.out.println("Status Code: " + response.getStatusCode());
		System.out.println("Response Body:");
		response.prettyPrint();
	}

}
