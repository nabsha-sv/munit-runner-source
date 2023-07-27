package org.mule.munit.runner.exception;

public class FlowException extends RuntimeException {
   private String flowName;

   public FlowException(String flowName, Throwable cause) {
      super(cause);
      this.flowName = flowName;
   }

   public String getFlowName() {
      return this.flowName;
   }
}
