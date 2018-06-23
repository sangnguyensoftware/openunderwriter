<%@ taglib uri="/birt.tld" prefix="birt" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<%
 	String reportName = portletConfig.getInitParameter("reportName");
	String reportUrl = "product://localhost:8080/AIL/Base/Reports/MI/Widgets/" + reportName + ".rptdesign";
	
%>

<div id="report-viewer-widget">
    <birt:viewer id="<%=reportName%>" 
                 reportDesign="<%=reportUrl%>"
                 baseURL="/birt"
                 pattern="run"
                 frameborder="no"
                 style="width: 100%; height: 320px"
                 showNavigationBar="false"
                 showToolBar="false"
                 format="html"
                 forceOverwriteDocument="false">
     </birt:viewer>
</div>