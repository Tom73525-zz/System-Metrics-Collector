package org.robert.tom;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import static org.junit.Assert.assertEquals;

public class JDBC_Connectivity_Test {

  Connection connection = null;

  /**
   * This method tests for the JDBC connectivity.
   */
  @Test
  public void testConnectiom(){
    try {
      SQLAdapter sqlAdapter = new SQLAdapter();
      connection = sqlAdapter.openDbConnection();
      assertEquals(connection.isClosed(),false);
    }catch (Exception e){
      e.printStackTrace();
    }

  }
}
