package org.mule.munit.runner.mule;

import java.util.List;
import org.mule.api.MuleContext;
import org.mule.munit.runner.MuleContextManager;
import org.mule.munit.runner.MunitRunner;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.notification.DummySuiteRunnerEventListener;
import org.mule.munit.runner.mule.result.notification.Notification;
import org.mule.munit.runner.mule.result.notification.SuiteRunnerEventListener;
import org.mule.munit.runner.output.DefaultOutputHandler;
import org.mule.munit.runner.output.TestOutputHandler;
import org.mule.munit.runner.spring.config.model.MockingConfiguration;

public class SuiteRunner {
   private MuleContext muleContext;
   private MunitSuite suite;
   private TestOutputHandler handler;
   private MuleContextManager muleContextManager;
   private SuiteRunnerEventListener suiteRunnerEventListener;

   public SuiteRunner(String resources, List testNameList, String projectName) {
      this(resources, testNameList, projectName, (MockingConfiguration)null, new DummySuiteRunnerEventListener());
   }

   public SuiteRunner(String resources, List testNameList, String projectName, SuiteRunnerEventListener suiteRunnerEventListener) {
      this(resources, testNameList, projectName, (MockingConfiguration)null, suiteRunnerEventListener);
   }

   public SuiteRunner(String resources, List testNameList, String projectName, MockingConfiguration configuration, SuiteRunnerEventListener suiteRunnerEventListener) {
      this.handler = new DefaultOutputHandler();
      this.suiteRunnerEventListener = new DummySuiteRunnerEventListener();
      this.suiteRunnerEventListener = suiteRunnerEventListener;

      try {
         this.muleContextManager = new MuleContextManager(configuration);
         this.muleContext = this.muleContextManager.startMule(resources, projectName);
         this.suite = (MunitSuite)(new MunitSuiteBuilder(this.muleContext, this.handler)).build(resources, testNameList);
      } catch (Throwable var7) {
         this.handleSuiteStartFailure(var7);
         throw new RuntimeException(var7);
      }
   }

   public SuiteResult run() {
      MunitRunner munitRunner = new MunitRunner(this.handler, this.muleContextManager, this.muleContext) {
         protected SuiteResult runSuite() throws Exception {
            return SuiteRunner.this.suite.run();
         }

         protected String getSuiteName() {
            return SuiteRunner.this.suite.getName();
         }
      };
      munitRunner.setSuiteRunnerEventListener(this.suiteRunnerEventListener);
      this.suite.setSuiteRunnerEventListener(this.suiteRunnerEventListener);
      this.suiteRunnerEventListener.notifySuiteStart(this.suite.getName());
      SuiteResult suiteResult = (SuiteResult)munitRunner.run();
      this.suiteRunnerEventListener.notifySuiteEnd(suiteResult);
      return suiteResult;
   }

   private void handleSuiteStartFailure(Throwable originalFailure) {
      try {
         this.suiteRunnerEventListener.notifySuiteStartFailure(new Notification(originalFailure.getMessage(), MunitTest.stack2string(originalFailure)));
         this.muleContextManager.killMule(this.muleContext);
      } catch (Throwable var3) {
         throw new RuntimeException(originalFailure);
      }
   }

   public void setSuiteRunnerEventListener(SuiteRunnerEventListener suiteRunnerEventListener) {
      this.suiteRunnerEventListener = suiteRunnerEventListener;
   }

   public int getNumberOfTests() {
      return this.suite.getNumberOfTests();
   }

   public void setHandler(TestOutputHandler handler) {
      this.handler = handler;
   }
}
