<!-- $Id: dslogin.jsp 85 2005-11-13 15:54:30Z frank $ -->
<%/*
dslogin.jsp JSP page that validates login to DVD store on Oracle database
 
modified by Frank Berger (www.fm-berger.de)

this code is based on:
  Copyright 2005 Dell
  Written by Todd Muirhead/Dave Jaffe      Last modified: 9/14/05 */%>

<%@page language="java" %>
<%@page session="false"%>

<%@page import="mypackage.DSLogin" %>
<%@page import="javax.sql.rowset.CachedRowSet" %>

<HTML>
<HEAD><TITLE>DVD Store Login Page</TITLE></HEAD>
<BODY style="font:Arial; color:blue;">
<H1 ALIGN="CENTER">DVD Store</H1>

<%
  DSLogin form = new DSLogin();
  form.readinRequest(request);

  int customerid = 0;

  // if username and password have been entered, check if valid
  if(form.isValid()) {
    customerid = form.doLogin();
    
    // if user exists, then print out previous purchases and recommendataions
    if(customerid != 0) { 
%>
      <H2>Welcome to the DVD Store - Click below to begin shopping</H2>
<%
      CachedRowSet history = form.getHistory();
      // if customer has previous purchases
      if(history.next()) { 
%>
        <H3>Your previous purchases:</H3>
        <TABLE border="2">
        <TR>
          <TH>Title</TH><TH>Actor</TH>
          <TH>People who liked this DVD also liked</TH>
        </TR>
<%
        do {
%>
          <TR>
            <TD> <%= history.getString(1)%> </TD>
            <TD> <%= history.getString(2)%> </TD>
            <TD> <%= history.getString(3)%> </TD>
          </tr>
<%
        } while(history.next());
        history.close();
%>
        </TABLE>
        <BR />
<%
      } // end if customer has previous purchases 
%>

      <FORM ACTION="dsbrowse.jsp" METHOD="GET">
        <INPUT TYPE="HIDDEN" NAME=customerid VALUE=<%= customerid %>>
        <INPUT TYPE="SUBMIT" VALUE="Start Shopping">
      </form>
<%
    }
    else {    // else, if the username password did not exsist
%>
      <H2>Username/password incorrect. Please re-enter your username and password</H2>
<%
    }  // end else
  } // end if username not null
  else {  // if no username, then must be first entry to site - give them the logon screen
%>
    <H2>Returning customer? Please enter your username and password</H2>
<%
  }

  if(!form.isValid() || (customerid == 0)) {
%>
    <FORM  ACTION="dslogin.jsp" METHOD="GET">
      Username <INPUT TYPE="TEXT" NAME="username" VALUE="<%= form.getUsername()%>" SIZE=16 MAXLENGTH=24>
      Password <INPUT TYPE=PASSWORD NAME="password" SIZE=16 MAXLENGTH=24>
      <INPUT TYPE="SUBMIT" VALUE="Login"> 
    </FORM>
    <H2>New customer? Please click New Customer</H2>
    <FORM  ACTION="dsnewcustomer.jsp" METHOD=GET >
    <INPUT TYPE="SUBMIT" VALUE="New Customer"> 
    </FORM>
<%
  }

%>
<HR />
<P ALIGN="CENTER">Thank You for Visiting the DVD Store!</P>
<HR />
<P ALIGN="CENTER">Copyright &copy; 2005 Dell / modified by Frank Berger (wwww.fm-berger.de)</P>
</BODY>
</HTML>