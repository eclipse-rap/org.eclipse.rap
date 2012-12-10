/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

/**
 * Maps widget id's to their respective object references. Allows for
 * adding, removing and disposing of widgets and their id. In addition
 * the mapping of widgets and their respective id's can be queried.
 */
qx.Class.define( "org.eclipse.swt.WidgetManager", {

  type : "singleton",
  extend : qx.core.Object,

  construct : function() {
    this.base( arguments );

    // this field is needed as Opera has some problems with
    // accessing local variables in eval expressions.
    this._current = null;

    this._fontPool = {};
  },

  statics : {

    _onAppearFocus : function( evt ) {
      var widget = this;
      widget.focus();
      widget.removeEventListener( "appear",
                                  org.eclipse.swt.WidgetManager._onAppearFocus,
                                  widget );
    }
  },

  members : {
    /**
     * Disposes of the widget that is registered with the given id. The widget
     * is disconnected from its parent, its 'dispose' method is called and it is
     * removed from this WidgetManager (see remove).
     * No action is taken if there is no widget registered for the given id or
     * the widget was already disposed of.
     */
    dispose : function( id ) {
      var widget = this.findWidgetById( id );
      if( widget != null ) {
        this.remove( widget );
        if( !widget.getDisposed() ) {
          this._removeToolTipPopup( widget );
          widget.destroy();
        }
      }
    },

    /**
     * Registers the given widget under the given id at the WidgetManager.
     */
    add : function( widget, id, isControl, adapter ) {
      if( isControl === true ) {
        widget.setUserData( "isControl", true );
      }
      rwt.protocol.ObjectRegistry.add( id, widget, adapter );
    },

    /**
     * Unregisters the given widget at the WidgetManager. Note that the widget is
     * not disposed of.
     */
    remove : function( widget ) {
      var id = this.findIdByWidget( widget );
      rwt.protocol.ObjectRegistry.remove( id );
    },

    /**
     * Returns the widget for the given id or null if there is no widget
     * registered for the given id exists.
     */
    findWidgetById : function( id ) {
      return rwt.protocol.ObjectRegistry.getObject( id );
    },

    /**
     * Returns the id (string) for the given widget or null if the widget is not
     * registered.
     */
    findIdByWidget : function( widget ) {
      return rwt.protocol.ObjectRegistry.getId( widget );
    },

    /**
     * Determines whether the given widget represents a server-side instance of
     * Control (or one of its subclasses)
     */
    isControl : function( widget ) {
      var data = null;
      if( widget != null ) {
        data = widget.getUserData( "isControl" );
      }
      return data === true;
    },

    /**
     * Returns the nearest SWT-control in the hierarchy for the given qxWidget
     * or null if no parent control could be found. If the given qxWidget
     * represents a control, it is returned.
     */
    findControl : function( qxWidget ) {
      var parent = qxWidget;
      while( parent != null && !this.isControl( parent ) ) {
        parent = parent.getParent ? parent.getParent() : null;
      }
      return parent;
    },

    findEnabledControl : function( qxWidget ) {
      var parent = qxWidget;
      while( parent != null && !( this.isControl( parent ) && parent.getEnabled() ) ) {
        parent = parent.getParent ? parent.getParent() : null;
      }
      return parent;
    },

    /**
     * Adds the given widget to the children of the widget denoted by parentId
     */
    setParent : function( widget, parentId ) {
      var parent = this.findWidgetById( parentId );
      // TODO [rh] there seems to be a difference between add and setParent
      //      when using add sizes and clipping are treated differently
      // parent.add( widget );
      if( parent instanceof rwt.widgets.ScrolledComposite ) {
        // [if] do nothing, parent is set in ScrolledComposite#setContent which is called from the
        // server-side - see bug 349161
      } else if ( parent instanceof rwt.widgets.TabFolder ) {
        // [if] do nothing, parent is set when the control is set on the tab item
      } else if( parent instanceof rwt.widgets.ExpandBar ) {
        parent.addWidget( widget );
      } else {
        widget.setParent( parent );
      }
    },

    setHtmlId : function( widget, id ) {
      // Test if 'widget' isn't undefined and provides a setHtmlProperty method,
      // fail silently otherwise
      if(    widget instanceof Object
          && typeof widget.setHtmlProperty == "function" )
      {
        widget.setHtmlProperty( "id", id );
      }
    },

    focus : function( widgetId ) {
      var widget = this.findWidgetById( widgetId );
      if( widget.isSeeable() ) {
        widget.focus();
      } else {
        widget.addEventListener( "appear", org.eclipse.swt.WidgetManager._onAppearFocus, widget );
      }
    },

    ////////////////
    // Font handling

    setFont : function( widget, name, size, bold, italic ) {
      if( widget.setFont ) { // test if font property is supported
        var font = this._createFont( name, size, bold, italic );
        widget.setFont( font );
      }
    },

    _createFont : function( name, size, bold, italic ) {
      var id = name + size + bold + italic;
      var font = this._fontPool[ id ];
      if( !font ) {
        font = new qx.ui.core.Font( size, name );
        font.setBold( bold );
        font.setItalic( italic );
        this._fontPool[ id ] = font;
      }
      return font;
    },

    ////////////////////
    // ToolTip handling

    /**
     * Sets the toolTipText for the given widget. An empty or null toolTipText
     * removes the tool tip of the widget.
     */
    setToolTip : function( widget, toolTipText ) {
      if( toolTipText != null && toolTipText !== "" ) {
        widget.setUserData( "toolTipText", toolTipText );
        var toolTip = rwt.widgets.base.WidgetToolTip.getInstance();
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

    _removeToolTipPopup : function( widget ) {
      widget.setToolTip( null );
      widget.setUserData( "toolTipText", null );
    },

    ///////////////////////////////
    // Background gradient handling

    /**
     * Sets the background gradient for the given widget. A null colors or null
     * percents removes the background gradient of the widget.
     */
    setBackgroundGradient : function( widget,
                                      gradientColor,
                                      percents,
                                      vertical )
    {
      var gradient = null;
      if( gradientColor != null && percents != null ) {
        gradient = [];
        for( var i = 0; i < gradientColor.length; i++ ) {
          gradient[ i ] = [ percents[ i ] / 100, gradientColor[ i ] ];
        }
        gradient.horizontal = !vertical;
      }
      widget.setBackgroundGradient( gradient );
    },

    //////////////////////////
    // Rounded border handling

    /**
     * Sets the rounded border for the given widget. A zero border width,
     * null color or non positive radii removes the rounded border of the widget.
     */
    setRoundedBorder : function( widget,
                                 width,
                                 color,
                                 topLeftRadius,
                                 topRightRaduis,
                                 bottomRightRaduis,
                                 bottomLeftRaduis )
    {
      if(    width > 0
          && color != null
          && topLeftRadius >= 0
          && topRightRaduis >= 0
          && bottomRightRaduis >= 0
          && bottomLeftRaduis >= 0 )
      {
        var radii = [ topLeftRadius, topRightRaduis, bottomRightRaduis, bottomLeftRaduis ];
        var border = new org.eclipse.rwt.Border( width, "rounded", color, radii );
        widget.setBorder( border );
      } else {
        widget.resetBorder();
      }
    },

    ////////////////////////
    // Context menu handling

    /**
     * Sets the context menu for the given widget.
     */
    setContextMenu : function( widget, menu ) {
      widget.setContextMenu( menu );
      if( menu == null ) {
        this._removeListener( widget, "contextMenu" );
      } else {
        this._addListener( widget, "contextMenu" );
      }
    },

    /////////////////////////
    // Common events handling

    /**
     * Add/remove event listener with specific type for the given widget.
     */
    setHasListener : function( widget, eventType, hasListener ) {
      if( hasListener ) {
        this._addListener( widget, eventType );
      } else {
        this._removeListener( widget, eventType );
      }
    },

    _addListener : function( targetObject, eventType ) {
      var list = this._listenerMap[ eventType ];
      for( var i = 0; i < list.length; i++ ) {
        targetObject.addEventListener( list[ i ].nativeType,
                                       list[ i ].listener,
                                       list[ i ].context );
      }
    },

    _removeListener : function( targetObject, eventType ) {
      var list = this._listenerMap[ eventType ];
      for( var i = 0; i < list.length; i++ ) {
        targetObject.removeEventListener( list[ i ].nativeType,
                                          list[ i ].listener,
                                          list[ i ].context );
      }
    },

    _listenerMap : {
      "focus" : [
        {
          nativeType : "focusin",
          context : undefined,
          listener : org.eclipse.swt.EventUtil.focusGained
        },
        {
          nativeType : "focusout",
          context : undefined,
          listener : org.eclipse.swt.EventUtil.focusLost
        }
      ],
      "mouse" : [
        {
          nativeType : "mousedown",
          context : undefined,
          listener : org.eclipse.swt.EventUtil.mouseDown
        },
        {
          nativeType : "mouseup",
          context : undefined,
          listener : org.eclipse.swt.EventUtil.mouseUp
        }
      ],
      "help" : [
        {
          nativeType : "keydown",
          context : undefined,
          listener : org.eclipse.swt.EventUtil.helpRequested
        }
      ],
      "contextMenu" : [
        {
          nativeType : "keydown",
          context : undefined,
          listener : rwt.widgets.Menu.menuDetectedByKey
        },
        {
          nativeType : "mouseup",
          context : undefined,
          listener : rwt.widgets.Menu.menuDetectedByMouse
        }
      ],
      "menuDetect" : [
        {
          nativeType : "keydown",
          context : undefined,
          listener : org.eclipse.swt.EventUtil.menuDetectedByKey
        },
        {
          nativeType : "mouseup",
          context : undefined,
          listener : org.eclipse.swt.EventUtil.menuDetectedByMouse
        }
      ]
    }

  }
});
