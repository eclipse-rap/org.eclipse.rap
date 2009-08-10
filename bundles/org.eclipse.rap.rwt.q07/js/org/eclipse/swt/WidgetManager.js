/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
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
    // Holds the association between widget-id's and widget-instances.
    // Key: id (string), value: widget instanace (qx.ui.core.Widget)
    this._map = {};
    
    // this field is needed as Opera has some problems with
    // accessing local variables in eval expressions.
    this._current = null;
    
    this._fontPool = new Object();
  },

  statics : {

    _onAppearFocus : function( evt ) {
      var widget = this;
      widget.focus();
      evt.getTarget().removeEventListener( 
        "appear", 
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
          // TODO [rh] workaround for disposing of a Sash: if( parent && ...
          var parent = widget.getParent();
          if( parent && parent.getChildren() ) {
            widget.setParent( null );
          }
          // [if] Replace dispose() with destroy()
          widget.destroy();
        }
      }
    },
    
    newWidget : function( widgetId, parentId, isControl, type, paramList ) {
      // Note [fappel]: Do not remove the 'wm' declaration. This is needed
      //                for IE if the 'newExpression' has a reference to
      //                the variable defined in the script from the server.
      // TODO [fappel]: Think about improvement of the hardcoded expression... 
      var wm = this;
      
      var newExpression;
      if( paramList != null ) {
        newExpression = "new " + type + "(" + paramList + ");";
      } else {
        newExpression = "new " + type + "();";
      }
      var result = window.eval( newExpression );
      
      // map the widget to the server side widgetId
      if( result.classname == "org.eclipse.swt.widgets.Shell" ) {
        // TODO [rh] HACK: when a Shell is created, the isControl parameter is
        //      set to false so that setParent isn't called (see below).
        //      But to keep the activeControl mechanism working, it must be 
        //      added to the WidgetManager with the isCcontrol flag set to true     
        this.add( result, widgetId, true );
      } else {
        this.add( result, widgetId, isControl );
      }
      // insert controls into the widget tree
      if( isControl ) {
        this.setParent( result, parentId );
      }
      return result;
    },

    /**
     * Registers the given widget under the given id at the WidgetManager.
     */
    add : function( widget, id, isControl ) {
      this._map[ id ] = widget;
      if( isControl != "undefined" && isControl == true ) {
        widget.setUserData( "isControl", true );
      }
      widget.setUserData( "id", id );
    },

    /**
     * Unregisters the given widget at the WidgetManager. Note that the widget is
     * not disposed of.
     */
    remove : function( widget ) {
      var id = this.findIdByWidget( widget );
      delete this._map[ id ];
    },

    /**
     * Returns the widget for the given id or null if there is no widget 
     * registered for the given id exists.
     */
    findWidgetById : function( id ) {
      var result = this._map[ id ];
      if( result == null ) {
        this.warn( "No widget registered for id " + id );
      }
      return result;
    },

    /**
     * Returns the id (string) for the given widget or null if the widget is not
     * registered.
     */
    findIdByWidget : function( widget ) {
      var result;
      if( widget != null ) {
        result = widget.getUserData( "id" );
      }
      return result;
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
      return data != null && data == true;
    },

    /**
     * Adds the given widget to the children of the widget denoted by parentId
     */
    setParent : function( widget, parentId ) {
      var parent = this.findWidgetById( parentId );
      // TODO [rh] there seems to be a difference between add and setParent
      //      when using add sizes and clipping are treated differently
      // parent.add( widget );
      widget.setParent( parent );
    },
    
    setHtmlId : function( widget, id ) {
      // Test if 'widget' provides a setHtmlProperty method, fail silently
      // otherwise 
      if( widget.setHtmlProperty ) {
        widget.setHtmlProperty( "id", id );
      }
    },

    focus : function( widgetId ) {
      var widget = this.findWidgetById( widgetId );
      if( widget.isMaterialized() ) {
        widget.focus();
      } else {
        widget.addEventListener( "appear", 
                                 org.eclipse.swt.WidgetManager._onAppearFocus, 
                                 widget );
      }
    },

    ////////////////
    // Font handling

    setFont : function( widget, name, size, bold, italic ) {
      if( widget.setFont ) { // test if font property is supported
        var font = this._createFont( name, size, bold, italic );
        widget.setFont( font );
      } else {
        this.debug( widget.classname + " does not support fonts" );
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
      if( toolTipText != null && toolTipText != "" ) {
        widget.setUserData( "toolTipText", toolTipText );
        widget.setToolTip( org.eclipse.rwt.widgets.ToolTip.getInstance() );
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
    setBackgroundGradient : function( widget, gradientColor, percents ) {
      var gradient = null;
      if( gradientColor != null && percents != null ) {
        gradient = new Array();
        for( var i = 0; i < gradientColor.length; i++ ) {
          gradient[ i ] = [ percents[ i ] / 100, gradientColor[ i ] ];
        }
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
          && bottomLeftRaduis >= 0 ) {
        var border = new org.eclipse.rwt.RoundedBorder( width, color );
        border.setRadii( [ topLeftRadius,
                           topRightRaduis,
                           bottomRightRaduis,
                           bottomLeftRaduis ] );
        widget.setBorder( border );
      } else {
        widget.resetBorder();
      }
    }
  }
});
