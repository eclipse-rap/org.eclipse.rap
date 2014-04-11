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

describe( "Singletons", function() {

  var Singletons = rwt.runtime.Singletons;
  var Foo = function() {};
  var Bar = function() {};

  var holder = Singletons._holder;

  beforeEach( function() {
    Singletons.clear();
  } );

  afterEach( function() {
   Singletons._holder = holder; // do not destroy work from Fixture.setup()
  } );

  describe( "get", function() {

    it( "should create an instance of a given type", function() {
      var instance = Singletons.get( Foo );

      expect( instance instanceof Foo ).toBe( true );
    } );

    it( "should always return the same instance", function() {
      var instance1 = Singletons.get( Foo );
      var instance2 = Singletons.get( Foo );

      expect( instance1 ).toBe( instance2 );
    } );

    it( "should return different instances for different types", function() {
      var foo = Singletons.get( Foo );
      var bar = Singletons.get( Bar );

      expect( foo instanceof Foo ).toBe( true );
      expect( bar instanceof Bar ).toBe( true );
    } );

  } );

  describe( "clear", function() {

    it( "should not overwrite instances for different types", function() {
      // This test fails if internal sequence counter is reset in clear().
      // Do not move Abc and Xyz out of this function, because Singletons.get() attaches an id to it.
      var Abc = function() {};
      var Xyz = function() {};
      Singletons.get( Abc );

      Singletons.clear();

      expect( Singletons.get( Xyz ) instanceof Xyz ).toBe( true );
      expect( Singletons.get( Abc ) instanceof Abc ).toBe( true );
    } );

    it( "should clear all existing instances", function() {
      var fooInstance = Singletons.get( Foo );
      var barInstance = Singletons.get( Bar );

      Singletons.clear();

      expect( Singletons.get( Foo ) ).not.toBe( fooInstance );
      expect( Singletons.get( Bar ) ).not.toBe( barInstance );
    } );

    it( "should clear only existing instance for a given type", function() {
      var fooInstance = Singletons.get( Foo );
      var barInstance = Singletons.get( Bar );

      Singletons.clear( Foo );

      expect( Singletons.get( Foo ) ).not.toBe( fooInstance );
      expect( Singletons.get( Bar ) ).toBe( barInstance );
    } );

  } );

} );
