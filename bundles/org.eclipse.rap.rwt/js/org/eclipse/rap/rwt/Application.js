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

/*
#resource(image:image)
*/
qx.OO.defineClass( 
  "org.eclipse.rap.rwt.Application", 
  qx.component.AbstractApplication,
  function() {
    qx.component.AbstractApplication.call( this );
  }
);

qx.Proto.initialize = function( evt ) {
  qx.manager.object.AliasManager.getInstance().add( "org.eclipse.rap.rwt", 
                                                    "./resource" );
  var theme = org.eclipse.rap.rwt.WidgetTheme.getInstance();                                                    
  qx.manager.object.ImageManager.getInstance().setWidgetTheme( theme );
};

qx.Proto.main = function( evt ) {
  var doc = qx.ui.core.ClientDocument.getInstance();
  // Observe window size
  // TODO [rh] event below isn't fired but is necessary to track browser window 
  //      resizes  
  doc.addEventListener( "windowresize", 
                        org.eclipse.rap.rwt.Application._onResize );  
  // Initial request to obtain startup-shell
  var req = org.eclipse.rap.rwt.Request.getInstance();
  org.eclipse.rap.rwt.Application._onResize(); // appends bounds to the request
  req.send();
};

qx.Proto.finalize = function( evt ) {
};

qx.Proto.close = function( evt ) {
};

qx.Proto.terminate = function( evt ) {
  // TODO [rh] code below causes error (this._onResize is undefined)
  //      Is it necessary to remove eventListener anyway?
//  var doc = qx.ui.core.ClientDocument.getInstance();
//  doc.removeEventListener( "windowresize", this._onResize );  
};

org.eclipse.rap.rwt.Application._onResize = function( evt ) {
  var doc = qx.ui.core.ClientDocument.getInstance();
  var req = org.eclipse.rap.rwt.Request.getInstance();
  var id = req.getUIRootId();
  // TODO [rh] replace code below with qx.dom.Window.getInnerWidth( window )
  //      and getInnerHeight( window ) when available. Seems like qx 0.6 does
  //      not yet support these functions.
  var width = 0;
  var height = 0;
  if( document.layers || ( document.getElementById && !document.all ) ) {
     width = window.innerWidth;
     height = window.innerHeight;
  } else if( document.all ) {
     width = document.body.clientWidth;
     height = document.body.clientHeight;
  }  
  req.addParameter( id + ".bounds.width", String( width ) );
  req.addParameter( id + ".bounds.height", String( height ) );
};
