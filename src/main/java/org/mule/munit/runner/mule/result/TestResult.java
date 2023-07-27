package org.mule.munit.runner.mule.result;

import org.mule.munit.runner.mule.result.notification.Notification;

public class TestResult implements MunitResult {
   private String name;
   private Notification failure;
   private Notification error;
   private float time;
   private boolean skipped;

   public TestResult(String name) {
      this.name = name;
   }

   public String getTestName() {
      return this.name;
   }

   public boolean hasSucceeded() {
      return this.error == null && this.failure == null;
   }

   public int getNumberOfFailures() {
      return this.failure != null ? 1 : 0;
   }

   public int getNumberOfErrors() {
      return this.error != null ? 1 : 0;
   }

   public int getNumberOfTests() {
      return 1;
   }

   public float getTime() {
      return this.time;
   }

   public int getNumberOfSkipped() {
      return this.skipped ? 1 : 0;
   }

   public Notification getFailure() {
      return this.failure;
   }

   public void setFailure(Notification failure) {
      this.failure = failure;
   }

   public Notification getError() {
      return this.error;
   }

   public void setError(Notification error) {
      this.error = error;
   }

   public void setTime(float time) {
      this.time = time;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setSkipped(boolean skipped) {
      this.skipped = skipped;
   }

   public boolean isSkipped() {
      return this.skipped;
   }
}
