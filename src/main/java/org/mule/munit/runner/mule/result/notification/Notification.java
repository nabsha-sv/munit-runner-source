package org.mule.munit.runner.mule.result.notification;

public class Notification {
   private String fullMessage;
   private String shortMessage;

   public Notification(String shortMessage, String fullMessage) {
      this.shortMessage = shortMessage;
      this.fullMessage = fullMessage;
   }

   public String getFullMessage() {
      return this.fullMessage;
   }

   public String getShortMessage() {
      return this.shortMessage;
   }
}
