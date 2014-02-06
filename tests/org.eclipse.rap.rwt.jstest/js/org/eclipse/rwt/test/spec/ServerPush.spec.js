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

describe( "ServerPush", function() {

  var serverPush;

  beforeEach( function() {
    serverPush = new rwt.client.ServerPush();
  } );

  it( "should be inactive by default", function() {
    expect( serverPush._active ).toBeFalsy();
  } );

  it( "can be activated", function() {
    serverPush.setActive( true );

    expect( serverPush._active ).toBe( true );
  } );

  describe( "getInstance", function() {

    it( "should return the same instance", function() {
      var instance = rwt.client.ServerPush.getInstance();

      expect( instance ).toBe( rwt.runtime.Singletons.get( rwt.client.ServerPush ) );
    } );

  } );

} );
