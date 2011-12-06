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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.CTabItem", {

  factory : function( properties ) {
    var result;
    org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result = new org.eclipse.swt.custom.CTabItem( parent, parent.hasState( "rwt_CLOSE" ) );
      parent.addAt( result, properties.index );
      org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    } );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getWidgetDestructor(),

  properties : [
    "bounds",
    "font",
    "text",
    "image",
    "toolTip",
    "customVariant",
    "showing",
    "showClose"
  ],

  propertyHandler : {
    "bounds" : function( widget, value ) {
      var bounds = value;
      if( widget.getParent().getTabPosition() === "bottom" ) {
        bounds[ 1 ] -= 1;
      }
      bounds[ 3 ] += 1;
      widget.setLeft( bounds[ 0 ] );
      widget.setTop( bounds[ 1 ] );
      widget.setWidth( bounds[ 2 ] );
      widget.setHeight( bounds[ 3 ] );
    },
    "font" : org.eclipse.rwt.protocol.AdapterUtil.getControlPropertyHandler( "font" ),
    "text" : function( widget, value ) {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = encodingUtil.escapeText( value, true );
      widget.setLabel( text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setIcon( null );
      } else {
        widget.setIcon( value[ 0 ] );
      }
    },
    "toolTip" : org.eclipse.rwt.protocol.AdapterUtil.getControlPropertyHandler( "toolTip" ),
    "showing" : function( widget, value ) {
      widget.setVisibility( value );
    }
  },

  listeners : [],

  listenerHandler : {},

  methods : []

} );