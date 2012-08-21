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

qx.Class.define( "org.eclipse.rwt.test.tests.MenuTest", {

  extend : qx.core.Object,

  construct : function() {
    this.base( arguments );
    this._menuClass = org.eclipse.rwt.widgets.Menu;
    this._menuItemClass = org.eclipse.rwt.widgets.MenuItem;
    this._menuBarClass = org.eclipse.rwt.widgets.MenuBar;
    this._menuBarItemClass = org.eclipse.rwt.widgets.MenuItem;
    this.TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
  },

  members : {
    menu : null,
    menuItem : null,
    menuBar : null,
    menuBarItem : null,

    testCreateMenuBarByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "BAR" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.MenuBar );
      assertIdentical( shell, widget.getParent() );
      assertNull( widget.getUserData( "isControl") );
      shell.destroy();
      widget.destroy();
    },

    testCreatePopUpMenuByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "POP_UP" ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.Menu );
      assertIdentical( qx.ui.core.ClientDocument.getInstance(), widget.getParent() );
      assertNull( widget.getUserData( "isControl") );
      widget.destroy();
    },

    testSetMenuBarBoundsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "BAR" ],
          "parent" : "w2",
          "bounds" : [ 1, 2, 3, 4 ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 1, widget.getLeft() );
      assertEquals( 2, widget.getTop() );
      assertEquals( 3, widget.getWidth() );
      assertEquals( 4, widget.getHeight() );
      shell.destroy();
      widget.destroy();
    },

    testSetEnabledByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "BAR" ],
          "parent" : "w2",
          "enabled" : false
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertFalse( widget.getEnabled() );
      shell.destroy();
      widget.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "BAR" ],
          "parent" : "w2",
          "customVariant" : "variant_blue"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasState( "variant_blue" ) );
      shell.destroy();
      widget.destroy();
    },

    testCallShowMenuByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createPopUpMenuByProtocol( "w3" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createPopUpMenuByProtocol( "w3" );
      widget.setHasMenuListener( true );
      widget._menuShown();
      assertTrue( widget._itemsHiddenFlag );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      var widget = this._createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.MenuItem );
      assertIdentical( menu._layout, widget.getParent() );
      assertNull( widget.getUserData( "isControl") );
      menu.destroy();
      widget.destroy();
    },

    testCreateMenuItemSeparatorByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      var widget = this._createMenuItemByProtocol( "w4", "w3", [ "SEPARATOR" ] );
      assertTrue( widget instanceof qx.ui.menu.Separator );
      assertIdentical( menu._layout, widget.getParent() );
      assertNull( widget.getUserData( "isControl") );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemIndexByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      this._createMenuItemByProtocol( "w4", "w3", [ "PUSH" ] );
      var widget = this._createMenuItemByProtocol( "w5", "w3", [ "PUSH" ] );
      assertIdentical( menu._layout.getChildren()[ 0 ], widget );
      menu.destroy();
      widget.destroy();
    },

    testSetMeniItemNoRadioGroupByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      menu.addState( "rwt_NO_RADIO_GROUP" );
      var widget = this._createMenuItemByProtocol( "w4", "w3", [ "RADIO" ] );
      assertTrue( widget._noRadioGroup );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemSubMenuByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      var submenu = this._createPopUpMenuByProtocol( "w5" );
      var widget = this._createMenuItemByProtocol( "w4", "w3", [ "CASCADE" ] );
      TestUtil.protocolSet( "w4", { "menu" : "w5" } )
      assertIdentical( submenu, widget.getMenu() );
      menu.destroy();
      submenu.destroy();
      widget.destroy();
    },

    testSetMenuItemEnabledByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      var widget = this._createMenuItemByProtocol( "w4", "w3", [ "CASCADE" ] );
      TestUtil.protocolSet( "w4", { "enabled" : false } )
      assertFalse( widget.getEnabled() );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      var widget = this._createMenuItemByProtocol( "w4", "w3", [ "RADIO" ] );
      TestUtil.protocolSet( "w4", { "text" : "foo >\t Ctrl+1" } )
      assertEquals( "foo &gt;", widget.getCellContent( 2 ) );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemImageByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      var widget = this._createMenuItemByProtocol( "w4", "w3", [ "RADIO" ] );
      TestUtil.protocolSet( "w4", { "image" : [ "image.gif", 10, 20 ] } )
      assertEquals( "image.gif", widget.getCellContent( 1 ) );
      assertEquals( 10, widget.getPreferredCellWidth( 1 ) );
      assertEquals( 20, widget.getCellHeight( 1 ) );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemSelectionByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      var widget = this._createMenuItemByProtocol( "w4", "w3", [ "CHECK" ] );
      TestUtil.protocolSet( "w4", { "selection" : true } )
      assertTrue( widget._selected );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuItemSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      var widget = this._createMenuItemByProtocol( "w4", "w3", [ "CHECK" ] );
      TestUtil.protocolListen( "w4", { "selection" : true } )
      assertTrue( widget._hasSelectionListener );
      menu.destroy();
      widget.destroy();
    },

    testSetMenuListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = this._createPopUpMenuByProtocol( "w3" );
      TestUtil.protocolListen( "w3", { "menu" : true } )
      assertTrue( menu._hasListener );
      menu.destroy();
    },

    testTextOnly : function() {
      this.createSimpleMenu( "push" );
      this.menuItem.setText( "Hello World!" );
      this.TestUtil.flush();
      assertEquals( 1, this.menuItem._getTargetNode().childNodes.length );
      assertEquals(
        "Hello World!",
        this.menuItem._getTargetNode().firstChild.innerHTML
      );
      this.disposeMenu();
    },

    testImageOnly : function() {
      this.createSimpleMenu( "push" );
      this.menuItem.setImage( "url.jpg", 20, 30 );
      this.TestUtil.flush();
      assertEquals( 1, this.menuItem._getTargetNode().childNodes.length );
      var node = this.menuItem._getTargetNode().firstChild;
      assertContains(
        "url.jpg",
        this.TestUtil.getCssBackgroundImage ( node )
      );
      var bounds = this.TestUtil.getElementBounds( node );
      assertEquals( 20, bounds.width );
      assertEquals( 30, bounds.height );
      this.disposeMenu();
    },

    testArrowOnly : function() {
      this.createSimpleMenu( "push" );
      this.menuItem.setArrow( [ "url.jpg", 13, 13 ] );
      this.TestUtil.flush();
      assertEquals( 1, this.menuItem._getTargetNode().childNodes.length );
      var node = this.menuItem._getTargetNode().firstChild;
      assertContains(
        "url.jpg",
        this.TestUtil.getCssBackgroundImage( node )
      );
      this.disposeMenu();
    },

    testMenuResize : function() {
      this.createSimpleMenu( "push" );
      this.menuItem.setSpacing( 3 );
      this.TestUtil.flush();
      var menuNode = this.menu.getElement();
      var itemNode = this.menuItem.getElement();
      var oldMenuBounds = this.TestUtil.getElementBounds( menuNode );
      assertTrue( this.menuItem.getWidth() === "auto" );
      this.menuItem.setText( "bla! " );
      this.TestUtil.flush();
      var newMenuBounds = this.TestUtil.getElementBounds( menuNode );
      // Theory: Fore some reason the _cachedPreferredInnerWidth is invalidated before/during(?)
      // initial flush, then not recomputed, leaving it null without a jobQueue entry...?
      assertLarger( oldMenuBounds.width, newMenuBounds.width );
      oldMenuBounds = newMenuBounds;
      var item2 = new this._menuItemClass( "push" );
      item2.setText( "blubblubblub!" );
      item2.setSpacing( 3 );
      this.menu.addMenuItemAt( item2, 0 );
      var itemNode1 = this.menuItem._getTargetNode();
      var oldItemBounds1 = this.TestUtil.getElementBounds( itemNode1 );
      this.TestUtil.flush();
      newMenuBounds = this.TestUtil.getElementBounds( menuNode );
      var itemNode2 = item2._getTargetNode();
      var itemBounds1 = this.TestUtil.getElementBounds( itemNode1 );
      var itemBounds2 = this.TestUtil.getElementBounds( itemNode2 );
      assertLarger( oldMenuBounds.height, newMenuBounds.height );
      assertLarger( oldItemBounds1.width, itemBounds1.width );
      assertEquals( itemBounds1.width, itemBounds2.width );
      oldItemBounds1 = itemBounds1;
      oldMenuBounds = newMenuBounds;
      item2.setText( "-" );
      this.TestUtil.flush();
      newMenuBounds = this.TestUtil.getElementBounds( menuNode );
      itemBounds1 = this.TestUtil.getElementBounds( itemNode1 );
      itemBounds2 = this.TestUtil.getElementBounds( itemNode2 );
      assertSmaller( oldMenuBounds.width, newMenuBounds.width );
      assertSmaller( oldItemBounds1.width, itemBounds1.width );
      assertEquals( itemBounds1.width, itemBounds2.width );
      oldMenuBounds = newMenuBounds;
      this.menuItem.setArrow( [ "bla.jpg", 13, 13 ] );
      this.TestUtil.flush();
      newMenuBounds = this.TestUtil.getElementBounds( menuNode );
      // the dimension of arrow are at least 13 and shouldn't have changed
      assertEquals( oldMenuBounds.width, newMenuBounds.width );
      oldMenuBounds = newMenuBounds;
      this.menuItem.setImage( "bla.jpg" , 30, 30 );
      this.TestUtil.flush();
      newMenuBounds = this.TestUtil.getElementBounds( menuNode );
      assertEquals( oldMenuBounds.width + 33, newMenuBounds.width );
      oldMenuBounds = newMenuBounds;
      this.menuItem.setImage( null, 0, 0 );
      this.TestUtil.flush();
      newMenuBounds = this.TestUtil.getElementBounds( menuNode );
      assertEquals( oldMenuBounds.width - 33, newMenuBounds.width );
      this.disposeMenu();
    },

    testItemHover : function() {
      this.createSimpleMenu( "push" );
      this.TestUtil.flush();
      this.TestUtil.mouseOver( this.menuItem );
      assertTrue( this.menuItem.hasState( "over" ) );
      this.TestUtil.mouseOut( this.menu );
      assertFalse( this.menuItem.hasState( "over" ) );
      this.disposeMenu();
    },

    testMenuLayout : function() {
      this.createSimpleMenu( "push" );
      this.menuItem.setText( "hello" );
      var item2 = new this._menuItemClass( "push" );
      item2.setText( "bla!" );
      this.menu.addMenuItemAt( item2, 0 );
      var item3 = new this._menuItemClass( "push" );
      item3.setText( "blabla!" );
      this.menu.addMenuItemAt( item3, 0 );
      var item4 = new this._menuItemClass( "push" );
      item4.setText( "blubblubblub!" );
      this.menu.addMenuItemAt( item4, 0 );
      var item5 = new this._menuItemClass( "push" );
      item5.setText( "asdfasdf!" );
      this.menu.addMenuItemAt( item5, 0 );
      this.TestUtil.flush();
      assertTrue( this.itemsXLayoutIsIdentical( this.menu ) );
      item2.setImage( "bla.jpg", 20, 20  );
      this.TestUtil.flush();
      assertTrue( this.itemsXLayoutIsIdentical( this.menu ) );
      item3.setImage( "bla.jpg", 40, 40  );
      this.TestUtil.flush();
      assertTrue( this.itemsXLayoutIsIdentical( this.menu ) );
      item3.setImage( null, 0, 0  );
      item2.setArrow( [ "bla.jpg", 13, 13 ] );
      item2.setSelectionIndicator( [ "bla.jpg", 13, 13 ] );
      this.TestUtil.flush();
      assertTrue( this.itemsXLayoutIsIdentical( this.menu ) );
      item2.setImage( null, 0, 0  );
      item2.setArrow( null );
      item2.setSelectionIndicator( null );
      this.TestUtil.flush();
      assertTrue( this.itemsXLayoutIsIdentical( this.menu ) );
      this.disposeMenu();
    },

    testOpenMenuByMenuBar : function() {
      this.createMenuBar( "push" );
      this.TestUtil.flush();
      var bar = this.menuBar;
      var barItem = this.menuBarItem;
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      this.TestUtil.click( barItem );
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.TestUtil.click( this.TestUtil.getDocument() );
      this.TestUtil.flush();
      assertFalse( this.menu.isSeeable() );
      this.TestUtil.click( barItem );
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.TestUtil.click( this.TestUtil.getDocument() );
      this.TestUtil.flush();
      assertFalse( this.menu.isSeeable() );
      this.disposeMenuBar();
    },

    testOpenMenuAsContextmenu : function() {
      this.menu = new this._menuClass();
      this.menuItem = new this._menuItemClass( "push" );
      this.menuItem.setText( "bla" );
      this.menu.addMenuItemAt( this.menuItem, 0 );
      var widget = this._createControl();
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( this.menu );
      this._addContextMenuListener( widget );
      this.TestUtil.flush();
      assertTrue( widget.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      this.TestUtil.rightClick( widget );
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.TestUtil.click( this.TestUtil.getDocument() );
      this.TestUtil.flush();
      assertFalse( this.menu.isSeeable() );
      widget.setContextMenu( null );
      this._removeContextMenuListener( widget );
      widget.setParent( null );
      widget.dispose();
      this.disposeMenu();
    },

    testContextMenuOpenOnControl : function() {
      this.TestUtil.fakeResponse( true );
      var menu1 = new org.eclipse.rwt.widgets.Menu();
      menu1.setHasMenuListener( true );
      var parent = this._createControl();
      parent.addToDocument();
      parent.setContextMenu( menu1 );
      this._addContextMenuListener( parent );
      var widget = new qx.ui.basic.Atom( "bla" );
      widget.setParent( parent );
      this.TestUtil.flush();
      assertFalse( menu1.isSeeable() );
      this.TestUtil.rightClick( widget );
      assertTrue( menu1.isSeeable() );
      menu1.destroy();
      widget.destroy();
      parent.destroy();
      this.TestUtil.fakeResponse( false );
    },

    testContextMenuOpenOnText : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeResponse( true );
      var menu = new org.eclipse.rwt.widgets.Menu();
      menu.setHasMenuListener( true );
      var text = new org.eclipse.rwt.widgets.Text( false );
      text.addToDocument();
      text.setUserData( "isControl", true );
      text.setContextMenu( menu );
      this._addContextMenuListener( text );
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
      var menu1 = new org.eclipse.rwt.widgets.Menu();
      menu1.setHasMenuListener( true );
      var parent = this._createControl();
      parent.addToDocument();
      parent.setContextMenu( menu1 );
      this._addContextMenuListener( parent );
      var widget = this._createControl();
      widget.setParent( parent );
      this.TestUtil.flush();
      assertFalse( menu1.isSeeable() );
      this.TestUtil.rightClick( widget );
      assertFalse( menu1.isSeeable() );
      menu1.destroy();
      widget.destroy();
      parent.destroy();
    },

    testDropDownExecuteMenuItem : function() {
      this.createMenuBar( "push" );
      this.executed = false;
      var command = function() {
        this.executed = true;
      };
      this.menuItem.addEventListener( "execute", command, this );
      this.TestUtil.flush(); // cannnot click until created
      this.TestUtil.click( this.menuBarItem );
      this.TestUtil.flush();
      assertFalse( this.executed );
      assertTrue( this.menu.isSeeable() );
      this.TestUtil.click( this.menuItem );
      this.TestUtil.flush();
      assertTrue( this.executed );
      assertFalse( this.menu.isSeeable() );
      delete this.executed;
      this.disposeMenuBar();
    },

    testExecuteWithoutOpener : function() {
      this.createSimpleMenu( "push" );
      this.executed = false;
      var command = function() {
        this.executed = true;
      };
      this.menuItem.addEventListener( "execute", command, this );
      this.TestUtil.flush(); // cannnot click until created
      assertTrue( this.menu.isSeeable() );
      this.TestUtil.click( this.menuItem );
      this.TestUtil.flush();
      assertTrue( this.executed );
      assertFalse( this.menu.isSeeable() );
      delete this.executed;
      this.disposeMenu();
    },

    testOpenSubmenuByMouse : function() {
      this.TestUtil.prepareTimerUse();
      this.createMenuBar( "cascade" );
      var subMenu = new this._menuClass();
      var subMenuItem = new this._menuItemClass( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      this.menuItem.setSubMenu( subMenu );
      var item2 = new this._menuItemClass( "push" );
      item2.setText( "bla!" );
      this.menu.addMenuItemAt( item2, 0 );
      this.TestUtil.flush();
      assertFalse( this.menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      this.TestUtil.click( this.menuBarItem );  //open menu
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      this.TestUtil.mouseOver( this.menuItem ); //starting open-timer
      assertTrue( this.menu._openTimer.isEnabled() );
      assertFalse( this.menu._closeTimer.isEnabled() );
      this.TestUtil.click( this.menuItem ); //clicking does nothing
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      this.TestUtil.forceInterval( this.menu._openTimer );  //timer opens submenu
      this.TestUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( this.menuItem.hasState( "over" ) );
      this.TestUtil.mouseFromTo( this.menuItem, item2 );
      // hovering another item starts the close-timer, but not the open-timer
      // since the item has no submenu
      assertFalse( this.menu._openTimer.isEnabled() );
      assertTrue( this.menu._closeTimer.isEnabled() );
      this.TestUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( item2.hasState( "over" ) );
      assertFalse( this.menuItem.hasState( "over" ) );
      this.TestUtil.forceInterval( this.menu._closeTimer );  //timer closes submenu
      this.TestUtil.flush();
      assertFalse( subMenu.isSeeable() );
      assertTrue( this.menu.isSeeable() );
      assertFalse( this.menuItem.hasState( "over" ) );
      assertTrue( item2.hasState( "over" ) );
      // re-open the submenu
      this.TestUtil.mouseFromTo( item2, this.menuItem );
      this.TestUtil.forceInterval( this.menu._openTimer );
      this.TestUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( this.menuItem.hasState( "over" ) );
      this.TestUtil.mouseFromTo( this.menuItem, subMenu );
      this.TestUtil.mouseFromTo( subMenu, subMenuItem );
      assertFalse( this.menu._closeTimer.isEnabled() );
      assertFalse( this.menu._openTimer.isEnabled() );
      this.TestUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( subMenuItem.hasState( "over"  ) );
      assertTrue( this.menuItem.hasState( "over" ) );
      assertIdentical( this.menuBarItem, this.menu.getOpener() );
      assertIdentical( this.menuItem, this.menu._openItem );
      assertIdentical( this.menuItem, subMenu.getOpener() );
      assertNull( subMenu._openItem );
      // now click:
      this.TestUtil.click( subMenuItem );
      this.TestUtil.flush();
      assertFalse( this.menu._closeTimer.isEnabled() );
      assertFalse( this.menu._openTimer.isEnabled() );
      assertFalse( subMenu.isSeeable() );
      assertFalse( "menu is gone", this.menu.isSeeable() );
      assertNull( this.menu._openItem );
      assertFalse( subMenuItem.hasState( "over" ) );
      assertFalse( this.menuItem.hasState( "pressed" ) );
      subMenuItem.setParent( null );
      subMenuItem.dispose();
      subMenu.setParent( null );
      subMenu.dispose();
      item2.setParent( null );
      item2.dispose();
      this.disposeMenuBar();
    },

    testCheckSelection : function() {
      this.createSimpleMenu( "check" );
      this.TestUtil.flush();
      assertEquals( 1, this.menuItem._getTargetNode().childNodes.length );
      var node = this.menuItem._getTargetNode().firstChild;
      assertEquals(
        "",
        this.TestUtil.getCssBackgroundImage( node )
      )
      this.TestUtil.click( this.menuItem );
      this.menuItem.setSelectionIndicator( [ "url.jpg", 13, 13 ]);
      this.TestUtil.flush();
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertContains(
        "url.jpg",
        this.TestUtil.getCssBackgroundImage( node )
      );
      this.TestUtil.click( this.menuItem );
      this.menuItem.setSelectionIndicator( null );
      assertFalse( this.menuItem.hasState( "selected" ) );
      this.TestUtil.flush();
      assertEquals( 1, this.menuItem._getTargetNode().childNodes.length );
      assertEquals(
        "",
        this.TestUtil.getCssBackgroundImage( node )
      );
      this.disposeMenu();
    },

    testMenuShowEvent : function() {
      this.createMenuBar( "push" );
      org.eclipse.swt.WidgetManager.getInstance().add( this.menu, "w3" );
      this.TestUtil.clearRequestLog();
      this.TestUtil.flush();
      this.TestUtil.click( this.menuBarItem );
      this.TestUtil.flush();
      assertEquals( 0, this.TestUtil.getRequestsSend() );
      assertTrue( this.menu.isSeeable() );
      assertTrue( this.menuItem.isSeeable() );
      this.TestUtil.click( this.TestUtil.getDocument() );
      this.TestUtil.flush();
      assertEquals( 0, this.TestUtil.getRequestsSend() );
      assertFalse( this.menu.isSeeable() );
      this.menu.setHasMenuListener( true );
      this.TestUtil.click( this.menuBarItem );
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      assertFalse( this.menuItem.isSeeable() );
      assertTrue( this.menu._preItem.isSeeable() );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      var msg = this.TestUtil.getMessage();
      assertContains( "org.eclipse.swt.events.menuShown",  msg );
      this.TestUtil.clearRequestLog();
      this.menu.unhideItems( true );
      this.TestUtil.flush();
      assertTrue( this.menuItem.isSeeable() );
      assertFalse( this.menu._preItem.isSeeable() );
      this.TestUtil.click( this.TestUtil.getDocument() );
      this.TestUtil.flush();
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      var msg = this.TestUtil.getMessage();
      assertContains( "org.eclipse.swt.events.menuHidden",  msg );
      this.disposeMenuBar();
    },

    testExecutePushItem : function() {
      this.createSimpleMenu( "push" );
      org.eclipse.swt.WidgetManager.getInstance().add( this.menuItem, "w3" );
      this.TestUtil.flush();
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( this.menuItem );
      assertEquals( 0, this.TestUtil.getRequestsSend() );
      this.menuItem.setHasSelectionListener( true );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( this.menuItem );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      assertFalse( this.menuItem.hasState( "selected" ) );
      var msg = this.TestUtil.getMessage();
      assertContains( "widgetSelected=w3",  msg );
      assertContainsNot( "w3.selection=true",  msg );
      this.TestUtil.clearRequestLog();
      this.disposeMenu();
    },

    testExecuteCheckItem: function() {
      this.createSimpleMenu( "check" );
      org.eclipse.swt.WidgetManager.getInstance().add( this.menuItem, "w3" );
      this.TestUtil.flush();
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( this.menuItem );
      assertEquals( 0, this.TestUtil.getRequestsSend() );
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertTrue(
        org.eclipse.swt.Server.getInstance()._parameters[ "w3.selection" ]
      );
      this.TestUtil.clearRequestLog();
      this.menuItem.setHasSelectionListener( true );
      this.menuItem.setSelection( false );
      assertFalse( this.menuItem.hasState( "selected" ) );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( this.menuItem );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertContains( "w3.selection=true",  this.TestUtil.getMessage() );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( this.menuItem );
      assertFalse( this.menuItem.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      assertContains( "w3.selection=false",  this.TestUtil.getMessage() );
      this.disposeMenu();
    },

    testExecuteRadioButton : function() {
      this.createSimpleMenu( "radio" );
      org.eclipse.swt.WidgetManager.getInstance().add( this.menuItem, "w3" );
      this.TestUtil.flush();
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( this.menuItem );
      assertEquals( 0, this.TestUtil.getRequestsSend() );
      assertTrue(
        org.eclipse.swt.Server.getInstance()._parameters[ "w3.selection" ]
      );
      this.menuItem.setSelection( false );
      this.menuItem.setHasSelectionListener( true );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( this.menuItem );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertContains( "w3.selection=true",  this.TestUtil.getMessage() );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( this.menuItem );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertContains( "w3.selection=true",  this.TestUtil.getMessage() );
      var item2 = new this._menuItemClass( "radio" );
      this.menu.addMenuItemAt( item2, 0 );
      org.eclipse.swt.WidgetManager.getInstance().add( item2, "w2" );
      item2.setHasSelectionListener( true );
      this.TestUtil.clearRequestLog();
      this.TestUtil.flush();
      this.TestUtil.click( item2 );
      assertFalse( this.menuItem.hasState( "selected" ) );
      assertTrue( item2.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      var msg = this.TestUtil.getMessage();
      assertContains( "w3.selection=false", msg );
      assertContains( "w2.selection=true", msg );
      this.TestUtil.clearRequestLog();
      // bug 328437
      this.TestUtil.click( item2 );
      assertFalse( this.menuItem.hasState( "selected" ) );
      assertTrue( item2.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      var msg = this.TestUtil.getMessage();
      assertContainsNot( "w3.selection=false", msg );
      assertContains( "w2.selection=true", msg );
      this.TestUtil.clearRequestLog();
      this.disposeMenu();
    },

    testExecuteRadioButton_NoRadioGroup : function() {
      this.createSimpleMenu( "radio" );
      org.eclipse.swt.WidgetManager.getInstance().add( this.menuItem, "w3" );
      this.menuItem.setNoRadioGroup( true );
      this.menuItem.setHasSelectionListener( true );
      var menuItem2 = new this._menuItemClass( "radio" );
      this.menu.addMenuItemAt( menuItem2, 0 );
      org.eclipse.swt.WidgetManager.getInstance().add( menuItem2, "w2" );
      menuItem2.setNoRadioGroup( true );
      menuItem2.setHasSelectionListener( true );
      this.TestUtil.clearRequestLog();
      this.TestUtil.flush();
      this.TestUtil.click( this.menuItem );
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertFalse( menuItem2.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      var msg = this.TestUtil.getMessage();
      assertContains( "w3.selection=true", msg );
      assertContainsNot( "w2.selection", msg );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( menuItem2 );
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertTrue( menuItem2.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      var msg = this.TestUtil.getMessage();
      assertContainsNot( "w3.selection", msg );
      assertContains( "w2.selection=true", msg );
      this.TestUtil.clearRequestLog();
      this.TestUtil.click( menuItem2 );
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertFalse( menuItem2.hasState( "selected" ) );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      var msg = this.TestUtil.getMessage();
      assertContainsNot( "w3.selection", msg );
      assertContains( "w2.selection=false", msg );
    },

    testKeyboardControl : function() {
      this.TestUtil.fakeResponse( true );
      this.TestUtil.prepareTimerUse();
      this.createMenuBar( "cascade" );
      var subMenu = new this._menuClass();
      this.subMenu = subMenu;
      var subMenuItem = new this._menuItemClass( "push" );
      subMenuItem.setText( "gnasdlkfn" );
      subMenu.addMenuItemAt( subMenuItem, 0 );
      var item2 = new this._menuItemClass( "push" );
      item2.setText( "blabla!" );
      this.menu.addMenuItemAt( item2, 0 );
      var dead1 = new qx.ui.menu.Separator();
      var dead2 = new this._menuItemClass( "push" );
      dead2.setText( "Disabled" );
      dead2.setEnabled( false );
      this.menu.addMenuItemAt( dead1, 0 );
      this.menu.addMenuItemAt( dead2, 0 );
      var item3 = new this._menuItemClass( "push" );
      item3.setText( "blub" );
      item3.setSubMenu( subMenu );
      this.menu.addMenuItemAt( item3, 0 );
      this.menu.setHasMenuListener( true ); // force creation of preItem
      this.TestUtil.flush();
      assertFalse( this.menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      this.TestUtil.click( this.menuBarItem );  //open menu
      this.TestUtil.flush();
      this.menu.unhideItems( true );
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      this.TestUtil.press( this.menu, "Down", true );
      assertTrue( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertFalse( this.menuItem.hasState( "over") );
      this.TestUtil.press( this.menu, "Down", true );
      assertFalse( item3.hasState( "over") );
      assertTrue( item2.hasState( "over") );
      assertFalse( this.menuItem.hasState( "over") );
      this.TestUtil.press( this.menu, "Down", true );
      assertFalse( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertTrue( this.menuItem.hasState( "over") );
      this.TestUtil.press( this.menu, "Down", true );
      assertTrue( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertFalse( this.menuItem.hasState( "over") );
      this.TestUtil.press( this.menu, "Up", true );
      assertFalse( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertTrue( this.menuItem.hasState( "over") );
      this.TestUtil.press( this.menu, "Up", true );
      assertFalse( item3.hasState( "over") );
      assertTrue( item2.hasState( "over") );
      assertFalse( this.menuItem.hasState( "over") );
      this.TestUtil.press( this.menu, "Up", true );
      assertTrue( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertFalse( this.menuItem.hasState( "over") );
      assertFalse( this.menu._openTimer.isEnabled() );
      assertFalse( this.menu._closeTimer.isEnabled() );
      this.TestUtil.press( this.menu, "Right", true );
      this.TestUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( this.TestUtil.isActive( subMenu ) );
      assertFalse( this.menuItem.hasState( "over") );
      assertFalse( item2.hasState( "over" ) );
      assertTrue( item3.hasState( "over" ) );
      assertFalse( this.menu._openTimer.isEnabled() );
      assertFalse( this.menu._closeTimer.isEnabled() );
      assertTrue( subMenuItem.hasState( "over") );
      assertFalse( this.TestUtil.isActive( this.menu ) );
      this.TestUtil.press( subMenu, "Left", true );
      this.TestUtil.flush();
      assertFalse( subMenu.isSeeable() );

      assertTrue( this.TestUtil.isActive( this.menu ) );

      assertTrue( item3.hasState( "over") );
      this.TestUtil.press( this.menu, "Enter", true );
      this.TestUtil.flush();
      assertTrue( item3.hasState( "over" ) );
      assertTrue( subMenu.isSeeable() );
      assertTrue( this.TestUtil.isActive( subMenu ) );
      assertTrue( subMenuItem.hasState( "over" ) );
      org.eclipse.swt.WidgetManager.getInstance().add( subMenuItem, "w3" );
      subMenuItem.setHasSelectionListener( true );
      this.TestUtil.clearRequestLog();
      this.TestUtil.press( subMenu, "Enter", true );
      this.TestUtil.flush();
      assertFalse( subMenu.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      assertEquals( 2, this.TestUtil.getRequestsSend() );
      var msg = this.TestUtil.getMessage();
      assertContains( "widgetSelected=w3",  msg );
      assertContainsNot( "w3.selection=true",  msg );
      this.TestUtil.clearRequestLog();
      this.disposeMenuBar();
      this.TestUtil.fakeResponse( false );
    },

    testGetMenuBar : function() {
      this.createMenuBar( "push" );
      var widget = new qx.ui.basic.Atom( "bla" );
      widget.addToDocument();
      var manager = org.eclipse.rwt.MenuManager.getInstance();
      this.TestUtil.flush();
      this.TestUtil.click( this.menuBarItem );
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      assertNull( manager._getMenuBar( widget ) );
      assertIdentical( this.menuBar, manager._getMenuBar( this.menuItem ) );
      assertIdentical( this.menuBar, manager._getMenuBar( this.menu ) );
      assertIdentical( this.menuBar, manager._getMenuBar( this.menuBarItem ) );
      assertIdentical( this.menuBar, manager._getMenuBar( this.menuBar ) );
      this.disposeMenuBar();
    },

    testContextMenuCloseOnOpenerClick : function() {
      this.menu = new this._menuClass();
      this.menuItem = new this._menuItemClass( "push" );
      this.menuItem.setText( "bla" );
      this.menu.addMenuItemAt( this.menuItem, 0 );
      var widget = this._createControl();
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( this.menu );
      this._addContextMenuListener( widget );
      this.TestUtil.flush();
      assertTrue( widget.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      this.TestUtil.rightClick( widget );
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.TestUtil.click( widget );
      this.TestUtil.flush();
      assertFalse( this.menu.isSeeable() );
      widget.setContextMenu( null );
      this._removeContextMenuListener( widget );
      widget.setParent( null );
      widget.dispose();
      this.disposeMenu();
    },

    testContextMenuCloseOnOtherContextMenu : function() {
      this.menu = new this._menuClass();
      this.menuItem = new this._menuItemClass( "push" );
      this.menuItem.setText( "bla" );
      this.menu.addMenuItemAt( this.menuItem, 0 );
      var widget = this._createControl();
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( this.menu );
      this._addContextMenuListener( widget );
      var menu2 = new this._menuClass();
      var menuItem2 = new this._menuItemClass( "push" );
      menuItem2.setText( "bla2" );
      menu2.addMenuItemAt( menuItem2, 0 );
      var widget2 = this._createControl();
      widget2.addToDocument();
      widget2.setLocation( 20, 20 );
      widget2.setDimension( 20, 20 );
      widget2.setContextMenu( menu2 );
      this._addContextMenuListener( widget2 );
      this.TestUtil.flush();
      assertTrue( widget.isSeeable() );
      assertTrue( widget2.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      this.TestUtil.rightClick( widget );
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.TestUtil.rightClick( widget2 );
      this.TestUtil.flush();
      assertTrue( menu2.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      widget.setContextMenu( null );
      widget2.setContextMenu( null );
      this._removeContextMenuListener( widget );
      this._removeContextMenuListener( widget2 );
      widget.setParent( null );
      widget2.setParent( null );
      widget.destroy();
      widget2.destroy();
      menu2.setParent( null );
      menuItem2.setParent( null )
      menu2.destroy();
      menuItem2.destroy();
      this.disposeMenu();
    },

    testContextMenuCloseOnMenuBarClick : function() {
      this.createMenuBar( "push" );
      var bar = this.menuBar;
      var barItem = this.menuBarItem;
      var menu = this.menu;
      var menu2 = new this._menuClass();
      var menuItem2 = new this._menuItemClass( "push" );
      menuItem2.setText( "bla" );
      menu2.addMenuItemAt( menuItem2, 0 );
      var widget = this._createControl();
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( menu2 );
      this._addContextMenuListener( widget );
      this.TestUtil.flush();
      assertTrue( widget.isSeeable() );
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      this.TestUtil.rightClick( widget );
      this.TestUtil.flush();
      assertTrue( menu2.isSeeable() );
      this.TestUtil.click( barItem );
      this.TestUtil.flush();
      assertTrue( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      this.disposeMenuBar();
      menu2.setParent( null );
      menuItem2.setParent( null );
      menu2.dispose();
      menuItem2.dispose();
      widget.setContextMenu( null );
      this._removeContextMenuListener( widget );
      widget.setParent( null );
      widget.dispose();
    },

    testDropDownMenuCloseOnOpenerClick : function() {
      this.createMenuBar( "push" );
      this.TestUtil.flush();
      var bar = this.menuBar;
      var barItem = this.menuBarItem;
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      this.TestUtil.click( barItem );
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.TestUtil.click( barItem );
      this.TestUtil.flush();
      assertFalse( this.menu.isSeeable() );
      this.disposeMenuBar();
    },

    testDropDownMenuCloseOnSameMenuBarClick : function() {
      this.createMenuBar( "push" );
      var bar = this.menuBar;
      var barItem = this.menuBarItem;
      var menu = this.menu;
      var menu2 = new this._menuClass();
      var menuItem2 = new this._menuItemClass( "push" );
      menuItem2.setText( "bla" );
      menu2.addMenuItemAt( menuItem2, 0 );
      barItem2 = new this._menuBarItemClass( "bar" );
      barItem2.setMenu( menu2 );
      this.menuBar.addMenuItemAt( barItem2, 1 );
      this.TestUtil.flush();
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertTrue( barItem2.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      this.TestUtil.click( barItem );
      this.TestUtil.flush();
      assertTrue( menu.isSeeable() );
      this.TestUtil.click( barItem2 );
      this.TestUtil.flush();
      assertFalse( menu.isSeeable() );
      assertTrue( menu2.isSeeable() );
      menu2.destroy();
      menuItem2.destroy();
      barItem2.destroy();
      this.disposeMenuBar();
    },

    testDropDownMenuCloseOnOtherMenuBarClick : function() {
      this.createMenuBar( "push" );
      var bar = this.menuBar;
      var barItem = this.menuBarItem;
      var menu = this.menu;
      var menuBar2 = new this._menuBarClass();
      menuBar2.addToDocument();
      var menu2 = new this._menuClass();
      var menuItem2 = new this._menuItemClass( "push" );
      menuItem2.setText( "bla" );
      menu2.addMenuItemAt( menuItem2, 0 );
      barItem2 = new this._menuBarItemClass( "bar" );
      barItem2.setMenu( menu2 );
      menuBar2.addMenuItemAt( barItem2, 0 );
      this.TestUtil.flush();
      assertTrue( bar.isSeeable() );
      assertTrue( menuBar2.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertTrue( barItem2.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      this.TestUtil.click( barItem );
      this.TestUtil.flush();
      assertTrue( menu.isSeeable() );
      this.TestUtil.click( barItem2 );
      this.TestUtil.flush();
      assertTrue( menu2.isSeeable() );
      assertFalse( menu.isSeeable() );
      menuBar2.destroy();
      menu2.destroy();
      menuItem2.destroy();
      barItem2.destroy();
      this.disposeMenuBar();
    },

    testDisposeWithAnimation : function() {
      this.createSimpleMenu( "push" );
      var menu = this.menu;
      this.TestUtil.flush();
      menu.setAnimation( {
        "slideIn" : [ 100, "easeIn" ]
      } );
      menu.setHasMenuListener( true );
      assertNotNull( menu._appearAnimation );
      var animation = menu._appearAnimation;
      var renderer = menu._appearAnimation.getDefaultRenderer();
      assertTrue( menu._hasParent );
      menu.destroy();
      this.TestUtil.flush();
      assertTrue( menu.isDisposed() );
      assertNull( menu._appearAnimation );
      assertTrue( animation.isDisposed() );
      assertTrue( renderer.isDisposed() );
      this.menu = null;
      this.menuItem = null;
    },

    testDisposeWithRunningAnimaton : function() {
      this.createSimpleMenu( "push" );
      var menu = this.menu;
      menu.hide();
      this.TestUtil.flush();
      menu.setAnimation( {
        "slideIn" : [ 100, "easeIn" ]
      } );
      menu.setHasMenuListener( true );
      assertNotNull( menu._appearAnimation );
      var animation = menu._appearAnimation;
      var renderer = menu._appearAnimation.getDefaultRenderer();
      menu.show();
      org.eclipse.rwt.Animation._mainLoop();
      assertTrue( menu._appearAnimation.isRunning() );
      menu.unhideItems();
      org.eclipse.rwt.Animation._mainLoop();
      menu.destroy();
      this.TestUtil.flush();
      org.eclipse.rwt.Animation._mainLoop();
      assertTrue( menu.isDisposed() );
      assertNull( menu._appearAnimation );
      assertTrue( animation.isDisposed() );
      assertTrue( renderer.isDisposed() );
      this.menu = null;
      this.menuItem = null;
    },

    testOpenContextmenuByClickOnSubwidget : function() {
      this.menu = new this._menuClass();
      this.menuItem = new this._menuItemClass( "push" );
      this.menuItem.setText( "bla" );
      this.menu.addMenuItemAt( this.menuItem, 0 );
      var widget = new qx.ui.basic.Atom( "bla" );
      var parent = this._createControl();
      parent.add( widget );
      parent.addToDocument();
      parent.setLocation( 10, 10 );
      parent.setDimension( 10, 10 );
      parent.setContextMenu( this.menu );
      this._addContextMenuListener( parent );
      this.TestUtil.flush();
      assertTrue( widget.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      this.TestUtil.rightClick( widget );
      this.TestUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.TestUtil.click( this.TestUtil.getDocument() );
      this.TestUtil.flush();
      assertFalse( this.menu.isSeeable() );
      widget.setContextMenu( null );
      widget.setParent( null );
      widget.dispose();
      this.disposeMenu();
    },

    testAddSeparatorInMenuBar : function() {
      this.menuBar = new this._menuBarClass();
      var separator = new qx.ui.menu.Separator();
      this.menuBar.addMenuItemAt( separator, 0 );
    },

    testHasNativeMenu : function() {
      var text = new org.eclipse.rwt.widgets.Text( false );
      text.addToDocument();
      this.TestUtil.flush();
      var element = text.getElement().getElementsByTagName( "input" )[ 0 ];
      assertTrue( org.eclipse.rwt.widgets.Menu._hasNativeMenu( element ) );
      text.dispose();
      text = new org.eclipse.rwt.widgets.Text( true );
      text.addToDocument();
      this.TestUtil.flush();
      element = text.getElement().getElementsByTagName( "textarea" )[ 0 ];
      assertTrue( org.eclipse.rwt.widgets.Menu._hasNativeMenu( element ) );
      text.dispose();
    },

    testMenuFiresChangeHoverItemEvent : function() {
      var menu = this._createMenuWithItems( "push", 3 );
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
      var menuBar = new org.eclipse.rwt.widgets.MenuBar();
      var menuItem = new org.eclipse.rwt.widgets.MenuItem( "push" );
      var menu = this._createMenuWithItems( "push", 3 );
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
      var menu = this._createMenuWithItems( "push", 3 );
      var subMenu = this._createMenuWithItems( "push", 3 );
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

    /************************* Helper *****************************/


    _createMenuWithItems : function( itemType, itemCount ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var menu = new org.eclipse.rwt.widgets.Menu();
      for( var i = 0; i < itemCount; i++ ) {
        var menuItem = new org.eclipse.rwt.widgets.MenuItem( itemType );
        menu.addMenuItemAt( menuItem, i );
      }
      var menuItem = new org.eclipse.rwt.widgets.MenuItem( itemType );
      menu.addMenuItemAt( menuItem, 0 );
      menu.addToDocument();
      menu.show();
      testUtil.flush();
      return menu;
    },

    _createMenuBarByProtocol : function( id, parentId ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "BAR" ],
          "parent" : parentId
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    _createPopUpMenuByProtocol : function( id ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.Menu",
        "properties" : {
          "style" : [ "POP_UP" ]
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    _createMenuItemByProtocol : function( id, parentId, style ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.MenuItem",
        "properties" : {
          "style" : style,
          "parent" : parentId,
          "index" : 0
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    createSimpleMenu : function( type ) {
      this.menu = new this._menuClass();
      this.menuItem = new this._menuItemClass( type );
      this.menu.addMenuItemAt( this.menuItem, 0 );
      this.menu.show();
    },

    createMenuBar : function( type ) {
      this.menuBar = new this._menuBarClass();
      this.menuBar.addToDocument();
      this.menu = new this._menuClass();
      this.menuItem = new this._menuItemClass( type );
      this.menuItem.setText( "bla" );
      this.menu.addMenuItemAt( this.menuItem, 0 );
      this.menuBarItem = new this._menuBarItemClass( "bar" );
      this.menuBarItem.setMenu( this.menu );
      this.menuBar.addMenuItemAt( this.menuBarItem, 0 );
    },

    disposeMenu : function() {
      this.menu.setParent( null );
      this.menuItem.setParent( null );
      this.menu.dispose();
      this.menuItem.dispose();
      this.menu = null;
      this.menuItem = null;
    },

    disposeMenuBar : function() {
      this.menuBar.setParent( null );
      this.menuBar.dispose();
      this.menuBar = null;
      this.menuBarItem.setParent( null );
      this.menuBarItem.dispose();
      this.menuBarItem = null;
      this.disposeMenu();
    },

    itemsXLayoutIsIdentical : function( menu ) {
      var ret = true;
      var children = menu._layout.getChildren();
      if( children.length >= 2 ) {
        var masterLayout = null;
        for( var i = 0; i < children.length; i++ ) {
          var child = children[ i ];
          if( child.classname == "org.eclipse.rwt.widgets.MenuItem" ) {
            var layout = this.getMenuItemLayout( child );
            if( masterLayout == null ) {
              masterLayout = layout;
            }
            for( var key in masterLayout ) {
              var value = layout[ key ];
              var masterValue = masterLayout[ key ];
              if( masterValue == null && value != null ){
                masterLayout[ key ] = value;
              }
              if(    value != null
                  && masterValue != null
                  && value != masterValue )
              {
                ret = false;
                this.warn(   "Item "
                           + i
                           + " value "
                           + key + " : "
                           + value
                           + " != "
                           + masterValue );
              }
            }
          }
        }
      } else {
        this.warn( "children length is < 2 : result always true" );
      }
      return ret;
    },

    getMenuItemLayout : function( item ) {
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
        nodeBounds = this.TestUtil.getElementBounds( node );
        layout.indicatorLeft = nodeBounds.left;
        layout.indicatorWidth = nodeBounds.width;
      }
      node = item.getCellNode( 1 );
      if( node ) {
        nodeBounds = this.TestUtil.getElementBounds( node );
        layout.iconLeft = nodeBounds.left;
        layout.iconWidth = nodeBounds.width;
      }
      node = item.getCellNode( 2 );
      if( node ) {
        nodeBounds = this.TestUtil.getElementBounds( node );
        layout.labelLeft = nodeBounds.left;
        layout.labelWidth = nodeBounds.width;
      }
      node = item.getCellNode( 3 );
      if( node ) {
        nodeBounds = this.TestUtil.getElementBounds( node );
        layout.arrowLeft = nodeBounds.left;
        layout.arrowWidth = nodeBounds.width;
      }
      return layout;
    },

    _addContextMenuListener : function( widget ) {
      var detectByKey = org.eclipse.rwt.widgets.Menu.menuDetectedByKey;
      var detectByMouse = org.eclipse.rwt.widgets.Menu.menuDetectedByMouse;
      widget.addEventListener( "keydown", detectByKey );
      widget.addEventListener( "mouseup", detectByMouse );
    },

    _removeContextMenuListener : function( widget ) {
      var detectByKey = org.eclipse.rwt.widgets.Menu.menuDetectedByKey;
      var detectByMouse = org.eclipse.rwt.widgets.Menu.menuDetectedByMouse;
      widget.removeEventListener( "keydown", detectByKey );
      widget.removeEventListener( "mouseup", detectByMouse );
    },

    _createControl : function() {
      var result = new org.eclipse.swt.widgets.Composite();
      result.setUserData( "isControl", true );
      return result;
    }

  }

} );