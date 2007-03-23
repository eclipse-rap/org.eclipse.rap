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

// TODO [rst] Setting the "icon" property on a qx.ui.window.Window does not
//      work with the current qx version. Changed constructor to be able to set
//      a window icon at all.
qx.OO.defineClass(
  "org.eclipse.rap.rwt.widgets.Shell", 
  qx.ui.window.Window,
  function( icon ) {
    qx.ui.window.Window.call( this, "", icon );
    this._activeControl = null;
    this._activateListenerWidgets = new Array();
    this.addEventListener( "changeActiveChild", this._onChangeActiveChild );
    this.addEventListener( "changeActive", this._onChangeActive );
    this.addEventListener( "keydown", this._onKeydown );    
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEventListener( "send", this._onSend, this );
  }
);

qx.OO.addProperty( { name : "defaultButton", type : "object" } );

qx.Proto.setActiveControl = function( control ) {
  this._activeControl = control;  
}

/**
 * To be called after rwt_TITLE is set
 */
qx.Proto.fixTitlebar = function() {
  if( this.hasState( "rwt_TITLE" ) ) {
    this._captionBar.addState( "rwt_TITLE" );
  } else {
    this.setShowCaption( false );
  }
}

qx.Proto.setDialogWindow = function() {
  this._isDialogWindow = true;
}

/**
 * Could be used to imitate Windows behavior: When SWT.MAX is set, the minimize
 * button is still shown but disabled.
 */
qx.Proto.disableMinimize = function() {
  this._minimizeButton.setEnabled( false );
}

/**
 * Could be used to imitate Windows behavior: When SWT.MIN is set, the maximize
 * button is still shown but disabled.
 */
qx.Proto.disableMaximize = function() {
  this._maximizeButton.setEnabled( false );
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
  // TODO [rst] This hack is a workaround for bug 345 in qooxdoo, remove this
  //      block as soon as the bug is fixed.
  //      See http://bugzilla.qooxdoo.org/show_bug.cgi?id=345
  if ( !this.getActive() && !isFinite( this.getZIndex() ) ) {
    this.forceZIndex( 1e8 );
  }
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
  var active = evt.getData();
  this._minimizeButton.setState( "active", active );
  this._maximizeButton.setState( "active", active );
  this._restoreButton.setState( "active", active );
  this._closeButton.setState( "active", active );
}

qx.Proto._onKeydown = function( evt ) {
  var keyId = evt.getKeyIdentifier();
  if( keyId == "Enter" ) {
    var defButton = this.getDefaultButton();
    if( defButton != null && defButton.isSeeable() ) {
      defButton.execute();
    }
  } else if ( keyId == "Escape" && this._isDialogWindow ) {
    this.close();
  }
}

qx.Proto._onSend = function( evt ) {
  if( this.getActive() ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var focusedChildId = null;
    if( this.getFocusedChild() != null ) {
      focusedChildId = widgetManager.findIdByWidget( this.getFocusedChild() );
    }
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( req.getUIRootId() + ".focusControl", focusedChildId );
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
