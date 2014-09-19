/*******************************************************************************
 * Copyright (c) 2010, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

/*jshint nonew:false */
(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectRegistry = rwt.remote.ObjectRegistry;
var MessageProcessor = rwt.remote.MessageProcessor;

var gridHandler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Grid" );
var itemHandler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.GridItem" );
var columnHandler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.GridColumn" );

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.GridTest", {

  extend : rwt.qx.Object,

  members : {

    testGridHandlerEventsList : function() {
      var events = [ "Selection", "DefaultSelection", "Expand", "Collapse", "SetData" ];
      assertEquals( events, gridHandler.events );
    },

    testCreateTreeByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Grid",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "appearance": "tree",
          "selectionPadding" : [ 2, 4 ],
          "indentionWidth" : 16,
          "checkBoxMetrics" : [ 5, 16 ]
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Grid );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "tree", widget.getAppearance() );
      assertEquals( "tree", widget.getRenderConfig().baseAppearance );
      assertFalse( widget.getRenderConfig().fullSelection );
      assertFalse( widget.getRenderConfig().hideSelection );
      assertFalse( widget.getRenderConfig().hasCheckBoxes );
      assertFalse( widget._hasMultiSelection );
      assertTrue( widget._rowContainer.hasEventListeners( "mousewheel" ) );
      assertEquals( [ 2, 4 ], widget.getRenderConfig().selectionPadding );
      assertEquals( 16, widget.getRenderConfig().indentionWidth );
      assertEquals( undefined, widget.getRenderConfig().checkBoxLeft );
      assertEquals( undefined, widget.getRenderConfig().checkBoxWidth );
      shell.destroy();
      widget.destroy();
    },

    testDestroyGridWithItemsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var grid = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 3
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );

      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "destroy"
      } );
      TestUtil.flush();

      assertTrue( grid.isDisposed() );
      assertTrue( item.isDisposed() );
      assertTrue( ObjectRegistry.getObject( "w4" ) == null );
      shell.destroy();
    },

    testCreateTreeWithStylesByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var styles = [ "FULL_SELECTION", "HIDE_SELECTION", "NO_SCROLL", "CHECK", "VIRTUAL", "MULTI" ];
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", styles );
      assertTrue( widget.getRenderConfig().fullSelection );
      assertTrue( widget.getRenderConfig().hideSelection );
      assertTrue( widget.getRenderConfig().hasCheckBoxes );
      assertTrue( widget._hasMultiSelection );
      assertFalse( widget._rowContainer.hasEventListeners( "mousewheel" ) );
      assertEquals( undefined, widget.getRenderConfig().selectionPadding );
      assertEquals( 16, widget.getRenderConfig().indentionWidth );
      assertEquals( 5, widget.getRenderConfig().checkBoxLeft );
      assertEquals( 16, widget.getRenderConfig().checkBoxWidth );
      shell.destroy();
      widget.destroy();
    },

    testCreateWithRowTemplateByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var template = [];

      MessageProcessor.processOperationArray( [
        "create",
         "w3",
        "rwt.widgets.Grid",
        {
          "style" : [],
          "parent" : "w2",
          "appearance": "table",
          "selectionPadding" : [ 2, 4 ],
          "rowTemplate" : template
        }
      ] );

      var grid = rwt.remote.ObjectRegistry.getObject( "w3" );
      var config = grid.getRenderConfig();
      assertTrue( config.rowTemplate instanceof rwt.widgets.util.Template );
      assertTrue( grid.hasState( "rowtemplate" ) );
      assertFalse( config.fullSelection );
      assertIdentical( template, config.rowTemplate._cells );
      shell.destroy();
    },

    // 442344: [Grid][RowTemplate] Crashes on load with JS error
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=442344
    testCreateWithRowTemplateAndBorderByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var template = [];

      MessageProcessor.processOperationArray( [
        "create",
         "w3",
        "rwt.widgets.Grid",
        {
          "style" : [ "BORDER" ],
          "parent" : "w2",
          "appearance": "table",
          "selectionPadding" : [ 2, 4 ],
          "rowTemplate" : template
        }
      ] );

      var grid = rwt.remote.ObjectRegistry.getObject( "w3" );
      var config = grid.getRenderConfig();
      assertTrue( config.rowTemplate instanceof rwt.widgets.util.Template );
      assertTrue( grid.hasState( "rowtemplate" ) );
      shell.destroy();
    },

    testSendSelectionEventForClickableRowTemplateCell : function() {
      var cellData = {
        "type" : "text",
        "bindingIndex" : 0,
        "name" : "bar",
        "selectable" : true,
        "left" : [ 0, 0 ],
        "top" : [ 0, 0 ],
        "width" : 1,
        "height" : 1
      };
      var template = new rwt.widgets.util.Template( [ cellData ] );
      var tree = this._createDefaultTree( false, false, "rowTemplate", template );
      TestUtil.fakeListener( tree, "Selection", true );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "foo" ] );
      rwt.remote.ObjectRegistry.add( "w4", item, itemHandler );
      TestUtil.flush();

      var node = tree._rowContainer.getRow( 0 ).$el.prop( "childNodes" )[ 0 ];
      TestUtil.clickDOM( node );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNull( message.findSetOperation( "w3", "selection" ) );
      assertEquals( "w4", message.findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( "cell", message.findNotifyProperty( "w3", "Selection", "detail" ) );
      var text = message.findNotifyProperty( "w3", "Selection", "text" );
      assertEquals( "bar", text );
      tree.destroy();
    },

    testSendSelectionEventForNonClickableRowTemplateCell : function() {
      var cellData = {
        "type" : "text",
        "bindingIndex" : 0,
        "name" : "bar",
        "left" : [ 0, 0 ],
        "top" : [ 0, 0 ],
        "width" : 1,
        "height" : 1
      };
      var template = new rwt.widgets.util.Template( [ cellData ] );
      var tree = this._createDefaultTree( false, false, "rowTemplate", template );
      tree.getRenderConfig().fullSelection = false;
      TestUtil.fakeListener( tree, "Selection", true );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "foo" ] );
      rwt.remote.ObjectRegistry.add( "w4", item, itemHandler );
      TestUtil.flush();

      var node = tree._rowContainer.$rows.get( 0 ).childNodes[ 0 ].childNodes[ 0 ];
      TestUtil.clickDOM( node );

      var message = TestUtil.getMessageObject();
      assertEquals( [ "w4" ], message.findSetProperty( "w3", "selection" ) );
      assertEquals( "w4", message.findNotifyProperty( "w3", "Selection", "item" ) );
      assertTrue( message.findNotifyProperty( "w3", "Selection", "detail" ) == null );
      tree.destroy();
    },

    testSendSelectionEventForClickableRowTemplateCell_withoutName : function() {
      var cellData = {
        "type" : "text",
        "bindingIndex" : 0,
        "selectable" : true,
        "left" : [ 0, 0 ],
        "top" : [ 0, 0 ],
        "width" : 1,
        "height" : 1
      };
      var template = new rwt.widgets.util.Template( [ cellData ] );
      var tree = this._createDefaultTree( false, false, "rowTemplate", template );
      TestUtil.fakeListener( tree, "Selection", true );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "foo" ] );
      rwt.remote.ObjectRegistry.add( "w4", item, itemHandler );
      TestUtil.flush();

      var node = tree._rowContainer.getRow( 0 ).$el.prop( "childNodes" )[ 0 ];
      TestUtil.clickDOM( node );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNull( message.findSetOperation( "w3", "selection" ) );
      assertEquals( "w4", message.findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( "cell", message.findNotifyProperty( "w3", "Selection", "detail" ) );
      assertTrue( message.findNotifyProperty( "w3", "Selection", "text" ) === undefined );
      tree.destroy();
    },

    testGridWithRowTempalteLimitsRowWidth : function() {
      var cellData = { "type" : "text", "left" : [ 0, 0 ], "top" : [ 0, 0 ], "width" : 1, "height" : 1 };
      var template = new rwt.widgets.util.Template( [ cellData ] );
      var tree = this._createDefaultTree( false, false, "rowTemplate", template );
      tree.setItemCount( 1 );

      tree.setWidth( 100 );
      tree.setItemMetrics( 0, 0, 150, 0, 0, 0, 0 );
      TestUtil.flush();

      var row = tree._rowContainer.getRow( 0 );
      assertEquals( 100, row.getWidth() );
    },

    testSetItemCountByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "itemCount" : 10 } );
      assertEquals( 10, widget._rootItem._children.length );
      shell.destroy();
      widget.destroy();
    },

    testSetItemHeightByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "itemHeight" : 20 } );
      assertEquals( 20, widget._itemHeight );
      shell.destroy();
      widget.destroy();
    },

    testSetItemMetricsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      var property = { "itemMetrics" : [ [ 0, 1, 2, 3, 4, 5, 6 ], [ 1, 11, 12, 13, 14, 15, 16 ] ] };
      TestUtil.protocolSet( "w3", property );
      assertEquals( 1, widget.getRenderConfig().itemLeft[ 0 ] );
      assertEquals( 2, widget.getRenderConfig().itemWidth[ 0 ] );
      assertEquals( 3, widget.getRenderConfig().itemImageLeft[ 0 ] );
      assertEquals( 4, widget.getRenderConfig().itemImageWidth[ 0 ] );
      assertEquals( 5, widget.getRenderConfig().itemTextLeft[ 0 ] );
      assertEquals( 6, widget.getRenderConfig().itemTextWidth[ 0 ] );
      assertEquals( 11, widget.getRenderConfig().itemLeft[ 1 ] );
      assertEquals( 12, widget.getRenderConfig().itemWidth[ 1 ] );
      assertEquals( 13, widget.getRenderConfig().itemImageLeft[ 1 ] );
      assertEquals( 14, widget.getRenderConfig().itemImageWidth[ 1 ] );
      assertEquals( 15, widget.getRenderConfig().itemTextLeft[ 1 ] );
      assertEquals( 16, widget.getRenderConfig().itemTextWidth[ 1 ] );
      shell.destroy();
      widget.destroy();
    },

    testSetColumnCountByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "columnCount" : 3 } );
      assertEquals( 3, widget.getRenderConfig().columnCount );
      shell.destroy();
      widget.destroy();
    },

    testSetColumnOrderByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var grid = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      var columns = [ {}, {} ];
      ObjectRegistry.add( "w4", columns[ 0 ] );
      ObjectRegistry.add( "w5", columns[ 1 ] );

      TestUtil.protocolSet( "w3", { "columnOrder" : [ "w4", "w5" ] } );

      expect( grid.getColumnOrder()[ 0 ] ).toBe( columns[ 0 ] );
      expect( grid.getColumnOrder()[ 1 ] ).toBe( columns[ 1 ] );
      shell.destroy();
      grid.destroy();
    },

    testSetTreeColumnByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "treeColumn" : 3 } );
      assertEquals( 3, widget.getRenderConfig().treeColumn );
      shell.destroy();
      widget.destroy();
    },

    testSetFixedColumnsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Grid",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "appearance": "tree",
          "selectionPadding" : [ 2, 4 ],
          "indentionWidth" : 16,
          "checkBoxMetrics" : [ 5, 16 ],
          "splitContainer" : true
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      TestUtil.protocolSet( "w3", { "fixedColumns" : 3 } );
      assertTrue( widget.getRowContainer() instanceof rwt.widgets.util.GridRowContainerWrapper );
      assertEquals( 3, widget.getRowContainer()._fixedColumns );
      shell.destroy();
      widget.destroy();
    },

    testSetHeaderHeightByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "headerHeight" : 30 } );
      assertEquals( 30, widget._headerHeight );
      shell.destroy();
      widget.destroy();
    },

    testSetHeaderVisibleByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );

      TestUtil.protocolSet( "w3", { "headerVisible" : true } );
      TestUtil.flush();

      assertTrue( widget._header.isSeeable() );
      shell.destroy();
      widget.destroy();
    },

    testSetFooterHeightByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "footerHeight" : 30 } );

      assertEquals( 30, widget._footerHeight );

      shell.destroy();
      widget.destroy();
    },

    testSetFooterVisibleByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );

      TestUtil.protocolSet( "w3", { "footerVisible" : true } );
      TestUtil.flush();

      assertTrue( widget._footer.isSeeable() );
      shell.destroy();
      widget.destroy();
    },

    testSetLinesVisibleByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "linesVisible" : true } );
      assertTrue( widget.getRenderConfig().linesVisible );
      shell.destroy();
      widget.destroy();
    },

    testSetTopItemIndexByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      widget.setItemCount( 10 );
      widget.setItemHeight( 20 );
      TestUtil.flush();
      TestUtil.protocolSet( "w3", { "topItemIndex" : 3 } );
      TestUtil.flush();

      assertEquals( 3, widget._vertScrollBar.getValue() );
      assertEquals( 3, widget.getTopItemIndex() );
      shell.destroy();
      widget.destroy();
    },

    testSetTopItemIndexToZeroOnEmptyGrid : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      widget.setItemCount( 10 );
      widget.setItemHeight( 20 );
      TestUtil.flush();
      TestUtil.protocolSet( "w3", { "topItemIndex" : 3 } );

      widget.setItemCount( 0 );
      TestUtil.protocolSet( "w3", { "topItemIndex" : 0 } );
      TestUtil.flush();

      assertEquals( 0, widget._vertScrollBar.getValue() );
      shell.destroy();
      widget.destroy();
    },

    testSetFocusItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      this._createTreeItemByProtocol( "w4", "w3", 0 );
      var item2 = this._createTreeItemByProtocol( "w5", "w3", 1 );
      TestUtil.protocolSet( "w3", { "focusItem" : "w5" } );
      assertIdentical( item2, widget._focusItem );
      shell.destroy();
      widget.destroy();
    },

    testSetScrollLeftByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      widget.setItemCount( 1 );
      widget.setItemMetrics( 0, 0, 150, 0, 0, 0, 0 );
      TestUtil.flush();

      TestUtil.protocolSet( "w3", { "scrollLeft" : 10 } );

      assertEquals( 10, widget._horzScrollBar.getValue() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [ "MULTI" ] );
      widget.setItemCount( 3 );
      var item1 = this._createTreeItemByProtocol( "w4", "w3", 0 );
      var item2 = this._createTreeItemByProtocol( "w5", "w3", 1 );
      widget.selectItem( item1 );
      var item3 = this._createTreeItemByProtocol( "w6", "w3", 2 );
      TestUtil.protocolSet( "w3", { "selection" : [ "w4", "w6" ] } );
      assertTrue( widget.isItemSelected( item1 ) );
      assertFalse( widget.isItemSelected( item2 ) );
      assertTrue( widget.isItemSelected( item3 ) );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionWithDisposeByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [ "MULTI" ] );
      widget.setItemCount( 3 );
      var item1 = this._createTreeItemByProtocol( "w4", "w3", 0 );
      this._createTreeItemByProtocol( "w5", "w3", 1 );
      this._createTreeItemByProtocol( "w6", "w3", 2 );

      TestUtil.protocolSet( "w3", { "selection" : [ "w4", "w5" ] } );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      TestUtil.protocolSet( "w3", { "selection" : [] } );
      TestUtil.flush();

      assertTrue( item1.isDisposed() );
      shell.destroy();
      widget.destroy();
    },

    testSetScrollBarsVisibleByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "scrollBarsVisible" : [ true, true ] } );
      assertTrue( widget._horzScrollBar.getDisplay() );
      assertTrue( widget._vertScrollBar.getDisplay() );
      shell.destroy();
      widget.destroy();
    },

    testSetAlwaysHideSelectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "alwaysHideSelection" : true } );
      assertTrue( widget.getRenderConfig().alwaysHideSelection );
      shell.destroy();
      widget.destroy();
    },

    testSetEnableCellToolTipByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "enableCellToolTip" : true } );
      assertTrue( widget.getCellToolTipsEnabled() );
      shell.destroy();
      widget.destroy();
    },

    testSetCellToolTipText_isNotEscaped_withToolTipMarkupEnabled : function() {
      var tree = this._createDefaultTree( false, false );
      tree.setCellToolTipsEnabled( true );
      tree.setUserData( "toolTipMarkupEnabled", true );
      this._fillTree( tree, 10 );
      TestUtil.flush();
      var row = tree.getRowContainer().getRow( 0 );

      TestUtil.fakeMouseEvent( row, "mouseover", 10, 10 );
      TestUtil.fakeMouseEvent( row, "mousemove", 10, 10 );
      TestUtil.forceInterval( rwt.widgets.base.WidgetToolTip.getInstance()._showTimer );
      tree.setCellToolTipText( "<i>foo</i>" );

      var labelObject = rwt.widgets.base.WidgetToolTip.getInstance()._label;
      assertEquals( "<i>foo</i>", labelObject.getCellContent( 0 ) );
      tree.destroy();
    },

    testSetCellToolTipTextByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      shell.setLocation( 0, 0 );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      widget.setLocation( 0, 0 );
      TestUtil.protocolSet( "w3", { "enableCellToolTip" : true } );
      this._fillTree( widget, 10 );
      TestUtil.flush();
      var row = widget.getRowContainer().getRow( 0 );

      TestUtil.fakeMouseEvent( row, "mouseover", 10, 10 );
      TestUtil.fakeMouseEvent( row, "mousemove", 10, 10 );
      TestUtil.forceInterval( rwt.widgets.base.WidgetToolTip.getInstance()._showTimer );
      TestUtil.protocolSet( "w3", { "cellToolTipText" : "foo && <> \"\n bar" } );

      var labelObject = rwt.widgets.base.WidgetToolTip.getInstance()._label;
      assertEquals( "foo &amp;&amp; &lt;&gt; &quot;<br/> bar", labelObject.getCellContent( 0 ) );
      assertEquals( "", widget.getRowContainer().getToolTipText() );
      shell.destroy();
    },

    testSetCellToolTipTextByProtocol_withFixedColumns : function() {
      var widget = this._createDefaultTree( false, false, "fixedColumns", 1 );
      TestUtil.protocolSet( "w3", { "enableCellToolTip" : true } );
      this._fillTree( widget, 10 );
      TestUtil.flush();
      var row = widget.getRowContainer().getSubContainer( 0 ).getRow( 0 );

      TestUtil.fakeMouseEvent( row, "mouseover", 10, 10 );
      TestUtil.fakeMouseEvent( row, "mousemove", 10, 10 );
      TestUtil.forceInterval( rwt.widgets.base.WidgetToolTip.getInstance()._showTimer );
      TestUtil.protocolSet( "w3", { "cellToolTipText" : "foo" } );

      var labelObject = rwt.widgets.base.WidgetToolTip.getInstance()._label;
      assertEquals( "foo", labelObject.getCellContent( 0 ) );
      assertEquals( "", widget.getRowContainer().getSubContainer( 0 ).getToolTipText() );
      widget.destroy();
    },

    testSetCellToolTipTextByProtocol_ToolTipHasBeenUnbound : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      shell.setLocation( 0, 0 );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      widget.setLocation( 0, 0 );
      TestUtil.protocolSet( "w3", { "enableCellToolTip" : true } );
      this._fillTree( widget, 10 );
      TestUtil.flush();
      var row = widget.getRowContainer().getRow( 0 );
      TestUtil.fakeMouseEvent( row, "mouseover", 10, 10 );
      TestUtil.fakeMouseEvent( row, "mousemove", 10, 10 );
      TestUtil.forceInterval( rwt.widgets.base.WidgetToolTip.getInstance()._showTimer );

      TestUtil.fakeMouseEvent( row, "mouseout", 10, 10 );
      TestUtil.fakeMouseEvent( shell, "mouseover", 10, 10 );
      TestUtil.fakeMouseEvent( shell, "mousemove", 10, 10 );
      TestUtil.forceInterval( rwt.widgets.base.WidgetToolTip.getInstance()._hideTimer );
      TestUtil.protocolSet( "w3", { "cellToolTipText" : "foo" } );

      assertFalse( rwt.widgets.base.WidgetToolTip.getInstance().isSeeable() );
      assertNull( rwt.widgets.base.WidgetToolTip.getInstance().getBoundToWidget() );
      shell.destroy();
    },

    testSetCellToolTipTextByProtocol_PositionIsColumnAligned : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      shell.setLocation( 1, 0 );
      shell.setBorder( null );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      widget.setLocation( 2, 0 );
      widget.setBorder( null );
      TestUtil.protocolSet( "w3", { "enableCellToolTip" : true } );
      this._fillTree( widget, 10 );
      widget.setColumnCount( 2 );
      widget.setItemMetrics( 0, 0, 20, 0, 0, 0, 20, 0, 10 );
      widget.setItemMetrics( 1, 20, 20, 0, 0, 20, 20, 0, 10 );
      TestUtil.flush();
      var row = widget.getRowContainer().getRow( 0 );

      TestUtil.fakeMouseEvent( row, "mouseover", 30, 10 );
      TestUtil.fakeMouseEvent( row, "mousemove", 30, 10 );
      TestUtil.forceInterval( rwt.widgets.base.WidgetToolTip.getInstance()._showTimer );
      TestUtil.protocolSet( "w3", { "cellToolTipText" : "foo" } );

      var left = rwt.widgets.base.WidgetToolTip.getInstance().getLeft();
      assertEquals( 1 + 2 + 20 + 4, left );
      shell.destroy();
    },

    testSetCellToolTipTextByProtocol_PositionIsColumnAligned_withFixedColumns : function() {
      var widget = this._createDefaultTree( false, false, "fixedColumns", 1 );
      widget.setLocation( 2, 0 );
      widget.setBorder( null );
      TestUtil.protocolSet( "w3", { "enableCellToolTip" : true } );
      this._fillTree( widget, 10 );
      widget.setColumnCount( 2 );
      widget.setItemMetrics( 0, 0, 20, 0, 0, 0, 20, 0, 10 );
      widget.setItemMetrics( 1, 20, 20, 0, 0, 20, 20, 0, 10 );
      TestUtil.flush();
      var rowContainer = widget.getRowContainer();
      rowContainer._splitOffset = 20;
      var row = rowContainer.getSubContainer( 1 ).getRow( 0 );

      TestUtil.fakeMouseEvent( row, "mouseover", 30, 10 );
      TestUtil.fakeMouseEvent( row, "mousemove", 30, 10 );
      TestUtil.forceInterval( rwt.widgets.base.WidgetToolTip.getInstance()._showTimer );
      TestUtil.protocolSet( "w3", { "cellToolTipText" : "foo" } );

      var left = rwt.widgets.base.WidgetToolTip.getInstance().getLeft();
      assertEquals( 2 + 4, left );
      widget.destroy();
    },

    testSetCellToolTipTextByProtocol_NoCrashWhenAnotherToolTipIsVisible : function() {
      rwt.widgets.util.GridCellToolTipSupport._cell = [ null, null, null ];
      var shell = TestUtil.createShellByProtocol( "w2" );
      rwt.widgets.base.WidgetToolTip.setToolTipText( shell, "foo" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      TestUtil.protocolSet( "w3", { "enableCellToolTip" : true } );
      this._fillTree( widget, 10 );
      widget.setColumnCount( 2 );
      widget.setItemMetrics( 0, 0, 20, 0, 0, 0, 20, 0, 10 );
      widget.setItemMetrics( 1, 20, 20, 0, 0, 20, 20, 0, 10 );
      TestUtil.flush();
      var row = widget.getRowContainer().getRow( 0 );

      TestUtil.fakeMouseEvent( shell, "mouseover", 30, 10 );
      rwt.widgets.base.WidgetToolTip.getInstance()._onshowtimer();
      TestUtil.fakeMouseEvent( row, "mouseover", 30, 10 );
      TestUtil.fakeMouseEvent( row, "mousemove", 30, 10 ); // can crash if _cell is not set

      shell.destroy();
    },

    testSetMarkupEnabledByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Grid",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "appearance": "tree",
          "markupEnabled" : true
        }
      } );
      var widget = rwt.remote.ObjectRegistry.getObject( "w3" );
      assertTrue( widget.getRenderConfig().markupEnabled );
      shell.destroy();
      widget.destroy();
    },

    testSetSortDirectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      var column = new rwt.widgets.GridColumn( widget );
      rwt.remote.ObjectRegistry.add( "w4", column, columnHandler );
      widget.setSortColumn( column );
      TestUtil.protocolSet( "w3", { "sortDirection" : "up" } );
      assertEquals( "up", widget._sortDirection );
      shell.destroy();
      column.dispose();
      widget.destroy();
    },

    testSetSortColumnByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      var column = new rwt.widgets.GridColumn( widget );
      rwt.remote.ObjectRegistry.add( "w4", column, columnHandler );
      widget.setSortDirection( "down" );
      TestUtil.protocolSet( "w3", { "sortColumn" : "w4" } );
      assertIdentical( column, widget._sortColumn );
      shell.destroy();
      column.dispose();
      widget.destroy();
    },

    testCreate : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      assertTrue( tree instanceof rwt.widgets.Grid );
      tree.destroy();
    },

    testDefaultProperties : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      assertEquals( "default", tree.getCursor() );
      tree.destroy();
    },

    testItemHeight : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      assertEquals( 16, tree._rowContainer._rowHeight );
      assertEquals( 1, tree._vertScrollBar._increment );
      assertEquals( 16, tree.getRootItem().getDefaultHeight() );
      tree.setItemHeight( 23 );
      assertEquals( 23, tree._rowContainer._rowHeight );
      assertEquals( 1, tree._vertScrollBar._increment );
      assertEquals( 23, tree.getRootItem().getDefaultHeight() );
      tree.destroy();
    },

    testSetItemMetrics : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      var item = this._createItem();
      item.setTexts( [ "Test", "Test2" ] );
      tree.setItemMetrics( 0, 0, 0, 0, 0, 0, 0 );
      tree.setItemMetrics( 1, 50, 40, 52, 13, 65, 25 );
      assertEquals( 50, tree._config.itemLeft[ 1 ] );
      assertEquals( 40, tree._config.itemWidth[ 1 ] );
      assertEquals( 52, tree._config.itemImageLeft[ 1 ] );
      assertEquals( 13, tree._config.itemImageWidth[ 1 ] );
      assertEquals( 65, tree._config.itemTextLeft[ 1 ] );
      assertEquals( 25, tree._config.itemTextWidth[ 1 ] );
      tree.destroy();
    },

    testSetIndentionWidths : function() {
      var tree = new rwt.widgets.Grid( {
        "appearance": "tree",
        "indentionWidth" : 16
      } );
      assertEquals( 16, tree.getRenderConfig().indentionWidth );
      tree.destroy();
    },

    testChildren : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      var child1 = rwt.widgets.GridItem.createItem( tree, 0 );
      var child2 = rwt.widgets.GridItem.createItem( tree, 1 );
      assertEquals( [ child1, child2 ], tree.getRootItem()._children );
      tree.destroy();
    },

    testSetItemCount : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setItemCount( 44 );
      assertEquals( 44, tree.getRootItem()._children.length );
      tree.destroy();
    },

    testSimpleInternalLayout : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      this._fakeAppearance();
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );
      TestUtil.flush();
      var node = tree._rowContainer.getElement();
      assertIdentical( tree._getTargetNode(), node.parentNode );
      assertEquals( 500, parseInt( node.style.height, 10 ) );
      assertEquals( 600, parseInt( node.style.width, 10 ) );
      tree.destroy();
    },

    testSimpleInternalLayoutResize : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      this._fakeAppearance();
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );
      TestUtil.flush();
      tree.setHeight( 400 );
      tree.setWidth( 700 );
      TestUtil.flush();
      var node = tree._rowContainer.getElement();
      assertEquals( 400, parseInt( node.style.height, 10 ) );
      assertEquals( 700, parseInt( node.style.width, 10 ) );
      tree.destroy();
    },

    testRenderItemsOnResizeWidth : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      this._fakeAppearance();
      tree.addToDocument();
      tree.setWidth( 300 );
      tree.setHeight( 300 );
      TestUtil.flush();
      var log = [];
      var row = tree.getRowContainer().getRow( 0 );
      row.addEventListener( "itemRendered", function( event ) {
        log.push( event );
      } );
      tree.setWidth( 700 );
      TestUtil.flush();

      assertEquals( 1, log.length );
      tree.destroy();
    },

    testSimpleInternalLayoutWithBorder : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      this._fakeAppearance();
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );
      TestUtil.flush();
      var border = new rwt.html.Border( 4, "solid", null );
      tree.setBorder( border );
      TestUtil.flush();
      var node = tree._rowContainer.getElement();
      assertIdentical( tree._getTargetNode(), node.parentNode );
      assertEquals( 492, parseInt( node.style.height, 10 ) );
      assertEquals( 592, parseInt( node.style.width, 10 ) );
      tree.destroy();
    },

    testTreeRowSmallerClientArea : function() {
      var tree = this._createDefaultTree();
      tree.setWidth( 600 );
      tree.setScrollBarsVisible( true, true );
      TestUtil.flush();
      var row = tree._rowContainer.getRow( 0 );
      var expected = 600 - tree._vertScrollBar.getWidth();
      assertEquals( expected, row.getWidth() );
      tree.destroy();
    },

    testChangeTreeRowBounds : function() {
      var tree = this._createDefaultTree();
      var sample = tree._rowContainer.getRow( 10 ).$el.get( 0 );
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

    testRenderFirstLayer : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 10 );
      TestUtil.flush();
      var sample = tree._rowContainer.getRow( 9 ).$el.get( 0 );
      assertEquals( 1, sample.childNodes.length );
      assertEquals( "Test9", sample.childNodes[ 0 ].innerHTML );
      var bounds = getElementBounds( sample );
      assertEquals( 0, bounds.left );
      assertEquals( 180, bounds.top );
      assertEquals( 500, bounds.width );
      assertEquals( 20, bounds.height );
      tree.destroy();
    },

    testRenderBeforeCreate : function() {
      var tree = this._createDefaultTree( true );
      this._fillTree( tree, 10 );
      TestUtil.flush();
      var sample = tree._rowContainer.getRow( 9 ).$el.get( 0 );
      assertEquals( 1, sample.childNodes.length );
      tree.destroy();
    },

    testRenderMoreItemsThanRows : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();
      var clientArea = tree._rowContainer;
      assertEquals( 26, clientArea.getRowCount() );
      var sample = clientArea.getRow( 25 ).$el.get( 0 );
      assertEquals( 1, sample.childNodes.length );
      assertEquals( "Test25", sample.childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testRenderRemoveItem : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 10 );
      var item;
      for( var i = 0; i < 10; i++ ) {
        item = this._createItem( tree.getRootItem(), i );
        item.setTexts( [ "Test" + i ] );
      }
      TestUtil.flush();

      item.dispose();
      tree.setItemCount( 9 ); // order is relevant: dispose before setItemCount
      TestUtil.flush();

      var sample = tree._rowContainer.getRow( 9 ).$el.get( 0 );
      assertEquals( 1, sample.childNodes.length );
      assertEquals( "", sample.childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testRenderRemoveAddItem : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 10 );
      var item = tree._rootItem.getLastChild();
      TestUtil.flush();
      item.dispose();
      item = this._createItem( tree.getRootItem(), 9 );
      item.setTexts( [ "newItem" ] );
      TestUtil.flush();
      var sample = tree._rowContainer.getRow( 9 ).$el.get( 0 );
      assertEquals( 1, sample.childNodes.length );
      assertEquals( "newItem", sample.childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testRenderRemoveItemVirtual : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 10 );
      var item = tree._rootItem.getLastChild();
      TestUtil.flush();
      item.dispose();
      TestUtil.flush();
      var sample = tree._rowContainer.getRow( 9 ).$el.get( 0 );
      assertEquals( 1, sample.childNodes.length );
      assertEquals( "...", sample.childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testRenderRemoveFirstItem : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 10 );
      this._fillTree( tree, 10 );
      var item = tree._rootItem._children[ 0 ];
      assertEquals( "Test0", item.getText( 0 ) );
      TestUtil.flush();

      item.dispose();
      tree.setItemCount( 9 );
      TestUtil.flush();

      item = tree._rootItem._children[ 0 ];
      assertEquals( "Test1", item.getText( 0 ) );
      var text0 = tree._rowContainer.getRow( 0 ).$el.prop( "childNodes" )[ 0 ].innerHTML;
      var text8 = tree._rowContainer.getRow( 8 ).$el.prop( "childNodes" )[ 0 ].innerHTML;
      var text9 = tree._rowContainer.getRow( 9 ).$el.prop( "childNodes" )[ 0 ].innerHTML;
      assertEquals( "Test1", text0 );
      assertEquals( "Test9", text8 );
      assertEquals( "", text9 );
      tree.destroy();
    },

    testRenderMultipleLayer : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 10 );
      for( var i = 0; i < 10; i++ ) {
        var item = this._createItem( tree.getRootItem(), i );
        item.setTexts( [ "Test" + i ] );
        item.setItemCount( 1 );
        var subitem = this._createItem( item, 0 );
        subitem.setTexts( [ "Test" + i + "sub" ] );
      }
      var items = tree.getRootItem()._children;
      items[ 1 ].setExpanded( true );
      TestUtil.flush();
      var rowNodes = tree._rowContainer.$rows.prop( "childNodes" );
      assertEquals( "Test1", rowNodes[ 1 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test1sub", rowNodes[ 2 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test2", rowNodes[ 3 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testRenderExpand : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 10 );
      for( var i = 0; i < 10; i++ ) {
        var item = this._createItem( tree.getRootItem(), i );
        item.setTexts( [ "Test" + i ] );
        item.setItemCount( 1 );
        var subitem = this._createItem( item, 0 );
        subitem.setTexts( [ "Test" + i + "sub" ] );
      }
      var items = tree.getRootItem()._children;
      TestUtil.flush();
      items[ 1 ].setExpanded( true );
      TestUtil.flush();
      var rowNodes = tree._rowContainer.$rows.prop( "childNodes" );
      assertEquals( "Test1", rowNodes[ 1 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test1sub", rowNodes[ 2 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test2", rowNodes[ 3 ].childNodes[ 0 ].innerHTML );
      items[ 1 ].setExpanded( false );
      TestUtil.flush();
      assertEquals( "Test1", rowNodes[ 1 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test2", rowNodes[ 2 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testIndentRenderOnAddRemoveChild : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeAppearance( "tree-row-indent", {
        style : function( states ) {
          var children = states.collapsed || states.expanded;
          return {
            "backgroundImage" : children ? "children.gif" : "empty.gif"
          };
        }
      } );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "foo" ] );
      TestUtil.flush();
      var node = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      assertTrue( node.innerHTML.indexOf( "empty.gif" ) != -1 );
      item.setItemCount( 1 );
      var item2 = this._createItem( item, 0 );
      TestUtil.flush();
      assertTrue( node.innerHTML.indexOf( "children.gif" ) != -1 );
      item2.dispose();
      item.setItemCount( 0 );
      TestUtil.flush();
      assertTrue( node.innerHTML.indexOf( "empty.gif" ) != -1 );
      tree.destroy();
    },

    testClickOnExpandSymbol : function() {
      var tree = this._createDefaultTree();
       TestUtil.fakeAppearance( "tree-row-indent",  {
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
      this._fillTree( tree, 10, true );
      TestUtil.flush();
      var rows = tree._rowContainer.$rows.prop( "childNodes" );
      TestUtil.clickDOM( rows[ 1 ] ); // nothing happens:
      assertEquals( "Test2", rows[ 2 ].childNodes[ 1 ].innerHTML );
      TestUtil.clickDOM( rows[ 1 ].childNodes[ 0 ] );
      assertEquals( "Test1sub", rows[ 2 ].childNodes[ 1 ].innerHTML );
      tree.destroy();
    },

    testSetTopItemIndex : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );

      rwt.remote.EventUtil.setSuspended( true );
      tree.setTopItemIndex( 55 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();

      var area = tree._rowContainer.$rows.get( 0 );
      assertEquals( 55, tree.getTopItemIndex() );
      assertEquals( 55, tree._vertScrollBar.getValue() );
      assertEquals( "Test55", area.childNodes[ 0 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test64", area.childNodes[ 9 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testSetTopItemIndexWithCustomItemHeight : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      tree.getRootItem().getChild( 0 ).setHeight( 40 );

      rwt.remote.EventUtil.setSuspended( true );
      tree.setTopItemIndex( 55 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();

      var area = tree._rowContainer.$rows.get( 0 );
      assertEquals( 55, tree._vertScrollBar.getValue() );
      assertEquals( 55, tree.getTopItemIndex() );
      assertEquals( "Test55", area.childNodes[ 0 ].childNodes[ 0 ].innerHTML );
      assertEquals( "Test64", area.childNodes[ 9 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testSetTopItemIndexWithMultipleCustomItemHeight : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      for( var i = 0; i < 100; i++ ) {
        tree.getRootItem().getChild( i ).setHeight( 40 );
      }

      rwt.remote.EventUtil.setSuspended( true );
      tree.setTopItemIndex( 55 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();

      var area = tree._rowContainer.$rows.get( 0 );
      assertEquals( 55, tree.getTopItemIndex() );
      assertEquals( "Test55", area.childNodes[ 0 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testScrollIntoView : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();
      tree.scrollIntoView( tree.getRootItem().findItemByFlatIndex( 55 ) );
      TestUtil.flush();

      var area = tree._rowContainer.$rows.get( 0 );
      assertEquals( 31, tree._vertScrollBar.getValue() );
      // TODO: re-enable when scrollbar layout or maximum is fixed
      //assertEquals( 159, parseInt( tree._vertScrollBar._thumb.getElement().style.top, 10 ) );
      assertEquals( "Test31", area.childNodes[ 0 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testScrollIntoView_doesNotRenderImmediately : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();

      tree.scrollIntoView( tree.getRootItem().findItemByFlatIndex( 55 ) );

      var area = tree._rowContainer.$rows.get( 0 );
      assertEquals( 31, tree._vertScrollBar.getValue() );
      assertEquals( "Test0", area.childNodes[ 0 ].childNodes[ 0 ].innerHTML );
      TestUtil.flush();
      tree.destroy();
    },

    testScrollIntoViewInternal_rendersImmediately : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();

      tree._scrollIntoView( 55, tree.getRootItem().findItemByFlatIndex( 55 ) ); // NOTE: NO Flush!

      var area = tree._rowContainer.$rows.get( 0 );
      assertEquals( 31, tree._vertScrollBar.getValue() );
      //assertEquals( 159, parseInt( tree._vertScrollBar._thumb.getElement().style.top, 10 ) );
      assertEquals( "Test31", area.childNodes[ 0 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testSetTopItemAndExpandClick : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeAppearance( "tree-row-indent",  {
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
      this._fillTree( tree, 100 );
      var topItem = tree._rootItem.getChild( 55 );
      topItem.setItemCount( 1 );
      var child = this._createItem( topItem, 0 );
      child.setTexts( [ "subitem" ] );
      rwt.remote.EventUtil.setSuspended( true );
      tree.setTopItemIndex( 55 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var area = tree._rowContainer.$rows.get( 0 );
      TestUtil.clickDOM( area.childNodes[ 0 ].childNodes[ 0 ] );
      assertEquals( "Test55", area.childNodes[ 0 ].childNodes[ 1 ].innerHTML );
      assertEquals( "subitem", area.childNodes[ 1 ].childNodes[ 0 ].innerHTML );
      tree.destroy();
    },

    testScrollBarsDefaultProperties : function() {
      var tree = this._createDefaultTree();
      assertFalse( tree._horzScrollBar.getVisibility() );
      assertFalse( tree._vertScrollBar.getVisibility() );
      tree.destroy();
    },

    testScrollBarsPreventDragStart : function() {
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var tree = this._createDefaultTree();
      var log = [];
      var loghandler = function( event ) { log.push( event ); };
      var drag = function( node ) {
        TestUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
        TestUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 25, 15 );
        TestUtil.fakeMouseEventDOM( node, "mouseup", leftButton, 25, 15 );
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
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( true, true );
      TestUtil.flush();
      assertTrue( tree._horzScrollBar.getVisibility() );
      assertTrue( tree._vertScrollBar.getVisibility() );
      tree.destroy();
    },

    testSetScrollBarsVisibleResetValue : function() {
      var tree = this._createDefaultTree();
      rwt.remote.ObjectRegistry.add( "wtest", tree, gridHandler );
      tree.setScrollBarsVisible( true, true );
      TestUtil.flush();
      tree._horzScrollBar.setValue( 10 );
      tree._vertScrollBar.setValue( 1 );
      TestUtil.initRequestLog();
      rwt.remote.EventUtil.setSuspended( true );
      tree.setScrollBarsVisible( false, false );
      rwt.remote.EventUtil.setSuspended( false );
      assertEquals( 0, tree._horzScrollBar.getValue() );
      assertEquals( 0, tree._vertScrollBar.getValue() );
      rwt.remote.Connection.getInstance().send();
      assertEquals( 0, TestUtil.getMessageObject().findSetProperty( "wtest", "scrollLeft" ) );
      tree.destroy();
    },

    testVerticalScrollBarLayout : function() {
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( false, true );
      TestUtil.flush();
      var area = TestUtil.getElementBounds( tree._rowContainer.getElement() );
      var vertical = TestUtil.getElementBounds( tree._vertScrollBar.getElement() );
      assertEquals( 500, vertical.height );
      assertEquals( 0, vertical.right);
      assertEquals( 0, vertical.bottom );
      assertEquals( 500, area.height );
      assertTrue( area.width == 500 - vertical.width );
      tree.destroy();
    },

    testHorizontalScrollBarLayout : function() {
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( true, false );
      TestUtil.flush();
      var area = TestUtil.getElementBounds( tree._rowContainer.getElement() );
      var horizontal = TestUtil.getElementBounds( tree._horzScrollBar.getElement() );
      assertEquals( 500, horizontal.width );
      assertEquals( 0, horizontal.bottom );
      assertEquals( 0, horizontal.right );
      assertEquals( 500, area.width );
      assertTrue( area.height == 500 - horizontal.height );
      tree.destroy();
    },

    testBothScrollBarsLayout : function() {
      var tree = this._createDefaultTree();
      tree.setScrollBarsVisible( true, true );
      TestUtil.flush();
      var area = TestUtil.getElementBounds( tree._rowContainer.getElement() );
      var horizontal = TestUtil.getElementBounds( tree._horzScrollBar.getElement() );
      var vertical = TestUtil.getElementBounds( tree._vertScrollBar.getElement() );
      var height = 500 - horizontal.height;
      var width = 500 - vertical.width;
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
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();
      assertEquals( 100, tree._vertScrollBar.getMaximum() );
      tree.destroy();
    },

    testScrollThumbHeight : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );

      TestUtil.flush();
      assertEquals( 100, tree._vertScrollBar.getMaximum() );
      tree.destroy();
    },

    testScrollHeightWithFooter : function() {
      var tree = this._createDefaultTree();
      tree.setFooterHeight( 50 );
      tree.setFooterVisible( true );
      this._fillTree( tree, 100 );
      TestUtil.flush();
      assertEquals( 100, tree._vertScrollBar.getMaximum() );
      tree.destroy();
    },

    testUpdateScrollHeightOnExpand : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      var lastItem = tree._rootItem.getChild( 99 );
      lastItem.setItemCount( 1 );
      this._createItem( lastItem, 0 );
      TestUtil.flush();
      assertEquals( 100, tree._vertScrollBar.getMaximum() );
      lastItem.setExpanded( true );
      TestUtil.flush();
      assertEquals( 101, tree._vertScrollBar.getMaximum() );
      tree.destroy();
    },

    testUpdateScrollOnItemHeightChange : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();
      assertEquals( 100, tree._vertScrollBar.getMaximum() );
      tree.setItemHeight( 40 );
      TestUtil.flush();
      assertEquals( 100, tree._vertScrollBar.getMaximum() );
      tree.destroy();
    },

    testUpdateScrollHeightOnCustomItemHeight : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();
      assertEquals( 100, tree._vertScrollBar.getMaximum() );

      for( var i = 0; i < 100; i++ ) {
        tree.getRootItem().getChild( i ).setHeight( 40 );
      }

      TestUtil.flush();
      assertEquals( 100, tree._vertScrollBar.getMaximum() );
      tree.destroy();
    },

    testScrollVertically_dragScrollThumb_releaseOnOtherWidget : function() {
      var tree = this._createDefaultTree();
      tree.getRenderConfig().markupEnabled = true;
      this._fillTree( tree, 100 );
      tree._vertScrollBar.setVisibility( true );
      var button = new rwt.widgets.Button( "push" );
      button.setSpace( 550, 50, 0, 25 );
      button.addToDocument();
      TestUtil.flush();
      var thumbNode = tree._vertScrollBar._thumb._getTargetNode();
      var buttonNode = button._getTargetNode();

      TestUtil.fakeMouseEventDOM( thumbNode, "mousedown", rwt.event.MouseEvent.buttons.left );
      TestUtil.fakeMouseEventDOM( buttonNode, "mouseup", rwt.event.MouseEvent.buttons.left );

      // ensures no exception is thrown when executing _findHyperlink on mouseup
      button.destroy();
      tree.destroy();
    },

    testScrollVerticallyOnlyOneLayer : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( true );
      tree._vertScrollBar.setValue( 50 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var itemNode = tree._rowContainer.$rows.get( 0 ).firstChild;
      assertEquals( "Test50", itemNode.firstChild.innerHTML );
      tree.destroy();
    },

    testScrollHeightWithHeaderBug : function() {
      var tree = this._createDefaultTree();
      tree.setHeaderHeight( 20 );
      tree.setHeaderVisible( true );
      tree.setHeight( 490 );
      this._fillTree( tree, 100 );
      TestUtil.flush();
      var maxScroll = tree._vertScrollBar.getMaximum() - tree._vertScrollBar.getThumb();

      rwt.remote.EventUtil.setSuspended( true );
      tree._vertScrollBar.setValue( maxScroll );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();

      var itemNode = tree._rowContainer.$rows.get( 0 ).firstChild;
      // 100 - ( 490 - 20 ) / 20 = 77
      assertEquals( "Test77", itemNode.firstChild.innerHTML );
      tree.destroy();
    },

    testScrollVerticallyMultipleLayer : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100, true, true );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( true );
      tree._vertScrollBar.setValue( 51 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var itemNode = tree._rowContainer.$rows.get( 0 ).firstChild;
      assertEquals( "Test51", itemNode.firstChild.innerHTML );
      tree.destroy();
    },

    testScrollBackwardsVerticallyMultipleLayer : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100, true, true );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( true );
      tree._vertScrollBar.setValue( 70 );
      tree._vertScrollBar.setValue( 51 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var itemNode = tree._rowContainer.$rows.get( 0 ).firstChild;
      assertEquals( "Test51", itemNode.firstChild.innerHTML );
      tree.destroy();
    },

    testScrollBackwardsVerticallyMultipleLayer2 : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100, true, true );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( true );
      tree._vertScrollBar.setValue( 52 );
      tree._vertScrollBar.setValue( 51 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var itemNode = tree._rowContainer.$rows.get( 0 ).firstChild;
      assertEquals( "Test51", itemNode.firstChild.innerHTML );
      tree.destroy();
    },

    testScrollBugExpanded : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100, true, true );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( true );
      tree._vertScrollBar.setValue( 5 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var itemNode = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      assertEquals( "Test5", itemNode.firstChild.innerHTML );
      tree.destroy();
    },

    testScrollBugCollapsed : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100, true );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( true );
      tree._vertScrollBar.setValue( 5 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var itemNode = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      assertEquals( "Test5", itemNode.firstChild.innerHTML );
      tree.destroy();
    },

    testDestroy : function() {
      var tree = this._createDefaultTree( false, false, "virtual" );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      tree._onShowResizeLine( { "position" : 0 } );
      tree.setHeaderHeight( 20 );
      tree.setHeaderVisible( true );
      tree.setFooterHeight( 20 );
      tree.setFooterVisible( true );
      TestUtil.flush();
      tree.setFocusItem( item );
      tree._shiftSelectItem( item );
      var row = tree._rowContainer.getRow( 0 );
      TestUtil.hoverFromTo( document.body, row.$el.get( 0 ) );
      var area = tree._rowContainer;
      var dummy = tree._header._dummyColumn;
      var hscroll = tree._horzScrollBar;
      var vscroll = tree._vertScrollBar;
      var resize = tree._resizeLine;
      var rootItem = tree._rootItem;
      var element = tree.getElement();
      var columnArea = tree._header;
      var footer = tree._footer;
      assertTrue( element.parentNode === document.body );
      assertNotNull( tree._rootItem );
      assertNotNull( tree._focusItem );
      assertNotNull( tree._leadItem );
      tree.destroy();
      TestUtil.flush();
      assertTrue( element.parentNode !== document.body );
      assertTrue( tree.isDisposed() );
      assertTrue( row.isDisposed() );
      assertTrue( columnArea.isDisposed() );
      assertTrue( footer.isDisposed() );
      assertTrue( area.isDisposed() );
      assertTrue( hscroll.isDisposed() );
      assertTrue( vscroll.isDisposed() );
      assertTrue( vscroll.isDisposed() );
      assertTrue( resize.isDisposed() );
      assertTrue( rootItem.isDisposed() );
      assertTrue( dummy.isDisposed() );
      assertNull( tree._rootItem );
      assertNull( tree._focusItem );
      assertNull( tree._leadItem );
      assertNull( tree._rowContainer );
      assertNull( tree._horzScrollBar );
      assertNull( tree._vertScrollBar );
      assertNull( tree._resizeLine );
      assertNull( tree._header );
      assertNull( tree._footer );
    },

    testSetCheckBoxMetrics : function() {
      var tree = this._createDefaultTree( false, false, "check", [ 5, 20 ] );
      tree.setItemCount( 1 );
      this._createItem( tree.getRootItem(), 0 );
      assertEquals( 5, tree._config.checkBoxLeft );
      assertEquals( 20, tree._config.checkBoxWidth );
      tree.destroy();
    },

    testSetHasCheckBox : function() {
      var tree = this._createDefaultTree( false, false, "check", [ 5, 20 ] );
      assertTrue( tree._config.hasCheckBoxes );
      tree.destroy();
    },

    testClickOnCheckBoxSymbol : function() {
      var tree = this._createDefaultTree( false, false, "check", [ 5, 20 ]  );
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      rwt.remote.ObjectRegistry.add( "w4", item, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();
      var node = tree._rowContainer.$rows.get( 0 ).childNodes[ 0 ].childNodes[ 0 ];
      TestUtil.clickDOM( node.parentNode ); // nothing happens:
      assertFalse( item.isChecked() );
      TestUtil.fakeListener( tree, "Selection", true );

      TestUtil.clickDOM( node );

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertTrue( item.isChecked() );
      var message = TestUtil.getMessageObject();
      assertEquals( true, message.findSetProperty( "w4", "checked" ) );
      assertEquals( "w4", message.findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( "check", message.findNotifyProperty( "w3", "Selection", "detail" ) );
      tree.destroy();
    },

    testClickOnCellCheckBoxDoesNotSelect : function() {
      var tree = this._createDefaultTree( false, false );
      tree.setCellCheck( 0, true );
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      var checkNode = tree.getRowContainer().getRow( 0 ).$el.prop( "childNodes" )[ 0 ];

      TestUtil.clickDOM( checkNode );

      assertFalse( tree.isItemSelected( item ) );
      tree.destroy();
    },

    testClickOnCellCheckBoxToggles : function() {
      var tree = this._createDefaultTree( false, false );
      tree.setCellCheck( 0, true );
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      var checkNode = tree.getRowContainer().getRow( 0 ).$el.prop( "childNodes" )[ 0 ];

      TestUtil.clickDOM( checkNode );

      assertTrue( item.isCellChecked( 0 ) );
      tree.destroy();
    },

    testClickOnCellCheckBoxSendChange : function() {
      var tree = this._createDefaultTree( false, false );
      tree.setCellCheck( 0, true );
      TestUtil.initRequestLog();
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      rwt.remote.ObjectRegistry.add( "w2", item, itemHandler );
      TestUtil.flush();
      var checkNode = tree.getRowContainer().getRow( 0 ).$el.prop( "childNodes" )[ 0 ];

      TestUtil.clickDOM( checkNode );

      rwt.remote.Connection.getInstance().send();
      var message = TestUtil.getMessageObject();
      assertEquals( [ true ], message.findSetProperty( "w2", "cellChecked" ) );
      tree.destroy();
    },

    testClickOnCellCheckBoxSendEvent : function() {
      var tree = this._createDefaultTree( false, false );
      tree.setCellCheck( 0, true );
      TestUtil.initRequestLog();
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      TestUtil.fakeListener( tree, "Selection", true );
      var item = this._createItem( tree.getRootItem(), 0 );
      rwt.remote.ObjectRegistry.add( "w2", item, itemHandler );
      TestUtil.flush();
      var checkNode = tree.getRowContainer().getRow( 0 ).$el.prop( "childNodes" )[ 0 ];

      TestUtil.clickDOM( checkNode );

      var message = TestUtil.getMessageObject();
      assertEquals( "check", message.findNotifyProperty( "w3", "Selection", "detail" ) );
      assertEquals( 0, message.findNotifyProperty( "w3", "Selection", "index" ) );
      tree.destroy();
    },

    testClickOnNotCheckableCellCheckBoxDoesNotToggle : function() {
      var tree = this._createDefaultTree( false, false );
      tree.setCellCheck( 0, true );
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setCellCheckable( [ false ] );
      rwt.remote.ObjectRegistry.add( "w2", item, itemHandler );
      TestUtil.flush();
      var checkNode = tree.getRowContainer().getRow( 0 ).$el.prop( "childNodes" )[ 0 ];

      TestUtil.clickDOM( checkNode );

      assertTrue( item.isCellChecked( 0 ) === undefined );
      tree.destroy();
    },

    testClickOnNotCheckableCellCheckBoxDoesNotSendChange : function() {
      var tree = this._createDefaultTree( false, false );
      tree.setCellCheck( 0, true );
      TestUtil.initRequestLog();
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setCellCheckable( [ false ] );
      rwt.remote.ObjectRegistry.add( "w2", item, itemHandler );
      TestUtil.flush();
      var checkNode = tree.getRowContainer().getRow( 0 ).$el.prop( "childNodes" )[ 0 ];

      TestUtil.clickDOM( checkNode );

      rwt.remote.Connection.getInstance().send();
      var message = TestUtil.getMessageObject();
      assertNull( message.findSetOperation( "w2", "cellChecked" ) );
      tree.destroy();
    },

    testClickOnNotCheckableCellCheckBoxDoesNotSendEvent : function() {
      var tree = this._createDefaultTree( false, false );
      tree.setCellCheck( 0, true );
      TestUtil.initRequestLog();
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      TestUtil.fakeListener( tree, "Selection", true );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setCellCheckable( [ false ] );
      rwt.remote.ObjectRegistry.add( "w2", item, itemHandler );
      TestUtil.flush();
      var checkNode = tree.getRowContainer().getRow( 0 ).$el.prop( "childNodes" )[ 0 ];

      TestUtil.clickDOM( checkNode );
      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertNull( message.findNotifyOperation( "w3", "Selection" ) );
      tree.destroy();
    },

    testClickOnCellCheckBoxSendChangeMoreColumns : function() {
      var tree = this._createDefaultTree( false, false );
      tree.setCellCheck( 0, true );
      TestUtil.initRequestLog();
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      tree.setColumnCount( 4 );
      var item = this._createItem( tree.getRootItem(), 0 );
      rwt.remote.ObjectRegistry.add( "w2", item, itemHandler );
      TestUtil.flush();
      var checkNode = tree.getRowContainer().getRow( 0 ).$el.prop( "childNodes" )[ 0 ];

      TestUtil.clickDOM( checkNode );

      rwt.remote.Connection.getInstance().send();
      var message = TestUtil.getMessageObject();
      assertEquals( [ true, false, false, false ], message.findSetProperty( "w2", "cellChecked" ) );
      tree.destroy();
    },

    testClickCheckBoxOnUnresolved : function() {
      var tree = this._createDefaultTree( false, false, "check", [ 5, 20 ]  );
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      TestUtil.flush();
      TestUtil.initRequestLog();
      TestUtil.fakeListener( tree, "Selection", true );
      var node = tree._rowContainer.$rows.get( 0 ).childNodes[ 0 ].childNodes[ 0 ];
      TestUtil.clickDOM( node );
      assertFalse( tree.getRootItem().getChild( 0 ).isChecked() );
      assertEquals( 0, TestUtil.getRequestsSend() );
      tree.destroy();
    },

    testClickOnRWTHyperlinkWithHref : function() {
      var tree = this._createDefaultTree( false, false );
      tree.getRenderConfig().markupEnabled = true;
      TestUtil.fakeListener( tree, "Selection", true );
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "<a href=\"foo\" target=\"_rwt\">Test</a>" ] );
      rwt.remote.ObjectRegistry.add( "w4", item, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();
      var node = tree._rowContainer.$rows.get( 0 ).childNodes[ 0 ].childNodes[ 0 ].childNodes[ 0 ];

      TestUtil.clickDOM( node );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "w4", message.findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( "hyperlink", message.findNotifyProperty( "w3", "Selection", "detail" ) );
      var text = message.findNotifyProperty( "w3", "Selection", "text" );
      if( text.indexOf( "/" ) !== 0 ) {
        text = text.slice( text.lastIndexOf( "/" ) + 1 );
      }
      assertEquals( "foo", text );
      tree.destroy();
    },

    testClickOnRWTHyperlinkWithoutHref : function() {
      var tree = this._createDefaultTree( false, false );
      tree.getRenderConfig().markupEnabled = true;
      TestUtil.fakeListener( tree, "Selection", true );
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "<a target=\"_rwt\">Test</a>" ] );
      rwt.remote.ObjectRegistry.add( "w4", item, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();
      var node = tree._rowContainer.$rows.get( 0 ).childNodes[ 0 ].childNodes[ 0 ].childNodes[ 0 ];

      TestUtil.clickDOM( node );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "w4", message.findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( "hyperlink", message.findNotifyProperty( "w3", "Selection", "detail" ) );
      assertEquals( "Test", message.findNotifyProperty( "w3", "Selection", "text" ) );
      tree.destroy();
    },

    testClickOnRWTHyperlinkWithInnerHTML : function() {
      var tree = this._createDefaultTree( false, false );
      tree.getRenderConfig().markupEnabled = true;
      TestUtil.fakeListener( tree, "Selection", true );
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "<a href=\"foo\" target=\"_rwt\"><b>Test</b></a>" ] );
      rwt.remote.ObjectRegistry.add( "w2", item, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();
      var rowContainerNode = tree._rowContainer.$rows.get( 0 );
      var node = rowContainerNode.childNodes[ 0 ].childNodes[ 0 ].childNodes[ 0 ].childNodes[ 0 ];

      TestUtil.clickDOM( node );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "w2", message.findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( "hyperlink", message.findNotifyProperty( "w3", "Selection", "detail" ) );
      var text = message.findNotifyProperty( "w3", "Selection", "text" );
      if( text.indexOf( "/" ) !== 0 ) {
        text = text.slice( text.lastIndexOf( "/" ) + 1 );
      }
      assertEquals( "foo", text );
      tree.destroy();
    },

    testHasFullSelection : function() {
      var tree = this._createDefaultTree();
      assertTrue( tree.getRenderConfig().fullSelection );
      tree.destroy();
    },

    testSelectionClick : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      assertFalse( tree.isItemSelected( item ) );
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item ) );
      tree.destroy();
    },

    testDeselect : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 2 );
      var item1 = this._createItem( tree.getRootItem(), 0 );
      var item2 = this._createItem( tree.getRootItem(), 1 );
      TestUtil.flush();
      var node1 = tree._rowContainer.$rows.get( 0 ).childNodes[ 0 ];
      var node2 = tree._rowContainer.$rows.get( 0 ).childNodes[ 1 ];
      TestUtil.clickDOM( node1 );
      assertTrue( tree.isItemSelected( item1 ) );
      TestUtil.clickDOM( node2 );
      assertTrue( tree.isItemSelected( item2 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      tree.destroy();
    },

    testWheelScroll : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();
      assertEquals( 100, tree._vertScrollBar.getMaximum() );
      assertEquals( 0, tree._vertScrollBar.getValue() );
      TestUtil.fakeWheel( tree._rowContainer , -3 );
      assertEquals( 6, tree._vertScrollBar.getValue() );
      TestUtil.fakeWheel( tree._rowContainer, 2 );
      assertEquals( 2, tree._vertScrollBar.getValue() );
      tree.destroy();
    },

    testWheelScroll_byTouchPad : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();
      TestUtil.fakeWheel( tree._rowContainer, -0.3 );
      assertEquals( 1, tree._vertScrollBar.getValue() );
      TestUtil.fakeWheel( tree._rowContainer, 0.2 );
      assertEquals( 0, tree._vertScrollBar.getValue() );
      tree.destroy();
    },

    testWheelScrollStopProppagation : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();
      var log = [];
      tree._rowContainer.addEventListener( "mousewheel", function( event ) {
        log.push( "area", event );
      } );
      tree.addEventListener( "mousewheel", function( event ) {
        log.push( "tree", event );
      } );

      TestUtil.fakeWheel( tree._rowContainer, -2 );

      assertEquals( 2, log.length );
      assertEquals( "area", log[ 0 ] );
      assertTrue( log[ 1 ].getDefaultPrevented() );
      tree.destroy();
    },

    testWheelScrollStopProppagation_AllowIfScrollBarIsUnchanged : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.flush();
      var log = [];
      tree.addEventListener( "mousewheel", function( event ) {
        log.push( event );
      } );

      TestUtil.fakeWheel( tree._rowContainer, 1 );

      assertEquals( 1, log.length );
      assertFalse( log[ 0 ].getDefaultPrevented() );
      tree.destroy();
    },

    testFocusItem : function() {
      var tree = this._createDefaultTree();
      TestUtil.initRequestLog();
      tree.setItemCount( 3 );
      this._createItem( tree.getRootItem(), 0 );
      this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      rwt.remote.ObjectRegistry.add( "w4", item2, itemHandler );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 2 ).$el.get( 0 ) );
      TestUtil.flush();
      assertTrue( tree.isFocusItem( item2 ) );
      rwt.remote.Connection.getInstance().send();
      assertEquals( "w4", TestUtil.getMessageObject().findSetProperty( "w3", "focusItem" ) );
      tree.destroy();
    },

    testChangeFocusItem : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 3 );
      this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      rwt.remote.ObjectRegistry.add( "w4", item2, itemHandler );
      var rows = tree._rowContainer.$rows.prop( "children" );
      TestUtil.clickDOM( rows[ 1 ] );
      TestUtil.flush();
      TestUtil.clickDOM( rows[ 2 ] );
      TestUtil.flush();
      assertFalse( tree.isFocusItem( item1 ) );
      assertTrue( tree.isFocusItem( item2 ) );
      rwt.remote.Connection.getInstance().send();
      assertEquals( "w4", TestUtil.getMessageObject().findSetProperty( "w3", "focusItem" ) );
      tree.destroy();
    },

    testSetFocusItemWhenFocusingGrid : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 3 );
      var item = this._createItem( tree.getRootItem(), 0 );
      this._createItem( tree.getRootItem(), 1 );
      this._createItem( tree.getRootItem(), 2 );
      rwt.remote.ObjectRegistry.add( "w4", item, itemHandler );
      TestUtil.flush();

      tree.setFocused( true );

      assertTrue( tree.isFocusItem( item ) );
      rwt.remote.Connection.getInstance().send();
      assertEquals( "w4", TestUtil.getMessageObject().findSetProperty( "w3", "focusItem" ) );
      tree.destroy();
    },

    testNoMultiSelection : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      var node0 = tree._rowContainer.$rows.get( 0 ).childNodes[ 0 ];
      var node2 = tree._rowContainer.$rows.get( 0 ).childNodes[ 2 ];
      TestUtil.clickDOM( node0 );
      assertTrue( tree.isItemSelected( item0 ) );
      var left = rwt.event.MouseEvent.buttons.left;
      TestUtil.fakeMouseEventDOM( node2, "mousedown", left, 0, 0, 7 );
      assertFalse( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testCtrlMultiSelection : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.ctrlClick( tree._rowContainer.getRow( 2 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testCtrlMultiSelectionDeselection : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.ctrlClick( tree._rowContainer.getRow( 2 ) );
      tree._selectionTimestamp = null;
      TestUtil.ctrlClick( tree._rowContainer.getRow( 2 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testCtrlMultiSelectionSingleSelection : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.ctrlClick( tree._rowContainer.getRow( 2 ) );
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelection : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.ctrlClick( tree._rowContainer.getRow( 0 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 2 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelectionWithoutFocusItem : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      TestUtil.shiftClick( tree._rowContainer.getRow( 1 ) );
      assertFalse( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelectionChangedFocus : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      tree.setFocusItem( item2 );
      TestUtil.shiftClick( tree._rowContainer.getRow( 1 ) );
      assertFalse( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelectionModify : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 2 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 1 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelectionTwice : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 2 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 1 ) );
      TestUtil.click( tree._rowContainer.getRow( 2 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelectionBackwards : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 2 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testShiftMultiSelectionDeselect : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 5 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      var item3 = this._createItem( tree.getRootItem(), 3 );
      var item4 = this._createItem( tree.getRootItem(), 4 );
      TestUtil.flush();
      TestUtil.ctrlClick( tree._rowContainer.getRow( 2 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 4 ) );
      assertFalse( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      assertTrue( tree.isItemSelected( item3 ) );
      assertTrue( tree.isItemSelected( item4 ) );
      tree.destroy();
    },

    testMultiSelectionCombination : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 5 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      var item3 = this._createItem( tree.getRootItem(), 3 );
      var item4 = this._createItem( tree.getRootItem(), 4 );
      TestUtil.flush();
      var node0 = tree._rowContainer.$rows.get( 0 ).childNodes[ 0 ];
      var node2 = tree._rowContainer.$rows.get( 0 ).childNodes[ 2 ];
      var node4 = tree._rowContainer.$rows.get( 0 ).childNodes[ 4 ];
      var left = rwt.event.MouseEvent.buttons.left;
      TestUtil.fakeMouseEventDOM( node0, "mousedown", left, 0, 0, 0 );
      TestUtil.fakeMouseEventDOM( node2, "mousedown", left, 0, 0, 2 );
      TestUtil.fakeMouseEventDOM( node4, "mousedown", left, 0, 0, 3 );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      assertFalse( tree.isItemSelected( item3 ) );
      assertTrue( tree.isItemSelected( item4 ) );
      tree.destroy();
    },

    testMultiSelectionRightClick : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.ctrlClick( tree._rowContainer.getRow( 0 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 1 ) );
      TestUtil.rightClick( tree._rowContainer.getRow( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      TestUtil.rightClick( tree._rowContainer.getRow( 2 ) );
      assertFalse( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testSetDimensionBeforeItemHeight : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setSpace( 0, 800, 19, 500 );
      tree.setItemHeight( 16 );
      //succeeds by not crashing
      tree.destroy();
    },

    testSetColumnCount : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "Test1", "Test2", "Test3" ] );
      TestUtil.flush();
      var nodes = tree._rowContainer.getRow( 0 ).$el.prop( "childNodes" );
      assertEquals( 1, nodes.length );
      tree.setColumnCount( 3 );
      TestUtil.flush();
      assertEquals( 3, tree.getRenderConfig().columnCount );
      assertEquals( 3, nodes.length );
      tree.destroy();
    },

    testSelectionPadding : function() {
       var tree = new rwt.widgets.Grid( {
         "appearance": "tree",
         "selectionPadding" : [ 2, 4 ]
       } );
       assertEquals( [ 2, 4 ], tree._config.selectionPadding );
       tree.destroy();
    },

    testSendExpandTrue : function() {
      TestUtil.initRequestLog();
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      new rwt.widgets.util.GridSynchronizer( tree );
      rwt.remote.ObjectRegistry.add( "w3", tree, gridHandler );
      var child1 = this._createItem( tree.getRootItem() );
      this._createItem( child1 );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );

      child1.setExpanded( true );
      rwt.remote.Connection.getInstance().send();

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertTrue( message.findSetProperty( "w4", "expanded" ) );
      tree.destroy();
    },

    testSendExpandFalse : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      new rwt.widgets.util.GridSynchronizer( tree );
      rwt.remote.ObjectRegistry.add( "w3", tree, gridHandler );
      var child1 = this._createItem( tree.getRootItem() );
      this._createItem( child1 );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      child1.setExpanded( true );
      TestUtil.initRequestLog();

      child1.setExpanded( false );
      rwt.remote.Connection.getInstance().send();

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertFalse( message.findSetProperty( "w4", "expanded" ) );
      tree.destroy();
    },

    testSendExpandEvent : function() {
      TestUtil.initRequestLog();
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      new rwt.widgets.util.GridSynchronizer( tree );
      var child1 = this._createItem( tree.getRootItem() );
      this._createItem( child1 );
      rwt.remote.ObjectRegistry.add( "w3", tree, gridHandler );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      TestUtil.fakeListener( tree, "Expand", true );

      child1.setExpanded( true );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "w4", message.findNotifyProperty( "w3", "Expand", "item" ) );
      tree.destroy();
    },

    testSendCollapseEvent : function() {
      TestUtil.initRequestLog();
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      new rwt.widgets.util.GridSynchronizer( tree );
      var child1 = this._createItem( tree.getRootItem() );
      this._createItem( child1 );
      rwt.remote.ObjectRegistry.add( "w3", tree, gridHandler );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      TestUtil.fakeResponse( true );
      child1.setExpanded( true );
      TestUtil.fakeListener( tree, "Collapse", true );
      TestUtil.fakeResponse( false );

      child1.setExpanded( false );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "w4", message.findNotifyProperty( "w3", "Collapse", "item" ) );
      tree.destroy();
    },

    testNoSendEventDuringResponse : function() {
      TestUtil.initRequestLog();
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      rwt.remote.ObjectRegistry.add( "w3", tree, gridHandler );
      tree.setItemCount( 1 );
      var child1 = this._createItem( tree.getRootItem(), 0 );
      child1.setItemCount( 1 );
      this._createItem( child1, 0 );

      rwt.remote.EventUtil.setSuspended( true );
      child1.setExpanded( true );
      child1.setExpanded( false );
      rwt.remote.EventUtil.setSuspended( false );

      assertEquals( 0, TestUtil.getRequestsSend() );
      tree.destroy();
    },

    testSendSelectionProperty : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 2 );
      var child1 = this._createItem( tree.getRootItem(), 0 );
      var child2 = this._createItem( tree.getRootItem(), 1 );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      rwt.remote.ObjectRegistry.add( "w5", child2, itemHandler );
      TestUtil.initRequestLog();
      TestUtil.flush();

      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      tree._selectionTimestamp = null;
      TestUtil.ctrlClick( tree._rowContainer.getRow( 1 ) );

      assertEquals( 0, TestUtil.getRequestsSend() );
      rwt.remote.Connection.getInstance().send();
      var message = TestUtil.getMessageObject();
      assertEquals( [ "w4","w5" ], message.findSetProperty( "w3", "selection" ) );
      tree.destroy();
    },

    testSendSelectionEvent : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      TestUtil.fakeListener( tree, "Selection", true );
      tree.setItemCount( 2 );
      var child1 = this._createItem( tree.getRootItem(), 0 );
      this._createItem( tree.getRootItem(), 1 );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      tree._selectionTimestamp = null;
      TestUtil.ctrlClick( tree._rowContainer.getRow( 0 ) );

      assertEquals( 2, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertEquals( "w4", messages[ 0 ].findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( "w4", messages[ 1 ].findNotifyProperty( "w3", "Selection", "item" ) );
      tree.destroy();
    },

    testSendDefaultSelectionEvent : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeListener( tree, "DefaultSelection", true );
      tree.setItemCount( 1 );
      var child1 = this._createItem( tree.getRootItem(), 0 );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.doubleClick( tree._rowContainer.getRow( 0 ) );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getLastMessage();
      assertEquals( [ "w4" ], message.findSetProperty( "w3", "selection" ) );
      assertEquals( "w4", message.findNotifyProperty( "w3", "DefaultSelection", "item" ) );
      assertNull( message.findNotifyOperation( "w3", "Selection" ) );
      tree.destroy();
    },

    testSendDefaultSelectionEventOnDragSource : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      var dragSource = this._createDragSource( tree );
      TestUtil.fakeListener( tree, "Selection", true );
      TestUtil.fakeListener( tree, "DefaultSelection", true );
      tree.setItemCount( 2 );
      var child0 = this._createItem( tree.getRootItem(), 0 );
      this._createItem( tree.getRootItem(), 1 );
      rwt.remote.ObjectRegistry.add( "w4", child0, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.doubleClick( tree._rowContainer.getRow( 0 ) );

      var messages = TestUtil.getMessages();
      assertEquals( "w4", messages[ 0 ].findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( [ "w4" ], messages[ 0 ].findSetProperty( "w3", "selection" ) );
      assertNull( messages[ 0 ].findNotifyOperation( "w3", "DefaultSelection" ) );
      assertEquals( "w4", messages[ 1 ].findNotifyProperty( "w3", "DefaultSelection", "item" ) );
      assertNull( messages[ 1 ].findNotifyOperation( "w3", "Selection" ) );
      assertNull( [ "w4" ], messages[ 1 ].findSetOperation( "w3", "selection" ) );
      dragSource.dispose();
      tree.destroy();
    },

    testDontSendDefaultSelectionEventOnDoubleRightClick : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeListener( tree, "Selection", true );
      TestUtil.fakeListener( tree, "DefaultSelection", true );
      tree.setItemCount( 1 );
      var child1 = this._createItem( tree.getRootItem(), 0 );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.rightClick( tree._rowContainer.getRow( 0 ) );
      TestUtil.rightClick( tree._rowContainer.getRow( 0 ) );

      assertEquals( 2, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertNull( messages[ 0 ].findNotifyOperation( "w3", "DefaultSelection" ) );
      assertNull( messages[ 1 ].findNotifyOperation( "w3", "DefaultSelection" ) );
      assertEquals( "w4", messages[ 0 ].findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( "w4", messages[ 1 ].findNotifyProperty( "w3", "Selection", "item" ) );
      tree.destroy();
    },

    testSendDefaultSelectionEventByEnter : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeListener( tree, "DefaultSelection", true );
      tree.setItemCount( 1 );
      var child1 = this._createItem( tree.getRootItem(), 0 );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();
      var node = tree._rowContainer.getRow( 0 ).$el.get( 0 );

      TestUtil.clickDOM( node );
      TestUtil.keyDown( node, "Enter" );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getLastMessage();
      assertEquals( [ "w4" ], message.findSetProperty( "w3", "selection" ) );
      assertEquals( "w4", message.findNotifyProperty( "w3", "DefaultSelection", "item" ) );
      assertNull( message.findNotifyOperation( "w3", "Selection" ) );
      tree.destroy();
    },

    testSendDefaultSelectionEventByEnterChangedFocus : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeListener( tree, "DefaultSelection", true );
      tree.setItemCount( 2 );
      var child1 = this._createItem( tree.getRootItem(), 0 );
      var child2 = this._createItem( tree.getRootItem(), 1 );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      rwt.remote.ObjectRegistry.add( "w5", child2, itemHandler );
      tree.focus();
      TestUtil.flush();
      TestUtil.initRequestLog();
      tree.setFocusItem( child2 );

      TestUtil.keyDown( tree._getTargetNode(), "Enter" );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "w5", message.findNotifyProperty( "w3", "DefaultSelection", "item" ) );
      tree.destroy();
    },

    testDontSendDefaultSelectionEventOnFastClick : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeListener( tree, "Selection", true );
      TestUtil.fakeListener( tree, "DefaultSelection", true );
      tree.setItemCount( 2 );
      var child1 = this._createItem( tree.getRootItem(), 0 );
      var child2 = this._createItem( tree.getRootItem(), 1 );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      rwt.remote.ObjectRegistry.add( "w5", child2, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.click( tree._rowContainer.getRow( 1 ) );

      assertEquals( 2, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertNull( messages[ 0 ].findNotifyOperation( "w3", "DefaultSelection" ) );
      assertNull( messages[ 1 ].findNotifyOperation( "w3", "DefaultSelection" ) );
      assertEquals( "w4", messages[ 0 ].findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( "w5", messages[ 1 ].findNotifyProperty( "w3", "Selection", "item" ) );
      tree.destroy();
    },

    testMultiSelectionEvent : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      TestUtil.fakeListener( tree, "Selection", true );
      TestUtil.fakeListener( tree, "DefaultSelection", true );
      tree.setItemCount( 3 );
      var child1 = this._createItem( tree.getRootItem(), 0 );
      var child2 = this._createItem( tree.getRootItem(), 1 );
      var child3 = this._createItem( tree.getRootItem(), 2 );
      rwt.remote.ObjectRegistry.add( "w4", child1, itemHandler );
      rwt.remote.ObjectRegistry.add( "w5", child2, itemHandler );
      rwt.remote.ObjectRegistry.add( "w6", child3, itemHandler );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      tree._selectionTimestamp = null;
      TestUtil.shiftClick( tree._rowContainer.getRow( 2 ) );

      assertEquals( 2, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertNull( messages[ 0 ].findNotifyOperation( "w3", "DefaultSelection" ) );
      assertNull( messages[ 1 ].findNotifyOperation( "w3", "DefaultSelection" ) );
      assertEquals( "w4", messages[ 0 ].findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( "w6", messages[ 1 ].findNotifyProperty( "w3", "Selection", "item" ) );
      assertEquals( [ "w4", "w5", "w6" ], messages[ 1 ].findSetProperty( "w3", "selection" ) );
      tree.destroy();
    },

    testRenderOnFocus : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      this._createItem( tree.getRootItem(), 0 );
      tree.focus();
      TestUtil.flush();
      assertFalse( tree._rowContainer.getRow( 0 ).hasState( "parent_unfocused" ) );
      tree.blur();
      TestUtil.flush();
      assertTrue( tree._rowContainer.getRow( 0 ).hasState( "parent_unfocused" ) );
      tree.destroy();
    },

    testSetBackgroundColor : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setBackgroundColor( "red" );
      assertEquals( "red", tree._rowContainer.getBackgroundColor() );
      tree.destroy();
    },

    testIsHoverItem : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      TestUtil.mouseOver( tree._rowContainer.getRow( 0 ) );
      assertTrue( tree._rowContainer.getHoverItem() === item );
      tree.destroy();
    },

    testIsHoverElement : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "bla" ] );
      item.setImages( [ [ "bla.jpg", 10, 10 ] ] );
      assertEquals( 0, tree._rowContainer._hoverTargetType.length );
      TestUtil.flush();
      var rowNode = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      TestUtil.hoverFromTo( document.body, rowNode );
      TestUtil.hoverFromTo( rowNode, rowNode.firstChild );
      assertEquals( "other", tree._rowContainer._hoverTargetType[ 0 ] );
      tree.destroy();
    },

    testRenderOnItemHover : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            background : states.over ? "red" : "green",
            backgroundGradient : null,
            backgroundImage : null,
            foreground : "undefined"
          };
        }
      } );
      tree.setItemCount( 1 );
      this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      var style = tree._rowContainer.getRow( 0 ).$el.get( 0 ).style;
      assertEquals( "green", style.backgroundColor );
      TestUtil.mouseOver( tree._rowContainer.getRow( 0 ) );
      TestUtil.forceInterval( tree._rowContainer._asyncTimer );
      assertEquals( "red", style.backgroundColor );
      TestUtil.hoverFromTo( tree._rowContainer.getRow( 0 ), document.body );
      TestUtil.forceInterval( tree._rowContainer._asyncTimer );
      assertEquals( "green", style.backgroundColor );
      tree.destroy();
    },

    testDisposeBeforeRenderItemHover : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeAppearance( "tree-row",  {
        style : function( states ) {
          return {
            background : states.over ? "red" : "green",
            backgroundGradient : null,
            backgroundImage : null
          };
        }
      } );
      tree.setItemCount( 1 );
      this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      var timer = tree._rowContainer._asyncTimer;
      TestUtil.mouseOver( tree._rowContainer.getRow( 0 ) );
      tree.destroy();
      TestUtil.flush();
      if( !timer.isDisposed() ) {
        TestUtil.forceInterval( timer );
      }
      // Succeeds by not crashing
    },

    testRenderOnCheckBoxHover : function() {
      var tree = this._createDefaultTree( false, false, "check", [ 5, 20 ] );
      TestUtil.fakeAppearance( "tree-row-check-box",  {
        style : function( states ) {
          return {
            "backgroundImage" : states.over ? "over.gif" : "normal.gif"
          };
        }
      } );
      tree.setItemCount( 1 );
      this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      var row = tree._rowContainer.getRow( 0 );
      var rowNode = row.$el.get( 0 );
      TestUtil.hoverFromTo( document.body, rowNode );
      TestUtil.forceInterval( tree._rowContainer._asyncTimer );
      assertTrue( row.hasState( "over" ) );
      var normal = TestUtil.getCssBackgroundImage( rowNode.firstChild );
      TestUtil.hoverFromTo( rowNode, rowNode.firstChild );
      TestUtil.forceInterval( tree._rowContainer._asyncTimer );
      var over = TestUtil.getCssBackgroundImage( rowNode.firstChild );
      TestUtil.hoverFromTo( rowNode.firstChild, rowNode );
      TestUtil.forceInterval( tree._rowContainer._asyncTimer );
      var normalAgain = TestUtil.getCssBackgroundImage( rowNode.firstChild );
      assertTrue( normal.indexOf( "normal.gif" ) != -1 );
      assertTrue( over.indexOf( "over.gif" ) != -1 );
      assertTrue( normalAgain.indexOf( "normal.gif" ) != -1 );
      assertTrue( row.hasState( "over" ) );
      tree.destroy();
    },

    testRenderOnCheckBoxHoverSkip : function() {
      var tree = this._createDefaultTree( false, false, "check", [ 5, 5 ] );
      TestUtil.fakeAppearance( "tree-row-check-box",  {
        style : function( states ) {
          return {
            "backgroundImage" : states.over ? "over.gif" : "normal.gif"
          };
        }
      } );
      tree.setItemCount( 2 );
      this._createItem( tree.getRootItem(), 0 );
      this._createItem( tree.getRootItem(), 1 );
      TestUtil.flush();
      var rowNode1 = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      var rowNode2 = tree._rowContainer.getRow( 1 ).$el.get( 0 );
      TestUtil.hoverFromTo( document.body, rowNode1.firstChild );
      TestUtil.forceInterval( tree._rowContainer._asyncTimer );
      var check1 = TestUtil.getCssBackgroundImage( rowNode1.firstChild );
      var check2 = TestUtil.getCssBackgroundImage( rowNode2.firstChild );
      assertTrue( check1.indexOf( "over.gif" ) != -1 );
      assertTrue( check2.indexOf( "normal.gif" ) != -1 );
      TestUtil.hoverFromTo( rowNode1.firstChild, rowNode2.firstChild );
      TestUtil.forceInterval( tree._rowContainer._asyncTimer );
      check1 = TestUtil.getCssBackgroundImage( rowNode1.firstChild );
      check2 = TestUtil.getCssBackgroundImage( rowNode2.firstChild );
      assertTrue( check1.indexOf( "normal.gif" ) != -1 );
      assertTrue( check2.indexOf( "over.gif" ) != -1 );
      tree.destroy();
    },

    testRenderOnExpandSymbolHover : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeAppearance( "tree-row-indent",  {
        style : function( states ) {
          var result = null;
          if( !states.line ) {
            result = states.over ? "over.gif" : "normal.gif";
          }
          return {
            "backgroundImage" : result
          };
        }
      } );
      tree.setItemCount( 1 );
      this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      var rowNode = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      TestUtil.hoverFromTo( document.body, rowNode );
      TestUtil.forceInterval( tree._rowContainer._asyncTimer );
      var normal = TestUtil.getCssBackgroundImage( rowNode.firstChild );
      TestUtil.hoverFromTo( rowNode, rowNode.firstChild );
      TestUtil.forceInterval( tree._rowContainer._asyncTimer );
      var over = TestUtil.getCssBackgroundImage( rowNode.firstChild );
      TestUtil.hoverFromTo( rowNode.firstChild, rowNode );
      TestUtil.forceInterval( tree._rowContainer._asyncTimer );
      var normalAgain = TestUtil.getCssBackgroundImage( rowNode.firstChild );
      assertTrue( normal.indexOf( "normal.gif" ) != -1 );
      assertTrue( over.indexOf( "over.gif" ) != -1 );
      assertTrue( normalAgain.indexOf( "normal.gif" ) != -1 );
      tree.destroy();
    },

    testSendTopItemIndex : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      TestUtil.initRequestLog();
      TestUtil.flush();

      tree._vertScrollBar.setValue( 8 );
      assertEquals( 0, TestUtil.getRequestsSend() );
      rwt.remote.Connection.getInstance().send();

      assertEquals( 8, TestUtil.getMessageObject().findSetProperty( "w3", "topItemIndex" ) );
      tree.destroy();
    },

    testFireTopItemChangedEventInResponse : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      var logger = TestUtil.getLogger();
      tree.addEventListener( "topItemChanged", logger.log );
      TestUtil.flush();

      rwt.remote.EventUtil.setSuspended( true );
      tree._vertScrollBar.setValue( 8 );
      rwt.remote.EventUtil.setSuspended( false );

      assertEquals( 1, logger.getLog().length );
      tree.destroy();
    },

    testScrollWidth : function() {
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

    testHeaderScrollWidth : function() {
      var tree = this._createDefaultTree();
      tree.setHeaderVisible( true );
      tree.setFooterVisible( true );

      tree.setItemMetrics( 3, 500, 700, 0, 0, 0, 500 );
      tree.setColumnCount( 4 );

      assertEquals( 1200, tree.getTableHeader()._scrollWidth );
      assertEquals( 1200, tree.getFooter()._scrollWidth );
      tree.destroy();
    },

    testScrollHorizontal : function() {
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 2, 500, 600, 0, 0, 0, 500 );
      tree.setColumnCount( 3 );
      TestUtil.flush();
      tree._horzScrollBar.setValue( 400 );
      assertEquals( 400, tree._rowContainer.getScrollLeft() );
      tree.destroy();
    },

    testShowColumnHeader : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      this._fakeAppearance();
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      TestUtil.flush();
      var areaNode = tree._rowContainer.getElement();
      assertEquals( 30, parseInt( areaNode.style.top, 10 ) );
      assertEquals( 470, parseInt( areaNode.style.height, 10 ) );
      assertEquals( 600, parseInt( areaNode.style.width, 10 ) );
      var headerNode = tree._header.getElement();
      assertEquals( 0, parseInt( headerNode.style.top, 10 ) );
      assertEquals( 30, parseInt( headerNode.style.height, 10 ) );
      assertEquals( 600, parseInt( headerNode.style.width, 10 ) );
      assertEquals( 2000, parseInt( headerNode.style.zIndex, 10 ) );
      tree.destroy();
    },

    testShowColumnHeaderWithScrollbars : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      this._fakeAppearance();
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setScrollBarsVisible( true, true );
      tree.setHeight( 500 );
      tree.setWidth( 600 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      TestUtil.flush();
      var horizontal = TestUtil.getElementBounds( tree._horzScrollBar.getElement() );
      var vertical = TestUtil.getElementBounds( tree._vertScrollBar.getElement() );
      var headerNode = tree._header.getElement();
      assertEquals( 600, parseInt( headerNode.style.width, 10 ) );
      var areaNode = tree._rowContainer.getElement();
      var expectedAreaHeight = 470 - horizontal.height;
      assertEquals( expectedAreaHeight, parseInt( areaNode.style.height, 10 ) );
      assertEquals( expectedAreaHeight, vertical.height );
      assertEquals( 30, vertical.top );
      assertEquals( expectedAreaHeight + 30, horizontal.top );
      tree.destroy();
    },

    testShowColumnFooter : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      this._fakeAppearance();
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );

      tree.setFooterHeight( 30 );
      tree.setFooterVisible( true );
      TestUtil.flush();

      var areaNode = tree._rowContainer.getElement();
      var footerNode = tree._footer.getElement();
      assertEquals( 0, parseInt( areaNode.style.top, 10 ) );
      assertEquals( 470, parseInt( areaNode.style.height, 10 ) );
      assertEquals( 600, parseInt( areaNode.style.width, 10 ) );
      assertEquals( 470, parseInt( footerNode.style.top, 10 ) );
      assertEquals( 30, parseInt( footerNode.style.height, 10 ) );
      assertEquals( 600, parseInt( footerNode.style.width, 10 ) );
      tree.destroy();
    },

    testShowColumnFooterAndHeader : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      this._fakeAppearance();
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );

      tree.setFooterHeight( 30 );
      tree.setFooterVisible( true );
      tree.setHeaderHeight( 20 );
      tree.setHeaderVisible( true );
      TestUtil.flush();

      var areaNode = tree._rowContainer.getElement();
      var footerNode = tree._footer.getElement();
      var headerNode = tree._header.getElement();
      assertEquals( 20, parseInt( areaNode.style.top, 10 ) );
      assertEquals( 450, parseInt( areaNode.style.height, 10 ) );
      assertEquals( 600, parseInt( areaNode.style.width, 10 ) );
      assertEquals( 0, parseInt( headerNode.style.top, 10 ) );
      assertEquals( 20, parseInt( headerNode.style.height, 10 ) );
      assertEquals( 600, parseInt( headerNode.style.width, 10 ) );
      assertEquals( 470, parseInt( footerNode.style.top, 10 ) );
      assertEquals( 30, parseInt( footerNode.style.height, 10 ) );
      assertEquals( 600, parseInt( footerNode.style.width, 10 ) );
      tree.destroy();
    },

    testShowColumnFooterWithScrollbars : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      this._fakeAppearance();
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );

      tree.setScrollBarsVisible( true, true );
      tree.setFooterHeight( 30 );
      tree.setFooterVisible( true );
      TestUtil.flush();

      var horizontal = TestUtil.getElementBounds( tree._horzScrollBar.getElement() );
      var vertical = TestUtil.getElementBounds( tree._vertScrollBar.getElement() );
      var footerNode = tree._footer.getElement();
      var areaNode = tree._rowContainer.getElement();
      var expectedAreaHeight = 470 - horizontal.height; // 500 - footerHeight = 470
      var expectedAreaWidth = 600 - vertical.width;
      assertEquals( 0, parseInt( areaNode.style.top, 10 ) );
      assertEquals( expectedAreaHeight, parseInt( areaNode.style.height, 10 ) );
      assertEquals( expectedAreaWidth, parseInt( areaNode.style.width, 10 ) );
      assertEquals( 30, parseInt( footerNode.style.height, 10 ) );
      assertEquals( 590, parseInt( footerNode.style.width, 10 ) );
      assertEquals( 0, vertical.top );
      assertEquals( expectedAreaHeight + 30, vertical.height );
      assertEquals( expectedAreaWidth, vertical.left );
      assertEquals( expectedAreaHeight + 30, horizontal.top );
      assertEquals( expectedAreaWidth, horizontal.width );
      assertEquals( 0, horizontal.left );
      tree.destroy();
    },

    testShowColumnHeaderAndFooterWithScrollbars : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      this._fakeAppearance();
      tree.addToDocument();
      tree.setItemHeight( 20 );
      tree.setHeight( 500 );
      tree.setWidth( 600 );

      tree.setScrollBarsVisible( true, true );
      tree.setFooterHeight( 30 );
      tree.setFooterVisible( true );
      tree.setHeaderHeight( 20 );
      tree.setHeaderVisible( true );
      TestUtil.flush();

      var horizontal = TestUtil.getElementBounds( tree._horzScrollBar.getElement() );
      var vertical = TestUtil.getElementBounds( tree._vertScrollBar.getElement() );
      var footerNode = tree._footer.getElement();
      var headerNode = tree._header.getElement();
      var areaNode = tree._rowContainer.getElement();
      var expectedAreaHeight = 450 - horizontal.height; // 500 - footerHeight - headerHeigth = 450
      var expectedAreaWidth = 600 - vertical.width;
      assertEquals( 20, parseInt( areaNode.style.top, 10 ) );
      assertEquals( expectedAreaHeight, parseInt( areaNode.style.height, 10 ) );
      assertEquals( expectedAreaWidth, parseInt( areaNode.style.width, 10 ) );
      assertEquals( expectedAreaHeight + 20, parseInt( footerNode.style.top, 10 ) );
      assertEquals( 30, parseInt( footerNode.style.height, 10 ) );
      assertEquals( 590, parseInt( footerNode.style.width, 10 ) );
      assertEquals( 0, parseInt( headerNode.style.top, 10 ) );
      assertEquals( 20, parseInt( headerNode.style.height, 10 ) );
      assertEquals( 600, parseInt( headerNode.style.width, 10 ) );
      assertEquals( 20, vertical.top );
      assertEquals( expectedAreaHeight + 30, vertical.height );
      assertEquals( expectedAreaWidth, vertical.left );
      assertEquals( expectedAreaHeight + 50, horizontal.top );
      assertEquals( expectedAreaWidth, horizontal.width );
      assertEquals( 0, horizontal.left );
      tree.destroy();
    },

    testCreateTreeColumn : function() {
      var tree = this._createDefaultTree();
      tree.setHeaderVisible( true );
      var column = new rwt.widgets.GridColumn( tree );
      TestUtil.flush();
      var label = this._getColumnLabel( tree, column );

      assertEquals( tree._header, label.getParent() );
      assertEquals( "tree-column", label.getAppearance() );
      assertEquals( "100%", label.getHeight() );
      tree.destroy();
    },

    testCreateTableColumn : function() {
      var tree = this._createDefaultTree( false, true );
      tree.setHeaderVisible( true );
      var column = new rwt.widgets.GridColumn( tree );
      TestUtil.flush();
      assertEquals( "table-column", this._getColumnLabel( tree, column ).getAppearance() );
      tree.destroy();
    },

    testShowDummyColumn : function() {
      var tree = this._createDefaultTree();
      rwt.remote.EventUtil.setSuspended( true );
      var column = new rwt.widgets.GridColumn( tree );
      column.setLeft( 0 );
      column.setWidth( 500 );
      tree.setWidth( 600 );
      tree.setHeaderVisible( true );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var dummy = tree._header._dummyColumn;
      assertEquals( tree._header, this._getColumnLabel( tree, column ).getParent() );
      assertTrue( dummy.getVisibility() );
      assertTrue( dummy.hasState( "dummy" ) );
      assertEquals( "tree-column", dummy.getAppearance() );
      // Fix for IEs DIV-height bug (322802):
      assertEquals( 500, dummy.getLeft() );
      assertEquals( 100, dummy.getWidth() );
      tree.destroy();
    },

    testShowDummyFooter : function() {
      var tree = this._createDefaultTree();
      rwt.remote.EventUtil.setSuspended( true );
      var column = new rwt.widgets.GridColumn( tree );
      column.setLeft( 0 );
      column.setWidth( 500 );
      tree.setWidth( 600 );
      tree.setFooterVisible( true );
      tree.setFooterHeight( 30 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var dummy = tree._footer._dummyColumn;
      assertEquals( tree._footer, this._getColumnLabel( tree, column, true ).getParent() );
      assertTrue( dummy.getVisibility() );
      assertTrue( dummy.hasState( "dummy" ) );
      assertEquals( "tree-column", dummy.getAppearance() );
      // Fix for IEs DIV-height bug (322802):
      assertEquals( 500, dummy.getLeft() );
      assertEquals( 100, dummy.getWidth() );
      tree.destroy();
    },

    testDummyColumnAppearance : function() {
      var tree = this._createDefaultTree( false, true );
      rwt.remote.EventUtil.setSuspended( true );
      var column = new rwt.widgets.GridColumn( tree );
      column.setLeft( 0 );
      column.setWidth( 500 );
      tree.setWidth( 600 );
      tree.setHeaderVisible( true );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var dummy = tree._header._dummyColumn;
      assertEquals( "table-column", dummy.getAppearance() );
      tree.destroy();
    },

    testDontShowDummyColumn : function() {
      var tree = this._createDefaultTree();
      rwt.remote.EventUtil.setSuspended( true );
      var column = new rwt.widgets.GridColumn( tree );
      column.setLeft( 0 );
      column.setWidth( 500 );
      tree.setHeaderVisible( true );
      tree.setWidth( 490 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var dummy = tree._header._dummyColumn;
      assertEquals( tree._header, dummy.getParent() );
      assertEquals( 0, dummy.getWidth() );
      assertTrue( dummy.hasState( "dummy" ) );
      tree.destroy();
    },

    testShowMinimalDummyColumn : function() {
      var tree = this._createDefaultTree();
      rwt.remote.EventUtil.setSuspended( true );
      tree.setHeaderHeight( 15 );
      tree.setScrollBarsVisible( true, true );
      var column = new rwt.widgets.GridColumn( tree );
      column.setLeft( 0 );
      column.setWidth( 500 );
      tree.setHeaderVisible( true );
      tree.setWidth( 450 );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var barWidth = tree._vertScrollBar.getWidth();
      var dummy = tree._header._dummyColumn;
      assertTrue( dummy.getVisibility() );
      assertEquals( 500, dummy.getLeft() );
      assertEquals( barWidth, dummy.getWidth() );
      tree.destroy();
    },

    testOnlyShowDummyColumn : function() {
      var tree = this._createDefaultTree();
      rwt.remote.EventUtil.setSuspended( true );
      tree.setHeaderVisible( true );
      tree.setScrollBarsVisible( true, true );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var dummy = tree._header._dummyColumn;
      assertTrue( dummy.getVisibility() );
      assertEquals( 0, dummy.getLeft() );
      //assertEquals( 500, dummy.getWidth() );
      assertTrue( dummy.hasState( "dummy" ) );
      tree.destroy();
    },

    testReLayoutDummyColumn : function() {
      var tree = this._createDefaultTree();
      rwt.remote.EventUtil.setSuspended( true );
      var column = new rwt.widgets.GridColumn( tree );
      column.setLeft( 0 );
      column.setWidth( 500 );
      tree.setWidth( 600 );
      tree.setHeaderVisible( true );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      var dummy = tree._header._dummyColumn;
      assertEquals( 500, dummy.getLeft() );
      assertEquals( 100, dummy.getWidth() );
      rwt.remote.EventUtil.setSuspended( true );
      column.setWidth( 400 );
      TestUtil.flush();
      rwt.remote.EventUtil.setSuspended( false );
      assertEquals( 400, dummy.getLeft() );
      assertEquals( 200, dummy.getWidth() );
      tree.destroy();
    },

    testScrollHeaderHorizontal : function() {
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 2, 500, 600, 0, 0, 0, 500 );
      tree.setColumnCount( 3 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      var columnX = new rwt.widgets.GridColumn( tree );
      ObjectRegistry.add( "wCol", columnX, columnHandler );
      columnX.setLeft( 0 );
      columnX.setWidth( 1100 );
      TestUtil.flush();
      tree._horzScrollBar.setValue( 400 );
      assertEquals( 400, tree._header.getScrollLeft() );
      tree.destroy();
    },

    testChangeTreeTextColor : function() {
      var tree = this._createDefaultTree();
      tree.setTextColor( "red" );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "Test1" ] );
      TestUtil.flush();
      var node = tree._rowContainer.getRow( 0 ).$el.get( 0 );
      assertEquals( "red", node.style.color );
      tree.setTextColor( "blue" );
      TestUtil.flush();
      assertEquals( "blue", node.style.color );
      tree.destroy();
    },

    changeTreeFont : function() {
      var tree = this._createDefaultTree();
      tree.setFont( new rwt.html.Font( 12, [ "monospace" ] ) );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "Test1" ] );
      TestUtil.flush();
      var row = tree._rowContainer.getRow( 0 );
      var node = row.$el.get( 0 ).childNodes[ 0 ];
      var font = TestUtil.getElementFont( node );
      assertTrue( font.indexOf( "monospace" ) != -1 );
      tree.setFont( new rwt.html.Font( 12, [ "fantasy" ] ) );
      TestUtil.flush();
      assertTrue( font.indexOf( "fantasy" ) != -1 );
      tree.destroy();
      row.dispose();
    },

    testDisposeTreeColumn : function() {
      var tree = this._createDefaultTree();
      var column = new rwt.widgets.GridColumn( tree );
      tree.setHeaderVisible( true );
      TestUtil.flush();
      var labels = tree._header.getChildren().length;
      column.dispose();
      TestUtil.flush();
      assertEquals( labels - 1, tree._header.getChildren().length );
      tree.destroy();
    },

    testChangeItemMetrics : function() {
      var tree = this._createDefaultTree();
      tree.setTreeColumn( 1 );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "Test1" ] );
      TestUtil.flush();
      tree.setItemMetrics( 0, 0, 500, 0, 0, 30, 500 );
      TestUtil.flush();
      var node = tree._rowContainer.getRow( 0 ).$el.get( 0 ).firstChild;
      assertEquals( 30, parseInt( node.style.left, 10 ) );
      tree.destroy();
    },

    testMoveColumn : function() {
      var tree = this._createDefaultTree();
      rwt.remote.EventUtil.setSuspended( true );
      tree.setHeaderVisible( true );
      var column = new rwt.widgets.GridColumn( tree );
      tree.setItemCount( 1 );
      this._createItem( tree.getRootItem(), 0 );
      rwt.remote.ObjectRegistry.add( "w11", column, columnHandler );
      column.setLeft( 100 );
      column.setWidth( 100 );
      column.setMoveable( true );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      TestUtil.initRequestLog();
      var left = rwt.event.MouseEvent.buttons.left;
      var node = this._getColumnLabel( tree, column )._getTargetNode();

      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 0, 0 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, 5, 0 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", left, 5, 0 );

      assertNotNull( TestUtil.getMessageObject().findCallOperation( "w11", "move" ) );
      tree.destroy();
    },

    testResizeColumn : function() {
      var tree = this._createDefaultTree();
      rwt.remote.EventUtil.setSuspended( true );
      tree.setHeaderVisible( true );
      var column = new rwt.widgets.GridColumn( tree );
      tree.setItemCount( 1 );
      this._createItem( tree.getRootItem(), 0 );
      rwt.remote.ObjectRegistry.add( "w11", column, columnHandler );
      column.setLeft( 100 );
      column.setWidth( 100 );
      column.setMoveable( true );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      TestUtil.initRequestLog();
      var left = rwt.event.MouseEvent.buttons.left;
      var node = this._getColumnLabel( tree, column )._getTargetNode();

      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 200, 0 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, 205, 0 );
      assertEquals( "table-column-resizer", tree._resizeLine.getAppearance() );
      var line = tree._resizeLine._getTargetNode();
      assertIdentical( tree._getTargetNode(), line.parentNode );
      assertEquals( 203, parseInt( line.style.left, 10 ) );
      assertEquals( "", tree._resizeLine.getStyleProperty( "visibility" ) );
      TestUtil.fakeMouseEventDOM( node, "mouseup", left, 205, 0 );

      assertNotNull( TestUtil.getMessageObject().findCallOperation( "w11", "resize" ) );
      assertEquals( "hidden", tree._resizeLine.getStyleProperty( "visibility" ) );
      tree.destroy();
    },

    testSetAlignment : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.addToDocument();
      tree.setHeaderVisible( true );
      var column1 = new rwt.widgets.GridColumn( tree );
      column1.setIndex( 0 );
      var column2 = new rwt.widgets.GridColumn( tree );
      column2.setIndex( 1 );
      var column3 = new rwt.widgets.GridColumn( tree );
      column3.setIndex( 2 );

      column1.setAlignment( "left" );
      column2.setAlignment( "center" );
      column3.setAlignment( "right" );
      TestUtil.flush();

      assertEquals( "left", tree.getRenderConfig().alignment[ 0 ] );
      assertEquals( "center", tree.getRenderConfig().alignment[ 1 ] );
      assertEquals( "right", tree.getRenderConfig().alignment[ 2 ] );
      assertEquals( "left", this._getColumnLabel( tree, column1 ).getHorizontalChildrenAlign() );
      assertEquals( "center", this._getColumnLabel( tree, column2 ).getHorizontalChildrenAlign() );
      assertEquals( "right", this._getColumnLabel( tree, column3 ).getHorizontalChildrenAlign() );
      tree.destroy();
    },

    testRenderAlignmentChange : function() {
      var tree = this._createDefaultTree();
      tree.setAlignment( 0, "right" );
      tree.setTreeColumn( 1 );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      item.setTexts( [ "Test1" ] );
      TestUtil.flush();
      var row = tree._rowContainer.getRow( 0 );
      var node = row.$el.get( 0 ).childNodes[ 0 ];
      assertEquals( "right", node.style.textAlign );
      tree.setAlignment( 0, "center" );
      TestUtil.flush();
      assertEquals( "center", node.style.textAlign );
      tree.destroy();
      row.dispose();
    },

    testSendScrollLeft : function() {
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      tree.setTreeColumn( 1 );
      tree.setItemCount( 1 );
      this._createItem( tree.getRootItem(), 0 );
      TestUtil.initRequestLog();
      TestUtil.flush();

      tree._horzScrollBar.setValue( 160 );
      assertEquals( 0, TestUtil.getRequestsSend() );
      rwt.remote.Connection.getInstance().send();

      assertEquals( 160, TestUtil.getMessageObject().findSetProperty( "w3", "scrollLeft" ) );
      tree.destroy();
    },

    testSetScrollLeft : function() {
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      TestUtil.flush();
      tree.setScrollLeft( 160 );
      assertEquals( 160, tree._horzScrollBar.getValue() );
      tree.destroy();
    },

    testSetScrollLeftBeforeAppear : function() {
      // See Bug 325091 (also the next 3 tests)
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      var columnX = new rwt.widgets.GridColumn( tree );
      ObjectRegistry.add( "colX", columnX, columnHandler );
      columnX.setLeft( 0 );
      columnX.setWidth( 1100 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      TestUtil.flush();
      tree.hide();
      TestUtil.flush();
      tree.setScrollLeft( 160 );
      tree.show();
      assertEquals( 160, tree._horzScrollBar.getValue() );
      assertEquals( 160, tree._rowContainer.getScrollLeft() );
      assertEquals( 160, tree._header.getScrollLeft() );
      tree.destroy();
    },

    testSetScrollLeftBeforeCreate : function() {
      var tree = this._createDefaultTree( true );
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      var columnX = new rwt.widgets.GridColumn( tree );
      ObjectRegistry.add( "colX", columnX, columnHandler );
      columnX.setLeft( 0 );
      columnX.setWidth( 1100 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      tree.setScrollLeft( 160 );
      TestUtil.flush();
      assertEquals( 160, tree._horzScrollBar.getValue() );
      assertEquals( 160, tree._rowContainer.getScrollLeft() );
      assertEquals( 160, tree._header.getScrollLeft() );
      tree.destroy();
    },

    testSetScrollBeforeColumnHeaderVisible: function() {
      var tree = this._createDefaultTree();
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      var columnX = new rwt.widgets.GridColumn( tree );
      ObjectRegistry.add( "colX", columnX, columnHandler );
      columnX.setLeft( 0 );
      columnX.setWidth( 1100 );
      TestUtil.flush();
      tree.setScrollLeft( 160 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      TestUtil.flush();
      assertEquals( 160, tree._header.getScrollLeft() );
      tree.destroy();
    },

    testDontScrollFixedColumn : function() {
      var tree = this._createDefaultTree( false, true, "fixedColumns", 1 );
      tree.setHeaderVisible( true );
      rwt.remote.EventUtil.setSuspended( true );
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      var columnX = new rwt.widgets.GridColumn( tree );
      ObjectRegistry.add( "colX", columnX, columnHandler );
      columnX.setLeft( 10 );
      columnX.setWidth( 1100 );
      var label = this._getColumnLabel( tree, columnX );
      columnX.setFixed( true );
      TestUtil.flush();
      assertEquals( 1e7, label.getZIndex() );
      tree.setScrollLeft( 160 );
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      rwt.remote.EventUtil.setSuspended( false );
      TestUtil.flush();
      assertEquals( 160, tree._header.getScrollLeft() );
      assertEquals( 10, columnX.getLeft() );
      assertEquals( 170, parseInt( label.getElement().style.left, 10 ) );
      tree.setScrollLeft( 10 );
      assertEquals( 20, parseInt( label.getElement().style.left, 10 ) );
      columnX.setFixed( false );
      TestUtil.flush();
      assertEquals( 1, label.getZIndex() );
      TestUtil.flush();
      assertEquals( 10, parseInt( label.getElement().style.left, 10 ) );
      tree.destroy();
    },

    testFixedColumnDontFlushInServerResponse : function() {
      var tree = this._createDefaultTree( false, true, "fixedColumns", 1 );
      rwt.remote.EventUtil.setSuspended( true );
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      var columnX = new rwt.widgets.GridColumn( tree );
      columnX.setLeft( 10 );
      ObjectRegistry.add( "colX", columnX, columnHandler );
      columnX.setWidth( 1100 );
      columnX.setFixed( true );
      TestUtil.flush();
      tree.setHeaderHeight( 30 );
      tree.setHeaderVisible( true );
      TestUtil.flush();
      tree.setScrollLeft( 10 );
      rwt.remote.EventUtil.setSuspended( false );
      var label = this._getColumnLabel( tree, columnX );
      assertEquals( 10, parseInt( label.getElement().style.left, 10 ) );
      TestUtil.flush();
      assertEquals( 20, parseInt( label.getElement().style.left, 10 ) );
      tree.destroy();
    },

    testRenderOnItemGrayed : function() {
      var tree = this._createDefaultTree( false, false, "check", [ 5, 5 ] );
      TestUtil.fakeAppearance( "tree-row-check-box",  {
        style : function( states ) {
          return {
            "backgroundImage" : states.grayed ? "grayed.gif" : "normal.gif"
          };
        }
      } );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      var node = tree._rowContainer.getRow( 0 ).$el.get( 0 ).firstChild;
      var normal = TestUtil.getCssBackgroundImage( node );
      item.setGrayed( true );
      var grayed = TestUtil.getCssBackgroundImage( node );
      assertTrue( normal.indexOf( "normal.gif" ) != -1 );
      assertTrue( grayed.indexOf( "grayed.gif" ) != -1 );
      tree.destroy();
    },

    testRenderBackgroundImage : function() {
      var tree = new rwt.widgets.Grid( { "appearance": "tree" } );
      tree.setBackgroundImage( "bla.jpg" );
      assertEquals( "bla.jpg", tree._rowContainer.getBackgroundImage() );
      tree.destroy();
    },

    testGridLinesState : function() {
      var tree = this._createDefaultTree( true );
      tree.setLinesVisible( true );
      TestUtil.flush();
      assertTrue( tree.hasState( "linesvisible" ) );
      tree.setLinesVisible( false );
      assertFalse( tree.hasState( "linesvisible" ) );
      tree.destroy();
    },

    testGridLinesHorizontal : function() {
      var tree = this._createDefaultTree();
      tree.setLinesVisible( true );
      TestUtil.flush();
      var border = tree._rowContainer._getHorizontalGridBorder();
      assertIdentical( border, tree._rowContainer._rowBorder );
      tree.destroy();
    },

    testCreateGridLinesVertical : function() {
      var tree = this._createDefaultTree();
      tree.setColumnCount( 3 );
      TestUtil.flush();
      tree.setLinesVisible( true );
      TestUtil.flush();
      assertEquals( 3, tree._rowContainer.$el.prop( "childNodes" ).length - 1 );
      tree.destroy();
    },

    testGridLinesVerticalDefaultProperties : function() {
      var tree = this._createDefaultTree();
      tree.setColumnCount( 3 );
      tree.setLinesVisible( true );
      TestUtil.flush();
      var line = tree._rowContainer.$el.prop( "childNodes" )[ 2 ];
      assertEquals( 1, parseInt( line.style.zIndex, 10 ) );
      assertEquals( "0px", line.style.width );
      assertTrue( line.style.border !== "" || line.style.borderRight !== "" );
      tree.destroy();
    },

    testAddGridLinesVertical : function() {
      var tree = this._createDefaultTree();
      tree.setLinesVisible( true );
      tree.setColumnCount( 1 );
      TestUtil.flush();
      assertEquals( 1, tree._rowContainer.$el.prop( "childNodes" ).length - 1 );
      tree.setColumnCount( 3 );
      TestUtil.flush();
      assertEquals( 3  , tree._rowContainer.$el.prop( "childNodes" ).length - 1 );
      tree.destroy();
    },

    testRemoveGridLinesVertical : function() {
      var tree = this._createDefaultTree();
      tree.setLinesVisible( true );
      tree.setColumnCount( 3 );
      TestUtil.flush();
      assertEquals( 3, tree._rowContainer.$el.prop( "childNodes" ).length - 1 );
      tree.setColumnCount( 1 );
      TestUtil.flush();
      assertEquals( 1, tree._rowContainer.$el.prop( "childNodes" ).length - 1 );
      tree.destroy();
    },

    testDisableGridLinesVertical : function() {
      var tree = this._createDefaultTree();
      tree.setLinesVisible( true );
      tree.setColumnCount( 3 );
      TestUtil.flush();
      assertEquals( 3, tree._rowContainer.$el.prop( "childNodes" ).length - 1 );
      tree.setLinesVisible( false );
      TestUtil.flush();
      assertEquals( 0, tree._rowContainer.$el.prop( "childNodes" ).length - 1 );
      tree.destroy();
    },

    testGridLinesVerticalLayoutY : function() {
      var tree = this._createDefaultTree();
      tree.setWidth( 1000 );
      tree.setColumnCount( 3 );
      tree.setLinesVisible( true );
      TestUtil.flush();
      var line = tree._rowContainer.$el.prop( "childNodes" )[ 1 ];
      assertEquals( "0px", line.style.top );
      assertEquals( "500px", line.style.height );
      tree.setHeaderHeight( 20 );
      tree.setHeaderVisible( true );
      TestUtil.flush();
      assertEquals( "0px", line.style.top );
      assertEquals( "480px", line.style.height );
      if( !TestUtil.isMobileWebkit() ) {
        tree.setScrollBarsVisible( true, true );
        assertEquals( "0px", line.style.top );
        assertTrue( parseInt( line.style.top, 10 ) < 480 );
      }
      tree.destroy();
    },

    testGridLinesVerticalPositionX : function() {
      var tree = this._createDefaultTree();
      tree.setColumnCount( 3 );
      tree.setLinesVisible( true );
      tree.setItemMetrics( 0, 0, 202, 0, 0, 0, 400 );
      tree.setItemMetrics( 1, 205, 100, 0, 0, 0, 400 );
      tree.setItemMetrics( 2, 310, 50, 0, 0, 0, 400 );
      TestUtil.flush();
      var line1 = tree._rowContainer.$el.prop( "childNodes" )[ 1 ];
      var line2 = tree._rowContainer.$el.prop( "childNodes" )[ 2 ];
      var line3 = tree._rowContainer.$el.prop( "childNodes" )[ 3 ];
      assertEquals( 201, parseInt( line1.style.left, 10 ) );
      assertEquals( 304, parseInt( line2.style.left, 10 ) );
      assertEquals( 359, parseInt( line3.style.left, 10 ) );
      tree.destroy();
    },

    testRedrawOnShiftMultiSelection : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      TestUtil.fakeAppearance( "tree-row", {
        style : function( states ) {
          var result = {};
          if( states.selected ) {
            result.background = "blue";
          } else {
            result.background = "white";
          }
          result.backgroundGradient = null;
          result.backgroundImage = null;
          return result;
        }
      } );
      tree.setItemCount( 3 );
      this._createItem( tree.getRootItem(), 0 );
      this._createItem( tree.getRootItem(), 1 );
      this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      var rows = tree._rowContainer.$rows.prop( "children" );
      TestUtil.click( rows[ 0 ] );
      TestUtil.shiftClick( rows[ 2 ] );
      assertEquals( "blue", rows[ 0 ].style.backgroundColor );
      assertEquals( "blue", rows[ 1 ].style.backgroundColor );
      assertEquals( "blue", rows[ 2 ].style.backgroundColor );
      tree.destroy();
    },

    testVirtualSendTopItemIndex : function() {
      TestUtil.prepareTimerUse();
      var tree = this._createDefaultTree( false, false, "virtual" );
      this._fillTree( tree, 100 );
      TestUtil.initRequestLog();
      TestUtil.flush();

      tree._vertScrollBar.setValue( 2 );
      tree._vertScrollBar.setValue( 8 );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertEquals( 8, TestUtil.getMessageObject().findSetProperty( "w3", "topItemIndex" ) );
      tree.destroy();
    },

    testVirtualNoScrollSendTopItemIndex : function() {
      TestUtil.prepareTimerUse();
      var tree = this._createDefaultTree( false, false, "virtual" );
      this._fillTree( tree, 100 );
      TestUtil.initRequestLog();
      TestUtil.flush();

      tree._vertScrollBar.setValue( 2 );
      tree._vertScrollBar.setValue( 8 );
      assertEquals( 0, TestUtil.getRequestsSend() );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertEquals( 8, TestUtil.getMessageObject().findSetProperty( "w3", "topItemIndex" ) );
      assertNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "Selection" ) );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w3", "SetData" ) );
      tree.destroy();
    },

    testCancelTimerOnRequest: function() {
      TestUtil.prepareTimerUse();
      var tree = this._createDefaultTree( false, false, "virtual" );
      tree.setItemMetrics( 0, 0, 1000, 0, 0, 0, 500 );
      this._createItem( tree.getRootItem() );
      TestUtil.initRequestLog();
      TestUtil.flush();
      tree._horzScrollBar.setValue( 160 );
      assertEquals( 0, TestUtil.getRequestsSend() );
      rwt.remote.Connection.getInstance().send();
      assertFalse( rwt.remote.Connection.getInstance()._delayTimer.getEnabled() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      tree.destroy();
    },

    testPreventDefaultKeys : function() {
      var tree = this._createDefaultTree();
      var stopped = true;
      var log = [];
      tree.addEventListener( "keypress", function( event ) {
        log.push( event.getDefaultPrevented() );
      }, this );
      TestUtil.getDocument().addEventListener( "keypress", function() {
        stopped = false;
      }, this );
      TestUtil.press( tree, "Up" );
      TestUtil.press( tree, "Down" );
      TestUtil.press( tree, "Left" );
      TestUtil.press( tree, "Right" );
      TestUtil.press( tree, "PageUp" );
      TestUtil.press( tree, "PageDown" );
      TestUtil.press( tree, "Home" );
      TestUtil.press( tree, "End" );
      assertEquals( [ true, true, true, true, true, true, true, true ], log );
      assertTrue( stopped );
      tree.destroy();
    },

    testKeyboardNavigationUpDown : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 2 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      item0.setItemCount( 1 );
      var item1 = this._createItem( item0, 0 );
      var item2 = this._createItem( tree.getRootItem(), 1 );
      item0.setExpanded( true );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 2 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      assertTrue( tree.isFocusItem( item2 ) );
      TestUtil.press( tree, "Up" );
      TestUtil.press( tree, "Up" );
      assertTrue( tree.isFocusItem( item0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      TestUtil.press( tree, "Down" );
      assertTrue( tree.isFocusItem( item1 ) );
      assertFalse( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testKeyboardNavigationCtrlUpDown : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      var item1 = this._createItem( tree.getRootItem(), 1 );
      var item2 = this._createItem( tree.getRootItem(), 2 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.pressOnce( tree, "Down", rwt.event.DomEvent.CTRL_MASK );
      assertFalse( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testKeyboardNavigationRight : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 2 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      item0.setItemCount( 2 );
      var item1 = this._createItem( item0, 0 );
      item1.setItemCount( 2 );
      var item2 = this._createItem( item1, 0 );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      assertFalse( item0.isExpanded() );
      TestUtil.press( tree, "Right" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      assertTrue( item0.isExpanded() );
      TestUtil.press( tree, "Right" );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isFocusItem( item1 ) );
      assertFalse( item1.isExpanded() );
      TestUtil.press( tree, "Right" );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isFocusItem( item1 ) );
      assertTrue( item1.isExpanded() );
      TestUtil.press( tree, "Right" );
      assertTrue( tree.isItemSelected( item2 ) );
      assertTrue( tree.isFocusItem( item2 ) );
      assertFalse( item2.isExpanded() );
      TestUtil.press( tree, "Right" );
      assertTrue( tree.isItemSelected( item2 ) );
      assertTrue( tree.isFocusItem( item2 ) );
      assertFalse( item2.isExpanded() );
      tree.destroy();
    },

    testKeyboardNavigationLeft : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      item0.setItemCount( 1 );
      var item1 = this._createItem( item0, 0 );
      item1.setItemCount( 1 );
      var item2 = this._createItem( item1, 0 );
      item0.setExpanded( true );
      item1.setExpanded( true );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 2 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      assertTrue( tree.isFocusItem( item2 ) );
      TestUtil.press( tree, "Left" );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isFocusItem( item1 ) );
      assertTrue( item1.isExpanded() );
      TestUtil.press( tree, "Left" );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isFocusItem( item1 ) );
      assertFalse( item1.isExpanded() );
      TestUtil.press( tree, "Left" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      assertTrue( item0.isExpanded() );
      TestUtil.press( tree, "Left" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      assertFalse( item0.isExpanded() );
      TestUtil.press( tree, "Left" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      assertFalse( item0.isExpanded() );
      tree.destroy();
    },

    testKeyboardNavigationOnlyOneItem : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      TestUtil.press( tree, "Up" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      TestUtil.press( tree, "Down" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      tree.destroy();
    },

    testKeyboardNavigationScrollDown : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      var root = tree.getRootItem();
      TestUtil.flush();
      assertEquals( 26, tree._rowContainer.$rows.prop( "children" ).length );
      assertIdentical( root.getChild( 0 ), tree._rowContainer._topItem );
      TestUtil.clickDOM( tree._rowContainer.$rows.prop( "children" )[ 23 ] );
      assertTrue( tree.isItemSelected( root.getChild( 23 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 23 ) ) );
      TestUtil.press( tree, "Down" );
      assertIdentical( root.getChild( 0 ), tree._rowContainer._topItem );
      TestUtil.press( tree, "Down" );
      assertIdentical( root.getChild( 1 ), tree._rowContainer._topItem );
      tree.destroy();
    },

    testKeyboardNavigationScrollUp : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      TestUtil.flush();
      assertIdentical( root.getChild( 50 ), tree._rowContainer._topItem );
      TestUtil.clickDOM( tree._rowContainer.getRow( 1 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( root.getChild( 51 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 51 ) ) );
      TestUtil.press( tree, "Up" );
      assertIdentical( root.getChild( 50 ), tree._rowContainer._topItem );
      TestUtil.press( tree, "Up" );
      assertIdentical( root.getChild( 49 ), tree._rowContainer._topItem );
      tree.destroy();
    },

    testKeyboardNavigationPageUp : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      TestUtil.flush();
      assertEquals( 26, tree._rowContainer.$rows.prop( "children" ).length );
      assertIdentical( root.getChild( 50 ), tree._rowContainer._topItem );
      TestUtil.clickDOM( tree._rowContainer.getRow( 5 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( root.getChild( 55 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 55 ) ) );

      TestUtil.press( tree, "PageUp" );

      assertIdentical( root.getChild( 31 ), tree._rowContainer._topItem );
      assertTrue( tree.isItemSelected( root.getChild( 31 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 31 ) ) );
      tree.destroy();
    },

    testKeyboardNavigationPageUpWithCustomHeight : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      for( var i = 0; i < 100; i++ ) {
        root.getChild( i ).setHeight( 50 );
      }
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 5 ).$el.get( 0 ) );
      assertTrue( tree.isFocusItem( root.getChild( 55 ) ) );

      TestUtil.press( tree, "PageUp" );

      assertIdentical( root.getChild( 46 ), tree._rowContainer._topItem );
      assertTrue( tree.isFocusItem( root.getChild( 46 ) ) );
      tree.destroy();
    },

    testKeyboardNavigationPageDown : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      TestUtil.flush();
      assertEquals( 26, tree._rowContainer.$rows.prop( "children" ).length );
      assertIdentical( root.getChild( 50 ), tree._rowContainer._topItem );
      TestUtil.clickDOM( tree._rowContainer.getRow( 5 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( root.getChild( 55 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 55 ) ) );

      TestUtil.press( tree, "PageDown" );

      assertTrue( tree.isFocusItem( root.getChild( 79 ) ) );
      assertTrue( tree.isItemSelected( root.getChild( 79 ) ) );
      assertIdentical( root.getChild( 55 ), tree._rowContainer._topItem );
      tree.destroy();
    },

    testKeyboardNavigationPageDownWithCustomItemHeight : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      var root = tree.getRootItem();
      for( var i = 0; i < 100; i++ ) {
        root.getChild( i ).setHeight( 50 );
      }
      tree.setTopItemIndex( 50 );
      TestUtil.flush();
      assertIdentical( root.getChild( 50 ), tree._rowContainer._topItem );
      TestUtil.clickDOM( tree._rowContainer.getRow( 5 ).$el.get( 0 ) );
      assertTrue( tree.isFocusItem( root.getChild( 55 ) ) );

      TestUtil.press( tree, "PageDown" );

      assertTrue( tree.isFocusItem( root.getChild( 64 ) ) );
      assertIdentical( root.getChild( 55 ), tree._rowContainer._topItem );
      tree.destroy();
    },

    testPageUpOutOfBounds : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      var root = tree.getRootItem();
      TestUtil.flush();
      assertEquals( 26, tree._rowContainer.$rows.prop( "children" ).length );
      assertIdentical( root.getChild( 0 ), tree._rowContainer._topItem );
      TestUtil.clickDOM( tree._rowContainer.getRow( 5 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( root.getChild( 5 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 5 ) ) );
      TestUtil.press( tree, "PageUp" );
      assertIdentical( root.getChild( 0 ), tree._rowContainer._topItem );
      assertTrue( tree.isItemSelected( root.getChild( 0 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 0 ) ) );
      tree.destroy();
    },

    testPageDownOutOfBounds : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 10 );
      var root = tree.getRootItem();
      TestUtil.flush();
      assertEquals( 26, tree._rowContainer.$rows.prop( "children" ).length );
      assertIdentical( root.getChild( 0 ), tree._rowContainer._topItem );
      TestUtil.clickDOM( tree._rowContainer.getRow( 5 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( root.getChild( 5 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 5 ) ) );
      TestUtil.press( tree, "PageDown" );
      assertIdentical( root.getChild( 0 ), tree._rowContainer._topItem );
      assertTrue( tree.isItemSelected( root.getChild( 9 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 9 ) ) );
      tree.destroy();
    },

    testKeyboardNavigationShiftSelect : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      item0.setItemCount( 1 );
      var item1 = this._createItem( item0, 0 );
      var item2 = this._createItem( tree.getRootItem(), 1 );
      var item3 = this._createItem( tree.getRootItem(), 2 );
      item0.setExpanded( true );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      TestUtil.shiftPress( tree, "Down" );
      TestUtil.shiftPress( tree, "Down" );
      TestUtil.shiftPress( tree, "Down" );
      TestUtil.shiftPress( tree, "Up" );
      assertTrue( tree.isFocusItem( item2 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      assertFalse( tree.isItemSelected( item3 ) );
      tree.destroy();
    },

    testKeyboardNavigationCtrlOnlyMovesFocus : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 3 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      item0.setItemCount( 1 );
      var item1 = this._createItem( item0, 0 );
      var item2 = this._createItem( tree.getRootItem(), 1 );
      var item3 = this._createItem( tree.getRootItem(), 2 );
      item0.setExpanded( true );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      TestUtil.ctrlPress( tree, "Down" );
      TestUtil.ctrlPress( tree, "Down" );
      TestUtil.ctrlPress( tree, "Down" );
      TestUtil.ctrlPress( tree, "Up" );
      assertTrue( tree.isFocusItem( item2 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertFalse( tree.isItemSelected( item2 ) );
      assertFalse( tree.isItemSelected( item3 ) );
      tree.destroy();
    },

    testKeyboardNavigationCtrlAndSpaceSelects : function() {
      var tree = this._createDefaultTree( false, false );
      tree.setItemCount( 1 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      item0.setItemCount( 1 );
      this._createItem( item0, 0 );
      item0.setExpanded( true );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      TestUtil.initRequestLog();
      TestUtil.fakeListener( tree, "Selection", true );
      TestUtil.ctrlPress( tree, "Space" );
      assertFalse( tree.isItemSelected( item0 ) );
      TestUtil.ctrlPress( tree, "Space" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertEquals( 2, TestUtil.getRequestsSend() );
      tree.destroy();
    },

    testKeyboardNavigationCtrlAndSpaceMultiSelects : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 1 );
      var item0 = this._createItem( tree.getRootItem() ,0 );
      item0.setItemCount( 1 );
      var item1 = this._createItem( item0, 0 );
      item0.setExpanded( true );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      TestUtil.ctrlPress( tree, "Down" );
      assertTrue( tree.isFocusItem( item1 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      TestUtil.ctrlPress( tree, "Space" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      TestUtil.ctrlPress( tree, "Space" );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      tree.destroy();
    },

    testKeyboardNavigationSpaceDoesNotCheckWithoutCheckBox : function() {
      var tree = this._createDefaultTree( false, false );
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item ) );
      assertTrue( tree.isFocusItem( item ) );
      assertFalse( item.isChecked() );
      TestUtil.initRequestLog();
      TestUtil.fakeListener( tree, "Selection", true );
      TestUtil.press( tree, "Space" );
      assertFalse( item.isChecked() );
      assertEquals( 0, TestUtil.getRequestsSend() );
      tree.destroy();
    },

    testKeyboardNavigationSpaceChecks : function() {
      var tree = this._createDefaultTree( false, false, "check", [ 5, 20 ]  );
      this._fakeCheckBoxAppearance();
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      TestUtil.flush();
      TestUtil.initRequestLog();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item ) );
      assertTrue( tree.isFocusItem( item ) );
      assertFalse( item.isChecked() );
      TestUtil.fakeListener( tree, "Selection", true );
      TestUtil.press( tree, "Space" );
      assertTrue( item.isChecked() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      tree.destroy();
    },

    testKeyboardNavigationNoShiftSelectForLeftRight : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 1 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      item0.setItemCount( 1 );
      var item1 = this._createItem( item0, 0 );
      item0.setExpanded( true );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isFocusItem( item0 ) );
      TestUtil.shiftPress( tree, "Right" );
      assertTrue( tree.isFocusItem( item1 ) );
      assertFalse( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      TestUtil.shiftPress( tree, "Left" );
      assertTrue( tree.isFocusItem( item0 ) );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      tree.destroy();
    },

    testKeyboardNavigationHome : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      TestUtil.flush();
      assertEquals( 26, tree._rowContainer.$rows.prop( "children" ).length );
      assertIdentical( root.getChild( 50 ), tree._rowContainer._topItem );
      TestUtil.clickDOM( tree._rowContainer.getRow( 5 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( root.getChild( 55 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 55 ) ) );
      TestUtil.press( tree, "Home" );
      assertIdentical( root.getChild( 0 ), tree._rowContainer._topItem );
      assertTrue( tree.isItemSelected( root.getChild( 0 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 0 ) ) );
      tree.destroy();
    },

    testKeyboardNavigationEnd : function() {
      var tree = this._createDefaultTree();
      this._fillTree( tree, 100 );
      tree.setTopItemIndex( 50 );
      var root = tree.getRootItem();
      TestUtil.flush();
      assertEquals( 26, tree._rowContainer.$rows.prop( "children" ).length );
      assertIdentical( root.getChild( 50 ), tree._rowContainer._topItem );
      TestUtil.clickDOM( tree._rowContainer.getRow( 5 ).$el.get( 0 ) );
      assertTrue( tree.isItemSelected( root.getChild( 55 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 55 ) ) );
      TestUtil.press( tree, "End" );
      assertEquals( 75, tree.getTopItemIndex() );
      assertTrue( tree.isItemSelected( root.getChild( 99 ) ) );
      assertTrue( tree.isFocusItem( root.getChild( 99 ) ) );
      tree.destroy();
    },

    testKeyboardNavigationEndThenAddItems : function() {
      var tree = this._createDefaultTree();
      tree.setHeight( 416 );
      tree.setItemHeight( 28 );
      this._fillTree( tree, 2 );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      tree.setItemCount( 21 );

      TestUtil.press( tree, "End" );
      assertEquals( 7, tree.getTopItemIndex() );
      tree.setItemCount( 26 );
      TestUtil.flush();

      assertEquals( 7, tree.getTopItemIndex() );
      tree.destroy();
    },

    testScrollWheelToEndThenAddItems : function() {
      var tree = this._createDefaultTree();
      tree.setHeight( 416 );
      tree.setItemHeight( 28 );
      this._fillTree( tree, 2 );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ) );
      tree.setItemCount( 21 );

      for( var i = 0; i < 10; i++ ) {
        TestUtil.fakeWheel( tree._rowContainer, -1 );
      }
      assertEquals( 7, tree.getTopItemIndex() );
      tree.setItemCount( 26 );
      TestUtil.flush();

      assertEquals( 7, tree.getTopItemIndex() );
      tree.destroy();
    },

    testDeselectionOnCollapseByMouse : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      TestUtil.fakeAppearance( "tree-row-indent",  {
        style : function() {
          return { "backgroundImage" : "bla.gif" };
        }
      } );
      tree.setItemCount( 2 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      item0.setItemCount( 1 );
      var item1 = this._createItem( item0, 0 );
      var item2 = this._createItem( tree.getRootItem(), 1 );
      item0.setExpanded( true );
      tree.selectItem( item0 );
      tree.selectItem( item1 );
      tree.selectItem( item2 );
      TestUtil.flush();
      TestUtil.clickDOM( tree._rowContainer.getRow( 0 ).$el.get( 0 ).firstChild );
      assertFalse( item0.isExpanded() );
      assertTrue( tree.isItemSelected( item0 ) );
      assertFalse( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

    testNoDeselectionOnNonMouseCollapse : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      TestUtil.fakeAppearance( "tree-row-indent",  {
        style : function() {
          return { "backgroundImage" : "bla.gif" };
        }
      } );
      tree.setItemCount( 2 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      item0.setItemCount( 1 );
      var item1 = this._createItem( item0, 0 );
      var item2 = this._createItem( tree.getRootItem(), 1 );
      item0.setExpanded( true );
      tree.selectItem( item0 );
      tree.selectItem( item1 );
      tree.selectItem( item2 );
      tree.setFocusItem( item0 );
      TestUtil.flush();
      item0.setExpanded( false );
      item0.setExpanded( true );
      tree.focus();
      TestUtil.press( tree, "Left" );
      assertFalse( item0.isExpanded() );
      assertTrue( tree.isItemSelected( item0 ) );
      assertTrue( tree.isItemSelected( item1 ) );
      assertTrue( tree.isItemSelected( item2 ) );
      tree.destroy();
    },

      // TODO [tb] : Can currently not be done since focusItem isn't synced
//    testDeselectFocusedItemOnCollapse : function() {
////      var tree = this._createDefaultTree();
//      TestUtil.fakeAppearance( "tree-row-indent",  {
//        style : function( states ) {
//          return { "backgroundImage" : "bla.gif" };
//        }
//      } );
//      tree.setHasMultiSelection( true );
//      var item0 = this._createItem( tree.getRootItem() );
//      var item1 = this._createItem( item0 );
//      var item2 = this._createItem( tree.getRootItem() );
//      item0.setExpanded( true );
//      tree.selectItem( item0 );
//      tree.selectItem( item1 );
//      tree.selectItem( item2 );
//      tree.setFocusItem( item1 );
//      TestUtil.flush();
//      item0.setExpanded( false );
//      assertTrue( tree.isItemSelected( item0 ) );
//      assertFalse( tree.isItemSelected( item1 ) );
//      assertTrue( tree.isItemSelected( item2 ) );
//      tree.destroy();
//    },

    testMoveFocusOnCollapse : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      var item0 = this._createItem( tree.getRootItem(), 0 );
      item0.setItemCount( 1 );
      var item1 = this._createItem( item0, 0 );
      item0.setExpanded( true );
      tree.setFocusItem( item1 );
      TestUtil.flush();
      item0.setExpanded( false );
      assertTrue( tree.isFocusItem( item0 ) );
      tree.destroy();
    },

    testNoDoubleClickOnDifferentItems : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeListener( tree, "Selection", true );
      tree.setItemCount( 2 );
      this._createItem( tree.getRootItem(), 0 );
      this._createItem( tree.getRootItem(), 1 );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.click( tree._rowContainer.getRow( 0 ), 10, 10 );
      TestUtil.click( tree._rowContainer.getRow( 1 ), 20, 20 );

      assertEquals( 2, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject( 0 ).findNotifyOperation( "w3", "DefaultSelection" ) );
      assertNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "w3", "DefaultSelection" ) );
      assertNotNull( TestUtil.getMessageObject( 0 ).findNotifyOperation( "w3", "Selection" ) );
      assertNotNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "w3", "Selection" ) );
      tree.destroy();
    },

    testNoDoubleClickOnSameItem : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeListener( tree, "Selection", true );
      tree.setItemCount( 2 );
      this._createItem( tree.getRootItem(), 0 );
      this._createItem( tree.getRootItem(), 1 );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.click( tree._rowContainer.getRow( 0 ), 10, 10 );
      TestUtil.click( tree._rowContainer.getRow( 0 ), 20, 10 );

      assertEquals( 2, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject( 0 ).findNotifyOperation( "w3", "DefaultSelection" ) );
      assertNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "w3", "DefaultSelection" ) );
      assertNotNull( TestUtil.getMessageObject( 0 ).findNotifyOperation( "w3", "Selection" ) );
      assertNotNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "w3", "Selection" ) );
      tree.destroy();
    },

    testNoDefaultSelectionWithCtrlSpace : function() {
      var tree = this._createDefaultTree();
      TestUtil.fakeListener( tree, "Selection", true );
      tree.setItemCount( 1 );
      var child0 = this._createItem( tree.getRootItem(), 0 );
      tree.setFocusItem( child0 );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.ctrlPress( tree, "Space" );
      TestUtil.ctrlPress( tree, "Space" );
      TestUtil.ctrlPress( tree, "Space" );
      TestUtil.ctrlPress( tree, "Space" );

      assertEquals( 4, TestUtil.getRequestsSend() );
      assertNull( TestUtil.getMessageObject( 0 ).findNotifyOperation( "w3", "DefaultSelection" ) );
      assertNull( TestUtil.getMessageObject( 1 ).findNotifyOperation( "w3", "DefaultSelection" ) );
      assertNull( TestUtil.getMessageObject( 2 ).findNotifyOperation( "w3", "DefaultSelection" ) );
      assertNull( TestUtil.getMessageObject( 3 ).findNotifyOperation( "w3", "DefaultSelection" ) );
      tree.destroy();
    },

    testKeyEventBeforeFlush : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      var child0 = this._createItem( tree.getRootItem(), 0 );
      tree.setFocusItem( child0 );
      TestUtil.ctrlPress( tree, "Space" );
      // succeeds by not crashing
      tree.destroy();
    },

    testRemoveDisposedItemFromState : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setItemCount( 2 );
      var child0 = this._createItem( tree.getRootItem(), 0 );
      child0.setTexts( [ "C0" ] );
      var child1 = this._createItem( tree.getRootItem(), 1 );
      child1.setTexts( [ "C1" ] );
      var child2 = this._createItem( tree.getRootItem(), 1 );
      child2.setTexts( [ "C2" ] );
      tree.setFocusItem( child0 );
      tree.setTopItemIndex( 0 );
      TestUtil.flush();
      TestUtil.mouseOver( tree._rowContainer.getRow( 0 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 0 ) );
      tree.selectItem( child1 );
      tree.selectItem( child2 );
      assertEquals( child0, tree._rowContainer._topItem );
      assertEquals( child0, tree._focusItem );
      assertEquals( child0, tree._leadItem );
      assertEquals( [ child0, child1, child2 ], tree._selection );
      child0.dispose();
      child1.dispose();
      TestUtil.flush();
      assertNull( tree._focusItem );
      assertNull( tree._leadItem );
      assertEquals( [ child2 ], tree._selection );
      tree.destroy();
    },

    testRemoveIndirectlyDisposedItemFromState : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      tree.setHeight( 15 );
      tree.setItemHeight( 20 );
      tree.setItemCount( 1 );
      var child0 = this._createItem( tree.getRootItem(), 0 );
      child0.setItemCount( 1 );
      var child1 = this._createItem( child0, 0 );
      child1.setTexts( [ "C1" ] );
      child0.setExpanded( true );
      tree.setTopItemIndex( 1 );
      tree.setFocusItem( child1 );
      TestUtil.flush();
      assertIdentical( child1, tree._rowContainer._topItem );
      TestUtil.mouseOver( tree._rowContainer.getRow( 0 ) );
      TestUtil.shiftClick( tree._rowContainer.getRow( 0 ) );
      assertIdentical( child1, tree._focusItem );
      assertEquals( [ child1 ], tree._selection );
      assertIdentical( child1, tree._leadItem );
      child0.dispose(); // Order is important for this test
      child1.dispose();
      var child0new = this._createItem( tree.getRootItem(), 0 );
      child0new.setItemCount( 1 );
      var child1new = this._createItem( child0new, 0 );
      child1new.setTexts( [ "C1new" ] );
      child0new.setExpanded( true );
      TestUtil.flush();
      assertIdentical( child1new, tree._rowContainer._topItem );
      assertNull( tree._leadItem );
      assertNull( tree._focusItem );
      assertEquals( [], tree._selection );
      tree.destroy();
    },

    testDisposeAndCollapseParentItem : function() {
      var tree = this._createDefaultTree();
      tree.setItemCount( 1 );
      var child0 = this._createItem( tree.getRootItem(), 0 );
      child0.setItemCount( 1 );
      var child1 = this._createItem( child0, 0 );
      child1.setTexts( [ "C1" ] );
      child0.setExpanded( true );
      tree.setFocusItem( child1 );
      TestUtil.flush();
      child1.dispose();
      child0.setExpanded( false );
      assertEquals( child0, tree._focusItem );
      tree.destroy();
    },

    testTreeMultiSelectionDrag : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      var dragSource = this._createDragSource( tree );
      tree.setItemCount( 2 );
      var child0 = this._createItem( tree.getRootItem(), 0 );
      var child1 = this._createItem( tree.getRootItem(), 1 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.ctrlClick( tree._rowContainer.getRow( 1 ) );
      assertTrue( tree.isItemSelected( child0 ) );
      assertTrue( tree.isItemSelected( child1 ) );
      tree._selectionTimestamp = null; // prevent double click detection
      TestUtil.fakeMouseEvent( tree._rowContainer.getRow( 1 ), "mousedown" );
      assertTrue( "child0 selected", tree.isItemSelected( child0 ) );
      assertTrue( "child1 selected", tree.isItemSelected( child1 ) );
      TestUtil.fakeMouseEvent( tree._rowContainer.getRow( 1 ), "mouseup" );
      TestUtil.fakeMouseEvent( tree._rowContainer.getRow( 1 ), "click" );
      assertFalse( tree.isItemSelected( child0 ) );
      assertTrue( tree.isItemSelected( child1 ) );
      dragSource.dispose();
      tree.destroy();
    },

    testTreeMultiSelectionDragMouseOut : function() {
      var tree = this._createDefaultTree( false, false, "multiSelection" );
      var dragSource = this._createDragSource( tree );
      tree.setItemCount( 2 );
      var child0 = this._createItem( tree.getRootItem(), 0 );
      var child1 = this._createItem( tree.getRootItem(), 1 );
      TestUtil.flush();
      TestUtil.click( tree._rowContainer.getRow( 0 ) );
      TestUtil.ctrlClick( tree._rowContainer.getRow( 1 ) );
      assertTrue( tree.isItemSelected( child0 ) );
      assertTrue( tree.isItemSelected( child1 ) );
      tree._selectionTimestamp = null; // prevent double click detection
      TestUtil.fakeMouseEvent( tree._rowContainer.getRow( 1 ), "mousedown" );
      TestUtil.mouseOut( tree._rowContainer.getRow( 1 ) );
      TestUtil.mouseOut( tree );
      TestUtil.mouseOver( tree );
      TestUtil.mouseOver( tree._rowContainer.getRow( 1 ) );
      assertTrue( "child0 selected", tree.isItemSelected( child0 ) );
      assertTrue( "child1 selected", tree.isItemSelected( child1 ) );
      TestUtil.fakeMouseEvent( tree._rowContainer.getRow( 1 ), "mouseup" );
      assertTrue( tree.isItemSelected( child0 ) );
      assertTrue( tree.isItemSelected( child1 ) );
      dragSource.dispose();
      tree.destroy();
    },

    testRequestCellToolTipText : function() {
      var tree = this._createDefaultTree();
      var widgetManager = rwt.remote.WidgetManager.getInstance();
      widgetManager.add( tree, "w3", true );
      tree.setEnableCellToolTip( true );
      tree.setColumnCount( 6 );
      tree.setItemMetrics( 0, 0, 5, 0, 0, 0, 50 );
      tree.setItemMetrics( 1, 5, 10, 0, 0, 0, 50 );
      tree.setItemMetrics( 2, 15, 10, 0, 0, 0, 50 );
      tree.setItemMetrics( 3, 25, 10, 0, 0, 0, 50 );
      tree.setItemMetrics( 4, 35, 350, 0, 0, 0, 50 );
      tree.setItemMetrics( 5, 400, 100, 405, 10, 430, 50 );
      tree.setItemCount( 1 );
      var item = this._createItem( tree.getRootItem(), 0 );
      widgetManager.add( item, "w45", true );
      TestUtil.flush();
      var leftButton = rwt.event.MouseEvent.buttons.left;
      var node = tree._rowContainer.getRow( 0 ).$el.get( 0 );

      TestUtil.fakeMouseEventDOM( node, "mouseover", leftButton, 450, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 450, 11 );
      TestUtil.forceInterval( rwt.widgets.base.WidgetToolTip.getInstance()._showTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject( 0 );
      var actualItem = message.findCallProperty( "w3", "renderToolTipText", "item" );
      var actualCol = message.findCallProperty( "w3", "renderToolTipText", "column" );
      assertEquals( "w45", actualItem );
      assertEquals( 5, actualCol );
      tree.destroy();
    },

    testSendSelectedEventHorizontal : function() {
      TestUtil.createShellByProtocol( "w2" );
      var tree = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w3_hscroll",
        "action" : "create",
        "type" : "rwt.widgets.ScrollBar",
        "properties" : {
          "parent" : "w3",
          "style" : [ "HORIZONTAL" ]
        }
      } );
      this._createTreeItemByProtocol( "w4", "w3", 0 );
      tree.setItemHeight( 20 );
      tree.setItemMetrics( 0, 0, 500, 0, 0, 0, 500, 0, 10 );
      tree._vertScrollBar.setHasSelectionListener( true );
      tree._horzScrollBar.setHasSelectionListener( true );
      TestUtil.flush();

      tree.setScrollLeft( 30 );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var msg = TestUtil.getMessageObject();
      assertEquals( 30, msg.findSetProperty( "w3", "scrollLeft" ) );
      assertNotNull( msg.findNotifyOperation( "w3_hscroll", "Selection" ) );
      tree.destroy();
    },

    testSendSelectedEventVertical : function() {
      TestUtil.createShellByProtocol( "w2" );
      var tree = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : "w3_vscroll",
        "action" : "create",
        "type" : "rwt.widgets.ScrollBar",
        "properties" : {
          "parent" : "w3",
          "style" : [ "VERTICAL" ]
        }
      } );
      this._fillTree( tree, 10 );
      tree.setItemHeight( 20 );
      tree.setItemMetrics( 0, 0, 500, 0, 0, 0, 500, 0, 10 );
      tree._vertScrollBar.setHasSelectionListener( true );
      tree._horzScrollBar.setHasSelectionListener( true );
      TestUtil.flush();

      tree._setTopItemIndex( 3 );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var msg = TestUtil.getMessageObject();
      assertEquals( 3, msg.findSetProperty( "w3", "topItemIndex" ) );
      assertNotNull( msg.findNotifyOperation( "w3_vscroll", "Selection" ) );
      tree.destroy();
    },

    // see bug 419982
    testSetTopItemIndexAndScrollBarsVisibilityTogether : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var widget = this._createDefaultTreeByProtocol( "w3", "w2", [] );
      var hScroll = this._createScrollBarByProtocol( "s1", "w3", [ "HORIZONTAL" ] );
      var vScroll = this._createScrollBarByProtocol( "s2", "w3", [ "VERTICAL" ] );
      widget.setItemCount( 10 );
      widget.setItemHeight( 20 );
      TestUtil.flush();

      TestUtil.protocolSet( "w3", { "topItemIndex" : 3 } );
      TestUtil.protocolSet( "s1", { "visibility" : true } );
      TestUtil.protocolSet( "s2", { "visibility" : true } );

      assertEquals( 3, widget._vertScrollBar.getValue() );
      assertEquals( 3, widget.getTopItemIndex() );
      shell.destroy();
      widget.destroy();
      hScroll.destroy();
      vScroll.destroy();
    },

    /////////
    // helper

    _createDefaultTreeByProtocol : function( id, parentId, styles ) {
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
          "bounds" : [ 0, 0, 100, 100 ]
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    _createScrollBarByProtocol : function( id, parentId, styles ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.ScrollBar",
        "properties" : {
          "style" : styles,
          "parent" : parentId
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    _createTreeItemByProtocol : function( id, parentId, index ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : parentId,
          "index": index
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    _createDefaultTree : function( noflush, asTable, option, arg ) {
      rwt.remote.EventUtil.setSuspended( true );
      this._fakeAppearance();
      var appearance = asTable ? "table" : "tree";
      var args = { "appearance": appearance };
      if( option ) {
        args[ option ] = arg ? arg : true;
      }
      if( option === "check" ) {
        args[ "checkBoxMetrics" ] = arg;
      }
      if( option === "fixedColumns" ) {
        args[ "splitContainer" ] = true;
      }
      args[ "fullSelection" ] = true;
      args[ "selectionPadding" ] = [ 2, 4 ];
      args[ "indentionWidth" ] = 16;
      var tree = new rwt.widgets.Grid( args );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Grid" );
      new rwt.widgets.util.GridSynchronizer( tree );
      rwt.remote.ObjectRegistry.add( "w3", tree, handler );
      if( option === "fixedColumns" ) {
        rwt.widgets.util.GridUtil.setFixedColumns( tree, arg );
      }
      if( option === "virtual" ) {
        TestUtil.fakeListener( tree, "SetData", true );
      }
      tree.setItemHeight( 20 );
      tree.setLeft( 0 );
      tree.setTop( 0 );
      tree.setWidth( 500 );
      tree.setHeight( 500 );
      tree.setItemMetrics( 0, 0, 500, 0, 0, 0, 500, 0, 10 );
      tree.setColumnCount( 1 );
      tree.setItemMetrics( 1, 0, 500, 0, 0, 0, 500, 0, 10 );
      tree.setItemMetrics( 2, 0, 500, 0, 0, 0, 500, 0, 10 );
      tree.addToDocument();
      if( !noflush ) {
        TestUtil.flush();
      }
      rwt.remote.EventUtil.setSuspended( false );
      return tree;
    },

    _fillTree : function( tree, count, subItems, flatCount ) {
      tree.setItemCount( ( subItems && flatCount ) ? ( count / 2 ) : count );
      var i = 0;
      var itemNr = 0;
      while( i < count ) {
        var item = this._createItem( tree.getRootItem(), itemNr );
        itemNr++;
        item.setTexts( [ "Test" + i ] );
        if( subItems ) {
          item.setItemCount( 1 );
          var subitem = this._createItem( item, 0 );
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
      TestUtil.fakeAppearance( "tree-row-indent", empty );
      TestUtil.fakeAppearance( "tree-row", empty );
      TestUtil.fakeAppearance( "tree-row-overlay", empty );
    },

    _fakeCheckBoxAppearance : function() {
      TestUtil.fakeAppearance( "tree-row-check-box", {
        style : function() {
          return {
            "backgroundImage" : "check.png"
          };
        }
      } );
    },

    _getColumnLabel : function( grid, column, footer ) {
      var header = footer ? grid.getFooter() : grid.getTableHeader();
      return header._getLabelByColumn( column );
    },

    _createItem : function( parent, index, placeholder ) {
      var result = new rwt.widgets.GridItem( parent, index, placeholder );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.GridItem" );
      rwt.remote.ObjectRegistry.add( "w" + result.toHashCode(), result, handler );
      return result;
    },

    _createDragSource : function( control ) {
      var operations = [ "DROP_COPY", "DROP_MOVE", "DROP_LINK" ];
      var dragSource = new rwt.widgets.DragSource( control, operations );
      rwt.remote.DNDSupport.getInstance().setDragSourceTransferTypes( control, [ "default" ] );
      return dragSource;
    }

  }

} );

  var getElementBounds = function( node ) {
    return {
      left : node.offsetLeft,
      top : node.offsetTop,
      width : node.offsetWidth,
      height : node.offsetHeight
    };
  };

}() );
