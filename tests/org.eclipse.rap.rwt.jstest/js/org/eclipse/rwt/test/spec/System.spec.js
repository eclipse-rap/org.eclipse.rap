/*******************************************************************************
 * Copyright (c) 2015, 2019 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

describe( "System", function() {

  var system = rwt.runtime.System.getInstance();

  describe( "parseQueryString", function() {

    it( "creates parameters map", function() {
      var parameters = system._parseQueryString( "param1=foo&param2=3" );

      expect( parameters.param1[ 0 ] ).toBe( "foo" );
      expect( parameters.param2[ 0 ] ).toBe( "3" );
    } );

    it( "works with duplicate parameters names", function() {
      var parameters = system._parseQueryString( "param1=foo&param2=bar&param1=baz" );

      expect( parameters.param1 ).toEqual( [ "foo", "baz" ] );
      expect( parameters.param2 ).toEqual( [ "bar" ] );
    } );

    it( "works with parameter without value", function() {
      var parameters = system._parseQueryString( "param1=" );

      expect( parameters.param1 ).toEqual( [ "" ] );
    } );

    it( "works with parameter without equal and value", function() {
      var parameters = system._parseQueryString( "param1" );

      expect( parameters.param1 ).toEqual( [ "" ] );
    } );

    it( "decodes parameter name", function() {
      var parameters = system._parseQueryString( "param%2F1=value1" );

      expect( parameters[ "param/1" ] ).toEqual( [ "value1" ] );
    } );

    it( "decodes parameter value", function() {
      var parameters = system._parseQueryString( "param1=value%2F1" );

      expect( parameters.param1 ).toEqual( [ "value/1" ] );
    } );

    it( "decodes parameter with + sign (space)", function() {
      var parameters = system._parseQueryString( "param+1=value+1%2B" );

      expect( parameters[ "param 1" ] ).toEqual( [ "value 1+" ] );
    } );

  } );

} );
