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

rwt.client.JavaScriptExecutor = function() {
  if( rwt.client.JavaScriptExecutor._instance !== undefined ) {
    throw new Error( "JavaScriptExecutor can not be created twice" );
  } else {
    rwt.client.JavaScriptExecutor._instance = this;
  }
};

rwt.client.JavaScriptExecutor.getInstance = function() {
  if( rwt.client.JavaScriptExecutor._instance === undefined ) {
    new rwt.client.JavaScriptExecutor();
  }
  return rwt.client.JavaScriptExecutor._instance;
};

rwt.client.JavaScriptExecutor.prototype = {

  execute : function( code ) {
    eval( code );
  }

};
