/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.SliderTest", {
  extend : qx.core.Object,
  
  construct : function() {
    org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
  },
    
  members : {

    testCreateDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider();
      assertTrue( slider.isSeeable() );
      slider.destroy();
      testUtil.flush();
      assertTrue( slider.isDisposed() );
    },
    
    testAppearances : function() {
      var slider = this._createSlider();
      assertEquals( "slider", slider.getAppearance() );
      // TODO [tb] : what do we need that subwidget anyway?
      assertEquals( "slider-line", slider._line.getAppearance() );
      assertEquals( "slider-thumb", slider._thumb.getAppearance() );
      assertEquals( "slider-max-button", slider._maxButton.getAppearance() );
      assertEquals( "slider-min-button", slider._minButton.getAppearance() );
      slider.destroy();
    },
    
    testBasicLayoutHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createSlider( true );
      var slider = testUtil.getElementLayout( widget.getElement() );
      var min = testUtil.getElementLayout( widget._minButton.getElement() );
      var max = testUtil.getElementLayout ( widget._maxButton.getElement() );
      var line = testUtil.getElementLayout( widget._line.getElement() );
      var thumb = testUtil.getElementLayout( widget._thumb.getElement() );
      assertEquals( [ 10, 10, 100, 20 ], slider );
      assertEquals( [ 0, 0, 16, 20 ], min );
      assertEquals( [ 84, 0, 16, 20 ], max );
      assertEquals( [ 16, 0, 68, 20 ], line );
      assertEquals( [ 16, 0, 7, 20 ], thumb );
      widget.destroy();
    },
    
    testBasicLayoutVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var widget = this._createSlider( false );
      var slider = testUtil.getElementLayout( widget.getElement() );
      var min = testUtil.getElementLayout( widget._minButton.getElement() );
      var max = testUtil.getElementLayout ( widget._maxButton.getElement() );
      var line = testUtil.getElementLayout( widget._line.getElement() );
      var thumb = testUtil.getElementLayout( widget._thumb.getElement() );
      assertEquals( [ 10, 10, 20, 100 ], slider );
      assertEquals( [ 0, 0, 20, 16 ], min );
      assertEquals( [ 0, 84, 20, 16 ], max );
      assertEquals( [ 0, 16, 20, 68 ], line );
      assertEquals( [ 0, 16, 20, 7 ], thumb );
      widget.destroy();
    },
    
    testThumbPositionNoScaling : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      // With minimum/maximum initially set to 0/100:
      slider.setHeight( 100 + 2 * 16 ); // to exclude the buttons
      assertEquals( 16 + 0 , thumb.getTop() );
      slider.setSelection( 30 );
      assertEquals( 16 + 30, thumb.getTop() );
      slider.destroy();
    },
    
    testThumbSizeNoScaling : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      slider.setHeight( 100 + 2 * 16 );
      assertEquals( 10 , thumb.getHeight() );
      slider.setThumb( 30 );
      assertEquals( 30, thumb.getHeight() );
      slider.destroy();
    },
    
    testThumbPositionWithScaling : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      slider.setHeight( 100 + 2 * 16 ); 
      slider.setSelection( 25 + 0 );
      slider.setMinimum( 25 );
      slider.setMaximum( 75 );
      assertEquals( 16 + 0, thumb.getTop() );
      slider.setSelection( 35 );
      assertEquals( 16 + 20, thumb.getTop() );
      slider.destroy();
    },
    
    testThumbSizeWithScaling : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      slider.setHeight( 100 + 2 * 16 ); // to exclude the buttons
      assertEquals( 10 , thumb.getHeight() );
      slider.setSelection( 25 + 0 );
      slider.setMinimum( 25 );
      slider.setMaximum( 75 );
      assertEquals( 20 , thumb.getHeight() );
      slider.setThumb( 30 );
      assertEquals( 60, thumb.getHeight() );
      slider.destroy();
    },
    
    testClickOnMaxButton : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      assertEquals( 0, slider._selection );
      testUtil.click( slider._maxButton );
      assertEquals( 5, slider._selection );
      assertFalse( slider._readyToSendChanges );
      slider.destroy();
    },
    
    testClickOnMinButton : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setSelection( 50 );
      assertEquals( 50, slider._selection );
      testUtil.click( slider._minButton );
      assertEquals( 45, slider._selection );
      assertFalse( slider._readyToSendChanges );
      slider.destroy();
    },
    
    testClickOnMinButtonLimit : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setSelection( 5 );
      assertEquals( 5, slider._selection );
      testUtil.click( slider._minButton );
      assertEquals( 0, slider._selection );
      assertFalse( slider._readyToSendChanges );
      slider._readyToSendChanges = true;
      testUtil.click( slider._minButton );
      assertEquals( 0, slider._selection );
      assertFalse( slider._readyToSendChanges );
      slider.destroy();
    },

    testClickOnMaxButtonLimit : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setThumb( 7 );
      slider.setSelection( 90 );
      testUtil.click( slider._maxButton );
      assertEquals( 93, slider._selection );
      assertFalse( slider._readyToSendChanges );
      slider._readyToSendChanges = true;
      testUtil.click( slider._maxButton );
      assertEquals( 93, slider._selection );
      assertFalse( slider._readyToSendChanges );
      slider.destroy();
    },
    
    testSendEvent : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setUserData( "id", "w99" );
      testUtil.click( slider._maxButton );
      assertFalse( slider._readyToSendChanges );
      testUtil.forceTimerOnce();
      assertTrue( slider._readyToSendChanges );
      assertEquals( 0, testUtil.getRequestsSend() );
      org.eclipse.swt.Request.getInstance().send();
      assertTrue( testUtil.getMessage().indexOf( "w99.selection=5" ) != -1 );
      testUtil.clearRequestLog();
      slider.setHasSelectionListener( true );
      testUtil.click( slider._maxButton );
      assertFalse( slider._readyToSendChanges );
      testUtil.forceTimerOnce();
      assertTrue( slider._readyToSendChanges );
      assertEquals( 1, testUtil.getRequestsSend() );
      assertTrue( testUtil.getMessage().indexOf( "widgetSelected=w99" ) != -1 );
      assertTrue( testUtil.getMessage().indexOf( "w99.selection=10" ) != -1 );
      slider.destroy();
    },
    
    testHoldMaxButton : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider._maxButton.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 5, slider._selection );
      testUtil.forceTimerOnce(); // start scrolling
      testUtil.forceInterval( slider._scrollTimer );
      assertEquals( 10, slider._selection );
      testUtil.forceInterval( slider._scrollTimer );
      assertEquals( 15, slider._selection );
      assertFalse( slider._readyToSendChanges );
      testUtil.forceTimerOnce(); // add request parameter
      assertTrue( slider._readyToSendChanges );
      testUtil.forceInterval( slider._scrollTimer );
      assertFalse( slider._readyToSendChanges );
      assertEquals( 20, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseup" );
      assertFalse( slider._scrollTimer.isEnabled() );
    },
    
    testHoldMaxButtonAbort : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider._maxButton.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 5, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseout" );
      testUtil.forceTimerOnce(); // try start scrolling
      assertFalse( slider._scrollTimer.isEnabled() );      
      assertEquals( 5, slider._selection );
    },

    /////////
    // Helper
    
    _createSlider : function( horizontal ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var style = horizontal === true ? "horizontal" : "vertical";
      var result = new org.eclipse.swt.widgets.Slider( style );
      result.addToDocument();
      result.setLeft( 10 );
      result.setTop( 10 );
      if( horizontal ) {
        result.setWidth( 100 );
        result.setHeight( 20 ); 
      } else {        
        result.setWidth( 20 );
        result.setHeight( 100 ); 
      }
      result.setIncrement( 5 );
      testUtil.flush();
      return result;
    }
    
  }
  
} );