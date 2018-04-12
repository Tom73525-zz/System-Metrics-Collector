package org.robert.tom;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MetricCollector {

    private ArrayList<Integer> pidList;


    public MetricCollector() {
        pidList = new ArrayList<Integer>();
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
    }

    public Process collectMetrics(Integer pid) {
        Process process = new Process();
        File procDir = new File("/proc/" + pid.toString());
        File statFile = new File(procDir, "stat");
        try{
            Scanner scanner = new Scanner(statFile);
            process.setPid(scanner.nextInt());
            process.setName(scanner.next("\\((.+?)\\)"));
            process.set
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }
}