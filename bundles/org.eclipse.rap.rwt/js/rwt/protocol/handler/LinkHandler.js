/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.protocol.HandlerRegistry.add( "rwt.widgets.Link", {

  factory : function( properties ) {
    var result = new rwt.widgets.Link();
    rwt.protocol.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.protocol.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.HandlerUtil.extendControlProperties( [
    "text"
  ] ),

  propertyHandler : rwt.protocol.HandlerUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.protocol.EncodingUtil;
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

  listeners : rwt.protocol.HandlerUtil.extendControlListeners( [
    "Selection"
  ] ),

  listenerHandler : rwt.protocol.HandlerUtil.extendControlListenerHandler( {} )

} );
