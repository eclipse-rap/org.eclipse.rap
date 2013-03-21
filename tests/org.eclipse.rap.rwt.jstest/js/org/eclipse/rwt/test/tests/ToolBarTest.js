/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ToolBarTest", {

  extend : rwt.qx.Object,

  construct : function() {
    this.base( arguments );
    this.TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
  },

  members : {

    testCreateTooBarByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ToolBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.ToolBar );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreateTooBarWithFlatByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ToolBar",
        "properties" : {
          "style" : [ "HORIZONTAL", "FLAT" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_HORIZONTAL" ) );
      assertTrue( widget.hasState( "rwt_FLAT" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreatePushToolItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      assertTrue( widget instanceof rwt.widgets.ToolItem );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "push" ) );
      assertTrue( widget.hasState( "rwt_PUSH" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      assertFalse( widget.hasState( "rwt_VERTICAL" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateRadioToolItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget =  this._createToolItemByProtocol( "w4", "w3", [ "RADIO" ] );
      assertTrue( widget instanceof rwt.widgets.ToolItem );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "radio" ) );
      assertTrue( widget.hasState( "rwt_RADIO" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      assertFalse( widget.hasState( "rwt_VERTICAL" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateCheckToolItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget =  this._createToolItemByProtocol( "w4", "w3", [ "CHECK" ] );
      assertTrue( widget instanceof rwt.widgets.ToolItem );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "check" ) );
      assertTrue( widget.hasState( "rwt_CHECK" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      assertFalse( widget.hasState( "rwt_VERTICAL" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateDropDownToolItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget =  this._createToolItemByProtocol( "w4", "w3", [ "DROP_DOWN" ] );
      assertTrue( widget instanceof rwt.widgets.ToolItem );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "dropDown" ) );
      assertTrue( widget.hasState( "rwt_DROP_DOWN" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      assertFalse( widget.hasState( "rwt_VERTICAL" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateSeparatorToolItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [ "FLAT" ] );
      var widget =  this._createToolItemByProtocol( "w4", "w3", [ "SEPARATOR" ] );
      assertTrue( widget instanceof rwt.widgets.ToolItemSeparator );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget._line.getVisibility() );
      assertTrue( widget.hasState( "rwt_SEPARATOR" ) );
      assertNotNull( widget._line );
      assertFalse( widget._vertical );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateVerticalSeparatorToolItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [ "VERTICAL" ] );
      var widget =  this._createToolItemByProtocol( "w4", "w3", [ "SEPARATOR" ] );
      assertTrue( widget instanceof rwt.widgets.ToolItemSeparator );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "rwt_SEPARATOR" ) );
      assertNull( widget._line );
      assertTrue( widget._vertical );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateToolItemWithIndexByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
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

    testCreateDestroyTooltemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      TestUtil.flush();
      assertTrue( widget.isDisposed() );
      var ObjectManager = rwt.remote.ObjectRegistry;
      assertEquals( undefined, ObjectManager.getObject( "w4" ) );
      shell.destroy();
      toolbar.destroy();
    },

    testDestroyTooltemWithToolBarByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );

      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      TestUtil.flush();

      assertTrue( toolbar.isDisposed() );
      assertTrue( widget.isDisposed() );
      var ObjectManager = rwt.remote.ObjectRegistry;
      assertEquals( undefined, ObjectManager.getObject( "w3" ) );
      assertEquals( undefined, ObjectManager.getObject( "w4" ) );
      shell.destroy();
      toolbar.destroy();
    },

    testSetBoundsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "visible" : false } );
      assertFalse( widget.getVisibility() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetEnabledByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "enabled" : false } );
      assertFalse( widget.getEnabled() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetToolTipByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "toolTip" : "hello blue world" } );
      assertEquals( "hello blue world", widget.getUserData( "toolTipText" ) );
      assertTrue( widget.getToolTip() !== null );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "text" : "text\n & \"text" } );
      assertEquals( "text\n &amp; &quot;text", widget.getCellContent( 2 ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetMnemonicIndexByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );

      TestUtil.protocolSet( "w4", { "mnemonicIndex" : 6 } );

      assertEquals( 6, widget.getMnemonicIndex() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextResetsMnemonicIndex : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      TestUtil.protocolSet( "w4", { "mnemonicIndex" : 6 } );

      TestUtil.protocolSet( "w4", { "text" : "blue" } );

      assertNull( widget.getMnemonicIndex() );
      shell.destroy();
      widget.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      this._setPropertyByProtocol( "w4", { "customVariant" : "variant_blue" } );
      assertTrue( widget.hasState( "variant_blue" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetImageByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [ "FLAT" ] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "SEPARATOR" ] );
      this._setPropertyByProtocol( "w4", { "control" : "w5" } );
      assertFalse( widget._line.getVisibility() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "CHECK" ] );
      this._setPropertyByProtocol( "w4", { "selection" : true } );
      assertTrue( widget._selected );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetNoRadioGroupByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [ "NO_RADIO_GROUP" ] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "RADIO" ] );
      assertTrue( widget.getNoRadioGroup() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "RADIO" ] );
      this._setListenerByProtocol( "w4", { "Selection" : true } );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testClickDropDown : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      this.item = item;
      item.setLeft( 100 );
      item.setTop( 100 );
      item.setText( "hallo" );
      item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
      this._currentItem = item;
      item.addToDocument();
      rwt.widgets.base.Widget.flushGlobalQueues();
      var lineStyle = item.getCellNode( 3 ).style;
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.ToolItem" );
      rwt.remote.ObjectRegistry.add( "w1", item, handler );
      this.TestUtil.clearRequestLog();
      item.setHasSelectionListener( true );
      this.TestUtil.fakeMouseClick( item, 103, 103 );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      var message = this.TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w1", "Selection"   ) );
      assertEquals( undefined, message.findNotifyProperty( "w1", "Selection", "detail" ) );
      this.TestUtil.clearRequestLog();
      this.TestUtil.fakeMouseClick( item, 103 + parseInt( lineStyle.left, 10 ), 103 );
      assertEquals( 1, this.TestUtil.getRequestsSend() );
      var message = this.TestUtil.getMessageObject();
      assertEquals( "arrow", message.findNotifyProperty( "w1", "Selection", "detail" ) );
      this.TestUtil.clearRequestLog();
      item.destroy();
      this.item = null;
    },

    testDropDownLayoutBug : function() {
      if( rwt.client.Client.isMshtml() ) {
        var item = new rwt.widgets.ToolItem( "dropDown" );
        this.item = item;
        item.setLeft( 100 );
        item.setTop( 100 );
        item.setWidth( 100 );
        item.setHeight( 100 );
        item.setText( "hallo" );
        item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
        this._currentItem = item;
        item.addToDocument();
        rwt.widgets.base.Widget.flushGlobalQueues();
        var down = item.getCellNode( 4 );
        assertEquals( "0px", down.style.lineHeight );
        this.TestUtil.hoverFromTo( document.body, item.getElement() );
        rwt.widgets.base.Widget.flushGlobalQueues();
        assertEquals( "0px", down.style.lineHeight );
        item.destroy();
        this.item = null;
      }
    },

    testDropDownLineHeight : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      this.item = item;
      item.setText( "hallo" );
      item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
      item.setPaddingTop( 10 );
      item.setPaddingBottom( 1 );
      this._currentItem = item;
      item.addToDocument();
      rwt.widgets.base.Widget.flushGlobalQueues();
      var lineNode = item.getCellNode( 3 );
      var padding = item.getPaddingTop() + item.getPaddingBottom();
      var inner = item.getInnerHeight();
      var targetHeight = inner + padding;
      assertTrue( targetHeight <= parseInt( lineNode.style.height, 10 ) );
      assertTrue( 0 >= parseInt( lineNode.style.top, 10 ) );
      item.destroy();
      this.item = null;
    },

    testDropDownLineBorder : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      var border = new rwt.html.Border( 1, "outset", "black");
      this.item = item;
      item.setText( "hallo" );
      item.setSeparatorBorder( border );
      item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
      this._currentItem = item;
      item.addToDocument();
      rwt.widgets.base.Widget.flushGlobalQueues();
      var lineNode = item.getCellNode( 3 );
      assertContains( "outset", lineNode.style.cssText );
      item.destroy();
      this.item = null;
    },

    testHotImage : function() {
      var item = new rwt.widgets.ToolItem( "push" );
      item.setText( "hallo" );
      item.setImage( "test1.jpg" );
      item.addToDocument();
      this.TestUtil.flush();
      assertTrue( item.isSeeable() );
      var node = item.getCellNode( 1 );
      assertContains(
        "test1.jpg",
        this.TestUtil.getCssBackgroundImage ( node )
      );
      this.TestUtil.mouseOver( item );
      this.TestUtil.flush();
      assertContains(
        "test1.jpg",
        this.TestUtil.getCssBackgroundImage ( node )
      );
      this.TestUtil.mouseOut( item );
      item.setHotImage( "test2.jpg" );
      this.TestUtil.flush();
      assertContains(
        "test1.jpg",
        this.TestUtil.getCssBackgroundImage ( node )
      );
      this.TestUtil.mouseOver( item );
      this.TestUtil.flush();
      assertTrue( item.hasState( "over" ) );
      this.item = item;
      assertContains(
        "test2.jpg",
        this.TestUtil.getCssBackgroundImage ( node )
      );
      this.TestUtil.mouseOut( item );
      this.TestUtil.flush();
      assertContains(
        "test1.jpg",
        this.TestUtil.getCssBackgroundImage ( node )
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
      this.TestUtil.mouseOver( this.toolItem3 );
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
      this.TestUtil.mouseOver( this.toolItem3 );
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
      this.TestUtil.press( this.toolBar, "Left" );
      assertFalse( this.toolItem1.hasState( "over" ) );
      assertTrue( this.toolItem3.hasState( "over" ) );
      this.TestUtil.press( this.toolBar, "Left" );
      assertTrue( this.toolItem1.hasState( "over" ) );
      assertFalse( this.toolItem3.hasState( "over" ) );
      this.disposeToolBar();
    },

    testKeyboardControlLeft_WithAllItemsDisabled : function() {
      this.createDefaultToolBar();
      this.toolItem1.setEnabled( false );
      this.toolItem3.setEnabled( false );
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );

      this.TestUtil.press( this.toolBar, "Left" );

      assertTrue( this.toolItem1.hasState( "over" ) );
      assertFalse( this.toolItem2.hasState( "over" ) );
      assertFalse( this.toolItem3.hasState( "over" ) );
      this.disposeToolBar();
    },

    testKeyboardControlRight : function() {
      this.createDefaultToolBar();
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      this.TestUtil.press( this.toolBar, "Right" );
      assertFalse( this.toolItem1.hasState( "over" ) );
      assertTrue( this.toolItem3.hasState( "over" ) );
      this.TestUtil.press( this.toolBar, "Right" );
      assertTrue( this.toolItem1.hasState( "over" ) );
      assertFalse( this.toolItem3.hasState( "over" ) );
      this.disposeToolBar();
    },

    testKeyboardControlRight_WithAllItemsDisabled : function() {
      this.createDefaultToolBar();
      this.toolItem1.setEnabled( false );
      this.toolItem3.setEnabled( false );
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );

      this.TestUtil.press( this.toolBar, "Right" );

      assertTrue( this.toolItem1.hasState( "over" ) );
      assertFalse( this.toolItem2.hasState( "over" ) );
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
      this.TestUtil.press( this.toolBar, "Enter" );
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
      var text = new rwt.widgets.Text( false );
      this.toolBar.addAt( text, 4 );
      text.setSpace( 21, 30, 0, 20 );
      this.TestUtil.flush();
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      var executed = false;
      this.toolItem1.addEventListener( "execute", function( event ) {
        executed = true;
      } );
      text.focus();
      this.TestUtil.pressOnce( text, "Enter" );
      assertFalse( executed );
      text.dispose();
      this.disposeToolBar();
    },

    testToolItemFiresDropDownClickedEvent : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      this.item = item;
      item.setLeft( 100 );
      item.setTop( 100 );
      item.setText( "hallo" );
      item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
      this._currentItem = item;
      item.addToDocument();
      rwt.widgets.base.Widget.flushGlobalQueues();
      var lineStyle = item.getCellNode( 3 ).style;
      rwt.remote.WidgetManager.getInstance().add( item, "w1" );
      this.TestUtil.clearRequestLog();
      item.setHasSelectionListener( true );
      var log = 0;
      item.addEventListener( "dropDownClicked", function() {
        log++;
      } );

      this.TestUtil.fakeMouseClick( item, 103 + parseInt( lineStyle.left, 10 ), 103 );

      assertTrue( log > 0 );
      this.TestUtil.clearRequestLog();
      item.destroy();
      this.item = null;
    },

    testRenderMnemonic_OnActivate : function() {
      var item = new rwt.widgets.ToolItem( "push" );
      item.setText( "foo" );
      item.addToDocument();
      item.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      assertEquals( "f<span style=\"text-decoration:underline\">o</span>o", item.getCellContent( 2 ) );
      item.destroy();
    },


    testRenderMnemonic_OnDeactivate : function() {
      var item = new rwt.widgets.ToolItem( "push" );
      item.setText( "foo" );
      item.addToDocument();
      item.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().deactivate();
      TestUtil.flush();

      assertEquals( "foo", item.getCellContent( 2 ) );
      item.destroy();
    },

    testTriggerMnemonic_SendsSelection : function() {
      var item = new rwt.widgets.ToolItem( "push" );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.ToolItem" );
      rwt.remote.ObjectRegistry.add( "w11", item, handler );
      item.setText( "foo" );
      item.addToDocument();
      item.setMnemonicIndex( 1 );
      item.setHasSelectionListener( true );
      TestUtil.flush();
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "Selection" ) );
      item.destroy();
    },

    /////////
    // Helper

    createDefaultToolBar : function() {
      var shell = this.TestUtil.createShellByProtocol( "w2" );
      this.toolBar = new rwt.widgets.ToolBar( false );
      this.toolItem1 = new rwt.widgets.ToolItem( "push", false );
      this.toolItem2 = new rwt.widgets.ToolItem( "push", false  );
      this.toolItem3 = new rwt.widgets.ToolItem( "push", false  );
      this.separator = new rwt.widgets.ToolItemSeparator( "push" );
      this.toolItem1.setText( "1" );
      this.toolItem2.setText( "2" );
      this.toolItem3.setText( "3" );
      this.toolBar.addAt( this.toolItem1, 0 );
      this.toolBar.addAt( this.toolItem2, 1 );
      this.toolBar.addAt( this.separator, 2 );
      this.toolBar.addAt( this.toolItem3, 3 );
      this.toolItem2.setEnabled( false );
      this.toolBar.setParent( shell );
      this.TestUtil.flush();
    },

    disposeToolBar : function() {
      this.toolBar.getParent().destroy();
      this.toolBar.setParent( null );
      this.toolItem1.dispose();
      this.toolItem2.dispose();
      this.toolItem3.dispose();
      this.separator.dispose();
      this.toolItem1 = null;
      this.toolItem2 = null;
      this.toolItem3 = null;
      this.toolBar.dispose();
    },

    _createToolBarByProtocol : function( id, parentId, style ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ToolBar",
        "properties" : {
          "style" : style,
          "parent" : parentId
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    _createToolItemByProtocol : function( id, parentId, style ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ToolItem",
        "properties" : {
          "style" : style,
          "parent" : parentId,
          "index" : 0
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    _setPropertyByProtocol : function( id, properties ) {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : id,
        "action" : "set",
        "properties" : properties
      } );
    },

    _setListenerByProtocol : function( id, listeners ) {
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : id,
        "action" : "listen",
        "properties" : listeners
      } );
    }

  }

} );

}());
