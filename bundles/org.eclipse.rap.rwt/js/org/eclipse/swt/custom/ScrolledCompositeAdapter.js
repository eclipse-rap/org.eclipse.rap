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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.ScrolledComposite", {

  factory : function( properties ) {
    var result = new org.eclipse.swt.custom.ScrolledComposite();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "origin",
    "content",
    "showFocusedControl",
    "scrollBarsVisible"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    // Override original bounds handler to set clipWidth and clipHeight
    "bounds" : function( widget, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.getControlPropertyHandler( "bounds" )( widget, value );
      widget.setClipWidth( value[ 2 ] );
      widget.setClipHeight( value[ 3 ] );
    },
    // Order is important: origin before scrollBarsVisible
    "origin" : function( widget, value ) {
      widget.setHBarSelection( value[ 0 ] );
      widget.setVBarSelection( value[ 1 ] );
    },
    "content" : function( widget, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( content ) {
        widget.setContent( content );        
      } );
    },
    "scrollBarsVisible" : function( widget, value ) {
      widget.setScrollBarsVisible( value[ 0 ], value[ 1 ] );
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "scrollBarsSelection"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {
    "scrollBarsSelection" : function( widget, value ) {
      widget.setHasSelectionListener( value );
    }
  } ),

  methods : []

} );