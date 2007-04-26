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
 * The parameter orientation must be one of 
 * "vertical" or "horizontal"
 * Note that updateHandleBounds must be called after each size manipulation.
 */
qx.OO.defineClass( 
  "org.eclipse.swt.widgets.CoolItem", 
  qx.ui.layout.CanvasLayout,
  function( orientation ) {
    qx.ui.layout.CanvasLayout.call( this );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this._orientation = orientation;
    this._locked = false;
    // Create handle to drag this CoolItem around
    this._handle = new qx.ui.basic.Terminator();
    this._handle.setAppearance( "coolitem-handle" );
    this._handle.addEventListener( "mousedown", this._onHandleMouseDown, this );
    this._handle.addEventListener( "mousemove", this._onHandleMouseMove, this );
    this._handle.addEventListener( "mouseup", this._onHandleMouseUp, this );
    this.add( this._handle );
    // buffers zIndex during drag to be restored when dropped
    this._bufferedZIndex = null;
  }
);

qx.Proto.setLocked = function( value ) {
  this._locked = value;
}

/** Updates the size and position of the handle. */
qx.Proto.updateHandleBounds = function() {
  // parameter order for setSpace: x, width, y, height
  if( this._orientation == "vertical" ) {
    this._handle.setSpace( 0, this.getWidth(), 0, 3 );
  } else {
    this._handle.setSpace( 0, 3, 0, this.getHeight() );
  }
}

/** React on mouseDown events on _handle widget. */
qx.Proto._onHandleMouseDown = function( evt ) {
  if( !this._locked ) {
    this._handle.setCapture( true );
    this._offsetX = evt.getPageX() - this.getLeft();
    this._offsetY = evt.getPageY() - this.getTop();
    this._bufferedZIndex = this.getZIndex();
    if( typeof( this.getZIndex() ) == "number" ) {
      this.setZIndex( this.getZIndex() + 100000 );
    } else {
      this.setZIndex( 100000 );
    }
  }
}

/** React on mouseMove events on _handle widget. */
qx.Proto._onHandleMouseMove = function( evt ) {
  if( !this._locked && this._handle.getCapture() ) {
    this.setLeft( evt.getPageX() - this._offsetX );
    this.setTop( evt.getPageY() - this._offsetY );
  }
}

/** React on mouseUp events on _handle widget. */
qx.Proto._onHandleMouseUp = function( evt ) {
  if( !this._locked ) {
    this._handle.setCapture( false );
    this.setZIndex( this._bufferedZIndex );
    // Send request that informs about dragged CoolItem
    if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetMoved", id );
      req.addParameter( id + ".bounds.x", this.getLeft() );
      req.addParameter( id + ".bounds.y", this.getTop() );
      req.send();
    }
  }
}

qx.Proto.dispose = function() {
  if( this.getDisposed() ) {
    return true;
  }
  this._handle.removeEventListener( "mousedown", this._onHandleMouseDown, this );
  this._handle.removeEventListener( "mousemove", this._onHandleMouseMove, this );
  this._handle.removeEventListener( "mouseup", this._onHandleMouseUp, this );
  if( this._handle != null ) {
    this._handle.dispose();
  }
  return qx.ui.layout.CanvasLayout.prototype.dispose.call( this );
}
