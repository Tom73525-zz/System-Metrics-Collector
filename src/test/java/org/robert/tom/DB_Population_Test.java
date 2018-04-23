package org.robert.tom;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class DB_Population_Test {

  /**
   * This method tests for the population of the database
   */
  @Test
  public void testDB(){
    SQLAdapter sqlAdapter = new SQLAdapter();
    ArrayList<MCAProcess> mcaProcesses = new ArrayList<MCAProcess>();
    MCAProcess mcaProcess = new MCAProcess();
    mcaProcess.setCpuUsage(12.3);
    mcaProcess.setStartTime(0L);
    mcaProcess.setUtime(43L);
    mcaProcess.setStime(22L);
    mcaProcess.setCutime(2L);
    mcaProcess.setCstime(5L);
    mcaProcess.setNumThreads(2);
    mcaProcess.setState('S');
    mcaProcess.setVmSize(432L);
    mcaProcess.setName("firefox");
    mcaProcess.setPpid(44);
    mcaProcess.setPid(10);
    mcaProcesses.add(mcaProcess);
    sqlAdapter.saveProcessMetrics(mcaProcesses);
    sqlAdapter = new SQLAdapter();
    boolean isMetricsSaved =   sqlAdapter.saveProcessMetrics(mcaProcesses);

    assertEquals(isMetricsSaved ,true);
  }
}
