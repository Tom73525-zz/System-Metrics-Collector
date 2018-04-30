package org.robert.tom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * MetricView
 * @author: Robert Muth
 *
 * Base class for Metric Collection App's Graphical User Interface
 * Contains a constructor, which sets up all components within a main JFrame window
 */
public class MetricFrame extends JFrame {

    // JPanel extension to house filter controls
    private FilterPanel filters;

    private SQLAdapter dbAdapter;

    private FilterEvent previousEvent;

    private Connection connection;

    /**
     * Constructor for main application window, sets title with super(String)
     *
     * @param windowTitle Title for created frame
     */
    public MetricFrame(String windowTitle) {
        super(windowTitle);
        setMinimumSize(new Dimension(960,720));

        // Create dbAdapter object
        try {
            dbAdapter = new SQLAdapter();
            connection = dbAdapter.openDbConnection();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // Initialize previousEvent filter
        previousEvent = new FilterEvent(this, false, -1, 0);

        // Set Layout Manager
        // BorderLayout will keep the Table window sized properly
        setLayout(new BorderLayout());


        // Create Swing Components
        /*
         * Only needs to hold JTable for displaying metrics
         *  STRETCH GOAL: This is where the tabbed panels will live.
         *      Unsure how that works, whether we update both of them and
         *      only display one at a time, or if we only update the
         *      currently visible panel.  If we only update the currently
         *      visible, FilterEvents will have to be handed to the
         *      proper tabbed panel.
         */
        filters = new FilterPanel();
        final JTable metricsTable = new JTable();
        final JScrollPane tableScrollPane = new JScrollPane(metricsTable);


        // Add Swing Components to content pane

        // Panels
        Container c = getContentPane();
        c.add(tableScrollPane, BorderLayout.CENTER);
        c.add(filters, BorderLayout.WEST);


        // Add behaviour
        /*
         * Handle FilterEvents here
         * Also handle periodic updates here
         *  Can probably just create a thread with a simple run function that calls
         *  the metricGathering utility and fires a new FilterEvent
         */

        //// TEMPORARY TABLE FILL ////////////////////////////////////////////////////////////
        MetricCollector mc = new MetricCollector();
        ArrayList<MCAProcess> procs = new ArrayList<MCAProcess>();

        // metric Collection
        try {
            mc.gatherCurrentPidList();
            for (Integer currentPid : mc.getPidList()) {
                procs.add(mc.collectMetricsForPid(currentPid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        // TABLE SETUP

        String[] columns = new String[]{
                "pid", "name", "state", "ppid", "utime", "stime", "numThreads",
                "startTime", "vmSize", "timeStamp", "cpuUsage"
        };

        final DefaultTableModel tableModel = new DefaultTableModel(0, 0);
        tableModel.setColumnIdentifiers(columns);
        metricsTable.setModel(tableModel);

//        // Adding Proc Rows to table
//        for (MCAProcess currentProc : procs) {
//            Object[] metricArray = currentProc.getProcessArray();
//            tableModel.addRow(metricArray);
//        }
//
//        tableModel.fireTableDataChanged();


        filters.addFilterListener(new FilterListener() {
            public void filterEventOccurred(FilterEvent event) {
                // Create Table Here
                ArrayList<MCAProcess> tableProcesses = new ArrayList<MCAProcess>();


                if (event.isNewFlag()) {
                    // Add to DB here
                    MetricCollector filterMc = new MetricCollector();
                    try {
                        filterMc.gatherCurrentPidList();
                        tableProcesses = filterMc.collectMetrics();
                        boolean storedMetrics = dbAdapter.saveProcessMetrics(tableProcesses, connection);
                        System.out.println("Stored Metrics: " + storedMetrics);
                    } catch (Exception e) {
                        // MOVE THIS TO FUNCTION CALL, RETURN NULL TO PROMPT NEW GATHERING ATTEMPT
                        e.printStackTrace();
                    }
                } else {
                    previousEvent = event;
                }

                // Retrieve relevant process records from database
                //  If FilterPid is positive, user wants to see records for a certain process
                //  Else, user wants to see current records for all processes
                tableProcesses.clear();
                try{
                    if(previousEvent.getFilterPid() > 0) {
                        tableProcesses = dbAdapter.getMaxStartTime(previousEvent.getFilterPid(), connection);
                        System.out.println("Filter Pid = " + previousEvent.getFilterPid());
                        System.out.println("Retrieved " + tableProcesses.size() + " records.");
                    } else {
                        tableProcesses = dbAdapter.getProcessLast5SecOlder(connection);
                        System.out.println("Retrieved " + tableProcesses.size() +" procs");
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }


                int[] currentFilterList = previousEvent.filterGroups[previousEvent.getFilterType()];
                ArrayList<Object> tableColumnHeaders = new ArrayList<Object>();
                for (int currentIndex : currentFilterList) {
                    tableColumnHeaders.add(previousEvent.filterList[currentIndex]);
                }

                // sets Column Identifiers in tableModel to retrieved headers from FilterEvent
                tableModel.setColumnIdentifiers(tableColumnHeaders.toArray());
                metricsTable.setModel(tableModel);
                // Clear Table
                tableModel.setNumRows(0);

                // First sort the arrayList of processes
                Collections.sort(tableProcesses, new Comparator<MCAProcess>() {
                    public int compare(MCAProcess o1, MCAProcess o2) {
                        return o1.getPid() - o2.getPid();
                    }
                });

                int scrollBarValue = tableScrollPane.getVerticalScrollBar().getValue();

                // Populate the Table Model from the sorted Process ArrayList
                for (MCAProcess currentProc : tableProcesses) {
                    Object[] currentProcArray = currentProc.getProcessArray();
                    ArrayList<Object> newRow = new ArrayList<Object>();
                    for (int currentIndex : currentFilterList) {
                        newRow.add(currentProcArray[currentIndex]);
                    }
                    tableModel.addRow(newRow.toArray());
                }

                tableScrollPane.getVerticalScrollBar().setValue(scrollBarValue);

                // Update table contents
                tableModel.fireTableDataChanged();
            }
        });

    }


}