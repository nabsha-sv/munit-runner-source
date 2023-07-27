package org.mule.munit.runner.mule.result.notification;

import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;

public class SuiteRunnerEventListenerAdapter implements SuiteRunnerEventListener {
   private NotificationListener notificationListener;

   public SuiteRunnerEventListenerAdapter(NotificationListener notificationListener) {
      this.notificationListener = notificationListener;
   }

   public void notifyTestStart(MunitTest test) {
      this.notificationListener.notifyStartOf(test);
   }

   public void notifyTestResult(TestResult testResult) {
      this.notificationListener.notify(testResult);
   }

   public void notifyTestIgnored(TestResult testResult) {
      this.notificationListener.notifyIgnored(testResult);
   }

   public void notifySuiteEnd(SuiteResult result) {
      this.notificationListener.notifyEnd(result);
   }

   public void notifySuiteStart(String path) {
   }

   public void notifyAfterSuiteFailure(Notification notification) {
   }

   public void notifyAfterSuiteError(Notification notification) {
   }

   public void notifyNumberOfTests(int numberOfTests) {
   }

   public void notifySuiteStartFailure(Notification notification) {
   }

   public void notifyBeforeSuiteFailure(Notification notification) {
   }

   public void notifyBeforeSuiteError(Notification notification) {
   }
}
