package org.mule.munit.runner.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;

public class ApplicationPropertyLoader {
   public static final String DEFAULT_APP_PROPERTIES_RESOURCE = "mule-app.properties";
   private Log log;
   private MUnitUserPropertiesManager propertiesManager;

   public ApplicationPropertyLoader(MUnitUserPropertiesManager propertiesManager, Log log) {
      this.log = log;
      this.propertiesManager = propertiesManager;
   }

   public void loadAndSetApplicationProperties(URL resourceUrl) {
      if (resourceUrl == null) {
         this.log.warn("mule-app.properties file was not found");
      } else {
         try {
            Map appPropsMap = this.loadApplicationProperties(resourceUrl);
            this.setApplicationProperties(appPropsMap);
         } catch (IOException var3) {
            this.log.warn("mule-app.properties could not be loaded.");
         }

      }
   }

   private Map loadApplicationProperties(URL appPropsFile) throws IOException {
      Map appPropsMap = new HashMap();
      Properties props = this.loadPropertiesFromFile(appPropsFile);
      Iterator var4 = props.keySet().iterator();

      while(var4.hasNext()) {
         Object key = var4.next();
         appPropsMap.put(key.toString(), props.getProperty(key.toString()));
      }

      return appPropsMap;
   }

   private void setApplicationProperties(Map properties) {
      Iterator var2 = properties.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry entry = (Map.Entry)var2.next();
         MUnitUserPropertiesManager var10000 = this.propertiesManager;
         if (!MUnitUserPropertiesManager.hasSystemProperty((String)entry.getKey())) {
            var10000 = this.propertiesManager;
            if (!MUnitUserPropertiesManager.hasEnvironmentProperty((String)entry.getKey())) {
               this.propertiesManager.addApplicationProperty((String)entry.getKey(), (String)entry.getValue());
               this.log.debug("System property [" + (String)entry.getKey() + "] set to: [" + (String)entry.getValue() + "]");
            }
         }
      }

   }

   private Properties loadPropertiesFromFile(URL url) throws IOException {
      if (url == null) {
         throw new IOException("Invalid file URL!");
      } else {
         InputStream is = url.openStream();

         Properties props;
         try {
            props = new Properties();
            props.load(is);
         } finally {
            is.close();
         }

         return props;
      }
   }
}
