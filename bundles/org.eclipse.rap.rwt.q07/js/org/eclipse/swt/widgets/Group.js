/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

/**
 * This class extends qx.ui.groupbox.GroupBox to ease its usage in RWT.
 */
qx.Class.define( "org.eclipse.swt.widgets.Group", {
  extend : qx.ui.groupbox.GroupBox,

  construct : function() {
    this.base( arguments );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    var themeValues = new org.eclipse.swt.theme.ThemeValues( {} );
    this._themeBackgroundColor
      = themeValues.getCssColor( "Group-Label", "background-color" );
    themeValues.dispose();
    // Make sure that the 'labelObject' is created
    var labelObject = this.getLegendObject().getLabelObject();
    if( labelObject == null ) {
      this.setLegend( "(empty)" );
      this.setLegend( "" );
    }
    labelObject = this.getLegendObject().getLabelObject();
    labelObject.setMode( qx.constant.Style.LABEL_MODE_HTML );
    this.addEventListener( "changeBackgroundColor",
                           this._onChangeBackgroundColor,
                           this );
    this.addEventListener( "changeFont",
                           this._onChangeFont,
                           this );
    this.getLegendObject().addEventListener( "mouseover",
                                             this._onMouseOver,
                                             this );
    this.getLegendObject().addEventListener( "mouseout",
                                             this._onMouseOut,
                                             this );
  },

  destruct : function() {
    this.removeEventListener( "changeBackgroundColor",
                              this._onChangeBackgroundColor,
                              this );
    this.removeEventListener( "changeFont",
                              this._onChangeFont,
                              this );
    this.getLegendObject().removeEventListener( "mouseover",
                                                this._onMouseOver,
                                                this );
    this.getLegendObject().removeEventListener( "mouseout",
                                                this._onMouseOut,
                                                this );
  },

  members : {

    addState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._legendObject.addState( state );
        this._frameObject.addState( state );
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._legendObject.removeState( state );
        this._frameObject.removeState( state );
      }
    },

    _onChangeBackgroundColor : function( evt ) {
      var newColor = evt.getValue();
      if( this._themeBackgroundColor === "undefined" ) {
        this.getLegendObject().setBackgroundColor( newColor );
      }
    },

    _onChangeFont : function( evt ) {
      var newFont = evt.getValue();
      this.getLegendObject().setFont( newFont );
    },
    
    _onMouseOver : function( event ) {
      this.getLegendObject().addState( "over" );
      this.getFrameObject().addState( "over" );
    },
    
    _onMouseOut : function( event ) {
      this.getLegendObject().removeState( "over" );
      this.getFrameObject().removeState( "over" );
    },
    
    _layoutPost : function( changes ) {
      this.base( arguments, changes );
      this._frameObject._layoutPost( changes );
    }
  }
});
