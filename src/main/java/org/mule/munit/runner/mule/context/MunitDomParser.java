package org.mule.munit.runner.mule.context;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;

public class MunitDomParser extends DOMParser {
   public static final String NAMESPACE = "http://www.mule.org/munit";
   protected XMLLocator xmlLocator;

   public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
      this.xmlLocator = locator;
      super.startDocument(locator, encoding, namespaceContext, augs);
   }

   public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
      String location = String.valueOf(this.xmlLocator.getLineNumber());
      attributes.addAttribute(new QName("__MUNIT_LINE_NUMBER", (String)null, "__MUNIT_LINE_NUMBER", "http://www.mule.org/munit"), "CDATA", location);
      super.startElement(element, attributes, augs);
   }
}
