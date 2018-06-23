function populateSandpitJson(url, requestId) {
	var json;
	
	$.ajax({
				url: url,
				async: false,
				headers: {
					'requestId': requestId
				},
				success: function(result) {
					jsonNode = result;
				},
				error: function(data, status, error) {
					jsonNode = {
						'data': data,
						'status': status,
						'error': error
					};
				}
			});

	YUI().use(
			'aui-ace-editor',
			function(Y) {
				var textarea = $('#sandpitJson');
				var editor = new Y.AceEditor({
					boundingBox: '#jsonEditor',
					width: textarea.width(),
					height: textarea.height(),
					mode: 'json',
					useWrapMode: 'false',
					readonly: 'true',
					showPrintMargin: false
				});
				var jsonString = JSON.stringify(jsonNode, null, '  ');
				editor.set('value', jsonString);
				editor.render();
			});
}
