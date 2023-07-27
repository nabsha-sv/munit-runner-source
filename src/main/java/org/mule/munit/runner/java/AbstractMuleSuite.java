package org.mule.munit.runner.java;

import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.mule.munit.runner.properties.MUnitUserPropertiesManager;

@RunWith(MuleSuiteRunner.class)
public abstract class AbstractMuleSuite extends TestSuite {
   public AbstractMuleSuite() {
      MUnitUserPropertiesManager.addUserPropertyToSystem("mule.testingMode", "true");
   }

   public abstract String getConfigResources();
}
