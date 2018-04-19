package org.robert.tom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
        final ArrayList<Process> procs = new ArrayList<Process>();
        final MetricCollector mc = new MetricCollector();

        // Gathering metrics
        try {
            mc.gatherCurrentPidList();
            for (Integer currentPid : mc.getPidList()) {
                procs.add(mc.collectMetricsForPid(currentPid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // TABLE SETUP
        final JTable table = new JTable();
        final DefaultTableModel tableModel = new DefaultTableModel(0, 0);
        tableModel.setColumnIdentifiers(columns);
        table.setModel(tableModel);

        // Adding Proc Rows to table
        for (Process currentProc : procs) {
            Object[] metricArray = currentProc.getProcessArray();
            tableModel.addRow(metricArray);
        }


        // BUTTON SETUP
        JButton updateButton = new JButton("Refresh");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                procs.clear();

                try {
                    mc.gatherCurrentPidList();
                    for (Integer currentPid : mc.getPidList()) {
                        procs.add(mc.collectMetricsForPid(currentPid));
                    }

                    tableModel.setRowCount(0);

                    for (Process proc : procs) {
                        Object[] updateMetricArray = proc.getProcessArray();
                        tableModel.addRow(updateMetricArray);
                    }

                    tableModel.fireTableDataChanged();

                } catch (IOException updateExeption) {
                    updateExeption.printStackTrace();
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        JPanel metricsContainer = new JPanel(new GridBagLayout());
        frame.getContentPane().add(metricsContainer);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        metricsContainer.add(updateButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 8;
        gbc.weighty = 8;
        gbc.gridheight = 8;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(table);
        metricsContainer.add(scrollPane, gbc);

        frame.pack();
        frame.setSize(960, 720);
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
