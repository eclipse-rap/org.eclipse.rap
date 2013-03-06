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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.CTabFolderTest", {
  extend : rwt.qx.Object,

  members : {

    testCreateCTabFolderOnTopByProtocol : function() {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CTabFolder",
        "properties" : {
          "style" : [ "TOP" ],
          "parent" : "w2",
          "toolTipTexts" : [ "a", "b", "c", "d", "e" ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.CTabFolder );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "top", widget.getTabPosition() );
      assertEquals( "a", rwt.widgets.CTabFolder.MIN_TOOLTIP );
      assertEquals( "b", rwt.widgets.CTabFolder.MAX_TOOLTIP );
      assertEquals( "c", rwt.widgets.CTabFolder.RESTORE_TOOLTIP );
      assertEquals( "d", rwt.widgets.CTabFolder.CHEVRON_TOOLTIP );
      assertEquals( "e", rwt.widgets.CTabFolder.CLOSE_TOOLTIP );
      widget.destroy();
    },

    testCreateCTabFolderOnBottomByProtocol : function() {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.CTabFolder",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "toolTipTexts": [ "Minimize", "Maximize", "Restore", "Show List", "Close" ],
          "tabPosition" : "bottom"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.CTabFolder );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "bottom", widget.getTabPosition() );
      widget.destroy();
    },

    testDestroyByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      TestUtil.flush();
      assertTrue( widget.isDisposed() );
    },

    testDestroyWithChildrenByProtocol : function() {
      var ObjectRegistry = rwt.remote.ObjectRegistry;
      var MessageProcessor = rwt.remote.MessageProcessor;
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      var processor = rwt.remote.MessageProcessor;
      var item = this._createCTabItemByProtocol( "w4", "w3" );
      MessageProcessor.processOperationArray( [ "create", "w5", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w3"
        }
      ] );
      var control = ObjectRegistry.getObject( "w5" );

      processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      TestUtil.flush();

      assertTrue( widget.isDisposed() );
      assertTrue( item.isDisposed() );
      assertTrue( control.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( ObjectRegistry.getObject( "w4" ) == null );
      assertTrue( ObjectRegistry.getObject( "w5" ) == null );
    },

    testSetTabPositionByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "tabPosition" : "bottom" } );
      assertEquals( "bottom", widget.getTabPosition() );
      widget.destroy();
    },

    testSetTabHeightByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "tabHeight" : 30 } );
      assertEquals( 30, widget._tabHeight );
      widget.destroy();
    },

    testSetMinMaxStateByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "minMaxState" : "min" } );
      assertEquals( "min", widget._minMaxState );
      widget.destroy();
    },

    testSetMinimizeBoundsByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "minimizeBounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( [ 1, 2, 3, 4 ], widget._minButtonBounds );
      widget.destroy();
    },

    testSetMaximizeBoundsByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "maximizeBounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( [ 1, 2, 3, 4 ], widget._maxButtonBounds );
      widget.destroy();
    },

    testSetChevronBoundsByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "chevronBounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( [ 1, 2, 3, 4 ], widget._chevronBounds );
      widget.destroy();
    },

    testSetMinimizeVisibleByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "minimizeVisible" : true } );
      assertTrue( widget._minButton.getVisibility() );
      widget.destroy();
    },

    testSetMaximizeVisibleByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "maximizeVisible" : true } );
      assertTrue( widget._maxButton.getVisibility() );
      widget.destroy();
    },

    testSetChevronVisibleByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "chevronVisible" : true } );
      assertTrue( widget._chevron.getVisibility() );
      widget.destroy();
    },

    testSetUnselectedCloseVisibleByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "unselectedCloseVisible" : false } );
      assertFalse( widget.getUnselectedCloseVisible() );
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      var item = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w3", { "selection" : "w4" } );
      assertTrue( item.isSelected() );
      widget.destroy();
      item.destroy();
    },

    testSetSelectionBackgroundByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "selectionBackground" : [ 0, 0, 255, 255 ] } );
      assertEquals( "rgb(0,0,255)", widget.getSelectionBackground() );
      widget.destroy();
    },

    testSetSelectionForegroundByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "selectionForeground" : [ 0, 0, 255, 255 ] } );
      assertEquals( "rgb(0,0,255)", widget.getSelectionForeground() );
      widget.destroy();
    },

    testSetSelectionBackgroundImageByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "selectionBackgroundImage" : [ "image.gif", 10, 20 ] } );
      assertEquals( [ "image.gif", 10, 20 ], widget.getSelectionBackgroundImage() );
      widget.destroy();
    },

    testSetSelectionBackgroundGradientByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      var gradient
        = [ [ [ 0, 0, 255, 255 ], [ 0, 255, 0, 255 ], [ 0, 0, 255, 255 ] ], [ 0, 50, 100 ], false ];
      TestUtil.protocolSet( "w3", { "selectionBackgroundGradient" : gradient } );
      var actual = widget.getSelectionBackgroundGradient();
      assertEquals( 3, gradient.length );
      assertEquals( [ 0, "rgb(0,0,255)" ], actual[ 0 ] );
      assertEquals( [ 0.5, "rgb(0,255,0)" ], actual[ 1] );
      assertEquals( [ 1, "rgb(0,0,255)" ], actual[ 2] );
      assertFalse( actual.horizontal === false );
      widget.destroy();
    },

    testSetBorderVisibleByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "borderVisible" : true } );
      assertTrue( widget.hasState( "rwt_BORDER" ) );
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      assertTrue( widget._hasSelectionListener );
      widget.destroy();
    },

    testSetHasDefaultSelectionListenerByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolListen( "w3", { "DefaultSelection" : true } );
      assertTrue( widget._hasDefaultSelectionListener );
      widget.destroy();
    },

    testSetHasFolderListenerByProtocol : function() {
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      assertTrue( widget._hasFolderListener );
      widget.destroy();
    },

    testCreateCTabItemByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.CTabItem",
        "properties" : {
          "style" : [],
          "parent" : "w3",
          "index" : 0
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w4" );
      assertTrue( widget instanceof rwt.widgets.CTabItem );
      assertIdentical( folder, widget.getParent() );
      assertFalse( widget._canClose );
      folder.destroy();
      widget.destroy();
    },

    testCreateCTabItemWithCloseByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      folder.addState( "rwt_CLOSE" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.CTabItem",
        "properties" : {
          "style" : [],
          "parent" : "w3",
          "index" : 0
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w4" );
      assertTrue( widget._canClose );
      folder.destroy();
      widget.destroy();
    },

    testSetItemBoundsByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      folder.setTabPosition( "bottom" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "bounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( 1, widget.getLeft() );
      assertEquals( 1, widget.getTop() );
      assertEquals( 3, widget.getWidth() );
      assertEquals( 5, widget.getHeight() );
      folder.destroy();
      widget.destroy();
    },

    testSetItemFontByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "font" : [ ["Arial"], 20, true, false ] } );
      assertEquals( "bold 20px Arial", widget.getFont().toCss() );
      folder.destroy();
      widget.destroy();
    },

    testSetItemTextByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "text" : "foo<>bar" } );
      assertEquals( "foo&lt;&gt;bar", widget.getLabel() );
      folder.destroy();
      widget.destroy();
    },

    testSetMnemonicIndexByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var item = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "text" : "test first" } );

      TestUtil.protocolSet( "w4", { "mnemonicIndex" : 6 } );

      assertEquals( 6, item.getMnemonicIndex() );
      folder.destroy();
    },

    testSetTextResetsMnemonicIndex : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var item = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "mnemonicIndex" : 6 } );

      TestUtil.protocolSet( "w4", { "text" : "test first" } );

      assertNull( item.getMnemonicIndex() );
      folder.destroy();
    },

    testSetItemImageByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "image" : [ "image.gif", 10, 20 ] } );
      assertEquals( "image.gif", widget.getIcon() );
      folder.destroy();
      widget.destroy();
    },

    testSetItemToolTipByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "toolTip" : "hello blue world" } );
      assertEquals( "hello blue world", widget.getUserData( "toolTipText" ) );
      assertTrue( widget.getToolTip() !== null );
      folder.destroy();
      widget.destroy();
    },

    testSetItemCustomVariantByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "customVariant" : "variant_blue" } );
      assertTrue( widget.hasState( "variant_blue" ) );
      folder.destroy();
      widget.destroy();
    },

    testSetItemShowingByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "showing" : false } );
      assertFalse( widget.getVisibility() );
      folder.destroy();
      widget.destroy();
    },

    testSetItemShowCloseByProtocol : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "showClose" : true } );
      assertTrue( widget._showClose );
      folder.destroy();
      widget.destroy();
    },

    testSetBackgroundImage : function() {
      var folder = new rwt.widgets.CTabFolder();
      folder.setUserData( "backgroundImageSize", [ 50, 50 ] );
      folder.setBackgroundImage( "bla.jpg" );
      assertEquals( "bla.jpg", folder._body.getBackgroundImage() );
      assertEquals( [ 50, 50 ], folder._body.getUserData( "backgroundImageSize" ) );
    },

    testSetSelectionBackgroundImage : function() {
      var folder = new rwt.widgets.CTabFolder();
      folder.setSelectionBackgroundImage( [ "bla.jpg", 50, 50 ] );
      var item = new rwt.widgets.CTabItem( folder, false );
      assertFalse( "bla.jpg" == item.getBackgroundImage() );
      item.setSelected( true );
      assertEquals( "bla.jpg", item.getBackgroundImage() );
      assertEquals( [ 50, 50 ], item.getUserData( "backgroundImageSize" ) );
    },

    testSendSelection : function() {
      this._createCTabFolderByProtocol( "w3", "w2" );
      var MessageProcessor = rwt.remote.MessageProcessor;
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.CTabItem",
        "properties" : {
          "style" : [],
          "parent" : "w3",
          "index" : 0
        }
      } );
      var widget = rwt.remote.ObjectRegistry.getObject( "w4" );
      TestUtil.flush();

      TestUtil.click( widget );
      rwt.remote.Server.getInstance().send();

      //assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "w4", message.findSetProperty( "w3", "selection" ) );
    },

    testItemDefaultSelected : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      folder.addState( "rwt_CLOSE" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.CTabItem",
        "properties" : {
          "style" : [],
          "parent" : "w3",
          "index" : 0
        }
      } );
      processor.processOperation( {
        "target" : "w3",
        "action" : "listen",
        "properties" : {
          "DefaultSelection" : true
        }
      } );
      var widget = rwt.remote.ObjectRegistry.getObject( "w4" );
      TestUtil.flush();

      TestUtil.doubleClick( widget );

      var message = TestUtil.getLastMessage();
      assertNotNull( message.findNotifyOperation( "w3", "DefaultSelection" ) );
      // NOTE [tb] : current CTabFolderLCA#readData always requires "selection" property
      //             when firing selection event
      assertNotNull( message.findSetProperty( "w3", "selection" ) );
    },

    testItemDefaultSelectedModifier : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      folder.addState( "rwt_CLOSE" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.CTabItem",
        "properties" : {
          "style" : [],
          "parent" : "w3",
          "index" : 0
        }
      } );
      processor.processOperation( {
        "target" : "w3",
        "action" : "listen",
        "properties" : {
          "DefaultSelection" : true
        }
      } );
      var widget = rwt.remote.ObjectRegistry.getObject( "w4" );
      TestUtil.flush();

      TestUtil.fakeMouseEventDOM(
          widget.getElement(),
          "dblclick",
          rwt.event.MouseEvent.buttons.left,
          0,
          0,
          rwt.event.DomEvent.CTRL_MASK
       );

      var message = TestUtil.getLastMessage();
      assertFalse( message.findNotifyProperty( "w3", "DefaultSelection", "shiftKey" ) );
      assertTrue( message.findNotifyProperty( "w3", "DefaultSelection", "ctrlKey" ) );
      assertFalse( message.findNotifyProperty( "w3", "DefaultSelection", "altKey" ) );
    },

    testSendMaximize : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "maximizeVisible" : true } );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      TestUtil.flush();

      TestUtil.click( folder._maxButton );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "maximize", message.findNotifyProperty( "w3", "Folder", "detail" ) );
    },

    testSendMinimize : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "minimizeVisible" : true } );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      TestUtil.flush();

      TestUtil.click( folder._minButton );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "minimize", message.findNotifyProperty( "w3", "Folder", "detail" ) );
    },

    testSendRestoreFromMinimize : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", {
        "minimizeVisible" : true,
        "minMaxState" : "min"
      } );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      TestUtil.flush();

      TestUtil.click( folder._minButton );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "restore", message.findNotifyProperty( "w3", "Folder", "detail" ) );
    },

    testSendRestoreFromMaximize : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", {
        "maximizeVisible" : true,
        "minMaxState" : "max"
      } );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      TestUtil.flush();

      TestUtil.click( folder._maxButton );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "restore", message.findNotifyProperty( "w3", "Folder", "detail" ) );
    },

    testSendShowList : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "chevronVisible" : true } );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      TestUtil.flush();

      TestUtil.click( folder._chevron );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "showList", message.findNotifyProperty( "w3", "Folder", "detail" ) );
    },

    testSendClose : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var item = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "showClose" : true } );
      TestUtil.protocolSet( "w3", { "selection" : "w4" } );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      TestUtil.flush();

      TestUtil.click( item._closeButton );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "close", message.findNotifyProperty( "w3", "Folder", "detail" ) );
      assertEquals( "w4", message.findNotifyProperty( "w3", "Folder", "item" ) );
    },

    testRenderMnemonic_OnActivate : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var item = this._createCTabItemByProtocol( "w4", "w3" );
      item.setText( "foo" );
      item.setMnemonicIndex( 1 );
      shell.setActive( true );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      assertEquals( "f<span style=\"text-decoration:underline\">o</span>o", item.getLabel() );
    },

    testRenderMnemonic_OnDeactivate : function() {
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var item = this._createCTabItemByProtocol( "w4", "w3" );
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
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var item = this._createCTabItemByProtocol( "w4", "w3" );
      item.setText( "foo" );
      item.setMnemonicIndex( 1 );
      shell.setActive( true );
      TestUtil.clearRequestLog();
      TestUtil.flush();
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      var success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      rwt.remote.Server.getInstance().send();

      assertTrue( success );
      var message = TestUtil.getMessageObject();
      assertEquals( "w4", message.findSetProperty( "w3", "selection" ) );
    },

    /////////
    // Helper

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
    },

    tearDown : function() {
      shell.destroy();
      shell = null;
    },

    _createCTabFolderByProtocol : function( id, parentId ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.CTabFolder",
        "properties" : {
          "style" : [ "TOP", "MULTI" ],
          "parent" : parentId,
          "toolTipTexts": [ "Minimize", "Maximize", "Restore", "Show List", "Close" ]
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    _createCTabItemByProtocol : function( id, parentId ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.CTabItem",
        "properties" : {
          "style" : [],
          "parent" : parentId,
          "index" : 0
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    }

  }

} );

}());