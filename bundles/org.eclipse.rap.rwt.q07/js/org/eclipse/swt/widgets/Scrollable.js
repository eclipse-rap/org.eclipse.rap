/*******************************************************************************
 *  Copyright: 2004,2011 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.Scrollable", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( clientArea ) {
    this.base( arguments );
    this._clientArea = clientArea;
    this._horzScrollBar = new org.eclipse.rwt.widgets.ScrollBar( true );
    this._vertScrollBar = new org.eclipse.rwt.widgets.ScrollBar( false );
    this._blockScrolling = false;
    this._internalChangeFlag = false;
    this.add( this._clientArea );
    this.add( this._horzScrollBar );
    this.add( this._vertScrollBar );
    this._configureScrollBars();
    this._configureClientArea();
    this.__onscroll = qx.lang.Function.bindEvent( this._onscroll, this );
  },
  
  destruct : function() {
    var el = this._clientArea._getTargetNode();
    if( el ) {
      var eventUtil = qx.html.EventRegistration;
      eventUtil.removeEventListener( el, "scroll", this.__onscroll );
      delete this.__onscroll;
    }
    this._clientArea = null;
    this._horzScrollBar = null;
    this._vertScrollBar = null;
  },
  
  events : {
    "userScroll" : "qx.event.type.Event"
  },
  
  statics : {
    _nativeWidth : null,
    
    getNativeScrollBarWidth : function() {
      if( this._nativeWidth === null ) {
        var dummy = document.createElement( "div" );
        dummy.style.width = "100px";
        dummy.style.height = "100px";
        dummy.style.overflow = "scroll";
        dummy.style.visibility = "hidden";
        document.body.appendChild( dummy );
        this._nativeWidth = dummy.offsetWidth - dummy.clientWidth;
        document.body.removeChild(dummy);
      }
      return this._nativeWidth;
    }

  },

  members : {
    
    /////////
    // Public
    
    setScrollBarsVisible : function( horizontal, vertical ) {
      this._horzScrollBar.setDisplay( horizontal );  
      this._vertScrollBar.setDisplay( vertical );
      var overflow = "hidden";
      if( horizontal && vertical ) {
        overflow = "scroll";
      } else if( horizontal ) {
        overflow = "scrollX";
      } else if( vertical ) {
        overflow = "scrollY";
      }
      this._clientArea.setOverflow( overflow );
      this._layoutX();
      this._layoutY();
    },
 
    setHBarSelection : function( value ) {
      this._internalChangeFlag = true;
      this._horzScrollBar.setValue( value );
      this._internalChangeFlag = false;
    },

    setVBarSelection : function( value ) {
      this._internalChangeFlag = true;
      this._vertScrollBar.setValue( value );
      this._internalChangeFlag = false;
    },
    
    setBlockScrolling : function( value ) {
      this._blockScrolling = value;
    },
 
    /////////
    // Layout

    _configureClientArea : function() {
      this._clientArea.setOverflow( "scroll" );
      this._clientArea.setLeft( 0 );
      this._clientArea.setTop( 0 );
      this._clientArea.addEventListener( "create", this._onClientCreate, this );
      this._clientArea.addEventListener( "appear", this._onClientAppear, this );
      // TOOD [tb] : Do this with an eventlistner after fixing Bug 327023 
      this._clientArea._layoutPost 
        = qx.lang.Function.bindEvent( this._onClientLayout, this );
    },
    
    _configureScrollBars : function() {
      var dragBlocker = function( event ) { event.stopPropagation(); };
      this._horzScrollBar.setLeft( 0 );
      this._horzScrollBar.addEventListener( "dragstart", dragBlocker );
      this._vertScrollBar.setTop( 0 );
      this._vertScrollBar.addEventListener( "dragstart", dragBlocker );
      this._horzScrollBar.addEventListener( "changeValue", 
                                            this._onHorzScrollBarChangeValue, 
                                            this );
      this._vertScrollBar.addEventListener( "changeValue", 
                                            this._onVertScrollBarChangeValue, 
                                            this );      
    },

    _applyWidth : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._layoutX();
    },
    
    _applyHeight : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._layoutY();
    },
    
    _applyBorder : function( newValue, oldValue ) {
      this.base( arguments, newValue, oldValue );
      this._layoutX();
      this._layoutY();
    },    

    _layoutX : function() {
      var clientWidth = this.getWidth() - this.getFrameWidth();
      if( this._vertScrollBar.getDisplay() ) {
        clientWidth -= this._vertScrollBar.getWidth();
      } 
      this._clientArea.setWidth( clientWidth );
      this._vertScrollBar.setLeft( clientWidth );
      this._horzScrollBar.setWidth( clientWidth );    },

    _layoutY : function() {        
      var clientHeight = this.getHeight() - this.getFrameHeight();
      if( this._horzScrollBar.getDisplay() ) {
        clientHeight -= this._horzScrollBar.getHeight();
      } 
      this._clientArea.setHeight( clientHeight );
      this._vertScrollBar.setHeight( clientHeight );
      this._horzScrollBar.setTop( clientHeight );
    },

    _onClientCreate : function( evt ) {
      this._clientArea.prepareEnhancedBorder();
      var el = this._clientArea._getTargetNode();
      var eventUtil = qx.html.EventRegistration;
      eventUtil.addEventListener( el, "scroll", this.__onscroll );
    },

    _onClientLayout : function() {
      var barWidth 
        = org.eclipse.swt.widgets.Scrollable.getNativeScrollBarWidth();
      var node = this._clientArea._getTargetNode();
      var el = this._clientArea.getElement();
      var overflow = this._clientArea.getOverflow();
      var width = parseInt( el.style.width );
      var height = parseInt( el.style.height );
      if( overflow === "scroll" || overflow === "scrollY" ) {
        width += barWidth;
      }
      if( overflow === "scroll" || overflow === "scrollX" ) {
        height += barWidth;
      }
      node.style.width = width
      node.style.height = height;
    },
    
    ////////////
    // Scrolling
   
    _onHorzScrollBarChangeValue : function() {
      if( this._isCreated ) {
        this._syncClientArea( true, false );
      }
      if( !this._internalChangeFlag ) {
        this.createDispatchEvent( "userScroll" );
      }
    }, 

    _onVertScrollBarChangeValue : function() {
      if( this._isCreated ) {
        this._syncClientArea( false, true );
      }
      if( !this._internalChangeFlag ) {
        this.createDispatchEvent( "userScroll" );
      }
    },

    _onClientAppear : function() {
      this._internalChangeFlag = true;
      this._syncClientArea( true, true );
      this._internalChangeFlag = false;
    },
     
    _onscroll : function( evt ) {
      if( !this._internalChangeFlag ) {
        org.eclipse.rwt.EventHandlerUtil.stopDomEvent( evt );
        var blockH = this._blockScrolling || !this._horzScrollBar.getDisplay();
        var blockV = this._blockScrolling || !this._vertScrollBar.getDisplay();
        this._internalChangeFlag = true;
        this._syncClientArea( blockH, blockV );        
        this._internalChangeFlag = false;
        this._syncScrollBars();
      }
    },

    _syncClientArea : function( horz, vert ) {
      if( horz ) {
        var scrollX = this._horzScrollBar.getValue();
        this._clientArea.setScrollLeft( scrollX );
      }
      if( vert ) {
        var scrollY = this._vertScrollBar.getValue();
        this._clientArea.setScrollTop( scrollY );
      }
    },

    _syncScrollBars : function() {
      var scrollX = this._clientArea.getScrollLeft();
      this._horzScrollBar.setValue( scrollX );
      var scrollY = this._clientArea.getScrollTop();
      this._vertScrollBar.setValue( scrollY );
    }

  }
} );
