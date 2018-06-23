/* 'item' is a string, 'list' is a string of semicolon separated values. This
 * function returns true if 'item' appears in 'list'.
 */
function isInList(item, list) {
	return (list.split(";").indexOf(item) != -1)
}

/*
 * IE's implementation of getElementsByName is very patchy - works in some
 * versions and not in others. Hence this local implementation that will work on
 * all versions of IE and other browsers. Deprecated: DO NOT INCLUDE THIS IN 2.2
 */
function findElementsByName(name) {
	var tags = new Array();
	var ret = new Array();
	var retIdx;

	tags[0] = "select";
	tags[1] = "input";
	tags[2] = "textarea";

	for (retIdx = 0, t = 0; t < tags.length; t++) {
		var elem = document.getElementsByTagName(tags[t]);

		for (i = 0; i < elem.length; i++) {
			if (elem[i].getAttribute("name") == name) {
				ret[retIdx++] = elem[i];
			}
		}
	}

	return ret;
}

/*
 * If 'condition' is true then disable the 'target' page element, otherwise
 * enable it.
 */
function disableTargetIf(condition, targetName) {
	$("[name='" + targetName + "']").prop("disabled", condition);
}

/*
 * If 'condition' is true then enable the 'target' page element, otherwise
 * disable it.
 */
function enableTargetIf(condition, targetName) {
	disableTargetIf(!condition, targetName);
}

/*
 * Expand and show a hidden div section.
 */
function showDivDisplay(id) {
	$('#' + id).fadeIn("slow");
}

/*
 * Shrink and hide a div section.
 */
function hideDivDisplay(id) {
	$('#' + id).fadeOut();
}

function showDivDisplayIf(condition, id) {
	if (condition) {
		showDivDisplay(id);
	} else {
		hideDivDisplay(id);
	}
}

function showHideDivDisplay(showCondition, hideCondition, id) {
	if (showCondition) {
		showDivDisplay(id);
	} else if (hideCondition) {
		hideDivDisplay(id);
	}
}

function showHideDivDisplayForRadioChoice(divId, enableForValues, questionId) {
	hideDivDisplay(divId);
	radios = document.getElementsByName(questionId);
	for (var i = 0; i < radios.length; i++) {
		if (radios[i].checked && isInList(radios[i].value, enableForValues)) {
			showDivDisplay(divId);
		}
	}
}

function _createOption(optionText, isSelected) {
	if (isSelected)
		return "<option selected>" + optionText + "</option>";
	else
		return "<option>" + optionText + "</option>";
}

$.fn.sortOptions = function(){
    $(this).each(function(){
    	var sel = $(this).find(":selected").attr("value");
        var op = $(this).children("option");
        op.sort(function(a, b) {
            return a.text > b.text ? 1 : -1;
        })
        $(this).empty().append(op);
        $(this).val(sel);
        return $(this);
    });
}

/*
 * This method is called to load options into a choice (drop down menu). It'll
 * be called once for each row (in a rowscroller etc), it is also used to
 * populate the 'Master' menu in a master/slave setup.
 */
function loadChoiceOptions(selectName, selectedOption, array) {
	var select = "select[name='" + selectName + "']";
	var selected = unescape(selectedOption);
	$(select).empty();
	for (var i = 1; i < array.length; i++) {
		$(select).append(_createOption(array[i][0], selected == array[i][0]));
	}
	resizeSelect(selectName);
}

/*
 * On page load, load the model options appropriate to whatever master is
 * currently selected.
 */
function loadSlaveChoiceOptions(masterSelectName, slaveSelectName,
		selectedOption, array) {
	var master = "select[name='" + masterSelectName + "']";
	var masterValue = $(master).val();
	var canvas = loadSlaveChoiceOptions.canvas
			|| (loadSlaveChoiceOptions.canvas = document
					.createElement("canvas"));
	var context = canvas.getContext("2d");
	var width = 0;

	slave = $("select[name='" + slaveSelectName + "']");

	context.font = slave.css("font-size") + " " + slave.css("font-family");

	slave.empty()

	for (var m = 1; m < array.length; m++) {
		if (array[m][0] == masterValue) {
			slave.append(_createOption(array[1][0], false));
			for (var i = 1; i < array[m].length; i++) {
				slave.append(_createOption(array[m][i],
						array[m][i] == selectedOption));
			}
		}

		$.each(array[m], function(index, value) {
			width = Math.max(width, context.measureText(value + "MMM").width);
		});
	}

	slave.css("width", width + "px");
}

function resizeSelect(targetsName) {
	var canvas = resizeSelect.canvas
			|| (resizeSelect.canvas = document.createElement("canvas"));
	var context = canvas.getContext("2d");
	var target = "[name='" + targetsName + "']";
	var select = $(target);
	context.font = select.css("font-size") + " " + select.css("font-family");

	var widths = $(target + " option").map(function() {
		return context.measureText($(this).val() + "MMM").width;
	}).get();

	select.css("width", Math.max.apply(Math, widths));
}

function formatnumber(obj, decimalSeparator, thousandsSeparator, places) {
	var num = new NumberFormat();
	num.setInputDecimal(decimalSeparator);
	num.setPlaces(places, places != -1);
	num.setSeparators(thousandsSeparator != '', thousandsSeparator,
			decimalSeparator);
	num.setNumber(obj.value);
	obj.value = num.toFormatted();
}

