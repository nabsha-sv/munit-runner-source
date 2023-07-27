package org.mule.munit.runner.remote;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/** @deprecated */
@Deprecated
public class MessageBuilder {
   public static final String RUN_START_MSG_ID = "0";
   public static final String TEST_SUITE_START_FAILURE_MSG_ID = "1";
   public static final String TEST_SUITE_START_MSG_ID = "2";
   public static final String NUMBER_OF_TESTS_MSG_ID = "3";
   public static final String TEST_START_MSG_ID = "4";
   public static final String TEST_FINSHED_MSG_ID = "5";
   public static final String TEST_IGNORED_MSG_ID = "6";
   public static final String TEST_FAILURE_MSG_ID = "7";
   public static final String TEST_ERROR_MSG_ID = "8";
   public static final String TEST_SUITE_FINISHED_MSG_ID = "9";
   public static final String RUN_FINISHED_MSG_ID = "10";
   public static final String APPLICATION_PATHS_MSG_ID = "11";

   public static String runStartMessage(String runToken) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("0", runToken);
      return msg.build();
   }

   public static String testSuiteStartMessage(String runToken, String suitePath, String suiteName) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("2", runToken);
      msg.setSuitePath(suitePath);
      msg.setSuiteName(suiteName);
      return msg.build();
   }

   public static String testSuiteStartFailureMessage(String runToken, String suitePath, String suiteName, String stackTrace) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("1", runToken);
      msg.setSuitePath(suitePath);
      msg.setSuiteName(suiteName);
      msg.setStackTrace(stackTrace);
      return msg.build();
   }

   public static String numberOfTestsMessage(String runToken, String suitePath, String suiteName, String numberOfTests) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("3", runToken);
      msg.setSuitePath(suitePath);
      msg.setSuiteName(suiteName);
      msg.setFreeMessage(numberOfTests);
      return msg.build();
   }

   public static String testStartMessage(String runToken, String suitePath, String suiteName, String testName) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("4", runToken);
      msg.setSuitePath(suitePath);
      msg.setSuiteName(suiteName);
      msg.setTestName(testName);
      return msg.build();
   }

   public static String testFinishedMessage(String runToken, String suitePath, String suiteName, String testName) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("5", runToken);
      msg.setSuitePath(suitePath);
      msg.setSuiteName(suiteName);
      msg.setTestName(testName);
      return msg.build();
   }

   public static String testIgnoredMessage(String runToken, String suitePath, String suiteName, String testName) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("6", runToken);
      msg.setSuitePath(suitePath);
      msg.setSuiteName(suiteName);
      msg.setTestName(testName);
      return msg.build();
   }

   public static String testFailureMessage(String runToken, String suitePath, String suiteName, String testName, String stackTrace) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("7", runToken);
      msg.setSuitePath(suitePath);
      msg.setSuiteName(suiteName);
      msg.setTestName(testName);
      msg.setStackTrace(stackTrace);
      return msg.build();
   }

   public static String testErrorMessage(String runToken, String suitePath, String suiteName, String testName, String stackTrace) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("8", runToken);
      msg.setSuitePath(suitePath);
      msg.setSuiteName(suiteName);
      msg.setTestName(testName);
      msg.setStackTrace(stackTrace);
      return msg.build();
   }

   public static String testSuiteFinishedMessage(String runToken, String suitePath, String suiteName) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("9", runToken);
      msg.setSuitePath(suitePath);
      msg.setSuiteName(suiteName);
      return msg.build();
   }

   public static String applicationPathsMessage(String runToken, String flowPaths, String subFlowPaths, String batchPaths) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("11", runToken);
      Map paths = new HashMap();
      paths.put("flowPaths", flowPaths);
      paths.put("subFlowPaths", subFlowPaths);
      paths.put("batchPaths", batchPaths);
      msg.setJsonMessage((new JSONObject(paths)).toString());
      return msg.build();
   }

   public static String runFinishedMessage(String runToken) {
      RemoteRunnerMessage msg = new RemoteRunnerMessage("10", runToken);
      return msg.build();
   }
}
