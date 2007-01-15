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

qx.OO.defineClass( 
  "org.eclipse.rap.rwt.widgets.Shell", 
  qx.ui.window.Window,
  function() {
    qx.ui.window.Window.call( this );
    this._activeControl = null;
    this._activateListenerWidgets = new Array();
    this.addEventListener( "changeActiveChild", this._onChangeActiveChild );
    this.addEventListener( "changeActive", this._onChangeActive );
  }
);

/**
 * Applies relevant RWT style bits that must have been passed using addState()
 * before.
 */
qx.Proto.applyStyle = function( ) {
  // If only one of the flags MIN and MAX is set, both buttons are shown in
  // Windows and one of them is disbaled.
  this._minimizeButton.setEnabled( this.hasState( "rwt_MIN" ) );
  this._maximizeButton.setEnabled( this.hasState( "rwt_MAX" ) );
}

qx.Proto.setActiveControl = function( control ) {
  this._activeControl = control;  
}

/**
 * Adds a widget that has a server-side ActivateListener. If this widget or
 * any of its children are activated, an org.eclipse.rap.rwt.events.controlActivated 
 * is fired.
 */
qx.Proto.addActivateListenerWidget = function( widget ) {
// TODO [rh] the line below leads to an error - investigate this  
//  qx.lang.Array.append( this._activateListenerWidgets, widget );
  this._activateListenerWidgets.push( widget );
}

qx.Proto.removeActivateListenerWidget = function( widget ) {
  qx.lang.Array.remove( this._activateListenerWidgets, widget );
}

qx.Proto._isRelevantActivateEvent = function( widget ) {
  var result = false;
  for( var i = 0; !result && i < this._activateListenerWidgets.length; i++ ) {
    var listeningWidget = this._activateListenerWidgets[ i ];
    if(    !listeningWidget.contains( this._activeControl ) 
        && listeningWidget.contains( widget ) ) 
    {
      result = true;  
    }
  }
  return result;
}

qx.Proto._onChangeActiveChild = function( evt ) {
  // Work around qooxdoo bug #254: the changeActiveChild is fired twice when
  // a widget was activated by keyboard (getData() is null in this case)
  var widget = this._getParentControl( evt.getData() );
  if( !org_eclipse_rap_rwt_EventUtil_suspend  && widget != null )  {
    var widgetMgr = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetMgr.findIdByWidget( widget );
    var shellId = widgetMgr.findIdByWidget( this );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    if( this._isRelevantActivateEvent( widget ) ) {
      this._activeControl = widget;
      req.removeParameter( shellId + ".activeControl" );
      req.addEvent( "org.eclipse.rap.rwt.events.controlActivated", id );
      req.send();    
    } else {
      req.addParameter( shellId + ".activeControl", id );
    }
  }
}

qx.Proto._onChangeActive = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend && this.getActive() ) {
    var widgetMgr = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetMgr.findIdByWidget( this );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    if( qx.lang.Array.contains( this._activateListenerWidgets, this ) ) {
      req.removeParameter( req.getUIRootId() + ".activeShell" );
      req.addEvent( "org.eclipse.rap.rwt.events.shellActivated", id );
      req.send();
    } else {
      req.addParameter( req.getUIRootId() + ".activeShell", id );
    }
  }
}

/**
 * Returns the parent Control for the given widget. If widget is a Control 
 * itself, the widget is returned. Otherwise its parent is returned or null
 * if there is no parent
 */
qx.Proto._getParentControl = function( widget ) {
  var widgetMgr = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var result = widget;
  while( result != null && !widgetMgr.isControl( result ) ) {
    if( result.getParent ) {
      result = result.getParent();
    } else {
      result = null;
    }
  }
  return result;
}  