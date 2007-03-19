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
 * This class extends qx.ui.groupbox.GroupBox to ease its usage in RWT.
 */
qx.OO.defineClass( 
  "org.eclipse.rap.rwt.widgets.Group", 
  qx.ui.groupbox.GroupBox,
  function() {
    qx.ui.groupbox.GroupBox.call( this );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
  }
);

qx.Proto.setFont = function( value ) {
  this._getLabelObject().setFont( value );
}

qx.Proto._onChangeEnabled = function( evt ) {
  this._getLabelObject().setEnabled( this.getEnabled() );
}

qx.Proto._getLabelObject = function() {
  if( this.getLegendObject().getLabelObject() == null ) {
    this.setLegend( "(empty)" );
    this.setLegend( "" );
  }
  return this.getLegendObject().getLabelObject();
}
