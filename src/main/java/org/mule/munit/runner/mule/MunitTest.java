package org.mule.munit.runner.mule;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.construct.FlowConstruct;
import org.mule.munit.assertion.processors.MunitFlow;
import org.mule.munit.assertion.processors.MunitTestFlow;
import org.mule.munit.common.MunitCore;
import org.mule.munit.common.exception.MunitError;
import org.mule.munit.runner.MuleEventBuilderWrapper;
import org.mule.munit.runner.mule.result.TestResult;
import org.mule.munit.runner.mule.result.notification.Notification;
import org.mule.munit.runner.output.TestOutputHandler;

public class MunitTest {
   private transient Log logger = LogFactory.getLog(this.getClass());
   private List before;
   private List after;
   private MunitTestFlow test;
   private TestOutputHandler outputHandler;
   private MuleContext muleContext;

   public static String stack2string(Throwable e) {
      try {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         e.printStackTrace(pw);
         return sw.toString();
      } catch (Exception var3) {
         return "";
      }
   }

   public MunitTest(List before, MunitTestFlow test, List after, TestOutputHandler outputHandler, MuleContext muleContext) {
      this.before = before;
      this.after = after;
      this.test = test;
      this.outputHandler = outputHandler;
      this.muleContext = muleContext;
   }

   public String getName() {
      return this.test.getName();
   }

   public boolean isIgnore() {
      return this.test.isIgnore();
   }

   public TestResult run() {
      TestResult result = new TestResult(this.getName());
      this.logger.debug("About to run MUnit test: " + this.getName());
      if (this.test.isIgnore()) {
         this.logger.debug("MUnit test: " + this.getName() + " is ignored it won't run.");
         result.setSkipped(true);
         return result;
      } else {
         long start = System.currentTimeMillis();
         MuleEvent event = this.muleEvent(this.test);

         try {
            this.runBefore(event);
            this.showDescription();
            this.test.process(event);
            if (StringUtils.isNotBlank(this.test.getExpectException())) {
               Assert.fail("Exception matching '" + this.test.getExpectException() + "', but wasn't thrown");
            }
         } catch (AssertionError var16) {
            result.setFailure(this.buildNotifcationFrom(var16));
         } catch (MuleException var17) {
            MuleException e = var17;

            Notification notification;
            try {
               if (!this.test.expectException(e, event)) {
                  Throwable cause = e.getCause();
                  if (cause != null && AssertionError.class.isAssignableFrom(cause.getClass())) {
                     cause.setStackTrace((StackTraceElement[])MunitCore.buildMuleStackTrace(event.getMuleContext()).toArray(new StackTraceElement[0]));
                     notification = this.buildNotifcationFrom(cause);
                     result.setFailure(notification);
                  } else {
                     e.setStackTrace((StackTraceElement[])MunitCore.buildMuleStackTrace(event.getMuleContext()).toArray(new StackTraceElement[0]));
                     notification = this.buildNotifcationFrom(e);
                     result.setError(notification);
                  }
               }
            } catch (AssertionError var14) {
               var14.setStackTrace((StackTraceElement[])MunitCore.buildMuleStackTrace(event.getMuleContext()).toArray(new StackTraceElement[0]));
               result.setFailure(this.buildNotifcationFrom(var14));
            } catch (MunitError var15) {
               var15.setStackTrace((StackTraceElement[])MunitCore.buildMuleStackTrace(event.getMuleContext()).toArray(new StackTraceElement[0]));
               notification = this.buildNotifcationFrom(var15);
               result.setError(notification);
            }
         } finally {
            MunitCore.reset(event.getMuleContext());
            this.runAfter(result, event);
         }

         long end = System.currentTimeMillis();
         result.setTime(new Float((float)(end - start)) / 1000.0F);
         return result;
      }
   }

   private Notification buildNotifcationFrom(Throwable t) {
      return new Notification(t.getMessage(), stack2string(t));
   }

   private void runBefore(MuleEvent event) throws MuleException {
      this.logger.debug("Running before test scopes...");
      this.run(event, this.before);
   }

   private void runAfter(TestResult result, MuleEvent event) {
      this.logger.debug("Running after test scopes...");

      try {
         this.run(event, this.after);
      } catch (MuleException var4) {
         result.setError(this.buildNotifcationFrom(var4));
      } catch (AssertionError var5) {
         result.setFailure(this.buildNotifcationFrom(var5));
      }

   }

   private void run(MuleEvent event, List flows) throws MuleException {
      if (flows != null) {
         Iterator var3 = flows.iterator();

         while(var3.hasNext()) {
            MunitFlow flow = (MunitFlow)var3.next();
            this.outputHandler.printDescription(flow.getName(), flow.getDescription());
            flow.process(event);
         }
      }

   }

   private void showDescription() {
      this.outputHandler.printDescription(this.test.getName(), this.test.getDescription());
   }

   protected MuleEvent muleEvent(FlowConstruct flowConstruct) {
      return MuleEventBuilderWrapper.muleEvent(this.muleContext, flowConstruct);
   }
}
