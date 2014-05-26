/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
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
var ObjectRegistry = rwt.remote.ObjectRegistry;
var MessageProcessor = rwt.remote.MessageProcessor;
var Menu = rwt.widgets.Menu;
var MenuItem = rwt.widgets.MenuItem;
var MenuBar = rwt.widgets.MenuBar;

var menuHandler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Menu" );
var menuItemHandler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.MenuItem" );

var shell, menuItem, menuBar, menu, menuBarItem;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.MenuBarTest", {

  extend : rwt.qx.Object,

  members : {

    tearDown : function() {
      destroy( shell, menuItem, menuBar, menu, menuBarItem );
      shell = menuItem = menuBar = menu = menuBarItem = null;
    },

    testCreateMenuBarByProtocol : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "BAR" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.MenuBar );
      assertIdentical( shell, widget.getParent() );
      assertNull( widget.getUserData( "isControl") );
    },

    testSetMenuBarBoundsByProtocol : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "BAR" ],
          "parent" : "w2",
          "bounds" : [ 1, 2, 3, 4 ]
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 1, widget.getLeft() );
      assertEquals( 2, widget.getTop() );
      assertEquals( 3, widget.getWidth() );
      assertEquals( 4, widget.getHeight() );
    },

    testSetEnabledByProtocol : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "BAR" ],
          "parent" : "w2",
          "enabled" : false
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertFalse( widget.getEnabled() );
    },

    testSetCustomVariantByProtocol : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "BAR" ],
          "parent" : "w2",
          "customVariant" : "variant_blue"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.hasState( "variant_blue" ) );
    },

    testDestroyMenuItemWithMenuBarByProtocol : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "BAR" ],
          "parent" : "w2"
        }
      } );
      var menu = ObjectRegistry.getObject( "w3" );
      var item = createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );

      MessageProcessor.processOperationArray( [ "destroy", "w3" ] );
      TestUtil.flush();

      assertTrue( menu.isDisposed() );
      assertTrue( item.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( ObjectRegistry.getObject( "w4" ) == null );
    },

    testOpenMenuByMenuBar : function() {
      createMenuBar( "push" );
      TestUtil.flush();
      assertTrue( menuBar.isSeeable() );
      assertTrue( menuBarItem.isSeeable() );
      assertFalse( menu.isSeeable() );
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( TestUtil.getDocument() );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( TestUtil.getDocument() );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
    },

    testMenuBarItemWithMnemonic_RenderMnemonic : function() {
      createMenuBar( "push" );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      var expected = "f<span style=\"text-decoration:underline\">o</span>o";
      assertEquals( expected, menuBarItem.getCellContent( 2 ) );
    },

    testMenuBar_setActiveTrue_hoversFirstItem : function() {
      createMenuBar();
      TestUtil.flush();

      menuBar.setActive( true );
      TestUtil.flush();

      assertIdentical( menuBarItem, menuBar._hoverItem );
    },

    testMenuBar_setActiveTrue_doesNotHoverFirstItemIfOtherItemIsHovered : function() {
      createMenuBar();
      var firstBarItem = new MenuItem( "cascade" );
      menuBar.addMenuItemAt( firstBarItem, 0 );
      TestUtil.flush();

      TestUtil.hoverFromTo( menuBar.getElement(), menuBarItem.getElement() );
      TestUtil.click( menuBarItem );
      TestUtil.flush();

      assertIdentical( menuBarItem, menuBar._hoverItem );
    },

    testMenuBar_setActiveTrue_setsCaptureTrue : function() {
      createMenuBar();
      TestUtil.flush();

      menuBar.setActive( true );

      assertTrue( menuBar.getCapture() );
    },

    testMenuBar_setActiveFalse_setsCaptureFalse : function() {
      createMenuBar();
      TestUtil.flush();

      menuBar.setActive( true );
      menuBar.setActive( false );

      assertFalse( menuBar.getCapture() );
    },

    testMenuBar_setActiveTrue_allowsMenuOpenByKeyDown : function() {
      createMenuBar();
      menu.addMenuItemAt( new MenuItem(), 1 );
      TestUtil.flush();

      menuBar.setActive( true );
      TestUtil.press( menuBar, "Down" );
      TestUtil.flush();

      assertTrue( menu.isSeeable() );
      assertIdentical( menuItem, menu._hoverItem );
    },

    testMenuBar_setActiveTrue_allowsMenuOpenByKeyUp : function() {
      createMenuBar();
      menu.addMenuItemAt( new MenuItem(), 0 );
      TestUtil.flush();

      menuBar.setActive( true );
      TestUtil.press( menuBar, "Up" );
      TestUtil.flush();

      assertTrue( menu.isSeeable() );
      assertIdentical( menuItem, menu._hoverItem );
    },

    testMenuBar_setActiveTrue_allowsNavigatinoByKeyRight : function() {
      createMenuBar();
      menuBar.addMenuItemAt( new MenuItem( "push" ), 0 );
      TestUtil.flush();

      menuBar.setActive( true );
      TestUtil.press( menuBar, "Right" );
      TestUtil.flush();

      assertIdentical( menuBarItem, menuBar._hoverItem );
    },

    testMenuBar_setActiveTrue_allowsNavigatinoByKeyLeft : function() {
      createMenuBar();
      menuBar.addMenuItemAt( new MenuItem( "push" ), 1 );
      TestUtil.flush();

      menuBar.setActive( true );
      TestUtil.press( menuBar, "Right" );
      TestUtil.press( menuBar, "Left" );
      TestUtil.flush();

      assertIdentical( menuBarItem, menuBar._hoverItem );
    },

    testMenuBar_setActiveTrue_allowsHover : function() {
      createMenuBar();
      menu.addMenuItemAt( new MenuItem(), 0 );
      TestUtil.flush();

      menuBar.setActive( true );
      TestUtil.press( menuBar, "Up" );
      TestUtil.flush();

      assertTrue( menu.isSeeable() );
      assertIdentical( menuItem, menu._hoverItem );
    },

    testMenuBar_setMnemonicsTrue_rendersMnemonic : function() {
      createMenuBar();
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      TestUtil.flush();

      menuBar.setMnemonics( true );
      TestUtil.flush();

      var expected = "f<span style=\"text-decoration:underline\">o</span>o";
      assertEquals( expected, menuBarItem.getCellContent( 2 ) );
    },

    testMenuBar_setMnemonicsTrue_triggerMnemonic : function() {
      createMenuBar();
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      menuBar.setMnemonics( true );
      menuBar.setActive( true );
      TestUtil.flush();

      TestUtil.press( menuBar, "O" );
      TestUtil.flush();

      assertTrue( menu.isSeeable() );
    },

    testMenuBar_openItemSetsItAsHoverItem : function() {
      createMenuBar();
      var firstBarItem = new MenuItem(  "cascade" );
      firstBarItem.setMenu( menuBar );
      menuBar.addMenuItemAt( firstBarItem, 0 );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      TestUtil.flush();
      menuBar.setMnemonics( true );
      menuBar.setActive( true );
      TestUtil.flush();

      TestUtil.press( menuBar, "O" );
      TestUtil.flush();

      assertIdentical( menuBarItem, menuBar._hoverItem );
    },

    testMenuBar_triggerMnemonicWhileActiveDoesNotHideMnemonics : function() {
      createMenuBar();
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      TestUtil.flush();
      // This test can only fail if mnemonics are activated by key
      rwt.widgets.util.MnemonicHandler.getInstance().setActivator( "CTRL" );
      var DomEvent = rwt.event.DomEvent;

      TestUtil.keyDown( menuBar, "Control", DomEvent.CTRL_MASK ); // important!
      TestUtil.keyUp( menuBar, "Control", 0 );
      TestUtil.press( menuBar, "O" ); // shows menu
      TestUtil.flush();

      var expected = "f<span style=\"text-decoration:underline\">o</span>o";
      assertEquals( expected, menuBarItem.getCellContent( 2 ) );
    },

    testMenuBar_setActiveFalse_setsCaptureWidgetToNull : function() {
      createMenuBar();
      var button = new rwt.widgets.base.BasicButton( "push" );
      button.addToDocument();
      TestUtil.flush();
      button.focus();
      menuBar.setActive( true );

      menuBar.setActive( false );

      assertNull( rwt.event.EventHandler.getCaptureWidget() );
    },

    testMenuBar_setActiveFalse_setsMnemonicsFalse : function() {
      createMenuBar();
      menuBar.setActive( true );
      menuBar.setMnemonics( true );

      menuBar.setActive( false );

      assertFalse( menuBar.getMnemonics() );
    },

    testMenuBar_setActiveFalse_clearsHoverItem : function() {
      createMenuBar();
      TestUtil.flush();

      menuBar.setActive( true );
      menuBar.setActive( false );

      assertNull( menuBar._hoverItem );
    },

    testMenuBar_setActiveFalse_isCalledOnOutsideClick : function() {
      createMenuBar();
      TestUtil.flush();

      menuBar.setActive( true );
      TestUtil.click( rwt.widgets.base.ClientDocument.getInstance() );

      assertFalse( menuBar.getActive() );
    },

    testMenuBar_setActiveTrue_isCalledOnMenuBarItemClick : function() {
      createMenuBar( "push" );
      TestUtil.flush();

      TestUtil.click( menuBarItem );
      TestUtil.flush();

      assertTrue( menuBar.getActive() );
    },

    testMenuBar_setActiveFalse_isCalledOnMenuPushItemClick : function() {
      createMenuBar( "push" );
      TestUtil.flush();

      TestUtil.click( menuBarItem );
      TestUtil.flush();
      TestUtil.click( menuItem );
      TestUtil.flush();

      assertFalse( menuBar.getActive() );
      assertNull( menuBar._hoverItem );
    },

    testMenuBar_setActiveFalse_isCalledOnMenuPushItemMnemonicTrigger : function() {
      createMenuBar( "push" );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );
      TestUtil.flush();

      TestUtil.click( menuBarItem );
      TestUtil.flush();
      menu.setMnemonics( true );
      TestUtil.press( menu, "O" );
      TestUtil.flush();

      assertFalse( menu.isSeeable() );
      assertFalse( menuBar.getActive() );
      assertNull( menuBar._hoverItem );
    },

    testMenuBarItemWithMnemonic_HideMnemonic : function() {
      createMenuBar( "push" );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().deactivate();
      TestUtil.flush();

      assertEquals( "foo", menuBarItem.getCellContent( 2 ) );
    },

    testMenuBarItemWithMnemonic_Trigger_onCascade : function() {
      createMenuBar( "push" );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      TestUtil.flush();
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertTrue( menu.isSeeable() );
      assertFalse( rwt.widgets.util.MnemonicHandler.getInstance().isActive() );
    },

    testMenuBarItemWithMnemonic_Trigger_onPush : function() {
      createMenuBar( "push", "push" );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      TestUtil.flush();
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertFalse( menu.isSeeable() );
      assertTrue( rwt.widgets.util.MnemonicHandler.getInstance().isActive() );
    },

    testMenuBarItemWithMnemonic_NoMnemonicShowWhileVisible : function() {
      createMenuBar( "push" );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      TestUtil.flush();
      TestUtil.click( menuBarItem );

      rwt.widgets.util.MnemonicHandler.getInstance().activate();

      assertFalse( rwt.widgets.util.MnemonicHandler.getInstance().isActive() );
    },

    testMenuBarItemWithMnemonic_TriggerShowsMenuMnemonics : function() {
      createMenuBar( "push" );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertEquals( "f<span style=\"text-decoration:underline\">o</span>o", menuItem.getCellContent( 2 ) );
    },

    testExecutePushItemInMenuBar : function() {
      createMenuBar( "push", "push" );
      rwt.remote.ObjectRegistry.add( "w3", menuBarItem, menuItemHandler );
      TestUtil.flush();
      TestUtil.clearRequestLog();
      TestUtil.click( menuBarItem );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.fakeListener( menuBarItem, "Selection", true );
      TestUtil.clearRequestLog();
      TestUtil.click( menuBarItem );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "Selection" ) );
      TestUtil.clearRequestLog();
    },

    testGetMenuBar : function() {
      createMenuBar( "push" );
      var widget = new rwt.widgets.base.Atom( "bla" );
      widget.addToDocument();
      var manager = rwt.widgets.util.MenuManager.getInstance();
      TestUtil.flush();
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      assertNull( manager._getMenuBar( widget ) );
      assertIdentical( menuBar, manager._getMenuBar( menuItem ) );
      assertIdentical( menuBar, manager._getMenuBar( menu ) );
      assertIdentical( menuBar, manager._getMenuBar( menuBarItem ) );
      assertIdentical( menuBar, manager._getMenuBar( menuBar ) );
    },

    testAddSeparatorInMenuBar : function() {
      createMenuBar();
      var separator = new rwt.widgets.MenuItemSeparator();
      menuBar.addMenuItemAt( separator, 0 );
    },

    testMenuBarFiresChangeOpenItemEvent : function() {
      menuBar = new rwt.widgets.MenuBar();
      menuBarItem = new rwt.widgets.MenuItem( "push" );
      menu = createMenuWithItems( "push", 3 );
      menuBarItem.setMenu( menu );
      menuBar.addToDocument();
      menuBar.addMenuItemAt( menuBarItem, 0 );
      TestUtil.flush();
      var log = 0;
      menuBar.addEventListener( "changeOpenItem", function() {
        log++;
      } );

      menuBar.setOpenItem( menuBarItem );

      assertTrue( log > 0 );
    },

    testMenuBarDoesNotStealFocus : function() {
      createMenuBar();
      var control = new rwt.widgets.Composite();
      control.setTabIndex( 1 ); // make focusable
      control.addToDocument();
      TestUtil.flush();
      TestUtil.click( control );

      assertTrue( control.getFocused() );
      TestUtil.click( menuBarItem );

      assertTrue( control.getFocused() );
      control.destroy();
    },

    testClickOnDisabledItemDoesNotOpenMenu : function() {
      createMenuBar( "push" );
      menuBarItem.setEnabled( false );
      TestUtil.flush();

      TestUtil.click( menuBarItem );
      TestUtil.flush();

      assertFalse( menu.isSeeable() );
    }

  }

} );


