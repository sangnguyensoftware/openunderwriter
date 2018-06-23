<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>


<portlet:defineObjects />

	<fmt:setBundle basename="com.ail.ui.client.common.i18n.Messages" var="lang"/>

	<table class="gui-cell-border" width="100%">
		<tr class="gui-cell-border">
			<td class="gui-title-text gui-standard-cell">
				<span id="gui-QUICKSEARCH-search-title"></span>
			</td>
		</tr>
		<tr>
			<td class="gui-standard-text gui-standard-cell"><fmt:message key="enterQuoteNumber" bundle="${lang}"/></td>
		</tr>
		<tr>
			<td class="gui-standard-text gui-standard-cell">
				<span id="gui-QUICKSEARCH-search-field-container"></span>
				<span id="gui-QUICKSEARCH-search-button-container"></span>
			</td>
		</tr>
		<tr>
			<td id="gui-QUICKSEARCH-error-label-container" class="gui-standard-text" align="center"></td>
		</tr>
	</table>

