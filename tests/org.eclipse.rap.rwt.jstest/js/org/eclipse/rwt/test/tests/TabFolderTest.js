/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;

var shell;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.TabFolderTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateTabFolderOnTopByProtocol : function() {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.TabFolder",
        "properties" : {
          "style" : [ "TOP" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.TabFolder );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.getHideFocus() );
      assertTrue( widget.getPlaceBarOnTop() );
    },

    testCreateTabFolderOnBottomByProtocol : function() {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.TabFolder",
        "properties" : {
          "style" : [ "BOTTOM" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.TabFolder );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.getHideFocus() );
      assertFalse( widget.getPlaceBarOnTop() );
    },

    testSetSelectionByProtocol : function() {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.TabFolder",
        "properties" : {
          "style" : [ "TOP" ],
          "parent" : "w2"
        }
      } );
      var item1 = this._createTabItemByProtocol( "w4", "w3" );
      var item2 = this._createTabItemByProtocol( "w5", "w3" );
      var item3 = this._createTabItemByProtocol( "w6", "w3" );
      TestUtil.protocolSet( "w3", { "selection" : "w5" } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertFalse( item1.getChecked() );
      assertTrue( item2.getChecked() );
      assertFalse( item3.getChecked() );
    },

    testSetBoundsByProtocol : function() {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.TabFolder",
        "properties" : {
          "style" : [ "TOP" ],
          "parent" : "w2",
          "bounds" : [ 10, 10, 100, 100 ]
        }
      } );
      TestUtil.flush();

      TestUtil.protocolSet( "w3", { "bounds" : [ 20, 30, 120, 130 ] } );
      TestUtil.flush();

      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var style = widget.getElement().style;
      var paneStyle = widget.getPane().getElement().style;
      var barStyle = widget.getBar().getElement().style;
      assertEquals( "20px", style.left );
      assertEquals( "30px", style.top );
      assertEquals( "120px", style.width );
      assertEquals( "130px", style.height );
    },

    testInternalLayoutChange : function() {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.TabFolder",
        "properties" : {
          "style" : [ "TOP" ],
          "parent" : "w2",
          "bounds" : [ 10, 10, 100, 100 ]
        }
      } );
      this._createTabItemByProtocol( "w4", "w3" );
      TestUtil.flush();

      TestUtil.protocolSet( "w3", { "bounds" : [ 20, 30, 120, 130 ] } );
      TestUtil.flush();

      var gecko = rwt.client.Client.isGecko();
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var paneStyle = widget.getPane().getElement().style;
      var barStyle = widget.getBar().getElement().style;
      var barHeight = parseInt( barStyle.height, 10 );
      assertEquals( "0px", barStyle.left );
      assertEquals( "0px", barStyle.top );
      assertEquals( gecko ? "" : "120px", barStyle.width );
      assertEquals( "0px", paneStyle.left );
      assertEquals( barHeight - 1, parseInt( paneStyle.top, 10 ) );
      assertEquals( gecko ? "" : "120px", paneStyle.width );
      assertEquals( 130 - barHeight + 1, parseInt( paneStyle.height, 10 ) );
    },

    testCreateTabItemByProtocol : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      assertTrue( item instanceof rwt.widgets.TabItem );
      assertIdentical( folder.getBar(), item.getParent() );
      assertNull( item.getUserData( "isControl") );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var page = ObjectManager.getObject( "w4pg" );
      assertTrue( page instanceof rwt.widgets.base.TabFolderPage );
      folder.destroy();
    },

    testDestroyTabItemByProtocol : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var page = ObjectManager.getObject( "w4pg" );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      TestUtil.flush();
      assertTrue( item.isDisposed() );
      assertTrue( page.isDisposed() );
      assertEquals( undefined, ObjectManager.getObject( "w4" ) );
      assertEquals( undefined, ObjectManager.getObject( "w4pg" ) );
      folder.destroy();
    },

    testDestroyChildrenWithFolderByProtocol : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      var ObjectRegistry = rwt.remote.ObjectRegistry;
      var page = ObjectRegistry.getObject( "w4pg" );
      var MessageProcessor = rwt.remote.MessageProcessor;
      MessageProcessor.processOperationArray( [ "create", "w5", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w3"
        }
      ] );
      var control = ObjectRegistry.getObject( "w4" );

      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      TestUtil.flush();

      assertTrue( folder.isDisposed() );
      assertTrue( item.isDisposed() );
      assertTrue( page.isDisposed() );
      assertTrue( control.isDisposed() );
      assertEquals( undefined, ObjectRegistry.getObject( "w3" ) );
      assertEquals( undefined, ObjectRegistry.getObject( "w4" ) );
      assertEquals( undefined, ObjectRegistry.getObject( "w5" ) );
      assertEquals( undefined, ObjectRegistry.getObject( "w4pg" ) );
    },

    testSetTextByProtocol : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "set",
        "properties" : {
          "text" : "foo<>\" bar"
        }
      } );
      assertEquals( "foo&lt;&gt;&quot; bar", item.getLabel().toString() );
      folder.destroy();
    },

    testSetMnemonicIndexByProtocol : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "text" : "test first" } );

      TestUtil.protocolSet( "w4", { "mnemonicIndex" : 6 } );

      assertEquals( 6, item.getMnemonicIndex() );
      folder.destroy();
    },

    testSetTextResetsMnemonicIndex : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "mnemonicIndex" : 6 } );

      TestUtil.protocolSet( "w4", { "text" : "blue" } );

      assertNull( item.getMnemonicIndex() );
      folder.destroy();
    },

    testSetImageByProtocol : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "set",
        "properties" : {
          "image" : [ "image.png", 10, 20 ]
        }
      } );
      assertEquals( "image.png", item.getIcon() );
      folder.destroy();
    },

    testSetControlByProtocol : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      var control =  new rwt.widgets.Button( "push" );
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      widgetManager.add( control, "w5", true, "rwt.widgets.Button" );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "set",
        "properties" : {
          "control" : "w5"
        }
      } );
      assertTrue( control.getParent() instanceof rwt.widgets.base.TabFolderPage );
      assertIdentical( widgetManager.findWidgetById( "w4pg" ), control.getParent() );
      folder.destroy();
    },

    testSetToolTipByProtocol : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "set",
        "properties" : {
          "toolTip" : "hello blue world"
        }
      } );
      assertEquals( "hello blue world", item.getUserData( "toolTipText" ) );
      assertTrue( item.getToolTip() !== null );
      folder.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "set",
        "properties" : {
          "customVariant" : "variant_blue"
        }
      } );
      assertTrue( item.hasState( "variant_blue" ) );
    },

    testSelectedEvent : function() {
      this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      TestUtil.flush();

      item.setChecked( true );

      var message = TestUtil.getMessageObject();
      assertEquals( "w4", message.findNotifyProperty( "w3", "Selection", "item" ) );
    },

    testRenderMnemonic_OnActivate : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      item.setText( "foo" );
      item.setMnemonicIndex( 1 );
      shell.setActive( true );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      assertEquals( "f<span style=\"text-decoration:underline\">o</span>o", item.getLabel() );
    },

    testRenderMnemonic_OnDeactivate : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      item.setText( "foo" );
      item.setMnemonicIndex( 1 );
      shell.setActive( true );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().deactivate();
      TestUtil.flush();

      assertEquals( "foo", item.getLabel() );
    },

    testRenderMnemonic_Trigger : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      item.setText( "foo" );
      item.setMnemonicIndex( 1 );
      shell.setActive( true );
      TestUtil.flush();
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertTrue( item.getChecked() );
    },

    testDestroyTabItemRemovesMnemonicHandler : function() {
      var folder = this._createTabFolderByProtocol( "w3", "w2" );
      var item = this._createTabItemByProtocol( "w4", "w3" );
      item.setText( "foo" );
      item.setMnemonicIndex( 1 );
      shell.setActive( true );
      TestUtil.flush();
      var success = false;

      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      TestUtil.flush();
      assertTrue( item.isDisposed() );
      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertFalse( success );
    },

    //////////////////
    // Helping methods

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
    },

    tearDown : function() {
      shell.destroy();
      shell = null;
    },

    _createTabFolderByProtocol : function( id, parentId ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.TabFolder",
        "properties" : {
          "style" : [ "TOP" ],
          "parent" : parentId
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    _createTabItemByProtocol : function( id, parentId ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.TabItem",
        "properties" : {
          "style" : [],
          "id" : id,
          "parent" : parentId,
          "index" : 0
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    }

  }

} );

}());
