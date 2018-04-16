package org.robert.tom;

import java.sql.*;
import java.util.HashMap;

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
              "p_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,\n" +
              "p_size INTEGER,\n" +
              "p_state CHARACTER(1) CHECK(" +
              "p_state == 'R' OR p_state == 'S' OR " + "p_state == 'D' OR " +
              "p_state == 'Z' OR p_state == 'T' OR " + "p_state == 't' OR " +
              "p_state == 'W' OR p_state == 'X' OR " + "p_state == 'x' OR " +
              "p_state == 'K' OR p_state == 'P'), \n" +
              "p_threads INTEGER,\n" +
              "p_bytes_sent INTEGER,\n" +
              "p_bytes_received INTEGER,\n" +
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
                                       long p_bytes_sent,
                                       long p_bytes_received,
                                       double p_cpu_utilization){
    try {
      String insertProcess = "INSERT OR REPLACE INTO PROCESS VALUES(\n" +
              pid + ",\n" +
              ppid + ",\n'" +
              p_name + "'\n);" +
              "\n";

      String insertMetrics = "INSERT INTO METRICS VALUES(\n"+
              pid + ",\n" +
              "CURRENT_TIMESTAMP" + ",\n" +
              p_size + ",\n'" +
              p_state + "',\n" +
              p_threads + ",\n" +
              p_bytes_sent + ",\n" +
              p_bytes_received + ",\n" +
              p_cpu_utilization + "\n);" +
              "\n";

      return (statement.executeUpdate(insertProcess) > 0

              && statement.executeUpdate(insertMetrics) > 0);

    } catch (SQLException sql) {
      sql.printStackTrace();
    }
    return false;
  }

  protected boolean getProcessMetrics(int pid){

    HashMap<String,String> process = null;
    String getMetrics = "SELECT *FROM METRICS WHERE pid = "+pid;

    try {
      ResultSet result = statement.executeQuery(getMetrics);

    }catch (Exception e){
      e.printStackTrace();
    }

    return false;
  }
  protected boolean deleteMetrics(int pid){
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
