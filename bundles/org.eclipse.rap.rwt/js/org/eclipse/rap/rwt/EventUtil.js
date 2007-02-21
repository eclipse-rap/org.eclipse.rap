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
qx.OO.defineClass( "org.eclipse.rap.rwt.EventUtil" );

var org_eclipse_rap_rwt_EventUtil_suspend = false;

org.eclipse.rap.rwt.EventUtil.suspendEventHandling = function() {
  org_eclipse_rap_rwt_EventUtil_suspend = true;
}

org.eclipse.rap.rwt.EventUtil.resumeEventHandling = function() {
  org_eclipse_rap_rwt_EventUtil_suspend = false;
}

org.eclipse.rap.rwt.EventUtil.widgetSelected = function( evt ) {
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var req = org.eclipse.rap.rwt.Request.getInstance();
  var id = widgetManager.findIdByWidget( evt.getTarget() );
  var left = evt.getTarget().getLeft();
  var top = evt.getTarget().getTop();
  var width = evt.getTarget().getWidth();
  var height = evt.getTarget().getHeight();
  org.eclipse.rap.rwt.EventUtil.doWidgetSelected( id, 
                                                  left, 
                                                  top, 
                                                  width,
                                                  height );
};

org.eclipse.rap.rwt.EventUtil.doWidgetSelected
  = function( id, left, top, width, height ) 
{
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.events.widgetSelected", id );
    req.addParameter( id + ".bounds.x", left );
    req.addParameter( id + ".bounds.y", top );
    req.addParameter( id + ".bounds.width", width );
    req.addParameter( id + ".bounds.height", height );
    req.send();
  }
};

org.eclipse.rap.rwt.EventUtil.widgetResized = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var req = org.eclipse.rap.rwt.Request.getInstance();
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
      if( isNaN( width ) ) { // IE special
        width = document.body.clientWidth;
      }
    }
    req.addParameter( id + ".bounds.height", height );
    req.addParameter( id + ".bounds.width", width );
    req.send();
  }
};

org.eclipse.rap.rwt.EventUtil.widgetMoved = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var req = org.eclipse.rap.rwt.Request.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    req.addParameter( id + ".bounds.x", evt.getTarget().getLeft() );
    req.addParameter( id + ".bounds.y", evt.getTarget().getTop() );
//    req.send();
  }
};

org.eclipse.rap.rwt.EventUtil.shellClosed = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.widgets.Shell_close", id );
    req.send();
  }
};

org.eclipse.rap.rwt.EventUtil.focusGained = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.events.focusGained", id );
    req.send();
  }
}

org.eclipse.rap.rwt.EventUtil.focusLost = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.events.focusLost", id );
    req.send();
  }
}