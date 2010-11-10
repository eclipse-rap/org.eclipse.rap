/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ScrollBarTest", {
  extend : qx.core.Object,
    
  members : {

    // NOTE : The native is very difficult, sometimes impossible to test in IE.
    // This line should be removed when using the non-native implementation:
    TARGETENGINE : [ "gecko", "webkit" ],
    
    
    testCreateDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bar = this._createScrollBar();
      assertTrue( bar.isSeeable() );
      bar.destroy();
      testUtil.flush();
      assertTrue( bar.isDisposed() );
      assertNull( bar._scrollContent )
      assertNull( bar._scrollBar )
      assertNull( bar._blocker )
    },
    
    testDimension : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var vBar = this._createScrollBar( true, false );
      var hBar = this._createScrollBar( true, true );
      assertFalse( hBar.isSeeable() );
      assertFalse( vBar.isSeeable() );
      var preferredWidth = vBar.getPreferredBoxWidth();
      var preferredHeight = hBar.getPreferredBoxHeight();
      assertTrue( preferredWidth > 0 );
      assertTrue( preferredHeight > 0 );
      assertEquals( 0, hBar.getPreferredBoxWidth() );
      assertEquals( 0, vBar.getPreferredBoxHeight() );
      testUtil.flush();
      var hBounds = testUtil.getElementBounds( hBar.getElement() );
      var vBounds = testUtil.getElementBounds( vBar.getElement() );
      assertEquals( 100, hBounds.width );
      assertEquals( 100, vBounds.height );
      assertEquals( preferredWidth, vBounds.width );
      assertEquals( preferredHeight, hBounds.height );
      hBar.destroy();      
      vBar.destroy();      
    },

    testSetValidValue : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bar = this._createScrollBar();
      var events = [];
      bar.addEventListener( "changeValue", function( event ) {
        events.push( event );
      }, this );
      bar.setValue( 50 );
      assertEquals( 50, bar.getValue() );
      bar.setValue( 200 );
      assertEquals( 200, bar.getValue() );
      bar.setValue( 0 );
      assertEquals( 0, bar.getValue() );
      assertEquals( 3, events.length );
      bar.destroy();
    },

    testSetInvalidValue : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bar = this._createScrollBar();
      var events = [];
      bar.setValue( 50 );
      assertEquals( 50, bar.getValue() );
      bar.setValue( -1 );
      assertEquals( 0, bar.getValue() );
      bar.setValue( 201 );
      assertEquals( 200, bar.getValue() );
      bar.destroy();
    },
        
    testRelativeKnobPosition : function() {
      // TODO [tb] : with new ScrollBar change to test absolute values
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var vBar = this._createScrollBar( false, false );
      var hBar = this._createScrollBar( false, true );
      vBar.setValue( 0 );
      hBar.setValue( 0 );
      assertEquals( [ 0, 0 ], this._getRelativeKnobPosition( vBar ) );
      assertEquals( [ 0, 0 ], this._getRelativeKnobPosition( hBar ) );
      vBar.setValue( 100 );
      hBar.setValue( 100 );
      assertEquals( [ 0, 33 ], this._getRelativeKnobPosition( vBar ) );
      assertEquals( [ 33, 0 ], this._getRelativeKnobPosition( hBar ) );
      hBar.setMaximum( 400 );
      vBar.setMaximum( 400 );
      assertEquals( [ 0, 25 ], this._getRelativeKnobPosition( vBar ) );
      assertEquals( [ 25, 0 ], this._getRelativeKnobPosition( hBar ) );
      vBar.setHeight( 200 );
      hBar.setWidth( 200 );
      assertEquals( [ 0, 25 ], this._getRelativeKnobPosition( vBar ) );
      assertEquals( [ 25, 0 ], this._getRelativeKnobPosition( hBar ) );
      hBar.destroy();      
      vBar.destroy();      
    },
    
    // TODO [tb] : testResizeSmallerThanMaximum
    // TODO [tb] : testChangeValueWhileInvisible

    // TODO [tb] : with new ScrollBar: testInternalLayout 
    // TODO [tb] : with new ScrollBar: testButtonImages 
    // TODO [tb] : with new ScrollBar: testKnobDimension  
    // TODO [tb] : with new ScrollBar: testClickButtons
    // TODO [tb] : with new ScrollBar: testClickBar
    // TODO [tb] : with new ScrollBar: testDragKnob

//    /////////
//    // Helper
//
    _createScrollBar : function( noFlush, horizontal) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bar = new qx.ui.basic.ScrollBar( horizontal );
      bar.setLeft( 10 );
      bar.setTop( 10 );
      bar.setMaximum( 300 );
      if( horizontal === true ) {
        var preferredHeight = bar.getPreferredBoxHeight();
        bar.setHeight( preferredHeight );
        bar.setWidth( 100 );
      } else {
        var preferredWidth = bar.getPreferredBoxWidth();
        bar.setWidth( preferredWidth );
        bar.setHeight( 100 );
      }
      bar.addToDocument();
      if( noFlush !== true ) {
        testUtil.flush();
      }
      return bar;
    },
    
    _getRelativeKnobPosition : function( bar ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var bounds = testUtil.getElementBounds( bar._scrollContent._getTargetNode() );
      var result = [ 0, 0 ];
      var offset;
      var length;
      if( bar._horizontal ) {
        offset = bar._scrollBar.getScrollLeft();
        length = bounds.width;
      } else {
        offset = bar._scrollBar.getScrollTop();
        length = bounds.height;
      }
      var position = Math.round( 100 * offset / length );
      if( bar._horizontal ) {
        result[ 0 ] = position;
      } else {
        result[ 1 ] = position;
      }
      return result;
    }
    
  }
  
} );