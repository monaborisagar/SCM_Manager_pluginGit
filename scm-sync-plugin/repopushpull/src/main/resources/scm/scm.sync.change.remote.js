loginCallbacks.push(function() {

	/* getting reference of already extisting components of scm-manager */
	var repoPanel = Ext.getCmp('repositories');
	var toolbar = repoPanel.getTopToolbar();
	var repoGrid = repoPanel.getGrid();

	var scmStore= new Ext.data.Store({
		// load using HTTP
		url : 'api/rest/plugins/scmaccount',

		// the return will be XML, so lets set up a
		// reader
		reader : new Ext.data.XmlReader({
			// records will have an "Item" tag
			record : 'ScmAccount',
			id : 'id',
			totalRecords : '@total'
		}, [
		// set up the fields mapping into the xml doc
		// The first needs mapping, the others are very
		// basic
		'accountName', 
		'id', 
		'password', 
		'username' ]
		)
	});
	
	/* button for remote change/create */

	var changeSyncBtn = new Ext.Button({
		xtype : 'tbbutton',
		id : 'changeSyncBtn',
		text : 'Change/Create Remote',
		/*disabled : true,*/
		handler : function(f) {
			
			console.log("Change/Create button clicked ");
			if (repoGrid.getSelectionModel().hasSelection()) {
				   var row = repoGrid.getSelectionModel().getSelections()[0];
				   console.log(row.get('name'))
				   
				   Ext.Ajax.request({
					   url: 'api/rest/plugins/remotesync/' + row.get('name'),
					   method: 'GET',
					   headers: {'accept': 'application/json'},
					   
					   success: function (response){
						   /*if remote sync is already present then we have to change it*/
						   /*creating update window for remote-setting, already presents*/
						  var remoteObj=JSON.parse(response.responseText);						   
						   scmStore.load();
						   var manageAccountWindow = new Ext.Window({
							   layout : 'fit',
							   width : 500,
							   height : 300,
							   closeAction : 'hide',
							   plain : true,
							   items : 
									   {
										   title : "Change Remote Settings",
										   items : new Ext.form.FormPanel({
												  id:'newRemoteSiteForm',
											      url: 'api/rest/plugins/remotesync/' + remoteObj.id,
											      xtype:'fieldset',
											      columnWidth: 0.5,
											      title: 'Configure Remote Repository',
											      collapsible: true,
											      autoHeight:true,
											      monitorValid: true,
											      frame: true,
											      defaultType: 'textfield',
											      defaults: { allowBlank: false },

											      items: [
											              {
											            	  fieldLabel: 'Remote URL',
											            	  name: 'syncRemoteUrl',
											            	  value: remoteObj.remoteSite
											              }, {
											            	  fieldLabel: 'Repository Name',
											            	  name: 'repositoryName',
											            	  readOnly:true,
											            	  value:remoteObj.repository
											              },{
											            	  fieldLabel: 'Remote Account',
											            	  name : 'remoteaccount',
											            	  xtype: 'combo',
											            	  mode: 'local',
											            	  allowBlank: false,
											            	  triggerAction: 'all',
											            	  forceSelection: true,
											            	  editable: false,
											            	  fieldLabel: 'Choose Account',
											            	  hiddenName: 'remoteaccount',
											            	  displayField: 'accountName',
											            	  valueField: 'id',
											            	  editable: false,
											            	  store: scmStore
											              }
											      ],

											      buttons : [ {
											    	  text : 'Save Account',
											    	  buttonAlign : 'left',
											    	  formBind : true,
											    	  handler : function(btn, evt) {

											    		  var remoteSiteForm = Ext.getCmp('newRemoteSiteForm').form;


								                    		var updatedRemoteSyncXML=
								                    		'<RemoteSyncs>' +
									                    		'<RemoteSync>'+
										                    		'<repository>' + remoteSiteForm.findField('repositoryName').getValue() + '</repository>'+
										                    		'<accountId>'+ remoteSiteForm.findField('remoteaccount').getValue() + '</accountId>' +
										                    		'<remoteSite>'+ remoteSiteForm.findField('syncRemoteUrl').getValue() + '</remoteSite>' +
										                    		'<type>hg</type>' +
									                    		'</RemoteSync>'+
								                    		'</RemoteSyncs>';
								                    		
											    		  console.log("RemoteSync XML : " + updatedRemoteSyncXML);

											    		  Ext.Ajax.request({
											    			  url : remoteSiteForm.url,
											    			  method : 'PUT',
											    			  headers : {
											    				  'Content-Type' : 'application/xml'
											    			  },
											    			  xmlData : updatedRemoteSyncXML,
											    			  success : function() {
											    				  Ext.Msg.show({
											    					  title : 'Success',
											    					  msg : 'Updated Remote Site Config is saved',
											    					  buttons : Ext.MessageBox.OK,
											    					  icon : Ext.MessageBox.INFO
											    				  });

											    			  },
											    			  failure : function() {
											    				  Ext.Msg.show({
											    					  title : 'Failure',
											    					  msg : 'On updation of Remote Configuration, Some error has been made, please try again',
											    					  buttons : Ext.MessageBox.OK,
											    					  icon : Ext.MessageBox.ERROR
											    				  });

											    			  }
											    		  });

											    	  }
											      } ]
											  })
									   }
						   });
						   /*var accountCombo=Ext.getCmp('remoteaccount');
						   accountCombo.setValue(remoteObj.accountId);*/
						   manageAccountWindow.show();
						   
						   
					   },
					   failure:function(){
						   /*if remote sync is not found, then we have to create new remote setting*/
						   /*creating new remote setting window for already present repository*/
						   scmStore.load();
						   var newRemoteWindow = new Ext.Window({
							   layout : 'fit',
							   width : 500,
							   height : 300,
							   closeAction : 'hide',
							   plain : true,
							   items :  
									   {
										   title : "Create New Remote Settings",
										   items : new Ext.form.FormPanel({
												  id:'newRemoteSiteForm',
											      url: 'api/rest/plugins/remotesync',
											      xtype:'fieldset',
											      columnWidth: 0.5,
											      title: 'Configure Remote Repository',
											      collapsible: true,
											      autoHeight:true,
											      monitorValid: true,
											      frame: true,
											      defaultType: 'textfield',
											      defaults: { allowBlank: false },

											      items: [
											              {
											            	  fieldLabel: 'Remote URL',
											            	  name: 'syncRemoteUrl',
											              }, {
											            	  fieldLabel: 'Repository Name',
											            	  name: 'repositoryName',
											            	  value: row.get('name'),
											            	  readOnly:true,
											              },{
											            	  fieldLabel: 'Remote Account',
											            	  name : 'remoteaccount',
											            	  xtype: 'combo',
											            	  mode: 'local',
											            	  allowBlank: false,
											            	  triggerAction: 'all',
											            	  forceSelection: true,
											            	  editable: false,
											            	  fieldLabel: 'Choose Account',
											            	  hiddenName: 'remoteaccount',
											            	  displayField: 'accountName',
											            	  valueField: 'id',
											            	  editable: false,
											            	  store: scmStore
											              }
											      ],

											      buttons : [ {
											    	  text : 'Save Account',
											    	  buttonAlign : 'left',
											    	  formBind : true,
											    	  handler : function(btn, evt) {

											    		  var remoteSiteForm = Ext.getCmp('newRemoteSiteForm').form;


								                    		var updatedRemoteSyncXML=
								                    		'<RemoteSyncs>' +
									                    		'<RemoteSync>'+
										                    		'<repository>' + remoteSiteForm.findField('repositoryName').getValue() + '</repository>'+
										                    		'<accountId>'+ remoteSiteForm.findField('remoteaccount').getValue() + '</accountId>' +
										                    		'<remoteSite>'+ remoteSiteForm.findField('syncRemoteUrl').getValue() + '</remoteSite>' +
										                    		'<type>hg</type>' +
									                    		'</RemoteSync>'+
								                    		'</RemoteSyncs>';
								                    		
											    		  console.log("RemoteSync XML : " + updatedRemoteSyncXML);

											    		  Ext.Ajax.request({
											    			  url : remoteSiteForm.url,
											    			  method : 'POST',
											    			  headers : {
											    				  'Content-Type' : 'application/xml'
											    			  },
											    			  xmlData : updatedRemoteSyncXML,
											    			  success : function() {
											    				  Ext.Msg.show({
											    					  title : 'Success',
											    					  msg : 'New Remote Site Config is saved',
											    					  buttons : Ext.MessageBox.OK,
											    					  icon : Ext.MessageBox.INFO
											    				  });

											    			  },
											    			  failure : function() {
											    				  Ext.Msg.show({
											    					  title : 'Failure',
											    					  msg : 'On Creating New Remote Configuration, Some error has been made, please try again',
											    					  buttons : Ext.MessageBox.OK,
											    					  icon : Ext.MessageBox.ERROR
											    				  });

											    			  }
											    		  });

											    	  }
											      } ]
											  })
									   }
						   });
						   newRemoteWindow.show();
					   }
				   });
				   
			}else{
				Ext.Msg.show({
					title: 'Select Repository',
					msg: 'To Create/Change Remote Setting , you have to first select the repository from grid, Select the Repository which you want to Create/Change configuration Remote',
					buttons: Ext.MessageBox.OK,
					icon: Ext.MessageBox.INFO
				});
			}
			
			
		}
	});

	/* adding items to the existing toolbar (of repository tab) */
	toolbar.addSeparator();
	toolbar.addButton(changeSyncBtn);
	
	/*
	 * changing state when clicking on repository-row, now we have selected the
	 * repository , we can enable the change setting for repository button on
	 */

	/*repoGrid.addListener('rowclick',function(f){
		changeSyncBtn.enable();
	});*/

});