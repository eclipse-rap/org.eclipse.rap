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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Menu", {

  factory : function( properties ) {
    var result;
    if( properties.style.indexOf( "BAR" ) != -1 ) {
      result = new org.eclipse.rwt.widgets.MenuBar();
    } else {
      result = new org.eclipse.rwt.widgets.Menu();
    }
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getWidgetDestructor(),

  properties : [
    "parent",
    "bounds",
    "enabled",
    "customVariant"
  ],

  propertyHandler : {
    "parent" : function( widget, value ) {
      if( widget.hasState( "rwt_BAR" ) ) {
        org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( parent ) {
          widget.setParent( parent );
        } );
      }
    },
    "bounds" : function( widget, value ) {
      if( widget.hasState( "rwt_BAR" ) ) {
        widget.setLeft( value[ 0 ] );
        widget.setTop( value[ 1 ] );
        widget.setWidth( value[ 2 ] );
        widget.setHeight( value[ 3 ] );
      }
    }
  },

  listeners : [
    "menu",
    "help"
  ],

  listenerHandler : {
    "menu" : function( widget, value ) {
      if( !widget.hasState( "rwt_BAR" ) ) {
        widget.setHasMenuListener( value );
      }
    },
    "help" : org.eclipse.rwt.protocol.AdapterUtil.getControlListenerHandler( "help" )
  },

  methods : [
    "unhideItems",
    "showMenu"
  ],
  
  methodHandler : {
    "unhideItems" : function( widget, args ) {
      if( !widget.hasState( "rwt_BAR" ) ) {
        widget.unhideItems( args.reveal );
      }
    },
    "showMenu" : function( widget, args ) {
      if( widget.hasState( "rwt_POP_UP" ) ) {
        widget.showMenu( widget, args.x, args.y );
      }
    }
  }

} );