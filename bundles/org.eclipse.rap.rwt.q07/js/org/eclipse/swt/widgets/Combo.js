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
 * org.eclipse.swt.widget.Combo and org.eclipse.swt.custom.CCombo.
 */
qx.Class.define( "org.eclipse.swt.widgets.Combo", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style ) {
    this.base( arguments );
    // Get style
    this._ccombo = false;
    if( style ) {
      this._ccombo = qx.lang.String.contains( style, "ccombo" );
    }
    //
    this._hasSelectionListener = false;
    this._hasVerifyModifyListener = false;
    this._isModified = false;
    // Default values
    this._selected = null;
    this._editable = true;
    this._dropped = false;
    this._borderWidth = 0;
    this._selectionStart = 0;
    this._selectionLength = 0;
    this._listItemHeight = "auto";
    // Text field
    this._field = new qx.ui.form.TextField();
    this._field.setTabIndex( -1 );
    this._field.setAllowStretchY( true );
    this.add( this._field );
    // Drop down button
    this._button = new qx.ui.form.Button();
    this._button.setTabIndex( -1 );
    this._button.setAllowStretchY( true );
    this.add( this._button );
    // List
    this._list = new qx.ui.form.List();
    this._list.setTabIndex( -1 );
    this._list.setDisplay( false );
    // List Manager
    this._manager = this._list.getManager();
    this._manager.setMultiSelection( false );
    this._manager.setDragSelection( false );
    // Do not visualize the focus rectangle around the widget
    this.setHideFocus( true );
    // Add events listeners
    var cDocument = qx.ui.core.ClientDocument.getInstance();
    cDocument.addEventListener( "windowblur", this._onBlur, this );
    // Set appearance
    if( this._ccombo ) {
      this.setAppearance( "ccombo" );
      this._field.setAppearance( "ccombo-field" );
      this._button.setAppearance( "ccombo-button" );
      this._list.setAppearance( "ccombo-list" );
    } else {
      this.setAppearance( "combo" );
      this._field.setAppearance( "combo-field" );
      this._button.setAppearance( "combo-button" );
      this._list.setAppearance( "combo-list" );
    }
    // Init events
    this.addEventListener( "appear", this._onAppear, this );
    this.addEventListener( "focusin", this._onFocusIn, this );
    this.addEventListener( "changeWidth", this._onChangeSize, this );
    this.addEventListener( "changeHeight", this._onChangeSize, this );
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "changeFont", this._onChangeFont, this );
    this.addEventListener( "changeTextColor", this._onChangeTextColor, this );
    this.addEventListener( "changeBackgroundColor",
                           this._onChangeBackgroundColor, 
                           this );
    this.addEventListener( "changeVisibility", this._onChangeVisibility, this );
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
    cDocument.removeEventListener( "windowblur", this._onBlur, this );
    this.removeEventListener( "appear", this._onAppear, this );
    this.removeEventListener( "focusin", this._onFocusIn, this );
    this.removeEventListener( "changeWidth", this._onChangeSize, this );
    this.removeEventListener( "changeHeight", this._onChangeSize, this );
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "changeFont", this._onChangeFont, this );
    this.removeEventListener( "changeTextColor", this._onChangeTextColor, this );
    this.removeEventListener( "changeBackgroundColor",
                              this._onChangeBackgroundColor, 
                              this );
    this.removeEventListener( "changeVisibility", this._onChangeVisibility, this );
    this.removeEventListener( "mousedown", this._onMouseDown, this );
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
    // Solution taken from Qooxdoo implementation of ComboBox
    // in order to prevent memory leak and other problems.
    if( this._list && !qx.core.Object.inGlobalDispose() ) {
      this._list.setParent( null );
    }
    this._disposeObjects( "_field", 
                          "_button", 
                          "_list", 
                          "_manager", 
                          "_selected" );
  },

  members : {

    addState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._field.addState( state );
        this._list.addState( state );
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._field.removeState( state );
        this._list.removeState( state );
      }
    },

    _onChangeSize : function( evt ) {
      this._list.setWidth( this.getWidth() );
      this._setListLocation();
    },

    _onAppear : function( evt ) {
      if( this._ccombo && this.hasState( "rwt_FLAT" ) ) {
        this._field.addState( "rwt_FLAT" );
        this._button.addState( "rwt_FLAT" );
        this._list.addState( "rwt_FLAT" );
      }
      this.getTopLevelWidget().add( this._list );
      this._setListLocation();
      org.eclipse.swt.TextUtil._updateLineHeight( this._field );
    },
    
    _onFocusIn : function( evt ) {
      if(    this._field.isCreated()
          && !org_eclipse_rap_rwt_EventUtil_suspend )
      {
        this._handleSelectionChange();
      }
    },

    _onContextMenu : function( evt ) {
      var menu = this.getContextMenu();
      if( menu != null && !this._dropped ) {
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
      org.eclipse.swt.TextUtil._updateLineHeight( this._field );
    },

    _onChangeTextColor : function( evt ) {
      var value = evt.getValue();
      this._field.setTextColor( value );
      this._list.setTextColor( value );
    },

    _onChangeBackgroundColor : function( evt ) {
      var color = evt.getValue();
      this._field.setBackgroundColor( color );
      // Ensure that the list is never transparent (see bug 282540)
      if( color != null ) {
        this._list.setBackgroundColor( color );
      } else {
        this._list.resetBackgroundColor();
      }
    },
    
    _onChangeVisibility : function( evt ) {
      var value = evt.getValue();
      if( !value && this._dropped ) {
        this._toggleListVisibility();
      }
    },
    
    _applyCursor : function( value, old ) {
      this.base( arguments, value, old );
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
    
    // Focus handling methods
    _visualizeFocus : function() {
      if( this._field.isCreated() ) {
        this._field._visualizeFocus();
      }
    },
    
    // Override of the _ontabfocus method from qx.ui.core.Widget
    _ontabfocus : function() {
      if( this._field.isCreated() ) {
        this._field.selectAll();
      }
    },
    
    _visualizeBlur : function() {
      if( this._field.isCreated() ) {
        // setting selection lenght to 0 needed for IE to deselect text
        this._field.setSelectionLength( 0 );
        this._field._visualizeBlur();
      }
    },
    
    // On "blur" or "windowblur" event: closes the list, if it is seeable
    _onBlur : function( evt ) {
      if( this._dropped ) {
        this._toggleListVisibility();
      }
    },

    ///////////////////////////////////////
    // List and list-items handling methods

    _setListLocation : function() {
      if( this.getElement() ){
        var elementPos = qx.bom.element.Location.get( this.getElement() );
        this._list.setLocation( elementPos.left,
                                elementPos.top + this.getHeight() );
      }
    },
    
    _toggleListVisibility : function() {
      if( this._list.getChildrenLength() ) {
        // Temporary make the text field ReadOnly, when the list is dropped.
        if( this._editable ) {
          this._field.setReadOnly( !this._dropped  );
        }
        if( !this._dropped ) {
          // Brings this widget on top of the others with same parent.
          this._bringToFront();
          this.setCapture( true );
          this._setListLocation();
        } else {
          this.setCapture( false );
        }
        this._list.setDisplay( !this._dropped );
        this._dropped = !this._dropped;
        if( this.hasState( "rwt_CCOMBO" ) ) {
          this._updateListVisibleRequestParam();
        }
      }
    },
    
    _resetListSelection : function() {
      this._manager.deselectAll();
      this._manager.setLeadItem( null );
      this._manager.setAnchorItem( null );
    },

    _onListAppear : function( evt ) {
      if( this._selected ) {
        this._selected.scrollIntoView();
      }
    },

    _bringToFront : function() {
      var allWidgets = this.getTopLevelWidget().getChildren();
      var topZIndex = this._list.getZIndex();
      for( var vHashCode in allWidgets ) {
        var widget = allWidgets[ vHashCode ];
        if( widget.getZIndex ) {
          if( topZIndex < widget.getZIndex() ) {
            topZIndex = widget.getZIndex();
          }
        }
      }
      if( topZIndex > this._list.getZIndex() ) {
        this._list.setZIndex( topZIndex + 1 );
      }
    },
   
    _setSelected : function( value ) {
      this._selected = value;
      this._manager.setLeadItem( value );
      this._manager.setAnchorItem( value );
      if( value ) {
        var fieldValue = value.getLabel().toString();
        this._field.setValue( this._formatText( fieldValue ) );
        if( this._field.isCreated() ) {
          this._field.selectAll();
          if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
            this._handleSelectionChange();
          }
        }
        this._manager.setSelectedItem( value );
        // avoid warning message. scrollIntoView works only for visible widgets
        // the assumtion is that if 'this' is visible, the item to scroll into
        // view is also visible
        if ( this.isCreated() && this.isDisplayable() ) {
          this._manager.scrollItemIntoView( value );
        }
      } else {
        this._resetListSelection();
      }
      if( !this._dropped ) {
        this._sendWidgetSelected();
      }
    },
    
    _formatText : function( value ) {
      var result = value;
      result = result.replace( /<[^>]+?>/g, "" );
      result = qx.html.String.unescape( result );
      return result;
    },

    ////////////////////////////////
    // Mouse events handling methods
    
    _onMouseDown : function( evt ) {
      if( evt.isLeftButtonPressed() ) {
        if( evt.getTarget() == this._field ) {
          if( !this._editable || this._dropped ) {
            this._toggleListVisibility();
          }
        }
        evt.stopPropagation();
      }
    },

    _onMouseClick : function( evt ) {
      if( evt.isLeftButtonPressed() ) {
        // Correction of the list manager selection 
        // after a mouse over interaction with ListItem
        if( this._selected ) {
          // There is a selected ListItem
          this._manager.setLeadItem( this._selected );
          this._manager.setAnchorItem( this._selected );
          this._manager.setSelectedItem( this._selected );
        } else {
          // There is no selected ListItem
          this._resetListSelection();
        }
        // In case the 'mouseout' event has not been catched
        if( this._button.hasState( "over" ) ) {
          this._button.removeState( "over" );
        }
        // Redirecting the action, according to the click target 
        var target = evt.getTarget();
        // Click is on a list item
        if(    target instanceof qx.ui.form.ListItem 
            && target.getParent() === this._list )
        {
          this._list._onmousedown( evt );
          this._toggleListVisibility();
          this._setSelected( this._manager.getSelectedItem() );
          this.setFocused( true );
        // Click is on the combo's button or outside the dropped combo
        } else if(    target == this._button
                   || (    this._dropped
                        && target != this 
                        && target != this._field 
                        && target != this._list ) ) 
        {
          this._toggleListVisibility();
        }
      }
    },
    
    _onMouseUp : function( evt ) {
      if( !this._dropped ) {
        this.setCapture( false );
      }
      if(    evt.getTarget() == this._field
          && !org_eclipse_rap_rwt_EventUtil_suspend ) 
      {
        this._handleSelectionChange();
      }
    },

    _onMouseWheel : function( evt ) {
      if( !this._dropped ) {
        evt.preventDefault();
        evt.stopPropagation();
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
      var target = evt.getTarget();
      if( target instanceof qx.ui.form.ListItem ) {
        this._manager.deselectAll();
        this._manager.setLeadItem( target );
        this._manager.setAnchorItem( target );
        this._manager.setSelectedItem( target );
      } else if( target == this._button ) {
        this._button.addState( "over" );
      }
    },
    
    _onMouseOut : function( evt ) {
      if( evt.getTarget() == this._button ) {
        this._button.removeState( "over" );
      }
    },

    ////////////////////////////////////
    // Keyboard events handling methods
    
    _onKeyDown : function( evt ) {
      switch( evt.getKeyIdentifier() ) {
        // Handle <ENTER>, <ESC>
        case "Enter":
          if( this._dropped ) {
            this._toggleListVisibility();
            this._setSelected( this._manager.getSelectedItem() );
          } else if(    !evt.isShiftPressed()
                     && !evt.isAltPressed()
                     && !evt.isCtrlPressed()
                     && !evt.isMetaPressed() )
          {
            this._sendWidgetDefaultSelected();
          }
          this.setFocused( true );
          evt.stopPropagation();
          break;
        case "Escape":
          if( this._dropped ) {
            this._toggleListVisibility();
            this._setSelected( this._manager.getSelectedItem() );
          } 
          this.setFocused( true );
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
      if(    this._field.isCreated()
          && !org_eclipse_rap_rwt_EventUtil_suspend ) 
      {
        this._handleSelectionChange();
      }
    },

    _onKeyPress : function( evt ) {
      switch( evt.getKeyIdentifier() ) {
        case "Escape":
          evt.stopPropagation();
          break;
        case "Tab":
          if( this._dropped ) {
            this._toggleListVisibility();
          }
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
        case "Up":
        case "Down":
        case "PageUp":
        case "PageDown":
          this._list._onkeypress( evt );
          var selected = this._manager.getSelectedItem();
          this._setSelected( selected );
          break;
        default:
          charCode = evt.getCharCode();
          keyIdentifier = evt.getKeyIdentifier();
          if( this._editable && (    charCode > 0 
                                  || keyIdentifier == "Delete" 
                                  || keyIdentifier == "Backspace" ) ) 
          {
            this._isModified = true;
            this._selected = null;
            this._resetListSelection();
            if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
              var req = org.eclipse.swt.Request.getInstance();
              req.addEventListener( "send", this._onSend, this );
              if( this._hasVerifyModifyListener ) {
                qx.client.Timer.once( this._sendModifyText, this, 500 );
              }
            }
          }
      }
      if(    this._field.isCreated()
          && !org_eclipse_rap_rwt_EventUtil_suspend ) 
      {
        this._handleSelectionChange();
      }
    },

    _onKeyInput : function( evt ) {
      if( this._dropped ) {
        this._list._onkeyinput( evt );
        var selected = this._manager.getSelectedItem();
        this._setSelected( selected );
      }
    },
    
    ///////////////////////////////////////////////
    // Actions, connected with server communication
    
    _onTextBlur : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && this._isModified ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
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
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.send();
      this._isModified = false;
    },

    _sendWidgetSelected : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var list = this._list;
        var listItem = this._list.getSelectedItem();
        req.addParameter( id + ".selectedItem", list.indexOf( listItem ) );
        if( this._hasSelectionListener || this._hasVerifyModifyListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          req.send();
        }
      }
    },
    
    _sendWidgetDefaultSelected : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        if( this._hasSelectionListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
          req.send();
        }
      }
    },
    
    _updateListVisibleRequestParam : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        req.addParameter( id + ".listVisible", this._list.getDisplay() );
      }
    },
    
    // Checks for a text field selection change and updates 
    // the request parameter if necessary.
    _handleSelectionChange : function() {
      var start = this._field.getSelectionStart();
      // TODO [ad] Solution from TextUtil.js - must be in synch with it
      // TODO [rst] Quick fix for bug 258632
      //            https://bugs.eclipse.org/bugs/show_bug.cgi?id=258632
      if( start === undefined ) {
        start = 0;
      }
      var length = this._field.getSelectionLength();
      // TODO [ad] Solution from TextUtil.js - must be in synch with it
      // TODO [rst] Workaround for qx bug 521. Might be redundant as the
      //            bug is marked as (partly) fixed.
      //            See http://bugzilla.qooxdoo.org/show_bug.cgi?id=521
      if( typeof length == "undefined" ) {
        length = 0;
      }
      if( this._selectionStart != start || this._selectionLength != length ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        this._selectionStart = start;
        req.addParameter( id + ".selectionStart", start );
        this._selectionLength = length;
        req.addParameter( id + ".selectionLength", length );
      }
    },
    
    // [if] Workaround for bug:
    // 278361: [Combo] Overlays text after changing items
    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=278361
    // Items are not removed from DOM if the _isDisplayable property is false.
    _removeAll : function() {
      var items = this._list.getChildren();
      var item = items.length > 0 ? items[ 0 ] : null;
      while( item != null ) {
        item._isDisplayable = true;
        item.destroy();
        item = items.length > 0 ? items[ 0 ] : null;
      }
    },

    //////////////
    // Set methods
    
    setItems : function( items ) {
      this._removeAll();
      for( var i = 0; i < items.length; i++ ) {
        var item = new qx.ui.form.ListItem();
        item.setLabel( "(empty)" );
        item.getLabelObject().setMode( "html" );
        item.setLabel( items[ i ] );
        item.setFont( this.getFont() );
        item.setHeight( this._listItemHeight );
        this._list.add( item );
      }
    },

    setMaxListHeight : function( value ) {
      this._list.setMaxHeight( value );
    },
    
    setListItemHeight : function( value ) {
      this._listItemHeight = value;
      var items = this._list.getChildren();
      for( var i = 0; i < items.length; i++ ) {
        items[ i ].setHeight( this._listItemHeight );
      }
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
      if( this._list.getDisplay() != value ) {
        this._dropped = !value;
        this._toggleListVisibility();
      }
    },

    setValue : function( value ) {
      this._field.setValue( value );
    },
    
    setTextSelection : function( start, length ) {
      if( this._field.isCreated() ) {
        this._selectionStart = start;
        this._field.setSelectionStart( start );
        this._selectionLength = length;
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
