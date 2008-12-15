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
      this._userCursor = null;
      this.addEventListener( "changeFont", this._rwt_onChangeFont, this );
      this.addEventListener( "changeTextColor", this._rwt_onChangeTextColor, this );
      this.addEventListener( "changeBackgroundColor", this._rwt_onChangeBackgoundColor, this );
      this.addEventListener( "changeValue", this._rwt_onChangeValue, this );
      this.addEventListener( "changeEditable", this._rwt_onChangeEditable, this );
      this._popup.addEventListener( "appear", this._rwt_onPopupAppear, this );
      this._popup.addEventListener( "disappear", this._rwt_onPopupDisappear, this );
      this._button.addEventListener( "mouseover", this._rwt_onButtonMouseOver, this );
      this._button.addEventListener( "mouseout", this._rwt_onButtonMouseOut, this );
    },

    rap_reset : function() {
      this.removeEventListener( "changeFont", this._rwt_onChangeFont, this );
      this.removeEventListener( "changeTextColor", this._rwt_onChangeTextColor, this );
      this.removeEventListener( "changeBackgroundColor", this._rwt_onChangeBackgoundColor, this );
      this.removeEventListener( "changeValue", this._rwt_onChangeValue, this );
      this.removeEventListener( "changeEditable", this._rwt_onChangeEditable, this );
      this._popup.removeEventListener( "appear", this._rwt_onPopupAppear, this );
      this._popup.removeEventListener( "disappear", this._rwt_onPopupDisappear, this );
      this._button.removeEventListener( "mouseover", this._rwt_onButtonMouseOver, this );
      this._button.removeEventListener( "mouseout", this._rwt_onButtonMouseOut, this );
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
      var value = evt.getValue();
      combo._field.setTextColor( value );
      combo._list.setTextColor( value );
    },

    // workaround for missing property propagation in qx ComboBox
    _rwt_onChangeBackgoundColor : function( evt ) {
      var combo = evt.getTarget();
      var value = evt.getValue();
      combo._field.setBackgroundColor( value );
      combo._list.setBackgroundColor( value );
    },

    _rwt_onChangeValue : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && evt.getValue() != null ) {
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
      if( this._userCursor == null ) {
        f.setCursor( "default" );
      }
      f.setSelectable( false );
    },

    // Enable text field when popup closes
    _rwt_onPopupDisappear : function( evt ) {
      var editable = this.getEditable();
      var f = this.getField();
      f.setReadOnly( !editable );
      if( this._userCursor == null ) {
        f.setCursor( editable ? null : "default" );
      }
      f.setSelectable( editable );
      this._button.removeState( "over" );
    },

    _rwt_onButtonMouseOver : function( evt ) {
      this._button.addState( "over" );
    },

    _rwt_onButtonMouseOut : function( evt ) {
      this._button.removeState( "over" );
    },

    _rwt_onChangeEditable : function( evt ) {
      if( this._userCursor != null ) {
        this.getField().setCursor( this._userCursor );
      }  
    },  

    _applyCursor : function( value, old ) {
      this.base( arguments, value, old );
      this._userCursor = value;
      if( value ) {
        this.getField().setCursor( value );
        this.getButton().setCursor( value );
        this.getList().setCursor( value );
      } else {
        this.getField().resetCursor();
        this.getButton().resetCursor();
        this.getList().resetCursor();
      }
    }
  }
} );
