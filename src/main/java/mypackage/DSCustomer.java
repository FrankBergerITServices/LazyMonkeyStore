package mypackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.http.HttpServletRequest;

/**
 * $Id: DSCustomer.java 85 2005-11-13 15:54:30Z frank $
 * JavaBean for dsnewcustomer.jsp
 * 
 */
public class DSCustomer extends DSBeanBase {
  public static final String[] countries = {"US", "Australia", "Canada", "Chile", "China", "France", "Germany", "Japan", "Russia", "South Africa", "UK"};
  public static final String[] cctypes = {"dummy", "MasterCard", "Visa", "Discover", "Amex", "Dell Preferred"};
  public static final String[] months = {"dummy", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  
  private String firstname = "";
  private String lastname = "";
  private String address1 = "";
  private String address2 = "";
  private String city = "";
  private String state = "";
  private String country = "";
  private String gender = "";
  private String username = "";
  private String password = "";
  private String email = "";
  private String phone = "";
  private String creditcard = "";
  private int ccexpyr = 2005;
  private int creditcardtype = 1;
  private int ccexpmon = 1;
  private int zip = 0;
  private int age = 0;
  private int income = 0;
  private int region = 1;
    
  public void readinRequest(HttpServletRequest request) {
  
    //check if we should print out error messages, when required fields are missing
    String qrystr = request.getQueryString();
    boolean check_required = !(qrystr==null || "".equals(qrystr));
    if(!check_required) error_counter++;
    
    firstname = processString(request.getParameter("firstname"), "firstname", check_required);
    lastname = processString(request.getParameter("lastname"), "lastname", check_required);
    address1 = processString(request.getParameter("address1"), "address1", check_required);
    address2 = processString(request.getParameter("address2"), "address2", false);
    city = processString(request.getParameter("city"), "city", check_required);
    state = processString(request.getParameter("state"), "state", false);
    setCountry(processString(request.getParameter("country"), "country", check_required));
    gender = processString(request.getParameter("gender"), "gender", false);
    username = processString(request.getParameter("username"), "username", check_required);
    password = processString(request.getParameter("password"), "password", check_required);
    email = processString(request.getParameter("email"), "email", false);
    phone = processString(request.getParameter("phone"), "phone", false);
    creditcard = processString(request.getParameter("creditcard"), "creditcard", false);
   
    zip = processInt(request.getParameter("zip"), "zip", false);
    creditcardtype = processInt(request.getParameter("creditcardtype"), "creditcardtype" , false);
    ccexpmon = processInt(request.getParameter("ccexpmon"), "ccexpmon", false); 
    ccexpyr = processInt(request.getParameter("ccexpyr"), "ccexpyr", false); 
    age = processInt(request.getParameter("age"), "age", false); 
    income = processInt(request.getParameter("income"), "income", false); 
  }
  
  public String getIncome() {
    return income==0?"":String.valueOf(income);
  }

  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public String getAddress1() {
    return address1;
  }

  public String getAddress2() {
    return address2;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public void setCountry(String country) {
    this.country = country;
    if(!country.equals("US")) { region = 2; }
  }

  public String getCountry() {
    return country;
  }

  public String getGender() {
    return gender;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public String getCreditcard() {
    return creditcard;
  }

  public int getCcexpyr() {
    return ccexpyr;
  }

  public int getCreditcardtype() {
    return creditcardtype;
  }

  public int getCcexpmon() {
    return ccexpmon;
  }

  public String getZip() {
    return zip==0?"":String.valueOf(zip);
  }

  public String getAge() {
    return age==0?"":String.valueOf(age);
  }
  
  public int getRegion() {
    return region;
  }
  
  public String getCCExp() {
    return String.format("%1$04d/%2$02d", ccexpyr, ccexpmon);
  }
  
  public boolean equalsGender(String in) {
    return this.gender.equals(in);
  }
  
  public boolean equalsCountry(int i) {
    return countries[i].equals(country);
  }
  
  public boolean isValid() {
    // If no errors, form is valid
    return (error_counter == 0);
  }
  
  public int createCustomer() {
    int customerid = 0;
    try {
      Class.forName("org.mariadb.jdbc.Driver");
      Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost/DS2?user=web&password=web");

      String query = "select COUNT(*) from DS2.CUSTOMERS where USERNAME='" + username + "';";
      Statement userqueryStatement = conn.createStatement();  // run query to check to see if username already exists
      ResultSet userqueryResult = userqueryStatement.executeQuery(query);
      userqueryResult.next();
      int check_username = userqueryResult.getInt("count(*)");    // get result from db into an int
      
      if(check_username == 0) { // if username did not exist in database already
    	String insert_newuser_query = "INSERT INTO DS2.CUSTOMERS (FIRSTNAME, LASTNAME, ADDRESS1, ADDRESS2, " + 
    	    	      "CITY, STATE, ZIP, COUNTRY, REGION, EMAIL, PHONE, CREDITCARDTYPE, CREDITCARD, CREDITCARDEXPIRATION," +
    	    	      " USERNAME, PASSWORD, AGE, INCOME, GENDER) " +
    	    	      " VALUES ('" + firstname + "','" + lastname + " ','" + address1 + " ','" + address2 + "','" + city + "','" + state + "','" + zip + "','" + country + "','" + 
    	    	      region + "','" + email + "','" + phone + "','" + creditcardtype + "','" + creditcard + "','" + getCCExp() + "','" + 
    	    	      username + "','" + password + "','" + age + "','" + income + "','" + gender + "');";
    	
    	Statement userInsertStatement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        userInsertStatement.executeUpdate(insert_newuser_query,Statement.RETURN_GENERATED_KEYS);
              // the RETURN_GENERATED_KEYS option on the executeUpdate is needed for the autoincrement
              // customerid colum to be returned.  This value is then forwarded to each subsquent page
        ResultSet userInsertResult = userInsertStatement.getGeneratedKeys();
              //  Get the auto generated key into a result set
        userInsertResult.next();
        customerid = userInsertResult.getInt(1);   // get autocreated customerid into string
      }

      conn.close();
    }
    catch(Exception e) {
      System.out.println("Error creating user:");
      e.printStackTrace();
    }
    
    return customerid;
  }
}
