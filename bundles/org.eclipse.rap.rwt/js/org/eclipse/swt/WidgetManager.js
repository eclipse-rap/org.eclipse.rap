
/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
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
  },

  statics : {
    _onAppearFocus : function( evt ) {
      var widget = this;
      widget.focus();
      evt.getTarget().removeEventListener( 
        "appear", 
        org.eclipse.swt.WidgetManager._onAppearFocus, 
        widget );
    },

    _onAppearSetForeground : function( evt ) {
      // 'this' references the color string but for some reason must be 
      // explicitly converted to a string
      var color = String( this );
      evt.getTarget().setTextColor( color );
      evt.getTarget().removeEventListener( 
        "appear", 
        org.eclipse.swt.WidgetManager._onAppearSetForeground, 
        this );
    }
  },

  members : {
    /**
     * Registeres the given widget under the given id at the WidgetManager.
     */
    add : function( widget, id, isControl ) {
      this._map[ id ] = widget;
      if( isControl != "undefined" && isControl == true ) {
        widget.setUserData( "isControl", true );
      }
      widget.setUserData( "id", id );
    },

    /**
     * Unregisteres the given widget at the WidgetManager. Note that the widget is
     * not disposed of.
     */
    remove : function( widget ) {
      var id = this.findIdByWidget( widget );
      delete this._map[ id ];
    },

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
        if( !widget.isDisposed() ) {
          // TODO [rh] workaround for disposing of a Sash: if( parent && ...
          var parent = widget.getParent();
          if( parent && parent.getChildren() ) {
            widget.setParent( null );
          }
          this._removeToolTipPopup( widget );
          widget.dispose();
        }
        this.remove( widget );
      }
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
      //  parent.add( widget );
      widget.setParent( parent );
    },

    focus : function( widgetId ) {
      var widget = this.findWidgetById(widgetId);
      if( widget.isMaterialized() ) {
        widget.focus();
      } else {
        widget.addEventListener( "appear", 
                                 org.eclipse.swt.WidgetManager._onAppearFocus, 
                                 widget );
      }
    },

    setForeground : function( widget, color ) {
      if ( widget.isMaterialized() ) {  // TODO [rh] isMaterialized or isCreated?
        widget.setTextColor( color );
      } else {
        widget.addEventListener( 
          "appear", 
          org.eclipse.swt.WidgetManager._onAppearSetForeground, 
          color );
      }
    },

    // TODO [rh] setting the font (for foreground color applies the same) does not
    //      work (is ignored) for an Atom that was created within the same
    //      JavaScript response (see note below)
    setFont : function( widget, name, size, bold, italic ) {
      // TODO [rh] revise this: is there a better way to change font
      if( widget.getLabelObject && widget.getLabel ) {
        var font = new qx.renderer.font.Font( size, name );
        font.setBold( bold );
        font.setItalic( italic );
        // Weird feature of qx.ui.basic.Atom:
        // getLabelObject() returns null until the label property was set to a 
        // non-empty string.
        if( widget.getLabelObject() != null ) {
          widget.getLabelObject().setFont( font );
        } else {
          var oldLabel = widget.getLabel();
          widget.setLabel( "(empty)" );
          widget.getLabelObject().setFont( font );
          // Even though label (Label.text precisely) does not allow null
          // values, there are situations where oldValue might be null
          widget.setLabel( oldLabel == null ? "" : oldLabel );
        }
      } else if( widget.setFont ) {  // test if font property is supported
        var font = new qx.renderer.font.Font( size, name );
        font.setBold( bold );
        font.setItalic( italic );
        widget.setFont( font );
      } else {
        this.debug( widget.classname + " does not support fonts" );
      }
    },

    /**
     * Sets the toolTipText for the given widget. An empty or null toolTipText
     * removes the tool tip of the widget.
     */
    setToolTip : function( widget, toolTipText ) {
      // remove and dispose of an eventually existing tool tip
      this._removeToolTipPopup( widget );
      // TODO [rh] can we avoid to destroy/create the tooltip every time its text
      //      gets changed?
      if( toolTipText != null && toolTipText != "" ) {
        var toolTip = new qx.ui.popup.ToolTip();
        var atom = toolTip.getAtom();
        atom.setLabel( "(empty)" );
        atom.getLabelObject().setMode( "html" );
        atom.setLabel( toolTipText );
        widget.setToolTip( toolTip );
      }
    },

    /**
     * Removes and disposes of the tool tip that is assigned to the given widget.
     * If the widget has no tool tip assigned, nothing is done.
     */
    _removeToolTipPopup : function( widget ) {
      var toolTip = widget.getToolTip();
      widget.setToolTip( null );
      if( toolTip != null ) {
        toolTip.dispose();
      }
    }
  }
});
