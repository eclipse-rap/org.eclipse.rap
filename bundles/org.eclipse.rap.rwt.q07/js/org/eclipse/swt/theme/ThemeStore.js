/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
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
    }
  }
} );
