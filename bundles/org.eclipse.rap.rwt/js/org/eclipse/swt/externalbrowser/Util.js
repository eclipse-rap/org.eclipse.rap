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

qx.Class.define( "org.eclipse.swt.externalbrowser.Util",
{
  extend : qx.core.Object,
  
  statics : {
    // maps id's (aka window names) to window instances
    // key = id, value = window object
    _map : {},
    
    open : function( id, url, features ) {
      var win = window.open( url, id, features, true );
      org.eclipse.swt.externalbrowser.Util._map[ id ] = win; 
    },
    
    close : function( id ) {
      var win = org.eclipse.swt.externalbrowser.Util._map[ id ];
      if( win != null ) {
        win.close();
      } 
      delete org.eclipse.swt.externalbrowser.Util._map[ id ];
    }
  }
} );

