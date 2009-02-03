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
    }
    this._states = states;
    this._store = org.eclipse.swt.theme.ThemeStore.getInstance();
  },

  members : {

    getCssBorder : function( element, key ) {
      var vkey = this._store.getCssValue( element, this._states, key );
      var values = this._store.getThemeValues();
      var result = values.borders[ vkey ];
      this.__checkDefined( result, element, key );
      return result;
    },

    getCssColor : function( element, key ) {
      var vkey = this._store.getCssValue( element, this._states, key );
      var values = this._store.getThemeValues();
      var result = values.colors[ vkey ];
      this.__checkDefined( result, element, key );
      return result;
    },

    getCssFont : function( element, key ) {
      var vkey = this._store.getCssValue( element, this._states, key );
      var values = this._store.getThemeValues();
      var result = values.fonts[ vkey ];
      this.__checkDefined( result, element, key );
      return result;
    },

    getCssDimension : function( element, key ) {
      var vkey = this._store.getCssValue( element, this._states, key );
      var values = this._store.getThemeValues();
      var result = values.dimensions[ vkey ];
      this.__checkDefined( result, element, key );
      return result;
    },

    getCssBoxDimensions : function( element, key ) {
      var vkey = this._store.getCssValue( element, this._states, key );
      var values = this._store.getThemeValues();
      var result = values.boxdims[ vkey ];
      this.__checkDefined( result, element, key );
      return result;
    },

    getCssBoolean : function( element, key ) {
      var vkey = this._store.getCssValue( element, this._states, key );
      var values = this._store.getThemeValues();
      var result = values.booleans[ vkey ];
      this.__checkDefined( result, element, key );
      return result;
    },

    getCssImage : function( element, key ) {
      var vkey = this._store.getCssValue( element, this._states, key );
      var values = this._store.getThemeValues();
      var result = values.images[ vkey ];
      this.__checkDefined( result, element, key );
      if( result != null ) {
        result = "resource/themes/images/" + result;
      } else {
        // TODO [rst] Handle null values - currently, both null and the string
        // "undefined" lead to a js error for icon property
        result = "static/image/blank.gif";
      }
      return result;
    },

    __checkDefined : function( value, element, key ) {
      if( value === undefined ) {
        this.error( "undefined value for " + element + "/" + key );
      }
  	}
  }
} );
