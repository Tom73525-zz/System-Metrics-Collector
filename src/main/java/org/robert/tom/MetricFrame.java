package org.robert.tom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * MetricView
 * Author: Robert Muth
 * <p>
 * Base class for Metric Collection App's Graphical User Interface
 * Contains a constructor, which sets up all components within a main JFrame window
 */
public class MetricFrame extends JFrame {

    // JPanel extension to house filter controls
    private FilterPanel filters;

    private final SQLAdapter dbAdapter;

    /**
     * Constructor for main application window, sets title with super(String)
     * @param windowTitle Title for created frame
     */
    public MetricFrame(String windowTitle) {
        super(windowTitle);

        // Create dbAdapter object
        dbAdapter = new SQLAdapter();

        // Set Layout Manager
        // BorderLayout will keep the Table window sized properly
        setLayout(new BorderLayout());


        // Create Swing Components
        /**
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
        JScrollPane tableScrollPane = new JScrollPane(metricsTable);


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
            // NEED TO MOVE THIS TO FUNCTION CALL, RETURN NULL to prompt new gathering attempt
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

        // Adding Proc Rows to table
        for (MCAProcess currentProc : procs) {
            Object[] metricArray = currentProc.getProcessArray();
            tableModel.addRow(metricArray);
        }

        tableModel.fireTableDataChanged();


        //// TEMPORARY TABLE FILL ////////////////////////////////////////////////////////////


        filters.addFilterListener(new FilterListener() {
            public void filterEventOccurred(FilterEvent event) {
                // Create Table Here
                ArrayList<MCAProcess> tableProcesses = new ArrayList<MCAProcess>();

                if(event.isNewFlag()){
                    // Metric Collection here
                    MetricCollector filterMc = new MetricCollector();
                    try {
                        filterMc.gatherCurrentPidList();
                        tableProcesses = filterMc.collectMetrics();
                    } catch (Exception e) {
                        // MOVE THIS TO FUNCTION CALL, RETURN NULL TO PROMPT NEW GATHERING ATTEMPT
                        e.printStackTrace();
                    }
                } else {
                    //tableProcesses = dbAdapter.
                }

                int[] currentFilterList = event.filterGroups[event.getFilterType()];
                ArrayList<Object> tableColumnHeaders = new ArrayList<Object>();
                for(int currentIndex: currentFilterList) {
                    tableColumnHeaders.add(event.filterList[currentIndex]);
                }

                // sets Column Identifiers in tableModel to retrieved headers from FilterEvent
                tableModel.setColumnIdentifiers(tableColumnHeaders.toArray());
                metricsTable.setModel(tableModel);

                // Get metrics from DB
                if(!event.isNewFlag()) {
                    //
                }

                // for (ArrayList<> currentProc: DBProcesses) {
                //      Object[] curProcArray
                //      ArrayList<Object> newRow = new ArrayList<>
                //      for(int currentIndex: currentFilterList){
                //          newRow.add(curProcArray[currentIndex])
                //      }
                //      table.addRow(currentProc)
                //  }
            }
        }); // filters.addFilterListener

    } // public MetricView(){...}



}