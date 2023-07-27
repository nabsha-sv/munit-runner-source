package org.mule.munit.runner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.construct.FlowConstruct;
import org.mule.construct.Flow;
import org.mule.construct.flow.DefaultFlowProcessingStrategy;

public class MuleEventBuilderWrapper {
   private static final String BUILD_EVENT_METHOD = "buildMuleEvent";

   public static MuleEvent muleEvent(MuleContext muleContext, FlowConstruct flow) {
      try {
         Method method = MunitMuleEventBuilder.class.getMethod("buildMuleEvent", MuleContext.class, FlowConstruct.class);
         MuleEvent muleEvent = buildMuleEvent(muleContext, flow, method);
         if (muleEvent != null) {
            return muleEvent;
         }
      } catch (NoSuchMethodException var4) {
      }

      return MunitMuleEventBuilder.buildGenericMuleEvent(muleContext);
   }

   protected static void setProcessingStrategyIfPossible(FlowConstruct flowConstruct) {
      if (flowConstruct instanceof Flow) {
         ((Flow)flowConstruct).setProcessingStrategy(new DefaultFlowProcessingStrategy());
      }

   }

   protected static MuleEvent buildMuleEvent(MuleContext muleContext, FlowConstruct flowConstruct, Method method) {
      try {
         if (method != null) {
            setProcessingStrategyIfPossible(flowConstruct);
            return (MuleEvent)method.invoke((Object)null, muleContext, flowConstruct);
         }
      } catch (IllegalAccessException var4) {
         var4.printStackTrace();
      } catch (InvocationTargetException var5) {
         var5.printStackTrace();
      }

      return null;
   }
}
