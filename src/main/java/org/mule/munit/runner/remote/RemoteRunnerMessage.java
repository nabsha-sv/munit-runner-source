package org.mule.munit.runner.remote;

import org.apache.commons.lang.Validate;

/** @deprecated */
@Deprecated
public class RemoteRunnerMessage {
   public static final String FIELD_TOKEN = new String(new byte[]{27});
   private String messageId;
   private String runToken;
   private String suitePath;
   private String suiteName;
   private String testName;
   private String freeMessage;
   private String stackTrace;
   private String jsonMessage;

   public RemoteRunnerMessage(String messageId, String runToken) {
      Validate.notEmpty(messageId, "The Remote runner message id must not be null nor empty");
      Validate.notEmpty(runToken, "The run token");
      this.messageId = messageId;
      this.runToken = runToken;
   }

   public void setSuitePath(String suitePath) {
      this.suitePath = suitePath;
   }

   public void setSuiteName(String suiteName) {
      this.suiteName = suiteName;
   }

   public void setTestName(String testName) {
      this.testName = testName;
   }

   public void setFreeMessage(String freeMessage) {
      this.freeMessage = freeMessage;
   }

   public void setStackTrace(String stackTrace) {
      this.stackTrace = stackTrace;
   }

   public void setJsonMessage(String jsonMessage) {
      this.jsonMessage = jsonMessage;
   }

   public String build() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.messageId).append(FIELD_TOKEN).append(this.runToken).append(FIELD_TOKEN).append(this.suitePath).append(FIELD_TOKEN).append(this.suiteName).append(FIELD_TOKEN).append(this.testName).append(FIELD_TOKEN).append(this.freeMessage).append(FIELD_TOKEN).append(this.stackTrace).append(FIELD_TOKEN).append(this.jsonMessage);
      return builder.toString();
   }
}
