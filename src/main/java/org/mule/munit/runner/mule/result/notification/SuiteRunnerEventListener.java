package org.mule.munit.runner.mule.result.notification;

import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;

public interface SuiteRunnerEventListener {
   void notifySuiteStart(String var1);

   void notifySuiteStartFailure(Notification var1);

   void notifyBeforeSuiteFailure(Notification var1);

   void notifyBeforeSuiteError(Notification var1);

   void notifyTestStart(MunitTest var1);

   void notifyNumberOfTests(int var1);

   void notifyTestResult(TestResult var1);

   void notifyTestIgnored(TestResult var1);

   void notifyAfterSuiteFailure(Notification var1);

   void notifyAfterSuiteError(Notification var1);

   void notifySuiteEnd(SuiteResult var1);
}
