/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

/**
 * This class provides the client-side counterpart for
 * org.eclipse.swt.widget.Combo and org.eclipse.swt.custom.CCombo.
 */
rwt.qx.Class.define( "rwt.widgets.Combo", {
  extend : rwt.widgets.base.Parent,

  construct : function( isCCombo ) {
    this.base( arguments );
    this._modifyScheduled = false;
    // Default values
    this._selected = null;
    this._editable = true;
    this._dropped = false;
    this._itemHeight = 20;
    this._visibleItemCount = 5;
    // Text field
    this._field = new rwt.widgets.base.BasicText();
    this._field.setTabIndex( null );
    this._field.setAllowStretchY( true );
    this.add( this._field );
    // Drop down button
    this._button = new rwt.widgets.base.Button();
    this._button.setTabIndex( null );
    this._button.setHeight( "100%" );
    this.add( this._button );
    // List
    this._list = new rwt.widgets.base.BasicList( false );
    this._list.setTabIndex( null );
    this._list.setDisplay( false );
    this._blockMouseOver = false;
    this._list.addEventListener( "userScroll", function() {
      this._blockMouseOver = true;
      rwt.client.Timer.once( function() {
        this._blockMouseOver = false;
      }, this, 300 ); // the browser may fire a mouse event with some delay
    }, this );
    // List Manager
    this._manager = this._list.getManager();
    this._manager.setMultiSelection( false );
    this._manager.setDragSelection( false );
    this._manager.scrollItemIntoView = this._scrollItemIntoView;
    // Do not visualize the focus rectangle around the widget
    this.setHideFocus( true );
    // Add events listeners
    var cDocument = rwt.widgets.base.ClientDocument.getInstance();
    cDocument.addEventListener( "windowblur", this._onBlur, this );
    // Set appearance
    var appearance = isCCombo ? "ccombo" : "combo";
    this.setAppearance( appearance );
    this._field.setAppearance( appearance + "-field" );
    this._button.setAppearance( appearance + "-button" );
    this._list.setAppearance( appearance + "-list" );
    // Listeners
    this.addEventListener( "appear", this._onAppear, this );
    this.addEventListener( "blur", this._onBlur, this );
    this.addEventListener( "mousedown", this._onMouseDown, this );
    this.addEventListener( "mouseup", this._onMouseUp, this );
    this.addEventListener( "click", this._onMouseClick, this );
    this.addEventListener( "mousewheel", this._onMouseWheel, this );
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mousemove", this._onMouseMove, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
    this.addEventListener( "keydown", this._onKeyDown, this );
    this.addEventListener( "keypress", this._onKeyPress, this );
    this._field.addEventListener( "input", this._onTextInput, this );
    this._field.addEventListener( "selectionChanged", this._handleSelectionChange, this );
    this._list.addEventListener( "appear", this._onListAppear, this );
    this._setupCaptureRestore();
  },

  destruct : function() {
    var cDocument = rwt.widgets.base.ClientDocument.getInstance();
    cDocument.removeEventListener( "windowblur", this._onBlur, this );
    this.removeEventListener( "appear", this._onAppear, this );
    this.removeEventListener( "blur", this._onBlur, this );
    this.removeEventListener( "mousedown", this._onMouseDown, this );
    this.removeEventListener( "mouseup", this._onMouseUp, this );
    this.removeEventListener( "click", this._onMouseClick, this );
    this.removeEventListener( "mousewheel", this._onMouseWheel, this );
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
    this.removeEventListener( "keydown", this._onKeyDown, this );
    this.removeEventListener( "keypress", this._onKeyPress, this );
    this._field.removeEventListener( "input", this._onTextInput, this );
    this._field.removeEventListener( "selectionChanged", this._handleSelectionChange, this );
    this._list.removeEventListener( "appear", this._onListAppear, this );
    // Solution taken from Qooxdoo implementation of ComboBox
    // in order to prevent memory leak and other problems.
    if( this._list && !rwt.qx.Object.inGlobalDispose() ) {
      this._list.setParent( null );
    }
    this._disposeObjects( "_field", "_button", "_list", "_manager", "_selected" );
  },

  members : {

    addState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._field.addState( state );
        this._list.addState( state );
        this._button.addState( state );
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._field.removeState( state );
        this._list.removeState( state );
        this._button.removeState( state );
      }
    },

    _onAppear : function( event ) {
        if( this.hasState( "rwt_FLAT" ) ) {
          this._field.addState( "rwt_FLAT" );
          this._button.addState( "rwt_FLAT" );
          this._list.addState( "rwt_FLAT" );
        }
        if( this.hasState( "rwt_BORDER" ) ) {
          this._field.addState( "rwt_BORDER" );
          this._button.addState( "rwt_BORDER" );
          this._list.addState( "rwt_BORDER" );
        }
        this.getTopLevelWidget().add( this._list );
    },

    _applyFont : function( value, old ) {
      this.base( arguments, value, old );
      this._field.setFont( value );
      var items = this._list.getItems();
      for( var i = 0; i < items.length; i++ ) {
        items[ i ].setFont( value );
      }
    },

    _applyTextColor : function( value, old ) {
      this.base( arguments, value, old );
      this._field.setTextColor( value );
      this._list.setTextColor( value );
    },

    _applyBackgroundColor : function( value, old ) {
      this.base( arguments, value, old );
      // Ensure that the list is never transparent (see bug 282540)
      if( value ) {
        this._list.setBackgroundColor( value );
      } else {
        this._list.resetBackgroundColor();
      }
    },

    _applyVisibility : function( value, old ) {
      this.base( arguments, value, old );
      if( !value ) {
        this._hideList();
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
        this._field._renderSelection();
      }
      if( !this._editable ) {
        var cssSelector = ( this.getAppearance() === "combo" ? "" : "C" ) + "Combo-FocusIndicator";
        rwt.widgets.util.FocusIndicator.getInstance().show( this, cssSelector, null );
      }
      this.addState( "focused" );
    },

    // Override of the _ontabfocus method from rwt.widgets.base.Widget
    _ontabfocus : function() {
      if( this._field.isCreated() ) {
        this._field.selectAll();
      }
    },

    _visualizeBlur : function() {
      if( this._field.isCreated() ) {
        // setting selection lenght to 0 needed for IE to deselect text
        this._field._setSelectionLength( 0 );
        this._field._visualizeBlur();
      }
      if( !this._editable ) {
        rwt.widgets.util.FocusIndicator.getInstance().hide( this );
      }
      this.removeState( "focused" );
    },

    // On "blur" or "windowblur" event: closes the list, if it is seeable
    _onBlur : function( event ) {
      this._hideList();
    },

    ///////////////////////////////////////
    // List and list-items handling methods

    _setListBounds : function() {
      if( this.getElement() ){
        var browserWidth = rwt.html.Window.getInnerWidth( window );
        var browserHeight = rwt.html.Window.getInnerHeight( window );
        var elementPos = rwt.html.Location.get( this.getElement() );
        var left = elementPos.left;
        var top = elementPos.top + this.getHeight();
        var width = Math.max( this.getWidth(), this._list.getPreferredWidth() );
        var itemsHeight = this._list.getItemsCount() * this._itemHeight;
        var height = Math.min( this._getListMaxHeight(), itemsHeight );
        height += this._list.getFrameHeight();
        if( top + height > browserHeight && elementPos.top - height > 0 ) {
          top = elementPos.top - height;
        }
        if( left + width > browserWidth ) {
          left =  Math.max( 0, browserWidth - width );
        }
        this._list.setLocation( left, top );
        this._list.setWidth( width );
        this._list.setHeight( height );
        this._list.setItemDimensions( width, this._itemHeight );
      }
    },

    _toggleListVisibility : function() {
      if( this._dropped ) {
        this._hideList();
      } else {
        this._showList();
      }
    },

    _showList : function() {
      if( !this._dropped ) {
        this._updateItems();
        if( this._list.getItemsCount() ) {
          this._dropped = true;
          if( this._editable ) {
            this._field.setReadOnly( true );
            this._field._visualizeBlur();
          }
          this._bringToFront();
          this.setCapture( true );
          this._list.setDisplay( true );
          this._setListBounds();
          this._setListSelection( this._selected );
          this._updateListScrollBar();
          this._updateListVisibleRequestParam();
        }
      }
    },

    _hideList : function() {
      if( this._dropped ) {
        this._dropped = false;
        if( this._editable ) {
          this._field.setReadOnly( false );
          this._field._visualizeFocus();
        }
        this.setCapture( false );
        this._list.setDisplay( false );
        this._updateListVisibleRequestParam();
      }
    },

    _updateListScrollBar : function() {
      if( this._dropped ) {
        var itemsHeight = this._list.getItemsCount() * this._itemHeight;
        var visible = this._getListMaxHeight() < itemsHeight;
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

    _onListAppear : function( event ) {
      this._setListBounds();
      if( this._selected ) {
        this._selected.scrollIntoView();
        this._list._syncScrollBars();
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
        this.setText( this._formatText( value.getLabel().toString() ) );
        if( this._field.isCreated() && !rwt.remote.EventUtil.getSuspended() ) {
          this._field.selectAll();
        }
        this._setListSelection( value );
        this._manager.scrollItemIntoView( value );
      } else {
        if( !this._editable ) {
          this.setText( "" );
        }
        this._resetListSelection();
      }
      this._notifySelectionChanged();
      this.dispatchSimpleEvent( "selectionChanged" );
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
      result = rwt.util.Encoding.unescape( result );
      return result;
    },

    ////////////////////////////////
    // Mouse events handling methods

    _reDispatch : function( event ) {
      var originalTarget = event.getTarget();
      if( this._list.contains( originalTarget ) ) {
        // TODO [tb] : should be disposed automatically, test
        originalTarget.dispatchEvent( event, false );
        event.stopPropagation();
      }
    },

    _onMouseDown : function( event ) {
      if( event.isLeftButtonPressed() ) {
        if( event.getTarget() == this._field ) {
          if( !this._editable || this._dropped ) {
            this._toggleListVisibility();
          }
        } else if( this._dropped ) {
          this._reDispatch( event );
        }
      }
    },

    _onMouseClick : function( event ) {
      if( event.isLeftButtonPressed() ) {
        // In case the 'mouseout' event has not been catched
        this._button.removeState( "over" );
        // Redirecting the action, according to the click target
        var target = event.getTarget();
        // Click is on a list item
        var isListItemTarget = target instanceof rwt.widgets.ListItem;
        if( isListItemTarget && target === this._list.getListItemTarget( target ) ) {
          this._reDispatch( event );
          this._hideList();
          this._setSelected( this._manager.getSelectedItem() );
          this.setFocused( true );
        } else if( target == this._button ) {
          this._toggleListVisibility();
        // Click is on outside the dropped combo
        } else if( target !== this && target !== this._field && !this._list.contains( target ) ) {
          this._hideList();
        }
      }
    },

    _onMouseUp : function( event ) {
      if( this._dropped ) {
        this._reDispatch( event );
      }
    },

    _onMouseWheel : function( event ) {
      if( this._dropped ) {
        if( !this._list.isRelevantEvent( event ) ) {
          event.preventDefault();
          event.stopPropagation();
        }
      } else if( this.getFocused() ) {
        this._updateItems();
        event.preventDefault();
        event.stopPropagation();
        if( this._selected ) {
          var toSelect;
          if( event.getWheelDelta() < 0 ) {
            toSelect = this._manager.getNext( this._selected );
          } else {
            toSelect = this._manager.getPrevious( this._selected );
          }
          if( toSelect ) {
            this._setSelected( toSelect );
          }
        } else if( this._list.getItemsCount() ) {
          this._setSelected( this._list.getItems()[0] );
        }
      }
    },

    _onMouseOver : function( event ) {
      var target = event.getTarget();
      if( target instanceof rwt.widgets.ListItem && !this._blockMouseOver ) {
        this._setListSelection( target );
      } else if( target == this._button ) {
        this._button.addState( "over" );
      }
    },

    _onMouseMove : function( event ) {
      var target = event.getTarget();
      if( target instanceof rwt.widgets.ListItem && target !== this._manager.getSelectedItem() ) {
        this._onMouseOver( event );
      }
    },

    _onMouseOut : function( event ) {
      if( event.getTarget() == this._button ) {
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

    _onKeyDown : function( event ) {
      switch( event.getKeyIdentifier() ) {
        case "Enter":
          this._handleKeyEnter( event );
        break;
        case "Escape":
          this._handleKeyEscape( event );
        break;
        case "Down":
        case "Up":
        case "PageUp":
        case "PageDown":
          this._handleKeyUpDown( event );
        break;
      }
    },

    _onKeyPress : function( event ) {
      switch( event.getKeyIdentifier() ) {
        case "Escape":
        case "Down":
        case "Up":
        case "PageUp":
        case "PageDown":
          event.stopPropagation();
        break;
        case "Tab":
          this._hideList();
        break;
        case "Right":
          this._handleKeyRight( event );
        break;
        case "Left":
          this._handleKeyLeft( event );
        break;
        case "Enter":
          event.preventDefault();
        break;
      }
      this._selectByFirstLetter( event );
    },

    _handleKeyEnter : function( event ) {
      if( this._dropped ) {
        this._hideList();
        this._setSelected( this._manager.getSelectedItem() );
      } else if( event.getModifiers() === 0 ) {
        rwt.remote.EventUtil.notifyDefaultSelected( this );
      }
      this.setFocused( true );
      event.stopPropagation();
    },

    _handleKeyEscape : function( event ) {
      if( this._dropped ) {
        this._hideList();
        this.setFocused( true );
        event.stopPropagation();
      }
    },

    _handleKeyUpDown : function( event ) {
      if( event.isAltPressed() ) {
        this._toggleListVisibility();
      } else {
        this._updateItems();
        if( this._selected || this._manager.getSelectedItem() ) {
          this._list._onkeypress( event );
          this._setSelected( this._manager.getSelectedItem() );
        } else if( this._list.getItemsCount() ) {
          this._setSelected( this._list.getItems()[ 0 ] );
        }
      }
    },

    _handleKeyLeft : function( event ) {
      if( this._dropped ) {
        var manager = this._manager;
        var toSelect = this._selected ? manager.getPrevious( this._selected ) : manager.getLast();
        if( toSelect ) {
          this._setSelected( toSelect );
        }
      }
    },

    _handleKeyRight : function( event ) {
      if( this._dropped ) {
        var manager = this._manager;
        var toSelect = this._selected ? manager.getNext( this._selected ) : manager.getFirst();
        if( toSelect ) {
          this._setSelected( toSelect );
        }
      }
    },

    _selectByFirstLetter : function( event ) {
      // Additional check for ALT and CTRL keys is added to fix bug 288344
      if( event.getCharCode() !== 0 && !event.isAltPressed() && !event.isCtrlPressed() ) {
        if( this._dropped || !this._editable ) {
          this._updateItems();
          this._list._onkeyinput( event );
          var selected = this._manager.getSelectedItem();
          if( selected ) {
            this._setSelected( selected );
          } else {
            this._setListSelection( this._selected );
          }
        }
      }
    },

    _onTextInput : function( event ) {
      if( this._editable ) {
        this._selected = null;
        this._resetListSelection();
        var connection = rwt.remote.Connection.getInstance();
        var remoteObject = connection.getRemoteObject( this );
        if( !this._modifyScheduled && remoteObject.isListening( "Modify" ) ) {
          this._modifyScheduled = true;
          connection.onNextSend( this._onSend, this );
          connection.sendDelayed( 500 );
        }
        remoteObject.set( "text", this._field.getComputedValue() );
      }
    },

    ///////////////////////////////////////////////
    // Actions, connected with server communication

    _onSend : function( event ) {
      if( this._modifyScheduled ) {
        rwt.remote.Connection.getInstance().getRemoteObject( this ).notify( "Modify" );
        this._modifyScheduled = false;
      }
    },

    _notifySelectionChanged : function() {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        var listItem = this._list.getSelectedItem();
        var remoteObject = rwt.remote.Connection.getInstance().getRemoteObject( this );
        remoteObject.set( "selectionIndex", this._list.getItemIndex( listItem ) );
        rwt.remote.EventUtil.notifySelected( this );
        remoteObject.notify( "Modify" );
      }
    },

    _updateListVisibleRequestParam : function() {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        var connection = rwt.remote.Connection.getInstance();
        connection.getRemoteObject( this ).set( "listVisible", this._list.getDisplay() );
      }
    },

    _handleSelectionChange : function( event ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        var remoteObject = rwt.remote.Connection.getInstance().getRemoteObject( this );
        remoteObject.set( "selection", this._field.getSelection() );
      }
    },

    _getListMaxHeight : function() {
      return this._itemHeight * this._visibleItemCount;
    },

    _updateItems : function() {
      if( this._items ) {
        this._list.setItems( this._items );
        this.createDispatchEvent( "itemsChanged" );
        delete this._items;
      }
    },

    //////////////
    // Set methods

    setItems : function( items ) {
      this._items = items;
      if( this._dropped ) {
        this._updateItems();
      }
    },

    setVisibleItemCount : function( value ) {
      this._visibleItemCount = value;
    },

    setItemHeight : function( value ) {
      this._itemHeight = value;
    },

    select : function( index ) {
      this._updateItems();
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
      if( value ) {
        this._showList();
      } else {
        this._hideList();
      }
    },

    setText : function( value ) {
      this._field.setValue( value );
    },

    setTextSelection : function( selection ) {
      this._field.setSelection( selection );
    },

    setTextLimit : function( value ) {
      this._field.setMaxLength( value );
    },

    ////////////////////////////
    // apply subwidget html IDs

    applyObjectId : function( id ) {
      this.base( arguments, id );
      if( rwt.widgets.base.Widget._renderHtmlIds ) {
        this._list.applyObjectId( id + "-listbox" );
        this.addEventListener( "itemsChanged", this._applyListItemIds );
      }
    },

    _applyListItemIds : function() {
      var listId = this._list.getHtmlAttribute( "id" );
      var listItems = this._list.getItems();
      if( listItems ) {
        for( var i = 0; i < listItems.length; i++ ) {
          listItems[ i ].setHtmlAttribute( "id", this._list.getHtmlAttribute( "id" ) + "-listitem-" + i );
        }
      }
    }

  }

} );
