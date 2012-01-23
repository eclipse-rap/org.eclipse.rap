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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Combo", {

  factory : function( properties ) {
    var result = new org.eclipse.swt.widgets.Combo( properties.ccombo );
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "itemHeight",
    "visibleItemCount",
    "items",
    "listVisible",
    "selectionIndex",
    "editable",
    "text",
    "selection",
    "textLimit"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "items" : function( widget, value ) {
      var items = value;
      var EncodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      for( var i = 0; i < items.length; i++ ) {
        items[ i ] = EncodingUtil.replaceNewLines( items[ i ], " " );
        items[ i ] = EncodingUtil.escapeText( items[ i ], false );
        items[ i ] = EncodingUtil.replaceWhiteSpaces( items[ i ] );
      }
      widget.setItems( items );
    },
    "selectionIndex" : function( widget, value ) {
      widget.select( value );
    },
    "selection" : function( widget, value ) {
      var start = value[ 0 ];
      var length = value[ 1 ] - value[ 0 ];
      widget.setTextSelection( start, length );
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "selection",
    "modify",
    "verify"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );