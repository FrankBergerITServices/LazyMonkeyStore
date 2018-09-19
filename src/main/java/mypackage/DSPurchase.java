package mypackage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

/**
 * $Id: DSPurchase.java 85 2005-11-13 15:54:30Z frank $
 * JavaBean for dspurchase.jsp
 * 
 */
public class DSPurchase extends DSBeanBase {
  public final static String[] cctypes = {"MasterCard", "Visa", "Discover",
                                          "Amex", "Dell Preferred"};

  private int customerid = 0;
  private String confirmpurchase = "";
  
  private int orderid = 0;
  private BigDecimal netamount;
  private BigDecimal taxamount;
  private BigDecimal totalamount;
  private int creditcardtype = 0;
  private String creditcard = "";
  private String creditcardexpiration = "";
  private String[] titles = {};
  private String[] actors = {};
  private BigDecimal[] prices = {};

  private int[] item = {};
  private int[] drop = {};
  private int[] quan = {};

  public void readinRequest(HttpServletRequest request) {
    customerid = processInt(request.getParameter("customerid"), "customerid", true);
    confirmpurchase = processString(request.getParameter("confirmpurchase"), "confirmpurchase", true);
    item = processIntArray(request.getParameterValues("item"), "item", false);
    drop = processIntArray(request.getParameterValues("drop"), "drop", false);
    quan = processIntArray(request.getParameterValues("quan"), "quan", false, item.length, 1);
  
    if(drop.length > 0) {
      int[] tmp_item = item;
      int[] tmp_quan = quan;
      item = new int[tmp_item.length - drop.length];    
      quan = new int[item.length];
      
      int j=0;
      for(int i=0; i < tmp_item.length; i++) {
        if(Arrays.binarySearch(drop, tmp_item[i]) < 0) {
          item[j] = tmp_item[i];
          quan[j] = (tmp_quan[i]>0?tmp_quan[i]:1);
          ++j;
        }
      }
    }
  }

  public int getCustomerid() {
    return customerid;
  }

  public String getConfirmpurchase() {
    return confirmpurchase;
  }

  public int[] getItem() {
    return item;
  }
  
  public String getItemStringList() {
	String itemList = null;
	for(int i = 0; i < item.length; i++){
      if(i == 0) {
    	itemList = String.valueOf(item[i]);  
      }
      else {
    	itemList = itemList + "," + String.valueOf(item[i]);	  
      }
	}
	
	System.out.println(itemList);
	return itemList;
  }    

  public int[] getDrop() {
    return drop;
  }

  public int[] getQuan() {
    return quan;
  }
  
  public boolean isValid() {
    return true;
  }
  
  public CachedRowSet getProducts() {
    CachedRowSet products = null;
    
    try {
      RowSetFactory factory = RowSetProvider.newFactory();	
      products = factory.createCachedRowSet();
    	
  	  Class.forName("org.mariadb.jdbc.Driver");
      Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost/DS2?user=web&password=web"); 
     
      PreparedStatement cstmt = conn.prepareStatement(
        "select PROD_ID, TITLE, ACTOR, PRICE from DS2.PRODUCTS where PROD_ID IN (" + getItemStringList() + ")");

      products.populate(cstmt.executeQuery());
      cstmt.close();
      conn.close();
    }
    catch(Exception e) {
      System.out.println("error in DSPurchase:");
      e.printStackTrace();
    }
    
    return products;
  }
  
