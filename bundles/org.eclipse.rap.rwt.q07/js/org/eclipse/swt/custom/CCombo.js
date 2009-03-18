/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

/**
 * This class provides the client-side counterpart for
 * org.eclipse.swt.custom.CCombo.
 */
qx.Class.define( "org.eclipse.swt.custom.CCombo", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style ) {
    this.base( arguments );
    this.setAppearance( "ccombo" );
    //
    this._hasSelectionListener = false;
    this._hasVerifyModifyListener = false;
    this._isModified = false;
    // Default values
    this._selected = null;
    this._editable = true;
    this._dropped = false;
    this._borderWidth = 0;
    // Text field
    this._field = new qx.ui.form.TextField();
    this._field.setAppearance( "ccombo-field" );
    this._field.setAllowStretchY( true );
    this.add( this._field );
    // Drop down button
    this._button = new qx.ui.form.Button();
    this._button.setAppearance( "ccombo-button" );
    this._button.setTabIndex( -1 );
    this._button.setAllowStretchY( true );
    this.add( this._button );
    // List
    this._list = new qx.ui.form.List();
    this._list.setAppearance( "ccombo-list" );
    this._list.setTabIndex( -1 );
    this._list.setVisibility( false );
    this._list.setHeight( "auto" );
    this.add( this._list );
    // List Manager
    this._manager = this._list.getManager();
    this._manager.setMultiSelection( false );
    this._manager.setDragSelection( false );
    // Add events listeners
    var cDocument = qx.ui.core.ClientDocument.getInstance();
    cDocument.addEventListener( "windowblur", this._onBlurCloseList, this );
    // Init events
    this.addEventListener( "appear", this._onAppear, this );
    this.addEventListener( "changeWidth", this._onChangeSize, this );
    this.addEventListener( "changeHeight", this._onChangeSize, this );
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "changeFont", this._onChangeFont, this );
    this.addEventListener( "changeTextColor", this._onChangeTextColor, this );
    this.addEventListener( "changeBackgroundColor",
                           this._onChangeBackgoundColor, this );
    // Mouse events
    this.addEventListener( "mousedown", this._onMouseDown, this );
    this.addEventListener( "mouseup", this._onMouseUp, this );
    this.addEventListener( "click", this._onMouseClick, this );
    this.addEventListener( "mousewheel", this._onMouseWheel, this );
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
    // Keyboard events
    this.addEventListener( "keydown", this._onKeyDown );
    this.addEventListener( "keypress", this._onKeyPress );
    this.addEventListener( "keyinput", this._onKeyInput );
    // Specific events
    this._field.addEventListener( "blur", this._onTextBlur, this );
    this._list.addEventListener( "appear", this._onListAppear, this );
  },

  destruct : function() {
    var cDocument = qx.ui.core.ClientDocument.getInstance();
    cDocument.removeEventListener( "windowblur", this._onBlurCloseList, this );
    this.removeEventListener( "appear", this._onAppear, this );
    this.removeEventListener( "changeWidth", this._onChangeSize, this );
    this.removeEventListener( "changeHeight", this._onChangeSize, this );
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "changeFont", this._onChangeFont, this );
    this.removeEventListener( "changeTextColor", this._onChangeTextColor, this );
    this.removeEventListener( "changeBackgroundColor",
                              this._onChangeBackgoundColor, this );
    this.removeEventListener( "mousedown", this._onLineMouseDown, this );
    this.removeEventListener( "mouseup", this._onMouseUp, this );
    this.removeEventListener( "click", this._onMouseClick, this );
    this.removeEventListener( "mousewheel", this._onMouseWheel, this );
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
    this.removeEventListener( "keydown", this._onKeyDown );
    this.removeEventListener( "keypress", this._onKeyPress );
    this.removeEventListener( "keyinput", this._onKeyInput );
    this._field.removeEventListener( "blur", this._onTextBlur, this );
    this._list.removeEventListener( "appear", this._onListAppear, this );
    this._disposeObjects( "_field", 
                          "_button", 
                          "_list", 
                          "_manager", 
                          "_selected" );
  },

  statics : {
    BUTTON_WIDTH : 14
  },

  members : {
    _onChangeSize : function( evt ) {
      this._list.setWidth( this.getWidth() );
      this._list.setTop( this.getHeight() - this._borderWidth );
      this._list.setLeft( - this._borderWidth );
    },

    _onAppear : function( evt ) {
      var ccombo = this.getElement();
      var field = this._field.getElement();
      var leftCcombo = qx.html.Location.getPageBoxLeft( ccombo );
      var leftField = qx.html.Location.getPageBoxLeft( field );
      this._borderWidth = leftField - leftCcombo;
      this._list.setTop( this.getHeight() - this._borderWidth );
      this._list.setLeft( - this._borderWidth );
      if( this.hasState( "rwt_FLAT" ) ) {
        this._field.addState( "rwt_FLAT" );
        this._button.addState( "rwt_FLAT" );
        this._list.addState( "rwt_FLAT" );
      }
    },

    _onContextMenu : function( evt ) {
      var menu = this.getContextMenu();
      if( menu != null ) {
        menu.setLocation( evt.getPageX(), evt.getPageY() );
        menu.setOpener( this );
        menu.show();
        evt.stopPropagation();
      }
    },

    _onChangeFont : function( evt ) {
      var value = evt.getValue();
      this._field.setFont( value );
      var items = this._list.getChildren();
      for( var i = 0; i < items.length; i++ ) {
        items[ i ].setFont( value );
      }
    },

    _onChangeTextColor : function( evt ) {
      var value = evt.getValue();
      this._field.setTextColor( value );
      this._list.setTextColor( value );
    },

    _onChangeBackgoundColor : function( evt ) {
      var value = evt.getValue();
      this._field.setBackgroundColor( value );
      this._list.setBackgroundColor( value );
    },

    _applyCursor : function( value, old ) {
      this.base( arguments, value, old );
      this._userCursor = value;
      if( value ) {
        this._field.setCursor( value );
        this._button.setCursor( value );
        this._list.setCursor( value );
      } else {
        this._field.resetCursor();
        this._button.resetCursor();
        this._list.resetCursor();
      }
    },
    
    _setSelected : function( value ) {
      this._selected = value;
      this._manager.setLeadItem( value );
      this._manager.setAnchorItem( value );
      if( value ) {
        var fieldValue = value.getLabel().toString();
        this._field.setValue( fieldValue );
        this._field._visualizeFocus();
        if( this._field.isCreated() ) {
          this._field.selectAll();
        }
        this._manager.setSelectedItem( value );
        this._manager.scrollItemIntoView( value );
      } else {
        this._manager.deselectAll();
      }
      this._sendWidgetSelected();
    },

    _toggleListVisibility : function() {
      if( !this._dropped ) {
        if( this._editable ) {
          this._field.setReadOnly( true );
        }
        // Brings this widget on top of the others with same parent.
        this._bringToFront();
        this.setCapture( true );
      } else {
        if( this._editable ) {
          this._field.setReadOnly( false );
        }
        this.setCapture( false );
      }
      this._list.setVisibility( !this._dropped );
      this._dropped = !this._dropped;
      this._updateListVisibleRequestParam();
    },

    _onListAppear : function( evt ) {
      if( this._selected ) {
        this._selected.scrollIntoView();
      }
    },

    _bringToFront : function() {
      var someObject, vHashCode;
      var allWidgets = this.getParent().getChildren();
      for( vHashCode in allWidgets ) {
        someObject = allWidgets[vHashCode];
        if( someObject.getZIndex ) {
          if( this.getZIndex() < someObject.getZIndex() ) {
            tmpZIndex = this.getZIndex();
            this.setZIndex( someObject.getZIndex() );
            someObject.setZIndex( tmpZIndex );
          }
        }
      }
    },

    // Mouse events handling
    _onMouseDown : function( evt ) {
      if( evt.isLeftButtonPressed() ) {
        var vTarget = evt.getTarget();
        if( vTarget == this._field ) {
          if( !this._editable || this._dropped ) {
            this._toggleListVisibility();
          }
        }
        evt.stopPropagation();
      }
    },

    _onMouseClick : function( evt ) {
      if( evt.isLeftButtonPressed() ) {
        var vTarget = evt.getTarget();
        switch( vTarget ) {
          case this:
          case this._field:
          case this._list:
            break;
          case this._button:
            this._toggleListVisibility();
            break;
          default:
            if(    vTarget instanceof qx.ui.form.ListItem 
                && vTarget.getParent() === this._list ) 
            {
              this._list._onmousedown( evt );
              this._setSelected( this._list.getSelectedItem() );
              this._toggleListVisibility();
              this.setFocused( true );
            } else if( this._dropped ) {
              // Click is outside the CCombo
              if( this._selected ) {
                this._manager.setLeadItem( this._selected );
                this._manager.setAnchorItem( this._selected );
                this._manager.setSelectedItem( this._selected );
              } else {
              	this._manager.deselectAll();
              	this._manager.setLeadItem( null );
                this._manager.setAnchorItem( null );
              }
              this._toggleListVisibility();
            }
        }
      }
      if( this._button.hasState( "over" ) ) {
        this._button.removeState( "over" );
      }
    },

    _onMouseUp : function( evt ) {
      if( !this._dropped ) {
        this.setCapture( false );
      }
    },

    _onMouseWheel : function( evt ) {
      if( !this._dropped ) {
        var toSelect;
        var isSelected = this._selected;
        if( evt.getWheelDelta() < 0 ) {
          toSelect =   isSelected
                     ? this._manager.getNext( isSelected )
                     : this._manager.getFirst();
        } else {
          toSelect =   isSelected
                     ? this._manager.getPrevious( isSelected )
                     : this._manager.getLast();
        }
        if( toSelect ) {
          this._setSelected( toSelect );
        }
      }
    },
    
    _onMouseOver : function( evt ) {
      var vTarget = evt.getTarget();
      if( vTarget instanceof qx.ui.form.ListItem ) {
        this._manager.deselectAll();
        this._manager.setLeadItem( vTarget );
        this._manager.setAnchorItem( vTarget );
        this._manager.setSelectedItem( vTarget );
      } else if( vTarget == this._button ) {
      	this._button.addState( "over" );
      }
    },
    
    _onMouseOut : function( evt ) {
      var vTarget = evt.getTarget();
      if( vTarget == this._button ) {
        this._button.removeState( "over" );
      }
    },

    // Keyboard events handling
    _onKeyDown : function( evt ) {
      switch( evt.getKeyIdentifier() ) {
        // Handle <ENTER>, <ESC>
        case "Enter":
        case "Escape":
          if( this._dropped ) {
            this._setSelected( this._manager.getSelectedItem() );
            this._toggleListVisibility();
          }
          evt.stopPropagation();
          break;
        // Handle Alt+Down, Alt+Up
        case "Down":
        case "Up":
          if( evt.isAltPressed() ) {
            this._toggleListVisibility();
          }
          break;
      }
    },

    _onKeyPress : function( evt ) {
      switch( evt.getKeyIdentifier() ) {
        case "Escape":
          evt.stopPropagation();
          break;
        case "Right":
          if( this._dropped ) {
            var toSelect =   this._selected
                           ? this._manager.getNext( this._selected )
                           : this._manager.getFirst();
            if( toSelect ) {
              this._setSelected( toSelect );
            }
          }
          break;
        case "Left":
          if( this._dropped ) {
            var toSelect =   this._selected
                           ? this._manager.getPrevious( this._selected )
                           : this._manager.getLast();
            if( toSelect ) {
              this._setSelected( toSelect );
            }
          }
          break;
      }
      // Default handling
      if(    !this._editable
          || this._dropped
          || ( this._editable && evt.getKeyIdentifier() == "Up" )
          || ( this._editable && evt.getKeyIdentifier() == "Down" ) ) 
      {
        this._list._onkeypress( evt );
        var selected = this._manager.getSelectedItem();
        this._setSelected( selected );
      } else if( this._editable && this._isModifyingKey( evt.getKeyIdentifier() ) ) 
      {
        this._setSelected( null );
        if( !org_eclipse_rap_rwt_EventUtil_suspend && !this._isModified ) {
          this._isModified = true;
          var req = org.eclipse.swt.Request.getInstance();
          req.addEventListener( "send", this._onSend, this );
          if( this._hasVerifyModifyListener ) {
            qx.client.Timer.once( this._sendModifyText, this, 500 );
          }
        }
      }
    },

    _onKeyInput : function( evt ) {
      if( this._dropped ) {
        this._list._onkeyinput( evt );
        var selected = this._manager.getSelectedItem();
        this._setSelected( selected );
      }
    },

    // On "windowblur" event: closes the list, if it is seeable.
    _onBlurCloseList : function() {
      if( this._dropped ) {
        this._toggleListVisibility();
      }
    },

    // Actions, connected with server communication
    _onTextBlur : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && this._isModified ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.modifyText", id );
        req.send();
      }
    },

    _onSend : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".text", this._field.getComputedValue() );
      req.removeEventListener( "send", this._onSend, this );
      this._isModified = false;
      this.setValue( this._field.getComputedValue() );
    },

    _sendModifyText : function() {
      if( this._isModified ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.modifyText", id );
        req.send();
        this._isModified = false;
      }
    },

    _sendWidgetSelected : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var list = this._list;
        var listItem = this._list.getSelectedItem();
        req.addParameter( id + ".selectedItem", list.indexOf( listItem ) );
        if( this._hasSelectionListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          req.send();
        }
      }
    },
    
    _updateListVisibleRequestParam : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        req.addParameter( id + ".listVisible", this._list.getVisibility() );
      }
    },

    _isModifyingKey : function( keyIdentifier ) {
      var result = false;
      switch( keyIdentifier ) {
        // Modifier keys
        case "Shift":
        case "Control":
        case "Alt":
        case "Meta":
        case "Win":
        // Navigation keys
        case "Left":
        case "Right":
        case "Home":
        case "End":
        case "PageUp":
        case "PageDown":
        case "Tab":
        // Context menu key
        case "Apps":
        //
        case "Escape":
        case "Insert":
        case "Enter":
        //
        case "CapsLock":
        case "NumLock":
        case "Scroll":
        case "PrintScreen":
        // Function keys 1 - 12
        case "F1":
        case "F2":
        case "F3":
        case "F4":
        case "F5":
        case "F6":
        case "F7":
        case "F8":
        case "F9":
        case "F10":
        case "F11":
        case "F12":
          break;
        default:
          result = true;
      }
      return result;
    },

    // Set methods
    setItems : function( items ) {
      this._list.removeAll();
      for( var i = 0; i < items.length; i++ ) {
        var item = new qx.ui.form.ListItem();
        item.setLabel( "(empty)" );
        item.getLabelObject().setMode( "html" );
        item.setLabel( items[ i ] );
        item.setFont( this.getFont() );
        this._list.add( item );
      }
    },

    setMaxListHeight : function( value ) {
      this._list.setMaxHeight( value );
    },

    select : function( index ) {
      var items = this._list.getChildren();
      var item = null;
      if( index >= 0 && index <= items.length - 1 ) {
        item = items[ index ];
      }
      this._setSelected( item );
    },

    setEditable : function( value ) {
      this._editable = value;
      this._field.setReadOnly( !value );
      this._field.setCursor( value ? null : "default" );
    },
    
    setListVisible : function( value ) {
      if( this._list.setVisibility != value ) {
        this._dropped = !value;
        this._toggleListVisibility();
      }
    },

    setValue : function( value ) {
      this._field.setValue( value );
    },
    
    setTextSelection : function( start, length ) {
      if( this._field.isCreated() ) {
        this._field.setSelectionStart( start );
        this._field.setSelectionLength( length );
      }
    },
    
    setTextLimit : function( value ) {
      this._field.setMaxLength( value );
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    setHasVerifyModifyListener : function( value ) {
      this._hasVerifyModifyListener = value;
    }
  }
} );
