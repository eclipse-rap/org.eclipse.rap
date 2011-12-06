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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.ExpandItem", {

  factory : function( properties ) {
    var result;
    org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result = new org.eclipse.swt.widgets.ExpandItem( parent );
      parent.addWidget( result );
      org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    } );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getWidgetDestructor(),

  properties : [
    "customVariant",
    "bounds",
    "text",
    "image",
    "expanded",
    "headerHeight"
  ],

  propertyHandler : {
    "bounds" : function( widget, value ) {
      widget.setLeft( value[ 0 ] );
      widget.setTop( value[ 1 ] );
      widget.setWidth( value[ 2 ] );
      widget.setHeight( value[ 3 ] );
    },
    "text" : function( widget, value ) {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = encodingUtil.escapeText( value, false );
      widget.setText( text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setImage( null );
      } else {
        widget.setImage( value[ 0 ] );
      }
    }
  },

  listeners : [],

  listenerHandler : {},

  methods : []

} );