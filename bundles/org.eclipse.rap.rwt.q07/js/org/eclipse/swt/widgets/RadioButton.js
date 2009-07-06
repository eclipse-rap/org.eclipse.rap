/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/

/**
 * This class provides the client-side implementation for
 * org.eclipse.swt.widgets.Button widget with SWT.RADIO style.
 */
qx.Class.define( "org.eclipse.swt.widgets.RadioButton", {
  extend : qx.ui.layout.HorizontalBoxLayout,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "radio-button" );
    this.setVerticalChildrenAlign( "middle" );
    
    // Default values
    this._selection = false;
    this._text = "";
    this._image = null;
    this._hasSelectionListener = false;
    
    // RadioButton icon
    this._icon = new qx.ui.basic.Image;
    this._icon.setAppearance( "radio-button-icon" );
    this.add( this._icon );
    
    // RadioButton content - image and text
    this._content = new qx.ui.basic.Atom( "(empty)", this._image );
    this._content.getLabelObject().setAppearance( "label-graytext" );
    this._content.setLabel( this._text );
    this._content.setHorizontalChildrenAlign( "center" );
    this._content.setVerticalChildrenAlign( "middle" );
    this.add( this._content );

    // Add events listeners
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "click", this._onclick );
    this.addEventListener( "mouseover", this._onmouseover );
    this.addEventListener( "mouseout", this._onmouseout );
    this.addEventListener( "keyup", this._onkeyup );
    this.addEventListener( "keypress", this._onkeypress );
  },
  
  destruct : function() {
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "click", this._onclick );
    this.removeEventListener( "mouseover", this._onmouseover );
    this.removeEventListener( "mouseout", this._onmouseout );
    this.removeEventListener( "keyup", this._onkeyup );
    this.removeEventListener( "keypress", this._onkeypress );
    this._disposeObjects( "_icon", "_content" );
  },  
  
  members : {
    _onContextMenu : function( evt ) {
      var menu = this.getContextMenu();      
      if( menu != null ) {
        menu.setLocation( evt.getPageX(), evt.getPageY() );
        menu.setOpener( this );
        menu.show();
        evt.stopPropagation();
      }
    },
    
    _applyCursor : function( value, old ) {
      this.base( arguments, value, old );
      if( value ) {
        this._content.setCursor( value );
      } else {
        this._content.resetCursor();
      }
    },

    _sendChanges : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && this._hasSelectionListener ) 
      {
        org.eclipse.swt.Request.getInstance().send();
      }
    },

    // Set the "checked" property and focus on the following (next or previous)
    // radio button of the same group, after a corresponding key press.
    _setNextOrPrevious : function( command ) {
      // array of all radio buttons from this group
      var allRb = new Array();
      // the index of the current radio button in allRb-array
      var currentRbIndex;
      // the index of the next selected radio button in allRb-array
      var nextSelectedRbIndex;
      var parent = this.getParent();
      var siblings = parent.getChildren();
      for( var i = 0; i < siblings.length; i++ ) {
        if( siblings[ i ].classname == this.classname ) {
          allRb.push( siblings[ i ] );
        }
      }
      for( var j = 0; j < allRb.length; j++ ) {
        if( allRb[ j ] == this ) {
          currentRbIndex = j;
        }
      }
      // assign a value to 'nextSelectedRbIndex', 
      // in case the 'command' is unrecognizable
      nextSelectedRbIndex = currentRbIndex;
      if ( command == "next" ) {
        nextSelectedRbIndex = currentRbIndex + 1;
        if( nextSelectedRbIndex >= allRb.length ) {
          nextSelectedRbIndex = 0;
        }
      }
      if ( command == "previous" ) {
        nextSelectedRbIndex = currentRbIndex - 1;
        if( nextSelectedRbIndex < 0 ) {
          nextSelectedRbIndex = allRb.length - 1;
        }
      }
      allRb[ nextSelectedRbIndex ].setSelection( true );
      allRb[ nextSelectedRbIndex ].setFocused( true );
    },

    // Event listeners
    _onclick : function( evt ) {
    	this.setSelection( true );
    	this._sendChanges();
    },
    
    _onmouseover : function( evt ) {
      this._icon.addState( "over" );
      this.addState( "over" );
    },
    
    _onmouseout : function( evt ) {
      this._icon.removeState( "over" );
      this.removeState( "over" );
    },
    
    // If "Space" is pressed, the property "checked" is set to true
    _onkeyup : function( evt ) {
      if ( evt.getKeyIdentifier() == "Space" ) {
    	  this.setSelection( true );
    	  this._sendChanges();
      }
    },
    
    // If "Left"-key or "Up"-key is pressed, previous RadioButton is selected.
    // If "Right"-key or "Down"-key is pressed, next RadioButton is selected.
    _onkeypress : function( evt ) {
      switch( evt.getKeyIdentifier() ) {
        case "Left":
        case "Up":
          this._setNextOrPrevious( "previous" );
          this._sendChanges();
          break;
        case "Right":
        case "Down":
          this._setNextOrPrevious( "next" );
          this._sendChanges();
          break;
      }
    },
    
    // Set-functions
    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },
    
    setLabel : function( value ) {
      this._text = value;
      this._content.setLabel( value );
    },
    
    setIcon : function( value ) {
      this._image = value;
      this._content.setIcon( value );
    },
    
    setHorizontalChildrenAlign : function( value ) {
      this._content.setHorizontalChildrenAlign( value );
    },
    
    setSelection : function( value ) {
      if( this._selection !== value ) {
        this._selection = value;
        if( this._selection ) {
          this._icon.addState( "selected" );
          this.addState( "selected" );
          this._unselectSiblings();
        } else {
          this._icon.removeState( "selected" );
          this.removeState( "selected" );
        }
        if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addParameter( id + ".selection", this._selection );
        }
      }
    },
    
    _unselectSiblings : function() {
      var parent = this.getParent();
      var siblings = parent.getChildren();
      for( var i = 0; i < siblings.length; i++ ) {
        if( siblings[ i ] != this && siblings[ i ].classname === this.classname )
        {
          siblings[ i ].setSelection( false );
        }
      }
    }
  
  }
} );
