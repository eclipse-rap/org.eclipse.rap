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

qx.OO.defineClass(
  "org.eclipse.rap.rwt.custom.CTabItem", 
  qx.ui.basic.Atom,
  function( canClose, closeToolTipText ) {
    qx.ui.basic.Atom.call( this );
    this.setAppearance( "c-tab-item" );
    this.setVerticalChildrenAlign( "middle" );
    this.setHorizontalChildrenAlign( "left" );
    this.setTabIndex( -1 );
    this._selected = false;
    this._closeButton = null;
    this._unselectedCloseVisible = true;
    this._selectionBackground = null;
    this._selectionForeground = null;
    if( canClose ) {
      this._closeButton = new qx.ui.basic.Image();
      this._closeButton.setWidth( 20 );
      this._closeButton.setHeight( "80%" );
      this._closeButton.addEventListener( "click", this._onClose, this );
      if( closeToolTipText != null ) {
        var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
        wm.setToolTip( this._closeButton, closeToolTipText );
      }
      this._updateCloseButton();
      this.add( this._closeButton );
    }
    this._control = null;
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
    this.addEventListener( "click", this._onClick, this );
    this.addEventListener( "dblclick", this._onDblClick, this );
  }
);

org.eclipse.rap.rwt.custom.CTabItem.IMG_CLOSE 
  = "org/eclipse/rap/rwt/custom/ctabfolder/close.gif";
org.eclipse.rap.rwt.custom.CTabItem.IMG_CLOSE_HOVER
  = "org/eclipse/rap/rwt/custom/ctabfolder/close_hover.gif";

qx.Proto.dispose = function() {
  if ( this.getDisposed() ) {
    return;
  }
  this.removeEventListener( "mouseover", this._onMouseOver, this );
  this.removeEventListener( "mouseout", this._onMouseOut, this );
  this.removeEventListener( "click", this._onClick, this );
  this.removeEventListener( "dblclick", this._onDblClick, this );
  if( this._closeButton != null ){
    this._closeButton.removeEventListener( "click", this._onClose, this );
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    wm.setToolTip( this._closeButton, null );
    this._closeButton.dispose();
    this._closeButton = null;
  }
  return qx.ui.basic.Atom.prototype.dispose.call( this );
}

qx.Proto.setControl = function( control ) {
  this._control = control;
}

qx.Proto.setSelected = function( selected ) {
  this._selected = selected;
  if( selected ) {
    this.addState( "checked" );
    this.setBackgroundColor( this._selectionBackground );
    this.setColor( this._selectionForeground );
  } else {
    this.removeState( "checked" );
    this.setBackgroundColor( null );
    this.setColor( null );
  }
  this._updateCloseButton();
}

qx.Proto.isSelected = function() {
  return this._selected;  
}

qx.Proto.setUnselectedCloseVisible = function( value ) {
  this._unselectedCloseVisible = value;  
}

qx.Proto.setSelectionBackground = function( color ) {
  this._selectionBackground = color;
  if( this.isSelected() ) {
    this.setBackgroundColor( this._selectionBackground );
  } 
}

qx.Proto.setSelectionForeground = function( color ) {
  this._selectionForeground = color;
  if( this.isSelected() ) {
    this.setColor( this._selectionForeground );
  } 
}

qx.Proto._applyStateAppearance = function() {
  this._states.firstChild = this.isFirstVisibleChild();
  this._states.lastChild = this.isLastVisibleChild();
  this._states.alignLeft = true;
  this._states.barTop = true;
  this._states.checked = this.isSelected();
  qx.ui.basic.Atom.prototype._applyStateAppearance.call( this );
}

qx.Proto._updateCloseButton = function() {
  if( this._closeButton != null ) {
    var visible =    ( !this._unselectedCloseVisible  && this.isSelected() )
                  && ( this.hasState( "over" ) || this.isSelected() );
    this._closeButton.setVisibility( visible );
    if( this._closeButton.hasState( "over" ) ) {
      this._closeButton.setSource( org.eclipse.rap.rwt.custom.CTabItem.IMG_CLOSE_HOVER );
    } else {
      this._closeButton.setSource( org.eclipse.rap.rwt.custom.CTabItem.IMG_CLOSE );
    }
  }
}

qx.Proto._onMouseOver = function( evt ) {
  this.addState( "over" );
  if( evt.getTarget() == this._closeButton ) {
    this._closeButton.addState( "over" );
  }
  this._updateCloseButton();
}  

qx.Proto._onMouseOut = function( evt ) {
  this.removeState( "over" );
  if( evt.getTarget() == this._closeButton ) {
    this._closeButton.removeState( "over" );
  }
  this._updateCloseButton();
}

qx.Proto._onClick = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    if( evt.getTarget() != this._closeButton ) {
      evt.getTarget().getParent()._notifyItemClick( evt.getTarget() );
    }
  }
}

qx.Proto._onDblClick = function( evt ) {
  if( evt.getTarget() != this._closeButton ) {
    evt.getTarget().getParent()._notifyItemDblClick( evt.getTarget() );
  }
}

qx.Proto._onClose = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var req = org.eclipse.rap.rwt.Request.getInstance();
    var id = widgetManager.findIdByWidget( this );
    req.addEvent( "org.eclipse.rap.rwt.events.ctabItemClosed", id );
    req.send();
  }
}