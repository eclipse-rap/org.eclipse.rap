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

qx.Class.define( "org.eclipse.ui.forms.widgets.ToggleHyperlink", {
  extend : qx.ui.basic.Image,
  
  construct : function() {
    this.base( arguments );
    this.setCursor( qx.constant.Style.CURSOR_HAND );
    this._hover = false;
    this._expanded = false;
    this._collapseNormal = null;
    this._collapseHover = null;
    this._expandHover = null;
    this._expandNormal = null;
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
widgetManager.debug( "clicked" );      
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
        req.send();
      }
    }

  },
    
  members : {
    
    setImages : function( collapseNormal, 
                          collapseHover, 
                          expandNormal, 
                          expandHover ) 
    {
      this._collapseNormal = collapseNormal;
      this._collapseHover 
        = collapseHover != null ? collapseHover : collapseNormal;      
      this._expandNormal = expandNormal;
      this._expandHover 
        = expandHover != null ? expandHover : expandNormal;
      this._updateImage();      
    },
    
    setExpanded : function( value ) {
      this._expanded = value;
      this._updateImage();
    },
    
    _onMouseMove : function( evt ) {
      this._hover = true;
      this._updateImage();
    },
      
    _onMouseOut : function( evt ) {
      this._hover = false;
      this._updateImage();
    },
      
    _updateImage : function() {
      var source;
      if( this._expanded ) {
        source = this._hover ? this._collapseHover : this._collapseNormal;
      } else {
        source = this._hover ? this._expandHover : this._expandNormal;
      }
      this.setSource( source );
    }

  }
  
} );

