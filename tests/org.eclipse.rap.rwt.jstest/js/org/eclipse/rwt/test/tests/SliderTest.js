/*******************************************************************************
 * Copyright (c) 2010, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectManager = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.SliderTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateSliderByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Slider );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "slider", widget.getAppearance() );
      assertFalse( widget._horizontal );
      shell.destroy();
      widget.destroy();
    },

    testCreateSliderHorizontalByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Slider );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "slider", widget.getAppearance() );
      assertTrue( widget._horizontal );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "minimum" : 50
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 50, widget._minimum );
      shell.destroy();
      widget.destroy();
    },

    testSetMaximumByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "maximum" : 150
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 150, widget._maximum );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "selection" : 50
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 50, widget._selection );
      shell.destroy();
      widget.destroy();
    },

    testSetIncrementByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "increment" : 5
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 5, widget._increment );
      shell.destroy();
      widget.destroy();
    },

    testSetPageIncrementByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "pageIncrement" : 20
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 20, widget._pageIncrement );
      shell.destroy();
      widget.destroy();
    },

    testSetThumbByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "thumb" : 20
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 20, widget._thumbLength );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Slider",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectManager.getObject( "w3" );

      TestUtil.protocolListen( "w3", { "Selection" : true } );

      var remoteObject = rwt.remote.Connection.getInstance().getRemoteObject( widget );
      assertTrue( remoteObject.isListening( "Selection" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreateDispose : function() {
      var slider = this._createSlider();
      assertTrue( slider.isSeeable() );
      slider.destroy();
      TestUtil.flush();
      assertTrue( slider.isDisposed() );
    },

    testAppearances : function() {
      var slider = this._createSlider();
      assertEquals( "slider", slider.getAppearance() );
      // TODO [tb] : what do we need that subwidget anyway?
      assertEquals( "slider-thumb", slider._thumb.getAppearance() );
      assertEquals( "slider-max-button", slider._maxButton.getAppearance() );
      assertEquals( "slider-min-button", slider._minButton.getAppearance() );
      slider.destroy();
    },

    testStatesHorizontal : function() {
      var widget = this._createSlider( true );
      assertTrue( widget.hasState( "rwt_HORIZONTAL" ) );
      assertTrue( widget._minButton.hasState( "horizontal" ) );
      assertTrue( widget._maxButton.hasState( "horizontal" ) );
      assertTrue( widget._minButton.hasState( "rwt_HORIZONTAL" ) );
      assertTrue( widget._maxButton.hasState( "rwt_HORIZONTAL" ) );
      assertTrue( widget._thumb.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget.hasState( "rwt_VERTICAL" ) );
      assertFalse( widget.hasState( "vertical" ) );
      assertFalse( widget._minButton.hasState( "vertical" ) );
      assertFalse( widget._maxButton.hasState( "vertical" ) );
      assertFalse( widget._minButton.hasState( "rwt_VERTICAL" ) );
      assertFalse( widget._maxButton.hasState( "rwt_VERTICAL" ) );
      assertFalse( widget._thumb.hasState( "rwt_VERTICAL" ) );
      widget.destroy();
    },

    testStatesVertical : function() {
      var widget = this._createSlider( false );
      assertTrue( widget.hasState( "rwt_VERTICAL" ) );
      assertTrue( widget._minButton.hasState( "vertical" ) );
      assertTrue( widget._maxButton.hasState( "vertical" ) );
      assertTrue( widget._minButton.hasState( "rwt_VERTICAL" ) );
      assertTrue( widget._maxButton.hasState( "rwt_VERTICAL" ) );
      assertTrue( widget._thumb.hasState( "rwt_VERTICAL" ) );
      assertFalse( widget.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget._minButton.hasState( "horizontal" ) );
      assertFalse( widget._maxButton.hasState( "horizontal" ) );
      assertFalse( widget._minButton.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget._maxButton.hasState( "rwt_HORIZONTAL" ) );
      assertFalse( widget._thumb.hasState( "rwt_HORIZONTAL" ) );
      widget.destroy();
    },

    testBasicLayoutVertical : function() {
      var widget = this._createSlider( false );
      var slider = TestUtil.getElementLayout( widget.getElement() );
      var min = TestUtil.getElementLayout( widget._minButton.getElement() );
      var max = TestUtil.getElementLayout ( widget._maxButton.getElement() );
      var thumb = TestUtil.getElementLayout( widget._thumb.getElement() );
      assertEquals( [ 10, 10, 20, 100 ], slider );
      assertEquals( [ 0, 0, 20, 16 ], min );
      assertEquals( [ 0, 84, 20, 16 ], max );
      assertEquals( [ 0, 16, 20, 7 ], thumb );
      widget.destroy();
    },

    testBasicLayoutHorizontal : function() {
      var widget = this._createSlider( true );

      var slider = TestUtil.getElementLayout( widget.getElement() );
      var min = TestUtil.getElementLayout( widget._minButton.getElement() );
      var max = TestUtil.getElementLayout ( widget._maxButton.getElement() );
      var thumb = TestUtil.getElementLayout( widget._thumb.getElement() );
      assertEquals( [ 10, 10, 100, 20 ], slider );
      assertEquals( [ 0, 0, 16, 20 ], min );
      assertEquals( [ 84, 0, 16, 20 ], max );
      assertEquals( [ 16, 0, 7, 20 ], thumb );
      widget.destroy();
    },

    testBasicLayoutHorizontal_RTL : function() {
      var widget = this._createSlider( true );

      widget.setDirection( "rtl" );
      TestUtil.flush();

      var slider = TestUtil.getElementLayout( widget.getElement() );
      var min = TestUtil.getElementLayout( widget._minButton.getElement() );
      var max = TestUtil.getElementLayout ( widget._maxButton.getElement() );
      var thumb = TestUtil.getElementLayout( widget._thumb.getElement() );
      assertEquals( [ 10, 10, 100, 20 ], slider );
      assertEquals( [ 84, 0, 16, 20 ], min );
      assertEquals( [ 0, 0, 16, 20 ], max );
      assertEquals( [ 77, 0, 7, 20 ], thumb );
      widget.destroy();
    },

    testGetToolTipTargetBoundsVertical : function() {
      var widget = this._createSlider( false );
      widget.setBorder( new rwt.html.Border( 1 ) );

      var bounds = widget.getToolTipTargetBounds();

      assertEquals( 1, bounds.left );
      assertEquals( 17, bounds.top );
      assertEquals( 20, bounds.width );
      assertEquals( 7, bounds.height );
      widget.destroy();
    },

    testGetToolTipTargetBoundsHorizontal : function() {
      var widget = this._createSlider( true );
      widget.setBorder( new rwt.html.Border( 1 ) );

      var bounds = widget.getToolTipTargetBounds();

      assertEquals( 17, bounds.left );
      assertEquals( 1, bounds.top );
      assertEquals( 7, bounds.width );
      assertEquals( 20, bounds.height );
      widget.destroy();
    },

    testFireToolTipUpdate : function() {
      var widget = this._createSlider( true );
      var fired = false;
      widget.addEventListener( "updateToolTip", function( arg ) {
        fired = arg;
      } );

      widget.setSelection( 30 );

      assertIdentical( widget, fired );
      widget.destroy();
    },

    testThumbPositionNoScaling : function() {
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
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      slider.setHeight( 100 + 2 * 16 );
      assertEquals( 10 , thumb.getHeight() );
      slider.setThumb( 30 );
      assertEquals( 30, thumb.getHeight() );
      slider.destroy();
    },

    testThumbPositionWithScaling : function() {
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      slider.setHeight( 100 + 2 * 16 );
      slider.setSelection( 25 );
      slider.setMinimum( 25 );
      slider.setMaximum( 75 );
      assertEquals( 16, thumb.getTop() );
      slider.setSelection( 35 );
      assertEquals( 16 + 20, thumb.getTop() );
      slider.destroy();
    },

    testThumbSizeWithScaling : function() {
      var slider = this._createSlider( false );
      var thumb = slider._thumb;
      slider.setHeight( 100 + 2 * 16 ); // to exclude the buttons
      assertEquals( 10 , thumb.getHeight() );
      slider.setSelection( 25 );
      slider.setMinimum( 25 );
      slider.setMaximum( 75 );
      assertEquals( 20 , thumb.getHeight() );
      slider.setThumb( 30 );
      assertEquals( 60, thumb.getHeight() );
      slider.destroy();
    },

    testClickOnMaxButton : function() {
      var slider = this._createSlider( false );
      assertEquals( 0, slider._selection );
      TestUtil.click( slider._maxButton );
      assertEquals( 5, slider._selection );
      assertTrue( slider._requestScheduled );
      slider.destroy();
    },

    testClickOnMinButton : function() {
      var slider = this._createSlider( false );
      slider.setSelection( 50 );
      assertEquals( 50, slider._selection );
      TestUtil.click( slider._minButton );
      assertEquals( 45, slider._selection );
      assertTrue( slider._requestScheduled );
      slider.destroy();
    },

    testClickOnMinButtonLimit : function() {
      var slider = this._createSlider( false );
      slider.setSelection( 5 );
      assertEquals( 5, slider._selection );
      TestUtil.click( slider._minButton );
      assertEquals( 0, slider._selection );
      assertTrue( slider._requestScheduled );
      slider._requestScheduled = false;
      TestUtil.click( slider._minButton );
      assertEquals( 0, slider._selection );
      assertTrue( slider._requestScheduled );
      slider.destroy();
    },

    testClickOnMaxButtonLimit : function() {
      var slider = this._createSlider( false );
      slider.setThumb( 7 );
      slider.setSelection( 90 );
      TestUtil.click( slider._maxButton );
      assertEquals( 93, slider._selection );
      assertTrue( slider._requestScheduled );
      slider._requestScheduled = false;
      TestUtil.click( slider._maxButton );
      assertEquals( 93, slider._selection );
      assertTrue( slider._requestScheduled );
      slider.destroy();
    },

    testSendEvent : function() {
      var slider = this._createSlider( false );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Slider" );
      ObjectManager.add( "w99", slider, handler );
      TestUtil.click( slider._maxButton );
      assertTrue( slider._requestScheduled );
      TestUtil.forceTimerOnce();
      assertFalse( slider._requestScheduled );
      assertEquals( 0, TestUtil.getRequestsSend() );
      rwt.remote.Connection.getInstance().send();
      assertEquals( 5, TestUtil.getMessageObject().findSetProperty( "w99", "selection" ) );
      TestUtil.clearRequestLog();
      TestUtil.fakeListener( slider, "Selection", true );
      TestUtil.click( slider._maxButton );
      assertTrue( slider._requestScheduled );
      TestUtil.forceTimerOnce();
      assertFalse( slider._requestScheduled );
      assertEquals( 1, TestUtil.getRequestsSend() );
      assertEquals( 10, TestUtil.getMessageObject().findSetProperty( "w99", "selection" ) );
      assertNotNull( TestUtil.getMessageObject().findNotifyOperation( "w99", "Selection" ) );
      slider.destroy();
    },

    testHoldMaxButton : function() {
      var slider = this._createSlider( false );
      var node = slider._maxButton.getElement();
      TestUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 5, slider._selection );
      TestUtil.forceInterval( slider._delayTimer ); // start scrolling
      TestUtil.forceInterval( slider._repeatTimer );
      assertEquals( 10, slider._selection );
      TestUtil.forceInterval( slider._repeatTimer );
      assertEquals( 15, slider._selection );
      assertTrue( slider._requestScheduled );
      TestUtil.forceTimerOnce(); // add request parameter
      assertFalse( slider._requestScheduled );
      TestUtil.forceInterval( slider._repeatTimer );
      assertTrue( slider._requestScheduled );
      assertEquals( 20, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseup" );
      assertFalse( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testHoldMaxButtonAbort : function() {
      var slider = this._createSlider( false );
      var node = slider._maxButton.getElement();
      TestUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 5, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseout" );
      try {
        TestUtil.forceInterval( slider._delayTimer ); // try start scrolling
      } catch( ex ) {
        // expected
      }
      assertFalse( slider._repeatTimer.isEnabled() );
      assertEquals( 5, slider._selection );
      slider.destroy();
    },

    testHoldMinButton : function() {
      var slider = this._createSlider( false );
      slider.setMaximum( 110 ); // the place needed by thumb
      slider.setSelection( 100 );
      assertEquals( 100, slider._selection );
      var node = slider._minButton.getElement();
      TestUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 95, slider._selection );
      TestUtil.forceInterval( slider._delayTimer ); // start scrolling
      TestUtil.forceInterval( slider._repeatTimer );
      assertEquals( 90, slider._selection );
      TestUtil.forceInterval( slider._repeatTimer );
      assertEquals( 85, slider._selection );
      assertTrue( slider._requestScheduled );
      TestUtil.forceTimerOnce(); // add request parameter
      assertFalse( slider._requestScheduled );
      TestUtil.forceInterval( slider._repeatTimer );
      assertTrue( slider._requestScheduled );
      assertEquals( 80, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseup" );
      assertFalse( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testHoldMinButtonAbort : function() {
      var slider = this._createSlider( false );
      slider.setMaximum( 110 ); // the place needed by thumb
      slider.setSelection( 100 );
      var node = slider._minButton.getElement();
      TestUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 95, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseout" );
      try {
        TestUtil.forceInterval( slider._delayTimer ); // start scrolling
      } catch( ex ) {
        // expected
      }
      assertFalse( slider._repeatTimer.isEnabled() );
      assertEquals( 95, slider._selection );
      slider.destroy();
    },

    testHoldMinButtonContinue : function() {
      var slider = this._createSlider( false );
      slider.setMaximum( 110 ); // the place needed by thumb
      slider.setSelection( 100 );
      var node = slider._minButton.getElement();
      TestUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 95, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseout" );
      TestUtil.fakeMouseEventDOM( document.body, "mouseover" );
      TestUtil.fakeMouseEventDOM( document.body, "mouseout" );
      TestUtil.fakeMouseEventDOM( node, "mouseover" );
      TestUtil.forceInterval( slider._delayTimer ); // start scrolling
      assertTrue( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testHoldMinButtonDontContinue : function() {
      var slider = this._createSlider( false );
      slider.setMaximum( 110 ); // the place needed by thumb
      slider.setSelection( 100 );
      var node = slider._minButton.getElement();
      TestUtil.fakeMouseEventDOM( node, "mousedown" );
      assertEquals( 95, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseout" );
      TestUtil.fakeMouseEventDOM( document.body, "mouseover" );
      TestUtil.fakeMouseEventDOM( document.body, "mouseout" );
      TestUtil.fakeMouseEventDOM( node.parentNode, "mouseover" );
      try {
        TestUtil.forceInterval( slider._delayTimer ); // start scrolling
      } catch( ex ) {
        // expected
      }
      assertFalse( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    // NOTE: This test may fail if there the document is too small, like on an a tablet.
    // Changing the orientation can help.
    testClickLineVertical : function() {
      var slider = this._createSlider( false );
      slider.setSelection( 2 );
      assertEquals( 2, slider._selection );
      var node = slider.getElement();
      var left = rwt.event.MouseEvent.buttons.left;
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 16 + 50 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 10 + 16 + 50 );
      assertEquals( 12, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 16 + 1 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 10 + 16 + 1 );
      assertEquals( 2, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 16 + 1 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 10 + 16 + 1 );
      assertEquals( 0, slider._selection );
      slider.setSelection( 85 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 10 + 84 - 1 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 10 + 84 - 1 );
      assertEquals( 90, slider._selection );
      slider.destroy();
    },

    testClickLineHorizontal : function() {
      var slider = this._createSlider( true );
      slider.setSelection( 2 );
      var node = slider.getElement();
      var button = rwt.event.MouseEvent.buttons.left;
      var minPageX = 10 + 16;
      var positions = [];

      TestUtil.fakeMouseEventDOM( node, "mousedown", button, minPageX + 50, 11 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", button, minPageX + 50, 11 );
      positions.push( slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mousedown", button, minPageX + 1, 11 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", button, minPageX + 1, 11 );
      positions.push( slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mousedown", button, minPageX + 1, 11 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", button, minPageX + 1, 11 );
      positions.push( slider._selection );

      assertEquals( [ 12, 2, 0 ], positions );
      slider.destroy();
    },

    testClickLineHorizontal_RTL : function() {
      var slider = this._createSlider( true );
      slider.setDirection( "rtl" );
      slider.setSelection( 2 );
      var node = slider.getElement();
      var button = rwt.event.MouseEvent.buttons.left;
      var minPageX = 10 + 100 - 16;
      var positions = [];

      TestUtil.fakeMouseEventDOM( node, "mousedown", button, minPageX - 50, 11 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", button, minPageX - 50, 11 );
      positions.push( slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mousedown", button, minPageX - 1, 11 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", button, minPageX - 1, 11 );
      positions.push( slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mousedown", button, minPageX - 1, 11 );
      TestUtil.fakeMouseEventDOM( node, "mouseup", button, minPageX - 1, 11 );
      positions.push( slider._selection );

      assertEquals( [ 12, 2, 0 ], positions );
      slider.destroy();
    },

    testHoldOnLine : function() {
      var slider = this._createSlider( false );
      var node = slider.getElement();
      var left = rwt.event.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      // scale is 100 - 32 = 68 : 100 => thumb is 6.8
      // last page-increment occured when distance between thumb middle
      // and mouse is smaller than one increment (also 6.8).
      // Thumb-middle on selection 50 is: 10 + 16 + 50 * 0.68 + 6.8 / 2 = 63.4
      // The mouse will be moved there after the scrolling started:
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 50 );
      assertEquals( 10, slider._selection );
      TestUtil.forceInterval( slider._delayTimer ); // start scrolling
      TestUtil.forceInterval( slider._repeatTimer );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, 11, 63 );
      TestUtil.forceInterval( slider._repeatTimer );
      TestUtil.forceInterval( slider._repeatTimer );
      assertEquals( 40, slider._selection );
      TestUtil.forceInterval( slider._repeatTimer );
      assertEquals( 50, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, 11, 64 );
      TestUtil.forceInterval( slider._repeatTimer );
      assertEquals( 60, slider._selection );
      // direction change not allowed:
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, 11, 30 );
      TestUtil.forceInterval( slider._repeatTimer );
      assertEquals( 60, slider._selection );
      slider.destroy();
    },

    testHoldOnLineAbort : function() {
      var slider = this._createSlider( false );
      var node = slider.getElement();
      var left = rwt.event.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 50 );
      assertEquals( 10, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseup", left, 11, 50 );
      try {
        TestUtil.forceInterval( slider._delayTimer ); // start scrolling
      } catch( ex ) {
        // expected
      }
      assertFalse( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testHoldOnLineMouseUpOnThumb : function() {
      var slider = this._createSlider( false );
      var node = slider.getElement();
      var left = rwt.event.MouseEvent.buttons.left;
      var thumb = slider._thumb.getElement();
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 50 );
      assertEquals( 10, slider._selection );
      TestUtil.fakeMouseEventDOM( thumb, "mouseup", left, 11, 50 );
      try {
        TestUtil.forceInterval( slider._delayTimer ); // start scrolling
      } catch( ex ) {
        // expected
      }
      assertFalse( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testHoldOnLineMouseOut : function() {
      var slider = this._createSlider( false );
      var node = slider.getElement();
      var left = rwt.event.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      TestUtil.fakeMouseEventDOM( node, "mouseover", left, 11, 90 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 90 );
      assertEquals( 10, slider._selection );
      TestUtil.forceInterval( slider._delayTimer ); // start scrolling
      TestUtil.forceInterval( slider._repeatTimer );
      assertEquals( 20, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseout", left, 9, 90 );
      TestUtil.fakeMouseEventDOM( document.body, "mouseover", left, 9, 90 );
      assertFalse( slider._repeatTimer.isEnabled() );
      TestUtil.fakeMouseEventDOM( node, "mouseover", left, 11, 90 );
      assertTrue( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testHoldOnLineMouseOutAbort : function() {
      var slider = this._createSlider( false );
      var node = slider.getElement();
      var left = rwt.event.MouseEvent.buttons.left;
      var thumb = slider._thumb.getHeight();
      TestUtil.fakeMouseEventDOM( node, "mouseover", left, 11, 90 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 11, 90 );
      assertEquals( 10, slider._selection );
      TestUtil.forceInterval( slider._delayTimer ); // start scrolling
      TestUtil.forceInterval( slider._repeatTimer );
      assertEquals( 20, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseout", left, 9, 90 );
      assertFalse( slider._repeatTimer.isEnabled() );
      TestUtil.fakeMouseEventDOM( document.body, "mouseup", left, 0, 0 );
      TestUtil.fakeMouseEventDOM( node, "mouseover", left, 11, 90 );
      assertFalse( slider._repeatTimer.isEnabled() );
      slider.destroy();
    },

    testKeyControlVertical : function() {
      var slider = this._createSlider( false );
      assertEquals( 0, slider._selection );
      TestUtil.press( slider, "Right" );
      assertEquals( 5, slider._selection );
      TestUtil.press( slider, "Down" );
      assertEquals( 10, slider._selection );
      TestUtil.press( slider, "Left" );
      assertEquals( 5, slider._selection );
      TestUtil.press( slider, "Up" );
      assertEquals( 0, slider._selection );
      TestUtil.press( slider, "PageDown" );
      assertEquals( 10, slider._selection );
      TestUtil.press( slider, "PageUp" );
      assertEquals( 0, slider._selection );
      TestUtil.press( slider, "End" );
      assertEquals( 90, slider._selection );
      TestUtil.press( slider, "Right" );
      assertEquals( 90, slider._selection );
      TestUtil.press( slider, "Home" );
      assertEquals( 0, slider._selection );
      TestUtil.press( slider, "Left" );
      assertEquals( 0, slider._selection );
      slider.destroy();
    },

    testKeyControlHorizontal : function() {
      var slider = this._createSlider( true );
      assertEquals( 0, slider._selection );
      TestUtil.press( slider, "Right" );
      assertEquals( 5, slider._selection );
      TestUtil.press( slider, "Up" );
      assertEquals( 10, slider._selection );
      TestUtil.press( slider, "Left" );
      assertEquals( 5, slider._selection );
      TestUtil.press( slider, "Down" );
      assertEquals( 0, slider._selection );
      TestUtil.press( slider, "PageUp" );
      assertEquals( 10, slider._selection );
      TestUtil.press( slider, "PageDown" );
      assertEquals( 0, slider._selection );
      TestUtil.press( slider, "End" );
      assertEquals( 90, slider._selection );
      TestUtil.press( slider, "Right" );
      assertEquals( 90, slider._selection );
      TestUtil.press( slider, "Home" );
      assertEquals( 0, slider._selection );
      TestUtil.press( slider, "Left" );
      assertEquals( 0, slider._selection );
      slider.destroy();
    },

    testMouseWheel : function() {
      var slider = this._createSlider( true );
      assertEquals( 0, slider._selection );
      TestUtil.fakeWheel( slider, 1 );
      assertEquals( 0, slider._selection );
      TestUtil.fakeWheel( slider, -1 );
      assertEquals( 0, slider._selection );
      TestUtil.fakeWheel( slider, -1 );
      assertEquals( 0, slider._selection );
      slider.focus();
      TestUtil.fakeWheel( slider, 1 );
      assertEquals( 0, slider._selection );
      TestUtil.fakeWheel( slider, -1 );
      assertEquals( 5, slider._selection );
      TestUtil.fakeWheel( slider, -1 );
      assertEquals( 10, slider._selection );
      slider.destroy();
    },

    testDragThumb : function() {
      var left = rwt.event.MouseEvent.buttons.left;
      var slider = this._createSlider( true );
      var node = slider._thumb.getElement();
      var selections = [];
      var minPageX = 10 + 16;

      TestUtil.fakeMouseEventDOM( node, "mouseover", left, minPageX, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, minPageX, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, minPageX + 10, 11 );
      selections.push( slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, minPageX + 5, 11 );
      selections.push( slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseup", left, minPageX + 5, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, minPageX + 10, 11 );
      selections.push( slider._selection );

      assertEquals( [ 15, 7, 7 ], selections );
      slider.destroy();
    },

    testDragThumb_RTL : function() {
      var left = rwt.event.MouseEvent.buttons.left;
      var slider = this._createSlider( true );
      slider.setDirection( "rtl" );
      var node = slider._thumb.getElement();
      var selections = [];
      var minPageX = 10 + 100 - 16;

      TestUtil.fakeMouseEventDOM( node, "mouseover", left, minPageX, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, minPageX, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, minPageX - 10, 11 );
      selections.push( slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, minPageX - 5, 11 );
      selections.push( slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mouseup", left, minPageX - 5, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, minPageX - 10, 11 );
      selections.push( slider._selection );

      assertEquals( [ 15, 7, 7 ], selections );
      slider.destroy();
    },

    testDragThumbMouseOut : function() {
      var left = rwt.event.MouseEvent.buttons.left;
      var slider = this._createSlider( true );
      var node = slider._thumb.getElement();
      assertEquals( 0, slider._selection );
      assertEquals( 16, TestUtil.getElementBounds( node ).left );
      TestUtil.fakeMouseEventDOM( node, "mouseover", left, 10 + 16, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16, 11 );
      TestUtil.fakeMouseEventDOM( node, "mouseout", left, 10 + 16, 9 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16, 9 );
      assertEquals( 16, TestUtil.getElementBounds( node ).left );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 5, 9 );
      assertEquals( 16 + 5, TestUtil.getElementBounds( node ).left );
      TestUtil.fakeMouseEventDOM( node, "mouseup", left, 10 + 16 + 5, 9 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 10 );
      assertEquals( 16 + 5, TestUtil.getElementBounds( node ).left );
      slider.destroy();
    },

    testDragThumbLimit : function() {
      var left = rwt.event.MouseEvent.buttons.left;
      var slider = this._createSlider( true );
      var node = slider._thumb.getElement();
      assertEquals( 0, slider._selection );
      assertEquals( 16, TestUtil.getElementBounds( node ).left );
      TestUtil.fakeMouseEventDOM( node, "mouseover", left, 10 + 16, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousedown", left, 10 + 16, 11 );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 - 10, 11 );
      assertEquals( 16, TestUtil.getElementBounds( node ).left );
      assertEquals( 0, slider._selection );
      TestUtil.fakeMouseEventDOM( node, "mousemove", left, 10 + 16 + 120, 11 );
      assertEquals( 77, TestUtil.getElementBounds( node ).left );
      assertEquals( 90, slider._selection );
      slider.destroy();
    },

    testDispose : function() {
      var slider = this._createSlider( true );
      var thumb = slider._thumb;
      var minButton = slider._minButton;
      var maxButton = slider._maxButton;
      var timer = slider._repeatTimer;
      var parent = slider.getElement().parentNode;
      var parentLength = parent.childNodes.length;
      slider.destroy();
      TestUtil.flush();
      assertNull( slider._thumb );
      assertNull( slider._minButton );
      assertNull( slider._maxButton );
      assertNull( slider._repeatTimer );
      assertNull( slider.getElement() );
      assertNull( slider.__listeners );
      assertTrue( parent.childNodes.length === parentLength - 1 );
      assertTrue( minButton.isDisposed() );
      assertTrue( maxButton.isDisposed() );
      assertTrue( timer.isDisposed() );
    },

    testDirectionAddsStateToSubWidgets : function() {
      var slider = this._createSlider( true );

      slider.setDirection( "rtl" );

      assertTrue( slider._minButton.hasState( "rwt_RIGHT_TO_LEFT" ) );
      assertTrue( slider._maxButton.hasState( "rwt_RIGHT_TO_LEFT" ) );
      slider.destroy();
    },

    /////////
    // Helper

    _createSlider : function( horizontal ) {
      var result = new rwt.widgets.Slider( horizontal );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Slider" );
      ObjectManager.add( "w3", result, handler );
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
      TestUtil.flush();
      return result;
    }

  }

} );

}() );
