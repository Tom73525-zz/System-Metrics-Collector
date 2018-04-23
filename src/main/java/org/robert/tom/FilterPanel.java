package org.robert.tom;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * FilterPanel
 * Author: Robert Muth
 * <p>
 * Extends JPanel, for use with MetricView class within Metric Collection App
 * Contains controls for filtering the displayed data within the MetricView
 * Utilizes Custom Events to notify the parent container of updated filter
 * settings.
 */
public class FilterPanel extends JPanel {

    // List of Event Listeners for use in firing events
    private EventListenerList listenerList = new EventListenerList();

    /**
     * Default constructor for Filter Panel
     * Sets up all components and controls
     */
    public FilterPanel() {

        // Setting up panel dimensions, which should remain constant as window is resized
        Dimension size = getPreferredSize();
        size.width = 250;
        setPreferredSize(size);

        // Creates border around control panel to separate controls from main table
        setBorder(BorderFactory.createTitledBorder("Filter Options"));

        // Creating Components for Controls
        JTextField nameField = new JTextField(10);
        JTextField occupationField = new JTextField(10);

        // Creating Components for Controls (REAL)
        // Plurality Button Group
        JRadioButton singleProcessRadio = new JRadioButton("Single Process");
        JRadioButton allProcessRadio = new JRadioButton("All Processes");
        ButtonGroup pluralityBG = new ButtonGroup();
        pluralityBG.add(singleProcessRadio);
        pluralityBG.add(allProcessRadio);

        // Filter Group Button Group
        JRadioButton basicInfoRadio = new JRadioButton("Basic Information");
        basicInfoRadio.setActionCommand("0");
        JRadioButton resourcesRadio = new JRadioButton("Resources");
        resourcesRadio.setActionCommand("1");
        JRadioButton cpuInfoRadio = new JRadioButton("CPU Information");
        cpuInfoRadio.setActionCommand("2");
        ButtonGroup filterGroupBG = new ButtonGroup();
        filterGroupBG.add(basicInfoRadio);
        filterGroupBG.add(resourcesRadio);
        filterGroupBG.add(cpuInfoRadio);

        /*
         * Creating button and adding ActionListener to it
         *  Should fire a filterEvent, which will be handed to each
         *  filterEventListener to handle as needed.  In the standard case,
         *  this will handle a listing of all chosen filters, which will then
         *  prompt the table to update based on filters.
         */
        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // FilterEvent scheduledEvent = new FilterEvent(this, true, GET pidRadio, GET filterClass);
                //fireFilterEvent(scheduledEvent);
            }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        //// Plurality Radio Buttons ////
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = 0.5;
        add(singleProcessRadio, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(allProcessRadio, gbc);

        //// Filter Group Radio Buttons ////
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(basicInfoRadio, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(resourcesRadio, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(cpuInfoRadio, gbc);


        //// Filter update ////
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 2;
        gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(filterButton, gbc);


        // Scheduled metric collection, runs every 5 seconds
        Runnable periodicMetricCollection = new Runnable() {
            public void run() {
                // FilterEvent scheduledEvent = new FilterEvent(this, true, GET pidRadio, GET filterClass);
                //fireFilterEvent(scheduledEvent);
            }
        };
        ScheduledExecutorService metricCollectionScheduler = Executors.newScheduledThreadPool(1);
        metricCollectionScheduler.scheduleAtFixedRate(periodicMetricCollection, 0, 5, TimeUnit.SECONDS);
    }

    public void fireFilterEvent(FilterEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == FilterListener.class) {
                ((FilterListener) listeners[i + 1]).filterEventOccurred(event);
            }
        }
    }

    public void addFilterListener(FilterListener listener) {
        listenerList.add(FilterListener.class, listener);
    }

    public void removeFilterListener(FilterListener listener) {
        listenerList.remove(FilterListener.class, listener);
    }
}