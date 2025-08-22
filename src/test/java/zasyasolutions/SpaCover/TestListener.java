package zasyasolutions.SpaCover;
import com.aventstack.extentreports.Status;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Test Started: " + result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test Passed: " + result.getMethod().getMethodName());
        if (ExtentManager.getExtent() != null) {
            ExtentManager.getExtent().createTest(result.getMethod().getMethodName())
                    .log(Status.PASS, "Test passed successfully");
        }
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test Failed: " + result.getMethod().getMethodName());
        System.out.println("Failure Reason: " + result.getThrowable().getMessage());
        
        if (ExtentManager.getExtent() != null) {
            ExtentManager.getExtent().createTest(result.getMethod().getMethodName())
                    .log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("Test Skipped: " + result.getMethod().getMethodName());
        if (ExtentManager.getExtent() != null) {
            ExtentManager.getExtent().createTest(result.getMethod().getMethodName())
                    .log(Status.SKIP, "Test skipped: " + result.getThrowable().getMessage());
        }
    }
}