package org.robert.tom;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MetricCollector {

  private ArrayList<Integer> pidList;

  public MetricCollector() {
    pidList = new ArrayList<Integer>();
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

  public ArrayList<Integer> getCurrentPidlist() throws IOException {
    File procDir;

    procDir = new File("/proc/");

    if (!procDir.exists()) {
      throw new FileNotFoundException(procDir.getPath());
    }

    String[] directories = procDir.list(new FilenameFilter() {
      public boolean accept(File current, String name) {
        File tempFile = new File(current, name);
        if (tempFile.getName().matches("[0-9]+")) {
          System.out.println(tempFile.getName());
          return tempFile.isDirectory();
        } else {
          return false;
        }
      }
    });

    if (directories.length > 0) {
      ArrayList<Integer> pidList = new ArrayList<>();
      for (String dir : directories) {
        pidList.add(Integer.valueOf(dir));
      }
    }

    return pidList;
  }

  public Process collectMetrics(Integer pid) {
    Process process = new Process();
    File procDir = new File("/proc/" + pid.toString());
    File statFile = new File(procDir, "stat");
    File statusFile = new File(procDir, "status");
    File netDevFile = new File("/proc/" + pid.toString() + "/net/dev");
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
        System.out.println("ERROR: Incorrect number of elements in stat for pid: " + pid.toString());
        return null;
      }
//      process.setPid(scanner.nextInt()); // 1
//      process.setName(scanner.next("\\((.+?)\\)")); // 2
//      process.setState(scanner.next().charAt(0)); // 3
//      process.setPpid(scanner.nextInt()); // 4
//      scanner.next();
//      scanner.next();
//      process.setNumThreads(scanner.nextInt()); // 20
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return null;
    } finally {
      if (scanner != null) {
        scanner.close();
      }
    }

    try {
      scanner = new Scanner(statusFile);
      String currentLine;
      while (scanner.hasNextLine()) {
        if (scanner.next().contains("VmSize")) {
          process.setVmSize(scanner.nextLong());
          scanner.nextLine();
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } finally {
      if (scanner != null) {
        scanner.close();
      }
    }

    try {
      scanner = new Scanner(netDevFile);
      Long recBytes = 0L;
      Long sentBytes = 0L;
      String currentLine;
      scanner.nextLine();
      while (scanner.hasNextLine()) {
        scanner.next();
        recBytes += scanner.nextLong(); // Add received bytes from current line's interface
        scanner.next(); // rec packets
        scanner.next(); // rec errors
        scanner.next(); // rec drop
        scanner.next(); // rec fifo
        scanner.next(); // rec frame
        scanner.next(); // rec compressed
        scanner.next(); // rec multicast
        sentBytes += scanner.nextLong(); // Add sent bytes from current line's interface
        scanner.nextLine();
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