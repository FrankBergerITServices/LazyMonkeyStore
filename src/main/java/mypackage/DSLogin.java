package mypackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

/**
 * $Id: DSLogin.java 85 2005-11-13 15:54:30Z frank $
 * JavaBean for dslogin.jsp
 * 
 */
public class DSLogin extends DSBeanBase {
  private String username = "";
  private String password = "";
  private int cust_hist_rows = 0;
  private String[] titles = {};
  private String[] actors = {};
  private String[] relatedtitles = {};
  private int hist_counter = 0;
  private int customerid = 0;
  private boolean found = false;
  private CachedRowSet history = null;

  public void readinRequest(HttpServletRequest request) {
    username = processString(request.getParameter("username"), "username", true);
    password = processString(request.getParameter("password"), "password", true);
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }
  
  public boolean isValid() {
    int error_counter = 0;
    
    //  Check to see if all required fields are complete
    if(username.length() == 0) {error_counter++;}

    // If no errors, form is valid
    return (error_counter == 0);
  }
  
  public int doLogin() {
    try {
	  Class.forName("org.mariadb.jdbc.Driver");
    
  	  Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost/DS2?user=web&password=web"); 
	  String query = "select CUSTOMERID FROM DS2.CUSTOMERS where USERNAME='" + username + "' and PASSWORD='" + password + "';";
	  Statement userqueryStatement = conn.createStatement();
	  ResultSet userqueryResult = userqueryStatement.executeQuery(query);
	
	  if(userqueryResult.next()) { // if user exists, then print out previous purchases and recommendataions
        customerid = userqueryResult.getInt("CUSTOMERID");	  
	  }  
	  
	  userqueryResult.close();
	  userqueryStatement.close();
	  
      if(customerid > 0) {
    	query = "SELECT PRODUCTS.TITLE, PRODUCTS.ACTOR, P2.TITLE" +
                "  FROM DS2.CUST_HIST" +
    			"  INNER JOIN DS2.PRODUCTS ON CUST_HIST.PROD_ID = PRODUCTS.PROD_ID" +
                "  INNER JOIN DS2.PRODUCTS P2 ON PRODUCTS.COMMON_PROD_ID = P2.PROD_ID" +
                " WHERE (CUST_HIST.CUSTOMERID = " + customerid + ") ORDER BY ORDERID DESC LIMIT 10;";
    	
    	Statement prevpod_queryStatement = conn.createStatement();
        ResultSet prevprod_queryResult = prevpod_queryStatement.executeQuery(query);
        
        RowSetFactory factory = RowSetProvider.newFactory();
        history = factory.createCachedRowSet();
        history.populate(prevprod_queryResult);
        
        prevprod_queryResult.close();
        prevpod_queryStatement.close();
      }
      
      conn.close();
    }
    catch(Exception e) {
      System.out.println("error in DSLogin:");
      e.printStackTrace();
    }
    
    return customerid;
  }

  public boolean hasHistory() {
    return found;
  }
  
  public boolean hasNextHistoryElement() {
    return (cust_hist_rows-1 > hist_counter);
  }
  
  public void nextHistoryElement() {
    hist_counter++;
  }
  
  public String getCurrentHistoryTitle() {
    return titles[hist_counter];
  }
  
  public String getCurrentHistoryActor() {
    return actors[hist_counter];
  }
  
  public String getCurrentHistoryRelatedTitle() {
    return relatedtitles[hist_counter];
  }

  public int getCustomerid() {
    return customerid;
  }

  public CachedRowSet getHistory() {
    return history;
  }
}
