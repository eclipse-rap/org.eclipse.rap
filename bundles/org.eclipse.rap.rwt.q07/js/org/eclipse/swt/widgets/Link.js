/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/

/**
 * This class provides the client-side implementation for
 * org.eclipse.swt.widgets.Link
 */
qx.Class.define( "org.eclipse.swt.widgets.Link", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "link" );
    // Default values
    this._text = "";
    this._hasSelectionListener = false;
    this._hyperlinksHaveListeners = false;
    this._linkColor;
    // innerTab handling
    this._currentLinkFocused = -1;
    this._linksCount = 0;        
    //
    this._link = new qx.ui.embed.HtmlEmbed();
    this._link.setAppearance( "link-text" );
    this.add( this._link );
    //
    this.setSelectable( false );
    this.setHideFocus( true );
    //
    this.__onMouseDown = qx.lang.Function.bindEvent( this._onMouseDown, this );
    this.__onKeyDown = qx.lang.Function.bindEvent( this._onKeyDown, this );
    //
    this.addEventListener( "appear", this._onAppear, this );
    this.addEventListener( "changeEnabled", this._onChangeEnabled, this );
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    // Event listener used for inner TabIndex change
    this.addEventListener( "keypress", this._onKeyPress );
    this.addEventListener( "focusout", this._onFocusOut );
    //
    this._link.addEventListener( "changeHtml", this._onChangeHtml, this );
  },

  destruct : function() {
    this._removeEventListeners();
    delete this.__onMouseDown;
    delete this.__onKeyDown;
    this.removeEventListener( "appear", this._onAppear, this );
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "changeEnabled", this._onChangeEnabled, this );
    this.removeEventListener( "keypress", this._onKeyPress );
    this.removeEventListener( "focusout", this._onFocusOut );
    this._link.removeEventListener( "changeHtml", this._onChangeHtml, this );
    this._link.dispose();
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
    
  	_onAppear : function( evt ) {
      this._link.setTabIndex( -1 );
      this._link.setHideFocus( true );
      this._applyHyperlinksStyleProperties();
      this._addEventListeners();
  	},
  	
  	_onChangeHtml : function( evt ) {
      this._applyHyperlinksStyleProperties();
      this._addEventListeners();
  	},
  	
  	_applyTextColor : function( value, old ) {
      this.base( arguments, value, old );
      var themeValues 
        = new org.eclipse.swt.theme.ThemeValues( this._getStates() );
      this._linkColor = themeValues.getCssColor( "Link-Hyperlink", "color" );
      themeValues.dispose();
      this._applyHyperlinksStyleProperties();
  	},
  	
  	_onChangeEnabled : function( evt ) {      
      this._applyHyperlinksStyleProperties();
      this._changeHyperlinksTabIndexProperty();
  	},
  	
  	_getStates : function() {
      if( !this.__states ) {
        this.__states = {};
      }
      return this.__states;
  	},
    
    addState : function( state ) {
      this.base( arguments, state );
      this._link.addState( state );
    },
    
    removeState : function( state ) {
      this.base( arguments, state );
      this._link.removeState( state );
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },
       
    addText : function( text ) {
      this._text += text;
    },

    addLink : function( text, index ) {
      this._text += "<span tabIndex=\"1\" ";
      this._text += "style=\"";
      this._text += "text-decoration:underline; ";
      this._text += "\" ";
      this._text += "id=\"" + index + "\"";
      this._text += ">";
      this._text += text;
      this._text += "</span>";
      this._linksCount++;
    },
    
    applyText : function() {
      this._link.setHtml( this._text );
      if ( this._linksCount == 0 ) {
        this.setTabIndex( -1 );
      } else {
        this.setTabIndex( 1 );
      }
    },
    
    clear : function() {
      this._removeEventListeners();
      this._text = "";
      this._linksCount = 0;
    },
    
    _applyHyperlinksStyleProperties : function() {
      var linkElement = this.getElement();
      if( linkElement ) {
        var hyperlinks = linkElement.getElementsByTagName( "span" );
        for( i = 0; i < hyperlinks.length; i++ ) {
          if( this._linkColor ) {
            if( this.isEnabled() ) {
      	      hyperlinks[ i ].style.color = this._linkColor;
            } else {
              hyperlinks[ i ].style.color = "";
            }  
      	  }
      	  if( this.isEnabled() ) {
      	  	hyperlinks[ i ].style.cursor = "pointer";
      	  } else {
      	  	hyperlinks[ i ].style.cursor = "default";
      	  }
        }
      }
    },
    
    _changeHyperlinksTabIndexProperty : function() {
      var linkElement = this.getElement();
      if( linkElement ) {
        var hyperlinks = linkElement.getElementsByTagName( "span" );
        for( i = 0; i < hyperlinks.length; i++ ) {
          if( this.isEnabled() ) {
            hyperlinks[ i ].tabIndex = "1";
          } else {
            hyperlinks[ i ].tabIndex = "-1";
          }
        }
      }
    },
    
    _addEventListeners : function() {
      var linkElement = this.getElement();
      if( linkElement && !this._hyperlinksHaveListeners ) {
        var hyperlinks = linkElement.getElementsByTagName( "span" );
        for( i = 0; i < hyperlinks.length; i++ ) {
          qx.html.EventRegistration.addEventListener( hyperlinks[ i ], 
                                                      "mousedown", 
                                                      this.__onMouseDown );
          qx.html.EventRegistration.addEventListener( hyperlinks[ i ], 
                                                      "keydown", 
                                                      this.__onKeyDown );
        }
        this._hyperlinksHaveListeners = true;
      }
    },
    
    _removeEventListeners : function() {
      var linkElement = this.getElement();
      if( linkElement && this._hyperlinksHaveListeners ) {
        var hyperlinks = linkElement.getElementsByTagName( "span" );
        for( i = 0; i < hyperlinks.length; i++ ) {
          qx.html.EventRegistration.removeEventListener( hyperlinks[ i ], 
                                                         "mousedown", 
                                                         this.__onMouseDown );
          qx.html.EventRegistration.removeEventListener( hyperlinks[ i ], 
                                                         "keydown", 
                                                         this.__onKeyDown );
        }
        this._hyperlinksHaveListeners = false;
      }
    },
    
    _onMouseDown : function( e ) {
      var target = this._getEventTarget( e );
      var index = target.id;
      this._currentLinkFocused = index;
      target.focus();
      target.style.outline = "1px dotted";
      var leftBtnPressed = this._isLeftMouseButtonPressed( e );
      if( this.isEnabled() && leftBtnPressed ) {
        this._sendChanges( index );
      }
    },
    
    _isLeftMouseButtonPressed : function( e ) {
      var leftBtnPressed;
      if( e.which ) {
        leftBtnPressed = ( e.which == 1 );
      } else if ( e.button ) {
        if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
          leftBtnPressed = ( e.button == 1 );
        } else {
          leftBtnPressed = ( e.button == 0 );
        }
      }
      return leftBtnPressed;
    },
    
    _onKeyDown : function( e ) {
      if( this.isEnabled() && e.keyCode == 13 ) {
        var target = this._getEventTarget( e );
        var index = target.id;
        this._sendChanges( index );
      }
    },
    
    _getEventTarget : function( e ) {
      var target;
      if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        target = window.event.srcElement;
      } else {
        target = e.target;
      }
      return target;
    },
    
    // Override of the _ontabfocus method from qx.ui.core.Widget 
    _ontabfocus : function() {
      if( this._currentLinkFocused == -1 && this._linksCount > 0 ) {
        var linkElement = this.getElement();
        if( linkElement ) {
          var hyperlinks = linkElement.getElementsByTagName( "span" );
          hyperlinks[ 0 ].focus();
          hyperlinks[ 0 ].style.outline = "1px dotted";
          this._currentLinkFocused = 0;
        }
      }
    },
    
    _onKeyPress : function( evt ) {
      if(    this.isFocused() 
          && evt.getKeyIdentifier() == "Tab" 
          && this._linksCount > 0 )
      {
        if(    !evt.isShiftPressed()
            && this._currentLinkFocused >= 0
            && this._currentLinkFocused < ( this._linksCount - 1 ) )
        {
          evt.stopPropagation();
          evt.preventDefault();
          this._currentLinkFocused++;
          this._focusLinkByID( this._currentLinkFocused, 
                               this._currentLinkFocused - 1 );
        } else if(    !evt.isShiftPressed()
                   && this._currentLinkFocused == -1 )
        {
          evt.stopPropagation();
          evt.preventDefault();
          var linkElement = this.getElement();
          if( linkElement ) {
            var hyperlinks = linkElement.getElementsByTagName( "span" );
            hyperlinks[ 0 ].focus();
            hyperlinks[ 0 ].style.outline = "1px dotted";
            this._currentLinkFocused = 0;
          }
        } else if(    evt.isShiftPressed()
                   && this._currentLinkFocused > 0
                   && this._currentLinkFocused <= ( this._linksCount - 1 ) )
        {
          evt.stopPropagation();
          evt.preventDefault();
          this._currentLinkFocused--;
          this._focusLinkByID( this._currentLinkFocused, 
                               this._currentLinkFocused + 1 );
        }    
      }
    },
    
    _focusLinkByID : function( id, old ) {
      var linkElement = this.getElement();
      if( linkElement ) {
        var hyperlinks = linkElement.getElementsByTagName( "span" );
        hyperlinks[ old ].blur();
        hyperlinks[ old ].style.outline = "none";
        hyperlinks[ id ].focus();
        hyperlinks[ id ].style.outline = "1px dotted";
      }
    },
    
    _onFocusOut : function( evt ) {
      var linkElement = this.getElement();
      if( linkElement ) {
        var hyperlinks = linkElement.getElementsByTagName( "span" );
        if( this._currentLinkFocused >= 0 ) {
          hyperlinks[ this._currentLinkFocused ].blur();
          hyperlinks[ this._currentLinkFocused ].style.outline = "none";
        }
      }
      this._currentLinkFocused = -1;
    },
    
    _sendChanges : function( index ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        if( this._hasSelectionListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          req.addEvent( "org.eclipse.swt.events.widgetSelected.index", index );
          req.send();
        }
      }
    }
  }
} );
