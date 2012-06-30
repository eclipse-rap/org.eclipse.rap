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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.ToolTip", {

  factory : function( properties ) {
    var styleMap = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    var style = null;
    if( styleMap.ICON_ERROR ) {
      style = "error";
    } else if( styleMap.ICON_WARNING ) {
      style = "warning";
    } else if( styleMap.ICON_INFORMATION ) {
      style = "information";
    }
    var result = new org.eclipse.swt.widgets.ToolTip( style );
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getWidgetDestructor(),

  properties : [
    "customVariant",
    "roundedBorder",
    "backgroundGradient",
    "autoHide",
    "text",
    "message",
    "location",
    // Visible must be set after all other properties
    "visible"
  ],

  propertyHandler : {
    "roundedBorder" : org.eclipse.rwt.protocol.AdapterUtil.getRoundedBorderHandler(),
    "backgroundGradient" : org.eclipse.rwt.protocol.AdapterUtil.getBackgroundGradientHandler(),
    "autoHide" : function( widget, value ) {
      widget.setHideAfterTimeout( value );
    },
    "text" : function( widget, value ) {
      var EncodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = EncodingUtil.escapeText( value, false );
      widget.setText( text );
    },
    "message" : function( widget, value ) {
      var EncodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = EncodingUtil.escapeText( value, false );
      text = EncodingUtil.replaceNewLines( text, "<br/>" );
      widget.setMessage( text );
    },
    "location" : function( widget, value ) {
      widget.setLocation( value[ 0 ], value[ 1 ] );
    }
  },

  listeners : [
    "selection"
  ],

  listenerHandler : {},

  methods : []

} );
