/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
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

var tree;
var row;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.GridRowTest", {

  extend : rwt.qx.Object,

  // TODO [tb] : Since TreeRow has been refactored to work without reference to Tree, the
  //             tests could also be refactored to not use the an tree instance anymore.
  members : {

    _gradient : [ [ 0, "red" ], [ 1, "yellow" ] ],

    testCreateRow : function() {
      assertTrue( row.isCreated() );
      assertEquals( 0, row._getTargetNode().childNodes.length );
    },

    testRenderItem : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "Test", row._getTargetNode().childNodes[ 1 ].innerHTML );
    },

    testRenderItemWithMarkupEnabled : function() {
      this._createTree( false, "markupEnabled" );
      var item = this._createItem( tree );
      item.setTexts( [ "<b>Test</b>" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "<b>test</b>", row._getTargetNode().childNodes[ 1 ].innerHTML.toLowerCase() );
    },

    testRenderItemWithoutMarkupEnabled : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "<b>Test</b>" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "&lt;b&gt;Test&lt;/b&gt;", row._getTargetNode().childNodes[ 1 ].innerHTML );
    },

    testRenderItemWithMultipleSpacesWhileNotVisible_Bug395822 : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test   Test" ] );
      row.setDisplay( false );

      row.renderItem( item, tree._config, false, null );
      row.setDisplay( true );

      var actual = row._getTargetNode().childNodes[ 1 ].innerHTML;
      assertEquals( "Test&nbsp;&nbsp; Test", actual );
    },

    testRenderItemWithMarkupEnabled_Bug374263 : function() {
      this._createTree( false, "markupEnabled" );
      var item = this._createItem( tree );
      item.setTexts( [ "<b>Test</b>" ] );

      row.renderItem( item, tree._config, false, null );
      row.renderItem( null, tree._config, false, null );
      row.renderItem( item, tree._config, false, null );

      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "<b>test</b>", row._getTargetNode().childNodes[ 1 ].innerHTML.toLowerCase() );
    },

    testRenderItemWithMarkupEnabled_Bug377746 : function() {
      this._createTree( false, "markupEnabled" );
      var item = this._createItem( tree );

      item.setTexts( [ "<b>Test</b>" ] );
      row.renderItem( item, tree._config, false, null );
      item.setTexts( [ "" ] );
      row.renderItem( item, tree._config, false, null );
      item.setTexts( [ "<b>Test</b>" ] );
      row.renderItem( item, tree._config, false, null );

      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "<b>test</b>", row._getTargetNode().childNodes[ 1 ].innerHTML.toLowerCase() );
    },

    testRenderEmptyItem : function() {
      var item = this._createItem( tree );
      item.setTexts( [] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 1, row._getTargetNode().childNodes.length );
    },

    testRenderNoItem : function() {
      row.renderItem( null );
      assertEquals( 0, row._getTargetNode().childNodes.length );
    },

    testRenderNoItemAfterContent : function() {
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
      assertEquals( "", TestUtil.getCssBackgroundImage( nodes[ 2 ] ) );
      assertEquals( "", nodes[ 3 ].innerHTML );
    },

    testRenderItemTwice : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, row._getTargetNode().childNodes.length );
      assertEquals( "Test", row._getTargetNode().childNodes[ 1 ].innerHTML );
    },

    testTreeColumnMetrics : function() {
      tree = new rwt.widgets.Grid( {
        "appearance": "tree",
        "indentionWidth" : 16
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      item.setTexts( [ "Test" ] );
      tree.setItemMetrics( 0, 10, 50, 12, 13, 30, 8 );
      assertEquals( 10, row._getItemLeft( item, 0, tree._config) );
      assertEquals( 50, row._getItemWidth( item, 0, tree._config ) );
      assertEquals( 28, row._getItemImageLeft( item, 0, tree._config ) );
      assertEquals( 13, row._getItemImageWidth( item, 0, tree._config ) );
      assertEquals( 46, row._getItemTextLeft( item, 0, tree._config ) );
      assertEquals( 8, row._getItemTextWidth( item, 0, tree._config ) );
    },

    testFirstColumnMetricsImageOverflow : function() {
      tree = new rwt.widgets.Grid( {
        "appearance": "tree",
        "indentionWidth" : 10
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      item.setTexts( [ "Test" ] );
      tree.setItemMetrics( 0, 0, 15, 0, 10, 10, 40 );
      assertEquals( 5, row._getItemImageWidth( item, 0, tree._config ) );
      assertEquals( 0, row._getItemTextWidth( item, 0, tree._config ) );
    },

    testSecondColumnAsTreeColumn : function() {
      tree = new rwt.widgets.Grid( {
        "appearance": "tree",
        "indentionWidth" : 16
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
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
    },

    testGetCheckBoxMetrics : function() {
      tree = new rwt.widgets.Grid( {
        "appearance": "tree",
        "check": true,
        "checkBoxMetrics": [ 5, 20 ],
        "indentionWidth" : 16
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      assertEquals( 21, row._getCheckBoxLeft( item, tree._config ) );
      assertEquals( 20, row._getCheckBoxWidth( item, tree._config ) );
    },

    testSetCheckBoxMetricsOverflow : function() {
      tree = new rwt.widgets.Grid( {
        "appearance": "tree",
        "check" : true,
        "checkBoxMetrics" : [ 5, 20 ],
        "indentionWidth" : 10
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      tree.setItemMetrics( 0, 0, 25, 0, 10, 10, 40 );
      assertEquals( 15, row._getCheckBoxLeft( item, tree._config ) );
      assertEquals( 10, row._getCheckBoxWidth( item, tree._config ) );
    },

    testRenderHeight : function() {
      tree.setTreeColumn( -1 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setHeight( 30 );

      row.renderItem( item, tree._config, false, null );
      TestUtil.flush();

      var node = row._getTargetNode();
      var bounds = TestUtil.getElementBounds( node );
      assertEquals( 30, bounds.height );
    },

    testLabelBoundsTree : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );

      row.renderItem( item, tree._config, false, null );

      var node = row._getTargetNode();
      assertEquals( 2, node.childNodes.length );
      var bounds = TestUtil.getElementBounds( node.childNodes[ 1 ] );
      assertEquals( 6, bounds.top );
      assertEquals( 21, bounds.left );
      assertEquals( 9, bounds.height );
      assertEquals( 45, bounds.width );
    },

    testLabelBoundsTable : function() {
      tree.setTreeColumn( -1 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );

      row.renderItem( item, tree._config, false, null );

      var node = row._getTargetNode();
      assertEquals( 1, node.childNodes.length );
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 6, bounds.top );
      assertEquals( 5, bounds.left );
      assertEquals( 9, bounds.height );
      assertEquals( 45, bounds.width );
    },

    testRenderLabelBoundsCustomHeight : function() {
      tree.setTreeColumn( -1 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setHeight( 40 );

      row.renderItem( item, tree._config, false, null );

      var node = row._getTargetNode();
      assertEquals( 1, node.childNodes.length );
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 6, bounds.top );
      assertEquals( 34, bounds.height );
    },

    testRenderLabelBoundsAfterCustomHeightItemContentOnly : function() {
      tree.setTreeColumn( -1 ); // important, tree row bounds are always rendered!
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setHeight( 40 );
      var item2 = this._createItem( tree );
      item2.setTexts( [ "Test2" ] );

      row.renderItem( item, tree._config, false, null );
      row.renderItem( item2, tree._config, false, null, true );

      var node = row._getTargetNode();
      assertEquals( 1, node.childNodes.length );
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 6, bounds.top );
      assertEquals( 9, bounds.height );
    },

    testChangeItemLabelMetricsWithEmptyItemThenScroll : function() {
      this._createTree( true );
      this._createRow( tree, true );
      row.setHeight( 15 );
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
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 6, bounds.top );
      assertEquals( 10, bounds.left );
      assertEquals( 9, bounds.height );
      assertEquals( 45, bounds.width );
    },

    testChangeItemLabelMetricsWithNullItemThenScroll : function() {
      this._createTree( true );
      this._createRow( tree, true );
      row.setHeight( 15 );
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
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 6, bounds.top );
      assertEquals( 10, bounds.left );
      assertEquals( 9, bounds.height );
      assertEquals( 41, bounds.width );
    },

    testChangeItemImageMetricsWithEmptyItemThenScroll : function() {
      this._createTree( true );
      this._createRow( tree, true );
      row.setHeight( 15 );
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
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 18, bounds.left );
      assertEquals( 20, bounds.width );
    },

    testChangeItemImageMetricsWithNullItemThenScroll : function() {
      this._createTree( true );
      this._createRow( tree, true );
      row.setHeight( 15 );
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
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 18, bounds.left );
      assertEquals( 20, bounds.width );
    },

    testChangeItemCellMetricsWithEmptyItemThenScroll : function() {
      this._createTree( true );
      this._createRow( tree, true );
      row.setHeight( 15 );
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
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 10, bounds.left );
      assertEquals( 50, bounds.width );
    },

    testChangeItemCellMetricsWithNullItemThenScroll : function() {
      this._createTree( true );
      this._createRow( tree, true );
      row.setHeight( 15 );
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
      var bounds = TestUtil.getElementBounds( node.childNodes[ 0 ] );
      assertEquals( 10, bounds.left );
      assertEquals( 50, bounds.width );
    },

    testRenderMultipleLabels : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 3 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 4, row._getTargetNode().childNodes.length );
      assertEquals( "Test1", row._getTargetNode().childNodes[ 1 ].innerHTML );
      assertEquals( "Test2", row._getTargetNode().childNodes[ 2 ].innerHTML );
      assertEquals( "Test3", row._getTargetNode().childNodes[ 3 ].innerHTML );
    },

    testIgnoreItemColumnWidthZero : function() {
      tree.setItemMetrics( 1, 50, 0, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 3 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 3, row._getTargetNode().childNodes.length );
      assertEquals( "Test1", row._getTargetNode().childNodes[ 1 ].innerHTML );
      assertEquals( "Test3", row._getTargetNode().childNodes[ 2 ].innerHTML );
    },

    testIgnoreItemColumnWidthChangedToZero : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 3 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 4, row._getTargetNode().childNodes.length );

      tree.setItemMetrics( 1, 50, 0, 50, 12, 65, 12 );
      row.renderItem( item, tree._config, false, null );

      assertEquals( 3, row._getTargetNode().childNodes.length );
      assertEquals( "Test1", row._getTargetNode().childNodes[ 1 ].innerHTML );
      assertEquals( "Test3", row._getTargetNode().childNodes[ 2 ].innerHTML );
    },

    testIgnoreItemColumnRemoved : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 3 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 4, row._getTargetNode().childNodes.length );

      tree.setColumnCount( 2 );
      row.renderItem( item, tree._config, false, null );

      assertEquals( 3, row._getTargetNode().childNodes.length );
      assertEquals( "Test1", row._getTargetNode().childNodes[ 1 ].innerHTML );
      assertEquals( "Test2", row._getTargetNode().childNodes[ 2 ].innerHTML );
    },

    testLabelDefaultStyle : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( 3, parseInt( node.style.zIndex, 10 ) );
      assertEquals( "absolute", node.style.position );
      assertEquals( "middle", node.style.verticalAlign );
      assertEquals( "nowrap", node.style.whiteSpace );
      assertEquals( "hidden", node.style.overflow );
      if( rwt.client.Client.isNewMshtml() ) {
        assertEquals( "rgba(0, 0, 0, 0)", node.style.backgroundColor );
      }
      assertFalse( row.getSelectable() );
    },

    testLabelDecoration : function() {
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          result.background = "blue";
          result.backgroundGradient = null;
          result.backgroundImage = null;
          result.foreground = "white";
          result.textDecoration = "line-through";
          return result;
        }
      } );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "line-through", node.style.textDecoration );
    },

    testRenderNoElementForEmptyText : function() {
      tree._columnCount = 2;
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test", "" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 2, row._getTargetNode().childNodes.length );
    },

    testHideUnneededElements : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var item = this._createItem( tree );

      item.setTexts( [ "Test1", "Test2" ] );
      row.renderItem( item, tree._config, false, null );
      item.setTexts( [ "Test1", "" ] );
      row.renderItem( item, tree._config, false, null );

      var element = row._getTargetNode().childNodes[ 2 ];
      assertEquals( "", element.innerHTML );
    },

    testShowHiddenElements : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
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
    },

    testSingleItemTreeLine : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );

      row.renderItem( item, tree._config, false, null );

      assertEquals( 2, row._getTargetNode().childNodes.length );
      var node = row._getTargetNode().childNodes[ 0 ];
      var url = TestUtil.getCssBackgroundImage( node );
      assertTrue( url.indexOf( "single.gif" ) != -1 );
      var position = node.style.backgroundPosition;
      assertTrue(    position.indexOf( "center" ) != -1
                  || position.indexOf( "50%" ) != -1 );
    },

    testRenderIndentSymbolsForParents : function() {
      var parent1 = this._createItem( tree, false, true );
      var parent2 = this._createItem( parent1, false, false );
      var parent3 = this._createItem( parent2, false, true );
      var item = this._createItem( parent3, true, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 4, row._getTargetNode().childNodes.length );
      var nodes = row._getTargetNode().childNodes;
      var indentSymbol1 = TestUtil.getCssBackgroundImage( nodes[ 2 ] );
      assertTrue( indentSymbol1.indexOf( "line.gif" ) != -1 );
      var indentSymbol2 = TestUtil.getCssBackgroundImage( nodes[ 1 ] );
      assertTrue( indentSymbol2.indexOf( "line.gif" ) != -1 );
      var urlEnd = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( urlEnd.indexOf( "end.gif" ) != -1 );
    },

    testIndentSymbolsPosition : function() {
      var parent1 = this._createItem( tree, false, true );
      var parent2 = this._createItem( parent1, false, false );
      var parent3 = this._createItem( parent2, false, true );
      var item = this._createItem( parent3, true, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 0, TestUtil.getElementBounds( nodes[ 2 ] ).top );
      assertEquals( 0, TestUtil.getElementBounds( nodes[ 1 ] ).top );
      assertEquals( 0, TestUtil.getElementBounds( nodes[ 0 ] ).top );
      assertEquals( 0, TestUtil.getElementBounds( nodes[ 2 ] ).left );
      assertEquals( 32, TestUtil.getElementBounds( nodes[ 1 ] ).left );
      assertEquals( 48, TestUtil.getElementBounds( nodes[ 0 ] ).left );
    },

    testIndentSymbolsNotEnoughSpace : function() {
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
    },

    testIndentSymbolsDimension : function() {
      row.setHeight( 15 );
      var parent1 = this._createItem( tree, false, true );
      var parent2 = this._createItem( parent1, false, false );
      var parent3 = this._createItem( parent2, false, true );
      var item = this._createItem( parent3, true, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 16, TestUtil.getElementBounds( nodes[ 2 ] ).width );
      assertEquals( 16, TestUtil.getElementBounds( nodes[ 1 ] ).width );
      assertEquals( 16, TestUtil.getElementBounds( nodes[ 0 ] ).width );
      assertEquals( 15, TestUtil.getElementBounds( nodes[ 2 ] ).height );
      assertEquals( 15, TestUtil.getElementBounds( nodes[ 1 ] ).height );
      assertEquals( 15, TestUtil.getElementBounds( nodes[ 0 ] ).height );
    },

    testIndentSymbolFirstItemOfLayer : function() {
      var item = this._createItem( tree, false, true );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      var startSymbol = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbol.indexOf( "start.gif" ) != -1 );
      item.setItemCount( 3 );
      new rwt.widgets.GridItem( item, 2 );
      row.renderItem( item, tree._config, false, null );
      var startSymbolCollapsed = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolCollapsed.indexOf( "start-collapsed.gif" ) != -1 );
      item.setExpanded( true );
      row.renderItem( item, tree._config, false, null );
      var startSymbolExpanded = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolExpanded.indexOf( "start-expanded.gif" ) != -1 );
    },

    testIndentSymbolLastItemOfLayer : function() {
      var item = this._createItem( tree, true, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      var startSymbol = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbol.indexOf( "end.gif" ) != -1 );
      item.setItemCount( 3 );
      new rwt.widgets.GridItem( item, 2 );
      row.renderItem( item, tree._config, false, null );
      var startSymbolCollapsed = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolCollapsed.indexOf( "end-collapsed.gif" ) != -1 );
      item.setExpanded( true );
      row.renderItem( item, tree._config, false, null );
      var startSymbolExpanded = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolExpanded.indexOf( "end-expanded.gif" ) != -1 );
    },

    testIndentSymbolIntermediateItemOfLayer : function() {
      var item = this._createItem( tree, true, true );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      var startSymbol = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbol.indexOf( "intermediate.gif" ) != -1 );
      item.setItemCount( 4 );
      new rwt.widgets.GridItem( item, 3 );
      row.renderItem( item, tree._config, false, null );
      var startSymbolCollapsed = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolCollapsed.indexOf( "intermediate-collapsed.gif" ) != -1 );
      item.setExpanded( true );
      row.renderItem( item, tree._config, false, null );
      var startSymbolExpanded = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolExpanded.indexOf( "intermediate-expanded.gif" ) != -1 );
    },

    testIndentSymbolSingleItemOfFirstLayer : function() {
      var item = this._createItem( tree, false, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      var startSymbol = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbol.indexOf( "single.gif" ) != -1 );
      item.setItemCount( 2 );
      new rwt.widgets.GridItem( item, 1 );
      row.renderItem( item, tree._config, false, null );
      var startSymbolCollapsed = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolCollapsed.indexOf( "single-collapsed.gif" ) != -1 );
      item.setExpanded( true );
      row.renderItem( item, tree._config, false, null );
      var startSymbolExpanded = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbolExpanded.indexOf( "single-expanded.gif" ) != -1 );
    },

    testIndentSymbolSingleItemOfSecondLayer : function() {
      var parent = this._createItem( tree );
      var item = this._createItem( parent );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      var startSymbol = TestUtil.getCssBackgroundImage( nodes[ 0 ] );
      assertTrue( startSymbol.indexOf( "end.gif" ) != -1 );
    },

    testRenderIndentSymbolsCustomVariant : function() {
      tree.addState( "variant_testVariant" );
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
    },

    testRenderItemFont : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setFont( "12px monospace" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      var font = TestUtil.getElementFont( node );
      assertTrue( font.indexOf( "monospace" ) != -1 );
      assertTrue( font.indexOf( "12px" ) != -1 );
    },

    testRenderCellFont : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 3 );
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
    },

    testRenderItemForeground : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setForeground( "red" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node.style.color );
    },

    testRenderItemForegroundDisabled : function() {
      tree.setEnabled( false );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setForeground( "red" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "black", node.style.color );
    },

    testResetForeground : function( ) {
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setForeground( "red" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node.style.color );
      item.setForeground( null );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "black", node.style.color );
    },

    testRenderCellForeground : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2" ] );
      item.setCellForegrounds( [ "red", "green" ] );
      row.renderItem( item, tree._config, false, null );
      var node1 = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node1.style.color );
      var node2 = row._getTargetNode().childNodes[ 2 ];
      assertEquals( "green", node2.style.color );
    },

    testRenderItemBackground : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setBackground( "red" );

      row.renderItem( item, tree._config, false, null );

      var node = row._getTargetNode();
      assertEquals( "red", node.style.backgroundColor );
    },

    testRenderItemBackgroundDisabled : function() {
      tree.setEnabled( false );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setBackground( "red" );
      row.renderItem( item, tree._config, false, null );
      assertNull( TestUtil.getCssBackgroundColor( row ) );
    },

    testRenderCellBackground : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
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
    },

    testRenderCellBackgroundDisabled : function() {
      tree.setEnabled( false );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test", "Test2" ] );
      item.setCellBackgrounds( [ "red", "green" ] );
      row.renderItem( item, tree._config, false, null );
      var children = row._getTargetNode().childNodes;
      assertEquals( 3, children.length );
      assertEquals( "Test", children[ 1 ].innerHTML );
      assertEquals( "Test2", children[ 2 ].innerHTML );
    },

    testRenderCellBackgroundBounds : function() {
      row.setHeight( 15 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setCellBackgrounds( [ "red" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node.style.backgroundColor );
      var bounds = TestUtil.getElementBounds( node );
      assertEquals( 4, bounds.left );
      assertEquals( 15, bounds.height );
      assertEquals( 66, bounds.width );
      assertEquals( 0, bounds.top );
    },

    testRenderCellBackgroundBoundsWithLinesVisible : function() {
      tree.setLinesVisible( true );
      row.setHeight( 15 );
      row.setState( "linesvisible", true );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setCellBackgrounds( [ "red" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node.style.backgroundColor );
      var bounds = TestUtil.getElementBounds( node );
      assertEquals( 4, bounds.left );
      assertEquals( 14, bounds.height );
      assertEquals( 66, bounds.width );
      assertEquals( 0, bounds.top );
    },

    testRenderImagesOnly : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      var item = this._createItem( tree );
      tree.setColumnCount( 3 );

      item.setTexts( [ "", "", "" ] );
      item.setImages( [ "test1.jpg", null, "test3.jpg" ] );
      row.renderItem( item, tree._config, false, null );
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
    },

    testRenderImagesDisabled : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setItemMetrics( 2, 50, 40, 50, 12, 65, 12 );
      var item = this._createItem( tree );
      tree.setColumnCount( 3 );
      item.setTexts( [ "", "", "" ] );
      item.setImages( [ "test1.jpg", null, "test3.jpg" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertFalse( TestUtil.hasElementOpacity( nodes[ 1 ] ) );
      tree.setEnabled( false );
      row.renderItem( item, tree._config, false, null );
      assertTrue( TestUtil.hasElementOpacity( nodes[ 1 ] ) );
    },

    testImageBounds : function() {
      row.setHeight( 15 );
      var item = this._createItem( tree );
      item.setTexts( [ "" ] );
      item.setImages( [ "bla.jpg" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode();
      assertEquals( 2, node.childNodes.length );
      var style = node.childNodes[ 1 ].style;
      assertEquals( 0, parseInt( style.top, 10 ) );
      assertEquals( 40, parseInt( style.left, 10 ) );
      assertEquals( 15, parseInt( style.height, 10 ) );
      assertEquals( 10, parseInt( style.width, 10 ) );
    },

    testImageBoundsUpdatedWithoutImage : function() {
      row.setHeight( 15 );
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
    },

    testImageBoundsScroll : function() {
      row.setHeight( 15 );
      var parentItem = this._createItem( tree );
      parentItem.setImages( [ "bla.jpg" ] );
      var item = this._createItem( parentItem );
      item.setTexts( [ "" ] );
      item.setImages( [ "bla.jpg" ] );

      row.renderItem( parentItem, tree._config, false, null );
      row.renderItem( item, tree._config, false, null, true ); // true = dont render bounds

      var node = row._getTargetNode();
      assertEquals( 2, node.childNodes.length );
      var style = node.childNodes[ 1 ].style;
      assertEquals( 0, parseInt( style.top, 10 ) );
      assertEquals( 56, parseInt( style.left, 10 ) );
      assertEquals( 15, parseInt( style.height, 10 ) );
      assertEquals( 10, parseInt( style.width, 10 ) );
    },

    testCheckboxIndetScroll : function() {
      this._createTree( false, "check" );
      this._setCheckBox( "mycheckbox.gif" );
      row.setHeight( 15 );
      var parentItem = this._createItem( tree );
      var item = this._createItem( parentItem );

      row.renderItem( parentItem, tree._config, false, null );
      row.renderItem( item, tree._config, false, null, true ); // true = dont render bounds

      var node = row._getTargetNode();
      assertEquals( 2, node.childNodes.length );
      var style = node.childNodes[ 1 ].style;
      assertEquals( 37, parseInt( style.left, 10 ) );
    },

    testLabelBoundsUpdatedWithoutLabel : function() {
      row.setHeight( 15 );
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
    },

    testLabelBoundsScroll : function() {
      row.setHeight( 15 );
      var parentItem = this._createItem( tree );
      parentItem.setTexts( [ "foo" ] );
      var item = this._createItem( parentItem );
      item.setTexts( [ "bar" ] );

      row.renderItem( parentItem, tree._config, false, null );
      row.renderItem( item, tree._config, false, null, true ); // true = dont render bounds

      var node = row._getTargetNode();
      assertEquals( 2, node.childNodes.length );
      var style = node.childNodes[ 1 ].style;
      assertEquals( 6, parseInt( style.top, 10 ) );
      assertEquals( 5 + 2 * 16, parseInt( style.left, 10 ) );
      assertEquals( 9, parseInt( style.height, 10 ) );
      assertEquals( 33, parseInt( style.width, 10 ) );
    },

    testRenderImageAndLabelAndBackground : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      item.setImages( [ "bla.jpg" ] );
      item.setCellBackgrounds( [ "green" ] );
      row.renderItem( item, tree._config, false, null );
      var nodes = row._getTargetNode().childNodes;
      assertEquals( 4, nodes.length );
      var background = nodes[ 1 ].style.backgroundColor;
      var image = TestUtil.getCssBackgroundImage( nodes[ 2 ] );
      var text = nodes[ 3 ].innerHTML;
      assertEquals( "green", background );
      assertTrue( image.indexOf( "bla.jpg" ) != -1 );
      assertEquals( "Test", text );
    },

    testHideUnusedBackgoundElement : function() {
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
    },

    testReUseBackgoundElement : function() {
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
    },

    tesHideUnusedImage : function() {
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
    },

    tesReUseImage : function() {
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
    },

    testReUseHiddenImage : function() {
      this._createTree( true );
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
    },

    testHideUnusedLabel : function() {
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
    },

    testReUselabel : function() {
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
    },

    testMultipleCellsPosition : function() {
      tree.setItemMetrics( 0, 0, 40, 0, 12, 14, 12 );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
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
    },

    testMultipleLayersMultipleCellsPosition : function() {
      tree.setItemMetrics( 0, 0, 50, 0, 12, 14, 12 );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
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
    },

    testMoreDataThanColumns : function() {
      tree.setItemMetrics( 1, 70, 40, 70, 20, 90, 20 );
      var item = this._createItem( tree );
      tree.setColumnCount( 2 );
      item.setTexts( [ "Test1", "Test2", "Test3", "Test4" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 3, row._getTargetNode().childNodes.length );
    },

    testMoreColumnsThanData : function() {
      tree.setItemMetrics( 1, 70, 40, 70, 20, 90, 20 );
      tree.setItemMetrics( 2, 70, 40, 70, 20, 90, 20 );
      tree.setItemMetrics( 3, 70, 40, 70, 20, 90, 20 );
      var item = this._createItem( tree );
      tree.setColumnCount( 4 );
      item.setTexts( [ "Test1", "Test2" ] );
      item.setImages( [ "bla1.jpg" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 4, row._getTargetNode().childNodes.length );
    },

    testRenderNoIndentSymbols : function() {
      TestUtil.fakeAppearance( "tree-row-indent", {} );
      var parent1 = this._createItem( tree, false, true );
      var parent2 = this._createItem( parent1, false, false );
      var parent3 = this._createItem( parent2, false, true );
      var item = this._createItem( parent3, true, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( 1, row._getTargetNode().childNodes.length );
    },

    testIsExpandClick : function() {
      var log = [];
      var item = this._createItem( tree, false, false );
      this._createItem( item, false, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      row.addEventListener( "mousedown", function( event ) {
        log.push( row.getTargetIdentifier( event )[ 0 ] === "expandIcon" );
      } );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 0 ] );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 1 ] );
      assertEquals( 2, log.length );
      assertTrue(  log[ 0 ] );
      assertFalse( log[ 1 ] );
    },

    testDestroy : function() {
      this._createTree( false, "check" );
      var item = this._createItem( tree, false, false );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      assertNotNull( row._expandImage );
      var element = row.getElement();
      assertTrue( element.parentNode === document.body );
      row.destroy();
      TestUtil.flush();
      assertTrue( row.isDisposed() );
      assertNull( row.getElement() );
      assertNull( row._cellImages );
      assertNull( row._cellCheckImages );
      assertNull( row._cellLabels );
      assertNull( row._cellBackgrounds );
      assertTrue( element.parentNode !== document.body );
      TestUtil.hasNoObjects( row, true );
    },

    testRenderCheckBox : function() {
      this._createTree( false, "check" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      this._setCheckBox( "mycheckbox.gif" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      var url = TestUtil.getCssBackgroundImage( node );
      assertTrue( url.indexOf( "mycheckbox.gif" ) != -1 );
    },

    testRenderCellCheckBox : function() {
      tree.setColumnCount( 3 );
      tree.setItemMetrics( 0, 0, 20, 10, 0, 10, 10, 2, 8 );
      tree.setItemMetrics( 1, 20, 20, 30, 0, 30, 10, 22, 8 );
      tree.setItemMetrics( 2, 40, 20, 50, 0, 50, 10, 42, 8 );
      tree.setCellCheck( 0, true );
      tree.setCellCheck( 2, true );
      var item = this._createItem( tree );
      this._setCheckBox( "mycheckbox.gif" );

      row.renderItem( item, tree._config, false, null );

      assertEquals( 3, row._getTargetNode().childNodes.length );
      var node1 = row._getTargetNode().childNodes[ 1 ];
      var node2 = row._getTargetNode().childNodes[ 2 ];
      var url1 = TestUtil.getCssBackgroundImage( node1 );
      var url2 = TestUtil.getCssBackgroundImage( node2 );
      assertTrue( url1.indexOf( "mycheckbox.gif" ) != -1 );
      assertTrue( url2.indexOf( "mycheckbox.gif" ) != -1 );
    },


    testRenderCellCheckBoxChecked : function() {
      tree.setColumnCount( 1 );
      tree.setItemMetrics( 0, 0, 20, 10, 0, 10, 10, 2, 8 );
      tree.setCellCheck( 0, true );
      var item = this._createItem( tree );
      item.setCellChecked( [ true ] );
      this._setCheckBox( "mycheckbox.gif" );

      row.renderItem( item, tree._config, false, null );

      var node = row._getTargetNode().childNodes[ 1 ];
      var url = TestUtil.getCssBackgroundImage( node );
      assertTrue( url.indexOf( "checked-mycheckbox.gif" ) != -1 );
    },

    testRenderCellCheckBoxGrayed : function() {
      tree.setColumnCount( 1 );
      tree.setItemMetrics( 0, 0, 20, 10, 0, 10, 10, 2, 8 );
      tree.setCellCheck( 0, true );
      var item = this._createItem( tree );
      item.setCellGrayed( [ true ] );
      this._setCheckBox( "mycheckbox.gif" );

      row.renderItem( item, tree._config, false, null );

      var node = row._getTargetNode().childNodes[ 1 ];
      var url = TestUtil.getCssBackgroundImage( node );
      assertTrue( url.indexOf( "grayed-mycheckbox.gif" ) != -1 );
    },

    testRenderCellCheckBoxHoveredItem : function() {
      tree.setColumnCount( 1 );
      tree.setItemMetrics( 0, 0, 20, 10, 0, 10, 10, 2, 8 );
      tree.setCellCheck( 0, true );
      var item = this._createItem( tree );
      item.setCellChecked( [ true ] );
      this._setCheckBox( "mycheckbox.gif" );

      row.renderItem( item, tree._config, false, null );
      TestUtil.hoverFromTo( document.body, row._getTargetNode() );
      row.renderItem( item, tree._config, false, [ "other" ] );

      var node = row._getTargetNode().childNodes[ 1 ];
      var url = TestUtil.getCssBackgroundImage( node );
      assertTrue( url.indexOf( "mycheckbox.gif" ) != -1 );
    },

    testRenderCellCheckBoxHoveredBox : function() {
      tree.setColumnCount( 1 );
      tree.setItemMetrics( 0, 0, 20, 10, 0, 10, 10, 2, 8 );
      tree.setCellCheck( 0, true );
      var item = this._createItem( tree );
      item.setCellChecked( [ true ] );
      this._setCheckBox( "mycheckbox.gif" );

      row.renderItem( item, tree._config, false, null );
      TestUtil.hoverFromTo( document.body, row._getTargetNode() );
      row.renderItem( item, tree._config, false, [ "cellCheckBox", 0 ] );

      var node = row._getTargetNode().childNodes[ 1 ];
      var url = TestUtil.getCssBackgroundImage( node );
      assertTrue( url.indexOf( "over.gif" ) != -1 );
    },

    testCellCheckBoxIdentifier : function() {
      tree.setColumnCount( 1 );
      tree.setItemMetrics( 0, 0, 20, 10, 0, 10, 10, 2, 8 );
      tree.setCellCheck( 0, true );
      var item = this._createItem( tree );
      item.setCellChecked( [ true ] );
      this._setCheckBox( "mycheckbox.gif" );
      var log = [];

      row.renderItem( item, tree._config, false, null );
      row.addEventListener( "mousedown", function( event ) {
        log.push( row.getTargetIdentifier( event ) );
      } );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 0 ] );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 1 ] );

      assertEquals( [ "cellCheckBox", 0 ], log[ 1 ] );
    },

    testRenderCellCheckBoxBounds : function() {
      tree.setColumnCount( 3 );
      tree.setItemMetrics( 0, 0, 30, 20, 0, 20, 10, 2, 8 );
      tree.setItemMetrics( 1, 30, 10, 30, 0, 30, 10, -1, -1 );
      tree.setItemMetrics( 2, 40, 20, 50, 0, 50, 10, 42, 8 );
      tree.setCellCheck( 0, true );
      tree.setCellCheck( 2, true );
      var item = this._createItem( tree );
      this._setCheckBox( "mycheckbox.gif" );

      row.renderItem( item, tree._config, false, null );

      assertEquals( 3, row._getTargetNode().childNodes.length );
      var node1 = row._getTargetNode().childNodes[ 1 ];
      var node2 = row._getTargetNode().childNodes[ 2 ];
      var bounds1 = TestUtil.getElementBounds( node1 );
      var bounds2 = TestUtil.getElementBounds( node2 );
      assertEquals( 18, bounds1.left );
      assertEquals( 8, bounds1.width );
      assertEquals( 42, bounds2.left );
      assertEquals( 8, bounds2.width );
    },

    testRenderCellCheckBoxCutOff : function() {
      tree.setColumnCount( 3 );
      tree.setItemMetrics( 0, 0, 20, 10, 0, 10, 10, 2, 8 );
      tree.setCellCheck( 0, true );
      var item = this._createItem( tree );
      this._setCheckBox( "mycheckbox.gif" );

      row.renderItem( item, tree._config, false, null );

      assertEquals( 2, row._getTargetNode().childNodes.length );
      var node = row._getTargetNode().childNodes[ 1 ];
      var bounds = TestUtil.getElementBounds( node );
      assertEquals( 18, bounds.left );
      assertEquals( 2, bounds.width );
    },

    testRenderCellCheckBoxClear : function() {
      tree.setColumnCount( 3 );
      tree.setItemMetrics( 0, 0, 20, 10, 0, 10, 10, 2, 8 );
      tree.setCellCheck( 0, true );
      var item = this._createItem( tree );
      this._setCheckBox( "mycheckbox.gif" );

      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      row.renderItem( null, tree._config, false, null );
      var url = TestUtil.getCssBackgroundImage( node );
      assertEquals( "", url );
    },

    testRenderCellCheckBoxHidden : function() {
      tree.setColumnCount( 3 );
      tree.setItemMetrics( 0, 0, 20, 10, 0, 10, 10, 2, 8 );
      tree.setCellCheck( 0, true );
      var item = this._createItem( tree );
      this._setCheckBox( "mycheckbox.gif" );

      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      tree.setItemMetrics( 0, 0, 0, 10, 0, 10, 10, 2, 8 );
      row.renderItem( item, tree._config, false, null );

      assertTrue( row._getTargetNode() != node.parentNode );
    },

    testRenderCheckBoxForTable : function() {
      this._createTree( true, "check" );
      this._createRow( tree, true );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      this._setCheckBox( "mycheckboxtable.gif", true );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 0 ];
      var url = TestUtil.getCssBackgroundImage( node );
      assertTrue( url.indexOf( "mycheckboxtable.gif" ) != -1 );
    },

    testRenderCheckBoxBounds : function() {
      this._createTree( false, "check" );
      row.setHeight( 15 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      this._setCheckBox( "mycheckbox.gif" );
      row.renderItem( item, tree._config, false, null );
      var style = row._getTargetNode().childNodes[ 1 ].style;
      assertEquals( 0, parseInt( style.top, 10 ) );
      assertEquals( 15, parseInt( style.height, 10 ) );
      assertEquals( 21, parseInt( style.left, 10 ) );
      assertEquals( 20, parseInt( style.width, 10 ) );
    },

    testIsCheckBoxClick : function() {
      this._createTree( false, "check" );
      this._setCheckBox( "mycheckbox.gif" );
      var log = [];
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      row.renderItem( item, tree._config, false, null );
      row.addEventListener( "mousedown", function( event ) {
        log.push( row.getTargetIdentifier( event )[ 0 ] === "checkBox" );
      } );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 0 ] );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 1 ] );
      TestUtil.clickDOM( row._getTargetNode().childNodes[ 2 ] );
      assertEquals( [ false, true, false ], log );
    },

    testRenderState : function() {
      this._createTree( false, false, "fullSelection" );
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
    },

    testRenderStateWidthDNDSelected : function() {
      this._createTree(  false, false, "fullSelection" );
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
    },

    testRenderSelectionStateWithHideSelection : function() {
      this._createTree( true, "fullSelection", "hideSelection" );
      var item = this._createItem( tree );
      tree.blur();
      row.renderItem( item, tree._config, true, null );
      assertTrue( row.hasState( "parent_unfocused" ) );
      assertFalse( row.hasState( "selected" ) );
    },

    testRenderSelectionStateWithAlwaysHideSelection : function() {
      this._createTree( true, "fullSelection" );
      var item = this._createItem( tree );
      tree.setAlwaysHideSelection( true );
      row.renderItem( item, tree._config, true, null );
      assertFalse( row.hasState( "selected" ) );
    },

    testRenderThemingItemBackgroundColor : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      assertNull( row.getBackgroundColor() );
      this._setItemBackground( "green" );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "green", row.getBackgroundColor() );
      assertEquals( 2, row._getTargetNode().childNodes.length );
    },

    testRenderThemingItemBackgroundGradient : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      assertNull( row.getBackgroundGradient() );
      this._setItemBackgroundGradient( this._gradient );
      row.renderItem( item, tree._config, false, null );
      assertEquals( this._gradient, row.getBackgroundGradient() );
    },

    testRenderThemingItemBackgroundImage : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      assertNull( row.getBackgroundImage() );
      this._setItemBackgroundImage( "test.png" );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "test.png", row.getBackgroundImage() );
    },

    testRenderItemBackgroundSelected : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var item = this._createItem( tree );
      item.setTexts( [ "T1", "T2" ] );
      this._setOverlayBackground( "green" );

      row.renderItem( item, tree._config, true, null );

      assertNull( row.getBackgroundColor() );
      assertEquals( 4, row._getTargetNode().childNodes.length );
      var selNode = row._overlayElement;
      assertEquals( "", selNode.innerHTML );
      var width = parseInt( selNode.style.width, 10 );
      assertEquals( "green", selNode.style.backgroundColor );
      assertEquals( 2, parseInt( selNode.style.zIndex, 10 ) );
      assertEquals( 18, parseInt( selNode.style.left, 10 ) );
      // Since we dont know the scrollwidth of the node, this is a bit fuzzy:
      assertTrue( width > 10 && width < 45 );
    },

    testRenderItemBackgroundSelectedColumnToSmall : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var item = this._createItem( tree );
      item.setTexts( [ "Teeeeeeeeeeeeessssst1", "T2" ] );
      this._setOverlayBackground( "green" );

      row.renderItem( item, tree._config, true, null );

      var selNode = row._overlayElement;
      assertEquals( "", selNode.innerHTML );
      var width = parseInt( selNode.style.width, 10 );
      assertEquals( "green", selNode.style.backgroundColor );
      assertEquals( 2, parseInt( selNode.style.zIndex, 10 ) );
      assertEquals( 18, parseInt( selNode.style.left, 10 ) );
      // Since we dont know the scrollwidth of the node, this is a bit fuzzy:
      assertEquals( 48, width );
    },

    testRenderItemBackgroundSelectedBeforeInDOM : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      row._isInGlobalDisplayQueue = true; // prevent add to display queue
      var item = this._createItem( tree );
      item.setTexts( [ "T1", "T2" ] );
      this._setOverlayBackground( "green" );

      row.renderItem( item, tree._config, true, null );

      var selNode = row._overlayElement;
      var width = parseInt( selNode.style.width, 10 );
      assertEquals( "green", selNode.style.backgroundColor );
      // Since we dont know the scrollwidth of the node, this is a bit fuzzy:
      assertTrue( width > 10 && width < 45 );
    },

    testSelectionBackgroundTheming : function() {
      this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.background = "blue";
          } else {
            result.background = "#888888";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "#888888", row.getBackgroundColor() );
      row.renderItem( item, tree._config, true, null );
      assertEquals( "blue", row.getBackgroundColor() );
      row.renderItem( item, tree._config, false, null );
    },

    testItemSelectionThemingDoesNotOverwriteCustomColors : function() {
      this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.background = "blue";
            result.foreground = "white";
          } else {
            result.background = "#888888";
            result.foreground = "black";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
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
    },

    testItemHoverThemingDoesNotOverwriteCustomColors : function() {
      this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          if( states.over ) {
            result.background = "blue";
            result.foreground = "white";
          } else {
            result.background = "#888888";
            result.foreground = "black";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
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
    },

    testFullSelectionOverlayWithSolidColorIsIgnoredByItemBackground : function() {
      this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          result.backgroundGradient = null;
          result.backgroundImage = null;
          if( states.selected ) {
            result.background = "yellow";
            result.foreground = "yellow";
          } else {
            result.background = "#888888";
            result.foreground = "black";
          }
          return result;
        }
      } );
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          var result = {};
          result.backgroundImage = null;
          result.backgroundGradient = null;
          if( states.selected ) {
            result.background = "blue";
            result.foreground = "white";
          } else {
            result.background = "undefined";
            result.foreground = "undefined";
          }
          return result;
        }
      } );
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

      children = row._getTargetNode().childNodes; // IE (quirksmode) needs to get it again
      assertEquals( "yellow", row.getBackgroundColor() );
      assertEquals( "red", children[ 1 ].style.backgroundColor );
      assertEquals( "white", children[ 2 ].style.color );
    },

    testOverlayWithCssGradient : function() {
      if( rwt.client.Client.supportsCss3() ) {
        this._setOverlayGradient( this._gradient );
        row.setAppearance( "tree-row" );
        var item = this._createItem( tree );
        item.setTexts( [ "Test1" ] );

        row.renderItem( item, tree._config, false, null );

        var gradient = TestUtil.getCssGradient( row._overlayElement );
        var expected1 = "gradient(-90deg, red 0%, yellow 100%)";
        var expected2 = "gradient(linear, 0% 0%, 0% 100%, from(red), to(yellow))";
        var expected3 = "gradient(180deg, red 0%, yellow 100%)";
        assertTrue( gradient === expected1 || gradient === expected2 || gradient === expected3 );
      }
    },

    testOverlayWithGfxGradientCreatesCanvas : function() {
      if( !rwt.client.Client.supportsCss3() ) {
        this._setOverlayGradient( this._gradient );
        row.setAppearance( "tree-row" );
        var item = this._createItem( tree );
        item.setTexts( [ "Test1" ] );

        row.renderItem( item, tree._config, false, null );

        assertIdentical( row._overlayElement, row._graphicsOverlay.canvas.node );
      }
    },

    testOverlayWithGfxGradientCreatesShape : function() {
      if( !rwt.client.Client.supportsCss3() ) {
        this._setOverlayGradient( this._gradient );
        row.setAppearance( "tree-row" );
        var item = this._createItem( tree );
        item.setTexts( [ "Test1" ] );

        row.renderItem( item, tree._config, false, null );

        var shape = row._graphicsOverlay.shape;
        assertTrue( shape.type === "svgRoundRect" || shape.node.tagName === "shape" );
      }
    },

    testFullSelectionOverlayWithGradientIgnoredByItemBackground : function() {
      this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          result.backgroundGradient = null;
          result.backgroundImage = null;
          if( states.selected ) {
            result.background = "yellow";
            result.foreground = "yellow";
          } else {
            result.background = "#888888";
            result.foreground = "black";
          }
          return result;
        }
      } );
      var gradient = this._gradient;
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          var result = {};
          result.backgroundImage = null;
          result.backgroundGradient = null;
          if( states.selected ) {
            result.backgroundGradient = gradient;
            result.background = "blue";
            result.foreground = "white";
          } else {
            result.background = "undefined";
            result.foreground = "undefined";
          }
          return result;
        }
      } );
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

      children = row._getTargetNode().childNodes; // IE (quirksmode) needs to get it again
      assertNull( row.getBackgroundGradient() );
      assertEquals( "yellow", row.getBackgroundColor() );
      assertEquals( "red", children[ 1 ].style.backgroundColor );
      assertEquals( "white", children[ 2 ].style.color );
      assertNotNull( row._overlayElement );
    },

    testFullSelectionOverlayCreatesElement : function() {
      this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          return {
            "background" : "#ff0000",
            "foreground" : "undefined",
            "backgroundImage" : null,
            "backgroundGradient" : null
          };
        }
      } );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );

      row.renderItem( item, tree._config, true, null );

      var element = this._getOverlayElement( row );
      assertIdentical( row._getTargetNode(), element.parentNode );
    },

    testFullSelectionOverlayWithAlpha : function() {
      this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          return {
            "background" : "#ff0000",
            "backgroundAlpha" : 0.4,
            "foreground" : "undefined",
            "backgroundImage" : null,
            "backgroundGradient" : null
          };
        }
      } );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );

      row.renderItem( item, tree._config, true, null );

      var element = this._getOverlayElement( row );
      assertTrue( TestUtil.hasElementOpacity( element ) );
      if( rwt.client.Client.isWebkit() ) {
        assertEquals( 0.4, element.style.opacity );
      }
    },

    testFullSelectionOverlayLayout : function() {
      this._createTree( false, false, "fullSelection" );
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          return {
            "background" : "#ff0000",
            "foreground" : "undefined",
            "backgroundImage" : null,
            "backgroundGradient" : null
          };
        }
      } );
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
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      row.renderItem( item, tree._config, false, null );
      assertEquals( "black", node.style.color );
      this._setItemForeground( "red" );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "red", node.style.color );
    },

    testSelectionForegroundTheming : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {
            "background" : null,
            "checkBox" : null
          };
          if( states.selected ) {
            result.foreground = "white";
          } else {
            result.foreground = "black";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
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
    },

    testSelectionWithItemForeground : function() {
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {
            "background" : null,
            "checkBox" : null
          };
          if( states.selected ) {
            result.foreground = "white";
          } else {
            result.foreground = "black";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
      row.setAppearance( "tree-row" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1", "Test2" ] );
      item.setCellForegrounds( [ "red", "red" ] );

      row.renderItem( item, tree._config, true, null );

      var nodes = row._getTargetNode().childNodes;
      assertEquals( "red", nodes[ 1 ].style.color );
      assertEquals( "red", nodes[ 2 ].style.color );
    },

    testSelectionForegroundThemingFullSelection : function() {
      this._createTree( false, false, "fullSelection" );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {
            "background" : null,
            "checkBox" : null
          };
          if( states.selected ) {
            result.foreground = "white";
          } else {
            result.foreground = "black";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
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
    },

    tesFullSelecitonWithItemForeground : function() {
      this._createTree( false, false, "fullSelection" );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {
            "itemBackground" : null,
            "checkBox" : null
          };
          if( states.selected ) {
            result.foreground = "white";
          } else {
            result.foreground = "black";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
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
    },

    testIsSelectionClick : function() {
      this._createTree( false, "check" );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var parent = this._createItem( tree, false, true );
      var item = this._createItem( parent );
      item.setTexts( [ "Test", "Test2" ] );
      item.setImages( [ "bla.jpg" ] );
      this._setCheckBox( "mycheckbox.gif" );
      row.renderItem( item, tree._config, false, null );
      var log = [];
      row.addEventListener( "mousedown", function( event ) {
        log.push( row.getTargetIdentifier( event )[ 0 ] === "treeColumn" );
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
    },

    testIsSelectionClickFullSelection : function() {
      this._createTree( false, "check", "fullSelection" );
      tree.setItemMetrics( 1, 50, 40, 50, 12, 65, 12 );
      tree.setColumnCount( 2 );
      var parent = this._createItem( tree, false, true  );
      var item = this._createItem( parent );
      item.setTexts( [ "Test", "Test2" ] );
      item.setImages( [ "bla.jpg" ] );
      this._setCheckBox( "mycheckbox.gif" );
      row.renderItem( item, tree._config, false, null );
      var log = [];
      row.addEventListener( "mousedown", function( event ) {
        log.push( row.getTargetIdentifier( event )[ 0 ] !== "checkBox" );
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
    },

    testInheritItemForeground : function() {
      tree.setTextColor( "red" );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "red", node.style.color );
    },

    testInheritFont : function() {
      tree.setFont( new rwt.html.Font( 12, [ "monospace" ] ) );
      var item = this._createItem( tree );
      item.setTexts( [ "Test1" ] );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      var font = TestUtil.getElementFont( node );
      assertTrue( font.indexOf( "monospace" ) != -1 );
      assertTrue( font.indexOf( "12px" ) != -1 );
    },

    testRenderNoItemNoThemingBackground: function() {
      this._setItemBackground( "blue" );
      row.renderItem( null );
      assertNull( row.getBackgroundColor() );
    },

    testRenderLabelAlignmentTreeColumn : function() {
      var item = this._createItem( tree );
      item.setTexts( [ "Test", "Test" ] );
      tree.setAlignment( 0, "center" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 1 ];
      assertEquals( "left", node.style.textAlign );
      tree.setAlignment( 0, "right" );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "left", node.style.textAlign );
    },

    testRenderLabelAlignment : function() {
      tree.setTreeColumn( 1 );
      var item = this._createItem( tree );
      item.setTexts( [ "Test", "Test" ] );
      tree.setAlignment( 0, "center" );
      row.renderItem( item, tree._config, false, null );
      var node = row._getTargetNode().childNodes[ 0 ];
      assertEquals( "center", node.style.textAlign );
      tree.setAlignment( 0, "right" );
      row.renderItem( item, tree._config, false, null );
      assertEquals( "right", node.style.textAlign );
    },

    testSelectionBackgroundLayout : function() {
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          result.foreground = "undefined";
          if( states.selected ) {
            result.background = "undefined";
          } else {
            result.background = "#888888";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          var result = {};
          result.background = "undefined";
          result.backgroundImage = null;
          result.backgroundGradient = null;
          result.foreground = "undefined";
          if( states.selected ) {
            result.background = "blue";
          }
          return result;
        }
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      var selectionPadding = 4;
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 100 );

      row.renderItem( item, tree._config, true, null );

      var overlay = this._getOverlayElement( row );
      var color = overlay.style.backgroundColor;
      assertEquals( "blue", color );
      var textWidth = row._getVisualTextWidth( item, 0, tree._config );
      var selectionWidth = parseInt( overlay.style.width, 10 );
      assertEquals( textWidth + selectionPadding, selectionWidth );
    },

    testSelectionBackgroundUsesItemColor : function() {
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.background = "red";
          } else {
            result.background = "undefined";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 100 );

      row.renderItem( item, tree._config, true, null );

      var color = this._getOverlayElement( row ).style.backgroundColor;
      assertEquals( "red", color );
    },

    testSelectionBackgroundHidesOverlayAfterDeselection : function() {
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.background = "red";
          } else {
            result.background = "undefined";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 100 );

      row.renderItem( item, tree._config, true, null );
      row.renderItem( item, tree._config, false, null );

      assertEquals( "none", this._getOverlayElement( row ).style.display );
    },

    testSelectionBackgroundHidesOverlayOnEmptyRow : function() {
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.background = "red";
          } else {
            result.background = "undefined";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 100 );

      row.renderItem( item, tree._config, true, null );
      row.renderItem( null, tree._config, false, null );

      assertEquals( "none", this._getOverlayElement( row ).style.display );
    },

    testSelectionBackgroundShowsOverlayOnSecondSelection : function() {
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.background = "red";
          } else {
            result.background = "undefined";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 100 );

      row.renderItem( item, tree._config, true, null );
      row.renderItem( item, tree._config, false, null );
      row.renderItem( item, tree._config, true, null );

      assertEquals( "", this._getOverlayElement( row ).style.display );
    },

    testSelectionBackgroundRendering_Bug373900 : rwt.util.Variant.select( "qx.client", {
      "mshtml|newmshtml" : function() {
        TestUtil.fakeAppearance( "tree-row", {
          style : function( states ) {
            var result = {};
            if( states.selected ) {
              result.background = "blue";
            } else {
              result.background = "#888888";
            }
            result.backgroundGradient = null;
            result.backgroundImage = null;
            return result;
          }
        } );
        var item = new rwt.widgets.GridItem( tree.getRootItem() );
        item.setTexts( [ "a&b" ] );
        tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 100 );

        row.renderItem( item, tree._config, true, null );

        assertEquals( "a&b", item.getText( 0, false ) );
      },
      "default" : function(){}
    } ),

    testSelectionBackgroundLayoutCutOff : function() {
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.background = "blue";
          } else {
            result.background = "#888888";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      var selectionPadding = 3; // only the left side
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 25 );

      row.renderItem( item, tree._config, true, null );

      var rowNode = row._getTargetNode();
      var textWidth = parseInt( rowNode.childNodes[ 1 ].style.width, 10 );
      var selectionWidth = parseInt( this._getOverlayElement( row ).style.width, 10 );
      assertEquals( textWidth + selectionPadding, selectionWidth );
    },

    testSelectionBackgroundLayoutInvisible : function() {
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.background = "blue";
          } else {
            result.background = "#888888";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 0 );

      row.renderItem( item, tree._config, true, null );

      var rowNode = row._getTargetNode();
      var selectionWidth = parseInt( this._getOverlayElement( row ).style.width, 10 );
      assertEquals( 0, selectionWidth );
    },

    testSelectionBackgroundHover : function() {
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          var result = {};
          result.background = "undefined";
          result.backgroundImage = null;
          result.backgroundGradient = null;
          result.foreground = "undefined";
          if( states.selected ) {
            result.background = "blue";
          } else if( states.over) {
            result.background = "green";
          }
          return result;
        }
      } );
      var item = new rwt.widgets.GridItem( tree.getRootItem() );
      item.setTexts( [ "Test1" ] );
      var selectionPadding = 4;
      tree.setItemMetrics( 0, 0, 100, 0, 0 ,0, 100 );

      row.renderItem( item, tree._config, false, null );
      row.renderItem( item, tree._config, false, [ "treeColumn" ] );

      var overlay = this._getOverlayElement( row );
      var color = overlay.style.backgroundColor;
      assertEquals( "green", color );
    },

    testTreeRowFiresItemRenderedEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.flush();
      var item = this._createItem( tree );
      item.setTexts( [ "Test" ] );
      var log = 0;
      row.addEventListener( "itemRendered", function() {
        log++;
      } );

      row.renderItem( item, tree._config, false, null );

      assertTrue( log > 0 );
    },


     /////////
     // Helper

     _createRow : function( tree, isTable ) {
       if( row != null ) {
       }
       var result = new rwt.widgets.base.GridRow( tree );
       if( isTable ) {
         result.setAppearance( "table-row" );
       } else {
         result.setAppearance( "tree-row" );
       }
       row = result;
       row.setWidth( 400 );
       this._addToDom( row );
     },

    _addToDom : function( widget ) {
      widget.addToDocument();
      org.eclipse.rwt.test.fixture.TestUtil.flush();
    },

    _createTree : function( isTable, option, option2 ) {
      var base = isTable ? "table" : "tree";
      TestUtil.fakeAppearance( base + "-row",  {
        style : function( states ) {
          return {
            "background" : "undefined",
            "backgroundGradient" : "undefined",
            "backgroundImage" : "undefined",
            "foreground" : "undefined",
            "checkBox" : null
          };
        }
      } );
      TestUtil.fakeAppearance( base + "-row-overlay ",  {
        style : function( states ) {
          return {
            "background" : "undefined",
            "backgroundImage" : null,
            "backgroundGradient" : null,
            "foreground" : "undefined"
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
      var result = new rwt.widgets.Grid( args );
      result.setTextColor( "black" );
      result.setItemMetrics( 0, 4, 66, 24, 10, 5, 45 );
      result.setItemHeight( 15 );
      result.setColumnCount( 1 );
      if( isTable ) {
        result.setTreeColumn( -1 );
      }
      tree = result;
    },

    _createItem : function( parent, hasPrevious, hasNext ) {
      var parentItem = rwt.widgets.GridItem._getItem( parent );
      var count = 0;
      parent.setItemCount( 1 + hasPrevious ? 1 : 0 + hasNext ? 1 : 0 );
      if( hasPrevious ) {
        new rwt.widgets.GridItem( parentItem, count );
        count++;
      }
      var item = new rwt.widgets.GridItem( parentItem, count );
      count++;
      if( hasNext ) {
        new rwt.widgets.GridItem( parentItem, count );
      }
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.GridItem" );
      rwt.remote.ObjectRegistry.add( "w" + item.toHashCode(), item, handler );
      return item;
    },

    _setCheckBox : function( value, isTable ) {
      var appearance = "tree-row-check-box";
      if( isTable ) {
        appearance = "table-row-check-box";
      }
      TestUtil.fakeAppearance( appearance,  {
        style : function( states ) {
          var pre = states.checked ? "checked-" : "";
          pre += states.grayed ? "grayed-" : "";
          return {
            "backgroundImage" : pre + ( states.over ? "over.gif" : value )
          };
        }
      } );
    },

    _setItemBackground : function( value ) {
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            "background" : value,
            "foreground" : "undefined",
            "backgroundGradient" : null,
            "backgroundImage" : null,
            "checkBox" : null
          };
        }
      } );
    },

    _setOverlayBackground : function( value ) {
      TestUtil.fakeAppearance( "tree-row-overlay",  {
        style : function( states ) {
          return {
            "background" : value,
            "foreground" : "undefined",
            "backgroundGradient" : null,
            "backgroundImage" : null,
            "checkBox" : null
          };
        }
      } );
    },

    _setOverlayGradient : function( value ) {
      TestUtil.fakeAppearance( "tree-row-overlay",  {
        style : function( states ) {
          return {
            "background" : "undefined",
            "foreground" : "undefined",
            "backgroundGradient" : value,
            "backgroundImage" : null,
            "checkBox" : null
          };
        }
      } );
    },

    _setItemBackgroundGradient : function( value ) {
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            "backgroundGradient" : value,
            "background" : "transparent",
            "backgroundImage" : null,
            "foreground" : "undefined",
            "checkBox" : null
          };
        }
      } );
      TestUtil.fakeAppearance( "tree-row-overlay",  {
        style : function( states ) {
          return {
            "backgroundGradient" : value,
            "background" : "transparent",
            "backgroundImage" : null,
            "foreground" : "undefined"
          };
        }
      } );
    },

    _setItemBackgroundImage : function( value ) {
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            "backgroundImage" : value,
            "backgroundGradient" : null,
            "background" : "transparent",
            "foreground" : "undefined",
            "checkBox" : null
          };
        }
      } );
      TestUtil.fakeAppearance( "tree-row-overlay",  {
        style : function( states ) {
          return {
            "backgroundImage" : value,
            "backgroundGradient" : null,
            "background" : "transparent",
            "foreground" : "undefined"
          };
        }
      } );
    },

    _setItemForeground : function( value ) {
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            "background" : "undefined",
            "backgroundGradient" : "undefined",
            "backgroundImage" : null,
            "foreground" : value,
            "checkBox" : null
          };
        }
      } );
    },

    _getOverlayElement : function( row ) {
      return row._overlayElement;
    },

    setUp : function() {
      this._createTree();
      this._createRow();
    },

    tearDown : function() {
      tree.destroy();
      tree = null;
      row.destroy();
      row = null;
    }

  }

} );

}());
