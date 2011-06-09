/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.TreeRowContainerTest", {

  extend : qx.core.Object,
  
  // NOTE : Many of these tests use the Tree as an intermediate layer for tests.
  //        This is for historical reasons and should be adapted step by step.
  members : {

    testCreate : function() {
      var cont = this._createContainer();
      assertTrue( cont instanceof org.eclipse.rwt.widgets.TreeRowContainer );
      cont.destroy();
    },
    
    testCreateTreeRowsWithAppearance : function() {
      var cont = this._createContainer();
      cont.setRowAppearance( "table-row" );
      cont.setRowHeight( 50 );
      cont.setHeight( 501 );
      assertEquals( 11, cont.getChildren().length );
      assertEquals( "table-row", cont.getChildren()[ 0 ].getAppearance() );
    },

    testAddTreeRowsOnTreeResize : function() {
      var tree = new org.eclipse.rwt.widgets.Tree( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setHeight( 551 );
      var clientArea = tree._rowContainer;
      assertEquals( 12, clientArea.getChildren().length );
      tree.destroy();
    },

    testAddOneAdditionalRow : function() {
      var tree = new org.eclipse.rwt.widgets.Tree( { "appearance": "tree" } );
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
      var tree = new org.eclipse.rwt.widgets.Tree( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setItemHeight( 25 );
      var clientArea = tree._rowContainer;
      assertEquals( 21, clientArea.getChildren().length );
      tree.destroy();
    },

    testRemoveTreeRowsOnTreeResize : function() {
      var tree = new org.eclipse.rwt.widgets.Tree( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setHeight( 401 );
      var clientArea = tree._rowContainer;
      assertEquals( 9, clientArea.getChildren().length );
      tree.destroy();
    },

    testRemoveTreeRowsOnRowResize : function() {
      var tree = new org.eclipse.rwt.widgets.Tree( { "appearance": "tree" } );
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setItemHeight( 100 );
      var clientArea = tree._rowContainer;
      assertEquals( 6, clientArea.getChildren().length );
      tree.destroy();
    },
    
    testTreeRowBounds : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var sample = tree._rowContainer._getTargetNode().childNodes[ 10 ];
      var bounds = testUtil.getElementBounds( sample );
      assertEquals( 0, bounds.left );
      assertEquals( 200, bounds.top );
      assertEquals( 500, bounds.width );
      assertEquals( 20, bounds.height );
      tree.destroy();
    },
    
    testChangeTreeRowBounds : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var sample = tree._rowContainer._getTargetNode().childNodes[ 10 ];
      tree.setWidth( 400 );
      tree.setItemHeight( 15 );
      testUtil.flush();
      var bounds = testUtil.getElementBounds( sample );
      assertEquals( 0, bounds.left );
      assertEquals( 150, bounds.top );
      assertEquals( 500, bounds.width );
      assertEquals( 15, bounds.height );
      tree.destroy();
    },

    testGridLinesState : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree( true );
      tree.setLinesVisible( true );
      testUtil.flush();
      var row = tree._rowContainer._children[ 0 ];
      assertTrue( row.hasState( "linesvisible" ) );
      tree.setLinesVisible( false );
      assertFalse( row.hasState( "linesvisible" ) );
      tree.destroy();
    },

    testGridLinesStateOnNewRows : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree( true );
      testUtil.flush();
      tree.setLinesVisible( true );
      var row = tree._rowContainer._children[ 24 ];
      assertTrue( row.hasState( "linesvisible" ) );
      tree.setHeight( 1000 );
      row = tree._rowContainer._children[ 28 ];
      assertTrue( row.hasState( "linesvisible" ) );
      tree.destroy();
    },

    testGridLinesHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var row = tree._rowContainer._children[ 0 ];
      assertFalse( testUtil.hasCssBorder( row.getElement() ) );
      tree.setLinesVisible( true );
      testUtil.flush();
      var border = tree._getHorizontalGridBorder();
      assertIdentical( border, row.getBorder() );      
      tree.destroy();
    },

    testInitialGridLinesHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree( true );
      tree.setHeight( 0 );
      tree.setLinesVisible( true );
      tree.setHeight( 100 );      
      testUtil.flush();
      var border = tree._getHorizontalGridBorder();
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
      assertEquals( true, config.focused );
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
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      this._fakeAppearance(); 
      var tree = new org.eclipse.rwt.widgets.Tree( { 
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
        testUtil.flush();
      }
      return tree;
    },
    
    _fakeAppearance : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var empty = {
        style : function( states ) {
          return {
            "itemBackground" : "undefined",
            "itemForeground" : "undefined",
            "backgroundImage" : null
          }
        }
      }; 
      testUtil.fakeAppearance( "tree-indent", empty );
      testUtil.fakeAppearance( "tree-row", empty );            
    },
    
    _createContainer : function() {
      var result = new org.eclipse.rwt.widgets.TreeRowContainer();
      result.setRowAppearance( "tree-row" );
      return result;
    }

  }
  
} );