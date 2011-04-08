/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ProgressBarTest", {
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this._gfxBorder = new org.eclipse.rwt.RoundedBorder( 2, "black", [ 7, 7, 7, 7 ] );
    this._gfxBorder2 = new org.eclipse.rwt.RoundedBorder( 2, "black", [ 7, 7, 7, 7 ] );
    this._gfxBorder2.setRadii( [ 0, 4, 6, 8 ] );
    this._cssBorder = new qx.ui.core.Border( 2, "outset" );
    this._gradient = [ [ 0, "red" ], [ 1, "yellow" ] ];
  },
  
  members : {
        
    testCreateSimpleBar : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      assertFalse( bar._isVertical() );
      assertFalse( bar._isUndetermined() );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( null );
      bar.setSelection( 50 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertNotNull( bar._getTargetNode() );
      assertNotNull( bar._canavs );
      assertTrue( bar._gfxCanvasAppended );
      assertNotNull( bar._backgroundShape );
      assertNotNull( bar._indicatorShape );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertNull( bar._borderShape );
      assertFalse( bar._useBorderShape );
      assertEquals( 0, bar._gfxBorderWidth );
      assertEquals( 100, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 0, 100 ) );
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;       
      assertFalse( testUtil.hasCssBorder( bar.getElement() ) ) ;       
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
        
    testComplexBorder : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      assertFalse( bar._isVertical() );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( new qx.ui.core.Border( 2, "inset" ) );
      bar.setSelection( 50 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertNotNull( bar._getTargetNode() );
      assertNotNull( bar._canavs );
      assertTrue( bar._gfxCanvasAppended );
      assertNotNull( bar._backgroundShape );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertNotNull( bar._indicatorShape );
      assertNull( bar._borderShape );
      assertFalse( bar._useBorderShape );
      assertEquals( 0, bar._gfxBorderWidth );
      assertEquals( 98, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 0, 100 ) );
      var edge = bar.getElement().style.borderLeftStyle;
      assertFalse( edge == "" || edge == "none"  );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
       
    testRoundedBorder : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 50 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertNotNull( bar._getTargetNode() );
      assertNotNull( bar._canavs );
      assertTrue( bar._gfxCanvasAppended );
      assertNotNull( bar._backgroundShape );
      assertNotNull( bar._indicatorShape );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertNotNull( bar._borderShape );
      assertTrue( bar._useBorderShape );
      assertEquals( 2, bar._gfxBorderWidth );
      assertEquals( 98, bar._getIndicatorLength() );
      assertEquals( [ 7, 0, 0, 7 ], bar._getIndicatorRadii( 0, 98 ) );
      var edge = bar.getElement().style.borderLeftStyle;  
      assertTrue( edge == "" || edge == "none"  );            
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },


    testOnCanvasAppearOnEnhancedBorder : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var shell = new org.eclipse.swt.widgets.Shell();
      shell.setShadow( null );
      shell.addToDocument();
      shell.setBackgroundColor( null );
      shell.open();
      var log = [];      
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar._onCanvasAppear = function(){ log.push( "bar" ); };
      bar.setDimension( 200, 30 );
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 50 );
      bar.setParent( shell );
      testUtil.flush();
      assertEquals( 1, log.length );
      shell.setBackgroundColor( "green" );
      shell.setBorder( new org.eclipse.rwt.RoundedBorder( 1, "black" ) );
      testUtil.flush();
      assertEquals( 2, log.length );
      shell.destroy();
      testUtil.flush();
    },
    
    testRoundedBorderIndicatorMinLength : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 0.0001 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( 6, bar._getIndicatorLength() );
      assertEquals( [ 7, 0, 0, 7 ], bar._getIndicatorRadii( 0, 6 ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testRoundedBorderIndicatorZero : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 0 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 0, bar._getIndicatorLength() );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },

    testRoundedBorderIndicatorMaxLength : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 99.9999 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 190, bar._getIndicatorLength() );
      assertEquals( [ 7, 0, 0, 7 ], bar._getIndicatorRadii( 0, 190 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },

    testRoundedBorderIndicatorFull : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 100 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 196, bar._getIndicatorLength() );
      assertEquals( [ 7, 7, 7, 7 ], bar._getIndicatorRadii( 0, 196 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testDifferentRadii : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar.setSelection( 50 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 98, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 0, 8 ], bar._getIndicatorRadii( 0, 98 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testDifferentRadiiIndicatorMinLength : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar.setSelection( 0.0001 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 7, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 0, 8 ], bar._getIndicatorRadii( 0, 7 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testDifferentRadiiIndicatorZero : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar.setSelection( 0 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 0, bar._getIndicatorLength() );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },

    testDifferentRadiiIndicatorMaxLength : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar.setSelection( 99.9999 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 191, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 0, 8 ], bar._getIndicatorRadii( 0, 191 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },

    testDifferentRadiiIndicatorFull : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar.setSelection( 100 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 196, bar._getIndicatorLength() );
      assertEquals( [ 0, 4, 6, 8 ], bar._getIndicatorRadii( 0, 196 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },    
    
    testCreateSimpleBarVertical : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_VERTICAL );
      assertTrue( bar._isVertical() );
      bar.setDimension( 50, 120 );
      bar.addToDocument();
      bar.setBorder( null );
      bar.setSelection( 50 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 60, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 0, 60 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },    
    
    testRoundedBorderVertical : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_VERTICAL );
      assertTrue( bar._isVertical() );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder );
      bar.addToDocument();
      bar.setSelection( 50 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 58, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 7, 7 ], bar._getIndicatorRadii( 0, 58 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },    
    
    testDifferentRadiiIndicatorVerticalZero : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_VERTICAL );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder2 );
      bar.addToDocument();
      bar.setSelection( 0 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 0, bar._getIndicatorLength() );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();      
    },
    
    testDifferentRadiiIndicatorVerticalMinLength : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_VERTICAL );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder2 );
      bar.addToDocument();
      bar.setSelection( 0.0001 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 7, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 6, 8 ], bar._getIndicatorRadii( 0, 7 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();      
      
    },
    
    testDifferentRadiiIndicatorVerticalMaxLength : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_VERTICAL );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder2 );
      bar.addToDocument();
      bar.setSelection( 99.9999 );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 113, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 6, 8 ], bar._getIndicatorRadii( 0, 113 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();      
    },
    
    testDifferentRadiiIndicatorVerticalFull : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_VERTICAL );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder2 );
      bar.addToDocument();
      bar.setSelection( 100 );
      bar.setIndicatorColor( "red" );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 116, bar._getIndicatorLength() );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 4, 6, 8 ], bar._getIndicatorRadii( 0, 116 ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();      
    },
    
    testIndicatorFill : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setSelection( 50 );
      qx.ui.core.Widget.flushGlobalQueues();
      bar.setIndicatorColor( "green" );
      var shape = bar._indicatorShape;
      assertEquals( "color", gfxUtil.getFillType( shape ) );
      bar.setIndicatorGradient( [ [ 0, "red" ], [ 1, "yellow" ] ] );
      assertEquals( "gradient", gfxUtil.getFillType( shape ) );
      bar.setIndicatorImage( [ "./fake.jpg", 70, 70 ] );
      assertEquals( "pattern", gfxUtil.getFillType( shape ) );
      bar.setIndicatorColor( null );
      bar.setIndicatorGradient( null );
      bar.setIndicatorImage( null );
      assertEquals( null, gfxUtil.getFillType( shape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },

    testBackgroundFill : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setSelection( 50 );
      bar.setBackgroundColor( "green" );
      qx.ui.core.Widget.flushGlobalQueues();
      var shape = bar._backgroundShape;
      assertEquals( "color", gfxUtil.getFillType( shape ) );
      bar.setBackgroundGradient( [ [ 0, "red" ], [ 1, "yellow" ] ] );
      assertEquals( "gradient", gfxUtil.getFillType( shape ) );
      bar.setBackgroundImageSized( [ "./fake.jpg", 70, 70 ] );
      assertEquals( "pattern", gfxUtil.getFillType( shape ) );
      bar.setBackgroundColor( null );
      bar.setBackgroundGradient( null );
      bar.setBackgroundImageSized( null );
      assertEquals( null, gfxUtil.getFillType( shape ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },

    testUndeterminedSimple : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_UNDETERMINED );      
      assertFalse( bar._isVertical() );
      assertTrue( bar._isUndetermined() );
      assertNotNull( bar._timer );
      assertTrue( bar._timer.getEnabled() );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( null );
      bar._indicatorVirtualPosition = 30;
      // first step:
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 40, bar._getIndicatorLength() );
      assertEquals( 32, bar._indicatorVirtualPosition );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 32, 40 ) );
      // second step:
      testUtil.forceInterval( bar._timer );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 34, bar._indicatorVirtualPosition );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testUndeterminedRounded : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_UNDETERMINED );      
      assertFalse( bar._isVertical() );
      assertTrue( bar._isUndetermined() );
      assertNotNull( bar._timer );
      assertTrue( bar._timer.getEnabled() );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar._indicatorVirtualPosition = 30;
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 32, bar._indicatorVirtualPosition );
      assertEquals( 40, bar._getIndicatorLength( 32 ) );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 32, 100 ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testUndeterminedWrap : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_UNDETERMINED );      
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( null );
      bar._indicatorVirtualPosition = 196;
      // step 1:
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 198, bar._indicatorVirtualPosition );
      assertEquals( 2, bar._getIndicatorLength( 198 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      // step 2:      
      testUtil.forceInterval( bar._timer );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( -40, bar._indicatorVirtualPosition );
      assertEquals( 0, bar._getIndicatorLength( -40 ) );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      // step 2:      
      testUtil.forceInterval( bar._timer );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( -38, bar._indicatorVirtualPosition );
      assertEquals( 2, bar._getIndicatorLength( -38 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );      
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testUndeterminedRoundedWrap : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_UNDETERMINED );      
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar._indicatorVirtualPosition = 188;
      // step 1:
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 190, bar._indicatorVirtualPosition );
      assertEquals( 6, bar._getIndicatorLength( 190 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 4, 6, 0 ], bar._getIndicatorRadii( 190, 6 ) );
      // step 2:      
      testUtil.forceInterval( bar._timer );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( -40, bar._indicatorVirtualPosition );
      assertEquals( 0, bar._getIndicatorLength( -40 ) );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      // step 3:      
      testUtil.forceInterval( bar._timer );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( -33, bar._indicatorVirtualPosition );
      assertEquals( 7, bar._getIndicatorLength( -33 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 0, 0, 8 ], bar._getIndicatorRadii( 0, 8 ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },

    
    testUndeterminedRoundedWrapVertical : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag(   org.eclipse.swt.widgets.ProgressBar.FLAG_VERTICAL 
                   | org.eclipse.swt.widgets.ProgressBar.FLAG_UNDETERMINED );
      assertTrue( bar._isVertical() );
      assertTrue( bar._isUndetermined() );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder2 );
      bar.addToDocument();
      bar._indicatorVirtualPosition = 110;
      // step 1:
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 112, bar._indicatorVirtualPosition );
      assertEquals( 4, bar._getIndicatorLength( 112 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 4, 0, 0 ], bar._getIndicatorRadii( 112, 4 ) );
      // step 2:      
      testUtil.forceInterval( bar._timer );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( -40, bar._indicatorVirtualPosition );
      assertEquals( 0, bar._getIndicatorLength( -40 ) );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      // step 3:      
      testUtil.forceInterval( bar._timer );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( -33, bar._indicatorVirtualPosition );
      assertEquals( 7, bar._getIndicatorLength( -33 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 0, 6, 8 ], bar._getIndicatorRadii( 0, 8 ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },
    
    testUndeterminedRoundedSkipStart : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_UNDETERMINED );      
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar._indicatorVirtualPosition = -2;
      // step 1:
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 0, bar._indicatorVirtualPosition );
      assertEquals( 40, bar._getIndicatorLength( 0 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 0, 0, 8 ], bar._getIndicatorRadii( 0, 40 ) );
      // step 2:
      testUtil.forceInterval( bar._timer );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 7, bar._indicatorVirtualPosition );
      assertEquals( 40, bar._getIndicatorLength( 7 ) );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 8, 40 ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    },

    testUndeterminedRoundedSkipEnd : function() {
      var gfxUtil = org.eclipse.rwt.GraphicsUtil;
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      testUtil.prepareTimerUse();
      var bar = new org.eclipse.swt.widgets.ProgressBar();
      bar.setFlag( org.eclipse.swt.widgets.ProgressBar.FLAG_UNDETERMINED );      
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar._indicatorVirtualPosition = 148;
      // step 1:
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 150, bar._indicatorVirtualPosition );
      assertEquals( 40, bar._getIndicatorLength( 150 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 150, 40 ) );
      // step 2:
      testUtil.forceInterval( bar._timer );
      qx.ui.core.Widget.flushGlobalQueues();
      assertEquals( 156, bar._indicatorVirtualPosition );
      assertEquals( 40, bar._getIndicatorLength( 156 ) );
      assertEquals( [ 0, 4, 6, 0 ], bar._getIndicatorRadii( 156, 40 ) );
      bar.destroy();
      qx.ui.core.Widget.flushGlobalQueues();
    }

  } 

} );