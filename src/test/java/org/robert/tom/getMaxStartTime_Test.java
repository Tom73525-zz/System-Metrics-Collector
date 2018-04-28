package org.robert.tom;

import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;

public class getMaxStartTime_Test {

  Connection connection = null;

  /**
   * This method tests the getMaxStartTime(pid) method
   */
  @Test
  public void testGetMaxStartTime(){
    SQLAdapter sqlAdapter = new SQLAdapter();

    try{

      connection = sqlAdapter.openDbConnection();

      assertEquals(true,sqlAdapter.getMaxStartTime(2122,connection).size() > 0);

    }catch (Exception e){

    }
  }
}
