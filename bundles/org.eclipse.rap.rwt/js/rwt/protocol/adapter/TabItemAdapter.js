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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.TabItem", {

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
      var EncodingUtil = rwt.protocol.EncodingUtil;
      var text = EncodingUtil.escapeText( value, false );
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
        rwt.protocol.AdapterUtil.callWithTarget( value, function( control ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( widget ) + "pg";
          rwt.protocol.AdapterUtil.callWithTarget( id, function( parent ) {
            control.setParent( parent );
          } );
        } );
      }
    },
    "toolTip" : rwt.protocol.AdapterUtil.getControlPropertyHandler( "toolTip" )
  },

  listeners : [],

  listenerHandler : {},

  methods : []

} );
