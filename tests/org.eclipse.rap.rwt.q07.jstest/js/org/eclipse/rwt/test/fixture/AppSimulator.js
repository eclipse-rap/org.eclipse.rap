/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

/**
 * Keep this class in sync with org.eclipse.swt.Application 
 */
qx.Class.define("org.eclipse.rwt.test.fixture.AppSimulator", {
  type : "static",

  statics : {    
    start : function() {
      qx.Class.patch( org.eclipse.swt.Request,
                        org.eclipse.rwt.test.fixture.RAPRequestPatch);
      qx.Class.patch( qx.ui.core.Parent, org.eclipse.rwt.GraphicsMixin );
      qx.Class.patch( qx.ui.form.TextField, org.eclipse.rwt.GraphicsMixin );
      qx.Class.patch( org.eclipse.rwt.widgets.MultiCellWidget,
                      org.eclipse.rwt.GraphicsMixin );
      qx.Class.patch( qx.ui.core.ClientDocumentBlocker,
                      org.eclipse.rwt.FadeAnimationMixin );
      org.eclipse.rwt.MobileWebkitSupport.init();      
      org.eclipse.rwt.KeyEventUtil.getInstance();                
      org.eclipse.rwt.GraphicsUtil.init();
      var eventHandler = org.eclipse.rwt.EventHandler;
      eventHandler.setAllowContextMenu(
        org.eclipse.rwt.widgets.Menu.getAllowContextMenu
      );
      eventHandler.setMenuManager( org.eclipse.rwt.MenuManager.getInstance() );                      
      qx.ui.basic.ScrollBar.EVENT_DELAY = 125;
      var doc = qx.ui.core.ClientDocument.getInstance();
      doc.addEventListener( "windowresize", 
                            org.eclipse.rwt.test.fixture.AppSimulator._onResize );
      doc.addEventListener( "keydown",
                            org.eclipse.rwt.test.fixture.AppSimulator._onKeyDown );
      org.eclipse.rwt.test.fixture.AppSimulator._appendWindowSize();
      var req = org.eclipse.swt.Request.getInstance();
      //req.send();     

      var startupTime = new Date().getTime();
      var realApp = qx.core.Init.getInstance().getApplication();
      realApp.getStartupTime = function() {
        return startupTime;
      }
      
    },
        
    _onResize : function( evt ) {
      org.eclipse.rwt.test.fixture.AppSimulator._appendWindowSize();
      var req = org.eclipse.swt.Request.getInstance();
      req.send();
    },

    _onKeyDown : function( e ) {
      if( e.getKeyIdentifier() == "Escape" ) {
        e.preventDefault();
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
    
  }
});