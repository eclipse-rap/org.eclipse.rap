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

rwt.remote.HandlerRegistry.add( "forms.widgets.FormText", {

  factory : function( properties ) {
    var result = new org.eclipse.ui.forms.widgets.FormText();
    result.setUserData( "isControl", true );
    rwt.remote.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.remote.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.remote.HandlerUtil.extendControlProperties( [
    "text",
    "hyperlinkSettings"
  ] ),

  propertyHandler : rwt.remote.HandlerUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      widget.clearContent();
      for( var i = 0; i < value.length; i++ ) {
        var type = value[ i ][ 0 ];
        var args = value[ i ].slice( 1 );
        switch( type ) {
          case "bullet":
            widget.createBullet.apply( widget, args );
          break;
          case "textHyperlink":
            widget.createTextHyperlinkSegment.apply( widget, args );
          break;
          case "text":
            widget.createTextSegment.apply( widget, args );
          break;
          case "imageHyperlink":
            widget.createImageHyperlinkSegment.apply( widget, args );
          break;
          case "image":
            widget.createImageSegment.apply( widget, args );
          break;
        }
      }
      widget.updateHyperlinks();
    },
    "hyperlinkSettings" : function( widget, value ) {
      var ColorUtil = rwt.util.Colors;
      var foreground = value[ 1 ] !== null ? ColorUtil.rgbToRgbString( value[ 1 ] ) : null;
      var activeForeground = value[ 2 ] !== null ? ColorUtil.rgbToRgbString( value[ 2 ] ) : null;
      widget.setHyperlinkSettings( value[ 0 ], foreground, activeForeground );
    }
  } ),

  listeners : rwt.remote.HandlerUtil.extendControlListeners( [] ),

  listenerHandler : rwt.remote.HandlerUtil.extendControlListenerHandler( {} ),

  methods : []

} );