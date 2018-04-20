package org.robert.tom;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class DB_Population_Test {

  @Test
  public void testDB(){
    SQLAdapter sqlAdapter = new SQLAdapter();
    boolean isMetricsSaved =   sqlAdapter.saveProcessMetrics(112,50,"firefox",
            1024,'S',2,53,643,1324,323,
            45,12.3);

    assertEquals(isMetricsSaved ,true);
  }
}
