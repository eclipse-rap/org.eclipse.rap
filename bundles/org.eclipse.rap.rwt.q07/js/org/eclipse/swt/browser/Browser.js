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

qx.Class.define( "org.eclipse.swt.browser.Browser", {
  extend : qx.ui.embed.Iframe,
  
  construct : function() {
    this.base( arguments );
    // TODO [rh] preliminary workaround to make Browser accessible by tab
    this.setTabIndex( 1 );
    this.setAppearance( "browser" );
    this.addEventListener( "load", this._onLoad, this );
  },
  
  destruct : function() {
    this.removeEventListener( "load", this._onLoad, this );
  },  

  members : {
    
    _onLoad : function( evt ) {
      this.release();
    },
    
    execute : function( script ) {
      var result = true;
      try {
        this.getContentWindow().eval( script );
      } catch( e ) {
        result = false;
      }
      var req = org.eclipse.swt.Request.getInstance();
      var id = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( this );
      req.addParameter( id + ".executeResult", result );
      req.send();
    }
    
  }
});
