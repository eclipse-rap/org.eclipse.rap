/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ListTest", {
  extend : qx.core.Object,
  
  members : {

    testCreateDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      assertTrue( list instanceof org.eclipse.swt.widgets.List );
      list.destroy();
      testUtil.flush();
      assertTrue( list.isDisposed() );
    },
    
    testSetItems : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      testUtil.flush();
      var items = this._getItems( list );
      assertEquals( 3, items.length );
      assertEquals( "item0", items[ 0 ].getLabel() );
      assertEquals( "item1", items[ 1 ].getLabel() );
      assertEquals( "item2", items[ 2 ].getLabel() );
      list.destroy();
    },
    
    testSelectItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      testUtil.flush();
      list.selectItem( 2 );
      var selection = this._getSelection( list ); 
      assertEquals( 1, selection.length );
      assertEquals( "item2", selection[ 0 ].getLabel() );
      list.destroy();
    },
    
    testSelectItemByCharacter : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "Akira", "Boogiepop", "C something", "Daria" ] );
      testUtil.flush();
      testUtil.press( list, "c" )
      var selection = this._getSelection( list ); 
      assertEquals( 1, selection.length );
      assertEquals( "C something", selection[ 0 ].getLabel() );
      list.destroy();
    },

    testSelectItems : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      testUtil.flush();
      list.selectItems( [ 1, 2 ] );
      var selection = this._getSelection( list ); 
      assertEquals( 2, selection.length );
      assertEquals( "item1", selection[ 0 ].getLabel() );
      assertEquals( "item2", selection[ 1 ].getLabel() );
      list.destroy();      
    },

    testSelectAll : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      testUtil.flush();
      list.selectAll();
      var selection = this._getSelection( list ); 
      assertEquals( 3, selection.length );
      assertEquals( "item0", selection[ 0 ].getLabel() );
      assertEquals( "item1", selection[ 1 ].getLabel() );
      assertEquals( "item2", selection[ 2 ].getLabel() );
      list.destroy();            
    },
    
    testFocusItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      list.focusItem( 1 );
      testUtil.flush();
      assertEquals( "item1", this._getLeadItem( list ).getLabel() );
      list.selectAll();
      list.destroy();
    },
    
    testSetTopIndex : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 300 );
      testUtil.flush();
      list.setTopIndex( 40 );
      assertEquals( 40, this._getTopItemIndex( list ) );
      list.selectAll();
      list.destroy();
    },
    
    testSendSelection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      testUtil.flush();
      var item = this._getItems( list )[ 1 ];
      list.setChangeSelectionNotification( "action" );
      list.setUserData( "id", "w3" );
      testUtil.click( item );
      assertEquals( 1, testUtil.getRequestsSend() );
      assertTrue( testUtil.getMessage().indexOf( "w3.selection=1" ) != -1 );
      list.selectAll();
      list.destroy();
    },
    
    testSetItemDimensions : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItemDimensions( 200, 20 );
      this._addItems( list, 3 );
      testUtil.flush();
      var items = this._getItems( list );
      assertEquals( 200, items[ 0 ].getWidth() );
      assertEquals( 20, items[ 0 ].getHeight() );
      assertEquals( 20, list._vertScrollBar._increment );
      assertEquals( 20, list._vertScrollBar._increment );
      list.setItemDimensions( 100, 30 );
      testUtil.flush();
      items = this._getItems( list );
      assertEquals( 100, items[ 0 ].getWidth() );
      assertEquals( 30, items[ 0 ].getHeight() );
      assertEquals( 30, list._vertScrollBar._increment );
      list.destroy();
    },

    testSendDefaultSelected : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      testUtil.flush();
      var item = this._getItems( list )[ 1 ];
      list.setChangeSelectionNotification( "action" );
      list.setUserData( "id", "w3" );
      testUtil.doubleClick( item );
      assertEquals( 2, testUtil.getRequestsSend() );
      var msg = testUtil.getRequestLog()[ 1 ];
      assertTrue( msg.indexOf( "widgetDefaultSelected=w3" ) != -1 );
      list.selectAll();
      list.destroy();
    },

    testBasicLayout : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      var client = list._clientArea;
      var hbar = list._horzScrollBar;
      var vbar = list._vertScrollBar;
      var barWidth = 15;
      assertIdentical( list, client.getParent() );
      assertIdentical( list, hbar.getParent() );
      assertIdentical( list, vbar.getParent() );
      var clientBounds = testUtil.getElementBounds( client.getElement() );
      var hbarBounds = testUtil.getElementBounds( hbar.getElement() );
      var vbarBounds = testUtil.getElementBounds( vbar.getElement() );
      assertEquals( 0, clientBounds.left );
      assertEquals( 0, clientBounds.top );
      assertEquals( barWidth, clientBounds.right );
      assertEquals( barWidth, clientBounds.bottom );
      assertEquals( 0, hbarBounds.left );
      assertEquals( barWidth, hbarBounds.right );
      assertEquals( 0, vbarBounds.top );
      assertEquals( barWidth, vbarBounds.bottom );
      assertEquals( clientBounds.width, vbarBounds.left );
      assertEquals( clientBounds.height, hbarBounds.top );
      list.destroy();
    },
    
    testScrollBarVisibility : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setScrollBarsVisible( false, false );
      testUtil.flush()
      assertFalse( this._isScrollbarVisible( list, true ) );
      assertFalse( this._isScrollbarVisible( list, false ) );
      list.setScrollBarsVisible( true, false );
      testUtil.flush()
      assertTrue( this._isScrollbarVisible( list, true ) );
      assertFalse( this._isScrollbarVisible( list, false ) );
      list.setScrollBarsVisible( false, true );
      testUtil.flush()
      assertFalse( this._isScrollbarVisible( list, true ) );
      assertTrue( this._isScrollbarVisible( list, false ) );
      list.setScrollBarsVisible( true, true );
      testUtil.flush()
      assertTrue( this._isScrollbarVisible( list, true ) );
      assertTrue( this._isScrollbarVisible( list, false ) );
      list.destroy();
    },
    
    testRelayoutOnScrollBarShowHide : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setScrollBarsVisible( false, true );
      testUtil.flush();
      var client = list._clientArea;
      var clientBounds = testUtil.getElementBounds( client.getElement() );
      list.setScrollBarsVisible( true, false );
      testUtil.flush();
      newClientBounds = testUtil.getElementBounds( client.getElement() );
      assertTrue( clientBounds.width < newClientBounds.width );
      assertTrue( clientBounds.height > newClientBounds.height );
      list.destroy();
    },

    testScrollBarMaximum : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 10 );
      list.setItemDimensions( 240, 25 )
      var item = list._clientArea.getFirstChild();
      testUtil.flush();
      assertEquals( 240, list._horzScrollBar.getMaximum() );
      assertEquals( 250, list._vertScrollBar.getMaximum() );
      list.destroy();
    },

    testScrollProgramatically : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      testUtil.flush();
      list.setHBarSelection( 10 );
      list.setVBarSelection( 20 );
      var position = this._getScrollPosition( list );
      assertEquals( [ 10, 20 ], position );
      list.destroy();
    },

    testScrollWhileInvisible : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItemDimensions( 500, 20 );
      this._addItems( list, 70 );
      testUtil.flush();
      list.hide();
      list.setHBarSelection( 10 );
      list.setVBarSelection( 20 );
      list.show();
      var position = this._getScrollPosition( list );
      assertEquals( [ 10, 20 ], position );
      list.destroy();
    },

