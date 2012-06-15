/*******************************************************************************
 * Copyright (c) 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.widgets.GridHeader", {

  extend : qx.ui.layout.CanvasLayout,
  
  construct : function( argsMap ) {
    this.base( arguments );
    this.setOverflow( "hidden" );
    this._fixedColumns = argsMap.splitContainer;
    this._scrollWidth = 0;
    this._scrollLeft = 0;
    this._dummyColumn = new qx.ui.basic.Atom();
    this._dummyColumn.setAppearance( argsMap.appearance + "-column" );
    this._dummyColumn.setHeight( "100%" );
    this._dummyColumn.setLabel( "&nbsp;" );
    this._dummyColumn.addState( "dummy" );
    this.add( this._dummyColumn );
  },
  
  destruct : function() {
    this._dummyColumn = null;
  },
  
  events: {
    "columnLayoutChanged" : "qx.event.type.Event",
    "scrollLeftChanged" : "qx.event.type.Event"
  },  

  members : {

    setScrollLeft : function( value ) {
      this._scrollLeft = value;
      if( this._fixedColumns ) {
        for( var i = 0; i < this._children.length; i++ ) {
          if( this._children[ i ].isFixed && this._children[ i ].isFixed() ) {
            this._children[ i ].addToQueue( "left" );
          }
        }
        if( !org.eclipse.swt.EventUtil.getSuspended() ) {
          qx.ui.core.Widget.flushGlobalQueues();
        }
      }
      // NOTE [tb] : order is important to prevent flickering in IE
      if( this.isSeeable() ) {
        this.base( arguments, value );
      }
    },
    
    getScrollLeft : function() {
      return this._scrollLeft;
    },
    
    setScrollWidth : function( value ) {
      this._scrollWidth = value;
      if( this.getVisibility() ) {
        this._renderDummyColumn();
      }
    },

    add : function( column ) {
      this.base( arguments, column );
      if( column !== this._dummyColumn ) {
        column.addEventListener( "changeWidth", this._fireUpdateEvent, this );
        column.addEventListener( "changeLeft", this._renderDummyColumn, this );
      }
    },
    
    remove : function( column ) {
      this.base( arguments, column );
      if( column !== this._dummyColumn ) {
        column.removeEventListener( "changeWidth", this._fireUpdateEvent, this );
        column.removeEventListener( "changeLeft", this._renderDummyColumn, this );
        this._fireUpdateEvent();
      }
    },
    
    _afterAppear : function() {
      this.base( arguments );
      this.setScrollLeft( this._scrollLeft );
    },
    
    _fireUpdateEvent : function( event ) {
      this.createDispatchEvent( "columnLayoutChanged" );
    },

    _renderDummyColumn : function() {
      var dummyLeft = this._getDummyColumnLeft();
      var totalWidth = Math.max( this._scrollWidth, this.getWidth() );
      var dummyWidth = Math.max( 0, totalWidth - dummyLeft );      
      this._dummyColumn.setLeft( dummyLeft );
      this._dummyColumn.setWidth( dummyWidth );
    },
    
    _getDummyColumnLeft : function() {
      var columns = this.getChildren();
      var result = 0;
      for( var i = 0; i < columns.length; i++ ) {
        if( columns[ i ] !== this._dummyColumn ) {
          var left = columns[ i ].getLeft() + columns[ i ].getWidth();
          result = Math.max( result, left );
        }
      }
      return result;
    }
    
  }

} );