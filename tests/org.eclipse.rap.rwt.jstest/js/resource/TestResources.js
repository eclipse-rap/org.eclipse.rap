/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

( function() {

  var testResources = [
    "org/eclipse/rwt/test/fixture/RAPRequestPatch.js",
    "org/eclipse/rwt/test/fixture/DummyRequest.js",
    "org/eclipse/rwt/test/fixture/RAPServer.js",
    "org/eclipse/rwt/test/Presenter.js",
    "org/eclipse/rwt/test/TestRunner.js",
    "org/eclipse/rwt/test/fixture/TestUtil.js",
    "org/eclipse/rwt/test/Asserts.js",
    "org/eclipse/rwt/test/Startup.js",
    "resource/RAPThemeSupport.js"
  ];

  var include = function( src ) {
    var output = [];
    output.push( '<script src="' );
    output.push( src );
    output.push( '" type="text/javascript"></script>' );
    document.write( output.join( "" ) );
  };

  for( var i = 0; i < testResources.length; i++ ) {
    include( "./test-resources/" + testResources[ i ] );
  }

} )();
