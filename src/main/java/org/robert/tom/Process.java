package org.robert.tom;


public class Process {
	private int pid, ppid, numThreads;
	private String name;
	private char state;
	private long utime, stime, cutime, cstime, starttime, vmsize, bytesSent, bytesReceived;

	public int getPid(){
		return this.pid;
	}

	public void setPid(int inPid){
		this.pid = inPid;
	}

	public int getPpid(){
		return this.ppid;
	}

	public void setPpid(int inPpid){
		this.ppid = inPpid;
	}

	public int getNumThreads(){
		return this.numThreads;
	}

	public void setNumThreads(int inNumThreads){
		this.numThreads = inNumThreads;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String inCmd){
		this.name = inCmd;
	}

	public long getUtime(){
		return this.utime;
	}

	public void setUtime(long inUtime){
		this.utime = inUtime;
	}

	public long getStime(){
		return this.stime;
	}

	public void setStime(long inStime){
		this.stime = inStime;
	}

	public long getCutime(){
		return this.cutime;
	}

	public void setCutime(long inCutime){
		this.cutime = inCutime;
	}

	public long getCstime(){
		return this.cstime;
	}

	public void setCstime(long inCstime){
		this.cstime = inCstime;
	}

	public long getStartTime(){
		return this.starttime;
	}

	public void setStartTime(long inStartTime){
		this.starttime = inStartTime;
	}

	public long getVmSize(){
		return this.vmsize;
	}

	public void setVmSize(long inVmSize){
		this.vmsize = inVmSize;
	}

	public long getBytesSent(){
		return this.bytesSent;
	}

	public void setBytesSent(long inBytesSent){
		this.bytesSent = inBytesSent;
	}

	public long getBytesReceived(){
		return this.bytesReceived;
	}

	public void setBytesReceived(long inBytesReceived){
		this.bytesReceived = inBytesReceived;
	}
}