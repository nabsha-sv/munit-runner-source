package org.mule.munit.runner.simple;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.munit.runner.MuleContextManager;
import org.mule.munit.runner.spring.config.model.MockingConfiguration;

public class MunitSimpleRunner {
   private transient Log log = LogFactory.getLog(this.getClass());
   private String resources;
   private List actions;
   private String projectName;

   public MunitSimpleRunner(String resources, List actions, String projectName) {
      this.resources = resources;
      this.actions = actions;
      this.projectName = projectName;
   }

   public void run() {
      this.log.debug("Counting application flow paths: " + (new Date()).toString());
      MuleContext muleContext = null;
      MuleContextManager muleContextManager = new MuleContextManager(this.createConfiguration());

      try {
         muleContext = muleContextManager.createMule(this.resources, this.projectName);
         Iterator var3 = this.actions.iterator();

         while(var3.hasNext()) {
            MunitSimpleRunnerAction action = (MunitSimpleRunnerAction)var3.next();
            action.execute(muleContext);
         }
      } catch (Exception var8) {
         throw new RuntimeException(var8);
      } finally {
         if (null != muleContext) {
            muleContextManager.killMule(muleContext);
         }

         this.log.debug("Application flow paths count done: " + (new Date()).toString());
      }

   }

   public String getResources() {
      return this.resources;
   }

   private MockingConfiguration createConfiguration() {
      return new MockingConfiguration(false, new ArrayList(), false, (Properties)null);
   }
}
