package org.mule.munit.runner.output;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

public class DefaultOutputHandler implements TestOutputHandler {
   private List printers = new ArrayList();

   public DefaultOutputHandler() {
      this.printers.add(new ConsolePrinter());
   }

   public DefaultOutputHandler(List printers) {
      this.printers = printers;
   }

   public void printDescription(String name, String description) {
      String text = "Running " + name;
      this.print(text);
   }

   public void printTestName(String suiteName) {
      String title = StringUtils.repeat("=", 40 + FilenameUtils.getName(suiteName).length());
      this.print(title);
      this.print("===========  Running  " + FilenameUtils.getName(suiteName) + "  test ===========");
      this.print(title);
   }

   public List getPrinters() {
      return this.printers;
   }

   private void print(String text) {
      Iterator var2 = this.printers.iterator();

      while(var2.hasNext()) {
         OutputPrinter printer = (OutputPrinter)var2.next();
         printer.print(text);
      }

   }
}