//    testDispose: function() {
//      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
//      var list = this._createDefaultList();
//      this._setScrollDimension( list, 200, 200 );
//      list.setHBarSelection( 10 );
//      list.setVBarSelection( 20 );
//      var clientArea = list._clientArea;
//      var hbar = list._horzScrollBar;
//      var vbar = list._vertScrollBar;
//      var scrollNode = clientArea._getTargetNode();
//      list.destroy();
//      testUtil.flush();
//      assertNull( list._horzScrollBar );
//      assertNull( list._vertScrollBar );
//      assertNull( list._clientArea );
//      assertTrue( list.isDisposed() );
//      assertTrue( clientArea.isDisposed() );
//      assertTrue( hbar.isDisposed() );
//      assertTrue( vbar.isDisposed() );
//      assertNull( list.hasEventListeners( "changeParent" ) );
//      assertNull( clientArea.hasEventListeners( "appear" ) );
//      assertNull( clientArea.hasEventListeners( "mousewheel" ) );
//      assertNull( clientArea.hasEventListeners( "keypress" ) );
//      assertNull( hbar.hasEventListeners( "changeValue" ) );
//      assertNull( vbar.hasEventListeners( "changeValue" ) );
//    },

    testInitialPosition : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList( true );
      list.setHBarSelection( 10 );
      list.setVBarSelection( 20 );
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      testUtil.flush();
      var position = this._getScrollPosition( list );
      assertEquals( [ 10, 20 ], position );
      list.destroy();      
    },

    testSyncScrollBars : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      testUtil.flush();
      list._clientArea.setScrollLeft( 10 ); 
      list._clientArea.setScrollTop( 20 );
      list._onscroll( {} );
      assertEquals( 10, list._horzScrollBar.getValue() );
      assertEquals( 20, list._vertScrollBar.getValue() );
      list.destroy();
    },

    testNoScrollStyle : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      list.setScrollBarsVisible( false, false );
      testUtil.flush();
      list._clientArea.setScrollLeft( 50 );
      list._clientArea.setScrollTop( 70 );
      list._onscroll( {} );
      testUtil.forceTimerOnce();
      var position = this._getScrollPosition( list );
      assertEquals( [ 0, 0 ], position );      
      list.destroy();      
    },

    testOnlyHScrollStyle : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      list.setScrollBarsVisible( true, false );
      testUtil.flush();
      testUtil.flush();
      list._clientArea.setScrollLeft( 50 );
      list._clientArea.setScrollTop( 70 );
      list._onscroll( {} );
      var position = this._getScrollPosition( list );
      assertEquals( [ 50, 0 ], position );
      list.destroy();      
    },

    testOnlyVScrollStyle : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      list.setScrollBarsVisible( false, true );
      testUtil.flush();
      list._clientArea.setScrollLeft( 50 );
      list._clientArea.setScrollTop( 70 );
      list._onscroll( {} );
      var position = this._getScrollPosition( list );
      assertEquals( [ 0, 70 ], position );      
      list.destroy();      
    },
    
    //////////
    // Helpers
    
    _createDefaultList : function( noflush ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = new org.eclipse.swt.widgets.List( true );
      list.setItemDimensions( 100, 20 );
      list.addToDocument(),
      list.setSpace( 5, 238, 5, 436 );
      if( noflush !== true ) {
        testUtil.flush();
      }
      return list;
    },
    
    _addItems : function( list, number ) {
      var items = [];
      for( var i = 0; i < number; i++ ) {
        items.push( "item" + i );
      }
      list.setItems( items );
    },
    
    _getItems : function( list ) {
      return list._clientArea.getChildren();
    },
    
    _getSelection : function( list ) {
      return list.getSelectedItems();
    },
    
    _getLeadItem : function( list ) {
      return list.getManager().getLeadItem();
    },
    
    _getTopItemIndex : function( list ) {
      return list._getTopIndex();
    },
    
    _isScrollbarVisible : function( list, horiz ) {
      var result;
      if( horiz ) {
        result = list._horzScrollBar.isSeeable();
      } else {
        result = list._vertScrollBar.isSeeable();
      }
      return result;
    },

    _getScrollPosition : function( list ) {
      var client = list._clientArea;
      return [ client.getScrollLeft(), client.getScrollTop() ];
    }

  }
  
} );