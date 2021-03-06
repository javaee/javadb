/*
 
Derby - Class org.apache.derbyTesting.functionTests.tests.replicationTests.TestPreStartedSlave
 
Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at
 
   http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 */

package org.apache.derbyTesting.functionTests.tests.replicationTests;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import junit.framework.Test;
import org.apache.derbyTesting.junit.BaseTestSuite;
import org.apache.derbyTesting.junit.TestConfiguration;

/**
 * Test correct behaviour on successive startSlave commands.
 */
public class TestPreStartedSlave extends ClientRunner
{
    
    public TestPreStartedSlave(String testcaseName)
    {
        super(testcaseName);
    }
    
    public static Test suite()
        throws Exception
    {
        System.out.println("**** TestPreStartedSlave.suite()");
        
        initEnvironment();
        
        // String masterHostName = System.getProperty("test.serverHost", "localhost");
        // int masterPortNo = Integer.parseInt(System.getProperty("test.serverPort", "1527"));
        
        BaseTestSuite suite = new BaseTestSuite("TestPreStartedSlave");
                
        suite.addTest(TestPreStartedSlave.suite(slaveServerHost, slaveServerPort));
        System.out.println("*** Done suite.addTest(TestPreStartedSlave.suite())");
        
        return (Test)suite;
    }

    /**
     * Adds this class to the *existing server* suite.
     */
    public static Test suite(String serverHost, int serverPort)
    {
        System.out.println("*** TestPreStartedSlave.suite(serverHost,serverPort)");
     
        Test t = TestConfiguration.existingServerSuite(TestPreStartedSlave.class,false,serverHost,serverPort);
        System.out.println("*** Done TestConfiguration.existingServerSuite(TestPreStartedSlave.class,false,serverHost,serverPort)");
        return t;
   }

    
    /**
     *
     *
     * @throws SQLException, IOException, InterruptedException
     */
    // Due to the startSlave-startMaster interdependency, what we try to test here
    // now must be tested on an already running slave db (and master db).
    public void testStartSlaveConnect_OK()
    throws SQLException, IOException, InterruptedException
    {
        System.out.println("**** TestPreStartedSlave.testStartSlaveConnect_OK() "+
                getTestConfiguration().getJDBCClient().getJDBCDriverName());
        
        Connection conn = null;
        String db = slaveDatabasePath +"/"+ReplicationRun.slaveDbSubPath +"/"+ replicatedDb;
        String connectionURL = "jdbc:derby:"  
                + "//" + slaveServerHost + ":" + slaveServerPort + "/"
                + db
                + ";startSlave=true"
                + ";slaveHost=" + slaveServerHost 
                + ";slavePort=" + slaveReplPort;
        System.out.println("Test moved to TestPostStartedMasterAndSlave! " + connectionURL);
        if (true) return;
        // First StartSlave connect ok:
        try
        {
            conn = DriverManager.getConnection(connectionURL); // Will hang until startMaster!
            System.out.println("1. Successfully connected as: " + connectionURL);
        }
        catch (SQLException se)
        {
            int ec = se.getErrorCode();
            String ss = se.getSQLState();
            String msg = ec + " " + ss + " " + se.getMessage();
            System.out.println(msg);
            assertSQLState("1. Unexpected SQLException: " + msg, "XRE08", se); // -1, XRE08
        }
        
        // Next StartSlave connect should fail:
        try
        {
            conn = DriverManager.getConnection(connectionURL);
            System.out.println("2. Unexpectedly connected as: " + connectionURL);
            assertTrue("2. Unexpectedly connected as: " + connectionURL, false);
        }
        catch (SQLException se)
        {
            int ec = se.getErrorCode();
            String ss = se.getSQLState();
            String msg = ec + " " + ss + " " + se.getMessage();
            System.out.println(msg);
            assertSQLState("2. Unexpected SQLException: " + msg, "08004", se); // 40000, 08004
        }
        
    }
    
    public void verifyTestStartSlaveConnect_OK()
    throws SQLException, IOException, InterruptedException
    {

    }
}
