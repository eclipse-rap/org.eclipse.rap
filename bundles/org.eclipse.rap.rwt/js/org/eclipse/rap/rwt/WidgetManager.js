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
qx.OO.defineClass( "org.eclipse.rap.rwt.WidgetManager", qx.core.Object,
  function() {
    qx.core.Object.call( this );
    // Holds the association between widget-id's and widget-instances.
    // Key: id (string), value: widget instanace (qx.ui.core.Widget)
    this._map = {};
  }
);

/**
 * Registeres the given widget under the given id at the WidgetManager.
 */
qx.Proto.add = function( widget, id, isControl ) {
  this._map[ id ] = widget;
  if( isControl != "undefined" && isControl == true ) {
    widget.setUserData( "isControl", true );
  }
  widget.setUserData( "id", id );
};

/**
 * Disposes of the widget that is registered with the given id. The widget is
 * disconnected from its parent, its 'dispose' method is called and it is 
 * removed from this WidgetManager.
 * No action is taken if there is no widget registered for the given id or the
 * widget was already disposed of.
 */
qx.Proto.dispose = function( id ) {
  var widget = this.findWidgetById( id );
  if( widget != null && !widget.isDisposed() ) {
    widget.setParent( null );
    this._removeToolTipPopup( widget );
    widget.dispose();
  }
  delete this._map[ id ];
};

/**
 * Returns the widget for the given id or null if there is no widget registered
 * for the given id exists.
 */
qx.Proto.findWidgetById = function( id ) {
  var result = this._map[ id ]
  if( result == null ) {
    this.warn( "no widget registered for id " + id );
  }
  return result;
};

/**
 * Returns the id (string) for the given widget or null if the widget is not
 * registered.
 */
qx.Proto.findIdByWidget = function( widget ) {
  return widget.getUserData( "id" );
};

/**
 * Determines whether the given widget represents a server-side instance of
 * Control (or one of its subclasses)
 */
qx.Proto.isControl = function( widget ) {
  var data = null;
  if( widget != null ) {
    data = widget.getUserData( "isControl" );
  }
  return data != null && data == true;
}

/**
 * Adds the given widget to the children of the widget denoted by parentId
 */
qx.Proto.setParent = function( widget, parentId ) {
  var parent = this.findWidgetById( parentId );
  // TODO [rh] there seems to be a difference between add and setParent
  //      when using add sizes and clipping are treated differently
//  parent.add( widget );
  widget.setParent( parent );
}

qx.Proto.setForeground = function( widget, color ) {
  if( widget.isMaterialized() ) {  // TODO [rh] isMaterialized or isCreated?
    widget.setColor( color );
  } else {
    widget.addEventListener( 
      "appear", 
      org.eclipse.rap.rwt.WidgetManager._onAppearSetForeground, 
      color );
  }
}

org.eclipse.rap.rwt.WidgetManager._onAppearSetForeground = function( evt ) {
  // 'this' references the color string but for some reason must be explicitly
  // converted to a string
  var color = String( this );
  evt.getTarget().setColor( color );
  evt.getTarget().removeEventListener( 
    "appear", 
    org.eclipse.rap.rwt.WidgetManager._onAppearSetForeground,
    this );
}

// TODO [rh] setting the font (for foreground color applies the same) does not 
//      work (is ignored) for an Atom that was created within the same 
//      JavaScript response (see note below)
qx.Proto.setFont = function( widget, name, size, bold, italic ) {
  // TODO [rh] revise this: is there a better way to change font
  if( widget.setFont ) {  // test if font property is supported
    var font = new qx.renderer.font.Font( size, name );
    font.setBold( bold );
    font.setItalic( italic );
    widget.setFont( font );
  } else if( widget.getLabelObject && widget.getLabel ) {
    var font = new qx.renderer.font.Font( size, name );
    font.setBold( bold );
    font.setItalic( italic );
    // Weird feature of qx.ui.basic.Atom:
    // getLabelObject() returns null until the label property was set to a non-
    // empty string.
    if( widget.getLabelObject() != null ) {
      widget.getLabelObject().setFont( font );
    } else {
      var oldLabel = widget.getLabel();
      widget.setLabel( "(empty)" );
      widget.getLabelObject().setFont( font );
      widget.setLabel( oldLabel );
    }
  } else {
    this.debug( widget.classname + " does not support fonts" );
  }
}

/**
 * Sets the toolTipText for the given widget. An empty or null toolTipText
 * removes the tool tip of the widget.
 */
qx.Proto.setToolTip = function( widget, toolTipText ) {
  // remove and dispose of an eventually existing tool tip
  this._removeToolTipPopup( widget );
  // TODO [rh] can we avoid to destroy/create the tooltip every time its text
  //      gets changed?
  if( toolTipText != null && toolTipText != "" ) {
    toolTip = new qx.ui.popup.ToolTip( toolTipText );
    widget.setToolTip( toolTip );
  }
};

/**
 * Removes and disposes of the tool tip that is assigned to the given widget.
 * If the widget has no tool tip assigned, nothing is done.
 */
qx.Proto._removeToolTipPopup = function( widget ) {
  var toolTip = widget.getToolTip();
  widget.setToolTip( null );
  if( toolTip != null ) {
    toolTip.dispose();
  }
};

qx.Class.getInstance = qx.lang.Function.returnInstance;
