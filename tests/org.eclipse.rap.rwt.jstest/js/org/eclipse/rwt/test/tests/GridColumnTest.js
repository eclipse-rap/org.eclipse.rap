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
var MessageProcessor = rwt.remote.MessageProcessor;
var ObjectRegistry = rwt.remote.ObjectRegistry;

var shell;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.GridColumnTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateTableColumnByProtocol : function() {
      var ObjectManager = rwt.remote.ObjectRegistry;
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var Processor = rwt.remote.MessageProcessor;
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
      assertTrue( column instanceof rwt.widgets.GridColumn );
      var label = this._getColumnLabel( tree, column );
      assertIdentical( tree._header, label.getParent() );
      assertEquals( "tree-column", label.getAppearance() );
      column.dispose();
      tree.destroy();
    },

    testDestroyColumnWithGrid : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      MessageProcessor.processOperationArray( [ "destroy", "w3"] );
      TestUtil.flush();

      assertTrue( ObjectRegistry.getObject( "w3" ) == null );
      assertTrue( tree.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w4" ) == null );
      assertTrue( column.isDisposed() );
      shell.destroy();
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

    testSetHasExpandListenerByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolListen( "w4", { "Expand" : true } );

      assertTrue( column.getHasExpandListener() );
      column.dispose();
      tree.destroy();
    },

    testSetHasCollapseListenerByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolListen( "w4", { "Collapse" : true } );

      assertTrue( column.getHasCollapseListener() );
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

    testRenderWhileParentNotDisplayable : function() {
      // See Bug 384792 - [Table] Header disappear when it's layouted in invisible TabFolder tab
      shell.setDisplay( false );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "text" : "foo" } );
      TestUtil.flush();

      rwt.remote.EventUtil.setSuspended( true );
      shell.setDisplay( true );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( "foo", label.getCellContent( 1 ).toString() );
      column.dispose();
      tree.destroy();
    },

    testRenderFooterText : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setFooterVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "footerText" : "foo<>\" bar" } );

      TestUtil.flush();
      var label = this._getColumnLabel( tree, column, true );
      assertEquals( "foo&lt;&gt;&quot; bar", label.getCellContent( 1 ).toString() );
      column.dispose();
      tree.destroy();
    },

    testRenderFooterColumnVisibility : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setFooterVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "footerText" : "foo<>\" bar" } );
      TestUtil.flush();

      TestUtil.protocolSet( "w4", { "visibility" : false } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column, true );
      assertFalse( label.isSeeable() );
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

    testSetFontByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "font" : [ [ "Arial" ], 12, true, false ] } );

      var font = column.getFont();
      assertEquals( [ "Arial" ], font.getFamily()  );
      assertEquals( 12, font.getSize()  );
      assertEquals( true, font.getBold()  );
      assertEquals( false, font.getItalic()  );
      column.dispose();
      tree.destroy();
    },

    testInitialCheckProperty : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );

      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "index" : 1 } );

      assertFalse( column.getCheck() );
      assertTrue( tree.getRenderConfig().itemCellCheck[ 1 ] !== true );
      column.dispose();
      tree.destroy();
    },

    testSetCheckByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "check" : true, "index" : 1 } );

      assertTrue( column.getCheck() );
      assertTrue( tree.getRenderConfig().itemCellCheck[ 1 ] );
      column.dispose();
      tree.destroy();
    },

    testResetFontByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "font" : [ [ "Arial" ], 12, true, false ] } );
      TestUtil.protocolSet( "w4", { "font" : null } );

      assertNull( column.getFont() );
      column.dispose();
      tree.destroy();
    },

    testSetFooterTextByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "footerText" : "foo<>\" bar" } );

      assertEquals( "foo<>\" bar", column.getFooterText().toString() );
      column.dispose();
      tree.destroy();
    },

    testSetFooterImageByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "footerImage" : [ "image.png", 10, 20 ] } );

      assertEquals( [ "image.png", 10, 20 ], column.getFooterImage() );
      column.dispose();
      tree.destroy();
    },

    testSetFooterFontByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "footerFont" : [ [ "Arial" ], 12, true, false ] } );

      var font = column.getFooterFont();
      assertEquals( [ "Arial" ], font.getFamily()  );
      assertEquals( 12, font.getSize()  );
      assertEquals( true, font.getBold()  );
      assertEquals( false, font.getItalic()  );
      column.dispose();
      tree.destroy();
    },

    testResetFooterFontByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "footerFont" : [ [ "Arial" ], 12, true, false ] } );
      TestUtil.protocolSet( "w4", { "footerFont" : null } );

      assertNull( column.getFooterFont() );
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

    testRenderFooterImage : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setFooterVisible( true );
      tree.setFooterHeight( 20 );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "footerImage" : [ "image.png", 10, 20 ] } );

      TestUtil.flush();
      var label = this._getColumnLabel( tree, column, true );
      assertEquals( "image.png", label.getCellContent( 0 ) );
      assertEquals( [ 10, 20 ], label.getCellDimension( 0 ) );
      column.dispose();
      tree.destroy();
    },

    testRenderFont : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "font" : [ [ "Arial" ], 12, true, false ] } );

      TestUtil.flush();
      var label = this._getColumnLabel( tree, column );
      var font = label.getFont();
      assertEquals( [ "Arial" ], font.getFamily()  );
      assertEquals( 12, font.getSize()  );
      assertEquals( true, font.getBold()  );
      assertEquals( false, font.getItalic()  );
      column.dispose();
      tree.destroy();
    },

    testRenderFooterFont : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setFooterVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "footerFont" : [ [ "Arial" ], 12, true, false ] } );

      TestUtil.flush();
      var label = this._getColumnLabel( tree, column, true );
      var font = label.getFont();
      assertEquals( [ "Arial" ], font.getFamily()  );
      assertEquals( 12, font.getSize()  );
      assertEquals( true, font.getBold()  );
      assertEquals( false, font.getItalic()  );
      column.dispose();
      tree.destroy();
    },

    testApplyObjectId : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      rwt.widgets.base.Widget._renderHtmlIds = true;
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( "w4", label.getHtmlAttribute( "id" ) );
      column.dispose();
      tree.destroy();
      rwt.widgets.base.Widget._renderHtmlIds = false;

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

      var label = this._getColumnLabel( tree, column );
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

    testRenderFooterLabelState : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setFooterVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "footerText" : "foo<>\" bar" } );

      TestUtil.flush();
      var label = this._getColumnLabel( tree, column, true );
      assertTrue( label.hasState( "footer" ) );
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
      var button = rwt.event.MouseEvent.buttons.left;

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 23, 3 );
      TestUtil.flush();

      var line = tree._resizeLine;
      assertEquals( "", line.getStyleProperty( "visibility" ) );
      assertEquals( 21, parseInt( line._style.left, 10 ) );
      column.dispose();
      tree.destroy();
    },

    testScrollLeft : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 23, 0, 0, 0, 0 );
      tree.setItemMetrics( 1, 23, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      tree.setFooterVisible( true );
      tree.setColumnCount( 2 );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );
      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 177 } ); // makes header scrollable

      tree.setScrollLeft( 10 );

      TestUtil.flush();
      assertEquals( 10, tree.getTableHeader().getScrollLeft() );
      assertEquals( 10, tree.getFooter().getScrollLeft() );
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
      var label = this._getColumnLabel( tree, column );
      assertNotNull( label.getElement() );
      var button = rwt.event.MouseEvent.buttons.left;

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
      var label = this._getColumnLabel( tree, column );
      assertNotNull( label.getElement() );
      var button = rwt.event.MouseEvent.buttons.left;

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
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();

      var message = TestUtil.getMessageObject();
      assertEquals( 10, message.findCallProperty( "w4", "move", "left" ) );
      column.dispose();
      tree.destroy();
    },

    testMoveableHoverEffect : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20, "moveable" : true } );
      TestUtil.flush();
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseover", button, 13, 3 );
      TestUtil.flush();

      assertTrue( label.hasState( "mouseover" ) );
      column.dispose();
      tree.destroy();
    },

    testNonMoveableHoverEffect : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );
      TestUtil.flush();
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseover", button, 13, 3 );
      TestUtil.flush();

      assertFalse( label.hasState( "mouseover" ) );
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
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

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
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

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
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

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
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

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
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();
      var feedbackLabel = this._getColumnDragFeedback( tree );
      rwt.remote.EventUtil.setSuspended( true );
      TestUtil.protocolSet( "w4", { "left" : 3 } );
      TestUtil.flush();
      assertTrue( feedbackLabel.isSeeable() );
      rwt.remote.EventUtil.setSuspended( false );
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
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();
      var feedbackLabel = this._getColumnDragFeedback( tree );
      rwt.remote.EventUtil.setSuspended( true );
      TestUtil.protocolSet( "w4", { "left" : 3 } );
      TestUtil.flush();
      var animation = rwt.animation.Animation._queue[ 0 ];
      assertTrue( feedbackLabel.isSeeable() );
      rwt.remote.EventUtil.setSuspended( false );
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
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 70, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 70, 3 );
      TestUtil.flush();
      var dummyLabel = this._getDummyLabel( tree );
      rwt.remote.EventUtil.setSuspended( true );
      TestUtil.protocolSet( "w4", { "left" : 30 } );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( false );

      assertEquals( 50, dummyLabel.getLeft() );
      column.dispose();
      tree.destroy();
    },

    testDummyIgnoresIvisibleColumn : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemMetrics( 0, 0, 200, 0, 0, 0, 0 );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      var colTwo = this._createColumnByProtocol( "w5", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );

      TestUtil.protocolSet( "w5", { "left" : 23, "width" : 40, "visibility" : false  } );
      TestUtil.flush();

      var dummyLabel = this._getDummyLabel( tree );
      assertEquals( 23, dummyLabel.getLeft() );
      column.dispose();
      colTwo.dispose();
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
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 70, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 70, 3 );
      TestUtil.flush();
      var feedbackLabel = this._getColumnDragFeedback( tree );
      rwt.remote.EventUtil.setSuspended( true );
      TestUtil.protocolSet( "w4", { "left" : 30 } );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( false );
      assertTrue( feedbackLabel.isSeeable() );
      rwt.animation.Animation._mainLoop();
      var animation = rwt.animation.Animation._queue[ 0 ];
      tree.destroy();
      TestUtil.flush();
      rwt.animation.Animation._mainLoop();

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
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.initRequestLog();
      var label = this._getColumnLabel( tree, column );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 70, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 70, 3 );
      TestUtil.flush();
      var feedbackLabel = this._getColumnDragFeedback( tree );
      rwt.remote.EventUtil.setSuspended( true );
      TestUtil.protocolSet( "w4", { "left" : 3 } );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( false );
      assertTrue( feedbackLabel.isSeeable() );
      rwt.animation.Animation._mainLoop();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 70, 3 );
      rwt.animation.Animation._mainLoop();
      TestUtil.flush();

      assertTrue( feedbackLabel.isSeeable() );
      assertEquals( 60, feedbackLabel.getLeft() );
      assertEquals( 0, rwt.animation.Animation._queue.length );
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
      var label = this._getColumnLabel( tree, column );

      TestUtil.click( label );
      TestUtil.flush();

      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w4", "Selection" ) );
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
      var label = this._getColumnLabel( tree, column );

      TestUtil.click( label, 23, 3 );
      TestUtil.flush();

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject().findNotifyOperation( "w4", "Selection" ) );
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
      var button = rwt.event.MouseEvent.buttons.left;
      var label = this._getColumnLabel( tree, column );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "click", button, 20, 3 );
      TestUtil.flush(); // NOTE: unrealistic. browser would very likely not fire click after moving

      assertNull( TestUtil.getMessageObject().findNotifyOperation( "w4", "Selection" ) );
      column.dispose();
      tree.destroy();
    },

    testDontSendSelectionEventInResize : function() {//move
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderVisible( true );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 3, "width" : 20 } );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column );
      assertNotNull( label.getElement() );
      var button = rwt.event.MouseEvent.buttons.left;
      var label = this._getColumnLabel( tree, column );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 23, 3 );
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 23, 3 );
      TestUtil.flush();

      var line = tree._resizeLine;
      assertEquals( "", line.getStyleProperty( "visibility" ) );
      assertEquals( 21, parseInt( line._style.left, 10 ) );
      column.dispose();
      tree.destroy();
    },

    testCreateColumnGroupByProtocol : function() {
      var ObjectManager = rwt.remote.ObjectRegistry;
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var Processor = rwt.remote.MessageProcessor;

      Processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridColumnGroup",
        "properties" : {
          "style" : [],
          "parent" : "w3"
        }
      } );
      TestUtil.flush();

      var column = ObjectManager.getObject( "w4" );
      assertTrue( column instanceof rwt.widgets.GridColumn );
      assertTrue( column.isGroup() );
      var label = this._getColumnLabel( tree, column );
      assertIdentical( tree._header, label.getParent() );
      assertEquals( "tree-column", label.getAppearance() );
      assertTrue( label.hasState( "group" ) );
      column.dispose();
      tree.destroy();
    },

    testSetGroupHeightByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "height" : 23 } );

      assertEquals( 23, column.getHeight() );
      column.dispose();
      tree.destroy();
    },

    testSetGroupInitialVisibility : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );

      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      assertTrue( column.getVisibility() );
      column.dispose();
      tree.destroy();
    },

    testSetGroupVisibilityByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "visibility" : false } );

      assertFalse( column.getVisibility() );
      column.dispose();
      tree.destroy();
    },


    testSetGroupInitialExpanded : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );

      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      assertTrue( column.isExpanded() );
      column.dispose();
      tree.destroy();
    },

    testSetGroupExpandedByProtocol : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "expanded" : false } );

      assertFalse( column.isExpanded() );
      column.dispose();
      tree.destroy();
    },

    testRenderColumnBounds : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40 } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( "100%", label.getHeight() );
      assertEquals( 0, label.getTop() );
      assertEquals( 10, label.getLeft() );
      assertEquals( 40, label.getWidth() );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupBounds : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23 } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( 23, label.getHeight() );
      assertEquals( 0, label.getTop() );
      assertEquals( 10, label.getLeft() );
      assertEquals( 40, label.getWidth() );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupVisibilityTrue : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23 } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertTrue( label.isSeeable() );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupVisibilityFalse : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );
      TestUtil.flush();

      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23, "visibility" : false } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertFalse( label.isSeeable() );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupVisibilityInitialFalse : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [], true );

      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23, "visibility" : false } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertNull( label );
      column.dispose();
      tree.destroy();
    },

    testDisposeGroup : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23 } );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column );

      column.dispose();
      TestUtil.flush();

      assertTrue( label.isDisposed() );
      column.dispose();
      tree.destroy();
    },

    testSetGroup : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnByProtocol( "w4", "w3", [], true );

      TestUtil.protocolSet( "w4", { "group" : "w5" } );
      var group = this._createColumnGroupByProtocol( "w5", "w3", [], true );
      TestUtil.flush();

      assertIdentical( group, column.getGroup() );
      column.dispose();
      group.dispose();
      tree.destroy();
    },

    testRenderColumnBoundsWithGroup : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnGroupByProtocol( "w5", "w3", [], true );

      TestUtil.protocolSet( "w4", { "group" : "w5", "left" : 22, "width": 20 } );
      TestUtil.protocolSet( "w5", { "height" : 22, "left" : 10, "width": 40 } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( 28, label.getHeight() );
      assertEquals( 22, label.getTop() );
      assertEquals( 22, label.getLeft() );
      assertEquals( 20, label.getWidth() );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupChevron : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23 } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( "chev-left.gif", label.getCellContent( 2 ) );
      assertEquals( [ 10, 7 ], label.getCellDimension( 2 ) );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupChevronHoverLabel : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23 } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      TestUtil.hoverFromTo( document.body, label.getElement() );
      TestUtil.flush();

      assertEquals( "chev-left.gif", label.getCellContent( 2 ) );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupChevronHoverCell : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23 } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      TestUtil.hoverFromTo( document.body, label.getCellNode( 2  ) );
      TestUtil.flush();

      assertEquals( "chev-left-hover.gif", label.getCellContent( 2 ) );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupChevronUnHoverCell : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23 } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      TestUtil.hoverFromTo( document.body, label.getCellNode( 2  ) );
      TestUtil.flush();
      TestUtil.hoverFromTo( label.getCellNode( 2  ), label.getCellNode( 1 ) );
      TestUtil.flush();

      assertEquals( "chev-left.gif", label.getCellContent( 2 ) );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupChevronAlignRight : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", { "left" : 10, "width": 50, "height" : 23, "text" : "x" } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( 21, label.getCellDimension( 1 )[ 0 ] );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupChevronAlignRightWithNoText : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", {
         "left" : 10,
         "width": 100,
         "height" : 23
       } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( 71, label.getCellDimension( 1 )[ 0 ] );
      assertNotNull( label.getCellNode( 1 ) );
      column.dispose();
      tree.destroy();
    },

    testRenderGroupChevronOverlap : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );

      TestUtil.protocolSet( "w4", {
        "left" : 10,
        "width": 45,
        "height" : 23,
        "text" : "foo0o0o0ooooooo"
      } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      assertEquals( 16, label.getCellDimension( 1 )[ 0 ] );
      column.dispose();
      tree.destroy();
    },

    testSendGroupCollapse : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      TestUtil.initRequestLog();
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23 } );
      TestUtil.protocolListen( "w4", { "Collapse" : true } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      TestUtil.clickDOM( label.getCellNode( 2  ) );
      TestUtil.flush();

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w4", "Collapse" ) );
      column.dispose();
      tree.destroy();
    },

    testSendGroupExpand : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      TestUtil.initRequestLog();
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23, "expanded" : false } );
      TestUtil.protocolListen( "w4", { "Expand" : true } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      TestUtil.clickDOM( label.getCellNode( 2  ) );
      TestUtil.flush();

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w4", "Expand" ) );
      assertEquals( "loading", label._chevron );
      column.dispose();
      tree.destroy();
    },

    testSendExpandedFalse : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      TestUtil.initRequestLog();
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23 } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      TestUtil.clickDOM( label.getCellNode( 2  ) );
      TestUtil.flush();
      rwt.remote.Server.getInstance().send();

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertFalse( TestUtil.getMessageObject().findSetProperty( "w4", "expanded" ) );
      column.dispose();
      tree.destroy();
    },

    testSendExpandedTrue : function() {
      this._fakeChevronAppearance();
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      TestUtil.initRequestLog();
      var column = this._createColumnGroupByProtocol( "w4", "w3", [] );
      TestUtil.protocolSet( "w4", { "left" : 10, "width": 40, "height" : 23, "expanded" : false } );
      TestUtil.flush();

      var label = this._getColumnLabel( tree, column );
      TestUtil.clickDOM( label.getCellNode( 2  ) );
      TestUtil.flush();
      rwt.remote.Server.getInstance().send();

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( TestUtil.getMessageObject().findSetProperty( "w4", "expanded" ) );
      column.dispose();
      tree.destroy();
    },


    testRenderDragFeedbackAfterGroupedColumnDrag : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnGroupByProtocol( "w5", "w3", [], true );
      var columnTwo = this._createColumnByProtocol( "w6", "w3", [] );
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.protocolSet( "w4", { "group" : "w5", "left" : 22, "width": 20, "moveable" : true } );
      TestUtil.protocolSet( "w5", { "height" : 22, "left" : 22, "width": 20 } );
      TestUtil.protocolSet( "w6", { "left" : 42, "width": 40, "moveable" : true } );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column );
      var labelTwo = this._getColumnLabel( tree, columnTwo );

      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();
      TestUtil.protocolSet( "w4", { "left" : 22 } );
      TestUtil.flush();
      TestUtil.skipAnimations();
      TestUtil.flush();
      assertFalse( this._getColumnDragFeedback( tree ).isSeeable() );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( labelTwo.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( labelTwo.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();

      var feedback = this._getColumnDragFeedback( tree );
      assertTrue( feedback.isSeeable() );
      assertEquals( "100%", feedback.getHeight() );
      column.dispose();
      tree.destroy();
    },


    testRenderDragFeedbackForGroupedColumnAfterNonGrouped : function() {
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setHeaderHeight( 50 );
      var column = this._createColumnByProtocol( "w4", "w3", [] );
      this._createColumnGroupByProtocol( "w5", "w3", [], true );
      var columnTwo = this._createColumnByProtocol( "w6", "w3", [] );
      var button = rwt.event.MouseEvent.buttons.left;
      TestUtil.protocolSet( "w4", { "group" : "w5", "left" : 22, "width": 20, "moveable" : true } );
      TestUtil.protocolSet( "w5", { "height" : 22, "left" : 22, "width": 20 } );
      TestUtil.protocolSet( "w6", { "left" : 42, "width": 40, "moveable" : true } );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column );
      var labelTwo = this._getColumnLabel( tree, columnTwo );

      TestUtil.fakeMouseEventDOM( labelTwo.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( labelTwo.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( labelTwo.getElement(), "mouseup", button, 20, 3 );
      TestUtil.flush();
      TestUtil.protocolSet( "w4", { "left" : 22 } );
      TestUtil.flush();
      TestUtil.skipAnimations();
      TestUtil.flush();
      assertFalse( this._getColumnDragFeedback( tree ).isSeeable() );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousedown", button, 13, 3 );
      TestUtil.flush();
      TestUtil.fakeMouseEventDOM( label.getElement(), "mousemove", button, 20, 3 );
      TestUtil.flush();

      var feedback = this._getColumnDragFeedback( tree );
      assertTrue( feedback.isSeeable() );
      assertEquals( 28, feedback.getHeight() );
      column.dispose();
      tree.destroy();
    },

    //////////////////
    // Helping methods

    _createTreeByProtocol : function( id, parentId, styles ) {
      rwt.remote.MessageProcessor.processOperation( {
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
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id + "_vscroll",
        "action" : "create",
        "type" : "rwt.widgets.ScrollBar",
        "properties" : {
          "parent" : id,
          "style" : [ "VERTICAL" ]
        }
      } );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id + "_hscroll",
        "action" : "create",
        "type" : "rwt.widgets.ScrollBar",
        "properties" : {
          "parent" : id,
          "style" : [ "HORIZONTAL" ]
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    _createColumnByProtocol : function( id, parentId, styles ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.GridColumn",
        "properties" : {
          "style" : styles,
          "parent" : parentId
        }
      } );
      TestUtil.flush( true );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    _createColumnGroupByProtocol : function( id, parentId, styles, noFlush ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.GridColumnGroup",
        "properties" : {
          "style" : styles,
          "parent" : parentId
        }
      } );
      if( !noFlush ) {
        TestUtil.flush( true );
      }
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      shell.setLeft( 0 );
    },

    tearDown : function() {
      shell.destroy();
      shell = null;
    },

    _getColumnLabel : function( grid, column, footer ) {
      var header = footer ? grid.getFooter() : grid.getTableHeader();
      return header._getLabelByColumn( column );
    },

    _getColumnDragFeedback : function( grid ) {
      return grid.getTableHeader()._feedbackLabel;
    },

    _getDummyLabel : function( grid ) {
      return grid.getTableHeader()._dummyColumn;
    },

    _fakeChevronAppearance : function() {
      TestUtil.fakeAppearance( "tree-column-chevron", {
        style : function( states ) {
          var source = "chev-";
          source += states.expanded ? "left" : "right";
          source += states.mouseover ? "-hover" : "";
          source += ".gif";
          var result = {
            "backgroundImage" : [ source, 10, 7 ]
          };
          return result;
        }
      } );
    }

  }

} );

}());
