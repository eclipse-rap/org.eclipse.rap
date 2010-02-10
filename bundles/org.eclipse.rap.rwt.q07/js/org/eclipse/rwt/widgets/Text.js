/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.widgets.Text", {

  extend : qx.ui.form.TextField,

  members : {
    
    setPasswordMode : function( value ) {
      var type = value ? "password" : "text";
      if( this._inputType != type ) {
        this._inputType = type;
        if( this._isCreated ) {
          if( qx.core.Client.getEngine() == "mshtml" ) {
            this._reCreateInputField();
          } else {
            this._inputElement.type = this._inputType;
          }        
        }
      }
    },
    
    _reCreateInputField : function() {
      var selectionStart = this.getSelectionStart();
      var selectionLength = this.getSelectionLength();
      this._inputElement.parentNode.removeChild( this._inputElement );
      this._inputElement.onpropertychange = null;
      this._inputElement = null;
      this._firstInputFixApplied = false;
      this._applyElement( this.getElement(), null );
      this._afterAppear();
      this._postApply();
      this._applyFocused( this.getFocused() );
      this.setSelectionStart( selectionStart );
      this.setSelectionLength( selectionLength );
    }
    
  }

} );