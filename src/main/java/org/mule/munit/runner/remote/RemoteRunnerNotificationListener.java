package org.mule.munit.runner.remote;

import java.io.IOException;
import java.io.ObjectOutput;
import org.apache.commons.lang.Validate;
import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;
import org.mule.munit.runner.mule.result.notification.Notification;
import org.mule.munit.runner.mule.result.notification.NotificationListener;

/** @deprecated */
@Deprecated
public class RemoteRunnerNotificationListener implements NotificationListener, RunNotificationListiner {
   private ObjectOutput out;
   private String runToken;
   private String suitePath;
   private String suiteName;

   public RemoteRunnerNotificationListener(String runToken, ObjectOutput out) {
      Validate.notNull(out, "The out can not be null");
      Validate.notEmpty(runToken, "The run token can not be null nor empty");
      this.out = out;
      this.runToken = runToken;
   }

   public synchronized void defineCurrentSuite(String suitePath, String suiteName) {
      Validate.notEmpty(suitePath, "The suite path can not be null nor empty");
      Validate.notEmpty(suiteName, "The suite name can not be null nor empty");
      this.suitePath = suitePath;
      this.suiteName = suiteName;
   }

   public void notifyRunStart() {
      String message = MessageBuilder.runStartMessage(this.runToken);
      this.sendMessage(message);
   }

   public void notifySuiteStart() {
      String message = MessageBuilder.testSuiteStartMessage(this.runToken, this.suitePath, this.suiteName);
      this.sendMessage(message);
   }

   public void notifySuiteStartFailure(Notification notification) {
      String message = MessageBuilder.testSuiteStartFailureMessage(this.runToken, this.suitePath, this.suiteName, notification.getFullMessage());
      this.sendMessage(message);
   }

   public void notifyNumberOfTest(int numberOfTests) {
      String message = MessageBuilder.numberOfTestsMessage(this.runToken, this.suitePath, this.suiteName, String.valueOf(numberOfTests));
      this.sendMessage(message);
   }

   public void notifyStartOf(MunitTest test) {
      String message = MessageBuilder.testStartMessage(this.runToken, this.suitePath, this.suiteName, test.getName());
      this.sendMessage(message);
   }

   public void notify(TestResult testResult) {
      String message;
      if (testResult.getError() != null) {
         message = MessageBuilder.testErrorMessage(this.runToken, this.suitePath, this.suiteName, testResult.getTestName(), testResult.getError().getFullMessage());
      } else if (testResult.getFailure() != null) {
         message = MessageBuilder.testFailureMessage(this.runToken, this.suitePath, this.suiteName, testResult.getTestName(), testResult.getFailure().getFullMessage());
      } else {
         message = MessageBuilder.testFinishedMessage(this.runToken, this.suitePath, this.suiteName, testResult.getTestName());
      }

      this.sendMessage(message);
   }

   public void notifyIgnored(TestResult testResult) {
      String message = MessageBuilder.testIgnoredMessage(this.runToken, this.suitePath, this.suiteName, testResult.getName());
      this.sendMessage(message);
   }

   public void notifyEnd(SuiteResult result) {
   }

   public void notifySuiteFinished() {
      String message = MessageBuilder.testSuiteFinishedMessage(this.runToken, this.suitePath, this.suiteName);
      this.sendMessage(message);
   }

   public void notifyApplicationPaths(String flowPaths, String subFlowPaths, String batchPaths) {
      String message = MessageBuilder.applicationPathsMessage(this.runToken, flowPaths, subFlowPaths, batchPaths);
      this.sendMessage(message);
   }

   public void notifyRunFinish() {
      String message = MessageBuilder.runFinishedMessage(this.runToken);
      this.sendMessage(message);
   }

   private void sendMessage(String message) {
      try {
         this.out.writeObject(message);
         this.out.flush();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }
}
