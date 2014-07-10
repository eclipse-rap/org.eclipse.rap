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
    Singletons._holder = {};
  } );

  afterEach( function() {
   Singletons._holder = holder; // do not destroy work from Fixture.setup()
  } );

  describe( "get", function() {

    it( "creates an instance of a given type", function() {
      var instance = Singletons.get( Foo );

      expect( instance instanceof Foo ).toBe( true );
    } );

    it( "always returns the same instance", function() {
      var instance1 = Singletons.get( Foo );
      var instance2 = Singletons.get( Foo );

      expect( instance1 ).toBe( instance2 );
    } );

    it( "returns different instances for different types", function() {
      var foo = Singletons.get( Foo );
      var bar = Singletons.get( Bar );

      expect( foo instanceof Foo ).toBe( true );
      expect( bar instanceof Bar ).toBe( true );
    } );

  } );

  describe( "clear", function() {

    it( "does not overwrite instances for different types", function() {
      // This test fails if internal sequence counter is reset in clear().
      // Do not move Abc and Xyz out of this function, because Singletons.get() attaches an id to it.
      var Abc = function() {};
      var Xyz = function() {};
      Singletons.get( Abc );

      Singletons.clear();

      expect( Singletons.get( Xyz ) ).toEqual( any( Xyz ) );
      expect( Singletons.get( Abc ) ).toEqual( any( Abc ) );
    } );

    it( "clears all existing instances", function() {
      var fooInstance = Singletons.get( Foo );
      var barInstance = Singletons.get( Bar );

      Singletons.clear();

      expect( Singletons.get( Foo ) ).not.toBe( fooInstance );
      expect( Singletons.get( Bar ) ).not.toBe( barInstance );
    } );

    it( "clears only existing instance for a given type", function() {
      var fooInstance = Singletons.get( Foo );
      var barInstance = Singletons.get( Bar );

      Singletons.clear( Foo );

      expect( Singletons.get( Foo ) ).not.toBe( fooInstance );
      expect( Singletons.get( Bar ) ).toBe( barInstance );
    } );

    it( "calls dispose if it exists", function() {
      var instance = Singletons.get( Foo );
      instance.dispose = jasmine.createSpy( "dispose" );

      Singletons.clear( Foo );

      expect( instance.dispose ).toHaveBeenCalled();
    } );

    it( "calls destroy instead of dispose if it exists", function() {
      var instance = Singletons.get( Foo );
      instance.dispose = jasmine.createSpy( "dispose" );
      instance.destroy = jasmine.createSpy( "destroy" );

      Singletons.clear( Foo );

      expect( instance.dispose ).not.toHaveBeenCalled();
      expect( instance.destroy ).toHaveBeenCalled();
    } );

    it( "calls dispose even if type is not cleared explicitly", function() {
      var instance = Singletons.get( Foo );
      instance.dispose = jasmine.createSpy( "dispose" );

      Singletons.clear();

      expect( instance.dispose ).toHaveBeenCalled();
    } );

  } );

  describe( "_clearExcept", function() {

    it( "clears all instances except given types", function() {
      var foo = Singletons.get( Foo );
      var bar = Singletons.get( Bar );

      Singletons._clearExcept( [ Foo ] );

      expect( Singletons.get( Foo ) ).toBe( foo );
      expect( Singletons.get( Bar ) ).not.toBe( bar );
    } );

  } );

} );
