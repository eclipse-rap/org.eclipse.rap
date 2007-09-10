
/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.Application", {
  extend : qx.application.Gui,

  construct : function() {
    this.base( arguments );
    
    this._exitMessage = null;
  },

  statics : {
    // TODO [rh] causes JavaScript error
    //  var doc = qx.ui.core.ClientDocument.getInstance();
    //  doc.removeEventListener( "windowresize", this._onResize );
    _onResize : function( evt ) {
      org.eclipse.swt.Application._appendWindowSize();
      var req = org.eclipse.swt.Request.getInstance();
      req.send();
    },
    
    _appendWindowSize : function() {
      var width = qx.html.Window.getInnerWidth( window );
      var height = qx.html.Window.getInnerHeight( window );
      // Append document size to request
      var req = org.eclipse.swt.Request.getInstance();
      var id = req.getUIRootId();
      req.addParameter( id + ".bounds.width", String( width ) );
      req.addParameter( id + ".bounds.height", String( height ) );
    }
  },

  members : {
    main : function( evt ) {
      this.base( arguments );
      // Overwrite the default mapping for internal images. This is necessary
      // if the application is deployed under a root different from "/".
      qx.io.Alias.getInstance().add( "static", "./resource/static" );
      qx.io.Alias.getInstance().add( "org.eclipse.swt", "./resource" );
      // Observe window size
      var doc = qx.ui.core.ClientDocument.getInstance();
      doc.addEventListener( "windowresize", 
                            org.eclipse.swt.Application._onResize );
      // Initial request to obtain startup-shell
      org.eclipse.swt.Application._appendWindowSize();
      var req = org.eclipse.swt.Request.getInstance();
      req.send();
    },

    setExitMessage : function( msg ) {
    	this._exitMessage = msg;
    },
    
    close : function( evt ) {
      var req = org.eclipse.swt.Request.getInstance();
      var id = req.getUIRootId();
      var request = new qx.io.remote.Request( this._url, 
                                              qx.net.Http.METHOD_GET, 
                                              qx.util.Mime.TEXT );
      request.setParameter( "org.eclipse.swt.closeRequested", id );
      request.send();
      
      this.base( arguments );
      return this._exitMessage;
    }
  }
});
