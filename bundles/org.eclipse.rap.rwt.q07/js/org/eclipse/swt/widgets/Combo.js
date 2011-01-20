/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
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
    this._listMaxHeight = 0;
    // Text field
    this._field = new qx.ui.form.TextField();
    this._field.setTabIndex( null );
    this._field.setAllowStretchY( true );
    this.add( this._field );
    // Drop down button
    this._button = new qx.ui.form.Button();
    this._button.setTabIndex( null );
    this._button.setHeight( "100%" );
    this.add( this._button );
    // List
    this._list = new org.eclipse.rwt.widgets.BasicList( false );
    this._list.setTabIndex( null );
    this._list.setDisplay( false );
    // List Manager
    this._manager = this._list.getManager();
    this._manager.setMultiSelection( false );
    this._manager.setDragSelection( false );
    this._manager.scrollItemIntoView = this._scrollItemIntoView;
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
    // Specific events
    this._field.addEventListener( "blur", this._onTextBlur, this );
    this._field.addEventListener( "input", this._onTextInput, this );
    this._list.addEventListener( "appear", this._onListAppear, this );
    this._setupCaptureRestore();
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
    this._field.removeEventListener( "blur", this._onTextBlur, this );
    this._field.removeEventListener( "input", this._onTextInput, this );
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
      this._setListBounds();
    },

    _onAppear : function( evt ) {
      if( this._ccombo && this.hasState( "rwt_FLAT" ) ) {
        this._field.addState( "rwt_FLAT" );
        this._button.addState( "rwt_FLAT" );
        this._list.addState( "rwt_FLAT" );
      }
      this.getTopLevelWidget().add( this._list );
      org.eclipse.swt.TextUtil._updateLineHeight( this._field );
    },
    
    _onFocusIn : function( evt ) {
      if(    this._field.isCreated()
          && !org.eclipse.swt.EventUtil.getSuspended() )
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
      var items = this._list.getItems();
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
      if( !this._editable ) {
        var focusIndicator = org.eclipse.rwt.FocusIndicator.getInstance();
        var cssSelector = this._ccombo
                        ? "CCombo-FocusIndicator"
                        : "Combo-FocusIndicator"
        focusIndicator.show( this, cssSelector, null );
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
      if( !this._editable ) {
        var focusIndicator = org.eclipse.rwt.FocusIndicator.getInstance();
        focusIndicator.hide( this );
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

    _setListBounds : function() {
      if( this.getElement() ){
        var elementPos = qx.bom.element.Location.get( this.getElement() );
        var listLeft = elementPos.left;
        var comboTop = elementPos.top;
        var listTop = comboTop + this.getHeight();
        var browserHeight = qx.html.Window.getInnerHeight( window );
        var browserWidth = qx.html.Window.getInnerWidth( window );
        var itemsWidth = this._list.getPreferredWidth();
        var listWidth = Math.min( browserWidth - listLeft, itemsWidth );
        listWidth = Math.max( this.getWidth(), listWidth )
        var itemsHeight = this._list.getItemsCount() * this._listItemHeight;
        var listHeight = Math.min( this._listMaxHeight, itemsHeight );
        listHeight += this._list.getFrameHeight();
        if(    browserHeight < listTop + listHeight
            && comboTop > browserHeight - listTop )
        {
          listTop = elementPos.top - listHeight;
        }
        this._list.setLocation( listLeft, listTop );
        this._list.setWidth( listWidth );
        this._list.setHeight( listHeight );
        this._list.setItemDimensions( listWidth, this._listItemHeight );
      }
    },
    
    _toggleListVisibility : function() {
      if( this._list.getItemsCount() ) {
        // Temporary make the text field ReadOnly, when the list is dropped.
        if( this._editable ) {
          this._field.setReadOnly( !this._dropped  );
        }
        if( !this._dropped ) {
          // Brings this widget on top of the others with same parent.
          this._bringToFront();
        }
        this.setCapture( !this._dropped );
        this._list.setDisplay( !this._dropped );
        this._dropped = !this._dropped;
        if( this._dropped ) {
          this._setListSelection( this._selected );
        }
        this._updateListScrollBar();
        if( this.hasState( "rwt_CCOMBO" ) ) {
          this._updateListVisibleRequestParam();
        }
      }
    },
    
    _updateListScrollBar : function() {
      if( this._dropped ) {        
        var itemsHeight = this._list.getItemsCount() * this._listItemHeight;
        var visible = this._listMaxHeight < itemsHeight;
        this._list.setScrollBarsVisible( false, visible );
      }
    },

    _resetListSelection : function() {
      this._manager.deselectAll();
      this._manager.setLeadItem( null );
      this._manager.setAnchorItem( null );
    },

    _setListSelection : function( item ) {
      this._manager.deselectAll();
      this._manager.setLeadItem( item );
      this._manager.setAnchorItem( item );
      this._manager.setSelectedItem( item );
    },

    _onListAppear : function( evt ) {
      this._setListBounds();
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
      if( value ) {
        var fieldValue = value.getLabel().toString();
        this._field.setValue( this._formatText( fieldValue ) );
        if( this._field.isCreated() ) {
          if( !org.eclipse.swt.EventUtil.getSuspended() ) {
            this._field.selectAll();
            this._handleSelectionChange();
          }
        }
        this._setListSelection( value );
        this._manager.scrollItemIntoView( value );
      } else {
        if( !this._editable ) {
          this._field.setValue( "" );
        }
        this._resetListSelection();
      }
      this._sendWidgetSelected();
    },
    
    // [if] avoid warning message - see bug 300038
    _scrollItemIntoView : function( item, topLeft ) {
      if( item.isCreated() && item.isDisplayable() ) {
        item.scrollIntoView( topLeft );
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
    
    _reDispatch : function( event ) {
      var original = event.getTarget();
      if( !this.contains( original ) ) {
        // TODO [tb] : should be disposed automatically, test
        original.dispatchEvent( event, false );
        event.stopPropagation();
      }
    },
    
    _onMouseDown : function( evt ) {
      if( evt.isLeftButtonPressed() ) {
        if( evt.getTarget() == this._field ) {
          if( !this._editable || this._dropped ) {
            this._toggleListVisibility();
          }
        } else if( this._dropped ) {
          this._reDispatch( evt );
        }
      }
    },

    _onMouseClick : function( evt ) {
      if( evt.isLeftButtonPressed() ) {
        // In case the 'mouseout' event has not been catched
        if( this._button.hasState( "over" ) ) {
          this._button.removeState( "over" );
        }
        // Redirecting the action, according to the click target 
        var target = evt.getTarget();
        // Click is on a list item 
        if(    target instanceof qx.ui.form.ListItem
            && target === this._list.getListItemTarget( target ) )
        {
          this._reDispatch( evt );
          this._toggleListVisibility();
          this._setSelected( this._manager.getSelectedItem() );
          this.setFocused( true );
        // Click is on the combo's button or outside the dropped combo
        } else if(    target == this._button
                   || (    this._dropped
                        && target != this 
                        && target != this._field 
                        && !this._list.contains( target ) ) ) 
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
          && !org.eclipse.swt.EventUtil.getSuspended() ) 
      {
        this._handleSelectionChange();
      } else if( this._dropped ) {
        this._reDispatch( evt );
      }
    },

    _onMouseWheel : function( evt ) {
      if( this._dropped ) {
        if( !this._list.isRelevantEvent( evt ) ) {      
          evt.preventDefault();
          evt.stopPropagation();
        }
      } else if( this.getFocused() ) {
        evt.preventDefault();
        evt.stopPropagation();
        var toSelect;
        var isSelected = this._selected;
        if( isSelected ) {
          if( evt.getWheelDelta() < 0 ) {
            toSelect = this._manager.getNext( isSelected );
          } else {
            toSelect = this._manager.getPrevious( isSelected );
          }
          if( toSelect ) {
            this._setSelected( toSelect );
          }
        } else if( this._list.getItemsCount() ) {
          this._setSelected( this._list.getItems()[0] );
        }
      }
    },
    
    _onMouseOver : function( evt ) {
      var target = evt.getTarget();
      if( target instanceof qx.ui.form.ListItem ) {
        this._setListSelection( target );
      } else if( target == this._button ) {
        this._button.addState( "over" );
      }
    },
    
    _onMouseOut : function( evt ) {
      if( evt.getTarget() == this._button ) {
        this._button.removeState( "over" );
      }
    },
    
    _setupCaptureRestore : function() {
      var thumb = this._list._vertScrollBar._thumb;
      thumb.addEventListener( "mouseup", this._captureRestore, this );
    },
    
    _captureRestore : function( event ) {
      this.setCapture( true );
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
          && !org.eclipse.swt.EventUtil.getSuspended() ) 
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
          if( this._selected ) {
            this._list._onkeypress( evt );
            var selected = this._manager.getSelectedItem();
            this._setSelected( selected );
          } else if( this._list.getItemsCount() ) {
            this._setSelected( this._list.getItems()[0] );
          }
          break;
      }
      if(    this._field.isCreated()
          && !org.eclipse.swt.EventUtil.getSuspended() ) 
      {
        this._handleSelectionChange();
      }
      if( evt.getCharCode() !== 0 ) {
        this._onKeyInput( evt );
      }
    },

    // Additional check for ALT and CTRL keys is added to fix bug 288344
    _onKeyInput : function( evt ) {
      if( this._dropped && !evt.isAltPressed() && !evt.isCtrlPressed() ) {
        this._list._onkeyinput( evt );
        var selected = this._manager.getSelectedItem();
        this._setSelected( selected );
      }
    },

    _onTextInput : function( evt ) {
      if( this._editable ) {
        this._isModified = true;
        this._selected = null;
        this._resetListSelection();
        if( !org.eclipse.swt.EventUtil.getSuspended() ) {
          var req = org.eclipse.swt.Request.getInstance();
          req.addEventListener( "send", this._onSend, this );
          if( this._hasVerifyModifyListener ) {
            qx.client.Timer.once( this._sendModifyText, this, 500 );
          }
        }
      }
    },
    
    ///////////////////////////////////////////////
    // Actions, connected with server communication
    
    _onTextBlur : function( evt ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() && this._isModified ) {
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
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var list = this._list;
        var listItem = this._list.getSelectedItem();
        req.addParameter( id + ".selectedItem", list.getItemIndex( listItem ) );
        if( this._hasSelectionListener || this._hasVerifyModifyListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
          req.send();
        }
      }
    },
    
    _sendWidgetDefaultSelected : function() {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );
        if( this._hasSelectionListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
          org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
          req.send();
        }
      }
    },
    
    _updateListVisibleRequestParam : function() {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
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

    //////////////
    // Set methods
    
    setItems : function( items ) {
      this._list.setItems( items );
    },

    setMaxListHeight : function( value ) {
      this._listMaxHeight = value;
    },
    
    setListItemHeight : function( value ) {
      this._listItemHeight = value;
    },

    select : function( index ) {
      var items = this._list.getItems();
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
