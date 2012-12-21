/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.base.MultiCellWidget",  {

  extend : rwt.widgets.base.Terminator,

  /**
   * param cells: an array of cell types to define widget structure.
   *              Valid types are "image" and "label".
   *              Examples:
   *                [ "image" ]
   *                [ "image", "image", "label", "image" ]
   */
  construct : function( cells ) {
    this.base( arguments );
    // cellData for a single cell is:
    // [ type, content, width, height, computedWidth, computedHeight, visible ]
    this.__cellData = null;
    this.__cellNodes = null;
    this.__cellCount = null;
    this.__computedTotalSpacing = null;
    this.__styleRegExp = /([a-z])([A-Z])/g;
    this.__createCellData( cells );
    this.__paddingCache = [ 0, 0, 0, 0 ];
    this.__fontCache = {};
    this.__colorCache = "";
    this._flexibleCell = -1;
    this.initWidth();
    this.initHeight();
    this.addToQueue( "createContent" );
    this.setOverflow( "hidden" );
    this.initSelectable();
    this.initCursor();
    this.initTextColor();
    this.initHorizontalChildrenAlign();
  },

  destruct : function() {
    this._disposeObjectDeep( "__cellData", 0 );
    this._disposeObjectDeep( "__cellNodes", 0 );
    this._disposeObjectDeep( "__paddingCache", 0 );
    this._disposeObjectDeep( "_fontCache", 0 );
  },

  properties : {

    spacing : {
      check : "Integer",
      init : 4,
      themeable : true,
      apply : "_applySpacing",
      event : "changeSpacing"
    },

    horizontalChildrenAlign : {
      check : [ "left", "center", "right" ],
      init : "center",
      themeable : true,
      apply : "_applyHorizontalChildrenAlign"
    },

    verticalChildrenAlign : {
      check : [ "top", "middle", "bottom" ],
      init : "middle",
      themeable : true,
      apply : "_applyVerticalChildrenAlign"
    },

    /////////////////////////////////
    // refined properties from Widget

    selectable : {
      refine : true,
      init : false
    },

    textColor : {
      refine : true,
      init : "#000000"
    },

    cursor : {
      refine : true,
      init : "default"
    },

    allowStretchX : {
      refine : true,
      init : false
    },

    allowStretchY : {
      refine : true,
      init : false
    },

    appearance : {
      refine : true,
      init : "atom"
    },

    width : {
      refine : true,
      init : "auto"
    },

    height : {
      refine : true,
      init : "auto"
    }

  },

  members : {

    // TODO [tb] : clean up api (private/public, order)

    ///////////////////////
    // LAYOUT : public api

    /**
     * This is either the URL (image) or the text (label)
     */
    setCellContent : function( cell, value ) {
      this.__updateComputedCellDimension( cell );
      if( this._cellHasContent( cell ) != ( value != null ) ) {
        this._invalidateTotalSpacing();
        this.addToQueue( "createContent" );
      } else {
        this.addToQueue( "updateContent" );
      }
      this.__cellData[ cell ][ 1 ] = value;
    },

    /**
     * The dimensions for the cell. Is mandatory for images (or 0x0 will
     * be assumed), optional for labels. Set a dimension to "null" to use the
     * computed value.
     */
    setCellDimension : function( cell, width, height ) {
      this.setCellWidth( cell, width );
      this.setCellHeight( cell, height );
    },

    /**
     * Setting visibility for a cell to false causes the element to have display:none,
     * but still to be created and layouted.
     */
    setCellVisible : function( cell, value ) {
      this.__cellData[ cell ][ 6 ] = value;
      if( this.getCellNode( cell ) ) {
        this.getCellNode( cell ).style.display = value ? "" : "none";
      }
    },

    isCellVisible : function( cell ) {
      return this.__cellData[ cell ][ 6 ];
    },

    getCellNode : function( cell ) {
      return this.__cellNodes[ cell ];
    },

    getCellContent : function( cell ) {
      return this.__cellData[ cell ][ 1 ];
    },

    setCellWidth : function( cell, width ) {
      if( this._getCellWidth( cell ) !== width ) {
        this._setCellWidth( cell, width );
        this._invalidateTotalSpacing();
        this._invalidatePreferredInnerWidth();
        this._scheduleLayoutX();
      }
    },

    setCellHeight : function( cell, height ) {
      this._setCellHeight( cell, height );
      this._invalidateTotalSpacing();
      this._invalidatePreferredInnerHeight();
      this._scheduleLayoutY();
    },

    setFlexibleCell : function( value ) {
      this._flexibleCell = value;
    },

    getFlexibleCell : function() {
      return this._flexibleCell;
    },

    // NOTE : Only needed by Tests
    getCellDimension : function( cell ) {
      var width = this.getCellWidth( cell );
      var height = this.getCellHeight( cell );
      return [ width, height ];
    },

    /**
     * Returns the user-set value for width if it exists, else the computed
     */
    getCellWidth : function( cell, ignoreFlexible ) {
      var cellEntry = this.__cellData[ cell ];
      var isFlexible = this._flexibleCell === cell && ignoreFlexible !== true;
      var width = ( cellEntry[ 2 ] != null ? cellEntry[ 2 ] : cellEntry[ 4 ] );
      if( width == null || ( isFlexible && cellEntry[ 3 ] === null ) ) {
        var computed = this.__computeCellDimension( cellEntry );
        width = computed[ 0 ];
      }
      if( isFlexible ) {
        width = this._limitCellWidth( cell, width );
      }
      return width;
    },

    /**
     * Returns the user-set value for height if it exists, else the computed
     */
    getCellHeight : function( cell, ignoreFlexible ) {
      var cellEntry = this.__cellData[ cell ];
      var isFlexible = this._flexibleCell === cell && ignoreFlexible !== true;
      var height = ( cellEntry[ 3 ] != null ? cellEntry[ 3 ] : cellEntry[ 5 ] );
      if( height == null || ( isFlexible && cellEntry[ 3 ] === null ) ) {
        var wrapWidth = isFlexible ? this.getCellWidth( cell ) : null;
        var computed = this.__computeCellDimension( cellEntry, wrapWidth );
        height = computed[ 1 ];
      }
      if( isFlexible ) {
        height = this._limitCellHeight( cell, height );
      }
      return height;
    },

    /*
    ---------------------------------------------------------------------------
      DOM/HTML
    ---------------------------------------------------------------------------
    */

    _applyElement : function( value, old ) {
      this.base( arguments, value, old );
      if( value ) {
        this._createSubelements();
        this._catchSubelements();
      }
    },

    _createSubelements : function() {
      var html = "";
      for( var i = 0; i < this.__cellCount; i++ ) {
        this.__setCellNode( i, null );
        if( this._cellHasContent( i ) ) {
          if( this._isTextCell( i ) ) {
            html += this._getLabelHtml( i );
          } else if( this._isImageCell( i ) ) {
            html += this._getImageHtml( i );
          }
        }
      }
      this._getTargetNode().innerHTML = html;
    },

    _catchSubelements : function() {
      var el = this._getTargetNode();
      var childNumber = 0;
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this._cellHasContent( i ) ) {
          this.__setCellNode( i, el.childNodes[ childNumber ] );
          childNumber++;
        }
      }
      if( !this.getEnabled() ) {
        this._applyEnabled( false );
      }
    },

    /*
    ---------------------------------------------------------------------------
      LAYOUT : _apply methods
    ---------------------------------------------------------------------------
    */

    _applySpacing : function( value, old ) {
      this._invalidateTotalSpacing();
      this._scheduleLayoutX();
    },

    _applyHorizontalChildrenAlign : function( value, old ) {
      this._scheduleLayoutX();
      this.setStyleProperty( "textAlign", value );
    },

    _applyVerticalChildrenAlign : function( value, old ) {
      this._scheduleLayoutY();
    },

    _applyPaddingTop : function( value, old ) {
      this.addToLayoutChanges( "paddingTop" );
      this.__paddingCache[ 0 ] = value;
      this._invalidateFrameHeight();
    },

    _applyPaddingRight : function( value, old ) {
      this.addToLayoutChanges( "paddingRight" );
      this.__paddingCache[ 1 ] = value;
      this._invalidateFrameWidth();
    },

    _applyPaddingBottom : function( value, old ) {
      this.addToLayoutChanges( "paddingBottom" );
      this.__paddingCache[ 2 ] = value;
      this._invalidateFrameHeight();
    },

    _applyPaddingLeft : function( value, old ) {
      this.addToLayoutChanges( "paddingLeft" );
      this.__paddingCache[ 3 ] = value;
      this._invalidateFrameWidth();
    },

    _applyEnabled : function( value, old ) {
      this.base( arguments, value, old );
      this._styleAllImagesEnabled();
    },

    /*
    ---------------------------------------------------------------------------
      LAYOUT : internals
    ---------------------------------------------------------------------------
    */

    _scheduleLayoutX : function() {
      this.addToQueue( "layoutX" );
      this._afterScheduleLayoutX();
    },

    _scheduleLayoutY : function() {
      this.addToQueue( "layoutY" );
      this._afterScheduleLayoutY();
    },

    _afterScheduleLayoutX : rwt.util.Functions.returnTrue,

    _afterScheduleLayoutY : rwt.util.Functions.returnTrue,

    _beforeComputeInnerWidth : rwt.util.Functions.returnTrue,

    _beforeComputeInnerHeight : rwt.util.Functions.returnTrue,

    _beforeRenderLayout : rwt.util.Functions.returnTrue,

    _afterRenderLayout : rwt.util.Functions.returnTrue,

    _cellHasContent : function( cell ) {
      var content = this.__cellData[ cell ][ 1 ];
      return content != null;
    },

    _isImageCell : function( cell ) {
      var type = this.__cellData[ cell ][ 0 ];
      return type == "image";
    },

    _isTextCell : function( cell ) {
      var type = this.__cellData[ cell ][ 0 ];
      return type == "label";
    },

    _setCellWidth : function( cell, width ) {
      this.__cellData[ cell ][ 2 ] = width;
    },

    _getCellWidth : function( cell ) {
      return this.__cellData[ cell ][ 2 ];
    },

    _setCellHeight : function( cell, height ) {
      this.__cellData[ cell ][ 3 ] = height;
    },

    __setCellNode : function( cell, node ) {
      this.__cellNodes[ cell ] = node;
      if( node !== null && !this.isCellVisible( cell ) ) {
        node.style.display = "none";
      }
    },

    __cellHasNode : function( cell ) {
      return this.__cellNodes[ cell ] != null;
    },

    __createCellData : function( cells ) {
      var data = [];
      var nodes = [];
      this.__cellCount = cells.length;
      for( var i = 0; i < this.__cellCount; i++ ) {
        nodes[ i ] = null;
        data[ i ] = [ cells[ i ], null, null, null, null, null, true ];
      }
      this.__cellNodes = nodes;
      this.__cellData = data;
    },

    __updateComputedCellDimension : function( cell ) {
      var cellEntry = this.__cellData[ cell ];
      cellEntry[ 4 ] = null; //delete computedWidth
      cellEntry[ 5 ] = null; //delete computedHeight
      if( cellEntry[ 2 ] == null ) { //uses computed width
        this._invalidatePreferredInnerWidth();
        this._scheduleLayoutX();
      }
      if( cellEntry[ 3 ] == null ) { //usses computedheight
        this._invalidatePreferredInnerHeight();
        this._scheduleLayoutY();
      }
    },

    __computeCellDimension : function( cellEntry, wrapWidth ) {
      var dimension;
      if( cellEntry[ 0 ] == "label" && cellEntry[ 1 ] != null ) {
        var calc = rwt.widgets.util.FontSizeCalculation;
        dimension = calc.computeTextDimensions( cellEntry[ 1 ], this.__fontCache, wrapWidth );
      } else {
        dimension = [ 0, 0 ];
      }
      cellEntry[ 4 ] = dimension[ 0 ];
      cellEntry[ 5 ] = dimension[ 1 ];
      return dimension;
    },

    _isWidthEssential : rwt.util.Functions.returnTrue,
    _isHeightEssential : rwt.util.Functions.returnTrue,

    _computePreferredInnerWidth : function() {
      return this._getContentWidth( "ignoreFlexible" );
    },

    _limitCellWidth : function( cell, preferredCellWidth ) {
      // NOTE: Will assume current width as valid, not to be used for widget size calculation
      var inner = this.getInnerWidth();
      var contentWidth = this._getContentWidth( "skipFlexible" );
      var maxCellWidth = Math.max( 0, inner - contentWidth );
      var result;
      if( preferredCellWidth > maxCellWidth ) {
        result = maxCellWidth;
      }  else {
        result = preferredCellWidth;
      }
      return result;
    },

    // TODO [tb] : refactor
    _getContentWidth : function( hint ) {
      this._beforeComputeInnerWidth();
      var result = 0;
      if( hint === "ignoreFlexible" ) {
        var space = this.getTotalSpacing();
        var content = 0;
        for( var i = 0; i < this.__cellCount; i++ ) {
          content += this.getCellWidth( i, true );
        }
        result = space + content;
      } else if( hint === "skipFlexible" ) {
        var spacing = this.getSpacing();
        for( var i = 0; i < this.__cellCount; i++ ) {
          if( i !== this._flexibleCell ) {
            var cellWidth = this.getCellWidth( i );
            result += cellWidth;
            if( cellWidth > 0 ) {
              result += spacing;
            }
          }
        }
      } else if( hint === "flexible" ) {
        var space = this.getTotalSpacing();
        var content = 0;
        for( var i = 0; i < this.__cellCount; i++ ) {
          content += this.getCellWidth( i );
        }
        result = space + content;
      } else {
        throw new Error( "unkown hint" );
      }
      return result;
    },

    _computePreferredInnerHeight : function() {
      this._beforeComputeInnerHeight();
      var maxHeight = 0;
      for( var i = 0; i < this.__cellCount; i++ ) {
        maxHeight = Math.max( maxHeight, this.getCellHeight( i, true ) );
      }
      return maxHeight;
    },

    _limitCellHeight : function( cell, preferredCellHeight ) {
      var inner = this.getInnerHeight();
      var result;
      if( preferredCellHeight > inner ) {
        result = inner;
      } else {
        result = preferredCellHeight;
      }
      return result;
    },

    getTotalSpacing : function() {
      if( this.__computedTotalSpacing == null ) {
        var spaces = Math.max( 0, ( this.getTotalVisibleCells() - 1 ) );
        this.__computedTotalSpacing = spaces * this.getSpacing();
      }
      return this.__computedTotalSpacing;
    },

    getTotalVisibleCells : function() {
      var ret = 0;
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this.cellIsDisplayable( i ) ) {
          ret++;
        }
      }
      return ret;
    },

    /**
     * a cell is "displayable" ( i.e. counts for the layout) if
     * it either has a content set, or at least one dimension
     */
    cellIsDisplayable : function( cell ) {
      return ( this.getCellWidth( cell, true ) > 0 );
    },

    _invalidateTotalSpacing : function() {
      this.__computedTotalSpacing = null;
      this._invalidatePreferredInnerWidth();
    },

    renderPadding : function( changes ) { },

    _layoutPost : function( changes ) {
      if( changes.createContent ){
        this._createSubelements();
        this._catchSubelements();
      }
      if( changes.updateContent && !changes.createContent ) {
        this._updateAllImages();
        this._updateAllLabels();
      }
      changes.layoutX =    changes.width
                        || changes.layoutX
                        || changes.frameWidth
                        || changes.initial;
      changes.layoutY =    changes.height
                        || changes.layoutY
                        || changes.frameHeight
                        || changes.initial;
      this._beforeRenderLayout( changes );
      if ( changes.layoutX ) {
        this._renderLayoutX();
      }
      if ( changes.layoutY ) {
        this._renderLayoutY();
      }
      this._afterRenderLayout( changes );
      this.base( arguments, changes );
    },

    _renderLayoutX : function() {
      var space = this.getSpacing();
      var pad = this.__paddingCache;
      var align = this.getHorizontalChildrenAlign();
      var total = this._getContentWidth( "flexible" );
      var inner = this.getInnerWidth();
      var firstCellLeft  = null;
      switch( align ) {
        case "left":
          firstCellLeft  = pad[ 3 ];
        break;
        case "center":
          firstCellLeft  = Math.round( pad[ 3 ] + inner * 0.5 - total * 0.5 );
        break;
        case "right":
          firstCellLeft  = pad[ 3 ] + inner - total;
        break;
        default:
          firstCellLeft  = pad[ 3 ];
        break;
      }
      var left = firstCellLeft ;
      var width = null;
      var style = null;
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this.cellIsDisplayable( i ) ) {
          width = this.getCellWidth( i );
          if( this._cellHasContent( i ) ) {
            style = this.getCellNode( i ).style;
            style.left = left + "px";
            style.width = width  + "px";
          }
          left += ( width + space );
        }
      }
    },

    _renderLayoutY : function() {
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this._cellHasContent( i ) ) {
          this._renderCellLayoutY( i );
        }
      }
    },

    _renderCellLayoutY : function( cell ) {
      var align = this.getVerticalChildrenAlign();
      var pad = this.__paddingCache;
      var inner = this.getInnerHeight();
      var height = this.getCellHeight( cell );
      var top = null;
      switch( align ) {
        case "top":
          top = pad[ 0 ];
        break;
        case "middle":
          top = Math.round( pad[ 0 ] + inner * 0.5 - height * 0.5 );
        break;
        case "bottom":
          top = pad[ 0 ] + inner - height;
        break;
        default:
          top = pad[ 0 ];
        break;
      }
      var style = this.getCellNode( cell ).style;
      style.top = top + "px";
      style.height = height + "px";
    },

    /*
    ---------------------------------------------------------------------------
      IMAGE
    ---------------------------------------------------------------------------
    */

    // TODO [tb] : refactor
    _getImageHtml : rwt.util.Variant.select( "qx.client", {
      "mshtml" : function( cell ) {
        if( rwt.client.Client.getVersion() < 7 ) {
          var content = this.getCellContent( cell );
          var cssImageStr = "";
          if( content ) {
            cssImageStr
              = "filter:progid:DXImageTransform.Microsoft"
              + ".AlphaImageLoader(src='"
              + content
              + "',sizingMethod='crop')";
          }
          return    '<div style="position:absolute;border:0 none;line-height:0px;font-size:0px;'
                  + cssImageStr
                  + '"></div>';
        } else {
          var content = this.getCellContent( cell );
          var cssImageStr = "";
          if( content ) {
            cssImageStr = "background-image:url(" + content + ")";
          }
          return   "<div style='position:absolute;border:0 none;line-height:0px;font-size:0px;"
                 + cssImageStr
                 + ";background-repeat:no-repeat;' ></div>";
        }
      },
      "default" : function( cell ) {
        var content = this.getCellContent( cell );
        var cssImageStr = "";
        if( content ) {
          cssImageStr = "background-image:url(" + content + ")";
        }
        return   "<div style='position:absolute;border:0 none;"
               + cssImageStr
               + ";background-repeat:no-repeat;' ></div>";
      }
    } ),

    _updateImage : function( cell ) {
      var node = this.getCellNode( cell );
      var source = this.getCellContent( cell );
      var opacity = this.getEnabled() ? 1 : 0.3;
      rwt.html.Style.setBackgroundImage( node, source, opacity );
    },

    _updateAllImages : function() {
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this._isImageCell( i ) && this._cellHasContent( i ) ) {
          this._updateImage( i );
        }
      }
    },

    _styleAllImagesEnabled : function() {
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this._isImageCell( i ) && this.__cellHasNode( i ) ) {
          this._updateImage( i );
        }
      }
    },

    /*
    ---------------------------------------------------------------------------
      LABEL
    ---------------------------------------------------------------------------
    */

    _getLabelHtml : function( cell ) {
      return   "<div style='position:absolute;border:0 none;overflow:hidden;"
             + this._joinStyleProperties( this.__fontCache )
             + "'>"
             + this.getCellContent( cell )
             + "</div>";
    },

   _joinStyleProperties : function( map ) {
      var str = [];
      var value;
      for( var attribute in map ) {
        value = map[ attribute ];
        if( value ) {
          str.push( attribute, ":", value, ";" );
        }
      }
      var joinedCss = str.join( "" );
      return joinedCss.replace( this.__styleRegExp, "$1-$2" ).toLowerCase();
    },

    _applyFont : function( value, old ) {
      this._styleFont( value );
    },

    _styleFont : function( font ) {
      if( font ) {
        font.renderStyle( this.__fontCache );
      } else {
        rwt.html.Font.resetStyle( this.__fontCache );
      }
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this._isTextCell( i ) && this._cellHasContent( i ) ) {
          if( this.__cellHasNode( i ) ) {
            if( font ) {
              font.renderStyle( this.getCellNode( i ).style );
            } else {
              rwt.html.Font.resetStyle( this.getCellNode( i ).style );
            }
          }
          this.__updateComputedCellDimension( i );
        }
      }
    },

    _applyTextColor : function( value, old ) {
      if( value ) {
        this.setStyleProperty( "color", value );
      } else {
        this.removeStyleProperty( "color" );
      }
    },

    _updateLabel : function( cell ) {
      this.getCellNode( cell ).innerHTML = this.getCellContent( cell );
    },

    _updateAllLabels : function() {
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this._isTextCell( i ) && this._cellHasContent( i ) ) {
          this._updateLabel( i );
        }
      }
    }

  }

} );
