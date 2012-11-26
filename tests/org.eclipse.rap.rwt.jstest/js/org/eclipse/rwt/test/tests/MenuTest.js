/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
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
var ObjectRegistry = rwt.protocol.ObjectRegistry;
var MessageProcessor = rwt.protocol.MessageProcessor;
var Menu = rwt.widgets.Menu;
var MenuItem = rwt.widgets.MenuItem;
var MenuBar = rwt.widgets.MenuBar;

var menuItem;
var menuBar;
var menu;
var menuBarItem;

qx.Class.define( "org.eclipse.rwt.test.tests.MenuTest", {

  extend : qx.core.Object,

  members : {

    testCreateMenuBarByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      shell.destroy();
      widget.destroy();
    },

    testCreatePopUpMenuByProtocol : function() {
     TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "POP_UP" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Menu );
      assertIdentical( rwt.widgets.base.ClientDocument.getInstance(), widget.getParent() );
      assertNull( widget.getUserData( "isControl") );
      widget.destroy();
    },

    testSetMenuBarBoundsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      shell.destroy();
      widget.destroy();
    },

    testSetEnabledByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      shell.destroy();
      widget.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      shell.destroy();
      widget.destroy();
    },

    testCallShowMenuByProtocol : function() {
      var widget = createPopUpMenuByProtocol( "w3" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "call",
        "method" : "showMenu",
        "properties" : {
          "x" : 10,
          "y" : 20
        }
      } );
      assertTrue( widget.getVisibility() );
      assertEquals( 10, widget.getLeft() );
      assertEquals( 20, widget.getTop() );
      widget.destroy();
    },

    testCallUnhideItemsByProtocol : function() {
      var widget = createPopUpMenuByProtocol( "w3" );
      widget.setHasShowListener( true );
      widget._menuShown();
      assertTrue( widget._itemsHiddenFlag );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "call",
        "method" : "unhideItems",
        "properties" : {
          "reveal" : true
        }
      } );
      assertFalse( widget._itemsHiddenFlag );
      widget.destroy();
    },

    testCreateMenuItemByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var widget = createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );
      assertTrue( widget instanceof rwt.widgets.MenuItem );
      assertIdentical( menu._layout, widget.getParent() );
      assertNull( widget.getUserData( "isControl") );
      menu.destroy();
      widget.destroy();
    },

    testDestroyMenuItemWithPopupMenuByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var item = createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );

      MessageProcessor.processOperationArray( [ "destroy", "w3" ] );
      TestUtil.flush();

      assertTrue( menu.isDisposed() );
      assertTrue( item.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( ObjectRegistry.getObject( "w4" ) == null );
    },

    testDestroyMenuItemWithMenuBarByProtocol : function() {
      TestUtil.createShellByProtocol( "w2" );
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

    testCreateMenuItemSeparatorByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var widget = createMenuItemByProtocol( "w4", "w3", [ "SEPARATOR" ] );
      assertTrue( widget instanceof rwt.widgets.MenuItemSeparator );
      assertIdentical( menu._layout, widget.getParent() );
      assertNull( widget.getUserData( "isControl") );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemIndexByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );
      var widget = createMenuItemByProtocol( "w5", "w3", [ "PUSH" ] );
      assertIdentical( menu._layout.getChildren()[ 0 ], widget );
      menu.destroy();
      widget.destroy();
    },

    testSetMeniItemNoRadioGroupByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      menu.addState( "rwt_NO_RADIO_GROUP" );
      var widget = createMenuItemByProtocol( "w4", "w3", [ "RADIO" ] );
      assertTrue( widget._noRadioGroup );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemSubMenuByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var submenu = createPopUpMenuByProtocol( "w5" );
      var widget = createMenuItemByProtocol( "w4", "w3", [ "CASCADE" ] );
      TestUtil.protocolSet( "w4", { "menu" : "w5" } );
      assertIdentical( submenu, widget.getMenu() );
      menu.destroy();
      submenu.destroy();
      widget.destroy();
    },

    testSetMenuItemEnabledByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var widget = createMenuItemByProtocol( "w4", "w3", [ "CASCADE" ] );
      TestUtil.protocolSet( "w4", { "enabled" : false } );
      assertFalse( widget.getEnabled() );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemTextByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var widget = createMenuItemByProtocol( "w4", "w3", [ "RADIO" ] );
      TestUtil.protocolSet( "w4", { "text" : "foo >\t Ctrl+1" } );
      assertEquals( "foo &gt;", widget.getCellContent( 2 ) );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemImageByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var widget = createMenuItemByProtocol( "w4", "w3", [ "RADIO" ] );
      TestUtil.protocolSet( "w4", { "image" : [ "image.gif", 10, 20 ] } );
      assertEquals( "image.gif", widget.getCellContent( 1 ) );
      assertEquals( 10, widget.getPreferredCellWidth( 1 ) );
      assertEquals( 20, widget.getCellHeight( 1 ) );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemSelectionByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var widget = createMenuItemByProtocol( "w4", "w3", [ "CHECK" ] );
      TestUtil.protocolSet( "w4", { "selection" : true } );
      assertTrue( widget._selected );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemSelectionListenerByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var widget = createMenuItemByProtocol( "w4", "w3", [ "CHECK" ] );
      TestUtil.protocolListen( "w4", { "Selection" : true } );
      assertTrue( widget._hasSelectionListener );
      menu.destroy();
      widget.destroy();
    },

    testSetShowListenerByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      TestUtil.protocolListen( "w3", { "Show" : true } );
      assertTrue( menu._hasShowListener );
      menu.destroy();
    },

    testSetHideListenerByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      TestUtil.protocolListen( "w3", { "Hide" : true } );
      assertTrue( menu._hasHideListener );
      menu.destroy();
    },

    testTextOnly : function() {
      createSimpleMenu( "push" );
      menuItem.setText( "Hello World!" );
      TestUtil.flush();
      assertEquals( 1, menuItem._getTargetNode().childNodes.length );
      assertEquals(
        "Hello World!",
        menuItem._getTargetNode().firstChild.innerHTML
      );
      disposeMenu();
    },

    testImageOnly : function() {
      createSimpleMenu( "push" );
      menuItem.setImage( "url.jpg", 20, 30 );
      TestUtil.flush();
      assertEquals( 1, menuItem._getTargetNode().childNodes.length );
      var node = menuItem._getTargetNode().firstChild;
      assertContains(
        "url.jpg",
        TestUtil.getCssBackgroundImage ( node )
      );
      var bounds = TestUtil.getElementBounds( node );
      assertEquals( 20, bounds.width );
      assertEquals( 30, bounds.height );
      disposeMenu();
    },

    testArrowOnly : function() {
      createSimpleMenu( "push" );
      menuItem.setArrow( [ "url.jpg", 13, 13 ] );
      TestUtil.flush();
      assertEquals( 1, menuItem._getTargetNode().childNodes.length );
      var node = menuItem._getTargetNode().firstChild;
      assertContains(
        "url.jpg",
        TestUtil.getCssBackgroundImage( node )
      );
      disposeMenu();
    },

    testMenuResize : function() {
      createSimpleMenu( "push" );
      menuItem.setSpacing( 3 );
      TestUtil.flush();
      var menuNode = menu.getElement();
      var oldMenuBounds = TestUtil.getElementBounds( menuNode );
      assertTrue( menuItem.getWidth() === "auto" );
      menuItem.setText( "bla! " );
      TestUtil.flush();
      var newMenuBounds = TestUtil.getElementBounds( menuNode );
      // Theory: Fore some reason the _cachedPreferredInnerWidth is invalidated before/during(?)
      // initial flush, then not recomputed, leaving it null without a jobQueue entry...?
      assertLarger( oldMenuBounds.width, newMenuBounds.width );
      oldMenuBounds = newMenuBounds;
      var item2 = new MenuItem( "push" );
      item2.setText( "blubblubblub!" );
      item2.setSpacing( 3 );
      menu.addMenuItemAt( item2, 0 );
      var itemNode1 = menuItem._getTargetNode();
      var oldItemBounds1 = TestUtil.getElementBounds( itemNode1 );
      TestUtil.flush();
      newMenuBounds = TestUtil.getElementBounds( menuNode );
      var itemNode2 = item2._getTargetNode();
      var itemBounds1 = TestUtil.getElementBounds( itemNode1 );
      var itemBounds2 = TestUtil.getElementBounds( itemNode2 );
      assertLarger( oldMenuBounds.height, newMenuBounds.height );
      assertLarger( oldItemBounds1.width, itemBounds1.width );
      assertEquals( itemBounds1.width, itemBounds2.width );
      oldItemBounds1 = itemBounds1;
      oldMenuBounds = newMenuBounds;
      item2.setText( "-" );
      TestUtil.flush();
      newMenuBounds = TestUtil.getElementBounds( menuNode );
      itemBounds1 = TestUtil.getElementBounds( itemNode1 );
      itemBounds2 = TestUtil.getElementBounds( itemNode2 );
      assertSmaller( oldMenuBounds.width, newMenuBounds.width );
      assertSmaller( oldItemBounds1.width, itemBounds1.width );
      assertEquals( itemBounds1.width, itemBounds2.width );
      oldMenuBounds = newMenuBounds;
      menuItem.setArrow( [ "bla.jpg", 13, 13 ] );
      TestUtil.flush();
      newMenuBounds = TestUtil.getElementBounds( menuNode );
      // the dimension of arrow are at least 13 and shouldn't have changed
      assertEquals( oldMenuBounds.width, newMenuBounds.width );
      oldMenuBounds = newMenuBounds;
      menuItem.setImage( "bla.jpg" , 30, 30 );
      TestUtil.flush();
      newMenuBounds = TestUtil.getElementBounds( menuNode );
      assertEquals( oldMenuBounds.width + 33, newMenuBounds.width );
      oldMenuBounds = newMenuBounds;
      menuItem.setImage( null, 0, 0 );
      TestUtil.flush();
      newMenuBounds = TestUtil.getElementBounds( menuNode );
      assertEquals( oldMenuBounds.width - 33, newMenuBounds.width );
      disposeMenu();
    },

    testItemHover : function() {
      createSimpleMenu( "push" );
      TestUtil.flush();
      TestUtil.mouseOver( menuItem );
      assertTrue( menuItem.hasState( "over" ) );
      TestUtil.mouseOut( menu );
      assertFalse( menuItem.hasState( "over" ) );
      disposeMenu();
    },

    testMenuLayout : function() {
      createSimpleMenu( "push" );
      menuItem.setText( "hello" );
      var item2 = new MenuItem( "push" );
      item2.setText( "bla!" );
      menu.addMenuItemAt( item2, 0 );
      var item3 = new MenuItem( "push" );
      item3.setText( "blabla!" );
      menu.addMenuItemAt( item3, 0 );
      var item4 = new MenuItem( "push" );
      item4.setText( "blubblubblub!" );
      menu.addMenuItemAt( item4, 0 );
      var item5 = new MenuItem( "push" );
      item5.setText( "asdfasdf!" );
      menu.addMenuItemAt( item5, 0 );
      TestUtil.flush();
      assertTrue( itemsXLayoutIsIdentical( menu ) );
      item2.setImage( "bla.jpg", 20, 20  );
      TestUtil.flush();
      assertTrue( itemsXLayoutIsIdentical( menu ) );
      item3.setImage( "bla.jpg", 40, 40  );
      TestUtil.flush();
      assertTrue( itemsXLayoutIsIdentical( menu ) );
      item3.setImage( null, 0, 0  );
      item2.setArrow( [ "bla.jpg", 13, 13 ] );
      item2.setSelectionIndicator( [ "bla.jpg", 13, 13 ] );
      TestUtil.flush();
      assertTrue( itemsXLayoutIsIdentical( menu ) );
      item2.setImage( null, 0, 0  );
      item2.setArrow( null );
      item2.setSelectionIndicator( null );
      TestUtil.flush();
      assertTrue( itemsXLayoutIsIdentical( menu ) );
      disposeMenu();
    },

    testOpenMenuByMenuBar : function() {
      createMenuBar( "push" );
      TestUtil.flush();
      var bar = menuBar;
      var barItem = menuBarItem;
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertFalse( menu.isSeeable() );
      TestUtil.click( barItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( TestUtil.getDocument() );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      TestUtil.click( barItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( TestUtil.getDocument() );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      disposeMenuBar();
    },

    testOpenMenuAsContextmenu : function() {
      menu = new Menu();
      menuItem = new MenuItem( "push" );
      menuItem.setText( "bla" );
      menu.addMenuItemAt( menuItem, 0 );
      var widget = createControl();
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( menu );
      addContextMenuListener( widget );
      TestUtil.flush();
      assertTrue( widget.isSeeable() );
      assertFalse( menu.isSeeable() );
      TestUtil.rightClick( widget );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( TestUtil.getDocument() );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      widget.setContextMenu( null );
      removeContextMenuListener( widget );
      widget.setParent( null );
      widget.dispose();
      disposeMenu();
    },

    testContextMenuOpenOnControl : function() {
      var parent = createControl();
      parent.addToDocument();
      var widget = new rwt.widgets.base.Atom( "foo" );
      widget.setParent( parent );
      var menu = new rwt.widgets.Menu();
      menu.addToDocument();
      var menuItem = new MenuItem( "PUSH" );
      menu.addMenuItemAt( menuItem, 0 );
      parent.setContextMenu( menu );
      addContextMenuListener( parent );
      TestUtil.flush();

      TestUtil.rightClick( widget );
      TestUtil.flush();

      assertTrue( menu.isSeeable() );
      assertIdentical( parent, menu.getOpener() ); // important for getting focusRoot on key events
      menu.destroy();
      parent.destroy();
    },

    testContextMenuOpenOnText : function() {
      TestUtil.fakeResponse( true );
      var menu = new rwt.widgets.Menu();
      menu.setHasShowListener( true );
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      text.setUserData( "isControl", true );
      text.setContextMenu( menu );
      addContextMenuListener( text );
      TestUtil.flush();
      var right = qx.event.type.MouseEvent.buttons.right;
      var node = text._inputElement;

      TestUtil.fakeMouseEventDOM( node, "mousedown", right );
      TestUtil.fakeMouseEventDOM( node, "mouseup", right );
      TestUtil.fakeMouseEventDOM( node, "click", right );
      TestUtil.fakeMouseEventDOM( node, "contextmenu", right );

      assertTrue( menu.isSeeable() );
      menu.destroy();
      text.destroy();
      TestUtil.fakeResponse( false );
    },

    testContextmenuNotOpenOnParentControl : function() {
      var menu1 = new rwt.widgets.Menu();
      menu1.setHasShowListener( true );
      var parent = createControl();
      parent.addToDocument();
      parent.setContextMenu( menu1 );
      addContextMenuListener( parent );
      var widget = createControl();
      widget.setParent( parent );
      TestUtil.flush();
      assertFalse( menu1.isSeeable() );
      TestUtil.rightClick( widget );
      assertFalse( menu1.isSeeable() );
      menu1.destroy();
      widget.destroy();
      parent.destroy();
    },

    testDropDownExecuteMenuItem : function() {
      createMenuBar( "push" );
      this.executed = false;
      var command = function() {
        this.executed = true;
      };
      menuItem.addEventListener( "execute", command, this );
      TestUtil.flush(); // cannnot click until created
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertFalse( this.executed );
      assertTrue( menu.isSeeable() );
      TestUtil.click( menuItem );
      TestUtil.flush();
      assertTrue( this.executed );
      assertFalse( menu.isSeeable() );
      delete this.executed;
      disposeMenuBar();
    },

    testExecuteWithoutOpener : function() {
      createSimpleMenu( "push" );
      this.executed = false;
      var command = function() {
        this.executed = true;
      };
      menuItem.addEventListener( "execute", command, this );
      TestUtil.flush(); // cannnot click until created
      assertTrue( menu.isSeeable() );
      TestUtil.click( menuItem );
      TestUtil.flush();
      assertTrue( this.executed );
      assertFalse( menu.isSeeable() );
      delete this.executed;
      disposeMenu();
    },

    testOpenSubmenuByMouse : function() {
      TestUtil.prepareTimerUse();
      createMenuBar( "cascade" );
      var subMenu = new Menu();
      var subMenuItem = new MenuItem( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      menuItem.setSubMenu( subMenu );
      var item2 = new MenuItem( "push" );
      item2.setText( "bla!" );
      menu.addMenuItemAt( item2, 0 );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      TestUtil.click( menuBarItem );  //open menu
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      TestUtil.mouseOver( menuItem ); //starting open-timer
      assertTrue( menu._openTimer.isEnabled() );
      assertFalse( menu._closeTimer.isEnabled() );
      TestUtil.click( menuItem ); //clicking does nothing
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      TestUtil.forceInterval( menu._openTimer );  //timer opens submenu
      TestUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( menuItem.hasState( "over" ) );
      TestUtil.mouseFromTo( menuItem, item2 );
      // hovering another item starts the close-timer, but not the open-timer
      // since the item has no submenu
      assertFalse( menu._openTimer.isEnabled() );
      assertTrue( menu._closeTimer.isEnabled() );
      TestUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( item2.hasState( "over" ) );
      assertFalse( menuItem.hasState( "over" ) );
      TestUtil.forceInterval( menu._closeTimer );  //timer closes submenu
      TestUtil.flush();
      assertFalse( subMenu.isSeeable() );
      assertTrue( menu.isSeeable() );
      assertFalse( menuItem.hasState( "over" ) );
      assertTrue( item2.hasState( "over" ) );
      // re-open the submenu
      TestUtil.mouseFromTo( item2, menuItem );
      TestUtil.forceInterval( menu._openTimer );
      TestUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( menuItem.hasState( "over" ) );
      TestUtil.mouseFromTo( menuItem, subMenu );
      TestUtil.mouseFromTo( subMenu, subMenuItem );
      assertFalse( menu._closeTimer.isEnabled() );
      assertFalse( menu._openTimer.isEnabled() );
      TestUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( subMenuItem.hasState( "over"  ) );
      assertTrue( menuItem.hasState( "over" ) );
      assertIdentical( menuBarItem, menu.getOpener() );
      assertIdentical( menuItem, menu._openItem );
      assertIdentical( menuItem, subMenu.getOpener() );
      assertNull( subMenu._openItem );
      // now click:
      TestUtil.click( subMenuItem );
      TestUtil.flush();
      assertFalse( menu._closeTimer.isEnabled() );
      assertFalse( menu._openTimer.isEnabled() );
      assertFalse( subMenu.isSeeable() );
      assertFalse( "menu is gone", menu.isSeeable() );
      assertNull( menu._openItem );
      assertFalse( subMenuItem.hasState( "over" ) );
      assertFalse( menuItem.hasState( "pressed" ) );
      subMenuItem.setParent( null );
      subMenuItem.dispose();
      subMenu.setParent( null );
      subMenu.dispose();
      item2.setParent( null );
      item2.dispose();
      disposeMenuBar();
    },

    testCheckSelection : function() {
      createSimpleMenu( "check" );
      TestUtil.flush();
      assertEquals( 1, menuItem._getTargetNode().childNodes.length );
      var node = menuItem._getTargetNode().firstChild;
      assertEquals(
        "",
        TestUtil.getCssBackgroundImage( node )
      );
      TestUtil.click( menuItem );
      menuItem.setSelectionIndicator( [ "url.jpg", 13, 13 ]);
      TestUtil.flush();
      assertTrue( menuItem.hasState( "selected" ) );
      assertContains(
        "url.jpg",
        TestUtil.getCssBackgroundImage( node )
      );
      TestUtil.click( menuItem );
      menuItem.setSelectionIndicator( null );
      assertFalse( menuItem.hasState( "selected" ) );
      TestUtil.flush();
      assertEquals( 1, menuItem._getTargetNode().childNodes.length );
      assertEquals(
        "",
        TestUtil.getCssBackgroundImage( node )
      );
      disposeMenu();
    },

    testMenuShowEvent : function() {
      createMenuBar( "push" );
      org.eclipse.swt.WidgetManager.getInstance().add( menu, "w3" );
      TestUtil.clearRequestLog();
      TestUtil.flush();
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertEquals( 0, TestUtil.getRequestsSend() );
      assertTrue( menu.isSeeable() );
      assertTrue( menuItem.isSeeable() );
      TestUtil.click( TestUtil.getDocument() );
      TestUtil.flush();
      assertEquals( 0, TestUtil.getRequestsSend() );
      assertFalse( menu.isSeeable() );
      menu.setHasShowListener( true );
      menu.setHasHideListener( true );
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      assertFalse( menuItem.isSeeable() );
      assertTrue( menu._preItem.isSeeable() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "Show" ) );
      TestUtil.clearRequestLog();
      menu.unhideItems( true );
      TestUtil.flush();
      assertTrue( menuItem.isSeeable() );
      assertFalse( menu._preItem.isSeeable() );
      TestUtil.click( TestUtil.getDocument() );
      TestUtil.flush();
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "Hide" ) );
      disposeMenuBar();
    },

    testExecutePushItem : function() {
      createSimpleMenu( "push" );
      org.eclipse.swt.WidgetManager.getInstance().add( menuItem, "w3" );
      TestUtil.flush();
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 0, TestUtil.getRequestsSend() );
      menuItem.setHasSelectionListener( true );
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertFalse( menuItem.hasState( "selected" ) );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "Selection" ) );
      assertNull( TestUtil.getMessageObject().findSetOperation( "w3", "selection" ) );
      TestUtil.clearRequestLog();
      disposeMenu();
    },

    testExecuteCheckItem: function() {
      createSimpleMenu( "check" );
      org.eclipse.swt.WidgetManager.getInstance().add( menuItem, "w3" );
      TestUtil.flush();
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 0, TestUtil.getRequestsSend() );
      assertTrue( menuItem.hasState( "selected" ) );
      TestUtil.clearRequestLog();
      menuItem.setHasSelectionListener( true );
      menuItem.setSelection( false );
      assertFalse( menuItem.hasState( "selected" ) );
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( menuItem.hasState( "selected" ) );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w3", "selection" ) );
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertFalse( menuItem.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertFalse( TestUtil.getMessageObject().findSetProperty( "w3", "selection" ) );
      disposeMenu();
    },

    testExecuteRadioButton : function() {
      createSimpleMenu( "radio" );
      org.eclipse.swt.WidgetManager.getInstance().add( menuItem, "w3" );
      TestUtil.flush();
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 0, TestUtil.getRequestsSend() );
      menuItem.setSelection( false );
      menuItem.setHasSelectionListener( true );
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( menuItem.hasState( "selected" ) );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w3", "selection" ) );
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( menuItem.hasState( "selected" ) );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w3", "selection" ) );
      var item2 = new MenuItem( "radio" );
      menu.addMenuItemAt( item2, 0 );
      org.eclipse.swt.WidgetManager.getInstance().add( item2, "w2" );
      item2.setHasSelectionListener( true );
      TestUtil.clearRequestLog();
      TestUtil.flush();
      TestUtil.click( item2 );
      assertFalse( menuItem.hasState( "selected" ) );
      assertTrue( item2.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertFalse( TestUtil.getMessageObject().findSetProperty( "w3", "selection" ) );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w2", "selection" ) );
      TestUtil.clearRequestLog();
      // bug 328437
      TestUtil.click( item2 );
      assertFalse( menuItem.hasState( "selected" ) );
      assertTrue( item2.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject().findSetOperation( "w3", "selection" ) );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w2", "selection" ) );
      TestUtil.clearRequestLog();
      disposeMenu();
    },

    testExecuteRadioButton_NoRadioGroup : function() {
      createSimpleMenu( "radio" );
      org.eclipse.swt.WidgetManager.getInstance().add( menuItem, "w3" );
      menuItem.setNoRadioGroup( true );
      menuItem.setHasSelectionListener( true );
      var menuItem2 = new MenuItem( "radio" );
      menu.addMenuItemAt( menuItem2, 0 );
      org.eclipse.swt.WidgetManager.getInstance().add( menuItem2, "w2" );
      menuItem2.setNoRadioGroup( true );
      menuItem2.setHasSelectionListener( true );
      TestUtil.clearRequestLog();
      TestUtil.flush();
      TestUtil.click( menuItem );
      assertTrue( menuItem.hasState( "selected" ) );
      assertFalse( menuItem2.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w3", "selection" ) );
      assertNull( TestUtil.getMessageObject().findSetOperation( "w2", "selection" ) );
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem2 );
      assertTrue( menuItem.hasState( "selected" ) );
      assertTrue( menuItem2.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject().findSetOperation( "w3", "selection" ) );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w2", "selection" ) );
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem2 );
      assertTrue( menuItem.hasState( "selected" ) );
      assertFalse( menuItem2.hasState( "selected" ) );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject().findSetOperation( "w3", "selection" ) );
      assertFalse( TestUtil.getMessageObject().findSetProperty( "w2", "selection" ) );
    },

    testKeyboardControl : function() {
      TestUtil.fakeResponse( true );
      TestUtil.prepareTimerUse();
      createMenuBar( "cascade" );
      var subMenu = new Menu();
      this.subMenu = subMenu;
      var subMenuItem = new MenuItem( "push" );
      subMenuItem.setText( "gnasdlkfn" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      var item2 = new MenuItem( "push" );
      item2.setText( "blabla!" );
      menu.addMenuItemAt( item2, 0 );
      var dead1 = new rwt.widgets.MenuItemSeparator();
      var dead2 = new MenuItem( "push" );
      dead2.setText( "Disabled" );
      dead2.setEnabled( false );
      menu.addMenuItemAt( dead1, 0 );
      menu.addMenuItemAt( dead2, 0 );
      var item3 = new MenuItem( "push" );
      item3.setText( "blub" );
      item3.setSubMenu( subMenu );
      menu.addMenuItemAt( item3, 0 );
      menu.setHasShowListener( true ); // force creation of preItem
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      TestUtil.click( menuBarItem );  //open menu
      TestUtil.flush();
      menu.unhideItems( true );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      TestUtil.press( menu, "Down", true );
      assertTrue( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertFalse( menuItem.hasState( "over") );
      TestUtil.press( menu, "Down", true );
      assertFalse( item3.hasState( "over") );
      assertTrue( item2.hasState( "over") );
      assertFalse( menuItem.hasState( "over") );
      TestUtil.press( menu, "Down", true );
      assertFalse( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertTrue( menuItem.hasState( "over") );
      TestUtil.press( menu, "Down", true );
      assertTrue( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertFalse( menuItem.hasState( "over") );
      TestUtil.press( menu, "Up", true );
      assertFalse( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertTrue( menuItem.hasState( "over") );
      TestUtil.press( menu, "Up", true );
      assertFalse( item3.hasState( "over") );
      assertTrue( item2.hasState( "over") );
      assertFalse( menuItem.hasState( "over") );
      TestUtil.press( menu, "Up", true );
      assertTrue( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertFalse( menuItem.hasState( "over") );
      assertFalse( menu._openTimer.isEnabled() );
      assertFalse( menu._closeTimer.isEnabled() );
      TestUtil.press( menu, "Right", true );
      TestUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( TestUtil.isActive( subMenu ) );
      assertFalse( menuItem.hasState( "over") );
      assertFalse( item2.hasState( "over" ) );
      assertTrue( item3.hasState( "over" ) );
      assertFalse( menu._openTimer.isEnabled() );
      assertFalse( menu._closeTimer.isEnabled() );
      assertTrue( subMenuItem.hasState( "over") );
      assertFalse( TestUtil.isActive( menu ) );
      TestUtil.press( subMenu, "Left", true );
      TestUtil.flush();
      assertFalse( subMenu.isSeeable() );
      assertTrue( TestUtil.isActive( menu ) );
      assertTrue( item3.hasState( "over") );
      TestUtil.press( menu, "Enter", true );
      TestUtil.flush();
      assertTrue( item3.hasState( "over" ) );
      assertTrue( subMenu.isSeeable() );
      assertTrue( TestUtil.isActive( subMenu ) );
      assertTrue( subMenuItem.hasState( "over" ) );
      org.eclipse.swt.WidgetManager.getInstance().add( subMenuItem, "w3" );
      subMenuItem.setHasSelectionListener( true );
      rwt.remote.Server.getInstance().send();
      TestUtil.clearRequestLog();
      TestUtil.press( subMenu, "Enter", true );
      TestUtil.flush();
      assertFalse( subMenu.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "Selection" ) );
      assertNull( TestUtil.getMessageObject().findSetOperation( "w3", "selection" ) );
      TestUtil.clearRequestLog();
      disposeMenuBar();
      TestUtil.fakeResponse( false );
    },

    testGetMenuBar : function() {
      createMenuBar( "push" );
      var widget = new rwt.widgets.base.Atom( "bla" );
      widget.addToDocument();
      var manager = org.eclipse.rwt.MenuManager.getInstance();
      TestUtil.flush();
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      assertNull( manager._getMenuBar( widget ) );
      assertIdentical( menuBar, manager._getMenuBar( menuItem ) );
      assertIdentical( menuBar, manager._getMenuBar( menu ) );
      assertIdentical( menuBar, manager._getMenuBar( menuBarItem ) );
      assertIdentical( menuBar, manager._getMenuBar( menuBar ) );
      disposeMenuBar();
    },

    testContextMenuCloseOnOpenerClick : function() {
      menu = new Menu();
      menuItem = new MenuItem( "push" );
      menuItem.setText( "bla" );
      menu.addMenuItemAt( menuItem, 0 );
      var widget = createControl();
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( menu );
      addContextMenuListener( widget );
      TestUtil.flush();
      assertTrue( widget.isSeeable() );
      assertFalse( menu.isSeeable() );
      TestUtil.rightClick( widget );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( widget );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      widget.setContextMenu( null );
      removeContextMenuListener( widget );
      widget.setParent( null );
      widget.dispose();
      disposeMenu();
    },

    testContextMenuCloseOnOtherContextMenu : function() {
      menu = new Menu();
      menuItem = new MenuItem( "push" );
      menuItem.setText( "bla" );
      menu.addMenuItemAt( menuItem, 0 );
      var widget = createControl();
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( menu );
      addContextMenuListener( widget );
      var menu2 = new Menu();
      var menuItem2 = new MenuItem( "push" );
      menuItem2.setText( "bla2" );
      menu2.addMenuItemAt( menuItem2, 0 );
      var widget2 = createControl();
      widget2.addToDocument();
      widget2.setLocation( 20, 20 );
      widget2.setDimension( 20, 20 );
      widget2.setContextMenu( menu2 );
      addContextMenuListener( widget2 );
      TestUtil.flush();
      assertTrue( widget.isSeeable() );
      assertTrue( widget2.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      TestUtil.rightClick( widget );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.rightClick( widget2 );
      TestUtil.flush();
      assertTrue( menu2.isSeeable() );
      assertFalse( menu.isSeeable() );
      widget.setContextMenu( null );
      widget2.setContextMenu( null );
      removeContextMenuListener( widget );
      removeContextMenuListener( widget2 );
      widget.setParent( null );
      widget2.setParent( null );
      widget.destroy();
      widget2.destroy();
      menu2.setParent( null );
      menuItem2.setParent( null );
      menu2.destroy();
      menuItem2.destroy();
      disposeMenu();
    },

    testContextMenuCloseOnMenuBarClick : function() {
      createMenuBar( "push" );
      var bar = menuBar;
      var barItem = menuBarItem;
      var menu2 = new Menu();
      var menuItem2 = new MenuItem( "push" );
      menuItem2.setText( "bla" );
      menu2.addMenuItemAt( menuItem2, 0 );
      var widget = createControl();
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( menu2 );
      addContextMenuListener( widget );
      TestUtil.flush();
      assertTrue( widget.isSeeable() );
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      TestUtil.rightClick( widget );
      TestUtil.flush();
      assertTrue( menu2.isSeeable() );
      TestUtil.click( barItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      disposeMenuBar();
      menu2.setParent( null );
      menuItem2.setParent( null );
      menu2.dispose();
      menuItem2.dispose();
      widget.setContextMenu( null );
      removeContextMenuListener( widget );
      widget.setParent( null );
      widget.dispose();
    },

    testDropDownMenuCloseOnOpenerClick : function() {
      createMenuBar( "push" );
      TestUtil.flush();
      var bar = menuBar;
      var barItem = menuBarItem;
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertFalse( menu.isSeeable() );
      TestUtil.click( barItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( barItem );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      disposeMenuBar();
    },

    testDropDownMenuCloseOnSameMenuBarClick : function() {
      createMenuBar( "push" );
      var bar = menuBar;
      var barItem = menuBarItem;
      var menu2 = new Menu();
      var menuItem2 = new MenuItem( "push" );
      menuItem2.setText( "bla" );
      menu2.addMenuItemAt( menuItem2, 0 );
      var barItem2 = new MenuItem( "bar" );
      barItem2.setMenu( menu2 );
      menuBar.addMenuItemAt( barItem2, 1 );
      TestUtil.flush();
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertTrue( barItem2.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      TestUtil.click( barItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( barItem2 );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      assertTrue( menu2.isSeeable() );
      menu2.destroy();
      menuItem2.destroy();
      barItem2.destroy();
      disposeMenuBar();
    },

    testDropDownMenuCloseOnOtherMenuBarClick : function() {
      createMenuBar( "push" );
      var bar = menuBar;
      var barItem = menuBarItem;
      var menuBar2 = new MenuBar();
      menuBar2.addToDocument();
      var menu2 = new Menu();
      var menuItem2 = new MenuItem( "push" );
      menuItem2.setText( "bla" );
      menu2.addMenuItemAt( menuItem2, 0 );
      var barItem2 = new MenuItem( "bar" );
      barItem2.setMenu( menu2 );
      menuBar2.addMenuItemAt( barItem2, 0 );
      TestUtil.flush();
      assertTrue( bar.isSeeable() );
      assertTrue( menuBar2.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertTrue( barItem2.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      TestUtil.click( barItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( barItem2 );
      TestUtil.flush();
      assertTrue( menu2.isSeeable() );
      assertFalse( menu.isSeeable() );
      menuBar2.destroy();
      menu2.destroy();
      menuItem2.destroy();
      barItem2.destroy();
      disposeMenuBar();
    },

    testDisposeWithAnimation : function() {
      createSimpleMenu( "push" );
      TestUtil.flush();
      menu.setAnimation( {
        "slideIn" : [ 100, "easeIn" ]
      } );
      menu.setHasShowListener( true );
      assertNotNull( menu._appearAnimation );
      var animation = menu._appearAnimation;
      var renderer = menu._appearAnimation.getDefaultRenderer();
      assertTrue( menu._hasParent );
      menu.destroy();
      TestUtil.flush();
      assertTrue( menu.isDisposed() );
      assertNull( menu._appearAnimation );
      assertTrue( animation.isDisposed() );
      assertTrue( renderer.isDisposed() );
      menu = null;
      menuItem = null;
    },

    testDisposeWithRunningAnimaton : function() {
      createSimpleMenu( "push" );
      rwt.protocol.ObjectRegistry.add( "w321", menu );
      menu.hide();
      TestUtil.flush();
      menu.setAnimation( {
        "slideIn" : [ 100, "easeIn" ]
      } );
      menu.setHasShowListener( true );
      assertNotNull( menu._appearAnimation );
      var animation = menu._appearAnimation;
      var renderer = menu._appearAnimation.getDefaultRenderer();
      menu.show();
      rwt.animation.Animation._mainLoop();
      assertTrue( menu._appearAnimation.isRunning() );
      menu.unhideItems();
      rwt.animation.Animation._mainLoop();
      menu.destroy();
      TestUtil.flush();
      rwt.animation.Animation._mainLoop();
      assertTrue( menu.isDisposed() );
      assertNull( menu._appearAnimation );
      assertTrue( animation.isDisposed() );
      assertTrue( renderer.isDisposed() );
      menu = null;
      menuItem = null;
    },

    testOpenContextmenuByClickOnSubwidget : function() {
      menu = new Menu();
      menuItem = new MenuItem( "push" );
      menuItem.setText( "bla" );
      menu.addMenuItemAt( menuItem, 0 );
      var widget = new rwt.widgets.base.Atom( "bla" );
      var parent = createControl();
      parent.add( widget );
      parent.addToDocument();
      parent.setLocation( 10, 10 );
      parent.setDimension( 10, 10 );
      parent.setContextMenu( menu );
      addContextMenuListener( parent );
      TestUtil.flush();
      assertTrue( widget.isSeeable() );
      assertFalse( menu.isSeeable() );
      TestUtil.rightClick( widget );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( TestUtil.getDocument() );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      widget.setContextMenu( null );
      widget.setParent( null );
      widget.dispose();
      disposeMenu();
    },

    testAddSeparatorInMenuBar : function() {
      menuBar = new MenuBar();
      var separator = new rwt.widgets.MenuItemSeparator();
      menuBar.addMenuItemAt( separator, 0 );
    },

    testHasNativeMenu : function() {
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      TestUtil.flush();
      var element = text.getElement().getElementsByTagName( "input" )[ 0 ];
      assertTrue( rwt.widgets.Menu._hasNativeMenu( element ) );
      text.dispose();
      text = new rwt.widgets.Text( true );
      text.addToDocument();
      TestUtil.flush();
      element = text.getElement().getElementsByTagName( "textarea" )[ 0 ];
      assertTrue( rwt.widgets.Menu._hasNativeMenu( element ) );
      text.dispose();
    },

    testMenuFiresChangeHoverItemEvent : function() {
      var menu = createMenuWithItems( "push", 3 );
      var menuItems = menu._layout.getVisibleChildren();
      var log = 0;
      menu.addEventListener( "changeHoverItem", function(){
        log++;
      } );

      menu.setHoverItem( menuItems[ 0 ] );

      assertTrue( log > 0 );
      menu.destroy();
    },

    testMenuBarFiresChangeOpenItemEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menuBar = new rwt.widgets.MenuBar();
      var menuItem = new rwt.widgets.MenuItem( "push" );
      var menu = createMenuWithItems( "push", 3 );
      menuItem.setMenu( menu );
      menuBar.addToDocument();
      menuBar.addMenuItemAt( menuItem, 0 );
      testUtil.flush();
      var log = 0;
      menuBar.addEventListener( "changeOpenItem", function() {
        log++;
      });

      menuBar.setOpenItem( menuItem );

      assertTrue( log > 0 );
      menuBar.destroy();
      menu.destroy();
    },

    testMenuItemFiresSubMenuChangedEvent : function() {
      var menu = createMenuWithItems( "push", 3 );
      var subMenu = createMenuWithItems( "push", 3 );
      var menuItems = menu._layout.getVisibleChildren();
      var menuItem = menuItems[ 0 ];
      var log = 0;
      menuItem.addEventListener( "subMenuChanged", function(){
        log++;
      } );

      menuItem.setMenu( subMenu );

      assertTrue( log > 0 );
      menu.destroy();
      subMenu.destroy();
    },

    testMenuHelpListener : function() {
      var widget = createPopUpMenuByProtocol( "w3" );
      TestUtil.protocolListen( "w3", { "Help" : true } );

      assertTrue( widget.hasEventListeners( "keydown" ) );

      widget.destroy();
    },

    testMenuItemHelpListener : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var widget = createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );
      TestUtil.protocolListen( "w4", { "Help" : true } );

      assertTrue( widget.hasEventListeners( "keydown" ) );

      menu.destroy();
      widget.destroy();
    },

    tearDown : function() {
      if( ObjectRegistry.getObject( "w2" ) ) {
        ObjectRegistry.getObject( "w2" ).destroy();
      }
    }

  }

} );


/////////
// Helper

var createMenuWithItems = function( itemType, itemCount ) {
  var menu = new rwt.widgets.Menu();
  for( var i = 0; i < itemCount; i++ ) {
    var menuItem = new rwt.widgets.MenuItem( itemType );
    menu.addMenuItemAt( menuItem, i );
  }
  var menuItem = new rwt.widgets.MenuItem( itemType );
  menu.addMenuItemAt( menuItem, 0 );
  menu.addToDocument();
  menu.show();
  TestUtil.flush();
  return menu;
};

var createPopUpMenuByProtocol = function( id ) {
  TestUtil.createShellByProtocol( "w2" );
  rwt.protocol.MessageProcessor.processOperation( {
    "target" : id,
    "action" : "create",
    "type" : "rwt.widgets.Menu",
    "properties" : {
      "style" : [ "POP_UP" ],
      "parent" : "w2"
    }
  } );
  return rwt.protocol.ObjectRegistry.getObject( id );
};

var createMenuItemByProtocol = function( id, parentId, style ) {
  rwt.protocol.MessageProcessor.processOperation( {
    "target" : id,
    "action" : "create",
    "type" : "rwt.widgets.MenuItem",
    "properties" : {
      "style" : style,
      "parent" : parentId,
      "index" : 0
    }
  } );
  return rwt.protocol.ObjectRegistry.getObject( id );
};

var createSimpleMenu = function( type ) {
  menu = new Menu();
  menuItem = new MenuItem( type );
  menu.addMenuItemAt( menuItem, 0 );
  menu.show();
};

var createMenuBar = function( type ) {
  menuBar = new MenuBar();
  menuBar.addToDocument();
  menu = new Menu();
  menuItem = new MenuItem( type );
  menuItem.setText( "bla" );
  menu.addMenuItemAt( menuItem, 0 );
  menuBarItem = new MenuItem( "bar" );
  menuBarItem.setMenu( menu );
  menuBar.addMenuItemAt( menuBarItem, 0 );
};

var disposeMenu = function() {
  menu.setParent( null );
  menuItem.setParent( null );
  menu.dispose();
  menuItem.dispose();
  menu = null;
  menuItem = null;
};

var disposeMenuBar = function() {
  menuBar.setParent( null );
  menuBar.dispose();
  menuBar = null;
  menuBarItem.setParent( null );
  menuBarItem.dispose();
  menuBarItem = null;
  disposeMenu();
};

var itemsXLayoutIsIdentical = function( menu ) {
  var ret = true;
  var children = menu._layout.getChildren();
  if( children.length >= 2 ) {
    var masterLayout = null;
    for( var i = 0; i < children.length; i++ ) {
      var child = children[ i ];
      if( child.classname == "rwt.widgets.MenuItem" ) {
        var layout = getMenuItemLayout( child );
        if( masterLayout == null ) {
          masterLayout = layout;
        }
        for( var key in masterLayout ) {
          var value = layout[ key ];
          var masterValue = masterLayout[ key ];
          if( masterValue == null && value != null ){
            masterLayout[ key ] = value;
          }
          if( value != null && masterValue != null && value != masterValue ) {
            ret = false;
          }
        }
      }
    }
  }
  return ret;
};

var getMenuItemLayout = function( item ) {
  var node = null;
  var nodeBounds = null;
  var layout = {
    indicatorLeft : null,
    indicatorWidth : null,
    iconLeft : null,
    iconWidth : null,
    labelLeft : null,
    labelWidth : null,
    arrowLeft : null,
    arrowWidth : null
  };
  node = item.getCellNode( 0 );
  if( node ) {
    nodeBounds = TestUtil.getElementBounds( node );
    layout.indicatorLeft = nodeBounds.left;
    layout.indicatorWidth = nodeBounds.width;
  }
  node = item.getCellNode( 1 );
  if( node ) {
    nodeBounds = TestUtil.getElementBounds( node );
    layout.iconLeft = nodeBounds.left;
    layout.iconWidth = nodeBounds.width;
  }
  node = item.getCellNode( 2 );
  if( node ) {
    nodeBounds = TestUtil.getElementBounds( node );
    layout.labelLeft = nodeBounds.left;
    layout.labelWidth = nodeBounds.width;
  }
  node = item.getCellNode( 3 );
  if( node ) {
    nodeBounds = TestUtil.getElementBounds( node );
    layout.arrowLeft = nodeBounds.left;
    layout.arrowWidth = nodeBounds.width;
  }
  return layout;
};

var addContextMenuListener = function( widget ) {
  var detectByKey = rwt.widgets.Menu.menuDetectedByKey;
  var detectByMouse = rwt.widgets.Menu.menuDetectedByMouse;
  widget.addEventListener( "keydown", detectByKey );
  widget.addEventListener( "mouseup", detectByMouse );
};

var removeContextMenuListener = function( widget ) {
  var detectByKey = rwt.widgets.Menu.menuDetectedByKey;
  var detectByMouse = rwt.widgets.Menu.menuDetectedByMouse;
  widget.removeEventListener( "keydown", detectByKey );
  widget.removeEventListener( "mouseup", detectByMouse );
};

var createControl = function() {
  var result = new rwt.widgets.Composite();
  result.setUserData( "isControl", true );
  return result;
};


}());
