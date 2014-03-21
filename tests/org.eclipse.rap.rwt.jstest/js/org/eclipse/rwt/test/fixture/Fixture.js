/*******************************************************************************
 * Copyright (c) 2009, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "org.eclipse.rwt.test.fixture" );
org.eclipse.rwt.test.fixture.Fixture = {

  setup : function() {
    rwt.remote.MessageProcessor.processMessage( {
      "head": {},
      "operations": [
        [
          "call",
          "rwt.theme.ThemeStore",
          "loadFallbackTheme", {
            "url" : "rwt-resources/rap-rwt.theme.Fallback.json"
          }
        ],
        [
          "call",
          "rwt.theme.ThemeStore",
          "loadActiveTheme", {
            "url" : "rwt-resources/rap-rwt.theme.Fallback.json"
          }
        ]
      ]
    } );
    rwt.remote.Request.createXHR = function() {
      return new org.eclipse.rwt.test.fixture.NativeRequestMock();
    };
    rwt.widgets.base.WidgetToolTip.getInstance()._computeFallbackMode
      = rwt.util.Functions.returnFalse;
    // undo the changed done by MobileWebkitSupport to allow normal tooltip tests:
    delete  rwt.widgets.util.ToolTipManager.getInstance().handleMouseEvent;
    rwt.remote.Request.prototype._shouldUseStateListener = rwt.util.Functions.returnTrue;
    var server = rwt.remote.Connection.getInstance();
    rwt.remote.KeyEventSupport.getInstance()._sendRequestAsync = function() {
      server._requestPending = false;
      server.sendImmediate( true );
    };
    server.send = function() {
      if( !this._ignoreSend && !this._sendTimer.isEnabled() ) {
        this._sendTimer.start();
        this._requestPending = false;
        this.sendImmediate( true ); // omit timer
      }
    };
    server._delayTimer = new rwt.client.Timer();
    server._delayTimer.addEventListener( "interval", function() {
      this._delayTimer.stop();
      this.send();
    }, server );
    org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
    rwt.remote.MessageProcessor.processMessage( {
      "head": {},
      "operations": [
        [ "create", "w1", "rwt.widgets.Display" ]
      ]
    } );
    rwt.runtime.ErrorHandler.processJavaScriptErrorInResponse
      = function( script, error, currentRequest ) { throw error; };
    server.setRequestCounter( 0 );
    org.eclipse.rwt.test.fixture.TestUtil.clearXMLHttpRequests();
    org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
    // prevent flush by timer
    this._disableAutoFlush();
    org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
    org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
    // prevent actual dom-events
    rwt.event.EventHandler.detachEvents();
    org.eclipse.rwt.test.fixture.TestUtil.initErrorPageLog();
  },

  reset : function() {
    org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
    org.eclipse.rwt.test.fixture.TestUtil.clearTimerOnceLog();
    org.eclipse.rwt.test.fixture.TestUtil.restoreAppearance();
    org.eclipse.rwt.test.fixture.TestUtil.emptyDragCache();
    org.eclipse.rwt.test.fixture.TestUtil.resetEventHandler();
    org.eclipse.rwt.test.fixture.TestUtil.cleanUpKeyUtil();
    org.eclipse.rwt.test.fixture.TestUtil.clearErrorPage();
    org.eclipse.rwt.test.fixture.TestUtil.resetObjectRegistry();
    org.eclipse.rwt.test.fixture.TestUtil.resetSendListener();
    org.eclipse.rwt.test.fixture.TestUtil.resetWindowManager();
    org.eclipse.rwt.test.fixture.TestUtil.clearXMLHttpRequests();
    rwt.widgets.base.WidgetToolTip.getInstance()._computeFallbackMode
      = rwt.util.Functions.returnFalse;
    rwt.widgets.util.MnemonicHandler.getInstance().deactivate();
    rwt.event.EventHandler.setFocusRoot( rwt.widgets.base.ClientDocument.getInstance() );
    rwt.widgets.base.WidgetToolTip.getInstance().hide();
    rwt.widgets.base.WidgetToolTip.getInstance()._hideTimeStamp = ( new Date( 0 ) ).valueOf();
    rwt.widgets.base.Widget.flushGlobalQueues();
  },

  _disableAutoFlush : function() {
    rwt.widgets.base.Widget._removeAutoFlush();
    rwt.widgets.base.Widget._initAutoFlush = function(){};
    rwt.widgets.base.Widget.__orgFlushGlobalQueues = rwt.widgets.base.Widget.flushGlobalQueues;
    rwt.widgets.base.Widget.flushGlobalQueues = function() {
      if( this.__allowFlushs ) {
        rwt.widgets.base.Widget.__orgFlushGlobalQueues();
      }
    };
    rwt.widgets.base.Widget.__allowFlushs = true;
  }

};
