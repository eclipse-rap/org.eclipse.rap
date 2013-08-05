/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

// NOTE: To be loaded after all other ClientScripting classes
(function(){

  //////////////////////////
  // Attach to rap.getObject

  var getWrapperFor = rap._.getWrapperFor;
  rap._.getWrapperFor = function( obj ) {
    var result = getWrapperFor.call( rap._, obj );
    var PROXY_KEY = rwt.scripting.WidgetProxyFactory._PROXY_KEY;
    if( obj.getUserData( PROXY_KEY ) == null ) {
     rwt.scripting.WidgetProxyFactory._initWrapper( obj, result );
     obj.setUserData( PROXY_KEY, result );
    }
    return result;
  };

 }());
