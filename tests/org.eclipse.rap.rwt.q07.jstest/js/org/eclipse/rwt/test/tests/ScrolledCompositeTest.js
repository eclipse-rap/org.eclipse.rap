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
            
    testScrollBarVisibility : function() {
      var composite = this._createComposite();
      composite.setOverflow( "hidden" );
      assertFalse( this._isScrollbarVisible( composite, true ) );
      assertFalse( this._isScrollbarVisible( composite, false ) );
      composite.setOverflow( "scrollX" );
      assertTrue( this._isScrollbarVisible( composite, true ) );
      assertFalse( this._isScrollbarVisible( composite, false ) );
      composite.setOverflow( "scrollY" );
      assertFalse( this._isScrollbarVisible( composite, true ) );
      assertTrue( this._isScrollbarVisible( composite, false ) );
      composite.setOverflow( "scroll" );
      assertTrue( this._isScrollbarVisible( composite, true ) );
      assertTrue( this._isScrollbarVisible( composite, false ) );
      composite.destroy();
    },

    testScrollProgramatically : function() {
      var composite = this._createComposite();
      this._setClientAreaDimension( composite, 200, 200 );
      composite.setHBarSelection( 10 );
      composite.setVBarSelection( 20 );
      var position = this._getScrollPosition( composite );
      assertEquals( [ 10, 20 ], position );
      composite.destroy();
    },

    testScrollOutOfBounds : function() {
      var composite = this._createComposite();
      this._setClientAreaDimension( composite, 200, 200 );
      composite.setOverflow( "hidden" ); //otherwise clientarea is smaller
      composite.setHBarSelection( 150 );
      composite.setVBarSelection( 250 );
      var position = this._getScrollPosition( composite );
      assertEquals( [ 100, 100 ], position );
      composite.destroy();
    },

    testInitialPosition : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite( true );
      this._setClientAreaDimension( composite, 200, 200, true );
      composite.setHBarSelection( 10 );
      composite.setVBarSelection( 20 );
      testUtil.flush();
      var position = this._getScrollPosition( composite );
      assertEquals( [ 10, 20 ], position );
      composite.destroy();      
    },
    
    testScrollByMouseClick : function() { 
      // native functionality can currently not be tested 
      // (possible with non-native scrollbar)
    },
        
    testSendChanges : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var composite = this._createComposite();
      this._setClientAreaDimension( composite, 200, 200 );
      testUtil.prepareTimerUse();
      composite.setHasSelectionListener( true );
      composite.setUserData( "id", "w3" );
      composite.setScrollLeft( 10 ); // otherwise the changes are not sent
      composite.setScrollTop( 20 );
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
      this._setClientAreaDimension( composite, 200, 200 );
      composite.setHBarSelection( 10 );
      composite.setVBarSelection( 20 );
      var position = this._getScrollPosition( composite );
      assertEquals( [ 10, 20 ], position );      
      testUtil.prepareTimerUse();
      composite.setHasSelectionListener( true );
      var child = new qx.ui.basic.Terminator();
      child.setParent( composite );
      child.setLeft( 0 );
      child.setTop( 0 );
      testUtil.flush();
      child.focus();
      composite.setScrollLeft( 50 );
      composite.setScrollTop( 70 );
      composite._onscroll( {} );
      assertEquals( 0, testUtil.getRequestsSend() );
      var position = this._getScrollPosition( composite );
      assertEquals( [ 10, 20 ], position );      
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
      composite.setOverflow( "scroll" );
      composite.addToDocument();
      if( noflush !== true ) {
        testUtil.flush();
      }
      return composite;
    },
    
    _isScrollbarVisible : function( composite, horiz ) {
      // NOTE: ignores "auto" value
      var result;
      var overflow = composite.getOverflow();
      if( horiz ) {
        result = overflow === "scroll" || overflow === "scrollX";
      } else {
        result = overflow === "scroll" || overflow === "scrollY";
      }
      return result;
    },
    
    _getScrollPosition : function( composite ) {
      return [ composite.getScrollLeft(), composite.getScrollTop() ];
    },
    
    _setClientAreaDimension : function( composite, width, height, noflush ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var child;
      if( composite.getChildren().length > 0 ) {
        child = composite.getChildren()[ 0 ];
      } else {
        child = new qx.ui.basic.Terminator();
        child.setParent( composite );
        child.setLeft( 0 );
        child.setTop( 0 );
      }
      child.setWidth( width );
      child.setHeight( height );
      if( noflush !== true ) {
        testUtil.flush();
      }
    }
    
  }
  
} );