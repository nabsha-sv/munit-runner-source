package org.mule.munit.runner.java;

import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.construct.FlowConstruct;
import org.mule.munit.assertion.processors.MunitFlow;
import org.mule.munit.assertion.processors.MunitTestFlow;
import org.mule.munit.common.MunitCore;
import org.mule.munit.runner.MuleEventBuilderWrapper;
import org.mule.munit.runner.output.DefaultOutputHandler;
import org.mule.munit.runner.output.TestOutputHandler;

public class MunitTest extends TestCase {
   private List before;
   private MunitTestFlow flow;
   private List after;
   private MuleContext muleContext;
   private TestOutputHandler outputHandler = new DefaultOutputHandler();

   public MunitTest(List before, MunitTestFlow flow, List after) {
      this.before = before;
      this.flow = flow;
      this.after = after;
      this.muleContext = flow.getMuleContext();
   }

   public String getName() {
      return this.flow.getName();
   }

   public int countTestCases() {
      return 1;
   }

   protected void runTest() throws Throwable {
      if (!this.flow.isIgnore()) {
         MuleEvent event = this.muleEvent(this.flow);
         this.run(event, this.before);
         this.showDescription();

         try {
            this.flow.process(event);
         } catch (Throwable var6) {
            if (!this.flow.expectException(var6, event)) {
               var6.setStackTrace((StackTraceElement[])MunitCore.buildMuleStackTrace(event.getMuleContext()).toArray(new StackTraceElement[0]));
               throw var6;
            }
         } finally {
            MunitCore.reset(this.muleContext);
            this.run(event, this.after);
         }

      }
   }

   private void run(MuleEvent event, List flows) throws MuleException {
      if (flows != null) {
         Iterator var3 = flows.iterator();

         while(var3.hasNext()) {
            MunitFlow flow = (MunitFlow)var3.next();
            this.outputHandler.printDescription(flow.getName(), flow.getDescription());
            flow.process(event);
         }
      }

   }

   private void showDescription() {
      this.outputHandler.printDescription(this.flow.getName(), this.flow.getDescription().replaceAll("\\.", "\\.%n"));
   }

   protected MuleEvent muleEvent(FlowConstruct flowConstruct) {
      return MuleEventBuilderWrapper.muleEvent(this.muleContext, flowConstruct);
   }
}
