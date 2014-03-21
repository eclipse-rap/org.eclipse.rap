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

describe( "WidgetUtil", function() {

  var WidgetUtil = rwt.widgets.util.WidgetUtil;

  var widget;

  beforeEach( function() {
    widget = {
      addEventListener : jasmine.createSpy(),
      removeEventListener : jasmine.createSpy(),
      getElement : function() { return null; }
    };
  } );

  afterEach( function() {
    widget = null;
  } );

  describe( "getShell", function() {

    var shell;

    beforeEach( function() {
      shell = mock( rwt.widgets.Shell );
    } );

    it( "finds the shell if it is the parent", function() {
      widget.getParent = function(){ return shell; };

      expect( WidgetUtil.getShell( widget ) ).toBe( shell );
    } );

    it( "finds the shell if it is an indirect parent", function() {
      widget.getParent = function(){
        return {
          getParent : function() { return shell; }
        };
      };

      expect( WidgetUtil.getShell( widget ) ).toBe( shell );
    } );

    it( "returns null if there is no shell", function() {
      widget.getParent = function(){ return null; };

      expect( WidgetUtil.getShell( widget ) ).toBeNull();
    } );

  } );

  describe( "callWithElement", function() {

    var element = {};

    it( "calls callback with existing element", function() {
      widget.getElement = function() { return element; };
      var callback = jasmine.createSpy();

      WidgetUtil.callWithElement( widget, callback );

      expect( callback ).toHaveBeenCalledWith( element );
    } );

    it( "does not call callback if element does not exist", function() {
      var callback = jasmine.createSpy();

      WidgetUtil.callWithElement( widget, callback );

      expect( callback ).not.toHaveBeenCalled();
    } );

    it( "registers a create listener", function() {
      var callback = jasmine.createSpy();

      WidgetUtil.callWithElement( widget, callback );

      expect( widget.addEventListener ).toHaveBeenCalledWith( "create", jasmine.any( Function ) );
    } );

    it( "calls callback on a create event", function() {
      var callback = jasmine.createSpy();
      WidgetUtil.callWithElement( widget, callback );
      var listener = widget.addEventListener.mostRecentCall.args[ 1 ];

      widget.getElement = function() { return element; };
      listener();

      expect( callback ).toHaveBeenCalledWith( element );
    } );

    it( "removes listener after create", function() {
      WidgetUtil.callWithElement( widget, jasmine.createSpy() );
      var listener = widget.addEventListener.mostRecentCall.args[ 1 ];

      listener();

      expect( widget.removeEventListener ).toHaveBeenCalledWith( "create", listener );
    } );

  } );

} );
