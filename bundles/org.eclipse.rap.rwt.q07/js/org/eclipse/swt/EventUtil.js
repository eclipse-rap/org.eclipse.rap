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
 * This class contains static listener functions for common events.
 */
qx.Class.define( "org.eclipse.swt.EventUtil", {

  statics : {
    suspendEventHandling : function() {
      org_eclipse_rap_rwt_EventUtil_suspend = true;
    },

    resumeEventHandling : function() {
      org_eclipse_rap_rwt_EventUtil_suspend = false;
    },
    
    DOUBLE_CLICK_TIME : 500,
    
    _capturingWidget : null,
    _lastMouseDown : {
      widget : null,
      button : "",
      x : -1,
      y : -1,
      mouseUpCount : 0
    },

    widgetSelected : function( evt ) {
      var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
      var req = org.eclipse.swt.Request.getInstance();
      var id = widgetManager.findIdByWidget( evt.getTarget() );
      var left = evt.getTarget().getLeft();
      var top = evt.getTarget().getTop();
      var width = evt.getTarget().getWidth();
      var height = evt.getTarget().getHeight();
      org.eclipse.swt.EventUtil.doWidgetSelected( id, left, top, width, height );
    },

    doWidgetSelected : function( id, left, top, width, height ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
        req.addParameter( id + ".bounds.x", left );
        req.addParameter( id + ".bounds.y", top );
        req.addParameter( id + ".bounds.width", width );
        req.addParameter( id + ".bounds.height", height );
        req.send();
      }
    },

    widgetResized : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        // TODO: [fappel] replace this ugly hack that is used in case of
        //                window maximizations
        var height = evt.getTarget().getHeight();
        if( height == null ) {
          height = window.innerHeight;
          if( isNaN( height ) ) {  // IE special
            height = document.body.clientHeight;
          }
        }
        var width = evt.getTarget().getWidth();
        if( width == null ) {
          width = window.innerWidth;
          if( isNaN( width ) ) {  // IE special
            width = document.body.clientWidth;
          }
        }
        req.addParameter( id + ".bounds.height", height );
        req.addParameter( id + ".bounds.width", width );
        req.send();
      }
    },

    widgetMoved : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        req.addParameter( id + ".bounds.x", evt.getTarget().getLeft() );
        req.addParameter( id + ".bounds.y", evt.getTarget().getTop() );
