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

describe( "HandlerUtil", function() {

  var HandlerUtil = rwt.remote.HandlerUtil;
  var ObjectRegistry = rwt.remote.ObjectRegistry;

  describe( "createStyleMap", function() {

    it( "creates object", function() {
      var result = HandlerUtil.createStyleMap( ["FOO", "BAR"] );

      expect( result ).toEqual( { "FOO": true, "BAR": true } );
    });

    it( "tolerates undefined parameter", function() {
      var result = HandlerUtil.createStyleMap();

      expect( result ).toEqual( {} );
    });

  });

  describe( "addStatesForStyles", function() {

    it( "adds states to widget", function() {
      var widget = mock( rwt.widgets.Composite );

      HandlerUtil.addStatesForStyles( widget, ["FOO", "BAR"] );

      expect( widget.addState ).toHaveBeenCalledWith( "rwt_FOO" );
      expect( widget.addState ).toHaveBeenCalledWith( "rwt_BAR" );
    });

    it( "tolerates undefined parameter", function() {
      var widget = mock( rwt.widgets.Composite );

      HandlerUtil.addStatesForStyles( widget );

      expect( widget.addState ).not.toHaveBeenCalled();
    });

  });

  describe( "callWithTargets", function() {

    afterEach( function(){
      org.eclipse.rwt.test.fixture.TestUtil.resetObjectRegistry();
    } );

    it( "calls callback with resolved array", function() {
      var objects = [ {}, {}, {} ];
      ObjectRegistry.add( "w10", objects[ 0 ] );
      ObjectRegistry.add( "w11", objects[ 1 ] );
      ObjectRegistry.add( "w12", objects[ 2 ] );
      var callback = jasmine.createSpy();

      HandlerUtil.callWithTargets( [ "w10", "w11", "w12" ], callback );

      var result = callback.calls[ 0 ].args[ 0 ];
      expect( result[ 0 ] ).toBe( objects[ 0 ] );
      expect( result[ 1 ] ).toBe( objects[ 1 ] );
      expect( result[ 2 ] ).toBe( objects[ 2 ] );
    });

    it( "calls callback after all objects are registered", function() {
      var objects = [ {}, {}, {} ];
      var callback = jasmine.createSpy();
      ObjectRegistry.add( "w10", objects[ 0 ] );

      HandlerUtil.callWithTargets( [ "w10", "w11", "w12" ], callback );
      ObjectRegistry.add( "w11", objects[ 1 ] );
      ObjectRegistry.add( "w12", objects[ 2 ] );

      var result = callback.calls[ 0 ].args[ 0 ];
      expect( result[ 0 ] ).toBe( objects[ 0 ] );
      expect( result[ 1 ] ).toBe( objects[ 1 ] );
      expect( result[ 2 ] ).toBe( objects[ 2 ] );
    });

  });

});
