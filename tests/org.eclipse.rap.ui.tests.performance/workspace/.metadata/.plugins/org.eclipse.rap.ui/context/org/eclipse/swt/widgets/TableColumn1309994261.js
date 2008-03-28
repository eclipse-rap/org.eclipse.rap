/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/**
 * This class provides the client-side counterpart for 
 * org.eclipse.swt.TableColumn.
 */
qx.Class.define( "org.eclipse.swt.widgets.TableColumn", {
  extend : qx.ui.basic.Atom,

  construct : function( parent ) {
    this.base( arguments );
    this.setAppearance( "table-column" );
    this.setHorizontalChildrenAlign( qx.constant.Layout.ALIGN_LEFT ); 
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    // Getter/setter variables
    this._sortImage = null;
    this._resizable = true;
    this._moveable = false;
    // Internally used fields for resizing
    this._resizeStartX = 0;
    this._inResize = false;
    this._wasResizeOrMoveEvent = false;
    // Internally used fields for moving
    this._inMove = false;
    this._offsetX = 0;
    this._initialLeft = 0;
    this._bufferedZIndex = 0;
    // Init width property, without this Table._updateScrollWidth would 
    // accidentially calculate a width of "0auto"
    this.setWidth( 0 );
    // Init left property, seems to be null initially which breaks the markup 
    // produced by TableItem
    this.setLeft( 0 );
    // Set the label part to 'html mode'
    this.setLabel( "(empty)" );
    this.getLabelObject().setMode( qx.constant.Style.LABEL_MODE_HTML );
    this.setLabel( "" );
    // Add this column to the list of coluimns maintained by the table
    this._table = parent;
    this._table._addColumn( this );
    // Register mouse-listener for 'mouseover' appearance state
    this.addEventListener( "mouseover", this._onMouseOver, this );
    // Register mouse-listeners for resizing    
    this.addEventListener( "mousemove", this._onMouseMove, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
    this.addEventListener( "mousedown", this._onMouseDown, this );
    this.addEventListener( "mouseup", this._onMouseUp, this );
  },

  destruct : function() {
    // Remove mouse-listener for 'mouseover' appearance state
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    // Remove mouse-listeners for resize
    this.removeEventListener( "mousemove", this._onMouseMove, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
    this.removeEventListener( "mousedown", this._onMouseDown, this );
    this.removeEventListener( "mouseup", this._onMouseUp, this );
    this._disposeSortImage();
    if( !this._table.getDisposed() ) {
      this._table._removeColumn( this );
    }
  },

  statics : {
    RESIZE_CURSOR : 
      (    qx.core.Client.getInstance().isGecko() 
        && ( qx.core.Client.getInstance().getMajor() > 1 
             || qx.core.Client.getInstance().getMinor() >= 8 ) ) 
        ? "ew-resize" 
        : "e-resize",
        
    STATE_MOVING : "moving",
    STATE_MOUSE_OVER : "mouseover"      
  },
   
  members : {
    
    setSortImage : function( value ) {
      if( value == "" ) {
        this._disposeSortImage();
      } else {
        if( this._sortImage == null ) {
          this._sortImage = new qx.ui.basic.Image();
          this._sortImage.setAnonymous( true );
          this.add( this._sortImage );
        }
        this._sortImage.setSource( value );
      }
    },
    
    setResizable : function( value ) {
      this._resizable = value;
    },
    
    setMoveable : function( value ) {
      this._moveable = value;
    },
    
    _disposeSortImage : function() {
      if( this._sortImage != null ) {
        this._sortImage.setParent( null );
        this._sortImage.dispose();
        this._sortImage = null;
      }
    },
    
    /** This listener function is added and removed server-side */
    onClick : function( evt ) {
      // Don't send selection event when the onClick was caused while resizing
      if( !this._wasResizeOrMoveEvent ) {
        org.eclipse.swt.EventUtil.widgetSelected( evt );
      }
      this._wasResizeOrMoveEvent = false;
    },

    _onMouseOver : function( evt ) {
      this.addState( org.eclipse.swt.widgets.TableColumn.STATE_MOUSE_OVER );
    },

    /////////////////////////////
    // Mouse listeners for resize
    
    _onMouseDown : function( evt ) {
      this._inResize = this._isResizeLocation( evt.getPageX() );
      if( this._inResize ) {
        var position = this.getLeft() + this.getWidth();
        this._table._showResizeLine( position );
        this._resizeStartX = evt.getPageX();
        this.setCapture( true );
      } else if( this._moveable ){
        this._inMove = true;
        this.setCapture( true );
        this._bufferedZIndex = this.getZIndex();
        this.setZIndex( 1e8 );
        this._table._unhookColumnMove( this );
        this._offsetX = evt.getPageX() - this.getLeft();
        this._initialLeft = this.getLeft();
      }
    },

    _onMouseUp : function( evt ) {
      if( this._inResize ) {
        this._table._hideResizeLine();
        this.getTopLevelWidget().setGlobalCursor( null );
        this.setCapture( false );
        var newWidth = this._getResizeWidth( evt.getPageX() );
        this._sendResized( newWidth );
        this._inResize = false;
        this._wasResizeOrMoveEvent = true;
      } else if( this._inMove ) {
        this._inMove = false;
        this.setCapture( false );
        this.setZIndex( this._bufferedZIndex );
        this._table._hookColumnMove( this );
        this.removeState( org.eclipse.swt.widgets.TableColumn.STATE_MOVING );
        if(    this.getLeft() < this._initialLeft - 1 
            || this.getLeft() > this._initialLeft + 1 ) 
        {
          this._wasResizeOrMoveEvent = true;
          var pageLeft = qx.html.Location.getPageBoxLeft( this.getElement() );
          this._sendMoved( this.getLeft() + evt.getPageX() - pageLeft );
        } else {
          this.setLeft( this._initialLeft );
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
    },

    _onMouseOut : function( evt ) {
      this.removeState( org.eclipse.swt.widgets.TableColumn.STATE_MOUSE_OVER );
      if( !this._inResize ) {
        this.getTopLevelWidget().setGlobalCursor( null );
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
          = qx.html.Location.getClientBoxLeft( this.getElement() ) 
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


    _sendResized : function( width ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.controlResized", id );
        req.addParameter( id + ".width", width );
        req.send();
      }
    },
    
    _sendMoved : function( left ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
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
