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

describe( "DropTargetHandler", function() {

  var control;
  var handler;

  beforeEach( function() {
    control = {
      addEventListener: function() {},
      setDropDataTypes : function() {},
      setSupportsDropMethod : function() {},
      toHashCode: function() {},
      getUserData: function() {},
      setUserData: function() {}
    };
    rwt.remote.ObjectRegistry.add( "some-id", control, null );
    handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.DropTarget" );
  } );

  afterEach( function() {
    rwt.remote.ObjectRegistry.remove( "some-id" );
  } );

  describe( "factory", function() {

    it( "creates DropTarget with given control", function() {
      var dropTarget = handler.factory( { control: "some-id", style: [] } );

      expect( dropTarget.control ).toBe( control );
    } );

    it( "creates DropTarget with given style", function() {
      var dropTarget = handler.factory( { control: "some-id", style: [ "DROP_MOVE" ] } );

      expect( dropTarget.actions ).toEqual( { "move" : true } );
    } );

  } );

  describe( "destructor", function() {

    it( "disposes the DropTarget", function() {
      var dropTarget = new rwt.widgets.DropTarget( control, [] );
      spyOn( dropTarget, "dispose" );

      handler.destructor( dropTarget );

      expect( dropTarget.dispose ).toHaveBeenCalled();
    } );

  } );

} );
