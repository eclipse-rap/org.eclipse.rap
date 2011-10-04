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
    var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( widget );
    org.eclipse.swt.TabUtil.releaseTabItem( id );
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
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( control ) {
        if( control !== null ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( widget );
          control.setParent( widgetManager.findWidgetById( id + "pg" ) );
        }
      } );
    },
    "toolTip" : org.eclipse.rwt.protocol.AdapterUtil.getControlPropertyHandler( "toolTip" )
  },

  listeners : [],

  listenerHandler : {},

  methods : []

} );