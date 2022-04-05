/*******************************************************************************
 * Copyright (c) 2009, 2022 EclipseSource and others.
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
    delete rwt.widgets.util.ToolTipManager.getInstance().handleMouseEvent;
    rwt.remote.Request.prototype._shouldUseStateListener = rwt.util.Functions.returnTrue;
    rwt.remote.Request.prototype._isFetchSupported = rwt.util.Functions.returnFalse;
    var connection = rwt.remote.Connection.getInstance();
    rwt.remote.KeyEventSupport.getInstance()._sendRequestAsync = function() {
      connection._requestPending = false;
      connection.sendImmediate( true );
    };
    connection.send = function() {
      if( !this._ignoreSend && !this._sendTimer.isEnabled() ) {
        this._sendTimer.start();
        this._requestPending = false;
        this.sendImmediate( true ); // omit timer
      }
    };
    connection._delayTimer = new rwt.client.Timer();
    connection._delayTimer.addEventListener( "interval", function() {
      this._delayTimer.stop();
      this.send();
    }, connection );
    org.eclipse.rwt.test.fixture.TestUtil.initRequestLog();
    rwt.remote.MessageProcessor.processMessage( {
      "head": {},
      "operations": [
        [ "create", "w1", "rwt.widgets.Display" ]
      ]
    } );
    rwt.runtime.ErrorHandler.processJavaScriptErrorInResponse
      = function( script, error, currentRequest ) { throw error; };
    connection._requestCounter = 0;
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
    org.eclipse.rwt.test.fixture.TestUtil.initRequestLog(); // needed because of ThemeStoreTest?
    org.eclipse.rwt.test.fixture.TestUtil.clearTimerOnceLog(); // could use Jasmine mock clock?
    org.eclipse.rwt.test.fixture.TestUtil.restoreAppearance(); // AppearanceManager can be singleton with some prototype manipulation?
    org.eclipse.rwt.test.fixture.TestUtil.resetEventHandler(); // TODO: make a singleton
    org.eclipse.rwt.test.fixture.TestUtil.clearErrorPage(); // ErrorHandler can be a singleton
    org.eclipse.rwt.test.fixture.TestUtil.resetObjectRegistry(); // TODO: make a singleton
    org.eclipse.rwt.test.fixture.TestUtil.resetSendListener(); // not needed if Connection is disposed after each test
    org.eclipse.rwt.test.fixture.TestUtil.resetWindowManager();  // TODO: make a singleton
    org.eclipse.rwt.test.fixture.TestUtil.cleanUpKeyUtil(); // TODO : can be a singletonwith some prototype maninulation
    org.eclipse.rwt.test.fixture.TestUtil.resetSingletons();
    org.eclipse.rwt.test.fixture.TestUtil.clearXMLHttpRequests(); // NativeRequestMock can be Singleton
    rwt.widgets.base.WidgetToolTip.getInstance()._computeFallbackMode // allows to reset tooltip singleton?
      = rwt.util.Functions.returnFalse;
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
