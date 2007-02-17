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
  "org.eclipse.rap.rwt.widgets.Spinner", 
  qx.ui.form.Spinner,
  function( readOnly, border ) {
    qx.ui.form.Spinner.call( this );
    this._textfield.setReadOnly( readOnly );
    if( border ) {
      this.addState( "rwt_BORDER" );
    }
    else {
      this._upbutton.addState( "rwt_FLAT" );
      this._downbutton.addState( "rwt_FLAT" );
    }
    this._manager.addEventListener( "changeValue", this._onChangeValue, this );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
  }
);

qx.Proto.setFont = function( value ) {
  this._textfield.setFont( value );
}

qx.Proto.dispose = function() {
  if( this.getDisposed() ) {
    return;
  }
  this._manager.removeEventListener( "changeValue", this._onChangeValue, this );
  this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
  return qx.ui.form.Spinner.prototype.dispose.call( this );
}

qx.Proto._onChangeValue = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( this );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( id + ".selection", this.getValue() );
  }
}

qx.Proto._onChangeEnabled = function( evt ) {
  this._textfield.setEnabled( this.getEnabled() );
  this._upbutton.setEnabled( this.getEnabled() );
  this._downbutton.setEnabled( this.getEnabled() );
}