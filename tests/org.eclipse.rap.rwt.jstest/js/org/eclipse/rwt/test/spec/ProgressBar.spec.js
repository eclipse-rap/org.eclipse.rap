/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

describe( "ProgressBar", function() {

  var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
  var ProgressBar = rwt.widgets.ProgressBar;

  var bar;
  var indicator;

  var createBar = function( vertical, indet ) {
    bar = new ProgressBar( vertical, indet );
    bar.setSpace( 0, 50, 0, 60 );
    bar.addToDocument();
    TestUtil.flush();
    indicator = bar._indicator;
    return bar;
  };

  var selection = function( selection, min, max ) {
    bar.setMinimum( min );
    bar.setMaximum( max );
    bar.setSelection( selection );
    TestUtil.flush();
  };

  afterEach( function() {
    bar.destroy();
    bar = null;
  } );

  describe( "constructor", function() {

    it( "creates horizontal bar by default", function() {
      bar = new ProgressBar();

      expect( bar.isHorizontal() ).toBe( true );
      expect( bar.isVertical() ).toBe( false );
      expect( bar.isIndeterminate() ).toBe( false );
    } );

    it( "creates vertical bar", function() {
      bar = new ProgressBar( true );

      expect( bar.isHorizontal() ).toBe( false );
      expect( bar.isVertical() ).toBe( true );
      expect( bar.isIndeterminate() ).toBe( false );
    } );

    it( "creates indeterminate bar", function() {
      bar = new ProgressBar( false, true );

      expect( bar.isHorizontal() ).toBe( true );
      expect( bar.isVertical() ).toBe( false );
      expect( bar.isIndeterminate() ).toBe( true );
    } );

    it( "sets default initial values", function() {
      bar = new ProgressBar();

      expect( bar.getMinimum() ).toBe( 0 );
      expect( bar.getMaximum() ).toBe( 100 );
      expect( bar.getSelection() ).toBe( 0 );
      expect( bar.hasState( "normal" ) ).toBe( true );
      expect( bar.hasState( "paused" ) ).toBe( false );
      expect( bar.hasState( "error" ) ).toBe( false );
    } );

  } );

  describe( "indicator element", function() {

    var indicatorLayout = function() {
      return [
        indicator.style.left,
        indicator.style.top,
        indicator.style.width,
        indicator.style.height
      ];
    };

    it( "is created", function() {
      createBar();

      expect( indicator.style.position ).toBe( "absolute" );
      expect( indicator.parentElement ).toBe( bar._getTargetNode() );
    } );

    it( "has indicatorColor", function() {
      createBar().setIndicatorColor( "#ff00ee" );

      var color = rwt.html.Style.getBackgroundColor( indicator );
      expect( color ).toBe( "#ff00ee" );
    } );

    it( "has indicatorImage", function() {
      createBar().setIndicatorImage( "foo.jpg" );

      var imageStr = TestUtil.getCssBackgroundImage( indicator );
      expect( imageStr ).toContain( "foo.jpg" );
    } );

    it( "has indicatorGradient", function() {
      createBar().setIndicatorGradient( [ [ 0, "#ffffff" ], [ 1, "#000000"  ] ] );
      expect( indicator.style.background ).toContain( "gradient(" );
    } );

    it( "has indicatorOpacity", function() {
      createBar().setIndicatorOpacity( 0.7 );

      expect( indicator.style.opacity ).toBe( "0.7" );
    } );

    describe( "with vertical orientation", function() {

      beforeEach( createBar );

      it( "is 0% for selection 0 of 0 to 100", function() {
        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "0%", "100%" ] );
      } );

      it( "is 50% for selection 50 of 0 to 100", function() {
        selection( 50, 0, 100 );
        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "50%", "100%" ] );
      } );

      it( "is 100% for selection 100 of 0 to 100", function() {
        selection( 100, 0, 100 );
        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "100%", "100%" ] );
      } );

      it( "is 0% for selection 50 of 50 to 100", function() {
        selection( 50, 50, 100 );
        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "0%", "100%" ] );
      } );

      it( "is 50% for selection 75 of 50 to 100", function() {
        selection( 75, 50, 100 );
        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "50%", "100%" ] );
      } );

      it( "is 100% for selection 100 of 50 to 100", function() {
        selection( 100, 50, 100 );
        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "100%", "100%" ] );
      } );

      it( "is 0% for selection 0 of 50 to 50", function() {
        selection( 0, 0, 50 );
        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "0%", "100%" ] );
      } );

      it( "is 50% for selection 25 of 0 to 50", function() {
        selection( 25, 0, 50 );
        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "50%", "100%" ] );
      } );

      it( "is 100% for selection 50 of 0 to 50", function() {
        selection( 50, 0, 50 );
        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "100%", "100%" ] );
      } );

      it( "is 99% for selection 999 of 0 to 1000", function() {
        selection( 999, 0, 1000 );
        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "99%", "100%" ] );
      } );

      it( "is updated by setSelection", function() {
        bar.setSelection( 50 );
        TestUtil.flush();

        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "50%", "100%" ] );
      } );

      it( "is updated by setMinimum", function() {
        bar.setSelection( 75 );
        TestUtil.flush();
        bar.setMinimum( 50 );
        TestUtil.flush();

        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "50%", "100%" ] );
      } );

      it( "is updated by setMaximum", function() {
        bar.setSelection( 25 );
        TestUtil.flush();
        bar.setMaximum( 50 );
        TestUtil.flush();

        expect( indicatorLayout() ).toEqual( [ "0px", "0px", "50%", "100%" ] );
      } );

    } );

    describe( "with vertical orientation", function() {

      beforeEach( function() {
        createBar( true );
      } );

      it( "is bottom aligned", function() {
        expect( indicator.style.bottom ).toBe( "0px" );
      } );

      it( "is 0% for selection 0 of 0 to 100", function() {
        expect( indicatorLayout() ).toEqual( [ "0px", "auto", "100%", "0%" ] );
      } );

      it( "is 50% for selection 50 of 0 to 100", function() {
        selection( 50, 0, 100 );
        expect( indicatorLayout() ).toEqual( [ "0px", "auto", "100%", "50%" ] );
      } );

      it( "is 100% for selection 100 of 0 to 100", function() {
        selection( 100, 0, 100 );
        expect( indicatorLayout() ).toEqual( [ "0px", "auto", "100%", "100%" ] );
      } );

    } );

    describe( "indeterminate", function() {

      var length = ProgressBar.UNDETERMINED_SIZE;

      var timer = function( position ) {
        if( arguments.length > 0 ) {
          bar._animPosition = position;
        }
        TestUtil.forceInterval( bar._timer );
      };

      describe( "horizontal", function() {

        beforeEach( function() {
          createBar( false, true );
        } );

        it( "starts hidden to left", function() {
          var left = ( length * -1 ) + "px";
          expect( indicatorLayout() ).toEqual( [ left, "0px", length + "px", "100%" ] );
        } );

        it( "moves 1 pixel to the right", function() {
          timer();

          var left = ( length * -1 + 1 ) + "px";
          expect( indicatorLayout() ).toEqual( [ left, "0px", length + "px", "100%" ] );
        } );

        it( "moves all the way to the right", function() {
          timer( 49 );

          expect( indicatorLayout() ).toEqual( [ "50px", "0px", length + "px", "100%" ] );
        } );

        it( "wraps back to the left", function() {
          timer( 50 );

          var left = ( length * -1 ) + "px";
          expect( indicatorLayout() ).toEqual( [ left, "0px", length + "px", "100%" ] );
        } );

      } );

      describe( "vertical", function() {

        beforeEach( function() {
          createBar( true, true );
        } );

        it( "starts hidden to bottom", function() {
          var bottom = ( length * -1 ) + "px";
          expect( indicatorLayout() ).toEqual( [ "0px", "auto", "100%", length + "px" ] );
          expect( indicator.style.bottom ).toBe( bottom );
        } );

        it( "moves 1 pixel to the top", function() {
          timer();

          var bottom = ( length * -1 + 1 ) + "px";
          expect( indicator.style.bottom ).toBe( bottom );
        } );

        it( "moves all the way to the top", function() {
          timer( 59 );

          expect( indicator.style.bottom ).toBe( "60px" );
        } );

        it( "wraps back to the bottom", function() {
          timer( 60 );

          var bottom = ( length * -1 ) + "px";
          expect( indicator.style.bottom ).toBe( bottom );
        } );

      } );

    } );

  } );

  describe( "change event", function() {

    beforeEach( createBar );

    it( "is fired by setSelection", function() {
      var spy = jasmine.createSpy();
      bar.addEventListener( "selectionChanged", spy );

      bar.setSelection( 22 );

      expect( spy ).toHaveBeenCalled();
    } );

    it( "is fired by toolTipTargetBounds change", function() {
      var spy = jasmine.createSpy();
      bar.addEventListener( "updateToolTip", spy );

      bar.setSelection( 22 );

      expect( spy ).toHaveBeenCalled();
    } );

    it( "is fired by setMinimum", function() {
      var spy = jasmine.createSpy();
      bar.addEventListener( "minimumChanged", spy );

      bar.setMinimum( 22 );

      expect( spy ).toHaveBeenCalled();
    } );

    it( "is fired by setMaximum", function() {
      var spy = jasmine.createSpy();
      bar.addEventListener( "maximumChanged", spy );

      bar.setMaximum( 22 );

      expect( spy ).toHaveBeenCalled();
    } );

  } );

  describe( "getToolTipTargetBounds", function() {

    it( "returns horizontal selection position", function() {
      createBar();
      bar.setDimension( 200, 30 );
      bar.setBorder( new rwt.html.Border( 3 ) );
      selection( 50, 0, 100 );

      var bounds = bar.getToolTipTargetBounds();

      expect( bounds ).toEqual( {
        "left" : 103,
        "top" : 0,
        "width" : 1,
        "height" : 30
      } );
    } );

    it( "returns vertical selection position", function() {
      createBar( true );
      bar.setDimension( 30, 200 );
      bar.setBorder( new rwt.html.Border( 3 ) );
      selection( 30, 0, 100 );

      var bounds = bar.getToolTipTargetBounds();

      expect( bounds ).toEqual( {
        "left" : 0,
        "top" : 137,
        "width" : 30,
        "height" : 1
      } );
    } );

    it( "returns center position for indeterminate ", function() {
      createBar( false, true );
      bar.setDimension( 30, 200 );
      bar.setBorder( new rwt.html.Border( 3 ) );
      TestUtil.flush();

      var bounds = bar.getToolTipTargetBounds();

      expect( bounds ).toEqual( {
        "left" : 0,
        "top" : 0,
        "width" : 30,
        "height" : 200
      } );
    } );

  } );


} );
