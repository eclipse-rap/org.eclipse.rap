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

var register = function( widget ) {
  var handler = rwt.remote.HandlerRegistry.getHandler( widget.classname );
  rwt.remote.ObjectRegistry.add( "w" + widget.toHashCode(), widget, handler );
};

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.GridRowContainerTest", {

  extend : rwt.qx.Object,

  // NOTE : Many of these tests use the Tree as an intermediate layer for tests.
  //        This is for historical reasons and should be adapted step by step.
  members : {

    testCreate : function() {
      var cont = this._createContainer();
      assertTrue( cont instanceof rwt.widgets.base.GridRowContainer );
      cont.destroy();
    },

    testCreateTreeRowsWithAppearance : function() {
      var cont = this._createContainer();
      cont.setBaseAppearance( "table" );
      cont.setRowHeight( 50 );
      cont.setHeight( 501 );
      assertEquals( 11, getRows( cont ).length );
      assertEquals( "table-row", getRows( cont )[ 0 ].getAppearance() );
    },

    testAddTreeRowsOnTreeResize : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setHeight( 551 );
      var clientArea = tree._rowContainer;
      assertEquals( 12, getRows( clientArea ).length );
      tree.destroy();
    },

    testAddOneAdditionalRow : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      var clientArea = tree._rowContainer;
      tree.setHeight( 499 );
      assertEquals( 10, getRows( clientArea ).length );
      tree.setHeight( 500 );
      assertEquals( 11, getRows( clientArea ).length );
      tree.setHeight( 501 );
      assertEquals( 11, getRows( clientArea ).length );
      tree.destroy();
    },

    testAddTreeRowsOnRowResize : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setItemHeight( 25 );
      var clientArea = tree._rowContainer;
      assertEquals( 21, getRows( clientArea ).length );
      tree.destroy();
    },

    testRemoveTreeRowsOnTreeResize : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setHeight( 401 );
      var clientArea = tree._rowContainer;
      assertEquals( 9, getRows( clientArea ).length );
      tree.destroy();
    },

    testRemoveTreeRowsOnRowResize : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setItemHeight( 100 );
      var clientArea = tree._rowContainer;
      assertEquals( 6, getRows( clientArea ).length );
      tree.destroy();
    },

    testTreeRowBounds : function() {
      var tree = this._createDefaultTree();
      var sample = tree._rowContainer.$rows.prop( "childNodes" )[ 10 ];
      var bounds = getElementBounds( sample );
      assertEquals( 0, bounds.left );
      assertEquals( 200, bounds.top );
      assertEquals( 500, bounds.width );
      assertEquals( 20, bounds.height );
      tree.destroy();
    },

    testRowOffsetsAfterOptimizedScrollingForward : function() {
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( false, true );
      tree.setItemCount( 100 );
      var items = [];
      for( var i = 0; i < 100; i++ ) {
        items[ i ] = new rwt.widgets.GridItem( tree.getRootItem(), i );
        items[ i ].setTexts( [ "Test" + i ] );
      }
      TestUtil.flush();

      // use small offset and scrollbar directly to trigger optimized scrolling:
      tree._vertScrollBar.setValue( 1 );
      TestUtil.flush();

      var container = tree.getRowContainer();
      var rowCount = container.getRowCount();
      var sampleTop = container.findRowByItem( items[ 1 ] ).$el.get( 0 );
      var sampleMiddle = container.findRowByItem( items[ 6 ] ).$el.get( 0 );
      var sampleBottom = container.findRowByItem( items[ rowCount ] ).$el.get( 0 );

      assertEquals( 0, getElementBounds( sampleTop ).top );
      assertContains( "Test1", sampleTop.innerHTML );
      assertEquals( 100, getElementBounds( sampleMiddle ).top );
      assertContains( "Test6", sampleMiddle.innerHTML );
      assertEquals( 20 * ( rowCount - 1 ), getElementBounds( sampleBottom ).top );
      assertContains( "Test" + rowCount, sampleBottom.innerHTML );
      tree.destroy();
    },

    testRowOffsetsAfterOptimizedScrollingBackwards : function() {
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( false, true );
      tree.setItemCount( 100 );
      var items = [];
      for( var i = 0; i < 100; i++ ) {
        items[ i ] = new rwt.widgets.GridItem( tree.getRootItem(), i );
        items[ i ].setTexts( [ "Test" + i ] );
      }
      tree.setTopItemIndex( 2 );
      TestUtil.flush();

      tree._vertScrollBar.setValue( 1 );
      TestUtil.flush();

      var container = tree.getRowContainer();
      var rowCount = container.getRowCount();
      var sampleTop = container.findRowByItem( items[ 1 ] ).$el.get( 0 );
      var sampleMiddle = container.findRowByItem( items[ 6 ] ).$el.get( 0 );
      var sampleBottom = container.findRowByItem( items[ rowCount ] ).$el.get( 0 );

      assertEquals( 0, getElementBounds( sampleTop ).top );
      assertContains( "Test1", sampleTop.innerHTML );
      assertEquals( 100, getElementBounds( sampleMiddle ).top );
      assertContains( "Test6", sampleMiddle.innerHTML );
      assertEquals( 20 * ( rowCount - 1 ), getElementBounds( sampleBottom ).top );
      assertContains( "Test" + rowCount, sampleBottom.innerHTML );
      tree.destroy();
    },

    testRenderEmptyRowOnHoverBug : function() {
      // See Bug 349310 - Mouseover on invisible items in SWT Tree
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      var root = new rwt.widgets.GridItem( tree.getRootItem(), 0 );
      register( root );
      root.setItemCount( 10 );
      for( var i = 0; i < 10; i++ ) {
        var item = new rwt.widgets.GridItem( root, i );
        item.setTexts( [ "item" + i ] );
        register( item );
      }
      root.setExpanded( true );
      TestUtil.flush();
      var node = tree._rowContainer.getRow( 2 ).$el.get( 0 );
      assertEquals( "item1", node.firstChild.innerHTML );
      root.setExpanded( false );
      TestUtil.flush();
      assertEquals( "", node.firstChild.innerHTML );
      TestUtil.mouseOver( tree._rowContainer.getRow( 2 ).$el );

      assertEquals( "", node.firstChild.innerHTML );
      tree.destroy();
    },

    testRenderEmptyRowsBackground : function() {
      var tree = this._createDefaultTree();
      var grad1 = [ [ 0, "red" ], [ 1, "yellow" ] ];
      var grad2 = [ [ 0, "yellow" ], [ 1, "red" ] ];
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          return {
            "background" : states.selected ? "red" : "blue",
            "backgroundGradient" : states.selected ?  grad1 : grad2,
            "backgroundImage" : states.selected ? "foo.jpg" : "bar.jpg",
            "foreground" : "undefined"
          };
        }
      } );
      TestUtil.fakeAppearance( "tree-row-overlay", {
        style : function( states ) {
          return {
            "background" : states.selected ? "red" : "blue",
            "backgroundGradient" : states.selected ?  grad1 : grad2,
            "backgroundImage" : states.selected ? "foo.jpg" : "bar.jpg",
            "foreground" : "undefined"
          };
        }
      } );
      tree.setItemCount( 1 );
      var root = new rwt.widgets.GridItem( tree.getRootItem(), 0 );
      register( root );
      root.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( root, 0 );
      register( item );
      root.setExpanded( true );
      tree.selectItem( item );
      TestUtil.flush();
      var row = tree._rowContainer.getRow( 1 );
      assertEquals( "red", row.$el.css( "backgroundColor" ) );
      assertEquals( "url(foo.jpg)", row.$el.css( "backgroundImage" ) );
      assertIdentical( "linear-gradient( to bottom, red 0%, yellow 100% )", row.$el.css( "backgroundGradient" ) );
      root.setExpanded( false );
      TestUtil.flush();
      assertEquals( "transparent", row.$el.css( "backgroundColor" ) );
      assertEquals( "none", row.$el.css( "backgroundImage" ) );
      assertIdentical( undefined, row.$el.css( "backgroundGradient" ) );
      tree.destroy();
    },

    testChangeTreeRowBounds : function() {
      var tree = this._createDefaultTree();
      var sample = tree._rowContainer.$rows.prop( "childNodes" )[ 10 ];
      tree.setWidth( 400 );
      tree.setItemHeight( 15 );
      TestUtil.flush();
      var bounds = getElementBounds( sample );
      assertEquals( 0, bounds.left );
      assertEquals( 150, bounds.top );
      assertEquals( 500, bounds.width );
      assertEquals( 15, bounds.height );
      tree.destroy();
    },

    testGridLinesState : function() {
      var tree = this._createDefaultTree( true );
      tree.setLinesVisible( true );
      TestUtil.flush();
      var row = tree._rowContainer.getRow( 0 );
      assertTrue( row.hasState( "linesvisible" ) );
      tree.setLinesVisible( false );
      assertFalse( row.hasState( "linesvisible" ) );
      tree.destroy();
    },

    testZIndex : function() {
      var tree = this._createDefaultTree( true );
      tree.setLinesVisible( true );
      TestUtil.flush();
      assertEquals( 1, parseInt( tree._rowContainer._vertGridLines[ 0 ].style.zIndex, 10 ) );
      tree.destroy();
    },

    testGridLinesStateOnNewRows : function() {
      var tree = this._createDefaultTree( true );
      TestUtil.flush();
      tree.setLinesVisible( true );
      var row = tree._rowContainer.getRow( 24 );
      assertTrue( row.hasState( "linesvisible" ) );
      tree.setHeight( 1000 );
      row = tree._rowContainer.getRow( 28 );
      assertTrue( row.hasState( "linesvisible" ) );
      tree.destroy();
    },

    testGridLinesHorizontal : function() {
      var tree = this._createDefaultTree();
      var row = tree._rowContainer.getRow( 0 );
      assertFalse( TestUtil.hasCssBorder( row.$el.get( 0 ) ) );
      tree.setLinesVisible( true );
      TestUtil.flush();
      var border = tree._rowContainer._getHorizontalGridBorder();
      // NOTE: FF has some weired behavior here, using computed css it returns 0.8px instead of 1px
      var element = row.$el.get( 0 );
      assertEquals( border.getWidthBottom(), parseInt( element.style.borderBottomWidth, 10 ) );
      assertEquals( border.getWidthTop(), parseInt( element.style.borderTopWidth, 10 ) );
      tree.destroy();
    },

    testInitialGridLinesHorizontal : function() {
      var tree = this._createDefaultTree( true );
      tree.setHeight( 0 );
      tree.setLinesVisible( true );
      tree.setHeight( 100 );
      TestUtil.flush();
      var border = tree._rowContainer._getHorizontalGridBorder();
      var row = tree._rowContainer.getRow( 0 );
      var element = row.$el.get( 0 );
      assertEquals( border.getWidthBottom(), parseInt( element.style.borderBottomWidth, 10 ) );
      assertEquals( border.getWidthTop(), parseInt( element.style.borderTopWidth, 10 ) );
      tree.destroy();
    },

    testGetConfig : function() {
      var cont = this._createContainer();
      var config = cont.getRenderConfig();
      assertEquals( null, config.textColor );
      assertEquals( null, config.font );
      assertEquals( true, config.enabled );
      assertEquals( false, config.focused );
      assertEquals( false, config.linesVisible );
      assertEquals( false, config.fullSelection );
      assertEquals( false, config.hasCheckBoxes );
      assertEquals( null, config.checkBoxLeft );
      assertEquals( null, config.checkBoxWidth );
      assertEquals( null, config.variant );
      assertEquals( null, config.selectionPadding );
      assertEquals( [], config.alignment );
      assertEquals( [], config.itemLeft );
      assertEquals( [], config.itemWidth );
      assertEquals( [], config.itemImageLeft );
      assertEquals( [], config.itemImageWidth );
      assertEquals( [], config.itemTextLeft );
      assertEquals( [], config.itemTextWidth );
      assertEquals( 16, config.indentionWidth );
      assertEquals( 0, config.columnCount);
      assertEquals( 0, config.treeColumn );
      cont.destroy();
    },

    testSetCellToolTipsEnabled_True : function() {
      var cont = this._createContainer();
      cont.setHeight( 100 );

      cont.setCellToolTipsEnabled( true );

      assertEquals( "", cont.getToolTipText() ); // "" causes the tooltip to be bound, but not shown
    },

    testSetCellToolTipsEnabled_False : function() {
      var cont = this._createContainer();
      cont.setHeight( 100 );

      cont.setCellToolTipsEnabled( true );
      cont.setCellToolTipsEnabled( false );

      assertNull( cont.getToolTipText() );
    },

    testRequestToolTipText_DispatchesBubblingEvent : function() {
      var grid = this._createDefaultTree();
      var row = grid.getRowContainer().getRow( 1 );
      TestUtil.hoverFromTo( document.body, row.$el );
      var data;
      grid.addEventListener( "renderCellToolTip", function( value ) {
        data = value;
      } );

      grid.getRowContainer().requestToolTipText();

      assertIdentical( row, data );
    },

    /////////
    // Helper

    // TODO [tb] : refactor to create TreeRowContainer
    _createDefaultTree : function( noflush ) {
      this._fakeAppearance();
      var tree = new rwt.widgets.Grid( {
        "appearance": "tree",
        "fullSelection": true,
        "selectionPadding" : [ 2, 4 ],
        "indentionWidth" : 16
      } );
      tree.setItemHeight( 20 );
      tree.setLeft( 0 );
      tree.setTop( 0 );
      tree.setWidth( 500 );
      tree.setHeight( 500 );
      tree.setItemMetrics( 0, 0, 500, 0, 0, 0, 500 );
      tree.setColumnCount( 1 );
      tree.setItemMetrics( 1, 0, 500, 0, 0, 0, 500 );
      tree.setItemMetrics( 2, 0, 500, 0, 0, 0, 500 );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Grid" );
      rwt.remote.ObjectRegistry.add( "w" + tree.toHashCode(), tree, handler );
      tree.addToDocument();
      if( !noflush ) {
        TestUtil.flush();
      }
      return tree;
    },

    _fakeAppearance : function() {
      var empty = {
        style : function() {
          return {
            "background" : "undefined",
            "backgroundGradient" : null,
            "backgroundImage" : null,
            "foreground" : "undefined"
          };
        }
      };
      TestUtil.fakeAppearance( "tree-indent", empty );
      TestUtil.fakeAppearance( "tree-row", empty );
      TestUtil.fakeAppearance( "tree-row-overlay", empty );
    },

    _createContainer : function() {
      var result = new rwt.widgets.base.GridRowContainer();
      result.setBaseAppearance( "tree" );
      return result;
    }

  }

} );

  var getRows = function( container ) {
    var count = container.getRowCount();
    var result = [];
    for( var i = 0; i < count; i++ ) {
      result[ i ] = container.getRow( i );
    }
    return result;
  };

  var getElementBounds = function( node ) {
    return {
      left : node.offsetLeft,
      top : node.offsetTop,
      width : node.offsetWidth,
      height : node.offsetHeight
    };
  };

}() );

