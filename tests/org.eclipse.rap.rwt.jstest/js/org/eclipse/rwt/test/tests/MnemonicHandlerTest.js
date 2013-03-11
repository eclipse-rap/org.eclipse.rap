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
var DomEvent = rwt.event.DomEvent;

var handler = rwt.widgets.util.MnemonicHandler.getInstance();
var shell;
var widget;
var typeLog;
var charLog;
var keyLog;
var success;

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

    testFireShowMnemonics_MultipleModifier : function() {
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

    testFireHideMnemonics_MultipleModifier : function() {
      handler.setActivator( "CTRL+ALT" );

      TestUtil.keyDown( shell, "Control", ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );
      TestUtil.keyUp( shell, "Control", DomEvent.ALT_MASK );

      assertEquals( [ "show", "hide" ], typeLog );
    },

    testFireHideMnemonics_ShellDeActivate : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      shell.setActive( false );

      assertEquals( [ "show", "hide" ], typeLog );
    },

    testDoNotFireShowMnemonics_WrongModifier : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Alt", DomEvent.ALT_MASK );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_ShellNotActive : function() {
      handler.setActivator( "CTRL" );
      shell.setActive( false );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_WrongSecondModifier : function() {
      handler.setActivator( "CTRL+ALT" );

      TestUtil.keyDown( shell, "Shift", DomEvent.ALT_MASK );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_NotModifierKey : function() {
      handler.setActivator( "CTRL+ALT" );

      TestUtil.keyDown( shell, "B", ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_NoActivatorSet : function() {
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

    testFireTrigger_ToSeeableWidgetsOnly : function() {
      handler.setActivator( "CTRL" );
      widget.setVisibility( false );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyDown( shell, "B", DomEvent.CTRL_MASK );

      assertEquals( [ "show" ], typeLog );
    },

    testFireTrigger_CharCodeIsSet : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyDown( shell, "B", DomEvent.CTRL_MASK );

      assertEquals( [ 66 ], charLog );
    },

    // Alwasy use upper key char code to prevent confusion and support shift
    testFireTrigger_CharCodeIsAlwaysUpper : function() {
      handler.setActivator( "CTRL" );

      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      TestUtil.keyDown( shell, "b", DomEvent.CTRL_MASK );

      assertEquals( [ 66 ], charLog );
    },

    testFireTrigger_NoSuccessAllowsKeyEvent : function() {
      handler.setActivator( "CTRL" );
      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );

      widget.focus();
      TestUtil.keyDown( widget, "B", DomEvent.CTRL_MASK );

      assertEquals( [ "keydown", "keypress" ], keyLog );
    },

    testFireTrigger_SuccessStopsKeyEvent : function() {
      handler.setActivator( "CTRL" );
      success = true;
      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );

      widget.focus();
      TestUtil.keyDown( widget, "B", DomEvent.CTRL_MASK );

      var expected = rwt.client.Client.isGecko() ? [ "keypress" ] : [];
      assertEquals( expected, keyLog );
    },

    testFireTrigger_SuccessStopsActiveKey : function() {
      handler.setActivator( "CTRL" );
      success = true;
      TestUtil.keyDown( shell, "Control", DomEvent.CTRL_MASK );
      var keyUtil = rwt.remote.KeyEventSupport.getInstance();
      keyUtil.setKeyBindings( { "CTRL+#66" : true } );
      widget.focus();
      TestUtil.clearRequestLog();

      TestUtil.keyDown( widget, "B", DomEvent.CTRL_MASK );
      assertEquals( 0, TestUtil.getRequestsSend() );
    }

  }

} );

}());