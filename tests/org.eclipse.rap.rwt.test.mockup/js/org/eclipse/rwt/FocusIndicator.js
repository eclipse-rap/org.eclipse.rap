/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.FocusIndicator", {
  type : "singleton",
  extend : qx.core.Object,

  construct : function() {
    this.base( arguments );
    this._frame = null;    
  },

  members : {
    
    _createFrame : function() {
      this._frame = document.createElement( "div" );
      this._frame.setAttribute( "id", "focusIndicator" );
      this._frame.style.position = "absolute";    
      this._frame.style.fontSize = 0;
      this._frame.style.lineHeight = 0;      
      this._frame.style.zIndex = 0;      
    },
    
    _createTheme : function( widget, cssSelector, focusElement ) {
      var theme = null;
      var states = typeof widget.__states != "undefined" ? widget.__states : {}; 
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var border = tv.getCssBorder( cssSelector, "border" );
      var hasValidBorder = border instanceof qx.ui.core.Border;
      var margin = tv.getCssBoxDimensions( cssSelector, "margin" );
      var padding = tv.getCssBoxDimensions( cssSelector, "padding" );
      var paddingIsZero =    padding[ 0 ] == 0
                          && padding[ 1 ] == 0
                          && padding[ 2 ] == 0
                          && padding[ 3 ] == 0;
      var usePadding = !paddingIsZero && focusElement != null; 
      if( hasValidBorder && ( margin != null || usePadding ) ) {
        var color = tv.getCssColor( cssSelector, "background-color" );
        theme = {
          "backgroundColor" : color != "undefined" ? color : "",
          "border" : border,
          "margin" : margin,
          "padding" : usePadding ? padding : null
        };
      }
      return theme;
    },

    show : function( widget, cssSelector, focusElement ) {
      this.hide();
      if( widget.isCreated() ) {
        var theme = this._createTheme( widget, cssSelector, focusElement );
        if( theme != null ) {
          var parentNode = widget._getTargetNode();
          if( this._frame == null ) {
            this._createFrame();
          }
          if( this._frame.parentNode != parentNode ) {
            if( parentNode.hasChildNodes() ) {
              var firstChild = parentNode.firstChild;
              parentNode.insertBefore( this._frame, firstChild );            
            } else {
              parentNode.appendChild( this._frame );            
            }
          }
          this._styleFocusIndicator( theme );
          this._layoutFocusIndicator( widget, theme, focusElement );
        }
      }
    },
    
    
    hide : function() {
      if( this._frame != null && this._frame.parentNode != null ) {
        this._frame.parentNode.removeChild( this._frame );
      }
    },
    
    _styleFocusIndicator : function( theme ) {
      // ignores complex borders and color-manager (for now):
      var border = theme[ "border" ];  
      var style = this._frame.style;
      var edges = [ "Left", "Top", "Bottom", "Right" ];
      for( var i = 0; i < 4; i++ ) {
        var edge = edges[ i ];
        style[ "border" + edge + "Style" ] = border[ "getStyle" + edge ]();
        style[ "border" + edge + "Color" ] = border[ "getColor" + edge ]();
        style[ "border" + edge + "Width" ]
          = border[ "getWidth" + edge ]() + "px";
      }
      style.backgroundColor = theme[ "backgroundColor" ];
    },
    
    _layoutFocusIndicator : function( widget, theme, focusElement ) {
      // NOTE : It is assumed that a focusElement, if given, has 
      //        valid css-bounds (top,left,width,height) set in "px".
      var border = theme[ "border" ];  
      var bounds = [];
      if( theme[ "padding" ] != null ) {
        var padding = theme[ "padding" ];
        bounds[ 0 ] = parseInt( focusElement.style.left ) - padding[ 3 ];
        bounds[ 1 ] = parseInt( focusElement.style.top ) - padding[ 0 ];
        bounds[ 2 ] =   parseInt( focusElement.style.width ) 
                      + padding[ 1 ] 
                      + padding[ 3 ];
        bounds[ 3 ] =   parseInt( focusElement.style.height ) 
                      + padding[ 2 ] 
                      + padding[ 0 ];
      } else {
        var margin = theme[ "margin" ];
        var parentWidth =   widget.getBoxWidth()
                          - widget._cachedBorderLeft
                          - widget._cachedBorderRight;
        var parentHeight =   widget.getBoxHeight()
                           - widget._cachedBorderTop
                           - widget._cachedBorderBottom;
        bounds[ 0 ] = margin[ 3 ];
        bounds[ 1 ] = margin[ 0 ];
        bounds[ 2 ] = parentWidth - ( margin[ 3 ] + margin[ 1 ] );
        bounds[ 3 ] = parentHeight - ( margin[ 0 ] + margin[ 2 ] );
      }
      this._frame.style.left = bounds[ 0 ] + "px";
      this._frame.style.top = bounds[ 1 ] + "px";
      this._frame.style.width = bounds[ 2 ] + "px";
      this._frame.style.height = bounds[ 3 ] + "px";      
    }    
  }
    
} );

