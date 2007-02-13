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

// TODO [rh] @fappel: please revise this. Removed glass pane handling (because 
//      it was removed from SplitPane. As far as I can see, everything works.
qx.OO.defineClass( 
  "org.eclipse.rap.rwt.Sash", 
  qx.ui.splitpane.SplitPane,
  function() {
    qx.ui.splitpane.SplitPane.call( this );
  }
);

qx.Proto._onSplitterMouseDownX = function( e ) {
  if( !e.isLeftButtonPressed() ) {
    return;
  }
  this._commonMouseDown();

  if( this.getEnabled() ) {
    // activate global cursor
    this.getTopLevelWidget().setGlobalCursor( "col-resize" );
    this._slider.addState( "dragging" );

    // initialize the drag session
    this._dragOffset = e.getPageX();
  }
}

qx.Proto._onSplitterMouseDownY = function( e ) {
  if( !e.isLeftButtonPressed() ) {
    return;
  }
  this._commonMouseDown();

  if( this.getEnabled() ) {
    // activate global cursor
    this.getTopLevelWidget().setGlobalCursor( "row-resize" );
    this._slider.addState( "dragging" );

    // initialize the drag session
    this._dragOffset = e.getPageY();
  }
}

qx.Proto._commonMouseDown = function()
{
  // enable capturing
  this._splitter.setCapture( true );
  // show the slider also outside of the sash's bounds
  this.setOverflow( qx.constant.OVERFLOW_VISIBLE );
  
  // initialize the slider
  if( !this.isLiveResize() ) {
    this._slider._applyRuntimeLeft( this._splitter.getOffsetLeft() );
    this._slider._applyRuntimeTop( this._splitter.getOffsetTop() );
    this._slider.setWidth( this._splitter.getBoxWidth() );
    this._slider.setHeight( this._splitter.getBoxHeight() );
    this._slider.show();
  }
}

qx.Proto._onSplitterMouseUpX = function( e ) {
  if( !this._splitter.getCapture() ) {
    return;
  }
  this._commonMouseUp();
}

qx.Proto._onSplitterMouseUpY = function( e ) {
  if( !this._splitter.getCapture() ) {
    return;
  }
  this._commonMouseUp();
}

qx.Proto._commonMouseUp = function() {
  // buffer move information
  var leftBuffer = this.getLeft() + this._slider.getOffsetLeft();
  var topBuffer = this.getTop() + this._slider.getOffsetTop();
  
  // disable capturing
  this._splitter.setCapture(false);

  // reset the global cursor
  this.getTopLevelWidget().setGlobalCursor(null);
  
  // cleanup dragsession
  this._slider.removeState("dragging");

  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var id = widgetManager.findIdByWidget( this );
  org.eclipse.rap.rwt.EventUtil.doWidgetSelected( id,
                                                  leftBuffer, 
                                                  topBuffer, 
                                                  this.getWidth(),
                                                  this.getHeight() );
}

qx.Proto._normalizeX = function( e ) {
  var toMove = e.getPageX() - this._dragOffset;
  if( this.getLeft() + toMove < 0 ) {
    toMove = - this.getLeft();
  }
  var parentWidth = this.getParent().getWidth();
  if( this.getLeft() + this.getWidth() + toMove > parentWidth ) {
    toMove = parentWidth - this.getLeft() - this.getWidth();
  }
  return toMove;
}

qx.Proto._normalizeY = function( e ) {
  var toMove = e.getPageY() - this._dragOffset;
  if( this.getTop() + toMove < 0 ) {
    toMove = - this.getTop();
  }
  var parentHeight = this.getParent().getHeight();
  if( this.getTop() + this.getHeight() + toMove > parentHeight ) {
    toMove = parentHeight - this.getTop() - this.getHeight();
  }
  return toMove;
}

qx.Proto._modifyEnabled = function( propValue, propOldValue, propData ) {
  // TODO [rst] call super._modifyEnabled ?
  this._splitter.setEnabled( propValue );
  if( propValue ) {
    if ( this.getOrientation() == qx.constant.ORIENTATION_VERTICAL ) {
      this._splitter.setCursor( "row-resize" );
    } else {
      this._splitter.setCursor( "col-resize" );
    }
  } else {
    this._splitter.setCursor( null );
  }
  return true;
};
