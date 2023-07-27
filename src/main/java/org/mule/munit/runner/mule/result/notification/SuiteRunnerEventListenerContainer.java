package org.mule.munit.runner.mule.result.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;

public class SuiteRunnerEventListenerContainer implements SuiteRunnerEventListener {
   private List suiteRunnerEventListeners = new ArrayList();

   public void notifySuiteStart(String path) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifySuiteStart(path);
      }

   }

   public void notifyTestStart(MunitTest test) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifyTestStart(test);
      }

   }

   public void notifyNumberOfTests(int numberOfTests) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifyNumberOfTests(numberOfTests);
      }

   }

   public void notifyTestResult(TestResult testResult) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifyTestResult(testResult);
      }

   }

   public void notifyTestIgnored(TestResult testResult) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifyTestIgnored(testResult);
      }

   }

   public void notifySuiteEnd(SuiteResult result) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifySuiteEnd(result);
      }

   }

   public void notifySuiteStartFailure(Notification notification) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifySuiteStartFailure(notification);
      }

   }

   public void notifyBeforeSuiteFailure(Notification notification) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifyBeforeSuiteFailure(notification);
      }

   }

   public void notifyBeforeSuiteError(Notification notification) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifyBeforeSuiteError(notification);
      }

   }

   public void notifyAfterSuiteFailure(Notification notification) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifyAfterSuiteFailure(notification);
      }

   }

   public void notifyAfterSuiteError(Notification notification) {
      Iterator var2 = this.suiteRunnerEventListeners.iterator();

      while(var2.hasNext()) {
         SuiteRunnerEventListener suiteRunnerEventListener = (SuiteRunnerEventListener)var2.next();
         suiteRunnerEventListener.notifyAfterSuiteError(notification);
      }

   }

   public void addNotificationListener(SuiteRunnerEventListener listener) {
      this.suiteRunnerEventListeners.add(listener);
   }
}
