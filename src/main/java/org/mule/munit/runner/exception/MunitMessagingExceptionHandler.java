package org.mule.munit.runner.exception;

import org.mule.api.GlobalNameableObject;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.exception.MessagingExceptionHandler;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Lifecycle;
import org.mule.api.processor.MessageProcessorContainer;
import org.mule.api.processor.MessageProcessorPathElement;
import org.mule.munit.common.MunitUtils;

public class MunitMessagingExceptionHandler implements MessagingExceptionHandler, MessageProcessorContainer, GlobalNameableObject, Lifecycle {
   private MessagingExceptionHandler originalMessagingExceptionHandler;

   public MunitMessagingExceptionHandler(MessagingExceptionHandler originalHandler) {
      this.originalMessagingExceptionHandler = originalHandler;
   }

   public MuleEvent handleException(Exception exception, MuleEvent event) {
      MunitUtils.throwRootCauseIfMatches(exception);
      return this.originalMessagingExceptionHandler.handleException(exception, event);
   }

   public void dispose() {
      ((Lifecycle)this.originalMessagingExceptionHandler).dispose();
   }

   public void initialise() throws InitialisationException {
      ((Lifecycle)this.originalMessagingExceptionHandler).initialise();
   }

   public void start() throws MuleException {
      ((Lifecycle)this.originalMessagingExceptionHandler).start();
   }

   public void stop() throws MuleException {
      ((Lifecycle)this.originalMessagingExceptionHandler).stop();
   }

   public void addMessageProcessorPathElements(MessageProcessorPathElement pathElement) {
      ((MessageProcessorContainer)this.originalMessagingExceptionHandler).addMessageProcessorPathElements(pathElement);
   }

   public String getGlobalName() {
      return this.originalMessagingExceptionHandler instanceof GlobalNameableObject ? ((GlobalNameableObject)this.originalMessagingExceptionHandler).getGlobalName() : null;
   }

   public void setGlobalName(String name) {
      if (this.originalMessagingExceptionHandler instanceof GlobalNameableObject) {
         ((GlobalNameableObject)this.originalMessagingExceptionHandler).setGlobalName(name);
      }

   }
}
