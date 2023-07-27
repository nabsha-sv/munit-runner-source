package org.mule.munit.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.construct.FlowConstruct;
import org.mule.munit.assertion.processors.MunitAfterSuite;
import org.mule.munit.assertion.processors.MunitBeforeSuite;
import org.mule.munit.assertion.processors.MunitFlow;
import org.mule.munit.runner.exception.BeforeSuiteException;
import org.mule.munit.runner.exception.FlowException;
import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.notification.DummySuiteRunnerEventListener;
import org.mule.munit.runner.mule.result.notification.Notification;
import org.mule.munit.runner.mule.result.notification.SuiteRunnerEventListener;
import org.mule.munit.runner.output.TestOutputHandler;

public abstract class MunitRunner {
   private transient Log logger = LogFactory.getLog(this.getClass());
   private TestOutputHandler handler;
   private MuleContext muleContext;
   private MuleContextManager muleContextManager;
   private SuiteRunnerEventListener suiteRunnerEventListener = new DummySuiteRunnerEventListener();

   public MunitRunner(TestOutputHandler handler, MuleContextManager muleContextManager, MuleContext muleContext) {
      this.handler = handler;
      this.muleContext = muleContext;
      this.muleContextManager = muleContextManager;
   }

   protected abstract Object runSuite() throws Exception;

   protected abstract String getSuiteName();

   public Object run() {
      this.logger.debug("About to run MUnit suite: " + this.getSuiteName() + " ...");
      this.handler.printTestName(this.getSuiteName());

      Object var2;
      try {
         this.processBeforeSuites();
         Object result = this.runSuite();
         this.logger.debug("Tests in MUnit suite: " + this.getSuiteName() + " run");
         var2 = result;
      } catch (BeforeSuiteException var7) {
         this.logger.error("Before suites execution failed", var7.getCause());
         throw new RuntimeException("Before suites execution failed", var7.getCause());
      } catch (Throwable var8) {
         this.logger.error("Could not Run the suite: " + this.getSuiteName(), var8);
         throw new RuntimeException("Could not Run the suite", var8);
      } finally {
         this.processAfterSuites();
      }

      return var2;
   }

   public void setSuiteRunnerEventListener(SuiteRunnerEventListener suiteRunnerEventListener) {
      this.suiteRunnerEventListener = suiteRunnerEventListener;
   }

   private void processBeforeSuites() {
      try {
         this.logger.debug("Executing Before Suite scopes...");
         List beforeSuites = this.lookupFlows(MunitBeforeSuite.class);
         if (!beforeSuites.isEmpty()) {
            this.process(beforeSuites, this.muleEvent((FlowConstruct)beforeSuites.get(0)));
         }

      } catch (FlowException var4) {
         Throwable e = var4.getCause();
         Notification notification = this.getNotification(e, String.format("Before suite %s failed", var4.getFlowName()));
         if (e instanceof AssertionError) {
            this.suiteRunnerEventListener.notifyBeforeSuiteFailure(notification);
         } else {
            this.suiteRunnerEventListener.notifyBeforeSuiteError(notification);
         }

         throw new BeforeSuiteException(e);
      }
   }

   private void processAfterSuites() {
      try {
         this.logger.debug("Executing After Suite scopes...");
         List afterSuites = this.lookupFlows(MunitAfterSuite.class);
         if (!afterSuites.isEmpty()) {
            this.process(afterSuites, this.muleEvent((FlowConstruct)afterSuites.get(0)));
         }
      } catch (FlowException var7) {
         Throwable e = var7.getCause();
         this.logger.error("After suites execution failed", e);
         Notification notification = this.getNotification(e, String.format("After suite %s failed", var7.getFlowName()));
         if (e instanceof AssertionError) {
            this.suiteRunnerEventListener.notifyAfterSuiteFailure(notification);
         } else {
            this.suiteRunnerEventListener.notifyAfterSuiteError(notification);
         }

         throw new RuntimeException("After suites execution failed", e);
      } finally {
         this.muleContextManager.killMule(this.muleContext);
      }

   }

   private MuleEvent muleEvent(FlowConstruct flowConstruct) {
      return MuleEventBuilderWrapper.muleEvent(this.muleContext, flowConstruct);
   }

   private void process(Collection flowConstructs, MuleEvent event) throws FlowException {
      String flowName = "";

      try {
         Iterator var4 = flowConstructs.iterator();

         while(var4.hasNext()) {
            MunitFlow flowConstruct = (MunitFlow)var4.next();
            flowName = flowConstruct.getName();
            this.handler.printDescription(flowName, flowConstruct.getDescription());
            flowConstruct.process(event);
         }

      } catch (Throwable var6) {
         throw new FlowException(flowName, var6);
      }
   }

   private List lookupFlows(Class munitClass) {
      return new ArrayList(this.muleContext.getRegistry().lookupObjects(munitClass));
   }

   private Notification getNotification(Throwable cause, String message) {
      RuntimeException e = new RuntimeException(message, cause);
      return new Notification(e.getMessage(), MunitTest.stack2string(e));
   }
}
