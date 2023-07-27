package org.mule.munit.runner.mule.result;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SuiteResult implements MunitResult {
   private List results = new ArrayList();
   private String name;

   public SuiteResult(String name) {
      this.name = name;
   }

   public boolean hasSucceeded() {
      Iterator var1 = this.results.iterator();

      MunitResult result;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         result = (MunitResult)var1.next();
      } while(result.hasSucceeded());

      return false;
   }

   public int getNumberOfFailures() {
      int failures = 0;

      MunitResult result;
      for(Iterator var2 = this.results.iterator(); var2.hasNext(); failures += result.getNumberOfFailures()) {
         result = (MunitResult)var2.next();
      }

      return failures;
   }

   public int getNumberOfErrors() {
      int errors = 0;

      MunitResult result;
      for(Iterator var2 = this.results.iterator(); var2.hasNext(); errors += result.getNumberOfErrors()) {
         result = (MunitResult)var2.next();
      }

      return errors;
   }

   public int getNumberOfTests() {
      return this.results.size();
   }

   public float getTime() {
      float total = 0.0F;

      MunitResult result;
      for(Iterator var2 = this.results.iterator(); var2.hasNext(); total += result.getTime()) {
         result = (MunitResult)var2.next();
      }

      return total;
   }

   public int getNumberOfSkipped() {
      int skipped = 0;

      MunitResult result;
      for(Iterator var2 = this.results.iterator(); var2.hasNext(); skipped += result.getNumberOfSkipped()) {
         result = (MunitResult)var2.next();
      }

      return skipped;
   }

   public String getTestName() {
      return this.name;
   }

   public List getFailingTests() {
      List failingTests = new ArrayList();
      Iterator var2 = this.results.iterator();

      while(var2.hasNext()) {
         MunitResult result = (MunitResult)var2.next();
         if (result.getNumberOfFailures() > 0) {
            failingTests.add(result);
         }
      }

      return failingTests;
   }

   public List getErrorTests() {
      List errorTests = new ArrayList();
      Iterator var2 = this.results.iterator();

      while(var2.hasNext()) {
         MunitResult result = (MunitResult)var2.next();
         if (result.getNumberOfErrors() > 0) {
            errorTests.add(result);
         }
      }

      return errorTests;
   }

   public void add(MunitResult result) {
      this.results.add(result);
   }
}
