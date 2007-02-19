/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.OO.defineClass(
  "org.eclipse.rap.rwt.widgets.Spinner", 
  qx.ui.form.Spinner,
  function( readOnly, border ) {
    qx.ui.form.Spinner.call( this );
    this._textfield.setReadOnly( readOnly );
    this._isModified = false;
    this._readOnly = readOnly;
    if( border ) {
      this.addState( "rwt_BORDER" );
    }
    else {
      this._upbutton.addState( "rwt_FLAT" );
      this._downbutton.addState( "rwt_FLAT" );
    }
    if( !readOnly ) {
      this._manager.addEventListener( "changeValue", this._onChangeValue, this );
      this._textfield.addEventListener( "keyinput", this._onChangeValue, this );
      this._textfield.addEventListener( "blur", this._sendModifyText, this );
      this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
    }
    this._updateButtonEnablement();
  }
);

qx.OO.addProperty( { name : "hasModifyListener", type : "boolean" } );

qx.Proto.setFont = function( value ) {
  this._textfield.setFont( value );
}

qx.Proto.dispose = function() {
  if( this.getDisposed() ) {
    return;
  }
  if( !this._readOnly ) {
    this._manager.removeEventListener( "changeValue", this._onChangeValue, this );
    this._textfield.removeEventListener( "keyinput", this._onChangeValue, this );
    this._textfield.removeEventListener( "blur", this._sendModifyText, this );
    this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
  }
  return qx.ui.form.Spinner.prototype.dispose.call( this );
}

/**
 * HACK: override qx.ui.form.Spinner._increment and _pageIncrement to achieve
 * read-only beavior.
 */
qx.Proto._increment = function() {
  if( !this._readOnly ) {
    qx.ui.form.Spinner.prototype._increment.call( this )
  }
}

qx.Proto._pageIncrement = function() {
  if( !this._readOnly ) {
    qx.ui.form.Spinner.prototype._pageIncrement.call( this )
  }
}

qx.Proto._onChangeValue = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend && !this._isModified ) {
    this._isModified = true;
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEventListener( "send", this._onSend, this );
    if( this.getHasModifyListener() ) {
      qx.client.Timer.once( this._sendModifyText, this, 500 );
    }
  }
}

qx.Proto._onChangeEnabled = function( evt ) {
  this._textfield.setEnabled( this.getEnabled() );
  this._updateButtonEnablement();
}

qx.Proto._updateButtonEnablement = function() {
  if( this.getEnabled() ) {
    this._upbutton.setEnabled( !this._readOnly );
    this._downbutton.setEnabled( !this._readOnly );
  } else {
    this._upbutton.setEnabled( false );
    this._downbutton.setEnabled( false );
  }
}

qx.Proto._onSend = function( evt ) {
  this._isModified = false;
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var id = widgetManager.findIdByWidget( this );
  var req = org.eclipse.rap.rwt.Request.getInstance();
  req.addParameter( id + ".selection", this.getValue() );
  req.removeEventListener( "send", this._onSend, this );
}

qx.Proto._sendModifyText = function( evt ) {
  if( this._isModified ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( this );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.events.modifyText", id );
    req.send();
    this._isModified = false;
  }
}