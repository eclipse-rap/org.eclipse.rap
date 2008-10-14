/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/


/**
 * Store for theme values that cannot be kept in a qooxdoo theme. The store is
 * filled from the server at startup.
 */
qx.Class.define( "org.eclipse.swt.theme.ThemeStore", {

  type : "singleton",

  extend : qx.core.Object,

  construct : function() {
    this._values = {};
    this._cssValues = {};
  },

  members : {

    /**
     * Returns the values container for a given theme. If no theme is given, the
     * values container for the current theme is returned. If the requested
     * values container does not exist, it is created.
     */
    getThemeValues : function( theme ) {
      if( theme == null ) {
        theme = qx.theme.manager.Meta.getInstance().getTheme().name;
      }
      if( this._values[ theme ] === undefined ) {
        this._values[ theme ] = {
          dimensions : {},
          boxdims : {},
          booleans : {},
          images : {},
          trcolors : {}
        };
      }
      return this._values[ theme ];
    },

    /**
     * Adds a dimension with the given type, key and value to the values
     * container for the given theme.
     */
    setValue : function( type, key, value, theme ) {
      var values = this.getThemeValues( theme );
      if( type == "dimension" ) {
        values.dimensions[ key ] = value;
      } else if ( type == "boxdim" ) {
        values.boxdims[ key ] = value;
      } else if ( type == "boolean" ) {
        values.booleans[ key ] = value;
      } else if ( type == "image" ) {
        values.images[ key ] = value;
      } else if ( type == "trcolor" ) {
        values.trcolors[ key ] = value;
      } else {
        this.error( "invalid type: " + type );
      }
    },

    // CSS SUPPORT

    setThemeCssValues : function( theme, values ) {
      if( this._cssValues[ theme ] === undefined ) {
        this._cssValues[ theme ] = values;
      }
    },

    getCssValue : function( element, states, property, theme ) {
      var result;
      if( theme == null ) {
        theme = qx.theme.manager.Meta.getInstance().getTheme().name;
      }
      if(    this._cssValues[ theme ] !== undefined
          && this._cssValues[ theme ][ element ] !== undefined 
          && this._cssValues[ theme ][ element ][ property ] !== undefined )
      {
        var values = this._cssValues[ theme ][ element ][ property ];
        var found = false;
        for( var i = 0; i < values.length && !found; i++ ) {
          if( this._matches( states, values[ i ][ 0 ] ) ) {
            result = values[ i ][ 1 ];
            found = true;
          }
        }
      }
      return result;
    },

    _matches : function( states, constraints ) {
      var result = true;
      for( var i = 0; i < constraints.length && result; i++ ) {
        var cond = constraints[ i ];
        if( cond.length > 0 ) {
          var c = cond.substr( 0, 1 );
          if( c == "." ) {
            result = "variant_" + cond.substr( 1 ) in states;
          } else if( c == ":" ) {
            result = this._translateState( cond.substr( 1 ) ) in states;
          } else if( c == "[" ) {
            result = "rwt_" + cond.substr( 1 ) in states;
          }
        }
      }
      return result;
    },

    _translateState : function( state ) {
      var result = state;
      var map = {
        "hover" : "over"
      };
      if( state in map ) {
        result = map[ state ];
      }
      return result;
    }
  }
} );
