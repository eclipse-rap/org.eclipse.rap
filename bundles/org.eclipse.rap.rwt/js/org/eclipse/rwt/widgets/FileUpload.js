/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.widgets.FileUpload", {

  extend : org.eclipse.rwt.widgets.Button,

  construct : function() {
    this.base( arguments, "push" );
    this.addState( "rwt_PUSH" ); // temporary
    this.addEventListener( "insertDom", this._layoutInputElement, this );
    this.addEventListener( "elementOver", this._onMouseOverElement, this );
    this._formElement = null;
    this._inputElement = null;
    this._iframe = null;
    this._cursor = "";
    this.__onValueChange = qx.lang.Function.bind( this._onValueChange, this );    
    this.setEnableElementFocus( false );
    this._createIframeWidget();
  },
  
  destruct : function() {
    this._formElement = null;
    this._inputElement = null;    
  },
  
  members : {

    submit : function( url ) {
      if( typeof url !== "string" ) {
        throw new Error( "No url given!" );
      } 
      if( this._getFileName() === "" ) {
        throw new Error( "No file selected!" );
      }
      if( this._formElement === null ) {
        throw new Error( "Form element not created!" );
      }
      this._formElement.setAttribute( "action", url );
      this._formElement.submit();
    },

    destroy : function() {
      this.base( arguments );
      this._iframe.destroy();
    },

    ////////////
    // Internals

    _createSubelements : function() {
      this.base( arguments );
      // NOTE: MultiCellWidget uses innerHTML, therefore this must be done here:
      if( this._formElement === null ) {
        this._createFormElement();
        this._createInputElement();
      } else {
        this._getTargetNode().appendChild( this._formElement );
        var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
        if( isMshtml ) {
          this._formElement.appendChild( this._inputElement );
        }
      }
    },

    _createFormElement : function() {
      this._formElement = document.createElement( "form" );
      this._formElement.setAttribute( "target", this._getFrameName() );
      this._formElement.setAttribute( "method", "POST" );
      var isMshtml = qx.core.Variant.isSet( "qx.client", "mshtml" );
      if( isMshtml ) {
        this._formElement.setAttribute( "encoding", "multipart/form-data" );        
      } else {
        this._formElement.setAttribute( "enctype", "multipart/form-data" );
      }
      this._getTargetNode().appendChild( this._formElement );
    },

    _createInputElement : function() {
      this._inputElement = document.createElement( "input" );
      this._inputElement.style.position = "absolute";
      this._inputElement.setAttribute( "type", "file" );
      this._inputElement.setAttribute( "name", "file" );
      this._inputElement.setAttribute( "size", "1" );
      this._inputElement.style.cursor = this._cursor;
      this._inputElement.onchange = this.__onValueChange;
      org.eclipse.rwt.HtmlUtil.setOpacity( this._inputElement, 0 );
      this._formElement.appendChild( this._inputElement );
    },

    _createIframeWidget : function() {
      this._iframe = new qx.ui.embed.Iframe();
      // NOTE: The frame-content should only be changed by the form:
      this._iframe.setSource( "about:blank" );
      this._iframe.setVisibility( false );
      this._iframe.setWidth( 0 );
      this._iframe.setHeight( 0 );
      this._iframe.setFrameName( this._getFrameName() );
      this._iframe.addToDocument();
    },

    _onValueChange : function( event ) {
      // TODO [tb] : implement setHasValueChangedListener?
      var fileName = this._formatFileName( this._getFileName() );
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var req = org.eclipse.swt.Request.getInstance();
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        req.addParameter( id + ".fileName", fileName );
        req.send();
      }
    },

    _getFileName : function() {
      return this._inputElement.value;
    },

    ////////////
    // Layouting

    _layoutPost : function( changes ) {
      this.base( arguments, changes );
      if( changes.width || changes.height ) {
        this._layoutInputElement();
      }
    },

    _layoutInputElement : function() {
      if( this.getEnabled() && this.isInDom() ) {
        //Assumed maximal padding between input button and input outer dimensions:
        var padding = 10; 
        this._layoutInputElementHorizontal( padding );
        this._layoutInputElementVertical( padding );
      }
    },

    _layoutInputElementHorizontal : function( padding ) {
      // Respect assumed maximal relative width of the input textfield:
      var inputButtonPercentage = 0.6;
      // NOTE : This is how inputWidth is calculated:
      // widgetWidth + padding * 2 = 0.6 * inputWidth
      // inputWidth = ( widthWidth + padding * 2 ) / 0.6
      var widgetWidth = this.getBoxWidth();
      var inputTargetWidth =   ( widgetWidth + padding * 2 ) 
                             / ( inputButtonPercentage );
      var fontSize = inputTargetWidth / 10; 
      this._inputElement.style.fontSize = fontSize;
      var iterations = 0;
      while( this._inputElement.offsetWidth <= inputTargetWidth ) {
        fontSize += 10;
        this._inputElement.style.fontSize = fontSize;
        iterations++;
        if( iterations > 100 ) {
          // crash the rap-application instead of freezing the browser.
          var msg = "Failed to force input-element width.";
          throw new Error( msg );
        }
      }
      var actualInputWidth = this._inputElement.offsetWidth;
      var inputLeft = widgetWidth - actualInputWidth + padding;
      this._inputElement.style.left = inputLeft + "px";
    },

    _layoutInputElementVertical : function( padding ) {
      var widgetHeight = this.getBoxHeight();
      this._inputElement.style.height = ( widgetHeight + padding * 2 ) + "px";
      this._inputElement.style.top = ( padding * -1 ) + "px";
    },

    setStyleProperty : function( propName, value ) {
      if( propName === "cursor" ) {
        this._cursor = value;
        if( this._inputElement != null ) {
          // NOTE : This will have no effect in firefox.
          this._inputElement.style.cursor = value;
        }
      } else {
        this.base( arguments, propName, value );
      }
    },

    _applyEnabled : function( value, oldValue ) {
      this.base( arguments, value, oldValue );
      if( this._inputElement ) {
        this._inputElement.style.display = value ? "" : "none";
        this._layoutInputElement();
      }
    },

    ////////////////
    // Mouse-control
    
    // NOTE : In contrast to other widgets, the border does not trigger the
    //        expected function, adapt state-behavior accordingly:
    
    _onMouseOver : function( event ) {
      if( event.getDomTarget() === this._inputElement ) {
        this.base( arguments, event );
      }
    },

    _onMouseOverElement : function( event ) {
      if( event.getDomTarget() === this._inputElement ) {
        this._onMouseOver( event );
      }
    },

    _onMouseDown : function( event ) {
      if( event.getDomTarget() === this._inputElement ) {
        this.base( arguments, event );
      }
      if( org.eclipse.rwt.Client.getBrowser() === "chrome") {
        // Chrome looses keyboard control on mouse-focus, see _ontabfocus.
        this._onBlur();
      }      
    },

    _onMouseUp : function( event ) {
      if( event.getDomTarget() === this._inputElement || this.hasState( "abandoned" ) ) {
        this.base( arguments, event );
      }
    },

    /////////////////////////
    // Focus/keyboard-control

    // NOTE : Since the browse-button can't be triggered programatically,
    //        supporting native keyboard-control is necessary, which is a bit
    //        problematic.
    _onFocus : function( event ) {
      this.base( arguments, event );
      this._inputElement.focus();
    },

    // NOTE : key-handling interferes with native keyboard control. This 
    //        disables the "pressed" state, but is still the lesser evil.
    _onKeyDown : qx.lang.Function.returnTrue,
    _onKeyUp : qx.lang.Function.returnTrue,

    // NOTE : In chrome (windows?), the input-element needs to be focused using
    //        tabulator for keyboard control to work. To minimize confusion,
    //        do not display focus frame in other cases.
    _ontabfocus : function() {
      if( org.eclipse.rwt.Client.getBrowser() === "chrome" ) {
        this._showFocusIndicator( true );
      }
    },

    _showFocusIndicator : function( allow ) {
      var isChrome = org.eclipse.rwt.Client.getBrowser() === "chrome";
      if( !isChrome || allow ) {
        this.base( arguments );
      }
    },

    /////////
    // Helper

    _formatFileName : function( fileName ) {
      var result = fileName;
      if( result.indexOf( "\\" ) != -1 ) {
        result = result.substr( result.lastIndexOf( "\\" ) + 1 );
      } else if( result.indexOf( "/" ) != -1 ) {
        result = result.substr( result.lastIndexOf( "/" ) + 1 );
      }
      return result;
    },

    _getFrameName : function() {
      return "FileUpload_" + this.toHashCode();
    }

  }

} );
