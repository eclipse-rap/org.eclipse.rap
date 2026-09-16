/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Kyle Smith - Add evaluate method
 ******************************************************************************/

namespace( "rwt.client" );

rwt.client.JavaScriptExecutor = function() {

  this.execute = function( code ) {
    eval( code );
  };

  this.evaluate = function( futureId, code ) {
    const remote = rwt.remote.Connection.getInstance().getRemoteObject( this );
    const retval = eval( code );
    remote.call( "complete", {
        futureId : futureId,
        retval: retval
    } );
  };

};

rwt.client.JavaScriptExecutor.getInstance = function() {
  return rwt.runtime.Singletons.get( rwt.client.JavaScriptExecutor );
};