  public void doPurchase() {
	titles = new String[item.length];
    actors = new String[item.length];
    prices = new BigDecimal[item.length]; 
    netamount = new BigDecimal(0);
	  
    try {
      Class.forName("org.mariadb.jdbc.Driver");
      Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost/DS2?user=web&password=web");
 
      PreparedStatement cstmt = conn.prepareStatement(
    	        "select PROD_ID, TITLE, ACTOR, PRICE from DS2.PRODUCTS where PROD_ID IN (" + getItemStringList() + ")");
      
      ResultSet purchasequeryResult = cstmt.executeQuery();
      
      int i = 0;
      while(purchasequeryResult.next()) {
    	titles[i] = purchasequeryResult.getString("TITLE");
    	actors[i] = purchasequeryResult.getString("ACTOR");
    	prices[i] = new BigDecimal(purchasequeryResult.getDouble("PRICE"));
    	
    	netamount = netamount.add(prices[i].multiply(new BigDecimal(quan[i])));
    	i++;
      }
      
      BigDecimal taxpct = new BigDecimal(8.25/100.0);  // double must be used for numberformat 
      taxamount = netamount.multiply(taxpct);
      totalamount = taxamount.add(netamount);
      
      conn.setAutoCommit(false);               // tell connection to not commit until instructed
      
      DateFormat ds2dateformat = new SimpleDateFormat("yyyy-MM-dd");    // set date format to match format in db
      String currentdate = ds2dateformat.format(new java.util.Date());  // get current date in right format
      
      NumberFormat totals = NumberFormat.getInstance();
      totals.setMaximumFractionDigits(2);
      totals.setMinimumFractionDigits(2);
      
      Statement purchaseupdateStatement = conn.createStatement();
      String purchase_insert_query = "INSERT into DS2.ORDERS (ORDERDATE, CUSTOMERID, NETAMOUNT, TAX, TOTALAMOUNT)" +
      " VALUES ( '" + currentdate + "'," + customerid + "," + totals.format(netamount) + "," + totals.format(taxamount) + "," + totals.format(totalamount) + ");";
      purchaseupdateStatement.executeUpdate(purchase_insert_query,Statement.RETURN_GENERATED_KEYS);
      ResultSet orderIDResult = purchaseupdateStatement.getGeneratedKeys();  // to get orderid that is autogenerated by db
      orderIDResult.next();
      orderid = orderIDResult.getInt(1);
      
      // To do: check $orderid and handle error if = 0

      // loop through purchased items and make inserts into orderdetails table     
      int h = 0;
      int j;
      String query;
      boolean success = true;
      String p_query = "INSERT into DS2.ORDERLINES (ORDERLINEID, ORDERID, PROD_ID, QUANTITY, ORDERDATE) VALUES"; 
      String c_insert= "INSERT INTO DS2.CUST_HIST (CUSTOMERID, ORDERID, PROD_ID) VALUES ";
      
      while (h < item.length) {
        j = h+1;
        query = "SELECT QUAN_IN_STOCK, SALES FROM DS2.INVENTORY WHERE PROD_ID=" + item[h] + ";";
 
        Statement quanquery = conn.createStatement();      // use quanconn instead of orderconn for simple quantity queries
        ResultSet quanResult = quanquery.executeQuery(query);
        quanResult.next();
        
        int curr_quan = quanResult.getInt("QUAN_IN_STOCK");
        int curr_sales = quanResult.getInt("SALES");
        int new_quan = curr_quan - quan[h];
        int new_sales = curr_sales + quan[h];
        
        if(new_quan < 0) {  // if insufficient stock on hand - then flag failure
          success = false;
        }
        else {  // if quantity does exist, update to new level   
          query = "UPDATE DS2.INVENTORY SET QUAN_IN_STOCK=" + new_quan + ", SALES=" + new_sales + " WHERE PROD_ID=" + item[h] + ";";
          purchaseupdateStatement.executeUpdate(query);
        }
                  
        p_query = p_query + "(" + j + "," + orderid + "," + item[h] + "," + quan[h] + ",'" + currentdate + "'),";
        if(h < 10) { 
          c_insert = c_insert + "( " + customerid + "," + orderid + "," + item[h] + "),"; 
        }
        
        h = h +1;
      } // End of while (!empty)
        
      p_query = p_query.substring(0,p_query.length()-1) + ";";
      c_insert = c_insert.substring(0,c_insert.length()-1) + " ;";

      purchaseupdateStatement.executeUpdate(p_query);  // Insert into orderlines
      purchaseupdateStatement.executeUpdate(c_insert);  // Update customers with recent purchases
        
      if(success == true) {  // if no errors were found, commit all 
        conn.commit();
      }
      else {                             
        conn.rollback(); // otherwise, rollback
      }        

      if(success == true) {
        // To Do: verify credit card purchase against a second database

        String cc_query = "select CREDITCARDTYPE, CREDITCARD, CREDITCARDEXPIRATION from DS2.CUSTOMERS where CUSTOMERID=" + customerid + ";";

        Statement ccqueryStatement = conn.createStatement();
        ResultSet ccqueryResult = ccqueryStatement.executeQuery(cc_query);
        ccqueryResult.next();
        creditcardtype = ccqueryResult.getInt("CREDITCARDTYPE");
        creditcard = ccqueryResult.getString("CREDITCARD");
        creditcardexpiration = ccqueryResult.getString("CREDITCARDEXPIRATION");
      }      
      
      conn.close();
    }
    catch(Exception e) {
      System.out.println("error in DSPurchase:");
      e.printStackTrace();
    }
  }

  public int getOrderid() {
    return orderid;
  }

  public double getNetamount() {
    return netamount.doubleValue();
  }

  public double getTaxamount() {
    return taxamount.doubleValue();
  }

  public double getTotalamount() {
    return totalamount.doubleValue();
  }

  public int getCreditcardtype() {
    return creditcardtype;
  }

  public String getCreditcard() {
    return creditcard;
  }

  public String getCreditcardexpiration() {
    return creditcardexpiration;
  }

  public String[] getTitles() {
    return titles;
  }

  public String[] getActors() {
    return actors;
  }

  public BigDecimal[] getPrices() {
    return prices;
  }
}
