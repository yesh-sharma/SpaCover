package zasyasolutions.SpaCover;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
    
    private static ExtentReports extent;
    private static ExtentSparkReporter sparkReporter;
    private static final String REPORT_PATH = "test-output/ExtentReport.html";
    
    public static ExtentReports createInstance() {
        if (extent == null) {
            sparkReporter = new ExtentSparkReporter(REPORT_PATH);
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("API Automation Test Report");
            sparkReporter.config().setReportName("REST Assured API Tests");
            
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("User", System.getProperty("user.name"));
            extent.setSystemInfo("Framework", "REST Assured + TestNG");
        }
        return extent;
    }
    
    public static ExtentReports getExtent() {
        return extent;
    }
    
    public static ExtentTest createTest(String testName) {
        return extent.createTest(testName);
    }
    
    public static ExtentTest createTest(String testName, String description) {
        return extent.createTest(testName, description);
    }
}