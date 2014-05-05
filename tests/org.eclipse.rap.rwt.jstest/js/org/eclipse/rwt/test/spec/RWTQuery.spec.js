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

describe( "RWTQuery", function() {

  var $ = function( target) {
    return new rwt.util.RWTQuery( target );
  };

  describe( "for widget:", function() {

    var widget;

    beforeEach( function() {
      widget = jasmine.createSpyObj( "widget", [ "setHtmlAttribute", "getHtmlAttributes" ] );
      widget.classname = "rwt.widgets.Foo";
    } );

    afterEach( function() {
      widget = null;
    } );

    describe( "attr", function() {

      it( "returns existing attribute", function() {
        widget.getHtmlAttributes.andReturn( { "foo" : "bar" } );
        expect( $( widget ).attr( "foo" ) ).toBe( "bar" );
      } );

      it( "returns undefined for non-existing attribute", function() {
        widget.getHtmlAttributes.andReturn( { } );
        expect( $( widget ).attr( "foo" ) ).toBeUndefined();
      } );

      it( "sets single attribute", function() {
        $( widget ).attr( "foo", "bar" );
        expect( widget.setHtmlAttribute ).toHaveBeenCalledWith( "foo", "bar" );
      } );

      it( "sets multiple attribute", function() {
        $( widget ).attr( {
          "foo" : "bar",
          "foo2" : "bar2"
        } );
        expect( widget.setHtmlAttribute ).toHaveBeenCalledWith( "foo", "bar" );
        expect( widget.setHtmlAttribute ).toHaveBeenCalledWith( "foo2", "bar2" );
      } );

      it( "is chainable as setter", function() {
        $( widget ).attr( "foo", "bar" ).attr( "foo2", "bar2" );

        expect( widget.setHtmlAttribute ).toHaveBeenCalledWith( "foo", "bar" );
        expect( widget.setHtmlAttribute ).toHaveBeenCalledWith( "foo2", "bar2" );
      } );

      it( "is chainable as multiple setter", function() {
        $( widget ).attr( { "foo" : "bar" } ).attr( { "foo2" : "bar2" } );

        expect( widget.setHtmlAttribute ).toHaveBeenCalledWith( "foo", "bar" );
        expect( widget.setHtmlAttribute ).toHaveBeenCalledWith( "foo2", "bar2" );
      } );

      it( "does not set forbidden attributes", function() {
        $( widget ).attr( "id", "foo" );
        $( widget ).attr( "style", "bar" );
        $( widget ).attr( "class", "foobar" );

        expect( widget.setHtmlAttribute ).not.toHaveBeenCalled();
      } );

    } );

  } );

  describe( "for element:", function() {

    var element;

    beforeEach( function() {
      element = document.createElement( "div" );
    } );

    afterEach( function() {
      element = null;
    } );

    describe( "attr", function() {

      it( "returns existing attribute", function() {
        element.setAttribute( "foo", "bar" );
        expect( $( element ).attr( "foo" ) ).toBe( "bar" );
      } );

      it( "returns undefined for non-existing attribute", function() {
        expect( $( element ).attr( "foo" ) ).toBeUndefined();
      } );

      it( "sets single attribute", function() {
        $( element ).attr( "foo", "bar" );
        expect( element.getAttribute( "foo" ) ).toBe( "bar" );
      } );

      it( "sets multiple attribute", function() {
        $( element ).attr( {
          "foo" : "bar",
          "foo2" : "bar2"
        } );

        expect( element.getAttribute( "foo" ) ).toBe( "bar" );
        expect( element.getAttribute( "foo2" ) ).toBe( "bar2" );
      } );

      it( "is chainable as setter", function() {
        $( element ).attr( "foo", "bar" ).attr( "foo2", "bar2" );

        expect( element.getAttribute( "foo" ) ).toBe( "bar" );
        expect( element.getAttribute( "foo2" ) ).toBe( "bar2" );
      } );

      it( "is chainable as multiple setter", function() {
        $( element ).attr( { "foo" : "bar" } ).attr( { "foo2" : "bar2" } );

        expect( element.getAttribute( "foo" ) ).toBe( "bar" );
        expect( element.getAttribute( "foo2" ) ).toBe( "bar2" );
      } );

      it( "does not set forbidden attributes", function() {
        $( element ).attr( "id", "foo" );
        $( element ).attr( "style", "bar" );
        $( element ).attr( "class", "foobar" );

        if( rwt.client.Client.isMshtml() ) {
          expect( element.getAttribute( "id" ) ).toBe( "" );
          expect( element.getAttribute( "style" ) ).not.toBe( "bar" );
        } else {
          expect( element.getAttribute( "id" ) ).toBeFalsy();
          expect( element.getAttribute( "style" ) ).toBeFalsy();
        }
        expect( element.getAttribute( "class" ) ).toBeFalsy();
      } );

    } );

  } );

} );
