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

org.eclipse.rwt.protocol.AdapterRegistry.add( "org.eclipse.swt.widgets.Shell", {

  factory : function( properties ) {
    var styles = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    var result = new org.eclipse.swt.widgets.Shell( styles );
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.initialize();
    result.setUserData( "isControl", true );
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
    "parentShell",
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
    "parentShell" : function( shell, value ) {
      var parent = org.eclipse.swt.WidgetManager.getInstance().findWidgetById( value );
      shell.setParentShell( parent );
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

  knownListeners : [
    "shell",
    // ControlLCAUtil from here on
    "focus",
    "mouse",
    "key",
    "traverse",
    "menuDetect",
    "help",
    "activate"
  ],

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {
    "activate" : function( widget, value ) {
      var shell = org.eclipse.rwt.protocol.AdapterUtil.getShell( widget );
      if( shell ) {
        if( value ) {
          shell.addActivateListenerWidget( widget );
        } else {
          shell.removeActivateListenerWidget( widget );          
        }
      }
    }
  } ),

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