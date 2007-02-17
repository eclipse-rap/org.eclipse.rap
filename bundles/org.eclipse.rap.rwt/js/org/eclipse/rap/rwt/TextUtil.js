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

// This function gets assigned to the 'keyinput' event of a text widget
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

// This function gets assigned to the 'keyinput' event of a text widget if there
// was a server-side ModifyListener registered
org.eclipse.rap.rwt.TextUtil.modifyTextAction = function( evt ) {
  if(    !org_eclipse_rap_rwt_EventUtil_suspend 
      && !org.eclipse.rap.rwt.TextUtil._isModified( evt.getTarget() ) )
  {
    org.eclipse.rap.rwt.TextUtil.modifyText( evt );
    // add modifyText-event with sender-id to request parameters
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    // register listener that is notified when a request is sent
    qx.client.Timer.once( org.eclipse.rap.rwt.TextUtil._delayedModifyText, 
                          evt.getTarget(), 
                          500 );
  }
};

// This function gets assigned to the 'blur' event of a text widget if there
// was a server-side ModifyListener registered
org.eclipse.rap.rwt.TextUtil.modifyTextOnBlur = function( evt ) {
  if(    !org_eclipse_rap_rwt_EventUtil_suspend 
      && org.eclipse.rap.rwt.TextUtil._isModified( evt.getTarget() ) )
  {
    org.eclipse.rap.rwt.TextUtil._sendModifyEvent( evt.getTarget() );
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
    org.eclipse.rap.rwt.TextUtil._sendModifyEvent( this );
  }
}

org.eclipse.rap.rwt.TextUtil._isModified = function( widget ) {
  return widget.getUserData( "modified" ) == true;
}

org.eclipse.rap.rwt.TextUtil._setModified = function( widget, modified ) {
  return widget.setUserData( "modified", modified );
}

org.eclipse.rap.rwt.TextUtil._sendModifyEvent = function( widget ) {
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var id = widgetManager.findIdByWidget( widget );
  var req = org.eclipse.rap.rwt.Request.getInstance();
  req.addEvent( "org.eclipse.rap.rwt.events.modifyText", id );
  req.send();
}
