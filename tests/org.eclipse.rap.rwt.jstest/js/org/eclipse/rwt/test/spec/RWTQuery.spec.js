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

  var $ = rwt.util.RWTQuery;
  var WidgetUtil = rwt.widgets.util.WidgetUtil;
  var Style = rwt.html.Style;

  describe( "for widget:", function() {

    var widget;

    beforeEach( function() {
      widget = jasmine.createSpyObj( "widget", [
        "setHtmlAttribute",
        "getHtmlAttributes",
        "setStyleProperty",
        "getStyleProperties"
      ] );
      widget.classname = "rwt.widgets.Foo";
      spyOn( WidgetUtil, "callWithElement" );
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

    describe( "css", function() {

      it( "returns existing property", function() {
        widget.getStyleProperties.andReturn( { "foo" : "bar" } );
        expect( $( widget ).css( "foo" ) ).toBe( "bar" );
      } );

      it( "returns undefined for non-existing property", function() {
        widget.getStyleProperties.andReturn( {} );
        expect( $( widget ).css( "foo" ) ).toBeUndefined();
      } );

      it( "sets single property", function() {
        $( widget ).css( "foo", "bar" );
        expect( widget.setStyleProperty ).toHaveBeenCalledWith( "foo", "bar" );
      } );

      it( "sets multiple property", function() {
        $( widget ).css( {
          "foo" : "bar",
          "foo2" : "bar2"
        } );
        expect( widget.setStyleProperty ).toHaveBeenCalledWith( "foo", "bar" );
        expect( widget.setStyleProperty ).toHaveBeenCalledWith( "foo2", "bar2" );
      } );

      it( "is chainable as setter", function() {
        $( widget ).css( "foo", "bar" ).css( "foo2", "bar2" );

        expect( widget.setStyleProperty ).toHaveBeenCalledWith( "foo", "bar" );
        expect( widget.setStyleProperty ).toHaveBeenCalledWith( "foo2", "bar2" );
      } );

      it( "is chainable as multiple setter", function() {
        $( widget ).css( { "foo" : "bar" } ).css( { "foo2" : "bar2" } );

        expect( widget.setStyleProperty ).toHaveBeenCalledWith( "foo", "bar" );
        expect( widget.setStyleProperty ).toHaveBeenCalledWith( "foo2", "bar2" );
      } );

    } );

    describe( "append", function() {

      it( "appends an element using WidgetUtil", function() {
        var element = document.createElement( "div" );
        var parentElement = document.createElement( "div" );

        $( widget ).append( element );
        WidgetUtil.callWithElement.calls[ 0 ].args[ 1 ]( parentElement );

        expect( element.parentElement ).toBe( parentElement );
      } );

      it( "isChainable", function() {
        var element = document.createElement( "div" );
        var $widget = $( widget );

        expect( $widget.append( element ) ).toBe( $widget );
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

        expect( element.getAttribute( "id" ) ).toBeFalsy();
        expect( element.getAttribute( "style" ) ).toBeFalsy();
        expect( element.getAttribute( "class" ) ).toBeFalsy();
      } );

    } );

    describe( "css", function() {

      it( "returns existing property using rwt.html.Style", function() {
        spyOn( Style, "get" ).andReturn( "bar" );

        expect( $( element ).css( "foo" ) ).toBe( "bar" );
        expect( Style.get ).toHaveBeenCalledWith( element, "foo" );
      } );

      it( "sets single property", function() {
        $( element ).css( "left", "11px" );
        expect( element.style.left ).toBe( "11px" );
      } );

      it( "sets multiple properties", function() {
        $( element ).css( { "left" : "11px", "top" : "12px" } );

        expect( element.style.left ).toBe( "11px" );
        expect( element.style.top ).toBe( "12px" );
      } );

      it( "is chainable as setter", function() {
        $( element ).css( "left", "11px" ).css( "top", "12px" );

        expect( element.style.left ).toBe( "11px" );
        expect( element.style.top ).toBe( "12px" );
      } );

      it( "is chainable as multiple setter", function() {
        $( element ).css( { "left" : "11px" } ).css( { "top" : "12px" } );

        expect( element.style.left ).toBe( "11px" );
        expect( element.style.top ).toBe( "12px" );
      } );

      it( "converts numbers to pixel", function() {
        $( element ).css( "left", 11 );
        expect( element.style.left ).toBe( "11px" );
      } );

      it( "does not convert numbers to pixel if property is a cssNumber", function() {
        $( element ).css( "opacity", 0.4 );
        expect( element.style.opacity ).toBe( "0.4" );
      } );

      it( "uses cssHooks setter", function() {
        var spy = jasmine.createSpy();
        $.cssHooks[ "foo" ] = { "set" : spy };

        $( element ).css( "foo", "bar" );

        expect( spy ).toHaveBeenCalledWith( element, "bar" );
        delete $.cssHooks.foo;
      } );

    } );

    describe( "cssHooks", function() {

      it( "delegate set backgroundColor", function() {
        spyOn( rwt.html.Style, "setBackgroundColor" );

        $( element ).css( "backgroundColor", "#ff00ee" );

        expect( rwt.html.Style.setBackgroundColor ).toHaveBeenCalledWith( element, "#ff00ee" );
      } );

      it( "delegate set backgroundGradient", function() {
        spyOn( rwt.html.Style, "setBackgroundGradient" );
        var gradient = [];

        $( element ).css( "backgroundGradient", gradient );

        expect( rwt.html.Style.setBackgroundGradient ).toHaveBeenCalledWith( element, gradient );
      } );

      it( "delegate set backgroundImage", function() {
        spyOn( rwt.html.Style, "setBackgroundImage" );

        $( element ).css( "backgroundImage", "foo" );

        expect( rwt.html.Style.setBackgroundImage ).toHaveBeenCalledWith( element, "foo" );
      } );

    } );

    describe( "append", function() {

      it( "appends an element", function() {
        var childElement = document.createElement( "div" );

        $( element ).append( childElement );

        expect( childElement.parentElement ).toBe( element );
      } );

      it( "isChainable", function() {
        var childElement = document.createElement( "div" );
        var $element = $( element );

        expect( $element.append( childElement ) ).toBe( $element );
      } );

    } );

  } );


} );
