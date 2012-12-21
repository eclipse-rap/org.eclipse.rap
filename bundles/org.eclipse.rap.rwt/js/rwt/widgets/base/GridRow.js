/*******************************************************************************
 * Copyright (c) 2010, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

/**
 * Represents a visible TreeItem.
 */

(function() {

var HtmlUtil = rwt.html.Style;
var Variant = rwt.util.Variant;

rwt.qx.Class.define( "rwt.widgets.base.GridRow", {

  extend : rwt.widgets.base.Terminator,

  construct : function() {
    this.base( arguments );
    this.setSelectable( false ); // Prevents user from selecting text
    this.setHeight( 16 );
    this._styleMap = null;
    this._variant = null;
    this._expandElement = null;
    this._checkBoxElement = null;
    this._treeColumnElements = [];
    this._cellLabels = [];
    this._cellImages = [];
    this._cellCheckImages = [];
    this._cellBackgrounds = [];
    this._miscNodes = [];
    this._usedMiscNodes = 0;
    this._cellsRendered = 0;
  },

  destruct : function() {
    this._expandElement = null;
    this._checkBoxElement = null;
    this._treeColumnElements = null;
    this._cellLabels = null;
    this._cellImages = null;
    this._cellCheckImages = null;
    this._cellBackgrounds = null;
    this._miscNodes = null;
  },

  events : {
    "itemRendered" : "rwt.event.Event"
  },

  members : {

    renderItem : function( item, config, selected, hoverTarget, scrolling ) {
      this._usedMiscNodes = 0;
      if( item !== null ) {
        var renderSelected = this._renderAsSelected( config, selected );
        var renderFullSelected = renderSelected && config.fullSelection;
        var heightChanged = this._renderHeight( item, config );
        var contentOnly = scrolling && !heightChanged;
        this._renderStates( item, config, renderFullSelected, hoverTarget );
        this._renderBackground( item, config, renderSelected );
        if( config.treeColumn !== -1 ) {
          this._renderIndention( item, config, hoverTarget );
        }
        this._renderCheckBox( item, config, hoverTarget, contentOnly );
        this._renderCells( item, config, renderSelected, hoverTarget, contentOnly );
        this._hideRemainingElements();
      } else {
        this.setBackgroundColor( null );
        this.setBackgroundImage( null );
        this.setBackgroundGradient( null );
        this._clearContent( config );
        if( !scrolling && config ) {
          this._renderAllBounds( config );
        }
      }
      this.dispatchSimpleEvent( "itemRendered", item );
    },

    getTargetIdentifier : function( event ) {
      var node = event.getDomTarget();
      var result = [ "other" ];
      if( this._expandElement !== null && this._expandElement === node ) {
        result = [ "expandIcon" ];
      } else if( this._checkBoxElement !== null && this._checkBoxElement === node ) {
        result = [ "checkBox" ];
      } else if( this._cellCheckImages.indexOf( node ) !== -1 ) {
        var cell = this._cellCheckImages.indexOf( node );
        result = [ "cellCheckBox", cell ];
      } else {
        while( node !== this.getElement() && result[ 0 ] === "other" ) { // Can be removed?
          if( this._treeColumnElements.indexOf( node ) != -1 ) {
            result = [ "treeColumn" ]; // TODO [tb] : now should be [ "label", 0 ] / [ "image", 0 ]
          }
          node = node.parentNode;
        }
      }
      return result;
    },

    updateEvenState : function( index ) {
      this.setState( "even", index % 2 === 0 );
    },

    ////////////
    // internals

    _renderHeight : function( item, config ) {
      var result = false;
      var itemHeight = item.getOwnHeight();
      if( itemHeight !== this.getHeight() ) {
        this.setHeight( item.getOwnHeight() );
        result = true;
      }
      return result;
    },

    _renderStates : function( item, config, selected, hoverTarget ) {
      this.setState( "checked", item.isChecked() );
      this.setState( "grayed", item.isGrayed() );
      this.setState( "parent_unfocused", this._renderAsUnfocused( config ) );
      this.setState( "selected", selected );
      this._renderVariant( item.getVariant() );
      this._renderOverState( hoverTarget );
      this._styleMap = this._getStyleMap();
    },

    _renderVariant : function( variant ) {
      if( this._variant != variant ) {
        if( this._variant != null ) {
          this.setState( this._variant, false );
        }
        this._variant = variant;
        if( this._variant != null ) {
          this.setState( this._variant, true );
        }
      }
    },

    _renderOverState : function( hoverTarget ) {
      this.setState( "over", hoverTarget !== null );
    },

    setState : function( state, value ) {
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
      var manager = rwt.theme.AppearanceManager.getInstance();
      return manager.styleFrom( this.getAppearance(), this.__states );
    },

    _styleFromMap : function() {
      // TODO [tb] : Overwrites (now) useless function from Widget.js
      //             Find a clean way to disable renderAppearance.
      //             This would need changes to Widget.js
    },

    _renderBackground : function( item, config, selected ) {
      var color = null;
      var image = null;
      var gradient = null;
      if( this._hasOverlayBackground() ) {
        // TODO [tb] : would currently not behave in an actual overlay (if semi-transparent)
        color = this._styleMap.overlayBackground;
        image = this._styleMap.overlayBackgroundImage;
        gradient = this._styleMap.overlayBackgroundGradient;
      } else if( config.enabled !== false && item !== null && item.getBackground() !== null ) {
        color = item.getBackground();
      } else {
        color = this._styleMap.itemBackground;
        image = this._styleMap.itemBackgroundImage;
        gradient = this._styleMap.itemBackgroundGradient;
      }
      // Note: "undefined" is a string stored in the themestore
      this.setBackgroundColor( color !== "undefined" ? color : null );
      this.setBackgroundImage( image !== "undefined" ? image : null );
      this.setBackgroundGradient( gradient !== "undefined" ? gradient : null );
    },

    _hasOverlayBackground : function() {
      var result =    this._styleMap.overlayBackground !== "undefined"
                   || this._styleMap.overlayBackgroundImage !== null
                   || this._styleMap.overlayBackgroundGradient !== null;
      return result;
    },

    _renderIndention : function( item, config, hoverTarget ) {
      var expandSymbol = this._getExpandSymbol( item, config, hoverTarget );
      if( expandSymbol != null ) {
        var element =  this._addIndentSymbol( item.getLevel(), config, expandSymbol );
        this._expandElement = element;
      } else {
        this._expandElement = null;
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

    _getExpandSymbol : function( item, config, hoverTarget ) {
      var states = this._getParentStates( config );
      if( item.getLevel() === 0 && !item.hasPreviousSibling() ) {
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
      if( hoverTarget && hoverTarget[ 0 ] === "expandIcon" ) {
        states.over = true;
      }
      return this._getImageFromAppearance( "indent", states );
    },

    _getLineSymbol : function( item, config ) {
      var states = this._getParentStates( config );
      states.line = true;
      return this._getImageFromAppearance( "indent", states );
    },

    _getParentStates : function( config ) {
      var result = {};
      if( config.variant ) {
        result[ config.variant ] = true;
      }
      return result;
    },

    _getImageFromAppearance : function( image, states ) {
      var appearance = this.getAppearance() + "-" + image;
      var manager = rwt.theme.AppearanceManager.getInstance();
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
        var element = this._getMiscImage();
        this._setImage( element, source, config.enabled );
        this._setBounds( element, offset, 0, width, height );
        result = element;
      }
      return result;
    },

    _renderCheckBox : function( item, config, hoverTarget, contentOnly ) {
      if( config.hasCheckBoxes ) {
        var states = this.__states;
        this.setState( "over", hoverTarget && hoverTarget[ 0 ] === "checkBox" );
        var image = this._getImageFromAppearance( "check-box", states );
        this._renderOverState( hoverTarget );
        if( this._checkBoxElement === null ) {
          this._checkBoxElement = this._createElement( 3 );
          this._checkBoxElement.style.backgroundRepeat = "no-repeat";
          this._checkBoxElement.style.backgroundPosition = "center";
        }
        this._setImage( this._checkBoxElement, image, config.enabled );
        if( config.treeColumn !== -1 || !contentOnly ) {
          var left = this._getCheckBoxLeft( item, config );
          var width = this._getCheckBoxWidth( item, config );
          var height = this.getHeight();
          this._setBounds( this._checkBoxElement, left, 0, width, height );
        }
      }
    },

    _renderCells : function( item, config, selected, hoverTarget, contentOnly ) {
      var columns = this._getColumnCount( config );
      if( this._cellsRendered > columns ) {
        this._removeCells( columns, this._cellsRendered );
      }
      if( !config.fullSelection && selected ) {
        this._renderStates( item, config, false, hoverTarget );
      }
      for( var i = 0; i < columns; i++ ) {
        var isTreeColumn = this._isTreeColumn( i, config );
        if( this._getItemWidth( item, i, config ) > 0 ) {
          this._renderCellBackground( item, i, config, contentOnly );
          if( !config.fullSelection && isTreeColumn ) {
            if( selected ) {
              this._renderStates( item, config, true, hoverTarget );
            }
            this._renderCellCheckBox( item, i, config, isTreeColumn, contentOnly, hoverTarget );
            var imageElement = this._renderCellImage( item, i, config, isTreeColumn, contentOnly );
            var labelElement = this._renderCellLabel( item, i, config, isTreeColumn, contentOnly );
            this._treeColumnElements = [ imageElement, labelElement ];
            if( selected ) {
              this._renderSelectionBackground( item, i, config );
              this._renderStates( item, config, false, hoverTarget);
            }
          } else {
            this._renderCellCheckBox( item, i, config, isTreeColumn, contentOnly, hoverTarget );
            this._renderCellImage( item, i, config, isTreeColumn, contentOnly );
            this._renderCellLabel( item, i, config, isTreeColumn, contentOnly );
          }
        } else {
          this._removeCell( i );
        }
      }
      this._cellsRendered = columns;
    },

    _renderSelectionBackground : function( item, cell, config ) {
      var overlayBg = this._styleMap.overlayBackground;
      var itemBg = this._styleMap.itemBackground;
      var hasOverlayBg = overlayBg !== "undefined" && overlayBg !== null;
      var hasItemBg = itemBg !== "undefined" && itemBg !== null;
      if( hasItemBg || hasOverlayBg ) {
        var element = this._getMiscBackground();
        element.style.backgroundColor = hasOverlayBg ? overlayBg : itemBg;
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

    _renderCellBackground : function( item, cell, config, contentOnly ) {
      var background = this._getCellBackgroundColor( item, cell, config );
      var renderBounds = false;
      if( background !== "undefined" && background != this._styleMap.backgroundColor ) {
        renderBounds = !contentOnly || !this._cellBackgrounds[ cell ];
        var element = this._getBackgroundElement( cell );
        element.style.backgroundColor = background;
      } else if( this._cellBackgrounds[ cell ] ){
        this._cellBackgrounds[ cell ].style.backgroundColor = "transparent";
        renderBounds = !contentOnly;
      }
      if( renderBounds ) {
        this._renderCellBackgroundBounds( item, cell, config );
      }
    },

    _renderCellBackgroundBounds : function( item, cell, config ) {
      var element = this._cellBackgrounds[ cell ];
      if( element ) {
        var left = this._getItemLeft( item, cell, config );
        var width = this._getItemWidth( item, cell, config );
        var height = this.getHeight();
        if( this.hasState( "linesvisible" ) ) {
          height -= 1;
        }
        this._setBounds( element, left, 0, width, height );
      }
    },

    _renderCellCheckBox : function( item, cell, config, isTreeColumn, contentOnly, hoverTarget ) {
      var element = null;
      var renderBounds = false;
      if( config.itemCellCheck[ cell ] ) {
        this.setState( "checked", item.isCellChecked( cell ) );
        this.setState( "grayed", item.isCellGrayed( cell ) );
        this.setState( "over",    hoverTarget
                               && hoverTarget[ 0 ] === "cellCheckBox"
                               && hoverTarget[ 1 ] === cell );
        var source = this._getImageFromAppearance( "check-box", this.__states );
        renderBounds = isTreeColumn || !contentOnly || !this._cellCheckImages[ cell ];
        element = this._getCellCheckImage( cell );
        this._setImage( element, source, config.enabled );
      }
      if( renderBounds ) {
        this._renderCellCheckBounds( item, cell, config );
      }
      return element;
    },

    _renderCellCheckBounds : function( item, cell, config ) {
      var element = this._cellCheckImages[ cell ];
      if( element ) {
        var left = this._getCellCheckLeft( item, cell, config );
        var width = this._getCellCheckWidth( item, cell, config );
        this._setBounds( element, left, 0, width, this.getHeight() );
      }
    },

    _renderCellImage : function( item, cell, config, isTreeColumn, contentOnly ) {
      var source = item.getImage( cell );
      var element = null;
      var renderBounds = false;
      if( source !== null ) {
        renderBounds = isTreeColumn || !contentOnly || !this._cellImages[ cell ];
        element = this._getCellImage( cell );
        this._setImage( element, source, renderBounds ? config.enabled : null );
      } else if( this._cellImages[ cell ] ) {
        renderBounds = isTreeColumn || !contentOnly;
        element = this._getCellImage( cell );
        this._setImage( element, null, null );
      }
      if( renderBounds ) {
        this._renderCellImageBounds( item, cell, config );
      }
      return element;
    },

    _renderCellImageBounds : function( item, cell, config ) {
      var element = this._cellImages[ cell ];
      if( element ) {
        var left = this._getItemImageLeft( item, cell, config );
        var width = this._getItemImageWidth( item, cell, config );
        this._setBounds( element, left, 0, width, this.getHeight() );
      }
    },

    _renderCellLabel : function( item, cell, config, isTreeColumn, contentOnly ) {
      // NOTE [tb] : When scrolling in Firefox, it may happen that the text
      //             becomes temorarily invisible. This is a browser-bug
      //             that ONLY occurs when Firebug is installed.
      var element = null;
      var renderBounds = false;
      if( item.hasText( cell ) ) {
        renderBounds = isTreeColumn || !contentOnly || !this._cellLabels[ cell ];
        element = this._getTextElement( cell, config );
        this._renderElementContent( element, item, cell, config.markupEnabled );
        if( renderBounds ) {
          element.style.textAlign = isTreeColumn ? "left" : this._getAlignment( cell, config );
        }
        this._styleLabel( element, item, cell, config );
      } else if( this._cellLabels[ cell ] ) {
        renderBounds = isTreeColumn || !contentOnly;
        element = this._getTextElement( cell, config );
        this._renderElementContent( element, null, -1, config.markupEnabled );
      }
      if( renderBounds ) {
        this._renderCellLabelBounds( item, cell, config );
      }
      return element;
    },

    _renderCellLabelBounds : function( item, cell, config ) {
      var element = this._cellLabels[ cell ];
      if( element ) {
        var left = this._getItemTextLeft( item, cell, config );
        var width = this._getItemTextWidth( item, cell, config );
        this._setBounds( element, left, 0, width, this.getHeight() );
        element.style.lineHeight = config.markupEnabled ? "" : element.style.height;
      }
    },

    _renderElementContent : Variant.select( "qx.client", {
      "mshtml|newmshtml" : function( element, item, cell, markupEnabled ) {
        if( markupEnabled ) {
          var html = item ? item.getText( cell, false ) : "";
          if( element.rap_Markup !== html ) {
            element.innerHTML = html;
            element.rap_Markup = html;
          }
        } else {
          // innerText is faster, does the escaping itself
          element.innerText = item ? item.getText( cell, false ) : "";
        }
      },
      "default" : function( element, item, cell, markupEnabled ) {
        var html = item ? item.getText( cell, !markupEnabled ) : "";
        if( markupEnabled ) {
          if( html !== element.rap_Markup ) {
            element.innerHTML = html;
            element.rap_Markup = html;
          }
        } else {
          element.innerHTML = html;
        }
      }
    } ),

    _styleLabel : function( element, item, cell, config ) {
      this._setForeground( element, this._getCellColor( item, cell, config ) );
      this._setFont( element, this._getCellFont( item, cell, config ) );
      this._setTextDecoration( element, this._styleMap.textDecoration );
      HtmlUtil.setTextShadow( element, this._styleMap.textShadow );
    },

    _getCellBackgroundColor : function( item, cell, config ) {
      var result;
      if(    config.enabled === false
          || this._styleMap.overlayBackground !== "undefined"
      ) {
        result = "undefined";
      } else {
        result = item.getCellBackground( cell );
      }
      return result;
    },

    _getCellColor : function( item, cell, config ) {
      var result = null;
      if( this._styleMap.overlayForeground !== "undefined" ) {
        result = this._styleMap.overlayForeground;
      } else if( config.enabled !== false && item.getCellForeground( cell ) ) {
        result = item.getCellForeground( cell );
      } else {
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
      var calc = rwt.widgets.util.FontSizeCalculation;
      var result = 0;
      if( this._cellLabels[ cell ] ) {
        var font = this._getCellFont( item, cell, config );
        var fontProps = this._getFontProps( font );
        var text = this._cellLabels[ cell ].innerHTML;
        var dimensions = calc.computeTextDimensions( text, fontProps );
        result = dimensions[ 0 ];
      }
      return result;
    },

    _renderAsUnfocused : function( config ) {
      return !config.focused && !this.hasState( "dnd_selected" );
    },

    _renderAsSelected : function( config, selected ) {
      var result =    ( selected || this.hasState( "dnd_selected" ) )
                   && ( !config.hideSelection || config.focused )
                   && !config.alwaysHideSelection;
      return result;
    },

    _getFontProps : function( font ) {
      var result = {};
      var fontObject;
      if( font instanceof rwt.html.Font ) {
        fontObject = font;
      } else {
        fontObject = rwt.html.Font.fromString( font );
      }
      fontObject.renderStyle( result );
      fontObject.dispose();
      return result;
    },

    /////////////
    // DOM-Helper

    _setFont : function( element, font ) {
      if( font === "" || font === null ) {
        this._resetFont( element );
      } else {
        if( font instanceof rwt.html.Font ) {
          font.renderStyle( element.style );
        } else {
          element.style.font = font;
        }
      }
    },

    _resetFont : Variant.select( "qx.client", {
      "default" : function( element ) {
        element.style.font = "";
        element.style.fontFamily = "";
        element.style.fontSize = "";
        element.style.fontVariant = "";
        element.style.fontStyle = "";
        element.style.fontWeight = "";
      },
      "mshtml" : function( element ) {
        // Resetting style.font causes errors in IE with any of these syntaxes:
        // node.style.font = null | undefined | "inherit" | "";
        element.style.fontFamily = "";
        element.style.fontSize = "";
        element.style.fontVariant = "";
        element.style.fontStyle = "";
        element.style.fontWeight = "";
      }
    } ),

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
      element.style.backgroundImage = src ? "URL(" + src + ")" : "none";
      if( enabled !== null ) {
        var opacity = enabled ? 1 : 0.3;
        HtmlUtil.setOpacity( element, opacity );
      }
    },

    _getTextElement : function( cell, config ) {
      var result = this._cellLabels[ cell ];
      if( !result ) {
        result = this._createElement( 3 );
        result.style.verticalAlign = "middle";
        result.style.whiteSpace = "nowrap";
        if( rwt.client.Client.isNewMshtml() ) {
          result.style.backgroundColor = "rgba(0, 0, 0, 0)";
        }
        this._cellLabels[ cell ] = result;
      }
      return result;
    },

    _getCellImage : function( cell ) {
      var result = this._cellImages[ cell ];
      if( !result ) {
        result = this._createElement( 3 );
        result.style.backgroundRepeat = "no-repeat";
        result.style.backgroundPosition = "center";
        this._cellImages[ cell ] = result;
      }
      return result;
    },

    _getCellCheckImage : function( cell ) {
      var result = this._cellCheckImages[ cell ];
      if( !result ) {
        result = this._createElement( 3 );
        result.style.backgroundRepeat = "no-repeat";
        result.style.backgroundPosition = "center";
        this._cellCheckImages[ cell ] = result;
      }
      return result;
    },

    _getMiscImage : function() {
      var result = this._getMiscElement( 3 );
      result.innerHTML = "";
      result.style.backgroundColor = "";
      return result;
    },

    _getMiscBackground : function() {
      var result = this._getMiscElement( 2 );
      result.style.backgroundImage = "";
      result.innerHTML = "";
      return result;
    },

    _getBackgroundElement : function( cell ) {
      var result = this._cellBackgrounds[ cell ];
      if( !result ) {
        result = this._createElement( 1 );
        this._cellBackgrounds[ cell ] = result;
      }
      return result;
    },

    _getMiscElement : function( zIndex ) {
      var result;
      var node = this._getTargetNode();
      if( this._usedMiscNodes < this._miscNodes.length ) {
        result = this._miscNodes[ this._usedMiscNodes ];
        result.style.display = "";
        result.style.zIndex = zIndex;
      } else {
        result = this._createElement( zIndex );
        result.style.backgroundRepeat = "no-repeat";
        result.style.backgroundPosition = "center";
        this._miscNodes.push( result );
      }
      this._usedMiscNodes++;
      return result;
    },

    _createElement : function( zIndex ) {
      var result = document.createElement( "div" );
      result.style.position = "absolute";
      result.style.overflow = "hidden";
      result.style.zIndex = zIndex;
      this._getTargetNode().appendChild( result );
      return result;
    },

    _clearContent : function( config ) {
      for( var i = 0; i < this._cellBackgrounds.length; i++ ) {
        if( this._cellBackgrounds[ i ] ) {
          this._cellBackgrounds[ i ].style.backgroundColor = "transparent";
        }
      }
      for( var i = 0; i < this._cellCheckImages.length; i++ ) {
        if( this._cellCheckImages[ i ] ) {
          this._cellCheckImages[ i ].style.backgroundImage = "";
        }
      }
      for( var i = 0; i < this._cellImages.length; i++ ) {
        if( this._cellImages[ i ] ) {
          this._cellImages[ i ].style.backgroundImage = "";
        }
      }
      for( var i = 0; i < this._cellLabels.length; i++ ) {
        if( this._cellLabels[ i ] ) {
          this._renderElementContent( this._cellLabels[ i ], null, -1, config.markupEnabled );
        }
      }
      if( this._checkBoxElement ) {
        this._checkBoxElement.style.backgroundImage = "";
      }
      this._hideRemainingElements();
    },

    _renderAllBounds : function( config ) {
      var columns = this._getColumnCount( config );
      for( var i = 0; i < columns; i++ ) {
        // tree column bounds can not be rendered without item, is rendered always anyway
        if( !this._isTreeColumn( i, config ) ) {
          this._renderCellLabelBounds( null, i, config );
          this._renderCellImageBounds( null, i, config );
        }
        this._renderCellBackgroundBounds( null, i, config );
      }
    },

    _hideRemainingElements : function() {
      var node = this._getTargetNode();
      for( var i = this._usedMiscNodes; i < this._miscNodes.length; i++ ) {
        this._miscNodes[ i ].style.display = "none";
      }
    },

    _removeCells : function( from, to ) {
      for( var i = from; i < to; i++ ) {
        this._removeCell( i );
      }
    },

    _removeCell : function( cell ) {
      this._removeNode( this._cellBackgrounds, cell );
      this._removeNode( this._cellImages, cell );
      this._removeNode( this._cellCheckImages, cell );
      this._removeNode( this._cellLabels, cell );
    },

    _removeNode : function( arr, pos ) {
      var node = arr[ pos ];
      if( node ) {
        this._getTargetNode().removeChild( node );
        arr[ pos ] = null;
      }
    },

    _ieFixLayoutOnAppear : Variant.select( "qx.client", {
      "mshtml" : function() {
        // TODO [tb] : find a faster alternative, possibly delete hidden nodes on disappear or collect hidden elements
        this.base( arguments );
        var node = this._getTargetNode();
        for( var i = 0; i < node.childNodes.length; i++ ) {
          if( node.childNodes[ i ].style.display === "none" ) {
            node.childNodes[ i ].style.display = "";
            node.childNodes[ i ].style.display = "none";
          }
        }
      },
      "default" : rwt.util.Functions.returnTrue
    } ),

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

    _getCellCheckLeft : function( item, columnIndex, config ) {
      var result = config.itemCellCheckLeft[ columnIndex ];
      if( this._isTreeColumn( columnIndex, config ) ) {
        result = this._correctOffset( result, item, config );
      }
      return result;
    },

    _getCellCheckWidth : function( item, columnIndex, config ) {
      var result = config.itemCellCheckWidth[ columnIndex ];
      if( this._isTreeColumn( columnIndex, config ) ) {
        var offset = this._getCellCheckLeft( item, columnIndex, config );
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
      // NOTE [tb] : Shoud actually add the isTreeColumns own offset, assumes 0 now.
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

}());
