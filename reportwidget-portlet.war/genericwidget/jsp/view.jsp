<%@page import="java.util.List"%>
<%@page import="com.ail.pageflow.render.ReportWidgetHelper.ReportData"%>
<%@page import="com.liferay.portal.kernel.util.WebKeys"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@page import="com.liferay.portal.kernel.util.GetterUtil"%>
<%@page import="com.ail.pageflow.render.ReportWidgetHelper"%>
<%@page import="com.ail.pageflow.render.ReportWidgetHelper.ReportStyle"%>
<%@page import="com.ail.pageflow.render.ReportWidgetHelper.ReportPeriod"%>
<%@page import="com.ail.pageflow.render.ReportWidgetHelper.ReportInterval"%>
<%@page import="com.ail.pageflow.render.ReportWidgetHelper.ReportType"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui"%>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<portlet:defineObjects />

<% 
String storedReportType1 = GetterUtil.getString(portletPreferences.getValue("reportType1", ""));
String storedReportType2 = GetterUtil.getString(portletPreferences.getValue("reportType2", ""));
String storedReportValueType = GetterUtil.getString(portletPreferences.getValue("reportValueType", ""));
String storedInterval = GetterUtil.getString(portletPreferences.getValue("interval", ""));
String storedPeriod = GetterUtil.getString(portletPreferences.getValue("period", ""));
String storedProduct = GetterUtil.getString(portletPreferences.getValue("product", ""));
String storedReportStyle = GetterUtil.getString(portletPreferences.getValue("reportStyle", ""));
String storedIncludeTest = GetterUtil.getString(portletPreferences.getValue("includeTest", ""));

String portletId = (String)request.getAttribute(WebKeys.PORTLET_ID);

ReportWidgetHelper helper = new ReportWidgetHelper();

	ReportData reportData = null;
	
	if (storedReportType1 != null && !"".equals(storedReportType1)) {
		reportData = helper.getData(
					ReportType.valueOf(storedReportType1), 
					ReportType.valueOf(storedReportType2),
					ReportInterval.valueOf(storedInterval), 
					ReportPeriod.valueOf(storedPeriod), 
					ReportStyle.valueOf(storedReportStyle), 
					storedProduct,
					storedIncludeTest);
		
	}
	
	if (reportData != null) {
		
%>
<script type="text/javascript">

	
	if (!loaded) {
		google.charts.load('current', {'packages' : [ 'corechart' ]});
	}
	var loaded = true;

	google.charts.setOnLoadCallback(drawChart<%=portletId%>);
	

	function drawChart<%=portletId%>() {
		
		var data = new google.visualization.DataTable();
		
		<% 
			for (int i = 0; i < reportData.getColumnTypes().length; i++) {
		%>
				data.addColumn('<%=reportData.getColumnTypes()[i]%>', '<%=reportData.getHeaders()[i]%>');
		<% 
			} 
		%>
		
		data.addRows(<%=reportData.getFormattedData()%>);
		
		var options = {
			'title' : '<%=reportData.getTitle()%>',
			'width' : 0,
			'height' : 0
			<% 
				if ( !"PieChart".equals(storedReportStyle) ) { // add legend for pie charts only
			%>					
					,'legend' : 'none'
			<%
				}
			%>
		};

		var targetDiv = document.getElementById('chart_div_<%=portletId%>');
		var chart = new google.visualization.<%=storedReportStyle%>(targetDiv);
		
		chart.draw(data, options);
	}

	window.addEventListener("resize", function() {
		drawChart<%=portletId%>();
	});
</script>

<div id="chart_div_<%=portletId%>"></div>

<% 
	} else { 
%>

<div class='alert alert-info'>This report widget is not configured. Select Configuration from widget menu to set report properties.</div>

<% 	
	} 
%>


