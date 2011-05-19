/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.TableTest", {
  extend : qx.core.Object,

  construct : function() {
    var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
    testUtil.prepareTimerUse();
    testUtil.initRequestLog();
    qx.Class.__initializeClass( org.eclipse.rwt.widgets.ScrollBar );
    org.eclipse.rwt.widgets.ScrollBar.prototype._getMinThumbSize = function() { return 8; };
  },
  
  members : {
    
    testCreateTable : function() {
      var table = this._createDefaultTable();
      assertTrue( table instanceof org.eclipse.swt.widgets.Table );
      assertEquals( "table", table.getAppearance() );
      table.destroy();
    },
    
    testTableCreatesRows : function() {
      var table = this._createDefaultTable();
      assertEquals( 4, table._rows.length );
      assertTrue( table._rows[ 0 ] instanceof org.eclipse.swt.widgets.TableRow );
      table.destroy();
    },

    testSelectItem : function() {
      var table = this._createDefaultTable();
      var item = new org.eclipse.swt.widgets.TableItem( table, 0 );
      table.selectItem( 0 );
      assertTrue( table._isItemSelected( 0 ) );
      table.deselectItem( 0 );
      assertFalse( table._isItemSelected( 0 ) );
      table.destroy();
    },

    testTableLinesVisibleState : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var table = this._createDefaultTable( true );
      table.setLinesVisible( true );
      testUtil.flush();
      assertTrue( table.hasState( "linesvisible" ) );
      assertTrue( table._rows[ 0 ].hasState( "linesvisible" ) );
      table.setLinesVisible( false );
      assertFalse( table.hasState( "linesvisible" ) );
      assertFalse( table._rows[ 0 ].hasState( "linesvisible" ) );      
      table.destroy();
    },
    
    testSendSetDataEvents : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.clearRequestLog();
      var table = this._createDefaultTable();
      assertNull( 0, table._resolveItemsFor );
      table.setItemCount( 100 );
      assertEquals( 0, table._resolveItemsFor );
      assertEquals( 0, testUtil.getRequestsSend() );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      var msg = testUtil.getMessage();
      assertTrue( msg.indexOf( "setData.index=0%2C1%2C2" ) != -1 );
      assertNull( 0, table._resolveItemsFor );
      table.destroy();
    },
    
    testSendSetDataEventsOnScroll : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.clearRequestLog();
      var table = this._createDefaultTable();
      table.setItemCount( 100 );
      testUtil.flush();
      table._vertScrollBar.setMergeEvents( true );
      table._vertScrollBar.setValue( 100 );
      testUtil.forceInterval( table._vertScrollBar._eventTimer );
      assertEquals( 5, table._resolveItemsFor );
      assertEquals( 0, testUtil.getRequestsSend() );
      table._vertScrollBar.setValue( 200 );
      assertEquals( 5, table._resolveItemsFor );
      table._vertScrollBar.setValue( 300 );
      testUtil.forceInterval( table._vertScrollBar._eventTimer );
      assertEquals( 15, table._resolveItemsFor );
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      var msg = testUtil.getMessage();
      assertTrue( msg.indexOf( "setData.index=15%2C16%2C" ) != -1 );
      table.destroy();
    },
    
    testSetItemHeight : function() {
      var table = this._createDefaultTable();
      table.setItemHeight( 30 );
      assertEquals( 3, table._rows.length );
      assertEquals( 30, table._vertScrollBar._increment );
      table.destroy();
    },
    
    testScrollBarsPreventDragStart : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var leftButton = qx.event.type.MouseEvent.buttons.left;
      var table = this._createDefaultTable();
      table.setScrollBarsVisibile( true, true );
      var log = [];
      var loghandler = function( event ) { log.push( event ); }
      var drag = function( node ) {
        testUtil.fakeMouseEventDOM( node, "mousedown", leftButton, 11, 11 );
        testUtil.fakeMouseEventDOM( node, "mousemove", leftButton, 25, 15 );
        testUtil.fakeMouseEventDOM( node, "mouseup", leftButton, 25, 15 );
      };
      table.addEventListener( "dragstart", loghandler );
      drag( table._getTargetNode() );
      assertEquals( 1, log.length );
      drag( table._horzScrollBar._getTargetNode() );
      drag( table._vertScrollBar._getTargetNode() );
      assertEquals( 1, log.length );      
      table.destroy();
    },

    testEnableCellToolTip : function() {
      var table = this._createDefaultTable();
      assertNull( table._cellToolTip );
      assertNull( table._clientArea.getToolTip() );
      table.setEnableCellToolTip( true );
      assertNotNull( table._cellToolTip );
      assertNotNull( table._clientArea.getToolTip() );
      table.setEnableCellToolTip( false );
      assertNull( table._cellToolTip );
      assertNull( table._clientArea.getToolTip() );
      table.destroy();
    },

    testMoveableColumnMenuDetect : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.initRequestLog();      
      var table = this._createDefaultTable();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.add( table, "w3", true );
      var column = new org.eclipse.swt.widgets.TableColumn( table );
      column.setWidth( 150 );
      column.setMoveable( true );
      table.addEventListener( "mouseup", org.eclipse.swt.EventUtil.menuDetectedByMouse );
      testUtil.flush();
      testUtil.rightClick( column );
      assertEquals( 1, testUtil.getRequestsSend() );
      var msg = testUtil.getMessage();
      column.destroy();
      table.destroy();
    },

    testRequestCellToolTipText : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var table = this._createDefaultTable();
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      widgetManager.add( table, "w3", true );
      table.setEnableCellToolTip( true );
      testUtil.prepareTimerUse();
      testUtil.initRequestLog();      
      table._cellToolTip.setCell( "w45", 5 );
      testUtil.forceInterval( table._cellToolTip._showTimer );
      var msg = testUtil.getMessage();
      var param1 = "org.eclipse.swt.events.cellToolTipTextRequested=w3";
      var param2 = "org.eclipse.swt.events.cellToolTipTextRequested.cell=w45%2C5";
      assertTrue( msg.indexOf( param1 ) != -1 );
      assertTrue( msg.indexOf( param2 ) != -1 );
      table.destroy();
    },

    testAdjustSelectedIndicesAfterAddingAnItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var table = this._createDefaultTable();
      for( var i = 0; i < 5; i++ ) {
        new org.eclipse.swt.widgets.TableItem( table, i );
      }
      table.selectItem( 1 );
      table.selectItem( 2 );
      table.selectItem( 4 );
      new org.eclipse.swt.widgets.TableItem( table, 2 );
      assertEquals( [ 1, 3, 5 ], table._selected );
      table.destroy();
    },

    testAdjustSelectedIndicesAfterRemovingAnItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var table = this._createDefaultTable();
      var items = [];
      for( var i = 0; i < 5; i++ ) {
        items[ i ] = new org.eclipse.swt.widgets.TableItem( table, i );
      }
      table.selectItem( 1 );
      table.selectItem( 2 );
      table.selectItem( 4 );
      items[ 1 ].dispose();
      assertEquals( [ 1, 3 ], table._selected );
      table.destroy();
    },

    /////////
    // Helper
    
    _createDefaultTable : function( noflush ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var result = new org.eclipse.swt.widgets.Table( "w3", "" );
      result.setWidth( 100 );
      result.setHeight( 90 );
      result.setItemHeight( 20 );
      result.setItemMetrics( 0, 0, 100, 2, 10, 15, 70 );
      result.addToDocument();
      if( noflush !== false ) {
        testUtil.flush();
      }
      return result;
    }

  }
  
} );