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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.TabItem", {

  factory : function( properties ) {
    return org.eclipse.swt.TabUtil.createTabItem( properties.id, 
                                                  properties.parent,
                                                  properties.index );
  },

  destructor : function( widget ) {
    org.eclipse.swt.TabUtil.releaseTabItem( widget );
  },

  properties : [
    "text",
    "image",
    "control",
    "toolTip",
    "customVariant"
  ],

  propertyHandler : {
    "text" : function( widget, value ) {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = encodingUtil.escapeText( value, false );
      widget.setLabel( text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setIcon( null );
      } else {
        widget.setIcon( value[ 0 ] );
      }
    },
    "control" : function( widget, value ) {
      if( value !== null ) {
        org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( control ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( widget ) + "pg";
          org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( id, function( parent ) {
            control.setParent( parent );
          } );
        } );
      }
    },
    "toolTip" : org.eclipse.rwt.protocol.AdapterUtil.getControlPropertyHandler( "toolTip" )
  },

  listeners : [],

  listenerHandler : {},

  methods : []

} );