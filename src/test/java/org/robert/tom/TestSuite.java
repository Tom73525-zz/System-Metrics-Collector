package org.robert.tom;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DB_Population_Test.class,
        JDBC_Connectivity_Test.class
})
public class TestSuite {

}
