/*******************************************************************************
 * Copyright (c) 2008, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.SyncKeyEventUtil",
{
  type : "singleton",
  extend : qx.core.Object,
  
  construct : function() {
    this.base( arguments );
    this._cancelEvent = false;
  },
  
  members : {
    intercept : function( eventType, keyCode, charCode, domEvent ) {
      // [if] Fix for bug 319159
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
      var realKeyCode = this._getRealKeyCode( keyCode, domEvent );
      var relevantEvent = keyUtil._isRelevantEvent( eventType, realKeyCode );
      if( !org.eclipse.swt.EventUtil.getSuspended() && relevantEvent ) {
        var control = keyUtil._getTargetControl();
        var hasKeyListener = keyUtil._hasKeyListener( control );
        var hasTraverseListener = keyUtil._hasTraverseListener( control );
        var isTraverseKey = false;
        if( hasTraverseListener ) {
          isTraverseKey = keyUtil._isTraverseKey( realKeyCode );
        }
        if( hasKeyListener || ( hasTraverseListener && isTraverseKey ) ) {
          // [if] Don't keep and modify the pending event object outside the
          // "intercept" method. Such approach does not work in IE.
          this._cancelEvent = false;
          this._sendKeyDown( control, realKeyCode, charCode, domEvent );
          if( this._cancelEvent ) {
            this._cancelDomEvent( domEvent );
          }
        } 
      }
      return this._cancelEvent;
    },
    
    cancelEvent : function() {
      this._cancelEvent = true;
    },
    
    allowEvent : function() {
      // do nothing
    },
    
    _getRealKeyCode : function( keyCode, domEvent ) {
      var result = keyCode;
      if( qx.core.Variant.isSet( "qx.client", "opera" ) ) {
        result = domEvent.keyCode;
      }
      return result;
    },

    _cancelDomEvent : function( domEvent ) {
      if( qx.core.Variant.isSet( "qx.client", "mshtml" ) ) {
        domEvent.returnValue = false;
        domEvent.cancelBubble = true;
      } else {
        domEvent.__isCanceled = true;
        domEvent.preventDefault();
        domEvent.stopPropagation();
      }
    },
    
    _sendKeyDown : function( widget, keyCode, charCode, domEvent ) {
      var keyUtil = org.eclipse.rwt.KeyEventUtil.getInstance();
	    keyUtil._attachKeyDown( widget, keyCode, charCode, domEvent );
      org.eclipse.swt.Request.getInstance().sendSyncronous();
    }

  }
} );

