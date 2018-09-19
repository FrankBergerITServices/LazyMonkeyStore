<!-- $Id: dsnewcustomer.jsp 76 2005-11-12 14:30:49Z frank $ -->
<%/*JSP page that creates new user entry in 
  ds2 database running on Oracle

  modified by Frank Berger (www.fm-berger.de)

  this code is based on:
  Copyright 2005 Dell
  Written by Todd Muirhead/Dave Jaffe      Last modified: 9/14/05*/%>

<%@page language="java" %>
<%@page session="false"%>
<%@page contentType="text/html;charset=ISO-8859-1"%>
<%@page import="mypackage.DSCustomer"%>

<HTML>
<HEAD>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1"/>
  <TITLE>DVD Store New Customer Login</TITLE>
</HEAD>
<BODY style="font:Arial; color:blue;">
<H1 ALIGN=CENTER>DVD Store</H1>

<%
  DSCustomer form = new DSCustomer();
  form.readinRequest(request);
  int customerid = -1;
  
  //  Check to see if all required fields are complete, 
  //  if they are complete then try to add new user 
  if(form.isValid()) {
    customerid = form.createCustomer();
  }
  
  // if username did exist in database already, print form again for retry
  if(customerid == 0) { 
%>
    <H2>Username already in use! Please try another username</H2>
<%
  }
  
  // this is executed if all of the required fields were not created
  if(customerid == -1) { 
%>
  <H2>New Customer - Please Complete All Required Fields Below (marked with *)</H2>
<%
  }

  if((customerid == -1) || (customerid == 0)) {
%>
<FORM ACTION="dsnewcustomer.jsp" METHOD="GET">
  Firstname *
  <INPUT TYPE=TEXT NAME='firstname' VALUE='<%= form.getFirstname()%>' 
         SIZE=16 MAXLENGTH=50>
  <span style="color:red;"><%= form.getError("firstname")%></span><BR />
         
  Lastname *
  <INPUT TYPE=TEXT NAME='lastname' VALUE='<%= form.getLastname()%>' 
         SIZE=16 MAXLENGTH=50> 
  <span style="color:red;"><%= form.getError("lastname")%></span><BR />
         
  Address1 *
  <INPUT TYPE=TEXT NAME='address1' VALUE='<%= form.getAddress1()%>' 
         SIZE=16 MAXLENGTH=50> 
  <span style="color:red;"><%= form.getError("address1")%></span><BR />
         
  Address2 
  <INPUT TYPE=TEXT NAME='address2' VALUE='<%= form.getAddress2()%>' 
         SIZE=16 MAXLENGTH=50>
  <span style="color:red;"><%= form.getError("address2")%></span><BR />
         
  City *
  <INPUT TYPE=TEXT NAME='city' VALUE='<%= form.getCity()%>' 
         SIZE=16 MAXLENGTH=50>
  <span style="color:red;"><%= form.getError("city")%></span><BR />
         
  State 
  <INPUT TYPE=TEXT NAME='state' VALUE='<%= form.getState()%>' 
         SIZE=16 MAXLENGTH=50> 
  <span style="color:red;"><%= form.getError("state")%></span><BR />         
         
  Zipcode
  <INPUT TYPE=TEXT NAME='zip' VALUE='<%= form.getZip()%>' 
         SIZE=16 MAXLENGTH=5> 
  <span style="color:red;"><%= form.getError("zip")%></span><BR />

  Country *
  <select NAME="country" SIZE="1">
<%
  int countries_length = form.countries.length;
  for(int i=0; i<countries_length; i++) {       // loop through countries for dropdown creation
   %>
    <option VALUE="<%= form.countries[i]%>" 
      <% if(form.equalsCountry(i)){%> SELECTED <%}%>
    >
      <%= form.countries[i]%>
    </OPTION>
   <%
  }
%>
  </SELECT>
  <span style="color:red;"><%= form.getError("country")%></span><BR />
  
  Email
  <INPUT TYPE=TEXT NAME='email' VALUE='<%= form.getEmail()%>' 
         SIZE=16 MAXLENGTH=50>
  <span style="color:red;"><%= form.getError("email")%></span><BR />  
         
  Phone 
  <INPUT TYPE=TEXT NAME='phone' VALUE='<%= form.getPhone()%>' 
         SIZE=16 MAXLENGTH=50>
  <span style="color:red;"><%= form.getError("phone")%></span><BR />  
  
  Credit Card Type 
  <SELECT NAME='creditcardtype' SIZE=1>
<%
  int cctypes_length = form.cctypes.length;
  int cctype = form.getCreditcardtype();
  for(int i=1; i < cctypes_length; i++) { // loop through creditcardtypes for dropdown creation
   %>  
    <OPTION VALUE="<%= i%>" <% if(i == cctype){%> SELECTED <%}%>>
      <%= form.cctypes[i]%>
    </OPTION>
   <% 
  }
%>
  </SELECT>
  <span style="color:red;"><%= form.getError("creditcardtype")%></span><BR />

  Credit Card Number
  <INPUT TYPE=TEXT NAME='creditcard' VALUE='<%= form.getCreditcard()%>' 
         SIZE=16 MAXLENGTH=50>
  <span style="color:red;"><%= form.getError("creditcard")%></span><BR />         

  Credit Card Expiration 
  <SELECT NAME='ccexpmon' SIZE=1>
<%
  int months_length = form.months.length;
  int month = form.getCcexpmon();
  for(int i=1; i<months_length; i++) { // loop to create credit card expiration month dropdown
   %>
    <OPTION VALUE="<%= i%>" <% if(i == month) {%> SELECTED<%}%>>
      <%= form.months[i]%>
    </OPTION>
   <% 
  }
%>  
  </SELECT>
  
  <SELECT NAME='ccexpyr' SIZE=1>
<%
  int yr = form.getCcexpyr();
  for(int i=2005; i<2013; i++) { // loop to create credit card expiration year dropdown
   %>
     <OPTION VALUE="<%= i%>" <% if(i == yr){%> SELECTED <%}%>>
       <%= i%>
     </OPTION>
   <%     
  }
%>
  </SELECT>
    <span style="color:red;"><%= form.getError("ccexpmon")%></span>
    <span style="color:red;"><%= form.getError("ccexpyr")%></span><BR />

  Username *
  <INPUT TYPE=TEXT NAME='username' VALUE='<%= form.getUsername()%>' 
         SIZE=16 MAXLENGTH=50>
  <span style="color:red;"><%= form.getError("username")%></span><BR />
         
  Password * 
  <INPUT TYPE='PASSWORD' NAME='password' VALUE='<%= form.getPassword()%>' 
         SIZE=16 MAXLENGTH=50>
  <span style="color:red;"><%= form.getError("password")%></span><BR />
         
  Age 
  <INPUT TYPE=TEXT NAME='age' VALUE='<%= form.getAge()%>'
         SIZE=3 MAXLENGTH=3> 
  <span style="color:red;"><%= form.getError("age")%></span><BR />         
         
  Income (\$US)
  <INPUT TYPE=TEXT NAME='income' VALUE='<%= form.getIncome()%>' 
         SIZE=16 MAXLENGTH=50>
  <span style="color:red;"><%= form.getError("income")%></span><BR />         
         
  Gender
  <INPUT TYPE=RADIO NAME='gender' VALUE="M"
         <% if(form.equalsGender("M")) { %>CHECKED<% } %>> Male
  <INPUT TYPE=RADIO NAME='gender' VALUE="F" 
         <% if(form.equalsGender("F")) { %>CHECKED<% } %>> Female 
  <INPUT TYPE=RADIO NAME='gender' VALUE="?" 
         <% if(form.equalsGender("?")) { %>CHECKED<% } %>> Don't Know 
  <span style="color:red;"><%= form.getError("gender")%></span><BR />         


  <INPUT TYPE='submit' VALUE='Submit New Customer Data'>
</FORM>
<%  
  }
  
  // executed if the username requested did not exist - insert the new user
  if(customerid > 0) { 
%> 
    <H2>New Customer Successfully Added.  Click below to begin shopping</h2>
    <FORM ACTION="dsbrowse.jsp" METHOD="GET">
      <INPUT TYPE=HIDDEN NAME=customerid VALUE=<%= customerid%>>  
      <INPUT TYPE=SUBMIT VALUE='Start Shopping'>
    </FORM>
<%
  }
%>

<HR />
<P ALIGN="CENTER">Thank You for Visiting the DVD Store!</P>
<HR />
<P ALIGN="CENTER">
  Copyright &copy; 2005 Dell / modified by Frank Berger (wwww.fm-berger.de)
</P>
</BODY>
</HTML>
