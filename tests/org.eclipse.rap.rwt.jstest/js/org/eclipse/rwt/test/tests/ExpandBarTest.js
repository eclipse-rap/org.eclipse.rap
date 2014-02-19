/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
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
var ObjectRegistry = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ExpandBarTest", {

  extend : rwt.qx.Object,

  members : {

    testExpandBarHandlerEventsList : function() {
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.ExpandBar" );

      assertTrue( rwt.util.Arrays.contains( handler.events, "Expand" ) );
      assertTrue( rwt.util.Arrays.contains( handler.events, "Collapse" ) );
    },

    testCreateExpandBarByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      assertTrue( widget instanceof rwt.widgets.ExpandBar );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      shell.destroy();
      widget.destroy();
    },

    testSetBottomSpacingBoundsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      TestUtil.protocolSet( "w3", { "bottomSpacingBounds"  : [ 1, 2, 3, 4] } );
      assertEquals( 1, widget._bottomSpacing.getLeft() );
      assertEquals( 2, widget._bottomSpacing.getTop() );
      assertEquals( 3, widget._bottomSpacing.getWidth() );
      assertEquals( 4, widget._bottomSpacing.getHeight() );
      shell.destroy();
      widget.destroy();
    },

    testSetVScrollBarVisibleByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );

      TestUtil.protocolSet( "w3_vscroll", { "visibility" : true } );

      assertTrue( widget._vertScrollBar.getDisplay() );
      shell.destroy();
      widget.destroy();
    },

    testSetVScrollBarMaxByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      TestUtil.protocolSet( "w3", { "vScrollBarMax"  : 35 } );
      assertEquals( 35, widget._vertScrollBar.getMaximum() );
      shell.destroy();
      widget.destroy();
    },

    testCreateExpandItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      assertTrue( item instanceof rwt.widgets.ExpandItem );
      assertIdentical( bar._clientArea, item.getParent() );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testDestroyExpandBarWithChildren : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      Processor.processOperationArray( [ "create", "w5", "rwt.widgets.Composite", {
          "style" : [ "BORDER" ],
          "parent" : "w3"
        }
      ] );
      var control = ObjectRegistry.getObject( "w5" );

      Processor.processOperationArray( [ "destroy", "w3" ] );
      TestUtil.flush();

      assertTrue( bar.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( item.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w4" ) == null );
      assertTrue( control.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w5" ) == null );
      shell.destroy();
    },


    testSetItemCustomVariantByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      TestUtil.protocolSet( "w4", { "customVariant" : "variant_blue" } );
      assertTrue( item.hasState( "variant_blue" ) );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemBoundsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      TestUtil.protocolSet( "w4", { "bounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( 1, item.getLeft() );
      assertEquals( 2, item.getTop() );
      assertEquals( 3, item.getWidth() );
      assertEquals( 4, item.getHeight() );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemTextByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      TestUtil.protocolSet( "w4", { "text" : "foo<>bar" } );
      assertEquals( "foo&lt;&gt;bar", item._text );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemImageByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      TestUtil.protocolSet( "w4", { "image" : [ "image.gif", 10, 20 ] } );
      assertEquals( "image.gif", item._image );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemExpandedByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      TestUtil.protocolSet( "w4", { "expanded" : true } );
      assertTrue( item._expanded );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemHeaderHeightByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      TestUtil.protocolSet( "w4", { "headerHeight" : 12 } );
      assertEquals( 12, item._headerHeight );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSendExpandEvent : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      TestUtil.flush();
      TestUtil.protocolListen( "w3", { "Expand" : true } );

      TestUtil.click( item._header );

      var message = TestUtil.getLastMessage();
      assertEquals( "w4", message.findNotifyProperty( "w3", "Expand", "item" ) );
      shell.destroy();
    },

    testSendCollapseEvent : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      item.setExpanded( true );
      TestUtil.flush();
      TestUtil.protocolListen( "w3", { "Collapse" : true } );

      TestUtil.click( item._header );

      var message = TestUtil.getLastMessage();
      assertEquals( "w4", message.findNotifyProperty( "w3", "Collapse", "item" ) );
      shell.destroy();
    },

    testSendExpandedTrue : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      TestUtil.flush();

      TestUtil.click( item._header );
      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getLastMessage();
      assertTrue( message.findSetProperty( "w4", "expanded" ) );
      assertTrue( item.getExpanded() );
      shell.destroy();
    },

    testSendExpandedFalse : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      item.setExpanded( true );
      TestUtil.flush();

      TestUtil.click( item._header );
      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getLastMessage();
      assertFalse( message.findSetProperty( "w4", "expanded" ) );
      assertFalse( item.getExpanded() );
      shell.destroy();
    },

    /////////
    // Helper

    _createExpandBarByProtocol : function( id, parentId, style ) {
      Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ExpandBar",
        "properties" : {
          "style" : style,
          "parent" : parentId
        }
      } );
      Processor.processOperation( {
        "target" : id + "_vscroll",
        "action" : "create",
        "type" : "rwt.widgets.ScrollBar",
        "properties" : {
          "parent" : id,
          "style" : [ "VERTICAL" ],
          "visibility" : true
        }
      } );
      Processor.processOperation( {
        "target" : id + "_hscroll",
        "action" : "create",
        "type" : "rwt.widgets.ScrollBar",
        "properties" : {
          "parent" : id,
          "style" : [ "HORIZONTAL" ],
          "visibility" : true
        }
      } );
      return ObjectRegistry.getObject( id );
    },

    _createExpandItemByProtocol : function( id, parentId ) {
      Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ExpandItem",
        "properties" : {
          "style" : [],
          "parent" : parentId
        }
      } );
      return ObjectRegistry.getObject( id );
    }

  }

} );

}() );
