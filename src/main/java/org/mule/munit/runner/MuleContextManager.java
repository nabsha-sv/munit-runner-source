package org.mule.munit.runner;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.RegistrationException;
import org.mule.modules.interceptor.connectors.ConnectorMethodInterceptorFactory;
import org.mule.munit.common.endpoint.MockEndpointManager;
import org.mule.munit.common.endpoint.MunitSpringFactoryPostProcessor;
import org.mule.munit.common.extensions.MunitPlugin;
import org.mule.munit.common.processor.interceptor.MunitMessageProcessorInterceptorFactory;
import org.mule.munit.runner.domain.MunitDomainContextBuilder;
import org.mule.munit.runner.exception.ExceptionStrategyReplacer;
import org.mule.munit.runner.mule.context.MunitDomParser;
import org.mule.munit.runner.properties.ApplicationPropertyLoader;
import org.mule.munit.runner.properties.MUnitUserPropertiesManager;
import org.mule.munit.runner.spring.config.MunitSpringXmlConfigurationBuilder;
import org.mule.munit.runner.spring.config.model.MockingConfiguration;
import org.mule.munit.runner.spring.config.reader.MunitHandlerWrapper;
import org.mule.util.ClassUtils;

public class MuleContextManager {
   private static final Integer CONSTRUCTOR_ARG_LIMIT = 13;
   public static final String USE_XALAN_TRANSFORMER_PROPERTY = "useXalanTransformer";
   private transient Log logger = LogFactory.getLog(this.getClass());
   private MUnitUserPropertiesManager propertiesManager = new MUnitUserPropertiesManager();
   private Collection plugins;
   private MockingConfiguration configuration;
   private Map appDomainMap = new HashMap();

   public MuleContextManager(MockingConfiguration configuration) {
      this.configuration = configuration;
   }

   public MuleContext startMule(String resources, String projectName) throws Exception {
      MuleContext context = this.createMule(resources, projectName);
      return this.startMule(context);
   }

   public MuleContext startMule(MuleContext context) throws MuleException {
      this.logger.debug("Starting Mule Context tuned by MUnit...");
      context.start();
      this.startPlugins();
      this.logger.debug("Mule Context tuned by MUnit Started");
      return context;
   }

   public void killMule(MuleContext muleContext) {
      this.logger.debug("Shooting down Mule Context tuned by MUnit...");
      this.stopMuleContext(muleContext);
      this.disposeMuleContext(muleContext);
      this.logger.debug("Mule Context shot down");
      this.clearLogginConfiguration();
   }

   public MuleContext createMule(String resources, String projectName) throws Exception {
      this.logger.debug("Creating Mule Context tuned by MUnit...");
      this.defineBeanConstructorArgLimit();
      this.loadMuleAppProperties();
      this.loadAdditionalSystemProperties();
      ConfigurationBuilder configurationBuilder = this.createConfigurationBuilder(resources, projectName);
      MuleContext domainContext = (new MunitDomainContextBuilder(projectName)).buildDomainContextIfRequired();
      if (null != domainContext) {
         ((MunitSpringXmlConfigurationBuilder)configurationBuilder).setDomainContext(domainContext);
      }

      List builders = new ArrayList();
      builders.add(configurationBuilder);
      MunitMuleContextFactory contextCreator = new MunitMuleContextFactory(this.getStartUpProperties(), builders);
      MuleContext context = contextCreator.createMuleContext();
      this.loadApplicationPropertiesToMuleContext(context);
      this.appDomainMap.put(context, domainContext);
      this.replaceExceptionStrategies(context);
      this.plugins = (new MunitPluginFactory()).loadPlugins(context);
      this.initialisePlugins();
      return context;
   }

   protected void defineBeanConstructorArgLimit() {
      try {
         Method method = MunitHandlerWrapper.class.getDeclaredMethod("setConstructorArgLimit", Integer.class);
         if (null != method) {
            method.invoke((Object)null, CONSTRUCTOR_ARG_LIMIT);
         }
      } catch (NoSuchMethodException var2) {
         this.logger.debug("Using MUnit Support that doesn't support constructor parameter definition");
      } catch (InvocationTargetException var3) {
         this.logger.debug("Fail to set constructor arg limit in MUnit support");
      } catch (IllegalAccessException var4) {
         this.logger.debug("Fail to set constructor arg limit in MUnit support");
      }

   }

   protected ConfigurationBuilder createConfigurationBuilder(String resources, String projectName) throws Exception {
      this.logger.debug("Creating ConfigurationBuilder for resources: " + resources);
      MunitSpringXmlConfigurationBuilder.ConfigurationBuilderBuilder builder = new MunitSpringXmlConfigurationBuilder.ConfigurationBuilderBuilder(resources);
      builder.withMockingConfiguration(this.configuration).withMunitFactoryPostProcessor("___MunitSpringFactoryPostProcessor", MunitSpringFactoryPostProcessor.class).withEndpointFactoryClass(MockEndpointManager.class).withBeanToRegister("__messageProcessorEnhancerFactory", MunitMessageProcessorInterceptorFactory.class).withBeanToRegister(ConnectorMethodInterceptorFactory.ID, ConnectorMethodInterceptorFactory.class).withBeanToRegister("__exceptionStrategyReplacer", ExceptionStrategyReplacer.class).withMunitDomParser(new MunitDomParser());
      MunitSpringXmlConfigurationBuilder configurationBuilder = builder.build();
      return configurationBuilder;
   }

