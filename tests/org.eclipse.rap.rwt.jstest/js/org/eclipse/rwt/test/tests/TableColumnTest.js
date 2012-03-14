/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
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

var shell;

  
qx.Class.define( "org.eclipse.rwt.test.tests.TableColumnTest", {

  extend : qx.core.Object,

  members : {

    testCreateTableColumnByProtocol : function() {
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var Processor = org.eclipse.rwt.protocol.Processor;
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TableColumn",
        "properties" : {
          "style" : [],
          "parent" : "w3"
        }
      } );
      var widget = ObjectManager.getObject( "w4" );
      assertTrue( widget instanceof org.eclipse.swt.widgets.TableColumn );
      assertIdentical( tree._columnArea, widget.getParent() );
      assertEquals( "tree-column", widget.getAppearance() );
      widget.destroy();
      tree.destroy();
    },

    testSetIndexByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "index" : 3 } );
      assertEquals( 3, column.getIndex() );
      column.destroy();
      tree.destroy();
    },

    testSetLeftByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3 } );
      assertEquals( 3, column.getLeft() );
      column.destroy();
      tree.destroy();
    },

    testSetWidthByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "width" : 3 } );
      assertEquals( 3, column.getWidth() );
      column.destroy();
      tree.destroy();
    },

    testSetTextByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "text" : "foo<>\" bar" } );
      assertEquals( "foo&lt;&gt;&quot; bar", column.getLabel().toString() );
      column.destroy();
      tree.destroy();
    },

    testSetImageByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "image" : [ "image.png", 10, 20 ] } );
      assertEquals( "image.png", column.getIcon() );
      column.destroy();
      tree.destroy();
    },

    testSetToolTipByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "toolTip" : "hello blue world" } );
      assertEquals( "hello blue world", column.getUserData( "toolTipText" ) );
      assertTrue( column.getToolTip() !== null );
      column.destroy();
      tree.destroy();
    },

    testSetResizableByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "resizable" : false } );
      assertFalse( column._resizable );
      column.destroy();
      tree.destroy();
    },

    testSetMoveableByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "moveable" : true } );
      assertTrue( column._moveable );
      column.destroy();
      tree.destroy();
    },

    testSetAlignmentByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "alignment" : "right" } );
      assertEquals( "right", column.getLabelObject().getTextAlign() );
      assertEquals( "right", column.getHorizontalChildrenAlign() );
      assertEquals( "right", tree.getRenderConfig().alignment[ 0 ] );
      column.destroy();
      tree.destroy();
    },

    testSetFixedByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "fixed" : true } );
      assertTrue( "right", column.isFixed() );
      column.destroy();
      tree.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "customVariant" : "variant_blue" } );
      assertTrue( column.hasState( "variant_blue" ) );
      column.destroy();
      tree.destroy();
    },

    testShowResizeLine : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );
      TestUtil.flush();
      assertNotNull( column.getElement() );
      var button = qx.event.type.MouseEvent.buttons.left;
      
      TestUtil.fakeMouseEventDOM( column.getElement(), "mousedown", button, 23, 3 );
      TestUtil.flush();
      
      var line = tree._resizeLine;
      assertEquals( "", line.getStyleProperty( "visibility" ) );
      assertEquals( 21, parseInt( line._style.left, 10 ) );
      column.destroy();
      tree.destroy();
    },

    testShowResizeLineScrolled : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setScrollLeft( 10 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } ); // makes header scrollable
      TestUtil.flush();
      assertNotNull( column.getElement() );
      var button = qx.event.type.MouseEvent.buttons.left;
      
      TestUtil.fakeMouseEventDOM( column.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      
      var line = tree._resizeLine;
      assertEquals( "", line.getStyleProperty( "visibility" ) );
      assertEquals( 11, parseInt( line._style.left, 10 ) );
      column.destroy();
      tree.destroy();
    },
    
    testShowResizeLineFixedScrolled : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setScrollLeft( 10 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "fixed" : true } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } ); // makes header scrollable
      TestUtil.flush();
      assertNotNull( column.getElement() );
      var button = qx.event.type.MouseEvent.buttons.left;
      
      TestUtil.fakeMouseEventDOM( column.getElement(), "mousedown", button, 23, 3 );
      TestUtil.flush();
      
      var line = tree._resizeLine;
      assertEquals( "", line.getStyleProperty( "visibility" ) );
      assertEquals( 21, parseInt( line._style.left, 10 ) );
      column.destroy();
      tree.destroy();
    },
    
    //////////////////
    // Helping methods

    _createTreeByProtocol : function( id, parentId, styles ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.Tree",
        "properties" : {
          "style" : styles,
          "parent" : parentId,
          "appearance" : "tree",
          "selectionPadding" : [ 2, 4 ],
          "indentionWidth" : 16,
          "checkBoxMetrics" : [ 5, 16 ],
          "bounds" : [ 0, 0, 100, 100 ],
          "columnCount" : 3
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    _createColumnByProtocol : function( id, parentId, styles ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.TableColumn",
        "properties" : {
          "style" : styles,
          "parent" : parentId
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },
    
    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
    },
    
    tearDown : function() {
      shell.destroy();
      shell = null;
    }

  }
  
} );

}());