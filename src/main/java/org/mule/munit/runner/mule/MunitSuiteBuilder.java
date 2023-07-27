package org.mule.munit.runner.mule;

import java.util.Iterator;
import java.util.List;
import org.mule.api.MuleContext;
import org.mule.munit.assertion.processors.MunitTestFlow;
import org.mule.munit.runner.SuiteBuilder;
import org.mule.munit.runner.output.TestOutputHandler;

public class MunitSuiteBuilder extends SuiteBuilder {
   private TestOutputHandler handler;

   public MunitSuiteBuilder(MuleContext muleContext, TestOutputHandler handler) {
      super(muleContext);
      if (handler == null) {
         throw new IllegalArgumentException("Handler must not be null");
      } else {
         this.handler = handler;
      }
   }

   protected MunitSuite createSuite(String name) {
      MunitSuite suite = new MunitSuite(name);
      Iterator var3 = this.tests.iterator();

      while(var3.hasNext()) {
         MunitTest test = (MunitTest)var3.next();
         suite.add(test);
      }

      return suite;
   }

   protected MunitTest test(List beforeTest, MunitTestFlow test, List afterTest) {
      return new MunitTest(beforeTest, test, afterTest, this.handler, this.muleContext);
   }
}
