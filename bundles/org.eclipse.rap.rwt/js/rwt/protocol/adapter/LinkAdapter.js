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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.Link", {

  factory : function( properties ) {
    var result = new rwt.widgets.Link();
    rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.AdapterUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.AdapterUtil.extendControlProperties( [
    "text"
  ] ),

  propertyHandler : rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
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

  listeners : rwt.protocol.AdapterUtil.extendControlListeners( [
    "Selection"
  ] ),

  listenerHandler : rwt.protocol.AdapterUtil.extendControlListenerHandler( {} )

} );
