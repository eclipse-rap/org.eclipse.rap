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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.TreeItem", {

  factory : function( properties ) {
    var result;
    org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result = org.eclipse.rwt.widgets.TreeItem.createItem( parent, properties.index );
    } );
    return result;
  },

  destructor : function( item ) {
    item.dispose();
  },

  properties : [
    "itemCount",
    "texts",
    "images",
    "background",
    "foreground",
    "font",
    "cellBackgrounds",
    "cellForegrounds",
    "cellFonts",
    "expanded",
    "checked",
    "grayed",
    "variant"
  ],

  propertyHandler : {
    "texts" : function( widget, value ) {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var texts = value;
      for( var i = 0; i < value.length; i++ ) {
        texts[ i ] = encodingUtil.escapeText( texts[ i ], false );
        texts[ i ] = encodingUtil.replaceWhiteSpaces( texts[ i ] );
      }
      widget.setTexts( texts );
    },
    "images" : function( widget, value ) {
      var images = [];
      for( var i = 0; i < value.length; i++ ) {
        if( value[ i ] === null ) {
          images[ i ] = null;
        } else {
          images[ i ] = value[ i ][ 0 ];
        }
      }
      widget.setImages( images );
    },
    "background" : function( widget, value ) {
      if( value === null ) {
        widget.setBackground( null );
      } else {
        widget.setBackground( qx.util.ColorUtil.rgbToRgbString( value ) );
      }
    },
    "foreground" : function( widget, value ) {
      if( value === null ) {
        widget.setForeground( null );
      } else {
        widget.setForeground( qx.util.ColorUtil.rgbToRgbString( value ) );
      }
    },
    "font" : function( widget, value ) {
      if( value === null ) {
        widget.setFont( null );
      } else {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var font = wm._createFont.apply( wm, value );
        widget.setFont( font );
      }
    },
    "cellBackgrounds" : function( widget, value ) {
      var backgrounds = [];
      for( var i = 0; i < value.length; i++ ) {
        if( value[ i ] === null ) {
          backgrounds[ i ] = null;
        } else {
          backgrounds[ i ] = qx.util.ColorUtil.rgbToRgbString( value[ i ] );
        }
      }
      widget.setCellBackgrounds( backgrounds );
    },
    "cellForegrounds" : function( widget, value ) {
      var foregrounds = [];
      for( var i = 0; i < value.length; i++ ) {
        if( value[ i ] === null ) {
          foregrounds[ i ] = null;
        } else {
          foregrounds[ i ] = qx.util.ColorUtil.rgbToRgbString( value[ i ] );
        }
      }
      widget.setCellForegrounds( foregrounds );
    },
    "cellFonts" : function( widget, value ) {
      var fonts = [];
      for( var i = 0; i < value.length; i++ ) {
        if( value[ i ] === null ) {
          fonts[ i ] = "";
        } else {
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var font = wm._createFont.apply( wm, value[ i ] );
          fonts[ i ] = font.toCss();
        }
      }
      widget.setCellFonts( fonts );
    }
  },

  listeners : [],

  listenerHandler : {},

  methods : []

} );