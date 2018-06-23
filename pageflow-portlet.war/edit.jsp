<%@page import="com.ail.pageflow.portlet.PageFlowCommon"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet"%>
<jsp:useBean class="java.lang.String" id="productNameURL" scope="request" />
<jsp:useBean class="java.lang.String" id="productName" scope="request" />
<jsp:useBean class="java.lang.String" id="pageFlowName" scope="request" />
<jsp:useBean class="java.lang.String" id="selectedSource" scope="request" />
<portlet:defineObjects />
<form class="aui-form" id="<portlet:namespace />" action="<%=productNameURL%>" method="post">
	<div class="taglib-form-navigator">
		<div class="form-section selected" style="float:none">
			<h3>PageFlow Preferences</h3>
			<fieldset class="aui-fieldset">
				<div class="aui-fieldset-content">
					<div class="lfr-panel-content">
						<div class="portlet-msg-info">
							<p>Select a product and page flow from the lists below to complete the configuration 
							of this	portlet. You can reconfigure these at any time by returning to the portlet's 
							Preferences view.</p>
							<p>The Configuration source option should in most cases be left set to "Preferences".
							When this portlet is embedded within another component, the "Session" mode allows that
							component to select the product and pageflow options.</p>
						</div>
					</div>
					<div>
						<span class="aui-field aui-field-select aui-field-menu"> 
							<span class="aui-field-content"> 
								<label class="aui-field-label" for="configurationSource"> Configuration source </label> 
								<span class="aui-field-element"> 
									<select class="aui-field-input aui-field-input-select aui-field-input-menu" name="selectedSource" onchange="return false;">
										<%=new PageFlowCommon().buildConfigurationSourceOptions(selectedSource)%>
									</select>
								</span>
							</span>
						</span> 
						<span class="aui-field aui-field-select aui-field-menu"> 
							<span class="aui-field-content"> 
								<label class="aui-field-label" for="productName"> Product </label> 
								<span class="aui-field-element"> 
									<select class="aui-field-input aui-field-input-select aui-field-input-menu" name="productName" onchange="this.form.submit()"><%=new PageFlowCommon().buildProductSelectOptions(productName)%></select>
								</span>
							</span>
						</span> 
						<span class="aui-field aui-field-select aui-field-menu"> 
							<span class="aui-field-content"> 
								<label class="aui-field-label" for="type"> Page Flow </label> 
								<span class="aui-field-element">
						    		<select class="aui-field-input aui-field-input-select aui-field-input-menu" name="pageFlowName"><%=new PageFlowCommon().buildPageFlowSelectOptions(productName, pageFlowName)%></select>
								</span>
							</span>
						</span> 
					</div>
					<div>
						<span class="aui-field aui-field-select aui-field-menu"> 
							<span class="aui-field-content"> 
								<span class="aui-field-element "> 
									<div class="button-holder">
										<button class="btn btn-primary" value="Save Preferences" title="Save Preferences" type="button" onclick="this.form.submit()"> Save Preferences </button>
									</div>
								</span>
							</span>
						</span>
					</div>
				</div>
			</fieldset>
		</div>
	</div>
</form>
<script type="text/javascript">
YUI().use('node', function (Y) {
	configChangeFunc=function(e) {
    	var p=Y.one("*[name='selectedSource'] option:checked").get("text").match(/Session|Request/);
    	Y.one("*[name='productName']").set("disabled",p);
    	Y.one("*[name='pageFlowName']").set("disabled",p);
    	if (p) {
	    	Y.one("*[name='productName']").set("value","?");
	    	Y.one("*[name='pageFlowName']").set("value","?");
		}    	
	}
	Y.one("*[name='selectedSource']").on("change",configChangeFunc);
	configChangeFunc();
});
</script>


