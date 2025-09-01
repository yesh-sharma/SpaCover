package zasyasolutions.SpaCover.TestCases;

import org.testng.annotations.Test;

import zasyasolutions.SpaCover.APIHelper;
import zasyasolutions.SpaCover.BaseTest;
import zasyasolutions.SpaCover.ConfigReader;
import zasyasolutions.SpaCover.TestDataProvider;
import zasyasolutions.SpaCover.Auth.AuthManager;

import static io.restassured.RestAssured.given;

public class InventoryApiTestCases extends BaseTest {

	// Shared variables
	private static String expectedQuantity;
	private static String expectedAllocatedQuantity = "0";
	private static String expectedInHandQuantity;
	String webhookkey = ConfigReader.getProperty("webhook.key");

	@Test(priority = 1, description = "Get sku detail", dataProvider = "skuData", dataProviderClass = TestDataProvider.class)
	public void getAllInventoryBySKU(String sku) {
		logInfo("Starting test: Get inventory by SKU " + sku);// Define the SKU

		logInfo("Starting test: Get inventory by SKU" + sku);
		logInfo(authToken);

		// Log the base URL for debugging
		logInfo("Base URL: " + io.restassured.RestAssured.baseURI);
		logInfo("Full URL will be: " + io.restassured.RestAssured.baseURI + "/inventory/by-sku/" + sku);

		response = given()

				.header("Accept", "application/json").spec(request)
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

	@Test(priority = 2, description = "Get SKU list from inventory and inbound", dataProvider = "skuData", dataProviderClass = TestDataProvider.class)
	public void getSkuDetailsInOurRecords(String Sku) {
		logInfo("Starting test: Get user by ID");

		logInfo(authToken);

		String requestBody = "{\n" + "  \"sku\": [\n" + "    \"" + Sku + "\"\n" + "  ]\n" + "}";
		response = given().spec(request).header("X-Webhook-Key", webhookkey).body(requestBody).when()
				.post("/inventory/inhand-quantity");

		// ✅ General checks
		APIHelper.validateStatusCode(response, 201);
		APIHelper.validateContentType(response, "application/json");

		// ✅ Inventory validations
		// APIHelper.validateJsonFieldNotNull(response, "inventory[0].id");
		APIHelper.validateJsonFieldValue(response, "inventory[0].sku", Sku);
		// ✅ Compare with values extracted from first test
		APIHelper.validateJsonFieldValue(response, "inventory[0].quantity", expectedQuantity);
		APIHelper.validateJsonFieldValue(response, "inventory[0].allocatedQuantity", expectedAllocatedQuantity);
		APIHelper.validateJsonFieldValue(response, "inventory[0].inHandQuantity", expectedInHandQuantity);

		// ✅ String checks
		// APIHelper.validateJsonFieldContains(response,
		// "inventory[0].vendorDescription", "HydroTex Pro");
		// APIHelper.validateJsonFieldValue(response, "inventory[0].materialColor",
		// "HydroTex Pro - Oxford Grey");

		// ✅ Null field checks
		// APIHelper.validateJsonFieldIsNull(response, "inventory[0].stripInsert");
		// APIHelper.validateJsonFieldIsNull(response, "inventory[0].shape");

		// ✅ Date fields format
		// APIHelper.validateJsonFieldMatchesRegex(response, "inventory[0].createdAt",
		// "\\d{4}-\\d{2}-\\d{2}T.*Z");
		// APIHelper.validateJsonFieldMatchesRegex(response, "inventory[0].updatedAt",
		// "\\d{4}-\\d{2}-\\d{2}T.*Z");

		// logInfo("Full Response:\n" + response.getBody().asPrettyString());
		logPass("All validations passed for SKU inventory details");

		logInfo("Response:\n" + response.getBody().asPrettyString());

	}

	@Test(priority = 3, description = "confirm")
	public void confirmSkuAddInBooked() {
		logInfo("Starting test: Confirm sku and booked sku");
		logInfo("Base URL: " + io.restassured.RestAssured.baseURI);
		String requestBody = "{\n" + "  \"sku\": \"E2E2-55-M1-3218\",\n" + "  \"qty\": \"2\",\n"
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

		int qty = 2;
		if (updatedAllocated != previousAllocated + qty) {
			throw new AssertionError("Allocated quantity mismatch: expected " + (previousAllocated + qty) + " but got "
					+ updatedAllocated);
		}

		logInfo("Response:\n" + response.getBody().asPrettyString());

	}

	// @Test(priority = 4, description = "Create user using AuthManager")
	public void createUserUsingAuthManager() {
		logInfo("Starting test: Create user using AuthManager");

		String requestBody = "{\n" + "  \"name\": \"Jane Smith\",\n" + "  \"job\": \"QA Engineer\"\n" + "}";

		response = given().header("Content-Type", "application/json")
				.header("Authorization", AuthManager.getBearerToken()).body(requestBody).when().post("/users");

		// Validations
		APIHelper.validateStatusCode(response, 201);
		APIHelper.validateJsonFieldValue(response, "name", "Jane Smith");

		logPass("Successfully created user using AuthManager");
	}

	// @Test(priority = 5, description = "Update user")
	public void updateUser() {
		logInfo("Starting test: Update user");

		int userId = 2;
		String updatedName = "Updated User Name";
		String requestBody = "{\n" + "  \"name\": \"" + updatedName + "\",\n" + "  \"job\": \"Senior Developer\"\n"
				+ "}";

		response = given().spec(request).pathParam("id", userId).body(requestBody).when().put("/users/{id}");

		// Validations
		APIHelper.validateStatusCode(response, 200);
		APIHelper.validateJsonFieldValue(response, "name", updatedName);

		// Extract updated timestamp
		String updatedAt = APIHelper.extractJsonPath(response, "updatedAt");
		logInfo("User updated at: " + updatedAt);

		logPass("Successfully updated user with ID: " + userId);
	}

	// @Test(priority = 6, description = "Delete user")
	public void deleteUser() {
		logInfo("Starting test: Delete user");

		int userId = 2;
		response = given().spec(request).pathParam("id", userId).when().delete("/users/{id}");

		// Validations
		APIHelper.validateStatusCode(response, 204);

		logPass("Successfully deleted user with ID: " + userId);
	}

	// @Test(priority = 7, description = "Test user not found")
	public void testUserNotFound() {
		logInfo("Starting test: User not found");

		int nonExistentUserId = 999;
		response = given().spec(request).pathParam("id", nonExistentUserId).when().get("/users/{id}");

		// Validation for not found
		APIHelper.validateStatusCode(response, 404);

		logPass("Successfully validated user not found scenario");
	}

}
