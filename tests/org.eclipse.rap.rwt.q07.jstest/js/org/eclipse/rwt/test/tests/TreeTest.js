/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.TreeTest", {

  extend : qx.core.Object,
  
  members : {
    
    testCreate : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      assertTrue( tree instanceof org.eclipse.rwt.widgets.Tree );
      assertEquals( "tree", tree.getAppearance() );
      tree.destroy();
    },
    
    testDefaultProperties : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      assertEquals( "default", tree.getCursor() );
      tree.destroy();
    },
    
    testItemHeight : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setItemHeight( 23 );
      assertEquals( 23, tree.getItemHeight() );
      tree.destroy();
    },
    
    testSetItemMetrics : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setTexts( [ "Test", "Test2" ] );      
      tree.setItemMetrics( 0, 0, 0, 0, 0, 0, 0 );
      tree.setItemMetrics( 1, 50, 40, 52, 13, 65, 25 );
      assertEquals( 50, tree.getItemLeft( item, 1 ) );      
      assertEquals( 40, tree.getItemWidth( item, 1 ) );      
      assertEquals( 52, tree.getItemImageLeft( item, 1 ) );      
      assertEquals( 13, tree.getItemImageWidth( item, 1 ) );      
      assertEquals( 65, tree.getItemTextLeft( item, 1 ) );      
      assertEquals( 25, tree.getItemTextWidth( item, 1 ) );
      tree.destroy();
    },
    
    testFirstItemMetrics : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "Test" ] );      
      tree.setIndentionWidth( 16 );
      tree.setItemMetrics( 0, 10, 50, 12, 13, 30, 8 );
      assertEquals( 26, tree.getItemLeft( item, 0, true ) );      
      assertEquals( 34, tree.getItemWidth( item, 0, true ) );      
      assertEquals( 28, tree.getItemImageLeft( item, 0 ) );      
      assertEquals( 13, tree.getItemImageWidth( item, 0 ) );      
      assertEquals( 46, tree.getItemTextLeft( item, 0 ) );      
      assertEquals( 8, tree.getItemTextWidth( item, 0 ) );
      tree.destroy();
    },
    
    testFirstItemMetricsLabelOverflow : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "Test" ] );      
      tree.setIndentionWidth( 10 );
      tree.setItemMetrics( 0, 0, 40, 0, 10, 10, 40 );
      assertEquals( 20, tree.getItemTextWidth( item, 0 ) );
      tree.destroy();
    },
    
    testFirstItemMetricsImageOverflow : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "Test" ] );      
      tree.setIndentionWidth( 10 );
      tree.setItemMetrics( 0, 0, 15, 0, 10, 10, 40 );
      assertEquals( 0, tree.getItemTextWidth( item, 0 ) );      
      assertEquals( 5, tree.getItemImageWidth( item, 0 ) );
      tree.destroy();
    },
    
    testSetIndentionWidths : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setIndentionWidth( 16 );
      assertEquals( 0, tree.getIndentionOffset( 0 ) );
      assertEquals( 16, tree.getIndentionOffset( 1 ) );
      assertEquals( 32, tree.getIndentionOffset( 2 ) );
      assertEquals( 48, tree.getIndentionOffset( 3 ) );
      tree.destroy();
    },
    
    testSecondColumnAsTreeColumn : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "Test", "Test2" ] );      
      tree.setIndentionWidth( 16 );
      tree.setItemMetrics( 0, 64, 40, 66, 13, 69, 8 );
      tree.setItemMetrics( 1, 34, 40, 36, 13, 49, 8 );
      tree.setTreeColumn( 1 );
      // first column is unchanged:
      assertEquals( 64, tree.getItemLeft( item, 0, true ) );      
      assertEquals( 40, tree.getItemWidth( item, 0, true ) );      
      assertEquals( 66, tree.getItemImageLeft( item, 0 ) );      
      assertEquals( 13, tree.getItemImageWidth( item, 0 ) );      
      assertEquals( 69, tree.getItemTextLeft( item, 0 ) );      
      assertEquals( 8, tree.getItemTextWidth( item, 0 ) );
      // second column is indented      
      assertEquals( 50, tree.getItemLeft( item, 1, true ) );      
      assertEquals( 24, tree.getItemWidth( item, 1, true ) );      
      assertEquals( 52, tree.getItemImageLeft( item, 1 ) );      
      assertEquals( 13, tree.getItemImageWidth( item, 1 ) );      
      assertEquals( 65, tree.getItemTextLeft( item, 1 ) );      
      assertEquals( 8, tree.getItemTextWidth( item, 1 ) );
      tree.destroy();
    },
    
    testGetTreeColumnWidth : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "Test", "Test2" ] );      
      tree.setIndentionWidth( 16 );
      tree.setItemMetrics( 0, 64, 40, 66, 13, 69, 8 );
      tree.setItemMetrics( 1, 0, 50, 36, 13, 49, 8 );
      tree.setTreeColumn( 1 );
      assertEquals( 50, tree.getTreeColumnWidth() );
      tree.destroy();
    },
    
    testChildren : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      assertEquals( [ child1, child2 ], tree.getRootItem().getChildren() );
      tree.destroy();
    },
    
    testSimpleInternalLayout : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      this._fakeAppearance();
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );
      testUtil.flush();
      var node = tree._clientArea.getElement();
      assertIdentical( tree._getTargetNode(), node.parentNode )
      assertEquals( "hidden", node.style.overflow );
      assertEquals( 500, parseInt( node.style.height ) );
      assertEquals( 600, parseInt( node.style.width ) );
      tree.destroy();
    },
    
    testSimpleInternalLayoutResize : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      this._fakeAppearance();
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );
      testUtil.flush();
      tree.setHeight( 400 );
      tree.setWidth( 700 );
      testUtil.flush();
      var node = tree._clientArea.getElement();
      assertEquals( 400, parseInt( node.style.height ) );
      assertEquals( 700, parseInt( node.style.width ) );
      tree.destroy();
    },

    testSimpleInternalLayoutWithBorder : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      this._fakeAppearance();
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );
      testUtil.flush();
      var border = new qx.ui.core.Border( 4 );
      tree.setBorder( border );
      testUtil.flush();
      var node = tree._clientArea.getElement();
      assertIdentical( tree._getTargetNode(), node.parentNode )
      assertEquals( 492, parseInt( node.style.height ) );
      assertEquals( 592, parseInt( node.style.width ) );
      tree.destroy();
    },

    testCreateTreeRows : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      var clientArea = tree._clientArea;
      assertEquals( 11, clientArea.getChildren().length );
      tree.destroy();
    },

    testAddTreeRowsOnTreeResize : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setHeight( 551 );
      var clientArea = tree._clientArea;
      assertEquals( 12, clientArea.getChildren().length );
      tree.destroy();
    },

    testAddTreeRowsOnRowResize : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setItemHeight( 25 );
      var clientArea = tree._clientArea;
      assertEquals( 21, clientArea.getChildren().length );
      tree.destroy();
    },

    testRemoveTreeRowsOnTreeResize : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setHeight( 401 );
      var clientArea = tree._clientArea;
      assertEquals( 9, clientArea.getChildren().length );
      tree.destroy();
    },

    testRemoveTreeRowsOnRowResize : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setItemHeight( 50 );
      tree.setHeight( 501 );
      tree.setItemHeight( 100 );
      var clientArea = tree._clientArea;
      assertEquals( 6, clientArea.getChildren().length );
      tree.destroy();
    },
    
    testTreeRowBounds : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var sample = tree._clientArea._getTargetNode().childNodes[ 10 ];
      var bounds = testUtil.getElementBounds( sample );
      assertEquals( 0, bounds.left );
      assertEquals( 200, bounds.top );
      assertEquals( 500, bounds.width );
      assertEquals( 20, bounds.height );
      tree.destroy();
    },
    
    testTreeRowSmallerClientArea : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setWidth( 600 );
      tree.setScrollBarsVisible( true, true );
      testUtil.flush();
      var row = tree._rows[ 0 ];
      var expected = 600 - tree._vertScrollBar.getWidth();
      assertEquals( expected, row.getWidth() );
      tree.destroy();
    },

    testChangeTreeRowBounds : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var sample = tree._clientArea._getTargetNode().childNodes[ 10 ];
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
    
    testRenderFirstLayer : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 10; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
      }
      testUtil.flush();
      sample = tree._clientArea._getTargetNode().childNodes[ 9 ];
      assertEquals( 1, sample.childNodes.length );
      assertEquals( "Test9", sample.childNodes[ 0 ].innerHTML );
      var bounds = testUtil.getElementBounds( sample );
      assertEquals( 0, bounds.left );
      assertEquals( 180, bounds.top );
      assertEquals( 500, bounds.width );
      assertEquals( 20, bounds.height );
      tree.destroy();
    },
    
    testRenderBeforeCreate : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree( true );
      for( var i = 0; i < 10; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
      }
      testUtil.flush();
      var sample = tree._clientArea._getTargetNode().childNodes[ 9 ];
      assertEquals( 1, sample.childNodes.length );
      tree.destroy();
    },
    
    testRenderMoreItemsThanRows : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
      }
      testUtil.flush();
      var clientArea = tree._clientArea;
      assertEquals( 25, clientArea.getChildren().length );            
      var sample = clientArea._getTargetNode().childNodes[ 24 ];
      assertEquals( 1, sample.childNodes.length );
      assertEquals( "Test24", sample.childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testRenderRemoveItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item;
      for( var i = 0; i < 10; i++ ) {
        item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
      }
      testUtil.flush();
      item.dispose();
      testUtil.flush();
      var sample = tree._clientArea._getTargetNode().childNodes[ 9 ];
      assertEquals( 1, sample.childNodes.length );
      assertEquals( "none", sample.childNodes[ 0 ].style.display );
      tree.destroy();
    },

    testRenderRemoveFirstItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item;
      for( var i = 0; i < 10; i++ ) {
        item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
      }
      item = tree._rootItem.getChildren()[ 0 ];
      assertEquals( "Test0", item.getText( 0 ) );
      testUtil.flush();
      item.dispose();
      testUtil.flush();
      item = tree._rootItem.getChildren()[ 0 ];
      assertEquals( "Test1", item.getText( 0 ) );
      var text0 = tree._rows[ 0 ]._getTargetNode().childNodes[ 0 ].innerHTML;
      var text8 = tree._rows[ 8 ]._getTargetNode().childNodes[ 0 ].innerHTML;
      var style9 = tree._rows[ 9 ]._getTargetNode().childNodes[ 0 ].style;
      assertEquals( "Test1", text0 );
      assertEquals( "Test9", text8 );
      assertEquals( "none", style9.display );
      tree.destroy();
    },
    
    testRenderMultipleLayer : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 10; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
        var subitem = new org.eclipse.rwt.widgets.TreeItem( item );
        subitem.setTexts( [ "Test" + i + "sub" ] );
      }
      var items = tree.getRootItem().getChildren();
      items[ 1 ].setExpanded( true );
      testUtil.flush();
      var rowNodes = tree._clientArea._getTargetNode().childNodes;
      assertEquals( "Test1", rowNodes[ 1 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test1sub", rowNodes[ 2 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test2", rowNodes[ 3 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },
    
    testRenderExpand : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 10; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
        var subitem = new org.eclipse.rwt.widgets.TreeItem( item );
        subitem.setTexts( [ "Test" + i + "sub" ] );
      }
      var items = tree.getRootItem().getChildren();
      testUtil.flush();
      items[ 1 ].setExpanded( true );
      testUtil.flush();
      var rowNodes = tree._clientArea._getTargetNode().childNodes;
      assertEquals( "Test1", rowNodes[ 1 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test1sub", rowNodes[ 2 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test2", rowNodes[ 3 ].childNodes[ 0 ].innerHTML );
      items[ 1 ].setExpanded( false );
      testUtil.flush();
      assertEquals( "Test1", rowNodes[ 1 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test2", rowNodes[ 2 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testRenderOnAddRemove : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      testUtil.fakeAppearance( "tree-indent", {
        style : function( states ) {
          var children = states.collapsed || states.expanded;
          return {
            "backgroundImage" : children ? "children.gif" : "empty.gif"
          };
        }
      } );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "foo" ] );
      testUtil.flush();
      var node = tree._rows[ 0 ]._getTargetNode();
      assertTrue( node.innerHTML.indexOf( "empty.gif" ) != -1 ); 
      var item2 = new org.eclipse.rwt.widgets.TreeItem( item );
      testUtil.flush();
      assertTrue( node.innerHTML.indexOf( "children.gif" ) != -1 );
      item2.dispose(); 
      testUtil.flush();
      assertTrue( node.innerHTML.indexOf( "empty.gif" ) != -1 );
      tree.destroy();
    },
    
    
    testClickOnExpandSymbol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
       testUtil.fakeAppearance( "tree-indent",  {
          style : function( states ) {
            var result = null;
            var children = states.expanded || states.collapsed;
            if( states.last && !states.first && !children ) {
              result = "end.gif";
            } else if( children && !(states.first || states.last ) ) {
              if( states.expanded ) {
                result = "intermediate-expanded.gif";
              } else {
                result = "intermediate-collapsed.gif";
              }
            }
            return { "backgroundImage" : result };
          }
        } );
        for( var i = 0; i < 10; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
        var subitem = new org.eclipse.rwt.widgets.TreeItem( item );
        subitem.setTexts( [ "Test" + i + "sub" ] );
      }
      var items = tree.getRootItem().getChildren();
      testUtil.flush();
      var rows = tree._clientArea._getTargetNode().childNodes;
      testUtil.clickDOM( rows[ 1 ] ); // nothing happens:
      assertEquals( "Test2", rows[ 2 ].childNodes[ 1 ].innerHTML );
      testUtil.clickDOM( rows[ 1 ].childNodes[ 0 ] )
      assertEquals( "Test1sub", rows[ 2 ].childNodes[ 1 ].innerHTML );
      tree.destroy();
    },
    
    testSetTopItemIndex : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var topItem;
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
      }
      org_eclipse_rap_rwt_EventUtil_suspend = true;
      tree.setTopItemIndex( 55 );
      org_eclipse_rap_rwt_EventUtil_suspend = false;
      testUtil.flush();
      var area = tree._clientArea._getTargetNode();
      assertEquals( 1100, tree._vertScrollBar.getValue() );
      assertEquals( "Test55", area.childNodes[ 0 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test64", area.childNodes[ 9 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testSetTopItemAndExpandClick : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      testUtil.fakeAppearance( "tree-indent",  {
        style : function( states ) {
          var result = null;
          var children = states.expanded || states.collapsed;
          if( children && !( states.first || states.last ) ) {
            if( states.expanded ) {
              result = "intermediate-expanded.gif";
            } else {
              result = "intermediate-collapsed.gif";
            }
          }
          return { "backgroundImage" : result };
        }
      } );
      var topItem;
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
        if( i == 55 ) {
          topItem = item;
        }
      }
      var child = new org.eclipse.rwt.widgets.TreeItem( topItem );
      child.setTexts( [ "subitem" ] );
      org_eclipse_rap_rwt_EventUtil_suspend = true;
      tree.setTopItemIndex( 55 );
      org_eclipse_rap_rwt_EventUtil_suspend = false;
      testUtil.flush();
      var area = tree._clientArea._getTargetNode();
      testUtil.clickDOM( area.childNodes[ 0 ].childNodes[ 0 ] )
      assertEquals( "Test55", area.childNodes[ 0 ].childNodes[ 1 ].innerHTML );
      assertEquals( "subitem", area.childNodes[ 1 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testScrollBarsDefaultProperties : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      assertFalse( tree._horzScrollBar.getVisibility() );
      assertFalse( tree._vertScrollBar.getVisibility() );
      assertFalse( tree._horzScrollBar.getMergeEvents() );
      assertFalse( tree._vertScrollBar.getMergeEvents() );
      tree.destroy();
    },

    testScrollBarsPreventDragStart : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var tree = this._createDefaultTree();
      var log = [];
      var loghandler = function( event ) { log.push( event ); }
      var drag = function( node ) {
        testUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
        testUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 25, 15 );
        testUtil.fakeMouseEventDOM( node, "mouseup", leftButton, 25, 15 );
      };
      tree.addEventListener( "dragstart", loghandler );
      drag( tree._getTargetNode() );
      assertEquals( 1, log.length );
      drag( tree._horzScrollBar._getTargetNode() );
      drag( tree._vertScrollBar._getTargetNode() );
      assertEquals( 1, log.length );      
      tree.destroy();
    },

    testSetScrollBarsVisibile : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( true, true );
      testUtil.flush();
      var node = tree._getTargetNode();
      assertTrue( tree._horzScrollBar.getVisibility() );
      assertTrue( tree._vertScrollBar.getVisibility() );
      tree.destroy();
    },
    
    testSetScrollBarsVisibileResetValue : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.add( tree, "wtest", false );
      tree.setScrollBarsVisible( true, true );      
      testUtil.flush();
      tree._horzScrollBar.setValue( 10 );
      tree._vertScrollBar.setValue( 10 );
      testUtil.initRequestLog();
      org_eclipse_rap_rwt_EventUtil_suspend = true;
      tree.setScrollBarsVisible( false, false );
      delete org_eclipse_rap_rwt_EventUtil_suspend;  
      assertEquals( 0, tree._horzScrollBar.getValue() );
      assertEquals( 0, tree._vertScrollBar.getValue() );
      var req = org.eclipse.swt.Request.getInstance();
      assertEquals( "0", req.getParameter( "wtest.scrollLeft" ) );
      wm.remove( tree );      
      tree.destroy();
    },

    testVerticalScrollBarLayout : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( false, true );
      testUtil.flush();
      var area = testUtil.getElementBounds( tree._clientArea.getElement() )
      var vertical 
        = testUtil.getElementBounds( tree._vertScrollBar.getElement() );
      assertEquals( 500, vertical.height );
      assertEquals( 0, vertical.right);
      assertEquals( 0, vertical.bottom );
      assertEquals( 500, area.height );
      assertTrue( area.width == 500 - vertical.width );
      tree.destroy();
    },

    testHorizontalScrollBarLayout : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( true, false );
      testUtil.flush();
      var area = testUtil.getElementBounds( tree._clientArea.getElement() )
      var horizontal 
        = testUtil.getElementBounds( tree._horzScrollBar.getElement() );
      assertEquals( 500, horizontal.width );
      assertEquals( 0, horizontal.bottom );
      assertEquals( 0, horizontal.right );
      assertEquals( 500, area.width );
      assertTrue( area.height == 500 - horizontal.height );
      tree.destroy();
    },

    testBothScrollBarsLayout : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( true, true );
      testUtil.flush();
      var area = testUtil.getElementBounds( tree._clientArea.getElement() )
      var horizontal 
        = testUtil.getElementBounds( tree._horzScrollBar.getElement() );
      var vertical 
        = testUtil.getElementBounds( tree._vertScrollBar.getElement() );
      var height = 500 - horizontal.height;
      var width = 500 - vertical.width
      assertTrue( area.height == height );
      assertTrue( area.width == width );
      assertTrue( horizontal.width == width );
      assertTrue( vertical.height == height );
      assertEquals( 0, horizontal.bottom );
      assertEquals( 0, vertical.right);
      assertTrue( vertical.width == horizontal.right );
      assertTrue( vertical.bottom == horizontal.height );
      tree.destroy();
    },
    
    testScrollHeight : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        new org.eclipse.rwt.widgets.TreeItem( tree );
      }
      testUtil.flush();
      assertEquals( 2020, tree._vertScrollBar.getMaximum() );
      tree.destroy();
    },

    testUpdateScrollHeightOnExpand : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var lastItem;
      for( var i = 0; i < 100; i++ ) {
        lastItem = new org.eclipse.rwt.widgets.TreeItem( tree );
      }
      new org.eclipse.rwt.widgets.TreeItem( lastItem );
      testUtil.flush();
      assertEquals( 2020, tree._vertScrollBar.getMaximum() );
      lastItem.setExpanded( true );
      testUtil.flush();
      assertEquals( 2040, tree._vertScrollBar.getMaximum() );
      tree.destroy();
    },
    
    testUpdateScrollOnItemHeightChange : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var lastItem;
      for( var i = 0; i < 100; i++ ) {
        new org.eclipse.rwt.widgets.TreeItem( tree );
      }
      testUtil.flush();
      assertEquals( 2020, tree._vertScrollBar.getMaximum() );
      tree.setItemHeight( 40 );
      testUtil.flush();
      assertEquals( 4040, tree._vertScrollBar.getMaximum() );
      tree.destroy();
    },
    
    testScrollVerticallyOnlyOneLayer : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
      }
      testUtil.flush();
      org_eclipse_rap_rwt_EventUtil_suspend = true;
      tree._vertScrollBar.setValue( 1000 );
      org_eclipse_rap_rwt_EventUtil_suspend = false;
      testUtil.flush();
      var itemNode = tree._clientArea._getTargetNode().firstChild;
      assertEquals( "Test50", itemNode.firstChild.innerHTML );
      tree.destroy();
    },
    
    testScrollHeightWithHeaderBug : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHeaderHeight( 20 );
      tree.setHeaderVisible( true );
      tree.setHeight( 490 );
      testUtil.flush();
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
      }
      testUtil.flush();
      var maxScroll = tree._vertScrollBar.getMaximum() 
                    - tree._vertScrollBar.getHeight();
      org_eclipse_rap_rwt_EventUtil_suspend = true;
      tree._vertScrollBar.setValue( maxScroll );
      org_eclipse_rap_rwt_EventUtil_suspend = false;
      testUtil.flush();
      var itemNode = tree._clientArea._getTargetNode().firstChild;
      assertEquals( "Test78", itemNode.firstChild.innerHTML );
      tree.destroy();
    },
    
    testScrollVerticallyMultipleLayer : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var i = 0;
      while( i < 100 ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
        item.setExpanded( true );
        i++;
        var subitem = new org.eclipse.rwt.widgets.TreeItem( item );
        subitem.setTexts( [ "Test" + i ] );
        i++;
      }
      testUtil.flush();
      org_eclipse_rap_rwt_EventUtil_suspend = true;
      tree._vertScrollBar.setValue( 1020 );
      org_eclipse_rap_rwt_EventUtil_suspend = false;
      testUtil.flush();
      var itemNode = tree._clientArea._getTargetNode().firstChild;
      assertEquals( "Test51", itemNode.firstChild.innerHTML );
      tree.destroy();
    },
    
    testScrollBackwardsVerticallyMultipleLayer : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var i = 0;
      while( i < 100 ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
        item.setExpanded( true );
        i++;
        var subitem = new org.eclipse.rwt.widgets.TreeItem( item );
        subitem.setTexts( [ "Test" + i ] );
        i++;
      }
      testUtil.flush();
      org_eclipse_rap_rwt_EventUtil_suspend = true;
      tree._vertScrollBar.setValue( 1400 ); 
      tree._vertScrollBar.setValue( 1020 );
      org_eclipse_rap_rwt_EventUtil_suspend = false;
      testUtil.flush();
      var itemNode = tree._clientArea._getTargetNode().firstChild;
      assertEquals( "Test51", itemNode.firstChild.innerHTML );
      tree.destroy();
    },
    
    testScrollBackwardsVerticallyMultipleLayer2 : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var i = 0;
      while( i < 100 ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
        item.setExpanded( true );
        i++;
        var subitem = new org.eclipse.rwt.widgets.TreeItem( item );
        subitem.setTexts( [ "Test" + i ] );
        i++;
      }
      testUtil.flush();
      org_eclipse_rap_rwt_EventUtil_suspend = true;
      tree._vertScrollBar.setValue( 1040 ); 
      tree._vertScrollBar.setValue( 1020 );
      org_eclipse_rap_rwt_EventUtil_suspend = false;
      testUtil.flush();
      var itemNode = tree._clientArea._getTargetNode().firstChild;
      assertEquals( "Test51", itemNode.firstChild.innerHTML );
      tree.destroy();
    },
    
    testScrollBugExpanded : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var i = 0;
      while( i < 100 ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
        item.setExpanded( true );
        i++;
        var subitem = new org.eclipse.rwt.widgets.TreeItem( item );
        subitem.setTexts( [ "Test" + i ] );
        i++;
      }
      testUtil.flush();
      org_eclipse_rap_rwt_EventUtil_suspend = true;
      tree._vertScrollBar.setValue( 100 );
      org_eclipse_rap_rwt_EventUtil_suspend = false;
      testUtil.flush();
      var itemNode = tree._rows[ 0 ]._getTargetNode();
      assertEquals( "Test5", itemNode.firstChild.innerHTML );
      tree.destroy();
    },
    
    testScrollBugCollapsed : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Test" + i ] );
        var subitem = new org.eclipse.rwt.widgets.TreeItem( item );
      }
      testUtil.flush();
      org_eclipse_rap_rwt_EventUtil_suspend = true;
      tree._vertScrollBar.setValue( 100 );
      org_eclipse_rap_rwt_EventUtil_suspend = false;
      testUtil.flush();
      var itemNode = tree._rows[ 0 ]._getTargetNode();
      assertEquals( "Test5", itemNode.firstChild.innerHTML );
      tree.destroy();
    },
    
    testDestroy : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      tree._showResizeLine( 0 );
      tree.setIsVirtual( true );
      tree.setHeaderHeight( 20 );
      tree.setHeaderVisible( true );
      testUtil.flush();
      tree.setFocusItem( item );
      tree._shiftSelectItem( item );
      var row = tree._rows[ 0 ]
      testUtil.hoverFromTo( document.body, row._getTargetNode() );
      var area = tree._clientArea;
      var dummy = tree._dummyColumn;
      var hscroll = tree._horzScrollBar;
      var vscroll = tree._vertScrollBar;
      var resize = tree._resizeLine;
      var rootItem = tree._rootItem;
      var element = tree.getElement();
      var columnArea = tree._columnArea;
      var mergeTimer = tree._mergeEventsTimer;
      var requestTimer = tree._sendRequestTimer;
      assertTrue( element.parentNode === document.body );
      assertNotNull( tree._rootItem );
      assertNotNull( tree._focusItem );
      assertNotNull( tree._leadItem );
      assertNotNull( tree._topItem );
      assertNotNull( tree._hoverItem );
      assertNotNull( tree._hoverElement );
      tree.destroy();
      testUtil.flush();
      assertTrue( element.parentNode !== document.body );
      assertTrue( tree.isDisposed() );
      assertTrue( row.isDisposed() );
      assertTrue( columnArea.isDisposed() );
      assertTrue( area.isDisposed() );
      assertTrue( hscroll.isDisposed() );
      assertTrue( vscroll.isDisposed() );
      assertTrue( vscroll.isDisposed() );
      assertTrue( resize.isDisposed() );
      assertTrue( rootItem.isDisposed() );
      assertTrue( mergeTimer.isDisposed() );
      assertTrue( requestTimer.isDisposed() );
      assertTrue( dummy.isDisposed() );
      assertNull( tree._rootItem );
      assertNull( tree._dummyColumn );
      assertNull( tree._focusItem );
      assertNull( tree._leadItem );
      assertNull( tree._rows );
      assertNull( tree._topItem );
      assertNull( tree._hoverItem );
      assertNull( tree._hoverElement );
      assertNull( tree._mergeEventsTimer );
      assertNull( tree._sendRequestTimer );
      assertNull( tree._clientArea );
      assertNull( tree._horzScrollBar );
      assertNull( tree._vertScrollBar );
      assertNull( tree._resizeLine );
      assertNull( tree._columnArea );
    },
    
    testSetCheckBoxMetrics : function() {
      var tree = this._createDefaultTree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      tree.setCheckBoxMetrics( 5, 20 );
      assertEquals( 21, tree.getCheckBoxLeft( item ) );
      assertEquals( 20, tree.getCheckBoxWidth( item ) );
      tree.destroy();
    },
    
    testSetCheckBoxMetricsOverflow : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      tree.setIndentionWidth( 10 );
      tree.setItemMetrics( 0, 0, 25, 0, 10, 10, 40 );
      tree.setCheckBoxMetrics( 5, 20 );
      assertEquals( 15, tree.getCheckBoxLeft( item ) );
      assertEquals( 10, tree.getCheckBoxWidth( item ) );
      tree.destroy();
    },

    testSetHasCheckBox : function() {
      var tree = this._createDefaultTree();
      assertFalse( tree.getHasCheckBoxes() );
      tree.setHasCheckBoxes( true );
      tree.setCheckBoxMetrics( 5, 20 );
      assertTrue( tree.getHasCheckBoxes() );
      tree.destroy();
    },
    
    testClickOnCheckBoxSymbol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      this._addCheckBoxes( tree );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      var node 
        = tree._clientArea._getTargetNode().childNodes[ 0 ].childNodes[ 0 ];
      testUtil.clickDOM( node.parentNode ); // nothing happens:
      assertFalse( item.isChecked() );
      testUtil.clickDOM( node );
      assertTrue( item.isChecked() );
      tree.destroy();
    },
    
    testHasFullSelection : function() {
      var tree = this._createDefaultTree();
      tree.setHasFullSelection( false );
      assertFalse( tree.getHasFullSelection() );
      tree.setHasFullSelection( true );
      assertTrue( tree.getHasFullSelection() );
      tree.destroy();
    },
    
    testSelectionClick : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setCheckBoxMetrics( 5, 20 );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      testUtil.clickDOM( tree._rows[ 0 ]._getTargetNode() ); 
      assertTrue( tree.isItemSelected( item ) );
      tree.destroy();
    },
    
    testDeselect : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setCheckBoxMetrics( 5, 20 );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      var node1 = tree._clientArea._getTargetNode().childNodes[ 0 ];
      var node2 = tree._clientArea._getTargetNode().childNodes[ 1 ];
      testUtil.clickDOM( node1 ); 
      assertTrue( tree.isItemSelected( item1 ) );
      testUtil.clickDOM( node2 ); 
      assertTrue( tree.isItemSelected( item2 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      tree.destroy();
    },
    
    testWheelScroll : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        new org.eclipse.rwt.widgets.TreeItem( tree );
      }
      testUtil.flush();
      assertEquals( 2020, tree._vertScrollBar.getMaximum() );
      assertEquals( 0, tree._vertScrollBar.getValue() );
      testUtil.fakeWheel( tree._clientArea , -3 );
      assertEquals( 120, tree._vertScrollBar.getValue() );
      testUtil.fakeWheel( tree._clientArea, 2 );
      assertEquals( 40, tree._vertScrollBar.getValue() );
      tree.destroy();
    },
    
    testWheelScrollStopProppagation : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var log = [];
      tree._clientArea.addEventListener( "mousewheel", function( event ) {
        log.push( "area", event );
      } );
      tree.addEventListener( "mousewheel", function( event ) {
        log.push( "tree", event );
      } );
      testUtil.fakeWheel( tree._clientArea, 2 );
      assertEquals( 2, log.length );
      assertEquals( "area", log[ 0 ] );
      assertTrue( log[ 1 ].getDefaultPrevented() );
      tree.destroy();
    },
    
    testFocusItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.clickDOM( tree._rows[ 2 ]._getTargetNode() );
      testUtil.flush();
      assertTrue( tree.isFocusItem( item2 ) );
      tree.destroy();
    },
    
    testChangeFocusItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var rows = tree._rows;
      testUtil.clickDOM( rows[ 1 ]._getTargetNode() );
      testUtil.flush();
      testUtil.clickDOM( rows[ 2 ]._getTargetNode() );
      testUtil.flush();
      assertFalse( tree.isFocusItem( item1 ) );
      assertTrue( tree.isFocusItem( item2 ) );
      tree.destroy();
    },

    testHasMultiSelection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      assertFalse( tree.getHasMultiSelection() );
      tree.setHasMultiSelection( true );
      assertTrue( tree.getHasMultiSelection() );
      tree.destroy();
    },

    testNoMultiSelection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      var node0 = tree._clientArea._getTargetNode().childNodes[ 0 ];
      var node1 = tree._clientArea._getTargetNode().childNodes[ 1 ];
      var node2 = tree._clientArea._getTargetNode().childNodes[ 2 ];
      testUtil.clickDOM( node0 ); 
      assertTrue( tree.isItemSelected( item0 ) );
      var left = qx.event.type.MouseEvent.buttons.left;
      testUtil.fakeMouseEventDOM( node2, "mousedown", left, 0, 0, 7 );
      assertFalse( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },    
    
    testCtrlMultiSelection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      var left = qx.event.type.MouseEvent.buttons.left;
      testUtil.click( tree._rows[ 0 ] );
      testUtil.ctrlClick( tree._rows[ 2 ] );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },    
    
    testCtrlMultiSelectionDeselection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      testUtil.click( tree._rows[ 0 ] );
      testUtil.ctrlClick( tree._rows[ 2 ] );
      tree._selectionTimestamp = null;
      testUtil.ctrlClick( tree._rows[ 2 ] );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },    
    
    testCtrlMultiSelectionSingleSelection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      var node0 = tree._clientArea._getTargetNode().childNodes[ 0 ];
      var node1 = tree._clientArea._getTargetNode().childNodes[ 1 ];
      var node2 = tree._clientArea._getTargetNode().childNodes[ 2 ];
      var left = qx.event.type.MouseEvent.buttons.left;
      testUtil.click( tree._rows[ 0 ] );
      testUtil.ctrlClick( tree._rows[ 2 ] );
      testUtil.click( tree._rows[ 0 ] );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },    
    
    testShiftMultiSelection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      testUtil.click( tree._rows[ 0 ] );
      testUtil.ctrlClick( tree._rows[ 0 ] );
      testUtil.shiftClick( tree._rows[ 2 ] );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },
    
    testShiftMultiSelectionWithoutFocusItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      testUtil.shiftClick( tree._rows[ 1 ] );
      assertFalse( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },
    
    testShiftMultiSelectionChangedFocus : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      tree.setFocusItem( item2 );
      testUtil.shiftClick( tree._rows[ 1 ] );
      assertFalse( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelectionModify : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      testUtil.click( tree._rows[ 0 ] );
      testUtil.shiftClick( tree._rows[ 2 ] );
      testUtil.shiftClick( tree._rows[ 1 ] );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelectionTwice : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      testUtil.click( tree._rows[ 0 ] );
      testUtil.shiftClick( tree._rows[ 2 ] );
      testUtil.shiftClick( tree._rows[ 1 ] );
      testUtil.click( tree._rows[ 2 ] );
      testUtil.shiftClick( tree._rows[ 0 ] );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelectionBackwards : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      testUtil.click( tree._rows[ 2 ] );
      testUtil.shiftClick( tree._rows[ 0 ] );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelectionDeselect : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item3 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item4 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      testUtil.ctrlClick( tree._rows[ 2 ] );
      testUtil.shiftClick( tree._rows[ 4 ] );
      assertFalse( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      assertTrue( tree.isItemSelected( item3 ) );
      assertTrue( tree.isItemSelected( item4 ) );
      tree.destroy();
    },

    testMultiSelectionCombination : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item3 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item4 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      var node0 = tree._clientArea._getTargetNode().childNodes[ 0 ];
      var node1 = tree._clientArea._getTargetNode().childNodes[ 1 ];
      var node2 = tree._clientArea._getTargetNode().childNodes[ 2 ];
      var node3 = tree._clientArea._getTargetNode().childNodes[ 3 ];
      var node4 = tree._clientArea._getTargetNode().childNodes[ 4 ];
      var left = qx.event.type.MouseEvent.buttons.left;
      testUtil.fakeMouseEventDOM( node0, "mousedown", left, 0, 0, 0 );
      testUtil.fakeMouseEventDOM( node2, "mousedown", left, 0, 0, 2 );
      testUtil.fakeMouseEventDOM( node4, "mousedown", left, 0, 0, 3 );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      assertFalse( tree.isItemSelected( item3 ) );
      assertTrue( tree.isItemSelected( item4 ) );
      tree.destroy();
    },

    testMultiSelectionRightClick : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      testUtil.click( tree._rows[ 0 ] );
      testUtil.ctrlClick( tree._rows[ 0 ] );
      testUtil.shiftClick( tree._rows[ 1 ] );
      testUtil.rightClick( tree._rows[ 0 ] );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      testUtil.rightClick( tree._rows[ 2 ] );
      assertFalse( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },
    
    testSetDimensionBeforeItemHeight : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setSpace( 0, 800, 19, 500 );
      tree.setItemHeight( 16 );
      //succeeds by not crashing
    },
    
    testSetColumnCount : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      testUtil.flush();
      var nodes = tree._rows[ 0 ]._getTargetNode().childNodes;
      assertEquals( 1, nodes.length );
      tree.setColumnCount( 3 );
      testUtil.flush();
      assertEquals( 3, tree.getColumnCount() );
      assertEquals( 3, nodes.length );
      tree.destroy();
    },
    
    testHasNoColumns : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setColumnCount( 0 );
      assertEquals( 1, tree.getColumnCount() );
      assertTrue( tree.hasNoColumns() );
      tree.setColumnCount( 1 );
      assertEquals( 1, tree.getColumnCount() );
      assertFalse( tree.hasNoColumns() );
      tree.destroy();
    },
    
    testSelectionPadding : function() {
       var tree = new org.eclipse.rwt.widgets.Tree();
       tree.setSelectionPadding( 2, 4 );
       assertEquals( [ 2, 4 ], tree.getSelectionPadding() );
       tree.destroy();
    },

    testSendExpandEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      testUtil.initRequestLog();
      var tree = new org.eclipse.rwt.widgets.Tree();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1 );
      wm.add( child1, "wtest", false );
      child1.setExpanded( true );
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getMessage();
      var expected = "org.eclipse.swt.events.treeExpanded=wtest";
      assertTrue( request.indexOf( expected ) != -1 );
      wm.remove( child1 );      
      tree.destroy();
    },

    testSendCollapseEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = new org.eclipse.rwt.widgets.Tree();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1 );
      wm.add( child1, "wtest", false );
      child1.setExpanded( true );
      testUtil.initRequestLog();
      child1.setExpanded( false );
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getMessage();
      var expected = "org.eclipse.swt.events.treeCollapsed=wtest";
      assertTrue( request.indexOf( expected ) != -1 );
      wm.remove( child1 );      
      tree.destroy();
    },

    testNoSendEventDuringResponse : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      testUtil.initRequestLog();
      var tree = new org.eclipse.rwt.widgets.Tree();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1 );
      wm.add( child1, "wtest", false );
      org.eclipse.swt.EventUtil.suspendEventHandling();
      child1.setExpanded( true );
      child1.setExpanded( false );
      assertEquals( 0, testUtil.getRequestsSend() );
      org.eclipse.swt.EventUtil.resumeEventHandling();
      wm.remove( child1 );
      tree.destroy()
    },

    testSendSelectionProperty : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      wm.add( tree, "w1", true );
      wm.add( child1, "w2", false );
      wm.add( child2, "w3", false );
      testUtil.initRequestLog();
      testUtil.flush();
      testUtil.click( tree._rows[ 0 ] );
      tree._selectionTimestamp = null;
      testUtil.ctrlClick( tree._rows[ 1 ] );
      assertEquals( 0, testUtil.getRequestsSend() );
      org.eclipse.swt.Request.getInstance().send();
      var request = testUtil.getMessage();
      var expected = "w1.selection=" + encodeURIComponent( "w2,w3" );
      assertTrue( request.indexOf( expected ) != -1 );      
      tree.destroy();
    },

    testSendSelectionEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasSelectionListeners( true );
      tree.setHasMultiSelection( true )
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1 );
      wm.add( tree, "w1", true );
      wm.add( child1, "w2", false );
      testUtil.flush();
      testUtil.initRequestLog();
      testUtil.click( tree._rows[ 0 ] );
      tree._selectionTimestamp = null; 
      testUtil.ctrlClick( tree._rows[ 0 ] );
      assertEquals( 2, testUtil.getRequestsSend() );
      var log = testUtil.getRequestLog();
      var expected1 = "org.eclipse.swt.events.widgetSelected=w1";
      var expected2 = "org.eclipse.swt.events.widgetSelected.item=w2";
      assertTrue( log[ 0 ].indexOf( expected1 ) != -1 );      
      assertTrue( log[ 0 ].indexOf( expected2 ) != -1 );            
      assertTrue( log[ 1 ].indexOf( expected1 ) != -1 );      
      assertTrue( log[ 1 ].indexOf( expected2 ) != -1 );            
      tree.destroy();
    },

    testSendDefaultSelectionEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasSelectionListeners( true );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      wm.add( tree, "w1", true );
      wm.add( child1, "w2", false );
      testUtil.flush();
      testUtil.initRequestLog();
      testUtil.doubleClick( tree._rows[ 0 ] );
      assertEquals( 2, testUtil.getRequestsSend() );
      var log = testUtil.getRequestLog();
      var expected1a = "org.eclipse.swt.events.widgetSelected=w1";
      var expected1b = "org.eclipse.swt.events.widgetSelected.item=w2";
      var expected2a = "org.eclipse.swt.events.widgetDefaultSelected=w1";
      var expected2b = "org.eclipse.swt.events.widgetDefaultSelected.item=w2";
      var expected3 = "w1.selection=" + encodeURIComponent( "w2" );
      assertTrue( log[ 0 ].indexOf( expected1a ) != -1 );            
      assertTrue( log[ 0 ].indexOf( expected1b ) != -1 );            
      assertTrue( log[ 0 ].indexOf( expected2a ) == -1 );            
      assertTrue( log[ 0 ].indexOf( expected2b ) == -1 );            
      assertTrue( log[ 0 ].indexOf( expected3 ) != -1 );            
      assertTrue( log[ 1 ].indexOf( expected1a ) == -1 );            
      assertTrue( log[ 1 ].indexOf( expected1b ) == -1 );            
      assertTrue( log[ 1 ].indexOf( expected2a ) != -1 );            
      assertTrue( log[ 1 ].indexOf( expected2b ) != -1 );            
      assertTrue( log[ 1 ].indexOf( expected3 ) == -1 );            
      tree.destroy();
    },

    testDontSendDefaultSelectionEventOnDoubleRightClick : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasSelectionListeners( true );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      wm.add( tree, "w1", true );
      wm.add( child1, "w2", false );
      testUtil.flush();
      testUtil.initRequestLog();
      testUtil.rightClick( tree._rows[ 0 ] );
      testUtil.rightClick( tree._rows[ 0 ] );
      assertEquals( 2, testUtil.getRequestsSend() );
      var log = testUtil.getRequestLog();
      var expected = "org.eclipse.swt.events.widgetSelected.item=w2";
      var notExpected = "org.eclipse.swt.events.widgetDefaultSelected";
      assertTrue( log[ 0 ].indexOf( notExpected ) == -1 );
      assertTrue( log[ 1 ].indexOf( notExpected ) == -1 );
      assertTrue( log[ 0 ].indexOf( expected ) != -1 );
      assertTrue( log[ 1 ].indexOf( expected ) != -1 );
      tree.destroy();
    },
  
    testSendDefaultSelectionEventByEnter : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasSelectionListeners( true );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      wm.add( tree, "w1", true );
      wm.add( child1, "w2", false );
      testUtil.flush();
      testUtil.initRequestLog();
      var node = tree._rows[ 0 ]._getTargetNode();
      testUtil.clickDOM( node );
      testUtil.fakeKeyEventDOM( node, "keypress", 13 );
      assertEquals( 2, testUtil.getRequestsSend() );
      var log = testUtil.getRequestLog();
      var expected1a = "org.eclipse.swt.events.widgetSelected=w1";
      var expected1b = "org.eclipse.swt.events.widgetSelected.item=w2";
      var expected2a = "org.eclipse.swt.events.widgetDefaultSelected=w1";
      var expected2b = "org.eclipse.swt.events.widgetDefaultSelected.item=w2";
      var expected3 = "w1.selection=" + encodeURIComponent( "w2" );
      assertTrue( log[ 0 ].indexOf( expected1a ) != -1 );            
      assertTrue( log[ 0 ].indexOf( expected1b ) != -1 );            
      assertTrue( log[ 0 ].indexOf( expected2a ) == -1 );            
      assertTrue( log[ 0 ].indexOf( expected2b ) == -1 );            
      assertTrue( log[ 0 ].indexOf( expected3 ) != -1 );            
      assertTrue( log[ 1 ].indexOf( expected1a ) == -1 );            
      assertTrue( log[ 1 ].indexOf( expected1b ) == -1 );            
      assertTrue( log[ 1 ].indexOf( expected2a ) != -1 );            
      assertTrue( log[ 1 ].indexOf( expected2b ) != -1 );            
      assertTrue( log[ 1 ].indexOf( expected3 ) == -1 );            
      tree.destroy();
    },

    testSendDefaultSelectionEventByEnterChangedFocus : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasSelectionListeners( true );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      wm.add( tree, "w1", true );
      wm.add( child1, "w2", false );
      wm.add( child2, "w3", false );
      tree.focus();
      testUtil.flush();
      testUtil.initRequestLog();
      tree.setFocusItem( child2 );
      testUtil.fakeKeyEventDOM( tree._getTargetNode(), "keypress", 13 );
      assertEquals( 1, testUtil.getRequestsSend() );
      var message = testUtil.getMessage();
      var expected1a = "org.eclipse.swt.events.widgetDefaultSelected=w1";
      var expected1b = "org.eclipse.swt.events.widgetDefaultSelected.item=w3";
      assertTrue( message.indexOf( expected1a ) != -1 );            
      assertTrue( message.indexOf( expected1b ) != -1 );            
      tree.destroy();
    },
    
    testDontSendDefaultSelectionEventOnFastClick : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasSelectionListeners( true );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      wm.add( tree, "w1", true );
      wm.add( child1, "w2", false );
      wm.add( child2, "w3", false );
      testUtil.flush();
      testUtil.initRequestLog();
      testUtil.click( tree._rows[ 0 ] );
      testUtil.click( tree._rows[ 1 ] );
      assertEquals( 2, testUtil.getRequestsSend() );
      var log = testUtil.getRequestLog();
      var expected1 = "org.eclipse.swt.events.widgetSelected.item=w2";
      var expected2 = "org.eclipse.swt.events.widgetSelected.item=w3";
      var notExpected = "org.eclipse.swt.events.widgetDefaultSelected";
      assertTrue( log[ 0 ].indexOf( notExpected ) == -1 );            
      assertTrue( log[ 1 ].indexOf( notExpected ) == -1 );            
      assertTrue( log[ 0 ].indexOf( expected1 ) != -1 );            
      assertTrue( log[ 1 ].indexOf( expected2 ) != -1 );            
      tree.destroy();
    },

    testMultiSelectionEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasSelectionListeners( true );
      tree.setHasMultiSelection( true );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child3 = new org.eclipse.rwt.widgets.TreeItem( tree );
      wm.add( tree, "w1", true );
      wm.add( child1, "w2", false );
      wm.add( child2, "w3", false );
      wm.add( child3, "w4", false );
      testUtil.flush();
      testUtil.initRequestLog();
      testUtil.click( tree._rows[ 0 ] );
      tree._selectionTimestamp = null;
      testUtil.shiftClick( tree._rows[ 2 ] );
      assertEquals( 2, testUtil.getRequestsSend() );
      var log = testUtil.getRequestLog();
      var expected1a = "org.eclipse.swt.events.widgetSelected=w1";
      var expected1b = "org.eclipse.swt.events.widgetSelected.item=w2";
      var expected1c = "w1.selection=" + encodeURIComponent( "w2" );
      var expected2a = "org.eclipse.swt.events.widgetSelected.item=w4";
      var expected2b = "w1.selection=" + encodeURIComponent( "w2,w3,w4" );
      var notExpected = "DefaultSelected";
      assertTrue( log.join().indexOf( notExpected ) == -1 );            
      assertTrue( log[ 0 ].indexOf( expected1a ) != -1 );      
      assertTrue( log[ 0 ].indexOf( expected1b ) != -1 );      
      assertTrue( log[ 0 ].indexOf( expected1c ) != -1 );      
      assertTrue( log[ 1 ].indexOf( expected1a ) != -1 );      
      assertTrue( log[ 1 ].indexOf( expected2a ) != -1 );      
      assertTrue( log[ 1 ].indexOf( expected2b ) != -1 );      
      tree.destroy();
    },

    testRenderOnFocus : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var child = new org.eclipse.rwt.widgets.TreeItem( tree );
      tree.focus();
      testUtil.flush();
      assertFalse( tree._rows[ 0 ].hasState( "parent_unfocused" ) );
      tree.blur();
      testUtil.flush();
      assertTrue( tree._rows[ 0 ].hasState( "parent_unfocused" ) );
      tree.destroy();
    },

    testSetBackgroundColor : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setBackgroundColor( "red" );
      assertEquals( "red", tree._clientArea.getBackgroundColor() );
      tree.destroy();
    },

    testIsHoverItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      testUtil.mouseOver( tree._rows[ 0 ] );
      assertTrue( tree.isHoverItem( item ) );
      tree.destroy();
    },

    testIsHoverElement : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "bla" ] );
      item.setImages( [ "bla.jpg" ] );
      assertFalse( tree.isHoverElement( null ) );
      testUtil.flush();
      var rowNode = tree._rows[ 0 ]._getTargetNode();
      testUtil.hoverFromTo( document.body, rowNode );
      assertFalse( tree.isHoverElement( rowNode.firstChild ) );
      assertFalse( tree.isHoverElement( rowNode.lastChild ) );
      testUtil.hoverFromTo( rowNode, rowNode.firstChild );
      assertTrue( tree.isHoverElement( rowNode.firstChild ) );
      assertFalse( tree.isHoverElement( rowNode.lastChild ) );
      tree.destroy();
    },

    testRenderOnItemHover : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      testUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            itemBackground : states.over ? "red" : "green"
          }
        }
      } );
      new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      var style = tree._rows[ 0 ]._getTargetNode().style;
      assertEquals( "green", style.backgroundColor );
      testUtil.mouseOver( tree._rows[ 0 ] );
      assertEquals( "red", style.backgroundColor );
      testUtil.mouseOut( tree._rows[ 0 ] );
      assertEquals( "green", style.backgroundColor );
      tree.destroy();
    },

    testRenderOnCheckBoxHover : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasCheckBoxes( true );
      tree.setCheckBoxMetrics( 5, 5 );
      testUtil.fakeAppearance( "tree-check-box",  {
        style : function( states ) {
          return {
            "backgroundImage" : states.over ? "over.gif" : "normal.gif"
          }
        }
      } );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush()
      var rowNode = tree._rows[ 0 ]._getTargetNode();
      testUtil.hoverFromTo( document.body, rowNode );
      var normal = testUtil.getCssBackgroundImage( rowNode.firstChild );
      testUtil.hoverFromTo( rowNode, rowNode.firstChild );
      var over = testUtil.getCssBackgroundImage( rowNode.firstChild );
      testUtil.hoverFromTo( rowNode.firstChild, rowNode );
      var normalAgain = testUtil.getCssBackgroundImage( rowNode.firstChild );
      assertTrue( normal.indexOf( "normal.gif" ) != -1 );
      assertTrue( over.indexOf( "over.gif" ) != -1 );
      assertTrue( normalAgain.indexOf( "normal.gif" ) != -1 );
      tree.destroy();
    },

    testRenderOnCheckBoxHoverSkip : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasCheckBoxes( true );
      tree.setCheckBoxMetrics( 5, 5 );
      testUtil.fakeAppearance( "tree-check-box",  {
        style : function( states ) {
          return {
            "backgroundImage" : states.over ? "over.gif" : "normal.gif"
          }
        }
      } );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush()
      var rowNode1 = tree._rows[ 0 ]._getTargetNode();
      var rowNode2 = tree._rows[ 1 ]._getTargetNode();
      testUtil.hoverFromTo( document.body, rowNode1.firstChild );
      var check1 = testUtil.getCssBackgroundImage( rowNode1.firstChild );
      var check2 = testUtil.getCssBackgroundImage( rowNode2.firstChild );
      assertTrue( check1.indexOf( "over.gif" ) != -1 );
      assertTrue( check2.indexOf( "normal.gif" ) != -1 );
      testUtil.hoverFromTo( rowNode1.firstChild, rowNode2.firstChild );
      check1 = testUtil.getCssBackgroundImage( rowNode1.firstChild );
      check2 = testUtil.getCssBackgroundImage( rowNode2.firstChild );
      assertTrue( check1.indexOf( "normal.gif" ) != -1 );
      assertTrue( check2.indexOf( "over.gif" ) != -1 );
      tree.destroy();
    },

    testRenderOnExpandSymbolHover : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      testUtil.fakeAppearance( "tree-indent",  {
        style : function( states ) {
        	var result = null;
        	if( !states.line ) {
        		result = states.over ? "over.gif" : "normal.gif";
        	}
          return {
            "backgroundImage" : result
          }
        }
      } );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush()
      var rowNode = tree._rows[ 0 ]._getTargetNode();
      testUtil.hoverFromTo( document.body, rowNode );
      var normal = testUtil.getCssBackgroundImage( rowNode.firstChild );
      testUtil.hoverFromTo( rowNode, rowNode.firstChild );
      var over = testUtil.getCssBackgroundImage( rowNode.firstChild );
      testUtil.hoverFromTo( rowNode.firstChild, rowNode );
      var normalAgain = testUtil.getCssBackgroundImage( rowNode.firstChild );
      assertTrue( normal.indexOf( "normal.gif" ) != -1 );
      assertTrue( over.indexOf( "over.gif" ) != -1 );
      assertTrue( normalAgain.indexOf( "normal.gif" ) != -1 );
      tree.destroy();
    },

    testSendTopItemIndex : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      wm.add( tree, "w1", false );
      for( var i = 0; i < 100; i ++ ) {
        new org.eclipse.rwt.widgets.TreeItem( tree );
      }
      testUtil.initRequestLog();
      testUtil.flush();
      tree._vertScrollBar.setValue( 160 );
      assertEquals( 0, testUtil.getRequestsSend() );
      org.eclipse.swt.Request.getInstance().send();
      var request = testUtil.getMessage();
      var expected = "w1.topItemIndex=8";
      assertTrue( request.indexOf( expected ) != -1 );      
      tree.destroy();
    },

    testScrollWidth : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 3, 500, 700, 0, 0, 0, 500 );
      assertEquals( 500, tree._horzScrollBar.getMaximum() );
      tree.setColumnCount( 4 );
      assertEquals( 1200, tree._horzScrollBar.getMaximum() );
      tree.setColumnCount( 3 );
      assertEquals( 500, tree._horzScrollBar.getMaximum() );      
      tree.setItemMetrics( 2, 500, 600, 0, 0, 0, 500 );
      assertEquals( 1100, tree._horzScrollBar.getMaximum() );
      tree.destroy();
    },

    testScrollHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 2, 500, 600, 0, 0, 0, 500 );
      tree.setColumnCount( 3 );
      testUtil.flush();
      tree._horzScrollBar.setValue( 400 );
      assertEquals( 400, tree._clientArea.getScrollLeft() );
      tree.destroy();
    },

    testShowColumnHeader : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      this._fakeAppearance();
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      testUtil.flush();
      var areaNode = tree._clientArea.getElement();
      assertEquals( 30, parseInt( areaNode.style.top ) );
      assertEquals( 470, parseInt( areaNode.style.height ) );
      assertEquals( 600, parseInt( areaNode.style.width ) );
      var headerNode = tree._columnArea.getElement();
      assertEquals( 0, parseInt( headerNode.style.top ) );
      assertEquals( 30, parseInt( headerNode.style.height ) );
      assertEquals( 600, parseInt( headerNode.style.width ) );
      tree.destroy();
    },

    testShowColumnHeaderWithScrollbars : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      this._fakeAppearance();
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setScrollBarsVisible( true, true );
      tree.setHeight( 500 );
      tree.setWidth( 600 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      testUtil.flush();
      var horizontal 
        = testUtil.getElementBounds( tree._horzScrollBar.getElement() );
      var vertical 
        = testUtil.getElementBounds( tree._vertScrollBar.getElement() );      
      var headerNode = tree._columnArea.getElement();
      assertEquals( 600, parseInt( headerNode.style.width ) );
      var areaNode = tree._clientArea.getElement();
      var areaHeight = 470 - horizontal.height;
      assertEquals( areaHeight, parseInt( areaNode.style.height ) );
      assertEquals( areaHeight, vertical.height );
      assertEquals( 30, vertical.top );
      assertEquals( areaHeight + 30, horizontal.top );
      tree.destroy();
    },

    testCreateTreeColumn : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var column = new org.eclipse.swt.widgets.TableColumn( tree );
      testUtil.flush();
      assertEquals( tree._columnArea, column.getParent() );
      assertEquals( "tree-column", column.getAppearance() );
      assertEquals( "100%", column.getHeight() );
      tree.destroy();
    },

    testShowDummyColumn : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var column = new org.eclipse.swt.widgets.TableColumn( tree );
      column.setLeft( 0 );
      column.setWidth( 500 );
      tree.setWidth( 600 );
      tree.setHeaderVisible( true );
      testUtil.flush();
      var dummy = tree._dummyColumn;
      assertEquals( tree._columnArea, column.getParent() );
      assertTrue( dummy.getVisibility() );
      assertTrue( dummy.hasState( "dummy" ) );
      // Fix for IEs DIV-height bug (322802):
      assertEquals( "&nbsp;", dummy.getLabel() );
      assertEquals( 500, dummy.getLeft() );
      assertEquals( 100, dummy.getWidth() );
      tree.destroy();
    },

    testDontShowDummyColumn : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var column = new org.eclipse.swt.widgets.TableColumn( tree );
      column.setLeft( 0 );
      column.setWidth( 500 );
      tree.setHeaderVisible( true );
      tree.setWidth( 490 );
      testUtil.flush();
      var dummy = tree._dummyColumn;
      assertEquals( tree._columnArea, dummy.getParent() );
      assertEquals( 0, dummy.getWidth() );
      assertTrue( dummy.hasState( "dummy" ) );
      tree.destroy();
    },

    testShowMinimalDummyColumn : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( true, true );
      var column = new org.eclipse.swt.widgets.TableColumn( tree );
      column.setLeft( 0 );
      column.setWidth( 500 );
      tree.setHeaderVisible( true );
      tree.setWidth( 450 );
      testUtil.flush();
      var barWidth = tree._vertScrollBar.getWidth();
      var dummy = tree._dummyColumn;
      assertTrue( dummy.getVisibility() );
      assertEquals( 500, dummy.getLeft() );
      assertEquals( barWidth, dummy.getWidth() );
      tree.destroy();
    },

    testOnlyShowDummyColumn : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHeaderVisible( true );
      tree.setScrollBarsVisible( true, true );
      testUtil.flush();
      var dummy = tree._dummyColumn;
      assertTrue( dummy.getVisibility() );
      assertEquals( 0, dummy.getLeft() );
      assertEquals( 500, dummy.getWidth() );
      assertTrue( dummy.hasState( "dummy" ) );
      tree.destroy();
    },

    testReLayoutDummyColumn : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var column = new org.eclipse.swt.widgets.TableColumn( tree );
      column.setLeft( 0 );
      column.setWidth( 500 );
      tree.setWidth( 600 );
      tree.setHeaderVisible( true );
      testUtil.flush();
      var dummy = tree._dummyColumn;
      assertEquals( 500, dummy.getLeft() );
      assertEquals( 100, dummy.getWidth() );
      column.setWidth( 400 );
      assertEquals( 400, dummy.getLeft() );
      assertEquals( 200, dummy.getWidth() );
      column.setLeft( 50 );
      assertEquals( 450, dummy.getLeft() );
      assertEquals( 150, dummy.getWidth() );
      tree.destroy();
    },

    testScrollHeaderHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 2, 500, 600, 0, 0, 0, 500 );
      tree.setColumnCount( 3 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      var columnX = new org.eclipse.swt.widgets.TableColumn( tree );
      columnX.setLeft( 0 );
      columnX.setWidth( 1100 );
      testUtil.flush();
      tree._horzScrollBar.setValue( 400 );
      assertEquals( 400, tree._columnArea.getScrollLeft() );
      tree.destroy();
    },
    
    testChangeTreeTextColor : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setTextColor( "red" );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "Test1" ] );
      testUtil.flush();
      var row = tree._rows[ 0 ];
      var node = row._getTargetNode().childNodes[ 0 ];      
      assertEquals( "red", node.style.color );
      tree.setTextColor( "blue" );
      testUtil.flush();
      assertEquals( "blue", node.style.color );
      tree.destroy();
    },

    changeTreeFont : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setFont( new qx.ui.core.Font( 12, [ "monospace" ] ) );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "Test1" ] );
      testUtil.flush();
      var row = tree._rows[ 0 ];
      var node = row._getTargetNode().childNodes[ 0 ];
      var font = testUtil.getElementFont( node );
      assertTrue( font.indexOf( "monospace" ) != -1 );
      tree.setFont( new qx.ui.core.Font( 12, [ "fantasy" ] ) );
      testUtil.flush();
      assertTrue( font.indexOf( "fantasy" ) != -1 );
      tree.destroy();
      row.destroy();
    },

    testDisposeTreeColumn : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var column = new org.eclipse.swt.widgets.TableColumn( tree );
      testUtil.flush();
      column.destroy();
      assertEquals( 1, tree._columnArea.getChildren().length );
      tree.destroy();
    },
    
    testChangeItemMetrics : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setTreeColumn( 1 );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "Test1" ] );
      testUtil.flush();
      tree.setItemMetrics( 0, 0, 500, 0, 0, 30, 500 );
      testUtil.flush();
      var node = tree._rows[ 0 ]._getTargetNode().firstChild;
      assertEquals( 30, parseInt( node.style.left ) );
      tree.destroy();
    },
    
    testMoveColumn : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHeaderVisible( true );
      var column = new org.eclipse.swt.widgets.TableColumn( tree );
      var child = new org.eclipse.rwt.widgets.TreeItem( tree );
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.add( column, "w1", false );
      column.setLeft( 100 );
      column.setWidth( 100 );
      column.setMoveable( true );
      testUtil.flush();
      testUtil.initRequestLog();
      var left = qx.event.type.MouseEvent.buttons.left;
      var node = column._getTargetNode();
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 5, 0 );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 5, 0 );
      var expected = "rg.eclipse.swt.events.controlMoved=w1";
      assertTrue( testUtil.getMessage().indexOf( expected ) != -1 );
      tree.destroy();      
    },
    
    testResizeColumn : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHeaderVisible( true );
      var column = new org.eclipse.swt.widgets.TableColumn( tree );
      var child = new org.eclipse.rwt.widgets.TreeItem( tree );
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      wm.add( column, "w1", false );
      column.setLeft( 100 );
      column.setWidth( 100 );
      column.setMoveable( true );
      testUtil.flush();
      testUtil.initRequestLog();
      var left = qx.event.type.MouseEvent.buttons.left;
      var node = column._getTargetNode();
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 200, 0 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 205, 0 );
      assertEquals( "table-column-resizer", tree._resizeLine.getAppearance() );
      var line = tree._resizeLine._getTargetNode();
      assertIdentical( tree._getTargetNode(), line.parentNode );
      assertEquals( 203, parseInt( line.style.left ) );
      assertEquals( "", tree._resizeLine.getStyleProperty( "visibility" ) );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 205, 0 );
      var expected = "rg.eclipse.swt.events.controlResized=w1";
      assertTrue( testUtil.getMessage().indexOf( expected ) != -1 );
      assertEquals( "hidden", tree._resizeLine.getStyleProperty( "visibility" ) );      
      tree.destroy();      
    },
    
    testSetAlignment : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setAlignment( 0, "left" );
      tree.setAlignment( 1, "center" );
      tree.setAlignment( 2, "right" );
      assertEquals( "left", tree.getAlignment( 0 ) );
      assertEquals( "center", tree.getAlignment( 1 ) );
      assertEquals( "right", tree.getAlignment( 2 ) );
      tree.destroy();
    },

    testRenderAlignmentChange : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setAlignment( 0, "right" );
      tree.setTreeColumn( 1 );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      item.setTexts( [ "Test1" ] );
      testUtil.flush();
      var row = tree._rows[ 0 ];
      var node = row._getTargetNode().childNodes[ 0 ];
      assertEquals( "right", node.style.textAlign );
      tree.setAlignment( 0, "center" );
      testUtil.flush();
      assertEquals( "center", node.style.textAlign );      
      tree.destroy();
      row.destroy();    
    },

    testSendScrollLeft : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      wm.add( tree, "w1", false );
      new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.initRequestLog();
      testUtil.flush();
      tree._horzScrollBar.setValue( 160 );
      assertEquals( 0, testUtil.getRequestsSend() );
      org.eclipse.swt.Request.getInstance().send();
      var request = testUtil.getMessage();
      var expected = "w1.scrollLeft=160";
      assertTrue( request.indexOf( expected ) != -1 );      
      tree.destroy();
    },

    testSetScrollLeft : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      testUtil.flush();
      tree.setScrollLeft( 160 );
      assertEquals( 160, tree._horzScrollBar.getValue() );      
      tree.destroy();
    },
    
    testSetScrollLeftBeforeAppear : function() {
      // See Bug 325091 (also the next 3 tests)
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      var columnX = new org.eclipse.swt.widgets.TableColumn( tree );
      columnX.setLeft( 0 );
      columnX.setWidth( 1100 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      testUtil.flush();
      tree.hide();
      testUtil.flush();
      tree.setScrollLeft( 160 );
      tree.show();
      assertEquals( 160, tree._horzScrollBar.getValue() );
      assertEquals( 160, tree._clientArea.getScrollLeft() );      
      assertEquals( 160, tree._columnArea.getScrollLeft() );      
      tree.destroy();
    },

      //NOTE: This next test would fail in IE. For some reason, under this very
      //specific set of circumstances, the scrollWidth of the clientArea element
      //will not be updated by IE, and setting scrollLeft fails. (This can be
      //fixed by setting the width of one of the children to 0 and back to its
      //original value.) But since i was unable to reproduce this problem 
      //in an actual RAP application, i will comment this test for now.
      //Also, see Bug 325091.

//    testSetScrollLeftBeforeAppearIEbug : function() {
//      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
//      var tree = this._createDefaultTree();
//      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
//      testUtil.flush();
//      tree.hide();
//      testUtil.flush();
//      tree.setScrollLeft( 160 );
//      tree.show();
//      assertEquals( 160, tree._horzScrollBar.getValue() );
//      assertEquals( 160, tree._clientArea.getScrollLeft() );      
//      tree.destroy();
//    },

    testSetScrollLeftBeforeCreate : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree( true );
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      var columnX = new org.eclipse.swt.widgets.TableColumn( tree );
      columnX.setLeft( 0 );
      columnX.setWidth( 1100 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      tree.setScrollLeft( 160 );
      testUtil.flush();
      assertEquals( 160, tree._horzScrollBar.getValue() );      
      assertEquals( 160, tree._clientArea.getScrollLeft() );      
      assertEquals( 160, tree._columnArea.getScrollLeft() );      
      tree.destroy();
    },

    testSetScrollBeforeColumnHeaderVisible: function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      var columnX = new org.eclipse.swt.widgets.TableColumn( tree );
      columnX.setLeft( 0 );
      columnX.setWidth( 1100 );
      testUtil.flush();
      tree.setScrollLeft( 160 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      testUtil.flush();
      assertEquals( 160, tree._columnArea.getScrollLeft() );      
      tree.destroy();
    },

    testRenderOnItemGrayed : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasCheckBoxes( true );
      tree.setCheckBoxMetrics( 5, 5 );
      testUtil.fakeAppearance( "tree-check-box",  {
        style : function( states ) {
          return {
            "backgroundImage" : states.grayed ? "grayed.gif" : "normal.gif"
          }
        }
      } );
      var item = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      var node = tree._rows[ 0 ]._getTargetNode().firstChild;
      var normal = testUtil.getCssBackgroundImage( node );
      item.setGrayed( true );
      var grayed = testUtil.getCssBackgroundImage( node );
      assertTrue( normal.indexOf( "normal.gif" ) != -1 );
      assertTrue( grayed.indexOf( "grayed.gif" ) != -1 );
      tree.destroy();
    },
    
    testRenderBackgroundImage : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setBackgroundImage( "bla.jpg" );
      assertEquals( "bla.jpg", tree._clientArea.getBackgroundImage() );
      tree.destroy();
    },
    
    testGetStatesCopy : function() {
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.addState( "bla" );
      tree.addState( "blub" );
      var copy = tree.getStatesCopy();
      assertTrue( copy.bla );
      assertTrue( copy.blub );
      tree.destroy();
    },
    
    testGridLinesState : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree( true );
      tree.setLinesVisible( true );
      testUtil.flush();
      var row = tree._rows[ 0 ];
      assertTrue( tree.hasState( "linesvisible" ) );
      assertTrue( row.hasState( "linesvisible" ) );
      tree.setLinesVisible( false );
      assertFalse( tree.hasState( "linesvisible" ) );
      assertFalse( row.hasState( "linesvisible" ) );
      tree.destroy();
    },
    
    testGridLinesHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var row = tree._rows[ 0 ];
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
      var row = tree._rows[ 0 ];
      assertIdentical( border, row.getBorder() );
      tree.destroy();
    },
    
    testCreateGridLinesVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setColumnCount( 3 );
      assertEquals( 4, tree.getChildren().length ); // clientArea, header, scrollbars      
      tree.setLinesVisible( true );
      testUtil.flush();
      assertEquals( 7, tree.getChildren().length );
      assertTrue( tree.getChildren()[ 4 ] instanceof qx.ui.basic.Terminator );
      assertTrue( tree.getChildren()[ 5 ] instanceof qx.ui.basic.Terminator );
      assertTrue( tree.getChildren()[ 6 ] instanceof qx.ui.basic.Terminator );
      tree.destroy();
    },
    
    testGridLinesVerticalDefaultProperties : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setColumnCount( 3 );
      tree.setLinesVisible( true );
      testUtil.flush();
      var line = tree.getChildren()[ 4 ];
      assertNotNull( line.getBorder() );
      assertEquals( 1, line.getZIndex() );
      assertEquals( 0, line.getWidth() );
      tree.destroy();
    },
    
    testAddGridLinesVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setLinesVisible( true );
      tree.setColumnCount( 1 );
      testUtil.flush();
      assertEquals( 5, tree.getChildren().length );       
      tree.setColumnCount( 3 );
      testUtil.flush();
      assertEquals( 7, tree.getChildren().length );       
      tree.destroy();
    },
    
    testRemoveGridLinesVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setLinesVisible( true );
      tree.setColumnCount( 3 );
      testUtil.flush();
      assertEquals( 7, tree.getChildren().length );       
      tree.setColumnCount( 1 );
      testUtil.flush();
      assertEquals( 5, tree.getChildren().length );       
      tree.destroy();
    },
    
    testDisableLinesVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setLinesVisible( true );
      tree.setColumnCount( 3 );
      testUtil.flush();
      assertEquals( 7, tree.getChildren().length );       
      tree.setLinesVisible( false );
      testUtil.flush();
      assertEquals( 4, tree.getChildren().length );       
      tree.destroy();
    },
    
    testGridLinesVerticalLayoutY : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setWidth( 1000 );
      tree.setColumnCount( 3 );
      tree.setLinesVisible( true );
      var line = tree.getChildren()[ 4 ];
      assertEquals( 0, line.getTop() );
      assertEquals( 500, line.getHeight() );
      tree.setHeaderHeight( 20 );
      tree.setHeaderVisible( true );
      assertEquals( 20, line.getTop() );
      assertEquals( 480, line.getHeight() );
      if( !testUtil.isMobileWebkit() ) {
	      tree.setScrollBarsVisible( true, true );
	      assertEquals( 20, line.getTop() );
	      assertTrue( line.getHeight() < 480 );
      }      
      tree.destroy();
    },
    
    testGridLinesVerticalPositionX : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setColumnCount( 3 );
      tree.setLinesVisible( true );
      tree.setItemMetrics( 0, 0, 202, 0, 0, 0, 400 );
      tree.setItemMetrics( 1, 205, 100, 0, 0, 0, 400 );
      tree.setItemMetrics( 2, 310, 50, 0, 0, 0, 400 );
      var line1 = tree.getChildren()[ 4 ];
      var line2 = tree.getChildren()[ 5 ];
      var line3 = tree.getChildren()[ 6 ];
      assertEquals( 201, line1.getLeft() );
      assertEquals( 304, line2.getLeft() );
      assertEquals( 359, line3.getLeft() );
      tree.destroy();
    },
    
    testGridLinesVerticalPositionXScrolled : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setWidth( 170 );
      tree.setColumnCount( 3 );
      tree.setLinesVisible( true );
      tree.setItemMetrics( 0, 0, 202, 0, 0, 0, 400 );
      testUtil.flush();
      tree.setScrollLeft( 40 );
      var line1 = tree.getChildren()[ 4 ];
      testUtil.flush();
      assertEquals( 161, line1.getLeft() );
      tree.destroy();
    },
    
    testGridLinesVerticalPositionXScrolledOut : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setWidth( 170 );
      tree.setColumnCount( 3 );
      tree.setLinesVisible( true );
      tree.setItemMetrics( 0, 0, 30, 0, 0, 0, 400 );
      testUtil.flush();
      tree.setScrollLeft( 40 );
      testUtil.flush();
      assertEquals( 4, tree.getChildren().length );
      tree.destroy();
    },
    
    testGridLinesScrolledOutChangedColumnOrder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setWidth( 170 );
      tree.setColumnCount( 3 );
      tree.setLinesVisible( true );
      tree.setItemMetrics( 0, 0, 30, 0, 0, 0, 400 );
      tree.setItemMetrics( 1, 200, 30, 0, 0, 0, 400 );
      tree.setItemMetrics( 2, 40, 30, 0, 0, 0, 400 );
      testUtil.flush();
      var line1 = tree.getChildren()[ 4 ];
      var line2 = tree.getChildren()[ 5 ];
      assertEquals( 29, line1.getLeft() )
      assertEquals( 69, line2.getLeft() )
      testUtil.flush();
      tree.destroy();
    },
    
    testGridLinesVerticalOverflow : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( true, true );
      tree.setLinesVisible( true );
      tree.setItemMetrics( 0, 0, 202, 0, 0, 0, 400 );
      tree.setItemMetrics( 1, 205, 100, 0, 0, 0, 400 );
      tree.setItemMetrics( 2, 310, 50, 0, 0, 0, 400 );
      tree.setItemMetrics( 3, 360, 11, 0, 0, 0, 400 );
      tree.setColumnCount( 4 );
      tree.setWidth( 370 );
      testUtil.flush();
      var expected = 6;
      if( testUtil.isMobileWebkit() ) {
      	expected += 1; // No scrollbars => bigger client-area
      }
      assertEquals( expected, tree.getChildren().length );
      tree.destroy();
    },
    
    testRedrawOnShiftMultiSelection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      testUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.itemBackground = "blue";
          } else {
            result.itemBackground = "white";
          }
          return result;
        }
      } );  
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.flush();
      var rows = tree._rows;
      testUtil.click( rows[ 0 ] );
      testUtil.shiftClick( rows[ 2 ] );
      assertEquals( "blue", rows[ 0 ].getElement().style.backgroundColor );
      assertEquals( "blue", rows[ 1 ].getElement().style.backgroundColor );
      assertEquals( "blue", rows[ 2 ].getElement().style.backgroundColor );
      tree.destroy();
    },    

    testSetIsVirtual: function() {
      var tree = this._createDefaultTree();
      assertFalse( tree.getIsVirtual() );
      tree.setIsVirtual( true );
      assertTrue( tree.getIsVirtual() );
      tree.destroy();
    },
    
    testVirtualSendTopItemIndex : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setIsVirtual( true );
      wm.add( tree, "w1", false );
      for( var i = 0; i < 100; i ++ ) {
        new org.eclipse.rwt.widgets.TreeItem( tree );
      }
      testUtil.initRequestLog();
      testUtil.flush();
      tree._vertScrollBar.setValue( 50 );
      tree._vertScrollBar.setValue( 160 );
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.forceInterval( tree._sendRequestTimer );
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getMessage();
      var expected = "w1.topItemIndex=8";
      assertTrue( request.indexOf( expected ) != -1 );      
      tree.destroy();
    },

    testVirtualSendScrollLeft : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setIsVirtual( true );
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      wm.add( tree, "w1", false );
      new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.initRequestLog();
      testUtil.flush();
      tree._horzScrollBar.setValue( 50 );
      tree._horzScrollBar.setValue( 160 );
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.forceInterval( tree._sendRequestTimer );
      assertEquals( 1, testUtil.getRequestsSend() );
      var request = testUtil.getMessage();
      var expected = "w1.scrollLeft=160";
      assertTrue( request.indexOf( expected ) != -1 );      
      tree.destroy();
    },

    testCancelTimerOnRequest: function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setIsVirtual( true );
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.initRequestLog();
      testUtil.flush();
      tree._horzScrollBar.setValue( 160 );      
      assertEquals( 0, testUtil.getRequestsSend() );
      org.eclipse.swt.Request.getInstance().send();
      assertFalse( tree._sendRequestTimer.getEnabled() );
      assertEquals( 1, testUtil.getRequestsSend() );
      tree.destroy();
    },
    
    testKeyboardNavigationUpDown : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      item0.setExpanded( true );
      testUtil.clickDOM( tree._rows[ 2 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( item2 ) );
      assertTrue( tree.isFocusItem( item2 ) );
      testUtil.press( tree, "Up" );
      testUtil.press( tree, "Up" );
      assertTrue( tree.isFocusItem( item0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      testUtil.press( tree, "Down" );
      assertTrue( tree.isFocusItem( item1 ) );
      assertFalse( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },
    
    testKeyboardNavigationRight : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( item1 );
      testUtil.clickDOM( tree._rows[ 0 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      assertFalse( item0.isExpanded() );
      testUtil.press( tree, "Right" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      assertTrue( item0.isExpanded() );
      testUtil.press( tree, "Right" );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isFocusItem( item1 ) );
      assertFalse( item1.isExpanded() );
      testUtil.press( tree, "Right" );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isFocusItem( item1 ) );
      assertTrue( item1.isExpanded() );
      testUtil.press( tree, "Right" );
      assertTrue( tree.isItemSelected( item2 ) );
      assertTrue( tree.isFocusItem( item2 ) );
      assertFalse( item2.isExpanded() );
      testUtil.press( tree, "Right" );
      assertTrue( tree.isItemSelected( item2 ) );
      assertTrue( tree.isFocusItem( item2 ) );
      assertFalse( item2.isExpanded() );
      tree.destroy();
    },

    testKeyboardNavigationLeft : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( item1 );
      item0.setExpanded( true );
      item1.setExpanded( true );
      testUtil.flush();
      testUtil.clickDOM( tree._rows[ 2 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( item2 ) );
      assertTrue( tree.isFocusItem( item2 ) );
      testUtil.press( tree, "Left" );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isFocusItem( item1 ) );
      assertTrue( item1.isExpanded() );
      testUtil.press( tree, "Left" );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isFocusItem( item1 ) );
      assertFalse( item1.isExpanded() );
      testUtil.press( tree, "Left" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      assertTrue( item0.isExpanded() );
      testUtil.press( tree, "Left" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      assertFalse( item0.isExpanded() );
      testUtil.press( tree, "Left" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      assertFalse( item0.isExpanded() );
      tree.destroy();
    },

    testKeyboardNavigationOnlyOneItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      testUtil.clickDOM( tree._rows[ 0 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      testUtil.press( tree, "Up" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      testUtil.press( tree, "Down" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      tree.destroy();
    },

    testKeyboardNavigationScrollDown : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Item" + i ] );
      }
      var root = tree.getRootItem();
      testUtil.flush();
      assertEquals( 25, tree._rows.length );
      assertIdentical( root.getChild( 0 ), tree._getTopItem() );
      testUtil.clickDOM( tree._rows[ 22 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( root.getChild( 22 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 22 ) ) );
      testUtil.press( tree, "Down" );
      assertIdentical( root.getChild( 0 ), tree._getTopItem() );
      testUtil.press( tree, "Down" );
      assertIdentical( root.getChild( 1 ), tree._getTopItem() );
      tree.destroy();
    },

    testKeyboardNavigationScrollUp : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Item" + i ] );
      }
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      testUtil.flush();
      assertIdentical( root.getChild( 50 ), tree._getTopItem() );
      testUtil.clickDOM( tree._rows[ 1 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( root.getChild( 51 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 51 ) ) );
      testUtil.press( tree, "Up" );
      assertIdentical( root.getChild( 50 ), tree._getTopItem() );
      testUtil.press( tree, "Up" );
      assertIdentical( root.getChild( 49 ), tree._getTopItem() );
      tree.destroy();
    },
    
    testKeyboardNavigationPageUp : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Item" + i ] );
      }
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      testUtil.flush();
      assertEquals( 25, tree._rows.length );
      assertIdentical( root.getChild( 50 ), tree._getTopItem() );
      testUtil.clickDOM( tree._rows[ 5 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( root.getChild( 55 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 55 ) ) );
      testUtil.press( tree, "PageUp" );
      assertIdentical( root.getChild( 32 ), tree._getTopItem() );
      assertTrue( tree.isItemSelected( root.getChild( 32 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 32 ) ) );
      tree.destroy();
    },
    
    testKeyboardNavigationPageDown : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Item" + i ] );
      }
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      testUtil.flush();
      assertEquals( 25, tree._rows.length );
      assertIdentical( root.getChild( 50 ), tree._getTopItem() );
      testUtil.clickDOM( tree._rows[ 5 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( root.getChild( 55 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 55 ) ) );
      testUtil.press( tree, "PageDown" );
      assertIdentical( root.getChild( 55 ), tree._getTopItem() );
      assertTrue( tree.isItemSelected( root.getChild( 78 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 78 ) ) );
      tree.destroy();
    },    

    testPageUpOutOfBounds : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 10; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Item" + i ] );
      }
      var root = tree.getRootItem();
      testUtil.flush();
      assertEquals( 25, tree._rows.length );
      assertIdentical( root.getChild( 0 ), tree._getTopItem() );
      testUtil.clickDOM( tree._rows[ 5 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( root.getChild( 5 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 5 ) ) );
      testUtil.press( tree, "PageUp" );
      assertIdentical( root.getChild( 0 ), tree._getTopItem() );
      assertTrue( tree.isItemSelected( root.getChild( 0 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 0 ) ) );
      tree.destroy();
    },
    
    testPageDownOutOfBounds : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 10; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Item" + i ] );
      }
      var root = tree.getRootItem();
      testUtil.flush();
      assertEquals( 25, tree._rows.length );
      assertIdentical( root.getChild( 0 ), tree._getTopItem() );
      testUtil.clickDOM( tree._rows[ 5 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( root.getChild( 5 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 5 ) ) );
      testUtil.press( tree, "PageDown" );
      assertIdentical( root.getChild( 0 ), tree._getTopItem() );
      assertTrue( tree.isItemSelected( root.getChild( 9 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 9 ) ) );
      tree.destroy();
    },
    
    testKeyboardNavigationShiftSelect : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item3 = new org.eclipse.rwt.widgets.TreeItem( tree );
      item0.setExpanded( true );
      testUtil.clickDOM( tree._rows[ 0 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      testUtil.shiftPress( tree, "Down" );
      testUtil.shiftPress( tree, "Down" );
      testUtil.shiftPress( tree, "Down" );
      testUtil.shiftPress( tree, "Up" );
      assertTrue( tree.isFocusItem( item2 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      assertFalse( tree.isItemSelected( item3 ) );
      tree.destroy();
    },
    
    testKeyboardNavigationCtrlOnlyMovesFocus : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item3 = new org.eclipse.rwt.widgets.TreeItem( tree );
      item0.setExpanded( true );
      testUtil.clickDOM( tree._rows[ 0 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      testUtil.ctrlPress( tree, "Down" );
      testUtil.ctrlPress( tree, "Down" );
      testUtil.ctrlPress( tree, "Down" );
      testUtil.ctrlPress( tree, "Up" );
      assertTrue( tree.isFocusItem( item2 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      assertFalse( tree.isItemSelected( item3 ) );
      tree.destroy();
    },

    testKeyboardNavigationCtrlAndSpaceSelects : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
      item0.setExpanded( true );
      testUtil.clickDOM( tree._rows[ 0 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      testUtil.ctrlPress( tree, "Down" );
      assertTrue( tree.isFocusItem( item1 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      testUtil.ctrlPress( tree, "Space" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      testUtil.ctrlPress( tree, "Space" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      tree.destroy();
    },
    
    testKeyboardNavigationNoShiftSelectForLeftRight : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
      item0.setExpanded( true );
      testUtil.clickDOM( tree._rows[ 0 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      testUtil.shiftPress( tree, "Right" );
      assertTrue( tree.isFocusItem( item1 ) );
      assertFalse( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      testUtil.shiftPress( tree, "Left" );
      assertTrue( tree.isFocusItem( item0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      tree.destroy();
    },

    testKeyboardNavigationHome : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Item" + i ] );
      }
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      testUtil.flush();
      assertEquals( 25, tree._rows.length );
      assertIdentical( root.getChild( 50 ), tree._getTopItem() );
      testUtil.clickDOM( tree._rows[ 5 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( root.getChild( 55 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 55 ) ) );
      testUtil.press( tree, "Home" );
      assertIdentical( root.getChild( 0 ), tree._getTopItem() );
      assertTrue( tree.isItemSelected( root.getChild( 0 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 0 ) ) );
      tree.destroy();      
    },
    
    testKeyboardNavigationEnd : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      for( var i = 0; i < 100; i++ ) {
        var item = new org.eclipse.rwt.widgets.TreeItem( tree );
        item.setTexts( [ "Item" + i ] );
      }
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      testUtil.flush();
      assertEquals( 25, tree._rows.length );
      assertIdentical( root.getChild( 50 ), tree._getTopItem() );
      testUtil.clickDOM( tree._rows[ 5 ]._getTargetNode() );
      assertTrue( tree.isItemSelected( root.getChild( 55 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 55 ) ) );
      testUtil.press( tree, "End" );
      assertIdentical( root.getChild( 76 ), tree._getTopItem() );
      assertTrue( tree.isItemSelected( root.getChild( 99 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 99 ) ) );
      tree.destroy();
    },
   
    testDeselectionOnCollapseByMouse : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      testUtil.fakeAppearance( "tree-indent",  {
        style : function( states ) {
          return { "backgroundImage" : "bla.gif" };
        }
      } );
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      item0.setExpanded( true );
      tree.selectItem( item0 );
      tree.selectItem( item1 );
      tree.selectItem( item2 );
      testUtil.flush();
      testUtil.clickDOM( tree._rows[ 0 ]._getTargetNode().firstChild );
      assertFalse( item0.isExpanded() );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },
    
    testNoDeselectionOnNonMouseCollapse : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      testUtil.fakeAppearance( "tree-indent",  {
        style : function( states ) {
          return { "backgroundImage" : "bla.gif" };
        }
      } );
      tree.setHasMultiSelection( true );
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
      item0.setExpanded( true );
      tree.selectItem( item0 );
      tree.selectItem( item1 );
      tree.selectItem( item2 );
      tree.setFocusItem( item0 );
      testUtil.flush();
      item0.setExpanded( false );
      item0.setExpanded( true );
      tree.focus();
      testUtil.press( tree, "Left" );
      assertFalse( item0.isExpanded() );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

      // TODO [tb] : Can currently not be done since focusItem isn't synced 
//    testDeselectFocusedItemOnCollapse : function() {
//      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
//      var tree = this._createDefaultTree();
//      testUtil.fakeAppearance( "tree-indent",  {
//        style : function( states ) {
//          return { "backgroundImage" : "bla.gif" };
//        }
//      } );
//      tree.setHasMultiSelection( true );
//      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
//      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
//      var item2 = new org.eclipse.rwt.widgets.TreeItem( tree );
//      item0.setExpanded( true );
//      tree.selectItem( item0 );
//      tree.selectItem( item1 );
//      tree.selectItem( item2 );
//      tree.setFocusItem( item1 );
//      testUtil.flush();
//      item0.setExpanded( false );
//      assertTrue( tree.isItemSelected( item0 ) );
//      assertFalse( tree.isItemSelected( item1 ) );
//      assertTrue( tree.isItemSelected( item2 ) );
//      tree.destroy();
//    },
    
    testMoveFocusOnCollapse : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var tree = this._createDefaultTree();
      var item0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( item0 );
      item0.setExpanded( true );
      tree.setFocusItem( item1 );
      testUtil.flush();
      item0.setExpanded( false );
      assertTrue( tree.isFocusItem( item0 ) );
      tree.destroy();    
    },
    
    testNoDoubleClickDetection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasSelectionListeners( true );
      var child0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( tree );
      wm.add( tree, "w1", true );
      wm.add( child0, "w2", false );
      wm.add( child1, "w3", false );
      testUtil.flush();
      testUtil.initRequestLog();
      testUtil.click( tree._rows[ 0 ] );
      testUtil.click( tree._rows[ 1 ] );
      assertEquals( 2, testUtil.getRequestsSend() );
      var log = testUtil.getRequestLog();
      var expected = "org.eclipse.swt.events.widgetSelected";
      var notExpected = "org.eclipse.swt.events.widgetDefaultSelected";
      assertTrue( log[ 0 ].indexOf( notExpected ) == -1 );            
      assertTrue( log[ 1 ].indexOf( notExpected ) == -1 );            
      assertTrue( log[ 0 ].indexOf( expected ) != -1 );            
      assertTrue( log[ 1 ].indexOf( expected ) != -1 );            
      tree.destroy();
    },
    
    testNoDefaultSelectionWithCtrlSpace : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasSelectionListeners( true );
      var child0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      tree.setFocusItem( child0 );
      wm.add( tree, "w1", true );
      wm.add( child0, "w2", false );
      testUtil.flush();
      testUtil.initRequestLog();
      testUtil.ctrlPress( tree, "Space" );
      testUtil.ctrlPress( tree, "Space" );
      testUtil.ctrlPress( tree, "Space" );
      testUtil.ctrlPress( tree, "Space" );
      assertEquals( 4, testUtil.getRequestsSend() );
      var log = testUtil.getRequestLog();
      var notExpected = "org.eclipse.swt.events.widgetDefaultSelected";
      assertTrue( log.join().indexOf( notExpected ) == -1 );            
      tree.destroy();
    },

    testRemoveDisposedItemFromState : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var tree = this._createDefaultTree();
      tree.setHasMultiSelection( true );
      var child0 = new org.eclipse.rwt.widgets.TreeItem( tree );
      child0.setTexts( [ "C0" ] );
      tree.setFocusItem( child0 );
      tree.setTopItemIndex( 0 );
      testUtil.flush();      
      testUtil.mouseOver( tree._rows[ 0 ] );
      testUtil.shiftClick( tree._rows[ 0 ] );
      assertEquals( child0, tree._topItem )
      assertEquals( child0, tree._focusItem )
      assertEquals( child0, tree._leadItem )
      assertEquals( child0, tree._hoverItem )
      assertEquals( [ child0 ], tree._selection );
      child0.dispose();
      assertNull( tree._topItem )
      assertNull( tree._focusItem )
      assertNull( tree._leadItem )
      assertNull( tree._hoverItem )
      assertEquals( [], tree._selection );
      tree.destroy();
    },

    /////////
    // helper

    _createDefaultTree : function( noflush ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      this._fakeAppearance(); 
      var tree = new org.eclipse.rwt.widgets.Tree();
      tree.setHasFullSelection( true );
      tree.setItemHeight( 20 );
      tree.setLeft( 0 );
      tree.setTop( 0 );
      tree.setWidth( 500 );
      tree.setHeight( 500 );
      tree.setItemMetrics( 0, 0, 500, 0, 0, 0, 500 );
      tree.setColumnCount( 1 )
      tree.setSelectionPadding( 2, 4 );
      tree.setItemMetrics( 1, 0, 500, 0, 0, 0, 500 );
      tree.setItemMetrics( 2, 0, 500, 0, 0, 0, 500 );
      tree.setIndentionWidth( 16 );
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
    
    _addCheckBoxes : function( tree ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.fakeAppearance( "tree-check-box", {
        style : function( states ) {
          var result = {
            "backgroundImage" : "check.png"
          };
          return result;
        }
      } );
      tree.setHasCheckBoxes( true );
      tree.setCheckBoxMetrics( 5, 20 );
    }

  }
  
} );