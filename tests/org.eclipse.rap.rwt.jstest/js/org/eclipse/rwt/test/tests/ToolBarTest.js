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

    testCreateTooBarByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ToolBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.ToolBar );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreateTooBarWithFlatByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ToolBar",
        "properties" : {
          "style" : [ "HORIZONTAL", "FLAT" ],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_HORIZONTAL" ) );
      assertTrue( widget.hasState( "rwt_FLAT" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreatePushTooItemByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.ToolItem );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "push" ) );
      assertTrue( widget.hasState( "rwt_PUSH" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      assertFalse( widget.hasState( "rwt_VERTICAL" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateRadioTooItemByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget =  this._createToolItemByProtocol( "w4", "w3", [ "RADIO" ] );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.ToolItem );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "radio" ) );
      assertTrue( widget.hasState( "rwt_RADIO" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      assertFalse( widget.hasState( "rwt_VERTICAL" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateCheckTooItemByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget =  this._createToolItemByProtocol( "w4", "w3", [ "CHECK" ] );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.ToolItem );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "check" ) );
      assertTrue( widget.hasState( "rwt_CHECK" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      assertFalse( widget.hasState( "rwt_VERTICAL" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateDropDownTooItemByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget =  this._createToolItemByProtocol( "w4", "w3", [ "DROP_DOWN" ] );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.ToolItem );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "dropDown" ) );
      assertTrue( widget.hasState( "rwt_DROP_DOWN" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      assertFalse( widget.hasState( "rwt_VERTICAL" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateSeparatorTooItemByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [ "FLAT" ] );
      var widget =  this._createToolItemByProtocol( "w4", "w3", [ "SEPARATOR" ] );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.ToolSeparator );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget._line.getVisibility() );
      assertTrue( widget.hasState( "rwt_SEPARATOR" ) );
      assertNotNull( widget._line );
      assertFalse( widget._vertical );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateVerticalSeparatorTooItemByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [ "VERTICAL" ] );
      var widget =  this._createToolItemByProtocol( "w4", "w3", [ "SEPARATOR" ] );
      assertTrue( widget instanceof org.eclipse.rwt.widgets.ToolSeparator );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "rwt_SEPARATOR" ) );
      assertNull( widget._line );
      assertTrue( widget._vertical );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateTooItemWithIndexByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var pushToolItem = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      var radioToolItem = this._createToolItemByProtocol( "w4", "w3", [ "RADIO" ] );
      var checkToolItem = this._createToolItemByProtocol( "w4", "w3", [ "CHECK" ] );
      var children = toolbar.getChildren();
      assertIdentical( checkToolItem, children[ 0 ] );
      assertIdentical( radioToolItem, children[ 1 ] );
      assertIdentical( pushToolItem, children[ 2 ] );
      shell.destroy();
      toolbar.destroy();
      pushToolItem.destroy();
      radioToolItem.destroy();
      checkToolItem.destroy();
    },

    testCreateDestroyTooItemByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      testUtil.flush();
      assertTrue( widget.isDisposed() );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      assertEquals( undefined, objectManager.getObject( "w4" ) );
      shell.destroy();
      toolbar.destroy();
    },

    testSetBoundsByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "bounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( 1, widget.getLeft() );
      assertEquals( 2, widget.getTop() );
      assertEquals( 3, widget.getWidth() );
      assertEquals( 4, widget.getHeight() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetVisibleByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "visible" : false } );
      assertFalse( widget.getVisibility() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetEnabledByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "enabled" : false } );
      assertFalse( widget.getEnabled() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetToolTipByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "toolTip" : "hello blue world" } );
      assertEquals( "hello blue world", widget.getUserData( "toolTipText" ) );
      assertTrue( widget.getToolTip() !== null );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "customVariant" : "variant_blue" } );
      assertTrue( widget.hasState( "variant_blue" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetImageByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "image" : [ "image.png", 10, 20 ] } );
      assertEquals( "image.png", widget._image[ 0 ] );
      assertEquals( 10, widget._image[ 1 ] );
      assertEquals( 20, widget._image[ 2 ] );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetHotImageByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "hotImage" : [ "image.png", 10, 20 ] } );
      assertEquals( "image.png", widget._hotImage[ 0 ] );
      assertEquals( 10, widget._hotImage[ 1 ] );
      assertEquals( 20, widget._hotImage[ 2 ] );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetControlByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [ "FLAT" ] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "SEPARATOR" ] );
      this._setPropertyByProtocol( "w4", { "control" : "w5" } );
      assertFalse( widget._line.getVisibility() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "CHECK" ] );
      this._setPropertyByProtocol( "w4", { "selection" : true } );
      assertTrue( widget._selected );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetNoRadioGroupByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [ "NO_RADIO_GROUP" ] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "RADIO" ] );
      assertTrue( widget.getNoRadioGroup() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "RADIO" ] );
      this._setListenerByProtocol( "w4", { "selection" : true } );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

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
      org.eclipse.swt.WidgetManager.getInstance().add( item, "w1" );
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
    },

    _createToolBarByProtocol : function( id, parentId, style ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ToolBar",
        "properties" : {
          "style" : style,
          "parent" : parentId
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    _createToolItemByProtocol : function( id, parentId, style ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ToolItem",
        "properties" : {
          "style" : style,
          "parent" : parentId,
          "index" : 0
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    _setPropertyByProtocol : function( id, properties ) {
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : id,
        "action" : "set",
        "properties" : properties
      } );
    },

    _setListenerByProtocol : function( id, listeners ) {
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : id,
        "action" : "listen",
        "properties" : listeners
      } );
    }

  }
  
} );
