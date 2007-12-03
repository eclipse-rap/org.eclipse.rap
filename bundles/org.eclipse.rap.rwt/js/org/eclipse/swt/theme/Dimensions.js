/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/*
 * Store for themeable dimensions and box dimensions.
 */
qx.Class.define( "org.eclipse.swt.theme.Dimensions", {
  
  type : "singleton",
  
  extend : qx.core.Object,
  
  construct : function() {
    this._values = {};
  },
  
  members : {

    set : function( name, theme, value ) {
      if( this._values[ theme ] === undefined ) {
        this._values[ theme ] = {};
      }
      var values = this._values[ theme ];
      values[ name ] = value;
    },
    
    get : function( name ) {
      var theme = qx.theme.manager.Meta.getInstance().getTheme().name;
      var values = this._values[ theme ];
      var value = values[ name ];
      return value;
    }
  }  
} );
