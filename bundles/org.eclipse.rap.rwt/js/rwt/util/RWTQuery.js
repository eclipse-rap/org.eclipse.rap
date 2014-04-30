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

/**
 * @private
 * @class An JQuery-like object which allows manipulation of HTML elements.
 * @exports rwt.util.RWTQuery as $
 * @description The constructor is not public. Instances can currently only be obtained from
 * {@link Widget#$el}.
 * @since 2.3
 */
rwt.util.RWTQuery = function( target ) {
  var isWidget = ( target.classname || "" ).indexOf( "rwt.widgets" ) === 0;
  this.__access = function( args, callbackWidget, callbackElement ) {
    var callback = isWidget ? callbackWidget : callbackElement;
    return callback.apply( this, [ target, args ] );
  };
};

rwt.util.RWTQuery.prototype = {


  /**
   * @description A method to either set or get the value of an HTML-attribute.
   * Note that the attributes "id" and "class" can not be set this way.
   * @param {string|Object} attribute The name of the attribute to return or modify. Alternatively
   * a plain object with key-value pairs to set.
   * @param {string} [value] The value to set the attribute to.
   * @return {string|$} The the value of the given attribute, if the function is called with a
   * string only. Otherwise a reference to this object.
   */
  attr : function() {
    return this.__access( arguments, attr_widget, attr_element );
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
    return widget.getHtmlAttributes()[ args[ 0 ] ];
  } else if( !restrictedAttributes[ args[ 0 ] ] ) {
    widget.setHtmlAttribute( args[ 0 ], args[ 1 ] );
  }
  return this;
} );

var attr_element = unwrapArgsFor( function( element, args ) {
  if( args.length === 1 ) {
    return element.getAttribute( args[ 0 ] ) || undefined;
  } else if( !restrictedAttributes[ args[ 0 ] ] ) {
    element.setAttribute( args[ 0 ], args[ 1 ] );
  }
  return this;
} );

var restrictedAttributes = {
  "id" : true, // RAP renders IDs. While it does not rely on them, addons and future versions may.
  "class" : true, // May be used by RAP in the future, separate API could allow access
  "style" : true // Would destroy layout, separate API could allow (limited) access
};

}());
