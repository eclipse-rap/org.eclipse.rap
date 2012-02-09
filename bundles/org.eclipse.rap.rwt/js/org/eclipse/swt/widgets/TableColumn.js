/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
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
 * This class provides the client-side counterpart for 
 * org.eclipse.swt.TableColumn.
 */
qx.Class.define( "org.eclipse.swt.widgets.TableColumn", {
  extend : qx.ui.basic.Atom,

  construct : function( parent ) {
    this.base( arguments );
    this._table = parent;
    this._parentIsTree = parent.getAppearance() === "tree";
    this.setAppearance( this._parentIsTree ? "tree-column" : "table-column" );
    this.setHorizontalChildrenAlign( qx.constant.Layout.ALIGN_LEFT ); 
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this._resizable = true;
    this._moveable = false;
    this._resizeStartX = 0;
    this._inResize = false;
    this._wasResizeOrMoveEvent = false;
    this._inMove = false;
    this._offsetX = 0;
    this._initialLeft = 0;
    this._index = 0;
    // Init width property, without this Table._updateScrollWidth would 
    // accidentially calculate a width of "0auto"
    this.setWidth( 0 );
    // Init left property, seems to be null initially which breaks the markup produced by TableItem
    this.setLeft( 0 );
    this.setHeight( "100%" );
    // Set the label part to 'html mode'
    this._createLabel();
    this.getLabelObject().setMode( qx.constant.Style.LABEL_MODE_HTML );
    this._table.getTableHeader().add( this );
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mousemove", this._onMouseMove, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
    this.addEventListener( "mousedown", this._onMouseDown, this );
    this.addEventListener( "mouseup", this._onMouseUp, this );
    this._sortImage = new qx.ui.basic.Image();
    this._sortImage.setAnonymous( true );
    if( this._parentIsTree ) {
      this._sortImage.setAppearance( "tree-column-sort-indicator" );
    } else {
      this._sortImage.setAppearance( "table-column-sort-indicator" );
    }
    this.add( this._sortImage );
    this._handleZIndex();
  },

  destruct : function() {
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    this.removeEventListener( "mousemove", this._onMouseMove, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
    this.removeEventListener( "mousedown", this._onMouseDown, this );
    this.removeEventListener( "mouseup", this._onMouseUp, this );
    this._disposeFields( "_sortImage" );
    // [if] The qx.core.Object.inGlobalDispose() is used to skip table rendering
    // on browser refresh. See bug:
    // 272686: [Table] Javascript error during table disposal
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=272686
    if( !this._table.getDisposed() && !qx.core.Object.inGlobalDispose() ) {
      this._table.getTableHeader().remove( this );
    }
  },

  statics : {
    RESIZE_CURSOR : 
      (    org.eclipse.rwt.Client.isGecko() 
        && ( org.eclipse.rwt.Client.getMajor() > 1 
             || org.eclipse.rwt.Client.getMinor() >= 8 ) ) 
        ? "ew-resize" 
        : "e-resize",
        
    STATE_MOVING : "moving",
    STATE_MOUSE_OVER : "mouseover"      
  },
   
  members : {

    setIndex : function( value ) {
      this._index = value;
    },

    getIndex : function() {
      return this._index;
    },

    setSortDirection : function( value ) {
      if( value == "up" ) {
        this._sortImage.addState( "up" );
      } else {
        this._sortImage.removeState( "up" );
      }
      if( value == "down" ) {
        this._sortImage.addState( "down" );
      } else {
        this._sortImage.removeState( "down" );
      }
    },

    setResizable : function( value ) {
      this._resizable = value;
    },

    setMoveable : function( value ) {
      this._moveable = value;
    },

    setHasSelectionListener : function( value ) {
      if( value ) {
        this.addEventListener( "click", this._onClick, this );
      } else {
        this.removeEventListener( "click", this._onClick, this );
      }
    },

    _onClick : function( evt ) {
      // Don't send selection event when the onClick was caused while resizing
      if( !this._wasResizeOrMoveEvent ) {
        org.eclipse.swt.EventUtil.widgetSelected( evt );
      }
      this._wasResizeOrMoveEvent = false;
    },

    _onMouseOver : function( evt ) {
      if( !this._inMove && !this._inResize ) {
        this.addState( org.eclipse.swt.widgets.TableColumn.STATE_MOUSE_OVER );
      }
    },

    setAlignment : function( value ) {
      this._table.setAlignment( this._index, value );
      this.getLabelObject().setTextAlign( value );
      this.setHorizontalChildrenAlign( value );
    },

    setFixed : function( value ) {
      if( this._fixed !== value ) {
        this._fixed = value;
        this._handleZIndex();
        this.addToQueue( "left" );
      }
    },
    
    isFixed : function() {
      return this._fixed;
    },
    
    _renderRuntimeLeft : function( value ) {
      var renderValue = value;
      if( this._fixed ) {
        renderValue += this.getParent().getScrollLeft();
      }
      this.base( arguments, renderValue );
    },

    /////////////////////////////
    // Mouse listeners for resize
    
    _onMouseDown : function( evt ) {
      if( !this._inMove && !this._inResize && evt.getButton() === "left" ) {
        var widgetUtil = org.eclipse.swt.WidgetUtil;
        if( this._isResizeLocation( evt.getPageX() ) ) {
          this._inResize = true;
          var position = this.getLeft() + this.getWidth();
          this._table._showResizeLine( position );
          this._resizeStartX = evt.getPageX();
          this.setCapture( true );
          evt.stopPropagation();
          evt.preventDefault();
          widgetUtil._fakeMouseEvent( this, "mouseout" );
        } else if( this._moveable ) {
          this._inMove = true;
          this.setCapture( true );
          this._handleZIndex();
          this._offsetX = evt.getPageX() - this.getLeft();
          this._initialLeft = this.getLeft();
          evt.stopPropagation();
          evt.preventDefault();
          widgetUtil._fakeMouseEvent( this, "mouseout" );
        }
      }
    },

    _onMouseUp : function( evt ) {
      var widgetUtil = org.eclipse.swt.WidgetUtil;
      if( this._inResize ) {
        this._table._hideResizeLine();
        this.getTopLevelWidget().setGlobalCursor( null );
        this.setCapture( false );
        var newWidth = this._getResizeWidth( evt.getPageX() );
        this._sendResized( newWidth );
        this._inResize = false;
        this._wasResizeOrMoveEvent = true;
        evt.stopPropagation();
        evt.preventDefault();
        widgetUtil._fakeMouseEvent( evt.getTarget(), "mouseover" );        
      } else if( this._inMove ) {
        this._inMove = false;
        this.setCapture( false );
        this._handleZIndex();
        this.removeState( org.eclipse.swt.widgets.TableColumn.STATE_MOVING );
        if(    this.getLeft() < this._initialLeft - 1 
            || this.getLeft() > this._initialLeft + 1 ) 
        {
          this._wasResizeOrMoveEvent = true;
          // Fix for bugzilla 306842
          var pageLeft = Math.round( this.getElement().getBoundingClientRect().left );
          this._sendMoved( this.getLeft() + evt.getPageX() - pageLeft );
        } else {
          this.setLeft( this._initialLeft );
        }
        evt.stopPropagation();
        evt.preventDefault();
        widgetUtil._fakeMouseEvent( evt.getTarget(), "mouseover" );
      }
    },

    _onMouseMove : function( evt ) {
      if( this._inResize ) {
        var position = this.getLeft() + this._getResizeWidth( evt.getPageX() );
        // min column width is 5 px
        if( position < this.getLeft() + 5 ) {
          position = this.getLeft() + 5;
        }
        this._table._showResizeLine( position );
      } else if( this._inMove ) {
        this.setLeft( evt.getPageX() - this._offsetX );
        this.addState( org.eclipse.swt.widgets.TableColumn.STATE_MOVING );
      } else {
        if( this._isResizeLocation( evt.getPageX() ) ) {
          this.getTopLevelWidget().setGlobalCursor( org.eclipse.swt.widgets.TableColumn.RESIZE_CURSOR );
        } else {
          this.getTopLevelWidget().setGlobalCursor( null );
        }
      }
      evt.stopPropagation();
      evt.preventDefault();
    },

    _onMouseOut : function( evt ) {
      this.removeState( org.eclipse.swt.widgets.TableColumn.STATE_MOUSE_OVER );
      if( !this._inResize ) {
        this.getTopLevelWidget().setGlobalCursor( null );
        evt.stopPropagation();
        evt.preventDefault();
      }
    },

    /////////////////////////////
    // Helping methods for resize

    /** Returns whether the given pageX is within the right 5 pixels of this
     * column */
    _isResizeLocation : function( pageX ) {
      var result = false;
      if( this._resizable ) {
        var columnRight 
          = qx.bom.element.Location.getLeft( this.getElement() ) 
          + this.getWidth();
        if( pageX >= columnRight - 5 && pageX <= columnRight ) {
          result = true;
        }
      }
      return result;
    },

    /** Returns the width of the column that is currently being resized */
    _getResizeWidth : function( pageX ) {
      var delta = this._resizeStartX - pageX;
      return this.getWidth() - delta;
    },

    _handleZIndex : function() {
      var value = 1;
      if( this._inMove ) {
        value = 1e8; 
      } else if( this._fixed ) {
        value = 1e7;
      }
      this.setZIndex( value );
    },

    _sendResized : function( width ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.controlResized", id );
        req.addParameter( id + ".width", width );
        req.send();
      }
    },
    
    _sendMoved : function( left ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.controlMoved", id );
        req.addParameter( id + ".left", left );
        req.send();
      }
    }

  }
});
