/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.protocol.AdapterRegistry.add( "forms.widgets.Hyperlink", {

  factory : function( properties ) {
    var wrap = properties.style.indexOf( "WRAP" ) !== -1 ? "wrap" : "";
    var result = new org.eclipse.ui.forms.widgets.Hyperlink( wrap );
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "text",
    "image",
    "underlined",
    "underlineMode",
    "activeForeground",
    "activeBackground"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = EncodingUtil.escapeText( value, false );
      widget.setText( text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setIcon( null );
      } else {
        widget.setIcon( value[ 0 ] );
      }
    },
    "activeForeground" : function( widget, value ) {
      if( value === null ) {
        widget.setActiveTextColor( null );
      } else {
        widget.setActiveTextColor( qx.util.ColorUtil.rgbToRgbString( value ) );
      }
    },
    "activeBackground" : function( widget, value ) {
      if( value === null ) {
        widget.setActiveBackgroundColor( null );
      } else {
        widget.setActiveBackgroundColor( qx.util.ColorUtil.rgbToRgbString( value ) );
      }
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "selection"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );