package zasyasolutions.SpaCover.utils;


import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.response.Response;
import zasyasolutions.SpaCover.BaseTest;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

public class ExtentTestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.createInstance();
        System.out.println("*** Test Suite " + context.getName() + " started ***");
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flush();
        System.out.println("*** Test Suite " + context.getName() + " ending ***");
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();
        
        ExtentTest test = ExtentReportManager.createTest(testName, description);
        
        // Add categories/tags if available
        if (result.getMethod().getGroups().length > 0) {
            test.assignCategory(result.getMethod().getGroups());
        }
        
        test.info("Test Started: " + testName);
        System.out.println("Starting test: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        test.log(Status.PASS, MarkupHelper.createLabel(
            result.getMethod().getMethodName() + " - PASSED", 
            ExtentColor.GREEN
        ));
        test.pass("Test Passed Successfully");
        
        // Log response if available in BaseTest
        logResponseDetails(result);
        
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        test.log(Status.FAIL, MarkupHelper.createLabel(
            result.getMethod().getMethodName() + " - FAILED", 
            ExtentColor.RED
        ));
        
        // Log failure details
        test.fail("Test Failed");
        test.fail(result.getThrowable());
        
        // Log stack trace
        String stackTrace = Arrays.toString(result.getThrowable().getStackTrace());
        test.fail("<details><summary><b>Stack Trace</b></summary>" + 
                  stackTrace.replace(",", "<br>") + "</details>");
        
        // Log response if available
        logResponseDetails(result);
        
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = ExtentReportManager.getTest();
        test.log(Status.SKIP, MarkupHelper.createLabel(
            result.getMethod().getMethodName() + " - SKIPPED", 
            ExtentColor.ORANGE
        ));
        test.skip("Test Skipped");
        test.skip(result.getThrowable());
        
        ExtentReportManager.removeTest();
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not commonly used
    }

    private void logResponseDetails(ITestResult result) {
        try {
            Object testInstance = result.getInstance();
            
            // Try to get response from BaseTest
            if (testInstance instanceof BaseTest) {
                BaseTest baseTest = (BaseTest) testInstance;
                Response response = baseTest.response;
                
                if (response != null) {
                    ExtentTest test = ExtentReportManager.getTest();
                    
                    // Log status code
                    test.info("<b>Status Code:</b> " + response.getStatusCode());
                    
                    // Log response time
                    test.info("<b>Response Time:</b> " + response.getTime() + " ms");
                    
                    // Log response headers
                    test.info("<b>Response Headers:</b><br>" + 
                              response.getHeaders().toString().replace("\n", "<br>"));
                    
                    // Log response body
                    String responseBody = response.getBody().asPrettyString();
                    test.info(MarkupHelper.createCodeBlock(responseBody, CodeLanguage.JSON));
                }
            }
        } catch (Exception e) {
            // Silently handle if response is not available
        }
    }
}