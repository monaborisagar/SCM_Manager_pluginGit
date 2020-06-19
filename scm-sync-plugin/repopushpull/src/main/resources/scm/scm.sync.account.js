loginCallbacks.push(function() {

	/* getting reference of already extisting components of scm-manager */
	var repoPanel = Ext.getCmp('repositories');
	var toolbar = repoPanel.getTopToolbar();
	var repoGrid = repoPanel.getGrid();

	var accountFormItem = new Ext.form.FormPanel({
		id : 'newAccountForm',
		url : 'api/rest/plugins/scmaccount',
		xtype : 'fieldset',
		columnWidth : 0.5,
		title : 'Create Account',
		collapsible : true,
		autoHeight : true,
		monitorValid : true,
		frame : true,
		width : 400,
		defaultType : 'textfield',
		defaults : {
			allowBlank : false
		},

		items : [ {
			fieldLabel : 'Account Name',
			name : 'accountname'
		}, {
			fieldLabel : 'Username',
			name : 'username'
		}, {
			fieldLabel : 'Password',
			name : 'password',
			inputType : 'password'
		} ],

		buttons : [ {
			text : 'Save Account',
			buttonAlign : 'left',
			formBind : true,
			handler : function(btn, evt) {

				var accountForm = Ext.getCmp('newAccountForm').form;

				var newScmAccountXML = '<ScmAccounts>' + '<ScmAccount>'

				+ '<accountName>'
						+ accountForm.findField('accountname').getValue()
						+ '</accountName>' + '<username>'
						+ accountForm.findField('username').getValue()
						+ '</username>' + '<password>'
						+ accountForm.findField('password').getValue()
						+ '</password>' + '</ScmAccount>' + '</ScmAccounts>';

				console.log("ScmAccount XML : " + newScmAccountXML);

				Ext.Ajax.request({
					url : accountForm.url,
					method : 'POST',
					headers : {
						'Content-Type' : 'application/xml'
					},
					xmlData : newScmAccountXML,
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
							msg : 'Some error has been made, please try again',
							buttons : Ext.MessageBox.OK,
							icon : Ext.MessageBox.ERROR
						});

					}
				});

			}
		} ]
	});
	
	

	/* creating button for manage user-account for remote-site */
	var manageAccountBtn = new Ext.Button({
		xtype : 'tbbutton',
		id : 'manageAccountBtn',
		text : 'Manage Remote Account',
		handler : function(f) {
			
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
				'username' 
				]
				)
			});
			
			scmStore.load();
			
			var manageAccountWindow = new Ext.Window({
				layout : 'fit',
				width : 500,
				height : 300,
				closeAction : 'hide',
				plain : true,
				items : new Ext.TabPanel({
					autoTabs : true,
					activeTab : 0,
					deferredRender : false,
					border : false,
					items : [ {
						title : "Accounts",
						items : new Ext.grid.GridPanel({
							title:"Remote Accounts",
							store : scmStore,
							autoHeight:true,
							colModel : new Ext.grid.ColumnModel({
								defaults : {
									sortable : true
								},
								columns : [ {
									header : "Account Name",
									dataIndex : 'accountName',
								}, {
									header : "Username",
									dataIndex : 'username',
								}, {
									header : "Password",
									dataIndex : 'password',
								},{
									header : "Remove Account",
									xtype:"actioncolumn",
									items:[
									       {
									    	   icon:'resources/images/delete.png',
									    	   tooltip:'Remove User',
									    	   handler:function(grid,rowIndex,colIndex){
									    		   var rec=grid.getStore().getAt(rowIndex);

									    		   Ext.Ajax.request({
									    			   url: 'api/rest/plugins/scmaccount/' + rec.get('id'),
									    			   method: 'DELETE',
									    			   headers: {'Content-Type': 'application/xml'},
									    			   success: function (){
									    				   Ext.Msg.show({
									    					   title: 'Success',
									    					   msg: 'Remote Account Successfully Deleted',
									    					   buttons: Ext.MessageBox.OK,
									    					   icon: Ext.MessageBox.INFO
									    				   });

									    			   },
									    			   failure:function(){
									    				   Ext.Msg.show({
									    					   title: 'Failure',
									    					   msg: 'On Remote Account Delete, Some error has been made, please try again',
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

						})
					}, {
						title : "Add New Accounts",
						items : accountFormItem

					}

					]
				})
			
			});
			manageAccountWindow.show();
		}
	});
	/* adding items to the existing toolbar (of repository tab) */
	toolbar.addSeparator();
	toolbar.addButton(manageAccountBtn);

});