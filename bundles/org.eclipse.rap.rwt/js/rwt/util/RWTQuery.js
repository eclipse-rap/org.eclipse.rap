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

var Style = rwt.html.Style;

/**
 * @private
 * @class An JQuery-like object which allows manipulation of HTML elements.
 * @exports rwt.util.RWTQuery as $
 * @description The constructor is not public. Instances can currently only be obtained from
 * {@link Widget#$el}.
 * @since 2.3
 */
rwt.util.RWTQuery = function( target ) {
  return new rwt.util.RWTQuery.fn.init( target );
};

var $ = rwt.util.RWTQuery;

$.prototype = {

  init : function( target ) {
    var isWidget = ( target.classname || "" ).indexOf( "rwt.widgets" ) === 0;
    this.__access = function( args, callbackWidget, callbackElement ) {
      var callback = isWidget ? callbackWidget : callbackElement;
      return callback.apply( this, [ target, args ] );
    };
  },

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
  },

  append : function() {
    return this.__access( arguments, append_widget, append_element );
  },

  css : function() {
    return this.__access( arguments, css_widget, css_element );
  },

  text : function() {
    return this.__access( arguments, null, text_element );
  },

  detach : function() {
    return this.__access( arguments, null, detach_element );
  }

};

$.fn = $.prototype; // for extendability
$.fn.init.prototype = $.prototype; // for creation without "new"

// TODO: these hooks are for element only, widgets would need separate ones
// TODO: have getter, allow for standard syntax on image & gradient
$.cssHooks = {
  "backgroundColor" : {
    "set" : function( element, value ) {
      rwt.html.Style.setBackgroundColor( element, value );
    }
  },
  "backgroundImage" : { // standard syntax URL("...") not supported
    "set" : function( element, value ) {
      rwt.html.Style.setBackgroundImage( element, value );
    }
  },
  "backgroundGradient" : { // standard syntax linear-gradient(...) not supported
    "set" : function( element, value ) {
      rwt.html.Style.setBackgroundGradient( element, value );
    }
  },
  "border" : {
    "set" : function( element, value ) {
      if( typeof value === "object" && value.renderElement ) {
        value.renderElement( element );
      } else {
        element.style.border = value;
      }
    }
  },
  "font" : {
    "set" : function( element, value ) {
      if( typeof value === "object" && value.renderElement ) {
        value.renderElement( element );
      } else {
        element.style.font = value;
      }
    }
  }
};

$.cssNumber = {
  "columnCount" : true,
  "fillOpacity" : true,
  "fontWeight" : true,
  "lineHeight" : true,
  "opacity" : true,
  "zIndex" : true,
  "zoom" : true
};

var unwrapArgsFor = function( originalSetter ) {
  return function( target, args ) {
    if( args.length === 1 && ( typeof args[ 0 ] === "object" ) ) {
      var map = args[ 0 ];
      for( var key in map ) {
        originalSetter.apply( this, [ target, [ key, map[ key ] ] ] );
      }
      return this;
    }
    return originalSetter.apply( this, arguments );
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

var css_widget = unwrapArgsFor( function( widget, args ) {
  // TODO: some of properties such as bounds and backgrounds should be applied to the widget itself
  if( args.length === 1 ) {
    return widget.getStyleProperties()[ args[ 0 ] ];
  }
  widget.setStyleProperty( args[ 0 ], args[ 1 ] );
  return this;
} );

var css_element = unwrapArgsFor( function( element, args ) {
  var hooks = $.cssHooks[ args[ 0 ] ];
  if( args.length === 1 ) {
    return Style.get( element, args[ 0 ] );
  }
  if( hooks && hooks.set ) {
    hooks.set( element, args[ 1 ] );
  }
  element.style[ args[ 0 ] ] = parseCssValue( args );
  return this;
} );

var append_widget = function( widget, args ) {
  if( !widget.getElement() ) {
    rwt.widgets.base.Widget.removeFromGlobalElementQueue( widget );
    widget._createElementImpl();
  }
  widget.getElement().appendChild( args[ 0 ] );
  return this;
};

var append_element = function( element, args ) {
  element.appendChild( args[ 0 ] );
  return this;
};

var detach_element = function( element ) {
  element.parentNode.removeChild( element );
  return this;
};

var text_element = function( element, args ) {
  if( args.length === 0 )  {
    return element.textContent;
  }
  element.textContent = args[ 0 ];
  return this;
};

var restrictedAttributes = {
  "id" : true, // RAP renders IDs. While it does not rely on them, addons and future versions may.
  "class" : true, // May be used by RAP in the future, separate API could allow access
  "style" : true // Would destroy layout, separate API could allow (limited) access
};

var parseCssValue = function( args ) {
  if( typeof args[ 1 ] === "number" && !$.cssNumber[ args[ 0 ] ] ) {
    return args[ 1 ] + "px";
  }
  return args[ 1 ];
};

}());
