/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.CTabFolderTest", {
  extend : rwt.qx.Object,

  members : {

    testCreateCTabFolderOnTopByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      shell.destroy();
      widget.destroy();
    },

    testCreateCTabFolderOnBottomByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      shell.destroy();
      widget.destroy();
    },

    testDestroyByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      TestUtil.flush();
      assertTrue( widget.isDisposed() );
      shell.destroy();
    },

    testDestroyWithChildrenByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var ObjectRegistry = rwt.remote.ObjectRegistry;
      var MessageProcessor = rwt.remote.MessageProcessor;
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      shell.destroy();
    },

    testSetTabPositionByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "tabPosition" : "bottom" } );
      assertEquals( "bottom", widget.getTabPosition() );
      shell.destroy();
      widget.destroy();
    },

    testSetTabHeightByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "tabHeight" : 30 } );
      assertEquals( 30, widget._tabHeight );
      shell.destroy();
      widget.destroy();
    },

    testSetMinMaxStateByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "minMaxState" : "min" } );
      assertEquals( "min", widget._minMaxState );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimizeBoundsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "minimizeBounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( [ 1, 2, 3, 4 ], widget._minButtonBounds );
      shell.destroy();
      widget.destroy();
    },

    testSetMaximizeBoundsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "maximizeBounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( [ 1, 2, 3, 4 ], widget._maxButtonBounds );
      shell.destroy();
      widget.destroy();
    },

    testSetChevronBoundsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "chevronBounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( [ 1, 2, 3, 4 ], widget._chevronBounds );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimizeVisibleByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "minimizeVisible" : true } );
      assertTrue( widget._minButton.getVisibility() );
      shell.destroy();
      widget.destroy();
    },

    testSetMaximizeVisibleByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "maximizeVisible" : true } );
      assertTrue( widget._maxButton.getVisibility() );
      shell.destroy();
      widget.destroy();
    },

    testSetChevronVisibleByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "chevronVisible" : true } );
      assertTrue( widget._chevron.getVisibility() );
      shell.destroy();
      widget.destroy();
    },

    testSetUnselectedCloseVisibleByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "unselectedCloseVisible" : false } );
      assertFalse( widget.getUnselectedCloseVisible() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      var item = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w3", { "selection" : "w4" } );
      assertTrue( item.isSelected() );
      shell.destroy();
      widget.destroy();
      item.destroy();
    },

    testSetSelectionBackgroundByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "selectionBackground" : [ 0, 0, 255, 255 ] } );
      assertEquals( "rgb(0,0,255)", widget.getSelectionBackground() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionForegroundByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "selectionForeground" : [ 0, 0, 255, 255 ] } );
      assertEquals( "rgb(0,0,255)", widget.getSelectionForeground() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionBackgroundImageByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "selectionBackgroundImage" : [ "image.gif", 10, 20 ] } );
      assertEquals( [ "image.gif", 10, 20 ], widget.getSelectionBackgroundImage() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionBackgroundGradientByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      shell.destroy();
      widget.destroy();
    },

    testSetBorderVisibleByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "borderVisible" : true } );
      assertTrue( widget.hasState( "rwt_BORDER" ) );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSetHasDefaultSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolListen( "w3", { "DefaultSelection" : true } );
      assertTrue( widget._hasDefaultSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSetHasFolderListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      assertTrue( widget._hasFolderListener );
      shell.destroy();
      widget.destroy();
    },

    testCreateCTabItemByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      shell.destroy();
      folder.destroy();
      widget.destroy();
    },

    testCreateCTabItemWithCloseByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      shell.destroy();
      folder.destroy();
      widget.destroy();
    },

    testSetItemBoundsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      folder.setTabPosition( "bottom" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "bounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( 1, widget.getLeft() );
      assertEquals( 1, widget.getTop() );
      assertEquals( 3, widget.getWidth() );
      assertEquals( 5, widget.getHeight() );
      shell.destroy();
      folder.destroy();
      widget.destroy();
    },

    testSetItemFontByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "font" : [ ["Arial"], 20, true, false ] } );
      assertEquals( "bold 20px Arial", widget.getFont().toCss() );
      shell.destroy();
      folder.destroy();
      widget.destroy();
    },

    testSetItemTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "text" : "foo<>bar" } );
      assertEquals( "foo&lt;&gt;bar", widget.getLabel() );
      shell.destroy();
      folder.destroy();
      widget.destroy();
    },

    testSetItemImageByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "image" : [ "image.gif", 10, 20 ] } );
      assertEquals( "image.gif", widget.getIcon() );
      shell.destroy();
      folder.destroy();
      widget.destroy();
    },

    testSetItemToolTipByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "toolTip" : "hello blue world" } );
      assertEquals( "hello blue world", widget.getUserData( "toolTipText" ) );
      assertTrue( widget.getToolTip() !== null );
      shell.destroy();
      folder.destroy();
      widget.destroy();
    },

    testSetItemCustomVariantByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "customVariant" : "variant_blue" } );
      assertTrue( widget.hasState( "variant_blue" ) );
      shell.destroy();
      folder.destroy();
      widget.destroy();
    },

    testSetItemShowingByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "showing" : false } );
      assertFalse( widget.getVisibility() );
      shell.destroy();
      folder.destroy();
      widget.destroy();
    },

    testSetItemShowCloseByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      var widget = this._createCTabItemByProtocol( "w4", "w3" );
      TestUtil.protocolSet( "w4", { "showClose" : true } );
      assertTrue( widget._showClose );
      shell.destroy();
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w4" );
      TestUtil.flush();

      TestUtil.click( widget );
      rwt.remote.Server.getInstance().send();

      //assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "w4", message.findSetProperty( "w3", "selection" ) );
      shell.destroy();
    },

    testItemDefaultSelected : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w4" );
      TestUtil.flush();

      TestUtil.doubleClick( widget );

      var message = TestUtil.getLastMessage();
      assertNotNull( message.findNotifyOperation( "w3", "DefaultSelection" ) );
      // NOTE [tb] : current CTabFolderLCA#readData always requires "selection" property
      //             when firing selection event
      assertNotNull( message.findSetProperty( "w3", "selection" ) );
      shell.destroy();
    },

    testItemDefaultSelectedModifier : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w4" );
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
      shell.destroy();
    },

    testSendMaximize : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "maximizeVisible" : true } );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      TestUtil.flush();

      TestUtil.click( folder._maxButton );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "maximize", message.findNotifyProperty( "w3", "Folder", "detail" ) );
      shell.destroy();
    },

    testSendMinimize : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "minimizeVisible" : true } );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      TestUtil.flush();

      TestUtil.click( folder._minButton );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "minimize", message.findNotifyProperty( "w3", "Folder", "detail" ) );
      shell.destroy();
    },

    testSendRestoreFromMinimize : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var ObjectManager = rwt.remote.ObjectRegistry;
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
      shell.destroy();
    },

    testSendRestoreFromMaximize : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var ObjectManager = rwt.remote.ObjectRegistry;
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
      shell.destroy();
    },

    testSendShowList : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var folder = this._createCTabFolderByProtocol( "w3", "w2" );
      TestUtil.protocolSet( "w3", { "chevronVisible" : true } );
      TestUtil.protocolListen( "w3", { "Folder" : true } );
      TestUtil.flush();

      TestUtil.click( folder._chevron );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "showList", message.findNotifyProperty( "w3", "Folder", "detail" ) );
      shell.destroy();
    },

    testSendClose : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var ObjectManager = rwt.remote.ObjectRegistry;
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
      shell.destroy();
    },

    /////////
    // Helper

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