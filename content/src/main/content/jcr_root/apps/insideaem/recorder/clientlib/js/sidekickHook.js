com.insideaem.recorder.SidekickHook = function(sidekickContainerWindow) {
    CQ.Log.debug('AEM Recorder loaded...');
    CQ.Log.debug('Registering handler for "sidekickready" event');
    sidekickContainerWindow.CQ.WCM.on('sidekickready', function(sidekick) {
	var recorderPanelAdded = false;
	CQ.Log.debug('"sidekickready" event fired. Registering handler for "loadcontent" event');
	sidekick.on('add', function(container, component, index) {
	    CQ.Log.debug('"loadcontent" event on sidekick fired --> Add recorder tab to sidekick');
	    if (!recorderPanelAdded && container.xtype === 'tabpanel') {
		recorderPanelAdded = true;
		container.insert(0, {
		    xtype : 'recorderpanel'
		});
	    }
	});
    });
}(top);