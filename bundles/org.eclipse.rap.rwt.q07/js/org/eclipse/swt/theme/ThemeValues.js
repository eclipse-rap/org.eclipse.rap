/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/


/**
 * An adapter class for accessing theme values. 
 * Values for the widget state given in the construtor are selected if available.
 */
qx.Class.define( "org.eclipse.swt.theme.ThemeValues", {

  extend : qx.core.Object,

  /**
   * Creates a new ThemeValues instance for the variant defined in the given
   * widget states.
   */
  construct : function( states ) {
    if( states === undefined ) {
      throw new Error( "no states given" );
    }
    this._states = states;
    this._store = org.eclipse.swt.theme.ThemeStore.getInstance();
  },
  
  statics : {
    NONE_IMAGE : null,
    NONE_IMAGE_SIZED : [ null, 0, 0 ]
  },

  members : {

    getCssBorder : function( element, key ) {
      return this._store.getBorder( element, this._states, key );
    },

    getCssNamedBorder : function( name ) {
      return this._store.getNamedBorder( name );
    },

    getCssColor : function( element, key ) {
      return this._store.getColor( element, this._states, key );
    },

    getCssNamedColor : function( name ) {
      return this._store.getNamedColor( name );
    },

    getCssFont : function( element, key ) {
      return this._store.getFont( element, this._states, key );
    },

    getCssDimension : function( element, key ) {
      return this._store.getDimension( element, this._states, key );
    },

    getCssBoxDimensions : function( element, key ) {
      return this._store.getBoxDimensions( element, this._states, key );
    },

    getCssBoolean : function( element, key ) {
      return this._store.getBoolean( element, this._states, key );
    },

    getCssFloat : function( element, key ) {
      return this._store.getFloat( element, this._states, key );
    },

    getCssIdentifier : function( element, key ) {
      return this._store.getIdentifier( element, this._states, key );
    },

    getCssImage : function( element, key ) {
      return this._store.getImage( element, this._states, key );      
    },

    getCssSizedImage : function( element, key ) {
      return this._store.getSizedImage( element, this._states, key );      
    },

    getCssGradient : function( element, key ) {
      return this._store.getGradient( element, this._states, key );
    },

    getCssCursor : function( element, key ) {
      return this._store.getCursor( element, this._states, key );
    },

    getCssAnimation : function( element, key ) {
      return this._store.getAnimation( element, this._states, key );
    },

    getCssShadow : function( element, key ) {
      return this._store.getShadow( element, this._states, key );
    }

  }
} );
