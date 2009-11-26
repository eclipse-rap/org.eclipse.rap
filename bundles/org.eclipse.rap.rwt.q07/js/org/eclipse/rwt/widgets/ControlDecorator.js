/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/


qx.Class.define( "org.eclipse.rwt.widgets.ControlDecorator", {
  extend : qx.ui.basic.Image,

  construct : function( parent ) {
    this.base( arguments );
    this.setParent( parent );
    this.setZIndex( 1000 );
    this._showHover = true;
    this._text = null;
    this._toolTip = new qx.ui.popup.ToolTip();
    this._toolTip.setShowInterval( 200 );
  },

  destruct : function() {
    this._disposeObjects( "_toolTip" );
  },

  members : {

    setText : function( value ) {
      this._text = value;
      this._updateToolTip();
    },

    setShowHover : function( value ) {
      this._showHover = value;
      this._updateToolTip();
    },

    _updateToolTip : function() {
      if( this._text === null || this._text === "" || !this._showHover ) {
        this.setToolTip( null );
      } else {
        this._toolTip.getAtom().setLabel( this._text );
        if( this.getToolTip() == null ) {
          this.setToolTip( this._toolTip );
        }
      }
    },

    ////////////////////////////////////////////////////////////
    // Event handling methods - added and removed by server-side

    onWidgetSelected : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
      org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
      req.send();
    },

    onWidgetDefaultSelected : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
      org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
      req.send();
    }

  }
} );