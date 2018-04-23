package org.robert.tom;

import org.junit.*;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void metricRetrievalTest(){
        MCAProcess testProc = new MCAProcess();
        MetricCollector testCollector = new MetricCollector();

        URL statUrl = this.getClass().getResource("/testStat");
        URL statusUrl = this.getClass().getResource("/testStatus");

        System.out.println(statUrl.getFile());

        File testStatFile = new File(statUrl.getFile());
        File testStatusFile = new File(statusUrl.getFile());

        testProc = testCollector.getStatMetrics(testProc, testStatFile);
        testProc = testCollector.getStatusMetrics(testProc, testStatusFile);

        assertEquals(371, testProc.getPid());
        assertEquals("ips-monitor", testProc.getName());
        assertEquals('S', testProc.getState());
        assertEquals(2, testProc.getPpid());
        assertEquals(30, testProc.getUtime());
        assertEquals(67, testProc.getStime());
        assertEquals(352, testProc.getCutime());
        assertEquals(985, testProc.getCstime());
        assertEquals(1, testProc.getNumThreads());
        assertEquals(1289, testProc.getStartTime());
        assertEquals(0, testProc.getVmSize());
    }
}
