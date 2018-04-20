package org.robert.tom;

import com.sun.org.apache.regexp.internal.RE;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLAdapter {

  public static Connection connection = null;
  private static Statement statement = null;


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

  protected boolean saveProcessMetrics(int pid,
                                       int ppid,
                                       String p_name,
                                       long p_size,
                                       char p_state,
                                       int p_threads,
                                       long p_cstime,
                                       long p_cutime,
                                       long p_stime,
                                       long p_utime,
                                       long p_startTime,
                                       double p_cpu_utilization){
    try {
      String insertProcess = "INSERT OR REPLACE INTO PROCESS VALUES(\n" +
              pid + ",\n" +
              ppid + ",\n'" +
              p_name + "'\n);" +
              "\n";

      String insertMetrics = "INSERT INTO METRICS VALUES(\n"+
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

      return (statement.executeUpdate(insertProcess) > 0

              && statement.executeUpdate(insertMetrics) > 0);

    } catch (SQLException sql) {
      sql.printStackTrace();
    }
    return false;
  }

  public static void main(String[] args) {
    SQLAdapter sqlAdapter = new SQLAdapter();
    sqlAdapter.saveProcessMetrics(112,50,"firefox",
            1024,'S',2,53,643,1324,323,
            45,12.3);
    System.out.println(sqlAdapter.getLatestTime(112)  );
    System.out.println(sqlAdapter.getProcessLast5SecOlder(112).get(0).getCpuUsage());

  }



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
      e.printStackTrace();
    }
    return null;
  }

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
      e.printStackTrace();
    }
    return 0;
  }

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
    }catch (Exception e){
      e.printStackTrace();
    }
    return processes;
  }

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
      e.printStackTrace();
    }
    return null;
  }

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
