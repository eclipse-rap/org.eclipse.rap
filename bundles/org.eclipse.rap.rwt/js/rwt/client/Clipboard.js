/*******************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "rwt.client" );

(function() {

  rwt.client.Clipboard = {

    writeText: function( properties ) {
      var remoteObject = rap.getRemoteObject( this );
      if( navigator.clipboard && navigator.clipboard.writeText ) {
        navigator.clipboard.writeText( properties.text ).then( function() {
          remoteObject.call( "operationSucceeded", {
            "operation" : "writeText",
            "result" : ""
          } );
        }, function( error ) {
          remoteObject.call( "operationFailed", {
            "operation" : "writeText",
            "errorMessage" : error.message
          } );
        } );
      } else {
        remoteObject.call( "operationFailed", {
          "operation" : "writeText",
          "errorMessage" : "Clipboard operation writeText is not supported."
        } );
      }
    },

    readText: function() {
      var remoteObject = rap.getRemoteObject( this );
      if( navigator.clipboard && navigator.clipboard.readText ) {
        navigator.clipboard.readText().then( function( text ) {
          remoteObject.call( "operationSucceeded", {
            "operation" : "readText",
            "result" : text
          } );
        }, function( error ) {
          remoteObject.call( "operationFailed", {
            "operation" : "readText",
            "errorMessage" : error.message
          } );
        } );
      } else {
        remoteObject.call( "operationFailed", {
          "operation" : "readText",
          "errorMessage" : "Clipboard operation readText is not supported."
        } );
      }
    }

  };

})();
