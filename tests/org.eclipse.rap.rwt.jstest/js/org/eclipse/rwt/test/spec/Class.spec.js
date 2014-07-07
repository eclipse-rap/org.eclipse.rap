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

/*global tests:true */
describe( "rwt.qx.Class", function() {

  var Class = rwt.qx.Class;
  var Mixin = rwt.qx.Mixin;

  afterEach( function() {
    delete window[ "tests" ];
  } );


  describe( ".define", function() {

    it( "includes members from config.members", function() {
      var f1 = function(){};
      var f2 = function(){};

      Class.define( "tests.Foo", {
        extend : Object,
        members : {
          "foo1" : f1,
          "foo2" : f2
        }
      } );
      var instance = new tests.Foo();

      expect( instance.foo1 ).toBe( f1 );
      expect( instance.foo2 ).toBe( f2 );
    } );

    it( "includes members from mixin members", function() {
      var f1 = function(){};
      var f2 = function(){};
      Mixin.define( "tests.FooMixin", {
        members : {
          "foo2" : f2
        }
      } );

      Class.define( "tests.Foo", {
        extend : Object,
        include : tests.FooMixin,
        members : {
          "foo1" : f1
        }
      } );
      var instance = new tests.Foo();

      expect( instance.foo1 ).toBe( f1 );
      expect( instance.foo2 ).toBe( f2 );
    } );

    it( "includes members from multiple mixins", function() {
      var f1 = function(){};
      var f2 = function(){};
      var f3 = function(){};
      Mixin.define( "tests.FooMixin1", {
        members : {
          "foo2" : f2
        }
      } );
      Mixin.define( "tests.FooMixin2", {
        members : {
          "foo3" : f3
        }
      } );

      Class.define( "tests.Foo", {
        extend : Object,
        include : [ tests.FooMixin1, tests.FooMixin2 ],
        members : {
          "foo1" : f1
        }
      } );
      var instance = new tests.Foo();

      expect( instance.foo1 ).toBe( f1 );
      expect( instance.foo2 ).toBe( f2 );
      expect( instance.foo3 ).toBe( f3 );
    } );

    it( "includes constructor from config.construct", function() {
      var spy = jasmine.createSpy();

      Class.define( "tests.Foo", {
        extend : Object,
        construct : spy
      } );
      var instance = new tests.Foo();

      expect( spy ).toHaveBeenCalled();
      expect( spy.calls[ 0 ].object ).toBe( instance ); // "this" at call time
    } );

    it( "includes constructor from mixin construct", function() {
      var spy1 = jasmine.createSpy();
      var spy2 = jasmine.createSpy();
      Mixin.define( "tests.FooMixin", {
        construct : spy1
      } );

      Class.define( "tests.Foo", {
        extend : Object,
        include : tests.FooMixin,
        construct : spy2
      } );
      var instance = new tests.Foo();

      expect( spy1 ).toHaveBeenCalled();
      expect( spy2 ).toHaveBeenCalled();
      expect( spy1.calls[ 0 ].object ).toBe( instance );
      expect( spy2.calls[ 0 ].object ).toBe( instance );
    } );

    it( "includes constructor from super class", function() {
      var log = [];
      Class.define( "tests.Bar", {
        extend : rwt.qx.Object, // provides the "base" function
        construct : function() {
          log.push( 1 );
        }
      } );

      Class.define( "tests.Foo", {
        extend : tests.Bar,
        construct : function() {
          this.base( arguments );
          log.push( 2 );
        }
      } );
      var instance = new tests.Foo();

      expect( log ).toEqual( [ 1, 2 ] );
    } );

    it( "includes destructor from config.destruct", function() {
      var spy = jasmine.createSpy();

      Class.define( "tests.Foo", {
        extend : rwt.qx.Object, // Class must extend rwt.qx.Object for dispose to exist
        destruct : spy
      } );
      var instance = new tests.Foo();
      instance.dispose();

      expect( spy ).toHaveBeenCalled();
      expect( spy.calls[ 0 ].object ).toBe( instance );
    } );

    it( "includes destructor from mixin destruct", function() {
      var spy1 = jasmine.createSpy();
      var spy2 = jasmine.createSpy();
      Mixin.define( "tests.FooMixin", {
        destruct : spy1
      } );

      Class.define( "tests.Foo", {
        extend : rwt.qx.Object,
        include : tests.FooMixin,
        destruct : spy2
      } );
      var instance = new tests.Foo();
      instance.dispose();

      expect( spy1 ).toHaveBeenCalled();
      expect( spy1.calls[ 0 ].object ).toBe( instance );
      expect( spy2 ).toHaveBeenCalled();
      expect( spy2.calls[ 0 ].object ).toBe( instance );
    } );

    it( "includes properties from config.properties", function() {
      Class.define( "tests.Foo", {
        extend : Object,
        properties : {
          foo : { "init" : 1 }
        }
      } );
      var instance = new tests.Foo();

      expect( instance.getFoo() ).toBe( 1 );
    } );

    it( "includes properties from mixin properties", function() {
      Mixin.define( "tests.FooMixin", {
        properties : {
          foo2 : { "init" : 2 }
        }
      } );

      Class.define( "tests.Foo", {
        extend : Object,
        include : tests.FooMixin,
        properties : {
          foo1 : { "init" : 1 }
        }
      } );
      var instance = new tests.Foo();

      expect( instance.getFoo1() ).toBe( 1 );
      expect( instance.getFoo2() ).toBe( 2 );
    } );

  } );

} );
