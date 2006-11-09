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

qx.Proto.initialize = function( e ) {
  qx.manager.object.AliasManager.getInstance().add( "org.eclipse.rap.rwt", 
                                                    "./resource" );
  var am = qx.manager.object.AppearanceManager.getInstance();
  am.setAppearanceTheme( new org.eclipse.rap.rwt.DefaultAppearanceTheme() );
};

qx.Proto.main = function( e ) {
  // Initial request to obtain startup-shell
  var req = org.eclipse.rap.rwt.Request.getInstance();
  req.send();
};

qx.Proto.finalize = function( e ) {
};

qx.Proto.close = function( e ) {
};

qx.Proto.terminate = function( e ) {
};