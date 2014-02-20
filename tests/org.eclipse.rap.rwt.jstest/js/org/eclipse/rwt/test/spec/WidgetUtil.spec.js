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
