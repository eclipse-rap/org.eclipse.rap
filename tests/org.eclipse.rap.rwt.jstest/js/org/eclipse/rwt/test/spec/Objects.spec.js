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

describe( "util.Objects", function() {

  var Objects = rwt.util.Objects;

  describe( "copy", function() {

    it( "creates equal object", function() {
      var obj = { "foo": 23, "bar": 42 };
      expect( Objects.copy( obj ) ).toEqual( obj );
    });

    it( "creates new instance", function() {
      var obj = { "foo": 23, "bar": 42 };
      expect( Objects.copy( obj ) ).not.toBe( obj );
    });

    it( "includes prototype", function() {
      var Foo = function() {};
      Foo.prototype = { "foo" : 23 };
      var obj = new Foo();
      obj.bar = 42;

      expect( Objects.copy( obj ) ).toEqual( { "foo": 23, "bar": 42 } );
    });

  });

  describe( "fromArray", function() {

    it( "empty for empty array", function() {
      expect( Objects.fromArray( [] ) ).toEqual( {} );
    });

    it( "includes strings, numeric, and boolean elements", function() {
      expect( Objects.fromArray( ["foo", 23, true] ) ).toEqual( { "foo": true,
                                                                  "23": true,
                                                                  "true": true } );
    });

  });

  describe( "isEmpty", function() {

    it( "true for emtpy object", function() {
      expect( Objects.isEmpty( {} ) ).toBe( true );
    });

    it( "false for non-empty object", function() {
      expect( Objects.isEmpty( { "" : 0 } ) ).toBe( false );
    });

    it( "checks prototype", function() {
      var Foo = function() {};
      Foo.prototype = { "foo" : 23 };

      expect( Objects.isEmpty( new Foo() ) ).toBe( false );
    });

  });

  describe( "getKeys", function() {

    it( "handles emtpy objects", function() {
      expect( Objects.getKeys( {} ) ).toEqual( [] );
    });

    it( "includes emtpy keys", function() {
      expect( Objects.getKeys( { "" : 0 } ) ).toEqual( [""] );
    });

    it( "includes prototype keys", function() {
      var Foo = function() {};
      Foo.prototype = { "foo" : 23 };
      var obj = new Foo();
      obj.bar = 42;

      expect( Objects.getKeys( obj ) ).toEqual( ["bar", "foo"] );
    });

    it( "includes shadowed keys", function() {
      var obj = { "foo": true, "toString" : true };

      expect( Objects.getKeys( obj ) ).toEqual( ["foo", "toString"] );
    });

  });

  describe( "getValues", function() {

    it( "handles emtpy objects", function() {
      expect( Objects.getValues( {} ) ).toEqual( [] );
    });

    it( "includes null values", function() {
      expect( Objects.getValues( { "" : null } ) ).toEqual( [null] );
    });

    it( "does not eliminate duplicate values", function() {
      expect( Objects.getValues( { "foo" : 23, "bar": 23 } ) ).toEqual( [23, 23] );
    });

    it( "includes prototype values", function() {
      var Foo = function() {};
      Foo.prototype = { "foo" : 23 };
      var obj = new Foo();
      obj.bar = 42;

      expect( Objects.getValues( obj ) ).toEqual( [42, 23] );
    });

  });

  describe( "mergeWith", function() {

    it( "handles emtpy objects", function() {
      var obj = {};
      expect( Objects.mergeWith( obj, {} ) ).toBe( obj );
      expect( obj ).toEqual( obj );
    });

    it( "overwrites values by default", function() {
      var obj = { "foo": 1, "bar": 1 };
      expect( Objects.mergeWith( obj, { "foo": 2 } ) ).toBe( obj );
      expect( obj ).toEqual( { "foo": 2, "bar": 1 } );
    });

    it( "does not overwrites values with overwrite = false", function() {
      var obj = { "foo": 1, "bar": 1 };
      expect( Objects.mergeWith( obj, { "foo": 2, "baz": 2 }, false ) ).toBe( obj );
      expect( obj ).toEqual( { "foo": 1, "bar": 1, "baz": 2 } );
    });

  });

});
