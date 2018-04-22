package org.robert.tom;

import java.sql.*;
import java.util.ArrayList;

public class SQLAdapter {

  public static Connection connection = null;
  private static Statement statement = null;


  /**
   * The SQLiteAdapter constructor establishes a connection to an existing
   * database through JDBC. If database is not found, a new database is
   * created.
   */
  public SQLAdapter() {
    try {
      Class.forName("org.sqlite.JDBC");

      connection = DriverManager.getConnection
              ("jdbc:sqlite:ProcessMetrics.db");

      statement = connection.createStatement();

      String createProcess = "CREATE TABLE IF NOT EXISTS PROCESS(" +
              "pid INTEGER PRIMARY KEY," + "ppid INTEGER," +
              "p_name VARCHAR(25));";

      String createMetrics = "CREATE TABLE IF NOT EXISTS METRICS( \n" +
              "pid INTEGER REFERENCES PROCESS(pid) ON UPDATE CASCADE,\n" +
              "p_timestamp DATETIME,\n" +
              "p_size INTEGER,\n" +
              "p_state CHARACTER(1) CHECK(" +
              "p_state == 'R' OR p_state == 'S' OR " + "p_state == 'D' OR " +
              "p_state == 'Z' OR p_state == 'T' OR " + "p_state == 't' OR " +
              "p_state == 'W' OR p_state == 'X' OR " + "p_state == 'x' OR " +
              "p_state == 'K' OR p_state == 'P'), \n" +
              "p_threads INTEGER, \n" +
              "p_cstime INTEGER, \n" +
              "p_cutime INTEGER, \n" +
              "p_stime INTEGER, \n" +
              "p_utime INTEGER, \n" +
              "p_startTime INTEGER, \n" +
              "p_cpu_utilization DOUBLE);\n";

      statement.execute(createProcess);
      statement.execute(createMetrics);
    } catch (Exception e) {
    }
  }

  /**
   * This method takes in an arraylist of MCAProcess, extracting the metrics and
   * calculate the cpu_usage. The the values are then inserted into the
   * database.
   * @param mcaProcesses - ArrayList of MCAProcess
   * @return - Returns true if all values are stored into the database.
   */
  protected boolean saveProcessMetrics(ArrayList<MCAProcess> mcaProcesses){

    int count = 1;
    MetricCollector metricCollector = new MetricCollector();
    Double uptime = metricCollector.getSystemUptime();
    for (MCAProcess mcaProcess : mcaProcesses) {
      int pid = mcaProcess.getPid();
      int ppid = mcaProcess.getPpid();
      String p_name = mcaProcess.getName();
      long p_size = mcaProcess.getVmSize();
      char p_state = mcaProcess.getState();
      int p_threads = mcaProcess.getNumThreads();
      long p_cstime = mcaProcess.getCstime();
      long p_cutime = mcaProcess.getCutime();
      long p_stime = mcaProcess.getStime();
      long p_utime = mcaProcess.getUtime();
      long p_startTime = mcaProcess.getStartTime();
     //double p_cpu_utilization = getCPUUsage(mcaProcess,uptime,
      // metricCollector.getClkTksPerSecond());

      // This is just a dummy input for uptime and clock_ticks
      double p_cpu_utilization = getCPUUsage(mcaProcess,240000.0,100);
      try {
        String insertProcess = "INSERT OR REPLACE INTO PROCESS " +
                "VALUES(\n" +
                pid + ",\n" +
                ppid + ",\n'" +
                p_name +
                "'\n);" +
                "\n";

        String insertMetrics = "INSERT INTO METRICS " +
                "VALUES(\n" +
                pid + ",\n" +
                "datetime('now', '-240 minutes')" + ",\n" +
                p_size + ",\n'" +
                p_state + "',\n" +
                p_threads + ",\n" +
                p_cstime + ",\n" +
                p_cutime + ",\n" +
                p_stime + ",\n" +
                p_utime + ",\n" +
                p_startTime + ",\n" +
                p_cpu_utilization + "\n);" +
                "\n";

        if(statement.executeUpdate(insertProcess) > 0 && statement
                .executeUpdate(insertMetrics) > 0){
          count++;
        }

      } catch (SQLException sql) {
        sql.printStackTrace();
      }
    }
    if(count == mcaProcesses.size()){
      return true;
    }
    return false;
  }

  // For testing purpose
  public static void main(String[] args) {
    SQLAdapter sqlAdapter = new SQLAdapter();
    ArrayList<MCAProcess> mcaProcesses = new ArrayList<MCAProcess>();
    MCAProcess mcaProcess = new MCAProcess();
    mcaProcess.setStartTime(20000L);
    mcaProcess.setUtime(43L);
    mcaProcess.setStime(22L);
    mcaProcess.setCutime(2L);
    mcaProcess.setCstime(5L);
    mcaProcess.setNumThreads(2);
    mcaProcess.setState('S');
    mcaProcess.setVmSize(432L);
    mcaProcess.setName("firefox");
    mcaProcess.setPpid(44);
    mcaProcess.setPid(1121);
    mcaProcesses.add(mcaProcess);
    sqlAdapter.saveProcessMetrics(mcaProcesses);
    sqlAdapter = new SQLAdapter();
    System.out.println(sqlAdapter.getLatestTime(1121)  );
    System.out.println(sqlAdapter.getProcessLast5SecOlder(1121).get
            (sqlAdapter.getProcessLast5SecOlder(1121).size() - 1)
            .getCpuUsage());
    
  }

