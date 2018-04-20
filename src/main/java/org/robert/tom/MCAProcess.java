package org.robert.tom;


import java.sql.Timestamp;

public class MCAProcess {
    private Integer pid, ppid, numThreads;
    private String name;
    private char state;
    private Long utime, stime, cutime, cstime, starttime, vmsize;
    private Timestamp timeStamp;
    private Double cpuUsage;

    public MCAProcess(){
        this.pid = 0;
        this.ppid = 0;
        this.numThreads = 0;
        this.name = "";
        this.state = ' ';
        this.utime = 0L;
        this.stime = 0L;
        this.cutime = 0L;
        this.cstime = 0L;
        this.starttime = 0L;
        this.vmsize = 0L;
        this.timeStamp = null;
        this.cpuUsage = 0.0;
    }

    public int getPid() {
        return this.pid;
    }

    public void setPid(Integer inPid) {
        this.pid = inPid;
    }

    public int getPpid() {
        return this.ppid;
    }

    public void setPpid(Integer inPpid) {
        this.ppid = inPpid;
    }

    public int getNumThreads() {
        return this.numThreads;
    }

    public void setNumThreads(Integer inNumThreads) {
        this.numThreads = inNumThreads;
    }

    public char getState() {
        return state;
    }

    public void setState(char state) {
        this.state = state;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String inCmd) {
        this.name = inCmd;
    }

    public long getUtime() {
        return this.utime;
    }

    public void setUtime(Long inUtime) {
        this.utime = inUtime;
    }

    public long getStime() {
        return this.stime;
    }

    public void setStime(Long inStime) {
        this.stime = inStime;
    }

    public long getCutime() {
        return this.cutime;
    }

    public void setCutime(Long inCutime) {
        this.cutime = inCutime;
    }

    public long getCstime() {
        return this.cstime;
    }

    public void setCstime(Long inCstime) {
        this.cstime = inCstime;
    }

    public long getStartTime() {
        return this.starttime;
    }

    public void setStartTime(Long inStartTime) {
        this.starttime = inStartTime;
    }

    public long getVmSize() {
        return this.vmsize;
    }

    public void setVmSize(Long inVmSize) {
        this.vmsize = inVmSize;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Object[] getProcessArray() {
        return new Object[]{
                this.pid,
                this.name,
                this.state,
                this.ppid,
                this.utime,
                this.stime,
                this.numThreads,
                this.starttime,
                this.vmsize,
                this.timeStamp,
                this.cpuUsage
        };
    }
}