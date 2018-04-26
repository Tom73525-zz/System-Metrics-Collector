package org.robert.tom;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DB_Population_Test.class,
        JDBC_Connectivity_Test.class,
        DB_Retrieveal_Test.class,
        getMaxStartTime_Test.class,
        deleteProcessTest.class,
        getLatestTime_Test.class
})
public class TestSuite {

}
