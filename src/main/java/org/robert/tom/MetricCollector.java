package org.robert.tom;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MetricCollector {

    // Contains an ArrayList of integers, representing the
    //  set of MCAProcess ID's existing during the last round of
    //  metric collections.
    private ArrayList<Integer> pidList;

    MetricCollector() {
        pidList = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getPidList() {
        return this.pidList;
    }

    /**
     * Returns the current system uptime, gathered from /proc/uptime
     * @return The current system uptime (in seconds since boot)
     */
    public Double getSystemUptime() {
        File uptimeFile;
        Double uptime = null;

        try {
            uptimeFile = new File("/proc/uptime");
            if (uptimeFile.exists()) {
                Scanner scanner = new Scanner(uptimeFile);
                uptime = scanner.nextDouble();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return uptime;
    }

    /**
     * Looks through "/proc/" and returns the names of all objects found
     *  which have names that only consist of digits and are directories.
     *  This gives a listing of all currently running processes on the system.
     * @throws IOException thrown if procfs is not found.  This is unlikely,
     * but this check is still necessary.  The exception is to be handled by
     * the caller in whichever manner is necessary.
     */
    public void gatherCurrentPidList() throws IOException {
        File procDir;
        procDir = new File("/proc/");

        // Clearing pidList
        this.pidList.clear();

        if (!procDir.exists()) {
            throw new FileNotFoundException(procDir.getPath());
        }

        String[] directories = procDir.list(new FilenameFilter() {
            public boolean accept(File current, String name) {
                File tempFile = new File(current, name);
                return tempFile.getName().matches("[0-9]+") &&
                        tempFile.isDirectory();
            }
        });

        if ((directories != null) && directories.length > 0) {
            for (String dir : directories) this.pidList.add(Integer.valueOf(dir));
        }
    }

    /**
     * This function completes all steps to gather metrics for all processes
     * currently represented in the /proc/ psuedo-filesystem.  It first calls
     * the gatherCurrentPidList function to populate the current list of
     * running processes, then calls collectMetricsForPid for each process to generate
     * an ArrayList of each currently running process.  This collection of processes
     * is then returned to be handled by the caller.
     *
     * @return an ArrayList containing all currently running processes and metrics,
     * null if an exception is caught
     */
    public ArrayList<MCAProcess> collectMetrics() {
        ArrayList<MCAProcess> mcaProcessArrayList = new ArrayList<MCAProcess>();
        try {
            this.gatherCurrentPidList();
            for (Integer currentPid : this.pidList) {
                mcaProcessArrayList.add(collectMetricsForPid(currentPid));
            }
        } catch (Exception e) {
            // Can be reached from gatherCurrentPidList
            //  If current pidList cannot be collected,
            //   return null for this round of collection,
            //   as procfs cannot be reached.
            e.printStackTrace();
            return null;
        }

        return mcaProcessArrayList;
    }

    /**
     * Calls several functions to open and parse files in procfs.
     *  First collects metrics from /proc/[pid]/stat
     *  Then collects metrics from /proc/[pid]status
     * @param pid The process ID for which to collect metrics
     * @return A MCAProcess object with metrics for process with [pid]
     */
    public MCAProcess collectMetricsForPid(Integer pid) {
        MCAProcess mcaprocess = new MCAProcess();
        File statFile = new File("/proc/" + pid.toString() + "/stat");
        File statusFile = new File("/proc/" + pid.toString() + "/status");


        // StatFile HERE
        mcaprocess = getStatMetrics(mcaprocess, statFile);


        // STATUSFILE HERE
        mcaprocess = getStatusMetrics(mcaprocess, statusFile);


        return mcaprocess;
    }


    /**
     * Retrieving metrics for mcaProcess with [pid]
     * Scans 'stat' file within mcaProcess directory,
     * splitting the file into separate String objects in an array.
     * Places values from array into 'mcaProcess' based on position
     * within the line, as described in proc documentation.
     */
    public MCAProcess getStatMetrics(MCAProcess mcaProcess, File statFile) {
        Scanner scanner = null;

        try {
            scanner = new Scanner(statFile);
            String[] stats = scanner.nextLine().split("\\s+");
            if (stats.length == 52) {
                mcaProcess.setPid(Integer.decode(stats[0]));
                mcaProcess.setName(stats[1].substring(1, stats[1].length() - 1));
                mcaProcess.setState(stats[2].charAt(0));
                mcaProcess.setPpid(Integer.valueOf(stats[3]));
                mcaProcess.setUtime(Long.valueOf(stats[13]));
                mcaProcess.setStime(Long.valueOf(stats[14]));
                mcaProcess.setCutime(Long.valueOf(stats[15]));
                mcaProcess.setCstime(Long.valueOf(stats[16]));
                mcaProcess.setNumThreads(Integer.valueOf(stats[19]));
                mcaProcess.setStartTime(Long.valueOf(stats[21]));
            } else {
                System.out.println("ERROR: Incorrect number of elements in stat file: " + statFile.getPath());
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }

        return mcaProcess;
    }


    /**
     * Retrieving VmSize from /proc/[pid]/status
     * Scans lines in the file until a line containing the string "VmSize"
     * is found.
     * Once found, the second to last token in the string (after splitting
     * the string using spaces as a delimiter) will be the desired value.
     */
    public MCAProcess getStatusMetrics(MCAProcess mcaProcess, File statusFile) {
        Scanner scanner = null;
        Long vmSize = 0L;

        try {
            scanner = new Scanner(statusFile);
            while (scanner.hasNextLine()) {
                String[] scanLine = scanner.nextLine().split("\\s+");
                if (scanLine[0].contains("VmSize")) {
                    vmSize = Long.valueOf(scanLine[scanLine.length - 2]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                mcaProcess.setVmSize(vmSize);
                scanner.close();
            }
        }

        return mcaProcess;
    }
}
