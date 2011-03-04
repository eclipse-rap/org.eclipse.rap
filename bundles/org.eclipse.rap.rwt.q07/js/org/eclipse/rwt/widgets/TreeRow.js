/*******************************************************************************
 * Copyright (c) 2010, 2011 Innoopract Informationssysteme GmbH.
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
 * Used to represent a visible TreeItem.
 */
 
qx.Class.define( "org.eclipse.rwt.widgets.TreeRow", {

  extend : qx.ui.basic.Terminator,
    
  construct : function( tree ) {
    this.base( arguments );
    this.setSelectable( false ); // Prevents user from selecting text
    this.setAppearance( "tree-row" ); 
    this._tree = tree;
    this._textNodes = [];
    this._usedNodes = 0;
    this._expandElement = null;
    this._checkBoxElement = null;
    this._selectionElements = [];
    this._styleMap = null;
    this._variant = null;
  },

  destruct : function() {
    this._tree = null;
    this._textNodes = null;
    this._expandElement = null;
    this._checkBoxElement = null;
    this._selectionElements = null;
  },

  members : {
    
    renderItem : function( item ) {
      this._usedNodes = 0;
      if( item != null ) {
        this._renderStates( item, this._tree.getHasFullSelection() );
        this._renderBackground( item );
        this._renderIndention( item );
        this._renderCheckBox( item );
        this._renderCells( item );
      } else {
        this.setBackgroundColor( null );
      }
      this._hideRemainingElements();
    },

    isExpandSymbolTarget : function( event ) {
      var node = event.getDomTarget();
      return this._expandElement !== null && this._expandElement === node;
    },
    
    isCheckBoxTarget : function( event ) {
      var node = event.getDomTarget();
      return this._checkBoxElement !== null && this._checkBoxElement === node;      
    },
    
    isSelectionClick : function( event ) {
      var result;
      var node = event.getDomTarget();
      if( this._tree.getHasFullSelection() ) {
        result = this._checkBoxElement !== node;
      } else {
        result = this._selectionElements.indexOf( node ) != -1;
      }
      return result;
    },
    
    updateEvenState : function( index ) {
      this._setState( "even", index % 2 == 0 );
    },
    
    updateGridlinesState : function( value ) {
      this._setState( "linesvisible", value );
    },

    ////////////
    // internals

    _renderStates : function( item, selection ) {
      this._setState( "checked", item.isChecked() );
      this._setState( "grayed", item.isGrayed() );
      this._setState( "parent_unfocused", this._renderAsUnfocused() );
      this._setState( "selected", selection && this._renderAsSelected( item ) );
      this._renderVariant( item.getVariant() );
      this._renderOverState( item );
      this._styleMap = this._getStyleMap();
    },

    _renderVariant : function( variant ) {
      if( this._variant != variant ) {
        if( this._variant != null ) {
          this._setState( this._variant, false );
        }
        this._variant = variant;
        if( this._variant != null ) {
          this._setState( this._variant, true );
        }
      }
    },

    _renderOverState : function( item ) {
      this._setState( "over", this._tree.isHoverItem( item ) );
    },

    _setState : function( state, value ) {
      if( !this.__states ) {
        this.__states = {};
      }
      if( value ) {
        this.__states[ state ] = true;
      } else {
        delete this.__states[ state ];
      }
    },
    
    _getStyleMap : function() {
      var manager = qx.theme.manager.Appearance.getInstance();
      return manager.styleFrom( this.getAppearance(), this.__states );      
    },
    
    _styleFromMap : function() {
      // TODO [tb] : Overwrites (now) useless function from Widget.js 
      //             Find a clean way to disable renderAppearance.
      //             This would need changes to Widget.js
    },
    
    _renderBackground : function( item ) {
      // TODO [tb] : Support gradient
      var color = null
      if( this._getRenderThemingBackground( item ) ) {
        color = this._styleMap.itemBackground;
      } else {
        color = item.getBackground();
      }
      // Note: "undefined" is a string stored in the themestore
      this.setBackgroundColor( color != "undefined" ? color : null );
    },
    
    _getRenderThemingBackground : function( item ) {
      var renderFullSelection =    this._renderAsSelected( item ) 
                                && this._tree.getHasFullSelection();
      var hasItemBackground = item !== null && item.getBackground() !== null;
      var result =    !hasItemBackground 
                   || renderFullSelection 
                   || this._hasHoverBackground(); 
      return result;
    },
    
    _hasHoverBackground : function() {
      // TODO [tb] : This detection is not prefect; Should the item be hovered,
      // but a hover-independent theming-background be set, this returns true.
      var result =    this.hasState( "over" ) 
                   && this._styleMap.itemBackground !== "undefined"; 
      return result;
    },

    _renderIndention : function( item ) {
      var expandSymbol = this._getExpandSymbol( item );
      this._expandElement = null;
      if( expandSymbol != null ) {
        var element =  this._addIndentSymbol( item.getLevel(), expandSymbol );
        this._expandElement = element;
      }
      var lineSymbol = this._getLineSymbol( item );
      if( lineSymbol != null ) {
        var parent = item.getParent();
        while( !parent.isRootItem() ) {
          if( parent.hasNextSibling() ) {
            this._addIndentSymbol( parent.getLevel(), lineSymbol );
          }
          parent = parent.getParent();
        }
      }
    },

    _getExpandSymbol : function( item ) {
      var states = this._tree.getStatesCopy();
      if( item.getLevel() == 0 && !item.hasPreviousSibling() ) {
        states.first = true;
      }
      if( !item.hasNextSibling() ) {
        states.last = true;
      }
      if( item.hasChildren() ) {
        if( item.isExpanded() ) {
          states.expanded = true;
        } else {
          states.collapsed = true;
        } 
      }
      if( this._tree.isHoverElement( this._expandElement ) ) {
        states.over = true;
      } 
      return this._getImageFromAppearance( "tree-indent", states );
    },

    _getLineSymbol : function( item ) {
      var states = this._tree.getStatesCopy();
      states.line = true;
      return this._getImageFromAppearance( "tree-indent", states );
    },
    
    _getImageFromAppearance : function( appearance, states ) {
      var manager = qx.theme.manager.Appearance.getInstance();
      var styleMap = manager.styleFrom( appearance, states );
      var valid = styleMap && styleMap.backgroundImage;
      return valid ? styleMap.backgroundImage : null;      
    },
    
    _addIndentSymbol : function( level, source ) {
      var result = null;
      var nextLevelOffset = this._tree.getIndentionOffset( level + 1 );
      var cellWidth = this._tree.getTreeColumnWidth();
      if( nextLevelOffset <= cellWidth ) {
        var offset = this._tree.getIndentionOffset( level );
        var height = this._tree.getItemHeight(); 
        var width = nextLevelOffset - offset;
        var element = this._getNextElement( 3 );
        this._setImage( element, source, true );
        this._setBounds( element, offset, 0, width, height );
        result = element;
      }
      return result;
    },

    _renderCheckBox : function( item ) {
      if( this._tree.getHasCheckBoxes() ) {
        var oldCheckBox = this._checkBoxElement;
        var states = this.__states;
        this._setState( "over", this._tree.isHoverElement( oldCheckBox ) );
        var image = this._getImageFromAppearance( "tree-check-box", states );
        this._renderOverState( item );
        var element = this._getNextElement( 3 );
        this._setImage( element, image, true );
        var left = this._tree.getCheckBoxLeft( item );
        var width = this._tree.getCheckBoxWidth( item );
        var height = this._tree.getItemHeight();
        this._setBounds( element, left, 0, width, height );
        this._checkBoxElement = element;
      }
    },

    _renderCells : function( item ) {
      var columns = this._tree.getColumnCount();
      var fullSelection = this._tree.getHasFullSelection();
      var selected = this._renderAsSelected( item );
      if( !fullSelection && selected ) { 
        this._renderStates( item, false );
      }
      for( var i = 0; i < columns; i++ ) {
        this._renderCellBackground( item, i );
        if( !fullSelection && this._tree.isTreeColumn( i ) ) {
          if( selected ) {
            this._renderStates( item, true );
          }
          var imageElement = this._renderCellImage( item, i );
          var labelElement = this._renderCellLabel( item, i );
          this._selectionElements = [ imageElement, labelElement ];
          if( selected ) {
            this._renderSelectionBackground( item, i );
            this._renderStates( item, false );
          }
        } else {
          this._renderCellImage( item, i );
          this._renderCellLabel( item, i );
        }
      }
    },
    
    _renderSelectionBackground : function( item, cell ) {
      if( this._styleMap.itemBackground !== null ) {
        var element = this._getNextElement( 2 );
        element.style.backgroundColor = this._styleMap.itemBackground;
        var padding = this._tree.getSelectionPadding();
        var left = this._tree.getItemTextLeft( item, cell, true );
        left -= padding[ 0 ];
        var width = this._tree.getItemTextWidth( item, cell, true );
        width += width > 0 ? padding[ 0 ] : 0;
        var visualWidth  = this._getVisualTextWidth( item, cell );
        visualWidth  += padding[ 0 ] + padding[ 1 ];
        width = Math.min( width, visualWidth );
        var height = this._tree.getItemHeight();
        this._setBounds( element, left, 0, width, height );
      }
    },

    _renderCellBackground : function( item, cell ) {
      var background = this._getCellBackground( item, cell );
      if(    background != "undefined" 
          && background != this._styleMap.backgroundColor ) 
      {
        var element = this._getNextElement( 1 );
        element.style.backgroundColor = background;
        var left = this._tree.getItemLeft( item, cell, false );
        var width = this._tree.getItemWidth( item, cell, false );
        var height = this._tree.getItemHeight();
        this._setBounds( element, left, 0, width, height );
      }
    },

    _renderCellImage : function( item, cell ) {
      var source = item.getImage( cell );
      var element = null;
      if( source !== null ) {
        element = this._getNextElement( 3 );
        this._setImage( element, source, true );
        var left = this._tree.getItemImageLeft( item, cell );
        var width = this._tree.getItemImageWidth( item, cell );
        this._setBounds( element, left, 0, width, this._tree.getItemHeight() );
      }
      return element;
    },

    _renderCellLabel : function( item, cell ) {
      // NOTE [tb] : When scrolling in Firefox, it may happen that the text
      //             becomes temorarily invisible. This is a browser-bug
      //             that ONLY occurs when Firebug is installed.
      var text = item.getText( cell );
      var element = null;
      if( text !== "" ) {
        element = this._getNextElement( 3 );
        var left = this._tree.getItemTextLeft( item, cell );
        var width = this._tree.getItemTextWidth( item, cell );
        element.style.verticalAlign = "middle";  
        element.style.whiteSpace = "nowrap";
        if( this._tree.isTreeColumn( cell ) ) {
          element.style.textAlign = "left";  
        } else {          
          element.style.textAlign = this._tree.getAlignment( cell );  
        }
        element.innerHTML = text;
        this._setForeground( element, this._getCellColor( item, cell ) );
        this._setBounds( element, left, 0, width, this._tree.getItemHeight() );
        this._setFont( element, this._getCellFont( item, cell ) );
        element.style.lineHeight = element.style.height;
        var decoration = this._styleMap.textDecoration;
        element.style.textDecoration = decoration === "none" ? "" : decoration;
      }
      return element;
    },
    
    _getCellBackground : function( item, cell ) {
      var result;
      if(    this.hasState( "selected" ) 
          || this._hasHoverBackground() 
      ) {
        result = "undefined";
      } else {
        result = item.getCellBackground( cell );
      }
      return result;
    },
    
    _getCellColor : function( item, cell ) {
      var result = item.getCellForeground( cell )
      if(    result === null 
          || result === "" 
          || this.hasState( "selected" ) 
          || this._hasHoverBackground()
      ) {
        result = this._styleMap.itemForeground;
        if( result === "undefined" ) { 
          result = this._tree.getTextColor();
        }
      }
      return result;
    },
    
    _getCellFont : function( item, cell ) {
      var result = item.getCellFont( cell );
      if( result === null || result === "" ) {
        result = this._tree.getFont();
      }
      return result;
    },
    
    _getVisualTextWidth : function( item, cell ) {
      var text = item.getText( cell );
      var font = this._getCellFont( item, cell );
      var element = qx.ui.basic.Label._getMeasureNode();
      element.innerHTML = text;
      this._setFont( element, font );
      return element.scrollWidth;
    },
    
    _renderAsSelected : function( item ) {
      return    this._tree.isItemSelected( item ) 
             || this.hasState( "dnd_selected" );
    },
    
    _renderAsUnfocused : function() {
      return !this._tree.isFocused() && !this.hasState( "dnd_selected" );
    },
    
    /////////////
    // DOM-Helper

    _setFont : function( element, font ) {
      if( font == "" || font == null ) {
        // Resetting style.font causes errors in IE with any of these syntaxes:
        // node.style.font = null | undefined | "inherit" | "";
        if( !qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
          element.style.font = font;
        }
        element.style.fontFamily = "";
        element.style.fontSize = "";
        element.style.fontVariant = "";
        element.style.fontStyle = "";
        element.style.fontWeight = "";
      } else {
        if( font instanceof qx.ui.core.Font ) {
          font.renderStyle( element.style );
        } else {
          element.style.font = font;
        }
      }
    },
    
    _setBounds : function( element, x, y, width, height ) {
      try{ 
        element.style.left = x + "px";
        element.style.top = y + "px";
        element.style.width = width + "px";
        element.style.height = height + "px";
      }catch( ex ) {
        throw "setBounds failed: " + [ element, x, y, width, height ];
      }
    },

    _setForeground : function( element, color ) {
      element.style.color = color != null ? color : "";
    },

    _setImage : function( element, src, center ) {
      if( src !== null ) {
        element.style.backgroundImage = "url( " + src + ")";
      } else {
        element.style.backgroundImage = "";
      }
      element.style.backgroundRepeat = "no-repeat";
      element.style.backgroundPosition = center ? "center" : "";
    },
        
    _getNextElement : function( zIndex ) {
      var result;
      var node = this._getTargetNode();
      if( node.childNodes.length > this._usedNodes ) {
        result = node.childNodes[ this._usedNodes ];
        result.style.display = "";
        result.style.backgroundColor = "";
        result.style.backgroundImage = "";
        result.innerHTML = "";
      } else {
        result = document.createElement( "div" );
        result.style.position = "absolute";
        result.style.overflow = "hidden";  
        node.appendChild( result );
      }
      result.style.zIndex = zIndex;
      this._usedNodes++; //TODO store in element? (method could be static again)
      return result;
    },
    
    _hideRemainingElements : function() {
      var node = this._getTargetNode();
      for( var i = this._usedNodes; i < node.childNodes.length; i++ ) {
        node.childNodes[ i ].style.display = "none";
      }
    },
    
    //////////////
    // DND-Support
    
   supportsDrop : function() {
     return true;
   }
        
  }
  
} );
