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

qx.Class.define( "org.eclipse.rwt.TableDNDFeedback", {
  extend : qx.core.Object,

  construct : function( table ) {
    this.base( arguments );
    this._table = table;
    this._feedback = null;
    this._currentRow = null;
    this._scrollTimer = null;      
  },

  destruct : function() {
    this._renderFeedback( this._currentRow, false );
    if( this._scrollTimer != null ) {
      this._scrollTimer.dispose();
      this._scrollTimer = null;
    }
    this._table = null;
    this._feedback = null;
    this._currentRow = null;
  },

  members : {
    
    /////////
    // Public
    
    setFeedback : function( feedbackMap ) {
      this._renderFeedback( this._currentRow, false );
      this._feedback = feedbackMap;
      this._renderFeedback( this._currentRow, true );
    },

    renderFeedback : function( target ) {
      this._renderFeedback( this._currentRow, false );
      this._renderFeedback( target, true );
      this._currentRow = target;
    },

    isFeedbackNode : function( node ) {
      return false;
    },
    
    ////////////
    // Internals
    
    _renderFeedback : function( row, value ) {
      if( this._feedback != null && row != null ) {
        if( this._feedback[ "select" ] ) {
          this._renderFeedbackSelect( row, value );
        }
        if( this._feedback[ "scroll" ] ) {
          this._renderFeedbackScroll( row, value );
        }
      }
    },
    
    _renderFeedbackSelect : function( row, value ) {
      if( value ) {
        row.addState( "selected" );
        row.removeState( "parent_unfocused" );
      } else {
        this._table.updateItem( row.getItemIndex(), false );
        if( !this._table.getFocused() ) {
          row.addState( "parent_unfocused" );
        }
      }
    },
    
    _renderFeedbackScroll : function( row, value ) {
      if( value ) {
        this._startScrollTimer();
      } else {
        this._stopScrollTimer();
      }      
    },
    
    /////////
    // Helper
    
    _startScrollTimer : function() {
      if( this._scrollTimer == null ) {
        this._scrollTimer = new qx.client.Timer( 250 );
        this._scrollTimer.addEventListener( "interval", 
                                            this._onScrollTimer, 
                                            this );
      }
      this._scrollTimer.setEnabled( true );
    },
    
    _stopScrollTimer : function() {
      if( this._scrollTimer != null ) {
        this._scrollTimer.stop();
      }
    },
    
    _getScrollDirection : function( itemIndex ) {
      var result = 0;      
      if( !this._table._isItemFullyVisible( itemIndex ) ) {
        result = 1;
      } else if( itemIndex > 0 && itemIndex < ( this._table._itemCount - 1 ) ) {
        if( !this._table._isItemFullyVisible( itemIndex + 1 ) ) {
          result = 1; 
        } else if( !this._table._isItemFullyVisible( itemIndex - 1 ) ) {
          result = -1;
        }
      }
      return result;
    },
   
    _onScrollTimer : function( event ) {
      this._stopScrollTimer();
      var itemIndex = this._currentRow.getItemIndex();
      if( itemIndex != -1 ) {
        var offset = this._getScrollDirection( itemIndex );
        if( offset != 0 ) {
          var topIndex = this._table._topIndex + offset;
          this._table._internalSetTopIndex( topIndex, true );
          var newIndex = itemIndex + offset;
          var rowIndex = this._table._getRowIndexFromItemIndex( newIndex );
          var newRow = this._table._rows[ rowIndex ];
          var oldRow = this._currentRow;
          var wrapper = function() {
            this._targetUpdateCheck( oldRow, newRow );
          };
          qx.client.Timer.once( wrapper, this, 1 );
        }
      }
    },
    
    _targetUpdateCheck : function( oldRow, newRow ) {
      if( !this.isDisposed() ) {
        if( newRow != this._currentRow && oldRow == this._currentRow ) {
          var dndSupport = org.eclipse.rwt.DNDSupport.getInstance();
          dndSupport.setCurrentTargetWidget( newRow );
        }
      }
    }
    
  }
    
} );

