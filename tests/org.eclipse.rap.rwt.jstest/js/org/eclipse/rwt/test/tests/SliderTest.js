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
    
    testHoldMinButton : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setMaximum( 110 ); // the place needed by thumb 
      slider.setSelection( 100 );
      assertEquals( 100, slider._selection );
      var node = slider._minButton.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 95, slider._selection );
      testUtil.forceTimerOnce(); // start scrolling
      testUtil.forceInterval( slider._scrollTimer );
      assertEquals( 90, slider._selection );
      testUtil.forceInterval( slider._scrollTimer );
      assertEquals( 85, slider._selection );
      assertFalse( slider._readyToSendChanges );
      testUtil.forceTimerOnce(); // add request parameter
      assertTrue( slider._readyToSendChanges );
      testUtil.forceInterval( slider._scrollTimer );
      assertFalse( slider._readyToSendChanges );
      assertEquals( 80, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseup" );
      assertFalse( slider._scrollTimer.isEnabled() );
      slider.destroy();
    },
    
    testHoldMinButtonAbort : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setMaximum( 110 ); // the place needed by thumb
      slider.setSelection( 100 );
      var node = slider._minButton.getElement();
      testUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 95, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseout" );
      testUtil.forceTimerOnce(); // try start scrolling
      assertFalse( slider._scrollTimer.isEnabled() );      
      assertEquals( 95, slider._selection );
      slider.destroy();
    },
    
    testClickLineVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      slider.setSelection( 2 );
      assertEquals( 2, slider._selection ); 
      var node = slider._line.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 16 + 50 );
      assertEquals( 12, slider._selection ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 16 + 1 );
      assertEquals( 2, slider._selection ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 16 + 1 );
      assertEquals( 0, slider._selection );
      slider.setSelection( 85 ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 84 - 1 );
      assertEquals( 90, slider._selection );
      slider.destroy();
    },
    
    testClickLineHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( true );
      slider.setSelection( 2 );
      assertEquals( 2, slider._selection ); 
      var node = slider._line.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16 + 50, 11 );
      assertEquals( 12, slider._selection ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16 + 1, 11 );
      assertEquals( 2, slider._selection ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16 + 1, 11 );
      assertEquals( 0, slider._selection );
      slider.setSelection( 85 ); 
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 84 - 1, 11 );
      assertEquals( 90, slider._selection );
      slider.destroy();
    },

    testHoldOnLine : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider._line.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      // scale is 100 - 32 = 68 : 100 => thumb is 6.8
      // last page-increment occured when distance between thumb middle 
      // and mouse is smaller than one increment (also 6.8).
      // Thumb-middle on selection 50 is: 10 + 16 + 50 * 0.68 + 6.8 / 2 = 63.4
      // The mouse will be moved there after the scrolling started:
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 50 );
      assertEquals( 10, slider._selection );
      testUtil.forceTimerOnce(); // start scrolling
      testUtil.forceInterval( slider._scrollTimer );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 11, 63 );
      testUtil.forceInterval( slider._scrollTimer );
      testUtil.forceInterval( slider._scrollTimer );
      assertEquals( 40, slider._selection );
      testUtil.forceInterval( slider._scrollTimer );
      assertEquals( 50, slider._selection );
      assertFalse( slider._scrollTimer.isEnabled() );
      // NOTE: this should work, but doesnt:
      //testUtil.fakeMouseEventDOM( node, "mousemove", left, 11, 64 );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 63 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 64 );
      assertEquals( 60, slider._selection );
      slider.destroy();
    },

    testHoldOnLineAbort : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider._line.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 50 );
      assertEquals( 10, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 50 );
      testUtil.forceTimerOnce(); // start scrolling
      assertFalse( slider._scrollTimer.isEnabled() );
      slider.destroy();
    },
        
    testHoldOnLineMouseOut : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      var node = slider._line.getElement();
      var left = qx.event.type.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 11, 90 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 90 );
      assertEquals( 10, slider._selection );
      testUtil.forceTimerOnce(); // start scrolling
      testUtil.forceInterval( slider._scrollTimer );
      assertEquals( 20, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseout", left, 9, 90 );
      assertFalse( slider._scrollTimer.isEnabled() );
      // NOTE: It should actually continue on mouseover, but doesnt right now:
      //testUtil.fakeMouseEventDOM( node, "mouseover", left, 11 90 );
      slider.destroy();
    },
        
    testKeyControlVertical : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( false );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "Right" );
      assertEquals( 5, slider._selection );
      testUtil.press( slider, "Down" );
      assertEquals( 10, slider._selection );
      testUtil.press( slider, "Left" );
      assertEquals( 5, slider._selection );
      testUtil.press( slider, "Up" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "PageDown" );
      assertEquals( 10, slider._selection );
      testUtil.press( slider, "PageUp" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "End" );
      assertEquals( 90, slider._selection );
      testUtil.press( slider, "Right" );
      assertEquals( 90, slider._selection );      
      testUtil.press( slider, "Home" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "Left" );
      assertEquals( 0, slider._selection );      
      slider.destroy();
    },
        
    testKeyControlHorizontal : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( true );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "Right" );
      assertEquals( 5, slider._selection );
      testUtil.press( slider, "Up" );
      assertEquals( 10, slider._selection );
      testUtil.press( slider, "Left" );
      assertEquals( 5, slider._selection );
      testUtil.press( slider, "Down" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "PageUp" );
      assertEquals( 10, slider._selection );
      testUtil.press( slider, "PageDown" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "End" );
      assertEquals( 90, slider._selection );
      testUtil.press( slider, "Right" );
      assertEquals( 90, slider._selection );      
      testUtil.press( slider, "Home" );
      assertEquals( 0, slider._selection );
      testUtil.press( slider, "Left" );
      assertEquals( 0, slider._selection );      
      slider.destroy();
    },

    testMouseWheel : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( true );
      assertEquals( 0, slider._selection );
      testUtil.fakeWheel( slider, -1 );
      assertEquals( 0, slider._selection );
      testUtil.fakeWheel( slider, 1 );
      assertEquals( 0, slider._selection );
      testUtil.fakeWheel( slider, 1 );
      assertEquals( 0, slider._selection );
      slider.destroy();
    },
  
    testDragThumb : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var left = qx.event.type.MouseEvent.buttons.left;
      var slider = this._createSlider( true );
      var node = slider._thumb.getElement();
      assertEquals( 0, slider._selection );
      assertEquals( 16, testUtil.getElementBounds( node ).left );
      // Note: 10 pixel = 14.7 units
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 10, 11 );
      assertEquals( 16 + 10, testUtil.getElementBounds( node ).left );
      assertEquals( 15, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 5, 11 );
      assertEquals( 16 + 5, testUtil.getElementBounds( node ).left );
      assertEquals( 7, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 10 + 16 + 5, 11 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 10, 11 );      
      assertEquals( 16 + 5, testUtil.getElementBounds( node ).left );
      assertEquals( 7, slider._selection );
      slider.destroy();
    },
  
    testDragThumbMouseOut : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var left = qx.event.type.MouseEvent.buttons.left;
      var slider = this._createSlider( true );
      var node = slider._thumb.getElement();
      assertEquals( 0, slider._selection );
      assertEquals( 16, testUtil.getElementBounds( node ).left );
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mouseout", left, 10 + 16, 9 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16, 9 );
      assertEquals( 16, testUtil.getElementBounds( node ).left );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 5, 9 );
      assertEquals( 16 + 5, testUtil.getElementBounds( node ).left );
      testUtil.fakeMouseEventDOM( node, "mouseup", left, 10 + 16 + 5, 9 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 10 );      
      assertEquals( 16 + 5, testUtil.getElementBounds( node ).left );
      slider.destroy();
    },
  
    testDragThumbLimit : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var left = qx.event.type.MouseEvent.buttons.left;
      var slider = this._createSlider( true );
      var node = slider._thumb.getElement();
      assertEquals( 0, slider._selection );
      assertEquals( 16, testUtil.getElementBounds( node ).left );
      testUtil.fakeMouseEventDOM( node, "mouseover", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16, 11 );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 - 10, 11 );
      assertEquals( 16, testUtil.getElementBounds( node ).left );
      assertEquals( 0, slider._selection );
      testUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 120, 11 );
      assertEquals( 77, testUtil.getElementBounds( node ).left );
      assertEquals( 90, slider._selection );
      slider.destroy();
    },

    testDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var slider = this._createSlider( true );
      var thumb = slider._thumb;
      var line = slider._line;
      var minButton = slider._minButton;
      var maxButton = slider._maxButton;
      var timer = slider._scrollTimer;
      var parent = slider.getElement().parentNode;
      var parentLength = parent.childNodes.length;
      slider.destroy();
      testUtil.flush();
      assertNull( slider._thumb );
      assertNull( slider._line );
      assertNull( slider._minButton );
      assertNull( slider._maxButton );
      assertNull( slider._scrollTimer );
      assertNull( slider.getElement() );
      assertNull( slider.__listeners );
      assertTrue( parent.childNodes.length === parentLength - 1 );
      assertTrue( line.isDisposed() );
      assertTrue( minButton.isDisposed() );
      assertTrue( maxButton.isDisposed() );
      assertTrue( timer.isDisposed() );
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