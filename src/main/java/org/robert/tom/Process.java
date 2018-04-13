package org.robert.tom;


public class Process {
    private Integer pid, ppid, numThreads;
    private String name;
    private char state;
    private Long utime, stime, cutime, cstime, starttime, vmsize, bytesSent, bytesReceived;
    private Double cpuUsage;

    public Integer getPid() {
        return this.pid;
    }

    public void setPid(Integer inPid) {
        this.pid = inPid;
    }

    public Integer getPpid() {
        return this.ppid;
    }

    public void setPpid(Integer inPpid) {
        this.ppid = inPpid;
    }

    public Integer getNumThreads() {
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

    public Long getUtime() {
        return this.utime;
    }

    public void setUtime(Long inUtime) {
        this.utime = inUtime;
    }

    public Long getStime() {
        return this.stime;
    }

    public void setStime(Long inStime) {
        this.stime = inStime;
    }

    public Long getCutime() {
        return this.cutime;
    }

    public void setCutime(Long inCutime) {
        this.cutime = inCutime;
    }

    public Long getCstime() {
        return this.cstime;
    }

    public void setCstime(Long inCstime) {
        this.cstime = inCstime;
    }

    public Long getStartTime() {
        return this.starttime;
    }

    public void setStartTime(Long inStartTime) {
        this.starttime = inStartTime;
    }

    public Long getVmSize() {
        return this.vmsize;
    }

    public void setVmSize(Long inVmSize) {
        this.vmsize = inVmSize;
    }

    public Long getBytesSent() {
        return this.bytesSent;
    }

    public void setBytesSent(Long inBytesSent) {
        this.bytesSent = inBytesSent;
    }

    public Long getBytesReceived() {
        return this.bytesReceived;
    }

    public void setBytesReceived(Long inBytesReceived) {
        this.bytesReceived = inBytesReceived;
    }

    public Double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
}