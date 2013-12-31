/*
* Recorder panel
*/
com.insideaem.recorder.RecorderPanel = CQ.Ext.extend(CQ.Ext.Panel, {
    tabTip : 'Recorder',
    iconCls : 'cq-sidekick-tab cq-sidekick-tab-icon-recorder',
    mask : function() {
        this.setDisabled(true);
    },
    unmask : function() {
        this.setDisabled(false);
    },
    constructor : function(config) {
        if(!config.inSideKick){
            config.renderTo = 'CQ';
        }
        var me = this;
        var reader = new CQ.Ext.data.JsonReader({
            // metadata configuration options:
            idProperty : 'timestamp',
            root : 'changes',
            totalProperty : 'count',
            
            // the fields config option will internally create an
            // CQ.Ext.data.Record
            // constructor that provides mapping for reading the record data
            // objects
            fields : [ 'path', 'timestamp', 'recorderSessionName' ]
        });
        
        var store = new CQ.Ext.data.GroupingStore({
            autoLoad : true,
            url : com.insideaem.recorder.STORE_URL,
            groupField : 'recorderSessionName',
            reader : reader
        });
        
        function activateChangesInSession(sessionName) {
            var changes = store.reader.jsonData.changes;
            var paths = [];
            for ( var i = 0; i < changes.length; i++) {
                var change = changes[i];
                if (change.recorderSessionName === sessionName) {
                    paths.push(change.path);
                }
            }
            
            me.mask();
            var config = {
                id : CQ.Util.createId("cq-asset-reference-search-dialog"),
                path : paths,
                listeners:{
                    show: function(dialog){
                        var cancelBtn = dialog.buttons[1];
                        if(cancelBtn){
                            cancelBtn.on('click',function(){
                                me.unmask();
                            });
                        }
                    }
                },
                callback : function(paths) {
                    CQ.wcm.SiteAdmin.internalActivatePage.call(me, paths, function() {
                        com.insideaem.recorder.RecorderController.deleteSession(sessionName);
                        store.reload();
                        me.unmask();
                    });
                }
            };
            new CQ.wcm.AssetReferenceSearchDialog(config);
            
        }
        ;
        
        config.items = [ {
            xtype : 'grid',
            ref : 'grid',
            tbar : [ {
                xtype : 'recorderbutton',
                store : store
            } ],
            height : 300,
            store : store,
            columns : [ {
                header : 'Path',
                dataIndex : 'path'
            }, {
                header : 'Session Name',
                dataIndex : 'recorderSessionName',
                groupRenderer : function(value) {
                    var currentSessionName = store.reader.jsonData.recorderSessionName;
                    if (value == currentSessionName) {
                        return value + ' (Current Session)';
                    } else {
                        return value;
                    }
                }
            }, {
                header : 'Last Updated',
                renderer : function(value) {
                    return new Date(value).format('d/m/Y');
                },
                dataIndex : 'timestamp'
            } ],
            view : new CQ.Ext.grid.GroupingView({
                forceFit : true,
                groupTextTpl : '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Changes" : "Change"]})'
            }),
            listeners : {
                groupcontextmenu : function(grid, groupField, groupValue, event) {
                    var currentSessionName = store.reader.jsonData.recorderSessionName;
                    var items = [];
                    if (groupValue !== currentSessionName) {
                        items.push({
                            text : 'Resume',
                            handler : function() {
                                com.insideaem.recorder.RecorderController.resumeSession(groupValue);
                                store.reload();
                            }
                        });
                    }
                    
                    items.push({
                        text : 'Delete',
                        handler : function() {
                            com.insideaem.recorder.RecorderController.deleteSession(groupValue);
                            store.reload();
                        }
                    });
                    
                    items.push({
                        text : 'Activate',
                        handler : function() {
                            activateChangesInSession(groupValue);
                        }
                    });
                    
                    var menu = new CQ.Ext.menu.Menu({
                        items : items
                    });
                    menu.showAt(event.getXY());
                    event.stopEvent();
                }
            }
        } ];
        com.insideaem.recorder.RecorderPanel.superclass.constructor.call(this, config);
        
    }
});

// Register the widget
CQ.Ext.reg("recorderpanel", com.insideaem.recorder.RecorderPanel);