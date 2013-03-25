/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "rwt.remote" );

rwt.remote.HandlerUtil = {

  SERVER_DATA : "org.eclipse.swt.widgets.Widget#data",

  _controlDestructor : function( widget ) {
    rwt.remote.HandlerUtil._widgetDestructor( widget );
  },

  _childrenFinder : function( widget ) {
    return rwt.remote.HandlerUtil.getDestroyableChildren( widget );
  },

  _widgetDestructor : function( widget ) {
    var parent = widget.getUserData( "protocolParent" );
    if( parent ) {
      rwt.remote.HandlerUtil.removeDestroyableChild( parent, widget );
    }
    widget.setToolTip( null );
    widget.setUserData( "toolTipText", null );
    widget.destroy();
  },

  _controlProperties : [
    "children",
    "tabIndex",
    "toolTip",
    "visibility",
    "enabled",
    "foreground",
    "background",
    "backgroundImage",
    "cursor",
    "customVariant",
    "bounds",
    "font",
    "menu",
    "activeKeys",
    "cancelKeys",
    "data"
  ],

  _controlPropertyHandler : {
    "data" : function( target, value ) {
      var map = rwt.remote.HandlerUtil.getServerData( target );
      rwt.util.Objects.mergeWith( map, value );
    },
    "children" : function( widget, value ) {
      if( value !== null ) {
        var childrenCount = value.length;
        var applyZIndex = function( child ) {
          var index = value.indexOf( rwt.remote.ObjectRegistry.getId( child ) );
          child.setZIndex( childrenCount - index );
        };
        for( var i = 0; i < childrenCount; i++ ) {
          rwt.remote.HandlerUtil.callWithTarget( value[ i ], applyZIndex );
        }
      }
      widget.setUserData( "rwt_Children", value );
    },
    "foreground" : function( widget, value ) {
      if( value === null ) {
        widget.resetTextColor();
      } else {
        widget.setTextColor( rwt.util.Colors.rgbToRgbString( value ) );
      }
    },
    "background" : function( widget, value ) {
      if( value === null ) {
        widget.resetBackgroundColor();
        if( widget.__user$backgroundGradient == null ) {
          widget.resetBackgroundGradient();
        }
      } else {
        if( widget.__user$backgroundGradient == null ) {
          widget.setBackgroundGradient( null );
        }
        var color = value[ 3 ] === 0 ? "transparent" : rwt.util.Colors.rgbToRgbString( value );
        widget.setBackgroundColor( color );
      }
    },
    "backgroundImage" : function( widget, value ) {
      if( value === null ) {
        widget.resetBackgroundImage();
        widget.setUserData( "backgroundImageSize", null );
      } else {
        widget.setUserData( "backgroundImageSize", value.slice( 1 ) );
        widget.setBackgroundImage( value[ 0 ] );
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
      var bounds = value;
      if( widget.getUserData( "tabFolder" ) !== null ) {
        bounds[ 0 ] = 0;
        bounds[ 1 ] = 0;
      }
      if( widget.getUserData( "scrolledComposite" ) === null ) {
        widget.setLeft( bounds[ 0 ] );
        widget.setTop( bounds[ 1 ] );
      }
      widget.setWidth( bounds[ 2 ] );
      widget.setHeight( bounds[ 3 ] );
    },
    "toolTip" : function( widget, value ) {
      if( value != null && value !== "" ) {
        var EncodingUtil = rwt.util.Encoding;
        var text = EncodingUtil.escapeText( value, false );
        text = EncodingUtil.replaceNewLines( text, "<br/>" );
        widget.setUserData( "toolTipText", text );
        var toolTip = rwt.widgets.base.WidgetToolTip.getInstance();
        widget.setToolTip( toolTip );
        // make sure "boundToWidget" is initialized:
        if( toolTip.getParent() != null ) {
          if( toolTip.getBoundToWidget() == widget ) {
            toolTip.updateText( widget );
          }
        }
      } else {
        widget.setToolTip( null );
        widget.setUserData( "toolTipText", null );
      }
    },
    "font" : function( widget, fontData ) {
      if( widget.setFont ) { // test if font property is supported - why wouldn't it? [tb]
        if( fontData === null ) {
          widget.resetFont();
        } else {
          var font = rwt.html.Font.fromArray( fontData );
          widget.setFont( font );
        }
      }
    },
    "menu" : function( widget, value ) {
      rwt.remote.HandlerUtil.callWithTarget( value, function( menu ) {
        widget.setContextMenu( menu );
        var detectByKey = rwt.widgets.Menu.menuDetectedByKey;
        var detectByMouse = rwt.widgets.Menu.menuDetectedByMouse;
        if( menu == null ) {
          widget.removeEventListener( "keydown", detectByKey );
          widget.removeEventListener( "mouseup", detectByMouse );
        } else {
          widget.addEventListener( "keydown", detectByKey );
          widget.addEventListener( "mouseup", detectByMouse );
        }
      } );
    },
    "activeKeys" : function( widget, value ) {
      var map = rwt.util.Objects.fromArray( value );
      widget.setUserData( "activeKeys", map );
    },
    "cancelKeys" : function( widget, value ) {
      var map = rwt.util.Objects.fromArray( value );
      widget.setUserData( "cancelKeys", map );
    }
  },

  _controlListeners : [
    "FocusIn",
    "FocusOut",
    "MouseDown",
    "MouseUp",
    "MouseDoubleClick",
    "KeyDown",
    "Traverse",
    "MenuDetect",
    "Help",
    "Activate",
    "Deactivate"
  ],

  _controlListenerHandler : {
    "KeyDown" : function( widget, value ) {
      widget.setUserData( "keyListener", value ? true : null );
    },
    "Traverse" : function( widget, value ) {
      widget.setUserData( "traverseListener", value ? true : null );
    },
    "FocusIn" : function( widget, value ) {
      var context = rwt.remote.EventUtil;
      var focusGained = rwt.remote.EventUtil.focusGained;
      if( value ) {
        widget.addEventListener( "focusin", focusGained, context );
      } else {
        widget.removeEventListener( "focusin", focusGained, context );
      }
    },
    "FocusOut" : function( widget, value ) {
      var context = rwt.remote.EventUtil;
      var focusLost = rwt.remote.EventUtil.focusLost;
      if( value ) {
        widget.addEventListener( "focusout", focusLost, context );
      } else {
        widget.removeEventListener( "focusout", focusLost, context );
      }
    },
    "MouseDown" : function( widget, value ) {
      var context;
      var mouseDown = rwt.remote.EventUtil.mouseDown;
      if( value ) {
        widget.addEventListener( "mousedown", mouseDown, context );
      } else {
        widget.removeEventListener( "mousedown", mouseDown, context );
      }
    },
    "MouseUp" : function( widget, value ) {
      var context;
      var mouseUp = rwt.remote.EventUtil.mouseUp;
      if( value ) {
        widget.addEventListener( "mouseup", mouseUp, context );
      } else {
        widget.removeEventListener( "mouseup", mouseUp, context );
      }
    },
    "MouseDoubleClick" : function( widget, value ) {
      var context;
      var mouseDoubleClick = rwt.remote.EventUtil.mouseDoubleClick;
      var mouseUpCounter = rwt.remote.EventUtil.mouseUpCounter;
      if( value ) {
        widget.addEventListener( "mousedown", mouseDoubleClick, context );
        widget.addEventListener( "mouseup", mouseUpCounter, context );
      } else {
        widget.removeEventListener( "mousedown", mouseDoubleClick, context );
        widget.removeEventListener( "mouseup", mouseUpCounter, context );
      }
    },
    "MenuDetect" : function( widget, value ) {
      var context;
      var detectByKey = rwt.remote.EventUtil.menuDetectedByKey;
      var detectByMouse = rwt.remote.EventUtil.menuDetectedByMouse;
      if( value ) {
        widget.addEventListener( "keydown", detectByKey, context );
        widget.addEventListener( "mouseup", detectByMouse, context );
      } else {
        widget.removeEventListener( "keydown", detectByKey, context );
        widget.removeEventListener( "mouseup", detectByMouse, context );
      }
    },
    "Help" : function( widget, value ) {
      var context;
      var helpRequested = rwt.remote.EventUtil.helpRequested;
      if( value ) {
        widget.addEventListener( "keydown", helpRequested, context );
      } else {
        widget.removeEventListener( "keydown", helpRequested, context );
      }
    },
    "Activate" : function( widget, value ) {
      widget.setUserData( "activateListener", value ? true : null );
    },
    "Deactivate" : function( widget, value ) {
      widget.setUserData( "deactivateListener", value ? true : null );
    }
  },

  _specialHandler : {
    "backgroundGradient" : function( widget, value ) {
      var gradient = null;
      if( value ) {
        var colors = value[ 0 ];
        var percents = value[ 1 ];
        var vertical = value[ 2 ];
        gradient = [];
        for( var i = 0; i < colors.length; i++ ) {
          gradient[ i ] = [ percents[ i ] / 100, rwt.util.Colors.rgbToRgbString( colors[ i ] ) ];
        }
        gradient.horizontal = !vertical;
      }
      widget.setBackgroundGradient( gradient );
    },
    "roundedBorder" : function( widget, value ) {
      if( value ) {
        var width = value[ 0 ];
        var color = rwt.util.Colors.rgbToRgbString( value[ 1 ] );
        var radii = value.slice( -4 );
        var border = new rwt.html.Border( width, "rounded", color, radii );
        widget.setBorder( border );
      } else {
        widget.resetBorder();
      }
    }
  },

  ////////////////////
  // lists and handler

  getWidgetDestructor : function() {
    return this._widgetDestructor;
  },

  getControlDestructor : function() {
    return this._controlDestructor;
  },

  getDestroyableChildrenFinder : function( widget ) {
    return this._childrenFinder;
  },

  extendControlProperties : function( list ) {
    return list.concat( this._controlProperties );
  },

  extendControlPropertyHandler : function( handler ) {
    return rwt.util.Objects.mergeWith( handler, this._controlPropertyHandler, false );
  },

  extendControlListeners : function( list ) {
    return list.concat( this._controlListeners );
  },

  extendControlListenerHandler : function( handler ) {
    return rwt.util.Objects.mergeWith( handler, this._controlListenerHandler, false );
  },

  getBackgroundGradientHandler : function() {
    return this._specialHandler.backgroundGradient;
  },

  getRoundedBorderHandler : function() {
    return this._specialHandler.roundedBorder;
  },

  getControlPropertyHandler : function( property ) {
    return this._controlPropertyHandler[ property ];
  },

  getControlListenerHandler : function( handler ) {
    return this._controlListenerHandler[ handler ];
  },

  /////////////////////
  // Helper for handler

  addStatesForStyles : function( targetObject, styleArray ) {
    for( var i = 0; i < styleArray.length; i++ ) {
      targetObject.addState( "rwt_" + styleArray[ i ] );
    }
    targetObject._renderAppearance();
    delete targetObject._isInGlobalStateQueue;
  },

  createStyleMap : function( styleArray ) {
    var result = {};
    for( var i = 0; i < styleArray.length; i++ ) {
      result[ styleArray[ i ] ] = true;
    }
    return result;
  },

  setParent : function( widget, parentId ) {
    var impl = this._setParentImplementation;
    this.callWithTarget( parentId, function( parent ) {
      impl( widget, parent );
    } );
  },

  _setParentImplementation : function( widget, parent ) {
    // TODO [rh] there seems to be a difference between add and setParent
    //      when using add sizes and clipping are treated differently
    // parent.add( widget );
    if( parent instanceof rwt.widgets.ScrolledComposite ) {
      // [if] do nothing, parent is set in ScrolledComposite#setContent which is called from the
      // server-side - see bug 349161
      widget.setUserData( "scrolledComposite", parent ); // Needed by "bounds" handler
    } else if ( parent instanceof rwt.widgets.TabFolder ) {
      widget.setUserData( "tabFolder", parent ); // Needed by "bounds" handler
    } else if( parent instanceof rwt.widgets.ExpandBar ) {
      parent.addWidget( widget );
    } else {
      widget.setParent( parent );
    }
    rwt.remote.HandlerUtil.addDestroyableChild( parent, widget );
    widget.setUserData( "protocolParent", parent );
  },

  callWithTarget : function( id, fun ) {
    if( id == null ) {
      fun( null );
    } else {
      var target = rwt.remote.ObjectRegistry.getObject( id );
      if( target ) {
        fun( target );
      } else {
        rwt.remote.ObjectRegistry.addRegistrationCallback( id, fun );
      }
    }
  },

  filterUnregisteredObjects : function( list ) {
    var ObjectRegistry = rwt.remote.ObjectRegistry;
    var result = [];
    for( var i = 0; i < list.length; i++ ) {
      if( ObjectRegistry.getId( list[ i ] ) ) {
        result.push( list[ i ] );
      }
    }
    return result;
  },

  getShell : function( widget ) {
    var result = widget;
    while( result && !( result instanceof rwt.widgets.Shell ) ) {
      result = result.getParent();
    }
    return result;
  },

  // TODO : Can we use "children" property in most cases instead??
  addDestroyableChild : function( parent, child ) {
    var list = parent.getUserData( "destroyableChildren" );
    if( list == null ) {
      list = {};
      parent.setUserData( "destroyableChildren", list );
    }
    list[ rwt.qx.Object.toHashCode( child ) ] = child;
  },

  removeDestroyableChild : function( parent, child ) {
    var list = parent.getUserData( "destroyableChildren" );
    if( list != null ) {
      delete list[ rwt.qx.Object.toHashCode( child ) ];
    }
  },

  getDestroyableChildren : function( parent ) {
    var list = parent.getUserData( "destroyableChildren" );
    if( list == null ) {
      list = {};
    }
    var result = [];
    for( var key in list ) {
      result.push( list[ key ] );
    }
    return result;
  },

  getServerData : function( target ) {
    var result = target.getUserData( rwt.remote.HandlerUtil.SERVER_DATA );
    if( result == null ) {
      result = {};
      target.setUserData( rwt.remote.HandlerUtil.SERVER_DATA, result );
    }
    return result;
  }

};
