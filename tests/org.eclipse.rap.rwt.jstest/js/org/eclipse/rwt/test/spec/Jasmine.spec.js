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

describe( "Jasmine", function() {

  describe( "any", function() {

    it( "checks for instance of constructor", function() {
      var mySpy = jasmine.createSpy();
      var Foo = function(){};
      mySpy( new Foo() );

      expect( mySpy ).not.toHaveBeenCalledWith( Foo );
      expect( mySpy ).toHaveBeenCalledWith( any( Foo ) );
    } );

  } );

  describe( "same", function() {

    it( "checks for same instance", function() {
      var mySpy = jasmine.createSpy();
      var foo = {};
      mySpy( foo );

      expect( mySpy ).toHaveBeenCalledWith( {} );
      expect( mySpy ).toHaveBeenCalledWith( foo );
      expect( mySpy ).not.toHaveBeenCalledWith( same( {} ) );
      expect( mySpy ).toHaveBeenCalledWith( same( foo ) );
    } );

  } );

  describe( "mock", function() {

    it( "creates mock for given constructor with prototype", function() {
      var Foo = function(){};
      Foo.prototype.bar = function(){};

      var myMock = mock( Foo );

      expect( jasmine.isSpy( myMock.bar ) ).toBeTruthy();
    } );

    it( "names spies with default mock name", function() {
      var Foo = function(){};
      Foo.prototype.bar = function(){};

      var myMock = mock( Foo );

      expect( myMock.bar.identity ).toBe( "mock.bar" );
    } );

    it( "names spies with given constructor name", function() {
      var Foo = function(){};
      Foo.prototype.bar = function(){};

      var myMock = mock( Foo, "Foo" );

      expect( myMock.bar.identity ).toBe( "Foo.bar" );
    } );

  } );

} );