  /**
   * Gets the name of the process
   * @param pid - pid of the process
   * @return - Returns name of the process, null if no such pid is found.
   */
  protected String getProcessName(int pid){
    try {
      String getPname = "SELECT p_name " +
              "FROM PROCESS " +
              "WHERE pid = " + pid + ";";
      ResultSet resultSet = statement.executeQuery(getPname);
      String result = resultSet.getString(1);
      resultSet.close();
      return result;
    }catch (Exception e){
    }
    return null;
  }

  /**
   * Gets the ppid of the process
   * @param pid  - pid of the process
   * @return - Returns ppid of the process, returns -1 if no such pid is found.
   */
  protected int getParentPid(int pid){
    try {
      String getPname = "SELECT ppid " +
              "FROM PROCESS " +
              "WHERE pid = " + pid + ";";
      ResultSet resultSet = statement.executeQuery(getPname);
      int ppid = resultSet.getInt(1);
      resultSet.close();
      return ppid;
    }catch (Exception e){
    }
    return -1;
  }

  /**
   * This method gets all MCAProcess from the database which fall under the
   * last 5 second window for a given pid.
   * @param pid - pid of the process
   * @return - Returns an arraylist of MCAProcess containing MCAProcess records
   * of the given pid.
   */
  protected ArrayList<MCAProcess> getProcessLast5SecOlder(int pid){
    ArrayList<MCAProcess> processes = new ArrayList<MCAProcess>();
    try {
      String getLast5SecOld = "SELECT * " +
              "FROM METRICS " +
              "WHERE pid = " + pid + " " +
              "AND p_timestamp >= datetime('now', '-245 minutes');";

      int ppid = getParentPid(pid);
      String p_name = getProcessName(pid);

      ResultSet resultSet = statement.executeQuery(getLast5SecOld);

      while(resultSet.next()){
        MCAProcess mcaProcess = new MCAProcess();
        mcaProcess.setPid(pid);
        mcaProcess.setPpid(ppid);
        mcaProcess.setName(p_name);
        mcaProcess.setTimeStamp(resultSet.getTimestamp(2));
        mcaProcess.setVmSize(resultSet.getLong(3));
        mcaProcess.setState((resultSet.getString(4).charAt(0)));
        mcaProcess.setNumThreads(resultSet.getInt(5));
        mcaProcess.setCstime(resultSet.getLong(6));
        mcaProcess.setCutime(resultSet.getLong(7));
        mcaProcess.setStime(resultSet.getLong(8));
        mcaProcess.setUtime(resultSet.getLong(9));
        mcaProcess.setStartTime(resultSet.getLong(10));
        mcaProcess.setCpuUsage(resultSet.getDouble(11));
        processes.add(mcaProcess);
      }
      resultSet.close();
    }catch (Exception e){
      e.printStackTrace();
    }
    return processes;
  }

  /**
   * Gets latest timestamp of the MCAProcess that fall under the last 5 second
   * window
   * @param pid - pid of the process
   * @return - Returns the latest timestamp of pid, null if no such pid is
   * found.
   */
  protected  Timestamp getLatestTime(int pid){

    try {
      String latestTime = "SELECT p_timestamp " +
              "FROM METRICS " +
              "WHERE p_timestamp >= datetime('now', '-245 minutes') " +
              "AND pid == " + pid + " " +
              "ORDER BY p_timestamp DESC;";
      ResultSet resultSet = statement.executeQuery(latestTime);
      return Timestamp.valueOf(resultSet.getString(1));
    }catch (Exception e){

    }
    return null;
  }

  /**
   * This method calculates the CPU usage of of the MCAProcess
   * @param mcaProcess - mcaProcess instance of a process.
   * @param uptime - System uptime
   * @param clk_ticks_per_second - System clock ticks per second
   * @return Returns the percentile CPU usage.
   */
  protected Double getCPUUsage(MCAProcess mcaProcess, Double uptime, long
          clk_ticks_per_second){
    double total_time = mcaProcess.getUtime() + mcaProcess.getStime() +
            mcaProcess.getCutime() + mcaProcess.getCstime();

    double seconds = uptime - (mcaProcess.getStartTime() / clk_ticks_per_second);

    double cpu_usage = 100 * ((total_time / clk_ticks_per_second) / seconds);

    return new Double(cpu_usage);
  }

  /**
   * This method deletes all the records of the given pid.
   * @param pid - pid of the process
   * @return - Return true if deletion was successful.
   */
  protected boolean deleteProcess(int pid){
    try{
      statement.executeUpdate("DELETE FROM PROCESS WHERE pid = "+pid);
      statement.executeUpdate("DELETE FROM METRICS WHERE pid = "+pid);
      return true;
    }catch (Exception e){
      e.printStackTrace();
    }
    return false;
  }
}
