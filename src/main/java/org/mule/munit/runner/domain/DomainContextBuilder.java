package org.mule.munit.runner.domain;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mule.DefaultMuleContext;
import org.mule.api.MuleContext;
import org.mule.api.config.ConfigurationBuilder;
import org.mule.api.context.MuleContextBuilder;
import org.mule.config.spring.SpringXmlDomainConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.util.ClassUtils;

public class DomainContextBuilder {
   private String domainConfig;
   private String domainName;
   private boolean disableMuleContextStart = false;
   private MuleContextBuilder muleContextBuilder = new DefaultMuleContextBuilder() {
      protected DefaultMuleContext createDefaultMuleContext() {
         DefaultMuleContext muleContext = super.createDefaultMuleContext();
         return muleContext;
      }
   };

   public DomainContextBuilder setDomainConfig(String domainConfig) {
      this.domainConfig = domainConfig;
      return this;
   }

   public DomainContextBuilder setDomainName(String domainName) {
      this.domainName = domainName;
      return this;
   }

   public DomainContextBuilder disableMuleContextStart() {
      this.disableMuleContextStart = true;
      return this;
   }

   public MuleContext build() throws Exception {
      List builders = new ArrayList(3);
      String domainConfigResource = this.getDomainConfigIfFoundInResources(ClassUtils.getResources(this.domainConfig, this.getClass()));
      ConfigurationBuilder cfgBuilder = this.getDomainBuilder(domainConfigResource);
      builders.add(cfgBuilder);
      DefaultMuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
      MuleContext domainContext = muleContextFactory.createMuleContext(builders, this.muleContextBuilder);
      if (!this.disableMuleContextStart) {
         domainContext.start();
      }

      return domainContext;
   }

   public String getDomainConfigIfFoundInResources(Enumeration urls) {
      if (StringUtils.isNotBlank(this.domainConfig) && StringUtils.isNotBlank(this.domainName)) {
         while(urls.hasMoreElements()) {
            URL url = (URL)urls.nextElement();
            if (this.isDomainJar(url) || this.isDomainDirectory(url)) {
               return url.toString();
            }
         }
      }

      return this.domainConfig;
   }

   private boolean isDomainDirectory(URL url) {
      URI domainDir = URI.create(this.domainName + "/target/classes/" + this.domainConfig);
      return "file".equals(url.getProtocol()) && url.getPath().endsWith(domainDir.getPath());
   }

   private boolean isDomainJar(URL url) {
      URI domainFolder = URI.create("/" + this.domainName + "/");
      return "jar".equals(url.getProtocol()) && url.getPath().contains(domainFolder.getPath());
   }

   public String getDomainConfig() {
      return this.domainConfig;
   }

   public boolean isDisableMuleContextStart() {
      return this.disableMuleContextStart;
   }

   protected ConfigurationBuilder getDomainBuilder(String configResource) throws Exception {
      return new SpringXmlDomainConfigurationBuilder(configResource);
   }
}
