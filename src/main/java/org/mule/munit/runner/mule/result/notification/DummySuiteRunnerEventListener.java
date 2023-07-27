package org.mule.munit.runner.mule.result.notification;

import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;

public class DummySuiteRunnerEventListener implements SuiteRunnerEventListener {
   public void notifySuiteStart(String name) {
   }

   public void notifyTestStart(MunitTest test) {
   }

   public void notifyNumberOfTests(int numberOfTests) {
   }

   public void notifyTestResult(TestResult testResult) {
   }

   public void notifyTestIgnored(TestResult testResult) {
   }

   public void notifySuiteEnd(SuiteResult result) {
   }

   public void notifyBeforeSuiteFailure(Notification notification) {
   }

   public void notifyBeforeSuiteError(Notification notification) {
   }

   public void notifyAfterSuiteFailure(Notification notification) {
   }

   public void notifyAfterSuiteError(Notification notification) {
   }

   public void notifySuiteStartFailure(Notification notification) {
   }
}
