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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.List", {

  factory : function( properties ) {
    var multiSelection = properties.style.indexOf( "MULTI" ) != -1;
    var result = new org.eclipse.swt.widgets.List( multiSelection );
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    // order of items, selection, focus is crucial
    "items",
    "selectionIndices",
    "topIndex",
    "focusIndex",
    "scrollBarsVisible",
    "itemDimensions"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "items" : function( widget, value ) {
      var items = value;
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      for( var i = 0; i < items.length; i++ ) {
        items[ i ] = encodingUtil.replaceNewLines( items[ i ], " " );
        items[ i ] = encodingUtil.escapeText( items[ i ], false );
        items[ i ] = encodingUtil.replaceWhiteSpaces( items[ i ] );
      }
      widget.setItems( items );
    },
    "selectionIndices" : function( widget, value ) {
      if( widget.hasState( "rwt_MULTI" ) ) {
        if( widget.getItemsCount() === value.length ) {
          widget.selectAll();
        } else {
          widget.selectItems( value );
        }
      } else {
        widget.selectItem( value[ 0 ] !== undefined ? value[ 0 ] : -1 );
      }
    },
    "focusIndex" : function( widget, value ) {
      widget.focusItem( value );
    },
    "scrollBarsVisible" : function( widget, value ) {
      widget.setScrollBarsVisible( value[ 0 ], value[ 1 ] );
    },
    "itemDimensions" : function( widget, value ) {
      widget.setItemDimensions( value[ 0 ], value[ 1 ] );
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "selection"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );