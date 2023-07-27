package org.mule.munit.runner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.mule.api.MuleContext;
import org.mule.munit.assertion.processors.MunitAfterTest;
import org.mule.munit.assertion.processors.MunitBeforeTest;
import org.mule.munit.assertion.processors.MunitTestFlow;
import org.mule.munit.common.util.ProxyExtractor;

public abstract class SuiteBuilder {
   protected MuleContext muleContext;
   protected List tests = new ArrayList();

   protected abstract Object createSuite(String var1);

   protected abstract Object test(List var1, MunitTestFlow var2, List var3);

   protected SuiteBuilder(MuleContext muleContext) {
      this.muleContext = muleContext;
   }

   public Object build(String suiteName) {
      return this.build(suiteName, (List)null);
   }

   public Object build(String suiteName, List testNameList) {
      List before = this.lookupFlows(MunitBeforeTest.class);
      List after = this.lookupFlows(MunitAfterTest.class);
      Collection flowConstructs = this.lookupTests();
      Iterator var6 = flowConstructs.iterator();

      while(var6.hasNext()) {
         MunitTestFlow flowConstruct = (MunitTestFlow)var6.next();
         if (this.shouldRunTest(flowConstruct, testNameList)) {
            this.tests.add(this.test(before, flowConstruct, after));
         }
      }

      return this.createSuite(suiteName);
   }

   private Boolean shouldRunTest(MunitTestFlow munitTestFlow, List testNameList) {
      if (null != testNameList && !testNameList.isEmpty()) {
         Iterator var3 = testNameList.iterator();

         String testToRunName;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            testToRunName = (String)var3.next();
         } while(!StringUtils.isNotBlank(testToRunName) || !munitTestFlow.getName().matches(testToRunName));

         return true;
      } else {
         return true;
      }
   }

   private Collection lookupTests() {
      List tests = new ArrayList();
      Iterator var2 = this.lookupFlows(MunitTestFlow.class).iterator();

      while(var2.hasNext()) {
         Object testFlow = var2.next();
         tests.add((MunitTestFlow)ProxyExtractor.extractIfProxy(testFlow));
      }

      return tests;
   }

   private List lookupFlows(Class munitClass) {
      return new ArrayList(this.muleContext.getRegistry().lookupObjects(munitClass));
   }
}
