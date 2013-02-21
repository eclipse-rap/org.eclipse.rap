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

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.MnemonicHandlerTest", {

  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.flush();
    },

    tearDown : function() {
      handler.setActivator( null );
      handler.deactivate();
      shell.destroy();
      shell = null;
    },

    testFireShowMnemonics : function() {
      var type = [];
      handler.setActivator( "CTRL" );
      handler.addEventListener( "mnemonic", function( event ) {
        type.push( event.type );
      } );

      TestUtil.press( shell, "Control", false, DomEvent.CTRL_MASK );

      assertEquals( [ "show" ], type );
    },

    testFireShowMnemonics_MultipleModifier : function() {
      var type = [];
      handler.setActivator( "CTRL+ALT" );
      handler.addEventListener( "mnemonic", function( event ) {
        type.push( event.type );
      } );

      TestUtil.press( shell, "Control", false, ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [ "show" ], type );
    },

    testDoNotFireShowMnemonics_WrongModifier : function() {
      var type = [];
      handler.setActivator( "CTRL" );
      handler.addEventListener( "mnemonic", function( event ) {
        type.push( event.type );
      } );

      TestUtil.press( shell, "Alt", false, DomEvent.ALT_MASK );

      assertEquals( [], type );
    },

    testDoNotFireShowMnemonics_WrongSecondModifier : function() {
      var type = [];
      handler.setActivator( "CTRL+ALT" );
      handler.addEventListener( "mnemonic", function( event ) {
        type.push( event.type );
      } );

      TestUtil.press( shell, "Shift", false, DomEvent.ALT_MASK );

      assertEquals( [], type );
    },

    testDoNotFireShowMnemonics_NotModifierKey : function() {
      var type = [];
      handler.setActivator( "CTRL+ALT" );
      handler.addEventListener( "mnemonic", function( event ) {
        type.push( event.type );
      } );

      TestUtil.press( shell, "B", false, ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [], type );
    },

    testDoNotFireShowMnemonics_NoActivatorSet : function() {
      var type = [];
      handler.addEventListener( "mnemonic", function( event ) {
        type.push( event.type );
      } );

      TestUtil.press( shell, "Control", false, ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [], type );
    },

    testDoNotFireActivateWithoutShow : function() {
      var type = [];
      handler.setActivator( "CTRL" );
      handler.addEventListener( "mnemonic", function( event ) {
        type.push( event.type );
      } );

      TestUtil.press( shell, "B", false, DomEvent.CTRL_MASK );

      assertEquals( [], type );
    },

    testDoNotFireTriggerWithoutShow : function() {
      var type = [];
      handler.setActivator( "CTRL" );
      handler.addEventListener( "mnemonic", function( event ) {
        type.push( event.type );
      } );

      TestUtil.press( shell, "B", false, DomEvent.CTRL_MASK );

      assertEquals( [], type );
    },

    testFireTrigger : function() {
      var type = [];
      handler.setActivator( "CTRL" );
      handler.addEventListener( "mnemonic", function( event ) {
        type.push( event.type );
      } );

      TestUtil.press( shell, "Control", false, DomEvent.CTRL_MASK );
      TestUtil.press( shell, "B", false, DomEvent.CTRL_MASK );

      assertEquals( [ "show", "trigger" ], type );
    },

    testFireTrigger_CharCodeIsSet : function() {
      var character = [];
      handler.setActivator( "CTRL" );
      handler.addEventListener( "mnemonic", function( event ) {
        if( event.type === "trigger" ) {
          character.push( event.charCode );
        }
      } );

      TestUtil.press( shell, "Control", false, DomEvent.CTRL_MASK );
      TestUtil.press( shell, "B", false, DomEvent.CTRL_MASK );

      assertEquals( [ 66 ], character );
    },

    // Alwasy use upper key char code to prevent confusion and support shift
    testFireTrigger_CharCodeIsAlwaysUpper : function() {
      var character = [];
      handler.setActivator( "CTRL" );
      handler.addEventListener( "mnemonic", function( event ) {
        if( event.type === "trigger" ) {
          character.push( event.charCode );
        }
      } );

      TestUtil.press( shell, "Control", false, DomEvent.CTRL_MASK );
      TestUtil.press( shell, "b", false, DomEvent.CTRL_MASK );

      assertEquals( [ 66 ], character );
    }

  }

} );

}());