package org.robert.tom;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CpuUsage_Test {


  @Test

  public void testPrecision(){

    SQLAdapter sqlAdapter = new SQLAdapter();

    MCAProcess mcaProcess = new MCAProcess();

    mcaProcess.setCstime(100L);
    mcaProcess.setStime(103232L);
    mcaProcess.setCutime(10L);
    mcaProcess.setUtime(100L);
    mcaProcess.setStartTime(0L);

    Double cpu = sqlAdapter.getCpuUsage(mcaProcess,1323.3232,10);

    String[] split = cpu.toString().split("\\.");
    split[0].length();

    int decimalLength = split[1].length();

    assertEquals(2,decimalLength);

  }

}
