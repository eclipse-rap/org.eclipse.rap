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
      handler.setActivator( null );
      shell = TestUtil.createShellByProtocol( "w2" );
      TestUtil.flush();
    },

    tearDown : function() {
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

      TestUtil.press( shell, "a", false, ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [], type );
    },

    testDoNotFireShowMnemonics_NoActivatorSet : function() {
      var type = [];
      handler.addEventListener( "mnemonic", function( event ) {
        type.push( event.type );
      } );

      TestUtil.press( shell, "Control", false, ( DomEvent.CTRL_MASK | DomEvent.ALT_MASK ) );

      assertEquals( [], type );
    }


  }

} );

}());