/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ExpandBarTest", {

  extend : qx.core.Object,

  members : {

    testCreateExpandBarByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      assertTrue( widget instanceof org.eclipse.swt.widgets.ExpandBar );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      shell.destroy();
      widget.destroy();
    },

    testSetBottomSpacingBoundsByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      testUtil.protocolSet( "w3", { "bottomSpacingBounds"  : [ 1, 2, 3, 4] } );
      assertEquals( 1, widget._bottomSpacing.getLeft() );
      assertEquals( 2, widget._bottomSpacing.getTop() );
      assertEquals( 3, widget._bottomSpacing.getWidth() );
      assertEquals( 4, widget._bottomSpacing.getHeight() );
      shell.destroy();
      widget.destroy();
    },

    testSetVScrollBarVisibleByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      testUtil.protocolSet( "w3", { "vScrollBarVisible"  : true } );
      assertTrue( widget._vertScrollBar.getDisplay() );
      shell.destroy();
      widget.destroy();
    },

    testSetVScrollBarMaxByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var widget = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      testUtil.protocolSet( "w3", { "vScrollBarMax"  : 35 } );
      assertEquals( 35, widget._vertScrollBar.getMaximum() );
      shell.destroy();
      widget.destroy();
    },

    testCreateExpandItemByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      assertTrue( item instanceof org.eclipse.swt.widgets.ExpandItem );
      assertIdentical( bar._clientArea, item.getParent() );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemCustomVariantByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      testUtil.protocolSet( "w4", { "customVariant" : "variant_blue" } );
      assertTrue( item.hasState( "variant_blue" ) );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemBoundsByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      testUtil.protocolSet( "w4", { "bounds" : [ 1, 2, 3, 4 ] } );
      assertEquals( 1, item.getLeft() );
      assertEquals( 2, item.getTop() );
      assertEquals( 3, item.getWidth() );
      assertEquals( 4, item.getHeight() );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemTextByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      testUtil.protocolSet( "w4", { "text" : "foo<>bar" } );
      assertEquals( "foo&lt;&gt;bar", item._text );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemImageByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      testUtil.protocolSet( "w4", { "image" : [ "image.gif", 10, 20 ] } );
      assertEquals( "image.gif", item._image );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemExpandedByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      testUtil.protocolSet( "w4", { "expanded" : true } );
      assertTrue( item._expanded );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    testSetItemHeaderHeightByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var bar = this._createExpandBarByProtocol( "w3", "w2", [ "NONE" ] );
      var item = this._createExpandItemByProtocol( "w4", "w3", [ "NONE" ] );
      testUtil.protocolSet( "w4", { "headerHeight" : 12 } );
      assertEquals( 12, item._headerHeight );
      shell.destroy();
      bar.destroy();
      item.destroy();
    },

    /////////
    // Helper

    _createExpandBarByProtocol : function( id, parentId, style ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ExpandBar",
        "properties" : {
          "style" : style,
          "parent" : parentId
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    _createExpandItemByProtocol : function( id, parentId ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ExpandItem",
        "properties" : {
          "style" : [],
          "parent" : parentId
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    }

  }
  
} );