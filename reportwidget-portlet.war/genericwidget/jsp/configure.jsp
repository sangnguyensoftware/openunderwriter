<%@page import="com.ail.pageflow.render.ReportWidgetHelper.ReportStyle"%>
<%@page import="com.ail.pageflow.render.ReportWidgetHelper.ReportPeriod"%>
<%@page import="com.ail.pageflow.render.ReportWidgetHelper.ReportInterval"%>
<%@page import="com.ail.pageflow.render.ReportWidgetHelper.ReportType"%>
<%@page import="com.ail.pageflow.render.ReportWidgetHelper"%>
<%@page import="java.util.List"%>
<%@page import="com.liferay.portal.kernel.util.Constants"%>
<%@page import="com.liferay.portal.kernel.util.StringPool"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<portlet:defineObjects />
<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<% 
String storedReportType1 = GetterUtil.getString(portletPreferences.getValue("reportType1", ""));
String storedReportType2 = GetterUtil.getString(portletPreferences.getValue("reportType2", ""));
String storedReportValueType = GetterUtil.getString(portletPreferences.getValue("reportValueType", ""));
String storedInterval = GetterUtil.getString(portletPreferences.getValue("interval", ""));
String storedPeriod = GetterUtil.getString(portletPreferences.getValue("period", ""));
String storedProduct = GetterUtil.getString(portletPreferences.getValue("product", ""));
String storedReportStyle = GetterUtil.getString(portletPreferences.getValue("reportStyle", ""));
String storedIncludeTest = GetterUtil.getString(portletPreferences.getValue("includeTest", ""));

%>
<aui:form action="<%=configurationURL%>" method="post" name="form">

    <aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

	
	<aui:select name="preferences--reportType1--" label="Select reporting data type 1">
		<% 
			for(ReportType value : ReportType.values()){ 
				if (value != ReportType.NONE) {
		%>
	        <aui:option label="<%=value.getLabel() %>" value="<%=value.name() %>"  selected="<%=value.name().equals(storedReportType1) %>"/>
	    <% 
				}
			} 
		%>
    </aui:select>
    
    <aui:select name="preferences--reportType2--" label="Select reporting data type 2 (Optional - BarChart, LineChart and ColumnChart only)">
		<% for(ReportType value : ReportType.values()){ %>
	        <aui:option label="<%=value.getLabel() %>" value="<%=value.name() %>"  selected="<%=value.name().equals(storedReportType2) %>"/>
	    <% } %>
    </aui:select>
    
    <aui:select name="preferences--interval--" label="Select report grouping">
		<% for(ReportInterval value : ReportInterval.values()){ %>
	        <aui:option label="<%=\"By \" + value.name() %>"  value="<%=value.name() %>"  selected="<%=value.name().equals(storedInterval) %>" />
	    <% } %>
    </aui:select>
    
    <aui:select name="preferences--period--" label="Select period report covers">
		<% for(ReportPeriod value : ReportPeriod.values()){ %>
	        <aui:option label="<%=value.getLabel() %>"  value="<%=value.name() %>"  selected="<%=value.name().equals(storedPeriod) %>" />
	    <% } %>
    </aui:select>
    
    <aui:select name="preferences--reportStyle--" label="Select report layout style">
		<% for(ReportStyle value : ReportStyle.values()){ %>
	        <aui:option label="<%=value.name() %>"  value="<%=value.name() %>"  selected="<%=value.name().equals(storedReportStyle) %>" />
	    <% } %>
    </aui:select>
    
    <aui:select name="preferences--product--" label="Select product">
		<% for(String value : ReportWidgetHelper.getProducts()){ %>
	        <aui:option label="<%=value %>"  selected="<%=value.equals(storedProduct) %>" />
	    <% } %>
    </aui:select>
    
    <aui:input name="preferences--includeTest--" type="checkbox" label="Include test data" value="<%=storedIncludeTest %>" />
    
	
    <aui:button-row>
        <aui:button type="submit" />
    </aui:button-row>
    
</aui:form>
