<!-- $Id: dspurchase.jsp 85 2005-11-13 15:54:30Z frank $ -->
<%/*
dspurchase.jsp jsp page that purchases an order from the DVD store by entering 
the order into a database running on Oracle

modified by Frank Berger (www.fm-berger.de)

this code is based on:
  Copyright 2005 Dell
  Written by Todd Muirhead/Dave Jaffe      Last modified: 9/14/05 */%>

<%@page language="java" %>
<%@page session="false"%>
<%@page import="java.lang.Exception" %>
<%@page import="java.math.BigDecimal" %>
<%@page import="java.text.NumberFormat" %>
<%@page import="javax.sql.rowset.CachedRowSet" %>
<%@page import="mypackage.DSPurchase" %>

<HTML>
<HEAD><TITLE>DVD Store Purchase Page</TITLE></HEAD>
<BODY style="font:Arial; color:blue;">
<H1 ALIGN="CENTER">DVD Store</H1>


<%
  DSPurchase form = new DSPurchase();
  form.readinRequest(request);
/* Oracle DMS performance measurement instrumentation */
/* page import="mypackage.PerformanceMeasurement" */
//PerformanceMeasurement p = new PerformanceMeasurement("dspurchase_" + (new Date()));
//p.start(p.TOTAL);

  NumberFormat money = NumberFormat.getCurrencyInstance();
  money.setMinimumFractionDigits(2);
  money.setMaximumFractionDigits(2);

  if(form.getConfirmpurchase().equals("")) { // if not confirmiming purchase - print list to allow for
                                // changes in quantities and for removal of items from list  
    CachedRowSet products = form.getProducts();                                
%>
  <H2>Selected Items: specify quantity desired; click Purchase when finished</H2>
  <BR />
  <FORM ACTION="dspurchase.jsp" METHOD="GET">
  <TABLE border="2">
    <TR><TH>Item</TH><TH>Quantity</TH><TH>Title</TH><TH>Actor</TH>
      <TH>Price</TH><TH>Remove From Order?</TH></TR>
<%
    double netamount = 0;
    int[] myquans = form.getQuan();
    try {
     int i=0;
     while(products.next()) {
%>
      <TR>
        <TD ALIGN="CENTER"><%= i+1%>
          <INPUT NAME="item" TYPE="HIDDEN" VALUE="<%= products.getInt(1)%>"></TD>
        <TD><INPUT NAME="quan" TYPE="TEXT" SIZE="10" VALUE="<%= myquans[i]%>"></TD>
        <TD><%= products.getString(2)%></TD>
        <TD><%= products.getString(3)%></TD>
        <TD ALIGN="RIGHT">$<%= products.getFloat(4)%></TD>
        <TD ALIGN="CENTER"><INPUT NAME="drop" TYPE="CHECKBOX" VALUE="<%= products.getInt(1)%>"></TD>
      </TR>
<%
      netamount = netamount + myquans[i] * products.getFloat(4);
      i++;
    }  // end of for loop to print items in list

    }
    catch(Exception e) {
      System.out.println("Error dspurchase.jsp:");
      e.printStackTrace();
    }
    double taxamount = netamount * 0.0825;
%>
  <TR><TD></TD><TD></TD><TD></TD><TD>Subtotal</TD><TD ALIGN="RIGHT"><%= money.format(netamount)%></TD></TR>
  <TR><TD></TD><TD></TD><TD></TD><TD>Tax (<%= "8.25"%>%)</TD><TD ALIGN="RIGHT"><%= money.format(taxamount)%></TD></TR>
  <TR><TD></TD><TD></TD><TD></TD><TD>Total</TD><TD ALIGN="RIGHT"><%= money.format(taxamount + netamount)%></TD></TR>
  </TABLE><BR />
    
  <INPUT TYPE="HIDDEN" NAME="customerid" VALUE="<%= form.getCustomerid()%>">

  <INPUT TYPE="SUBMIT" name="update" VALUE="Update and Recalculate Total">
  <br />
  <INPUT TYPE="SUBMIT" NAME="confirmpurchase" VALUE="Purchase">
  </FORM><BR />

<%  
  }
  else {  // confirmpurchase=yes  => update ORDERS, ORDERLINES, INVENTORY and CUSTOMER table
%>
  <H2>Purchase complete</H2>
  <TABLE border="2">
  <TR><TH>Item</TH><TH>Quantity</TH><TH>Title</TH><TH>Actor</TH><TH>Price</TH></TR>
<%

  // The following section does the transactional commit of update
  // on orders, orderlines, customers, and inventory on the orderconn connection
    int[] myquans = form.getQuan();
   
    form.doPurchase();
    String[] titles = form.getTitles();
    String[] actors = form.getActors();
    BigDecimal[] prices = form.getPrices();
    
    // loop to print out list of items purchased on purchase confirmation page
    for(int i=0; i<titles.length; i++) {
%>    
      <TR>
        <TD ALIGN="CENTER"><%= i+1%></TD>
        <TD><%= myquans[i]%></TD>
        <TD><%= titles[i]%></TD>
        <TD><%= actors[i]%></TD>
        <TD ALIGN="RIGHT"><%= money.format(prices[i])%></TD>
      </TR>
<%
    }  // end of for loop to print out items
%>
  <TR><TD></TD><TD></TD><TD></TD><TD>Subtotal</TD><TD ALIGN="RIGHT">$<%= form.getNetamount()%></TD></TR>
  <TR><TD></TD><TD></TD><TD></TD><TD>Tax (<%= "8.25"%>%)</TD><TD ALIGN="RIGHT"><%= money.format(form.getTaxamount())%></TD></TR>
  <TR><TD></TD><TD></TD><TD></TD><TD>Total</TD><TD ALIGN="RIGHT"><%= money.format(form.getTotalamount())%></TD></TR>
  </TABLE><BR />
<%
    if(form.getOrderid() != 0) {
      // To Do: verify credit card purchase against a second database

      // get credit card info and print confirmation message
%>
      <H3><%= money.format(form.getTotalamount())%> charged to credit card <%= form.getCreditcard()%> (<%= form.cctypes[form.getCreditcardtype()-1]%>), expiration <%= form.getCreditcardexpiration()%></H3><BR />
      <H2>Order Completed Successfully --- ORDER NUMBER:  <%= form.getOrderid()%></H2><BR />
<%
    }
    else {
%>
      <H3>Insufficient stock - order not processed</H3>
<%
    }
  } 
//p.stop(p.TOTAL);
%>
<HR />
<P ALIGN="CENTER">Thank You for Visiting the DVD Store!</P>
<HR />
<P ALIGN="CENTER">Copyright &copy; 2005 Dell / modified by Frank Berger (wwww.fm-berger.de)</P>
</BODY>
</HTML>