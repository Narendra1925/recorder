com.insideaem.recorder.SidekickHook = function(sidekickContainerWindow) {
    CQ.Log.debug('AEM Recorder loaded...');
    CQ.Log.debug('Registering handler for "sidekickready" event');
    sidekickContainerWindow.CQ.WCM.on('sidekickready', function(sidekick) {
        CQ.Log.debug('"sidekickready" event fired. Registering handler for "loadcontent" event');
        sidekick.on('add', function(container, component, index) {
            CQ.Log.debug('"loadcontent" event on sidekick fired --> Add recorder tab to sidekick');
            if (!sidekick.recorderPanelAdded && container.xtype === 'tabpanel') {
                sidekick.recorderPanelAdded = true;
                if(com.insideaem.recorder.RecorderController.canRecord()){
                    container.insert(0, {
                        xtype : 'recorderpanel',
                        tabpanel: container
                    });
                }
            }
        });
    });
}(top);