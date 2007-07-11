/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
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
      this.getManager().addEventListener( "change", this._onChangeValue, this );
      this._textfield.addEventListener( "keyinput", this._onChangeValue, this );
      this._textfield.addEventListener( "blur", this._onChangeValue, this );
      this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
    },
    
    rap_reset : function() {
      this.getManager().removeEventListener( "change", this._onChangeValue, this );
      this._textfield.removeEventListener( "keyinput", this._onChangeValue, this );
      this._textfield.removeEventListener( "blur", this._onChangeValue, this );
      this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
    },
    
    setFont : function( value ) {
      this._textfield.setFont( value );
    },
    
    setTabIndex : function( value ) {
      this._textfield.setTabIndex( value );
    },
    
    setHasModifyListener : function( value ) {
      this._hasModifyListener = value;      
    },
    
    _onChangeValue : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && !this._isModified ) {
        this._isModified = true;
        var req = org.eclipse.swt.Request.getInstance();
        req.addEventListener( "send", this._onSend, this );
        if( this._hasModifyListener ) {
          this._addModifyTextEvent();
          qx.client.Timer.once( this._sendModifyText, this, 500 );
        }
      }
    },

    // TODO [rst] workaround: setting enabled to false still leaves the buttons
    //      enabled
    _onChangeEnabled : function( evt ) {
      var enabled = evt.getData();
      this._upbutton.setEnabled( enabled && this.getValue() < this.getMax() );
      this._downbutton.setEnabled( enabled && this.getValue() > this.getMin() );
    },

    _addModifyTextEvent : function() {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var id = widgetManager.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.modifyText", id );
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
