/*
* Recorder button
*/
com.insideaem.recorder.RecorderButton = CQ.Ext.extend(CQ.Ext.Button, {
    enableToggle : true,
    itemId : 'recorderBtn',
    ref : 'recorderBtn',
    listeners : {
        toggle : function(button, state) {
            if(state){
                CQ.Ext.Msg.prompt('Name', 'Please enter a name for this recording session:', function(btn, text){
                    if (btn == 'ok'){
                        com.insideaem.recorder.RecorderController.startSession(text);
                        button.store.reload();
                    }
                    else{
                        button.toggle(false, false);
                    }
                });

            }
            else{
                com.insideaem.recorder.RecorderController.stopCurrentSession();
                button.store.reload();
            }
        }
    },
    initComponent : function() {
        var me = this;
        this.store.on('load', function() {
            var recorderEnabled = me.store.reader.jsonData['recorderEnabled'];
            me.toggle(recorderEnabled,true);
            if(recorderEnabled){
                var currentSessionName = me.store.reader.jsonData['recorderSessionName'];
                me.setText('Online. Recording: '+currentSessionName);
                me.setIconClass('recorder-online');
            }
            else{
                me.setText('Offline. Click to start recording');
                me.setIconClass('recorder-offline');
            }
        });
        
        com.insideaem.recorder.RecorderButton.superclass.initComponent.call(this);
        
    }
});

// Register the widget
CQ.Ext.reg("recorderbutton", com.insideaem.recorder.RecorderButton);