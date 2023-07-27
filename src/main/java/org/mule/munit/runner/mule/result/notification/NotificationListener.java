package org.mule.munit.runner.mule.result.notification;

import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;

/** @deprecated */
@Deprecated
public interface NotificationListener {
   void notifyStartOf(MunitTest var1);

   void notify(TestResult var1);

   void notifyIgnored(TestResult var1);

   void notifyEnd(SuiteResult var1);
}
