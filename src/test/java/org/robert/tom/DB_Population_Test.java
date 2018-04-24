package org.robert.tom;

import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DB_Population_Test {

  Connection connection = null;
  /**
   * This method tests for the population of the database
   */
  @Test
  public void testDB(){

    SQLAdapter sqlAdapter = new SQLAdapter();
    try {
      connection = sqlAdapter.openDbConnection();
      ArrayList<MCAProcess> mcaProcesses = new ArrayList<MCAProcess>();
      MCAProcess mcaProcess = new MCAProcess();
      mcaProcess.setCpuUsage(12.3);
      mcaProcess.setStartTime(1214L);
      mcaProcess.setUtime(43L);
      mcaProcess.setStime(22L);
      mcaProcess.setCutime(2L);
      mcaProcess.setCstime(5L);
      mcaProcess.setNumThreads(2);
      mcaProcess.setState('S');
      mcaProcess.setVmSize(432L);
      mcaProcess.setName("abc");
      mcaProcess.setPpid(44);
      mcaProcess.setPid(10);
      mcaProcesses.add(mcaProcess);
      boolean isMetricsSaved = sqlAdapter.saveProcessMetrics(mcaProcesses, connection);
      assertEquals(isMetricsSaved ,true);
    }catch(Exception e){

    }

  }
}
