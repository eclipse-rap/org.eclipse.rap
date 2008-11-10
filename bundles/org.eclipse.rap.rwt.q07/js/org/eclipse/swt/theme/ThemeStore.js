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
    this._statesMap = {
      "*" : {
        "hover" : "over"
      },
      "Button" : {
        "selected" : "checked"
      },
      "List-Item" : {
        "inactive" : "parent_unfocused"
      },
      "Shell" : {
        "inactive" : "!active"
      },
      "Shell-Titlebar" : {
        "inactive" : "!active"
      },
      "Shell-MinButton" : {
        "inactive" : "!active"
      },
      "Shell-MaxButton" : {
        "inactive" : "!active"
      },
      "Shell-CloseButton" : {
        "inactive" : "!active"
      },
      "TabItem" : {
        "selected" : "checked"
      }
    };
  },

  members : {

    /**
     * Returns the values container for a given theme. If no theme is given, the
     * values container for the current theme is returned. If the requested
     * values container does not exist, it is created.
     * 
     * TODO [rst] A theme value of "_" is used for shared CSS values. Later, the
     * parameter can be dropped, as there will only be one values object.
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
          trcolors : {},
          // === new types ===
          fonts : {},
          colors : {},
          borders : {}
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
      // === new types ===
      } else if ( type == "font" ) {
        var font = new qx.ui.core.Font();
        font.setSize( value.size );
        font.setFamily( value.family );
        font.setBold( value.bold );
        font.setItalic( value.italic );
        values.fonts[ key ] = font;
      } else if ( type == "color" ) {
        values.colors[ key ] = value;
      } else if ( type == "border" ) {
        var border = new qx.ui.core.Border( value.width, value.style );
        if( value.color ) {
          border.setUserData( "color", value.color );
        }
        if( value.innerColor ) {
          border.setUserData( "innerColor", value.innerColor );
        }
        values.borders[ key ] = border;
      } else {
        this.error( "invalid type: " + type );
      }
    },

    resolveBorderColors : function( theme ) {
      var values = this.getThemeValues( theme );
      for( var key in values.borders ) {
        var border = values.borders[ key ];
        var colorData = border.getUserData( "color" );
        if( colorData != null ) {
          var colors = [];
          for( var i = 0; i < colorData.length; i++ ) {
            if( colorData[ i ].charAt( 0 ) == "#" ) {
              colors.push( colorData[ i ] );
            } else {
              colors.push( values.colors[ colorData[ i ] ] );
            }
          }
          border.setColor( colors );
          border.setUserData( "color", null );
        }
        var innerColorData = border.getUserData( "innerColor" );
        if( innerColorData != null ) {
          var innerColors = [];
          for( var i = 0; i < innerColorData.length; i++ ) {
            innerColors.push( values.colors[ innerColorData[ i ] ] );
          }
          border.setInnerColor( innerColors );
          border.setUserData( "innerColor", null );
        }
      }
    },

    // CSS SUPPORT

    setThemeCssValues : function( theme, values, isDefault ) {
      if( this._cssValues[ theme ] === undefined ) {
        this._cssValues[ theme ] = values;
      }
      if( isDefault ) {
        this.defaultTheme = theme;
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
          if( this._matches( states, element, values[ i ][ 0 ] ) ) {
            result = values[ i ][ 1 ];
            found = true;
          }
        }
      }
      if( result === undefined && theme != this.defaultTheme ) {
        result = this.getCssValue( element, states, property, this.defaultTheme );
      }
      return result;
    },

    _matches : function( states, element, constraints ) {
      var result = true;
      for( var i = 0; i < constraints.length && result; i++ ) {
        var cond = constraints[ i ];
        if( cond.length > 0 ) {
          var c = cond.charAt( 0 );
          if( c == "." ) {
            result = "variant_" + cond.substr( 1 ) in states;
          } else if( c == ":" ) {
            var state = this._translateState( cond.substr( 1 ), element );
            if( state.charAt( 0 ) == "!" ) {
              result = ! ( state.substr( 1 ) in states );
            } else {
              result = state in states;
            }
          } else if( c == "[" ) {
            result = "rwt_" + cond.substr( 1 ) in states;
          }
        }
      }
      return result;
    },

    _translateState : function( state, element ) {
      var result = state;
      if( state in this._statesMap[ "*" ] ) {
        result = this._statesMap[ "*" ][ state ];
      } else if( element in this._statesMap
                 && state in this._statesMap[ element ] )
      {
        result = this._statesMap[ element ][ state ];
      }
      return result;
    }
  }
} );
