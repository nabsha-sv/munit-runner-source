package org.mule.munit.runner.remote;

import org.mule.munit.runner.mule.result.notification.Notification;

/** @deprecated */
@Deprecated
public interface RunNotificationListiner {
   void notifyRunStart();

   void notifySuiteStart();

   void notifySuiteStartFailure(Notification var1);

   void notifySuiteFinished();

   void notifyApplicationPaths(String var1, String var2, String var3);

   void notifyRunFinish();
}
