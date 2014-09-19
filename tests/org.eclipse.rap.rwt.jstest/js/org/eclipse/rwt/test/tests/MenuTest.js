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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.MenuTest", {

  extend : rwt.qx.Object,

  members : {

    tearDown : function() {
      destroy( shell, menuItem, menuBar, menu, menuBarItem );
      shell = menuItem = menuBar = menu = menuBarItem = null;
    },

    testMenuHandlerEventsList : function() {
      assertEquals( [ "Show", "Hide" ], menuHandler.events );
    },

    testMenuItemHandlerEventsList : function() {
      assertEquals( [ "Selection" ], menuItemHandler.events );
    },

    testCreatePopUpMenuByProtocol : function() {
     shell = TestUtil.createShellByProtocol( "w2" );
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
      TestUtil.fakeListener( widget, "Show", true );
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

    testCreateMenuItemByProtocolAtPosition : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      TestUtil.fakeListener( menu, "Show", true );
      menu._menuShown();

      createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );
      createMenuItemByProtocol( "w5", "w3", [ "PUSH" ] );
      var item = createMenuItemByProtocol( "w6", "w3", [ "PUSH" ], 1 );

      assertEquals( 0, menu._layout.indexOf( menu._preItem ) );
      assertEquals( 2, menu._layout.indexOf( item ) );
      menu.destroy();
    },

    testCreateMenuItemWithMnemonicByProtocol : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var item = createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );

      TestUtil.protocolSet( "w4", { "mnemonicIndex" : 1 } );

      assertEquals( 1, item.getMnemonicIndex() );
      menu.destroy();
      item.destroy();
    },

    testSetTextResetsMnemonic : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var item = createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );
      TestUtil.protocolSet( "w4", { "mnemonicIndex" : 1 } );

      TestUtil.protocolSet( "w4", { "text" : "foo" } );

      assertNull( item.getMnemonicIndex() );
      menu.destroy();
      item.destroy();
    },

    testAccelerator_SetWithText : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var item = createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );

      TestUtil.protocolSet( "w4", { "text" : "foo\tac&c" } );

      assertEquals( "foo", item.getCellContent( 2 ) );
      assertEquals( "&nbsp;&nbsp;&nbsp; ac&amp;c", item.getCellContent( 3 ) );
      menu.destroy();
    },

    testAccelerator_HasTextAlignRight : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var item = createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );

      TestUtil.protocolSet( "w4", { "text" : "foo\tac&c" } );
      menu.show();
      TestUtil.flush();

      assertEquals( "right", item.getCellNode( 3 ).style.textAlign );
      menu.destroy();
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

    testTextOnly : function() {
      openSimpleMenu( "push" );
      menuItem.setText( "Hello World!" );
      TestUtil.flush();
      assertEquals( 1, menuItem._getTargetNode().childNodes.length );
      assertEquals(
        "Hello World!",
        menuItem._getTargetNode().firstChild.innerHTML
      );
    },

    testImageOnly : function() {
      openSimpleMenu( "push" );
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
    },

    testArrowUrl : function() {
      openSimpleMenu( "push" );

      menuItem.setArrow( [ "url.jpg", 13, 13 ] );
      TestUtil.flush();

      assertEquals( 1, menuItem._getTargetNode().childNodes.length );
      var node = menuItem._getTargetNode().firstChild;
      assertContains( "url.jpg", TestUtil.getCssBackgroundImage( node ) );
    },

    testArrowDimension : function() {
      openSimpleMenu( "push" );

      menuItem.setArrow( [ "url.jpg", 13, 14 ] );
      TestUtil.flush();

      var node = menuItem._getTargetNode().firstChild;
      var bounds = TestUtil.getElementBounds( node );
      assertEquals( 13, bounds.width );
      assertEquals( 14, bounds.height );
    },

    testMenuResize : function() {
      openSimpleMenu( "push" );
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
    },

    testItemHover : function() {
      openSimpleMenu( "push" );
      TestUtil.flush();
      TestUtil.mouseOver( menuItem );
      assertTrue( menuItem.hasState( "over" ) );
      TestUtil.mouseOut( menu );
      assertFalse( menuItem.hasState( "over" ) );
    },

    testMenuLayout : function() {
      openSimpleMenu( "push" );
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
    },

    testMenuBarWithMnemonic_RenderMnemonicsOnNewItems : function() {
      createMenuBar( "push" );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      TestUtil.flush();
      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      var newMenuItem = new MenuItem( "push" );
      menu.addMenuItemAt( newMenuItem, 0 );
      newMenuItem.setText( "foo" );
      newMenuItem.setMnemonicIndex( 1 );
      TestUtil.flush();

      assertTrue( newMenuItem.isSeeable() );
      assertEquals(
        "f<span style=\"text-decoration:underline\">o</span>o",
        newMenuItem.getCellContent( 2 )
      );
    },

    testMenuWithMnemonic_TriggerSendsSelection : function() {
      createMenuBar( "push" );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );
      TestUtil.fakeListener( menuItem, "Selection", true );
      TestUtil.flush();
      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();
      TestUtil.press( menu, "O", true );
      TestUtil.flush();

      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w5", "Selection" ) );
    },

    testMenuWithMnemonic_AndSeparator : function() {
      createMenuBar( "push" );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );
      menu.addMenuItemAt( new rwt.widgets.MenuItemSeparator(), 0 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();
      TestUtil.press( menu, "O", true );
      TestUtil.flush();

      assertFalse( menu.isSeeable() );
    },

    testMenuWithMnemonic_TriggerClosesMenu : function() {
      createMenuBar( "push" );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );
      rwt.remote.ObjectRegistry.add( "w3", menuItem, menuItemHandler );
      TestUtil.fakeListener( menuItem, "Selection", true );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();
      TestUtil.press( menu, "O", true );
      TestUtil.flush();

      assertFalse( menu.isSeeable() );
    },

    testMenuWithMnemonic_TriggerOpensCascade : function() {
      createMenuBar( "cascade" );
      var subMenu = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      var subMenuItem = new MenuItem( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      subMenuItem.setText( "bar" );
      menuItem.setSubMenu( subMenu );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();
      TestUtil.press( menu, "O", true );
      TestUtil.flush();

      assertTrue( menu.isSeeable() );
      assertTrue( subMenu.isSeeable() );
      subMenu.destroy();
    },

    testMenuWithMnemonic_CascadeMenuIsHovered : function() {
      createMenuBar( "cascade" );
      var subMenu = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      var subMenuItem = new MenuItem( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      subMenuItem.setText( "bar" );
      menuItem.setSubMenu( subMenu );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();
      TestUtil.press( menu, "O", true );
      TestUtil.flush();

      assertIdentical( menuItem, menu.getHoverItem() );
      assertIdentical( subMenuItem, subMenu.getHoverItem() );
      subMenu.destroy();
    },

    testMenuWithMnemonic_CascadeMenuRendersMnemonics : function() {
      createMenuBar( "cascade" );
      var subMenu = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      var subMenuItem = new MenuItem( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      subMenuItem.setText( "bar" );
      subMenuItem.setMnemonicIndex( 1 );
      menuItem.setSubMenu( subMenu );
      menuBarItem.setText( "foo" );
      menuBarItem.setMnemonicIndex( 1 );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();
      TestUtil.press( menu, "O", true );
      TestUtil.flush();

      assertEquals(
        "b<span style=\"text-decoration:underline\">a</span>r",
        subMenuItem.getCellContent( 2 )
      );
      subMenu.destroy();
    },

    testOpenMenuAsContextmenu : function() {
      menu = new Menu();
      rwt.remote.ObjectRegistry.add( "w3", menu, menuHandler );
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
    },

    testContextMenuGetsKeyEvents : function() {
      var menu = createPopUpMenuByProtocol( "w3" );
      var item = createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );
      menu.show();
      TestUtil.flush();

      TestUtil.press( menu, "Down" );

      assertTrue( item.hasState( "over" ) );
      menu.destroy();
    },

    testContextMenuOpenOnControl : function() {
      var parent = createControl();
      parent.addToDocument();
      var widget = new rwt.widgets.base.Terminator();
      widget.setParent( parent );
      var menu = new rwt.widgets.Menu();
      rwt.remote.ObjectRegistry.add( "w3", menu, menuHandler );
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

    testContextMenuDoesNotStealFocus : function() {
      var control = createControl();
      control.setTabIndex( 1 ); // make focusable
      control.addToDocument();
      var menu = new rwt.widgets.Menu();
      rwt.remote.ObjectRegistry.add( "w3", menu, menuHandler );
      menu.addToDocument();
      var menuItem = new MenuItem( "PUSH" );
      rwt.remote.ObjectRegistry.add( "w4", menuItem, menuItemHandler );
      menu.addMenuItemAt( menuItem, 0 );
      control.setContextMenu( menu );
      addContextMenuListener( control );
      TestUtil.flush();
      TestUtil.click( control );
      assertTrue( control.getFocused() );
      TestUtil.rightClick( control );
      TestUtil.flush();

      assertTrue( menuItem.isFocusable() );
      TestUtil.click( menuItem );

      assertTrue( control.getFocused() );
      menu.destroy();
      control.destroy();
      menu.destroy();
    },

    testContextMenuOpenOnText : function() {
      TestUtil.fakeResponse( true );
      var menu = new rwt.widgets.Menu();
      rwt.remote.ObjectRegistry.add( "w3", menu, menuHandler );
      TestUtil.fakeListener( menu, "Show", true );
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      text.setUserData( "isControl", true );
      text.setContextMenu( menu );
      addContextMenuListener( text );
      TestUtil.flush();
      var right = rwt.event.MouseEvent.buttons.right;
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
      var menu = new rwt.widgets.Menu();
      rwt.remote.ObjectRegistry.add( "w3", menu, menuHandler );
      TestUtil.fakeListener( menu, "Show", true );
      var parent = createControl();
      parent.addToDocument();
      parent.setContextMenu( menu );
      addContextMenuListener( parent );
      var widget = createControl();
      widget.setParent( parent );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      TestUtil.rightClick( widget );
      assertFalse( menu.isSeeable() );
      menu.destroy();
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
    },

    testExecuteWithoutOpener : function() {
      openSimpleMenu( "push" );
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
    },

    testOpenSubmenuByMouseOver : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      menuItem.setSubMenu( subMenu );
      var subMenuItem = new MenuItem( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      TestUtil.flush();

      TestUtil.mouseOver( menuItem );
      TestUtil.forceInterval( menu._openTimer );
      TestUtil.flush();

      assertTrue( subMenu.isSeeable() );
      assertTrue( menuItem.hasState( "over" ) );
      subMenu.destroy();
    },

    testDoesNotOpenSubmenuByMouseOver_onDisabledItem : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      menuItem.setSubMenu( subMenu );
      menuItem.setEnabled( false );
      var subMenuItem = new MenuItem( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      TestUtil.flush();

      TestUtil.mouseOver( menuItem );
      TestUtil.forceInterval( menu._openTimer );
      TestUtil.flush();

      assertFalse( subMenu.isSeeable() );
      subMenu.destroy();
    },

    testCloseSubmenuByMouseOverSiblingItem : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      menuItem.setSubMenu( subMenu );
      var subMenuItem = new MenuItem( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      var menuItem2 = new MenuItem( "push" );
      menu.addMenuItemAt( menuItem2, 1 );
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      TestUtil.flush();
      TestUtil.mouseOver( menuItem );
      TestUtil.forceInterval( menu._openTimer );

      TestUtil.mouseFromTo( menuItem, menuItem2 );
      TestUtil.forceInterval( menu._closeTimer );
      TestUtil.flush();

      assertFalse( subMenu.isSeeable() );
      assertFalse( menuItem.hasState( "over" ) );
      subMenu.destroy();
    },

    testDoNotCloseSubmenuByMouseOverSubMenuItem : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      menuItem.setSubMenu( subMenu );
      var subMenuItem = new MenuItem( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      var menuItem2 = new MenuItem( "push" );
      menu.addMenuItemAt( menuItem2, 0 );
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      TestUtil.flush();
      TestUtil.mouseOver( menuItem );
      TestUtil.forceInterval( menu._openTimer );

      TestUtil.mouseFromTo( menuItem, subMenuItem );
      try {
        TestUtil.forceInterval( menu._closeTimer );
      } catch( e ) {}
      TestUtil.flush();

      assertTrue( subMenu.isSeeable() );
      assertTrue( menuItem.hasState( "over" ) );
      subMenu.destroy();
    },

    testDoNotOpenSubmenuByMouseOverAndOut : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      menuItem.setSubMenu( subMenu );
      var subMenuItem = new MenuItem( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      rwt.remote.ObjectRegistry.add( "w8", subMenuItem, menuItemHandler );
      TestUtil.flush();

      TestUtil.mouseOver( menuItem );
      TestUtil.mouseOut( menuItem );
      try {
        TestUtil.forceInterval( menu._openTimer );
      } catch( e ) {}
      TestUtil.flush();

      assertFalse( subMenu.isSeeable() );
      assertFalse( menuItem.hasState( "over" ) );
      subMenu.destroy();
    },

    testDoNotOpenSubmenuByMouseDown : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      menuItem.setSubMenu( subMenu );
      var subMenuItem = new MenuItem( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      rwt.remote.ObjectRegistry.add( "w8", subMenuItem, menuItemHandler );
      TestUtil.flush();

      TestUtil.mouseOver( menuItem );
      TestUtil.click( menuItem );
      TestUtil.flush();

      assertFalse( subMenu.isSeeable() );
      assertTrue( menuItem.hasState( "over" ) );
      subMenu.destroy();
    },

    testCheckSelection : function() {
      openSimpleMenu( "check" );
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
    },

    testMenuShowEvent : function() {
      createMenuBar( "push" );
      rwt.remote.ObjectRegistry.add( "w3", menu, menuHandler );
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
      TestUtil.fakeListener( menu, "Show", true );
      TestUtil.fakeListener( menu, "Hide", true );
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
    },

    testExecutePushItem : function() {
      openSimpleMenu( "push" );
      rwt.remote.ObjectRegistry.add( "w3", menuItem, menuItemHandler );
      TestUtil.flush();
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.fakeListener( menuItem, "Selection", true );
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertFalse( menuItem.hasState( "selected" ) );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "Selection" ) );
      assertNull( TestUtil.getMessageObject().findSetOperation( "w3", "selection" ) );
      TestUtil.clearRequestLog();
    },

    //See Bug 434306 - [Menu] Application crashes when the preItem is clicked
    testExecutePreItem_doNotSendSelection : function() {
      openSimpleMenu( "push" );
      menu.hide();
      TestUtil.fakeListener( menu, "Show", true );
      menu.show();
      TestUtil.flush();
      TestUtil.clearRequestLog();

      TestUtil.click( menu._preItem );

      assertTrue( menu._preItem.isSeeable() );
      assertEquals( 0, TestUtil.getRequestsSend() );
    },

    testExecuteCheckItem: function() {
      openSimpleMenu( "check" );
      rwt.remote.ObjectRegistry.add( "w3", menuItem, menuItemHandler );
      TestUtil.flush();
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 0, TestUtil.getRequestsSend() );
      assertTrue( menuItem.hasState( "selected" ) );
      TestUtil.clearRequestLog();
      TestUtil.fakeListener( menuItem, "Selection", true );
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
    },

    testExecuteRadioButton : function() {
      openSimpleMenu( "radio" );
      rwt.remote.ObjectRegistry.add( "w3", menuItem, menuItemHandler );
      TestUtil.flush();
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 0, TestUtil.getRequestsSend() );
      menuItem.setSelection( false );
      TestUtil.fakeListener( menuItem, "Selection", true );
      TestUtil.clearRequestLog();

      TestUtil.click( menuItem );

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( menuItem.hasState( "selected" ) );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w3", "selection" ) );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "Selection" ) );
    },

    testExecuteRadioButton_DeselectSiblings : function() {
      openSimpleMenu( "radio" );
      rwt.remote.ObjectRegistry.add( "w3", menuItem, menuItemHandler );
      TestUtil.flush();
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 0, TestUtil.getRequestsSend() );
      menuItem.setSelection( false );
      TestUtil.fakeListener( menuItem, "Selection", true );
      var item2 = new MenuItem( "radio" );
      menu.addMenuItemAt( item2, 0 );
      rwt.remote.ObjectRegistry.add( "w2", item2, menuItemHandler );
      item2.setSelection( true );
      TestUtil.fakeListener( item2, "Selection", true );
      TestUtil.clearRequestLog();

      TestUtil.click( menuItem );

      assertEquals( 2, TestUtil.getRequestsSend() );
      assertFalse( item2.hasState( "selected" ) );
      assertFalse( TestUtil.getMessageObject( 0 ).findSetProperty( "w2", "selection" ) );
      assertNotNull( TestUtil.getMessageObject( 0 ).findNotifyOperation( "w2", "Selection" ) );
      assertTrue( menuItem.hasState( "selected" ) );
      assertTrue( TestUtil.getMessageObject( 1 ).findSetProperty( "w3", "selection" ) );
      assertNotNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "w3", "Selection" ) );
    },

    testExecuteSelectedRadioButton : function() {
      openSimpleMenu( "radio" );
      rwt.remote.ObjectRegistry.add( "w3", menuItem, menuItemHandler );
      TestUtil.flush();
      TestUtil.clearRequestLog();
      TestUtil.click( menuItem );
      assertEquals( 0, TestUtil.getRequestsSend() );
      menuItem.setSelection( true );
      TestUtil.fakeListener( menuItem, "Selection", true );
      TestUtil.clearRequestLog();

      TestUtil.click( menuItem );

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( menuItem.hasState( "selected" ) );
      assertNull( TestUtil.getMessageObject().findSetOperation( "w3", "selection" ) );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "Selection" ) );
    },

    testExecuteRadioButton_NoRadioGroup : function() {
      openSimpleMenu( "radio" );
      rwt.remote.ObjectRegistry.add( "w3", menuItem, menuItemHandler );
      menuItem.setNoRadioGroup( true );
      TestUtil.fakeListener( menuItem, "Selection", true );
      var menuItem2 = new MenuItem( "radio" );
      menu.addMenuItemAt( menuItem2, 0 );
      rwt.remote.ObjectRegistry.add( "w2", menuItem2, menuItemHandler );
      menuItem2.setNoRadioGroup( true );
      TestUtil.fakeListener( menuItem2, "Selection", true );
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

    testExecuteSubItem_resetCaptureWidget : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      var subMenuItem = new MenuItem( "push" );
      rwt.remote.ObjectRegistry.add( "w71", subMenuItem, menuItemHandler );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      menuItem.setSubMenu( subMenu );
      TestUtil.flush();

      TestUtil.press( menu, "Down", true );
      TestUtil.press( menu, "Right", true );
      TestUtil.flush();
      TestUtil.press( subMenuItem, "Enter", true );
      TestUtil.flush();

      assertNull( rwt.event.EventHandler.getCaptureWidget() );
      menu.destroy();
    },

    testOpenMenuDoesReDispatchEvents : function() {
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      var logger = TestUtil.getLogger();
      text.addEventListener( "mouseover", logger.log );
      TestUtil.flush();
      openSimpleMenu();

      TestUtil.mouseOver( text );

      assertEquals( 1, logger.getLog().length );
    },

    testDisabledMenuDoesNotCaptureEvents : function() {
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      var logger = TestUtil.getLogger();
      text.addEventListener( "mouseover", logger.log );
      TestUtil.flush();
      openSimpleMenu();
      menu.setEnabled( false );

      TestUtil.mouseOver( text );

      assertEquals( 1, logger.getLog().length );
    },

    testExecute_onMouseUp : function() {
      openSimpleMenu( "push" );
      rwt.remote.ObjectRegistry.add( "w3", menuItem, menuItemHandler );
      TestUtil.fakeListener( menuItem, "Selection", true );
      TestUtil.flush();
      TestUtil.clearRequestLog();

      TestUtil.fakeMouseEventDOM( menuItem._getTargetNode(), "mousedown" );
      assertEquals( 0, TestUtil.getRequestsSend() );

      TestUtil.fakeMouseEventDOM( menuItem._getTargetNode(), "mouseup" );

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "Selection" ) );
    },

    testFocusedChild_withOpenMenu : function() {
      var button = new rwt.widgets.Button( "push" );
      button.addToDocument();
      openSimpleMenu();
      TestUtil.flush();

      TestUtil.fakeMouseEventDOM( button._getTargetNode(), "mousedown" );

      var root = rwt.event.EventHandler.getFocusRoot();
      assertTrue( button === root.getFocusedChild() );
      assertTrue( button === root.getActiveChild() );
      button.destroy();
    },

    testKeyboardControl_KeyDownHoversFirstItemAfterItemsUnhide : function() {
      openSimpleMenu();
      menu.addMenuItemAt( new MenuItem( "push" ), 1 );
      TestUtil.fakeListener( menu, "Show", true ); // force creation of preItem
      TestUtil.flush();
      menu.unhideItems( true );
      TestUtil.flush();

      TestUtil.press( menu, "Down", true );

      assertTrue( menuItem.hasState( "over" ) );
    },

    testKeyboardControl_KeyEnterSelectsItem : function() {
      openSimpleMenu();
      TestUtil.fakeListener( menuItem, "Selection", true );
      TestUtil.flush();

      TestUtil.press( menu, "Down", true );
      TestUtil.press( menu, "Enter", true );
      TestUtil.flush();

      assertFalse( menu.isSeeable() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w4", "Selection" ) );
    },

    testKeyboardControl_KeyUpHoversLastItemAfterItemsUnhide : function() {
      openSimpleMenu();
      menu.addMenuItemAt( new MenuItem( "push" ), 0 );
      TestUtil.flush();

      TestUtil.press( menu, "Up", true );

      assertTrue( menuItem.hasState( "over" ) );
    },

    testKeyboardControl_KeyDownHoversNextItem : function() {
      openSimpleMenu();
      menu.addMenuItemAt( new MenuItem( "push" ), 0 );
      TestUtil.flush();

      TestUtil.press( menu, "Down", true );
      TestUtil.press( menu, "Down", true );

      assertTrue( menuItem.hasState( "over" ) );
    },

    testKeyboardControl_KeyUpHoversPreviousItem : function() {
      openSimpleMenu();
      menu.addMenuItemAt( new MenuItem( "push" ), 1 );
      TestUtil.flush();

      TestUtil.press( menu, "Up", true );
      TestUtil.press( menu, "Up", true );

      assertTrue( menuItem.hasState( "over" ) );
    },

    testKeyboardControl_KeyDownHoversFirstItemWhenLastItemIsHovered : function() {
      openSimpleMenu();
      menu.addMenuItemAt( new MenuItem( "push" ), 1 );
      menu.addMenuItemAt( new MenuItem( "push" ), 2 );
      TestUtil.flush();

      TestUtil.press( menu, "Up", true );
      TestUtil.press( menu, "Down", true );

      assertTrue( menuItem.hasState( "over" ) );
    },

    testKeyboardControl_KeyUpHoversLastItemWhenFirstItemIsHovered : function() {
      openSimpleMenu();
      menu.addMenuItemAt( new MenuItem( "push" ), 0 );
      menu.addMenuItemAt( new MenuItem( "push" ), 0 );
      TestUtil.flush();

      TestUtil.press( menu, "Down", true );
      TestUtil.press( menu, "Up", true );

      assertTrue( menuItem.hasState( "over" ) );
    },

    testKeyboardControl_KeyRightOpensSubMenu : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      var subMenuItem = new MenuItem( "push" );
      subMenuItem.setText( "subMenuItem1" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      subMenu.addMenuItemAt( new MenuItem( "push" ), 1 );
      menuItem.setSubMenu( subMenu );
      TestUtil.flush();

      TestUtil.press( menu, "Down", true );
      TestUtil.press( menu, "Right", true );
      TestUtil.flush();

      assertTrue( menuItem.hasState( "over" ) );
      assertTrue( subMenu.isSeeable() );
      assertTrue( subMenuItem.hasState( "over") );
      subMenu.destroy();
    },

    testKeyboardControl_KeyEnterOpensSubMenu : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      var subMenuItem = new MenuItem( "push" );
      subMenuItem.setText( "subMenuItem1" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      subMenu.addMenuItemAt( new MenuItem( "push" ), 1 );
      menuItem.setSubMenu( subMenu );
      TestUtil.flush();

      TestUtil.press( menu, "Down", true );
      TestUtil.press( menu, "Enter", true );
      TestUtil.flush();

      assertTrue( menuItem.hasState( "over" ) );
      assertTrue( subMenu.isSeeable() );
      assertTrue( subMenuItem.hasState( "over") );
      subMenu.destroy();
    },

    testKeyboardControl_KeysControlTopSubMenuOnly : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      var subMenuItem = new MenuItem( "push" );
      subMenuItem.setText( "subMenuItem1" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      subMenu.addMenuItemAt( new MenuItem( "push" ), 0 );
      menuItem.setSubMenu( subMenu );
      TestUtil.flush();

      TestUtil.press( menu, "Down", true );
      TestUtil.press( menu, "Right", true );
      TestUtil.press( menu, "Down", true );
      TestUtil.flush();

      assertTrue( menuItem.hasState( "over" ) );
      assertTrue( subMenu.isSeeable() );
      assertTrue( subMenuItem.hasState( "over") );
      subMenu.destroy();
    },

    testKeyboardControl_KeyLeftClosesSubMenu : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      subMenu.addMenuItemAt( new MenuItem( "push" ), 0 );
      menuItem.setSubMenu( subMenu );
      TestUtil.flush();

      TestUtil.press( menu, "Down", true );
      TestUtil.press( menu, "Right", true );
      TestUtil.flush();
      TestUtil.press( menu, "Left", true );
      TestUtil.flush();

      assertTrue( menuItem.hasState( "over" ) );
      assertFalse( subMenu.isSeeable() );
      subMenu.destroy();
    },

    testKeyboardControl_ClosingSubMenuReturnsKeyboardControlToParent : function() {
      openSimpleMenu();
      var subMenu = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", subMenu, menuHandler );
      subMenu.addMenuItemAt( new MenuItem( "push" ), 0 );
      menuItem.setSubMenu( subMenu );
      var item2 = new MenuItem( "push" );
      menu.addMenuItemAt( item2, 1 );
      TestUtil.flush();

      TestUtil.press( menu, "Down", true );
      TestUtil.press( menu, "Right", true );
      TestUtil.flush();
      TestUtil.press( menu, "Left", true );
      TestUtil.flush();
      TestUtil.press( menu, "Down", true );
      TestUtil.flush();

      assertFalse( menuItem.hasState( "over" ) );
      assertTrue( item2.hasState( "over" ) );
      subMenu.destroy();
    },

    testContextMenuCloseOnOpenerClick : function() {
      menu = new Menu();
      rwt.remote.ObjectRegistry.add( "w3", menu, menuHandler );
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
    },

    testContextMenuCloseOnOtherContextMenu : function() {
      menu = new Menu();
      rwt.remote.ObjectRegistry.add( "w3", menu, menuHandler );
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
      rwt.remote.ObjectRegistry.add( "w4", menu2, menuHandler );
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
    },

    testContextMenuCloseOnMenuBarClick : function() {
      createMenuBar( "push" );
      var menu2 = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", menu2, menuHandler );
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
      assertTrue( menuBar.isSeeable() );
      assertTrue( menuBarItem.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      TestUtil.rightClick( widget );
      TestUtil.flush();
      assertTrue( menu2.isSeeable() );
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
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
      assertTrue( menuBar.isSeeable() );
      assertTrue( menuBarItem.isSeeable() );
      assertFalse( menu.isSeeable() );
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
    },

    testDropDownMenuCloseOnSameMenuBarClick : function() {
      createMenuBar( "push" );
      var menu2 = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", menu2, menuHandler );
      var menuItem2 = new MenuItem( "push" );
      menuItem2.setText( "bla" );
      menu2.addMenuItemAt( menuItem2, 0 );
      var barItem2 = new MenuItem( "bar" );
      rwt.remote.ObjectRegistry.add( "w8", barItem2, menuItemHandler );
      barItem2.setMenu( menu2 );
      menuBar.addMenuItemAt( barItem2, 1 );
      TestUtil.flush();
      assertTrue( menuBar.isSeeable() );
      assertTrue( menuBarItem.isSeeable() );
      assertTrue( barItem2.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      TestUtil.click( menuBarItem );
      TestUtil.flush();
      assertTrue( menu.isSeeable() );
      TestUtil.click( barItem2 );
      TestUtil.flush();
      assertFalse( menu.isSeeable() );
      assertTrue( menu2.isSeeable() );
      menu2.destroy();
      menuItem2.destroy();
      barItem2.destroy();
    },

    testDropDownMenuCloseOnOtherMenuBarClick : function() {
      createMenuBar( "push" );
      var menuBar2 = new MenuBar();
      menuBar2.addToDocument();
      var menu2 = new Menu();
      rwt.remote.ObjectRegistry.add( "w7", menu2, menuHandler );
      var menuItem2 = new MenuItem( "push" );
      menuItem2.setText( "bla" );
      menu2.addMenuItemAt( menuItem2, 0 );
      var barItem2 = new MenuItem( "bar" );
      rwt.remote.ObjectRegistry.add( "w8", barItem2, menuItemHandler );
      barItem2.setMenu( menu2 );
      menuBar2.addMenuItemAt( barItem2, 0 );
      TestUtil.flush();
      assertTrue( menuBar.isSeeable() );
      assertTrue( menuBar2.isSeeable() );
      assertTrue( menuBarItem.isSeeable() );
      assertTrue( barItem2.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      TestUtil.click( menuBarItem );
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
    },

    testDisposeWithAnimation : function() {
      openSimpleMenu( "push" );
      TestUtil.flush();
      menu.setAnimation( {
        "slideIn" : [ 100, "easeIn" ]
      } );
      TestUtil.fakeListener( menu, "Show", true );
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
      openSimpleMenu( "push" );
      rwt.remote.ObjectRegistry.add( "w321", menu, menuHandler );
      menu.hide();
      TestUtil.flush();
      menu.setAnimation( {
        "slideIn" : [ 100, "easeIn" ]
      } );
      TestUtil.fakeListener( menu, "Show", true );
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
      rwt.remote.ObjectRegistry.add( "w3", menu, menuHandler );
      menuItem = new MenuItem( "push" );
      menuItem.setText( "bla" );
      menu.addMenuItemAt( menuItem, 0 );
      var widget = new rwt.widgets.base.Terminator();
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
    },

    testGetAllowContextMenu_Text : function() {
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      TestUtil.flush();
      var element = text.getElement().getElementsByTagName( "input" )[ 0 ];

      assertTrue( rwt.widgets.Menu.getAllowContextMenu( text, element ) );
      text.destroy();
    },

    testGetAllowContextMenu_Disabled : function() {
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      TestUtil.flush();
      var element = text.getElement().getElementsByTagName( "input" )[ 0 ];

      text.setEnabled( false );

      assertFalse( rwt.widgets.Menu.getAllowContextMenu( text, element ) );
      text.destroy();
    },

    testGetAllowContextMenu_HasMenuWidget : function() {
      var text = new rwt.widgets.Text( false );
      text.addToDocument();
      TestUtil.flush();
      var element = text.getElement().getElementsByTagName( "input" )[ 0 ];

      text.setContextMenu( new rwt.widgets.Menu() );

      assertFalse( rwt.widgets.Menu.getAllowContextMenu( text, element ) );
      text.destroy();
    },

    testGetAllowContextMenu_MultiText : function() {
      var text = new rwt.widgets.Text( true );
      text.addToDocument();
      TestUtil.flush();
      var element = text.getElement().getElementsByTagName( "textarea" )[ 0 ];

      assertTrue( rwt.widgets.Menu.getAllowContextMenu( text, element ) );
      text.destroy();
    },

    testGetAllowContextMenu_LabelLink : function() {
      var label = new rwt.widgets.Label( { "MARKUP_ENABLED" : true } );
      label.addToDocument();

      label.setText( "foo<a href='asdf.html' >bar</a>foo" );
      TestUtil.flush();

      var element = label.getElement().getElementsByTagName( "a" )[ 0 ];
      assertTrue( rwt.widgets.Menu.getAllowContextMenu( label, element ) );
      label.destroy();
    },

    testGetAllowContextMenu_LabelLinkNoHref : function() {
      var label = new rwt.widgets.Label( { "MARKUP_ENABLED" : true } );
      label.addToDocument();

      label.setText( "foo<a>bar</a>foo" );
      TestUtil.flush();

      var element = label.getElement().getElementsByTagName( "a" )[ 0 ];
      assertFalse( rwt.widgets.Menu.getAllowContextMenu( label, element ) );
      label.destroy();
    },

    testGetAllowContextMenu_Grid : function() {
      var args = { "appearance": "tree" };
      args[ "selectionPadding" ] = [ 2, 4 ];
      args[ "indentionWidth" ] = 16;
      var tree = new rwt.widgets.Grid( args );
      tree.setItemHeight( 20 );
      tree.setItemMetrics( 0, 0, 500, 0, 0, 0, 500, 0, 10 );
      tree.addToDocument();
      TestUtil.flush();
      tree.getRenderConfig().markupEnabled = true;
      tree.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( tree.getRootItem(), 0, false );

      item.setTexts( [ "<a href=\"foo\">Test</a>" ] );
      TestUtil.flush();

      var row = tree.getRowContainer().getRow( 0 ).$el.get( 0 );
      var element = row.getElementsByTagName( "a" )[ 0 ];
      assertTrue( rwt.widgets.Menu.getAllowContextMenu( tree.getRowContainer(), element ) );
      tree.destroy();
    },

    testGetAllowContextMenu_List : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var list = new rwt.widgets.List( true );
      list.setItemDimensions( 100, 20 );
      list.addToDocument();
      list.setMarkupEnabled( true );

      list.setItems( [ "<a href=\"foo\" >Test</a>" ] );
      TestUtil.flush();

      var item = list.getItems()[ 0 ];
      var element = item.getElement().getElementsByTagName( "a" )[ 0 ];
      assertTrue( rwt.widgets.Menu.getAllowContextMenu( item, element ) );
      list.destroy();
      rwt.remote.EventUtil.setSuspended( false );
    },

    testGetAllowContextMenu_ListTargetIsRWT : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var list = new rwt.widgets.List( true );
      list.setItemDimensions( 100, 20 );
      list.addToDocument();
      list.setMarkupEnabled( true );

      list.setItems( [ "<a href=\"foo\" target=\"_rwt\">Test</a>" ] );
      TestUtil.flush();

      var item = list.getItems()[ 0 ];
      var element = item.getElement().getElementsByTagName( "a" )[ 0 ];
      assertFalse( rwt.widgets.Menu.getAllowContextMenu( item, element ) );
      list.destroy();
      rwt.remote.EventUtil.setSuspended( false );
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

    // should not throw error - see bug 420981
    testLeftArrowPressWithNonMenuItemOpener : function() {
      var menu = createMenuWithItems( "push", 3 );
      menu.setOpener( menu.getParent() );

      TestUtil.press( menu, "Left" );

      menu.destroy();
    },

    testMenuIsNotFocusRoot : function() {
      openSimpleMenu( "push" );

      assertFalse( menu.isFocusRoot() );
      assertNull( menu.getFocusRoot() );
      menu.destroy();
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

var createPopUpMenuByProtocol = function( id ) {
  TestUtil.createShellByProtocol( "w2" );
  MessageProcessor.processOperation( {
    "target" : id,
    "action" : "create",
    "type" : "rwt.widgets.Menu",
    "properties" : {
      "style" : [ "POP_UP" ],
      "parent" : "w2"
    }
  } );
  return ObjectRegistry.getObject( id );
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

var openSimpleMenu = function( type ) {
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
  TestUtil.flush();
  TestUtil.clearRequestLog();
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

var destroy = function() {
  for( var i = 0; i < arguments.length; i++ ) {
    if( arguments[ i ] ) {
      arguments[ i ].destroy();
    }
  }
};

}());
