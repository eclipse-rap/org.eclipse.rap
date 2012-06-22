/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.widgets.GridColumnLabel", {
  extend : org.eclipse.rwt.widgets.MultiCellWidget,

  construct : function( baseAppearance ) {
    this.base( arguments, [ "image", "label", "image" ] );
    this._resizeStartX = 0;
    this._inResize = false;
    this._wasResizeOrMoveEvent = false;
    this._feedbackVisible = false;
    this._inMove = false;
    this._offsetX = 0;
    this._initialLeft = 0;
    this.setAppearance( baseAppearance + "-column" );
    this.setHorizontalChildrenAlign( "left" ); 
    this.setOverflow( "hidden" );
    this.setWidth( 0 );
    this.setLeft( 0 );
    this.setHeight( "100%" );
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mousemove", this._onMouseMove, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
    this.addEventListener( "mousedown", this._onMouseDown, this );
    this.addEventListener( "mouseup", this._onMouseUp, this );
    this.addEventListener( "click", this._onClick, this );
  },

  members : {

    setText : function( value ) {
      var EncodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = EncodingUtil.escapeText( value, false );
      text = EncodingUtil.replaceNewLines( text, "<br/>" );
      this.setCellContent( 1, text );
    },

    setImage : function( value ) {
      if( value === null ) {
        this.setCellContent( 0, null );
        this.setCellDimension( 0, 0, 0 );
      } else {
        this.setCellContent( 0, value[ 0 ] );
        this.setCellDimension( 0, value[ 1 ], value[ 2 ] );
      }
    },

    setSortIndicator : function( value ) {
      if( value ) {
        var manager = qx.theme.manager.Appearance.getInstance();
        var states = {};
        states[ value ] = true;
        var styleMap = manager.styleFrom( this.getAppearance() + "-sort-indicator", states );
        var image = styleMap.backgroundImage;
        this.setCellContent( 2, image[ 0 ] );
        this.setCellDimension( 2, image[ 1 ], image[ 2 ] );
      } else {
        this.setCellContent( 2, null );
        this.setCellDimension( 2, 0, 0 );
      }
    },

    setLeft : function( value ) {
      this.base( arguments, value );
      this._hideDragFeedback( true );
    },

    _onMouseOver : function( evt ) {
      if( !this._inMove && !this._inResize ) {
        this.addState( "mouseover" );
      }
    },

    _onMouseDown : function( evt ) {
      if( !this._inMove && !this._inResize && evt.getButton() === "left" ) {
        if( this._isResizeLocation( evt.getPageX() ) && this._allowResize() ) {
          this._inResize = true;
          var position = this.getLeft() + this.getWidth();
          this.dispatchSimpleEvent( "showResizeLine", { "position" : position }, true );
          this._resizeStartX = evt.getPageX();
          this.setCapture( true );
          evt.stopPropagation();
          evt.preventDefault();
          org.eclipse.swt.WidgetUtil._fakeMouseEvent( this, "mouseout" );
        } else if( this._allowMove() ) {
          this._inMove = true;
          this.setCapture( true );
          this._offsetX = evt.getPageX() - this.getLeft();
          this._initialLeft = evt.getPageX();
          evt.stopPropagation();
          evt.preventDefault();
          org.eclipse.swt.WidgetUtil._fakeMouseEvent( this, "mouseout" );
        }
      }
    },

    _onMouseMove : function( evt ) {
      if( this._inResize ) {
        var position = this.getLeft() + this._getResizeWidth( evt.getPageX() );
        // min column width is 5 px
        if( position < this.getLeft() + 5 ) {
          position = this.getLeft() + 5;
        }
        this.dispatchSimpleEvent( "showResizeLine", { "position" : position }, true );
      } else if( this._inMove ) {
        this.addState( "mouseover" );
        var left = evt.getPageX() - this._offsetX;
        this.dispatchSimpleEvent( "showDragFeedback", { "target" : this, "position" : left } );
        this._feedbackVisible = true;
      } else {
        if( this._isResizeLocation( evt.getPageX() ) ) {
          this.getTopLevelWidget().setGlobalCursor( "ew-resize" );
        } else {
          this.getTopLevelWidget().setGlobalCursor( null );
        }
      }
      evt.stopPropagation();
      evt.preventDefault();
    },

    _onMouseUp : function( evt ) {
      var widgetUtil = org.eclipse.swt.WidgetUtil;
      if( this._inResize ) {
        this.dispatchSimpleEvent( "hideResizeLine", null, true ); // bubbles: handled by grid
        this.getTopLevelWidget().setGlobalCursor( null );
        this.setCapture( false );
        var newWidth = this._getResizeWidth( evt.getPageX() );
        this.dispatchSimpleEvent( "resizeEnd", { 
          "target" : this,
          "width" : newWidth 
        } );
        this._inResize = false;
        this._wasResizeOrMoveEvent = true;
        evt.stopPropagation();
        evt.preventDefault();
        widgetUtil._fakeMouseEvent( evt.getTarget(), "mouseover" );
      } else if( this._inMove ) {
        this._inMove = false;
        this.setCapture( false );
        this.removeState( "mouseover" );
        if( Math.abs( evt.getPageX() - this._initialLeft ) > 1 ) {
          this._wasResizeOrMoveEvent = true;
          // Fix for bugzilla 306842
          this.dispatchSimpleEvent( "moveEnd", { 
            "target" : this, 
            "position" : evt.getPageX() 
          } );
        } else {
          this._hideDragFeedback( false );
        }
        evt.stopPropagation();
        evt.preventDefault();
        widgetUtil._fakeMouseEvent( evt.getTarget(), "mouseover" );
      }
    },

    _onClick : function( evt ) {
       // Don't send selection event when the onClick was caused by resizing
      if( !this._wasResizeOrMoveEvent ) {
        this.dispatchSimpleEvent( "selected", { "target" : this } );
      }
      this._wasResizeOrMoveEvent = false;
    },

    _onMouseOut : function( evt ) {
      if( !this._inMove ) {
        this.removeState( "mouseover" );
      }
      if( !this._inResize ) {
        this.getTopLevelWidget().setGlobalCursor( null );
        evt.stopPropagation();
        evt.preventDefault();
      }
    },

    _allowResize : function() {
      return this.dispatchSimpleEvent( "resizeStart", { "target" : this } );
    },

    _allowMove : function() {
      return this.dispatchSimpleEvent( "moveStart", { "target" : this } );
    },

    _hideDragFeedback : function( snap ) {
      if( this._feedbackVisible ) {
        this.dispatchSimpleEvent( "hideDragFeedback", { 
          "target" : this,
          "snap" : snap
        } );
        this._feedbackVisible = false;
      }
    },

    /** Returns whether the given pageX is within the right 5 pixels of this
     * column */
    _isResizeLocation : function( pageX ) {
      var result = false;
      var columnRight = qx.bom.element.Location.getLeft( this.getElement() ) + this.getWidth();
      if( pageX >= columnRight - 5 && pageX <= columnRight ) {
        result = true;
      }
      return result;
    },

    /** Returns the width of the column that is currently being resized */
    _getResizeWidth : function( pageX ) {
      var delta = this._resizeStartX - pageX;
      return this.getWidth() - delta;
    }

  }
} );
