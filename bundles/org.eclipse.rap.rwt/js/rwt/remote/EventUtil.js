/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

/**
 * This class contains static listener functions for common events.
 */
rwt.qx.Class.define( "rwt.remote.EventUtil", {

  statics : {
    _suspended : false,

    setSuspended : function( value ) {
      this._suspended = value;
    },

    getSuspended : function() {
      return this._suspended;
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
    _shiftKey : false,
    _ctrlKey : false,
    _altKey : false,
    _metaKey : false,

    eventTimestamp : function() {
      var init = rwt.runtime.System.getInstance();
      return new Date().getTime() - init.getStartupTime();
    },

    widgetDefaultSelected : function( evt, target ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        var server = rwt.remote.Server.getInstance();
        var properties = {};
        rwt.remote.EventUtil.addModifierToProperties( properties );
        var remoteObject = server.getRemoteObject( target ? target : evt.getTarget() );
        remoteObject.notify( "DefaultSelection", properties );
      }
    },

    widgetSelected : function( evt ) {
      var left = evt.getTarget().getLeft();
      var top = evt.getTarget().getTop();
      var width = evt.getTarget().getWidth();
      var height = evt.getTarget().getHeight();
      rwt.remote.EventUtil.notifySelected( evt.getTarget(), left, top, width, height );
    },

    notifySelected : function( target, left, top, width, height, detail ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        var server = rwt.remote.Server.getInstance();
        var properties;
        if( arguments.length === 2 ) {
          properties = left;
        } else {
          properties = {
              "x" : left,
              "y" : top,
              "width" : width,
              "height" : height,
              "detail" : detail
          };
        }
        rwt.remote.EventUtil.addModifierToProperties( properties );
        server.getRemoteObject( target ).notify( "Selection", properties );
      }
    },

    notifyDefaultSelected : function( target, left, top, width, height, detail ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        var server = rwt.remote.Server.getInstance();
        var properties;
        if( arguments.length === 2 ) {
          properties = left;
        } else {
          properties = {
              "x" : left,
              "y" : top,
              "width" : width,
              "height" : height,
              "detail" : detail
          };
        }
        rwt.remote.EventUtil.addModifierToProperties( properties );
        server.getRemoteObject( target ).notify( "DefaultSelection", properties );
      }
    },

    addModifierToProperties : function( properties ) {
      var commandKey
        = rwt.client.Client.getPlatform() === "mac" && rwt.remote.EventUtil._metaKey;
      properties.shiftKey = rwt.remote.EventUtil._shiftKey;
      properties.ctrlKey = rwt.remote.EventUtil._ctrlKey || commandKey;
      properties.altKey = rwt.remote.EventUtil._altKey;
    },

    _getKeyModifier : function() {
      var modifier = ""; // TODO [tb] : use real array for json protocol
      var commandKey
        = rwt.client.Client.getPlatform() === "mac" && rwt.remote.EventUtil._metaKey;
      if( rwt.remote.EventUtil._shiftKey ) {
        modifier += "shift,";
      }
      if( rwt.remote.EventUtil._ctrlKey || commandKey ) {
        modifier += "ctrl,";
      }
      if( rwt.remote.EventUtil._altKey ) {
        modifier += "alt,";
      }
      return modifier;
    },

    focusGained : function( evt ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        var remoteObject = rwt.remote.Server.getInstance().getRemoteObject( evt.getTarget() );
        remoteObject.notify( "FocusIn" );
      }
    },

    focusLost : function( evt ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        var remoteObject = rwt.remote.Server.getInstance().getRemoteObject( evt.getTarget() );
        remoteObject.notify( "FocusOut" );
      }
    },

    ///////////////////////
    // Mouse event handling

    mouseDown : function( evt ) {
      if(    !rwt.remote.EventUtil.getSuspended()
          && rwt.remote.EventUtil._isRelevantMouseEvent( this, evt ) )
      {
        // disabled capturing as it interferes with Combo capturing
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=262171
        // from now on, redirect mouse event to this widget
        // this.setCapture( true );
        rwt.remote.EventUtil._capturingWidget = this;
        // Collect request parameters and send
        rwt.remote.EventUtil._notifyMouseListeners( this, evt, "MouseDown" );
      }
    },

    mouseUp : function( evt ) {
      if(    !rwt.remote.EventUtil.getSuspended()
          && rwt.remote.EventUtil._isRelevantMouseEvent( this, evt ) )
      {
        // disabled capturing as it interferes with Combo capturing
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=262171
        // release mouse event capturing
        // this.setCapture( false );
        rwt.remote.EventUtil._capturingWidget = null;
        // Add mouse-up request parameter
        rwt.remote.EventUtil._notifyMouseListeners( this, evt, "MouseUp" );
      }
    },

    mouseDoubleClick : function( evt ) {
      if(    !rwt.remote.EventUtil.getSuspended()
          && rwt.remote.EventUtil._isRelevantMouseEvent( this, evt ) )
      {
        // disabled capturing as it interferes with Combo capturing
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=262171
        // from now on, redirect mouse event to this widget
        // this.setCapture( true );
        rwt.remote.EventUtil._capturingWidget = this;
        // Add parameters for double-click event
        if( rwt.remote.EventUtil._isDoubleClick( this, evt ) ) {
          rwt.remote.EventUtil._clearLastMouseDown();
          rwt.remote.EventUtil._notifyMouseListeners( this, evt, "MouseDoubleClick" );
        } else {
          // Store relevant data of current event to detect double-clicks
          var lastMouseDown = rwt.remote.EventUtil._lastMouseDown;
          lastMouseDown.widget = this;
          lastMouseDown.button = evt.getButton();
          lastMouseDown.x = evt.getPageX();
          lastMouseDown.y = evt.getPageY();
          lastMouseDown.mouseUpCount = 0;
          rwt.client.Timer.once( rwt.remote.EventUtil._clearLastMouseDown,
                                this,
                                rwt.remote.EventUtil.DOUBLE_CLICK_TIME );
        }
      }
    },

    mouseUpCounter : function( evt ) {
      if(    !rwt.remote.EventUtil.getSuspended()
          && rwt.remote.EventUtil._isRelevantMouseEvent( this, evt ) )
      {
        // disabled capturing as it interferes with Combo capturing
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=262171
        // release mouse event capturing
        // this.setCapture( false );
        rwt.remote.EventUtil._capturingWidget = null;
        // increase number of mouse-up events since last stored mouse down
        rwt.remote.EventUtil._lastMouseDown.mouseUpCount += 1;
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
      if(    widget !== rwt.remote.EventUtil._capturingWidget
          && widget !== evt.getOriginalTarget() )
      {
        // find parent control and ensure that it is the same as the widget-
        // parameter. Otherwise the mouse event is ignored.
        var widgetManager = rwt.remote.WidgetManager.getInstance();
        var target = evt.getOriginalTarget();
        var control = widgetManager.findEnabledControl( target );
        result = widget === control;
      }
      return result;
    },

    _clearLastMouseDown : function() {
      var lastMouseDown = rwt.remote.EventUtil._lastMouseDown;
      lastMouseDown.widget = null;
      lastMouseDown.button = "";
      lastMouseDown.mouseUpCount = 0;
      lastMouseDown.x = -1;
      lastMouseDown.y = -1;
    },

    _isDoubleClick : function( widget, evt ) {
      // TODO [rh] compare last position with current position and don't
      //      report double-click if deviation is too big
      var lastMouseDown = rwt.remote.EventUtil._lastMouseDown;
      return    lastMouseDown.mouseUpCount === 1
             && lastMouseDown.widget === widget
             && lastMouseDown.button === rwt.event.MouseEvent.C_BUTTON_LEFT
             && lastMouseDown.button === evt.getButton();
    },

    _notifyMouseListeners : function( widget, evt, eventType ) {
      var button = rwt.remote.EventUtil._determineMouseButton( evt );
      var modifier = rwt.remote.EventUtil._getKeyModifier();
      var remoteObject = rwt.remote.Server.getInstance().getRemoteObject( widget );
      var properties = {
        "button" : button,
        "x" : evt.getPageX(),
        "y" : evt.getPageY(),
        "time" : this.eventTimestamp()
      };
      rwt.remote.EventUtil.addModifierToProperties( properties );
      remoteObject.notify( eventType, properties );
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
        case rwt.event.MouseEvent.C_BUTTON_LEFT:
          result = 1;
          break;
        case rwt.event.MouseEvent.C_BUTTON_MIDDLE:
          result = 2;
          break;
        case rwt.event.MouseEvent.C_BUTTON_RIGHT:
          result = 3;
          break;
      }
      return result;
    },

    helpRequested : function( evt ) {
      if( evt.getKeyIdentifier() === "F1" ) {
        // stop further handling and default handling by the browser
        evt.stopPropagation();
        evt.preventDefault();
        // send help request to server
        var widget = evt.getTarget();
        var widgetManager = rwt.remote.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( widget );
        if( id === null ) {
          // find parent control for the widget that received the event in case
          // it wasn't the control itself that received the event
          widget = widgetManager.findControl( widget );
          id = widgetManager.findIdByWidget( widget );
        }
        if( id != null ) {
          var remoteObject = rwt.remote.Server.getInstance().getRemoteObject( widget );
          remoteObject.notify( "Help" );
        }
      }
    },

    menuDetectedByKey : function( evt ) {
      if( evt.getKeyIdentifier() === "Apps" ) {
        // stop further handling and default handling by the browser
        evt.stopPropagation();
        evt.preventDefault();
        var x = rwt.event.MouseEvent.getPageX();
        var y = rwt.event.MouseEvent.getPageY();
        rwt.remote.EventUtil.sendMenuDetected( evt.getTarget(), x, y );
      }
    },

    menuDetectedByMouse : function( evt ) {
      if( evt.getButton() === rwt.event.MouseEvent.C_BUTTON_RIGHT ) {
        // stop further handling and default handling by the browser
        evt.stopPropagation();
        evt.preventDefault();
        var x = evt.getPageX();
        var y = evt.getPageY();
        rwt.remote.EventUtil.sendMenuDetected( evt.getTarget(), x, y );
      }
    },

    sendMenuDetected : function( widget, x, y ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        // send menu detect request to server
        var widgetManager = rwt.remote.WidgetManager.getInstance();
        // find parent control for the widget that received the event in case
        // it wasn't the control itself that received the event
        while( widget != null && !widgetManager.isControl( widget ) ) {
          widget = widget.getParent ? widget.getParent() : null;
        }
        var id = widgetManager.findIdByWidget( widget );
        if( id != null ) {
          var remoteObject = rwt.remote.Server.getInstance().getRemoteObject( widget );
          remoteObject.notify( "MenuDetect", { "x" : x, "y" : y } );
        }
      }
    }

  }
} );
