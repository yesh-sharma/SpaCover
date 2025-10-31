package zasyasolutions.SpaCover;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtils {

    private static final String DB_URL = "jdbc:mysql://34.133.157.99:3306/msc-inventory-db";
    private static final String DB_USER = "anoop";
    private static final String DB_PASS = "qazx1234";

    // ✅ Get DB connection
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("✅ Connected to database successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    // ✅ Fetch full row (all columns) for given order number
    public static Map<String, String> getOrderDetails(String orderNumber) {
        Map<String, String> orderData = new HashMap<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String query = "SELECT * FROM orders WHERE order_number = '" + orderNumber + "'";
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            if (rs.next()) {
                System.out.println("\n===== Order Details for " + orderNumber + " =====");
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = rs.getString(i);
                    orderData.put(columnName, columnValue);
                    System.out.println(columnName + " : " + columnValue);
                }
                System.out.println("=====================================\n");
            } else {
                System.out.println("⚠️ No record found for order_number: " + orderNumber);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orderData;  // ✅ Return all data as a Map
    }

    // ✅ For manual test
    public static void main(String[] args) {
        Map<String, String> orderInfo = getOrderDetails("ORD-2025-5002");
    //    System.out.println("Returned Map Data: " + orderInfo.);

        // Print returned map for debugging
        System.out.println("Returned Map Data: " + orderInfo);
    }
}
