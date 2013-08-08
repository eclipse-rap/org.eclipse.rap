/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var MessageProcessor = rwt.remote.MessageProcessor;
var DomEvent = rwt.event.DomEvent;

var handler = rwt.widgets.util.MnemonicHandler.getInstance();
var shell;
var widget;
var typeLog;
var charLog;
var keyLog;
var success;
var menuItem;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.MnemonicHandlerTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      widget = TestUtil.createWidgetByProtocol( "w3", "w2" );
      success = false;
      typeLog = [];
      charLog = [];
      keyLog = [];
      var keylogger = function( event ) {
        keyLog.push( event.getType() );
      };
      widget.addEventListener( "keydown", keylogger );
      widget.addEventListener( "keypress", keylogger );
      widget.addEventListener( "keyup", keylogger );
      handler.add( widget, function( event ) {
        typeLog.push( event.type );
        if( event.type === "trigger" ) {
          charLog.push( event.charCode );
          event.success = success;
        }
      } );
      TestUtil.flush();
      shell.setActive( true );
    },

    tearDown : function() {
      handler.setActivator( null );
      handler.deactivate();
      handler.remove( widget );
      shell.destroy();
      shell = null;
      charLog = null;
      typeLog = null;
    },

    testFireShowMnemonics : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );

      assertEquals( [ "show" ], typeLog );
    },

    testFireShowMnemonics_multipleModifier : function() {
      handler.setActivator( "CTRL+ALT" );

      TestUtil.keyDown( shell, "Control", ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [ "show" ], typeLog );
    },

    testFireHideMnemonics : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyUp( shell, "Control", 0 );

      assertEquals( [ "show", "hide" ], typeLog );
    },

    testFireHideMnemonics_multipleModifier : function() {
      handler.setActivator( "CTRL+ALT" );

      TestUtil.keyDown( shell, "Control", ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );
      TestUtil.keyUp( shell, "Control", DomEvent.ALT_MASK );

      assertEquals( [ "show", "hide" ], typeLog );
    },

    testFireHideMnemonics_shellDeActivate : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      shell.setActive( false );

      assertEquals( [ "show", "hide" ], typeLog );
    },

    testDoNotFireShowMnemonics_wrongModifier : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Alt", DomEvent.ALT_MASK );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_shellNotActive : function() {
      handler.setActivator( "CTRL" );
      shell.setActive( false );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_wrongSecondModifier : function() {
      handler.setActivator( "CTRL+ALT" );

      TestUtil.keyDown( shell, "Shift", DomEvent.ALT_MASK );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_notModifierKey : function() {
      handler.setActivator( "CTRL+ALT" );

      TestUtil.keyDown( shell, "B", ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_noActivatorSet : function() {
      TestUtil.keyDown( shell, "Control", ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [], typeLog );
    },

    testDoNotFireActivateWithoutShow : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "B", DomEvent.CTRL_MASK );

      assertEquals( [], typeLog );
    },

    testDoNotFireTriggerWithoutShow : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "B", DomEvent.CTRL_MASK );

      assertEquals( [], typeLog );
    },

    testFireTrigger : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyDown( shell, "B", DomEvent.CTRL_MASK );

      assertEquals( [ "show", "trigger" ], typeLog );
    },

    testFireTrigger_toSeeableWidgetsOnly : function() {
      handler.setActivator( "CTRL" );
      widget.setVisibility( false );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyDown( shell, "B", DomEvent.CTRL_MASK );

      assertEquals( [ "show" ], typeLog );
    },

    testFireTrigger_charCodeIsSet : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyDown( shell, "B", DomEvent.CTRL_MASK );

      assertEquals( [ 66 ], charLog );
    },

    // Alwasy use upper key char code to prevent confusion and support shift
    testFireTrigger_charCodeIsAlwaysUpper : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyDown( shell, "b", DomEvent.CTRL_MASK );

      assertEquals( [ 66 ], charLog );
    },

    testFireTrigger_noSuccessAllowsKeyEvent : function() {
      handler.setActivator( "CTRL" );
      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );

      widget.focus();
      TestUtil.keyDown( widget, "B", DomEvent.CTRL_MASK );

      assertEquals( [ "keydown", "keypress" ], keyLog );
    },

    testFireTrigger_successStopsKeyEvent : function() {
      handler.setActivator( "CTRL" );
      success = true;
      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );

      widget.focus();
      TestUtil.keyDown( widget, "B", DomEvent.CTRL_MASK );

      var expected = rwt.client.Client.isGecko() ? [ "keypress" ] : [];
      assertEquals( expected, keyLog );
    },

    testFireTrigger_successStopsActiveKey : function() {
      handler.setActivator( "CTRL" );
      success = true;
      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      var keyUtil = rwt.remote.KeyEventSupport.getInstance();
      keyUtil.setKeyBindings( { "CTRL+#66" : true } );
      widget.focus();
      TestUtil.clearRequestLog();

      TestUtil.keyDown( widget, "B", DomEvent.CTRL_MASK );
      assertEquals( 0, TestUtil.getRequestsSend() );
    },

    testFireTrigger_successStopsTriggerEvent : function() {
      handler.setActivator( "CTRL" );
      success = true;
      widget.focus();
      var widgetTwo = TestUtil.createWidgetByProtocol( "w4", "w2" );
      TestUtil.flush();
      var secondLog = [];
      handler.add( widgetTwo, function( event ) {
        if( event.type === "trigger" ) {
          secondLog.push( event );
          event.success = true;
        }
      } );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyDown( widget, "B", DomEvent.CTRL_MASK );

      var totalSuccess = charLog.length + secondLog.length;
      assertEquals( 1, totalSuccess );
    },

    testFireTrigger_menuRecievesEventLast : function() {
      handler.setActivator( "CTRL" );
      this._createMenu();
      var menuItem = rwt.remote.ObjectRegistry.getObject( "w5" );
      // widget from setup may be first in any case, need a new one:
      var widgetTwo = TestUtil.createWidgetByProtocol( "w6", "w2" );
      TestUtil.flush();
      var order = [];
      handler.add( widgetTwo, function( event ) {
        if( event.type === "trigger" ) {
          order.push( widgetTwo );
        }
      } );
      handler.add( menuItem, function( event ) {
        if( event.type === "trigger" ) {
          order.push( menuItem );
        }
      } );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyDown( widget, "B", DomEvent.CTRL_MASK );

      assertEquals( [ widgetTwo, menuItem ], order );
    },

    testDeactivate_activateMenuWithMnemonics : function() {
      handler.setActivator( "CTRL" );
      this._createMenu();
      var menuBar = rwt.remote.ObjectRegistry.getObject( "w4" );
      var menuItem = rwt.remote.ObjectRegistry.getObject( "w5" );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyUp( shell, "Control", 0 );

      assertTrue( menuBar.getActive() );
      assertTrue( menuBar.getMnemonics() );
    },

    testDeactivate_doNotActivateMenuIfKeyWasPressed : function() {
      handler.setActivator( "CTRL" );
      this._createMenu();
      var menuBar = rwt.remote.ObjectRegistry.getObject( "w4" );
      var menuItem = rwt.remote.ObjectRegistry.getObject( "w5" );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyDown( shell, "X", DomEvent.CTRL_MASK );
      TestUtil.keyUp( shell, "X", DomEvent.CTRL_MASK );
      TestUtil.keyUp( shell, "Control", 0 );

      assertFalse( menuBar.getActive() );
    },

    testDeactivate_activateMenuWhenAcivatorWasHeldDown : function() {
      handler.setActivator( "CTRL" );
      this._createMenu();
      var menuBar = rwt.remote.ObjectRegistry.getObject( "w4" );
      var menuItem = rwt.remote.ObjectRegistry.getObject( "w5" );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyHold( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyUp( shell, "Control", 0 );

      assertTrue( menuBar.getActive() );
    },

    testActivate_notCalledIfKeysHoldDownFromMenuDeactivate : function() {
      handler.setActivator( "CTRL" );
      this._createMenu();
      var menuBar = rwt.remote.ObjectRegistry.getObject( "w4" );
      var menuItem = rwt.remote.ObjectRegistry.getObject( "w5" );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyUp( shell, "Control", 0 ); // menu made active
      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK ); //menu de-activate
      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK ); // should not activate anything

      assertFalse( menuBar.getActive() );
      assertFalse( handler.isActive() );
    },

    testDeactivate_activatesMenuAfterItWasDeacivatedProgramatically : function() {
      handler.setActivator( "CTRL" );
      this._createMenu();
      var menuBar = rwt.remote.ObjectRegistry.getObject( "w4" );
      var menuItem = rwt.remote.ObjectRegistry.getObject( "w5" );
      menuItem.setText( "foo" );
      menuItem.setMnemonicIndex( 1 );
      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyUp( shell, "Control", 0 );
      menuBar.setActive( false );
      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyUp( shell, "Control", 0 );

      assertTrue( menuBar.getActive() );
    },

    _createMenu : function() {
      MessageProcessor.processOperationArray(
        [ "create", "w4", "rwt.widgets.Menu", { "style" : [ "BAR" ], "parent" : "w2" } ]
      );
      var itemProperties = { "style" : [ "CASCADE" ], "parent" : "w4", "index" : 0 };
      MessageProcessor.processOperationArray(
        [ "create", "w5", "rwt.widgets.MenuItem", itemProperties ]
      );
    }
  }

} );

}());