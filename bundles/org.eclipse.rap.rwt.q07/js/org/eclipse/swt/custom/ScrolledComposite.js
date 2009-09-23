/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
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
  extend : qx.ui.basic.ScrollArea,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "scrolledcomposite" );
    this._hasSelectionListener = false;
    // Flag indicates that the next request can be sent
    this._readyToSendChanges = true;
    this._showFocusedControl = false;
    this._blockScrolling = false;
    this._initialScrollTop = null;
    this._initialScrollLeft = null;
    this._lastScrollLeft = 0;
    this._lastScrollTop = 0;
    this.addEventListener( "appear", this._onAppear, this );
    this.addEventListener( "changeParent", this._onChangeParent, this );
    this.addEventListener( "mousewheel", this._onMouseWheel, this );
  },
  
  destruct : function() {
    this.removeEventListener( "appear", this._onAppear, this );
    this.removeEventListener( "changeParent", this._onChangeParent, this );
    this.removeEventListener( "mousewheel", this._onMouseWheel, this );
    this.getFocusRoot().removeEventListener( "changeFocusedChild",
                                             this._onChangeFocusedChild,
                                             this );
  },
  
  members : {
    
    _onAppear : function( evt ) {
      this.setScrollTop( this._lastScrollTop );
      this.setScrollLeft( this._lastScrollLeft );
      
    },
    
    _onChangeParent : function( evt ) {
      this.getFocusRoot().addEventListener( "changeFocusedChild",
                                            this._onChangeFocusedChild,
                                            this );
    },
    
    _onMouseWheel : function( evt ) {
      this._blockScrolling = false;
    },
    
    _onscroll : function( evt ) {
      this.base( arguments, evt );
      if( this._blockScrolling ) {
        this._blockScrolling = false;
        this.setScrollTop( this._lastScrollTop );
        this.setScrollLeft( this._lastScrollLeft );        
      } else if( this._readyToSendChanges ) {
        this._readyToSendChanges = false;
        // Send changes
        qx.client.Timer.once( this._sendChanges, this, 500 );
      }
    },
    
    _onChangeFocusedChild : function( evt ) {
      var focusedChild = this.getFocusRoot().getFocusedChild();
      this._blockScrolling = false;
      if( !this._showFocusedControl && focusedChild !== this ) {
        this._blockScrolling = this._contains( focusedChild );
      }
    },
    
    _contains : function( widget ) {
      var result = false;
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      if( widget != null && widgetManager.isControl( widget ) ) {
        var parent = widget.getParent();
        while(    parent != null
               && !( parent instanceof org.eclipse.swt.widgets.Shell )
               && !result ) {
          if( this === parent ) {
            result = true;
          }
          parent = parent.getParent();
        }
      }
      return result;
    },

    setHBarSelection : function( value ) {
      if( !this.isCreated() ) {
        this._initialScrollLeft = value;
        this.addEventListener( "create", this._setHBarSelectionOnCreate, this );
      } else {
        this.setScrollLeft( value );
        this._lastScrollLeft = value;
      }
    },

    setVBarSelection : function( value ) {
      if( !this.isCreated() ) {
        this._initialScrollTop = value;
        this.addEventListener( "create", this._setVBarSelectionOnCreate, this );
      } else {
        this.setScrollTop( value );
        this._lastScrollTop = value;
      }
    },

    _setHBarSelectionOnCreate : function( evt ) {
      if( this._initialScrollLeft != null ) {
        // Workaround: IE throws error when setting scrollLeft to a higher value
        // than scrollWidth. 
        if( this._initialScrollLeft <= this.getScrollWidth() ) {
          this.setScrollLeft( this._initialScrollLeft );
        }
        this._lastScrollLeft = this._initialScrollLeft;
      }
      this.removeEventListener( "create", this._setHBarSelectionOnCreate, this );
    },

    _setVBarSelectionOnCreate : function( evt ) {
      if( this._initialScrollTop != null ) {
        // Workaround: IE throws error when setting scrollTop to a higher value
        // than scrollHeight
        if( this._initialScrollTop <= this.getScrollHeight() ) {
          this.setScrollTop( this._initialScrollTop );
        }
        this._lastScrollTop = this._initialScrollTop;
      }
      this.removeEventListener( "create", this._setVBarSelectionOnCreate, this );
    },
    
    setShowFocusedControl : function( value ) {
      this._showFocusedControl = value;
    },
    
    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },
    
    _sendChanges : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && this.isCreated() ) {
        var hasChanges = false;
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = wm.findIdByWidget( this );
        var scrollX = this.getScrollLeft();
        if( scrollX != this._lastScrollLeft ) {
          req.addParameter( id + ".horizontalBar.selection", scrollX );
          this._lastScrollLeft = scrollX;
          hasChanges = true;
        }
        var scrollY = this.getScrollTop();
        if( scrollY != this._lastScrollTop ) {
          req.addParameter( id + ".verticalBar.selection", scrollY );
          this._lastScrollTop = scrollY;
          hasChanges = true;
        }
        if( this._hasSelectionListener && hasChanges ) {
          req.send();
        }
        this._readyToSendChanges = true;
      }
    }
  }
});
