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

  var renderer = {};

  this.add = function( type, rendererMap ) {
    renderer[ type ] = rendererMap;
  };

  this.get = function( type ) {
    return renderer[ type ];
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

rwt.widgets.util.CellRendererRegistry.getInstance().add( "text", {
} );

}());
