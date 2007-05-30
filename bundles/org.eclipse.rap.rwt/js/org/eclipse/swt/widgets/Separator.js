
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
 * This class represents RWT Labels with style RWT.SEPARATOR
 */
qx.Class.define( "org.eclipse.swt.widgets.Separator", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style ) {
    qx.ui.layout.CanvasLayout.call( this );
    this._swtStyle = style;
    this.setAppearance( "separator" );

    // Fix IE Styling issues
    this.setStyleProperty( "fontSize", "0" );
    this.setStyleProperty( "lineHeight", "0" );

    // the actual separator line
    this._line = new qx.ui.basic.Terminator();
    this._line.setAnonymous( true );
    this._line.setBorder( this._getLineBorder( style ) );
    this.addEventListener( "changeWidth", this._updateLineBounds, this );
    this.addEventListener( "changeHeight", this._updateLineBounds, this );
    this.add(this._line);
  },
  
  destruct : function() {
    if( this._line ) {
      this._line.dispose();
      this._line = null;
    }
  },
  
  members : {

    _updateLineBounds : function() {
      var borderSize = 0;
      if( this.getBorder() != null ) {
        borderSize = 1;
      }
      var clientWidth = this.getWidth() - ( 2 * borderSize );
      var clientHeight = this.getHeight() - ( 2 * borderSize );
      if( this._isHorizontal() ) {
        this._line.setLeft( borderSize );
        this._line.setTop( clientHeight / 2 );
        this._line.setWidth( clientWidth );
        this._line.setHeight( 2 );
      } else {
        this._line.setTop( borderSize );
        this._line.setLeft( clientWidth / 2 );
        this._line.setHeight( clientHeight );
        this._line.setWidth( 2 );
      }
    },

    _getLineBorder : function( style ) {
      var result = null;
      if( qx.lang.String.contains( style, "SHADOW_OUT" ) ) {
        result = new qx.renderer.border.Border( 1, "outset" );
      } else if( qx.lang.String.contains( style, "SHADOW_IN" ) ) {
        result = new qx.renderer.border.Border( 1, "solid", "white" );
      }
      if( result != null ) {
        if( this._isHorizontal() ) {
          result.setWidthLeft( 0 );
          result.setWidthRight( 0 );
        } else {
          result.setWidthTop( 0 );
          result.setWidthBottom( 0 );
        }
      }
      return result;
    },

    _isHorizontal : function() {
      return qx.lang.String.contains( this._swtStyle, "HORIZONTAL" );
    }
  }
});
