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
  extend : org.eclipse.swt.widgets.Scrollable,

  construct : function() {
    this.base( arguments, new qx.ui.layout.CanvasLayout() );
    this._clientArea.addEventListener( "mousewheel", this._onMouseWheel, this );
    this._clientArea.addEventListener( "keypress", this._onKeyPress, this );
    this.addEventListener( "userScroll", this._onUserScroll );
    this._content = null;
    this._hasSelectionListener = false;
    this._requestTimerRunning = false;
    this._showFocusedControl = false;
    this._focusRoot = null;
    this.addEventListener( "changeParent", this._onChangeParent, this );
    this.setAppearance( "scrolledcomposite" );
  },
  
  members : {
    
    /////////
    // Public

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
      this.setBlockScrolling( false );
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
          this.setBlockScrolling( false );
          evt.stopPropagation();
        break;
      }
    },
    
    _onUserScroll : function() {
      if( !this._requestTimerRunning ) {
        this._requestTimerRunning = true;
        qx.client.Timer.once( this._sendChanges, this, 500 );
      }
    },
    
    _onChangeFocusedChild : function( evt ) {
      var focusedChild = this.getFocusRoot().getFocusedChild();
      this.setBlockScrolling(    !this._showFocusedControl 
                              && focusedChild !== this );
    },

    _sendChanges : function() {
      if( !org.eclipse.swt.EventUtil.getSuspended() && this.isCreated() ) {
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
    }

  }
} );
