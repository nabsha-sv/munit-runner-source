package org.mule.munit.runner.output;

public class ConsolePrinter implements OutputPrinter {
   public void print(String text) {
      System.out.println(text);
   }
}
