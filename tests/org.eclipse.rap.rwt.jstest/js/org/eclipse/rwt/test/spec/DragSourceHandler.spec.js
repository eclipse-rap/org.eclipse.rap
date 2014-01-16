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

describe( "DragSourceHandler", function() {

  var control;
  var handler;

  beforeEach( function() {
    control = {
      addEventListener: function() {},
      toHashCode: function() {},
      getUserData: function() {},
      setUserData: function() {}
    };
    rwt.remote.ObjectRegistry.add( "some-id", control, null );
    handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.DragSource" );
  } );

  afterEach( function() {
    rwt.remote.ObjectRegistry.remove( "some-id" );
  } );

  describe( "factory", function() {

    it( "creates DragSource with given control", function() {
      var dragSource = handler.factory( { control: "some-id", style: [] } );

      expect( dragSource.control ).toBe( control );
    } );

    it( "creates DragSource with given style", function() {
      var dragSource = handler.factory( { control: "some-id", style: [ "DROP_MOVE" ] } );

      expect( dragSource.actions ).toEqual( { "move" : true } );
    } );

  } );

  describe( "destructor", function() {

    it( "disposes the DragSource", function() {
      var dragSource = new rwt.widgets.DragSource( control, [] );
      spyOn( dragSource, "dispose" );

      handler.destructor( dragSource );

      expect( dragSource.dispose ).toHaveBeenCalled();
    } );

  } );

} );
