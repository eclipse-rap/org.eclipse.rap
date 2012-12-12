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

rwt.client.URLLauncher = function() {
  if( rwt.client.URLLauncher._instance !== undefined ) {
    throw new Error( "URLLauncher can not be created twice" );
  } else {
    rwt.client.URLLauncher._instance = this;
  }
  this._window = window;
  var iframe = document.createElement( "iframe" );
  iframe.style.visibility = "hidden";
  iframe.style.position = "absolute";
  iframe.style.left = "-1000px";
  iframe.style.top = "-1000px";
  iframe.src = rwt.remote.Server.RESOURCE_PATH + "static/html/blank.html";
  document.body.appendChild( iframe );
  this._iframe = iframe;
};

rwt.client.URLLauncher.getInstance = function() {
  if( rwt.client.URLLauncher._instance === undefined ) {
    new rwt.client.URLLauncher();
  }
  return rwt.client.URLLauncher._instance;
};

rwt.client.URLLauncher.prototype = {

  openURL : function( url ) {
    var protocol = this.getProtocol( url );
    try {
      if( [ "http:", "https:", "ftp:", "ftps:" ].indexOf( protocol ) !== -1 ) {
        this._window.open( url, "_blank" );
      } else {
        this._iframe.src = url;
      }
    } catch( ex ) {
      // IE may throw security exception even if the user allows the popup
    }
  },

  getProtocol : function( url ) {
    var protocol = url.indexOf( ":" ) !== -1 ? url.split( ":" )[ 0 ] + ":" : location.protocol;
    return protocol.toLowerCase();
  }

}