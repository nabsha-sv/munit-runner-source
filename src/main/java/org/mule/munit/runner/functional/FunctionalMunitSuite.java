package org.mule.munit.runner.functional;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.registry.MuleRegistry;
import org.mule.modules.interceptor.matchers.AnyClassMatcher;
import org.mule.modules.interceptor.matchers.EqMatcher;
import org.mule.modules.interceptor.matchers.Matcher;
import org.mule.modules.interceptor.matchers.Matchers;
import org.mule.modules.interceptor.matchers.NotNullMatcher;
import org.mule.modules.interceptor.matchers.NullMatcher;
import org.mule.munit.common.MunitCore;
import org.mule.munit.common.mocking.Attribute;
import org.mule.munit.common.mocking.EndpointMocker;
import org.mule.munit.common.mocking.MessageProcessorMocker;
import org.mule.munit.common.mocking.MunitSpy;
import org.mule.munit.common.mocking.MunitVerifier;
import org.mule.munit.common.util.MunitMuleTestUtils;
import org.mule.munit.runner.MuleContextManager;
import org.mule.munit.runner.spring.config.model.MockingConfiguration;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;

public abstract class FunctionalMunitSuite {
   protected static MuleContext muleContext;
   private static MuleContextManager muleContextManager;

   public FunctionalMunitSuite() {
      try {
         if (muleContext == null || muleContext.isDisposed()) {
            String resources = this.getConfigResources();
            muleContextManager = new MuleContextManager(this.createConfiguration());
            muleContext = muleContextManager.createMule(resources, this.getApplicationName());
            this.muleContextCreated(muleContext);
            muleContext = muleContextManager.startMule(muleContext);
            this.muleContextStarted(muleContext);
         }

      } catch (Exception var2) {
         muleContextManager.killMule(muleContext);
         throw new RuntimeException(var2);
      }
   }

   protected void muleContextStarted(MuleContext muleContext) {
   }

   protected void muleContextCreated(MuleContext muleContext) {
   }

   private MockingConfiguration createConfiguration() {
      return new MockingConfiguration(this.haveToDisableInboundEndpoints(), this.getFlowsExcludedOfInboundDisabling(), this.haveToMockMuleConnectors(), this.getStartUpProperties());
   }

   protected List getFlowsExcludedOfInboundDisabling() {
      return new ArrayList();
   }

   protected boolean haveToDisableInboundEndpoints() {
      return true;
   }

   protected boolean haveToMockMuleConnectors() {
      return true;
   }

   @Before
   public final void __setUpMunit() {
      MunitCore.registerManager(muleContext);
   }

   @After
   public final void __restartMunit() {
      MunitCore.reset(muleContext);
   }

   protected String getConfigResources() {
      Properties props = this.loadProperties("/mule-deploy.properties");
      if (props != null && props.getProperty("config.resources") != null) {
         return props.getProperty("config.resources");
      } else {
         InputStream in = this.getClass().getResourceAsStream("/mule-config.xml");
         if (in != null) {
            return "mule-config.xml";
         } else {
            throw new IllegalStateException("Could not find mule-deploy.properties nor mule-config.xml file on classpath. Please add any of those files or override the getConfigResources() method to provide the resources by your own");
         }
      }
   }

   protected String getApplicationName() {
      return "";
   }

   protected final MuleEvent testEvent(Object payload) throws Exception {
      return new DefaultMuleEvent(this.muleMessageWithPayload(payload), MessageExchangePattern.REQUEST_RESPONSE, MunitMuleTestUtils.getTestFlow(muleContext));
   }

   protected final MuleMessage muleMessageWithPayload(Object payload) {
      return new DefaultMuleMessage(payload, muleContext);
   }

   protected final MessageProcessorMocker whenMessageProcessor(String name) {
      return (new MessageProcessorMocker(muleContext)).when(name);
   }

   protected MessageProcessorMocker whenFlow(String name) {
      return this.whenMessageProcessor("flow").withAttributes(new Attribute[]{Attribute.attribute("name").withValue(name)});
   }

   protected MessageProcessorMocker whenSubFlow(String name) {
      return this.whenMessageProcessor("sub-flow").withAttributes(new Attribute[]{Attribute.attribute("name").withValue(Matchers.contains(name))});
   }

