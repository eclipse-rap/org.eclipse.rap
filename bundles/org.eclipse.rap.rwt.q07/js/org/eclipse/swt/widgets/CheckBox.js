/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

/**
 * This class provides the client-side implementation for
 * org.eclipse.swt.widgets.Button widget with SWT.CHECK style.
 */
qx.Class.define( "org.eclipse.swt.widgets.CheckBox", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "check-box" );

    // Default values
    this._selected = false;
    this._grayed = false;
    this._text = "";
    this._image = null;
    this._hasSelectionListener = false;

    // CheckButton icon
    this._icon = new qx.ui.basic.Image;
    this._icon.setAppearance( "check-box-icon" );
    this.add( this._icon );

    // CheckButton content - image and text
    this._content = new qx.ui.basic.Atom( "(empty)", this._image );
    this._content.getLabelObject().setAppearance( "label-graytext" );
    this._content.setLeft( 17 );
    this._content.setLabel( this._text );
    this._content.setHorizontalChildrenAlign( "center" );
    this._content.setVerticalChildrenAlign( "top" );
    this.add( this._content );

    // Add events listeners
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "click", this._onclick );
    this.addEventListener( "mouseover", this._onmouseover );
    this.addEventListener( "mouseout", this._onmouseout );
    this.addEventListener( "keyup", this._onkeyup );
  },

  destruct : function() {
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "click", this._onclick );
    this.removeEventListener( "mouseover", this._onmouseover );
    this.removeEventListener( "mouseout", this._onmouseout );
    this.removeEventListener( "keyup", this._onkeyup );
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

    _sendChanges : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".selection", this._selected );
        if( this._hasSelectionListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          req.send();
        }
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

    // Event listeners
    _onclick : function( evt ) {
      this.setSelection( !this._selected );
    },

    _onmouseover : function( evt ) {
      this._icon.addState( "over" );
      this.addState( "over" );
    },

    _onmouseout : function( evt ) {
      this._icon.removeState( "over" );
      this.removeState( "over" );
    },

    // Toggles property "checked" when "Space" key was pressed.
    _onkeyup : function( evt ) {
      if ( evt.getKeyIdentifier() == "Space" ) {
        this.setSelection( !this._selected );
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
      this._selected = value;
      if( this._selected ) {
        this._icon.addState( "selected" );
        this.addState( "selected" );
      } else {
        this._icon.removeState( "selected" );
        this.removeState( "selected" );
      }
      this._sendChanges();
    },

    setGrayed : function( value ) {
      this._grayed = value;
      if( this._grayed ) {
        this._icon.addState( "grayed" );
      } else {
        this._icon.removeState( "grayed" );
      }
    }
  }
} );
