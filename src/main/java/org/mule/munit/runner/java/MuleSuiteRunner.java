package org.mule.munit.runner.java;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.runner.Describable;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mule.api.MuleContext;
import org.mule.munit.runner.MuleContextManager;
import org.mule.munit.runner.MunitRunner;
import org.mule.munit.runner.output.DefaultOutputHandler;
import org.mule.munit.runner.spring.config.model.MockingConfiguration;

public class MuleSuiteRunner extends Runner implements Filterable, Sortable {
   private TestSuite testSuite;
   private MuleContext muleContext;
   private MuleContextManager muleContextManager = new MuleContextManager((MockingConfiguration)null);

   public MuleSuiteRunner(Class testClass) {
      try {
         Method getConfigResources = testClass.getMethod("getConfigResources");
         String resources = (String)getConfigResources.invoke(testClass.newInstance());
         this.muleContext = this.muleContextManager.startMule(resources, "");
         this.testSuite = (TestSuite)(new JunitTestSuiteBuilder(this.muleContext)).build(testClass.getSimpleName());
      } catch (Exception var4) {
         this.muleContextManager.killMule(this.muleContext);
         throw new RuntimeException(var4);
      }
   }

   public Description getDescription() {
      return makeDescription(this.testSuite);
   }

   public TestListener createAdaptingListener(RunNotifier notifier) {
      return new OldTestClassAdaptingListener(notifier);
   }

   public void run(RunNotifier notifier) {
      final TestResult result = new TestResult();
      result.addListener(this.createAdaptingListener(notifier));
      (new MunitRunner(new DefaultOutputHandler(), this.muleContextManager, this.muleContext) {
         protected Void runSuite() throws Exception {
            MuleSuiteRunner.this.testSuite.run(result);
            return null;
         }

         protected String getSuiteName() {
            return MuleSuiteRunner.this.testSuite.getName();
         }
      }).run();
   }

   private static Description makeDescription(Test test) {
      if (!(test instanceof TestSuite)) {
         TestCase mt = (TestCase)test;
         return Description.createTestDescription(mt.getClass(), mt.getName());
      } else {
         TestSuite ts = (TestSuite)test;
         String name = ts.getName() == null ? createSuiteDescription(ts) : ts.getName();
         Description description = Description.createSuiteDescription(name, new Annotation[0]);
         int n = ts.testCount();

         for(int i = 0; i < n; ++i) {
            Description made = makeDescription(ts.testAt(i));
            description.addChild(made);
         }

         return description;
      }
   }

   private static String createSuiteDescription(TestSuite ts) {
      int count = ts.countTestCases();
      String example = count == 0 ? "" : String.format(" [example: %s]", ts.testAt(0));
      return String.format("TestSuite with %s tests%s", count, example);
   }

   public void filter(Filter filter) throws NoTestsRemainException {
      TestSuite filtered = new TestSuite(this.testSuite.getName());
      int n = this.testSuite.testCount();

      for(int i = 0; i < n; ++i) {
         Test test = this.testSuite.testAt(i);
         if (filter.shouldRun(makeDescription(test))) {
            filtered.addTest(test);
         }
      }

      this.testSuite = filtered;
   }

   public void sort(Sorter sorter) {
      if (this.testSuite instanceof Sortable) {
         Sortable adapter = (Sortable)this.testSuite;
         adapter.sort(sorter);
      }

   }

   private final class OldTestClassAdaptingListener implements TestListener {
      private final RunNotifier fNotifier;

      private OldTestClassAdaptingListener(RunNotifier notifier) {
         this.fNotifier = notifier;
      }

      public void endTest(Test test) {
         this.fNotifier.fireTestFinished(this.asDescription(test));
      }

      public void startTest(Test test) {
         this.fNotifier.fireTestStarted(this.asDescription(test));
      }

      public void addError(Test test, Throwable t) {
         Failure failure = new Failure(this.asDescription(test), t);
         this.fNotifier.fireTestFailure(failure);
      }

      private Description asDescription(Test test) {
         if (test instanceof Describable) {
            Describable facade = (Describable)test;
            return facade.getDescription();
         } else {
            return Description.createTestDescription(this.getEffectiveClass(test), this.getName(test));
         }
      }

      private Class getEffectiveClass(Test test) {
         return test.getClass();
      }

      private String getName(Test test) {
         return test instanceof TestCase ? ((TestCase)test).getName() : test.toString();
      }

      public void addFailure(Test test, AssertionFailedError t) {
         this.addError(test, t);
      }

      // $FF: synthetic method
      OldTestClassAdaptingListener(RunNotifier x1, Object x2) {
         this(x1);
      }
   }
}
