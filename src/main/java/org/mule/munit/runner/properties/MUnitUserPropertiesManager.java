package org.mule.munit.runner.properties;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.Validate;

public class MUnitUserPropertiesManager {
   private static Properties initialSystemProperties;
   private static Set notAllowedPropertyKeys = defineNotAllowedPropertyKeys();
   private Map applicationProperties = new ConcurrentHashMap();

   public static void storeInitialSystemProperties() {
      initialSystemProperties = (Properties)System.getProperties().clone();
   }

   public static void restoreInitialSystemProperties() {
      System.setProperties(initialSystemProperties);
   }

   public MUnitUserPropertiesManager() {
      storeInitialSystemProperties();
   }

   public static void addUserPropertiesToSystem(Map userProperties) {
      if (userProperties != null) {
         Iterator var1 = userProperties.entrySet().iterator();

         while(var1.hasNext()) {
            Map.Entry entry = (Map.Entry)var1.next();
            addUserPropertyToSystem((String)entry.getKey(), (String)entry.getValue());
         }
      }

   }

   public static void addUserPropertyToSystem(String key, String value) {
      Validate.notBlank(key, "The property key must not be null nor empty", new Object[0]);
      if (isPropertyKeyAllowed(key)) {
         System.setProperty(key, value);
      }

   }

   public void addApplicationProperty(String key, String value) {
      Validate.notBlank(key, "The property key must not be null nor empty", new Object[0]);
      if (isPropertyKeyAllowed(key)) {
         this.applicationProperties.put(key, value);
         System.setProperty(key, value);
      }

   }

   public Map getApplicationProperties() {
      Map appProp = new HashMap();
      appProp.putAll(this.applicationProperties);
      return appProp;
   }

   public static boolean hasSystemProperty(String key) {
      return System.getProperty(key) != null;
   }

   public static boolean hasEnvironmentProperty(String key) {
      return System.getenv(key) != null;
   }

   private static Boolean isPropertyKeyAllowed(String key) {
      return !notAllowedPropertyKeys.contains(key);
   }

   private static Set defineNotAllowedPropertyKeys() {
      Set propertyKeysSet = new HashSet();
      propertyKeysSet.add("java.library.path");
      propertyKeysSet.add("file.encoding");
      propertyKeysSet.add("jdk.map.althashing.threshold");
      return propertyKeysSet;
   }
}
