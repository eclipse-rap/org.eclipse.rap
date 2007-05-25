
/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
 
qx.Class.define( "org.eclipse.swt.custom.ScrolledComposite", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style ) {
    this.base( arguments );
    this._initialScrollLeft = null;
    this._lastScrollLeft = 0;
    this._lastScrollTop = 0;
    var req = org.eclipse.swt.Request.getInstance();
    req.addEventListener( "send", this._onRequestSend, this );
  },

  destruct : function() {
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "send", this._onRequestSend, this );
  },
  
  members : {

    setHBarSelection : function( value ) {
      if( !this.isMaterialized() || !this.isCreated() ) {
        this._initialScrollLeft = value;
        this.addEventListener( "create", this._onAppear );
      } else {
        this.setScrollLeft( value );
        this._lastScrollLeft = value;
      }
    },

    setVBarSelection : function( value ) {
      // this._materialize();
      // TODO [rst] the above line didn't seem to work:
      // An error when a SC on an invisible tab was created
      // --> copied code from setHBarSelection
      if( !this.isMaterialized() || !this.isCreated() ) {
        this._initialScrollTop = value;
        this.addEventListener( "create", this._onAppear );
      } else {
        this.setScrollTop( value );
        this._lastScrollTop = value;
      }
    },

    setVBarMaximum : function( value ) {
      this.setScrollHeight( value );
    },

    setHBarMaximum : function(value) {
      this.setScrollWidth(value);
    },

    // TODO [rst] remove (see comment above)
    _materialize : function() {
      if( !this.isMaterialized() || !this.isCreated() ) {
        qx.ui.core.Widget.flushGlobalQueues();
      }
    },

    _onAppear : function( evt ) {
      if( this._initialScrollLeft != null ) {
        this.setScrollLeft( this._initialScrollLeft );
        this._lastScrollLeft = this._initialScrollLeft;
      }
      if( this._initialScrollTop != null ) {
        this.setScrollLeft( this._initialScrollTop );
        this._lastScrollLeft = this._initialScrollTop;
      }
      this.removeEventListener( "create", this._onAppear );
    },


    /**
     * Creates request parameters that denote the current scroll position just 
     * before a request is sent.
     * This is a workaround, it seems that there is no 'scroll event'.
     */
    _onRequestSend : function( evt ) {
      if( this.isCreated() ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( this );
        var scrollX = this.getScrollLeft();
        if( scrollX != this._lastScrollLeft ) {
          evt.getTarget().addParameter( id + ".horizontalBar.selection", scrollX );
          this._lastScrollLeft = scrollX;
        }
        var scrollY = this.getScrollTop();
        if( scrollY != this._lastScrollTop ) {
          evt.getTarget().addParameter( id + ".verticalBar.selection", scrollY );
          this._lastScrollTop = scrollY;
        }
      }
    }
  }
});
