package org.mule.munit.runner.exception;

import java.util.Collection;
import java.util.Iterator;
import org.mule.api.MuleContext;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.construct.AbstractFlowConstruct;
import org.mule.exception.DefaultMessagingExceptionStrategy;
import org.mule.exception.RollbackMessagingExceptionStrategy;
import org.mule.munit.assertion.processors.MunitTestFlow;
import org.mule.munit.common.util.ProxyExtractor;

public class ExceptionStrategyReplacer {
   public static final String ID = "__exceptionStrategyReplacer";
   private MuleContext context;

   public void replace() {
      if (null == this.context) {
         throw new IllegalStateException("The bean __exceptionStrategyReplacer has not been correctly initialized. MuleContext missing.");
      } else {
         Collection flows = this.context.getRegistry().lookupFlowConstructs();
         Iterator var2 = flows.iterator();

         while(true) {
            FlowConstruct flow;
            MessagingExceptionHandler originalHandler;
            do {
               do {
                  if (!var2.hasNext()) {
                     return;
                  }

                  flow = (FlowConstruct)var2.next();
                  originalHandler = flow.getExceptionListener();
               } while(originalHandler == null);
            } while(flow instanceof MunitTestFlow && originalHandler instanceof DefaultMessagingExceptionStrategy);

            AbstractFlowConstruct abstractFlowConstruct = (AbstractFlowConstruct)ProxyExtractor.extractIfProxy(flow);
            MessagingExceptionHandler munitHandler = this.getMessagingExceptionHandler(originalHandler);
            abstractFlowConstruct.setExceptionListener(munitHandler);
         }
      }
   }

   private MessagingExceptionHandler getMessagingExceptionHandler(MessagingExceptionHandler originalHandler) {
      return (MessagingExceptionHandler)(originalHandler instanceof RollbackMessagingExceptionStrategy ? originalHandler : new MunitMessagingExceptionHandler(originalHandler));
   }

   public void setMuleContext(MuleContext muleContext) {
      this.context = muleContext;
   }
}
