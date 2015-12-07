/*******************************************************************************
 * Copyright (c) 2016 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){
  'use strict';

  var createStubs = function( object, names ) {
    var fun = function(){ return false; };
    for( var i = 0; i < names.length; i++ ) {
      object[ names[ i ] ] = fun;
    }
  };

  window.CKEDITOR = {
    editor : function(){},
    appendTo : function( element ) {
      return new CKEDITOR.editor( element );
    }
  };

  createStubs(
    CKEDITOR.editor.prototype,
    [ "on", "resize", "setData", "getData", "setReadOnly", "checkDirty", "resetDirty", "destroy" ]
  );

  CKEDITOR.editor.prototype.document = {
    "getBody" : function() {
      return this.body;
    },
    "body" : {
      setStyle : function(){}
    }
  };

}());
