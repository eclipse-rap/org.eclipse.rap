/*******************************************************************************
 * Copyright (c) 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.MobileWebkitSupport", {

  type : "static",
  
  statics : {
    
    init : function() {
      if( this._isMobileWebkit() ) {
        this._hideTabHighlight();
      } 
    },
 
    _isMobileWebkit : function() {
      var platform = qx.core.Client.getPlatform();
      var engine = qx.core.Client.getEngine();
      var isMobile = platform === "ipad" || platform === "iphone";
      return isMobile && engine === "webkit"; 
    },
    
    _hideTabHighlight : function() {
      qx.html.StyleSheet.createElement( 
        " * { -webkit-tap-highlight-color: rgba(0,0,0,0); }"
      );
    }

  }
    
} );

