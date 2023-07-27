package org.mule.munit.runner.mule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.mule.munit.runner.mule.result.SuiteResult;
import org.mule.munit.runner.mule.result.TestResult;
import org.mule.munit.runner.mule.result.notification.DummySuiteRunnerEventListener;
import org.mule.munit.runner.mule.result.notification.SuiteRunnerEventListener;

public class MunitSuite {
   private String name;
   private List munitTests = new ArrayList();
   private SuiteRunnerEventListener suiteRunnerEventListener = new DummySuiteRunnerEventListener();

   public MunitSuite(String name) {
      this.name = name;
   }

   public void add(MunitTest test) {
      this.munitTests.add(test);
   }

   public SuiteResult run() throws Exception {
      SuiteResult result = new SuiteResult(this.name);
      this.suiteRunnerEventListener.notifyNumberOfTests(this.getNumberOfTests());

      TestResult testResult;
      for(Iterator var2 = this.munitTests.iterator(); var2.hasNext(); this.suiteRunnerEventListener.notifyTestResult(testResult)) {
         MunitTest test = (MunitTest)var2.next();
         this.suiteRunnerEventListener.notifyTestStart(test);
         testResult = test.run();
         result.add(testResult);
         if (testResult.isSkipped()) {
            this.suiteRunnerEventListener.notifyTestIgnored(testResult);
         }
      }

      return result;
   }

   public void setSuiteRunnerEventListener(SuiteRunnerEventListener suiteRunnerEventListener) {
      if (suiteRunnerEventListener == null) {
         throw new IllegalArgumentException();
      } else {
         this.suiteRunnerEventListener = suiteRunnerEventListener;
      }
   }

   public int getNumberOfTests() {
      return this.munitTests.size();
   }

   public String getName() {
      return this.name;
   }
}
