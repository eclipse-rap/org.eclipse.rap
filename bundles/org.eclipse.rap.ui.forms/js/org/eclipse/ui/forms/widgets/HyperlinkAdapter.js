/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
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
