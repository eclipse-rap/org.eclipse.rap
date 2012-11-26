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

namespace( "rwt.protocol" );

rwt.protocol.AdapterUtil = {

  _controlDestructor : function( widget ) {
    rwt.protocol.AdapterUtil._widgetDestructor( widget );
  },

  _childrenFinder : function( widget ) {
    return rwt.protocol.AdapterUtil.getDestroyableChildren( widget );
  },

  _widgetDestructor : function( widget ) {
    var parent = widget.getUserData( "protocolParent" );
    if( parent ) {
      rwt.protocol.AdapterUtil.removeDestroyableChild( parent, widget );
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
    "cancelKeys"
  ],

  _controlPropertyHandler : {
    "children" : function( widget, value ) {
      if( value !== null ) {
        var childrenCount = value.length;
        var applyZIndex = function( child ) {
          var index = value.indexOf( rwt.protocol.ObjectRegistry.getId( child ) );
          child.setZIndex( childrenCount - index );
        };
        for( var i = 0; i < childrenCount; i++ ) {
          rwt.protocol.AdapterUtil.callWithTarget( value[ i ], applyZIndex );
        }
      }
    },
    "foreground" : function( widget, value ) {
      if( value === null ) {
        widget.resetTextColor();
      } else {
        widget.setTextColor( rwt.util.ColorUtil.rgbToRgbString( value ) );
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
        var color = value[ 3 ] === 0 ? "transparent" : rwt.util.ColorUtil.rgbToRgbString( value );
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
        var EncodingUtil = rwt.protocol.EncodingUtil;
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
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          // TODO [tb] : move helper
          var font = wm._createFont.apply( wm, fontData );
          widget.setFont( font );
        }
      }
    },
    "menu" : function( widget, value ) {
      rwt.protocol.AdapterUtil.callWithTarget( value, function( menu ) {
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
      var map = rwt.util.Object.fromArray( value );
      widget.setUserData( "activeKeys", map );
    },
    "cancelKeys" : function( widget, value ) {
      var map = rwt.util.Object.fromArray( value );
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
      var context = org.eclipse.swt.EventUtil;
      var focusGained = org.eclipse.swt.EventUtil.focusGained;
      if( value ) {
        widget.addEventListener( "focusin", focusGained, context );
      } else {
        widget.removeEventListener( "focusin", focusGained, context );
      }
    },
    "FocusOut" : function( widget, value ) {
      var context = org.eclipse.swt.EventUtil;
      var focusLost = org.eclipse.swt.EventUtil.focusLost;
      if( value ) {
        widget.addEventListener( "focusout", focusLost, context );
      } else {
        widget.removeEventListener( "focusout", focusLost, context );
      }
    },
    "MouseDown" : function( widget, value ) {
      var context;
      var mouseDown = org.eclipse.swt.EventUtil.mouseDown;
      if( value ) {
        widget.addEventListener( "mousedown", mouseDown, context );
      } else {
        widget.removeEventListener( "mousedown", mouseDown, context );
      }
    },
    "MouseUp" : function( widget, value ) {
      var context;
      var mouseUp = org.eclipse.swt.EventUtil.mouseUp;
      if( value ) {
        widget.addEventListener( "mouseup", mouseUp, context );
      } else {
        widget.removeEventListener( "mouseup", mouseUp, context );
      }
    },
    "MouseDoubleClick" : function( widget, value ) {
      var context;
      var mouseDoubleClick = org.eclipse.swt.EventUtil.mouseDoubleClick;
      var mouseUpCounter = org.eclipse.swt.EventUtil.mouseUpCounter;
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
      var detectByKey = org.eclipse.swt.EventUtil.menuDetectedByKey;
      var detectByMouse = org.eclipse.swt.EventUtil.menuDetectedByMouse;
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
      var helpRequested = org.eclipse.swt.EventUtil.helpRequested;
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
          gradient[ i ] = [ percents[ i ] / 100, rwt.util.ColorUtil.rgbToRgbString( colors[ i ] ) ];
        }
        gradient.horizontal = !vertical;
      }
      widget.setBackgroundGradient( gradient );
    },
    "roundedBorder" : function( widget, value ) {
      if( value ) {
        var width = value[ 0 ];
        var color = rwt.util.ColorUtil.rgbToRgbString( value[ 1 ] );
        var radii = value.slice( -4 );
        var border = new org.eclipse.rwt.Border( width, "rounded", color, radii );
        widget.setBorder( border );
      } else {
        widget.resetBorder();
      }
    }
  },

  ////////////////////////////////
  // lists and handler for adapter

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
    return rwt.util.Object.mergeWith( handler, this._controlPropertyHandler, false );
  },

  extendControlListeners : function( list ) {
    return list.concat( this._controlListeners );
  },

  extendControlListenerHandler : function( handler ) {
    return rwt.util.Object.mergeWith( handler, this._controlListenerHandler, false );
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
    rwt.protocol.AdapterUtil.addDestroyableChild( parent, widget );
    widget.setUserData( "protocolParent", parent );
  },

  callWithTarget : function( id, fun ) {
    if( id == null ) {
      fun( null );
    } else {
      var target = rwt.protocol.ObjectRegistry.getObject( id );
      if( target ) {
        fun( target );
      } else {
        rwt.protocol.ObjectRegistry.addRegistrationCallback( id, fun );
      }
    }
  },

  filterUnregisteredObjects : function( list ) {
    var ObjectRegistry = rwt.protocol.ObjectRegistry;
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

  addDestroyableChild : function( parent, child ) {
    var list = parent.getUserData( "destroyableChildren" );
    if( list == null ) {
      list = {};
      parent.setUserData( "destroyableChildren", list );
    }
    list[ qx.core.Object.toHashCode( child ) ] = child;
  },

  removeDestroyableChild : function( parent, child ) {
    var list = parent.getUserData( "destroyableChildren" );
    if( list != null ) {
      delete list[ qx.core.Object.toHashCode( child ) ];
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
  }

};
