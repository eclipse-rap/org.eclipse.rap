/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
 
qx.Class.define( "org.eclipse.swt.custom.ScrolledComposite", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this._content = null;
    this._hasSelectionListener = false;
    this._requestTimerRunning = false;
    this._showFocusedControl = false;
    this._focusRoot = null;
    this._blockScrolling = false;
    this._clientArea = new qx.ui.layout.CanvasLayout();
    this._horzScrollBar = new qx.ui.basic.ScrollBar( true );
    this._vertScrollBar = new qx.ui.basic.ScrollBar( false );
    this.add( this._clientArea );
    this.add( this._horzScrollBar );
    this.add( this._vertScrollBar );
    this._configureScrollBars();
    this._configureClientArea();
    this.addEventListener( "changeParent", this._onChangeParent, this );
    this.__onscroll = qx.lang.Function.bindEvent( this._onscroll, this );    
    this.setAppearance( "scrolledcomposite" );
  },
  
  destruct : function() {
    var el = this._clientArea.getElement();
    if( el ) {
      qx.html.EventRegistration.removeEventListener( el, 
                                                     "scroll", 
                                                     this.__onscroll );
      delete this.__onscroll;
    }
    this._clientArea.removeEventListener( "appear", 
                                          this._onClientAppear, 
                                          this );
    this._clientArea.removeEventListener( "mousewheel", 
                                          this._onMouseWheel, 
                                          this );
    this._clientArea.removeEventListener( "keypress", this._onKeyPress, this );
    this.removeEventListener( "changeParent", this._onChangeParent, this );
  },
  
  members : {
    
    /////////
    // Public

    setScrollBarsVisible : function( horizontal, vertical ) {
      this._horzScrollBar.setDisplay( horizontal );  
      this._vertScrollBar.setDisplay( vertical );
      this._layoutX();
      this._layoutY();
    },

    setHBarSelection : function( value ) {
      // TODO [tb] : the _internalChangeFlag is needed here and on
      // setVBarSelection only due to a very nasty IE-bug that fires the dom
      // scroll event onto the wrong target. Remove after re-implementing 
      // scroll-bar. 
      this._internalChangeFlag = true;
      if( this._horzScrollBar.getMaximum() < value ) {
        // TODO [tb] : The ScrollBar should do that itself
        this._horzScrollBar.setMaximum( value );
      }
      this._horzScrollBar.setValue( value );
      this._internalChangeFlag = false;
    },

    setVBarSelection : function( value ) {
      this._internalChangeFlag = true;
      if( this._vertScrollBar.getMaximum() < value ) {
        // TODO [tb] : The ScrollBar should do that itself
        this._vertScrollBar.setMaximum( value );
      }
      this._vertScrollBar.setValue( value );
      this._internalChangeFlag = false;
    },

    setShowFocusedControl : function( value ) {
      this._showFocusedControl = value;
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    setContent : function( widget ) {
      if( this._content != null ) {
        this._content.removeEventListener( "changeParent", 
                                          this._onContentRemove, 
                                          this );
        this._content.removeEventListener( "changeWidth", 
                                          this._onContentResize, 
                                          this );
        this._content.removeEventListener( "changeHeight", 
                                          this._onContentResize, 
                                          this );
      }
      this._content = widget;
      this._onContentResize();
      if( this._content != null ) {
        this._clientArea.add( this._content );
        this._content.addEventListener( "changeParent", 
                                        this._onContentRemove, 
                                        this );
        this._content.addEventListener( "changeWidth", 
                                        this._onContentResize, 
                                        this );
        this._content.addEventListener( "changeHeight", 
                                        this._onContentResize, 
                                        this );
      }                             
    },

    ////////////////
    // Configuration
    
    _configureScrollBars : function() {
      var dragBlocker = function( event ) { event.stopPropagation(); };
      var preferredWidth = this._vertScrollBar.getPreferredBoxWidth()
      var preferredHeight = this._horzScrollBar.getPreferredBoxHeight();
      this._horzScrollBar.setLeft( 0 );
      this._horzScrollBar.setHeight( preferredHeight );
      this._horzScrollBar.addEventListener( "dragstart", dragBlocker );
      this._vertScrollBar.setTop( 0 );
      this._vertScrollBar.setWidth( preferredWidth );
      this._vertScrollBar.addEventListener( "dragstart", dragBlocker );
      this._horzScrollBar.addEventListener( "changeValue", 
                                            this._onHorzScrollBarChangeValue, 
                                            this );
      this._vertScrollBar.addEventListener( "changeValue", 
                                            this._onVertScrollBarChangeValue, 
                                            this );      
    },
    
    _configureClientArea : function() {
      this._clientArea.setOverflow( "scroll" );
      this._clientArea.setLeft( 0 );
      this._clientArea.setTop( 0 );
      this._clientArea.addEventListener( "create", this._onClientCreate, this );
      this._clientArea.addEventListener( "appear", this._onClientAppear, this );
      this._clientArea.addEventListener( "mousewheel", this._onMouseWheel, this );
      this._clientArea.addEventListener( "keypress", this._onKeyPress, this );
      // TOOD [tb] : Do this with an eventlistner after fixing Bug 327023 
      this._clientArea._layoutPost 
        = qx.lang.Function.bindEvent( this._onClientLayout, this );
    },    
    
    ///////////////
    // Eventhandler
    
    _onContentRemove : function() {
      this.setContent( null ); 
    },
    
    _onContentResize : function() {
      if(    this._content !== null 
          && typeof this._content.getWidth() === "number"  
          && typeof this._content.getHeight() === "number" ) 
      {
        var maxWidth = this._content.getWidth();
        var maxHeight = this._content.getHeight();
        this._horzScrollBar.setMaximum( maxWidth );
        this._vertScrollBar.setMaximum( maxHeight );
      }
    },
    
    _onHorzScrollBarChangeValue : function() {
      if( this._isCreated ) {
        this._syncClientArea( true, false );
      }
    }, 

    _onVertScrollBarChangeValue : function() {
      if( this._isCreated ) {
        this._syncClientArea( false, true );
      }      
    },

    _onClientAppear : function() {
      this._syncClientArea( true, true );
    },
    
    _onClientCreate : function( evt ) {
      this._clientArea.prepareEnhancedBorder();
      var el = this._clientArea._getTargetNode();
      qx.html.EventRegistration.addEventListener( el, 
                                                  "scroll", 
                                                  this.__onscroll );
    },
    
    _onClientLayout : function() {
      // TODO [tb] : _getScrollBarWidth should be a static function, it has 
      // nothing to do with the instance, which here is only used to call it
      var barWidth = this._horzScrollBar._getScrollBarWidth();
      var node = this._clientArea._getTargetNode();
      var el = this._clientArea.getElement();
      node.style.width = parseInt( el.style.width ) + barWidth;
      node.style.height = parseInt( el.style.height ) + barWidth;      
    },
    
    _onChangeParent : function( evt ) {
      if( this._focusRoot != null ) {
        this._focusRoot.removeEventListener( "changeFocusedChild",
                                             this._onChangeFocusedChild,
                                             this );
      }
      this._focusRoot = this.getFocusRoot();
      if( this._focusRoot != null ) {
        this._focusRoot.addEventListener( "changeFocusedChild",
                                          this._onChangeFocusedChild,
                                          this );
      }
    },
    
    _onMouseWheel : function( evt ) {
      this._blockScrolling = false;
    },
    
    _onKeyPress : function( evt ) {
      switch( evt.getKeyIdentifier() ) {
        case "Left":
        case "Up":
        case "Right":
        case "Down":
        case "PageUp":
        case "PageDown":
        case "End":
        case "Home":
          this._blockScrolling = false;
          evt.stopPropagation();
      }
    },
    
    _onscroll : function( evt ) {
      org.eclipse.rwt.EventHandlerUtil.stopDomEvent( evt );
      if( this._blockScrolling ) {
        this._syncClientArea( true, true );        
      } else {
        this._syncScrollBars();
        if( !this._requestTimerRunning ) {
          this._requestTimerRunning = true;
          qx.client.Timer.once( this._sendChanges, this, 500 );
        }
      }
    },
    
    _onChangeFocusedChild : function( evt ) {
      var focusedChild = this.getFocusRoot().getFocusedChild();
      this._blockScrolling = !this._showFocusedControl && focusedChild !== this;
    },
    
    //////////
    // Syncing

    _syncClientArea : function( horz, vert ) {
      this._internalChangeFlag = true;
      if( horz ) {
        var scrollX = this._horzScrollBar.getValue();
        this._clientArea.setScrollLeft( scrollX );
      }
      if( vert ) {
        var scrollY = this._vertScrollBar.getValue();
        this._clientArea.setScrollTop( scrollY );
      }
      this._internalChangeFlag = false;
    },

    _syncScrollBars : function() {
      if( !this._internalChangeFlag ) {
        var scrollX = this._clientArea.getScrollLeft();
        this._horzScrollBar.setValue( scrollX );
        var scrollY = this._clientArea.getScrollTop();
        this._vertScrollBar.setValue( scrollY );
      }
    },

    _sendChanges : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && this.isCreated() ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = wm.findIdByWidget( this );
        var scrollX = this._clientArea.getScrollLeft();
        req.addParameter( id + ".horizontalBar.selection", scrollX );
        var scrollY = this._clientArea.getScrollTop();
        req.addParameter( id + ".verticalBar.selection", scrollY );
        if( this._hasSelectionListener ) {
          req.send();
        }
        this._requestTimerRunning = false;
      }
    },

    /////////
    // Layout

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
      this._horzScrollBar.setWidth( clientWidth );
    },

    _layoutY : function() {        
      var clientHeight = this.getHeight() - this.getFrameHeight();
      if( this._horzScrollBar.getDisplay() ) {
        clientHeight -= this._horzScrollBar.getHeight();
      } 
      this._clientArea.setHeight( clientHeight );
      this._vertScrollBar.setHeight( clientHeight );
      this._horzScrollBar.setTop( clientHeight );
    }

  }
} );
