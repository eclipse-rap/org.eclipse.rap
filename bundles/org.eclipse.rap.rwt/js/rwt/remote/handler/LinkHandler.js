/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.remote.HandlerRegistry.add( "rwt.widgets.Link", {

  factory : function( properties ) {
    var result = new rwt.widgets.Link();
    rwt.remote.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.remote.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.remote.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.remote.HandlerUtil.extendControlProperties( [
    "text"
  ] ),

  propertyHandler : rwt.remote.HandlerUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.util.Encoding;
      widget.clear();
      for (var i = 0; i < value.length; i++ ) {
        var text = EncodingUtil.escapeText( value[ i ][ 0 ], false );
        text = EncodingUtil.replaceNewLines( text, "<br/>" );
        var index = value[ i ][ 1 ];
        if( index !== null ) {
          widget.addLink( text, index );
        } else {
          widget.addText( text );
        }
      }
      widget.applyText();
    }
  } ),

  events : [ "Selection" ],

  listeners : rwt.remote.HandlerUtil.extendControlListeners( [] ),

  listenerHandler : rwt.remote.HandlerUtil.extendControlListenerHandler( {} )

} );
