/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.ToolItem", {

  extend : rwt.widgets.base.BasicButton,

  construct : function( itemType ) {
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
    this._rawText = null;
    this._mnemonicIndex = null;
    this.setAppearance( "toolbar-button" );
    this.removeEventListener( "keydown", this._onKeyDown );
    this.removeEventListener( "keyup", this._onKeyUp );
  },

  destruct : function() {
    this.setMnemonicIndex( null );
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

  events : {
    "dropDownClicked" : "rwt.event.Event"
  },

  members : {
    // overwritten:
    _CELLORDER : [ "image", "image", "label", "label", "image" ],

    // overwritten:
    _onKeyPress : function( event ) {
      // give to toolBar for keyboard control (left/right keys):
      this.getParent().dispatchEvent( event );
      this.base( arguments, event );
    },

    setText : function( value ) {
      this._rawText = value;
      this._mnemonicIndex = null;
      this._applyText( false );
    },

    setMnemonicIndex : function( value ) {
      this._mnemonicIndex = value;
      var mnemonicHandler = rwt.widgets.util.MnemonicHandler.getInstance();
      if( ( typeof value === "number" ) && ( value >= 0 ) ) {
        mnemonicHandler.add( this, this._onMnemonic );
      } else {
        mnemonicHandler.remove( this );
      }
    },

    getMnemonicIndex : function() {
      return this._mnemonicIndex;
    },

    _applyText : function( mnemonic ) {
      if( this._rawText ) {
        var mnemonicIndex = mnemonic ? this._mnemonicIndex : undefined;
        var text = rwt.util.Encoding.escapeText( this._rawText, mnemonicIndex );
        this.setCellContent( 2, text );
      } else {
        this.setCellContent( 2, null );
      }
    },

    _onMnemonic : function( event ) {
      switch( event.type ) {
        case "show":
          this._applyText( true );
        break;
        case "hide":
          this._applyText( false );
        break;
        case "trigger":
          var charCode = this._rawText.toUpperCase().charCodeAt( this._mnemonicIndex );
          if( event.charCode === charCode ) {
            this.execute();
            event.success = true;
          }
        break;
      }
    },

    ////////////////////
    // Dropdown-support

    // overwritten:
    _onMouseDown : function( event ) {
      if ( event.getTarget() == this && event.isLeftButtonPressed() ) {
        this.removeState( "abandoned" );
        if( this._isDropdownClick( event ) ) {
          this._onDropDownClick();
        } else {
          this.addState( "pressed" );
        }
      }
    },

    _isDropdownClick : function( event ) {
      var result = false;
      var node = this.getCellNode( 3 );
      if( node != null ) {
        var nodeLeft = rwt.html.Location.getLeft( node );
        var clickLeft = event.getClientX();
        result = clickLeft > nodeLeft;
      }
      return result;
    },

    _onDropDownClick : function() {
      if(    !rwt.remote.EventUtil.getSuspended()
          && this._hasSelectionListener
          && this._sendEvent )
      {
        rwt.remote.EventUtil.notifySelected( this, 0, 0, 0, 0, "arrow" );
      }
      this.dispatchSimpleEvent( "dropDownClicked" );
    },

    _applyDropDownArrow : function( value, oldValue ) {
      var url = value ? value[ 0 ] : null;
      var width = value ? value[ 1 ] : 0;
      var height = value ? value[ 2 ] : 0;
      this.setCellContent( 4, url );
      this.setCellDimension( 4, width, height );
    },

    _applySeparatorBorder : function( value, oldValue ) {
      this._queueSeparatorBorder( value );
    },

    _queueSeparatorBorder : function( value ) {
      this._separatorBorder = value;
      this.addToQueue( "separatorBorder" );
    },

    // overwritten:
    _beforeRenderLayout : function( changes ) {
      // TODO [tb] : Is there a less error-prone and shabby way to layout the dropDown icon?
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
          this._setCellWidth( 0, 0 );
          this._setCellWidth( 2, null );
          var preferred = this.getPreferredInnerWidth();
          var diff = inner - preferred;
          if( diff > 0 ) {
            var space = this.getSpacing();
            if( ( diff > ( space * 2 ) ) && this.getHorizontalChildrenAlign() != "left" ) {
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
          var borderWidth = this._separatorBorder.getWidthLeft();
          var borderStyle = this._separatorBorder.getStyleLeft();
          var borderColor = this._separatorBorder.getColorLeft();
          style.borderLeftWidth = ( borderWidth || 0 ) + "px";
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
