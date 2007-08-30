
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
qx.Class.define("org.eclipse.swt.EventUtil", {

  statics : {
    suspendEventHandling : function() {
      org_eclipse_rap_rwt_EventUtil_suspend = true;
    },

    resumeEventHandling : function() {
      org_eclipse_rap_rwt_EventUtil_suspend = false;
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
    }
  }
});

var org_eclipse_rap_rwt_EventUtil_suspend = false;
