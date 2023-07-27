package org.mule.munit.runner.spring.config.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MockingConfiguration implements BeanDefinitionGenericBuilder {
   public static final String MOCK_INBOUNDS_PROPERTY_NAME = "mockInbounds";
   public static final String MOCK_CONNECTORS_PROPERTY_NAME = "mockConnectors";
   public static final String MOCKING_EXCLUDED_FLOWS_PROPERTY_NAME = "mockingExcludedFlows";
   private boolean mockInbounds;
   private List mockingExcludedFlows;
   private boolean mockConnectors;
   private Properties startUpProperties;

   public MockingConfiguration(boolean mockInbounds, List mockingExcludedFlows, boolean mockConnectors, Properties startUpProperties) {
      this.mockInbounds = mockInbounds;
      this.mockingExcludedFlows = mockingExcludedFlows;
      this.mockConnectors = mockConnectors;
      this.startUpProperties = startUpProperties;
   }

   public List getMockingExcludedFlows() {
      return this.mockingExcludedFlows;
   }

   public boolean isMockInbounds() {
      return this.mockInbounds;
   }

   public boolean isMockConnectors() {
      return this.mockConnectors;
   }

   public Properties getStartUpProperties() {
      return this.startUpProperties;
   }

   public Map buildAttributeMap() {
      Map propertyMap = new HashMap();
      propertyMap.put("mockInbounds", this.isMockInbounds());
      propertyMap.put("mockConnectors", this.isMockConnectors());
      propertyMap.put("mockingExcludedFlows", this.getMockingExcludedFlows());
      return propertyMap;
   }
}
