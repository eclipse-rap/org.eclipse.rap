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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.ControlDecorator", {

  factory : function( properties ) {
    var result = new org.eclipse.rwt.widgets.ControlDecorator();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getWidgetDestructor(),

  properties : [
    "bounds",
    "text",
    "image",
    "visible",
    "showHover"
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
        widget.setSource( null );
      } else {
        widget.setSource( value[ 0 ] );
      }
    },
    "visible" : function( widget, value ) {
      widget.setVisibility( value );
    }
  },

  listeners : [
    "selection"
  ],

  listenerHandler : {},

  methods : []

} );