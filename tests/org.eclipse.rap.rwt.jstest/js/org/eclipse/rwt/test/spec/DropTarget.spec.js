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

describe( "DropTarget", function() {

  var control;

  beforeEach( function() {
    control = {
      addEventListener : jasmine.createSpy(),
      removeEventListener : jasmine.createSpy(),
      setDropDataTypes : jasmine.createSpy(),
      setSupportsDropMethod : jasmine.createSpy(),
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

    it( "assigns control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      expect( dropTarget.control ).toBe( control );
    } );

    it( "assigns actions", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [ "DROP_MOVE", "DROP_COPY" ] );

      expect( dropTarget.actions ).toEqual( { "move" : true, "copy" : true } );
    } );

    it( "registers listeners with control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      expect( control.addEventListener.calls.length ).toEqual( 4 );
      expect( control.addEventListener.calls[ 0 ].args[ 0 ] ).toEqual( "dragover" );
      expect( control.addEventListener.calls[ 1 ].args[ 0 ] ).toEqual( "dragmove" );
      expect( control.addEventListener.calls[ 2 ].args[ 0 ] ).toEqual( "dragout" );
      expect( control.addEventListener.calls[ 3 ].args[ 0 ] ).toEqual( "dragdrop" );
    } );

    it( "sets support drop method on control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      expect( control.setSupportsDropMethod ).toHaveBeenCalledWith( jasmine.any( Function ) );
    } );

  } );

  describe( "dispose", function() {

    it( "deregisters listeners from control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      dropTarget.dispose();

      expect( control.removeEventListener.calls.length ).toEqual( 4 );
      expect( control.removeEventListener.calls[ 0 ].args[ 0 ] ).toEqual( "dragover" );
      expect( control.removeEventListener.calls[ 1 ].args[ 0 ] ).toEqual( "dragmove" );
      expect( control.removeEventListener.calls[ 2 ].args[ 0 ] ).toEqual( "dragout" );
      expect( control.removeEventListener.calls[ 3 ].args[ 0 ] ).toEqual( "dragdrop" );
    } );

    it( "resets support drop method on control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      dropTarget.dispose();

      expect( control.setSupportsDropMethod.calls[ 1 ].args[ 0 ] ).toEqual( null );
    } );

    it( "clears drop data types on control", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      dropTarget.dispose();

      expect( control.setDropDataTypes ).toHaveBeenCalledWith( [] );
    } );

  } );

  describe( "setTransfer", function() {

    it( "sets data types", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );

      dropTarget.setTransfer( [ "foo", "bar" ] );

      expect( control.setDropDataTypes ).toHaveBeenCalledWith( [ "foo", "bar" ] );
    } );

  } );

} );
