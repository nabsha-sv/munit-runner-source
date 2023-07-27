package org.mule.munit.runner.mule.result.notification;

import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;

/** @deprecated */
@Deprecated
public class DummyNotificationListener implements NotificationListener {
   public void notifyStartOf(MunitTest test) {
   }

   public void notify(TestResult testResult) {
   }

   public void notifyIgnored(TestResult testResult) {
   }

   public void notifyEnd(SuiteResult result) {
   }
}
