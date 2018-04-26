package org.robert.tom;

import org.junit.Test;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class getLatestTime_Test {

  Connection connection = null;

  /**
   * This method tests if the database returns the latest timestamp within
   * the last 5 second window
   */
  @Test
  public void testLatestTime(){

    SQLAdapter sqlAdapter = new SQLAdapter();

    try{

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
      mcaProcess.setPid(122);
      mcaProcesses.add(mcaProcess);
      sqlAdapter.saveProcessMetrics(mcaProcesses, connection);

      boolean timeStampExists = sqlAdapter
              .getLatestTime(122,connection) != null;

      boolean isGreaterThan5Secondsb4CurrentTime = sqlAdapter
              .getLatestTime(122,connection)
              .after(new Timestamp(System.currentTimeMillis() - 5000));

      assertEquals(true, timeStampExists
              && isGreaterThan5Secondsb4CurrentTime);

    }catch (Exception e){

    }
  }
}
