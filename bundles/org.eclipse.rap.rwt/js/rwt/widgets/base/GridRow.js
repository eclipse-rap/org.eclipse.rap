/*******************************************************************************
 * Copyright (c) 2010, 2015 Innoopract Informationssysteme GmbH and others.
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

(function( $ ) {

var cellRenderer = rwt.widgets.util.CellRendererRegistry.getInstance().getAll();
var FADED = 0.3;

rwt.qx.Class.define( "rwt.widgets.base.GridRow", {

  extend : rwt.qx.Target,

  construct : function() {
    this.base( arguments );
    this.$el = $( "<div>" ).css( {
      "overflow" : "hidden",
      "userSelect" : "none",
      "height" : 16,
      "position" : "relative",
      "borderWidth" : "0px",
      "borderStyle" : "solid"
    });
    this.$el.prop( "row", this );
    this._styleMap = {};
    this._appearance = null;
    this._overlayStyleMap = {};
    this._elementStyleCache = {};
    this._variant = null;
    this.$expandIcon = null;
    this.$checkBox = null;
    this.$overlay = null;
    this.$treeColumnParts = [];
    this._lastAttributes = {};
    this.$cellLabels = [];
    this.$cellImages = [];
    this.$cellCheckBoxes = [];
    this.$cellBackgrounds = [];
    this.$indentIcons = [];
    this._usedIdentIcons = 0;
    this._cellsRendered = 0;
    this._templateRenderer = null;
    this._mirror = false;
  },

  destruct : function() {
    this.$el.removeProp( "row" ).detach();
    this.$el = null;
    this.$expandIcon = null;
    this.$checkBox = null;
    this.$treeColumnParts = null;
    this.$cellLabels = null;
    this.$cellImages = null;
    this.$cellCheckBoxes = null;
    this.$cellBackgrounds = null;
    this.$indentIcons = null;
  },

  members : {

    _gridLines : { horizontal : null, vertical : null },

    renderItem : function( item, gridConfig, selected, hoverTarget, scrolling ) {
      var renderArgs = {
        item: item,
        gridConfig: gridConfig,
        selected: this._renderAsSelected( gridConfig, selected ),
        hoverTarget: hoverTarget,
        scrolling: scrolling
      };
      this._renderStates( renderArgs );
      this._renderItemBackground( renderArgs );
      this._renderItemForeground( renderArgs );
      this._renderItemFont( renderArgs );
      this._renderIndention( renderArgs );
      this._renderContent( renderArgs );
      this._renderHeight( renderArgs );
      this._renderOverlay( renderArgs );
      this._renderHtmlAttributes( renderArgs );
      this.dispatchSimpleEvent( "itemRendered", item );
    },

    identify : function( node ) {
      var result = [ "other" ];
      var match = function( candidate ) {
        return candidate != null && candidate.is( node );
      };
      if( match( this.$expandIcon ) ) {
        result = [ "expandIcon" ];
      } else if( match( this.$checkBox ) ) {
        result = [ "checkBox" ];
      } else if( this.$cellCheckBoxes.some( match ) ) {
        var cell = this.$cellCheckBoxes.filter( match )[ 0 ];
        result = [ "cellCheckBox", this.$cellCheckBoxes.indexOf( cell ) ];
      } else {
        while( !this.$el.is( node ) && result[ 0 ] === "other" ) { // Can be removed?
          if( this.$treeColumnParts.some( match ) ) {
            result = [ "treeColumn" ]; // TODO [tb] : now should be [ "label", 0 ] / [ "image", 0 ]
          } else if( this._templateRenderer ) {
            if( this._templateRenderer.isCellSelectable( node ) ) {
              result = [ "selectableCell", this._templateRenderer.getCellName( node ) ];
            }
          }
          node = node.parentNode;
        }
      }
      return result;
    },

    updateEvenState : function( index ) {
      this.setState( "even", index % 2 === 0 );
    },

    setAppearance : function( appearance ) {
      this._appearance = appearance;
    },

    getAppearance : function() {
      return this._appearance;
    },

    setWidth : function( width ) {
      this.$el.css( "width", width );
    },

    setHeight : function( height ) {
      this.$el.css( "height", height );
    },

    getTop : function() {
      return this.$el.get( 0 ).offsetTop;
    },

    getWidth : function() {
      // Do NOT use anything like offsetWidth/outerWidth/clientRectBounds for this, it would
      // force rendering and potentially impact performance!
      return parseInt( this.$el.css( "width" ) || "0" );
    },

    getHeight : function() {
      return parseInt( this.$el.css( "height" ) || "0" );
    },

    renderHeight : function( item, gridConfig ) {
      this._renderHeight( { item: item, gridConfig: gridConfig } );
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

    hasState : function( state ) {
      return this.__states && this.__states[ state ] ? true : false;
    },

    setGridLines : function( lines ) {
      this._gridLines = lines;
      this.$el.css( {
        "borderBottomColor" : lines.horizontal || "",
        "borderBottomWidth" : lines.horizontal ? "1px" : "0px"
      } );
      for( var cell = 0; cell < this.$cellBackgrounds.length; cell++ ) {
        this._renderVericalGridLine( cell );
      }
    },

    getGridLines : function() {
      return this._gridLines;
    },

    setMirror : function( mirror ) {
      this._mirror = mirror;
      for( var cell = 0; cell < this.$cellBackgrounds.length; cell++ ) {
        this._renderVericalGridLine( cell );
      }
    },

    getMirror : function() {
      return this._mirror;
    },

    ///////////////////////
    // First-level Renderer

    _renderStates : function( renderArgs ) {
      if( renderArgs.item ) {
        this.setState( "rowtemplate", renderArgs.gridConfig.rowTemplate != null );
        this.setState( "checked", renderArgs.item.isChecked() );
        this.setState( "grayed", renderArgs.item.isGrayed() );
        this.setState( "parent_unfocused", this._renderAsUnfocused( renderArgs.gridConfig ) );
        this.setState( "selected", renderArgs.gridConfig.fullSelection ? renderArgs.selected : false );
        this._renderVariantState( renderArgs.item.getVariant() );
        this._renderOverState( renderArgs.hoverTarget, renderArgs.gridConfig );
        this._styleMap = this._getStyleMap();
        this.setState( "selected", renderArgs.selected );
        if( renderArgs.gridConfig.fullSelection ) {
          this._overlayStyleMap = this._getOverlayStyleMap( renderArgs.selected );
        } else {
          this._overlayStyleMap = this._getTreeColumnStyleMap( renderArgs.selected );
        }
      }
    },

    _renderItemBackground : function( renderArgs ) {
      var color, image, gradient;
      if( renderArgs.item ) {
        if( renderArgs.item.getBackground() !== null && renderArgs.gridConfig.enabled !== false ) {
          color = renderArgs.item.getBackground();
        } else {
          color = this._styleMap.background;
          image = this._styleMap.backgroundImage;
          gradient = this._styleMap.backgroundGradient;
        }
      }
      // Note: "undefined" is a string stored in the themestore
      this.$el.css( {
        "backgroundColor" :  color !== "undefined" ? color : "",
        "backgroundImage" : image !== "undefined" ? image : "",
        "backgroundGradient" : gradient !== "undefined" ? gradient : ""
      } );
    },

    _renderItemForeground : function( renderArgs ) {
      // TODO [tb] : could be inherited
      this.$el.css( "color", this._getItemColor( renderArgs ) || "" );
    },

    _renderItemFont : function( renderArgs ) {
      // TODO [tb] : could be inherited
      if( this._elementStyleCache.font !== renderArgs.gridConfig.font ) {
        this._elementStyleCache.font = renderArgs.gridConfig.font;
        this._setFont( this.$el, renderArgs.gridConfig.font );
      }
      if( this._elementStyleCache.textDecoration !== this._styleMap.textDecoration ) {
        this._elementStyleCache.textDecoration = this._styleMap.textDecoration;
        var decoration = this._styleMap.textDecoration;
        this.$el.css( {
          "textDecoration" : ( decoration == null || decoration === "none" ) ? "" : decoration
        } );
      }
      if( this._elementStyleCache.textOverflow !== this._styleMap.textOverflow ) {
        this._elementStyleCache.textOverflow = this._styleMap.textOverflow;
        var overflow = this._styleMap.textOverflow;
        this.$el.css( "textOverflow", ( overflow == null || overflow === "clip" ) ? "" : overflow );
      }
      if( this._elementStyleCache.textShadow !== this._styleMap.textShadow ) {
        this._elementStyleCache.textShadow = this._styleMap.textShadow;
        this.$el.css( "textShadow", this._styleMap.textShadow || "" );
      }
    },

    // TODO: broken on first render
    _renderIndention : function( renderArgs ) {
      this._usedIdentIcons = 0;
      if( renderArgs.item && renderArgs.gridConfig.treeColumn !== -1 ) {
        this._renderExpandImage( renderArgs );
        this._renderLineImages( renderArgs );
      }
      for( var i = this._usedIdentIcons; i < this.$indentIcons.length; i++ ) {
        this.$indentIcons[ i ].css( "display", "none" );
      }
    },

    _renderContent : function( renderArgs ) {
      if( renderArgs.gridConfig.rowTemplate ) {
        this._renderTemplate( renderArgs );
      } else {
        if( renderArgs.gridConfig.hasCheckBoxes ) {
          this._renderCheckBox( renderArgs );
        }
        this._renderCells( renderArgs );
      }
    },

    _renderHeight : function( renderArgs ) {
      if( renderArgs.item ) {
        if( renderArgs.gridConfig.autoHeight ) {
          var computedHeight = this._computeAutoHeight( renderArgs );
          if( renderArgs.item.getDefaultHeight() >= computedHeight - 1 ) {
            computedHeight = null; // ignore rounding error for network optimization
          }
          renderArgs.item.setHeight( computedHeight, true );
        }
        var itemHeight = renderArgs.item.getOwnHeight();
        if( itemHeight !== this.getHeight() ) {
          this.$el.css( "height", renderArgs.item.getOwnHeight() );
        }
      }
    },

    _renderOverlay : function( renderArgs ) {
      if( renderArgs.item && this._hasOverlayBackground( renderArgs.gridConfig ) ) {
        var gradient = this._overlayStyleMap.backgroundGradient;
        if( gradient ) {
          this._getOverlayElement().css( "backgroundGradient", gradient || "" );
        } else {
          this._getOverlayElement().css( {
            "backgroundColor" : this._overlayStyleMap.background,
            "opacity" : this._overlayStyleMap.backgroundAlpha
        } );
        }
        this._renderOverlayBounds( renderArgs );
      } else if( this.$overlay ){
        this.$overlay.css( "display", "none" );
      }
    },

    _renderHtmlAttributes : function( renderArgs ) {
      this.$el.removeAttr( Object.keys( this._lastAttributes ).join( " " ) );
      var attributes = renderArgs.item ? renderArgs.item.getHtmlAttributes() : {};
      if( attributes[ "id" ] && renderArgs.gridConfig.containerNumber === 1 ) {
        attributes = rwt.util.Objects.copy( attributes );
        attributes[ "id" ] += "-1";
      }
      this.$el.attr( attributes );
      this._lastAttributes = attributes;
    },

    ///////////////////
    // Content Renderer

    _renderTemplate : function( renderArgs ) {
      var hasIndention =    renderArgs.item
                         && typeof renderArgs.gridConfig.treeColumn === "number"
                         && renderArgs.gridConfig.treeColumn > -1;
      var xOffset = hasIndention ? this._indent( renderArgs, 0 ) : 0;
      var renderer = this._getTemplateRenderer( renderArgs.gridConfig );
      renderer.targetBounds = [ xOffset, 0, this.getWidth() - xOffset, this.getHeight() ];
      renderer.markupEnabled = renderArgs.gridConfig.markupEnabled;
      renderer.targetIsEnabled = renderArgs.gridConfig.enabled;
      renderer.targetIsSeeable = renderArgs.gridConfig.seeable;
      renderer.renderItem( renderArgs.item );
    },

    _getTemplateRenderer : function( gridConfig ) {
      if( this._templateRenderer == null ) {
        this._templateRenderer = new rwt.widgets.util.TemplateRenderer(
          gridConfig.rowTemplate,
          this.$el.get( 0 ),
          100
        );
      }
      return this._templateRenderer;
    },

    _renderCheckBox : function( renderArgs ) {
      var image = this._getCheckBoxImage( renderArgs );
      this._getCheckBoxElement().css( {
        "display" : image === null ? "none" : "",
        "opacity" : renderArgs.gridConfig.enabled ? 1 : FADED,
        "backgroundImage" : image || ""
      } );
      var isTree = renderArgs.gridConfig.treeColumn !== -1;
      if( renderArgs.item && ( isTree || !renderArgs.scrolling ) ) {
        this._renderCheckBoxBounds( renderArgs );
      }
    },

    _renderCells : function( renderArgs ) {
      var columns = this._getColumnCount( renderArgs.gridConfig );
      if( this._cellsRendered > columns ) {
        this._removeCells( columns, this._cellsRendered );
      }
      for( var cell = 0; cell < columns; cell++ ) {
        if( this._getItemWidth( renderArgs, cell ) > 0 ) {
          this._renderCellBackground( renderArgs, cell );
          this._renderCellCheckBox( renderArgs, cell );
          this._renderCellImage( renderArgs, cell );
          this._renderCellLabel( renderArgs, cell );
          if( !renderArgs.gridConfig.fullSelection && this._isTreeColumn( renderArgs, cell ) ) {
            this.$treeColumnParts = [ this.$cellImages[ cell ], this.$cellLabels[ cell ] ];
          }
        } else {
          this._removeCell( cell );
        }
      }
      this._cellsRendered = columns;
    },

    _renderCellBackground : function( renderArgs, cell ) {
      var background = this._getCellBackgroundColor( renderArgs, cell );
      var renderBounds = false;
      if( background !== "undefined" && background != this._styleMap.backgroundColor ) {
        renderBounds = !renderArgs.scrolling || !this.$cellBackgrounds[ cell ];
        this._getCellBackgroundElement( cell ).css( "backgroundColor", background );
      } else if( this.$cellBackgrounds[ cell ] || this._gridLines.vertical ) {
        this._getCellBackgroundElement( cell ).css( "backgroundColor", "" );
        renderBounds = !renderArgs.scrolling;
      }
      if( renderBounds ) {
        this._renderCellBackgroundBounds( renderArgs, cell );
      }
    },

    _renderCellCheckBox : function( renderArgs, cell ) {
      if( renderArgs.gridConfig.itemCellCheck[ cell ] ) {
        var image = this._getCellCheckBoxImage( renderArgs, cell );
        var isTreeColumn = this._isTreeColumn( renderArgs, cell );
        var renderBounds = isTreeColumn || !renderArgs.scrolling || !this.$cellCheckBoxes[ cell ];
        this._getCellCheckBoxElement( cell ).css( {
          "display" : image === null ? "none" : "",
          "opacity" : renderArgs.gridConfig.enabled ? 1 : FADED,
          "backgroundImage" : image || ""
        } );
        if( renderBounds ) {
          this._renderCellCheckBounds( renderArgs, cell );
        }
      }
    },

    _renderCellImage : function( renderArgs, cell ) {
      var source = renderArgs.item ? renderArgs.item.getImage( cell ) : null;
      var isTreeColumn = this._isTreeColumn( renderArgs, cell );
      var renderBounds = isTreeColumn || !renderArgs.scrolling;
      if( source !== null ) {
        renderBounds = renderBounds || !this.$cellImages[ cell ];
        this._getCellImageElement( cell ).css( {
          "opacity" : renderArgs.gridConfig.enabled ? 1 : FADED,
          "backgroundImage" : source[ 0 ] || ""
        } );
      } else if( this.$cellImages[ cell ] ) {
        this._getCellImageElement( cell ).css( { "backgroundImage" : "" } );
      }
      if( renderBounds ) {
        this._renderCellImageBounds( renderArgs, cell );
      }
    },

    _renderCellLabel : function( renderArgs, cell ) {
      var element = null;
      var isTreeColumn = this._isTreeColumn( renderArgs, cell );
      var renderBounds = isTreeColumn || !renderArgs.scrolling;
      if( renderArgs.item && renderArgs.item.hasText( cell ) ) {
        renderBounds = renderBounds || !this.$cellLabels[ cell ];
        element = this._getCellLabelElement( cell );
        this._renderCellLabelContent( renderArgs, cell, element );
        if( renderBounds ) {
          var treeColumnAlignment = this._mirror ? "right" : "left";
          var columnAlignment = this._getAlignment( cell, renderArgs.gridConfig );
          element.css( "textAlign", isTreeColumn ? treeColumnAlignment : columnAlignment );
        }
        this._renderCellLabelFont( renderArgs, cell, element );
      } else if( this.$cellLabels[ cell ] ) {
        element = this._getCellLabelElement( cell );
        this._renderCellLabelContent( renderArgs, -1, element );
      }
      if( renderBounds ) {
        this._renderCellLabelBounds( renderArgs, cell );
      }
    },

    _renderCellLabelContent : function( renderArgs, cell, element ) {
      var options = {
        "markupEnabled" : renderArgs.gridConfig.markupEnabled,
        "seeable" : renderArgs.gridConfig.seeable,
        "removeNewLines" : true
      };
      var item = renderArgs.item ? renderArgs.item.getText( cell ) : null;
      cellRenderer.text.renderContent( element.get( 0 ), item, null, options );
    },

    _renderCellLabelFont : function( renderArgs, cell, element ) {
      element.css( {
        "color" : this._getCellColor( renderArgs, cell ) || "",
        "whiteSpace" : renderArgs.gridConfig.wordWrap[ cell ] ? "" : "nowrap"
      } );
      this._setFont( element, renderArgs.item.getCellFont( cell ) );
    },

    /////////////////
    // Content Getter

    _getCheckBoxImage : function( renderArgs ) {
      if( !renderArgs.item ) {
        return null;
      }
      this.setState( "over", renderArgs.hoverTarget && renderArgs.hoverTarget[ 0 ] === "checkBox" );
      this.setState( "disabled", !renderArgs.item.isCellCheckable( 0 ) );
      var image = this._getImageFromAppearance( "check-box", this.__states );
      this.setState( "over", renderArgs.hoverTarget !== null );
      this.setState( "disabled", false );
      return image;
    },

    _getCellCheckBoxImage : function( renderArgs, cell ) {
      if( !renderArgs.item ) {
        return null;
      }
      this.setState( "checked", renderArgs.item.isCellChecked( cell ) );
      this.setState( "disabled", !renderArgs.item.isCellCheckable( cell ) );
      this.setState( "grayed", renderArgs.item.isCellGrayed( cell ) );
      this.setState( "over",    renderArgs.hoverTarget
                             && renderArgs.hoverTarget[ 0 ] === "cellCheckBox"
                             && renderArgs.hoverTarget[ 1 ] === cell );
      var image = this._getImageFromAppearance( "check-box", this.__states );
      this.setState( "disabled", false );
      return image;
    },

    _getCellBackgroundColor : function( renderArgs, cell ) {
      if( !renderArgs.item || renderArgs.gridConfig.enabled === false ) {
        return "undefined";
      }
      return renderArgs.item.getCellBackground( cell );
    },

    _getItemColor : function( renderArgs ) {
      var result = "undefined";
      if( renderArgs.gridConfig.fullSelection ) {
        result = this._overlayStyleMap.foreground;
      }
      if( result === "undefined" ) {
        result = this._styleMap.foreground;
      }
      if( result === "undefined" ) {
        result = renderArgs.gridConfig.textColor;
      }
      if( result === "undefined" ) {
        result = "inherit";
      }
      return result;
    },

    _getCellColor : function( renderArgs, cell ) {
      var treeColumn = this._isTreeColumn( renderArgs, cell );
      var allowOverlay = renderArgs.gridConfig.fullSelection || treeColumn;
      var result = allowOverlay ? this._overlayStyleMap.foreground : "undefined";
      if(    result === "undefined"
          && renderArgs.gridConfig.enabled !== false
          && renderArgs.item.getCellForeground( cell )
      ) {
        result = renderArgs.item.getCellForeground( cell );
      }
      if( result === "undefined" && treeColumn && !renderArgs.gridConfig.fullSelection ) {
        // If there is no overlay the tree column foreground may still have a different color
        // due to selection. In this case _overlayStyleMap has the tree column foreground color.
        result = this._overlayStyleMap.rowForeground;
      }
       if( result === "undefined" ) {
         result = "inherit";
      }
      return result;
    },

    //////////////////
    // Layout Renderer

    _renderCheckBoxBounds : function( renderArgs ) {
      var left = this._getCheckBoxLeft( renderArgs );
      var width = this._getCheckBoxWidth( renderArgs );
      this.$checkBox.css( {
        "left" : this._mirror ? "" : left,
        "right" : this._mirror ? left : "",
        "top" : 0,
        "width" : width,
        "height" : "100%"
      } );
    },

    _renderCellBackgroundBounds : function( renderArgs, cell ) {
      var element = this.$cellBackgrounds[ cell ];
      if( element ) {
        var left = this._getItemLeft( renderArgs, cell );
        var width = this._getItemWidth( renderArgs, cell );
        element.css( {
          "left" : this._mirror ? "" : left,
          "right" : this._mirror ? left : "",
          "top" : 0,
          "width" : width,
          "height" : "100%"
        } );
      }
    },

    _renderCellCheckBounds : function( renderArgs, cell ) {
      var element = this.$cellCheckBoxes[ cell ];
      if( element ) {
        var left = this._getCellCheckLeft( renderArgs, cell );
        var width = this._getCellCheckWidth( renderArgs, cell );
        element.css( {
          "left" : this._mirror ? "" : left,
          "right" : this._mirror ? left : "",
          "top" : 0,
          "width" : width,
          "height" : "100%"
        } );
      }
    },

    _renderCellImageBounds : function( renderArgs, cell ) {
      var element = this.$cellImages[ cell ];
      if( element ) {
        var left = this._getItemImageLeft( renderArgs, cell );
        var width = this._getItemImageWidth( renderArgs, cell );
        element.css( {
          "left" : this._mirror ? "" : left,
          "right" : this._mirror ? left : "",
          "top" : 0,
          "width" : width,
          "height" : "100%"
        } );
      }
    },

    _renderCellLabelBounds : function( renderArgs, cell ) {
      var element = this.$cellLabels[ cell ];
      if( element ) {
        var left = this._getItemTextLeft( renderArgs, cell );
        var width = this._getItemTextWidth( renderArgs, cell );
        var top = this._getCellPadding( renderArgs )[ 0 ];
        // TODO : for vertical center rendering line-height should also be set,
        //        but not otherwise. Also not sure about bottom alignment.
        element.css( {
          "left" : this._mirror ? "" : left,
          "right" : this._mirror ? left : "",
          "top" : top,
          "width" : width,
          "height" : "auto"
        } );
      }
    },

    _renderOverlayBounds : function( renderArgs ) { // TODO: broken on first render
      if( !renderArgs.gridConfig.fullSelection ) {
        var cell = renderArgs.gridConfig.treeColumn;
        var padding = renderArgs.gridConfig.selectionPadding;
        var left = this._getItemTextLeft( renderArgs, cell );
        left -= padding[ 0 ];
        var width = this._getItemTextWidth( renderArgs, cell );
        width += width > 0 ? padding[ 0 ] : 0;
        var visualWidth  = this._getVisualTextWidth( renderArgs, cell );
        visualWidth  += padding[ 0 ] + padding[ 1 ];
        width = Math.min( width, visualWidth );
        this._getOverlayElement().css( {
          "left" : this._mirror ? "" : left,
          "right" : this._mirror ? left : "",
          "width" : width
        } );
      }
    },

    ////////////////
    // Layout Getter

    _computeAutoHeight : function( renderArgs ) {
      var maxHeight = 0;
      for( var i = 0; i < this.$cellLabels.length; i++ ) {
        if( this.$cellLabels[ i ] ) {
          maxHeight = Math.max( maxHeight, Math.ceil( this.$cellLabels[ i ].outerHeight() ) );
        }
      }
      var padding = this._getCellPadding( renderArgs );
      return maxHeight + padding[ 0 ] + padding[ 2 ];
    },

    _getCheckBoxLeft : function( renderArgs ) {
      return this._indent( renderArgs, renderArgs.gridConfig.checkBoxLeft );
    },

    _getCheckBoxWidth : function( renderArgs ) {
      var result = renderArgs.gridConfig.checkBoxWidth;
      var offset = this._getCheckBoxLeft( renderArgs );
      return this._limitWidth( renderArgs, 0, offset, result );
    },

    _getItemLeft : function( renderArgs, cell ) {
      return renderArgs.gridConfig.itemLeft[ cell ];
    },

    _getItemWidth : function( renderArgs, cell ) {
      return renderArgs.gridConfig.itemWidth[ cell ];
    },

    _getItemImageLeft : function( renderArgs, cell ) {
      var result = renderArgs.gridConfig.itemImageLeft[ cell ];
      if( this._isTreeColumn( renderArgs, cell ) ) {
        result = this._indent( renderArgs, result );
      }
      return result;
    },

    _getItemImageWidth : function( renderArgs, cell ) {
      var result = renderArgs.gridConfig.itemImageWidth[ cell ];
      if( this._isTreeColumn( renderArgs, cell ) ) {
        var offset = this._getItemImageLeft( renderArgs, cell );
        result = this._limitWidth( renderArgs, cell, offset, result );
      }
      return result;
    },

    _getCellCheckLeft : function( renderArgs, cell ) {
      var result = renderArgs.gridConfig.itemCellCheckLeft[ cell ];
      if( this._isTreeColumn( renderArgs, cell ) ) {
        result = this._indent( renderArgs, result );
      }
      return result;
    },

    _getCellCheckWidth : function( renderArgs, cell ) {
      var result = renderArgs.gridConfig.itemCellCheckWidth[ cell ];
      if( this._isTreeColumn( renderArgs, cell ) ) {
        var offset = this._getCellCheckLeft( renderArgs, cell );
        result = this._limitWidth( renderArgs, cell, offset, result );
      }
      return result;
    },

    _getItemTextLeft : function( renderArgs, cell ) {
      var result = renderArgs.gridConfig.itemTextLeft[ cell ];
      if( this._isTreeColumn( renderArgs, cell ) ) {
        result = this._indent( renderArgs, result );
      }
      return result;
    },

    _getItemTextWidth : function( renderArgs, cell ) {
      var result = renderArgs.gridConfig.itemTextWidth[ cell ];
      if( this._isTreeColumn( renderArgs, cell ) ) {
        var offset = this._getItemTextLeft( renderArgs, cell );
        result = this._limitWidth( renderArgs, cell, offset, result );
      }
      return result;
    },

    _getCellPadding : function( renderArgs ) {
      var manager = rwt.theme.AppearanceManager.getInstance();
      return manager.styleFrom( renderArgs.gridConfig.baseAppearance + "-cell", {} ).padding;
    },

    _limitWidth : function( renderArgs, cell, offset, width ) {
      var result = width;
      var columnEnd =   renderArgs.gridConfig.itemLeft[ cell ]
                      + renderArgs.gridConfig.itemWidth[ cell ];
      var elementEnd = offset + result;
      if( elementEnd > columnEnd ) {
        result = Math.max( 0, columnEnd - offset );
      }
      return result;
    },

    _indent : function( renderArgs, offset ) {
      var result = offset;
      if( renderArgs.item ) {
        result += this._getIndentionOffset( renderArgs.item.getLevel() + 1, renderArgs.gridConfig );
      }
      return result;
    },

    _getVisualTextWidth : function( renderArgs, cell ) {
      var calc = rwt.widgets.util.FontSizeCalculation;
      var result = 0;
      if( this.$cellLabels[ cell ] ) {
        var font = this._getCellFont( renderArgs, cell );
        var fontProps = this._getFontProps( font );
        var text = this.$cellLabels[ cell ].html();
        var dimensions = calc.computeTextDimensions( text, fontProps );
        result = dimensions[ 0 ];
      }
      return result;
    },

    _getCellFont : function( renderArgs, cell ) {
      var result = renderArgs.item.getCellFont( cell );
      if( result === null || result === "" ) {
        result = renderArgs.gridConfig.font;
      }
      return result;
    },

    _getFontProps : function( font ) {
      var result = {};
      if( font instanceof rwt.html.Font ) {
        font.renderStyle( result );
      } else {
        var fontObject = rwt.html.Font.fromString( font );
        fontObject.renderStyle( result );
        fontObject.dispose();
      }
      return result;
    },

    /////////////////////
    // Indention Renderer

    _renderExpandImage : function( renderArgs ) {
      var src = this._getExpandSymbol( renderArgs );
      if( src != null ) {
        this.$expandIcon = this._addIndentSymbol( renderArgs.item.getLevel(), renderArgs.gridConfig, src );
      } else {
        this.$expandIcon = null;
      }
    },

    _renderLineImages : function( renderArgs ) {
      var src = this._getLineSymbol( renderArgs );
      if( src != null ) {
        var parent = renderArgs.item.getParent();
        while( !parent.isRootItem() ) {
          if( parent.hasNextSibling() ) {
            this._addIndentSymbol( parent.getLevel(), renderArgs.gridConfig, src );
          }
          parent = parent.getParent();
        }
      }
    },

    _getExpandSymbol : function( renderArgs ) {
      var states = this._getParentStates( renderArgs.gridConfig );
      if( renderArgs.item.getLevel() === 0 && !renderArgs.item.hasPreviousSibling() ) {
        states.first = true;
      }
      if( !renderArgs.item.hasNextSibling() ) {
        states.last = true;
      }
      if( renderArgs.item.hasChildren() ) {
        if( renderArgs.item.isExpanded() ) {
          states.expanded = true;
        } else {
          states.collapsed = true;
        }
      }
      if( renderArgs.hoverTarget && renderArgs.hoverTarget[ 0 ] === "expandIcon" ) {
        states.over = true;
      }
      if( this._mirror ) {
        states.rwt_RIGHT_TO_LEFT = true;
      }
      return this._getImageFromAppearance( "indent", states );
    },

    _getLineSymbol : function( renderArgs ) {
      var states = this._getParentStates( renderArgs.gridConfig );
      states.line = true;
      if( this._mirror ) {
        states.rwt_RIGHT_TO_LEFT = true;
      }
      return this._getImageFromAppearance( "indent", states );
    },

    _getParentStates : function( gridConfig ) {
      var result = {};
      if( gridConfig.variant ) {
        result[ gridConfig.variant ] = true;
      }
      return result;
    },

    _addIndentSymbol : function( level, gridConfig, source ) {
      var result = null;
      var nextLevelOffset = ( level + 1 ) * gridConfig.indentionWidth;
      var cellWidth = gridConfig.itemWidth[ gridConfig.treeColumn ];
      if( nextLevelOffset <= cellWidth || gridConfig.rowTemplate ) {
        var offset = level * gridConfig.indentionWidth;
        var width = nextLevelOffset - offset;
        var element = this._getIndentImageElement().css( {
          "opacity" : gridConfig.enabled ? 1 : FADED,
          "backgroundImage" : source,
          "left" : this._mirror ? "" : offset,
          "right" : this._mirror ? offset : "",
          "top" : 0,
          "width" : width,
          "height" : "100%"
        } );
        result = element;
      }
      return result;
    },

    ///////////////////
    // Element Handling

    _getCheckBoxElement : function() {
      if( this.$checkBox === null ) {
        this.$checkBox = this._createElement( 3 ).css( {
          "backgroundRepeat" : "no-repeat",
          "backgroundPosition" : "center"
        } );
      }
      return this.$checkBox;
    },

    _getCellLabelElement : function( cell ) {
      var result = this.$cellLabels[ cell ];
      if( !result ) {
        result = this._createElement( 3 );
        result.css( {
          "textDecoration" : "inherit",
          "textOverflow": "inherit",
          "backgroundColor" : ""
        } );
        this.$cellLabels[ cell ] = result;
      }
      return result;
    },

    _getCellImageElement : function( cell ) {
      var result = this.$cellImages[ cell ];
      if( !result ) {
        result = this._createElement( 3 );
        result.css( { "backgroundRepeat" : "no-repeat", "backgroundPosition" : "center" } );
        this.$cellImages[ cell ] = result;
      }
      return result;
    },

    _getCellCheckBoxElement : function( cell ) {
      var result = this.$cellCheckBoxes[ cell ];
      if( !result ) {
        result = this._createElement( 3 );
        result.css( { "backgroundRepeat" : "no-repeat", "backgroundPosition" : "center" } );
        this.$cellCheckBoxes[ cell ] = result;
      }
      return result;
    },

    _getOverlayElement : function() {
      if( this.$overlay === null ) {
        this.$overlay = this._createElement( 2 );
        this.$overlay.css( { "width" : "100%", "height" : "100%" } );
      }
      return this.$overlay.css( { "display" : "" } );
    },

    _getCellBackgroundElement : function( cell ) {
      var result = this.$cellBackgrounds[ cell ];
      if( !result ) {
        result = this._createElement( 1 ).css( {
          "borderWidth" : "0px",
          "borderStyle" : "solid"
        } );
        this.$cellBackgrounds[ cell ] = result;
        this._renderVericalGridLine( cell );
      }
      return result;
    },

    _renderVericalGridLine : function( cell ) {
      this.$cellBackgrounds[ cell ].css( {
        "borderRightWidth" : !this._mirror && this._gridLines.vertical ? "1px" : "0px",
        "borderLeftWidth" : this._mirror && this._gridLines.vertical ? "1px" : "0px",
        "borderColor" : this._gridLines.vertical || ""
      } );
    },

    _getIndentImageElement : function() {
      var result;
      if( this._usedIdentIcons < this.$indentIcons.length ) {
        result = this.$indentIcons[ this._usedIdentIcons ];
      } else {
        result = this._createElement( 3 ).css( {
          "backgroundRepeat" : "no-repeat",
          "backgroundPosition" : "center",
          "zIndex" : 3
        } );
        this.$indentIcons.push( result );
      }
      this._usedIdentIcons++;
      return result.html( "" ).css( { "backgroundColor" : "", "display" : "" } );
    },

    _createElement : function( zIndex ) {
      return $( "<div>" ).css( {
        "position" : "absolute",
        "overflow" : "hidden",
        "zIndex" : zIndex
      } ).appendTo( this.$el );
    },

    _removeCells : function( from, to ) {
      for( var cell = from; cell < to; cell++ ) {
        this._removeCell( cell );
      }
    },

    _removeCell : function( cell ) {
      this._removeNode( this.$cellBackgrounds, cell );
      this._removeNode( this.$cellImages, cell );
      this._removeNode( this.$cellCheckBoxes, cell );
      this._removeNode( this.$cellLabels, cell );
    },

    _removeNode : function( arr, pos ) {
      var node = arr[ pos ];
      if( node ) {
        node.detach();
        arr[ pos ] = null;
      }
    },

    //////////////////
    // Theming Helper

    _getStyleMap : function() {
      var manager = rwt.theme.AppearanceManager.getInstance();
      return manager.styleFrom( this._appearance, this.__states );
    },

    _getOverlayStyleMap : function() {
      var manager = rwt.theme.AppearanceManager.getInstance();
      return manager.styleFrom( this._appearance + "-overlay", this.__states );
    },

    _getTreeColumnStyleMap : function( selected ) {
      var manager = rwt.theme.AppearanceManager.getInstance();
      var overlayMap = manager.styleFrom( this._appearance + "-overlay", this.__states );
      if( selected ) {
        var rowMap = manager.styleFrom( this._appearance, this.__states );
        overlayMap.rowForeground = rowMap.foreground;
      } else {
        overlayMap.rowForeground = "undefined";
      }
      return overlayMap;
    },

    _renderVariantState : function( variant ) {
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

    _renderOverState : function( hoverTarget, gridConfig ) {
      var fullOverState = hoverTarget !== null && gridConfig.fullSelection;
      var singleOverState = hoverTarget != null && hoverTarget[ 0 ] === "treeColumn";
      this.setState( "over", fullOverState || singleOverState );
    },

    _renderAsUnfocused : function( gridConfig ) {
      return !gridConfig.focused && !this.hasState( "dnd_selected" );
    },

    _renderAsSelected : function( gridConfig, selected ) {
      return    ( selected || this.hasState( "dnd_selected" ) )
             && ( !gridConfig.hideSelection || gridConfig.focused )
             && !gridConfig.alwaysHideSelection;
    },

    _hasOverlayBackground : function( gridConfig ) {
      if( !gridConfig.fullSelection && gridConfig.rowTemplate ) {
        return false;
      }
      return    this._overlayStyleMap.background !== "undefined"
             || this._overlayStyleMap.backgroundImage !== null
             || this._overlayStyleMap.backgroundGradient !== null;
    },

    _getImageFromAppearance : function( image, states ) {
      var appearance = this._appearance + "-" + image;
      var manager = rwt.theme.AppearanceManager.getInstance();
      var styleMap = manager.styleFrom( appearance, states );
      var valid = styleMap && styleMap.backgroundImage;
      return valid ? styleMap.backgroundImage : null;
    },

    /////////////
    // DOM Helper

    // TODO: integrate this in RWTQuery
    _setFont : function( element, font ) {
      if( font === "" || font === null ) {
        this._resetFont( element );
      } else {
        if( font instanceof rwt.html.Font ) {
          font.renderStyle( element.get( 0 ).style );
        } else {
          element.css( "font", font );
        }
      }
    },

    _resetFont : function( element ) {
      element.css( {
        "font" : "",
        "fontFamily" : "",
        "fontSize" : "",
        "fontVariant" : "",
        "fontStyle" : "",
        "fontWeight" : ""
      } );
    },

    ///////////////
    // Column Info

    _getAlignment : function( column, gridConfig ) {
      var alignment = gridConfig.alignment[ column ] ? gridConfig.alignment[ column ] : "left";
      if( this._mirror ) {
        if( alignment === "left" ) {
          return "right";
        } else if( alignment === "right" ) {
          return "left";
        }
      }
      return alignment;
    },

    _getIndentionOffset : function( level, gridConfig ) {
      // NOTE [tb] : Should actually add the isTreeColumns own offset, assumes 0 now.
      return gridConfig.indentionWidth * level;
    },

    _getColumnCount : function( gridConfig ) {
      return Math.max( 1, gridConfig.columnCount );
    },

    _isTreeColumn : function( renderArgs, cell ) {
      return cell === renderArgs.gridConfig.treeColumn;
    }

  }

} );

}( rwt.util._RWTQuery ));
