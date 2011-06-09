/*******************************************************************************
 * Copyright (c) 2010, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.widgets.TreeItem", {

  extend : qx.core.Target,

  construct : function( parent, index, placeholder ) {
    // Dispose is only needed to remove items from the tree and widget manager.
    // Since it holds no references to the dom, it suffices to dispose tree. 
    this._autoDispose = false; 
    this.base( arguments );
    this._parent = parent
    this._level = -1;
    this._children = [];
    this._indexCache = {};
    this._visibleChildrenCount = 0;
    this._expandedItems = {};
    this._texts = placeholder ? [ "..." ] : [];
    this._images = [];
    this._cached = !placeholder;
    this._font = null;
    this._cellFonts = [];
    this._foreground = null;
    this._cellForegrounds = [];
    this._background = null;
    this._cellBackgrounds = [];
    this._checked = false;
    this._grayed = false;
    this._variant = null;
    if( this._parent != null ) {
      this._level = this._parent.getLevel() + 1; 
      this._parent._add( this, index );
    }
    this._expanded = this.isRootItem();
    this.addEventListener( "update", this._onUpdate, this );
  },

  destruct : function() {
    if( this._parent != null ) {
      this._parent._remove( this );
    }
    org.eclipse.swt.WidgetManager.getInstance().remove( this );
  },

  statics : {

    createItem : function( parent, index, id ) {
      var parentItem = this._getItem( parent );
      var item;
      if( parentItem.isChildCreated( index ) && !parentItem.isChildCached( index ) ) {
        item = parentItem.getChild( index );
        item.markCached();
      } else {
        item = new org.eclipse.rwt.widgets.TreeItem( parentItem, index, false );
      }
      org.eclipse.swt.WidgetManager.getInstance().add( item, id, false );
    },

    _getItem : function( treeOrItem ) {
      var result;
      if( treeOrItem instanceof org.eclipse.rwt.widgets.Tree ) {
        result = treeOrItem.getRootItem(); 
      } else {
        result = treeOrItem;
      }
      return result;
    }

  },

  events: {
    "update" : "qx.event.type.Event" 
  },  

  members : {

    setItemCount : function( value ) {
      var msg = this._children.length > value ? "remove" : "add";
      this._children.length = value; 
      this._update( msg );
    },

    clear : function() {
      // TODO [tb] : children?
      this._cached = false;
      this._checked = false;
      this._grayed = false;
      this._texts = [ "..." ];
      this._images = [];
      this._background = null;
      this._foreground = null;
      this._font = null;
      this._cellBackgrounds = [];
      this._cellForegrounds = [];
      this._cellFonts = [];
      this._variant = null;
    },

    isCached : function() {
      return this._cached;
    },

    markCached : function() {
      this._cached = true;
      this._texts = [];
    },

    setTexts : function( texts ) {
      this._texts = texts;
      this._update( "content" );
    },

    getText : function( column ) {
      var result = this._texts[ column ];
      return typeof result === "string" ? result : "";
    },

    setFont : function( font ) {
      this._font = font;
      this._update( "content" );
    },

    getCellFont : function( column ) {
      var result = this._cellFonts[ column ];
      return typeof result === "string" && result != "" ? result : this._font;
    },

    setCellFonts : function( fonts ) {
      this._cellFonts = fonts;
      this._update( "content" );
    },

    setForeground : function( color ) {
      this._foreground = color;
      this._update( "content" );
    },

    getCellForeground : function( column ) {
      var result = this._cellForegrounds[ column ];
      return typeof result === "string" ? result : this._foreground;
    },

    setCellForegrounds : function( colors ) {
      this._cellForegrounds = colors;
      this._update( "content" );
    },

    setBackground : function( color ) {
      this._background = color;
      this._update( "content" );
    },

    getCellBackground : function( column ) {
      var result = this._cellBackgrounds[ column ];
      return typeof result === "string" ? result : null;
    },

    getBackground : function() {
      return this._background;
    },

    setCellBackgrounds : function( colors ) {
      this._cellBackgrounds = colors;
      this._update( "content" );
    },

    setImages : function( images ) {
      this._images = images;
      this._update( "content" );
    },

    getImage : function( column ) {
      var result = this._images[ column ];
      return typeof result === "string" ? result : null;
    },

    setChecked : function( value ) {
      this._checked = value;
      this._update( "content" );
    },
    
    isChecked : function() {
      return this._checked;
    },
    
    setGrayed : function( value ) {
      this._grayed = value;
      this._update( "content" );
    },
    
    isGrayed : function() {
      return this._grayed;
    },
    
    setVariant : function( variant ) {
      this._variant = variant;
    },

    getVariant : function() {
      return this._variant;
    },
    
    //////////////////////////
    // relationship management

    isRootItem : function() {
      return this._level < 0;
    },

    getLevel : function() {
      return this._level;
    },

    getParent : function() {
      return this._parent;
    },

    setExpanded : function( value ) {
      if( this._expanded != value ) {
        this._expanded = value;
        this._update( value ? "expanded" : "collapsed" );
        if( value ) {
          this._parent._addToExpandedItems( this );
        } else {
          this._parent._removeFromExpandedItems( this );          
        }
      }
    },

    isExpanded : function() {
      return this._expanded;
    },

    isDisplayable : function() {
      var result = false;
      if( this.isRootItem() || this._parent.isRootItem() ) {
        result = true;
      } else {
        result = this._parent.isExpanded() && this._parent.isDisplayable();
      }
      return result;
    },

    hasChildren : function() {
      return this._children.length > 0;
    },
    
    getChildrenLength : function() {
    	return this._children.length;
    },

    isChildCreated : function( index ) {
      return this._children[ index ] !== undefined;
    },

    isChildCached : function( index ) {
      return this._children[ index ].isCached();
    },

    getVisibleChildrenCount : function() { // TODO [tb] : rather "itemCount"
      if( this._visibleChildrenCount == null ) {
        this._computeVisibleChildrenCount();
      }
      return this._visibleChildrenCount;
    },
    
    getChild : function( index ) {
      var result = this._children[ index ];
      if( !result ) {
       if( index >= 0 && index < this._children.length ) {
          result = new org.eclipse.rwt.widgets.TreeItem( this, index, true );
        }
      }
      return result;
    },    

    getLastChild : function() {
      return this.getChild( this._children.length - 1 ); 
    },
    
    indexOf : function( item ) {
    	var hash = item.toHashCode();
    	if( this._indexCache[ hash ] === undefined ) {
	    	this._indexCache[ hash ] = this._children.indexOf( item );
    	}
      return this._indexCache[ hash ];
    },
    
    /**
     * Returns true if the given item is one of the parents of this item (recursive).
     */
    isChildOf : function( parent ) {
      var result = this._parent === parent
      if( !result && !this._parent.isRootItem() ) {
        result = this._parent.isChildOf( parent );
      }
      return result;
    },

    /**
     * Finds the item at the given index, counting all visible children.
     */
    findItemByFlatIndex : function( index ) {
      // TODO [tb] : could be optimized by creating helper "flatIndexOf" using cached values for
      //             expanded items. This helper could also be used by "getFlatIndex"
      var expanded = this._getExpandedIndicies();
      var localIndex = index;
      var result = null;
      var success = false;
      while( !success && localIndex >= 0) {
        var expandedIndex = expanded.shift();
        if( expandedIndex === undefined || expandedIndex >= localIndex ) {
          result = this.getChild( localIndex );
          if( result ) {
	          this._indexCache[ result.toHashCode() ] = localIndex;
          }
          success = true;
        } else {
          var childrenCount = this.getChild( expandedIndex ).getVisibleChildrenCount();
          var offset = localIndex - expandedIndex; // Items between current item and target item
          if( offset <= childrenCount ) {
            result = this.getChild( expandedIndex ).findItemByFlatIndex( offset - 1 );
            success = true;
            if( result == null ) {
              throw new Error( "getItemByFlatIndex failed" );
            }
          } else {
            localIndex -= childrenCount;
          }
        }
      }
      return result;
    },
    
    /**
     * Gets the index relative to the root-item, counting all visible items inbetween.
     */
    getFlatIndex : function() {
      var localIndex = this._parent.indexOf( this );
      var result = localIndex;
      var expanded = this._parent._getExpandedIndicies();
      while( expanded.length > 0 && localIndex > expanded[ 0 ] ) {
        var expandedIndex = expanded.shift();
        result += this._parent._children[ expandedIndex ].getVisibleChildrenCount();
      }
      if( !this._parent.isRootItem() ) {
        result += this._parent.getFlatIndex() + 1;
      }
      return result;
    },

    hasPreviousSibling : function() {
      var index = this._parent.indexOf( this ) - 1 ;
      return index >= 0;
    },

    hasNextSibling : function() {
      var index = this._parent.indexOf( this ) + 1 ;
      return index < this._parent.getChildrenLength();
    },

    getPreviousSibling : function() {
      var index = this._parent.indexOf( this ) - 1 ;
      return this._parent.getChild( index );
    },

    getNextSibling : function() {
      var index = this._parent.indexOf( this ) + 1 ;
      var item = this._parent.getChild( index );
      this._parent._indexCache[ item.toHashCode() ] = index;
      return item;
    },
    
    /**
     * Returns the next visible item, which my be the first child, 
     * the next sibling or the next sibling of the parent.
     */
    getNextItem : function( skipChildren ) {
      var result = null;
      if( !skipChildren && this.hasChildren() && this.isExpanded() ) {
        result = this.getChild( 0 );
      } else if( this.hasNextSibling() ) {
        result = this.getNextSibling();
      } else if( this.getLevel() > 0 ) {
        result = this._parent.getNextItem( true );
      }
      return result;
    },
    
    /**
     * Returns the previous visible item, which my be the previous sibling, 
     * the previous siblings last child, or the parent.
     */
    getPreviousItem : function() {
      var result = null;
      if( this.hasPreviousSibling() ) {
        result = this.getPreviousSibling();
        while( result.hasChildren() && result.isExpanded() ) {
          result = result.getLastChild();
        }        
      } else if( this.getLevel() > 0 ) {
        result = this._parent
      }
      return result;
    },
    
    /////////////////////////
    // API for other TreeItem

    _add : function( item, index ) {
      if( this._children[ index ] ) {
        this._children.splice( index, 0, item );
        this._children.pop();
        this._update( "add", item );
      } else {
        this._children[ index ] = item;
      }
    },

    _remove : function( item ) {
      if( item.isExpanded() ) {
        delete this._expandedItems[ item.toHashCode() ];
      }
      var index = this._children.indexOf( item );
      this._children.splice( index, 1 );
      this._children.push( undefined );
      this._update( "remove", item );
    },
    
    _addToExpandedItems : function( item ) {
      this._expandedItems[ item.toHashCode() ] = item;
    },

    _removeFromExpandedItems : function( item ) {
      delete this._expandedItems[ item.toHashCode() ];
    },
    
    //////////////////////////////
    // support for event-bubbling:

    getEnabled : function() {
      return true;
    },

    _update : function( msg, related ) {
      var event = new qx.event.type.DataEvent( "update" );
      event.setData( msg );
      event.setBubbles( true );
      event.setPropagationStopped( false );
      if( related ) {
        event.setRelatedTarget( related );
      }
      this.dispatchEvent( event, true );
    },
    
    _onUpdate : function( event ) {
      if( event.getData() !== "content" ) {
        this._visibleChildrenCount = null;
        this._indexCache = {};
      }
    },
    
    /////////
    // Helper
    
    _computeVisibleChildrenCount : function() {
      // NOTE: Caching this value speeds up creating and scrolling the tree considerably
      var result = 0;
      if( this.isExpanded() || this.isRootItem() ) {
       result = this._children.length;
        for( var i = 0; i < this._children.length; i++ ) {
          if( this.isChildCreated( i ) ) {
            result += this.getChild( i ).getVisibleChildrenCount();
          }
        }
      }
      this._visibleChildrenCount = result;      
    },
    
    _getExpandedIndicies : function() {
      var result = [];
      for( var key in this._expandedItems ) {
        result.push( this.indexOf( this._expandedItems[ key ] ) ); 
      }
      // TODO [tb] : result could be cached
      return result.sort( function( a, b ){ return a - b; } );
    },
    
    toString : function() {
      return "TreeItem " + this._texts.join();
    }

  }

} );
