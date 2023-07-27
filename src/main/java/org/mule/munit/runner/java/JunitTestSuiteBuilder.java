package org.mule.munit.runner.java;

import java.util.Iterator;
import java.util.List;
import junit.framework.TestSuite;
import org.mule.api.MuleContext;
import org.mule.munit.assertion.processors.MunitTestFlow;
import org.mule.munit.runner.SuiteBuilder;

public class JunitTestSuiteBuilder extends SuiteBuilder {
   protected JunitTestSuiteBuilder(MuleContext muleContext) {
      super(muleContext);
   }

   protected TestSuite createSuite(String name) {
      TestSuite testSuite = new TestSuite(name);
      Iterator var3 = this.tests.iterator();

      while(var3.hasNext()) {
         MunitTest test = (MunitTest)var3.next();
         testSuite.addTest(test);
      }

      return testSuite;
   }

   protected MunitTest test(List beforeTest, MunitTestFlow test, List afterTest) {
      return new MunitTest(beforeTest, test, afterTest);
   }
}
