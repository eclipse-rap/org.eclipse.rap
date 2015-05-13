/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Processor = rwt.remote.MessageProcessor;
var ObjectRegistry = rwt.remote.ObjectRegistry;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ToolBarTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateTooBarByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ToolBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.ToolBar );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertTrue( widget.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      assertFalse( widget.hasState( "rwt_RIGHT" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreateTooBarWithFlatByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ToolBar",
        "properties" : {
          "style" : [ "HORIZONTAL", "FLAT" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_HORIZONTAL" ) );
      assertTrue( widget.hasState( "rwt_FLAT" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreateTooBarWithRightByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ToolBar",
        "properties" : {
          "style" : [ "HORIZONTAL", "RIGHT" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.hasState( "rwt_HORIZONTAL" ) );
      assertTrue( widget.hasState( "rwt_RIGHT" ) );
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

    testCreateVerticalPushToolItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [ "VERTICAL" ] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      assertTrue( widget instanceof rwt.widgets.ToolItem );
      assertIdentical( toolbar, widget.getParent() );
      assertTrue( widget.hasState( "push" ) );
      assertTrue( widget.hasState( "rwt_PUSH" ) );
      assertFalse( widget.hasState( "rwt_FLAT" ) );
      assertTrue( widget.hasState( "rwt_VERTICAL" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testCreateVerticallyOrientedToolItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );

      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );

      assertTrue( widget.getVertical() );
      assertFalse( widget.hasState( "rwt_RIGHT" ) );
      shell.destroy();
    },

    testCreateHorizontallyOrientedToolItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );

      var toolbar = this._createToolBarByProtocol( "w3", "w2", [ "RIGHT" ] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );

      assertFalse( widget.getVertical() );
      assertTrue( widget.hasState( "rwt_RIGHT" ) );
      shell.destroy();
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

    testCreateDestroyToolItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      Processor.processOperation( { "target" : "w4", "action" : "destroy" } );
      TestUtil.flush();
      assertTrue( widget.isDisposed() );
      assertEquals( undefined, ObjectRegistry.getObject( "w4" ) );
      shell.destroy();
      toolbar.destroy();
    },

    testDestroyTooltemWithToolBarByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );

      Processor.processOperation( { "target" : "w3", "action" : "destroy" } );
      TestUtil.flush();

      assertTrue( toolbar.isDisposed() );
      assertTrue( widget.isDisposed() );
      assertEquals( undefined, ObjectRegistry.getObject( "w3" ) );
      assertEquals( undefined, ObjectRegistry.getObject( "w4" ) );
      shell.destroy();
      toolbar.destroy();
    },

    testSetBoundsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      TestUtil.protocolSet( "w4", { "bounds" : [ 1, 2, 3, 4 ] } );
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
      TestUtil.protocolSet( "w4", { "visible" : false } );
      assertFalse( widget.getVisibility() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetEnabledByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      TestUtil.protocolSet( "w4", { "enabled" : false } );
      assertFalse( widget.getEnabled() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetToolTipMarkupEnabledByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );

      TestUtil.protocolSet( "w4", { "toolTipMarkupEnabled" : true } );

      assertTrue( widget.getUserData( "toolTipMarkupEnabled" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetToolTipByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      TestUtil.protocolSet( "w4", { "toolTip" : "hello blue world" } );
      assertEquals( "hello blue world", widget.getToolTipText() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      TestUtil.protocolSet( "w4", { "text" : "text\n & \"text" } );
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
      TestUtil.protocolSet( "w4", { "customVariant" : "variant_blue" } );
      assertTrue( widget.hasState( "variant_blue" ) );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetImageByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "PUSH" ] );
      TestUtil.protocolSet( "w4", { "image" : [ "image.png", 10, 20 ] } );
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
      TestUtil.protocolSet( "w4", { "hotImage" : [ "image.png", 10, 20 ] } );
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
      TestUtil.protocolSet( "w4", { "control" : "w5" } );
      assertFalse( widget._line.getVisibility() );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "CHECK" ] );
      TestUtil.protocolSet( "w4", { "selection" : true } );
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

    testSetBadgeByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var toolbar = this._createToolBarByProtocol( "w3", "w2", [] );
      var widget = this._createToolItemByProtocol( "w4", "w3", [ "CHECK" ] );

      TestUtil.protocolSet( "w4", { "badge" : "11" } );

      assertEquals( "11", widget.getElement().lastChild.textContent );
      shell.destroy();
      toolbar.destroy();
      widget.destroy();
    },

    testComputeBadgePosition : function() {
      var item = new rwt.widgets.ToolItem( "push" );

      assertEquals( [ 0, 0, "auto", "auto" ], item.computeBadgePosition() );
    },

    testClickDropDown : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      item.setLeft( 100 );
      item.setTop( 100 );
      item.setText( "hallo" );
      item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
      item.addToDocument();
      rwt.widgets.base.Widget.flushGlobalQueues();
      rwt.remote.ObjectRegistry.add( "w11", item );
      TestUtil.clearRequestLog();
      TestUtil.fakeListener( item, "Selection", true );
      TestUtil.fakeMouseClick( item, 103, 103 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w11", "Selection"   ) );
      assertEquals( undefined, message.findNotifyProperty( "w11", "Selection", "detail" ) );
      TestUtil.clearRequestLog();
      TestUtil.fakeMouseClick( item, 103 + item.$separator.get( 0).offsetLeft, 103 );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "arrow", message.findNotifyProperty( "w11", "Selection", "detail" ) );
      TestUtil.clearRequestLog();
      item.destroy();
    },

    testDropDownLineLayout : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      item.setPadding( 10, 5, 1, 5 );
      item.setText( "foo" );
      item.setDropDownArrow( [ "bar.jpg", 13, 13 ] );
      item.addToDocument();
      TestUtil.flush();

      var bounds = TestUtil.getElementBounds(item.$separator.get( 0 ));
      assertEquals(0, bounds.top);
      assertEquals(0, bounds.bottom);
      assertEquals(21, bounds.right);
      assertEquals(1, bounds.width);
      item.destroy();
    },

    testDropDownLineBorder : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      var border = new rwt.html.Border( 1, "outset", "black");
      item.setText( "foo" );
      item.setSeparatorBorder( border );
      item.setDropDownArrow( [ "bar.jpg", 13, 13 ] );
      item.addToDocument();
      rwt.widgets.base.Widget.flushGlobalQueues();
      var lineNode = item.$separator.get( 0 );
      assertContains( "outset", lineNode.style.cssText );
      item.destroy();
    },

    testDropDownArrowLayout : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      item.setPadding( 10, 5, 1, 5 );
      item.setHeight( 80 );
      item.setText( "foo" );
      item.setDropDownArrow( [ "bar.jpg", 13, 13 ] );
      item.addToDocument();
      TestUtil.flush();

      var bounds = TestUtil.getElementBounds( item.$dropDownArrow.get( 0 ) );
      assertEquals( 38, bounds.top );
      assertEquals( 29, bounds.bottom );
      assertEquals( 4, bounds.right );
      assertEquals( 13, bounds.width );
      assertEquals( 13, bounds.height );
      item.destroy();
    },

    testDropDownArrowImage : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      item.setText( "foo" );
      item.setDropDownArrow( [ "bar.jpg", 13, 13 ] );
      item.addToDocument();
      TestUtil.flush();

      var arrow = item.$dropDownArrow.get( 0 );
      assertEquals( "bar.jpg", TestUtil.getCssBackgroundImage( arrow ).split( "/" ).pop() );
      assertEquals( "absolute", arrow.style.position );
      item.destroy();
    },

    testDropDownIconCellHorizontalPosition : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      item.setPadding( 10, 5, 1, 5 );
      item.setWidth( 80 );
      item.setDropDownArrow( [ "bar.jpg", 13, 13 ] );
      item.setIcon( [ "foo.jpg", 20, 20 ] );
      item.addToDocument();
      TestUtil.flush();

      var bounds = TestUtil.getElementBounds( item.getCellNode( 1 ) );
      assertEquals(19, bounds.left);
      item.destroy();
    },

    testHotImage : function() {
      var item = new rwt.widgets.ToolItem( "push" );
      item.setText( "hallo" );
      item.setImage( "test1.jpg" );
      item.addToDocument();
      TestUtil.flush();
      assertTrue( item.isSeeable() );
      var node = item.getCellNode( 1 );
      assertContains(
        "test1.jpg",
        TestUtil.getCssBackgroundImage ( node )
      );
      TestUtil.mouseOver( item );
      TestUtil.flush();
      assertContains(
        "test1.jpg",
        TestUtil.getCssBackgroundImage ( node )
      );
      TestUtil.mouseOut( item );
      item.setHotImage( "test2.jpg" );
      TestUtil.flush();
      assertContains(
        "test1.jpg",
        TestUtil.getCssBackgroundImage ( node )
      );
      TestUtil.mouseOver( item );
      TestUtil.flush();
      assertTrue( item.hasState( "over" ) );
      assertContains(
        "test2.jpg",
        TestUtil.getCssBackgroundImage ( node )
      );
      TestUtil.mouseOut( item );
      TestUtil.flush();
      assertContains(
        "test1.jpg",
        TestUtil.getCssBackgroundImage ( node )
      );
      item.destroy();
    },

    testDoNotHoverDisabledItems : function() {
      this.createDefaultToolBar();
      this.toolItem1.setEnabled( false );

      TestUtil.mouseOver( this.toolItem1 );

      assertFalse( this.toolItem1.hasState( "over" ) );
      this.disposeToolBar();
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
      TestUtil.mouseOver( this.toolItem3 );
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
      TestUtil.mouseOut( this.toolItem1 );
      TestUtil.mouseOver( this.toolItem3 );
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
      TestUtil.press( this.toolBar, "Left" );
      assertFalse( this.toolItem1.hasState( "over" ) );
      assertTrue( this.toolItem3.hasState( "over" ) );
      TestUtil.press( this.toolBar, "Left" );
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

      TestUtil.press( this.toolBar, "Left" );

      assertTrue( this.toolItem1.hasState( "over" ) );
      assertFalse( this.toolItem2.hasState( "over" ) );
      assertFalse( this.toolItem3.hasState( "over" ) );
      this.disposeToolBar();
    },

    testKeyboardControlRight : function() {
      this.createDefaultToolBar();
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      TestUtil.press( this.toolBar, "Right" );
      assertFalse( this.toolItem1.hasState( "over" ) );
      assertTrue( this.toolItem3.hasState( "over" ) );
      TestUtil.press( this.toolBar, "Right" );
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

      TestUtil.press( this.toolBar, "Right" );

      assertTrue( this.toolItem1.hasState( "over" ) );
      assertFalse( this.toolItem2.hasState( "over" ) );
      assertFalse( this.toolItem3.hasState( "over" ) );
      this.disposeToolBar();
    },

    testKeyboardControlExecute : function() {
      this.createDefaultToolBar();
      rwt.remote.ObjectRegistry.add( "w11", this.toolItem1 );
      var executed = false;
      this.toolItem1.addEventListener( "execute", function( event ) {
        executed = true;
      } );
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      TestUtil.press( this.toolBar, "Enter" );
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
      ObjectRegistry.add( "t4", text );
      this.toolBar.addAt( text, 4 );
      text.setSpace( 21, 30, 0, 20 );
      TestUtil.flush();
      this.toolBar.focus();
      assertTrue( this.toolItem1.hasState( "over" ) );
      var executed = false;
      this.toolItem1.addEventListener( "execute", function( event ) {
        executed = true;
      } );
      text.focus();
      TestUtil.pressOnce( text, "Enter" );
      assertFalse( executed );
      text.dispose();
      this.disposeToolBar();
    },

    testToolItemFiresDropDownClickedEvent : function() {
      var item = new rwt.widgets.ToolItem( "dropDown" );
      item.setLeft( 100 );
      item.setTop( 100 );
      item.setText( "hallo" );
      item.setDropDownArrow( [ "bla.jpg", 13, 13 ] );
      item.addToDocument();
      rwt.widgets.base.Widget.flushGlobalQueues();
      rwt.remote.WidgetManager.getInstance().add( item, "w1" );
      TestUtil.clearRequestLog();
      TestUtil.fakeListener( item, "Selection", true );
      var log = 0;
      item.addEventListener( "dropDownClicked", function() {
        log++;
      } );

      TestUtil.fakeMouseClick( item, 103 + item.$separator.get( 0).offsetLeft, 103 );

      assertTrue( log > 0 );
      TestUtil.clearRequestLog();
      item.destroy();
    },

    testRenderMnemonic_OnActivate : function() {
      var shell = createActiveShell();
      var item = new rwt.widgets.ToolItem( "push" );
      item.setText( "foo" );
      item.setParent( shell );
      item.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      TestUtil.flush();

      assertEquals( "f<span style=\"text-decoration:underline\">o</span>o", item.getCellContent( 2 ) );
      shell.destroy();
    },

    testRenderMnemonic_OnDeactivate : function() {
      var shell = createActiveShell();
      var item = new rwt.widgets.ToolItem( "push" );
      item.setText( "foo" );
      item.setParent( shell );
      item.setMnemonicIndex( 1 );
      TestUtil.flush();

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      rwt.widgets.util.MnemonicHandler.getInstance().deactivate();
      TestUtil.flush();

      assertEquals( "foo", item.getCellContent( 2 ) );
      shell.destroy();
    },

    testTriggerMnemonic_SendsSelection : function() {
      var shell = createActiveShell();
      var item = new rwt.widgets.ToolItem( "push" );
      rwt.remote.ObjectRegistry.add( "w11", item );
      item.setText( "foo" );
      item.setParent( shell );
      item.setMnemonicIndex( 1 );
      TestUtil.fakeListener( item, "Selection", true );
      TestUtil.flush();
      var success = false;

      rwt.widgets.util.MnemonicHandler.getInstance().activate();
      success = rwt.widgets.util.MnemonicHandler.getInstance().trigger( 79 );
      TestUtil.flush();

      assertTrue( success );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w11", "Selection" ) );
      shell.destroy();
    },

    testToggleFirstLastState_onSingleItem : function() {
      var shell = createActiveShell();
      var item = new rwt.widgets.ToolItem( "push" );

      shell.add( item );

      assertTrue( item.hasState( "first" ) );
      assertTrue( item.hasState( "last" ) );
      shell.destroy();
    },

    testToggleFirstLastState_onMultipleItems : function() {
      var shell = createActiveShell();
      var item1 = new rwt.widgets.ToolItem( "push" );
      var item2 = new rwt.widgets.ToolItem( "push" );
      var item3 = new rwt.widgets.ToolItem( "push" );

      shell.add( item1 );
      shell.add( item2 );
      shell.add( item3 );

      assertTrue( item1.hasState( "first" ) );
      assertFalse( item1.hasState( "last" ) );
      assertFalse( item2.hasState( "first" ) );
      assertFalse( item2.hasState( "last" ) );
      assertFalse( item3.hasState( "first" ) );
      assertTrue( item3.hasState( "last" ) );
      shell.destroy();
    },

    testToggleFirstLastState_onItemRemoval : function() {
      var shell = createActiveShell();
      var item1 = new rwt.widgets.ToolItem( "push" );
      var item2 = new rwt.widgets.ToolItem( "push" );
      var item3 = new rwt.widgets.ToolItem( "push" );
      shell.add( item1 );
      shell.add( item2 );
      shell.add( item3 );

      shell.remove( item3 );

      assertTrue( item1.hasState( "first" ) );
      assertFalse( item1.hasState( "last" ) );
      assertFalse( item2.hasState( "first" ) );
      assertTrue( item2.hasState( "last" ) );
      shell.destroy();
    },

    testToggleFirstLastState_onAddingItemAtFirstPosition : function() {
      var shell = createActiveShell();
      var item1 = new rwt.widgets.ToolItem( "push" );
      var item2 = new rwt.widgets.ToolItem( "push" );
      var item3 = new rwt.widgets.ToolItem( "push" );
      shell.add( item1 );
      shell.add( item2 );

      shell.addAt( item3, 0 );

      assertFalse( item1.hasState( "first" ) );
      assertFalse( item1.hasState( "last" ) );
      assertFalse( item2.hasState( "first" ) );
      assertTrue( item2.hasState( "last" ) );
      assertTrue( item3.hasState( "first" ) );
      assertFalse( item3.hasState( "last" ) );
      shell.destroy();
    },

    /////////
    // Helper

    createDefaultToolBar : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
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
      TestUtil.flush();
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
      Processor.processOperation( {
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
      Processor.processOperation( {
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
    }

  }

} );

var createActiveShell = function() {
  var shell = TestUtil.createShellByProtocol( "w2" );
  shell.show();
  shell.setActive( true );
  TestUtil.flush();
  TestUtil.clearRequestLog();
  return shell;
};

}() );
