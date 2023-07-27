package org.mule.munit.runner.mule.result.notification;

import java.io.PrintStream;
import org.apache.commons.lang3.StringUtils;
import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;

/** @deprecated */
@Deprecated
public class StreamNotificationListener implements NotificationListener {
   private PrintStream out;
   private boolean debugMode = true;

   public StreamNotificationListener(PrintStream out) {
      this.out = out;
   }

   public void notifyRuntimeStartFailure(Notification notification) {
   }

   public void notifyStartOf(MunitTest test) {
      this.out.flush();
   }

   public void notify(TestResult testResult) {
      Notification notification = null;
      if (testResult.getNumberOfErrors() > 0) {
         this.out.println("ERROR - The test " + testResult.getTestName() + " finished with an Error.");
         this.out.flush();
         notification = testResult.getError();
      } else if (testResult.getFailure() != null) {
         this.out.println("FAILURE - The test " + testResult.getTestName() + " finished with a Failure.");
         this.out.flush();
         notification = testResult.getFailure();
      }

      if (notification != null) {
         this.out.println(notification.getShortMessage());
         if (this.debugMode) {
            this.out.println(notification.getFullMessage());
         }

         this.out.flush();
      } else if (testResult.isSkipped()) {
         this.out.println("SKIPPED - Test " + testResult.getTestName() + " was Skipped.");
         this.out.flush();
      } else {
         this.out.println("SUCCESS - Test " + testResult.getTestName() + " finished Successfully.");
         this.out.flush();
      }

   }

   public void notifyIgnored(TestResult testResult) {
      this.out.flush();
   }

   public void notifyEnd(SuiteResult result) {
      this.out.println();
      String title = "Number of tests run: " + result.getNumberOfTests() + " - Failed: " + result.getNumberOfFailures() + " - Errors: " + result.getNumberOfErrors() + " - Skipped: " + result.getNumberOfSkipped() + " - Time elapsed: " + result.getTime() + "ms";
      String titleFrame = StringUtils.repeat("=", title.length());
      this.out.println(titleFrame);
      this.out.println(title);
      this.out.println(titleFrame);
      this.out.flush();
   }

   public void setDebugMode(boolean debugMode) {
      this.debugMode = debugMode;
   }
}
