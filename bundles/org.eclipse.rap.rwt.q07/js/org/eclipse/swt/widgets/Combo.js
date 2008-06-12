/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/**
 * This class extends qx.ui.form.ComboBox to ease its usage in RWT.
 */
qx.Class.define( "org.eclipse.swt.widgets.Combo", {
  extend : qx.ui.form.ComboBox,

  construct : function() {
    this.base( arguments );
    this.rap_init();
  },

  members : {
    rap_init : function() { 
      this.addEventListener( "changeFont", this._rwt_onChangeFont, this );
      this.addEventListener( "changeTextColor", this._rwt_onChangeTextColor, this );
      this.addEventListener( "changeBackgroundColor", this._rwt_onChangeBackgoundColor, this );
      this.addEventListener( "changeValue", this._rwt_onChangeValue, this );
      this._popup.addEventListener( "appear", this._rwt_onPopupAppear, this );
      this._popup.addEventListener( "disappear", this._rwt_onPopupDisappear, this );
    },

    rap_reset : function() {
      this.removeEventListener( "changeFont", this._rwt_onChangeFont, this );
      this.removeEventListener( "changeTextColor", this._rwt_onChangeTextColor, this );
      this.removeEventListener( "changeBackgroundColor", this._rwt_onChangeBackgoundColor, this );
      this.removeEventListener( "changeValue", this._rwt_onChangeValue, this );
      this._popup.removeEventListener( "appear", this._rwt_onPopupAppear, this );
      this._popup.removeEventListener( "disappear", this._rwt_onPopupDisappear, this );
    },

    // workaround for missing property propagation in qx ComboBox
    _rwt_onChangeFont : function( evt ) {
      var combo = evt.getTarget();
      // Apply changed font to embedded text-field and drop-down-button
      var children = combo.getChildren();
      for( var i = 0; i < children.length; i++ ) {
        children[ i ].setFont( combo.getFont() );
      }  
      // Apply changed font to items
      var items = combo.getList().getChildren();
      for( var i = 0; i < items.length; i++ ) {
        items[ i ].setFont( combo.getFont() );
      }
    },
    
    // workaround for missing property propagation in qx ComboBox
    _rwt_onChangeTextColor : function( evt ) {
      var combo = evt.getTarget();
      var value = evt.getData();
      combo._field.setTextColor( value );
      combo._list.setTextColor( value );
    },

    // workaround for missing property propagation in qx ComboBox
    _rwt_onChangeBackgoundColor : function( evt ) {
      var combo = evt.getTarget();
      var value = evt.getData();
      combo._field.setBackgroundColor( value );
      combo._list.setBackgroundColor( value );
    },

    _rwt_onChangeValue : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && evt.getData() != null ) {
        var combo = evt.getTarget();
        var value = combo.getValue();
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( combo );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".text", value );
      }
    },

    rwt_setItems : function( items ) {
      this.removeAll();
      for( var i = 0; i < items.length; i++ ) {
        var item = new qx.ui.form.ListItem();
        item.setLabel( "(empty)" );
        item.getLabelObject().setMode( "html" );
        item.setLabel( items[ i ] );
        item.setFont( this.getFont() );
        this.add( item );
      }
    },

    rwt_select : function( index ) {
      var items = this.getList().getChildren();
      var item = null;
      if( index >= 0 && index <= items.length - 1 ) {
        item = items[ index ];
      }
      this.setSelected( item );
    },

    rwt_setMaxPopupHeight : function( maxHeight ) {
      this.getPopup().setMaxHeight( maxHeight );
    },

    // workaround for broken context menu on qx ComboBox
    // see http://bugzilla.qooxdoo.org/show_bug.cgi?id=465
    rwt_applyContextMenu : function( evt ) {
      var menu = this.getContextMenu();
      this._field.setContextMenu( menu );
      this._button.setContextMenu( menu );
    },

    // Disable text field when popup opens up
    _rwt_onPopupAppear : function( evt ) {
      var f = this.getField();
      f.setReadOnly( true );
      f.setCursor( "default" );
      f.setSelectable( false );
    },

    // Enable text field when popup closes
    _rwt_onPopupDisappear : function( evt ) {
      var editable = this.getEditable();
      var f = this.getField();
      f.setReadOnly( !editable );
      f.setCursor( editable ? null : "default" );
      f.setSelectable( editable );
    },

    // =======================================================================
    
    // == BEGIN OVERWRITTEN METHODS ==

    // == BEGIN MODIFIED QX COPY ==
    _onkeydown : function(e)
    {
      var vManager = this._manager;
      var vVisible = this._popup.isSeeable();

      switch(e.getKeyIdentifier())
      {
          // Handle <ENTER>
        case "Enter":
          if (vVisible)
          {
            this.setSelected(this._manager.getSelectedItem());
            this._closePopup();
            this.setFocused(true);
          }
          else
          {
            this._openPopup();
          }
          // Workaround for http://bugzilla.qooxdoo.org/show_bug.cgi?id=878
          e.stopPropagation();

          return;

          // Handle <ESC>

        case "Escape":
          if (vVisible)
          {
            vManager.setLeadItem(this._oldSelected);
            vManager.setAnchorItem(this._oldSelected);

            vManager.setSelectedItem(this._oldSelected);

            this._field.setValue(this._oldSelected ? this._oldSelected.getLabel() : "");

            this._closePopup();
            this.setFocused(true);
            // Workaround for http://bugzilla.qooxdoo.org/show_bug.cgi?id=878
            e.stopPropagation();
          }

          return;

          // Handle Alt+Down

        case "Down":
          if (e.isAltPressed())
          {
            this._togglePopup();
            return;
          }

          break;
      }
    },
    // == END MODIFIED QX COPY ==

    // == BEGIN MODIFIED QX COPY ==
    _onkeypress : function(e)
    {
      var vVisible = this._popup.isSeeable();
      var vManager = this._manager;

      switch(e.getKeyIdentifier())
      {
          // Handle <PAGEUP>
        case "PageUp":
          if (!vVisible)
          {
            var vPrevious;
            var vTemp = this.getSelected();

            if (vTemp)
            {
              var vInterval = this.getPagingInterval();

              do {
                vPrevious = vTemp;
              } while (--vInterval && (vTemp = vManager.getPrevious(vPrevious)));
            }
            else
            {
              vPrevious = vManager.getLast();
            }

            this.setSelected(vPrevious);

            return;
          }

          break;

          // Handle <PAGEDOWN>

        case "PageDown":
          if (!vVisible)
          {
            var vNext;
            var vTemp = this.getSelected();

            if (vTemp)
            {
              var vInterval = this.getPagingInterval();

              do {
                vNext = vTemp;
              } while (--vInterval && (vTemp = vManager.getNext(vNext)));
            }
            else
            {
              vNext = vManager.getFirst();
            }

            this.setSelected(vNext||null);

            return;
          }

          break;
      }

      // Default Handling
      if (!this.isEditable() || vVisible)
      {
        this._list._onkeypress(e);

//        var vSelected = this._manager.getSelectedItem();
//
//        if (!vVisible) {
//          this.setSelected(vSelected);
//        } else if (vSelected) {
//          this._field.setValue(vSelected.getLabel());
//        }
      }
    },
    // == END MODIFIED QX COPY ==
    
    // == BEGIN MODIFIED QX COPY ==
    _onkeyinput : function(e)
    {
      var vVisible = this._popup.isSeeable();

      if (!this.isEditable() || vVisible)
      {
        this._list._onkeyinput(e);

//        var vSelected = this._manager.getSelectedItem();
//
//        if (!vVisible) {
//          this.setSelected(vSelected);
//        } else if (vSelected) {
//          this._field.setValue(vSelected.getLabel());
//        }
      }
    },
    // == END MODIFIED QX COPY ==

    // == BEGIN MODIFIED QX COPY ==
    _oninput : function(e)
    {
      // Hint for modifier
      this._fromInput = true;

      this.setValue(this._field.getComputedValue());
      
      // clear selection on input change
      // TODO [rst] trigger selection change
      var vSelected = this.getSelected();
      if( vSelected && vSelected.getLabel() != this.getValue() ) {
      	this.resetSelected();
      }

//      // be sure that the found item is in view
//      if (this.getPopup().isSeeable() && this.getSelected()) {
//        this.getSelected().scrollIntoView();
//      }

      delete this._fromInput;
    },
    // == END MODIFIED QX COPY ==

    // == BEGIN MODIFIED QX COPY ==
    _applyValue : function(value, old)
    {
      this._fromValue = true;

      // only do this if we called setValue seperatly
      // and not from the event "input".
      if (!this._fromInput)
      {
        if (this._field.getValue() == value) {
          this._field.setValue(null);
        }

        this._field.setValue(value);
      }

//      // only do this if we called setValue seperatly
//      // and not from the property "selected".
//      if (!this._fromSelected)
//      {
//        // inform selected property
//        var vSelItem = this._list.findStringExact(value);
//
//        // ignore disabled items
//        if (vSelItem != null && !vSelItem.getEnabled()) {
//          vSelItem = null;
//        }
//
//        this.setSelected(vSelItem);
//      }

      // reset hint
      delete this._fromValue;
    }
    // == END MODIFIED QX COPY ==
    
  }

} );
