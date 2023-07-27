package org.mule.munit.runner.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;

public class MunitDomainContextBuilder {
   private transient Log logger = LogFactory.getLog(this.getClass());
   private static final String DOMAIN_CONFIG_FILE_NAME = "mule-domain-config.xml";
   private final String projectName;
   private String domainName;
   private DomainContextBuilder domainContextBuilder;
   private MuleDeployPropertyLoader muleDeployPropertyLoader;

   public MunitDomainContextBuilder(String projectName) {
      this.projectName = projectName;
      this.domainContextBuilder = new DomainContextBuilder();
      this.muleDeployPropertyLoader = new MuleDeployPropertyLoader(projectName);
   }

   public MuleContext buildDomainContextIfRequired() throws Exception {
      MuleContext domainContext = null;
      if (StringUtils.isBlank(this.projectName)) {
         this.logger.debug("No project name provided Domain Context won't be built");
         return domainContext;
      } else {
         this.logger.debug("Looking for domain resources of project: " + this.projectName);
         String domainResources = this.loadDomainResources();
         if (StringUtils.isNotBlank(domainResources)) {
            this.logger.debug("About to build Domain Context with resources: " + domainResources);
            domainContext = this.domainContextBuilder.setDomainConfig(domainResources).setDomainName(this.domainName).build();
            this.logger.debug("Domain Context built");
         } else {
            this.logger.debug("No domain resources found, Domain Context won't be built");
         }

         return domainContext;
      }
   }

   private String loadDomainResources() {
      String domainResources = "";
      this.logger.info("Loading mule-deploy.properties ...");
      this.domainName = (String)this.muleDeployPropertyLoader.getApplicationDeployProperties().get("domain");
      if (StringUtils.isNotBlank(this.domainName) && !"default".equals(this.domainName.toLowerCase())) {
         this.logger.debug("Domain found: " + this.domainName.toLowerCase());
         domainResources = "mule-domain-config.xml";
      } else {
         this.logger.debug("No domain or default domain found.");
      }

      return domainResources;
   }

   public void setDomainContextBuilder(DomainContextBuilder domainContextBuilder) {
      this.domainContextBuilder = domainContextBuilder;
   }

   public void setMuleDeployPropertyLoader(MuleDeployPropertyLoader muleDeployPropertyLoader) {
      this.muleDeployPropertyLoader = muleDeployPropertyLoader;
   }
}