//      req.send();
      }
    },

    focusGained : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.focusGained", id );
        req.send();
      }
    },

    focusLost : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.focusLost", id );
        req.send();
      }
    },
    
    ///////////////////////
    // Mouse event handling

    mouseDown : function( evt ) {
      if(    !org_eclipse_rap_rwt_EventUtil_suspend
          && org.eclipse.swt.EventUtil._isRelevantMouseEvent( this, evt ) )
      {
        // from now on, redirect mouse event to this widget
        this.setCapture( true );
        org.eclipse.swt.EventUtil._capturingWidget = this;
        // Add parameters for double-click event
        if( org.eclipse.swt.EventUtil._isDoubleClick( this, evt ) ) {
          org.eclipse.swt.EventUtil._clearLastMouseDown();
          org.eclipse.swt.EventUtil._mouseDoubleClickParams( this, evt );    
        } else {
          // Store relevant data of current event to detect double-clicks
          var lastMouseDown = org.eclipse.swt.EventUtil._lastMouseDown;
          lastMouseDown.widget = this;
          lastMouseDown.button = evt.getButton();
          lastMouseDown.x = evt.getPageX();
          lastMouseDown.y = evt.getPageY();
          lastMouseDown.mouseUpCount = 0;
          qx.client.Timer.once( org.eclipse.swt.EventUtil._clearLastMouseDown,
                                this,
                                org.eclipse.swt.EventUtil.DOUBLE_CLICK_TIME );
        }
        // Collect request parameters and send
        org.eclipse.swt.EventUtil._mouseDownParams( this, evt );
        req.send();
      }
    },

    mouseUp : function( evt ) {
      if(    !org_eclipse_rap_rwt_EventUtil_suspend
          && org.eclipse.swt.EventUtil._isRelevantMouseEvent( this, evt ) ) 
      {
        // release mouse event capturing
        this.setCapture( false );
        org.eclipse.swt.EventUtil._capturingWidget = null;
        // increase number of mouse-up events since last stored mouse down
        org.eclipse.swt.EventUtil._lastMouseDown.mouseUpCount += 1;
        // Add mouse-up request parameter
        org.eclipse.swt.EventUtil._mouseUpParams( this, evt );
        req.send();
      }
    },

    /**
     * Determines whether the event is relevant (i.e. should be sent) for the
     * given widget.
     * @param widget - the listening widget
     * @param evt - the mouse event
     */
    _isRelevantMouseEvent : function( widget, evt ) {
      var result = true;
      if(    widget !== org.eclipse.swt.EventUtil._capturingWidget
          && widget !== evt.getOriginalTarget() )
      {
        // find parent control and ensure that it is the same as the widget-
        // parameter. Otherwise the mouse event is ignored.  
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var parent = evt.getOriginalTarget();
        while( parent != null && !widgetManager.isControl( parent ) ) {
          parent = parent.getParent ? parent.getParent() : null;
        }
        result = widget === parent;
      }
      return result;
    },
    
    _clearLastMouseDown : function() {
      var lastMouseDown = org.eclipse.swt.EventUtil._lastMouseDown;
      lastMouseDown.widget = null;
      lastMouseDown.button = "";
      lastMouseDown.mouseUpCount = 0;
      lastMouseDown.x = -1;
      lastMouseDown.y = -1;
    },

    _isDoubleClick : function( widget, evt ) {
      // TODO [rh] compare last position with current position and don't
      //      report double-click if deviation is too big
      var lastMouseDown = org.eclipse.swt.EventUtil._lastMouseDown;
      return    lastMouseDown.mouseUpCount === 1
             && lastMouseDown.widget === widget
             && lastMouseDown.button === qx.event.type.MouseEvent.C_BUTTON_LEFT
             && lastMouseDown.button === evt.getButton();
    },

    _mouseDownParams : function( widget, evt ) {
      var id = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( widget );
      var req = org.eclipse.swt.Request.getInstance();
      var button = org.eclipse.swt.EventUtil._determineMouseButton( evt );
      req.addEvent( "org.eclipse.swt.events.mouseDown", id );
      req.addParameter( "org.eclipse.swt.events.mouseDown.button", button );
      req.addParameter( "org.eclipse.swt.events.mouseDown.x", evt.getPageX() );
      req.addParameter( "org.eclipse.swt.events.mouseDown.y", evt.getPageY() );
      req.addParameter( "org.eclipse.swt.events.mouseDown.time", this._eventTimestamp() );
    },

    _mouseUpParams : function( widget, evt ) {
      var id = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( widget );
      var button = org.eclipse.swt.EventUtil._determineMouseButton( evt );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.mouseUp", id );
      req.addParameter( "org.eclipse.swt.events.mouseUp.button", button );
      req.addParameter( "org.eclipse.swt.events.mouseUp.x", evt.getPageX() );
      req.addParameter( "org.eclipse.swt.events.mouseUp.y", evt.getPageY() );
      req.addParameter( "org.eclipse.swt.events.mouseUp.time", this._eventTimestamp() );
    },

    _mouseDoubleClickParams : function( widget, evt ) {
      var id = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( widget );
      var req = org.eclipse.swt.Request.getInstance();
      req.addEvent( "org.eclipse.swt.events.mouseDoubleClick", id );
      req.addParameter( "org.eclipse.swt.events.mouseDoubleClick.button",
                        org.eclipse.swt.EventUtil._determineMouseButton( evt ) );
      req.addParameter( "org.eclipse.swt.events.mouseDoubleClick.x",
                        evt.getPageX() );
      req.addParameter( "org.eclipse.swt.events.mouseDoubleClick.y",
                        evt.getPageY() );
      req.addParameter( "org.eclipse.swt.events.mouseDoubleClick.time", 
                        this._eventTimestamp() );
    },

    _eventTimestamp : function() {
      var app = qx.core.Init.getInstance().getApplication();
      return new Date().getTime() - app.getStartupTime();
    },

    /**
     * Returns an integer value that represents the button property from the 
     * given mouse event.
     * 0 = unknown 
     * 1 = left button
     * 2 = middle button
     * 3 = right button
     */
    _determineMouseButton : function( evt ) {
      var result = 0;
      switch( evt.getButton() ) {
        case qx.event.type.MouseEvent.C_BUTTON_LEFT: 
          result = 1;
          break;
        case qx.event.type.MouseEvent.C_BUTTON_MIDDLE:
          result = 2;
          break;
        case qx.event.type.MouseEvent.C_BUTTON_RIGHT:
          result = 3;
          break;
      }
      return result;
    }

  }
} );

var org_eclipse_rap_rwt_EventUtil_suspend = false;
