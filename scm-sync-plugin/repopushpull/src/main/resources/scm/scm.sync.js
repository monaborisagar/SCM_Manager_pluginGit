  

function sayHello(){
	
	Ext.Ajax.request({
		url: restUrl + 'sample/hello',
		method: 'GET',
		disableCaching: true,
		success: function(response){
			var msg = response.responseText;
			Ext.Msg.show({
				title: 'Hello Message',
				msg: msg,
				buttons: Ext.MessageBox.OK,
				icon: Ext.MessageBox.INFO
			});
		},
		failure: function(){
			Ext.Msg.show({
				title: 'Error',
				msg: 'Could not display the hello message',
				buttons: Ext.MessageBox.OK,
				icon: Ext.MessageBox.ERROR
			});
		}
	});
}



loginCallbacks.push(function(){
	
	var repoPanel=Ext.getCmp('repositories');
	var toolbar=repoPanel.getTopToolbar();
	var repoGrid=repoPanel.getGrid();
	
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
	scmStore.load();
	
	/*
	 * window to show onclick of clone repository , to get the new remote site
	 * and credentials for that remote-site
	 */
	var cloneWindowForm = new Ext.Window({
        layout: 'fit',
        width: 500,
        height: 300,
        closeAction: 'hide',
        plain: true,

        items: 
        	new Ext.FormPanel({
                labelWidth: 75,
                id:'cloneRepoRemoteForm',
                url: 'api/rest/plugins/remotesyncclone',
                frame: true,
                title: 'Clone Mercurial Repository',
                bodyStyle: 'padding:5px 5px 0',
                width: 350,

                items: [{
                  
                    xtype: 'fieldset',
                    title: 'Remote Site',
                    collapsible: true,
                    autoHeight: true,
                    defaults: {
                        width: 210
                    },
                    defaultType: 'textfield',
                    items: [{
                        fieldLabel: 'Remote URL',
                        name: 'cloneRemoteUrl',
                        value: 'https://'
                    }, {
                        fieldLabel: 'Repository Name',
                        name: 'repositoryName'
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
                    ]
                }],

                buttons: [{
                    	text: 'Save',
                    	formBind: true,
                    	handler: function (btn, evt) { 

                    		var cloneRemoteForm=Ext.getCmp('cloneRepoRemoteForm').form;

                    		var cloneRemoteXML=
                    		'<RemoteSyncs>' +
	                    		'<RemoteSync>'+
		                    		'<repository>' + cloneRemoteForm.findField('repositoryName').getValue() + '</repository>'+
		                    		'<accountId>'+ cloneRemoteForm.findField('remoteaccount').getValue() + '</accountId>' +
		                    		'<remoteSite>'+ cloneRemoteForm.findField('cloneRemoteUrl').getValue() + '</remoteSite>' +
		                    		'<type>hg</type>' +
	                    		'</RemoteSync>'+
                    		'</RemoteSyncs>';

                    		console.log("CloneRemote XML : " + cloneRemoteXML);

                    		Ext.Ajax.request({
                    			url: cloneRemoteForm.url,
                    			method: 'POST',
                    			headers: {'Content-Type': 'application/xml'},
                    			xmlData:cloneRemoteXML,
                    			success: function (){
                    				
                    				/*import repository called, to import automatically after cloning succeed*/
                    				cloneWindowForm.hide();                    				
                    				Ext.Ajax.request({
                    					url:'api/rest/import/repositories/hg.json',
                    					method:'POST',
                    					success:function(){
                    						Ext.Msg.show({
                    							title: 'Success',
                    							msg: 'Cloning completed successfully, and imported into SCM-MANAGER',
                    							buttons: Ext.MessageBox.OK,
                    							icon: Ext.MessageBox.INFO
                    						});
                    					},
                    					failure:function(){
                    						Ext.Msg.show({
                    							title: 'Failure',
                    							msg: 'Cloning Mercurial from remote is completed but, importing into SCM-MANAGER registry is failed, try to import it manually..',
                    							buttons: Ext.MessageBox.OK,
                    							icon: Ext.MessageBox.ERROR
                    						});
                    					}
                    					
                    				});
                    			},
                    			failure:function(){
                    				cloneWindowForm.hide();
                    				Ext.Msg.show({
                    					title: 'Failure',
                    					msg: 'Cloning remote repository cause errors, please try again',
                    					buttons: Ext.MessageBox.OK,
                    					icon: Ext.MessageBox.ERROR
                    				});

                    			}
                    		});
                	}
                },
                {
                	text: 'Cancel',
                	handler:function(){
                		cloneWindowForm.hide();
                	}
                }
                ]	
        	})
	});
			


						/* creating button for repository toolbar */
			var cloneRepoBtn = new Ext.Button({
				xtype : 'tbbutton',
				id : 'cloneRepoBtn',
				text : 'Clone Mercurial',
				handler : function(f) {
					cloneWindowForm.show();
				}
			});

			var pushRepoBtn = new Ext.Button(
					{
						xtype : 'tbbutton',
						id : 'tempRepoButton',
						text : 'Push',

						handler : function() {
							if (repoGrid.getSelectionModel().hasSelection()) {
								var row = repoGrid.getSelectionModel()
										.getSelections()[0];
								if (row) {
									var branchStore = new Sonia.rest.JsonStore(
											{
												proxy : new Ext.data.HttpProxy(
														{
															url : 'api/rest/repositories/'
																	+ row.id
																	+ '/branches.json',
															method : 'GET',
															disableCaching : false
														}),
												root : 'branch',
												idProperty : 'name',
												fields : [ 'name' ],
												sortInfo : {
													field : 'name'
												}
											});

									var pushParameterWindow = new Ext.Window({
										title : 'Push Commits',
										layout : 'form',
										width : 350,
										height : 150,
										border : false,

										items : [ {
											region : 'north',
											fieldLabel : 'Forced Push',
											name : 'isForcedPush',
											id : 'isForcedPush',
											xtype : 'checkbox'
										}, new Ext.form.ComboBox({
											region : 'center',
											name : 'Branch',
											id : 'Branch',
											fieldLabel : 'Branch',
											hiddenName : 'Branch',
											valueField : 'name',
											displayField : 'name',
											typeAhead : false,
											editable : false,
											triggerAction : 'all',
											store : branchStore,
											listeners : {
												select : {
													fn : this.selectBranch,
													scope : this
												}
											}
										}) ],

										buttons : [ {
											text : "Cancel",
											handler : function() {
												pushParameterWindow.close();}
										}, {
											text : "Push",
											handler : function() {
												var row = null;
												if (repoGrid.getSelectionModel().hasSelection()) {
													    row = repoGrid.getSelectionModel().getSelections()[0];
												}
												
												   Ext.Ajax.request({
													   url: 'api/rest/plugins/remotesyncpush/' + row.get('name') +'/'+Ext.getCmp('isForcedPush').getValue()+'/'+ Ext.getCmp('Branch').getValue(),
													   method: 'POST',
													   headers: {'Content-Type': 'application/xml'},
													   success: function (response){
														   Ext.Msg.show({
															   title: 'Success',
															   msg: Ext.decode(response.responseText),
															   buttons: Ext.MessageBox.OK,
															   icon: Ext.MessageBox.INFO
														   });

													   },
													   failure:function(response){
														   Ext.Msg.show({
															   title: 'Failure',
															   msg: Ext.decode(response.responseText),
															   buttons: Ext.MessageBox.OK,
															   icon: Ext.MessageBox.ERROR
														   });

													   }
												   });	
												
											}
										} ]

									});

									pushParameterWindow.show();
								} else {
									Ext.Msg
											.show({
												title : 'Select Repository',
												msg : 'To Do Push operation, you have to first select the repository from grid, Select the Repository which you want to Push on configured Remote',
												buttons : Ext.MessageBox.OK,
												icon : Ext.MessageBox.INFO
											});
								}
							} else {
								Ext.Msg
										.show({
											title : 'Select Repository',
											msg : 'To Do Push operation, you have to first select the repository from grid, Select the Repository which you want to Push on configured Remote',
											buttons : Ext.MessageBox.OK,
											icon : Ext.MessageBox.INFO
										});

							}
						}

					}); 
		
//	});
	
	
/*	var pushRepoBtn=new Ext.Button({
		xtype:'tbbutton',
		id:'pushRepoBtn',
		text:'Push',
		handler:function(f){
			console.log("push button clicked ");

			if (repoGrid.getSelectionModel().hasSelection()) {
				   var row = repoGrid.getSelectionModel().getSelections()[0];
				   console.log(row.get('name'))

				   
				   Ext.Ajax.request({
					   url: 'api/rest/plugins/remotesyncpush/' + row.get('name'),
					   method: 'POST',
					   headers: {'Content-Type': 'application/xml'},
					   success: function (response){
						   Ext.Msg.show({
							   title: 'Success',
							   msg: Ext.decode(response.responseText),
							   buttons: Ext.MessageBox.OK,
							   icon: Ext.MessageBox.INFO
						   });

					   },
					   failure:function(response){
						   Ext.Msg.show({
							   title: 'Failure',
							   msg: Ext.decode(response.responseText),
							   buttons: Ext.MessageBox.OK,
							   icon: Ext.MessageBox.ERROR
						   });

					   }
				   });
				   
			}else{
				Ext.Msg.show({
					title: 'Select Repository',
					msg: 'To Do Push operation, you have to first select the repository from grid, Select the Repository which you want to Push on configured Remote',
					buttons: Ext.MessageBox.OK,
					icon: Ext.MessageBox.INFO
				});
			}
		}
	});*/
	
	var pullRepoBtn=new Ext.Button({
		xtype:'tbbutton',
		id:'pullRepoBtn',
		text:'Pull',
		handler:function(f){
			console.log("pull button clicked ");
			if (repoGrid.getSelectionModel().hasSelection()) {
				var row = repoGrid.getSelectionModel().getSelections()[0];
				console.log(row.get('name'));
				
				Ext.Ajax.request({
					url: 'api/rest/plugins/remotesyncpull/' + row.get('name'),
					method: 'POST',
					headers: {'Content-Type': 'application/xml'},
					success: function (response){
						
						Ext.Msg.show({
							title: 'Success',
							msg: Ext.decode(response.responseText),
							buttons: Ext.MessageBox.OK,
							icon: Ext.MessageBox.INFO
						});

					},
					failure:function(response){
						Ext.Msg.show({
							title: 'Failure',
							msg: Ext.decode(response.responseText),
							buttons: Ext.MessageBox.OK,
							icon: Ext.MessageBox.ERROR
						});

					}
				});
				
			}else{
				Ext.Msg.show({
					title: 'Select Repository',
					msg: 'To Do Pull operation, you have to first select the repository from grid, Select the Repository which you want to Pull from the configured Remote',
					buttons: Ext.MessageBox.OK,
					icon: Ext.MessageBox.INFO
				});
			}
		}
	});
	
	/* adding items to the existing toolbar */
	toolbar.addSeparator();
	toolbar.addButton(cloneRepoBtn);
	toolbar.addSeparator();
	toolbar.addButton(pushRepoBtn);
	toolbar.addSeparator();
	toolbar.addButton(pullRepoBtn);
	

	
  var tabPanel = Ext.getCmp('mainTabPanel');
  
 /* creating form, to set repository remote setting */
  
  Ext.QuickTips.init();
  // turn on validation errors beside the field globally
  Ext.form.Field.prototype.msgTarget = 'side';

  var remoteForm = new Ext.form.FormPanel({
	  id:'newRemoteSiteForm',
      url: 'api/rest/plugins/remotesync',
      xtype:'fieldset',
      columnWidth: 0.5,
      title: 'Configure Remote Repository',
      collapsible: true,
      autoHeight:true,
      monitorValid: true,
      frame: true, 
      width: 250,
      defaultType: 'textfield',
      defaults: { allowBlank: false },

      items: [
          { fieldLabel: 'Local Repository', name: 'localRepoName' },
          { fieldLabel: 'Repository Type', name: 'repoType' },
          { 
        	  xtype:'textfield',
        	  fieldLabel: 'URL', 
        	  name: 'remoteUrl',
        	  listeners: {
        		    'change': function(){
        		      alert('you changed the text of this input field');
        		    }
        	}
        	  
          },
          { fieldLabel: 'Username', name: 'username'},
          { fieldLabel: 'Password', name: 'password', inputType: 'password'}
      ],

      buttons: [
          {
              text: 'Save Remote Settings',
              buttonAlign:'left',
              formBind: true,
              handler: function (btn, evt) { 

            	  var remoteSiteForm=Ext.getCmp('newRemoteSiteForm').form;
            	  
            	  var newRemoteSyncXML='<RemoteSyncs>' +
            	  						'<RemoteSync>'+
            	  							'<repository>' + remoteSiteForm.findField('localRepoName').getValue() + '</repository>'+
            	  							'<username>'+ remoteSiteForm.findField('username').getValue() + '</username>'+
            	  							'<password>'+ remoteSiteForm.findField('password').getValue() + '</password>' +
            	  							'<remoteSite>'+ remoteSiteForm.findField('remoteUrl').getValue() + '</remoteSite>' +
            	  							'<type>'+ remoteSiteForm.findField('repoType').getValue() + '</type>' +
            	  						'</RemoteSync>'+
            	  						'</RemoteSyncs>';
            	  
            	  console.log("RemoteSync XML : " + newRemoteSyncXML);
            	  
            	  Ext.Ajax.request({
                      url: remoteSiteForm.url,
                      method: 'POST',
                      headers: {'Content-Type': 'application/xml'},
                      xmlData:newRemoteSyncXML,
                      success: function (){
                    	  Ext.Msg.show({
              				title: 'Success',
              				msg: 'New Remote Site Config is saved',
              				buttons: Ext.MessageBox.OK,
              				icon: Ext.MessageBox.INFO
              			});
                    	  
                      },
                      failure:function(){
                    	  Ext.Msg.show({
                    		  title: 'Failure',
                    		  msg: 'Some error has been made, please try again',
                    		  buttons: Ext.MessageBox.OK,
                    		  icon: Ext.MessageBox.ERROR
                    	  });
                    	  
                      }
                  });
            	  
              }
          }
      ]
  });
  
  var store = new Ext.data.Store({
      // load using HTTP
      url: 'api/rest/plugins/remotesync',

      // the return will be XML, so lets set up a reader
      reader: new Ext.data.XmlReader({
          // records will have an "Item" tag
          record: 'RemoteSync',
          id: 'id',
          totalRecords: '@total'
      }, [
	      // set up the fields mapping into the xml doc
	      // The first needs mapping, the others are very basic
	      'id',
	      'repository',
	      'username', 
	      'password', 
	      'remoteSite',
	      'type'
	     ]
      )
  });
  
  // create the grid
	  var syncGridPanel = new Ext.grid.GridPanel({
		  title:"Repository Sync",
	      store: store,
	      
	      colModel: new Ext.grid.ColumnModel({
	          defaults: {
	              width: 100,
	              sortable: true
	          },
	          columns: [{
	        	  header: "Repository",
	        	  dataIndex: 'repository',
	        	  width:100,
	        	  sortable: true
	          }, {
	        	  header: "Remote Username",
	        	  dataIndex: 'username',
	        	  width:100,
	        	  sortable: true
	          }, {
	        	  header: "Remote Password",
	        	  dataIndex: 'password',
	        	  width:100,
	        	  sortable: true
	          }, {
	        	  header: "Remote Site",
	        	  dataIndex: 'remoteSite',
	        	  width:100,
	        	  sortable: true
	          },{
	        	  header: "Sync It",
	        	  xtype:"actioncolumn",
	        	  width:100,
	        	  items:[
	        	         {
	        	        	 icon:'plugins/scm/images/arrow-up.png',
	        	        	 tooltip:'PUSH at Site',
	        	        	 handler:function(grid,rowIndex,colIndex){
	        	        		 var rec=grid.getStore().getAt(rowIndex);
	        	        		 
	        	        		 Ext.Ajax.request({
	        	                      url: 'api/rest/plugins/remotesyncpush/' + rec.get('id'),
	        	                      method: 'POST',
	        	                      headers: {'Content-Type': 'application/xml'},
	        	                      success: function (){
	        	                    	  Ext.Msg.show({
	        	              				title: 'Success',
	        	              				msg: 'Remote PUSH Operation Successfully Completed',
	        	              				buttons: Ext.MessageBox.OK,
	        	              				icon: Ext.MessageBox.INFO
	        	              			});
	        	                    	  
	        	                      },
	        	                      failure:function(){
	        	                    	  Ext.Msg.show({
	        	                    		  title: 'Failure',
	        	                    		  msg: 'Some error has been made, please try again',
	        	                    		  buttons: Ext.MessageBox.OK,
	        	                    		  icon: Ext.MessageBox.ERROR
	        	                    	  });
	        	                    	  
	        	                      }
	        	                  });
	        	        		 
	        	        	 }
	        	         },
	        	         {
	        	        	 icon:'plugins/scm/images/arrow-down.png',
	        	        	 tooltip:'PULL at Site',
	        	        	 handler:function(grid,rowIndex,colIndex){
	        	        		 var rec=grid.getStore().getAt(rowIndex);
	        	        		 
	        	        		 
	        	        		 Ext.Ajax.request({
	        	        			 url: 'api/rest/plugins/remotesyncpull/' + rec.get('id'),
	        	        			 method: 'POST',
	        	        			 headers: {'Content-Type': 'application/xml'},
	        	        			 success: function (){
	        	        				 Ext.Msg.show({
	        	        					 title: 'Success',
	        	        					 msg: 'Remote PULL Operation Successfully Completed',
	        	        					 buttons: Ext.MessageBox.OK,
	        	        					 icon: Ext.MessageBox.INFO
	        	        				 });

	        	        			 },
	        	        			 failure:function(){
	        	        				 Ext.Msg.show({
	        	        					 title: 'Failure',
	        	        					 msg: 'Some error has been made, please try again',
	        	        					 buttons: Ext.MessageBox.OK,
	        	        					 icon: Ext.MessageBox.ERROR
	        	        				 });

	        	        			 }
	        	        		 });
	        	        	 }
	        	         }

	        	         ]
	          }
	          ]
	          
	          
	      })
	      
	  });
	   
	  
 
	  store.load();
  
  
  /* adding both container in tab in tabPanel after RepositoryTab */ 
  tabPanel.insert(1,{
	  title:"Remote Sync",
	  xtype:"tabpanel",
	  activeTab:0,
	  items:[
		         syncGridPanel,
		         remoteForm
	         ]
  		}
  	);
   
});