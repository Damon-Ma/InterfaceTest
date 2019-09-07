package com.damon.config;

import com.damon.model.TestDataModel;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * 重写testng监听
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult iTestResult) {
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        TestDataModel model = TestConfig.testDataModel;
        model.setResult(iTestResult.getStatus());
        TestCaseInfo.setTestResult(model);
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        try {
            TestDataModel model = TestConfig.testDataModel;
            model.setResult(iTestResult.getStatus());
            TestCaseInfo.setTestResult(model);
        }catch (NullPointerException e){}
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        try {
            TestDataModel model = TestConfig.testDataModel;
            model.setResult(iTestResult.getStatus());
            TestCaseInfo.setTestResult(model);
        }catch (NullPointerException e){}
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
    }

    @Override
    public void onStart(ITestContext iTestContext) {
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
    }
}
