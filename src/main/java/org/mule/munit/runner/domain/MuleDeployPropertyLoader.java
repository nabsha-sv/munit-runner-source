package org.mule.munit.runner.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.util.ClassUtils;

public class MuleDeployPropertyLoader {
   public static final String DEFAULT_MULE_DEPLOY_PROPERTIES_RESOURCE = "mule-deploy.properties";
   private static final String MULE_DEPLOY_PROPERTIES_RELATIVE_PATH;
   private static final String MULE_DEPLOY_PROPERTIES_TEST_RELATIVE_PATH;
   private transient Log log = LogFactory.getLog(this.getClass());
   private final String projectName;
   private Map domainDeployPropertiesMap = null;
   private Map applicationDeployPropertiesMap = null;

   public MuleDeployPropertyLoader(String projectName) {
      this.projectName = projectName;
   }

   public Map getApplicationDeployProperties() {
      if (null == this.applicationDeployPropertiesMap) {
         this.loadApplicationDeployProperties();
      }

      return this.applicationDeployPropertiesMap;
   }

   public Map getDomainDeployProperties() {
      if (null == this.applicationDeployPropertiesMap) {
         this.loadApplicationDeployProperties();
      }

      if (null == this.domainDeployPropertiesMap) {
         String domainName = (String)this.applicationDeployPropertiesMap.get("domain");
         this.loadDomainDeployProperties(domainName);
      }

      return this.domainDeployPropertiesMap;
   }

   private void loadApplicationDeployProperties() {
      this.applicationDeployPropertiesMap = new HashMap();
      URL url = null;
      Enumeration urls = ClassUtils.getResources("mule-deploy.properties", this.getClass());

      while(urls.hasMoreElements()) {
         URL u = (URL)urls.nextElement();
         if (this.doesTheFileBelongToTheApplication(u)) {
            url = u;
         }
      }

      if (null != url) {
         this.log.debug("Loaded mule-deploy.properties file from:" + url.getPath().toString());
         this.loadDeployProperties(url, this.applicationDeployPropertiesMap);
      }

   }

   private void loadDomainDeployProperties(String domainName) {
      this.domainDeployPropertiesMap = new HashMap();
      if (StringUtils.isBlank(domainName)) {
         this.log.debug("Attempting to load mule-deploy.properties for a blank domain will return not properties");
      }

      URL url = null;
      Enumeration urls = ClassUtils.getResources("mule-deploy.properties", this.getClass());

      while(urls.hasMoreElements()) {
         URL u = (URL)urls.nextElement();
         if (this.doesTheFileBelongToTheDomain(u, domainName)) {
            url = u;
         }
      }

      if (null != url) {
         this.log.debug("Loaded mule-deploy.properties file from:" + url.getPath().toString());
         this.loadDeployProperties(url, this.domainDeployPropertiesMap);
      }

   }

   private void loadDeployProperties(URL resourceUrl, Map propertiesMap) {
      if (null == resourceUrl) {
         this.log.warn("mule-deploy.properties file was not found");
      } else {
         try {
            propertiesMap.putAll(this.loadProperties(resourceUrl));
         } catch (IOException var4) {
            this.log.warn("mule-deploy.properties could not be loaded.");
         }

      }
   }

   private boolean doesTheFileBelongToTheApplication(URL url) {
      String path = (new File(url.getPath())).getPath();
      return path.contains(this.projectName + MULE_DEPLOY_PROPERTIES_RELATIVE_PATH) || path.contains(this.projectName + MULE_DEPLOY_PROPERTIES_TEST_RELATIVE_PATH);
   }

   private boolean doesTheFileBelongToTheDomain(URL url, String domainName) {
      String domainMuleDeployPropertiesRelativePath = File.separator + domainName + File.separator;
      String path = (new File(url.getPath())).getPath();
      return path.contains(domainMuleDeployPropertiesRelativePath);
   }

   private Map loadProperties(URL propsFile) throws IOException {
      Map appPropsMap = new HashMap();
      Properties props = this.loadPropertiesFromFile(propsFile);
      Iterator var4 = props.keySet().iterator();

      while(var4.hasNext()) {
         Object key = var4.next();
         appPropsMap.put(key.toString(), props.getProperty(key.toString()));
      }

      return appPropsMap;
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

   static {
      MULE_DEPLOY_PROPERTIES_RELATIVE_PATH = File.separator + "target" + File.separator + "classes" + File.separator + "mule-deploy.properties";
      MULE_DEPLOY_PROPERTIES_TEST_RELATIVE_PATH = File.separator + "target" + File.separator + "test-classes" + File.separator + "mule-deploy.properties";
   }
}
