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

describe( "Style", function() {

  var Style = rwt.html.Style;
  var Client = rwt.client.Client;
  var css3 = Client.supportsCss3();
  var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
  var vGradientObject = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
  var hGradientObject = [ [ 0, "rgb(255, 0, 255)" ], [ 1, "rgb(0, 255, 0)" ] ];
  hGradientObject.horizontal = true;
  var vGradientString = (function() {
    var result;
    if( rwt.client.Client.isWebkit() ) {
      result = "gradient(linear, 0% 0%, 0% 100%, from(rgb(255, 0, 255)), to(rgb(0, 255, 0)))";
    } else if( rwt.client.Client.isGecko() ) {
      result = "gradient(-90deg, rgb(255, 0, 255) 0%, rgb(0, 255, 0) 100%)";
    } else if( rwt.client.Client.isTrident() && rwt.client.Client.getMajor() === 9 ) {
      result =   "url(\"data:image/svg+xml;charset=utf-8,"
               + encodeURIComponent( "<svg xmlns='http://www.w3.org/2000/svg'><linearGradient id='g' x2='0' y2='1'><stop offset='0%' stop-color='rgb(255, 0, 255)'/><stop offset='100%' stop-color='rgb(0, 255, 0)'/></linearGradient><rect fill='url(#g)' width='100%' height='100%'/></svg>" ) + "\")";
    } else {
      result = "gradient(180deg, rgb(255, 0, 255) 0%, rgb(0, 255, 0) 100%)";
    }
    return result;
  }() );
  var hGradientString = (function() {
    var result;
    if( rwt.client.Client.isWebkit() ) {
      result = "gradient(linear, 0% 0%, 100% 0%, from(rgb(255, 0, 255)), to(rgb(0, 255, 0)))";
    } else if( rwt.client.Client.isGecko() ) {
      result = "gradient(0deg, rgb(255, 0, 255) 0%, rgb(0, 255, 0) 100%)";
    } else if( rwt.client.Client.isTrident() && rwt.client.Client.getMajor() === 9 ) {
      result =   "url(\"data:image/svg+xml;charset=utf-8,"
               + encodeURIComponent( "<svg xmlns='http://www.w3.org/2000/svg'><linearGradient id='g'><stop offset='0%' stop-color='rgb(255, 0, 255)'/><stop offset='100%' stop-color='rgb(0, 255, 0)'/></linearGradient><rect fill='url(#g)' width='100%' height='100%'/></svg>" ) + "\")";
    } else {
      result = "gradient(90deg, rgb(255, 0, 255) 0%, rgb(0, 255, 0) 100%)";
    }
    return result;
  }() );
  var webkitFlatGradient = "gradient(linear, 0% 0%, 0% 100%, from(rgb(255, 0, 0)), to(rgb(255, 0, 0)))";
  var color = "rgb(255, 0, 0)";
  var image = "foo.png";
  var element;

  beforeEach( function() {
    element = document.createElement( "div" );
  } );

  if( css3 ) {

    describe( "setBackgroundGradient", function() {

      it( "sets background property for vertical gradient", function() {
        Style.setBackgroundGradient( element, vGradientObject );
        expect( TestUtil.getCssGradient( element ) ).toBe( vGradientString );
      } );

      it( "sets background property for horizontal gradient", function() {
        Style.setBackgroundGradient( element, hGradientObject );

        expect( TestUtil.getCssGradient( element ) ).toBe( hGradientString );
      } );

      it( "vertical gradient is returned by getBackgroundGradient in standard syntax", function() {
        Style.setBackgroundGradient( element, vGradientObject );

        var expected = "linear-gradient( to bottom, rgb(255, 0, 255) 0%, rgb(0, 255, 0) 100% )";
        expect( Style.getBackgroundGradient( element ) ).toBe( expected );
      } );

      it( "horizontal gradient is returned by getBackgroundGradient in standard syntax", function() {
        Style.setBackgroundGradient( element, hGradientObject );

        var expected = "linear-gradient( to right, rgb(255, 0, 255) 0%, rgb(0, 255, 0) 100% )";
        expect( Style.getBackgroundGradient( element ) ).toBe( expected );
      } );

      it( "clears background property", function() {
        Style.setBackgroundGradient( element, vGradientObject );

        Style.setBackgroundGradient( element, null );

        expect( TestUtil.getCssGradient( element ) ).toBe( "" );
        expect( TestUtil.getCssBackgroundColor( element ) ).toBeNull();
      } );

      it( "overwrites background color", function() {
        Style.setBackgroundColor( element, "#FF0000" );

        Style.setBackgroundGradient( element, vGradientObject );

        expect( TestUtil.getCssGradient( element ) ).toBe( vGradientString );
      } );

      it( "does not overwrite background image", function() {
        Style.setBackgroundImage( element, image );

        Style.setBackgroundGradient( element, vGradientObject );

        expect( TestUtil.getCssBackgroundImage( element ) ).toContain( image );
      } );

      it( "restores background color when set to null", function() {
        Style.setBackgroundColor( element, "#FF0000" );
        Style.setBackgroundGradient( element, vGradientObject );

        Style.setBackgroundGradient( element, null );

        if( Client.isWebkit() ) {
          expect( TestUtil.getCssGradient( element ) ).toBe( webkitFlatGradient );
        } else {
          expect( TestUtil.getCssGradient( element ) ).toBe( "" );
        }
        var cssColor = rwt.util.Colors.stringToRgb( element.style.backgroundColor );
        expect( cssColor ).toEqual( [ 255, 0, 0 ] );
      } );

    } );

  }

  describe( "setBackgroundImage", function() {

    it( "sets background property", function() {
      Style.setBackgroundImage( element, image );

      expect( TestUtil.getCssBackgroundImage( element ) ).toContain( image );
    } );

    it( "value is returned by getBackgroundImage", function() {
      Style.setBackgroundImage( element, image );

      expect( Style.getBackgroundImage( element ) ).toBe( "url(" + image + ")" );
    } );

    it( "sets background property together with background color", function() {
      Style.setBackgroundColor( element, color );

      Style.setBackgroundImage( element, image );

      var cssColor = rwt.util.Colors.stringToRgb( TestUtil.getCssBackgroundColor( element ) );
      expect( cssColor ).toEqual( [ 255, 0, 0 ] );
      expect( TestUtil.getCssBackgroundImage( element ) ).toContain( image );
    } );

    it( "overwrites background gradient", function() {
      Style.setBackgroundGradient( element, vGradientObject );

      Style.setBackgroundImage( element, image );

      expect( TestUtil.getCssGradient( element ) ).toBe( "" );
    } );

    it( "clears background property", function() {
      Style.setBackgroundImage( element, image );

      Style.setBackgroundImage( element, null );

      expect( TestUtil.getCssBackgroundImage( element ) ).toBe( "" );
      expect( TestUtil.getCssBackgroundColor( element ) ).toBeNull();
    } );

  } );

  describe( "setBackgroundRepeat", function() {

    it( "sets background property when image is set first", function() {
      Style.setBackgroundImage( element, image );

      Style.setBackgroundRepeat( element, "no-repeat" );

      expect( element.style.backgroundRepeat ).toBe( "no-repeat" );
    } );

    it( "sets background property when repeat is set first", function() {
      Style.setBackgroundRepeat( element, "no-repeat" );

      Style.setBackgroundImage( element, image );

      expect( element.style.backgroundRepeat ).toBe( "no-repeat" );
    } );

  } );

  describe( "setBackgroundPosition", function() {

    it( "sets background property when image is set first", function() {
      Style.setBackgroundImage( element, image );

      Style.setBackgroundPosition( element, "40% 60%" );

      expect( element.style.backgroundPosition ).toBe( "40% 60%" );
    } );

    it( "sets background property when position is set first", function() {
      Style.setBackgroundPosition( element, "40% 60%" );

      Style.setBackgroundImage( element, image );

      expect( element.style.backgroundPosition ).toBe( "40% 60%" );
    } );

  } );

  describe( "setBackgroundSize", function() {

    it( "sets background property when image is set first", function() {
      Style.setBackgroundImage( element, image );

      Style.setBackgroundSize( element, "cover" );

      expect( element.style.backgroundSize ).toBe( "cover" );
    } );

    it( "sets background property when size is set first", function() {
      Style.setBackgroundSize( element, "cover" );

      Style.setBackgroundImage( element, image );

      expect( element.style.backgroundSize ).toBe( "cover" );
    } );

  } );

  describe( "setBackgroundColor", function() {

    it( "sets background property", function() {
      Style.setBackgroundColor( element, color );

      var cssColor = rwt.util.Colors.stringToRgb( TestUtil.getCssBackgroundColor( element ) );
      expect( cssColor ).toEqual( [ 255, 0, 0 ] );
    } );

    it( "value is returned by getBackgroundColor", function() {
      Style.setBackgroundColor( element, color );

      expect( Style.getBackgroundColor( element ) ).toBe( color );
    } );

    it( "sets background property together with background image", function() {
      Style.setBackgroundImage( element, image );

      Style.setBackgroundColor( element, color );

      var cssColor = rwt.util.Colors.stringToRgb( TestUtil.getCssBackgroundColor( element ) );
      expect( cssColor ).toEqual( [ 255, 0, 0 ] );
      expect( TestUtil.getCssBackgroundImage( element ) ).toContain( image );
    } );

    it( "sets background color and image in correct order", function() {
      Style.setBackgroundImage( element, image );

      Style.setBackgroundColor( element, "#f1f2f3" );

      var background = element.style.background.toLowerCase();
      var colorOffset = background.indexOf( "rgb(241, 242, 243)" );
      var imageOffset = background.indexOf( "url(" );
      var gradientOffset = background.indexOf( "gradient" );
      if( colorOffset === -1 ) {
        colorOffset = background.indexOf( "#f1f2f3" );
      }
      if( Client.isWebkit() ) {
        expect( imageOffset ).toBeLessThan( gradientOffset );
        expect( gradientOffset ).toBeLessThan( colorOffset );
      } else {
        expect( imageOffset ).toBeLessThan( colorOffset );
        expect( gradientOffset ).toBe( -1 );
      }
    } );

    it( "clears background property if set to null", function() {
      Style.setBackgroundColor( element, color );

      Style.setBackgroundColor( element, null );

      expect( TestUtil.getCssBackgroundColor( element ) ).toBeNull();
      if( Client.isTrident() ) {
        expect( element.style.backgroundColor ).toBe( "rgba(0, 0, 0, 0)" );
      }
    } );

    it( "clears background property if set to transparent", function() {
      Style.setBackgroundColor( element, color );

      Style.setBackgroundColor( element, "transparent" );

      expect( TestUtil.getCssBackgroundColor( element ) ).toBeNull();
      if( Client.isTrident() ) {
        expect( element.style.backgroundColor ).toBe( "rgba(0, 0, 0, 0)" );
      }
    } );

    // see bug 428717
    it( "sets background gradient for color in webkit", function() {
      Style.setBackgroundColor( element, color );

      if( Client.isWebkit() ) {
        expect( TestUtil.getCssGradient( element ) ).toBe( webkitFlatGradient );
      } else {
        expect( TestUtil.getCssGradient( element ) ).toBe( "" );
      }
    } );

    it( "don't set two gradients in webkit", function() {
      Style.setBackgroundGradient( element, vGradientObject );

      Style.setBackgroundColor( element, color );

      var background = element.style.background;
      var firstGradient = background.indexOf( "gradient" );
      var lastGradient = background.lastIndexOf( "gradient" );
      expect( firstGradient ).toBe( lastGradient );
    } );

    it( "removes background gradient on webkit", function() {
      Style.setBackgroundColor( element, "#FF0000" );

      Style.setBackgroundColor( element, null );

      expect( TestUtil.getCssGradient( element ) ).toBe( "" );
    } );

    if( css3 ) {
      it( "does nothing if gradient is already set", function() {
        Style.setBackgroundGradient( element, vGradientObject );

        Style.setBackgroundColor( element, "#FF0000" );

        expect( TestUtil.getCssGradient( element ) ).toBe( vGradientString );
      } );
    }

  } );

  describe( "userSelect", function() {

    it( "is set with vendor prefix", function() {
      Style.setUserSelect( element, "none" );

      var userSelect =    element.style[ Style.VENDOR_PREFIX_PROPERTY + "UserSelect" ]
                       || element.style.userSelect;
      expect( userSelect ).toBe( "none" );
    } );

    it( "is get without vendor prefix", function() {
      Style.setUserSelect( element, "none" );

      expect( Style.getUserSelect( element ) ).toBe( "none" );
    } );

  } );

} );