function initialiseTinyMCE() {
	var themeName = Liferay.ThemeDisplay.getPathThemeRoot();
	var themeCssHref;

	for (var i = 0; i < document.styleSheets.length && themeCssHref == null; i++) {
		var href = document.styleSheets[i].href;
		if (href.indexOf(themeName) != -1 && href.indexOf('themeId') != -1
				&& href.indexOf('main.css') != -1) {
			themeCssHref = href;
		}
	}

	tinymce.init({
		mode : "specific_textareas",
		editor_selector : "pf-rich-note",
		plugins : "contextmenu",
		contextmenu : "undo redo | bold italic underline",
		content_css : themeCssHref,
		body_class : "textarea-tinymce",
		menubar : false,
		toolbar : false,
		statusbar : false,
		browser_spellcheck : true
	});
}

function submitVirtualForm(action, method, fields) {
	var form = document.createElement("form");

	form.method = method;
	form.action = action;

	for ( var fieldName in fields) {
		if (fields.hasOwnProperty(fieldName)) {
			var element = document.createElement("input");
			element.name = fieldName;
			element.value = fields[fieldName];
			element.hidden = true;
			form.appendChild(element);
		}
	}

	document.body.appendChild(form);

	form.submit();
}

function selectFocus(requestParam) {
	var formElement;
	if (requestParam.split(':')[0] == "op=add") {
		var id = requestParam.split(':')[1].split('=')[1];
		formElement = $('#' + id).find(".pf-scroller-data").last();
	} else if (requestParam.split(':')[0] == "op=delete") {
		var id = requestParam.split(':')[1].split('=')[1];
		var row = parseInt(requestParam.split(':')[2].split('=')[1]);
		row = (row > 0) ? row - 1 : row;
		formElement = $('#' + id).find(".pf-scroller-data").slice(row, row+1);
	}

	if (formElement && formElement.size()!=0) {
		formElement.find('input,textarea,select').first().focus();
	}
	else { 
		var y=$('#pageflow').position().top;
		if ($('.pf-input-error').size()!=0) {
			y=$('.pf-input-error').first().position().top;
		}
		$(window).scrollTop(y);		
	}
}

function callServeResource(requestUri, requestParam) {
	mask();

	$('#pageflow-wrapper').find('.pf-rich-note').each(function(i) {
		tinymce.get(this.id).save();
	})

	var params = 'op=' + requestParam + '&' + $('#pageflow').serialize();

	$.ajax({
		type : 'POST',
		url : requestUri,
		dataType : 'html',
		data : params,
		success : function(data) {
			$('#pageflow-wrapper').find('.pf-rich-note').each(function(i) {
				tinymce.get(this.id).remove();
			})
			$('#pageflow-wrapper').replaceWith(data);
			selectFocus(requestParam);
		},
		error : function(data, status, error) {
			if (requestUri.indexOf("/sandpit") > 0) {
				unmask();
				$('select[name="view"]').val([]);
				$('option[value="Exception"]').removeAttr('disabled');
				$('option[value="Exception"]').attr('selected', 'selected');
				$('form[name="productDebug"]').submit()
			} else {
				$('#pageflow-wrapper').replaceWith(data);
			}
		}
	});
}

function registerUploadDocumentHandler(target, uploadUrl, uploadFileName,
		requestUri, fileTypes) {
	$(document).ready(function() {
		$(target).uploadFile({
			url : uploadUrl,
			fileName : uploadFileName,
			multiple : true,
			dragDrop : true,
			showStatusAfterSuccess : false,
			sequential : true,
			sequentialCount : 1,
			dragdropWidth : "",
			allowedTypes : fileTypes,
			afterUploadAll : function(obj) {
				callServeResource(requestUri, "none");
			}
		});
	});
}

function registerUploadSingleDocumentHandler(target, uploadUrl, uploadFileName,
		requestUri, fileTypes) {
	
	$(document).ready(function() {
		$(target).uploadFile({
			url : uploadUrl,
			fileName : uploadFileName,
			multiple : false,
			dragDrop : true,
			sequential : false,
			showStatusAfterSuccess : true,
			allowedTypes : fileTypes,
			maxFileSize : 5000000, // ~5GB 
			onLoad : function(obj) {
				removeLoadingMessage();
			},
			afterUploadAll : function(obj) {
				callServeResource(requestUri, "immediate=true");
			}
	            
		});
	});
}

function removeLoadingMessage() {
	$("#file-upload-loading-message").remove();
}

function downloadDocument(reference, renderHint) {
	if (renderHint.indexOf("inline") >= 0) {
		window.open("/pageflow-portlet/fetchDocument?reference=" + reference + "&inline", "_blank");
	}
	else {
		mask();
	
		$.fileDownload("/pageflow-portlet/fetchDocument?reference=" + reference, {
			successCallback : function(url) {
				unmask();
			},
			failCallback : function(responseHtml, url) {
				alert("Problem downloading document\n\n" + responseHtml);
				unmask();
			}
		});
	}
}

function mask() {
	$("#ui-datepicker-div").hide();
	$(".pf-pageflow-container").find("input[type=button]").attr("disabled",
			"disabled");
	$(".pf-pageflow-container").mask("", 1000);
}

function unmask() {
	$(".pf-pageflow-container").unmask();
	$(".pf-pageflow-container").find("input[type=button][onclick]").removeAttr(
			"disabled");
}

$(document).ready(function() {
 	$(".pf-item").on('click', function() {
		var target = $(this).children(".pf-item-value:first");
		if($(target).prop("value") === undefined){		
			$(this).prepend("<textarea id='js-copytxtarea' style='height:0;'>"+$(target).prop("innerText")+"</textarea>");
			$("#js-copytxtarea").select();
			document.execCommand('copy');
			$("#js-copytxtarea").remove();
			target.effect( "highlight",{},1500);
		}
	});
});