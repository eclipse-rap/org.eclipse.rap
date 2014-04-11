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

namespace( "rwt.util" );

(function(){

rwt.util.RWTQuery = function( target ) {
  this.__access = function( args, callback ) {
    return callback.apply( this, [ target, args ] );
  };
};

rwt.util.RWTQuery.prototype = {

  attr : function() {
    return this.__access( arguments, attr_widget );
  }

};

var unwrapArgsFor = function( setter ) {
  return function( target, args ) {
    if( args.length === 1 && ( typeof args[ 0 ] === "object" ) ) {
      var map = args[ 0 ];
      for( var key in map ) {
        setter.apply( this, [ target, [ key, map[ key ] ] ] );
      }
      return this;
    } else {
      return setter.apply( this, arguments );
    }
  };
};

var attr_widget = unwrapArgsFor( function( widget, args ) {
  if( args.length === 1 ) {
    return widget.getHtmlAttributes()[ args[ 0 ] ] || "";
  } else if( !restrictedAttributes[ args[ 0 ] ] ) {
    widget.setHtmlAttribute( args[ 0 ], args[ 1 ] );
  }
  return this;
} );

var restrictedAttributes = {
  "id" : true, // RAP renders IDs. While it does not rely on them, addons and future versions may.
  "class" : true, // May be used by RAP in the future, separate API could allow access
  "style" : true // Would destroy layout, separate API could allow (limited) access
};

}());
