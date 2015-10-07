/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
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

var shell;
var bar;
var item;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ExpandBarTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      TestUtil.flush();
    },

    tearDown : function() {
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testExpandBarHandlerEventsList : function() {
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.ExpandBar" );

      assertTrue( rwt.util.Arrays.contains( handler.events, "Expand" ) );
      assertTrue( rwt.util.Arrays.contains( handler.events, "Collapse" ) );
    },

    testCreateExpandBarByProtocol : function() {
      assertTrue( bar instanceof rwt.widgets.ExpandBar );
      assertIdentical( shell, bar.getParent() );
      assertTrue( bar.getUserData( "isControl") );
    },

    testSetBottomSpacingBoundsByProtocol : function() {
      TestUtil.protocolSet( "w3", { "bottomSpacingBounds"  : [ 1, 2, 3, 4] } );
      assertEquals( 1, bar._bottomSpacing.getLeft() );
      assertEquals( 2, bar._bottomSpacing.getTop() );
      assertEquals( 3, bar._bottomSpacing.getWidth() );
      assertEquals( 4, bar._bottomSpacing.getHeight() );
    },

    testSetVScrollBarVisibleByProtocol : function() {
      TestUtil.protocolSet( "w3_vscroll", { "visibility" : true } );

      assertTrue( bar._vertScrollBar.getDisplay() );
    },

    testSetVScrollBarMaxByProtocol : function() {
      TestUtil.protocolSet( "w3", { "vScrollBarMax"  : 35 } );

      assertEquals( 35, bar._vertScrollBar.getMaximum() );
    },

    testCreateExpandItemByProtocol : function() {
      assertTrue( item instanceof rwt.widgets.ExpandItem );
      assertIdentical( bar._clientArea, item.getParent() );
      assertEquals( 1, item._header.getFlexibleCell() );
      assertEquals( "ellipsis", item._header.getTextOverflow() );
    },

    testDestroyExpandBarWithChildren : function() {
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
    },

    testSetItemCustomVariantByProtocol : function() {
      TestUtil.protocolSet( "w4", { "customVariant" : "variant_blue" } );

      assertTrue( item.hasState( "variant_blue" ) );
      assertTrue( item._header.hasState( "variant_blue" ) );
    },

    testSetItemBoundsByProtocol : function() {
      TestUtil.protocolSet( "w4", { "bounds" : [ 1, 2, 3, 4 ] } );

      assertEquals( 1, item.getLeft() );
      assertEquals( 2, item.getTop() );
      assertEquals( 3, item.getWidth() );
      assertEquals( 4, item.getHeight() );
    },

    testSetItemTextByProtocol : function() {
      TestUtil.protocolSet( "w4", { "text" : "foo<>bar" } );

      assertEquals( "foo&lt;&gt;bar", item._header.getCellContent( 1 ) );
    },

    testSetItemImageByProtocol : function() {
      TestUtil.protocolSet( "w4", { "image" : [ "image.gif", 10, 20 ] } );

      assertEquals( "image.gif", item._header.getCellContent( 0 ) );
    },

    testSetItemExpandedByProtocol : function() {
      TestUtil.protocolSet( "w4", { "expanded" : true } );

      assertTrue( item.getExpanded() );
    },

    testSetItemHeaderHeightByProtocol : function() {
      TestUtil.protocolSet( "w4", { "headerHeight" : 12 } );

      assertEquals( 12, item._header.getHeight() );
    },

    testSendExpandEvent : function() {
      TestUtil.protocolListen( "w3", { "Expand" : true } );

      TestUtil.click( item._header );

      var message = TestUtil.getLastMessage();
      assertEquals( "w4", message.findNotifyProperty( "w3", "Expand", "item" ) );
    },

    testSendCollapseEvent : function() {
      item.setExpanded( true );
      TestUtil.flush();
      TestUtil.protocolListen( "w3", { "Collapse" : true } );

      TestUtil.click( item._header );

      var message = TestUtil.getLastMessage();
      assertEquals( "w4", message.findNotifyProperty( "w3", "Collapse", "item" ) );
    },

    testSendExpandedTrue : function() {
      TestUtil.click( item._header );
      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getLastMessage();
      assertTrue( message.findSetProperty( "w4", "expanded" ) );
      assertTrue( item.getExpanded() );
    },

    testSendExpandedFalse : function() {
      item.setExpanded( true );
      TestUtil.flush();

      TestUtil.click( item._header );
      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getLastMessage();
      assertFalse( message.findSetProperty( "w4", "expanded" ) );
      assertFalse( item.getExpanded() );
    },

    testTextColor : function() {
      bar.setTextColor( "#FF0000" );

      var style = item._header._getTargetNode().style;
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      bar.setTextColor( "#00FF00" );
      assertEquals( [ 0, 255, 0 ], rwt.util.Colors.stringToRgb( style.color ) );
    },

    testTextColor_byChangingEnabled_withoutUserColor : function() {
      TestUtil.fakeAppearance( "foo", {
        "style" : function( states ) {
          return {
            textColor : states.disabled ? "#FF0000" : "#00FF00"
          };
        }
      } );
      bar.setAppearance( "foo" );
      var style = item._header._getTargetNode().style;

      bar.setEnabled( false );
      TestUtil.flush();
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      bar.setEnabled( true );
      TestUtil.flush();
      assertEquals( [ 0, 255, 0 ], rwt.util.Colors.stringToRgb( style.color ) );
    },

    testTextColor_byChangingEnabled_withUserColor : function() {
      TestUtil.fakeAppearance( "foo", {
        "style" : function( states ) {
          return {
            textColor : states.disabled ? "#FF0000" : "#00FF00"
          };
        }
      } );
      bar.setAppearance( "foo" );
      bar.setTextColor( "#0000FF" );
      var style = item._header._getTargetNode().style;

      bar.setEnabled( false );
      TestUtil.flush();
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      bar.setEnabled( true );
      TestUtil.flush();
      assertEquals( [ 0, 0, 255 ], rwt.util.Colors.stringToRgb( style.color ) );
    },

    testChevronIcon : function() {
      assertNotNull( item._header.getCellContent( 2 ) );
      assertEquals( [ 16, 16 ], item._header.getCellDimension( 2 ) );
    },

    testSetDirection : function() {
      bar.setDirection( "rtl" );

      assertEquals( "rtl", item.getDirection() );
      assertEquals( "rtl", item._header.getDirection() );
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
      return ObjectRegistry.getObject( id );
    },

    _createExpandItemByProtocol : function( id, parentId ) {
      Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ExpandItem",
        "properties" : {
          "style" : [],
          "parent" : parentId,
          "text" : "foo"
        }
      } );
      return ObjectRegistry.getObject( id );
    }

  }

} );

}() );
