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
  var SingletonObject = function() {};

  beforeEach( function() {
    Singletons.clear();
  } );

  describe( "get", function() {

    it( "should create an instance of a given type", function() {
      var instance = Singletons.get( SingletonObject );

      expect( instance instanceof SingletonObject ).toBe( true );
    } );

    it( "should always return the same instance", function() {
      var instance1 = Singletons.get( SingletonObject );
      var instance2 = Singletons.get( SingletonObject );

      expect( instance1 ).toBe( instance2 );
    } );

    it( "should return different instances for different types", function() {
      var Foo = function() {};
      var Bar = function() {};

      var foo = Singletons.get( Foo );
      var bar = Singletons.get( Bar );

      expect( foo instanceof Foo ).toBe( true );
      expect( bar instanceof Bar ).toBe( true );
    } );

  } );

  describe( "clear", function() {

    it( "should not overwrite instances for different types", function() {
      // This test fails if internal sequence counter is reset in clear().
      // Do not move Foo and Bar out of this function, because Singletons.get() attaches an id to it.
      var Foo = function() {};
      var Bar = function() {};
      Singletons.get( Foo );

      Singletons.clear();

      expect( Singletons.get( Bar ) instanceof Bar ).toBe( true );
      expect( Singletons.get( Foo ) instanceof Foo ).toBe( true );
    } );

    it( "should clear existing instances", function() {
      var instance1 = Singletons.get( SingletonObject );

      Singletons.clear();

      var instance2 = Singletons.get( SingletonObject );
      expect( instance1 ).not.toBe( instance2 );
    } );

  } );

} );
