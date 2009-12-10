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

qx.Class.define( "org.eclipse.rwt.TreeDNDFeedback", {
  extend : qx.core.Object,

  construct : function( tree ) {
    this.base( arguments );
    this._tree = tree._tree;
    this._feedback = null;
    this._currentItem = null;
    this._insertIndicator = null;
    this._expandTimer = null;      
    this._scrollTimer = null;      
  },

  destruct : function() {
    this._renderFeedback( this._currentItem, false );
    if( this._expandTimer != null ) {
      this._expandTimer.dispose();
      this._expandTimer = null;
    }
    if( this._scrollTimer != null ) {
      this._scrollTimer.dispose();
      this._scrollTimer = null;
    }
    this._tree = null;
    this._feedback = null;
    this._insertIndicator = null;
    this._currentItem = null;
  },

  members : {

    /////////
    // Public

    setFeedback : function( feedbackMap ) {
      this._renderFeedback( this._currentItem, false );
      this._feedback = feedbackMap;
      this._renderFeedback( this._currentItem, true );
    },

    renderFeedback : function( target ) {
      this._renderFeedback( this._currentItem, false );
      this._renderFeedback( target, true );
      this._currentItem = target;
    },

    isFeedbackNode : function( node ) {
      var result = false;
      if( this._insertIndicator != null && this._insertIndicator == node ) {
        result = true;
      }
      return result;
    },

    ////////////
    // Internals

    _renderFeedback : function( item, value ) {
      if( this._feedback != null && item != null ) {
        if( this._feedback[ "select" ] ) {
          this._renderFeedbackSelect( item, value );
        } else if( this._feedback[ "before" ] ) {
          this._renderFeedbackBefore( item, value );
        } else if( this._feedback[ "after" ] ) {
          this._renderFeedbackAfter( item, value );
        }
        if( this._feedback[ "expand" ] ) {
          this._renderFeedbackExpand( item, value );
        }
        if( this._feedback[ "scroll" ] ) {
          this._renderFeedbackScroll( item, value );
        }
      }
    },
    
    _renderFeedbackSelect : function( item, value ) {
      var labelObject = item.getLabelObject();      
      if( value ) {
        item.addState( "selected" );
        labelObject.addState( "selected" );
        labelObject.removeState( "parent_unfocused" );
      } else {
        if( item.getSelected() ) {              
          if( !this._tree._hasFocus ) {
            labelObject.addState( "parent_unfocused" );
          }
        } else {
          item.removeState( "selected" );
          labelObject.removeState( "selected" );
        }
      }
    },

    _renderFeedbackBefore : function( item, value ) {
      if( value ) {
        var labelObject = item.getLabelObject();
        // draw insert-indicator above item (1px heigher)
        var location = this._getItemLocation( labelObject );
        location.y--;
        this._showInsertIndicator( location.x, location.y );
      } else {
        this._hideInsertIndicator();
      }
    },

    _renderFeedbackAfter : function( item, value ) {
      if( value ) {
        var labelObject = item.getLabelObject();
        // draw insert-indicator below item (1px heigher)  
        var location = this._getItemLocation( labelObject );
        var height = labelObject.getHeightValue();
        location.y = location.y + ( height - 1 );
        this._showInsertIndicator( location.x, location.y );
      } else {
        this._hideInsertIndicator();
      }
    },

    _renderFeedbackExpand : function( item, value ) {
      if( value && item.getOpen() == false ) {
        this._startExpandTimer();
      } else {
        this._stopExpandTimer();
      }
    },
    
    _renderFeedbackScroll : function( item, value ) {
      if( value ) {
        this._startScrollTimer();
      } else {
        this._stopScrollTimer();
      }      
    },

    _getItemLocation : function( item ) {
      var location = { x : 0, y : 0 };
      var node = item.getElement();
      var treeNode = this._tree._getTargetNode();
      while( node != treeNode ) {
        location.x += parseInt( node.style.left );
        location.y += parseInt( node.style.top );
        node = node.parentNode;
      }
      return location;
    },
    
    /////////
    // Helper

    _showInsertIndicator : function( x, y ) {
      if( this._insertIndicator == null ) {
        var div = document.createElement( "div" );
        div.style.position = "absolute";
        div.style.borderTopStyle = "solid";
        div.style.borderTopColor = "black";
        div.style.borderTopWidth = "2px";
        div.style.zIndex = 100000;
        div.style.height = "2px";
        this._insertIndicator = div;
      }
      var width = this._tree.getWidthValue() - ( x + 6 );
      this._insertIndicator.style.left = x + "px";
      this._insertIndicator.style.top = y + "px";
      this._insertIndicator.style.width = width + "px";
      var treeNode = this._tree._getTargetNode();
      treeNode.appendChild( this._insertIndicator );
    },

    _hideInsertIndicator : function() {
      var treeNode = this._tree._getTargetNode();
      treeNode.removeChild( this._insertIndicator );
    },
    
    _startExpandTimer : function() {
      if( this._expandTimer == null ) {
        this._expandTimer = new qx.client.Timer( 750 );
        this._expandTimer.addEventListener( "interval", 
                                            this._onExpandTimer, 
                                            this );
      }
      this._expandTimer.setEnabled( true );
    },

    _stopExpandTimer : function() {
      if( this._expandTimer != null ) {
        this._expandTimer.stop();
      }
    },

    _onExpandTimer : function( event ) {
      this._stopExpandTimer();
      this._currentItem.open();
    },

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
    
    _getScrollItem : function( item ) { 
      var result = null; 
      if( !this._isScrolledIntoView( item ) ) {
        result = item;
      } else {
        var manager = this._tree.getManager();      
        var next = manager.getNext( item );
        if(    typeof next != "undefined"
            && next != item 
            && !this._isScrolledIntoView( next ) ) 
        {
          result = next;
        } else {
          var previous = manager.getPrevious( item );
          if(    typeof previous != "undefined"
              && previous != item 
              && !this._isScrolledIntoView( previous ) ) 
          {
            result = previous;
          }
        }
      }
      return result;
    },

    _isScrolledIntoView : function( item ) {
      var itemTop = this._getItemLocation( item.getLabelObject() )[ 1 ];
      var scrollTop = this._tree.getScrollTop();
      var itemTopRelative = itemTop - scrollTop;
      var containerHeight = this._tree.getInnerHeight();
      var itemHeight = item.getLabelObject().getHeightValue();
      var above = itemTopRelative < 0;
      var below = ( itemTopRelative + itemHeight ) > containerHeight;
      return below == above; 
    },

    _onScrollTimer : function( event ) {
      this._stopScrollTimer();
      var item = this._getScrollItem( this._currentItem );
      if( item != null ) {
        item.getLabelObject().scrollIntoViewY();
        var oldItem = this._currentItem;
        var wrapper = function() {
          this._targetUpdateCheck( oldItem, item );
        };
        qx.client.Timer.once( wrapper, this, 1 );
      }
    },

    _targetUpdateCheck : function( oldItem, newItem ) {
      if( !this.isDisposed() ) {
        if(    newItem != this._currentItem
            && oldItem == this._currentItem
            || oldItem == newItem ) 
        {
          // NOTE : oldItem == newItem: the item was only partly in view
          var dndSupport = org.eclipse.rwt.DNDSupport.getInstance()
          dndSupport.setCurrentTargetWidget( newItem );
        }
      }
    }
    
  }

} );

