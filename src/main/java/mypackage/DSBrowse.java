package mypackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;


/**
 * $Id: DSBrowse.java 77 2005-11-12 15:01:10Z frank $
 * JavaBean for dsbrowse.jsp
 * 
 */
public class DSBrowse extends DSBeanBase {
  public final static String[] categories = {"dummy", "Action", "Animation", "Children",
                                             "Classics", "Comedy", "Documentary",
                                             "Drama", "Family", "Foreign", "Games",
                                             "Horror", "Music", "New", "Sci-Fi",
                                             "Sports", "Travel"};

  private String customerid = "";
  private String browsetype = "";  
  private String browse_title = "";  
  private String browse_actor = "";
  private int limit_num = 0;
  private int browse_category = 0;
  private int[] item = {};
  private int[] selected_item = {};

  public void readinRequest(HttpServletRequest request) {
    customerid = processString(request.getParameter("customerid"), "customerid", true);
    browsetype = processString(request.getParameter("browsetype"), "browsetype", false);  
    browse_title = processString(request.getParameter("browse_title"), "browse_title", false);  
    browse_actor = processString(request.getParameter("browse_actor"), "browse_actor", false);
    limit_num = processInt(request.getParameter("limit_num"), "limit_num", false);
    browse_category = processInt(request.getParameter("browse_category"), "browse_category", false);
    item = processIntArray(request.getParameterValues("item"), "item", false);
    selected_item = processIntArray(request.getParameterValues("selected_item"), "selected_item", false);
    
    if(selected_item.length > 0) {
      int[] tmp = item;
      item = new int[tmp.length + selected_item.length];
      if(tmp.length > 0) System.arraycopy(tmp, 0, item, 0, tmp.length);      
      System.arraycopy(selected_item, 0, item, tmp.length, selected_item.length);
    }    
  }

  public String getCustomerid() {
    return customerid;
  }

  public String getBrowsetype() {
    return browsetype;
  }

  public String getBrowse_title() {
    return browse_title;
  }

  public String getBrowse_actor() {
    return browse_actor;
  }

  public int getLimit_num() {
    return limit_num;
  }

  public int getBrowse_category() {
    return browse_category;
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

  public int[] getSelected_item() {
    return selected_item;
  }
  
  public boolean isValid() {
    int error_counter = 0;
    
    //  Check to see if all required fields are complete
    if(customerid.length() == 0) {error_counter++;}

    // If no errors, form is valid
    return (error_counter == 0);
  }

  public boolean equalsBrowsetype(String in) {
    return browsetype.equals(in);
  }

  public int getNew_item_length() {
    return item.length + selected_item.length;
  }
  
  public int getItem_length() {
    return item.length;
  }
  
  public CachedRowSet doBrowse() {
    CachedRowSet products = null;
    
    try {
      RowSetFactory factory = RowSetProvider.newFactory();
      products = factory.createCachedRowSet();
      
      Class.forName("org.mariadb.jdbc.Driver");
      Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost/DS2?user=web&password=web"); 

      PreparedStatement browsestmt = null;
      
      switch(browsetype.charAt(0)) { // switch on browsetype that was selected
        case 't':
          browsestmt = conn.prepareStatement(
            "select * from PRODUCTS where MATCH (TITLE) AGAINST (?) LIMIT ?");
          browsestmt.setString(1, browse_title);
          break;
        case 'a':
          browsestmt = conn.prepareStatement(
            "select * from PRODUCTS where MATCH (ACTOR) AGAINST (?) LIMIT ?");
          browsestmt.setString(1, browse_actor);
          break;
        case 'c':
          browsestmt = conn.prepareStatement(
            "select * from PRODUCTS where CATEGORY = ? and SPECIAL=1 LIMIT ?");
          browsestmt.setInt(1, browse_category);
          break;
        default:  //yea, yea I know thats dirty, but do you know something better?
          browsestmt = conn.prepareStatement(
            "select * from PRODUCTS where MATCH (TITLE) AGAINST (?) LIMIT ?");
          browsestmt.setString(1, "foobar");
      }

      browsestmt.setInt(2, limit_num);

      products.populate(browsestmt.executeQuery());
      
      browsestmt.close();
      conn.close();
    } 
    catch(Exception e) {
      System.out.println("error in DSBrowse:");
      e.printStackTrace();
    }
    
    return products;
  }
  
  public CachedRowSet getProductTitles() {
	  
    CachedRowSet titles = null;
    
    try {
      RowSetFactory factory = RowSetProvider.newFactory();	
      titles = factory.createCachedRowSet();
      
  	  Class.forName("org.mariadb.jdbc.Driver");
      Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost/DS2?user=web&password=web"); 
    
      PreparedStatement cstmt = conn.prepareStatement("SELECT prod_id, TITLE FROM PRODUCTS WHERE PROD_ID IN (" + getItemStringList() + ")");


      //cstmt.setString(1, );
      titles.populate(cstmt.executeQuery());
      cstmt.close();
      conn.close();
    } 
    catch(Exception e) {
      System.out.println("error in DSBrowse:");
      e.printStackTrace();
    }
    
    return titles;
  }
  
}
