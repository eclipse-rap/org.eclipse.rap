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

qx.Class.define( "org.eclipse.rwt.test.tests.GridColumnTest", {

  extend : qx.core.Object,

  members : {

    testCreateTableColumnByProtocol : function() {
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var Processor = org.eclipse.rwt.protocol.Processor;
      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridColumn",
        "properties" : {
          "style" : [],
          "parent" : "w3"
        }
      } );
      TestUtil.flush();
      var column = ObjectManager.getObject( "w4" );
      assertTrue( column instanceof org.eclipse.rwt.widgets.GridColumn );
      var label = this._getColumnLabel( tree, column )
      assertIdentical( tree._header, label.getParent() );
      assertEquals( "tree-column", label.getAppearance() );
      column.dispose();
      tree.destroy();
    },

    testSetIndexByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "index" : 3 } );
      assertEquals( 3, column.getIndex() );
      column.dispose();
      tree.destroy();
    },

    testSetLeftByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "left" : 3 } );

      assertEquals( 3, column.getLeft() );
      column.dispose();
      tree.destroy();
    },

    testSetWidthByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "width" : 3 } );

      assertEquals( 3, column.getWidth() );
      column.dispose();
      tree.destroy();
    },

    testSetTextByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "text" : "foo<>\" bar" } );
      assertEquals( "foo<>\" bar", column.getText().toString() );
      column.dispose();
      tree.destroy();
    },

    testRenderText : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "text" : "foo<>\" bar" } );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column );
      assertEquals( "foo&lt;&gt;&quot; bar", label.getCellContent( 1 ).toString() );
      column.dispose();
      tree.destroy();
    },

    testSetImageByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "image" : [ "image.png", 10, 20 ] } );
      assertEquals( [ "image.png", 10, 20 ], column.getImage() );
      column.dispose();
      tree.destroy();
    },

    testRenderImage : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "image" : [ "image.png", 10, 20 ] } );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column );
      assertEquals( "image.png", label.getCellContent( 0 ) );
      assertEquals( [ 10, 20 ], label.getCellDimension( 0 ) );
      column.dispose();
      tree.destroy();
    },

    testApplyObjectId : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      qx.ui.core.Widget._renderHtmlIds = true;
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( "w4", label.getHtmlAttribute( "id" ) );
      column.dispose();
      tree.destroy();
      qx.ui.core.Widget._renderHtmlIds = false;

    },

    testSetToolTipByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "toolTip" : "hello blue world" } );
      assertEquals( "hello blue world", column.getUserData( "toolTipText" ) );
      assertTrue( column.getToolTip() !== null );
      column.dispose();
      tree.destroy();
    },

    testSetResizableByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "resizable" : false } );
      assertFalse( column._resizable );
      column.dispose();
      tree.destroy();
    },

    testSetMoveableByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "moveable" : true } );
      assertTrue( column._moveable );
      column.dispose();
      tree.destroy();
    },

    testSetAlignmentByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "alignment" : "right" } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column )
      assertEquals( "right", label.getHorizontalChildrenAlign() );
      assertEquals( "right", tree.getRenderConfig().alignment[ 0 ] );
      column.dispose();
      tree.destroy();
    },

    testSetFixedByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "fixed" : true } );
      assertTrue( "right", column.isFixed() );
      column.dispose();
      tree.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "customVariant" : "variant_blue" } );
      assertEquals( "variant_blue", column.getCustomVariant() );
      column.dispose();
      tree.destroy();
    },

    testRenderCustomVariant : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "customVariant" : "variant_blue" } );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column );
      assertTrue( label.hasState( "variant_blue" ) );
      column.dispose();
      tree.destroy();
    },

    testSetSortDirection : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w3", { "sortColumn" : "w4", "sortDirection" : "up" } );

      assertEquals( "up", column.getSortDirection() );
      column.dispose();
      tree.destroy();
    },

    testRenderSortIndicator : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.fakeAppearance( "tree-column-sort-indicator", {
        style : function( states ) {
          return {
            "backgroundImage" : [ states.up ? "up.gif" : "down.gif", 10, 15 ]
          };
        }
      } );

      TestUtil.protocolSet( "w3", { "sortColumn" : "w4", "sortDirection" : "up" } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( "up.gif", label.getCellContent( 2 ) );
      assertEquals( [ 10, 15 ], label.getCellDimension( 2 ) );
      column.dispose();
      tree.destroy();
    },

    testShowResizeLine : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column );
      assertNotNull( label.getElement() );
      var button = qx.event.type.MouseEvent.buttons.left;

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 23, 3 );
      TestUtil.flush();

      var line = tree._resizeLine;
      assertEquals( "", line.getStyleProperty( "visibility" ) );
      assertEquals( 21, parseInt( line._style.left, 10 ) );
      column.dispose();
      tree.destroy();
    },

    testShowResizeLineScrolled : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 23, 0, 0, 0, 0 );
      tree.setItemMetrics( 1, 23, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      tree.setColumnCount( 2 );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } ); // makes header scrollable
      tree.setScrollLeft( 10 );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column )
      assertNotNull( label.getElement() );
      var button = qx.event.type.MouseEvent.buttons.left;

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();

      var line = tree._resizeLine;
      assertEquals( "", line.getStyleProperty( "visibility" ) );
      assertEquals( 11, parseInt( line._style.left, 10 ) );
      column.dispose();
      tree.destroy();
    },

    testShowResizeLineFixedScrolled : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 23, 0, 0, 0, 0 );
      tree.setItemMetrics( 1, 23, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      tree.setColumnCount( 2 );
      tree.setScrollLeft( 10 );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "fixed" : true } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } ); // makes header scrollable
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column )
      assertNotNull( label.getElement() );
      var button = qx.event.type.MouseEvent.buttons.left;

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 23, 3 );
      TestUtil.flush();

      var line = tree._resizeLine;
      assertEquals( "", line.getStyleProperty( "visibility" ) );
      assertEquals( 21, parseInt( line._style.left, 10 ) );
      column.dispose();
      tree.destroy();
    },

    testDropMoveable : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } );
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();

      var message = TestUtil.getMessage();
      assertTrue( message.indexOf( "w4.left=10" ) !== -1 );
      column.dispose();
      tree.destroy();
    },

    testShowDragFeedback : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } ); // makes header scrollable
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();

      var feedbackLabel = this._getColumnDragFeedback( tree );
      assertTrue( feedbackLabel.isSeeable() );
      assertEquals( 1e8, feedbackLabel.getZIndex() );
      assertTrue( feedbackLabel.hasState( "moving" ) );
      assertEquals( 10, feedbackLabel.getLeft() );
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();
      column.dispose();
      tree.destroy();
    },

    testKeepOverStateWhileDragging : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } ); // makes header scrollable
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseout", button, 20, 3 );
      TestUtil.flush();

      assertTrue( label.hasState( "mouseover" ) );
      column.dispose();
      tree.destroy();
    },

    testDragFeedbackProperties : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { 
        "left" : 3, 
        "width" : 20, 
        "moveable" : true,
        "text" : "foo",
        "image" : [ "bar.jpg", 10, 10 ],
        "sortDirection" : "up",
        "customVariant" : "blue" 
      } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } ); // makes header scrollable
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();

      var feedbackLabel = this._getColumnDragFeedback( tree );
      assertEquals( 20, feedbackLabel.getWidth() );
      assertEquals( "foo", feedbackLabel.getCellContent( 1 ) );
      assertEquals( "bar.jpg", feedbackLabel.getCellContent( 0 ) );
      assertTrue( feedbackLabel.hasState( "blue" ) );
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();
      column.dispose();
      tree.destroy();
    },

    testMoveOnePixel : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } ); 
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 14, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 14, 3 );
      TestUtil.flush();

      var feedbackLabel = this._getColumnDragFeedback( tree );
      assertEquals( 0, TestUtil.getRequestsSend() );
      assertTrue( feedbackLabel === null || !feedbackLabel.isSeeable() );
      column.dispose();
      tree.destroy();
    },

    testHideDragFeedbackAfterSetLeft : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } ); // makes header scrollable
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();
      var feedbackLabel = this._getColumnDragFeedback( tree );
      org.eclipse.swt.EventUtil.setSuspended( true );
      TestUtil.protocolSet( "w4", { "left" : 3 } );
      TestUtil.flush();
      assertTrue( feedbackLabel.isSeeable() );
      org.eclipse.swt.EventUtil.setSuspended( false );
      TestUtil.skipAnimations();

      assertFalse( feedbackLabel.isSeeable() );
      assertEquals( 3, feedbackLabel.getLeft() );
      column.dispose();
      tree.destroy();
    },

    testCleanUpSnapAnimation : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();
      var feedbackLabel = this._getColumnDragFeedback( tree );
      org.eclipse.swt.EventUtil.setSuspended( true );
      TestUtil.protocolSet( "w4", { "left" : 3 } );
      TestUtil.flush();
      var animation = org.eclipse.rwt.Animation._queue[ 0 ];
      assertTrue( feedbackLabel.isSeeable() );
      org.eclipse.swt.EventUtil.setSuspended( false );
      TestUtil.skipAnimations();
      TestUtil.forceTimerOnce();

      assertFalse( feedbackLabel.hasEventListeners( "cancelAnimations" ) );
      assertFalse( feedbackLabel.hasEventListeners( "dispose" ) );
      assertTrue( animation.isDisposed() );
      column.dispose();
      tree.destroy();
    },

    testDummyIgnoresDragFeedback : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 70, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 70, 3 );
      TestUtil.flush();
      var feedbackLabel = this._getColumnDragFeedback( tree );
      var dummyLabel = this._getDummyLabel( tree );
      org.eclipse.swt.EventUtil.setSuspended( true );
      TestUtil.protocolSet( "w4", { "left" : 30 } );
      TestUtil.flush();
      org.eclipse.swt.EventUtil.setSuspended( false );

      assertEquals( 50, dummyLabel.getLeft() );
      column.dispose();
      tree.destroy();
    },

    testDisposeDuringAnimation : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 70, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 70, 3 );
      TestUtil.flush();
      var feedbackLabel = this._getColumnDragFeedback( tree );
      org.eclipse.swt.EventUtil.setSuspended( true );
      TestUtil.protocolSet( "w4", { "left" : 30 } );
      TestUtil.flush();
      org.eclipse.swt.EventUtil.setSuspended( false );
      assertTrue( feedbackLabel.isSeeable() );
      org.eclipse.rwt.Animation._mainLoop();
      var animation = org.eclipse.rwt.Animation._queue[ 0 ];
      tree.destroy();
      TestUtil.flush();
      org.eclipse.rwt.Animation._mainLoop();

      assertTrue( feedbackLabel.isDisposed() );
      assertTrue( animation.isDisposed() );
    },

    testRenderAgainDuringAnimation : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 70, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 70, 3 );
      TestUtil.flush();
      var feedbackLabel = this._getColumnDragFeedback( tree );
      org.eclipse.swt.EventUtil.setSuspended( true );
      TestUtil.protocolSet( "w4", { "left" : 3 } );
      TestUtil.flush();
      org.eclipse.swt.EventUtil.setSuspended( false );
      assertTrue( feedbackLabel.isSeeable() );
      org.eclipse.rwt.Animation._mainLoop();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 70, 3 );
      org.eclipse.rwt.Animation._mainLoop();
      TestUtil.flush();

      assertTrue( feedbackLabel.isSeeable() );
      assertEquals( 60, feedbackLabel.getLeft() );
      assertEquals( 0, org.eclipse.rwt.Animation._queue.length );
      tree.destroy();
    },

    testSendSelectionEvent : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderVisible( true );
      TestUtil.initRequestLog();
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      column.setHasSelectionListener( true );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      var label = this._getColumnLabel( tree, column )

      TestUtil.click( label );
      TestUtil.flush();

      var message = TestUtil.getMessage();
      assertTrue( message.indexOf( "org.eclipse.swt.events.widgetSelected=w4" ) !== -1 );
      column.dispose();
      tree.destroy();
    },

    testDontSendSelectionEventAfterResize : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderVisible( true );
      TestUtil.initRequestLog();
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      column.setHasSelectionListener( true );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      var label = this._getColumnLabel( tree, column )

      TestUtil.click( label, 23, 3 );
      TestUtil.flush();

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( TestUtil.getMessage().indexOf( "org.eclipse.swt.events.widgetSelected=w4" ) === -1 );
      column.dispose();
      tree.destroy();
    },

    testDontSendSelectionEventAfterMove : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderVisible( true );
      TestUtil.initRequestLog();
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      column.setHasSelectionListener( true );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.flush();
      var button = qx.event.type.MouseEvent.buttons.left;
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "click", button, 20, 3 );
      TestUtil.flush(); // NOTE: unrealistic. browser would very likely not fire click after moving 

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( TestUtil.getMessage().indexOf( "org.eclipse.swt.events.widgetSelected=w4" ) === -1 );
      column.dispose();
      tree.destroy();
    },

    testDontSendSelectionEventInResize : function() {//move
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column )
      assertNotNull( label.getElement() );
      var button = qx.event.type.MouseEvent.buttons.left;
      var label = this._getColumnLabel( tree, column )

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 23, 3 );
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 23, 3 );
      TestUtil.flush();

      var line = tree._resizeLine;
      assertEquals( "", line.getStyleProperty( "visibility" ) );
      assertEquals( 21, parseInt( line._style.left, 10 ) );
      column.dispose();
      tree.destroy();
    },

    //////////////////
    // Helping methods

    _createTreeByProtocol : function( id, parentId, styles ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.Grid",
        "properties" : {
          "style" : styles,
          "parent" : parentId,
          "appearance" : "tree",
          "selectionPadding" : [ 2, 4 ],
          "indentionWidth" : 16,
          "checkBoxMetrics" : [ 5, 16 ],
          "bounds" : [ 0, 0, 100, 100 ],
          "columnCount" : 3,
          "headerVisible" : true
        }
      } );
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    _createColumnByProtocol : function( id, parentId, styles ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.GridColumn",
        "properties" : {
          "style" : styles,
          "parent" : parentId
        }
      } );
      TestUtil.flush();
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
    },

    tearDown : function() {
      shell.destroy();
      shell = null;
    },

    _getColumnLabel : function( grid, column ) {
      return grid.getTableHeader()._getLabelByColumn( column );
    },

    _getColumnDragFeedback : function( grid ) {
      return grid.getTableHeader()._feedbackLabel;
    },

    _getDummyLabel : function( grid ) {
      return grid.getTableHeader()._dummyColumn;
    }

  }

} );

}());