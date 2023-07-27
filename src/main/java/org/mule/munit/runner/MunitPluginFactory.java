package org.mule.munit.runner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.munit.common.extensions.MunitPlugin;

public class MunitPluginFactory {
   private transient Log logger = LogFactory.getLog(this.getClass());

   public Collection loadPlugins(MuleContext context) {
      this.logger.debug("Loading MUnitPlugins...");
      List munitPlugins = new ArrayList();
      ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

      try {
         Enumeration resources = contextClassLoader.getResources("META-INF/munit-plugin.properties");

         while(resources.hasMoreElements()) {
            Properties properties = new Properties();
            Object content = ((URL)resources.nextElement()).getContent();
            properties.load((InputStream)content);
            MunitPlugin plugin = this.createMunitPlugin(contextClassLoader, properties);
            if (plugin != null) {
               if (plugin instanceof MuleContextAware) {
                  ((MuleContextAware)plugin).setMuleContext(context);
               }

               munitPlugins.add(plugin);
               this.logger.debug("MUnit plugin: " + plugin.getClass().getCanonicalName() + " loaded");
            }
         }
      } catch (IOException var8) {
         this.logger.error("Could not read the Classpath in order to get the plugin configurations");
      }

      return munitPlugins;
   }

   private MunitPlugin createMunitPlugin(ClassLoader contextClassLoader, Properties properties) {
      String property = properties.getProperty("plugin.className");

      try {
         if (property != null && !property.isEmpty()) {
            this.logger.debug("Attempting to load MUnit plugin: " + property);
            return (MunitPlugin)contextClassLoader.loadClass(property).newInstance();
         }
      } catch (Throwable var5) {
         this.logger.error("The class " + property + " could not be load");
      }

      return null;
   }
}
