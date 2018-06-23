<%@ page import="com.ail.workflow.NewBusinessReferralAssetRendererData" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<style type="text/css">
	div.task-content-actions {
		display: none;
	}
	
	li.task-change-status-link {
		display: none;
	}
</style>

<table class="referral-reasons">
	<%
		NewBusinessReferralAssetRendererData rendererData = (NewBusinessReferralAssetRendererData) request.getAttribute("rendererData");
		Boolean showReferralControls = (Boolean)request.getAttribute("viewReferralControlPanel");
		String referralWorkflowState = (String)request.getAttribute("referralWorkflowState");
		
		if (rendererData.getMarkerReasons().size() > 0) {
			%><tr><th>MARKERS</th></tr><% 
			for(String reason: rendererData.getMarkerReasons()) {
				%><tr><td class='accordion-group'><%=reason %></td></tr><%
			}
		}
	
		if (rendererData.getNoteReasons().size() > 0) {
			%><tr><th>NOTES</th></tr><% 
			for(String reason: rendererData.getNoteReasons()) {
				%><tr><td class='accordion-group'><%=reason %></td></tr><%
			}
		}
	%>
</table>
<br/>
<script>
	var psysid="<%=rendererData.getSystemId() %>";
	var referralWorkflowState="<%=referralWorkflowState %>";
	
	function enableDone() {
		enableDisableDone(false);
	}
	
	function disableDone() {
		enableDisableDone(true);			
	}
	
	function enableDisableDone(isDisabled) {
		var doneLink = document.getElementsByClassName("task-change-status-link");
		if (doneLink[0]) {
			if (isDisabled) {
				doneLink[0].style.display = 'none';
			} else {
				doneLink[0].style.display = 'block';
			}
		} 
	}
</script>

<script src="/gwtui-portlet/workflow/workflow.nocache.js"></script>

<meta name='gwt:module' content='com.ail.ui.client.workflow.ReferralWorflowControlPanel'>

<span id="gui-referral-workflow-control-container"></span>

<br/>
