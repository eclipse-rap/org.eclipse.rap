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
    this._controls = new Array();
  }
);

/**
 * Registeres the given widget under the given id at the WidgetManager.
 */
qx.Proto.add = function( widget, id, isControl ) {
  this._map[ id ] = widget;
  if( isControl != "undefined" && isControl == true ) {
//  qx.lang.Array.append( this._controls, widget );
    this._controls.push( widget );
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
  	qx.lang.Array.remove( this._controls, widget );
  }
  delete this._map[ id ];
};

/**
 * Returns the widget for the given id or null if there is no widget registered
 * for the given id exists.
 */
qx.Proto.findWidgetById = function( id ) {
  return this._map[ id ];
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
  return widget != null && qx.lang.Array.contains( this._controls, widget );
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

qx.Class.getInstance = qx.util.Return.returnInstance;
