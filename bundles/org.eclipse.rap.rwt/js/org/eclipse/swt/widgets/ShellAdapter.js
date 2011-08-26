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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Shell", {

  factory : function( properties ) {
    var adapterUtil = org.eclipse.rwt.protocol.AdapterUtil;
    var styles = adapterUtil.createStyleMap( properties.style );
    var result = new org.eclipse.swt.widgets.Shell( styles );
    adapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    adapterUtil.callWithTarget( properties.parentShell, function( parentShell ) {
      if( parentShell ) {
        result.setParentShell( parentShell );    
      }
      result.initialize();
      result.show();
    } );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "showMinimize", 
    "allowMinimize", 
    "showMaximize", 
    "allowMaximize", 
    "showClose", 
    "allowClose",
    "resizable",
    "image",
    "text",
    "alpha",
    "active",
    "mode",
    "fullScreen",
    "hasShellListener",
    "minimumSize",
    "defaultButton",
    "menu",
    "activeControl"
  ] ),

  propertyMapping : {
    "image" : "icon",
    "text" : "caption"
  },

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    // Overwrites original bounds handler, see bug 306042 and 354597
    "bounds" : function( widget, value ) {
      if( !widget.isDisableResize() ) {
        widget.setLeft( value[ 0 ] );
        widget.setTop( value[ 1 ] );
        widget.setWidth( value[ 2 ] );
        widget.setHeight( value[ 3 ] );
      }
    },
    "alpha" : function( shell, alpha ) {
      shell.setOpacity( alpha / 255 );
    },
    "defaultButton" : function( shell, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( widget ) {
        shell.setDefaultButton( widget );
      } );
    },
    "activeControl" : function( shell, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( widget ) {
        shell.setActiveControl( widget );
      } );
    },
    "menu" : function( shell, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( menu ) {
        shell.setContextMenu( menu );
        var listener = org.eclipse.rwt.widgets.Menu.contextMenuHandler;
        if( menu == null ) {
          shell.removeEventListener( "contextmenu", listener ); 
        } else {
          shell.addEventListener( "contextmenu", listener );
        }
      } );     
    },
    "mode" : function( shell, value ) {
      var fullscreen = value === "fullscreen";
      shell.setMode( fullscreen ? "maximized" : value );
      shell.setFullScreen( fullscreen );
    },
    "minimumSize" : function( shell, value ) {
      shell.setMinWidth( value[ 0 ] );
      shell.setMinHeight( value[ 1 ] );
    }
  } ),

  knownListeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "shell"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  knownMethods : [
    "allowEvent",
    "cancelEvent"
  ],

  methodHandler : {
    "allowEvent" : function( widget, properties ) {
      org.eclipse.rwt.KeyEventUtil.getInstance().allowEvent();
    },
    "cancelEvent" : function( widget, properties ) {
      org.eclipse.rwt.KeyEventUtil.getInstance().cancelEvent();
    }
  }

} );