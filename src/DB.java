package ora.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

import ora.metadata.table.EmployeeDeserializer;
//import ora.metadata.table.EmployeeMetadata;

public class DB {
    String userName;
    String userPassword;
    Connection conn;
    public DB () {}
    public void getConnection () {
       try {
          Class.forName ("oracle.jdbc.driver.OracleDriver");
          String jdbcUrl = "jdbc:oracle:thin:@192.168.1.75:1521/pdb2";
          if (userName == null || userPassword == null) {
             throw new Exception ("Username or password not set");
          }
          conn = DriverManager.getConnection (jdbcUrl, userName, userPassword);
          conn.setAutoCommit (false);
       } catch (Exception e) {
          e.printStackTrace ();
       }
    }
    public DB (String user, String passwd) {
       this.userName = user;
       this.userPassword = passwd;
    }
    public void setUser (String uName) {
       this.userName = uName;
    }
    public void setPassword (String uPasswd) {
       this.userPassword = uPasswd;
    }
    public void load (EmployeeDeserializer x) {
       //EmployeeDeserializer empData = (EmployeeDeserializer) x;
       int count = 0;
       try {
          //Statement stmt = conn.createStatement ();
          //String insertQuery =  "INSERT INTO ivrtst (empno, ename, start_date) VALUES (?, ?)";
          String insertQuery =  "INSERT INTO ivrtst (empno, ename, start_date)";
          int empNo = x.empno ();
          String eName = x.empName ();
          String startDate = x.joinDate ();
           insertQuery += " VALUES (" + empNo + ", '" + eName + "', ";// + startDate + ")";
           insertQuery += "to_date ('" + startDate + "', 'yyyy-mm-dd hh24:mi:ss'))";
          PreparedStatement stmt = conn.prepareStatement (insertQuery);
          //stmt.setInt (1, empData.empno ());
          //stmt.setString (2, empData.empName ());
          //stmt.setTimestamp (3, empData.joinDate ());
          System.out.println ("Insert Query : " + insertQuery);
          stmt.execute ();
          //count = stmt.executeUpdate (insertQuery);
          conn.commit ();
       } catch (Exception e) {
          e.printStackTrace ();
       }
       return ;
    }
    public void viewTableData (String tableName) {
       ResultSet rs = null;
       try {
          String selectQuery = "select * from " + tableName;
          Statement stmt = conn.createStatement ();
          rs = stmt.executeQuery (selectQuery);
          System.out.println ("--------------------------------------------------------------");
          System.out.println ("EmpNo " + "   Emp Name" + "           Start Date");
          System.out.println ("--------------------------------------------------------------");
          while (rs.next ()) {
             int empno = rs.getInt ("empno");
             String empName = rs.getString ("ename");
             String startDate = rs.getString ("start_date");
             System.out.println (empno + "    | " + empName + "          | " + startDate);
          }
       } catch (Exception e) {
          e.printStackTrace ();
       }
    }
}

