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
    this._visibleChildrenCount = 0;
    this._expanded = false;
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

    isCached : function() {
      return this._cached;
    },

    markCached : function() {
      this._cached = true;
      this._texts = [];
    },

    setTexts : function( texts ) {
      this._texts = texts;
      this._update();
    },

    getText : function( column ) {
      var result = this._texts[ column ];
      return typeof result === "string" ? result : "";
    },

    setFont : function( font ) {
      this._font = font;
      this._update();
    },

    getCellFont : function( column ) {
      var result = this._cellFonts[ column ];
      return typeof result === "string" && result != "" ? result : this._font;
    },

    setCellFonts : function( fonts ) {
      this._cellFonts = fonts;
      this._update();
    },

    setForeground : function( color ) {
      this._foreground = color;
      this._update();
    },

    getCellForeground : function( column ) {
      var result = this._cellForegrounds[ column ];
      return typeof result === "string" ? result : this._foreground;
    },

    setCellForegrounds : function( colors ) {
      this._cellForegrounds = colors;
      this._update();
    },

    setBackground : function( color ) {
      this._background = color;
      this._update();
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
      this._update();
    },

    setImages : function( images ) {
      this._images = images;
      this._update();
    },

    getImage : function( column ) {
      var result = this._images[ column ];
      return typeof result === "string" ? result : null;
    },

    getLevel : function() {
      return this._level;
    },

    getParent : function() {
      return this._parent;
    },

    getVisibleChildrenCount : function() {
      if( this._visibleChildrenCount == null ) {
        this._computeVisibleChildrenCount();
      }
      return this._visibleChildrenCount;
    },

    setVariant : function( variant ) {
      this._variant = variant;
    },

    getVariant : function() {
      return this._variant;
    },

    /** 
     * Behavior is consistent with SWT:
     *  - without index the item is added as the last one (item count increases)
     *  - if the index already has an item, it and all after it are shifted (item count increases)
     *  - if the index does not have an item, its inserted at that index (item count stays)
     *  - if the index is greater the the item count it is ignored (item count increases)
     */
    _add : function( item, index ) {
      if( index === this._children.length || index === undefined ) {    
        this._children.push( item );
        this._update( "add", item );
      } else {
        if( this._children[ index ] ) {
          this._children.splice( index, 0, item );
          this._update( "add", item );
        } else {
          this._children[ index ] = item;
          item._update();
        }
      }
    },

    _remove : function( item ) {
      var children = this._children;
      var index = children.indexOf( item );
      this._children.splice( index, 1 );
      this._update( "remove", item );
    },

    hasChildren : function() {
      return this._children.length > 0;
    },
    
    getChild : function( index ) {
      var result = this._children[ index ];
      if( !result ) {
       if( index >= 0 && index < this._children.length ) {
          result = new org.eclipse.rwt.widgets.TreeItem( this, index, true );
        } else {
          result = null;
        }
      }
      return result;
    },
    
    isChildCreated : function( index ) {
      return this._children[ index ] !== undefined;
    },

    isChildCached : function( index ) {
      return this._children[ index ].isCached();
    },

    getLastChild : function() {
      return this.getChild( this._children.length - 1 ); 
    },
    
    getIndexOfChild : function( item ) {
      return this._children.indexOf( item );
    },

    setExpanded : function( value ) {
      if( this._expanded != value ) {
        this._expanded = value;
        this._update( value ? "expanded" : "collapsed" );
      } 
    },

    isExpanded : function() {
      return this._expanded;
    },

    hasPreviousSibling : function() {
      var siblings = this._parent._children;
      var index = siblings.indexOf( this ) - 1 ;
      return index >= 0;
    },

    hasNextSibling : function() {
      var siblings = this._parent._children;
      var index = siblings.indexOf( this ) + 1 ;
      return index < siblings.length;
    },

    getPreviousSibling : function() {
      var index = this._parent.getIndexOfChild( this ) - 1 ;
      return this._parent.getChild( index );
    },

    getNextSibling : function() {
      var index = this._parent.getIndexOfChild( this ) + 1 ;
      return this._parent.getChild( index );
    },

    isRootItem : function() {
      return this._level < 0;
    },
    
    setChecked : function( value ) {
      this._checked = value;
      this._update();
    },
    
    isChecked : function() {
      return this._checked;
    },
    
    setGrayed : function( value ) {
      this._grayed = value;
      this._update();
    },
    
    isGrayed : function() {
      return this._grayed;
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

    //////////////////////////////
    // support for event-bubbling:

    getEnabled : function() {
      return true;
    },

    _update : function( msg, related ) {
      var event = new qx.event.type.DataEvent( "update" );
      event.setData( typeof msg != "undefined" ? msg : null );
      event.setBubbles( true );
      event.setPropagationStopped( false );
      if( related ) {
        event.setRelatedTarget( related );
      }
      this.dispatchEvent( event, true );
    },
    
    _onUpdate : function() {
      this._visibleChildrenCount = null;
    },
    
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
    
    toString : function() {
      return "TreeItem " + this._texts.join();
    }

  }

} );
