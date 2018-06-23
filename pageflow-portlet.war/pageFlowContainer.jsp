<%@page import="com.ail.pageflow.portlet.PageFlowContainerPortlet.PolicyAdminTab"%>
<%@page import="com.ail.pageflow.portlet.PageFlowContainerPortlet.PageFlowTab"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>

<jsp:useBean class="java.lang.String" id="actionURL" scope="request" />
<jsp:useBean class="java.lang.String" id="resourceURL" scope="request" />
<jsp:useBean class="java.lang.String" id="policySystemId" scope="request" />
<jsp:useBean class="java.lang.String" id="auth" scope="request" />
<jsp:useBean class="java.lang.String" id="status" scope="request" />

<jsp:useBean type="java.util.Collection<PageFlowTab>" id="tabs" scope="request" />


<portlet:defineObjects />

<meta name='gwt:module' content='com.ail.ui.client.search.PolicySearch'>



<div class="portlet-body">
	<script >
		
		function enableTabs() {
	    	
	        [].forEach.call(document.querySelectorAll('.disable-enable-tab-selector'), 
	        		function(tab) {
	        	
	        			var tabName = tab.id;
	        			if ("PolicySummaryPageFlow" == tabName
	        				|| "PolicyDetailPageFlow" == tabName
	        				|| "AssessmentSheetPageFlow" == tabName
	        				|| "PolicyDocumentsPageFlow" == tabName
	        				|| "PolicyAdminPageFlow" == tabName
	        				
	        				|| ("PaymentsPageFlow" == tabName && "QUO" == polsts)
	        				|| ("PaymentsPageFlow" == tabName && "ONR" == polsts)
	        				|| ("PaymentsPageFlow" == tabName && "SUB" == polsts)
	        				|| ("PaymentsPageFlow" == tabName && "CAN" == polsts)
	        				|| ("PaymentsPageFlow" == tabName && "DEL" == polsts)
	        				
	        				|| ("ReferralDetailPageFlow" == tabName && "REF" == polsts)
	        				|| ("ReferralDetailPageFlow" == tabName && "DEC" == polsts)
	        				
	        				|| ("QuoteToOnRiskPageFlow" == tabName && "SUB" == polsts)
	        				|| ("QuoteToOnRiskPageFlow" == tabName && "QUO" == polsts)
	        				
	        				|| "ClientDetailsPageFlow" == tabName
	        				
	        				|| "LogsPageFlow" == tabName) {
	        				
	        				tab.className = tab.className.replace("pf-tab-disabled", "pf-tab-enabled");
	        			}
	        			
	       			});
		}
		
		function disableTabs() {
	        	
	        [].forEach.call(document.querySelectorAll('.disable-enable-tab-selector'), 
	        		function(tab) {
			        	tab.className = tab.className.replace("pf-tab-enabled", "pf-tab-disabled");
			       	});
		}
		var psysid = '<%=policySystemId%>';
		var comusr = '<%=auth%>';
		var polsts = '<%=status%>';
		
		$('#disableClick').click(function(event) {
		    event.preventDefault();
		    return false;
		});
		
		function removeResultHighlight() {
        	
	        [].forEach.call(document.querySelectorAll('.gui-highlight-result'), 
	        		function(row) {
			        	row.className = row.className.replace("gui-highlight-result", "");
			       	});
		}
		
	</script>
		<div>
			<ul class="nav nav-tabs pf-section">
				<% 
				
				boolean isSearchScreen = false;
				String tabDisabled = "";
				
				for(PageFlowTab pageFlowTab: tabs) { 
	
					if (pageFlowTab.isVisible()) {
							
						if (pageFlowTab.isSelected()) { 
							if (pageFlowTab.getTab() == PolicyAdminTab.Search) {
								isSearchScreen = true;
							}
						%>
							<li class="tab active">
								<a href="" onclick="return false;" > <%=pageFlowTab.getTab().getLabel()%> </a>
							</li>
						<% 
						} else {
							if (pageFlowTab.getTab() != PolicyAdminTab.Search) {
								tabDisabled = "disable-enable-tab-selector pf-tab-disabled";
							} else {
								tabDisabled = "";
							}
						%>
						
							<li class="tab inactive">
								<a class="<%=tabDisabled%>"
									id="<%=pageFlowTab.getTab().name()%>"
									<% 
									if (pageFlowTab.getTab() == PolicyAdminTab.Search) {
									%>	
									
										href="<%=actionURL%>&pageFlowTab=<%=pageFlowTab.getTab().name()%>"
										
									<% 
									} else {
									%>
										id="disableClick"
										href="#anchor" 
										onclick="callServeResource('${resourceURL}', 'op=selectTab&policyEntityId=' + psysid + '&pageFlowTab=<%=pageFlowTab.getTab().name()%>')" 
									<% 
									}
									%>
									><%=pageFlowTab.getTab().getLabel()%>
								</a>
							</li>
						<% 
						} 
					}
				}
				%>
				
			</ul>
		</div>

		<% if (isSearchScreen) { %>
			<script src="/gwtui-portlet/policysearch/policysearch.nocache.js?version=1.1"></script>
			<script>
		
			</script>
			<fmt:setBundle basename="com.ail.ui.client.common.i18n.Messages" var="lang"/>
			<div id='pf-pageflow-container'>
				<table class="gui-cell-border" width="100%">
					<tr class="gui-cell-border">
						<td class="gui-title-text gui-standard-cell">
							<span id="gui-search-title">POLICY ADMIN SEARCH</span>
						</td>
					</tr>
					
					<tr>
						<td class="gui-standard-text gui-standard-cell">
							<span id="gui-POLICYSEARCH-advancedsearch-showhide"></span>
						</td>
					</tr>
					<tr>
						<td class="gui-standard-text gui-standard-cell">
							<span id="gui-POLICYSEARCH-advancedsearch-container"></span>
						</td>
					</tr>
					<tr class="gui-cell-border">
						<td class="gui-standard-text gui-standard-cell">
							<span id="gui-POLICYSEARCH-advancedsearch-results"></span>
						</td>
					</tr>
				</table>
			</div>
		<% } else { %>
			<script>
				$(document).ready(function() { 
					enableTabs();
				 });
			</script>
		<% } %>
</div>


