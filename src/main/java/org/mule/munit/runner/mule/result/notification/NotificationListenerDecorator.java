package org.mule.munit.runner.mule.result.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;

public class NotificationListenerDecorator implements NotificationListener {
   private List notificationListeners = new ArrayList();

   public void notifyRuntimeStartFailure(Notification notification) {
   }

   public void notifyStartOf(MunitTest test) {
      Iterator var2 = this.notificationListeners.iterator();

      while(var2.hasNext()) {
         NotificationListener notificationListener = (NotificationListener)var2.next();
         notificationListener.notifyStartOf(test);
      }

   }

   public void notify(TestResult testResult) {
      Iterator var2 = this.notificationListeners.iterator();

      while(var2.hasNext()) {
         NotificationListener notificationListener = (NotificationListener)var2.next();
         notificationListener.notify(testResult);
      }

   }

   public void notifyIgnored(TestResult testResult) {
      Iterator var2 = this.notificationListeners.iterator();

      while(var2.hasNext()) {
         NotificationListener notificationListener = (NotificationListener)var2.next();
         notificationListener.notifyIgnored(testResult);
      }

   }

   public void notifyEnd(SuiteResult result) {
      Iterator var2 = this.notificationListeners.iterator();

      while(var2.hasNext()) {
         NotificationListener notificationListener = (NotificationListener)var2.next();
         notificationListener.notifyEnd(result);
      }

   }

   public void addNotificationListener(NotificationListener listener) {
      this.notificationListeners.add(listener);
   }
}