   protected final MunitVerifier verifyCallOfMessageProcessor(String name) {
      return (new MunitVerifier(muleContext)).verifyCallOfMessageProcessor(name);
   }

   protected MunitVerifier verifyCallOfFlow(String name) {
      return this.verifyCallOfMessageProcessor("flow").withAttributes(new Attribute[]{Attribute.attribute("name").withValue(Matchers.contains(name))});
   }

   protected MunitVerifier verifyCallOfSubFlow(String name) {
      return this.verifyCallOfMessageProcessor("sub-flow").withAttributes(new Attribute[]{Attribute.attribute("name").withValue(Matchers.contains(name))});
   }

   protected final MunitSpy spyMessageProcessor(String name) {
      return (new MunitSpy(muleContext)).spyMessageProcessor(name);
   }

   protected final MuleEvent runFlow(String name, MuleEvent event) throws MuleException {
      MessageProcessor flow = this.lookupFlow(name, muleContext.getRegistry());
      if (flow == null) {
         throw new IllegalArgumentException("Flow " + name + " does not exist");
      } else {
         this.initialiseAndStartSubFlow(flow, event);
         return flow.process(event);
      }
   }

   private MessageProcessor lookupFlow(String name, MuleRegistry muleRegistry) {
      Method lookupMethod = MethodUtils.getMatchingAccessibleMethod(muleRegistry.getClass(), "lookupObject", new Class[]{String.class, Boolean.class});
      if (lookupMethod != null) {
         try {
            return (MessageProcessor)lookupMethod.invoke(muleRegistry, name, false);
         } catch (Exception var5) {
            throw new RuntimeException(var5);
         }
      } else {
         return (MessageProcessor)muleRegistry.get(name);
      }
   }

   private void initialiseAndStartSubFlow(MessageProcessor messageProcessor, MuleEvent event) throws MuleException {
      if (messageProcessor instanceof SubflowInterceptingChainLifecycleWrapper) {
         SubflowInterceptingChainLifecycleWrapper subFlow = (SubflowInterceptingChainLifecycleWrapper)messageProcessor;
         subFlow.setMuleContext(muleContext);
         subFlow.setFlowConstruct(event.getFlowConstruct());
         subFlow.initialise();
         subFlow.start();
      }

   }

   protected final EndpointMocker whenEndpointWithAddress(String address) {
      EndpointMocker endpointMocker = new EndpointMocker(muleContext);
      return endpointMocker.whenEndpointWithAddress(address);
   }

   protected final Matcher any() {
      return new AnyClassMatcher(Object.class);
   }

   protected final Matcher isNotNull() {
      return new NotNullMatcher();
   }

   protected final Matcher isNull() {
      return new NullMatcher();
   }

   protected final Matcher anyCollection() {
      return new AnyClassMatcher(Collection.class);
   }

   protected final Matcher anyMap() {
      return new AnyClassMatcher(Map.class);
   }

   protected final Matcher anySet() {
      return new AnyClassMatcher(Set.class);
   }

   protected final Matcher anyList() {
      return new AnyClassMatcher(List.class);
   }

   protected final Matcher anyString() {
      return new AnyClassMatcher(String.class);
   }

   protected final Matcher anyObject() {
      return new AnyClassMatcher(Object.class);
   }

   protected final Matcher anyShort() {
      return new AnyClassMatcher(Short.class);
   }

   protected final Matcher anyFloat() {
      return new AnyClassMatcher(Float.class);
   }

   protected Matcher anyDouble() {
      return new AnyClassMatcher(Double.class);
   }

   protected final Matcher eq(Object o) {
      return new EqMatcher(o);
   }

   protected final Matcher anyBoolean() {
      return new AnyClassMatcher(Boolean.class);
   }

   protected final Matcher anyByte() {
      return new AnyClassMatcher(Byte.class);
   }

   protected final Matcher anyInt() {
      return new AnyClassMatcher(Integer.class);
   }

   protected Properties getStartUpProperties() {
      return null;
   }

   private Properties loadProperties(String propertyFile) {
      try {
         Properties prop = new Properties();
         InputStream in = this.getClass().getResourceAsStream(propertyFile);
         prop.load(in);
         in.close();
         return prop;
      } catch (Throwable var4) {
         return null;
      }
   }

   @AfterClass
   public static void killMule() throws Throwable {
      muleContextManager.killMule(muleContext);
   }
}
