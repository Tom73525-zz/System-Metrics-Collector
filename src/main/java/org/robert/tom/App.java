package org.robert.tom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;


public class App {

    private static native long getSystemClockInTicks();

    static {
        System.loadLibrary("mcollect");
    }

    public static void main(String[] args) {
        System.out.println("Loading interface...");

        System.out.println("System Clock ticks/second: " + getSystemClockInTicks());

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //launchGUI();
                MetricFrame metricView = new MetricFrame("Metric Collection App");
                metricView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                metricView.pack();
                metricView.setSize(960,720);
                metricView.setVisible(true);
            }
        });
    }
}