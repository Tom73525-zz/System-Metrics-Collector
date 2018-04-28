package org.robert.tom;

import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DB_Retrieveal_Test {

  Connection connection = null;

  /**
   * This method checks if the data is successfully retrieved from  the
   * database.
   */
  @Test
  public void testDbRetrieval(){
    SQLAdapter sqlAdapter = new SQLAdapter();
    try {
      connection = sqlAdapter.openDbConnection();

      ArrayList<MCAProcess> mcaProcesses1 = new ArrayList<MCAProcess>();
      MCAProcess mcaProcess = new MCAProcess();
      mcaProcess.setCpuUsage(12.3);
      mcaProcess.setStartTime(12165553L);
      mcaProcess.setUtime(43L);
      mcaProcess.setStime(22L);
      mcaProcess.setCutime(2L);
      mcaProcess.setCstime(5L);
      mcaProcess.setNumThreads(2);
      mcaProcess.setState('S');
      mcaProcess.setVmSize(432L);
      mcaProcess.setName("abc");
      mcaProcess.setPpid(44);
      mcaProcess.setPid(2122);

      mcaProcesses1.add(mcaProcess);

      sqlAdapter.saveProcessMetrics(mcaProcesses1,
              connection);

      ArrayList<MCAProcess> mcaProcesses2 = sqlAdapter
              .getProcessLast5SecOlder(connection);
      assertEquals(true,mcaProcesses2.size() > 0);
    }catch(Exception e){

    }


  }
}
