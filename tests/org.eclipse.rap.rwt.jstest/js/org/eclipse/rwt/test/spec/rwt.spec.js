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

/*global foo: false */

describe( "rwt", function() {

  describe( "define", function() {

    afterEach(function() {
      delete window.foo;
      delete window.bar;
    });

    it( "creates an object with the given name", function() {
      var object = {};

      rwt.define( "foo", object );

      expect( foo ).toBe( object );
    });

    it( "creates an object with a nested namespace", function() {
      var object = {};

      rwt.define( "foo.bar.baz", object );

      expect( foo.bar.baz ).toBe( object );
    });

    it( "does not overwrite an existing object", function() {
      var object = {};
      rwt.define( "foo.bar", object );

      rwt.define( "foo.bar" );

      expect( foo.bar ).toBe( object );
    });

    it( "sets name to an empty object if object parameter missing", function() {
      rwt.define( "foo.bar" );

      expect( foo.bar ).toEqual( {} );
    });

    it( "returns last name segment", function() {
      var result = rwt.define( "foo.bar", {} );

      expect( result ).toBe( "bar" );
    });

  });

});
