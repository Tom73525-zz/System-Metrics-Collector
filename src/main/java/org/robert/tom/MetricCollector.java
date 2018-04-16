package org.robert.tom;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MetricCollector {

    private ArrayList<Integer> pidList;

    MetricCollector() {
        pidList = new ArrayList<Integer>();
    }

    public ArrayList<Integer> getPidList() {
        return this.pidList;
    }

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

    public Process collectMetrics(Integer pid) {
        Process process = new Process();
        File statFile = new File("/proc/" + pid.toString() + "/stat");
        File statusFile = new File("/proc/" + pid.toString() + "/status");
        File netDevFile = new File("/proc/" + pid.toString() + "/net/dev");


        // StatFile HERE
        process = getStatMetrics(process, statFile);


        // STATUSFILE HERE
        process = getStatusMetrics(process, statusFile);


        //NETDEV HERE
        process = getNetDevMetrics(process, netDevFile);

        return process;
    }


    /**
     * Retrieving metrics for process with [pid]
     *   Scans 'stat' file within process directory,
     *    splitting the file into separate String objects in an array.
     *   Places values from array into 'process' based on position
     *    within the line, as described in proc documentation.
     */
    public Process getStatMetrics(Process process, File statFile) {
        Scanner scanner = null;

        try {
            scanner = new Scanner(statFile);
            String[] stats = scanner.nextLine().split("\\s+");
            if (stats.length == 52) {
                process.setPid(Integer.decode(stats[0]));
                process.setName(stats[1].substring(1, stats[1].length() - 1));
                process.setState(stats[2].charAt(0));
                process.setPpid(Integer.valueOf(stats[3]));
                process.setUtime(Long.valueOf(stats[13]));
                process.setStime(Long.valueOf(stats[14]));
                process.setCutime(Long.valueOf(stats[15]));
                process.setCstime(Long.valueOf(stats[16]));
                process.setNumThreads(Integer.valueOf(stats[19]));
                process.setStartTime(Long.valueOf(stats[21]));
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

        return process;
    }


    /**
     * Retrieving VmSize from /proc/[pid]/status
     *  Scans lines in the file until a line containing the string "VmSize"
     *    is found.
     *  Once found, the second to last token in the string (after splitting
     *    the string using spaces as a delimiter) will be the desired value.
     */
    public Process getStatusMetrics(Process process, File statusFile) {
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
                process.setVmSize(vmSize);
                scanner.close();
            }
        }

        return process;
    }


    /**
     * Calculating network traffic to and from process using /proc/[pid]/net/dev
     *  Skips first two lines of file (they contain header information to lay out
     *    data in a table).
     *  For each line after header information, adds 2rd value in line to accumulator
     *    for bytes received, and adds the 10th value to bytes sent.
     */
    public Process getNetDevMetrics(Process process, File netDevFile) {
        Scanner scanner = null;

        try {
            scanner = new Scanner(netDevFile);
            Long recBytes = 0L;
            Long sentBytes = 0L;

            if (scanner.hasNextLine()) {
                scanner.nextLine();
                scanner.nextLine();
                while (scanner.hasNextLine()) {
                    String[] scanLine = scanner.nextLine().trim().split("\\s+");
                    recBytes += Long.valueOf(scanLine[1]);
                    sentBytes += Long.valueOf(scanLine[9]);
                }
            }
            process.setBytesReceived(recBytes);
            process.setBytesSent(sentBytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }


        return process;
    }
}
