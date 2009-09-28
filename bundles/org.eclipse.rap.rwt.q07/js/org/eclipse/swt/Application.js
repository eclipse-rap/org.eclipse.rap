/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.Application", {
  extend : qx.application.Gui,

  construct : function() {
    this.base( arguments );
    this._exitConfirmation = null;
    this._startupTime = new Date().getTime();
    qx.Class.patch( qx.event.handler.KeyEventHandler,
                    org.eclipse.rwt.KeyEventHandlerPatch );
    qx.Class.patch( qx.ui.core.Parent, org.eclipse.rwt.GfxMixin );
    qx.Class.patch( qx.ui.form.TextField, org.eclipse.rwt.GfxMixin );
    qx.Class.patch( org.eclipse.rwt.widgets.MultiCellWidget,
                    org.eclipse.rwt.GfxMixin );
    var eventHandler = qx.event.handler.EventHandler.getInstance();
    eventHandler.setAllowContextMenu(
      org.eclipse.rwt.widgets.Menu.getAllowContextMenu
    );
  },
  
  destruct : function() {
    var doc = qx.ui.core.ClientDocument.getInstance();
    doc.removeEventListener( "windowresize", 
                             org.eclipse.swt.Application._onResize );
    doc.removeEventListener( "keypress",
                             org.eclipse.swt.Application._onKeyPress );
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "send", this._onSend, this );
  },

  statics : {
    _onResize : function( evt ) {
      org.eclipse.swt.Application._appendWindowSize();
      var req = org.eclipse.swt.Request.getInstance();
      req.send();
    },

    _onKeyPress : function( evt ) {
      if( evt.getKeyIdentifier() == "Escape" ) {
        evt.preventDefault();
      }
    },
    
    _appendWindowSize : function() {
      var width = qx.html.Window.getInnerWidth( window );
      var height = qx.html.Window.getInnerHeight( window );
      // Append document size to request
      var req = org.eclipse.swt.Request.getInstance();
      var id = req.getUIRootId();
      req.addParameter( id + ".bounds.width", String( width ) );
      req.addParameter( id + ".bounds.height", String( height ) );
    }
  },

  members : {
    /**
     * An exit confirmation dialog will be displayed if the given message is not
     * null. If the message is empty, the dialog will be displayed but without a
     * message.
     */
    setExitConfirmation : function( message ) {
      // IE shows exit dialog also on empty string
      if( message == "" ) {
        this._exitConfirmation = " ";
      } else {
        this._exitConfirmation = message;
      }
    },
    
    reload : function( message ) {
      if( confirm( message ) ) {
        this.setExitConfirmation( null );
        window.location.reload( false );
      }
    },
    
    getStartupTime : function() {
      return this._startupTime;
    },
    
    main : function( evt ) {
      this.base( arguments );
      // Reduce scroll-event delay to 80ms (default is 250ms)
      // All scroll events that arrive in shorter time will be merged
      qx.ui.basic.ScrollBar.EVENT_DELAY = 125;
      // Overwrite the default mapping for internal images. This is necessary
      // if the application is deployed under a root different from "/".
      qx.io.Alias.getInstance().add( "static", "./rwt-resources/resource/static" );
      qx.io.Alias.getInstance().add( "org.eclipse.swt", "./rwt-resources/resource" );
      // Observe window size
      var doc = qx.ui.core.ClientDocument.getInstance();
      doc.addEventListener( "windowresize",
                            org.eclipse.swt.Application._onResize );
      doc.addEventListener( "keypress",
                            org.eclipse.swt.Application._onKeyPress );
      // Initial request to obtain startup-shell
      org.eclipse.swt.Application._appendWindowSize();
      var req = org.eclipse.swt.Request.getInstance();
      req.addEventListener( "send", this._onSend, this );
      req.send();
    },
    
    close : function( evt ) {
      this.base( arguments );
      return this._exitConfirmation;
    },

    _onSend : function( evt ) {
      var pageX = qx.event.type.MouseEvent.getPageX();
      var pageY = qx.event.type.MouseEvent.getPageY();
      var req = org.eclipse.swt.Request.getInstance();
      var id = req.getUIRootId();
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".cursorLocation.x", String( pageX ) );
      req.addParameter( id + ".cursorLocation.y", String( pageY ) );
    }
    
  }
});
