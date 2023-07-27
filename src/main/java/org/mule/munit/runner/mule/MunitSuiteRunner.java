package org.mule.munit.runner.mule;

import java.util.List;
import org.mule.api.MuleContext;
import org.mule.munit.runner.MuleContextManager;
import org.mule.munit.runner.MunitRunner;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.notification.NotificationListener;
import org.mule.munit.runner.mule.result.notification.SuiteRunnerEventListenerAdapter;
import org.mule.munit.runner.output.DefaultOutputHandler;
import org.mule.munit.runner.output.TestOutputHandler;
import org.mule.munit.runner.spring.config.model.MockingConfiguration;

public class MunitSuiteRunner {
   private MuleContext muleContext;
   private MunitSuite suite;
   private TestOutputHandler handler;
   private MuleContextManager muleContextManager;

   public MunitSuiteRunner(String resources, List testNameList, String projectName, MockingConfiguration configuration) {
      this.handler = new DefaultOutputHandler();

      try {
         this.muleContextManager = new MuleContextManager(configuration);
         this.muleContext = this.muleContextManager.startMule(resources, projectName);
         this.suite = (MunitSuite)(new MunitSuiteBuilder(this.muleContext, this.handler)).build(resources, testNameList);
      } catch (Exception var6) {
         this.muleContextManager.killMule(this.muleContext);
         throw new RuntimeException(var6);
      }
   }

   public MunitSuiteRunner(String resources, List testNameList, String projectName) {
      this(resources, testNameList, projectName, (MockingConfiguration)null);
   }

   public SuiteResult run() {
      return (SuiteResult)(new MunitRunner(this.handler, this.muleContextManager, this.muleContext) {
         protected SuiteResult runSuite() throws Exception {
            return MunitSuiteRunner.this.suite.run();
         }

         protected String getSuiteName() {
            return MunitSuiteRunner.this.suite.getName();
         }
      }).run();
   }

   public void setNotificationListener(NotificationListener notificationListener) {
      this.suite.setSuiteRunnerEventListener(new SuiteRunnerEventListenerAdapter(notificationListener));
   }

   public int getNumberOfTests() {
      return this.suite.getNumberOfTests();
   }

   public void setHandler(TestOutputHandler handler) {
      this.handler = handler;
   }
}
