/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.GridUtilTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateDisposeSplitContainer : function() {
      var container = this._createSplitContainer();
      container.destroy();
    },

    testGetSubContainer : function() {
      var container = this._createSplitContainer();
      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertTrue( sub1 instanceof rwt.widgets.base.GridRowContainer );
      assertTrue( sub2 instanceof rwt.widgets.base.GridRowContainer );
      container.destroy();
    },

    testSubContainerCanBeIdentifiedByRenderConfig : function() {
      var container = this._createSplitContainer();
      var configLeft = container.getSubContainer( 0 ).getRenderConfig();
      var configRight = container.getSubContainer( 1 ).getRenderConfig();

      assertEquals( 0, configLeft.containerNumber );
      assertEquals( 1, configRight.containerNumber );
    },

    testAddToParent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var container = this._createSplitContainer();
      var parent = TestUtil.getDocument();
      parent.add( container );
      assertIdentical( parent, container.getSubContainer( 0 ).getParent() );
      assertIdentical( parent, container.getSubContainer( 1 ).getParent() );
      container.destroy();
    },

    testDestroy : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var container = this._createSplitContainer();
      TestUtil.getDocument().add( container );
      container.destroy();
      TestUtil.flush();
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

    testGetTopHeight : function() {
      var container = this._createSplitContainer();
      container.setTop( 30 );
      container.setHeight( 300 );
      assertEquals( 30, container.getTop() );
      assertEquals( 300, container.getHeight() );
      container.destroy();
    },

    testSetWidth : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var container = this._createSplitContainer();
      container.setWidth( 100 );
      container.renderAll();
      TestUtil.flush();
      TestUtil.flush();
      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 60, sub1.getWidth() );
      assertEquals( 60, sub1.getRow( 0 ).getWidth() );
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
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 90, sub2.getRow( 0 ).getWidth() );
      container.destroy();
    },

    testFixedColumnsEqualsTotalColumns : function() {
      var container = this._createSplitContainer();

      container.setFixedColumns( 5 );
      container.renderAll();

      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 100, container.getWidth() );
      assertEquals( 100, sub1.getWidth() );
      assertEquals( 100, sub2.getLeft() );
      assertEquals( 0, sub2.getWidth() );
      container.destroy();
    },

    testSetRowWidthSmallerWidth : function() {
      var container = this._createSplitContainer();
      container.setRowWidth( 60 );
      container.renderAll();
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 0, sub2.getRow( 0 ).getWidth() );
      container.destroy();
    },

    testScrollLeftRightContainer : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var container = this._createSplitContainer();
      container.setRowWidth( 200 );
      container.renderAll();
      TestUtil.flush();
      container.setScrollLeft( 20 );
      var sub1 = container.getSubContainer( 0 );
      var sub2 = container.getSubContainer( 1 );
      assertEquals( 0, sub1.getScrollLeft() );
      assertEquals( 20, sub2.getScrollLeft() );
      container.destroy();
    },

    ////////////////////////
    // Tests with "real" Tree

    testCreateMinimalTreeWithFixedColumns : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = new rwt.widgets.Grid( {
        "appearance": "table",
        "splitContainer" : true
      } );
      rwt.widgets.util.GridUtil.setFixedColumns( tree, 3 );
      // first 3 columns fixed -> one extra contianer for first 3
      assertIdentical( tree, tree._rowContainer.getSubContainer( 0 ).getParent() );
      assertIdentical( tree, tree._rowContainer.getSubContainer( 1 ).getParent() );
      assertNull( tree, tree._rowContainer.getSubContainer( 2 ) );
      tree.addToDocument();
      TestUtil.flush();
      tree.destroy();
    },

    testIgnoreFixedColumnsWithoutSplitContainer : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = new rwt.widgets.Grid( {
        "appearance": "table"
      } );
      rwt.widgets.util.GridUtil.setFixedColumns( tree, 3 );
      tree.addToDocument();
      TestUtil.flush();
      assertTrue( tree._rowContainer instanceof rwt.widgets.base.GridRowContainer );
      tree.destroy();
    },

    testCreateNormalTreeWithFixedColumns : function() {
      var tree = this._createSplitTree();
      assertIdentical( tree, tree._rowContainer.getSubContainer( 0 ).getParent() );
      assertIdentical( tree, tree._rowContainer.getSubContainer( 1 ).getParent() );
      assertNull( tree, tree._rowContainer.getSubContainer( 2 ) );
      tree.destroy();
    },

    testChangeFixedColumns : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      assertEquals( 2, tree.getRowContainer().getFixedColumns() );
      var containerEl = tree.getRowContainer().getSubContainer( 0 ).getElement();
      var orgWidth = parseInt( containerEl.style.width, 10 );
      rwt.widgets.util.GridUtil.setFixedColumns( tree, 3 );
      assertEquals( 3, tree.getRowContainer().getFixedColumns() );
      TestUtil.flush();
      var newWidth = parseInt( containerEl.style.width, 10 );
      assertTrue( newWidth > orgWidth );
      tree.destroy();
    },

    testRenderSplitItem : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      tree.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( tree.getRootItem(), 0 );
      item.setTexts( [ "c0", "c1", "c2", "c3", "c4" ] );
      TestUtil.flush();
      var rowLeft = tree._rowContainer.getSubContainer( 0 ).getRow( 0 );
      var rowRight = tree._rowContainer.getSubContainer( 1 ).getRow( 0 );
      assertEquals( 2, rowLeft.$el.get( 0 ).childNodes.length );
      assertEquals( 3, rowRight.$el.get( 0 ).childNodes.length );
      assertEquals( "c0", rowLeft.$el.get( 0 ).firstChild.innerHTML );
      assertEquals( "c1", rowRight.$el.get( 0 ).firstChild.innerHTML );
      tree.destroy();
    },

    testSplitSelectionClickLeft : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      tree.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      assertFalse( tree.isItemSelected( item ) );
      TestUtil.clickDOM( tree._rowContainer.getSubContainer( 0 ).getRow( 0 ).$el.get( 0 ) );
      TestUtil.flush();
      assertTrue( tree.isItemSelected( item ) );
      tree.destroy();
    },

    testSplitSelectionClickRight : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      tree.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      assertFalse( tree.isItemSelected( item ) );
      TestUtil.clickDOM( tree._rowContainer.getSubContainer( 1 ).getRow( 0 ).$el.get( 0 ) );
      TestUtil.flush();
      assertTrue( tree.isItemSelected( item ) );
      tree.destroy();
    },

    testSplitSelectionByServer : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      tree.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      assertFalse( tree.isItemSelected( item ) );
      rwt.remote.EventUtil.setSuspended( true );
      tree.selectItem( item );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      assertTrue( tree.isItemSelected( item ) );
      tree.destroy();
    },

    testSyncHoverItem : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      tree.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( tree.getRootItem(), 0 );
      item.setTexts( [ "bla" ] );
      item.setImages( [ [ "bla.jpg", 10, 10 ] ] );
      TestUtil.flush();
      var rowNode = tree._rowContainer.getSubContainer( 0 ).getRow( 0 ).$el.get( 0 );
      TestUtil.hoverFromTo( document.body, rowNode );
      TestUtil.hoverFromTo( rowNode, rowNode.firstChild );
      TestUtil.forceInterval( tree._rowContainer.getSubContainer( 1 )._asyncTimer );
      assertIdentical( item, tree._rowContainer.getSubContainer( 0 ).getHoverItem() );
      assertIdentical( item, tree._rowContainer.getSubContainer( 1 ).getHoverItem() );
      assertTrue( tree._rowContainer.getSubContainer( 1 ).getRow( 0 ).hasState( "over" ) );
      tree.destroy();
    },

    testCellToolTipOnFixedColumns : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      widgetManager.add( tree, "w3", true );
      tree.setWidth( 300 );
      tree.setEnableCellToolTip( true );
      tree.setColumnCount( 6 );
      tree.setItemMetrics( 0, 0, 5, 0, 0, 0, 50 );
      tree.setItemMetrics( 1, 5, 10, 0, 0, 0, 50 );
      tree.setItemMetrics( 2, 15, 10, 0, 0, 0, 50 );
      tree.setItemMetrics( 3, 25, 10, 0, 0, 0, 50 );
      tree.setItemMetrics( 4, 35, 350, 0, 0, 0, 50 );
      tree.setItemMetrics( 5, 400, 100, 405, 10, 430, 50 );
      tree.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( tree.getRootItem(), 0 );
      widgetManager.add( item, "w45", true );
      TestUtil.flush();
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      tree.setScrollLeft( 20 );
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var node = tree._rowContainer.getSubContainer( 1 ).getRow( 0 ).$el.get( 0 );

      TestUtil.fakeMouseEventDOM( node, "mouseover", leftButton, 6, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 6, 11 );
      TestUtil.forceInterval( rwt.widgets.base.WidgetToolTip.getInstance()._showTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      var actualItem = message.findCallProperty( "w3", "renderToolTipText", "item" );
      var actualCol = message.findCallProperty( "w3", "renderToolTipText", "column" );
      assertEquals( "w45", actualItem );
      assertEquals( 1, actualCol );
      tree.destroy();
    },

    testCellToolTipOnNonFixedColumns : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      widgetManager.add( tree, "w3", true );
      tree.setWidth( 300 );
      tree.setEnableCellToolTip( true );
      tree.setColumnCount( 6 );
      tree.setItemMetrics( 0, 0, 5, 0, 0, 0, 50 );
      tree.setItemMetrics( 1, 5, 10, 0, 0, 0, 50 );
      tree.setItemMetrics( 2, 15, 10, 0, 0, 0, 50 );
      tree.setItemMetrics( 3, 25, 10, 0, 0, 0, 50 );
      tree.setItemMetrics( 4, 35, 350, 0, 0, 0, 50 );
      tree.setItemMetrics( 5, 400, 100, 405, 10, 430, 50 );
      tree.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( tree.getRootItem(), 0 );
      widgetManager.add( item, "w45", true );
      TestUtil.flush();
      TestUtil.prepareTimerUse();
      TestUtil.initRequestLog();
      tree.setScrollLeft( 20 );
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var node = tree._rowContainer.getSubContainer( 1 ).getRow( 0 ).$el.get( 0 );

      TestUtil.fakeMouseEventDOM( node, "mouseover", leftButton, 16, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 16, 11 );
      TestUtil.forceInterval( rwt.widgets.base.WidgetToolTip.getInstance()._showTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      var actualItem = message.findCallProperty( "w3", "renderToolTipText", "item" );
      var actualCol = message.findCallProperty( "w3", "renderToolTipText", "column" );
      assertEquals( "w45", actualItem );
      assertEquals( 4, actualCol );
      tree.destroy();
    },

    testLinesVisibleForSplitTree : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      for( var i = 0; i < 5; i++ ) {
        var column = new rwt.widgets.GridColumn( tree );
        var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.GridColumn" );
        rwt.remote.ObjectRegistry.add( "col", column, handler );
        column.setLeft( tree.getRenderConfig().itemLeft[ i ] );
        column.setWidth( tree.getRenderConfig().itemWidth[ i ] );
        if( i < 2 ) {
          column.setFixed( true );
        }
      }
      tree.setLinesVisible( true );
      TestUtil.flush();
      var border = tree._rowContainer.getSubContainer( 0 )._getHorizontalGridBorder();
      var element = tree._rowContainer.getSubContainer( 0 ).getRow( 0 ).$el.get( 0 );
      assertTrue( tree.hasState( "linesvisible" ) );
      assertEquals( border.getWidthBottom(), parseInt( element.style.borderBottomWidth, 10 ) );
      assertEquals( border.getWidthTop(), parseInt( element.style.borderTopWidth, 10 ) );
      tree.destroy();
    },

    testVerticalGridLayoutOnSplitTree : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      tree.setLinesVisible( true );
      TestUtil.flush();
      var cont1 = tree.getRowContainer().getSubContainer( 0 ).$el.get( 0 );
      var cont2 = tree.getRowContainer().getSubContainer( 1 ).$el.get( 0 )
      assertEquals( 3, cont1.childNodes.length );
      assertEquals( 4, cont2.childNodes.length );
      assertEquals( 49, parseInt( cont1.childNodes[ 1 ].style.left, 10 ) );
      assertEquals( 21, parseInt( cont1.childNodes[ 2 ].style.left, 10 )  );
      assertEquals( 20, parseInt( cont2.childNodes[ 1 ].style.left, 10 )  );
      assertEquals( 52, parseInt( cont2.childNodes[ 2 ].style.left, 10 )  );
      assertEquals( 52, parseInt( cont2.childNodes[ 3 ].style.left, 10 )  );
      tree.destroy();
    },

    testVerticalGridLayoutOnChangeFixedColumns : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createSplitTree();
      TestUtil.flush();
      var cont1 = tree.getRowContainer().getSubContainer( 0 ).$el.get( 0 );
      var cont2 = tree.getRowContainer().getSubContainer( 1 ).$el.get( 0 );
      tree.setLinesVisible( true );
      TestUtil.flush();
      rwt.widgets.util.GridUtil.setFixedColumns( tree, 0 );
      TestUtil.flush();
      assertEquals( 1, cont1.childNodes.length );
      assertEquals( 6, cont2.childNodes.length );
      tree.destroy();
    },

    /////////
    // helper

    _createSplitContainer : function() {
      // column order: 2,0 - 1,3,4
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var argsMap = { "splitContainer" : true };
      var result = rwt.widgets.util.GridUtil.createTreeRowContainer( argsMap );
      result.setFixedColumns( 2 );
      result.setBaseAppearance( "table" );
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
      TestUtil.getDocument().add( result );
      TestUtil.flush();
      result.renderAll();
      return result;
    },

    _createSplitTree : function( noflush, option, arg ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var args = {
        "appearance" : "table",
        "splitContainer" : true
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
      var tree = new rwt.widgets.Grid( args );
      rwt.widgets.util.GridUtil.setFixedColumns( tree, 2 );
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
        TestUtil.flush();
      }
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Grid" );
      var barHandler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.ScrollBar" );
      rwt.remote.ObjectRegistry.add( "w3", tree, handler );
      rwt.remote.ObjectRegistry.add( "w3_vscroll", tree.getVerticalBar(), barHandler );
      rwt.remote.ObjectRegistry.add( "w3_hscroll", tree.getHorizontalBar(), barHandler );
      return tree;
    },

    _fillTree : function( tree, count, subItems, flatCount ) {
      tree.setItemCount( ( subItems && flatCount ) ? ( count / 2 ) : count );
      var i = 0;
      var itemNr = 0;
      while( i < count ) {
        var item = new rwt.widgets.GridItem( tree.getRootItem(), itemNr );
        itemNr++;
        item.setTexts( [ "Test" + i ] );
        if( subItems ) {
          item.setItemCount( 1 );
          var subitem = new rwt.widgets.GridItem( item, 0 );
          if( flatCount ) {
            item.setExpanded( true );
            i++;
            subitem.setTexts( [ "Test" + i ] );
          } else {
            subitem.setTexts( [ "Test" + i + "sub" ] );
          }
        }
        i++;
      }
    }

  }

} );
