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

describe( "util.Colors", function() {

  var Colors = rwt.util.Colors;

  describe( "isValid", function() {

    it( "accepts named colors", function() {
      expect( Colors.isValid( "fuchsia" ) ).toBe( true );
    });

    it( "accepts #xxx style colors", function() {
      expect( Colors.isValid( "#f80" ) ).toBe( true );
    });

    it( "accepts #xxxxxx style colors", function() {
      expect( Colors.isValid( "#ff8000" ) ).toBe( true );
    });

    it( "accepts rgb(...) style colors", function() {
      expect( Colors.isValid( "rgb(255, 128, 0)" ) ).toBe( true );
    });

    it( "rejects unsupported strings", function() {
      expect( Colors.isValid( "unknown" ) ).toBe( false );
    });

  });

  describe( "stringToRgb", function() {

    it( "accepts named colors", function() {
      expect( Colors.stringToRgb( "fuchsia" ) ).toEqual( [255, 0, 255] );
    });

    it( "accepts #xxx style colors", function() {
      expect( Colors.stringToRgb( "#f80" ) ).toEqual( [255, 136, 0] );
    });

    it( "accepts #xxxxxx style colors", function() {
      expect( Colors.stringToRgb( "#ff8000" ) ).toEqual( [255, 128, 0] );
    });

    it( "accepts rgb(...) style colors", function() {
      expect( Colors.stringToRgb( "rgb(255, 128, 0)" ) ).toEqual( [255, 128, 0] );
    });

    it( "rejects unsupported strings", function() {
      expect( function() {
        Colors.stringToRgb( "unknown" );
      }).toThrow();
    });

  });

  describe( "rgbToRgbString", function() {

    it( "converts rgb array into rgb string", function() {
      expect( Colors.rgbToRgbString( [255, 128, 0] ) ).toEqual( "rgb(255,128,0)" );
    });

  });

  describe( "rgbToHexString", function() {

    it( "converts rgb array into hex6 string", function() {
      expect( Colors.rgbToHexString( [255, 128, 0] ) ).toEqual( "ff8000" );
    });

  });

});
