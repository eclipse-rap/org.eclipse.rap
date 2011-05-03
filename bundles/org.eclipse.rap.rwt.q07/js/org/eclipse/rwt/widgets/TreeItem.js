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

  construct : function( parent, index ) {
    // Dispose is only needed to remove items from the tree.
    // Since it holds no references to the dom, it suffices to dispose tree. 
    this._autoDispose = false; 
    this.base( arguments );
    this._parent = null;
    this._level = -1;
    if( parent instanceof org.eclipse.rwt.widgets.TreeItem ) {
      this._parent = parent;
    } else if( parent instanceof org.eclipse.rwt.widgets.Tree ) {
      this._parent = parent.getRootItem(); 
    }
    this._children = [];
    this._visibleChildrenCount = 0;
    this._expanded = false;
    this._texts = [];
    this._images = [];
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
  },
  
  events: {
    "update" : "qx.event.type.Event" 
  },  

  members : {
    
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

    getChildren : function() {
      return this._children;
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

    _add : function( item, index ) {
      if( typeof index == "undefined" ) {        
        this._children.push( item );
      } else {
        this._children.splice( index, 0, item );
      }
      this._update( "add", item );
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
      return this._children[ index ] ? this._children[ index ] : null;
    },

    getLastChild : function( index ) {
      var length = this._children.length ;
      return length > 0 ? this._children[ length - 1 ] : null; 
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
      return this.getPreviousSibling() != null;
    },

    hasNextSibling : function() {
      return this.getNextSibling() != null;
    },

    getPreviousSibling : function() {
      var siblings = this._parent.getChildren();
      var index = siblings.indexOf( this ) - 1;
      return index >= 0 ? siblings[ index ] : null;
    },

    getNextSibling : function() {
      var siblings = this._parent.getChildren();
      var index = siblings.indexOf( this ) + 1 ;
      return index < siblings.length ? siblings[ index ] : null;
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
          result += this.getChild( i ).getVisibleChildrenCount();
        }
      }
      this._visibleChildrenCount = result;      
    },
    
    toString : function() {
      return "TreeItem " + this._texts.join();
    }

  }

} );
