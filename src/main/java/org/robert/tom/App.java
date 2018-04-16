package org.robert.tom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;


public class App {

    private static void launchGUI() {
        String[] columns = new String[]{
                "pid", "name", "state", "ppid", "utime", "stime", "numThreads",
                "startTime", "vmSize", "bytesReceived", "bytesSent"
        };

        JFrame frame = new JFrame("Metrics Collection App");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Setting up Objects
        ArrayList<Process> procs = new ArrayList<Process>();
        MetricCollector mc = new MetricCollector();

        // Gathering metrics
        try {
            mc.gatherCurrentPidList();
            for (Integer currentPid : mc.getPidList()) {
                procs.add(mc.collectMetrics(currentPid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // TABLE SETUP
        JTable table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel(0, 0);
        tableModel.setColumnIdentifiers(columns);
        table.setModel(tableModel);

        // Adding Proc Rows to table
        for (Process currentProc : procs) {
            Object[] metricArray = currentProc.getProcessArray();
            tableModel.addRow(metricArray);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        frame.getContentPane().add(scrollPane);

        frame.pack();
        frame.setSize(500, 300);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        System.out.println("Loading interface...");

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                launchGUI();
            }
        });
    }
}
