/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.TreeRowTest", {

  extend : qx.core.Object,

  // TODO [tb] : Since TreeRow has been refactored to work without reference to Tree, the
  //             tests could also be refactored to not use the an tree instance anymore.
  members : {

    _gradient : [ [ 0, "red" ], [ 1, "yellow" ] ],

    testCreateRow : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      assertTrue( row.isCreated() );
      assertEquals( 0, row._getTargetNode().childNodes.length );
      tree.destroy();
      row.destroy();
    },

    testRenderItem : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "Test", row._getTargetNode().childNodes[ 1 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testRenderItemWithMarkupEnabled : function() {
      var tree = this._createTree( false, "markupEnabled" );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "<b>Test</b>" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "<b>test</b>", row._getTargetNode().childNodes[ 1 ].innerHTML.toLowerCase() );
      tree.destroy();
      row.destroy();
    },

    testRenderItemWithoutMarkupEnabled : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "<b>Test</b>" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "&lt;b&gt;Test&lt;/b&gt;", row._getTargetNode().childNodes[ 1 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testRenderItemWithMarkupEnabled_Bug374263 : function() {
      var tree = this._createTree( false, "markupEnabled" );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "<b>Test</b>" ] );

      row.renderItem( item, tree._config, false, null );
      row.renderItem( null, tree._config, false, null );
      row.renderItem( item, tree._config, false, null );

      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "<b>test</b>", row._getTargetNode().childNodes[ 1 ].innerHTML.toLowerCase() );
      tree.destroy();
      row.destroy();
    },

    testRenderItemWithMarkupEnabled_Bug377746 : function() {
      var tree = this._createTree( false, "markupEnabled" );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );

      item.setTexts( [ "<b>Test</b>" ] );
      row.renderItem( item, tree._config, false, null );
      item.setTexts( [ "" ] );
      row.renderItem( item, tree._config, false, null );
      item.setTexts( [ "<b>Test</b>" ] );
      row.renderItem( item, tree._config, false, null );

      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "<b>test</b>", row._getTargetNode().childNodes[ 1 ].innerHTML.toLowerCase() );
      tree.destroy();
      row.destroy();
    },


    testRenderEmptyItem : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 1, row._getTargetNode().childNodes.length );
      tree.destroy();
      row.destroy();
    },

    testRenderNoItem : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      row.renderItem( null );
      assertEquals( 0, row._getTargetNode().childNodes.length );
      tree.destroy();
      row.destroy();
    },

    testRenderNoItemAfterContent : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Text" ] );
      item.setCellBackgrounds( [ "red" ] );
      item.setImages( [ "bla.jpg" ] );

      row.renderItem( item, tree._config, false, null );
      row.renderItem( null, tree._config, false, null );

      assertEquals( 4, row._getTargetNode().childNodes.length );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( "none", nodes[ 0 ].style.display );
      assertEquals( "transparent", nodes[ 1 ].style.backgroundColor );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      assertEquals( "", TestUtil.getCssBackgroundImage( nodes[ 2 ] ) );
      assertEquals( "", nodes[ 3 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testRenderItemTwice : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "Test", row._getTargetNode().childNodes[ 1 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testTreeColumnMetrics : function() {
      var tree = new org.eclipse.rwt.widgets.Tree( {
        "appearance": "tree",
        "indentionWidth" : 16
      } );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem() );
      item.setTexts( [ "Test" ] );
      tree.setItemMetrics( 0, 10, 50, 12, 13, 30, 8 );
      var row = this._createRow( tree );
      assertEquals( 10, row._getItemLeft( item, 0, tree._config) );
      assertEquals( 50, row._getItemWidth( item, 0, tree._config ) );
      assertEquals( 28, row._getItemImageLeft( item, 0, tree._config ) );
      assertEquals( 13, row._getItemImageWidth( item, 0, tree._config ) );
      assertEquals( 46, row._getItemTextLeft( item, 0, tree._config ) );
      assertEquals( 8, row._getItemTextWidth( item, 0, tree._config ) );
      tree.destroy();
    },

    testFirstColumnMetricsImageOverflow : function() {
      var tree = new org.eclipse.rwt.widgets.Tree( {
        "appearance": "tree",
        "indentionWidth" : 10
      } );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem() );
      item.setTexts( [ "Test" ] );
      tree.setItemMetrics( 0, 0, 15, 0, 10, 10, 40 );
      var row = this._createRow( tree );
      assertEquals( 5, row._getItemImageWidth( item, 0, tree._config ) );
      assertEquals( 0, row._getItemTextWidth( item, 0, tree._config ) );
      tree.destroy();
    },

    testSecondColumnAsTreeColumn : function() {
      var tree = new org.eclipse.rwt.widgets.Tree( {
        "appearance": "tree",
        "indentionWidth" : 16
      } );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem() );
      var row = this._createRow( tree );
      item.setTexts( [ "Test", "Test2" ] );
      tree.setItemMetrics( 0, 64, 40, 66, 13, 69, 8 );
      tree.setItemMetrics( 1, 34, 40, 36, 13, 49, 8 );
      tree.setTreeColumn( 1 );
      // first column is unchanged:
      assertEquals( 64, row._getItemLeft( item, 0, tree._config ) );
      assertEquals( 40, row._getItemWidth( item, 0, tree._config) );
      assertEquals( 66, row._getItemImageLeft( item, 0, tree._config ) );
      assertEquals( 13, row._getItemImageWidth( item, 0, tree._config ) );
      assertEquals( 69, row._getItemTextLeft( item, 0, tree._config ) );
      assertEquals( 8,  row._getItemTextWidth( item, 0, tree._config ) );
      // second column
      assertEquals( 34, row._getItemLeft( item, 1, tree._config ) );
      assertEquals( 40, row._getItemWidth( item, 1, tree._config ) );
      assertEquals( 52, row._getItemImageLeft( item, 1, tree._config ) );
      assertEquals( 13, row._getItemImageWidth( item, 1, tree._config ) );
      assertEquals( 65, row._getItemTextLeft( item, 1, tree._config ) );
      assertEquals( 8,  row._getItemTextWidth( item, 1, tree._config ) );
      tree.destroy();
    },

    testGetCheckBoxMetrics : function() {
      var tree = new org.eclipse.rwt.widgets.Tree( {
        "appearance": "tree",
        "check": true,
        "checkBoxMetrics": [ 5, 20 ],
        "indentionWidth" : 16
      } );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem() );
      var row = this._createRow( tree );
      assertEquals( 21, row._getCheckBoxLeft( item, tree._config ) );
      assertEquals( 20, row._getCheckBoxWidth( item, tree._config ) );
      tree.destroy();
    },

    testSetCheckBoxMetricsOverflow : function() {
      var tree = new org.eclipse.rwt.widgets.Tree( {
        "appearance": "tree",
        "check" : true,
        "checkBoxMetrics" : [ 5, 20 ],
        "indentionWidth" : 10
      } );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem() );
      var row = this._createRow( tree );
      tree.setItemMetrics( 0, 0, 25, 0, 10, 10, 40 );
      assertEquals( 15, row._getCheckBoxLeft( item, tree._config ) );
      assertEquals( 10, row._getCheckBoxWidth( item, tree._config ) );
      tree.destroy();
    },

    testLabelBounds : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );

      row.renderItem( item, tree._config, false, null );

      var node = row._getTargetNode();
      assertEquals( 2, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bounds = TestUtil.getElementBounds( node.childNodes[ 1 ] );
      assertEquals( 0, bounds.top );
      assertEquals( 21, bounds.left );
      assertEquals( 15, bounds.height );
      assertEquals( 45, bounds.width );
      tree.destroy();
      row.destroy();
    },

    testChangeItemLabelMetricsWithEmptyItemThenScroll : function() {
      var tree = this._createTree( true );
      var row = this._createRow( tree, true );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      var emptyItem = this._createItem( tree );
      tree.setItemMetrics( 0, 4, 66, 24, 10, 5, 41 );

      row.renderItem( emptyItem, tree._config, false, null, false ); // render empty with metrics 1
      row.renderItem( item, tree._config, false, null, true ); // scroll to content, create node
      row.renderItem( emptyItem, tree._config, false, null, true ); // scroll back
      tree.setItemMetrics( 0, 4, 66, 24, 10, 10, 45 ); // change metrics
      row.renderItem( emptyItem, tree._config, false, null, false ); // re-render
      row.renderItem( item, tree._config, false, null, true ); // scroll to content

      var node = row._getTargetNode();
      assertEquals( 1, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 0, bounds.top );
      assertEquals( 10, bounds.left );
      assertEquals( 15, bounds.height );
      assertEquals( 45, bounds.width );
      tree.destroy();
      row.destroy();
    },

    testChangeItemLabelMetricsWithNullItemThenScroll : function() {
      var tree = this._createTree( true );
      var row = this._createRow( tree, true );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      tree.setItemMetrics( 0, 4, 66, 24, 10, 5, 45 );

      row.renderItem( null, tree._config, false, null, false ); // render empty with metrics 1
      row.renderItem( item, tree._config, false, null, true ); // scroll to content, create node
      row.renderItem( null, tree._config, false, null, true ); // scroll back
      tree.setItemMetrics( 0, 4, 66, 24, 10, 10, 41 ); // change metrics
      row.renderItem( null, tree._config, false, null, false ); // re-render
      row.renderItem( item, tree._config, false, null, true ); // scroll to content

      var node = row._getTargetNode();
      assertEquals( 1, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 0, bounds.top );
      assertEquals( 10, bounds.left );
      assertEquals( 15, bounds.height );
      assertEquals( 41, bounds.width );
      tree.destroy();
      row.destroy();
    },

    testChangeItemImageMetricsWithEmptyItemThenScroll : function() {
      var tree = this._createTree( true );
      var row = this._createRow( tree, true );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setImages( [ "url.jpg" ] );
      var emptyItem = this._createItem( tree );
      tree.setItemMetrics( 0, 4, 66, 24, 10, 5, 45 );

      row.renderItem( emptyItem, tree._config, false, null, false ); // render empty with metrics 1
      row.renderItem( item, tree._config, false, null, true ); // scroll to content, create node
      row.renderItem( emptyItem, tree._config, false, null, true ); // scroll back
      tree.setItemMetrics( 0, 4, 66, 18, 20, 5, 45 ); // change metrics
      row.renderItem( emptyItem, tree._config, false, null, false ); // re-render
      row.renderItem( item, tree._config, false, null, true ); // scroll to content

      var node = row._getTargetNode();
      assertEquals( 1, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 18, bounds.left );
      assertEquals( 20, bounds.width );
      tree.destroy();
      row.destroy();
    },

    testChangeItemImageMetricsWithNullItemThenScroll : function() {
      var tree = this._createTree( true );
      var row = this._createRow( tree, true );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setImages( [ "url.jpg" ] );
      tree.setItemMetrics( 0, 4, 66, 24, 10, 5, 45 );

      row.renderItem( null, tree._config, false, null, false ); // render empty with metrics 1
      row.renderItem( item, tree._config, false, null, true ); // scroll to content, create node
      row.renderItem( null, tree._config, false, null, true ); // scroll back
      tree.setItemMetrics( 0, 4, 66, 18, 20, 10, 45 ); // change metrics
      row.renderItem( null, tree._config, false, null, false ); // re-render
      row.renderItem( item, tree._config, false, null, true ); // scroll to content

      var node = row._getTargetNode();
      assertEquals( 1, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 18, bounds.left );
      assertEquals( 20, bounds.width );
      tree.destroy();
      row.destroy();
    },

    testChangeItemCellMetricsWithEmptyItemThenScroll : function() {
      var tree = this._createTree( true );
      var row = this._createRow( tree, true );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setCellBackgrounds( [ "#ffffff" ] );
      var emptyItem = this._createItem( tree );
      tree.setItemMetrics( 0, 4, 66, 24, 10, 5, 45 );

      row.renderItem( emptyItem, tree._config, false, null, false ); // render empty with metrics 1
      row.renderItem( item, tree._config, false, null, true ); // scroll to content, create node
      row.renderItem( emptyItem, tree._config, false, null, true ); // scroll back
      tree.setItemMetrics( 0, 10, 50, 24, 10, 5, 45 ); // change metrics
      row.renderItem( emptyItem, tree._config, false, null, false ); // re-render
      row.renderItem( item, tree._config, false, null, true ); // scroll to content

      var node = row._getTargetNode();
      assertEquals( 1, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 10, bounds.left );
      assertEquals( 50, bounds.width );
      tree.destroy();
      row.destroy();
    },

    testChangeItemCellMetricsWithNullItemThenScroll : function() {
      var tree = this._createTree( true );
      var row = this._createRow( tree, true );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setCellBackgrounds( [ "#ffffff" ] );
      tree.setItemMetrics( 0, 4, 66, 24, 10, 5, 45 );

      row.renderItem( null, tree._config, false, null, false ); // render empty with metrics 1
      row.renderItem( item, tree._config, false, null, true ); // scroll to content, create node
      row.renderItem( null, tree._config, false, null, true ); // scroll back
      tree.setItemMetrics( 0, 10, 50, 24, 10, 10, 45 ); // change metrics
      row.renderItem( null, tree._config, false, null, false ); // re-render
      row.renderItem( item, tree._config, false, null, true ); // scroll to content

      var node = row._getTargetNode();
      assertEquals( 1, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 10, bounds.left );
      assertEquals( 50, bounds.width );
      tree.destroy();
      row.destroy();
    },

    testRenderMultipleLabels : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 3 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 4, row._getTargetNode().childNodes.length );
      assertEquals( "Test1", row._getTargetNode().childNodes[ 1 ].innerHTML );
      assertEquals( "Test2", row._getTargetNode().childNodes[ 2 ].innerHTML );
      assertEquals( "Test3", row._getTargetNode().childNodes[ 3 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testIgnoreItemColumnWidthZero : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 0, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 3 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 3, row._getTargetNode().childNodes.length );
      assertEquals( "Test1", row._getTargetNode().childNodes[ 1 ].innerHTML );
      assertEquals( "Test3", row._getTargetNode().childNodes[ 2 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testIgnoreItemColumnWidthChangedToZero : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 3 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 4, row._getTargetNode().childNodes.length );

      tree.setItemMetrics( 1, 50, 0, 50, 12, 65, 12 );
      row.renderItem( item, tree._config, false, null );

      assertEquals( 3, row._getTargetNode().childNodes.length );
      assertEquals( "Test1", row._getTargetNode().childNodes[ 1 ].innerHTML );
      assertEquals( "Test3", row._getTargetNode().childNodes[ 2 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testIgnoreItemColumnRemoved : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 3 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 4, row._getTargetNode().childNodes.length );

      tree.setColumnCount( 2 );
      row.renderItem( item, tree._config, false, null );

      assertEquals( 3, row._getTargetNode().childNodes.length );
      assertEquals( "Test1", row._getTargetNode().childNodes[ 1 ].innerHTML );
      assertEquals( "Test2", row._getTargetNode().childNodes[ 2 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testLabelDefaultStyle : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( 3, parseInt( node.style.zIndex, 10 ) );
      assertEquals( "absolute", node.style.position );
      assertEquals( "middle", node.style.verticalAlign );
      assertEquals( "nowrap", node.style.whiteSpace );
      assertEquals( "hidden", node.style.overflow );
      if( org.eclipse.rwt.Client.isNewMshtml() ) {
        assertEquals( "rgba(0, 0, 0, 0)", node.style.backgroundColor );
      }
      assertFalse( row.getSelectable() );
      tree.destroy();
      row.destroy();
    },

    testLabelDecoration : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          result.itemBackground = "blue";
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          result.itemForeground = "white";
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          result.textDecoration = "line-through";
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "line-through", node.style.textDecoration );
      tree.destroy();
      row.destroy();
    },

    testRenderNoElementForEmptyText : function() {
      var tree = this._createTree();
      tree._columnCount = 2;
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test", "" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, row._getTargetNode().childNodes.length );
      tree.destroy();
      row.destroy();
    },

    testHideUnneededElements : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );

      item.setTexts( [ "Test1", "Test2" ] );
      row.renderItem( item, tree._config, false, null );
      item.setTexts( [ "Test1", "" ] );
      row.renderItem( item, tree._config, false, null );

      var element = row._getTargetNode().childNodes[ 2 ];
      assertEquals( "", element.innerHTML );
      tree.destroy();
      row.destroy();
    },

    testShowHiddenElements : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );

      item.setTexts( [ "Test1", "Test2" ] );
      row.renderItem( item, tree._config, false, null );
      item.setTexts( [ "Test1", "" ] );
      row.renderItem( item, tree._config, false, null );
      item.setTexts( [ "Test1", "Test2" ] );
      row.renderItem( item, tree._config, false, null );

      var element = row._getTargetNode().childNodes[ 2 ];
      assertEquals( "Test2", element.innerHTML );
      assertEquals( "", element.style.display );
      tree.destroy();
      row.destroy();
    },

    testSingleItemTreeLine : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );

      row.renderItem( item, tree._config, false, null );

      assertEquals( 2, row._getTargetNode().childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var node = row._getTargetNode().childNodes[ 0 ];
      var url = TestUtil.getCssBackgroundImage( node );
      assertTrue( url.indexOf( "single.gif" ) != -1 );
      var position = node.style.backgroundPosition;
      assertTrue(    position.indexOf( "center" ) != -1
                  || position.indexOf( "50%" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testRenderIndentSymbolsForParents : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var parent1 = this._createItem( tree, false, true );
      var parent2 = this._createItem( parent1, false, false );
      var parent3 = this._createItem( parent2, false, true );
      var item = this._createItem( parent3, true, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 4, row._getTargetNode().childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var nodes = row._getTargetNode().childNodes;
      var indentSymbol1 = TestUtil.getCssBackgroundImage( nodes[ 2 ] );
      assertTrue( indentSymbol1.indexOf( "line.gif" ) != -1 );
      var indentSymbol2 = TestUtil.getCssBackgroundImage( nodes[ 1 ] );
      assertTrue( indentSymbol2.indexOf( "line.gif" ) != -1 );
      var urlEnd = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( urlEnd.indexOf( "end.gif" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testIndentSymbolsPosition : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var parent1 = this._createItem( tree, false, true );
      var parent2 = this._createItem( parent1, false, false );
      var parent3 = this._createItem( parent2, false, true );
      var item = this._createItem( parent3, true, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 0, TestUtil.getElementBounds( nodes[ 2 ] ).top );
      assertEquals( 0, TestUtil.getElementBounds( nodes[ 1 ] ).top );
      assertEquals( 0, TestUtil.getElementBounds( nodes[ 0 ] ).top );
      assertEquals( 0, TestUtil.getElementBounds( nodes[ 2 ] ).left );
      assertEquals( 32, TestUtil.getElementBounds( nodes[ 1 ] ).left );
      assertEquals( 48, TestUtil.getElementBounds( nodes[ 0 ] ).left );
      tree.destroy();
      row.destroy();
    },

    testIndentSymbolsNotEnoughSpace : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var parent1 = this._createItem( tree, false, true );
      var parent2 = this._createItem( parent1, false, true );
      var item = this._createItem( parent2, true, false );
      var nodes = row._getTargetNode().childNodes;
      tree.setItemMetrics( 0, 0, 15, 24, 10, 5, 45 );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 0, nodes.length );
      tree.setItemMetrics( 0, 0, 16, 24, 10, 5, 45 );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 1, nodes.length );
      tree.setItemMetrics( 0, 0, 47, 24, 10, 5, 45 );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, nodes.length );
      tree.setItemMetrics( 0, 0, 100, 20, 10, 5, 45 );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 3, nodes.length );
      tree.destroy();
      row.destroy();
    },

    testIndentSymbolsDimension : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      row.setHeight( 15 );
      this._addToDom( row );
      var parent1 = this._createItem( tree, false, true );
      var parent2 = this._createItem( parent1, false, false );
      var parent3 = this._createItem( parent2, false, true );
      var item = this._createItem( parent3, true, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 16, TestUtil.getElementBounds( nodes[ 2 ] ).width );
      assertEquals( 16, TestUtil.getElementBounds( nodes[ 1 ] ).width );
      assertEquals( 16, TestUtil.getElementBounds( nodes[ 0 ] ).width );
      assertEquals( 15, TestUtil.getElementBounds( nodes[ 2 ] ).height );
      assertEquals( 15, TestUtil.getElementBounds( nodes[ 1 ] ).height );
      assertEquals( 15, TestUtil.getElementBounds( nodes[ 0 ] ).height );
      tree.destroy();
      row.destroy();
    },

    testIndentSymbolFirstItemOfLayer : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree, false, true );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var nodes = row._getTargetNode().childNodes;
      var startSymbol = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbol.indexOf( "start.gif" ) != -1 );
      item.setItemCount( 3 );
      new org.eclipse.rwt.widgets.TreeItem( item, 2 );
      row.renderItem( item, tree._config, false, null );
      var startSymbolCollapsed = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolCollapsed.indexOf( "start-collapsed.gif" ) != -1 );
      item.setExpanded( true );
      row.renderItem( item, tree._config, false, null );
      var startSymbolExpanded = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolExpanded.indexOf( "start-expanded.gif" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testIndentSymbolLastItemOfLayer : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree, true, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var nodes = row._getTargetNode().childNodes;
      var startSymbol = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbol.indexOf( "end.gif" ) != -1 );
      item.setItemCount( 3 );
      new org.eclipse.rwt.widgets.TreeItem( item, 2 );
      row.renderItem( item, tree._config, false, null );
      var startSymbolCollapsed = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolCollapsed.indexOf( "end-collapsed.gif" ) != -1 );
      item.setExpanded( true );
      row.renderItem( item, tree._config, false, null );
      var startSymbolExpanded = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolExpanded.indexOf( "end-expanded.gif" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testIndentSymbolIntermediateItemOfLayer : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree, true, true );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var nodes = row._getTargetNode().childNodes;
      var startSymbol = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbol.indexOf( "intermediate.gif" ) != -1 );
      item.setItemCount( 4 );
      new org.eclipse.rwt.widgets.TreeItem( item, 3 );
      row.renderItem( item, tree._config, false, null );
      var startSymbolCollapsed = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolCollapsed.indexOf( "intermediate-collapsed.gif" ) != -1 );
      item.setExpanded( true );
      row.renderItem( item, tree._config, false, null );
      var startSymbolExpanded = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolExpanded.indexOf( "intermediate-expanded.gif" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testIndentSymbolSingleItemOfFirstLayer : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree, false, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var nodes = row._getTargetNode().childNodes;
      var startSymbol = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbol.indexOf( "single.gif" ) != -1 );
      item.setItemCount( 2 );
      new org.eclipse.rwt.widgets.TreeItem( item, 1 );
      row.renderItem( item, tree._config, false, null );
      var startSymbolCollapsed = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolCollapsed.indexOf( "single-collapsed.gif" ) != -1 );
      item.setExpanded( true );
      row.renderItem( item, tree._config, false, null );
      var startSymbolExpanded = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolExpanded.indexOf( "single-expanded.gif" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testIndentSymbolSingleItemOfSecondLayer : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var parent = this._createItem( tree );
      var item = this._createItem( parent );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var nodes = row._getTargetNode().childNodes;
      var startSymbol = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbol.indexOf( "end.gif" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testRenderIndentSymbolsCustomVariant : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.addState( "variant_testVariant" );
      var row = this._createRow( tree );
      this._addToDom( row );
      TestUtil.fakeAppearance( "tree-row-indent",  {
        style : function( states ) {
          var result = null;
          if( states[ "variant_testVariant" ] ) {
            result = "test.gif";
          } else {
            result = "false.gif";
          }
          return { "backgroundImage" : result };
        }
      } );
      var parent = this._createItem( tree, false, true );
      var item = this._createItem( parent, false, false );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      var indentSymbol1 = TestUtil.getCssBackgroundImage( nodes[ 1 ] );
      assertTrue( indentSymbol1.indexOf( "test.gif" ) != -1 );
      var indentSymbol2 = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( indentSymbol2.indexOf( "test.gif" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testRenderItemFont : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setFont( "12px monospace" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      var font = TestUtil.getElementFont( node );
      assertTrue( font.indexOf( "monospace" ) != -1 );
      assertTrue( font.indexOf( "12px" ) != -1 );
      assertEquals( node.style.height, node.style.lineHeight );
      tree.destroy();
      row.destroy();
    },

    testRenderCellFont : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 3 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setFont( "7px Curier New" );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      item.setCellFonts( [ "12px Arial", "14px monospace", "" ] );
      row.renderItem( item, tree._config, false, null );
      var node1 = row._getTargetNode().childNodes[ 1 ];
      var font1 = TestUtil.getElementFont( node1 );
      assertTrue( font1.indexOf( "Arial" ) != -1 );
      assertTrue( font1.indexOf( "12px" ) != -1 );
      var node2 = row._getTargetNode().childNodes[ 2 ];
      var font2 = TestUtil.getElementFont( node2 );
      assertTrue( font2.indexOf( "monospace" ) != -1 );
      assertTrue( font2.indexOf( "14px" ) != -1 );
      var node3 = row._getTargetNode().childNodes[ 3 ];
      var font3 = TestUtil.getElementFont( node3 );
      assertTrue( font3.indexOf( "Curier" ) != -1 );
      assertTrue( font3.indexOf( "7px" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testRenderItemForeground : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setForeground( "red" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node.style.color );
      tree.destroy();
      row.destroy();
    },

    testRenderItemForegroundDisabled : function() {
      var tree = this._createTree();
      tree.setEnabled( false );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setForeground( "red" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "black", node.style.color );
      tree.destroy();
      row.destroy();
    },

    testResetForeground : function( ) {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setForeground( "red" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node.style.color );
      item.setForeground( null );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "black", node.style.color );
      tree.destroy();
      row.destroy();
    },

    testRenderCellForeground : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2" ] );
      item.setCellForegrounds( [ "red", "green" ] );
      row.renderItem( item, tree._config, false, null );
      var node1 = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node1.style.color );
      var node2 = row._getTargetNode().childNodes[ 2 ];
      assertEquals( "green", node2.style.color );
      tree.destroy();
      row.destroy();
    },

    testRenderItemBackground : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setBackground( "red" );

      row.renderItem( item, tree._config, false, null );

      var node = row._getTargetNode();
      assertEquals( "red", node.style.backgroundColor );
      tree.destroy();
      row.destroy();
    },

    testRenderItemBackgroundDisabled : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.setEnabled( false );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setBackground( "red" );
      row.renderItem( item, tree._config, false, null );
      assertNull( TestUtil.getCssBackgroundColor( row ) );
      tree.destroy();
      row.destroy();
    },

    testRenderCellBackground : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test", "Test2" ] );
      item.setCellBackgrounds( [ "red", "green" ] );
      row.renderItem( item, tree._config, false, null );
      var children = row._getTargetNode().childNodes;
      assertEquals( 5, children.length );
      assertEquals( "red", children[ 1 ].style.backgroundColor );
      assertEquals( 1, parseInt( children[ 1 ].style.zIndex, 10 ) );
      assertEquals( "Test", children[ 2 ].innerHTML );
      assertEquals( "green", children[ 3 ].style.backgroundColor );
      assertEquals( "Test2", children[ 4 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testRenderCellBackgroundDisabled : function() {
      var tree = this._createTree();
      tree.setEnabled( false );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test", "Test2" ] );
      item.setCellBackgrounds( [ "red", "green" ] );
      row.renderItem( item, tree._config, false, null );
      var children = row._getTargetNode().childNodes;
      assertEquals( 3, children.length );
      assertEquals( "Test", children[ 1 ].innerHTML );
      assertEquals( "Test2", children[ 2 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testRenderCellBackgroundBounds : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setCellBackgrounds( [ "red" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node.style.backgroundColor );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bounds = TestUtil.getElementBounds( node );
      assertEquals( 4, bounds.left );
      assertEquals( 15, bounds.height );
      assertEquals( 66, bounds.width );
      assertEquals( 0, bounds.top );
      tree.destroy();
      row.destroy();
    },

    testRenderCellBackgroundBoundsWithLinesVisible : function() {
      var tree = this._createTree();
      tree.setLinesVisible( true );
      var row = this._createRow( tree );
      row.setHeight( 15 );
      row.setState( "linesvisible", true );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setCellBackgrounds( [ "red" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node.style.backgroundColor );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bounds = TestUtil.getElementBounds( node );
      assertEquals( 4, bounds.left );
      assertEquals( 14, bounds.height );
      assertEquals( 66, bounds.width );
      assertEquals( 0, bounds.top );
      tree.destroy();
      row.destroy();
    },

    testRenderImagesOnly : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      tree.setColumnCount( 3 );

      item.setTexts( [ "", "", "" ] );
      item.setImages( [ "test1.jpg", null, "test3.jpg" ] );
      row.renderItem( item, tree._config, false, null );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 3, nodes.length );
      assertEquals( 3, parseInt( nodes[ 1 ].style.zIndex, 10 ) );
      var position = nodes[ 1 ].style.backgroundPosition;
      assertTrue(    position.indexOf( "center" ) != -1
                  || position.indexOf( "50%" ) != -1 );
      var url1 = TestUtil.getCssBackgroundImage( nodes[ 1 ] );
      assertTrue( url1.indexOf( "test1.jpg" ) != -1 );
      var url2 = TestUtil.getCssBackgroundImage( nodes[ 2 ] );
      assertTrue( url2.indexOf( "test3.jpg" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testRenderImagesDisabled : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      tree.setColumnCount( 3 );
      item.setTexts( [ "", "", "" ] );
      item.setImages( [ "test1.jpg", null, "test3.jpg" ] );
      row.renderItem( item, tree._config, false, null );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var nodes = row._getTargetNode().childNodes;
      assertFalse( TestUtil.hasElementOpacity( nodes[ 1 ] ) );
      tree.setEnabled( false );
      row.renderItem( item, tree._config, false, null );
      assertTrue( TestUtil.hasElementOpacity( nodes[ 1 ] ) );
      tree.destroy();
      row.destroy();
    },

    testImageBounds : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "" ] );
      item.setImages( [ "bla.jpg" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode();
      assertEquals( 2, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var style = node.childNodes[ 1 ].style;
      assertEquals( 0, parseInt( style.top, 10 ) );
      assertEquals( 40, parseInt( style.left, 10 ) );
      assertEquals( 15, parseInt( style.height, 10 ) );
      assertEquals( 10, parseInt( style.width, 10 ) );
      tree.destroy();
      row.destroy();
    },

    testImageBoundsUpdatedWithoutImage : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "foo" ] );
      item.setImages( [ "bla.jpg" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode();
      assertEquals( 3, node.childNodes.length );
      var left1 = parseInt( node.childNodes[ 1 ].style.left, 10 );
      var subitem = this._createItem( item );
      subitem.setTexts( [ "bar" ] );
      row.renderItem( subitem, tree._config, false, null );
      node = row._getTargetNode();
      assertEquals( 3, node.childNodes.length );
      var left2 = parseInt( node.childNodes[ 1 ].style.left, 10 );
      assertTrue( left2 > left1 );
      tree.destroy();
      row.destroy();
    },

    testImageBoundsScroll : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      row.setHeight( 15 );
      this._addToDom( row );
      var parentItem = this._createItem( tree );
      parentItem.setImages( [ "bla.jpg" ] );
      var item = this._createItem( parentItem );
      item.setTexts( [ "" ] );
      item.setImages( [ "bla.jpg" ] );

      row.renderItem( parentItem, tree._config, false, null );
      row.renderItem( item, tree._config, false, null, true ); // true = dont render bounds

      var node = row._getTargetNode();
      assertEquals( 2, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var style = node.childNodes[ 1 ].style;
      assertEquals( 0, parseInt( style.top, 10 ) );
      assertEquals( 56, parseInt( style.left, 10 ) );
      assertEquals( 15, parseInt( style.height, 10 ) );
      assertEquals( 10, parseInt( style.width, 10 ) );
      tree.destroy();
      row.destroy();
    },

    testCheckboxIndetScroll : function() {
      var tree = this._createTree( false, "check" );
      this._setCheckBox( "mycheckbox.gif" );
      var row = this._createRow( tree );
      row.setHeight( 15 );
      this._addToDom( row );
      var parentItem = this._createItem( tree );
      var item = this._createItem( parentItem );

      row.renderItem( parentItem, tree._config, false, null );
      row.renderItem( item, tree._config, false, null, true ); // true = dont render bounds

      var node = row._getTargetNode();
      assertEquals( 2, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var style = node.childNodes[ 1 ].style;
      assertEquals( 37, parseInt( style.left, 10 ) );
      tree.destroy();
      row.destroy();
    },

    testLabelBoundsUpdatedWithoutLabel : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "foo" ] );
      item.setImages( [ "bla.jpg" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode();
      assertEquals( 3, node.childNodes.length );
      var left1 = parseInt( node.childNodes[ 2 ].style.left, 10 );
      var subitem = this._createItem( item );
      subitem.setImages( [ "bla.jpg" ] );
      row.renderItem( subitem, tree._config, false, null );
      node = row._getTargetNode();
      assertEquals( 3, node.childNodes.length );
      var left2 = parseInt( node.childNodes[ 2 ].style.left, 10 );
      assertTrue( left2 > left1 );
      tree.destroy();
      row.destroy();
    },

    testLabelBoundsScroll : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      row.setHeight( 15 );
      this._addToDom( row );
      var parentItem = this._createItem( tree );
      parentItem.setTexts( [ "foo" ] );
      var item = this._createItem( parentItem );
      item.setTexts( [ "bar" ] );

      row.renderItem( parentItem, tree._config, false, null );
      row.renderItem( item, tree._config, false, null, true ); // true = dont render bounds

      var node = row._getTargetNode();
      assertEquals( 2, node.childNodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var style = node.childNodes[ 1 ].style;
      assertEquals( 0, parseInt( style.top, 10 ) );
      assertEquals( 5 + 2 * 16, parseInt( style.left, 10 ) );
      assertEquals( 15, parseInt( style.height, 10 ) );
      assertEquals( 33, parseInt( style.width, 10 ) );
      tree.destroy();
      row.destroy();
    },

    testRenderImageAndLabelAndBackground : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setImages( [ "bla.jpg" ] );
      item.setCellBackgrounds( [ "green" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 4, nodes.length );
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var background = nodes[ 1 ].style.backgroundColor;
      var image = TestUtil.getCssBackgroundImage( nodes[ 2 ] );
      var text = nodes[ 3 ].innerHTML;
      assertEquals( "green", background );
      assertTrue( image.indexOf( "bla.jpg" ) != -1 );
      assertEquals( "Test", text );
      tree.destroy();
      row.destroy();
    },

    testHideUnusedBackgoundElement : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item1 = this._createItem( tree );
      var item2 = this._createItem( tree );
      item1.setTexts( [ "" ] );
      item1.setCellBackgrounds( [ "green" ] );
      item2.setTexts( [ "Test" ] );
      var nodes = row._getTargetNode().childNodes;

      row.renderItem( item1, tree._config, false, null );
      assertEquals( "green", nodes[ 1 ].style.backgroundColor );
      row.renderItem( item2, tree._config, false, null );

      assertEquals( "transparent", nodes[ 1 ].style.backgroundColor );
      tree.destroy();
      row.destroy();
    },

    testReUseBackgoundElement : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item1 = this._createItem( tree );
      var item2 = this._createItem( tree );
      item1.setTexts( [ "" ] );
      item1.setCellBackgrounds( [ "green" ] );
      item1.setTexts( [ "Test" ] );
      item2.setCellBackgrounds( [ "blue" ] );
      var nodes = row._getTargetNode().childNodes;

      row.renderItem( item1, tree._config, false, null );
      assertEquals( "green", nodes[ 1 ].style.backgroundColor );
      row.renderItem( item2, tree._config, false, null );

      assertEquals( "blue", nodes[ 1 ].style.backgroundColor );
      assertEquals( "", nodes[ 1 ].style.display );
      tree.destroy();
      row.destroy();
    },

    tesHideUnusedImage : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item1 = this._createItem( tree );
      item1.setTexts( [ "" ] );
      item1.setImages( [ "bla.jpg" ] );
      var item2 = this._createItem( tree );
      item2.setTexts( [ "Test" ] );

      row.renderItem( item1, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      var image = TestUtil.getCssBackgroundImage( nodes[ 1 ] );
      assertTrue( image.indexOf( "bla.jpg" ) != -1 );
      row.renderItem( item2, tree._config, false, null );

      assertEquals( "", TestUtil.getCssBackgroundImage( nodes[ 1 ] ) );
      tree.destroy();
      row.destroy();
    },

    tesReUseImage : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item1 = this._createItem( tree );
      item1.setImages( [ "bla.jpg" ] );
      item1.setTexts( [ "Test" ] );
      var item2 = this._createItem( tree );
      item2.setTexts( [ "" ] );
      item2.setImages( [ "bla2.jpg" ] );

      row.renderItem( item1, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertTrue( TestUtil.getCssBackgroundImage( nodes[ 1 ] ).indexOf( "bla.jpg" ) != -1 );
      row.renderItem( item2, tree._config, false, null );

      assertTrue( TestUtil.getCssBackgroundImage( nodes[ 1 ] ).indexOf( "bla2.jpg" ) != -1 );
      assertEquals( "", nodes[ 1 ].style.display );
      tree.destroy();
      row.destroy();
    },

    testReUseHiddenImage : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( true );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item1 = this._createItem( tree );
      item1.setImages( [ "bla.jpg" ] );
      item1.setTexts( [ "Test" ] );
      var item2 = this._createItem( tree );
      item2.setTexts( [ null ] );
      item2.setImages( [ null ] );

      row.renderItem( item1, tree._config, false, null );
      row.renderItem( item2, tree._config, false, null );
      row.renderItem( item1, tree._config, false, null, true );

      var nodes = row._getTargetNode().childNodes;
      assertTrue( TestUtil.getCssBackgroundImage( nodes[ 0 ] ).indexOf( "bla.jpg" ) != -1 );
      assertFalse( TestUtil.hasElementOpacity( nodes[ 0 ] ) );
      tree.destroy();
      row.destroy();
    },

    testHideUnusedLabel : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item1 = this._createItem( tree );
      item1.setTexts( [ "Test" ] );
      var item2 = this._createItem( tree );
      item2.setTexts( [ "" ] );
      item2.setCellBackgrounds( [ "red" ] );

      row.renderItem( item1, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( "Test", nodes[ 1 ].innerHTML );
      row.renderItem( item2, tree._config, false, null );

      assertEquals( "", nodes[ 1 ].innerHTML );
      tree.destroy();
      row.destroy();
    },

    testReUselabel : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item1 = this._createItem( tree );
      item1.setTexts( [ "Test" ] );
      item1.setImages( [ "bla.jpg" ] );
      var item2 = this._createItem( tree );
      item2.setTexts( [ "Test2" ] );
      item2.setCellBackgrounds( [ "red" ] );

      row.renderItem( item1, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( "Test", nodes[ 2 ].innerHTML );
      row.renderItem( item2, tree._config, false, null );

      assertEquals( "Test2", nodes[ 2 ].innerHTML );
      assertEquals( "", nodes[ 2 ].style.display );
      tree.destroy();
      row.destroy();
    },

    testMultipleCellsPosition : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 0, 0, 40, 0, 12, 14, 12 );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test", "Test2" ] );
      item.setImages( [ "bla.jpg", "bla2.jpg" ] );
      item.setCellBackgrounds( [ "green", "blue" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 7, nodes.length );
      assertEquals( 0, parseInt( nodes[ 1 ].style.left, 10 ) );
      assertEquals( 16, parseInt( nodes[ 2 ].style.left, 10 ) );
      assertEquals( 30, parseInt( nodes[ 3 ].style.left, 10 ) );
      assertEquals( 50, parseInt( nodes[ 4 ].style.left, 10 ) );
      assertEquals( 50, parseInt( nodes[ 5 ].style.left, 10 ) );
      assertEquals( 65, parseInt( nodes[ 6 ].style.left, 10 ) );
      tree.destroy();
      row.destroy();
    },

    testMultipleLayersMultipleCellsPosition : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 0, 0, 50, 0, 12, 14, 12 );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var parent1 = this._createItem( tree );
      var parent2 = this._createItem( parent1 );
      var item = this._createItem( parent2 );
      item.setTexts( [ "Test", "Test2" ] );
      item.setImages( [ "bla.jpg", "bla2.jpg" ] );
      item.setCellBackgrounds( [ "green", "blue" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 0, parseInt( nodes[ 1 ].style.left, 10 ) );
      assertEquals( 48, parseInt( nodes[ 2 ].style.left, 10 ) );
      assertEquals( 62, parseInt( nodes[ 3 ].style.left, 10 ) );
      assertEquals( 50, parseInt( nodes[ 4 ].style.left, 10 ) );
      assertEquals( 50, parseInt( nodes[ 5 ].style.left, 10 ) );
      assertEquals( 65, parseInt( nodes[ 6 ].style.left, 10 ) );
      tree.destroy();
      row.destroy();
    },

    testMoreDataThanColumns : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 70, 40, 70, 20, 90, 20 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      tree.setColumnCount( 2 );
      item.setTexts( [ "Test1", "Test2", "Test3", "Test4" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 3, row._getTargetNode().childNodes.length );
      tree.destroy();
      row.destroy();
    },

    testMoreColumnsThanData : function() {
      var tree = this._createTree();
      tree.setItemMetrics( 1, 70, 40, 70, 20, 90, 20 );
      tree.setItemMetrics( 2, 70, 40, 70, 20, 90, 20 );
      tree.setItemMetrics( 3, 70, 40, 70, 20, 90, 20 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      tree.setColumnCount( 4 );
      item.setTexts( [ "Test1", "Test2" ] );
      item.setImages( [ "bla1.jpg" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 4, row._getTargetNode().childNodes.length );
      tree.destroy();
      row.destroy();
    },

    testRenderNoIndentSymbols : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      TestUtil.fakeAppearance( "tree-row-indent", {} );
      var row = this._createRow( tree );
      this._addToDom( row );
      var parent1 = this._createItem( tree, false, true );
      var parent2 = this._createItem( parent1, false, false );
      var parent3 = this._createItem( parent2, false, true );
      var item = this._createItem( parent3, true, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 1, row._getTargetNode().childNodes.length );
      tree.destroy();
      row.destroy();
    },

    testIsExpandClick : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      var row = this._createRow( tree );
      var log = [];
      this._addToDom( row );
      var item = this._createItem( tree, false, false );
      this._createItem( item, false, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      row.addEventListener( "mousedown", function( event ) {
        log.push( row.getTargetIdentifier( event ) === "expandIcon" );
      } );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 0 ] );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 1 ] );
      assertEquals( 2, log.length );
      assertTrue(  log[ 0 ] );
      assertFalse( log[ 1 ] );
      tree.destroy();
      row.destroy();
    },

    testDestroy : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, "check" );
      var row = this._createRow( tree );
      var item = this._createItem( tree, false, false );
      item.setTexts( [ "Test" ] );
      this._addToDom( row );
      row.renderItem( item, tree._config, false, null );
      assertNotNull( row._expandImage );
      var element = row.getElement();
      assertTrue( element.parentNode === document.body );
      row.destroy();
      TestUtil.flush();
      assertTrue( row.isDisposed() );
      assertNull( row.getElement() );
      assertTrue( element.parentNode !== document.body );
      TestUtil.hasNoObjects( row, true );
      tree.destroy();
      row.destroy();
    },

    testRenderCheckBox : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, "check" );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      this._setCheckBox( "mycheckbox.gif" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      var url = TestUtil.getCssBackgroundImage( node );
      assertTrue( url.indexOf( "mycheckbox.gif" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testRenderCheckBoxForTable : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( true, "check" );
      var row = this._createRow( tree, true );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      this._setCheckBox( "mycheckboxtable.gif", true );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 0 ];
      var url = TestUtil.getCssBackgroundImage( node );
      assertTrue( url.indexOf( "mycheckboxtable.gif" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testRenderCheckBoxBounds : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, "check" );
     var row = this._createRow( tree );
      row.setHeight( 15 );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      this._setCheckBox( "mycheckbox.gif" );
      row.renderItem( item, tree._config, false, null );
      var style = row._getTargetNode().childNodes[ 1 ].style;
      assertEquals( 0, parseInt( style.top, 10 ) );
      assertEquals( 15, parseInt( style.height, 10 ) );
      assertEquals( 21, parseInt( style.left, 10 ) );
      assertEquals( 20, parseInt( style.width, 10 ) );
      tree.destroy();
      row.destroy();
    },

    testIsCheckBoxClick : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, "check" );
      var row = this._createRow( tree );
      this._setCheckBox( "mycheckbox.gif" );
      var log = [];
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      row.addEventListener( "mousedown", function( event ) {
        log.push( row.getTargetIdentifier( event ) === "checkBox" );
      } );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 0 ] );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 1 ] );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 2 ] );
      assertEquals( [ false, true, false ], log );
      tree.destroy();
      row.destroy();
    },

    testRenderState : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, false, "fullSelection" );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      tree.blur();
      row.renderItem( item, tree._config, false, null );
      assertFalse( row.hasState( "over" ) );
      assertFalse( row.hasState( "checked" ) );
      assertFalse( row.hasState( "selected" ) );
      assertFalse( row.hasState( "grayed" ) );
      assertTrue( row.hasState( "parent_unfocused" ) );
      item.setChecked( true );
      item.setGrayed( true );
      item.setVariant( "testVariant" );
      tree.focus();
      TestUtil.flush();
      row.renderItem( item, tree._config, true, row.getElement() );
      assertTrue( typeof row._isInGlobalStateQueue == "undefined" );
      assertTrue( row.hasState( "over" ) );
      assertTrue( row.hasState( "checked" ) );
      assertTrue( row.hasState( "selected" ) );
      assertTrue( row.hasState( "grayed" ) );
      assertFalse( row.hasState( "parent_unfocused" ) );
      assertTrue( row.hasState( "testVariant" ) );
      tree.destroy();
      row.destroy();
    },

    testRenderStateWidthDNDSelected : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree(  false, false, "fullSelection" );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      tree.blur();
      row.renderItem( item, tree._config, false, null );
      assertFalse( row.hasState( "selected" ) );
      assertTrue( row.hasState( "parent_unfocused" ) );
      row.setState( "dnd_selected", true );
      TestUtil.flush();
      row.renderItem( item, tree._config, false, null );
      assertTrue( row.hasState( "dnd_selected" ) );
      assertTrue( row.hasState( "selected" ) );
      assertFalse( row.hasState( "parent_unfocused" ) );
      tree.destroy();
      row.destroy();
    },

    testRenderSelectionStateWithHideSelection : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( true, "fullSelection", "hideSelection" );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      tree.blur();
      row.renderItem( item, tree._config, true, null );
      assertTrue( row.hasState( "parent_unfocused" ) );
      assertFalse( row.hasState( "selected" ) );
      tree.destroy();
      row.destroy();
    },

    testRenderSelectionStateWithAlwaysHideSelection : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( true, "fullSelection" );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      tree.setAlwaysHideSelection( true );
      row.renderItem( item, tree._config, true, null );
      assertFalse( row.hasState( "selected" ) );
      tree.destroy();
      row.destroy();
    },

    testRenderThemingItemBackgroundColor : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      assertNull( row.getBackgroundColor() );
      this._setItemBackground( "green" );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "green", row.getBackgroundColor() );
      assertEquals( 2, row._getTargetNode().childNodes.length );
      tree.destroy();
      row.destroy();
    },

    testRenderThemingItemBackgroundGradient : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      assertNull( row.getBackgroundGradient() );
      this._setItemBackgroundGradient( this._gradient );
      row.renderItem( item, tree._config, false, null );
      assertEquals( this._gradient, row.getBackgroundGradient() );
      tree.destroy();
      row.destroy();
    },

    testRenderThemingItemBackgroundImage : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      assertNull( row.getBackgroundImage() );
      this._setItemBackgroundImage( "test.png" );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "test.png", row.getBackgroundImage() );
      tree.destroy();
      row.destroy();
    },

    testRenderItemBackgroundSelected : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "T1", "T2" ] );
      this._setItemBackground( "green" );

      row.renderItem( item, tree._config, true, null );

      assertEquals( "green", row.getBackgroundColor() );
      assertEquals( 4, row._getTargetNode().childNodes.length );
      var selNode = row._getTargetNode().childNodes[ 2 ];
      assertEquals( "", selNode.innerHTML );
      var width = parseInt( selNode.style.width, 10 );
      assertEquals( "green", selNode.style.backgroundColor );
      assertEquals( 2, parseInt( selNode.style.zIndex, 10 ) );
      assertEquals( 18, parseInt( selNode.style.left, 10 ) );
      // Since we dont know the scrollwidth of the node, this is a bit fuzzy:
      assertTrue( width > 10 && width < 45 );
      tree.destroy();
      row.destroy();
    },

    testRenderItemBackgroundSelectedColumnToSmall : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Teeeeeeeeeeeeessssst1", "T2" ] );
      this._setItemBackground( "green" );

      row.renderItem( item, tree._config, true, null );

      assertEquals( "green", row.getBackgroundColor() );
      assertEquals( 4, row._getTargetNode().childNodes.length );
      var selNode = row._getTargetNode().childNodes[ 2 ];
      assertEquals( "", selNode.innerHTML );
      var width = parseInt( selNode.style.width, 10 );
      assertEquals( "green", selNode.style.backgroundColor );
      assertEquals( 2, parseInt( selNode.style.zIndex, 10 ) );
      assertEquals( 18, parseInt( selNode.style.left, 10 ) );
      // Since we dont know the scrollwidth of the node, this is a bit fuzzy:
      assertEquals( 48, width );
      tree.destroy();
      row.destroy();
    },

    testRenderItemBackgroundSelectedBeforeInDOM : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      row._isInGlobalDisplayQueue = true; // prevent add to display queue
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "T1", "T2" ] );
      this._setItemBackground( "green" );
      row.renderItem( item, tree._config, true, null );
      assertEquals( "green", row.getBackgroundColor() );
      assertEquals( 4, row._getTargetNode().childNodes.length );
      var selNode = row._getTargetNode().childNodes[ 2 ];
      var width = parseInt( selNode.style.width, 10 );
      assertEquals( "green", selNode.style.backgroundColor );
      // Since we dont know the scrollwidth of the node, this is a bit fuzzy:
      assertTrue( width > 10 && width < 45 );
      tree.destroy();
      row.destroy();
    },

    testSelectionBackgroundTheming : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.itemBackground = "blue";
          } else {
            result.itemBackground = "#888888";
          }
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "#888888", row.getBackgroundColor() );
      row.renderItem( item, tree._config, true, null );
      assertEquals( "blue", row.getBackgroundColor() );
      row.renderItem( item, tree._config, false, null );
      tree.destroy();
      row.destroy();
    },

    testItemSelectionThemingDoesNotOverwriteCustomColors : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.itemBackground = "blue";
            result.itemForeground = "white";
          } else {
            result.itemBackground = "#888888";
            result.itemForeground = "black";
          }
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      item.setCellBackgrounds( [ "red" ] );
      item.setCellForegrounds( [ "yellow" ] );

      row.renderItem( item, tree._config, true, null );

      var children = row._getTargetNode().childNodes;
      assertEquals( "blue", row.getBackgroundColor() );
      assertEquals( "red", children[ 1 ].style.backgroundColor );
      assertEquals( "yellow", children[ 2 ].style.color );
      tree.destroy();
      row.destroy();
    },

    testItemHoverThemingDoesNotOverwriteCustomColors : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          if( states.over ) {
            result.itemBackground = "blue";
            result.itemForeground = "white";
          } else {
            result.itemBackground = "#888888";
            result.itemForeground = "black";
          }
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      item.setCellBackgrounds( [ "red" ] );
      item.setBackground( "black" );
      item.setCellForegrounds( [ "yellow" ] );
      row.renderItem( item, tree._config, false, null );
      var children = row._getTargetNode().childNodes;
      assertEquals( "black", row.getBackgroundColor() );

      row.renderItem( item, tree._config, row.getElement() );

      assertEquals( "black", row.getBackgroundColor() );
      assertEquals( "red", children[ 1 ].style.backgroundColor );
      assertEquals( "yellow", children[ 2 ].style.color );
      tree.destroy();
      row.destroy();
    },

    testFullSelectionOverlayWithSolidColorIsIgnoredByItemBackground : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          result.itemBackgroundGradient = null; // TODO TEST
          result.itemBackgroundImage = null;  // TODO TEST
          if( states.selected ) {
            result.itemBackground = "yellow";
            result.itemForeground = "yellow";
            result.overlayBackground = "blue";
            result.overlayForeground = "white";
          } else {
            result.itemBackground = "#888888";
            result.itemForeground = "black";
          }
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      item.setCellBackgrounds( [ "red" ] );
      item.setCellForegrounds( [ "yellow" ] );
      row.renderItem( item, tree._config, false, null );
      var children = row._getTargetNode().childNodes;
      assertEquals( "#888888", row.getBackgroundColor() );
      assertEquals( "red", children[ 1 ].style.backgroundColor );
      assertEquals( "yellow", children[ 2 ].style.color );

      row.renderItem( item, tree._config, true, null );

      assertEquals( "yellow", row.getBackgroundColor() );
      assertEquals( "red", children[ 1 ].style.backgroundColor );
      assertEquals( "white", children[ 2 ].style.color );
      tree.destroy();
      row.destroy();
    },

    testFullSelectionOverlayWithGradientOverwritesItemBackground : function() {
      var tree = this._createTree( false, false, "fullSelection" );
      var gradient = this._gradient;
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          if( states.selected ) {
            result.itemBackground = "yellow";
            result.itemForeground = "yellow";
            result.overlayBackgroundGradient = gradient;
            result.overlayBackground = "blue";
            result.overlayForeground = "white";
          } else {
            result.itemBackground = "#888888";
            result.itemForeground = "black";
            result.overlayBackground = "undefined";
            result.overlayForeground = "undefined";
          }
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      item.setCellBackgrounds( [ "red" ] );
      item.setCellForegrounds( [ "yellow" ] );
      row.renderItem( item, tree._config, false, null );
      var children = row._getTargetNode().childNodes;
      assertEquals( "#888888", row.getBackgroundColor() );
      assertEquals( "red", children[ 1 ].style.backgroundColor );
      assertEquals( "yellow", children[ 2 ].style.color );

      row.renderItem( item, tree._config, true, null );

      assertNotNull( row.getBackgroundGradient() );
      children = row._getTargetNode().childNodes; // IE needs this, ALL VERSIONS
      assertEquals( "transparent", children[ 1 ].style.backgroundColor );
      assertEquals( "white", children[ 2 ].style.color );
      assertTrue( this._getOverlayElement( row ) == null );
    },

    testFullSelectionOverlayCreatesElement : function() {
      var tree = this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          return {
            "itemBackground" : "undefined",
            "itemForeground" : "undefined",
            "itemBackgroundImage" : null,
            "itemBackgroundGradient" : null,
            "overlayBackground" : "#ff0000",
            "overlayBackgroundImage" : null,
            "overlayBackgroundGradient" : null
          };
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );

      row.renderItem( item, tree._config, true, null );

      var element = this._getOverlayElement( row );
      assertIdentical( row._getTargetNode(), element.parentNode );
    },

    testFullSelectionOverlayWithAlpha : function() {
      var tree = this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          return {
            "overlayBackground" : "#ff0000",
            "overlayBackgroundAlpha" : 0.4,
            "overlayForeground" : "undefined",
            "overlayBackgroundImage" : null,
            "overlayBackgroundGradient" : null,
            "itemBackground" : "#ff0000",
            "itemForeground" : "undefined",
            "itemBackgroundImage" : null,
            "itemBackgroundGradient" : null
          };
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );

      row.renderItem( item, tree._config, true, null );

      var element = this._getOverlayElement( row );
      assertTrue( TestUtil.hasElementOpacity( element ) );
      if( org.eclipse.rwt.Client.isWebkit() ) {
        assertEquals( 0.4, element.style.opacity );
      }
    },

    testFullSelectionOverlayLayout : function() {
      var tree = this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          return {
            "itemBackground" : "undefined",
            "itemForeground" : "undefined",
            "itemBackgroundImage" : null,
            "itemBackgroundGradient" : null,
            "overlayBackground" : "#ff0000",
            "overlayForeground" : "undefined",
            "overlayBackgroundImage" : null,
            "overlayBackgroundGradient" : null
          };
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );

      row.renderItem( item, tree._config, true, null );

      var bounds = TestUtil.getElementBounds( this._getOverlayElement( row ) );
      assertEquals( 0, bounds.left );
      assertEquals( 0, bounds.top );
      assertEquals( row.getHeight(), bounds.height );
      assertEquals( row.getWidth(), bounds.width );
     },

         testRenderThemingItemForeground : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      row.renderItem( item, tree._config, false, null );
      assertEquals( "black", node.style.color );
      this._setItemForeground( "red" );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "red", node.style.color );
      tree.destroy();
      row.destroy();
    },

    testSelectionForegroundTheming : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {
            "itemBackground" : null,
            "checkBox" : null
          };
          if( states.selected ) {
            result.itemForeground = "white";
          } else {
            result.itemForeground = "black";
          }
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 3, nodes.length );
      assertEquals( "black", nodes[ 1 ].style.color );
      assertEquals( "black", nodes[ 2 ].style.color );
      row.renderItem( item, tree._config, true, null );
      assertEquals( "white", nodes[ 1 ].style.color );
      assertEquals( "black", nodes[ 2 ].style.color );
      tree.destroy();
      row.destroy();
    },

    testSelectionWithItemForeground : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {
            "itemBackground" : null,
            "checkBox" : null
          };
          if( states.selected ) {
            result.itemForeground = "white";
          } else {
            result.itemForeground = "black";
          }
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2" ] );
      item.setCellForegrounds( [ "red", "red" ] );

      row.renderItem( item, tree._config, true, null );

      var nodes = row._getTargetNode().childNodes;
      assertEquals( "red", nodes[ 1 ].style.color );
      assertEquals( "red", nodes[ 2 ].style.color );
      tree.destroy();
      row.destroy();
    },

    testSelectionForegroundThemingFullSelection : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, false, "fullSelection" );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {
            "itemBackground" : null,
            "checkBox" : null
          };
          if( states.selected ) {
            result.itemForeground = "white";
          } else {
            result.itemForeground = "black";
          }
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 3, nodes.length );
      assertEquals( "black", nodes[ 1 ].style.color );
      assertEquals( "black", nodes[ 2 ].style.color );
      row.renderItem( item, tree._config, true, null );
      assertEquals( "white", nodes[ 1 ].style.color );
      assertEquals( "white", nodes[ 2 ].style.color );
      tree.destroy();
      row.destroy();
    },

    tesFullSelecitonWithItemForeground : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, false, "fullSelection" );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {
            "itemBackground" : null,
            "checkBox" : null
          };
          if( states.selected ) {
            result.itemForeground = "white";
          } else {
            result.itemForeground = "black";
          }
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2" ] );
      item.setCellForegrounds( [ "red", "red" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 3, nodes.length );
      assertEquals( "red", nodes[ 1 ].style.color );
      assertEquals( "red", nodes[ 2 ].style.color );
      row.renderItem( item, tree._config, true, null );
      assertEquals( "white", nodes[ 1 ].style.color );
      assertEquals( "white", nodes[ 2 ].style.color );
      tree.destroy();
      row.destroy();
    },

    testIsSelectionClick : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, "check" );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var parent = this._createItem( tree, false, true );
      var item = this._createItem( parent );
      item.setTexts( [ "Test", "Test2" ] );
      item.setImages( [ "bla.jpg" ] );
      this._setCheckBox( "mycheckbox.gif" );
      row.renderItem( item, tree._config, false, null );
      var log = [];
      row.addEventListener( "mousedown", function( event ) {
        log.push( row.getTargetIdentifier( event ) === "treeColumn" );
      } );
      var nodes = row._getTargetNode().childNodes;
      TestUtil.clickDOM( nodes[ 0 ] ); // expandimage
      TestUtil.clickDOM( nodes[ 1 ] ); // treeline
      TestUtil.clickDOM( nodes[ 2 ] ); // checkbox
      TestUtil.clickDOM( nodes[ 3 ] ); // image
      TestUtil.clickDOM( nodes[ 4 ] ); // label
      TestUtil.clickDOM( nodes[ 5 ] ); // label
      TestUtil.clickDOM( row._getTargetNode() );
      assertEquals( [ false, false, false, true, true, false, false  ], log );
      tree.destroy();
      row.destroy();
    },

    testIsSelectionClickFullSelection : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree( false, "check", "fullSelection" );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var parent = this._createItem( tree, false, true  );
      var item = this._createItem( parent );
      item.setTexts( [ "Test", "Test2" ] );
      item.setImages( [ "bla.jpg" ] );
      this._setCheckBox( "mycheckbox.gif" );
      row.renderItem( item, tree._config, false, null );
      var log = [];
      row.addEventListener( "mousedown", function( event ) {
        log.push( row.getTargetIdentifier( event ) !== "checkBox" );
      } );
      var nodes = row._getTargetNode().childNodes;
      TestUtil.clickDOM( nodes[ 0 ] ); // expandimage
      TestUtil.clickDOM( nodes[ 1 ] ); // treeline
      TestUtil.clickDOM( nodes[ 2 ] ); // checkbox
      TestUtil.clickDOM( nodes[ 3 ] ); // image
      TestUtil.clickDOM( nodes[ 4 ] ); // label
      TestUtil.clickDOM( nodes[ 5 ] ); // label
      TestUtil.clickDOM( row._getTargetNode() );
      assertEquals( [ true, true, false, true, true, true, true ], log );
      tree.destroy();
      row.destroy();
    },

    testInheritItemForeground : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.setTextColor( "red" );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node.style.color );
      tree.destroy();
      row.destroy();
    },

    testInheritFont : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      tree.setFont( new qx.ui.core.Font( 12, [ "monospace" ] ) );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      var font = TestUtil.getElementFont( node );
      assertTrue( font.indexOf( "monospace" ) != -1 );
      assertTrue( font.indexOf( "12px" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testRenderNoItemNoThemingBackground: function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._setItemBackground( "blue" );
      this._addToDom( row );
      row.renderItem( null );
      assertNull( row.getBackgroundColor() );
      tree.destroy();
      row.destroy();
    },

    testRenderLabelAlignmentTreeColumn : function() {
      var tree = this._createTree();
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test", "Test" ] );
      tree.setAlignment( 0, "center" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "left", node.style.textAlign );
      tree.setAlignment( 0, "right" );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "left", node.style.textAlign );
      tree.destroy();
      row.destroy();
    },

    testRenderLabelAlignment : function() {
      var tree = this._createTree();
      tree.setTreeColumn( 1 );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = this._createItem( tree );
      item.setTexts( [ "Test", "Test" ] );
      tree.setAlignment( 0, "center" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 0 ];
      assertEquals( "center", node.style.textAlign );
      tree.setAlignment( 0, "right" );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "right", node.style.textAlign );
      tree.destroy();
      row.destroy();
    },

    testSelectionBackgroundLayout : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          if( states.selected ) {
            result.overlayBackground = "blue";
          } else {
            result.itemBackground = "#888888";
          }
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      var selectionPadding = 4;
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 100 );
      row.renderItem( item, tree._config, true, null );
      var rowNode = row._getTargetNode();
      var color = rowNode.childNodes[ 2 ].style.backgroundColor;
      assertEquals( "blue", color );
      var textWidth = row._getVisualTextWidth( item, 0, tree._config );
      //parseInt( rowNode.childNodes[ 1 ].style.width );
      var selectionWidth = parseInt( rowNode.childNodes[ 2 ].style.width, 10 );
      assertEquals( textWidth + selectionPadding, selectionWidth );
      tree.destroy();
      row.destroy();
    },

    testSelectionBackgroundUsesItemColor : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          if( states.selected ) {
            result.itemBackground = "red";
          } else {
            result.itemBackground = "undefined";
          }
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 100 );

      row.renderItem( item, tree._config, true, null );

      var rowNode = row._getTargetNode();
      var color = rowNode.childNodes[ 2 ].style.backgroundColor;
      assertEquals( "red", color );
      tree.destroy();
      row.destroy();
    },

    testSelectionBackgroundRendering_Bug373900 : qx.core.Variant.select( "qx.client", {
      "mshtml|newmshtml" : function() {
        var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
        var tree = this._createTree();
        TestUtil.fakeAppearance( "tree-row", {
          style : function( states ) {
            var result = {};
            if( states.selected ) {
              result.itemBackground = "blue";
            } else {
              result.itemBackground = "#888888";
            }
            result.overlayBackground = "undefined";
            result.overlayBackgroundImage = null;
            result.overlayBackgroundGradient = null;
            result.overlayForeground = "undefined";
            result.itemBackgroundGradient = null;
            result.itemBackgroundImage = null;
            return result;
          }
        } );
        var row = this._createRow( tree );
        this._addToDom( row );
        var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem() );
        item.setTexts( [ "a&b" ] );
        tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 100 );

        row.renderItem( item, tree._config, true, null );

        assertEquals( "a&b", item.getText( 0, false ) );
        tree.destroy();
        row.destroy();
      },
      "default" : function(){}
    } ),

    testSelectionBackgroundLayoutCutOff : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.itemBackground = "blue";
          } else {
            result.itemBackground = "#888888";
          }
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      var selectionPadding = 3; // only the left side
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 25 );
      row.renderItem( item, tree._config, true, null );
      var rowNode = row._getTargetNode();
      var textWidth = parseInt( rowNode.childNodes[ 1 ].style.width, 10 );
      var selectionWidth = parseInt( rowNode.childNodes[ 2 ].style.width, 10 );
      assertEquals( textWidth + selectionPadding, selectionWidth );
      tree.destroy();
      row.destroy();
    },

    testSelectionBackgroundLayoutInvisible : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.itemBackground = "blue";
          } else {
            result.itemBackground = "#888888";
          }
          result.overlayBackground = "undefined";
          result.overlayBackgroundImage = null;
          result.overlayBackgroundGradient = null;
          result.overlayForeground = "undefined";
          result.itemBackgroundGradient = null;
          result.itemBackgroundImage = null;
          return result;
        }
      } );
      var row = this._createRow( tree );
      this._addToDom( row );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 0 );
      row.renderItem( item, tree._config, true, null );
      var rowNode = row._getTargetNode();
      var selectionWidth = parseInt( rowNode.childNodes[ 2 ].style.width, 10 );
      assertEquals( 0, selectionWidth );
      tree.destroy();
      row.destroy();
    },

    testTreeRowFiresItemRenderedEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createTree();
      testUtil.flush();
      var treeRow = this._createRow( tree );
      this._addToDom( treeRow );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      var log = 0;
      treeRow.addEventListener( "itemRendered", function() {
        log++;
      } );

      treeRow.renderItem( item, tree._config, false, null );

      assertTrue( log > 0 );
      tree.destroy();
      treeRow.destroy();
    },


     /////////
     // Helper

     _createRow : function( tree, isTable ) {
       var result = new org.eclipse.rwt.widgets.TreeRow( tree );
       if( isTable ) {
         result.setAppearance( "table-row" );
       } else {
         result.setAppearance( "tree-row" );
       }
       result.setWidth( 400 );
       return result;
     },

    _addToDom : function( widget ) {
      widget.addToDocument();
      org.eclipse.rwt.test.fixture.TestUtil.flush();
    },

    _createTree : function( isTable, option, option2 ) {
      var base = isTable ? "table" : "tree";
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeAppearance( base + "-row",  {
        style : function( states ) {
          return {
            "itemBackground" : "undefined",
            "itemBackgroundGradient" : "undefined",
            "itemBackgroundImage" : "undefined",
            "itemForeground" : "undefined",
            "checkBox" : null,
            "overlayBackground" : "undefined",
            "overlayBackgroundImage" : null,
            "overlayBackgroundGradient" : null,
            "overlayForeground" : "undefined"
          };
        }
      } );
      TestUtil.fakeAppearance( "tree-row-indent",  {
        style : function( states ) {
          var result = null;
          if( states.line ) {
            result = "line.gif";
          } else {
            if( states.first && states.last ) {
              result = "single";
            } else if( states.first ) {
              result = "start";
            } else if( states.last ) {
              result = "end";
            } else {
              result = "intermediate";
            }
            if( states.expanded ) {
              result += "-expanded";
            } else if( states.collapsed ) {
              result += "-collapsed";
            }
          }
          result += ".gif";
          return { "backgroundImage" : result };
        }
      } );
      var args = { "appearance": base };
      args[ "selectionPadding" ] = [ 3, 1 ];
      args[ "indentionWidth" ] = 16;
      args[ option ] = true;
      if( option === "check" ) {
        args[ "checkBoxMetrics" ] = [ 5, 20 ];
      }
      if( option2 ) {
        args[ option2 ] = true;
      }
      var result = new org.eclipse.rwt.widgets.Tree( args );
      result.setTextColor( "black" );
      result.setItemMetrics( 0, 4, 66, 24, 10, 5, 45 );
      result.setItemHeight( 15 );
      result.setColumnCount( 1 );
      if( isTable ) {
        result.setTreeColumn( -1 );
      }
      return result;
    },

    _createItem : function( parent, hasPrevious, hasNext ) {
      var parentItem = org.eclipse.rwt.widgets.TreeItem._getItem( parent );
      var count = 0;
      parent.setItemCount( 1 + hasPrevious ? 1 : 0 + hasNext ? 1 : 0 );
      if( hasPrevious ) {
        new org.eclipse.rwt.widgets.TreeItem( parentItem, count );
        count++;
      }
      var item = new org.eclipse.rwt.widgets.TreeItem( parentItem, count );
      count++;
      if( hasNext ) {
        new org.eclipse.rwt.widgets.TreeItem( parentItem, count );
      }
      return item;
    },

    _setCheckBox : function( value, isTable ) {
      var appearance = "tree-row-check-box";
      if( isTable ) {
        appearance = "table-row-check-box";
      }
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeAppearance( appearance,  {
        style : function( states ) {
          return {
            "backgroundImage" : states.over ? "over.gif" : value
          };
        }
      } );
    },

    _setItemBackground : function( value ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            "itemBackground" : value,
            "itemForeground" : "undefined",
            "itemBackgroundGradient" : null,
            "itemBackgroundImage" : null,
            "checkBox" : null,
            "overlayBackground" : "undefined",
            "overlayBackgroundImage" : null,
            "overlayBackgroundGradient" : null,
            "overlayForeground" : "undefined"
          };
        }
      } );
    },

    _setItemBackgroundGradient : function( value ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            "itemBackgroundGradient" : value,
            "itemBackground" : "transparent",
            "itemBackgroundImage" : null,
            "itemForeground" : "undefined",
            "checkBox" : null,
            "overlayBackground" : "undefined",
            "overlayBackgroundImage" : null,
            "overlayBackgroundGradient" : value,
            "overlayForeground" : "undefined"
          };
        }
      } );
    },

    _setItemBackgroundImage : function( value ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            "itemBackgroundImage" : value,
            "itemBackgroundGradient" : null,
            "itemBackground" : "transparent",
            "itemForeground" : "undefined",
            "checkBox" : null,
            "overlayBackground" : "undefined",
            "overlayBackgroundImage" : value,
            "overlayBackgroundGradient" : null,
            "overlayForeground" : "undefined"
          };
        }
      } );
    },

    _setItemForeground : function( value ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            "itemBackground" : "undefined",
            "itemBackgroundGradient" : "undefined",
            "itemBackgroundImage" : null,
            "itemForeground" : value,
            "checkBox" : null,
            "overlayBackground" : "undefined",
            "overlayBackgroundImage" : null,
            "overlayBackgroundGradient" : null,
            "overlayForeground" : "undefined"
          };
        }
      } );
    },

    _getOverlayElement : function( row ) {
      return row._miscNodes[ 1 ]; // first is always the single.gif
    }

  }

} );
