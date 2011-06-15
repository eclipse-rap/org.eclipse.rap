/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.TreeUtilTest", {

  extend : qx.core.Object,
  
  members : {

    testCreateDisposeSplitContainer : function() {
      var container = this._createSplitContainer();
      container.destroy();
    },

    testGetSubContainer : function() {
      var container = this._createSplitContainer();
      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertTrue( sub1 instanceof org.eclipse.rwt.widgets.TreeRowContainer );
      assertTrue( sub2 instanceof org.eclipse.rwt.widgets.TreeRowContainer );
      container.destroy();
    },

    testAddToParent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var container = this._createSplitContainer();
      var parent = testUtil.getDocument();
      parent.add( container );
      assertIdentical( parent, container.getSubContainer( 0 ).getParent() );
      assertIdentical( parent, container.getSubContainer( 1 ).getParent() );
      container.destroy();
    },

    testDestroy : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var container = this._createSplitContainer();
      testUtil.getDocument().add( container );
      container.destroy();
      testUtil.flush();
      assertTrue( container.getSubContainer( 0 ).isDisposed() );
      assertTrue( container.getSubContainer( 1 ).isDisposed() );
    },

    testCopySimpleRenderConfigFields : function() {
      var container = this._createSplitContainer();
      var config = container.getRenderConfig();
      var config1 = container.getSubContainer( 0 ).getRenderConfig();
      var config2 = container.getSubContainer( 1 ).getRenderConfig();
      var params = {
        "textColor" : "blue",
        "font" : "arial",
        "enabled" : false,
        "focused" : false,
        "linesVisible" : true,
        "fullSelection" : true,
        "hideSelection" : true,
        "variant" : "rwt_foo", 
        "selectionPadding" : [ 2, 3 ], 
        "indentionWidth" : 10,   
        //"hasCheckBoxes" : true,
        "checkBoxLeft" : 4, 
        "checkBoxWidth" : 13,
        "columnCount" : 5,
        "treeColumn" : 2,
        "itemImageWidth" : [ 4, 5, 6, 7, 8 ],
        "itemTextWidth" : [ 3, 4, 5, 6, 7 ],
        "alignment" : [ "left", "left", "right", "right", "left" ]
      };
      for( var key in params ) {
        config[ key ] = params[ key ];
      }
      container.renderAll();
      for( var key in params ) {
        assertEquals( params[ key ], config1[ key ] );
        assertEquals( params[ key ], config2[ key ] );
      }
      container.destroy();
    },

    testFixItemWidths : function() {
      var container = this._createSplitContainer();
      container.renderAll();
      var config1 = container.getSubContainer( 0 ).getRenderConfig();
      var config2 = container.getSubContainer( 1 ).getRenderConfig();
      assertEquals( [ 20, 0, 22, 0, 0 ], config1.itemWidth );
      assertEquals( [ 0, 21, 0, 23, 24 ], config2.itemWidth );
      container.destroy();
    },

    testHasCheckBoxes : function() {
      var container = this._createSplitContainer();
      container.getRenderConfig().hasCheckBoxes = true;
      container.renderAll();
      var config1 = container.getSubContainer( 0 ).getRenderConfig();
      var config2 = container.getSubContainer( 1 ).getRenderConfig();
      assertTrue( config1.hasCheckBoxes );
      assertFalse( config2.hasCheckBoxes );
      container.destroy();
    },

    testFixItemOffsets : function() {
      var container = this._createSplitContainer();
      container.renderAll();
      var config1 = container.getSubContainer( 0 ).getRenderConfig();
      var config2 = container.getSubContainer( 1 ).getRenderConfig();
      assertEquals( [ 30, 60, 0, 90, 120 ], config1.itemLeft ); 
      assertEquals( [ 32, 63, 4, 95, 126 ], config1.itemImageLeft );   
      assertEquals( [ 33, 64, 5, 96, 127 ], config1.itemTextLeft );
      assertEquals( [ 30, 0, 0, 30, 60 ], config2.itemLeft ); 
      assertEquals( [ 32, 3, 4, 35, 66 ], config2.itemImageLeft );   
      assertEquals( [ 33, 4, 5, 36, 67 ], config2.itemTextLeft );
      container.destroy();
    },

    testSetWidth : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var container = this._createSplitContainer();
      container.setWidth( 100 );
      container.renderAll();
      testUtil.flush();
      testUtil.flush();
      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 60, sub1.getWidth() );
      assertEquals( 60, sub1.getChildren()[ 0 ].getWidth() );
      assertEquals( 60, sub2.getLeft() );
      assertEquals( 40, sub2.getWidth() );
      assertEquals( 100, container.getWidth() );
      container.destroy();
    },

    testChangeWidth : function() {
      var container = this._createSplitContainer();
      container.setWidth( 100 );
      container.renderAll();
      container.setWidth( 200 );
      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 60, sub1.getWidth() );
      assertEquals( 60, sub2.getLeft() );
      assertEquals( 140, sub2.getWidth() );
      container.destroy();
    },

    testWidthSmallerSplit : function() {
      var container = this._createSplitContainer();
      container.setWidth( 50 );
      container.renderAll();
      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 50, sub1.getWidth() );
      assertEquals( 50, sub2.getLeft() );
      assertEquals( 0, sub2.getWidth() );
      container.destroy();
    },

    testSetRowWidth : function() {
      var container = this._createSplitContainer();
      container.setRowWidth( 150 );
      container.renderAll();
      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 90, sub2.getChildren()[ 0 ].getWidth() );
      container.destroy();
    },

    testSetRowWidthSmallerWidth : function() {
      var container = this._createSplitContainer();
      container.setRowWidth( 60 );
      container.renderAll();
      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 0, sub2.getChildren()[ 0 ].getWidth() );
      container.destroy();
    },

    testScrollLeft : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var container = this._createSplitContainer();
      container.setRowWidth( 200 );
      container.renderAll();
      testUtil.flush();
      container.setScrollLeft( 20 );
      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 0, sub1.getScrollLeft() );
      assertEquals( 20, sub2.getScrollLeft() );
      container.destroy();
    },

    testCreateMinimalTreeWithFixedColumns : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = new org.eclipse.rwt.widgets.Tree( { 
        "appearance": "table",
        "fixedColumns" : 3
      } );
      // first 3 columns fixed -> one extra contianer for first 3
      assertIdentical( tree, tree._rowContainer.getSubContainer( 0 ).getParent() );
      assertIdentical( tree, tree._rowContainer.getSubContainer( 1 ).getParent() );
      assertNull( tree, tree._rowContainer.getSubContainer( 2 ) );
      tree.addToDocument();
      testUtil.flush();
      tree.destroy();
    },

    testCreateNormalTreeWithFixedColumns : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      assertIdentical( tree, tree._rowContainer.getSubContainer( 0 ).getParent() );
      assertIdentical( tree, tree._rowContainer.getSubContainer( 1 ).getParent() );
      assertNull( tree, tree._rowContainer.getSubContainer( 2 ) );
      tree.destroy();
    },

    testRenderSplitItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      tree.setItemCount( 1 );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem(), 0 );
      item.setTexts( [ "c0", "c1", "c2", "c3", "c4" ] );
      testUtil.flush();
      var rowLeft = tree._rowContainer.getSubContainer( 0 ).getChildren()[ 0 ];
      var rowRight = tree._rowContainer.getSubContainer( 1 ).getChildren()[ 0 ];
      assertEquals( 2, rowLeft._getTargetNode().childNodes.length );
      assertEquals( 3, rowRight._getTargetNode().childNodes.length );
      assertEquals( "c0", rowLeft._getTargetNode().firstChild.innerHTML );
      assertEquals( "c1", rowRight._getTargetNode().firstChild.innerHTML );
      tree.destroy();
    },

    /////////
    // helper
    
    _createSplitContainer : function() {
      // column order: 2,0 - 1,3,4
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var argsMap = { "fixedColumns" : 2 };
      var result = org.eclipse.rwt.TreeUtil.createTreeRowContainer( argsMap );
      result.setRowAppearance( "table-row" );
      result.setSelectionProvider( function(){ return true; }, {} );
      result.setWidth( 100 );
      result.setHeight( 200 );
      var config = result.getRenderConfig();
      config.columnCount = 5;
      config.treeColumn = 2;
      config.alignment = [ "left", "left", "right", "right", "left" ];
      config.itemLeft = [ 30, 60, 0, 90, 120 ];
      config.itemWidth = [ 20, 21, 22, 23, 24 ];
      config.itemImageLeft = [ 32, 63, 4, 95, 126 ];   
      config.itemImageWidth = [ 4, 5, 6, 7, 8 ];
      config.itemTextLeft = [ 33, 64, 5, 96, 127];   
      config.itemTextWidth = [ 3, 4, 5, 6, 7 ];
      testUtil.getDocument().add( result );   
      testUtil.flush();
      return result;
    },

    _createSplitTree : function( noflush, option, arg ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var args = {
        "appearance" : "table",
        "fixedColumns" : 2 
      };
      if( option ) {
        args[ option ] = true;
      }
      if( option === "check" ) {
        args[ "checkBoxMetrics" ] = arg;
      }
      args[ "fullSelection" ] = true;
      args[ "selectionPadding" ] = [ 2, 4 ];
      args[ "indentionWidth" ] = 16;
      var tree = new org.eclipse.rwt.widgets.Tree( args );
      tree.setTreeColumn( -1 );
      tree.setItemHeight( 20 );
      tree.setLeft( 0 );
      tree.setTop( 0 );
      tree.setWidth( 500 );
      tree.setHeight( 500 );
      tree.setColumnCount( 5 );
      tree.setItemMetrics( 0, 30, 20, 32, 4, 33, 3 );
      tree.setItemMetrics( 1, 60, 21, 63, 5, 64, 4 );
      tree.setItemMetrics( 2, 0, 22, 4, 6, 5, 5 );
      tree.setItemMetrics( 3, 90, 23, 95, 7, 96, 6 );
      tree.setItemMetrics( 4, 90, 23, 95, 7, 96, 6 );
      tree.setItemMetrics( 5, 120, 24, 126, 8, 127, 7 );      
      tree.addToDocument();
      if( !noflush ) {
        testUtil.flush();
      }
      return tree;
    },
    
    _fillTree : function( tree, count, subItems, flatCount ) {
      tree.setItemCount( ( subItems && flatCount ) ? ( count / 2 ) : count );
      var i = 0;
      var itemNr = 0;
      while( i < count ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem(), itemNr );
        itemNr++;
        item.setTexts( [ "Test" + i ] );
        if( subItems ) {
	        item.setItemCount( 1 );
	        var subitem = new org.eclipse.rwt.widgets.TreeItem( item, 0 );
	        if( flatCount ) {
	        	item.setExpanded( true );
	        	i++
		        subitem.setTexts( [ "Test" + i ] );
	        } else {
		        subitem.setTexts( [ "Test" + i + "sub" ] );
	        }
        }
        i++;
      }
    },
    

  }
  
} );