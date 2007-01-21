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
 * This class represents Labels with style RWT.SEPARATOR
 */
qx.OO.defineClass( 
  "org.eclipse.rap.rwt.widgets.Separator", 
  qx.ui.layout.CanvasLayout,
  function( style ) {
    qx.ui.layout.CanvasLayout.call( this );
    this._style = style;
    this.setAppearance( "separator" );
    // Fix IE Styling Issues
    this.setStyleProperty( "fontSize", "0" );
    this.setStyleProperty( "lineHeight", "0" );
    // the actual separator line
    this._line = new qx.ui.basic.Terminator();
    this._line.setAnonymous( true );
    this._line.setBorder( this._getLineBorder( style ) );
    this.addEventListener( "changeWidth", this._updateLineBounds, this );
    this.addEventListener( "changeHeight", this._updateLineBounds, this );
    this.add( this._line );
  }
);

qx.Proto.dispose = function() {
  if( this.getDisposed() ) {
    return true;
  }
  if( this._line ) {
    this._line.dispose();
    this._line = null;
  }
  return qx.ui.layout.CanvasLayout.prototype.dispose.call( this );
}

qx.Proto._updateLineBounds = function() {
  var borderSize = 0;
  if( this.getBorder() != null ) {
    borderSize = 1;
  }
  var clientWidth = this.getWidth() - ( 2 * borderSize );
  var clientHeight = this.getHeight() - ( 2 * borderSize )
  if( this._isHorizontal() ) {
    this._line.setLeft( this._borderSize );    
    this._line.setTop( clientHeight / 2 );
    this._line.setWidth( clientWidth );
    this._line.setHeight( 2 );
  } else {
    this._line.setTop( this._borderSize );    
    this._line.setLeft( clientWidth / 2 );
    this._line.setHeight( clientHeight );
    this._line.setWidth( 2 );
  }
}

qx.Proto._getLineBorder = function( style ) {
  var result = null;
  if( qx.lang.String.contains( style, "SHADOW_OUT" ) ) {
    result = new qx.renderer.border.BorderObject( 
      1, 
      qx.renderer.border.Border.STYLE_OUTSET );
  } else  if( qx.lang.String.contains( style, "SHADOW_IN" ) ) {
    result = new qx.renderer.border.BorderObject( 
      1, 
      qx.renderer.border.Border.STYLE_SOLID, "white" );
  }
  if( result != null ) {
    if( this._isHorizontal() ) {
      result.setLeftWidth( 0 );
      result.setRightWidth( 0 );
    } else {
      result.setTopWidth( 0 );
      result.setBottomWidth( 0 );
    }
  }
  return result;
}

qx.Proto._isHorizontal = function() {
  return qx.lang.String.contains( this._style, "HORIZONTAL" );
}