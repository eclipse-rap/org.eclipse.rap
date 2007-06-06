
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
    // Internally used fields for resizing
    this._resizeStartX = 0;
    this._inResize = false;
    this._wasResizeEvent = false;
    // Init width property, without this Table._updateScrollWidth would 
    // accidentially calculate a width of "0auto"
    this.setWidth( 0 );
    // Init left property, seems to be null initially which breaks the markup 
    // produced by TableItem
    this.setLeft( 0 );
    // Set the label part to 'html mode'
    this.setLabel( "(empty)" );
    this.getLabelObject().setMode( "html" );
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
    // Removemouse-listener for 'mouseover' appearance state
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    // Remove mouse-listeners for resize
    this.removeEventListener( "mousemove", this._onMouseMove, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
    this.removeEventListener( "mousedown", this._onMouseDown, this );
    this.removeEventListener( "mouseup", this._onMouseUp, this );
    this._disposeSortImage();
  },

  statics : {
    RESIZE_CURSOR : 
      (    qx.core.Client.getInstance().isGecko() 
        && ( qx.core.Client.getInstance().getMajor() > 1 
             || qx.core.Client.getInstance().getMinor() >= 8 ) ) 
        ? "ew-resize" 
        : "e-resize"
  },
   
  members : {
    
    setSortImage : function( value ) {
      if( value == "" ) {
        this._disposeSortImage();
      } else {
        if( this._sortImage == null ) {
          this._sortImage = new qx.ui.basic.Image();
          this.add( this._sortImage );
        }
        this._sortImage.setSource( value );
      }
    },
    
    setResizable : function( value ) {
      this._resizable = value;
    },
    
    getResizable : function() {
      return this._resizable;
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
      if( !this._wasResizeEvent ) {
        org.eclipse.swt.EventUtil.widgetSelected( evt );
      }
      this._wasResizeEvent = false;
    },

    _onMouseOver : function( evt ) {
      this.addState( "mouseover" );
    },

    /////////////////////////////
    // Mouse listeners for resize
    
    _onMouseMove : function( evt ) {
      if( this._inResize ) {
        var position = this.getLeft() + this._getResizeWidth( evt.getPageX() );
        // min column width is 5 px
        if( position < this.getLeft() + 5 ) {
          position = this.getLeft() + 5;
        }
        this._table._showResizeLine( position );
      } else {
        if( this._isResizeLocation( evt.getPageX() ) ) {
          this.getTopLevelWidget().setGlobalCursor( org.eclipse.swt.widgets.TableColumn.RESIZE_CURSOR );
        } else {
          this.getTopLevelWidget().setGlobalCursor( null );
        }
      }
    },

    _onMouseOut : function( evt ) {
      this.removeState( "mouseover" );
      if( !this._inResize ) {
        this.getTopLevelWidget().setGlobalCursor( null );
      }
    },

    _onMouseDown : function( evt ) {
      this._inResize = this._isResizeLocation( evt.getPageX() );
      if( this._inResize ) {
        var position = this.getLeft() + this.getWidth();
        this._table._showResizeLine( position );
        this._resizeStartX = evt.getPageX();
        this.setCapture( true );
      }
    },

    _onMouseUp : function( evt ) {
      if( this._inResize ) {
        this._table._hideResizeLine();
        this.getTopLevelWidget().setGlobalCursor( null );
        this.setCapture( false );
        var newWidth = this._getResizeWidth( evt.getPageX() );
        this._sendResize( newWidth );
        this._inResize = false;
        this._wasResizeEvent = true;
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


    _sendResize : function( width ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.controlResized", id );
        req.addParameter( id + ".width", width );
        req.send();
      }
    }

  }
});
