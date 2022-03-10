package ora.metadata.table; 

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.StringBuffer;
import java.lang.Exception;
import java.sql.Timestamp;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

//import java.util.regex.PatternSyntaxExpression;

import ora.util.DB;

public class EmployeeDeserializer {
   String strInput;
   String sqlStr;
   int empNo;
   String empName;
   //Timestamp joinDate;
   String joinDate;
   DB dbObject;
   public EmployeeDeserializer () {
      dbObject = new DB ();
      dbObject.setUser ("prajeeth2");
      dbObject.setPassword ("prajeeth2");
      dbObject.getConnection ();
      this.strInput = "{\"table\":\"TOPIC1.EMP9\",\"op_type\":\"I\",\"op_ts\":\"2020-11-09 11:13:26.000812\",\"current_ts\":\"2020-11-09T16:43:32.537000\",\"pos\":\"00000000130000007006\",\"after\":{\"EMPNO\":1073,\"ENAME\":\"emp73\",\"JOIN_DT\":\"2020-11-09 16:43:25\"}}"; 
   }
   public EmployeeDeserializer (String data) {
      this.strInput = data;
   }
   public void setInput (String input) {
      this.strInput = input;
      this.generateSQL ();
   }
   public int empno () {
     return this.empNo;
   }
   public String empName () {
      return this.empName;
   }
   //public Timestamp joinDate () {
   public String joinDate () {
      return this.joinDate;
   }

   public String getOpType () {
      String inputStr = this.strInput;
      String BEGIN_REGEX = "op_type";
      Pattern pattern = Pattern.compile (BEGIN_REGEX);
      String[] rawData = pattern.split (inputStr); 
      String opType = rawData[1].substring (3, 4); 
      return opType;
   }
   private void generateSQL () {
      if (!getOpType ().equals ("I")) {
         System.out.println (" ");
         System.out.println ("Warning : Only OPTYPE INSERT is processed");
         System.out.println (" ");
         return;
      }
      String inputStr1 = strInput.replaceAll ("\":", "|");
      String inputStr2 = inputStr1.replaceAll ("\"", "");
      String str2;
      String str3;
      int i;
      i = 1;
      String BEGIN_REGEX = "after\\|";
      String END_REGEX = "\\}\\}\\{";
      Pattern patternSplitAfter = Pattern.compile (BEGIN_REGEX);
      Pattern patternEnd = Pattern.compile (END_REGEX);
      String[] dbTransaction = patternSplitAfter.split (inputStr2);
      System.out.println ("Operations in transaction : " + dbTransaction.length);
      int arrIter = 1;
      String[] transactionOp;
      String x2;
      for (;arrIter < dbTransaction.length; arrIter++) {
          System.out.println ("Record " + arrIter + " => " + dbTransaction[arrIter]);
          transactionOp = patternEnd.split (dbTransaction[arrIter]);;
          String x = transactionOp[0].replaceAll ("\\{", "");
          x2 = x.replaceAll ("\\}", "");
          System.out.println ("Operation " + arrIter + " : " + x2);
          String[] keyAndValue = x2.split (",");
          String fieldValue;
          StringBuffer sb = new StringBuffer ();
          sb.append ("INSERT INTO ivrtst VALUES (");
          for (i = 0; i < keyAndValue.length; i++) {
          fieldValue = keyAndValue[i].split ("\\|")[1];
             System.out.println ("Field value in EmployeeDeserializer.generateSQL method " + fieldValue);
             switch (i) {
             case 0 :
                sb.append (fieldValue + ", ");
                this.empNo = Integer.parseInt (fieldValue);
                break;
             case 1 :
                sb.append ("'" + fieldValue + "', ");
                this.empName = fieldValue;
                break;
             case 2 :
                sb.append ("to_date ('" + fieldValue + "', 'yyyy-mm-dd hh24:mi:ss')");
                this.joinDate = fieldValue;
                break;
             }
          }
          //String insertStmt = sb.substring (0, sb.length ()) + ")";
          //this.sqlStr = insertStmt;
          //loadData (); 
      }
     
  }
  public void loadData () {
     System.out.println ("EmployeeDeserializer.loadData () " + this.sqlStr);
     //dbObject.load (this);
     //dbObject.viewTableData ("IVRTST");
  }
/*
  public static void main (String[] a) {
     EmployeeMetadata x = new EmployeeMetadata ();
     x.getOpType ();  
  }
*/
} 

