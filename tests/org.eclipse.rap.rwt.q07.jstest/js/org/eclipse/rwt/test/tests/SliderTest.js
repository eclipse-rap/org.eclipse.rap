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
      testUtil.flush();
      return result;
    }
    
  }
  
} );