package org.mule.munit.runner.mule.result;

public interface MunitResult {
   String getTestName();

   boolean hasSucceeded();

   int getNumberOfFailures();

   int getNumberOfErrors();

   int getNumberOfTests();

   float getTime();

   int getNumberOfSkipped();
}
