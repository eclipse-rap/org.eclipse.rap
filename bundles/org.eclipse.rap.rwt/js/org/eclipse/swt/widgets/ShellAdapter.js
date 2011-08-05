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
    var styles = org.eclipse.rwt.protocol.Processor.createStyleMap( properties.style );
    var result = new org.eclipse.swt.widgets.Shell( styles );
    org.eclipse.rwt.protocol.Processor.addStatesForStyles( result, properties.style );
    result.initialize();
    result.setUserData( "isControl", true );
    return result;
  },

  properties : [
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
    // ControlLCAUtil properties from there on
    "zIndex",
    "tabIndex",
    "toolTip",
    "visibility",
    "enabled",
    "foreground",
    "background",
    "backgroundGradient",
    "cursor",
    "customVariant",
    "bounds",
    "font"
  ],

  propertyMapping : {
    "image" : "icon",
    "text" : "caption"
  },

  propertyHandler : {
    "alpha" : function( shell, alpha ) {
      shell.setOpacity( alpha / 255 );
    },
    "defaultButton" : function( shell, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.callbackForTargetId( value, function( widget ) {
        shell.setDefaultButton( widget );
      } );
    },
    "parentShell" : function( shell, value ) {
      var parent = org.eclipse.swt.WidgetManager.getInstance().findWidgetById( value );
      shell.setParentShell( parent );
    },
    "menu" : function( shell, value ) {
      var menu = org.eclipse.swt.WidgetManager.getInstance().findWidgetById( value );
      shell.setContextMenu( menu );
      var listener = org.eclipse.rwt.widgets.Menu.contextMenuHandler;
      if( menu == null ) {
        shell.removeEventListener( "contextmenu", listener ); 
      } else {
        shell.addEventListener( "contextmenu", listener );
      }
      
    },
    "mode" : function( shell, value ) {
      var fullscreen = value === "fullscreen";
      shell.setMode( fullscreen ? "maximized" : value );
      shell.setFullScreen( fullscreen );
    },
    "minimumSize" : function( shell, value ) {
      shell.setMinWidth( value[ 0 ] );
      shell.setMinHeight( value[ 1 ] );
    },
    // ControlLCAUtil actions from there on
    "foreground" : function( widget, value ) {
      if( value === null ) {
        widget.resetTextColor();
      } else {
        widget.setTextColor( value );
      }
    },
    "background" : function( widget, value ) {
      if( value === null ) {
        widget.resetBackgroundColor();
        widget.resetBackgroundGradient();
      } else {
        widget.setBackgroundGradient( null );
        widget.setBackgroundColor( value );
      }
    },
    "cursor" : function( widget, value ) {
      if( value === null ) {
        widget.resetCursor();
      } else {
        widget.setCursor( value );
      }
    },
    "bounds" : function( widget, value ) {
      widget.setLeft( value[ 0 ] );
      widget.setTop( value[ 1 ] );
      widget.setWidth( value[ 2 ] );
      widget.setHeight( value[ 3 ] );
    },
    "toolTip" : function( widget, toolTipText ) {
      if( toolTipText != null && toolTipText != "" ) {
        widget.setUserData( "toolTipText", toolTipText );
        var toolTip = org.eclipse.rwt.widgets.WidgetToolTip.getInstance()
        widget.setToolTip( toolTip );
        // make sure "boundToWidget" is initialized:
        if( toolTip.getParent() != null ) {  
          if( toolTip.getBoundToWidget() == widget ) {
            toolTip.updateText( widget );
          }
        }
      } else {
        this._removeToolTipPopup( widget );
      }
    },
    "font" : function( widget, fontData ) {
      if( widget.setFont ) { // test if font property is supported - why wouldn't it? [tb]
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        // TODO [tb] : move helper
        var font = wm._createFont.apply( wm, fontData );
        widget.setFont( font );
      }
    }
    
  },

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

  listenerHandler : {
    "activate" : function( widget, value ) {
      var shell = widget;
      while( shell && ! ( shell instanceof org.eclipse.swt.widgets.Shell ) ) {
        shell = shell.getParent();
      }
      if( shell ) {
        if( value ) {
          shell.addActivateListenerWidget( widget );
        } else {
          shell.removeActivateListenerWidget( widget );          
        }
      }
    },
    // ControlLCAUtil from here on
    "key" : function( widget, value ) {
      widget.setUserData( "keyListener", value ? true : null );
    },
    "traverse" : function( widget, value ) {
      widget.setUserData( "traverseListener", value ? true : null );
    }
  },

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