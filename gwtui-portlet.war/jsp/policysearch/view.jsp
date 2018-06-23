<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<portlet:defineObjects />

	<fmt:setBundle basename="com.ail.ui.client.common.i18n.Messages" var="lang"/>

	<table class="gui-cell-border" width="100%">
		<tr class="gui-cell-border">
			<td class="gui-title-text gui-standard-cell">
				<span id="gui-POLICYSEARCH-search-title"></span>
			</td>
		</tr>
		<tr>
			<td class="gui-standard-text gui-standard-cell"><fmt:message key="enterQuoteNumber" bundle="${lang}"/></td>
		</tr>
		<tr class="gui-cell-border">
			<td class="gui-standard-text gui-standard-cell">
				<span id="gui-POLICYSEARCH-search-field-container"></span>
				<span id="gui-POLICYSEARCH-search-button-container"></span>
				<div id="gui-POLICYSEARCH-error-label-container" class="gui-standard-text"></div>
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

