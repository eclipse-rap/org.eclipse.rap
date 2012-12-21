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

rwt.remote.HandlerRegistry.add( "forms.widgets.Hyperlink", {

  factory : function( properties ) {
    var wrap = properties.style.indexOf( "WRAP" ) !== -1 ? "wrap" : "";
    var result = new org.eclipse.ui.forms.widgets.Hyperlink( wrap );
    rwt.remote.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.remote.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.remote.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.remote.HandlerUtil.extendControlProperties( [
    "text",
    "image",
    "underlined",
    "underlineMode",
    "activeForeground",
    "activeBackground"
  ] ),

  propertyHandler : rwt.remote.HandlerUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.util.Encoding;
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
        widget.setActiveTextColor( rwt.util.Colors.rgbToRgbString( value ) );
      }
    },
    "activeBackground" : function( widget, value ) {
      if( value === null ) {
        widget.setActiveBackgroundColor( null );
      } else {
        widget.setActiveBackgroundColor( rwt.util.Colors.rgbToRgbString( value ) );
      }
    }
  } ),

  listeners : rwt.remote.HandlerUtil.extendControlListeners( [
    "DefaultSelection"
  ] ),

  listenerHandler : rwt.remote.HandlerUtil.extendControlListenerHandler( {} ),

  methods : []

} );