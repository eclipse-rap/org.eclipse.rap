/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.OO.defineClass( "org.eclipse.rap.rwt.TextUtil" );

// This function gets assigned to the 'keypress' event of a text widget
org.eclipse.rap.rwt.TextUtil.modifyText = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var text = evt.getTarget();
    // if not yet done, register an event listener that adds a request param
    // with the text widgets' content just before the request is sent
    if( !org.eclipse.rap.rwt.TextUtil._isModified( text ) ) {
      var req = org.eclipse.rap.rwt.Request.getInstance();
      req.addEventListener( "send", org.eclipse.rap.rwt.TextUtil._onSend, text );
      org.eclipse.rap.rwt.TextUtil._setModified( text, true );
    }
  }
}

/*
 * This function gets assigned to the 'keypress' event of a text widget if there
 * was a server-side ModifyListener registered
 */
org.eclipse.rap.rwt.TextUtil.modifyTextAction = function( evt ) {
  if(    !org_eclipse_rap_rwt_EventUtil_suspend 
      && !org.eclipse.rap.rwt.TextUtil._isModified( evt.getTarget() ) 
      && org.eclipse.rap.rwt.TextUtil._isModifyingKey( evt.getKeyIdentifier() ) )
  {
    org.eclipse.rap.rwt.TextUtil.modifyText( evt );
    // add modifyText-event with sender-id to request parameters
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.events.modifyText", id );
    // register listener that is notified when a request is sent
    qx.client.Timer.once( org.eclipse.rap.rwt.TextUtil._delayedModifyText, 
                          evt.getTarget(), 
                          500 );
  }
};

/**
 * This function gets assigned to the 'blur' event of a text widget if there
 * was a server-side ModifyListener registered
 */
org.eclipse.rap.rwt.TextUtil.modifyTextOnBlur = function( evt ) {
  if(    !org_eclipse_rap_rwt_EventUtil_suspend 
      && org.eclipse.rap.rwt.TextUtil._isModified( evt.getTarget() ) )
  {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.events.modifyText", id );
    req.send();
  }
}

org.eclipse.rap.rwt.TextUtil._onSend = function( evt ) {
  // NOTE: 'this' references the text widget
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var id = widgetManager.findIdByWidget( this );
  var req = org.eclipse.rap.rwt.Request.getInstance();
  req.addParameter( id + ".text", this.getComputedValue() );
  // remove the _onSend listener and change the text widget state to 'unmodified'
  req.removeEventListener( "send", org.eclipse.rap.rwt.TextUtil._onSend, this );
  org.eclipse.rap.rwt.TextUtil._setModified( this, false );
}

org.eclipse.rap.rwt.TextUtil._delayedModifyText = function( evt ) {
  // NOTE: this references the text widget (see qx.client.Timer.once above)
  if( org.eclipse.rap.rwt.TextUtil._isModified( this ) ) {
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.send();
  }
}

org.eclipse.rap.rwt.TextUtil._isModified = function( widget ) {
  return widget.getUserData( "modified" ) == true;
}

org.eclipse.rap.rwt.TextUtil._setModified = function( widget, modified ) {
  return widget.setUserData( "modified", modified );
}

/**
 * Determines whether the given keyIdentifier potentially
 * modifies the content of a text widget.
 */
org.eclipse.rap.rwt.TextUtil._isModifyingKey = function( keyIdentifier ) {
  var result = false;
  switch( keyIdentifier ) {
    // Modifier keys
    case "Shift":
    case "Control":
    case "Alt":
    case "Meta":
    case "Win":
    // Navigation keys
    case "Up":
    case "Down":
    case "Left":
    case "Right":
    case "Home":
    case "End":
    case "PageUp":
    case "PageDown":
    case "Tab":
    // Context menu key
    case "Apps":
    //
    case "Escape":
    case "Insert":
    case "Enter":
    // 
    case "CapsLock":
    case "NumLock":
    case "Scroll":
    case "PrintScreen":
    // Function keys 1 - 12 
    case "F1":
    case "F2":
    case "F3":
    case "F4":
    case "F5":
    case "F6":
    case "F7":
    case "F8":
    case "F9":
    case "F10":
    case "F11":
    case "F12":
      break;

    default:
      result = true;
  }
  return result;
}