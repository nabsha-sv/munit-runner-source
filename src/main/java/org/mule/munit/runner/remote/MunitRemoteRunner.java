package org.mule.munit.runner.remote;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.mule.munit.runner.mule.MunitSuiteRunner;
import org.mule.munit.runner.mule.MunitTest;
import org.mule.munit.runner.mule.result.notification.Notification;

/** @deprecated */
@Deprecated
public class MunitRemoteRunner {
   public static final String PORT_PARAMETER = "-port";
   public static final String RESOURCE_PARAMETER = "-resource";
   public static final String RUN_TOKEN_PARAMETER = "-run_token";
   public static final String TEST_NAME_PARAMETER = "-test_name";
   public static final String RESOURCES_TOKEN_SEPARATOR = ",";
   public static final String TEST_NAME_TOKEN_SEPARATOR = "<";
   public static final String CALCULATE_APPLICATION_PATHS_SYSTEM_PROPERTY = "calculate.application.paths";
   protected String message;
   protected Socket requestSocket;
   protected ObjectOutputStream out;

   public static void main(String[] args) {
      int port = -1;
      String runToken = null;
      String resources = null;
      String testName = null;

      for(int i = 0; i < args.length; ++i) {
         if (args[i].equalsIgnoreCase("-run_token")) {
            runToken = args[i + 1];
         }

         if (args[i].equalsIgnoreCase("-resource")) {
            resources = args[i + 1];
         }

         if (args[i].equalsIgnoreCase("-port")) {
            port = Integer.valueOf(args[i + 1]);
         }

         if (args[i].equalsIgnoreCase("-test_name")) {
            testName = args[i + 1];
         }
      }

      MunitRemoteRunner serverRemoteRunner = new MunitRemoteRunner();
      serverRemoteRunner.run(port, runToken, resources, testName);
   }

   public void run(int port, String runToken, String resources, String testName) {
      RemoteRunnerNotificationListener listener = null;

      try {
         this.connectToStudioServer(port);
         listener = new RemoteRunnerNotificationListener(runToken, this.out);
         listener.notifyRunStart();
         List testNameList = this.buildTestNamesList(testName);
         String[] var7 = resources.split(",");
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            String resource = var7[var9];
            this.runTestSuite(resource, testNameList, listener);
         }
      } catch (IOException var14) {
         var14.printStackTrace();
      } finally {
         if (null != listener) {
            listener.notifyRunFinish();
         }

         this.closeConnectionToStudioServer();
         System.out.println("[" + this.getClass().getName() + "]Done");
      }

      System.exit(0);
   }

   private List buildTestNamesList(String testNames) {
      Object testNameList;
      if (StringUtils.isNotBlank(testNames)) {
         testNameList = Arrays.asList(testNames.split("<"));
      } else {
         testNameList = new ArrayList();
      }

      return (List)testNameList;
   }

   private void connectToStudioServer(int port) throws IOException {
      this.requestSocket = new Socket("localhost", port);
      System.out.println("[" + this.getClass().getName() + "]Connected to localhost in port " + port);
      this.out = new ObjectOutputStream(this.requestSocket.getOutputStream());
      this.out.flush();
   }

   private void closeConnectionToStudioServer() {
      try {
         if (null != this.out) {
            this.out.close();
         }

         if (null != this.requestSocket) {
            this.requestSocket.close();
         }
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   private int runTestSuite(String resource, List testNames, RemoteRunnerNotificationListener listener) {
      String suitePath = FilenameUtils.getPath(resource);
      String suiteName = FilenameUtils.getName(resource);
      String projectName = System.getProperty("munitProjectName");
      listener.defineCurrentSuite(suitePath, suiteName);

      MunitSuiteRunner runner;
      try {
         runner = new MunitSuiteRunner(resource, testNames, projectName);
         runner.setNotificationListener(listener);
         listener.notifySuiteStart();
         listener.notifyNumberOfTest(runner.getNumberOfTests());
      } catch (RuntimeException var13) {
         listener.notifySuiteStartFailure(new Notification(var13.getMessage(), MunitTest.stack2string(var13)));
         throw var13;
      }

      try {
         runner.run();
      } finally {
         listener.notifySuiteFinished();
         return runner.getNumberOfTests();
      }
   }
}
