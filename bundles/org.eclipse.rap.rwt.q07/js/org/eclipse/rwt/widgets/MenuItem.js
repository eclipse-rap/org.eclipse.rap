/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define("org.eclipse.rwt.widgets.MenuItem",  {
  extend : org.eclipse.rwt.widgets.MultiCellWidget,

  construct : function( menuItemType ) {
    this.base( arguments, [ "image", "image", "label", "image" ] );
    this._hasSelectionListener = false;
    this._selected = false;
    this._parentMenu = null;
    this._subMenu = null;
    this._subMenuOpen = false;
    this._preferredCellWidths = null;    
    this.initTabIndex();
    this.set( {
      width : "auto", 
      height : "auto",
      paddingTop : 2, 
      paddingBottom : 2,
      paddingLeft : 4,
      paddingRight : 4,
      horizontalChildrenAlign : "left",
      verticalChildrenAlign : "middle"      
    } );
    this.addEventListener( "mouseup", this.execute );    
    this.addEventListener( "changeFont", this._onFontChange );
    this.addState( menuItemType );
    switch( menuItemType ){
     case "push" : 
      this._isSelectable = false;
      this._isDeselectable = false;
      this._sendEvent = true;
     break;
     case "check":
      this._isSelectable = true;
      this._isDeselectable = true;
      this._sendEvent = true;     
     break;
     case "cascade":
      this._isSelectable = false;
      this._isDeselectable = false;
      this._sendEvent = false;
     break;
     case "radio":
      this._isSelectable = true;
      this._isDeselectable = false;
      this._sendEvent = false;     
      org.eclipse.rwt.RadioButtonUtil.registerExecute( this );
     break;
     default:
       throw( "Unkown menuItem type " + menuItemType );
     break; 
    }
    this._preferredCellWidths = [ 0, 0, 0, 13 ];
    if( this._isSelectable ) {
      this.setCellContent( 0, "" ); 
    }
  },

  destruct : function() {
    this._disposeFields( "_parentMenu", "_subMenu" );     
  },
  
  properties : {

    selectionIndicator : {
      apply : "_applySelectionIndicator",
      nullable : true,
      themeable : true
    },
    
    arrow : {
      apply : "_applyArrow",
      nullable : true,
      themeable : true
    },

    appearance : {
      refine : true,
      init : "menu-item"
    },    
    
    tabIndex : {
      refine : true,
      init : 1
    }

  },

  members : {
        
    setParentMenu : function( menu ) {
      this._parentMenu = menu;
    },
    
    getParentMenu : function() {
      return this._parentMenu;
    },
    
    setSubMenuOpen : function( bool ) {
      this._subMenuOpen = bool;
    },

    setMenu : function( menu ) {
      this._subMenu = menu;
    },    

    getMenu : function() {
      return this._subMenu;
    },
    
    _applySelectionIndicator : function( value, old ) {
      //never remove cell-node
      var url = value ? value[ 0 ] : null;
      var width = value ? value[ 1 ] : 0;
      var height = value ? value[ 2 ] : 0;
      if( url == null ) {
        var content = this._isSelectable ? "" : null;          
        this.setCellContent( 0, content );
      } else {
        this.setCellContent( 0, url );
      }       
      this.setCellHeight( 0, height );
      this._setPreferredCellWidth( 0, width );       
    },
    
    _setPreferredCellWidth : function( cell, width ) {
      this._preferredCellWidths[ cell ] = width;
      if( this._parentMenu ) {        
        this._parentMenu.invalidateMaxCellWidth( cell );
      }
      this._scheduleLayoutX();
    },
        
    _afterScheduleLayoutX : function() {      
      if( this._parentMenu ) {        
        this._parentMenu.scheduleMenuLayout();
      }
    },    
    
    getPreferredCellWidth : function( cell ) {
      return this._preferredCellWidths[ cell ];
    },

    setImage : function( value, width, height ) {
      this.setCellContent( 1, value );     
      this.setCellHeight( 1, height );
      this._setPreferredCellWidth( 1, width ); 
    },
    
    setText : function( value ) {
      this.setCellContent( 2, value );
      this.setCellDimension( 2, null, null ); // force to recompute the width
      this._setPreferredCellWidth( 2, this.getCellWidth( 2 ) );
    },
    
    _onFontChange : function() {  
      this.setCellDimension( 2, null, null ); 
      this._setPreferredCellWidth( 2, this.getCellWidth( 2 ) );      
    },

    _applyArrow : function( value, old ) { 
      var url = value ? value[ 0 ] : null;
      var width = value ? value[ 1 ] : 13;
      var height = value ? value[ 2 ] : 0;
      this.setCellContent( 3, url );
      this.setCellHeight( 3, height );
      this._setPreferredCellWidth( 3, width );
    },
    
    _beforeComputeInnerWidth : function() {
      this._invalidateTotalSpacing();      
      for( var i = 0; i < 4; i++ ) {
        this._setCellWidth( i, this._parentMenu.getMaxCellWidth( i ) )
      }
    },
    
    _beforeAppear : function() {
      this.base( arguments );
      this._parentMenu.invalidateAllMaxCellWidths();
      this._parentMenu.scheduleMenuLayout();
    },            

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },
    
    setSubMenu : function( value ) {
      this._subMenu = value;
    },
    
    // TODO [tb] "execute", "setSelection", "_sendChanges" and possibly more
    // could be shared between Button, MenuItem and (future) ToolItem.
    // Then, also the corrosponding LCA-methods could be shared
    execute : function() {
      this.base( arguments );
      if( this._isSelectable ) {
        this.setSelection( !( this._selected && this._isDeselectable ) );
      }
      this._sendChanges();
    },
        
    setSelection : function( value ) {
      if( this._selected != value ) {
        this._selected = value;
        if( this._selected ) {
          this.addState( "selected" );
        } else {
          this.removeState( "selected" );
        }
        if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addParameter( id + ".selection", this._selected );
        }
      }
    },    
    
    // Not using EventUtil since no event should be sent (for radio at least)
    _sendChanges : function() {
      if(    !org_eclipse_rap_rwt_EventUtil_suspend  
          && this._hasSelectionListener ) 
      {
        var req = org.eclipse.swt.Request.getInstance();
        if( this._sendEvent ) {        
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( this );
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
        }
        req.send();       
      }
    },
 
    _onmouseup : function( event ) {      
      this.execute(); 
    }
  }

});