/////////
// Helper

var createMenuWithItems = function( itemType, itemCount ) {
  var menu = new rwt.widgets.Menu();
  ObjectRegistry.add( "w3", menu, menuHandler );
  for( var i = 0; i < itemCount; i++ ) {
    var menuItem = new rwt.widgets.MenuItem( itemType );
    ObjectRegistry.add( "w4" + i, menuItem, menuItemHandler );
    menu.addMenuItemAt( menuItem, i );
  }
  var menuItem = new rwt.widgets.MenuItem( itemType );
  ObjectRegistry.add( "w5" + i, menuItem, menuItemHandler );
  menu.addMenuItemAt( menuItem, 0 );
  menu.addToDocument();
  menu.show();
  TestUtil.flush();
  return menu;
};

var createMenuItemByProtocol = function( id, parentId, style, index ) {
  MessageProcessor.processOperation( {
    "target" : id,
    "action" : "create",
    "type" : "rwt.widgets.MenuItem",
    "properties" : {
      "style" : style,
      "parent" : parentId,
      "index" : index ? index : 0
    }
  } );
  return ObjectRegistry.getObject( id );
};

var createSimpleMenu = function( type ) {
  menu = new Menu();
  ObjectRegistry.add( "w3", menu, menuHandler );
  menuItem = new MenuItem( type || "push" );
  ObjectRegistry.add( "w4", menuItem, menuItemHandler );
  menu.addMenuItemAt( menuItem, 0 );
  menu.show();
};

var createMenuBar = function( type, barItemType ) {
  shell = TestUtil.createShellByProtocol( "w2" );
  shell.setActive( true );
  menuBar = new MenuBar();
  ObjectRegistry.add( "w3", menuBar, menuHandler );
  menuBar.setParent( shell );
  menu = new Menu();
  ObjectRegistry.add( "w4", menu, menuHandler );
  menuItem = new MenuItem( type );
  ObjectRegistry.add( "w5", menuItem, menuItemHandler );
  menuItem.setText( "bla" );
  menu.addMenuItemAt( menuItem, 0 );
  menuBarItem = new MenuItem( barItemType ? barItemType : "cascade" );
  ObjectRegistry.add( "w6", menuBarItem, menuItemHandler );
  menuBarItem.setMenu( menu );
  menuBar.addMenuItemAt( menuBarItem, 0 );
};

var destroy = function() {
  for( var i = 0; i < arguments.length; i++ ) {
    if( arguments[ i ] ) {
      arguments[ i ].destroy();
    }
  }
};


}());
