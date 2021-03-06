com.insideaem.recorder.RecorderController = function() {
    function send(params) {
	CQ.shared.HTTP.post(com.insideaem.recorder.CONTROL_URL, null, params);
    }
    ;
    return {
	canRecord : function() {
	    var result = CQ.shared.HTTP.get(com.insideaem.recorder.STORE_URL, null, {});
	    return result.status === 200;
	},
	startSession : function(name) {
	    var params = {};
	    params['_charset_'] = 'UTF-8';
	    params['recorderSessionName'] = name;
	    params['cmd'] = 'start';

	    send(params);
	},
	stopCurrentSession : function() {
	    var params = {};
	    params['_charset_'] = 'UTF-8';
	    params['cmd'] = 'stop';

	    send(params);
	},
	resumeSession : function(name) {
	    this.startSession(name);
	},
	deleteSession : function(name) {
	    var params = {};
	    params['recorderSessionName'] = name;
	    params['_charset_'] = 'UTF-8';
	    params['cmd'] = 'delete';

	    send(params);
	}
    };
}();