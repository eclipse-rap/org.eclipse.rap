/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ProgressBarTest", {

  extend : rwt.qx.Object,

  construct : function() {
    this.base( arguments );
    this._gfxBorder = new rwt.html.Border( 2, "rounded", "black", [ 7, 7, 7, 7 ] );
    this._gfxBorder2 = new rwt.html.Border( 2, "rounded", "black", [ 0, 4, 6, 8 ] );
    this._cssBorder = new rwt.html.Border( 2, "outset", null );
    this._gradient = [ [ 0, "red" ], [ 1, "yellow" ] ];
  },

  members : {

    testCreateProgressBarByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.ProgressBar );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "progressbar", widget.getAppearance() );
      assertTrue( widget._isHorizontal() );
      assertFalse( widget._isVertical() );
      assertFalse( widget._isIndeterminate() );
      assertEquals( 0, widget._minimum );
      assertEquals( 100, widget._maximum );
      assertEquals( 0, widget._selection );
      assertTrue( widget.hasState( "normal" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreateProgressBarWithVerticalByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "VERTICAL" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertFalse( widget._isHorizontal() );
      assertTrue( widget._isVertical() );
      assertFalse( widget._isIndeterminate() );
      shell.destroy();
      widget.destroy();
    },

    testCreateProgressBarWithVerticalAndIndeterminateByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "VERTICAL", "INDETERMINATE" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertFalse( widget._isHorizontal() );
      assertTrue( widget._isVertical() );
      assertTrue( widget._isIndeterminate() );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2",
          "minimum" : 50
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 50, widget._minimum );
      shell.destroy();
      widget.destroy();
    },

    testSetMaximumByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2",
          "maximum" : 150
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 150, widget._maximum );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumBiggerThanCurrentMaximumByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2",
          "minimum" : 150,
          "maximum" : 200
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 150, widget._minimum );
      assertEquals( 200, widget._maximum );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2",
          "selection" : 50
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 50, widget._selection );
      shell.destroy();
      widget.destroy();
    },

    testSetBackgroundImageByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2",
          "backgroundImage" : [ "image.png", 10, 20 ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertNull( widget.getBackgroundImage() );
      var backgroundImage = widget.getBackgroundImageSized();
      assertNotNull( backgroundImage );
      assertEquals( "image.png", backgroundImage[ 0 ] );
      assertEquals( 10, backgroundImage[ 1 ] );
      assertEquals( 20, backgroundImage[ 2 ] );
      shell.destroy();
      widget.destroy();
    },

    testResetBackgroundImageByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeAppearance( "progressbar",  {
        style : function( states ) {
          return {
            "backgroundImageSized" : [ "fromTheme.png", 10, 20 ]
          };
        }
      } );
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2",
          "backgroundImage" : [ "image.png", 10, 20 ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var backgroundImage = widget.getBackgroundImageSized();
      assertEquals( "image.png", backgroundImage[ 0 ] );
      TestUtil.protocolSet( "w3", { "backgroundImage" : null } );
      backgroundImage = widget.getBackgroundImageSized();
      assertEquals( "fromTheme.png", backgroundImage[ 0 ] );
      shell.destroy();
      widget.destroy();
    },

    testCreateSimpleBar : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      assertFalse( bar._isVertical() );
      assertFalse( bar._isIndeterminate() );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( null );
      bar.setSelection( 50 );
      rwt.widgets.base.Widget.flushGlobalQueues();
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
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      assertFalse( TestUtil.hasCssBorder( bar.getElement() ) ) ;
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testComplexBorder : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      assertFalse( bar._isVertical() );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( new rwt.html.Border( 2, "inset", null ) );
      bar.setSelection( 50 );
      rwt.widgets.base.Widget.flushGlobalQueues();
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
      assertFalse( edge === "" || edge === "none"  );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testRoundedBorder : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 50 );
      rwt.widgets.base.Widget.flushGlobalQueues();
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
      assertTrue( edge === "" || edge === "none"  );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testOnCanvasAppearOnEnhancedBorder : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( { "style" : [] } );
      shell.setShadow( null );
      shell.addToDocument();
      shell.setBackgroundColor( null );
      shell.setBorder( null );
      shell.open();
      var log = [];
      var bar = new rwt.widgets.ProgressBar();
      bar._onCanvasAppear = function(){ log.push( "bar" ); };
      bar.setDimension( 200, 30 );
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 50 );
      bar.setParent( shell );
      TestUtil.flush();
      TestUtil.fakeResponse( false );
      assertEquals( 1, log.length );
      if( !rwt.client.Client.supportsCss3() ) {
        shell.setBackgroundColor( "green" );
        shell.setBorder( new rwt.html.Border( 1, "rounded", "black", 2 ) );
        TestUtil.flush();
        assertEquals( 2, log.length );
      }
      shell.destroy();
      TestUtil.flush();
    },

    testRoundedBorderIndicatorMinLength : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 0.0001 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( 6, bar._getIndicatorLength() );
      assertEquals( [ 7, 0, 0, 7 ], bar._getIndicatorRadii( 0, 6 ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testRoundedBorderIndicatorZero : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 0 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 0, bar._getIndicatorLength() );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testRoundedBorderIndicatorMaxLength : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 99.9999 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 190, bar._getIndicatorLength() );
      assertEquals( [ 7, 0, 0, 7 ], bar._getIndicatorRadii( 0, 190 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testRoundedBorderIndicatorFull : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder );
      bar.setSelection( 100 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 196, bar._getIndicatorLength() );
      assertEquals( [ 7, 7, 7, 7 ], bar._getIndicatorRadii( 0, 196 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testDifferentRadii : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar.setSelection( 50 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 98, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 0, 8 ], bar._getIndicatorRadii( 0, 98 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testDifferentRadiiIndicatorMinLength : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar.setSelection( 0.0001 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 7, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 0, 8 ], bar._getIndicatorRadii( 0, 7 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testDifferentRadiiIndicatorZero : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar.setSelection( 0 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 0, bar._getIndicatorLength() );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testDifferentRadiiIndicatorMaxLength : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar.setSelection( 99.9999 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 191, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 0, 8 ], bar._getIndicatorRadii( 0, 191 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testDifferentRadiiIndicatorFull : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar.setSelection( 100 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 196, bar._getIndicatorLength() );
      assertEquals( [ 0, 4, 6, 8 ], bar._getIndicatorRadii( 0, 196 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testCreateSimpleBarVertical : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_VERTICAL" );
      assertTrue( bar._isVertical() );
      bar.setDimension( 50, 120 );
      bar.addToDocument();
      bar.setBorder( null );
      bar.setSelection( 50 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 60, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 0, 60 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testRoundedBorderVertical : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_VERTICAL" );
      assertTrue( bar._isVertical() );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder );
      bar.addToDocument();
      bar.setSelection( 50 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 58, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 7, 7 ], bar._getIndicatorRadii( 0, 58 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testDifferentRadiiIndicatorVerticalZero : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_VERTICAL" );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder2 );
      bar.addToDocument();
      bar.setSelection( 0 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 0, bar._getIndicatorLength() );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testDifferentRadiiIndicatorVerticalMinLength : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_VERTICAL" );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder2 );
      bar.addToDocument();
      bar.setSelection( 0.0001 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 7, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 6, 8 ], bar._getIndicatorRadii( 0, 7 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();

    },

    testDifferentRadiiIndicatorVerticalMaxLength : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_VERTICAL" );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder2 );
      bar.addToDocument();
      bar.setSelection( 99.9999 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 113, bar._getIndicatorLength() );
      assertEquals( [ 0, 0, 6, 8 ], bar._getIndicatorRadii( 0, 113 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testDifferentRadiiIndicatorVerticalFull : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_VERTICAL" );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder2 );
      bar.addToDocument();
      bar.setSelection( 100 );
      bar.setIndicatorColor( "red" );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 116, bar._getIndicatorLength() );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 4, 6, 8 ], bar._getIndicatorRadii( 0, 116 ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testIndicatorFill : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setSelection( 50 );
      rwt.widgets.base.Widget.flushGlobalQueues();
      bar.setIndicatorColor( "green" );
      bar.setIndicatorGradient( null );
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
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testBackgroundFill : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var bar = new rwt.widgets.ProgressBar();
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setSelection( 50 );
      bar.setBackgroundColor( "green" );
      bar.setBackgroundGradient( null );
      bar.setBackgroundImageSized( null );
      rwt.widgets.base.Widget.flushGlobalQueues();
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
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testUndeterminedSimple : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_INDETERMINATE" );
      assertFalse( bar._isVertical() );
      assertTrue( bar._isIndeterminate() );
      assertNotNull( bar._timer );
      assertTrue( bar._timer.getEnabled() );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( null );
      bar._indicatorVirtualPosition = 30;
      // first step:
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 40, bar._getIndicatorLength() );
      assertEquals( 32, bar._indicatorVirtualPosition );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 32, 40 ) );
      // second step:
      TestUtil.forceInterval( bar._timer );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 34, bar._indicatorVirtualPosition );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testUndeterminedRounded : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_INDETERMINATE" );
      assertFalse( bar._isVertical() );
      assertTrue( bar._isIndeterminate() );
      assertNotNull( bar._timer );
      assertTrue( bar._timer.getEnabled() );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar._indicatorVirtualPosition = 30;
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 32, bar._indicatorVirtualPosition );
      assertEquals( 40, bar._getIndicatorLength( 32 ) );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 32, 100 ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testUndeterminedWrap : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_INDETERMINATE" );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( null );
      bar._indicatorVirtualPosition = 196;
      // step 1:
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 198, bar._indicatorVirtualPosition );
      assertEquals( 2, bar._getIndicatorLength( 198 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      // step 2:
      TestUtil.forceInterval( bar._timer );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( -40, bar._indicatorVirtualPosition );
      assertEquals( 0, bar._getIndicatorLength( -40 ) );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      // step 2:
      TestUtil.forceInterval( bar._timer );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( -38, bar._indicatorVirtualPosition );
      assertEquals( 2, bar._getIndicatorLength( -38 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testUndeterminedRoundedWrap : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_INDETERMINATE" );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar._indicatorVirtualPosition = 188;
      // step 1:
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 190, bar._indicatorVirtualPosition );
      assertEquals( 6, bar._getIndicatorLength( 190 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 4, 6, 0 ], bar._getIndicatorRadii( 190, 6 ) );
      // step 2:
      TestUtil.forceInterval( bar._timer );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( -40, bar._indicatorVirtualPosition );
      assertEquals( 0, bar._getIndicatorLength( -40 ) );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      // step 3:
      TestUtil.forceInterval( bar._timer );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( -33, bar._indicatorVirtualPosition );
      assertEquals( 7, bar._getIndicatorLength( -33 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 0, 0, 8 ], bar._getIndicatorRadii( 0, 8 ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },


    testUndeterminedRoundedWrapVertical : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_VERTICAL" );
      bar.addState( "rwt_INDETERMINATE" );
      assertTrue( bar._isVertical() );
      assertTrue( bar._isIndeterminate() );
      bar.setDimension( 50, 120 );
      bar.setBorder( this._gfxBorder2 );
      bar.addToDocument();
      bar._indicatorVirtualPosition = 110;
      // step 1:
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 112, bar._indicatorVirtualPosition );
      assertEquals( 4, bar._getIndicatorLength( 112 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 4, 0, 0 ], bar._getIndicatorRadii( 112, 4 ) );
      // step 2:
      TestUtil.forceInterval( bar._timer );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( -40, bar._indicatorVirtualPosition );
      assertEquals( 0, bar._getIndicatorLength( -40 ) );
      assertFalse( gfxUtil.getDisplay( bar._indicatorShape ) );
      // step 3:
      TestUtil.forceInterval( bar._timer );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( -33, bar._indicatorVirtualPosition );
      assertEquals( 7, bar._getIndicatorLength( -33 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 0, 6, 8 ], bar._getIndicatorRadii( 0, 8 ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testUndeterminedRoundedSkipStart : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_INDETERMINATE" );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar._indicatorVirtualPosition = -2;
      // step 1:
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 0, bar._indicatorVirtualPosition );
      assertEquals( 40, bar._getIndicatorLength( 0 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 0, 0, 8 ], bar._getIndicatorRadii( 0, 40 ) );
      // step 2:
      TestUtil.forceInterval( bar._timer );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 7, bar._indicatorVirtualPosition );
      assertEquals( 40, bar._getIndicatorLength( 7 ) );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 8, 40 ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testUndeterminedRoundedSkipEnd : function() {
      var gfxUtil = rwt.graphics.GraphicsUtil;
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.prepareTimerUse();
      var bar = new rwt.widgets.ProgressBar();
      bar.addState( "rwt_INDETERMINATE" );
      bar.setDimension( 200, 30 );
      bar.addToDocument();
      bar.setBorder( this._gfxBorder2 );
      bar._indicatorVirtualPosition = 148;
      // step 1:
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 150, bar._indicatorVirtualPosition );
      assertEquals( 40, bar._getIndicatorLength( 150 ) );
      assertTrue( gfxUtil.getDisplay( bar._indicatorShape ) );
      assertEquals( [ 0, 0, 0, 0 ], bar._getIndicatorRadii( 150, 40 ) );
      // step 2:
      TestUtil.forceInterval( bar._timer );
      rwt.widgets.base.Widget.flushGlobalQueues();
      assertEquals( 156, bar._indicatorVirtualPosition );
      assertEquals( 40, bar._getIndicatorLength( 156 ) );
      assertEquals( [ 0, 4, 6, 0 ], bar._getIndicatorRadii( 156, 40 ) );
      bar.destroy();
      rwt.widgets.base.Widget.flushGlobalQueues();
    },

    testFiresSelectionChangedEvent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var progressBar = ObjectManager.getObject( "w3" );
      var log = 0;
      progressBar.addEventListener( "selectionChanged", function() {
        log++;
      } );

      progressBar.setSelection( 33 );

      assertEquals( 1, log );
      shell.destroy();
      progressBar.destroy();
    },

    testFiresMinimumChangedEvent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var progressBar = ObjectManager.getObject( "w3" );
      var log = 0;
      progressBar.addEventListener( "minimumChanged", function() {
        log++;
      } );

      progressBar.setMinimum( 5 );

      assertEquals( 1, log );
      shell.destroy();
      progressBar.destroy();
    },

    testFiresMaximumChangedEvent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.ProgressBar",
        "properties" : {
          "style" : [ "HORIZONTAL" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var progressBar = ObjectManager.getObject( "w3" );
      var log = 0;
      progressBar.addEventListener( "maximumChanged", function() {
        log++;
      } );

      progressBar.setMaximum( 200 );

      assertEquals( 1, log );
      shell.destroy();
      progressBar.destroy();
    }

  }

} );