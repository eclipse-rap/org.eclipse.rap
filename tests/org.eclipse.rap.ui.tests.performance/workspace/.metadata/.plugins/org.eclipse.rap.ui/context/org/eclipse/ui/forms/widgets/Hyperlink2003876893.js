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

qx.Class.define( "org.eclipse.ui.forms.widgets.Hyperlink", {
  extend : qx.ui.basic.Atom,
  
  construct : function() {
    this.base( arguments );
    this.setAppearance( "hyperlink" );
    // TODO [rh] workaround for weird getLabelObject behavior
    this.setLabel( "(empty)" );
    // End of workaround
    var labelObject = this.getLabelObject();
    // Explicitly set cursor on label object
    // see http://bugzilla.qooxdoo.org/show_bug.cgi?id=455
    labelObject.setMode( qx.constant.Style.LABEL_MODE_HTML );
    labelObject.setCursor( qx.constant.Style.CURSOR_HAND );
    // TODO [rh] workaoround for weird getLabelObject behavior
    this.setLabel( "" );
    // End of workaround
    this.setCursor( qx.constant.Style.CURSOR_HAND );
    this._inactiveForeground = null;
    this._inactiveBackground = null;
    this._activeForeground = null;
    this._activeBackground = null;
    this._hover = false;
    this.addEventListener( "mousemove", this._onMouseMove, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
  },
  
  destruct : function() {
    this.removeEventListener( "mousemove", this._onMouseMove, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
  },
  
  statics : {
    
    // This event handler is added/removed by the server-side LCA
    onClick : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
        req.send();
      }
    }

  },
    
  members : {
    
    setInactiveForeground : function( value ) {
      this._inactiveForeground = value;      
    },
    
    setInactiveBackground : function( value ) {
      this._inactiveBackground = value;
    },
    
    setActiveForeground : function( value ) {
      this._activeForeground = value;      
    },
    
    setActiveBackground : function( value ) {
      this._activeBackground = value;
    },
    
    _onMouseMove : function( evt ) {
      if( !this._hover ) {
        this._hover = true;
        this._updateAppearanceState();
      }
    },
      
    _onMouseOut : function( evt ) {
      this._hover = false;
      this._updateAppearanceState();
    },
    
    _updateAppearanceState : function() {
      if( this._hover ) {
        this.setBackgroundColor( this._activeBackground );
        this.setTextColor( this._activeForeground );
      } else {
	      this.setBackgroundColor( this._inactiveBackground );
	      this.setTextColor( this._inactiveForeground );
      } 
    }
      
  }
  
} );

