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

describe( "util.Numbers", function() {

  var Numbers = rwt.util.Numbers;

  describe( "isNumber", function() {

    it( "accepts number", function() {
      expect( Numbers.isNumber( 3.14 ) ).toBe( true );
    });

    it( "accepts Infinity", function() {
      expect( Numbers.isNumber( Infinity ) ).toBe( true );
    });

    it( "rejects NaN", function() {
      expect( Numbers.isNumber( NaN ) ).toBe( false );
    });

    it( "rejects strings", function() {
      expect( Numbers.isNumber( "3.14" ) ).toBe( false );
    });

  });

  describe( "isBetween", function() {

    it( "accepts number between bounds", function() {
      expect( Numbers.isBetween( 2, 1, 3 ) ).toBe( true );
    });

    it( "rejects number equal to lower bound", function() {
      expect( Numbers.isBetween( 1, 1, 2 ) ).toBe( false );
    });

    it( "rejects number equal to upper bound", function() {
      expect( Numbers.isBetween( 2, 1, 2 ) ).toBe( false );
    });

  });

  describe( "limit", function() {

    it( "limits number to lower bound", function() {
      expect( Numbers.limit( 0, 1, 2 ) ).toBe( 1 );
    });

    it( "limits number to upper bound", function() {
      expect( Numbers.limit( 3, 1, 2 ) ).toBe( 2 );
    });

    it( "accepts -Infinity as lower bound", function() {
      expect( Numbers.limit( 1, -Infinity, 0 ) ).toBe( 0 );
    });

    it( "accepts Infinity as upper bound", function() {
      expect( Numbers.limit( -1, 0, Infinity ) ).toBe( 0 );
    });

    it( "does not change number between bounds", function() {
      expect( Numbers.limit( 2, 1, 3 ) ).toBe( 2 );
    });

    it( "does not change NaN", function() {
      expect( isNaN( Numbers.limit( NaN, 0, 1 ) ) ).toBe( true );
    });

  });

});
