/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Link", {

  factory : function( properties ) {
    var result = new org.eclipse.swt.widgets.Link();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "text"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      widget.clear();
      for (var i = 0; i < value.length; i++ ) {
        var text = encodingUtil.escapeText( value[ i ][ 0 ], false );
        text = encodingUtil.replaceNewLines( text, "<br/>" );
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

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "selection"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );