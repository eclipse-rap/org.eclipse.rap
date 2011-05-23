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
    
  construct : function() {
    this.base( arguments );
    this.setSelectable( false ); // Prevents user from selecting text
    this.setHeight( 16 );
    this._textNodes = [];
    this._usedNodes = 0;
    this._expandElement = null;
    this._checkBoxElement = null;
    this._selectionElements = [];
    this._styleMap = null;
    this._variant = null;
  },

  destruct : function() {
    this._textNodes = null;
    this._expandElement = null;
    this._checkBoxElement = null;
    this._selectionElements = null;
  },

  members : {
    
    renderItem : function( item, config, selected, hoverElement ) {
      this._usedNodes = 0;
      if( item != null ) {
        var renderSelected = selected || this.hasState( "dnd_selected" );
        var renderFullSelected = renderSelected && config.fullSelection;
        this._renderStates( item, config, renderFullSelected, hoverElement );
        this._renderBackground( item, config, renderSelected );
        this._renderIndention( item, config, hoverElement );
        this._renderCheckBox( item, config, hoverElement );
        this._renderCells( item, config, renderSelected );
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

    isSelectionClick : function( event, fullSelection ) {
      var result;
      var node = event.getDomTarget();
      if( fullSelection ) {
        result = this._checkBoxElement !== node;
      } else {
        result = this._selectionElements.indexOf( node ) != -1;
      }
      return result;
    },
    
    updateEvenState : function( index ) {
      this._setState( "even", index % 2 == 0 );
    },
    
    setLinesVisible : function( value ) {
      this._setState( "linesvisible", value );
    },

    ////////////
    // internals

    _renderStates : function( item, config, selected, hoverElement ) {
      this._setState( "checked", item.isChecked() );
      this._setState( "grayed", item.isGrayed() );
      this._setState( "parent_unfocused", this._renderAsUnfocused( config ) );
      this._setState( "selected", selected );
      this._renderVariant( item.getVariant() );
      this._renderOverState( hoverElement );
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

    _renderOverState : function( hoverElement ) {
      this._setState( "over", hoverElement !== null );
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
    
    _renderBackground : function( item, config, selected ) {
      // TODO [tb] : Support gradient
      var color = null
      if( this._getRenderThemingBackground( item, config, selected ) ) {
        color = this._styleMap.itemBackground;
      } else {
        color = item.getBackground();
      }
      // Note: "undefined" is a string stored in the themestore
      this.setBackgroundColor( color != "undefined" ? color : null );
    },
    
    _getRenderThemingBackground : function( item, config, selected ) {
      var renderFullSelection = selected && config.fullSelection;
      var hasItemBackground = item !== null && item.getBackground() !== null;
      var result =    !hasItemBackground 
                   || renderFullSelection 
                   || this._hasHoverBackground() 
                   || config.enabled === false; 
      return result;
    },
    
    _hasHoverBackground : function() {
      // TODO [tb] : This detection is not prefect; Should the item be hovered,
      // but a hover-independent theming-background be set, this returns true.
      return this.hasState( "over" ) && this._styleMap.itemBackground !== "undefined";
    },

    _renderIndention : function( item, config, hoverElement ) {
      var expandSymbol = this._getExpandSymbol( item, config, hoverElement );
      this._expandElement = null;
      if( expandSymbol != null ) {
        var element =  this._addIndentSymbol( item.getLevel(), config, expandSymbol );
        this._expandElement = element;
      }
      var lineSymbol = this._getLineSymbol( item, config );
      if( lineSymbol != null ) {
        var parent = item.getParent();
        while( !parent.isRootItem() ) {
          if( parent.hasNextSibling() ) {
            this._addIndentSymbol( parent.getLevel(), config, lineSymbol );
          }
          parent = parent.getParent();
        }
      }
    },

    _getExpandSymbol : function( item, config, hoverElement ) {
      var states = this._getParentStates( config );
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
      if( hoverElement !== null && hoverElement === this._expandElement ) {
        states.over = true;
      } 
      return this._getImageFromAppearance( "tree-indent", states );
    },

    _getLineSymbol : function( item, config ) {
      var states = this._getParentStates( config );
      states.line = true;
      return this._getImageFromAppearance( "tree-indent", states );
    },
    
    _getParentStates : function( config ) {
      var result = {};
      if( config.variant ) {
        result[ config.variant ] = true; 
      }
      return result;
    },

    _getImageFromAppearance : function( appearance, states ) {
      var manager = qx.theme.manager.Appearance.getInstance();
      var styleMap = manager.styleFrom( appearance, states );
      var valid = styleMap && styleMap.backgroundImage;
      return valid ? styleMap.backgroundImage : null;      
    },

    _addIndentSymbol : function( level, config, source ) {
      var result = null;
      var nextLevelOffset = ( level + 1 ) * config.indentionWidth;
      var cellWidth = config.itemWidth[ config.treeColumn ];
      if( nextLevelOffset <= cellWidth ) {
        var offset = level * config.indentionWidth;
        var height = this.getHeight(); 
        var width = nextLevelOffset - offset;
        var element = this._getImageElement( 3 );
        this._setImage( element, source, config.enabled );
        this._setBounds( element, offset, 0, width, height );
        result = element;
      }
      return result;
    },

    _renderCheckBox : function( item, config, hoverElement ) {
      if( config.hasCheckBoxes ) {
        var oldCheckBox = this._checkBoxElement;
        var states = this.__states;
        this._setState( "over", hoverElement !== null && hoverElement === oldCheckBox );
        var image = this._getImageFromAppearance( "tree-check-box", states );
        this._renderOverState( hoverElement );
        var element = this._getImageElement( 3 );
        this._setImage( element, image, config.enabled );
        var left = this._getCheckBoxLeft( item, config );
        var width = this._getCheckBoxWidth( item, config );
        var height = this.getHeight();
        this._setBounds( element, left, 0, width, height );
        this._checkBoxElement = element;
      }
    },

    _renderCells : function( item, config, selected, hoverElement ) {
      var columns = this._getColumnCount( config );
      if( !config.fullSelection && selected ) { 
        this._renderStates( item, config, false, hoverElement );
      }
      for( var i = 0; i < columns; i++ ) {
        this._renderCellBackground( item, i, config );
        if( !config.fullSelection && this._isTreeColumn( i, config ) ) {
          if( selected ) {
            this._renderStates( item, config, true, hoverElement );
          }
          var imageElement = this._renderCellImage( item, i, config );
          var labelElement = this._renderCellLabel( item, i, config );
          this._selectionElements = [ imageElement, labelElement ];
          if( selected ) {
            this._renderSelectionBackground( item, i, config );
            this._renderStates( item, config, false, hoverElement);
          }
        } else {
          this._renderCellImage( item, i, config );
          this._renderCellLabel( item, i, config );
        }
      }
    },
    
    _renderSelectionBackground : function( item, cell, config ) {
      if( this._styleMap.itemBackground !== null ) {
        var element = this._getBackgroundElement( 2 );
        element.style.backgroundColor = this._styleMap.itemBackground;
        var padding = config.selectionPadding;
        var left = this._getItemTextLeft( item, cell, config );
        left -= padding[ 0 ];
        var width = this._getItemTextWidth( item, cell, config );
        width += width > 0 ? padding[ 0 ] : 0;
        var visualWidth  = this._getVisualTextWidth( item, cell, config );
        visualWidth  += padding[ 0 ] + padding[ 1 ];
        width = Math.min( width, visualWidth );
        var height = this.getHeight();
        this._setBounds( element, left, 0, width, height );
      }
    },

    _renderCellBackground : function( item, cell, config ) {
      var background = this._getCellBackground( item, cell, config );
      if( background != "undefined" && background != this._styleMap.backgroundColor ) {
        var element = this._getBackgroundElement( 1 );
        element.style.backgroundColor = background;
        var left = this._getItemLeft( item, cell, config );
        var width = this._getItemWidth( item, cell, config );
        var height = this.getHeight();
        if( this.hasState( "linesvisible" ) ) {
          height -= 1;
        }
        this._setBounds( element, left, 0, width, height );
      }
    },

    _renderCellImage : function( item, cell, config ) {
      var source = item.getImage( cell );
      var element = null;
      if( source !== null ) {
        element = this._getImageElement( 3 );
        this._setImage( element, source, config.enabled );
        var left = this._getItemImageLeft( item, cell, config );
        var width = this._getItemImageWidth( item, cell, config );
        this._setBounds( element, left, 0, width, this.getHeight() );
      }
      return element;
    },

    _renderCellLabel : function( item, cell, config ) {
      // NOTE [tb] : When scrolling in Firefox, it may happen that the text
      //             becomes temorarily invisible. This is a browser-bug
      //             that ONLY occurs when Firebug is installed.
      var text = item.getText( cell );
      var element = null;
      if( text !== "" ) {
        element = this._getTextElement( 3 );
        //do not reset since we are about to reassign
        var left = this._getItemTextLeft( item, cell, config );
        var width = this._getItemTextWidth( item, cell, config );
        element.style.verticalAlign = "middle";  
        element.style.whiteSpace = "nowrap";
        if( this._isTreeColumn( cell, config ) ) {
          element.style.textAlign = "left";  
        } else {          
          element.style.textAlign = this._getAlignment( cell, config );  
        }
        element.innerHTML = text;
        this._setForeground( element, this._getCellColor( item, cell, config ) );
        this._setBounds( element, left, 0, width, this.getHeight() );
        this._setFont( element, this._getCellFont( item, cell, config ) );
        this._setTextDecoration( element, this._styleMap.textDecoration );
        element.style.lineHeight = element.style.height;
      }
      return element;
    },
    
    _getCellBackground : function( item, cell, config ) {
      var result;
      if(    this.hasState( "selected" ) 
          || config.enabled === false 
          || this._hasHoverBackground() 
      ) {
        result = "undefined";
      } else {
        result = item.getCellBackground( cell );
      }
      return result;
    },
    
    _getCellColor : function( item, cell, config ) {
      var result = item.getCellForeground( cell )
      if(    result === null 
          || result === "" 
          || config.enabled === false 
          || this.hasState( "selected" ) 
          || this._hasHoverBackground()
      ) {
        result = this._styleMap.itemForeground;
        if( result === "undefined" ) { 
          result = config.textColor;
        }
      }
      return result;
    },
    
    _getCellFont : function( item, cell, config ) {
      var result = item.getCellFont( cell );
      if( result === null || result === "" ) {
        result = config.font;
      }
      return result;
    },
    
    _getVisualTextWidth : function( item, cell, config ) {
      var text = item.getText( cell );
      var font = this._getCellFont( item, cell, config );
      var element = qx.ui.basic.Label._getMeasureNode();
      element.innerHTML = text;
      this._setFont( element, font );
      return element.scrollWidth;
    },
    
    _renderAsUnfocused : function( config ) {
      return !config.focused && !this.hasState( "dnd_selected" );
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

    _setTextDecoration : function( element, decoration ) {
      if( decoration == null || decoration === "none" ) {
        element.style.textDecoration = "";
      } else {
        element.style.textDecoration = decoration;
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

    _setImage : function( element, src, enabled ) {
      var opacity = enabled ? 1 : 0.3;
      org.eclipse.rwt.HtmlUtil.setBackgroundImage( element, src, opacity );
    },
    
    _getTextElement : function( zIndex ) {
      var result = this._getNextElement( zIndex );
      org.eclipse.rwt.HtmlUtil.setBackgroundImage( result, null );
      result.style.backgroundColor = "";
      // NOTE: It's important for the iPad not to set innerHTML twice. See bug 323988
      return result;
    },
    
    _getImageElement : function( zIndex ) {
      var result = this._getNextElement( zIndex );
      result.innerHTML = "";
      result.style.backgroundColor = "";
      return result;
    },
        
    _getBackgroundElement : function( zIndex ) {
      var result = this._getNextElement( zIndex );
      org.eclipse.rwt.HtmlUtil.setBackgroundImage( result, null );
      result.innerHTML = "";
      return result;
    },
        
    _getNextElement : function( zIndex ) {
      var result;
      var node = this._getTargetNode();
      if( node.childNodes.length > this._usedNodes ) {
        result = node.childNodes[ this._usedNodes ];
        result.style.display = "";
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

    ////////////////
    // layout-helper

    _getCheckBoxLeft : function( item, config ) {
      return this._correctOffset( config.checkBoxLeft, item, config );
    },
    
    _getCheckBoxWidth : function( item, config ) {
      var result = config.checkBoxWidth;
      var offset = this._getCheckBoxLeft( item, config );
      return this._correctWidth( result, offset, 0, config );
    },

    _getItemLeft : function( item, columnIndex, config ) {
      return config.itemLeft[ columnIndex ];
    },

    _getItemWidth : function( item, columnIndex, config ) {
      return config.itemWidth[ columnIndex ];
    },

    _getItemImageLeft : function( item, columnIndex, config ) {
      var result = config.itemImageLeft[ columnIndex ];
      if( this._isTreeColumn( columnIndex, config ) ) {
        result = this._correctOffset( result, item, config );
      }
      return result;
    },

    _getItemImageWidth : function( item, columnIndex, config ) {
      var result = config.itemImageWidth[ columnIndex ];
      if( this._isTreeColumn( columnIndex, config ) ) {
        var offset = this._getItemImageLeft( item, columnIndex, config );
        result = this._correctWidth( result, offset, columnIndex, config );
      }
      return result;
    },

    _getItemTextLeft : function( item, columnIndex, config ) {
      var result = config.itemTextLeft[ columnIndex ];
      if( this._isTreeColumn( columnIndex, config ) ) {
        result = this._correctOffset( result, item, config );
      }
      return result;
    },

    _getItemTextWidth : function( item, columnIndex, config ) {
      var result = config.itemTextWidth[ columnIndex ];
      if( this._isTreeColumn( columnIndex, config ) ) {
        var offset = this._getItemTextLeft( item, columnIndex, config );
        result = this._correctWidth( result, offset, columnIndex, config );
      }
      return result;
    },

    _correctOffset : function( offset, item, config ) {
      return offset + this._getIndentionOffset( item.getLevel() + 1, config );
    },

    _correctWidth : function( width, offset, column, config ) {
      var result = width;
      var columnEnd = config.itemLeft[ column ] + config.itemWidth[ column ];
      var elementEnd = offset + result;
      if( elementEnd > columnEnd ) {
        result = Math.max( 0, columnEnd - offset );
      }
      return result;
    },

    _getAlignment : function( column, config ) {
      return config.alignment[ column ] ? config.alignment[ column ] : "left";
    },

    _getIndentionOffset : function( level, config ) {
      // NOTE [tb] : Shoud actually add the treeColumns own offset, assumes 0 now.
      return config.indentionWidth * level;
    },
    
    _getColumnCount : function( config ) {
      return Math.max( 1, config.columnCount );
    },

    _isTreeColumn : function( columnIndex, config ) {
      return columnIndex === config.treeColumn;
    },
    
    //////////////
    // DND-Support
    
   supportsDrop : function() {
     return true;
   }
        
  }
  
} );
