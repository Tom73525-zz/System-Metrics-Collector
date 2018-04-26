package org.robert.tom;

import java.sql.*;
import java.util.ArrayList;

public class SQLAdapter {


  /**
   * The SQLiteAdapter constructor establishes a connection to an existing
   * database through JDBC. If database is not found, a new database is
   * created.
   */
  public SQLAdapter() {
    try {

      Connection connection = openDbConnection();

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

      PreparedStatement preparedStatement1 = connection
              .prepareStatement(createProcess);
      preparedStatement1.execute();

      PreparedStatement preparedStatement2 = connection
              .prepareStatement(createMetrics);
      preparedStatement2.execute();

      preparedStatement1.close();
      preparedStatement2.close();
    } catch (Exception e) {
    }
  }

  /**
   * Opens a database connection
   * NOTE: Create a global Connection variable and initialize the object in the
   * constructor by calling this method. Connection has to be initialized
   * only once.
   * @return - Returns a Connection object instance.
   * @throws Exception - Throws ConnectionException
   */
  public Connection openDbConnection()throws Exception{

    Class.forName("org.sqlite.JDBC");

    Connection connection = DriverManager
            .getConnection("jdbc:sqlite:ProcessMetrics.db");

    return connection;
  }

  /**
   * Closes a database connection
   * @param connection
   */
  public void closeDbConnection(Connection connection){

    try {
      connection.close();
    }catch (Exception e){

    }
  }

