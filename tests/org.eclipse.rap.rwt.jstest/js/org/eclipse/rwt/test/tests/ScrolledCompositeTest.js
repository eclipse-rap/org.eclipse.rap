/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ScrolledCompositeTest", {
  extend : qx.core.Object,
  
  members : {

    testSetParent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var composite = this._createComposite();
      wm.add( composite, "w3", true );
      var child = new qx.ui.basic.Terminator();
      child.setDimension( 0, 0 );
      // This is temporary: change if testing via non-internals is possible
      assertNull( composite._content );
      wm.setParent( child, "w3" );      
      assertIdentical( child, composite._content );
      assertIdentical( composite._clientArea, child.getParent() );
      child.destroy();   
      assertNull( composite._content );
      composite.destroy();
    },
    
    testBasicLayout : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite();
      var client = composite._clientArea;
      var hbar = composite._horzScrollBar;
      var vbar = composite._vertScrollBar;
      var barWidth = vbar._getScrollBarWidth();
      assertIdentical( composite, client.getParent() );
      assertIdentical( composite, hbar.getParent() );
      assertIdentical( composite, vbar.getParent() );
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
      composite.destroy();
    },
    
    testHideNativeScrollbars : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite();
      var client = composite._clientArea;
      var hbar = composite._horzScrollBar;
      var vbar = composite._vertScrollBar;
      var barWidth = vbar._getScrollBarWidth();
      assertEquals( "scroll", client._getTargetNode().style.overflow );
      assertEquals( "hidden", client.getElement().style.overflow );
      var elementBounds = testUtil.getElementBounds( client.getElement() );
      var targetBounds = testUtil.getElementBounds( client._getTargetNode() );
      assertEquals( elementBounds.width + barWidth, targetBounds.width );
      assertEquals( elementBounds.height + barWidth, targetBounds.height );
      composite.destroy();
    },
            
    testScrollBarVisibility : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite();
      composite.setScrollBarsVisible( false, false );
      testUtil.flush()
      assertFalse( this._isScrollbarVisible( composite, true ) );
      assertFalse( this._isScrollbarVisible( composite, false ) );
      composite.setScrollBarsVisible( true, false );
      testUtil.flush()
      assertTrue( this._isScrollbarVisible( composite, true ) );
      assertFalse( this._isScrollbarVisible( composite, false ) );
      composite.setScrollBarsVisible( false, true );
      testUtil.flush()
      assertFalse( this._isScrollbarVisible( composite, true ) );
      assertTrue( this._isScrollbarVisible( composite, false ) );
      composite.setScrollBarsVisible( true, true );
      testUtil.flush()
      assertTrue( this._isScrollbarVisible( composite, true ) );
      assertTrue( this._isScrollbarVisible( composite, false ) );
      composite.destroy();
    },
    
    testRelayoutOnScrollBarShowHide : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite();
      composite.setScrollBarsVisible( false, true );
      testUtil.flush();
      var client = composite._clientArea;
      var clientBounds = testUtil.getElementBounds( client.getElement() );
      composite.setScrollBarsVisible( true, false );
      testUtil.flush();
      newClientBounds = testUtil.getElementBounds( client.getElement() );
      assertTrue( clientBounds.width < newClientBounds.width );
      assertTrue( clientBounds.height > newClientBounds.height );
      composite.destroy();
    },
    
    testScrollBarMaximum : function() {
      var composite = this._createComposite();
      this._setScrollDimension( composite, 220, 230 );
      assertEquals( 220, composite._horzScrollBar.getMaximum() );
      assertEquals( 230, composite._vertScrollBar.getMaximum() );
      this._setScrollDimension( composite, 240, 250 );
      assertEquals( 240, composite._horzScrollBar.getMaximum() );
      assertEquals( 250, composite._vertScrollBar.getMaximum() );
      composite.destroy();
    },

    testScrollProgramatically : function() {
      var composite = this._createComposite();
      this._setScrollDimension( composite, 200, 200 );
      composite.setHBarSelection( 10 );
      composite.setVBarSelection( 20 );
      var position = this._getScrollPosition( composite );
      assertEquals( [ 10, 20 ], position );
      composite.destroy();
    },

    testScrollOutOfBounds : function() {
      var composite = this._createComposite();
      this._setScrollDimension( composite, 200, 200 );
      var maxScroll = 100; // 200 (content) - 100 (clientArea)
      // visible scrollbars => clientArea is smaller
      maxScroll += composite._vertScrollBar._getScrollBarWidth();
      composite.setHBarSelection( 150 );
      composite.setVBarSelection( 250 );
      var position = this._getScrollPosition( composite );
      assertEquals( [ maxScroll, maxScroll ], position );
      composite.destroy();
    },

    testInitialPosition : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite( true );
      composite.setHBarSelection( 10 );
      composite.setVBarSelection( 20 );
      this._setScrollDimension( composite, 200, 200, true );
      testUtil.flush();
      var position = this._getScrollPosition( composite );
      assertEquals( [ 10, 20 ], position );
      composite.destroy();      
    },

    testScrollByMouseClick : function() { 
      // native functionality can currently not be tested 
      // (possible with non-native scrollbar)
    },

    testSyncScrollBars : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite();
      this._setScrollDimension( composite, 200, 200 );
      testUtil.prepareTimerUse();
      composite.setHasSelectionListener( true );
      composite._clientArea.setScrollLeft( 10 ); 
      composite._clientArea.setScrollTop( 20 );
      composite._onscroll( {} );
      assertEquals( 10, composite._horzScrollBar.getValue() );
      assertEquals( 20, composite._vertScrollBar.getValue() );
      composite.destroy();
    },

    testSendChanges : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite();
      this._setScrollDimension( composite, 200, 200 );
      testUtil.prepareTimerUse();
      composite.setHasSelectionListener( true );
      composite.setUserData( "id", "w3" );
      // set directly, otherwise the changes are not sent
      composite._clientArea.setScrollLeft( 10 ); 
      composite._clientArea.setScrollTop( 20 );
      composite._onscroll( {} ); // The dom event would be fired after the test
      testUtil.forceTimerOnce();
      assertEquals( 1, testUtil.getRequestsSend() );
      var msg = testUtil.getMessage();
      assertTrue( msg.indexOf( "w3.horizontalBar.selection=10" ) != -1 );
      assertTrue( msg.indexOf( "w3.verticalBar.selection=20" ) != -1 );      
      composite.destroy();
    },

    testBlockScrolling : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite();
      this._setScrollDimension( composite, 200, 200 );
      composite.setHBarSelection( 10 );
      composite.setVBarSelection( 20 );
      var position = this._getScrollPosition( composite );
      assertEquals( [ 10, 20 ], position );      
      testUtil.prepareTimerUse();
      composite.setHasSelectionListener( true );
      var child = new qx.ui.basic.Terminator();
      child.setParent( composite._clientArea );
      child.setLeft( 0 );
      child.setTop( 0 );
      testUtil.flush();
      child.focus();
      composite._clientArea.setScrollLeft( 50 );
      composite._clientArea.setScrollTop( 70 );
      composite._onscroll( {} );
      assertEquals( 0, testUtil.getRequestsSend() );
      var position = this._getScrollPosition( composite );
      assertEquals( [ 10, 20 ], position );      
      composite.destroy();      
    },

    testAddMultiple : function() {
      // Setting multiple children is possible, but not supported.
      // This just ensures there are no bad errors
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite();
      var child1 = new qx.ui.basic.Terminator();
      var child2 = new qx.ui.basic.Terminator();
      // This is temporary: remove if testing via non-internals is possible
      assertNull( composite._content );
      composite.setContent( child1 );      
      assertIdentical( child1, composite._content );
      composite.setContent( child2 );      
      assertIdentical( child2, composite._content );
      child1.destroy();   
      assertIdentical( child2, composite._content );
      child2.destroy();   
      assertNull( composite._content );
      composite.destroy();
    },

    /////////
    // Helper

    _createComposite : function( noflush ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = new org.eclipse.swt.custom.ScrolledComposite();
      composite.setLeft( 10 );
      composite.setTop( 10 );
      composite.setWidth( 100 );
      composite.setHeight( 100 );
      composite.addToDocument();
      if( noflush !== true ) {
        testUtil.flush();
      }
      return composite;
    },
    
    _isScrollbarVisible : function( composite, horiz ) {
      var result;
      if( horiz ) {
        result = composite._horzScrollBar.isSeeable();
      } else {
        result = composite._vertScrollBar.isSeeable();
      }
      return result;
    },
    
    _getScrollPosition : function( composite ) {
      var client = composite._clientArea;
      return [ client.getScrollLeft(), client.getScrollTop() ];
    },
    
    _setScrollDimension : function( composite, width, height, noflush ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var child;
      if( composite._content ) {
        child = composite._content;
        child.setWidth( width );
        child.setHeight( height );
      } else {
        child = new qx.ui.basic.Terminator();
        child.setLeft( 0 );
        child.setTop( 0 );
        composite.setContent( child );
        child.setWidth( width );
        child.setHeight( height );
      }
      if( noflush !== true ) {
        testUtil.flush();
      }
    }
    
  }
  
} );