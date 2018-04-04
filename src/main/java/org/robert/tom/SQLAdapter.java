package org.robert.tom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class SQLAdapter {
  public static void main(String arg[]){
    try {
      Class.forName("org.sqlite.JDBC");

      Connection connection = DriverManager.getConnection
              ("jdbc:sqlite:Metrics.db");
      Statement statement = connection.createStatement();
      statement.executeUpdate("drop table if exists person");
      statement.executeUpdate("create table person (id integer, name string)");
      statement.executeUpdate("insert into person values(1, 'leo')");
      statement.executeUpdate("insert into person values(2, 'yui')");
    }
    catch (Exception e){

    }
  }
}
