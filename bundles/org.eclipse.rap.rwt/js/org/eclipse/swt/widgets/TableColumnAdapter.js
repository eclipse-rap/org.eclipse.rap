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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.TableColumn", {

  factory : function( properties ) {
    var result;
    org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result = new org.eclipse.swt.widgets.TableColumn( parent );
    } );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getWidgetDestructor(),

  properties : [
    // Always set column index first
    "index",
    "left",
    "width",
    "text",
    "image",
    "toolTip",
    "resizable",
    "moveable",
    "alignment",
    "fixed",
    "customVariant"
  ],

  propertyHandler : {
    "text" : function( widget, value ) {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = encodingUtil.escapeText( value, false );
      text = encodingUtil.replaceNewLines( text, "<br/>" );
      widget.setLabel( text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setIcon( null );
      } else {
        widget.setIcon( value[ 0 ] );
      }
    },
    "toolTip" : org.eclipse.rwt.protocol.AdapterUtil.getControlPropertyHandler( "toolTip" )
  },

  listeners : [
    "selection"
  ],

  listenerHandler : {},

  methods : []

} );