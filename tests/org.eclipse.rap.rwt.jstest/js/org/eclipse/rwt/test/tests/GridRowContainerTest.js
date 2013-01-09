/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

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
      assertEquals( 11, cont.getChildren().length );
      assertEquals( "table-row", cont.getChildren()[ 0 ].getAppearance() );
    },

    testAddTreeRowsOnTreeResize : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setHeight( 551 );
      var clientArea = tree._rowContainer;
      assertEquals( 12, clientArea.getChildren().length );
      tree.destroy();
    },

    testAddOneAdditionalRow : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      var clientArea = tree._rowContainer;
      tree.setHeight( 499 );
      assertEquals( 10, clientArea.getChildren().length );
      tree.setHeight( 500 );
      assertEquals( 11, clientArea.getChildren().length );
      tree.setHeight( 501 );
      assertEquals( 11, clientArea.getChildren().length );
      tree.destroy();
    },

    testAddTreeRowsOnRowResize : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setItemHeight( 25 );
      var clientArea = tree._rowContainer;
      assertEquals( 21, clientArea.getChildren().length );
      tree.destroy();
    },

    testRemoveTreeRowsOnTreeResize : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setHeight( 401 );
      var clientArea = tree._rowContainer;
      assertEquals( 9, clientArea.getChildren().length );
      tree.destroy();
    },

    testRemoveTreeRowsOnRowResize : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setItemHeight( 100 );
      var clientArea = tree._rowContainer;
      assertEquals( 6, clientArea.getChildren().length );
      tree.destroy();
    },

    testTreeRowBounds : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var sample = tree._rowContainer._getTargetNode().childNodes[ 10 ];
      var bounds = TestUtil.getElementBounds( sample );
      assertEquals( 0, bounds.left );
      assertEquals( 200, bounds.top );
      assertEquals( 500, bounds.width );
      assertEquals( 20, bounds.height );
      tree.destroy();
    },

    testRenderEmptyRowOnHoverBug : function() {
      // See Bug 349310 - Mouseover on invisible items in SWT Tree
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      var node = tree._rowContainer._children[ 2 ]._getTargetNode();
      assertEquals( "item1", node.firstChild.innerHTML );
      root.setExpanded( false );
      TestUtil.flush();
      assertEquals( "", node.firstChild.innerHTML );
      TestUtil.mouseOver( tree._rowContainer._children[ 2 ] );

      assertEquals( "", node.firstChild.innerHTML );
      tree.destroy();
    },

    testRenderEmptyRowsBackground : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var grad1 = [ [ 0, "red" ], [ 1, "yellow" ] ];
      var grad2 = [ [ 0, "yellow" ], [ 1, "red" ] ];
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          return {
            "itemBackground" : states.selected ? "red" : "blue",
            "itemBackgroundGradient" : states.selected ?  grad1 : grad2,
            "itemBackgroundImage" : states.selected ? "foo.jpg" : "bar.jpg",
            "itemForeground" : "undefined",
            "overlayBackground" : states.selected ? "red" : "blue",
            "overlayBackgroundGradient" : states.selected ?  grad1 : grad2,
            "overlayBackgroundImage" : states.selected ? "foo.jpg" : "bar.jpg",
            "overlayForeground" : "undefined",
            "backgroundImage" : null
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
      var row = tree._rowContainer._children[ 1 ];
      assertEquals( "red", row.getBackgroundColor() );
      assertEquals( "foo.jpg", row.getBackgroundImage() );
      assertIdentical( grad1, row.getBackgroundGradient() );
      root.setExpanded( false );
      TestUtil.flush();
      assertNull( row.getBackgroundColor() );
      assertNull( row.getBackgroundImage() );
      assertNull( row.getBackgroundGradient() );
      tree.destroy();
    },

    testChangeTreeRowBounds : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var sample = tree._rowContainer._getTargetNode().childNodes[ 10 ];
      tree.setWidth( 400 );
      tree.setItemHeight( 15 );
      TestUtil.flush();
      var bounds = TestUtil.getElementBounds( sample );
      assertEquals( 0, bounds.left );
      assertEquals( 150, bounds.top );
      assertEquals( 500, bounds.width );
      assertEquals( 15, bounds.height );
      tree.destroy();
    },

    testGridLinesState : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree( true );
      tree.setLinesVisible( true );
      TestUtil.flush();
      var row = tree._rowContainer._children[ 0 ];
      assertTrue( row.hasState( "linesvisible" ) );
      tree.setLinesVisible( false );
      assertFalse( row.hasState( "linesvisible" ) );
      tree.destroy();
    },

    testZIndex : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree( true );
      tree.setLinesVisible( true );
      TestUtil.flush();
      var row = tree._rowContainer._children[ 0 ];
      assertEquals( 0, row.getZIndex() );
      assertEquals( 1, tree._rowContainer._vertGridLines[ 0 ].style.zIndex );
      tree.destroy();
    },

    testGridLinesStateOnNewRows : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree( true );
      TestUtil.flush();
      tree.setLinesVisible( true );
      var row = tree._rowContainer._children[ 24 ];
      assertTrue( row.hasState( "linesvisible" ) );
      tree.setHeight( 1000 );
      row = tree._rowContainer._children[ 28 ];
      assertTrue( row.hasState( "linesvisible" ) );
      tree.destroy();
    },

    testGridLinesHorizontal : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var row = tree._rowContainer._children[ 0 ];
      assertFalse( TestUtil.hasCssBorder( row.getElement() ) );
      tree.setLinesVisible( true );
      TestUtil.flush();
      var border = tree._rowContainer._getHorizontalGridBorder();
      assertIdentical( border, row.getBorder() );
      tree.destroy();
    },

    testInitialGridLinesHorizontal : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree( true );
      tree.setHeight( 0 );
      tree.setLinesVisible( true );
      tree.setHeight( 100 );
      TestUtil.flush();
      var border = tree._rowContainer._getHorizontalGridBorder();
      var row = tree._rowContainer._children[ 0 ];
      assertIdentical( border, row.getBorder() );
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

    /////////
    // Helper

    // TODO [tb] : refactor to create TreeRowContainer
    _createDefaultTree : function( noflush ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
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
      tree.setColumnCount( 1 )
      tree.setItemMetrics( 1, 0, 500, 0, 0, 0, 500 );
      tree.setItemMetrics( 2, 0, 500, 0, 0, 0, 500 );
      tree.addToDocument();
      if( !noflush ) {
        TestUtil.flush();
      }
      return tree;
    },

    _fakeAppearance : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var empty = {
        style : function( states ) {
          return {
            "itemBackground" : "undefined",
            "itemBackgroundGradient" : "undefined",
            "itemBackgroundImage" : null,
            "itemForeground" : "undefined",
            "overlayBackground" : "undefined",
            "overlayBackgroundGradient" : "undefined",
            "overlayBackgroundImage" : null,
            "overlayForeground" : "undefined",
            "backgroundImage" : null
          }
        }
      };
      TestUtil.fakeAppearance( "tree-indent", empty );
      TestUtil.fakeAppearance( "tree-row", empty );
    },

    _createContainer : function() {
      var result = new rwt.widgets.base.GridRowContainer();
      result.setBaseAppearance( "tree" );
      return result;
    }

  }

} );

}());

