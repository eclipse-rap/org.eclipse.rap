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

describe( "Variant", function() {

  var Variant = rwt.util.Variant;
  var variantsBuffer;

  beforeEach( function() {
    variantsBuffer = Variant.__variants;
    Variant.__variants = {};
  } );

  afterEach( function() {
    Variant.__variants = variantsBuffer;
  } );

  it( "stores a defined variant", function() {
    Variant.define( "foo", 23 );

    expect( Variant.get( "foo" ) ).toBe( 23 );
  } );

  it( "prevents re-definition of variants", function() {
    // Variants should not change at runtime, because the change would not affect code that is
    // already parsed.
    Variant.define( "foo", 23 );

    expect( function() {
      Variant.define( "foo", 42 );
    } ).toThrow( "Variant already defined: 'foo'" );
  } );

  describe( "get", function() {

    it( "throws error if variant does not exist", function() {
      expect( function() {
        Variant.get( "foo" );
      } ).toThrow();
    } );

  } );

  describe( "isSet", function() {

    it( "returns true on match, false on mismatch", function() {
      Variant.define( "status", "good" );

      expect( Variant.isSet( "status", "good" ) ).toBe( true );
      expect( Variant.isSet( "status", "bad" ) ).toBe( false );
    } );

    it( "supports disjunctive form (a|b)", function() {
      Variant.define( "status", "good" );

      expect( Variant.isSet( "status", "clear|good|perfect" ) ).toBe( true );
    } );

    it( "throws error if variant does not exist", function() {
      expect( function() {
        Variant.isSet( "foo", "something" );
      } ).toThrow();
    } );

  } );

  describe( "select", function() {

    it( "selects match", function() {
      Variant.define( "status", "good" );
      var expected = {};

      expect( Variant.select( "status", {
        "bad" : null,
        "good" : expected
      } ) ).toBe( expected );
    } );

    it( "selects default if no match found", function() {
      Variant.define( "status", "unclear" );
      var expected = {};

      expect( Variant.select( "status", {
        "bad" : null,
        "good" : null,
        "default" : expected
      } ) ).toBe( expected );
    } );

    it( "supports disjunctive form (a|b)", function() {
      Variant.define( "status", "good" );
      var expected = {};

      expect( Variant.select( "status", {
        "clear|good|perfect" : expected,
        "bad" : null
      } ) ).toBe( expected );
    } );

    it( "throws error if no match was found", function() {
      Variant.define( "status", "worse" );

      expect( function() {
        Variant.select( "status", {
          "good" : null,
          "bad" : null
        } );
      } ).toThrow();
    } );

    it( "throws error if variant does not exist", function() {
      expect( function() {
        Variant.select( "foo", { "default" : null } );
      } ).toThrow();
    } );

  } );

} );
