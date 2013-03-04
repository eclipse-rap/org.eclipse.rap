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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.MnemonicHandlerTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      widget = TestUtil.createWidgetByProtocol( "w3", "w2" );
      typeLog = [];
      charLog = [];
      handler.add( widget, function( event ) {
        typeLog.push( event.type );
        if( event.type === "trigger" ) {
          charLog.push( event.charCode );
        }
      } );
      TestUtil.flush();
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

      TestUtil.press( shell, "Control", false, DomEvent.CTRL_MASK );

      assertEquals( [ "show" ], typeLog );
    },

    testFireShowMnemonics_MultipleModifier : function() {
      handler.setActivator( "CTRL+ALT" );

      TestUtil.press( shell, "Control", false, ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [ "show" ], typeLog );
    },

    testDoNotFireShowMnemonics_WrongModifier : function() {
      handler.setActivator( "CTRL" );

      TestUtil.press( shell, "Alt", false, DomEvent.ALT_MASK );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_WrongSecondModifier : function() {
      handler.setActivator( "CTRL+ALT" );

      TestUtil.press( shell, "Shift", false, DomEvent.ALT_MASK );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_NotModifierKey : function() {
      handler.setActivator( "CTRL+ALT" );

      TestUtil.press( shell, "B", false, ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [], typeLog );
    },

    testDoNotFireShowMnemonics_NoActivatorSet : function() {
      TestUtil.press( shell, "Control", false, ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [], typeLog );
    },

    testDoNotFireActivateWithoutShow : function() {
      handler.setActivator( "CTRL" );

      TestUtil.press( shell, "B", false, DomEvent.CTRL_MASK );

      assertEquals( [], typeLog );
    },

    testDoNotFireTriggerWithoutShow : function() {
      handler.setActivator( "CTRL" );

      TestUtil.press( shell, "B", false, DomEvent.CTRL_MASK );

      assertEquals( [], typeLog );
    },

    testFireTrigger : function() {
      handler.setActivator( "CTRL" );

      TestUtil.press( shell, "Control", false, DomEvent.CTRL_MASK );
      TestUtil.press( shell, "B", false, DomEvent.CTRL_MASK );

      assertEquals( [ "show", "trigger" ], typeLog );
    },

    testFireTrigger_CharCodeIsSet : function() {
      handler.setActivator( "CTRL" );

      TestUtil.press( shell, "Control", false, DomEvent.CTRL_MASK );
      TestUtil.press( shell, "B", false, DomEvent.CTRL_MASK );

      assertEquals( [ 66 ], charLog );
    },

    // Alwasy use upper key char code to prevent confusion and support shift
    testFireTrigger_CharCodeIsAlwaysUpper : function() {
      handler.setActivator( "CTRL" );

      TestUtil.press( shell, "Control", false, DomEvent.CTRL_MASK );
      TestUtil.press( shell, "b", false, DomEvent.CTRL_MASK );

      assertEquals( [ 66 ], charLog );
    }

  }

} );

}());