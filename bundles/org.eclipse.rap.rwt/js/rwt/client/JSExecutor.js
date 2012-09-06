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

rwt.client.JSExecutor = function() {
  if( rwt.client.JSExecutor._instance !== undefined ) {
    throw new Error( "JSExecutor can not be created twice" );
  } else {
    rwt.client.JSExecutor._instance = this;
  }
};

rwt.client.JSExecutor.getInstance = function() {
  if( rwt.client.JSExecutor._instance === undefined ) {
    new rwt.client.JSExecutor();
  }
  return rwt.client.JSExecutor._instance;
};

rwt.client.JSExecutor.prototype = {

  execute : function( code ) {
    eval( code );
  }

};
