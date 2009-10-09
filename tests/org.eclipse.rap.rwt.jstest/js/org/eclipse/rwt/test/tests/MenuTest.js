/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.MenuTest", {
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this._menuClass = org.eclipse.rwt.widgets.Menu;
    this._menuItemClass = org.eclipse.rwt.widgets.MenuItem;
    this._menuBarClass = org.eclipse.rwt.widgets.MenuBar;
    this._menuBarItemClass = org.eclipse.rwt.widgets.MenuItem;
    this.testUtil = org.eclipse.rwt.test.fixture.TestUtil;
  },  
  
  members : {
    menu : null,
    menuItem : null,
    menuBar : null,
    menuBarItem : null,
 
    testTextOnly : function() {
      this.createSimpleMenu( "push" );      
      this.menuItem.setText( "Hello World!" );
      this.testUtil.flush();
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
      this.testUtil.flush();      
      assertEquals( 1, this.menuItem._getTargetNode().childNodes.length );
      var node = this.menuItem._getTargetNode().firstChild;
      assertContains( 
        "url.jpg", 
        this.testUtil.getCssBackgroundImage ( node )
      );
      var bounds = this.testUtil.getElementBounds( node );
      assertEquals( 20, bounds.width );
      assertEquals( 30, bounds.height );
      this.disposeMenu();
    },    
        
    testArrowOnly : function() {
      this.createSimpleMenu( "push" );      
      this.menuItem.setArrow( [ "url.jpg", 13, 13 ] );
      this.testUtil.flush();      
      assertEquals( 1, this.menuItem._getTargetNode().childNodes.length );
      var node = this.menuItem._getTargetNode().firstChild;
      assertContains( 
        "url.jpg", 
        this.testUtil.getCssBackgroundImage( node )
      );
      this.disposeMenu();
    },

    testMenuResize : function() {
      this.createSimpleMenu( "push" );
      this.menuItem.setSpacing( 3 );
      this.testUtil.flush();
      var menuNode = this.menu.getElement();
      var itemNode = this.menuItem.getElement();
      var oldMenuBounds = this.testUtil.getElementBounds( menuNode );
      this.oldMenuBounds = oldMenuBounds;
      //throw( "setop" );
       
      this.menuItem.setText( "bla! " );
      this.testUtil.flush();
      var newMenuBounds = this.testUtil.getElementBounds( menuNode );      
      assertLarger( oldMenuBounds.width, newMenuBounds.width );
      oldMenuBounds = newMenuBounds;       
      var item2 = new this._menuItemClass( "push" );
      item2.setText( "blubblubblub!" );
      item2.setSpacing( 3 );
      this.menu.addMenuItemAt( item2, 0 );
      var itemNode1 = this.menuItem._getTargetNode();
      var oldItemBounds1 = this.testUtil.getElementBounds( itemNode1 );
      this.testUtil.flush();
      newMenuBounds = this.testUtil.getElementBounds( menuNode );
      var itemNode2 = item2._getTargetNode();
      var itemBounds1 = this.testUtil.getElementBounds( itemNode1 );
      var itemBounds2 = this.testUtil.getElementBounds( itemNode2 );
      assertLarger( oldMenuBounds.height, newMenuBounds.height );
      assertLarger( oldItemBounds1.width, itemBounds1.width );
      assertEquals( itemBounds1.width, itemBounds2.width );
      oldItemBounds1 = itemBounds1;
      oldMenuBounds = newMenuBounds;
      item2.setText( "-" );
      this.testUtil.flush();
      newMenuBounds = this.testUtil.getElementBounds( menuNode );
      itemBounds1 = this.testUtil.getElementBounds( itemNode1 );
      itemBounds2 = this.testUtil.getElementBounds( itemNode2 );
      assertSmaller( oldMenuBounds.width, newMenuBounds.width );
      assertSmaller( oldItemBounds1.width, itemBounds1.width );
      assertEquals( itemBounds1.width, itemBounds2.width );
      oldMenuBounds = newMenuBounds;
      this.menuItem.setArrow( [ "bla.jpg", 13, 13 ] );            
      this.testUtil.flush();
      newMenuBounds = this.testUtil.getElementBounds( menuNode );
      // the dimension of arrow are at least 13 and shouldn't have changed  
      assertEquals( oldMenuBounds.width, newMenuBounds.width );
      oldMenuBounds = newMenuBounds;
      this.menuItem.setImage( "bla.jpg" , 30, 30 );
      this.testUtil.flush();
      newMenuBounds = this.testUtil.getElementBounds( menuNode );      
      assertEquals( oldMenuBounds.width + 33, newMenuBounds.width );
      oldMenuBounds = newMenuBounds;
      this.menuItem.setImage( null, 0, 0 );
      this.testUtil.flush();
      newMenuBounds = this.testUtil.getElementBounds( menuNode );      
      assertEquals( oldMenuBounds.width - 33, newMenuBounds.width );
      this.disposeMenu();
    },
    
    testItemHover : function() {
      this.createSimpleMenu( "push" );
      this.testUtil.flush();
      this.testUtil.mouseOver( this.menuItem );
      assertTrue( this.menuItem.hasState( "over" ) );
      this.testUtil.mouseOut( this.menu );
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
      this.testUtil.flush();
      assertTrue( this.itemsXLayoutIsIdentical( this.menu ) );
      item2.setImage( "bla.jpg", 20, 20  );
      this.testUtil.flush();
      assertTrue( this.itemsXLayoutIsIdentical( this.menu ) );
      item3.setImage( "bla.jpg", 40, 40  );
      this.testUtil.flush();
      assertTrue( this.itemsXLayoutIsIdentical( this.menu ) );
      item3.setImage( null, 0, 0  );
      item2.setArrow( [ "bla.jpg", 13, 13 ] );
      item2.setSelectionIndicator( [ "bla.jpg", 13, 13 ] );
      this.testUtil.flush();
      assertTrue( this.itemsXLayoutIsIdentical( this.menu ) );
      item2.setImage( null, 0, 0  );
      item2.setArrow( null );
      item2.setSelectionIndicator( null );
      this.testUtil.flush();
      assertTrue( this.itemsXLayoutIsIdentical( this.menu ) );
      this.disposeMenu();
    },
    
    testOpenMenuByMenuBar : function() {    
      this.createMenuBar( "push" );
      this.testUtil.flush();
      var bar = this.menuBar;
      var barItem = this.menuBarItem;
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      this.testUtil.click( barItem );
      this.testUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.testUtil.click( this.testUtil.getDocument() );      
      this.testUtil.flush();
      assertFalse( this.menu.isSeeable() );
      this.testUtil.click( barItem );
      this.testUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.testUtil.click( this.testUtil.getDocument() );      
      this.testUtil.flush();
      assertFalse( this.menu.isSeeable() );      
      this.disposeMenuBar();
    },
    
    testOpenMenuAsContextmenu : function() {
      this.menu = new this._menuClass();
      this.menuItem = new this._menuItemClass( "push" );
      this.menuItem.setText( "bla" ); 
      this.menu.addMenuItemAt( this.menuItem, 0 );
      var widget = new qx.ui.basic.Atom( "bla" );
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( this.menu );
      widget.addEventListener( 
        "contextmenu", 
        org.eclipse.rwt.widgets.Menu.contextMenuHandler );      
      this.testUtil.flush();
      assertTrue( widget.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      this.testUtil.rightClick( widget );
      this.testUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.testUtil.click( this.testUtil.getDocument() );
      this.testUtil.flush();
      assertFalse( this.menu.isSeeable() );
      widget.setContextMenu( null );
      widget.removeEventListener( 
        "contextmenu", 
        org.eclipse.rwt.widgets.Menu.contextMenuHandler
      );  
      widget.setParent( null );
      widget.dispose();
      this.disposeMenu();            
    },
    
    testExecuteMenuItem : function() {
      this.createMenuBar( "push" );
      this.executed = false;
      var command = function() {
        this.executed = true;
      };
      this.menuItem.addEventListener( "execute", command, this );   
      this.testUtil.flush(); // cannnot click until created
      this.testUtil.click( this.menuBarItem );
      this.testUtil.flush();
      assertFalse( this.executed );
      assertTrue( this.menu.isSeeable() );
      this.testUtil.click( this.menuItem );
      this.testUtil.flush();
      assertTrue( this.executed );
      assertFalse( this.menu.isSeeable() );
      delete this.executed;
      this.disposeMenuBar();
    },
    
    
    testOpenSubmenuByMouse : function() {
      this.testUtil.prepareTimerUse();
      this.createMenuBar( "cascade" );
      var subMenu = new this._menuClass();
      var subMenuItem = new this._menuItemClass( "push" );
      subMenu.addMenuItemAt( subMenuItem, 0 );      
      this.menuItem.setSubMenu( subMenu );
      var item2 = new this._menuItemClass( "push" );
      item2.setText( "bla!" );
      this.menu.addMenuItemAt( item2, 0 );      
      this.testUtil.flush();
      assertFalse( this.menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );      
      this.testUtil.click( this.menuBarItem );  //open menu
      this.testUtil.flush();
      assertTrue( this.menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );      
      this.testUtil.mouseOver( this.menuItem ); //starting open-timer 
      assertTrue( this.menu._openTimer.isEnabled() );
      assertFalse( this.menu._closeTimer.isEnabled() );
      this.testUtil.click( this.menuItem ); //clicking does nothing
      this.testUtil.flush();
      assertTrue( this.menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      this.testUtil.forceInterval( this.menu._openTimer );  //timer opens submenu
      this.testUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( this.menuItem.hasState( "over" ) );
      this.testUtil.mouseFromTo( this.menuItem, item2 );  
      // hovering another item starts the close-timer, but not the open-timer
      // since the item has no submenu
      assertFalse( this.menu._openTimer.isEnabled() );
      assertTrue( this.menu._closeTimer.isEnabled() );
      this.testUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( item2.hasState( "over" ) );
      assertFalse( this.menuItem.hasState( "over" ) );
      this.testUtil.forceInterval( this.menu._closeTimer );  //timer closes submenu
      this.testUtil.flush();      
      assertFalse( subMenu.isSeeable() );
      assertTrue( this.menu.isSeeable() );
      assertFalse( this.menuItem.hasState( "over" ) );
      assertTrue( item2.hasState( "over" ) );      
      // re-open the submenu 
      this.testUtil.mouseFromTo( item2, this.menuItem );  
      this.testUtil.forceInterval( this.menu._openTimer );  
      this.testUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( this.menuItem.hasState( "over" ) );
      this.testUtil.mouseFromTo( this.menuItem, subMenu );
      this.testUtil.mouseFromTo( subMenu, subMenuItem );
      assertFalse( this.menu._closeTimer.isEnabled() );
      assertFalse( this.menu._openTimer.isEnabled() );      
      this.testUtil.flush();
      assertTrue( subMenu.isSeeable() );      
      assertTrue( subMenuItem.hasState( "over"  ) );
      assertTrue( this.menuItem.hasState( "over" ) );
      assertIdentical( this.menuBarItem, this.menu.getOpener() );
      assertIdentical( this.menuItem, this.menu._openItem );
      assertIdentical( this.menuItem, subMenu.getOpener() );
      assertNull( subMenu._openItem );
      // now click:
      this.testUtil.click( subMenuItem );  
      this.testUtil.flush();      
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
      this.testUtil.flush();      
      assertEquals( 1, this.menuItem._getTargetNode().childNodes.length );
      var node = this.menuItem._getTargetNode().firstChild;      
      assertEquals( 
        "", 
        this.testUtil.getCssBackgroundImage( node )
      )
      this.testUtil.click( this.menuItem );
      this.menuItem.setSelectionIndicator( [ "url.jpg", 13, 13 ]);
      this.testUtil.flush();
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertContains( 
        "url.jpg", 
        this.testUtil.getCssBackgroundImage( node )
      );                  
      this.testUtil.click( this.menuItem );
      this.menuItem.setSelectionIndicator( null );
      assertFalse( this.menuItem.hasState( "selected" ) );
      this.testUtil.flush();
      assertEquals( 1, this.menuItem._getTargetNode().childNodes.length );      
      assertEquals( 
        "", 
        this.testUtil.getCssBackgroundImage( node )
      );      
      this.disposeMenu();
    },
    
    testMenuShowEvent : function() {
      this.createMenuBar( "push" );
      this.menu.setUserData( "id", "w1" );
      this.testUtil.clearRequestLog();
      this.testUtil.flush();
      this.testUtil.click( this.menuBarItem );
      this.testUtil.flush();
      assertEquals( 0, this.testUtil.getRequestsSend() );
      assertTrue( this.menu.isSeeable() ); 
      assertTrue( this.menuItem.isSeeable() );     
      this.testUtil.click( this.testUtil.getDocument() );
      this.testUtil.flush();      
      assertEquals( 0, this.testUtil.getRequestsSend() );
      assertFalse( this.menu.isSeeable() );
      this.menu.setHasMenuListener( true );      
      this.testUtil.click( this.menuBarItem );
      this.testUtil.flush();      
      assertTrue( this.menu.isSeeable() ); 
      assertFalse( this.menuItem.isSeeable() );
      assertTrue( this.menu._preItem.isSeeable() );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      var msg = this.testUtil.getMessage();
      assertContains( "org.eclipse.swt.events.menuShown",  msg );         
      this.testUtil.clearRequestLog();
      this.menu.unhideItems( true );
      this.testUtil.flush();      
      assertTrue( this.menuItem.isSeeable() );
      assertFalse( this.menu._preItem.isSeeable() );
      this.testUtil.click( this.testUtil.getDocument() );
      this.testUtil.flush();      
      assertEquals( 1, this.testUtil.getRequestsSend() );
      var msg = this.testUtil.getMessage();
      assertContains( "org.eclipse.swt.events.menuHidden",  msg );
      this.disposeMenuBar();         
    },
    
    testExecutePushItem : function() {
      this.createSimpleMenu( "push" );
      this.menuItem.setUserData( "id", "w1" );
      this.testUtil.flush();
      this.testUtil.clearRequestLog();
      this.testUtil.click( this.menuItem );
      assertEquals( 0, this.testUtil.getRequestsSend() );      
      this.menuItem.setHasSelectionListener( true );
      this.testUtil.clearRequestLog();
      this.testUtil.click( this.menuItem );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      assertFalse( this.menuItem.hasState( "selected" ) );      
      var msg = this.testUtil.getMessage();
      assertContains( "widgetSelected=w1",  msg );
      assertContainsNot( "w1.selection=true",  msg );   
      this.testUtil.clearRequestLog();
      this.disposeMenu();
    },    


    testExecuteCheckItem: function() { 
      this.createSimpleMenu( "check" );
      this.menuItem.setUserData( "id", "w1" );
      this.testUtil.flush();
      this.testUtil.clearRequestLog();
      this.testUtil.click( this.menuItem );
      assertEquals( 0, this.testUtil.getRequestsSend() );      
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertTrue(
        org.eclipse.swt.Request.getInstance()._parameters[ "w1.selection" ]
      );
      this.testUtil.clearRequestLog();
      this.menuItem.setHasSelectionListener( true );
      this.menuItem.setSelection( false );
      assertFalse( this.menuItem.hasState( "selected" ) );
      this.testUtil.clearRequestLog();
      this.testUtil.click( this.menuItem );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      assertTrue( this.menuItem.hasState( "selected" ) );
      assertContains( "w1.selection=true",  this.testUtil.getMessage() );
      this.testUtil.clearRequestLog();
      this.testUtil.click( this.menuItem );
      assertFalse( this.menuItem.hasState( "selected" ) );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      assertContains( "w1.selection=false",  this.testUtil.getMessage() );
      this.disposeMenu();
    },
    

    testExecuteRadioButton : function() {
      this.createSimpleMenu( "radio" );
      this.menuItem.setUserData( "id", "w1" );
      this.testUtil.flush();      
      this.testUtil.clearRequestLog();
      this.testUtil.click( this.menuItem );
      assertEquals( 0, this.testUtil.getRequestsSend() );      
      assertTrue(
        org.eclipse.swt.Request.getInstance()._parameters[ "w1.selection" ]
      );
      this.menuItem.setSelection( false );      
      this.menuItem.setHasSelectionListener( true );
      this.testUtil.clearRequestLog();
      this.testUtil.click( this.menuItem );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      assertTrue( this.menuItem.hasState( "selected" ) );      
      assertContains( "w1.selection=true",  this.testUtil.getMessage() );
      var item2 = new this._menuItemClass( "radio" );
      this.menu.addMenuItemAt( item2, 0 );      
      item2.setUserData( "id", "w2" );      
      item2.setHasSelectionListener( true );
      this.testUtil.clearRequestLog();
      this.testUtil.flush();
      this.testUtil.click( item2 );
      assertFalse( this.menuItem.hasState( "selected" ) );
      assertTrue( item2.hasState( "selected" ) );
      assertEquals( 1, this.testUtil.getRequestsSend() );      
      var msg = this.testUtil.getMessage();      
      assertContains( "w1.selection=false", msg );
      assertContains( "w2.selection=true", msg );
      this.testUtil.clearRequestLog();
      this.disposeMenu();
    },

    testKeyboardControl : function() {
      this.testUtil.prepareTimerUse();
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
      this.testUtil.flush();
      assertFalse( this.menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );      
      this.testUtil.click( this.menuBarItem );  //open menu
      this.testUtil.flush();
      this.menu.unhideItems( true );
      this.testUtil.flush();
      assertTrue( this.menu.isSeeable() );
      assertFalse( subMenu.isSeeable() );
      this.testUtil.press( this.menu, "Down", true );
      assertTrue( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertFalse( this.menuItem.hasState( "over") );
      this.testUtil.press( this.menu, "Down", true );
      assertFalse( item3.hasState( "over") );
      assertTrue( item2.hasState( "over") );
      assertFalse( this.menuItem.hasState( "over") );
      this.testUtil.press( this.menu, "Down", true );
      assertFalse( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertTrue( this.menuItem.hasState( "over") );
      this.testUtil.press( this.menu, "Down", true );
      assertTrue( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertFalse( this.menuItem.hasState( "over") );
      this.testUtil.press( this.menu, "Up", true );
      assertFalse( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertTrue( this.menuItem.hasState( "over") );
      this.testUtil.press( this.menu, "Up", true );
      assertFalse( item3.hasState( "over") );
      assertTrue( item2.hasState( "over") );
      assertFalse( this.menuItem.hasState( "over") );
      this.testUtil.press( this.menu, "Up", true );
      assertTrue( item3.hasState( "over") );
      assertFalse( item2.hasState( "over") );
      assertFalse( this.menuItem.hasState( "over") );
      assertFalse( this.menu._openTimer.isEnabled() );
      assertFalse( this.menu._closeTimer.isEnabled() );
      this.testUtil.press( this.menu, "Right", true ); 
      this.testUtil.flush();
      assertTrue( subMenu.isSeeable() );
      assertTrue( this.testUtil.isActive( subMenu ) );      
      assertFalse( this.menuItem.hasState( "over") );
      assertFalse( item2.hasState( "over" ) );
      assertTrue( item3.hasState( "over" ) );
      assertFalse( this.menu._openTimer.isEnabled() );
      assertFalse( this.menu._closeTimer.isEnabled() );
      assertTrue( subMenuItem.hasState( "over") );
      assertFalse( this.testUtil.isActive( this.menu ) );
      this.testUtil.press( subMenu, "Left", true );
      this.testUtil.flush();     
      assertFalse( subMenu.isSeeable() );
      assertTrue( this.testUtil.isActive( this.menu ) );
      assertTrue( item3.hasState( "over") );
      this.testUtil.press( this.menu, "Enter", true );
      this.testUtil.flush();     
      assertTrue( item3.hasState( "over" ) );
      assertTrue( subMenu.isSeeable() );
      assertTrue( this.testUtil.isActive( subMenu ) );
      assertTrue( subMenuItem.hasState( "over" ) );
      subMenuItem.setUserData( "id", "w1" );
      subMenuItem.setHasSelectionListener( true );
      this.testUtil.clearRequestLog();
      this.testUtil.press( subMenu, "Enter", true );
      this.testUtil.flush();     
      assertFalse( subMenu.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      assertEquals( 2, this.testUtil.getRequestsSend() );
      var msg = this.testUtil.getMessage();
      assertContains( "widgetSelected=w1",  msg );
      assertContainsNot( "w1.selection=true",  msg );   
      this.testUtil.clearRequestLog();      
      this.disposeMenuBar();
    },
    
    testGetMenuBar : function() {
      this.createMenuBar( "push" );
      var widget = new qx.ui.basic.Atom( "bla" );
      widget.addToDocument();
      var manager = org.eclipse.rwt.MenuManager.getInstance();
      this.testUtil.flush();
      this.testUtil.click( this.menuBarItem );
      this.testUtil.flush();
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
      var widget = new qx.ui.basic.Atom( "bla" );
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( this.menu );
      widget.addEventListener( 
        "contextmenu", 
        org.eclipse.rwt.widgets.Menu.contextMenuHandler );      
      this.testUtil.flush();
      assertTrue( widget.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      this.testUtil.rightClick( widget );
      this.testUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.testUtil.click( widget );
      this.testUtil.flush();
      assertFalse( this.menu.isSeeable() );
      widget.setContextMenu( null );
      widget.removeEventListener( 
        "contextmenu", 
        org.eclipse.rwt.widgets.Menu.contextMenuHandler
      );  
      widget.setParent( null );
      widget.dispose();
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
      var widget = new qx.ui.basic.Atom( "bla" );
      widget.addToDocument();
      widget.setLocation( 10, 10 );
      widget.setDimension( 10, 10 );
      widget.setContextMenu( menu2 );
      widget.addEventListener( 
        "contextmenu", 
        org.eclipse.rwt.widgets.Menu.contextMenuHandler );      
      this.testUtil.flush();
      assertTrue( widget.isSeeable() );
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      this.testUtil.rightClick( widget );
      this.testUtil.flush();
      assertTrue( menu2.isSeeable() );
      this.testUtil.click( barItem );
      this.testUtil.flush();
      assertTrue( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );
      this.disposeMenuBar();      
      menu2.setParent( null );
      menuItem2.setParent( null );
      menu2.dispose();
      menuItem2.dispose();
      widget.setContextMenu( null );
      widget.removeEventListener( 
        "contextmenu", 
        org.eclipse.rwt.widgets.Menu.contextMenuHandler
      );  
      widget.setParent( null );
      widget.dispose();
    },
        
    testDropDownMenuCloseOnOpenerClick : function() {
      this.createMenuBar( "push" );
      this.testUtil.flush();
      var bar = this.menuBar;
      var barItem = this.menuBarItem;
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertFalse( this.menu.isSeeable() );
      this.testUtil.click( barItem );
      this.testUtil.flush();
      assertTrue( this.menu.isSeeable() );
      this.testUtil.click( barItem );      
      this.testUtil.flush();
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
      this.testUtil.flush();
      assertTrue( bar.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertTrue( barItem2.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );      
      this.testUtil.click( barItem );
      this.testUtil.flush();
      assertTrue( menu.isSeeable() );
      this.testUtil.click( barItem2 );      
      this.testUtil.flush();
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
      this.testUtil.flush();
      assertTrue( bar.isSeeable() );
      assertTrue( menuBar2.isSeeable() );
      assertTrue( barItem.isSeeable() );
      assertTrue( barItem2.isSeeable() );
      assertFalse( menu.isSeeable() );
      assertFalse( menu2.isSeeable() );      
      this.testUtil.click( barItem );
      this.testUtil.flush();
      assertTrue( menu.isSeeable() );
      this.testUtil.click( barItem2 );      
      this.testUtil.flush();
      assertTrue( menu2.isSeeable() );
      assertFalse( menu.isSeeable() );
      menuBar2.destroy();
      menu2.destroy();
      menuItem2.destroy();
      barItem2.destroy();
      this.disposeMenuBar();            
    },
        
    /************************* Helper *****************************/
        
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
        nodeBounds = this.testUtil.getElementBounds( node );
        layout.indicatorLeft = nodeBounds.left;
        layout.indicatorWidth = nodeBounds.width;
      }
      node = item.getCellNode( 1 );
      if( node ) {
        nodeBounds = this.testUtil.getElementBounds( node );
        layout.iconLeft = nodeBounds.left;
        layout.iconWidth = nodeBounds.width;
      }
      node = item.getCellNode( 2 );
      if( node ) {
        nodeBounds = this.testUtil.getElementBounds( node );
        layout.labelLeft = nodeBounds.left;
        layout.labelWidth = nodeBounds.width;
      }
      node = item.getCellNode( 3 );
      if( node ) {
        nodeBounds = this.testUtil.getElementBounds( node );
        layout.arrowLeft = nodeBounds.left;
        layout.arrowWidth = nodeBounds.width;
      }
      return layout;
    }
    
  }
  
} );