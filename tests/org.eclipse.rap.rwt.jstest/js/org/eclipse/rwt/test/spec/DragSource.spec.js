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

describe( "DragSource", function() {

  var control;

  beforeEach( function() {
    control = {
      addEventListener : jasmine.createSpy(),
      removeEventListener : jasmine.createSpy(),
      toHashCode : function() {},
      getUserData : function() {},
      setUserData : function() {}
    };
    rwt.remote.ObjectRegistry.add( "some-id", control, null );
  } );

  afterEach( function() {
    rwt.remote.ObjectRegistry.remove( "some-id" );
  } );

  describe( "constructor", function() {

    it( "assigns parameters", function() {
      var dragSource = new rwt.widgets.DragSource( control, [] );

      expect( dragSource.control ).toBe( control );
      expect( dragSource.dataTypes ).toEqual( [] );
    } );

    it( "assigns actions", function() {
      var dragSource = new rwt.widgets.DragSource( control, [ "DROP_MOVE", "DROP_COPY" ] );

      expect( dragSource.actions ).toEqual( { "move" : true, "copy" : true } );
    } );

    it( "registers listeners with control", function() {
      var dragSource = new rwt.widgets.DragSource( control, [] );

      expect( control.addEventListener.calls.length ).toEqual( 2 );
      expect( control.addEventListener.calls[ 0 ].args[ 0 ] ).toEqual( "dragstart" );
      expect( control.addEventListener.calls[ 1 ].args[ 0 ] ).toEqual( "dragend" );
    } );

  } );

  describe( "dispose", function() {

    it( "deregisters listeners from control", function() {
      var dragSource = new rwt.widgets.DragSource( control, [] );

      dragSource.dispose();

      expect( control.removeEventListener.calls.length ).toEqual( 2 );
      expect( control.removeEventListener.calls[ 0 ].args[ 0 ] ).toEqual( "dragstart" );
      expect( control.removeEventListener.calls[ 1 ].args[ 0 ] ).toEqual( "dragend" );
    } );

  } );

  describe( "setTransfer", function() {

    it( "sets data types", function() {
      var dragSource = new rwt.widgets.DragSource( control, [] );

      dragSource.setTransfer( [ "foo", "bar" ] );

      expect( dragSource.dataTypes ).toEqual( [ "foo", "bar" ] );
    } );

  } );

} );
