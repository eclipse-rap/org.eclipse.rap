/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.ExpandItem", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( parent ) {
    this.base( arguments ); 
    if( parent.classname != "org.eclipse.swt.widgets.ExpandBar" ) {
      throw new Error( "illegal parent, must be a ExpandBar" );
    } 
    this.setAppearance( "expand-item" );    
    this._expandBar = parent; 
    this._headerHeight = 24; // Chevron size with top/bottom insets
    this._expanded = false;
    this._image = null;
    this._text = "";    
    // Construct a header area
    this._header = new qx.ui.basic.Atom( "(empty)", this._image, 16, 16 );
    this._header.getLabelObject().setPaddingBottom( 4 );    
    this._header.setAppearance( "expand-item-header" );    
    this._header.addEventListener( "click", this._onClick, this );  
    this._header.addEventListener( "mouseover", this._onHandleMouseOver, this );
    this._header.addEventListener( "mouseout", this._onHandleMouseOut, this );  
    this._header.addEventListener( "contextmenu", this._onContextMenu, this );
    this._header.setHeight( this._headerHeight ); 
    this._header.setLabel( this._text );      
    this.add( this._header );    
    // Chevron image
    this._chevron = new qx.ui.basic.Image;
    this._chevron.setAppearance( "expand-item-chevron-button" );    
    this._chevron.setTop( ( this._headerHeight - this._chevron.getHeight() ) / 2 );
    this._chevron.addEventListener( "click", this._onClick, this ); 
    this._chevron.addEventListener( "mouseover", this._onHandleMouseOver, this );
    this._chevron.addEventListener( "mouseout", this._onHandleMouseOut, this );  
    this._chevron.addEventListener( "contextmenu", this._onContextMenu, this );   
    this.add( this._chevron );
  },

  destruct : function() {    
    this._header.removeEventListener( "click", this._onClick, this );
    this._header.removeEventListener( "mouseover", this._onHandleMouseOver, this );
    this._header.removeEventListener( "mouseout", this._onHandleMouseOut, this ); 
    this._header.removeEventListener( "contextmenu", this._onContextMenu, this ); 
    this._chevron.removeEventListener( "click", this._onClick, this );
    this._chevron.removeEventListener( "mouseover", this._onHandleMouseOver, this );
    this._chevron.removeEventListener( "mouseout", this._onHandleMouseOut, this ); 
    this._chevron.removeEventListener( "contextmenu", this._onContextMenu, this );   
    this._disposeObjects( "_header", "_chevron" );     
  },

  statics : {
    STATE_EXPANDED : "expanded",
    STATE_OVER : "over"
  },

  members : {      
    addState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._header.addState( state );
        this._chevron.addState( state );
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._header.removeState( state );
        this._chevron.removeState( state );
      }
    },

    setExpanded : function( expanded ) {
      this._expanded = expanded;
      if( expanded ) { 
        this._chevron.addState( org.eclipse.swt.widgets.ExpandItem.STATE_EXPANDED );
        this._header.addState( org.eclipse.swt.widgets.ExpandItem.STATE_EXPANDED );
      } else { 
        this._chevron.removeState( org.eclipse.swt.widgets.ExpandItem.STATE_EXPANDED );
        this._header.removeState( org.eclipse.swt.widgets.ExpandItem.STATE_EXPANDED );
      }
    }, 
    
    setImage : function( image ) {
      this._image = image;
      this._header.setIcon( image );      
    },
    
    setText : function( text ) {
      this._text = text;
      this._header.setLabel( text );      
    },
    
    setHeaderHeight : function( headerHeight ) {
      this._headerHeight = headerHeight;
      this._header.setHeight( this._headerHeight );   
      this._chevron.setTop( ( this._headerHeight - this._chevron.getHeight() ) / 2 );
    },
    
    _onClick : function( evt ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {        
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );        
        if( this._expanded ) {
          req.addEvent( "org.eclipse.swt.events.expandItemCollapsed", id );
        } else {
          req.addEvent( "org.eclipse.swt.events.expandItemExpanded", id );
        }
        req.send();
      }
    },
    
    _onContextMenu : function( evt ) {      
      var menu = this._expandBar.getContextMenu();      
      if( menu != null ) {
        menu.setLocation( evt.getPageX(), evt.getPageY() );
        menu.setOpener( this._expandBar );
        menu.show();
        evt.stopPropagation();
      }
    },
    
    _onHandleMouseOver : function( evt ) {
      this._chevron.addState( org.eclipse.swt.widgets.ExpandItem.STATE_OVER );
    },
    
    _onHandleMouseOut : function( evt ) {
      this._chevron.removeState( org.eclipse.swt.widgets.ExpandItem.STATE_OVER );
    }
  }
} );
