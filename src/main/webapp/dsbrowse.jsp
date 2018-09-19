<!-- $Id: dsbrowse.jsp 85 2005-11-13 15:54:30Z frank $ -->
<%/*
dsbrowse.jsp jsp page that browses DVD store by author, title, or category
on oracle database. It also collects selected items into a list and allows
customer to purchase them

modified by Frank Berger (www.fm-berger.de)

this code is based on:
  Copyright 2005 Dell
  Written by Todd Muirhead/Dave Jaffe      Last modified: 9/14/05 */%>

<%@page language="java" %>
<%@page session="false"%>
<%@page import="javax.sql.rowset.CachedRowSet"%>
<%@page import="mypackage.DSBrowse"%>

<HTML>
<HEAD><TITLE>DVD Store Browse Page</TITLE></HEAD>
<BODY style="font:Arial; color:blue;">
<H1 ALIGN="CENTER">DVD Store</H1>

<%
  DSBrowse form = new DSBrowse();
  form.readinRequest(request);

  if(!form.isValid()) {   // check to see if the user has logged in
%>
  <H2>You have not logged in - Please click below to Login to DVD Store</H2>
  <FORM ACTION="dslogin.jsp" METHOD="GET">
  <INPUT TYPE="SUBMIT" VALUE="Login">
  </FORM>
<%
  }  // end of if customerid is blank
  else {
%>
  <H2>Select Type of Search</H2>

  <FORM ACTION="dsbrowse.jsp" METHOD="GET">

  <INPUT NAME="browsetype" TYPE="RADIO" VALUE="title" 
    <% if(form.equalsBrowsetype("title")) { %> CHECKED <% } %>>
  Title  <INPUT NAME='browse_title' VALUE='<%= form.getBrowse_title()%>' TYPE=TEXT SIZE=15> <BR />
  
  <INPUT NAME='browsetype' TYPE='RADIO' VALUE='actor'
    <% if(form.equalsBrowsetype("actor")) { %> CHECKED <% } %>> 
  Actor  <INPUT NAME='browse_actor' VALUE='<%= form.getBrowse_actor()%>' TYPE=TEXT SIZE=15> <BR />
  
  <INPUT NAME='browsetype' TYPE='RADIO' VALUE='category'
    <% if(form.equalsBrowsetype("category")) { %> CHECKED <% } %>>
  Category
  <SELECT NAME="browse_category">
<% 
    int categories_length = form.categories.length;
    int browse_category = form.getBrowse_category();
    for(int i=1; i < categories_length; i++) {  // loop to create category dropdown
%>
      <OPTION VALUE="<%= i%>" <% if(i == browse_category) {%> SELECTED <%}%>>
        <%= form.categories[i]%>
      </OPTION>
<%
    }
%>
  </SELECT><BR />

  Number of search results to return
  <SELECT NAME="limit_num">
<%
    int limit_num = form.getLimit_num();
    for(int i=1; i<11; i++) {   // loop to create number of results to return dropdown
%>
      <OPTION VALUE="<%= i%>" <% if(i == limit_num) {%> SELECTED <%}%>> 
        <%= i%>
      </OPTION>
<%
    }
%>
  </SELECT><BR />

  <INPUT TYPE=HIDDEN NAME='customerid' VALUE=<%= form.getCustomerid()%>>
<%
    int[] new_item = form.getItem();
    int new_item_length = new_item.length;
    for(int i=0; i<new_item_length; i++) {
%>
      <INPUT TYPE=HIDDEN NAME='item' VALUE=<%= new_item[i] %>> 
<%
    }
%>
  <INPUT TYPE=SUBMIT VALUE='Search'>
  </FORM>
<%
  }

//PerformanceMeasurement p = new PerformanceMeasurement("dspurchase_" + (new Date()));
//p.start(p.TOTAL);
//p.start(p.READING);

  // if a browse type of either TITLE AUTHOR or CATEGORY was selected
  if(!form.equalsBrowsetype("")) {  
    CachedRowSet products = form.doBrowse();
    if(!products.next()) {
%>
      <H2>No DVDs Found</H2>
<%
    }
    else { // search results were returned
%>
      <BR />
      <H2>Search Results</H2>
      <FORM ACTION="dsbrowse.jsp" METHOD="GET">
      <TABLE border="2">
      <TR>
        <TH>Add to Shopping Cart</TH><TH>Title</TH>
        <TH>Actor</TH><TH>Price</TH>
      </TR>
<%
      do {
%>
          <TR>
          <TD><INPUT NAME='selected_item' TYPE="CHECKBOX" VALUE=<%= products.getString(1)%>></TD>
          <TD><%= products.getString(3)%></TD>
          <TD><%= products.getString(4)%></TD>
          <TD><%= products.getString(5)%></TD>
          </TR>
          <%
      } while(products.next());     // loop to display search results in HTML table

      products.close();
//p.stop(p.READING);        
//p.stop(p.TOTAL);
%>
      </TABLE>
      <BR />

      <INPUT TYPE=HIDDEN NAME='customerid' VALUE=<%= form.getCustomerid()%>>
<%
      int[] item = form.getItem();
      int item_length = item.length;
      for(int i=0; i<item_length; i++) {
%>
        <INPUT TYPE=HIDDEN NAME='item' VALUE=<%= item[i]%>>
<%    }
%>
      <INPUT TYPE=SUBMIT VALUE='Update Shopping Cart'>
      </FORM>
<%
    }   // end of else
  }  // end of if browsetype not equal to null

  if(form.getNew_item_length() > 0) { // If the shopping cart is not empty then - Show shopping cart
%>
  <H2>Shopping Cart</H2>
  <FORM ACTION="dspurchase.jsp" METHOD="GET">
  <TABLE border="2">
  <TR>
    <TH>Item</TH><TH>Title</TH>
  </TR>
<%
    CachedRowSet titles = form.getProductTitles(); 
    int i=0;
    while(titles.next()) {  
%>
    <tr>
      <TD><%= ++i%></TD>
      <TD><%= titles.getString(2)%><INPUT TYPE="HIDDEN" NAME="item" VALUE=<%= titles.getInt(1)%>></TD>
    </TR>
<%
    }
    titles.close();
%>  
  </TABLE>
  <BR />
  <INPUT TYPE=HIDDEN NAME='customerid' VALUE=<%= form.getCustomerid()%>>
  <INPUT TYPE=HIDDEN NAME='num_of_items' VALUE=<%= i%>>
  
  
  <INPUT TYPE=SUBMIT VALUE='Checkout'>
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