  /**
   * This method takes in an arraylist of MCAProcess, extracting the metrics and
   * calculate the cpu_usage. The the values are then inserted into the
   * database.
   * @param mcaProcesses - ArrayList of MCAProcess
   * @param connection - Connection object
   * @return - Returns true if all values are stored into the database.
   */
  protected boolean saveProcessMetrics(ArrayList<MCAProcess> mcaProcesses,
                                       Connection connection){

    int count = 0;
    MetricCollector metricCollector = new MetricCollector();

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
      double p_cpu_utilization = getCPUUsage(mcaProcess,
              240000.0,100);

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

        PreparedStatement preparedStatement1 = connection
                .prepareStatement(insertProcess);
        PreparedStatement preparedStatement2 = connection
                .prepareStatement(insertMetrics);

        if(preparedStatement1.executeUpdate() > 0
                && preparedStatement2.executeUpdate() > 0 ){
          count++;
        }
        preparedStatement1.close();
        preparedStatement2.close();

      } catch (Exception sql) {
        sql.printStackTrace();
      }
    }
    if(count == mcaProcesses.size()){
      return true;
    }
    return false;
  }

  /**
   * Gets the name of the process
   * @param pid - pid of the process
   * @param connection - Connection object
   * @return - Returns name of the process, null if no such pid is found.
   */
  protected String getProcessName(int pid, Connection connection){

    try {

      String getPname = "SELECT p_name " +
              "FROM PROCESS " +
              "WHERE pid = " + pid + ";";

      PreparedStatement preparedStatement = connection
              .prepareStatement(getPname);

      String pName = preparedStatement.executeQuery().getString(1);

      preparedStatement.close();
      return pName;
    }catch (Exception e){
    }
    return null;
  }

  /**
   * Gets the ppid of the process
   * @param pid  - pid of the process
   * @param connection - Connection object
   * @return - Returns ppid of the process, returns -1 if no such pid is found.
   */
  protected int getParentPid(int pid, Connection connection){
    try {

      String getPpid = "SELECT ppid " +
              "FROM PROCESS " +
              "WHERE pid = " + pid + ";";

      PreparedStatement preparedStatement = connection
              .prepareStatement(getPpid);

      int ppid = preparedStatement.executeQuery().getInt(1);
      preparedStatement.close();

      return ppid;
    }catch (Exception e){
    }
    return -1;
  }

  /**
   * This method gets all MCAProcess from the database which fall under the
   * last 5 second window.
   * @param connection - Connection object
   * @return - Returns an arraylist of MCAProcess containing MCAProcess records
   * of the given pid.
   */
  protected ArrayList<MCAProcess> getProcessLast5SecOlder(Connection connection){
    ArrayList<MCAProcess> mcaProcesses = new ArrayList<MCAProcess>();
    try {

      String getLast5SecOld = "SELECT * " +
              "FROM METRICS " +
              "WHERE p_timestamp >= datetime('now', '-4 hours','-5 seconds');";

      PreparedStatement preparedStatement = connection
              .prepareStatement(getLast5SecOld);

      ResultSet resultSet = preparedStatement.executeQuery();
      while(resultSet.next()){
        MCAProcess mcaProcess = new MCAProcess();
        int pid = resultSet.getInt(1);
        int ppid = getParentPid(pid,connection);
        String p_name = getProcessName(pid,connection);
        mcaProcess.setPid(pid);
        mcaProcess.setPpid(ppid);
        mcaProcess.setName(p_name);
        mcaProcess.setTimeStamp(Timestamp.valueOf(resultSet
                .getString(2)));
        mcaProcess.setVmSize(resultSet.getLong(3));
        mcaProcess.setState((resultSet.getString(4).charAt(0)));
        mcaProcess.setNumThreads(resultSet.getInt(5));
        mcaProcess.setCstime(resultSet.getLong(6));
        mcaProcess.setCutime(resultSet.getLong(7));
        mcaProcess.setStime(resultSet.getLong(8));
        mcaProcess.setUtime(resultSet.getLong(9));
        mcaProcess.setStartTime(resultSet.getLong(10));
        mcaProcess.setCpuUsage(resultSet.getDouble(11));
        mcaProcesses.add(mcaProcess);
      }
      preparedStatement.close();
      resultSet.close();
    }catch (Exception e){
      e.printStackTrace();
    }
    return mcaProcesses;
  }

  /**
   * Gets latest timestamp of the MCAProcess that fall under the last 5 second
   * window
   * @param pid - pid of the process
   * @param connection - Connection object
   * @return - Returns the latest timestamp of pid, null if no such pid is
   * found.
   */
  protected Timestamp getLatestTime(int pid, Connection connection){

    try {

      String latestTime = "SELECT p_timestamp " +
              "FROM METRICS " +
              "WHERE p_timestamp >= datetime('now', '-4 hours','-5 seconds') " +
              "AND pid == " + pid + " " +
              "ORDER BY p_timestamp DESC;";

      PreparedStatement preparedStatement = connection
              .prepareStatement(latestTime);

      String timestamp = preparedStatement.executeQuery()
              .getString(1);

      preparedStatement.close();
      return Timestamp.valueOf(timestamp);

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
  public Double getCPUUsage(MCAProcess mcaProcess, Double uptime,
                            long clk_ticks_per_second){

    double total_time = mcaProcess.getUtime() + mcaProcess.getStime() +
            mcaProcess.getCutime() + mcaProcess.getCstime();

    double seconds = uptime - (mcaProcess.getStartTime() /
            clk_ticks_per_second);

    double cpu_usage = 100 * ((total_time / clk_ticks_per_second) / seconds);

    return new Double(cpu_usage);
  }

  /**
   * Gets ArrayList of MCAProcesses having the MAX p_starttime
   * @param pid pid of the process
   * @param connection - Connection object.
   * @return ArrayList of MCAProcesses.
   */
  protected ArrayList<MCAProcess> getMaxStartTime(int pid, Connection connection){
    ArrayList<MCAProcess> mcaProcesses = new ArrayList<MCAProcess>();


    String getMetrics = "SELECT DISTINCT * " +
            "FROM METRICS " +
            "WHERE p_starttime = " +
            "(SELECT MAX(p_starttime) " +
            "FROM METRICS " +
            "WHERE pid = " + pid + ");";
    try {

      PreparedStatement preparedStatement = connection
              .prepareStatement(getMetrics);

      ResultSet resultSet = preparedStatement.executeQuery();
      while(resultSet.next()){
        MCAProcess mcaProcess = new MCAProcess();
        int ppid = getParentPid(pid, connection);
        String p_name = getProcessName(pid, connection);
        mcaProcess.setPid(pid);
        mcaProcess.setPpid(ppid);
        mcaProcess.setName(p_name);
        mcaProcess.setTimeStamp(Timestamp.valueOf(resultSet
                .getString(2)));
        mcaProcess.setVmSize(resultSet.getLong(3));
        mcaProcess.setState((resultSet.getString(4).charAt(0)));
        mcaProcess.setNumThreads(resultSet.getInt(5));
        mcaProcess.setCstime(resultSet.getLong(6));
        mcaProcess.setCutime(resultSet.getLong(7));
        mcaProcess.setStime(resultSet.getLong(8));
        mcaProcess.setUtime(resultSet.getLong(9));
        mcaProcess.setStartTime(resultSet.getLong(10));
        mcaProcess.setCpuUsage(resultSet.getDouble(11));
        mcaProcesses.add(mcaProcess);
      }
      preparedStatement.close();
      resultSet.close();

    }catch(Exception e){

    }
    return mcaProcesses;
  }

  /**
   * This method deletes all the records of the given pid.
   * @param pid - pid of the process
   * @param connection - Connection object
   * @return - Return true if deletion was successful.
   */
  protected boolean deleteProcess(int pid, Connection connection){
    try{

      PreparedStatement preparedStatement1 = connection
              .prepareStatement("DELETE FROM PROCESS WHERE pid = "+pid);

      PreparedStatement preparedStatement2 = connection
              .prepareStatement("DELETE FROM METRICS WHERE pid = "+pid);

      preparedStatement1.executeUpdate();
      preparedStatement2.executeUpdate();

      preparedStatement1.close();
      preparedStatement2.close();
      return true;
    }catch (Exception e){
      e.printStackTrace();
    }
    return false;
  }
}
