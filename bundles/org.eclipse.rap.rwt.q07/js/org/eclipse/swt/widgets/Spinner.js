/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.Spinner", {
  extend : qx.ui.form.Spinner,

  construct : function() {
    this.base( arguments );
    this.rap_init();
  },

  destruct : function() {
    this.rap_reset();
  },

  members : {
    
    rap_init : function() {
      this._isModified = false;
      this._hasModifyListener = false;
      this._hasSelectionListener = false;
      this.setWrap( false );
      this.getManager().addEventListener( "change", this._onChangeValue, this );
      this._textfield.addEventListener( "keyinput", this._onChangeValue, this );
      this._textfield.addEventListener( "blur", this._onChangeValue, this );
      this._textfield.addEventListener( "keydown", this._onKeyDown, this );
      this._textfield.setTabIndex( -1 );
      this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
    },
    
    rap_reset : function() {
      this.getManager().removeEventListener( "change", this._onChangeValue, this );
      this._textfield.removeEventListener( "keyinput", this._onChangeValue, this );
      this._textfield.removeEventListener( "blur", this._onChangeValue, this );
      this._textfield.removeEventListener( "keydown", this._onKeyDown, this );
      this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
    },
    
    setFont : function( value ) {
      this._textfield.setFont( value );
    },

    _applyCursor : function( value, old ) {
      this.base( arguments, value, old );
      if( value ) {
        this._upbutton.setCursor( value );
        this._downbutton.setCursor( value );
        this._textfield.setCursor( value );
      } else {
        this._upbutton.resetCursor();
        this._downbutton.resetCursor();
        this._textfield.resetCursor();
      }
    },

    setHasModifyListener : function( value ) {
      this._hasModifyListener = value;      
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;      
    },
    
    _visualizeFocus : function() {
      this._textfield._visualizeFocus();
      if( this._textfield.isCreated() ) {
        this._textfield.selectAll();
      }
    },
      
    _visualizeBlur : function() {
      // setSelectionLength( 0 ) for TextField - needed for IE
      this._textfield.setSelectionLength( 0 );
      this._textfield._visualizeBlur();
    },

    _onChangeValue : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && !this._isModified ) {
        this._isModified = true;
        var req = org.eclipse.swt.Request.getInstance();
        req.addEventListener( "send", this._onSend, this );
        if( this._hasSelectionListener ) {
          this._addModifyTextEvent();
          this._sendWidgetSelected();
        } else if( this._hasModifyListener ) {
          this._addModifyTextEvent();
          qx.client.Timer.once( this._sendModifyText, this, 500 );
        }
      }
    },

    // TODO [rst] workaround: setting enabled to false still leaves the buttons
    //      enabled
    _onChangeEnabled : function( evt ) {
      var enabled = evt.getValue();
      this._upbutton.setEnabled( enabled && this.getValue() < this.getMax() );
      this._downbutton.setEnabled( enabled && this.getValue() > this.getMin() );
    },

    _onKeyDown : function( event ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if(    event.getKeyIdentifier() == "Enter"
            && !event.isShiftPressed()
            && !event.isAltPressed()
            && !event.isCtrlPressed()
            && !event.isMetaPressed()
            && this._hasSelectionListener )
        {
          event.stopPropagation();
          this._sendWidgetDefaultSelected();
        }
      }
    },

    _addModifyTextEvent : function() {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.modifyText", id );
    },

    _sendWidgetSelected : function() {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
      req.send();
    },

    _sendWidgetDefaultSelected : function() {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
      req.send();
    },

    _onSend : function( evt ) {
      this._isModified = false;
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget(this);
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".selection", this.getValue() );
      req.removeEventListener( "send", this._onSend, this );
    },

    _sendModifyText : function( evt ) {
      if( this._isModified ) {
        var req = org.eclipse.swt.Request.getInstance();
        req.send();
        this._isModified = false;
      }
    }
  }
});
