/*
 * Recorder view
 */
com.insideaem.recorder.RecorderView = CQ.Ext.extend(CQ.Ext.Viewport, {
    constructor : function(config) {
	var id = "insideaem-recorder";
	config.id = id;
	config.renderTo = CQ.Util.ROOT_ID;
	config.layout = "border";

	config.items = [ {
	    "id" : "cq-header",
	    "xtype" : "container",
	    "cls" : id + "-header",
	    "autoEl" : "div",
	    "region" : "north",
	    "items" : [ {
		"xtype" : "panel",
		"border" : false,
		"layout" : "column",
		"cls" : "cq-header-toolbar",
		"items" : [ new CQ.Switcher({}), new CQ.UserInfo({}), new CQ.HomeLink({}) ]
	    } ]
	}, {
	    "region" : "center",
	    items : {
		xtype : 'recorderpanel'
	    }
	} ];
	com.insideaem.recorder.RecorderView.superclass.constructor.call(this, config);

    }
});

// Register the widget
CQ.Ext.reg("recorderview", com.insideaem.recorder.RecorderView);