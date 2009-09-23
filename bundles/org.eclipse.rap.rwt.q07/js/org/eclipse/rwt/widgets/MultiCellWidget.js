/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/


qx.Class.define( "org.eclipse.rwt.widgets.MultiCellWidget",  {
  extend : qx.ui.basic.Terminator,

  /**
   * param cells: an array of cell types to define widget structure.
   *              Valid types are "image" and "label".
   *              Examples:
   *                [ "image" ]
   *                [ "image, "image", "label", "image" ]
   */
  construct : function( cells ) {
    this.base( arguments );
    this.__createCellData( cells );
    this.__paddingCache = [ 0, 0, 0, 0 ];
    this.__fontCache = {};
    this.__colorCache = "";
    this.initWidth();
    this.initHeight();
    this.addToQueue( "createContent" );
    this.setOverflow( "hidden" );
    this.initSelectable();
    this.initCursor();    
    this.initTextColor();        
  },
  
  destruct : function() {
    this._disposeObjectDeep( "__cellData", 0 );
    this._disposeObjectDeep( "__cellNodes", 0 );
    this._disposeObjectDeep( "__paddingCache", 0 );
    this._disposeObjectDeep( "_fontCache", 0 );
  },

  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

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
    
    textColor : {
      refine : true,
      init : "#000000"
    },

    cursor : {
      refine : true,
      init : "default"
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
    __cellData : null,
    __cellNodes : null,
    __cellCount : null,
    __computedTotalSpacing : null,
    __paddingCache : null,
    __fontCache : null,
    __styleRegExp : /([a-z])([A-Z])/g,

    _applyEnabled : function( value, old ) {
      this.base( arguments, value, old );
      this._styleAllImagesEnabled();
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
      if( this.getEnabled() == false ) {
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

    /*
    ---------------------------------------------------------------------------
      LAYOUT : public api
    ---------------------------------------------------------------------------
    */
    
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
    
    getCellNode : function( cell ) {
      return this.__cellNodes[ cell ];
    },
    
    getCellContent : function( cell ) {
      return this.__cellData[ cell ][ 1 ];
    },
    
    setCellWidth : function( cell, width ) {
      this._setCellWidth( cell, width );
      this._invalidateTotalSpacing(); 
      this._invalidatePreferredInnerWidth(); 
      this._scheduleLayoutX(); 
    },
    
    setCellHeight : function( cell, height ) {
      this._setCellHeight( cell, height );
      this._invalidateTotalSpacing(); 
      this._invalidatePreferredInnerHeight();  
      this._scheduleLayoutY();
    },
    
    getCellDimension : function( cell ) { 
      var width = this.getCellWidth( cell );
      var height = this.getCellHeight( cell );
      return [ width, height ];
    },    
    
    /**
     * Returns the user-set value for width if it exists, else the computed
     */
    getCellWidth : function( cell ) {
      var cellEntry = this.__cellData[ cell ]; 
      var width = ( cellEntry[ 2 ] != null ? cellEntry[ 2 ] : cellEntry[ 4 ] );
      if( width == null ) {
        var computed = this.__computeCellDimension( cellEntry );
        width = computed[ 0 ];
      }
      return width;      
    },
    
    /**
     * Returns the user-set value for height if it exists, else the computed
     */
    getCellHeight : function( cell ) {
      var cellEntry = this.__cellData[ cell ]; 
      var height = ( cellEntry[ 3 ] != null ? cellEntry[ 3 ] : cellEntry[ 5 ] );
      if( height == null ) {
        var computed = this.__computeCellDimension( cellEntry );
        height = computed[ 1 ];
      }
      return height;      
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
    
    _afterScheduleLayoutX : qx.lang.Function.returnTrue,
    
    _afterScheduleLayoutY : qx.lang.Function.returnTrue,    
    
    _beforeComputeInnerWidth : qx.lang.Function.returnTrue,
    
    _beforeComputeInnerHeight : qx.lang.Function.returnTrue,
    
    _beforeRenderLayout : qx.lang.Function.returnTrue,
    
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

    _setCellHeight : function( cell, height ) {
      this.__cellData[ cell ][ 3 ] = height;
    },

    __setCellNode : function( cell, node ) {
      this.__cellNodes[ cell ] = node;
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
        data[ i ] = [ cells[ i ], null, null, null, null, null ];
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

    __computeCellDimension : function( cellEntry ) {
      var dimension;
      if( cellEntry[ 0 ] == "label" && cellEntry[ 1 ] != null ) {
        dimension = this._computeTextDimensions( cellEntry[ 1 ] );
      } else {
        dimension = [ 0, 0 ];
      }
      cellEntry[ 4 ] = dimension[ 0 ];
      cellEntry[ 5 ] = dimension[ 1 ];      
      return dimension;
    },

    _isWidthEssential : qx.lang.Function.returnTrue,
    _isHeightEssential : qx.lang.Function.returnTrue,    

    _computePreferredInnerWidth : function() {
      this._beforeComputeInnerWidth();
      var space = this.getTotalSpacing();
      var content = 0;
      for( var i = 0; i < this.__cellCount; i++ ) {
        content += this.getCellWidth( i );
      }
      return space + content;
    },

    _computePreferredInnerHeight : function() {
      this._beforeComputeInnerHeight();
      var maxHeight = 0;
      for( var i = 0; i < this.__cellCount; i++ ) {
        maxHeight = Math.max( maxHeight, this.getCellHeight( i ) );
      }
      return maxHeight;
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
        if( this.cellIsVisible( i ) ) {
          ret++;
        }
      }
      return ret;
    },

    /**
     * a cell is "visible" ( i.e. counts for the layout) if
     * it either has a content set, or at least one dimension 
     */      
    cellIsVisible : function( cell ) {
      return ( this.getCellWidth( cell ) > 0 );      
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
      this.base( arguments, changes );
    },

    _renderLayoutX : function() {
      var space = this.getSpacing();
      var pad = this.__paddingCache;
      var align = this.getHorizontalChildrenAlign();
      var total = this.getPreferredInnerWidth();
      var inner = this.getInnerWidth();
      var firstCellLeft  = null;
      switch( align ) {
        default:
        case "left":
          firstCellLeft  = pad[ 3 ];
        break;
        case "center":
          firstCellLeft  = Math.round( pad[ 3 ] + inner * 0.5 - total * 0.5 );
        break;
        case "right":
          firstCellLeft  = pad[ 3 ] + inner - total;
        break;
      }
      var left = firstCellLeft ;
      var width = null;
      var style = null;
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this.cellIsVisible( i ) ) {
          width = this.getCellWidth( i );
          if( this._cellHasContent( i ) ) {
            style = this.getCellNode( i ).style;
            style.left = left;
            style.width = width;
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
        default:
        case "top":
          top = pad[ 0 ];
        break;
        case "middle":
          top = Math.round( pad[ 0 ] + inner * 0.5 - height * 0.5 );      
        break;
        case "bottom":
          top = pad[ 0 ] + inner - height;
        break;
      }
      var style = this.getCellNode( cell ).style;
      style.top = top;
      style.height = height;
    },

    /*
    ---------------------------------------------------------------------------
      IMAGE
    ---------------------------------------------------------------------------
    */

    _getImageHtml : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( cell ) {
        var content = this.getCellContent( cell );
        var cssImageStr = "";
        if( content ) {
          cssImageStr
            = "filter:progid:DXImageTransform.Microsoft"
            + ".AlphaImageLoader(src='"
            + content
            + "',sizingMethod='crop')";           
        }
        return    '<div style="position:absolute;border:0 none;'
                + cssImageStr 
                + '"></div>';
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

    _updateImage : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( cell ) {
        var version = qx.core.Client.getVersion();
        var node = this.getCellNode( cell );
        var content = this.getCellContent( cell );
        if( version >= 8 ) {
          if( content ) {
            node.style.filter
              = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"
              + content
              + "',sizingMethod='crop')";
            if ( !this.getEnabled() ) {
              node.style.filter
                += "progid:DXImageTransform.Microsoft.Alpha(opacity = 30)";
            }
          } else {
            node.style.filter = "";
            node.style.backgroundImage = "none";
          }
        } else {
          if( content ) {
            if ( this.getEnabled() ) {
              node.style.backgroundImage = "none";
              node.style.filter
                = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"
                + this.getCellContent( cell )
                + "',sizingMethod='crop')";
            } else {
              node.style.backgroundImage
                =   "URL("
                  + this.getCellContent( cell )
                  + ")";
              node.style.backgroundRepeat = "no-repeat";
              // removed Gray()
              node.style.filter = this.getEnabled() ? "" : "Alpha(Opacity=30)";
            }
          } else {
            node.style.filter = "";
            node.style.backgroundImage = "none";
          }
        }
      },
      "default" : function( cell ) {
        var content = this.getCellContent( cell );
        var cssImageStr = content ? "URL(" + content + ")" : "none"; 
        this.getCellNode( cell ).style.backgroundImage = cssImageStr;
      }
    }),

    _updateAllImages : function() {
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this._isImageCell( i ) && this._cellHasContent( i ) ) {
          this._updateImage( i );
        }
      }
    },

    _styleImageEnabled : qx.core.Variant.select( "qx.client", {
      "default" : function( cell ) {
        var opacity = ( this.getEnabled() === false ) ? 0.3 : "";
        var style = this.getCellNode( cell ).style;
        style.opacity = style.KhtmlOpacity = style.MozOpacity = opacity;
      },
      "mshtml" : function( cell ) {
        this._updateImage( cell );
      }
    }),

    _styleAllImagesEnabled : function() {
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this._isImageCell( i ) && this.__cellHasNode( i ) ) {
          this._styleImageEnabled( i );
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
             + "white-space:nowrap;'>"
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
      qx.theme.manager.Font.getInstance().connect(
        this._styleFont,
        this,
        value
      );
    },

    _styleFont : function( font ) {
      if( font ) {
        font.renderStyle( this.__fontCache );
      } else {
        qx.ui.core.Font.resetStyle( this.__fontCache );
      }
      for( var i = 0; i < this.__cellCount; i++ ) {
        if( this._isTextCell( i ) && this._cellHasContent( i ) ) {
          if( this.__cellHasNode( i ) ) {
            if( font ) {
              font.renderStyle( this.getCellNode( i ).style );
            } else {
              qx.ui.core.Font.resetStyle( this.getCellNode( i ).style );
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
    },

    _computeTextDimensions : function( text ) {
        var element = qx.ui.basic.Label._getMeasureNode();
        var style = element.style;
        var source = this.__fontCache;
        style.fontFamily = source.fontFamily || "";
        style.fontSize = source.fontSize || "";
        style.fontWeight = source.fontWeight || "";
        style.fontStyle = source.fontStyle || "";
        element.innerHTML = text;
        return [ element.scrollWidth, element.scrollHeight ];
    }
  }
  
});