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
 * An adapter class for accessing theme values in appearance definitions. The
 * values are read from the respective qooxdoo theme or the RAP ThemeStore.
 * Values for the actual widget variant are selected if available.
 */
qx.Class.define( "org.eclipse.swt.theme.ThemeValues", {

  extend : qx.core.Object,

  /**
   * Creates a new ThemeValues instance for the variant defined in the given
   * widget states.
   */
  construct : function( states ) {
    if( states === undefined ) {
      this.warn( "no states given" );
    } else if( typeof states == "string" ) {
      this._variant = states;
    } else {
      this._variant = this.__extractVariant( states );
    }
    this._store = org.eclipse.swt.theme.ThemeStore.getInstance();
  },

  members : {

    getBorder : function( key ) {
      var theme = qx.theme.manager.Border.getInstance().getBorderTheme();
      return this.__selectVariant( key, theme.borders );
    },

    getColor : function( key ) {
      var theme = qx.theme.manager.Color.getInstance().getColorTheme();
      var result = this.__selectVariant( key, theme.colors );
      var values = this._store.getThemeValues();
      if( values.trcolors[ result ] ) {
        result = "undefined";
      }
      return result;
    },

    getFont : function( key ) {
      var theme = qx.theme.manager.Font.getInstance().getFontTheme();
      return this.__selectVariant( key, theme.fonts );
    },

    getDimension : function( key ) {
      var values = this._store.getThemeValues();
      var vkey = this.__selectVariant( key, values.dimensions );
      return values.dimensions[ vkey ];
    },

    getBoxDimensions : function( key ) {
      var values = this._store.getThemeValues();
      var vkey = this.__selectVariant( key, values.boxdims );
      return values.boxdims[ vkey ];
    },

    getBoolean : function( key ) {
      var values = this._store.getThemeValues();
      var vkey = this.__selectVariant( key, values.booleans );
      return values.booleans[ vkey ];
    },

    getImage : function( key ) {
      var values = this._store.getThemeValues();
      var vkey = this.__selectVariant( key, values.images );
      var result = values.images[ vkey ];
      if( result != null ) {
        result = "widget/" + result;
      } else {
        // TODO [rst] Handle null values - currently, both null and the string
        // "undefined" lead to a js error for icon property
        result = "static/image/blank.gif";
      }
      return result;
    },

    __selectVariant : function( key, values ) {
      var result = key;
      if( !key ) {
        this.warn( "missing key argument" );
      }
      if( values[ key ] === undefined ) {
        this.error( "undefined key: '" + key + "'" );
      }
      if( this._variant && values[ this._variant + "/" + key ] !== undefined ) {
      	result = this._variant + "/" + key;
      }
      return result;      
    },

  	__extractVariant: function( states ) {
  	  var result = null;
  	  if( states != null ) {
        for( var state in states ) {
          if( state.substr( 0, 8 ) == "variant_" ) {
            result = state.substr( 8 );
      	  }
        }
  	  }
      return result;
  	}
  }
} );
