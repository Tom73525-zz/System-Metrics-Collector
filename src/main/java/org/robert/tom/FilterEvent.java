package org.robert.tom;

import java.util.EventObject;

/*
   0 pid
   1 name
   2 state
   3 ppid
   4 utime
   5 stime
   6 numThreads
   7 starttime
   8 vmsize
   9 TimeStamp
   10 cpuUsage

   Basic Info Filter
    0, 1, 2, 3, 7
   Resources
    0, 1, 9, 8, 10
    CPU Info
    0, 1, 9, 10, 4, 5, 6
 */

public class FilterEvent extends EventObject {

    // Indexes of filter groups within filterGroups array
    public final static int BASICINFO = 0;
    public final static int RESOURCES = 1;
    public final static int CPUINFO = 2;

    // Contains indexes of
    public final static int[][] filterGroups = {
            {0, 1, 2, 3, 7},            // Basic Info
            {1, 2, 9, 8, 10},           // Resources
            {0, 1, 9, 10, 4, 5, 6}};    // CPU Info

    // List of Columns Headers to build table
    public final static String[] filterList = new String[]{
            "pid",
            "name",
            "state",
            "ppid",
            "utime",
            "stime",
            "numThreads",
            "startTime",
            "vmSize",
            "timeStamp",
            "cpuUsage"
    };

    /////  SET BY EVENT FIRER /////
    // If set, pull new metrics
    private boolean newFlag;

    // For use with single pid filter
    private int filterPid;

    // Filled with one of filter group constants
    private int filterType;


    /**
     * Used to fire new event, building a new table to display metrics
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public FilterEvent(Object source, boolean isNew, int filterByPid, int filterClass) {
        super(source);

        this.newFlag = isNew;
        this.filterPid = filterByPid;
        this.filterType = filterClass;
    }


    public boolean isNewFlag() {
        return newFlag;
    }

    public void setNewFlag(boolean newFlag) {
        this.newFlag = newFlag;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public int getFilterPid() {

        return filterPid;
    }

    public void setFilterPid(int filterPid) {
        this.filterPid = filterPid;
    }
}