package org.robert.tom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLAdapter {

  private static Connection connection = null;
  private static Statement statement = null;

  public SQLAdapter() {
    try {
      Class.forName("org.sqlite.JDBC");

      connection = DriverManager.getConnection
              ("jdbc:sqlite:ProcessMetrics.db");

      statement = connection.createStatement();

      String createProcess = "CREATE TABLE PROCESS(" +
              "pid INTEGER PRIMARY KEY," + "ppid INTEGER," +
              "p_name VARCHAR(25));";

      String createMetrics = "CREATE TABLE METRICS( \n" +
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

  public static void main(String arg[]) {
    SQLAdapter sqlAdapter = new SQLAdapter();


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
      String insertProcess = "INSERT INTO PROCESS VALUES(\n" +
              pid + ",\n" +
              ppid + ",\n" +
              p_name + "\n);" +
              "\n";

      String insertMetrics = "INSERT INTO METRICS VALUES(\n"+
              pid + ",\n" +
              "DEFAULT" + ",\n" +
              p_size + ",\n" +
              p_state + ",\n" +
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
}
