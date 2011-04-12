/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ToolBarTest", {

  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this.testUtil = org.eclipse.rwt.test.fixture.TestUtil;        
  },
  
  members : {
    
    testClickDropDown : function() {
      var item = new org.eclipse.rwt.widgets.ToolItem( "dropDown" );
      this.item = item; 
      item.setLeft( 100 );
      item.setTop( 100 );
      item.setText( "hallo" );
      item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
      this._currentItem = item;
      item.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      var lineStyle = item.getCellNode( 3 ).style;
      item.setUserData( "id", "w1" );
      this.testUtil.clearRequestLog();
      item.setHasSelectionListener( true );
      this.testUtil.fakeMouseClick( item, 103, 103 );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      var msg = this.testUtil.getMessage();
      assertContains( "widgetSelected=w1",  msg );
      assertContainsNot( "detail=arrow", msg );
      this.testUtil.clearRequestLog();
      this.testUtil.fakeMouseClick( item, 103 + parseInt( lineStyle.left ), 103 );
      assertEquals( 1, this.testUtil.getRequestsSend() );
      var msg = this.testUtil.getMessage();
      assertContains( "detail=arrow", msg );
      this.testUtil.clearRequestLog();
      item.destroy();
      this.item = null;
    },
    
    testDropDownLayoutBug : function() {
      if( org.eclipse.rwt.Client.isMshtml() ) {
        var item = new org.eclipse.rwt.widgets.ToolItem( "dropDown" );
        this.item = item; 
        item.setLeft( 100 );
        item.setTop( 100 );
        item.setWidth( 100 );
        item.setHeight( 100 );
        item.setText( "hallo" );
        item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
        this._currentItem = item;
        item.addToDocument();
        qx.ui.core.Widget.flushGlobalQueues();
        var down = item.getCellNode( 4 );
        assertEquals( "0px", down.style.lineHeight );
        this.testUtil.hoverFromTo( document.body, item.getElement() );
        qx.ui.core.Widget.flushGlobalQueues();
        assertEquals( "0px", down.style.lineHeight );
        item.destroy();
        this.item = null;
      }
    },

    testDropDownLineHeight : function() {
      var item = new org.eclipse.rwt.widgets.ToolItem( "dropDown" );
      this.item = item; 
      item.setText( "hallo" );
      item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
      item.setPaddingTop( 10 );
      item.setPaddingBottom( 1 );     
      this._currentItem = item;
      item.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      var lineNode = item.getCellNode( 3 );
      var padding = item.getPaddingTop() + item.getPaddingBottom();
      var inner = item.getInnerHeight();
      var targetHeight = inner + padding;
      assertTrue( targetHeight <= parseInt( lineNode.style.height ) );
      assertTrue( 0 >= parseInt( lineNode.style.top ) );
      item.destroy();
      this.item = null;
    },
    
    testDropDownLineBorder : function() {
      var item = new org.eclipse.rwt.widgets.ToolItem( "dropDown" );
      var border = new org.eclipse.rwt.Border( 1, "outset", "black");  
      this.item = item; 
      item.setText( "hallo" );
      item.setSeparatorBorder( border );
      item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
      this._currentItem = item;
      item.addToDocument();
      qx.ui.core.Widget.flushGlobalQueues();
      var lineNode = item.getCellNode( 3 );
      assertContains( "outset", lineNode.style.cssText );
      item.destroy();
      this.item = null;
    },
    
    testHotImage : function() {
      var item = new org.eclipse.rwt.widgets.ToolItem( "push" );
      item.setText( "hallo" );
      item.setImage( "test1.jpg" );
      item.addToDocument();
      this.testUtil.flush();
      assertTrue( item.isSeeable() );
      var node = item.getCellNode( 1 );
      assertContains( 
        "test1.jpg",
        this.testUtil.getCssBackgroundImage ( node )
      );
      this.testUtil.mouseOver( item );      
      this.testUtil.flush();
      assertContains( 
        "test1.jpg",
        this.testUtil.getCssBackgroundImage ( node )
      );      
      this.testUtil.mouseOut( item );      
      item.setHotImage( "test2.jpg" );
      this.testUtil.flush();
      assertContains( 
        "test1.jpg",
        this.testUtil.getCssBackgroundImage ( node )
      );
      this.testUtil.mouseOver( item );      
      this.testUtil.flush();
      assertTrue( item.hasState( "over" ) );
      this.item = item;
      assertContains( 
        "test2.jpg",
        this.testUtil.getCssBackgroundImage ( node )
      );      
      this.testUtil.mouseOut( item );      
      this.testUtil.flush();
      assertContains( 
        "test1.jpg",
        this.testUtil.getCssBackgroundImage ( node )
      );      
      item.destroy();
    },
    
    testKeyboardControlActivate : function() {
      this.createDefaultToolBar();
      assertFalse( this.toolBar.isFocused() );
      assertFalse( this.toolItem1.hasState( "over" ) );
      this.toolBar.focus();
      assertTrue( this.toolBar.isFocused() );
      assertTrue( this.toolItem1.hasState( "over" ) );
      this.disposeToolBar();      
    },
    
    testKeyboardControlActivateOnHover : function() {
      this.createDefaultToolBar();
      assertFalse( this.toolItem3.hasState( "over" ) );
      this.testUtil.mouseOver( this.toolItem3 );
      assertTrue( this.toolItem3.hasState( "over" ) );
      this.toolBar.focus();
      assertTrue( this.toolBar.isFocused() );
      assertFalse( this.toolItem1.hasState( "over" ) );
      assertTrue( this.toolItem3.hasState( "over" ) );
      this.disposeToolBar();      
    },
    
    testKeyboardControlOffByHover : function() {
      this.createDefaultToolBar();
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      this.testUtil.mouseOver( this.toolItem3 );
      assertFalse( this.toolItem1.hasState( "over" ) );
      assertTrue( this.toolItem3.hasState( "over" ) );
      this.disposeToolBar();
    },
    
    testKeyboardControlOffByBlur : function() {
      this.createDefaultToolBar();
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      this.toolBar.blur();
      assertFalse( this.toolItem1.hasState( "over" ) );
      assertFalse( this.toolItem3.hasState( "over" ) );     
      this.disposeToolBar();
    },
    
    testKeyboardControlLeft : function() {
      this.createDefaultToolBar();
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      this.testUtil.press( this.toolBar, "Left" );
      assertFalse( this.toolItem1.hasState( "over" ) );
      assertTrue( this.toolItem3.hasState( "over" ) );
      this.testUtil.press( this.toolBar, "Left" );
      assertTrue( this.toolItem1.hasState( "over" ) );
      assertFalse( this.toolItem3.hasState( "over" ) );
      this.disposeToolBar();      
    },
    
    testKeyboardControlRight : function() {
      this.createDefaultToolBar();
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      this.testUtil.press( this.toolBar, "Right" );
      assertFalse( this.toolItem1.hasState( "over" ) );
      assertTrue( this.toolItem3.hasState( "over" ) );
      this.testUtil.press( this.toolBar, "Right" );
      assertTrue( this.toolItem1.hasState( "over" ) );
      assertFalse( this.toolItem3.hasState( "over" ) );
      this.disposeToolBar();      
    },
    
    testKeyboardControlExecute : function() {
      this.createDefaultToolBar();
      var executed = false;
      this.toolItem1.addEventListener( "execute", function( event ) {
        executed = true;
      } );
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      this.testUtil.press( this.toolBar, "Enter" );      
      assertTrue( executed );
      this.disposeToolBar();      
    },

    testKeyboardControlExecuteWithTextWidget : function() {
      this.createDefaultToolBar();
      this.toolBar.setSpace( 0, 100, 0, 20 );
      this.toolItem1.setSpace( 0, 10, 0, 20 );
      this.toolItem2.setSpace( 11, 10, 0, 20 );
      this.separator.setSpace( 21, 30, 0, 20 );
      this.toolItem3.setSpace( 51, 10, 0, 20 );
      var text = new org.eclipse.rwt.widgets.Text( false );
      this.toolBar.addAt( text, 4 );
      text.setSpace( 21, 30, 0, 20 );
      this.testUtil.flush();
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      var executed = false;
      this.toolItem1.addEventListener( "execute", function( event ) {
        executed = true;
      } );
      text.focus();
      this.testUtil.pressOnce( text, "Enter" );
      assertFalse( executed );
      text.dispose();
      this.disposeToolBar();
    },

    /////////
    // Helper
    
    createDefaultToolBar : function() {
      this.toolBar = new org.eclipse.rwt.widgets.ToolBar( false );
      this.toolItem1 = new org.eclipse.rwt.widgets.ToolItem( "push", false );
      this.toolItem2 = new org.eclipse.rwt.widgets.ToolItem( "push", false  );
      this.toolItem3 = new org.eclipse.rwt.widgets.ToolItem( "push", false  );
      this.separator = new org.eclipse.rwt.widgets.ToolSeparator( "push" );
      this.toolItem1.setText( 1 );
      this.toolItem2.setText( 2 );
      this.toolItem3.setText( 3 );
      this.toolBar.addAt( this.toolItem1, 0 );
      this.toolBar.addAt( this.toolItem2, 1 );
      this.toolBar.addAt( this.separator, 2 );
      this.toolBar.addAt( this.toolItem3, 3 );
      this.toolItem2.setEnabled( false );
      this.toolBar.addToDocument();
      this.testUtil.flush();
    },
    
    disposeToolBar : function() {
      this.toolBar.setParent( null );
      this.toolItem1.dispose();
      this.toolItem2.dispose();
      this.toolItem3.dispose();
      this.separator.dispose();
      this.toolBar.dispose();
    }
    
  }
  
} );
