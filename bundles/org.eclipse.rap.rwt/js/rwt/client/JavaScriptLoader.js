/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "rwt.client" );

rwt.client.JavaScriptLoader = {

  load : function( params ) {
    for( var i = 0; i < params.files.length; i++ ) {
      this._loadFile( params.files[ i ] );
    }
  },

  _loadFile : function( file ) {
    var request = new rwt.remote.Request( file, "GET", "text/javascript" );
    request.setAsynchronous( false );
    request.setSuccessHandler( this._onLoad, this );
    request.send();
  },

  _onLoad : function( event ) {
    var scriptElement = document.createElement( "script" );
    scriptElement.type = "text/javascript";
    scriptElement.text = event.responseText;
    document.getElementsByTagName( "head" )[ 0 ].appendChild( scriptElement );
  }

};
