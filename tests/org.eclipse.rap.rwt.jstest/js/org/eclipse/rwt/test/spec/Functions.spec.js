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

describe( "util.Functions", function() {

  var Functions = rwt.util.Functions;

  describe( "bind", function() {

    var context = {};

    it( "creates wrapper that is bound to context", function() {
      var fn = function() {
        return this;
      };

      var wrapper = Functions.bind( fn, context );

      expect( wrapper ).not.toBe( fn );
      expect( wrapper() ).toBe( context );
    });

    it( "wrapper passes arguments to function", function() {
      var fn = jasmine.createSpy();

      var wrapper = Functions.bind( fn, context );

      wrapper( 23, 42 );
      expect( fn ).toHaveBeenCalledWith( 23, 42 );
    });

  });

});