   private void replaceExceptionStrategies(MuleContext context) {
      this.logger.debug("Replacing exception strategies with MUnit proxies...");
      ExceptionStrategyReplacer replacer = (ExceptionStrategyReplacer)context.getRegistry().get("__exceptionStrategyReplacer");
      replacer.setMuleContext(context);
      replacer.replace();
   }

   private void clearLogginConfiguration() {
      MunitMuleContextFactory.clearLoggingConfiguration();
   }

   private Properties getStartUpProperties() {
      this.logger.debug("Loading startup properties...");
      Properties properties = this.configuration == null ? null : this.configuration.getStartUpProperties();
      if (properties == null) {
         properties = new Properties();
      }

      try {
         Object homeProperty = properties.get("app.home");
         if (homeProperty == null || StringUtils.isBlank(homeProperty.toString())) {
            String appHomePath = URLDecoder.decode((new File(this.getClass().getResource("/").getPath())).getParentFile().getAbsolutePath(), "UTF-8");
            properties.setProperty("app.home", appHomePath);
         }
      } catch (UnsupportedEncodingException var4) {
         var4.printStackTrace();
      }

      this.logger.debug("Startup properties loaded: [" + properties.toString() + "]");
      return properties;
   }

   private void startPlugins() throws MuleException {
      this.logger.debug("Starting MUnit plugins...");
      Iterator var1 = this.plugins.iterator();

      while(var1.hasNext()) {
         MunitPlugin plugin = (MunitPlugin)var1.next();
         plugin.start();
         this.logger.debug(plugin.getClass().getName() + " plugin started");
      }

   }

   private void disposePlugins() {
      this.logger.debug("Disposing MUnit plugins...");
      Iterator var1 = this.plugins.iterator();

      while(var1.hasNext()) {
         MunitPlugin plugin = (MunitPlugin)var1.next();
         plugin.dispose();
         this.logger.debug(plugin.getClass().getName() + " plugin disposed");
      }

   }

   private void stopPlugins() throws MuleException {
      Iterator var1 = this.plugins.iterator();

      while(var1.hasNext()) {
         MunitPlugin plugin = (MunitPlugin)var1.next();
         plugin.stop();
      }

   }

   private void initialisePlugins() throws InitialisationException {
      this.logger.debug("Initializing MUnit plugins...");
      Iterator var1 = this.plugins.iterator();

      while(var1.hasNext()) {
         MunitPlugin plugin = (MunitPlugin)var1.next();
         plugin.initialise();
         this.logger.debug(plugin.getClass().getName() + " plugin initialised");
      }

   }

   private void loadMuleAppProperties() {
      this.logger.info("Loading mule-app.properties ...");
      ApplicationPropertyLoader propertyLoader = new ApplicationPropertyLoader(this.propertiesManager, this.logger);
      URL url = ClassUtils.getResource("mule-app.properties", this.getClass());
      propertyLoader.loadAndSetApplicationProperties(url);
      this.logger.debug("mule-app.properties loading done");
   }

   private void loadApplicationPropertiesToMuleContext(MuleContext context) {
      this.logger.info("Loading application properties to Mule Context");
      Map appProp = new HashMap();
      Iterator var3 = this.propertiesManager.getApplicationProperties().entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry e = (Map.Entry)var3.next();
         appProp.put(e.getKey(), e.getValue());
      }

      try {
         context.getRegistry().registerObjects(appProp);
      } catch (RegistrationException var5) {
         this.logger.warn("There has been an error loading the application properties to the Mule Context", var5);
      }

      this.logger.debug("Loading application properties to Mule Context done");
   }

   private void loadAdditionalSystemProperties() {
      if ("true".equals(System.getProperty("useXalanTransformer"))) {
         System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
      }

   }

   private void stopMuleContext(MuleContext muleContext) {
      this.logger.debug("Stopping Mule Context tuned by MUnit...");

      try {
         if (muleContext != null && !muleContext.isStopped()) {
            muleContext.stop();
            this.stopPlugins();
            if (null != this.appDomainMap.get(muleContext) && !((MuleContext)this.appDomainMap.get(muleContext)).isStopped()) {
               this.logger.debug("Stopping Mule Domain Context tuned...");
               ((MuleContext)this.appDomainMap.get(muleContext)).stop();
               this.logger.debug("Mule Domain Context tuned stopped");
            }
         }
      } catch (Throwable var3) {
         this.logger.debug("There has been an error while stopping Mule Context", var3);
      }

      this.logger.debug("Mule Context stopped");
   }

   private void disposeMuleContext(MuleContext muleContext) {
      if (muleContext != null && !muleContext.isDisposed()) {
         this.logger.debug("Disposing Mule Context tuned by MUnit...");
         muleContext.dispose();
         this.disposePlugins();
         if (null != this.appDomainMap.get(muleContext) && !((MuleContext)this.appDomainMap.get(muleContext)).isDisposed()) {
            this.logger.debug("Disposing Mule Domain Context tuned...");
            ((MuleContext)this.appDomainMap.get(muleContext)).dispose();
            this.logger.debug("Mule Domain Context tuned disposed");
         }

         this.logger.debug("Mule Context tuned by MUnit disposed");
      }

   }
}
