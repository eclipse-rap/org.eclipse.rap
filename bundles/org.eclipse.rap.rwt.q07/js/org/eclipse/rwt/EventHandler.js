/*******************************************************************************
 *  Copyright: 2004-2010 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.EventHandler", {
  type : "static",

  statics : {
    _lastMouseEventType : null,
    _lastMouseDown : false,
    _lastMouseEventDate : 0,
    _focused : false,

    _filter : {},
    _allowContextMenu : qx.lang.Function.returnFalse,
    _captureWidget : null,
    _focusRoot : null,
    _menuManager : null,
    
    ///////////////////
    // Public functions

    init : function() {
      var functionUtil = qx.lang.Function;
      this.__onmouseevent = functionUtil.bind( this._onmouseevent, this );
      this.__ondragevent = functionUtil.bind( this._ondragevent, this );
      this.__onselectevent = functionUtil.bind( this._onselectevent, this );
      this.__onwindowblur = functionUtil.bind( this._onwindowblur, this );
      this.__onwindowfocus = functionUtil.bind( this._onwindowfocus, this );
      this.__onwindowresize = functionUtil.bind( this._onwindowresize, this );
      org.eclipse.rwt.KeyEventHandler.init();
      this.__keyHandler = 
        org.eclipse.rwt.KeyEventHandler.__onKeyEvent;      
    },

    cleanUp : function() {
      delete this.__onmouseevent; 
      delete this.__ondragevent; 
      delete this.__onselectevent;
      delete this.__onwindowblur; 
      delete this.__onwindowfocus; 
      delete this.__onwindowresize;
      delete this._lastMouseEventType;
      delete this._lastMouseDown;
      delete this._lastMouseEventDate;
      delete this._lastMouseDownDomTarget;
      delete this._lastMouseDownDispatchTarget;
      org.eclipse.rwt.KeyEventHandler.cleanUp();
    },

    attachEvents : function() {
      var eventUtil = qx.html.EventRegistration;
      this.attachEventTypes( this._mouseEventTypes, this.__onmouseevent );
      this.attachEventTypes( this._dragEventTypes, this.__ondragevent );
      this.attachEventTypes( this._keyEventTypes, this.__keyHandler );
      eventUtil.addEventListener( window, "blur", this.__onwindowblur );
      eventUtil.addEventListener( window, "focus", this.__onwindowfocus );
      eventUtil.addEventListener( window, "resize", this.__onwindowresize );
      document.body.onselect = this.__onselectevent;
      document.onselectstart = this.__onselectevent;
      document.onselectionchange = this.__onselectevent;
    },

    detachEvents : function() {
      var eventUtil = qx.html.EventRegistration;
      this.detachEventTypes( this._mouseEventTypes, this.__onmouseevent);
      this.detachEventTypes( this._dragEventTypes, this.__ondragevent);
      this.detachEventTypes( this._keyEventTypes, this.__keyHandler );
      eventUtil.removeEventListener( window, "blur", this.__onwindowblur );
      eventUtil.removeEventListener( window, "focus", this.__onwindowfocus );
      eventUtil.removeEventListener( window, "resize", this.__onwindowresize );
      document.body.onselect = null;
      document.onselectstart = null;
      document.onselectionchange = null;
    },

    setCaptureWidget : function( widget ) {
      if( this._captureWidget !== widget ) {
        if( this._captureWidget !== null ) {
          this._captureWidget.setCapture( false );
        }
        this._captureWidget = widget;
        if( widget != null ) {
          widget.setCapture( true );
        }
      }
    },
    
    getCaptureWidget : function() {
      return this._captureWidget;
    },

    setFocusRoot : function( widget ) {
      if( widget !== this._focusRoot ) {
        if( this._focusRoot !== null ) {
          this._focusRoot.setFocusedChild( null );
        }
        this._focusRoot = widget;
        if( widget !== null && widget.getFocusedChild() === null ) {
          widget.setFocusedChild( widget );
        }
      }
    },
    
    getFocusRoot : function() {
      return this._focusRoot;
    },

    /**
     * Sets a callback-function to decide if the native context- 
     * menu is displayed. It will be called on DOM-events of the type 
     * "contextmenu". The target-Widget of the event will be given as
     * the first argument, the dom-target as the second. 
     * It must return a boolean. Null is not allowed.
     *
     */    
    setAllowContextMenu : function( func ) {
      this._allowContextMenu = func;
    },
    
    setMenuManager : function( manager ) {
      this._menuManager = manager;
    },
    
    getMenuManager : function( manager ) {
      return this._menuManager; 
    },

    setMouseEventFilter : function( filter, context ) {
      this._filter[ "mouseevent" ] = [ filter, context ];
    },

    ///////////////////
    // Global Handlers:
    
    _onkeyevent_post : function( vDomEvent, 
                                 vType, 
                                 vKeyCode, 
                                 vCharCode, 
                                 vKeyIdentifier ) 
    {
      var vDomTarget = this.getDomTarget( vDomEvent );
      var vTarget = this._getKeyEventTarget();
      var vKeyEventObject = new qx.event.type.KeyEvent( vType, 
                                                        vDomEvent, 
                                                        vDomTarget, 
                                                        vTarget, 
                                                        null, 
                                                        vKeyCode, 
                                                        vCharCode, 
                                                        vKeyIdentifier );

      if( vTarget != null && vTarget.getEnabled() ) {
        switch( vKeyIdentifier ) {
          case "Escape":
          case "Tab":
            if ( this._menuManager != null ) {
              this._menuManager.update(vTarget, vType);
            }
          break;
        }

        if( vDomEvent.ctrlKey && vKeyIdentifier == "A" ) {
          switch( vDomTarget.tagName.toLowerCase() ) {
            case "input":
            case "textarea":
            case "iframe":
              // selection allowed
            break;
            default:
             this.stopDomEvent(vDomEvent);
            break;
          }
        }

        vTarget.dispatchEvent( vKeyEventObject );

        if( qx.Class.isDefined("qx.event.handler.DragAndDropHandler") ) {
          qx.event.handler.DragAndDropHandler.getInstance().handleKeyEvent( vKeyEventObject );
        }
      }

      // Cleanup Event Object
      vKeyEventObject.dispose();
    },
 
    _onmouseevent : function( event ) {
      var process = true;
      if( typeof this._filter[ "mouseevent" ] !== "undefined" ) {
        var context = this._filter[ "mouseevent" ][ 1 ];
        process = this._filter[ "mouseevent" ][ 0 ].call( context, event );
      }
      if( process ) {
        this._processMouseEvent( event ); 
      }
    },

    _processMouseEvent : qx.core.Variant.select("qx.client",  {
      "mshtml" : function() {
        var vDomEvent = this._getDomEvent( arguments );
        var vDomTarget = this.getDomTarget( vDomEvent );
        var vType = vDomEvent.type;
        if( vType == "mousemove" ) {
          if( this._mouseIsDown && vDomEvent.button == 0 ) {
            this._onmouseevent_post( vDomEvent, "mouseup", vDomTarget );
            this._mouseIsDown = false;
          }
        } else {
          if( vType == "mousedown" ) {
            this._mouseIsDown = true;
          } else if( vType == "mouseup" ) {
            this._mouseIsDown = false;
          }
          if(    vType == "mouseup" 
              && !this._lastMouseDown 
              && ( ( new Date() ).valueOf() - this._lastMouseEventDate ) < 250
          ) {
            // Fix MSHTML Mouseup, should be after a normal click 
            // or contextmenu event, like Mozilla does this
            this._onmouseevent_post( vDomEvent, "mousedown", vDomTarget );
          } else if (    vType == "dblclick" 
                      && this._lastMouseEventType == "mouseup" 
                      && ( ( new Date ).valueOf() - this._lastMouseEventDate ) < 250
          ) {
            // Fix MSHTML Doubleclick, should be after a normal click event, 
             // like Mozilla does this
            this._onmouseevent_post(vDomEvent, "click", vDomTarget);
          }  
          switch( vType ) {
            case "mousedown":
            case "mouseup":
            case "click":
            case "dblclick":
            case "contextmenu":
              this._lastMouseEventType = vType;
              this._lastMouseEventDate = ( new Date() ).valueOf();
              this._lastMouseDown = vType == "mousedown";
            break;
          }
        }
        this._onmouseevent_post(vDomEvent, vType, vDomTarget);
      },

      "default" : function( vDomEvent ) {
        var vDomTarget = this.getDomTarget( vDomEvent );
        var vType = vDomEvent.type;
        switch(vType) {
          case "  ":
            vType = "mousewheel";
          break;
          case "click":
          case "dblclick":
            // ignore click or dblclick events with other then the left mouse button
            if( vDomEvent.which !== 1 ) {
              return;
            }
        }
        this._onmouseevent_post( vDomEvent, vType, vDomTarget );
      }
    } ),

    _onmouseevent_post : function( vDomEvent, vType, vDomTarget ) {
      var vCaptureTarget = this.getCaptureWidget();
      var vOriginalTarget 
        = this.getOriginalTargetObject( vDomTarget );
      var vTarget = this.getTargetObject( null, vOriginalTarget, true );
      if( !vTarget ) {
        return;
      }
      var vDispatchTarget = vCaptureTarget ? vCaptureTarget : vTarget;
      var vFixClick 
        = this._onmouseevent_click_fix( vDomTarget, vType, vDispatchTarget );
      if(    vType == "contextmenu" 
          && !this._allowContextMenu( vOriginalTarget, vDomTarget ) ) {
       this.stopDomEvent( vDomEvent );
      }
      if( vTarget.getEnabled() && vType == "mousedown" ) {
        qx.event.handler.FocusHandler.mouseFocus = true;
        var vRoot = vTarget.getFocusRoot();
        if( vRoot ) {
          this.setFocusRoot( vRoot );
          var vFocusTarget = vTarget;
          while( !vFocusTarget.isFocusable() && vFocusTarget != vRoot ) {
            vFocusTarget = vFocusTarget.getParent();
          }
          // We need to focus first and active afterwards.
          // Otherwise the focus will activate another widget if the
          // active one is not tabable.
          vRoot.setFocusedChild( vFocusTarget );
          vRoot.setActiveChild( vTarget );
        }
      }
      // handle related target object
      if( vType == "mouseover" || vType == "mouseout" ) {
        var vRelatedTarget =this.getRelatedTargetObjectFromEvent( vDomEvent );
        var elementEventType = vType == "mouseover" ? "elementOver" : "elementOut";
        this._fireElementHoverEvents( elementEventType,
                                      vDomEvent,
                                      vDomTarget, 
                                      vTarget,
                                      vOriginalTarget, 
                                      vRelatedTarget,
                                      vDispatchTarget );
        // Ignore events where the related target and
        // the real target are equal - from our sight
        if( vRelatedTarget == vTarget ) {
          return;
        }
      }
      var vEventObject = new qx.event.type.MouseEvent( vType, 
                                                       vDomEvent, 
                                                       vDomTarget, 
                                                       vTarget, 
                                                       vOriginalTarget, 
                                                       vRelatedTarget );
      // Store last Event in MouseEvent Constructor. Needed for Tooltips, ...
      qx.event.type.MouseEvent.storeEventState( vEventObject );
      if( vDispatchTarget.getEnabled() ) {
        vDispatchTarget.dispatchEvent( vEventObject );
        this._onmouseevent_special_post( vType, 
                                         vTarget, 
                                         vOriginalTarget, 
                                         vDispatchTarget, 
                                         vEventObject, 
                                         vDomEvent );
      } else if( vType == "mouseover" ) {
        if( qx.Class.isDefined( "qx.ui.popup.ToolTipManager" ) ) {
          var toolTipManager = qx.ui.popup.ToolTipManager.getInstance();
          toolTipManager.handleMouseOver( vEventObject );
        }
      }
      vEventObject.dispose();
      qx.ui.core.Widget.flushGlobalQueues();
      // Fix Click (Gecko Bug, see above)
      if( vFixClick ) {
        this._onmouseevent_post( vDomEvent, 
                                 "click", 
                                 this._lastMouseDownDomTarget );
        this._lastMouseDownDomTarget = null;
        this._lastMouseDownDispatchTarget = null;
      }
    },
    
    _fireElementHoverEvents : function( type,
                                        domEvent,
                                        domTarget, 
                                        target,
                                        originalTarget, 
                                        relatedTarget,
                                        dispatchTarget )
    {
      if( dispatchTarget.getEnabled() ) {
        var eventObject = new qx.event.type.MouseEvent( type, 
                                                        domEvent, 
                                                        domTarget, 
                                                        target, 
                                                        originalTarget, 
                                                        relatedTarget );
        dispatchTarget.dispatchEvent( eventObject );
      }
    },

    _onmouseevent_special_post : function( vType, 
                                           vTarget, 
                                           vOriginalTarget, 
                                           vDispatchTarget, 
                                           vEventObject, 
                                           vDomEvent )
    {
      switch( vType ) {
        case "mousedown":
          if( qx.Class.isDefined( "qx.ui.popup.PopupManager" ) ) {
            qx.ui.popup.PopupManager.getInstance().update( vTarget );
          }
          if( this._menuManager != null ) {
            this._menuManager.update( vTarget, vType );
          }
          if( qx.Class.isDefined( "qx.ui.embed.IframeManager" ) ) {
            qx.ui.embed.IframeManager.getInstance().handleMouseDown( vEventObject );
          }
        break;
        case "mouseup":
          // Mouseup event should always hide, independed of target,
          //  so don't send a target
          if( this._menuManager != null ) {
            this._menuManager.update( vTarget, vType );
          }
          if( qx.Class.isDefined("qx.ui.embed.IframeManager" ) ) {
            qx.ui.embed.IframeManager.getInstance().handleMouseUp( vEventObject );
          }
        break;
        case "mouseover":
          if( qx.Class.isDefined("qx.ui.popup.ToolTipManager" ) ) {
            qx.ui.popup.ToolTipManager.getInstance().handleMouseOver( vEventObject );
          }
        break;
        case "mouseout":
          if( qx.Class.isDefined("qx.ui.popup.ToolTipManager" ) ) {
            qx.ui.popup.ToolTipManager.getInstance().handleMouseOut( vEventObject );
          }
        break;
      }
      this._ignoreWindowBlur = vType === "mousedown";
      if( qx.Class.isDefined("qx.event.handler.DragAndDropHandler" ) 
          && vTarget ) {
        qx.event.handler.DragAndDropHandler.getInstance().handleMouseEvent( vEventObject );
      }
    },

    _ondragevent : function( vEvent ) {
      if( !vEvent ) {
        vEvent = window.event;
      }
      this.stopDomEvent( vEvent );
    },

    ////////////////
    // SELECT EVENTS

    _onselectevent : function( ) {
      var e = this._getDomEvent( arguments );
      var target = this.getOriginalTargetObjectFromEvent( e );
      while( target )       {
        if( target.getSelectable() != null ) {
          if ( !target.getSelectable() ) {
           this.stopDomEvent( e );
          }
          break;
        }
        target = target.getParent();
      }
    },

    _onwindowblur : function( e ) {
      if (    !this._focused 
           || this._ignoreWindowBlur 
           || e.originalTarget != window ) {
        return;
      }
      this._focused = false;
      this.setCaptureWidget( null );
      if( qx.Class.isDefined( "qx.ui.popup.PopupManager" ) ) {
        qx.ui.popup.PopupManager.getInstance().update();
      }
      if ( this._menuManager ) {
        this._menuManager.update();
      }
      if( qx.Class.isDefined( "qx.event.handler.DragAndDropHandler" ) ) {
        qx.event.handler.DragAndDropHandler.getInstance().globalCancelDrag();
      }
      qx.ui.core.ClientDocument.getInstance().createDispatchEvent( "windowblur" );
    },

    _onwindowfocus : function( e ) {
      if( this._focused ) {
        return;
      }
      this._focused = true;
      qx.ui.core.ClientDocument.getInstance().createDispatchEvent( "windowfocus" );
    },

    _onwindowresize : function( e ) {
      qx.ui.core.ClientDocument.getInstance().createDispatchEvent( "windowresize" );
    },

    ///////////////
    // Helper-maps:

    _mouseEventTypes : [
      "mouseover", 
      "mousemove", 
      "mouseout", 
      "mousedown", 
      "mouseup", 
      "click",
      "dblclick", 
      "contextmenu",
      qx.core.Variant.isSet( "qx.client", "gecko" ) ? "DOMMouseScroll" : "mousewheel"
    ],

    _keyEventTypes : [ 
      "keydown", 
      "keypress", 
      "keyup" 
    ],

    _dragEventTypes : qx.core.Variant.select("qx.client", {
      "gecko" : [ 
        "dragdrop", 
        "dragover", 
        "dragenter", 
        "dragexit", 
        "draggesture" 
       ],
      "mshtml" : [ 
        "dragend", 
        "dragover", 
        "dragstart", 
        "drag", 
        "dragenter", 
        "dragleave" 
      ],
      "default" : [ 
        "dragstart", 
        "dragdrop", 
        "dragover", 
        "drag", 
        "dragleave", 
        "dragenter", 
        "dragexit", 
        "draggesture" 
      ]
    } ),

    ////////////////////
    // Helper-functions:

    _getDomEvent : qx.core.Variant.select("qx.client", {
      "mshtml" : function( args ) {
        return args.length > 0 ? args[ 0 ] : window.event;
      },
      "default" : function( args ) {
        return args[ 0 ];
      }
    } ),

    getDomTarget : qx.core.Variant.select("qx.client", {
      "mshtml" : function( vDomEvent ) {
        return vDomEvent.target || vDomEvent.srcElement;
      },
      "webkit" : function( vDomEvent ) {
        var vNode = vDomEvent.target || vDomEvent.srcElement;
        // Safari takes text nodes as targets for events
        if( vNode && ( vNode.nodeType == qx.dom.Node.TEXT ) ) {
          vNode = vNode.parentNode;
        }
        return vNode;
      },
      "default" : function(vDomEvent) {
        return vDomEvent.target;
      }
    } ),
    
    _getKeyEventTarget : function() {
      var vFocusRoot = this.getFocusRoot();
      return this.getCaptureWidget() || (vFocusRoot == null ? null : vFocusRoot.getActiveChild());
    },

    stopDomEvent : function( vDomEvent ) {
      if( vDomEvent.preventDefault ) {
        vDomEvent.preventDefault();
      }
      try {
        // this allows us to prevent some key press events in IE and Firefox.
        // See bug #1049
        vDomEvent.keyCode = 0;
      } catch( ex ) {
      }

      vDomEvent.returnValue = false;
    },

    // BUG: http://xscroll.mozdev.org/
    // If your Mozilla was built with an option `--enable-default-toolkit=gtk2',
    // it can not return the correct event target for DOMMouseScroll.

    getOriginalTargetObject : function( vNode ) {
      // Events on the HTML element, when using absolute locations which
      // are outside the HTML element. Opera does not seem to fire events
      // on the HTML element.
      if( vNode == document.documentElement ) {
        vNode = document.body;
      }
      // Walk up the tree and search for an qx.ui.core.Widget
      while( vNode != null && vNode.qx_Widget == null )       {
        try {
          vNode = vNode.parentNode;
        } catch( vDomEvent ) {
          vNode = null;
        }
      }
      return vNode ? vNode.qx_Widget : null;
    },


    getOriginalTargetObjectFromEvent : function( vDomEvent, vWindow ) {
      var vNode = this.getDomTarget(vDomEvent);
      // Especially to fix key events.
      // 'vWindow' is the window reference then
      if( vWindow ) {
        var vDocument = vWindow.document;
        if(    vNode == vWindow 
            || vNode == vDocument 
            || vNode == vDocument.documentElement 
            || vNode == vDocument.body ) 
        {
          return vDocument.body.qx_Widget;
        }
      }
      return this.getOriginalTargetObject( vNode );
    },

    getRelatedOriginalTargetObjectFromEvent : function( vDomEvent ) {
      return this.getOriginalTargetObject(
           vDomEvent.relatedTarget 
        || ( vDomEvent.type == "mouseover" ? vDomEvent.fromElement : vDomEvent.toElement )
      );
    },


    getTargetObject : function( vNode, vObject, allowDisabled ) {
      if( !vObject ) {
        var vObject =this.getOriginalTargetObject( vNode );
        if (!vObject) {
          return null;
        }
      }
      while( vObject ) {
        if( !allowDisabled && !vObject.getEnabled() ) {
          return null;
        }
        if( !vObject.getAnonymous() ) {
          break;
        }
        vObject = vObject.getParent();
      }
      return vObject;
    },

    getTargetObjectFromEvent : function( vDomEvent ) {
      this.getTargetObject( this.getDomTarget( vDomEvent ) );
    },

    getRelatedTargetObjectFromEvent : function( vDomEvent ) {
      var target = vDomEvent.relatedTarget;
      if( !target ) {
        if( vDomEvent.type == "mouseover" ) {
          target = vDomEvent.fromElement;
        } else {
          target = vDomEvent.toElement;
        }
      }
      return this.getTargetObject(target);
    },
    
    attachEventTypes : function( vEventTypes, vFunctionPointer ) {
      try {
        // Gecko is a bit buggy to handle key events on document if 
        // not previously focused. Internet Explorer has problems to use 
        // 'window', so there we use the 'body' element 
        var el = qx.core.Variant.isSet("qx.client", "gecko") ? window : document.body;
        for( var i=0, l=vEventTypes.length; i<l; i++ ) {
          qx.html.EventRegistration.addEventListener( el, vEventTypes[i], vFunctionPointer );
        }
      }
      catch( ex ) {
        throw new Error( "EventHandler: Failed to attach window event types: " + vEventTypes + ": " + ex );
      }
    },

    detachEventTypes : function( vEventTypes, vFunctionPointer ) {
      try {
        var el = qx.core.Variant.isSet("qx.client", "gecko") ? window : document.body;
        for (var i=0, l=vEventTypes.length; i<l; i++) {
          qx.html.EventRegistration.removeEventListener( el, vEventTypes[i], vFunctionPointer );
        }
      } catch( ex ) {
        throw new Error("EventHandler: Failed to detach window event types: " + vEventTypes + ": " + ex);
      }
    },

    /** 
     * Fixes browser quirks with 'click' detection
     *
     * Firefox: The DOM-targets are different. The click event only fires, 
     * if the target of the mousedown is the same than with the mouseup. 
     * If the content moved away, the click isn't fired.
     */
    _onmouseevent_click_fix : qx.core.Variant.select("qx.client", {
      "gecko" : function( vDomTarget, vType, vDispatchTarget ) {
        var vReturn = false;
        switch( vType ) {
          case "mousedown":
            this._lastMouseDownDomTarget = vDomTarget;
            this._lastMouseDownDispatchTarget = vDispatchTarget;
          break;
          case "mouseup":
            if(    this._lastMouseDownDispatchTarget === vDispatchTarget 
                && vDomTarget !== this._lastMouseDownDomTarget) {
              vReturn = true;
            } else {
              this._lastMouseDownDomTarget = null;
              this._lastMouseDownDispatchTarget = null;
            }
          break;
        }
        return vReturn;
      },

      "default" : function() {
        return false;
      }
    } )

  }

} );