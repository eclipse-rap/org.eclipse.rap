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

describe( "util.Arrays", function() {

  var Arrays = rwt.util.Arrays;

  describe( "fromArguments", function() {

    it( "returns an array with all arguments", function() {
      var x = {};
      var result = (function(x, y, z) {
        return Arrays.fromArguments( arguments );
      })( "a", 23, x );
      expect( result ).toEqual( ["a", 23, x] );
    });

  });

  describe( "contains", function() {

    it( "true for contained element", function() {
      expect( Arrays.contains( [1, 2, 3], 2 ) ).toBe( true );
    });

    it( "false for missing element", function() {
      expect( Arrays.contains( [1, 2, 3], 4 ) ).toBe( false );
    });

  });

  describe( "copy", function() {

    it( "returns equal array", function() {
      var array = [1, 2, 3];
      expect( Arrays.copy( array ) ).toEqual( array );
    });

    it( "returns a new instance", function() {
      var array = [1, 2, 3];
      expect( Arrays.copy( array ) ).not.toBe( array );
    });

  });

  describe( "getFirst", function() {

    it( "returns first element", function() {
      expect( Arrays.getFirst( [1, 2, 3] ) ).toBe( 1 );
    });

    it( "returns undefined for empty array", function() {
      expect( Arrays.getFirst( [] ) ).toBeUndefined();
    });

  });

  describe( "getLast", function() {

    it( "returns last element", function() {
      expect( Arrays.getLast( [1, 2, 3] ) ).toBe( 3 );
    });

    it( "returns undefined for empty array", function() {
      expect( Arrays.getLast( [] ) ).toBeUndefined();
    });

  });

  describe( "insertAt", function() {

    it( "inserts at start", function() {
      var array = [1, 2, 3];
      expect( Arrays.insertAt( array, 'x', 0 ) ).toBe( array );
      expect( array ).toEqual( ['x', 1, 2, 3] );
    });

    it( "inserts at end", function() {
      var array = [1, 2, 3];
      expect( Arrays.insertAt( array, 'x', 3 ) ).toBe( array );
      expect( array ).toEqual( [1, 2, 3, 'x'] );
    });

    it( "inserts at end if index too large", function() {
      var array = [1, 2, 3];
      expect( Arrays.insertAt( array, 'x', 5 ) ).toBe( array );
      expect( array ).toEqual( [1, 2, 3, 'x'] );
    });

    it( "negative index counts from end", function() {
      var array = [1, 2, 3];
      expect( Arrays.insertAt( array, 'x', -1 ) ).toBe( array );
      expect( array ).toEqual( [1, 2, 'x', 3] );
    });

  });

  describe( "removeAt", function() {

    it( "removes first element", function() {
      var array = [1, 2, 3];
      expect( Arrays.removeAt( array, 0 ) ).toBe( 1 );
      expect( array ).toEqual( [2, 3] );
    });

    it( "removes last element", function() {
      var array = [1, 2, 3];
      expect( Arrays.removeAt( array, 2 ) ).toBe( 3 );
      expect( array ).toEqual( [1, 2] );
    });

    it( "does not change array if index too large", function() {
      var array = [1, 2, 3];
      expect( Arrays.removeAt( array, 3 ) ).toBeUndefined();
      expect( array ).toEqual( [1, 2, 3] );
    });

    it( "negative index counts from end", function() {
      var array = [1, 2, 3];
      expect( Arrays.removeAt( array, -2 ) ).toBe( 2 );
      expect( array ).toEqual( [1, 3] );
    });

  });

  describe( "remove", function() {

    var x = {};

    it( "removes first element", function() {
      var array = [x, 2, 3];
      expect( Arrays.remove( array, x ) ).toBe( x );
      expect( array ).toEqual( [2, 3] );
    });

    it( "removes last element", function() {
      var array = [1, 2, x];
      expect( Arrays.remove( array, x ) ).toBe( x );
      expect( array ).toEqual( [1, 2] );
    });

    it( "does not change array if not included", function() {
      var array = [1, 2, 3];
      expect( Arrays.remove( array, x ) ).toBeUndefined();
      expect( array ).toEqual( [1, 2, 3] );
    });

    it( "removes only first occurrence", function() {
      var array = [x, 2, x];
      expect( Arrays.remove( array, x ) ).toBe( x );
      expect( array ).toEqual( [2, x] );
    });

    it( "removes null", function() {
      var array = [1, null, 3];
      expect( Arrays.remove( array, null ) ).toBe( null );
      expect( array ).toEqual( [1, 3] );
    });

  });

});
