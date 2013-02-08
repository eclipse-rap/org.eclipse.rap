/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var MessageProcessor = rwt.remote.MessageProcessor;
var ObjectRegistry = rwt.remote.ObjectRegistry;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ShellTest", {
  extend : rwt.qx.Object,

  members : {

    testDisplayOverlayBackground : function() {
      // first check that the default theme for overlay has no background set
      var tv = new rwt.theme.ThemeValues( {} );
      var backgroundImage = tv.getCssImage( "Shell-DisplayOverlay",
                                            "background-image" );
      assertNull( backgroundImage );
// [if] This is not testable with the new default theme as we currently can't
// fake the ThemeStore. Reactivate when the ThemeStore fixture is available.
//    assertEquals( "undefined", backgroundColor );
      // create shell like the LCA would do:
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.initialize();
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      TestUtil.flush();
      TestUtil.fakeResponse( false );
      // Check for overlay background-image to be "blank.gif", as IE needs
      // this to capture mouse events.
      var overlay = rwt.widgets.base.ClientDocument.getInstance()._getBlocker();
      assertTrue( overlay.isSeeable() );
// [if] This is not testable with the new default theme as we currently can't
// fake the ThemeStore. Reactivate when the ThemeStore fixture is available.
//    assertEquals( "static/image/blank.gif", overlay.getBackgroundImage() );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      TestUtil.flush();
      shell.destroy();
      TestUtil.flush();
    },

    testDisplayOverlayCopyStates : function() {
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.addState( "rwt_myTest" );
      shell.initialize();
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      TestUtil.flush();
      TestUtil.fakeResponse( false );
      // Check for overlay to have the same states as the shell
      var overlay = rwt.widgets.base.ClientDocument.getInstance()._getBlocker();
      assertTrue( overlay.isSeeable() );
      assertTrue( overlay.hasState( "rwt_APPLICATION_MODAL" ) );
      assertTrue( overlay.hasState( "rwt_myTest" ) );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      TestUtil.flush();
      shell.destroy();
      TestUtil.flush();
    },

    testDisplayOverlayAddStates : function() {
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.initialize();
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      TestUtil.flush();
      TestUtil.fakeResponse( false );
      // Check for overlay to have the same states as the shell
      var overlay = rwt.widgets.base.ClientDocument.getInstance()._getBlocker();
      assertTrue( overlay.isSeeable() );
      assertTrue( overlay.hasState( "rwt_APPLICATION_MODAL" ) );
      shell.addState( "rwt_myTest" );
      assertTrue( overlay.hasState( "rwt_myTest" ) );
      shell.removeState( "rwt_myTest" );
      assertFalse( overlay.hasState( "rwt_myTest" ) );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      TestUtil.flush();
      shell.destroy();
      TestUtil.flush();
    },

    testDisplayOverlayMultipleShells : function() {
      var overlay = rwt.widgets.base.ClientDocument.getInstance()._getBlocker();
      var visibilityChanges = 0;
      overlay.addEventListener( "changeVisibility", function( event) {
        visibilityChanges++;
      } );
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Shell" );
      rwt.remote.ObjectRegistry.add( "w222", shell, handler );
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.initialize();
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      TestUtil.flush();
      var shell2 = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      shell2.addState( "rwt_APPLICATION_MODAL" );
      shell2.initialize();
      shell2.addState( "rwt_myTest2" );
      shell2.open();
      shell2.setActive( true );
      shell2.setSpace( 100, 300, 50, 200 );
      shell2.setVisibility( true );
      TestUtil.flush();
      TestUtil.fakeResponse( false );
      shell.addState( "rwt_myTest1" );
      shell2.addState( "rwt_myTest2b" );
      // check for Z-index and states for shell2
      assertTrue( overlay.isSeeable() );
      assertTrue( overlay.getZIndex() > shell.getZIndex() );
      assertTrue( overlay.getZIndex() < shell2.getZIndex() );
      assertFalse( overlay.hasState( "rwt_myTest1" ) );
      assertTrue( overlay.hasState( "rwt_myTest2" ) );
      assertTrue( overlay.hasState( "rwt_myTest2b" ) );
      // close shell2, check for Z-index and states for shell1
      shell2.doClose();
      TestUtil.flush();
      shell.addState( "rwt_myTest1b" );
      assertTrue( overlay.isSeeable() );
      assertTrue( overlay.getZIndex() < shell.getZIndex() );
      assertTrue( overlay.hasState( "rwt_myTest1" ) );
      assertTrue( overlay.hasState( "rwt_myTest1b" ) );
      assertFalse( overlay.hasState( "rwt_myTest2" ) );
      assertFalse( overlay.hasState( "rwt_myTest2b" ) );
      shell.doClose();
      overlay.hide(); // not done by doClose because this is the only shell
      assertEquals( 2, visibilityChanges ); // to prevent unwanted animations
      TestUtil.flush();
      shell.destroy();
      TestUtil.flush();
    },

    testCustomVariant : function() {
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      var variant = "variant_myCustomVariant";
      shell.addState( variant );
      assertTrue( shell._captionBar.hasState( variant) );
      assertTrue( shell._captionTitle.hasState( variant) );
      assertTrue( shell._minimizeButton.hasState( variant) );
      assertTrue( shell._maximizeButton.hasState( variant) );
      assertTrue( shell._restoreButton.hasState( variant) );
      assertTrue( shell._closeButton.hasState( variant) );
      shell.removeState( variant );
      assertFalse( shell._captionBar.hasState( variant) );
      assertFalse( shell._captionTitle.hasState( variant) );
      assertFalse( shell._minimizeButton.hasState( variant) );
      assertFalse( shell._maximizeButton.hasState( variant) );
      assertFalse( shell._restoreButton.hasState( variant) );
      assertFalse( shell._closeButton.hasState( variant) );
    },

    testDefaultButtonState : function() {
      var shell = new rwt.widgets.Shell( [ "APPLICATION_MODAL" ] );
      var button = new rwt.widgets.Button( "push" );
      assertFalse( button.hasState( "default") );
      shell.setDefaultButton( button );
      assertTrue( button.hasState( "default") );
      shell.setDefaultButton( null );
      assertFalse( button.hasState( "default") );
      button.destroy();
      shell.destroy();
    },

    testDefaultButtonGainFocusOnExecute : function() {
      var shell = TestUtil.createShellByProtocol( "w3" );
      rwt.event.EventHandler.setFocusRoot( shell );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w3"
        }
      } );
      var button = ObjectRegistry.getObject( "w4" );
      shell.setDefaultButton( button );
      TestUtil.flush();
      assertFalse( button.getFocused() );

      TestUtil.keyDown( shell.getElement(), "Enter", 0 );

      assertTrue( button.getFocused() );
      button.destroy();
      shell.destroy();
    },

    testFiresParentShellChangedEvent : function() {
      var shell = this._createDefaultShell( {} );
      var parentShell = this._createDefaultShell( {} );
      var log = 0;
      shell.addEventListener( "parentShellChanged", function() {
        log++;
      } );

      shell.setParentShell( parentShell );

      assertTrue( log > 0 );
      shell.destroy();
      parentShell.destroy();
    },

    testSendBounds : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w2",
        "action" : "listen",
        "properties" : { "Move" : true, "Resize" : true }
      } );

      shell.setLeft( 51 );
      shell.setTop( 52 );
      shell.setWidth( 53 );
      shell.setHeight( 54 );

      var messages = TestUtil.getMessages();
      assertEquals( [ 51, 10, 100, 100 ], messages[ 0 ].findSetProperty( "w2", "bounds" ) );
      assertEquals( [ 51, 52, 100, 100 ], messages[ 1 ].findSetProperty( "w2", "bounds" ) );
      assertEquals( [ 51, 52, 53, 100 ], messages[ 2 ].findSetProperty( "w2", "bounds" ) );
      assertEquals( [ 51, 52, 53, 54 ], messages[ 3 ].findSetProperty( "w2", "bounds" ) );
      shell.destroy();
    },

    testSendResize : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w2",
        "action" : "listen",
        "properties" : { "Resize" : true }
      } );

      shell.setLeft( 51 );
      shell.setTop( 52 );
      shell.setWidth( 53 );
      shell.setHeight( 54 );

      var messages = TestUtil.getMessages();
      assertEquals( 2, messages.length );
      assertEquals( [ 51, 52, 53, 100 ], messages[ 0 ].findSetProperty( "w2", "bounds" ) );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w2", "Resize" ) );
      assertNull( messages[ 0 ].findNotifyOperation( "w2", "Move" ) );
      assertEquals( [ 51, 52, 53, 54 ], messages[ 1 ].findSetProperty( "w2", "bounds" ) );
      assertNotNull( messages[ 1 ].findNotifyOperation( "w2", "Resize" ) );
      assertNull( messages[ 1 ].findNotifyOperation( "w2", "Move" ) );
      shell.destroy();
    },

    testSendMove : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w2",
        "action" : "listen",
        "properties" : { "Move" : true }
      } );

      shell.setLeft( 51 );
      shell.setTop( 52 );
      shell.setWidth( 53 );
      shell.setHeight( 54 );

      var messages = TestUtil.getMessages();
      assertEquals( 2, messages.length );
      assertEquals( [ 51, 10, 100, 100 ], messages[ 0 ].findSetProperty( "w2", "bounds" ) );
      assertNull( messages[ 0 ].findNotifyOperation( "w2", "Resize" ) );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w2", "Move" ) );
      assertEquals( [ 51, 52, 100, 100 ], messages[ 1 ].findSetProperty( "w2", "bounds" ) );
      assertNull( messages[ 1 ].findNotifyOperation( "w2", "Resize" ) );
      assertNotNull( messages[ 1 ].findNotifyOperation( "w2", "Move" ) );
      shell.destroy();
    },

    testNotifyActivateDeactivate_WithListeners : function() {
      var shell = this._createWidgetTree();
      shell.setActiveChild( ObjectRegistry.getObject( "w8" ) );
      TestUtil.protocolListen( "w7", { "Activate" : true } );
      TestUtil.protocolListen( "w8", { "Deactivate" : true } );
      TestUtil.clearRequestLog();

      shell.setActiveChild( ObjectRegistry.getObject( "w7" ) );

      var messages = TestUtil.getMessages();
      assertEquals( 2, messages.length );
      assertNull( messages[ 0 ].findSetOperation( "w3", "activeControl" ) );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w8", "Deactivate" ) );
      assertEquals( "w7", messages[ 1 ].findSetProperty( "w3", "activeControl" ) );
      assertNotNull( messages[ 1 ].findNotifyOperation( "w7", "Activate" ) );
      shell.destroy();
    },

    testNotifyActivateDeactivate_WithoutListeners : function() {
      var shell = this._createWidgetTree();
      shell.setActiveChild( ObjectRegistry.getObject( "w8" ) );
      TestUtil.clearRequestLog();

      shell.setActiveChild( ObjectRegistry.getObject( "w7" ) );
      rwt.remote.Server.getInstance().send();

      var messages = TestUtil.getMessages();
      assertEquals( 1, messages.length );
      assertNull( messages[ 0 ].findNotifyOperation( "w8", "Deactivate" ) );
      assertEquals( "w7", messages[ 0 ].findSetProperty( "w3", "activeControl" ) );
      assertNull( messages[ 0 ].findNotifyOperation( "w7", "Activate" ) );
      shell.destroy();
    },

    testNotifyActivateDeactivate_WithActivateListenerOnSameParent : function() {
      var shell = this._createWidgetTree();
      shell.setActiveChild( ObjectRegistry.getObject( "w8" ) );
      TestUtil.protocolListen( "w6", { "Activate" : true } );
      TestUtil.clearRequestLog();

      shell.setActiveChild( ObjectRegistry.getObject( "w9" ) );
      rwt.remote.Server.getInstance().send();

      var messages = TestUtil.getMessages();
      assertEquals( 1, messages.length );
      assertNull( messages[ 0 ].findNotifyOperation( "w8", "Deactivate" ) );
      assertEquals( "w9", messages[ 0 ].findSetProperty( "w3", "activeControl" ) );
      assertNull( messages[ 0 ].findNotifyOperation( "w6", "Activate" ) );
      shell.destroy();
    },

    testNotifyActivateDeactivate_WithDeactivateListenerOnSameParent : function() {
      var shell = this._createWidgetTree();
      shell.setActiveChild( ObjectRegistry.getObject( "w8" ) );
      TestUtil.protocolListen( "w6", { "Deactivate" : true } );
      TestUtil.clearRequestLog();

      shell.setActiveChild( ObjectRegistry.getObject( "w9" ) );
      rwt.remote.Server.getInstance().send();

      var messages = TestUtil.getMessages();
      assertEquals( 1, messages.length );
      assertNull( messages[ 0 ].findNotifyOperation( "w8", "Deactivate" ) );
      assertEquals( "w9", messages[ 0 ].findSetProperty( "w3", "activeControl" ) );
      assertNull( messages[ 0 ].findNotifyOperation( "w6", "Deactivate" ) );
      shell.destroy();
    },

    testNotifyActivateDeactivate_WithActivateListenerOnDifferentParent : function() {
      var shell = this._createWidgetTree();
      shell.setActiveChild( ObjectRegistry.getObject( "w7" ) );
      TestUtil.protocolListen( "w6", { "Activate" : true } );
      TestUtil.clearRequestLog();

      shell.setActiveChild( ObjectRegistry.getObject( "w9" ) );

      var messages = TestUtil.getMessages();
      assertEquals( 1, messages.length );
      assertEquals( "w9", messages[ 0 ].findSetProperty( "w3", "activeControl" ) );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w6", "Activate" ) );
      shell.destroy();
    },

    testNotifyActivateDeactivate_WithDectivateListenerOnDifferentParent : function() {
      var shell = this._createWidgetTree();
      shell.setActiveChild( ObjectRegistry.getObject( "w7" ) );
      TestUtil.protocolListen( "w5", { "Deactivate" : true } );
      TestUtil.clearRequestLog();

      shell.setActiveChild( ObjectRegistry.getObject( "w9" ) );
      rwt.remote.Server.getInstance().send();

      var messages = TestUtil.getMessages();
      assertEquals( 2, messages.length );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w5", "Deactivate" ) );
      assertEquals( "w9", messages[ 1 ].findSetProperty( "w3", "activeControl" ) );
      shell.destroy();
    },

    testNotifyShellActivate : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var shell = new rwt.widgets.Shell( {} );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Shell" );
      rwt.remote.ObjectRegistry.add( "w222", shell, handler );
      shell.initialize();
      shell.open();
      rwt.remote.EventUtil.setSuspended( false );

      shell.setActive( true );

      var messages = TestUtil.getMessages();
      assertNotNull( messages[ 0 ].findNotifyOperation( "w222", "Activate" ) );
      shell.destroy();
    },

    testNotifyShellClose : function() {
      rwt.remote.EventUtil.setSuspended( true );
      var shell = new rwt.widgets.Shell( {} );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Shell" );
      rwt.remote.ObjectRegistry.add( "w222", shell, handler );
      shell.initialize();
      shell.open();
      rwt.remote.EventUtil.setSuspended( false );

      shell.close();

      var messages = TestUtil.getMessages();
      assertNotNull( messages[ 0 ].findNotifyOperation( "w222", "Close" ) );
      shell.destroy();
    },

    testApplicationModal : function() {
      var shell = new rwt.widgets.Shell( {} );
      shell.addState( "rwt_APPLICATION_MODAL" );
      shell.initialize();

      assertTrue( shell._appModal );
      shell.destroy();
    },

    testPrimaryModal : function() {
      var shell = new rwt.widgets.Shell( {} );
      shell.addState( "rwt_PRIMARY_MODAL" );
      shell.initialize();

      assertTrue( shell._appModal );
      shell.destroy();
    },

    /////////
    // Helper

    _createDefaultShell : function( styles, noFlush ) {
      TestUtil.fakeResponse( true );
      var shell = new rwt.widgets.Shell( styles );
      shell.initialize();
      shell.open();
      shell.setActive( true );
      shell.setSpace( 50, 300, 50, 200 );
      shell.setVisibility( true );
      TestUtil.fakeResponse( false );
      if( !noFlush ) {
        TestUtil.flush();
      }
      return shell;
    },

    _createWidgetTree : function() {
      var shell = TestUtil.createShellByProtocol( "w3" );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.Composite",
        "properties" : {
          "style" : [ "BORDER" ],
          "parent" : "w3"
        }
      } );
      MessageProcessor.processOperation( {
        "target" : "w5",
        "action" : "create",
        "type" : "rwt.widgets.Composite",
        "properties" : {
          "style" : [ "BORDER" ],
          "parent" : "w4"
        }
      } );
      MessageProcessor.processOperation( {
        "target" : "w7",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w5"
        }
      } );
      MessageProcessor.processOperation( {
        "target" : "w6",
        "action" : "create",
        "type" : "rwt.widgets.Composite",
        "properties" : {
          "style" : [ "BORDER" ],
          "parent" : "w4"
        }
      } );
      MessageProcessor.processOperation( {
        "target" : "w8",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w6"
        }
      } );
      MessageProcessor.processOperation( {
        "target" : "w9",
        "action" : "create",
        "type" : "rwt.widgets.Button",
        "properties" : {
          "style" : [ "PUSH" ],
          "parent" : "w6"
        }
      } );
      TestUtil.flush();
      return shell;
    }

  }

} );

}());
