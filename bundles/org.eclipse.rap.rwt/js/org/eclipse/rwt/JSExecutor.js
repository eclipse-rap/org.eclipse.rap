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

namespace( "org.eclipse.rwt" );

org.eclipse.rwt.JSExecutor = function() {
  if( org.eclipse.rwt.JSExecutor._instance !== undefined ) {
    throw new Error( "JSExecutor can not be created twice" );
  } else {
    org.eclipse.rwt.JSExecutor._instance = this;
  }
};

org.eclipse.rwt.JSExecutor.getInstance = function() {
  if( org.eclipse.rwt.JSExecutor._instance === undefined ) {
    new org.eclipse.rwt.JSExecutor();
  }
  return org.eclipse.rwt.JSExecutor._instance;
};

org.eclipse.rwt.JSExecutor.prototype = {

  execute : function( code ) {
    eval( code );
  }

};