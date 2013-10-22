/*******************************************************************************
 * Copyright (c) 2010, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

namespace( "rwt.widgets.util" );

(function(){

rwt.widgets.util.CellRendererRegistry = function() {

  var rendererMap = {};
  var Wrapper = function() {};

  this.add = function( renderer ) {
    if(    renderer == null
        || typeof renderer.contentType !== "string"
        || typeof renderer.cellType !== "string" )
    {
      throw new Error( "Can not register invalid renderer" );
    }
    if( rendererMap[ renderer.cellType ] != null ) {
      throw new Error( "Renderer for cellType " + renderer.cellType + " already registered" );
    }
    if( !renderer.shouldEscapeText ) {
      renderer.shouldEscapeText = rwt.util.Functions.returnFalse;
    }
    rendererMap[ renderer.cellType ] = renderer;
  };

  this.getRendererFor = function( type ) {
    return rendererMap[ type ] || null;
  };

  this.removeRendererFor = function( type ) {
    delete rendererMap[ type ];
  };

  this.getAll = function( type ) {
    Wrapper.prototype = rendererMap;
    return new Wrapper();
  };

};

rwt.widgets.util.CellRendererRegistry.getInstance = function() {
  if( !rwt.widgets.util.CellRendererRegistry._instance ) {
    rwt.widgets.util.CellRendererRegistry._instance
      = new rwt.widgets.util.CellRendererRegistry();
  }
  return rwt.widgets.util.CellRendererRegistry._instance;
};

///////////////////
// default renderer

rwt.widgets.util.CellRendererRegistry.getInstance().add( {
  "cellType" : "text",
  "contentType" : "text",
  "renderContent" : function(){}
} );

rwt.widgets.util.CellRendererRegistry.getInstance().add( {
  "cellType" : "image",
  "contentType" : "image",
  "renderContent" : function(){}
} );

}());
