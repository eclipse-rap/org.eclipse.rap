/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/


qx.Class.define( "org.eclipse.rwt.widgets.ToolItem", {
  extend : org.eclipse.rwt.widgets.AbstractButton,

  construct : function( itemType, flat ) {
    this.base( arguments, itemType );
    this._isDropDown = false;
    if( itemType == "dropDown" ) {
      this._isDropDown = true;
      this._isSelectable = false;
      this._isDeselectable = false;
      this._sendEvent = true;
      this.setCellDimension( 3, 1, 0 );
      this.setCellContent( 3, "" );
    }
    this._separatorBorder = null;
    this.setAppearance( "toolbar-button" );
    if( flat ) {
      this.addState( "rwt_FLAT" );
    }
  },
  
  properties : {
    
    dropDownArrow : {
      apply : "_applyDropDownArrow",
      nullable : true,
      themeable : true
    },
    
    separatorBorder : {
      nullable : true,
      init : null,
      apply : "_applySeparatorBorder",
      themeable : true
    }
    
  },
  
  members : {
    
    // overwritten:
    _onKeyPress : function( event ) {
      // give to toolBar for keyboard control (left/right keys):
      this.getParent().dispatchEvent( "keypress", event );     
      this.base( arguments, event ); 
    },
    
    ////////////////////
    // Dropdown-support

    // overwritten: 
    _onMouseDown : function( event ) {
      if ( event.getTarget() == this && event.isLeftButtonPressed() ) {
        this.removeState( "abandoned" );
        if( this._isDropdownClick( event ) ) {
          this._onDropDownClick();
          this._sendChanges();
        } else {
          this.addState( "pressed" );
        }        
      }
    },

    _isDropdownClick : function( event ) {
      var result = false;
      var node = this.getCellNode( 3 );
      if( node != null ) { 
        var nodeLeft = qx.html.Location.getClientBoxLeft( node );
        var clickLeft = event.getClientX();
        result = clickLeft > nodeLeft;
      }
      return result;
    },

    _onDropDownClick : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && this._hasSelectionListener )
      {
        var req = org.eclipse.swt.Request.getInstance();
        if( this._sendEvent ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( this );
          req.addEvent( "org.eclipse.swt.events.widgetSelected.detail", 
                        "arrow" );
        }
      }
    },
        
    _applyDropDownArrow : function( value, oldValue ) {
      var url = value ? value[ 0 ] : null;
      var width = value ? value[ 1 ] : 0;
      var height = value ? value[ 2 ] : 0;      
      this.setCellContent( 4, url );
      this.setCellDimension( 4, width, height );      
    },
    
    _applySeparatorBorder : function( value, oldValue ) {
      qx.theme.manager.Border.getInstance().connect( this._queueSeparatorBorder, 
                                                     this, 
                                                     value);
    },
    
    _queueSeparatorBorder : function( value ) {
      this._separatorBorder = value;      
      this.addToQueue( "separatorBorder" );
    },
    
    // overwritten:
    _beforeRenderLayout : function( changes ) {
      if( this._isDropDown ) {
        if( changes.layoutY ) {
          // the cell used for the line needs to have 100% height 
          var padding = this.getPaddingTop() + this.getPaddingBottom(); 
          this._setCellHeight( 3, this.getInnerHeight() + padding );
        }
        if( changes.layoutX ) {
          // uses cell 0 (unused for tool-items) and 2 (text) 
          // to force the dropdown-area to the right
          var inner = this.getInnerWidth();
          this.setCellWidth( 0, 0 );
          this.setCellWidth( 2, null );
          var preferred = this.getPreferredInnerWidth();
          var diff = inner - preferred;
          if( diff > 0 ) {
            var space = this.getSpacing();
            if(    ( diff > ( space * 2 ) ) 
                && this.getHorizontalChildrenAlign() != "left" ) 
            {
              var spaceLeft = Math.round( diff * 0.5 ) - space; 
              var spaceRight = Math.round( diff * 0.5 );
              this.setCellWidth( 0, spaceLeft );
              this.setCellWidth( 2, this.getCellWidth( 2 ) + spaceRight );
            } else {
              this.setCellWidth( 2, this.getCellWidth( 2 ) + diff );
            }
          } 
        }        
        if( changes.separatorBorder ) {
          // apply the separator-border (currently verly limited)
          var style = this.getCellNode( 3 ).style;
          var borderWidth = this._separatorBorder.__widthLeft;
          var borderStyle = this._separatorBorder.getStyleLeft();
          var borderColor = this._separatorBorder.__colorLeft;
          // simplified version of "renderLeft" (of qx.ui.core.Border):
          style.borderLeftWidth = borderWidth || "0px";
          style.borderLeftStyle = borderStyle || "none";
          style.borderLeftColor = borderColor || "";
        }
      }
    },
    
    // overwritten:
    _renderCellLayoutY : function( cell ) {
      this.base( arguments, cell );       
      if( this._isDropDown && cell == 3 ) {
        this.getCellNode( cell ).style.top = 0;
      }
    }

  }
  
} );
