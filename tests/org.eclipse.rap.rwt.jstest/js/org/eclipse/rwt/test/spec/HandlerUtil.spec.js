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

});
