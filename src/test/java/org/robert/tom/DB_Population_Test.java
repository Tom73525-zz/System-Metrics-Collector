package org.robert.tom;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class DB_Population_Test {

  @Test
  public void testDB(){
    SQLAdapter sqlAdapter = new SQLAdapter();
    boolean isMetricsSaved = sqlAdapter.saveProcessMetrics(102,50,"firefox",
            1024,'S',2,34,10,
            12.3);

    assertEquals(isMetricsSaved ,true);
  }
}
