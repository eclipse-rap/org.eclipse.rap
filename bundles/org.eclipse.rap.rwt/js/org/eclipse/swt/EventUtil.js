/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
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
      button : 0,
      time : 0
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
    
    mouseDown : function( evt ) {
      if(    !org_eclipse_rap_rwt_EventUtil_suspend 
          && org.eclipse.swt.EventUtil._isRelevantMouseEvent( this, evt ) ) 
      {
        // from now on, redirect mouse event to this widget 
        this.setCapture( true );
        org.eclipse.swt.EventUtil._capturingWidget = this;
        // Convert left/middle/right button name to 1/2/3
        var button = org.eclipse.swt.EventUtil._determineMouseButton( evt );
        // Collect request parameters and send
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( "org.eclipse.swt.events.mouseDown.button", button );
        req.addParameter( "org.eclipse.swt.events.mouseDown.x", evt.getPageX() );
        req.addParameter( "org.eclipse.swt.events.mouseDown.y", evt.getPageY() );
        req.addEvent( "org.eclipse.swt.events.mouseDown", id );
        // Store relevant data of current event to detect double-clicks
        if( org.eclipse.swt.EventUtil._isDoubleClick( this, button ) ) {
          org.eclipse.swt.EventUtil._clearLastMouseDown();    
          req.addParameter( "org.eclipse.swt.events.mouseDoubleClick.button",
                            button );
          req.addParameter( "org.eclipse.swt.events.mouseDoubleClick.x", 
                            evt.getPageX() );
          req.addParameter( "org.eclipse.swt.events.mouseDoubleClick.y",
                            evt.getPageY() );
          req.addEvent( "org.eclipse.swt.events.mouseDoubleClick", id );
        } else {
          var lastMouseDown = org.eclipse.swt.EventUtil._lastMouseDown; 
          lastMouseDown.widget = this;
          lastMouseDown.button = button;
          lastMouseDown.time = new Date();
          qx.client.Timer.once( org.eclipse.swt.EventUtil._clearLastMouseDown, 
                                this,
                                org.eclipse.swt.EventUtil.DOUBLE_CLICK_TIME );
        }
        // Send request
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
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        var button = org.eclipse.swt.EventUtil._determineMouseButton( evt );
        req.addParameter( "org.eclipse.swt.events.mouseUp.button", button );
        req.addParameter( "org.eclipse.swt.events.mouseUp.x", evt.getPageX() );
        req.addParameter( "org.eclipse.swt.events.mouseUp.y", evt.getPageY() );
        req.addEvent( "org.eclipse.swt.events.mouseUp", id );
        req.send();
      }
    },
    
    /**
     * Determines whether the event is relevant (i.e. should be sent) for the
     * given widget.
     * In case a Control that is contained in widget was clicked, the widget
     * must not be notified. On the other hand, when an item (e.g. TreeItem)
     * was clicked, the widget (Tree in this case) must be notified.
     * If the widget itself was clicked, it must be notified.
     * @param widget - the listening widget
     * @param evt - the mouse event
     */
    _isRelevantMouseEvent : function( widget, evt ) {
      var result = true;
      if(    widget !== org.eclipse.swt.EventUtil._capturingWidget
          && widget !== evt.getOriginalTarget() ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        result = !widgetManager.isControl( evt.getOriginalTarget() );
      }
      return result;
    },
    
    _clearLastMouseDown : function() {
      var lastMouseDown = org.eclipse.swt.EventUtil._lastMouseDown;
      lastMouseDown.widget = null;
      lastMouseDown.button = 0;
      lastMouseDown.time = 0;
    },
    
    _isDoubleClick : function( widget, button ) {
      var lastMouseDown = org.eclipse.swt.EventUtil._lastMouseDown;
      return    lastMouseDown.widget === widget
             && button === 1
             && lastMouseDown.button === 1;
    },

    _determineMouseButton : function( evt ) {
      var result = 0;
      switch( evt.getButton() ) {
        case qx.event.type.MouseEvent.C_BUTTON_LEFT : 
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
});

var org_eclipse_rap_rwt_EventUtil_suspend = false;
