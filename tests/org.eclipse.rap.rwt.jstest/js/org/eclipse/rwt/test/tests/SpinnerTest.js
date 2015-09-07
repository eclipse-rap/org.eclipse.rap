/*******************************************************************************
 * Copyright (c) 2010, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectRegistry = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;
var EventHandlerUtil = rwt.event.EventHandlerUtil;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.SpinnerTest", {

  extend : rwt.qx.Object,

  construct : function() {
    org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
  },

  members : {

    testSpinnerHandlerEventsList : function() {
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Spinner" );

      assertEquals( [ "Selection", "DefaultSelection", "Modify" ], handler.events );
    },

    testCreateSpinnerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );

      var spinner = this._createSpinnerByProtocol( "w3", "w2" );

      assertTrue( spinner instanceof rwt.widgets.Spinner );
      assertIdentical( shell, spinner.getParent() );
      assertTrue( spinner.getUserData( "isControl") );
      assertEquals( "spinner", spinner.getAppearance() );
      assertTrue( spinner.getEditable() );
      assertFalse( spinner.getWrap() );
      assertEquals( 0, spinner.getMin() );
      assertEquals( 100, spinner.getMax() );
      assertEquals( 0, spinner.getValue() );
      assertEquals( 0, spinner.getDigits() );
      shell.destroy();
    },

    testCreateSpinnerWithWrapAdndReadOnlyByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );

      var spinner = this._createSpinnerByProtocol( "w3", "w2", [ "READ_ONLY", "WRAP" ] );

      assertFalse( spinner.getEditable() );
      assertTrue( spinner.getWrap() );
      shell.destroy();
    },

    testSetMinimumByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );

      TestUtil.protocolSet( "w3", { "minimum" : 50 } );

      assertEquals( 50, spinner.getMin() );
      shell.destroy();
    },

    testSetMaximumByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );

      TestUtil.protocolSet( "w3", { "maximum" : 150 } );

      assertEquals( 150, spinner.getMax() );
      shell.destroy();
    },

    testSetMinimumBiggerThanCurrentMaximumByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );

      TestUtil.protocolSet( "w3", { "minimum" : 150, "maximum" : 200 } );

      assertEquals( 150, spinner.getMin() );
      assertEquals( 200, spinner.getMax() );
      shell.destroy();
    },

    testSetSelectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );

      TestUtil.protocolSet( "w3", { "selection" : 50 } );

      assertEquals( 50, spinner.getValue() );
      shell.destroy();
    },

    testSetDigitsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );

      TestUtil.protocolSet( "w3", { "digits" : 2 } );

      assertEquals( 2, spinner.getDigits() );
      shell.destroy();
    },

    testSetIncrementByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );

      TestUtil.protocolSet( "w3", { "increment" : 5 } );

      assertEquals( 5, spinner.getIncrementAmount() );
      assertEquals( 5, spinner.getWheelIncrementAmount() );
      shell.destroy();
    },

    testSetPageIncrementByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );

      TestUtil.protocolSet( "w3", { "pageIncrement" : 20 } );

      assertEquals( 20, spinner.getPageIncrementAmount() );
      shell.destroy();
    },

    testSetTextLimitByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );

      TestUtil.protocolSet( "w3", { "textLimit" : 3 } );

      assertEquals( 3, spinner._textfield.getMaxLength() );
      shell.destroy();
    },

    testSetDecimalSeparatorLimitByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );

      TestUtil.protocolSet( "w3", { "decimalSeparator" : "," } );

      assertEquals( ",", spinner.getDecimalSeparator() );
      shell.destroy();
    },

    testGetManager : function() {
      var spinner = this._createDefaultSpinner();

      spinner.setZIndex( 299 );
      spinner.setTabIndex( 58 );

      assertTrue( spinner.getManager() instanceof rwt.util.Range );
      spinner.destroy();
    },

    testDispose : function() {
      var spinner = this._createDefaultSpinner();

      spinner.destroy();
      TestUtil.flush();

      assertTrue( spinner.isDisposed() );
    },

    testAcceptNumbersOnly : function() {
      var spinner = this._createDefaultSpinner();
      spinner.focus();

      var domEvent = TestUtil.fireFakeKeyDomEvent( spinner._textfield, "keypress", "1" );
      assertFalse( EventHandlerUtil.wasStopped( domEvent ) );

      domEvent = TestUtil.fireFakeKeyDomEvent( spinner._textfield, "keypress", "C" );
      assertTrue( EventHandlerUtil.wasStopped( domEvent ) );

      var shift = rwt.event.DomEvent.SHIFT_MASK;
      domEvent = TestUtil.fireFakeKeyDomEvent( spinner._textfield, "keypress", "C", shift );
      assertTrue( EventHandlerUtil.wasStopped( domEvent ) );

      spinner.destroy();
    },

    testAcceptMinus : function() {
      var spinner = this._createDefaultSpinner();
      spinner.focus();
      // ensure caret position on iOs:
      spinner._textfield._setSelectionStart( 0 );

      var domEvent = TestUtil.fireFakeKeyDomEvent( spinner._textfield, "keypress", "-" );
      assertFalse( EventHandlerUtil.wasStopped( domEvent ) );

      rwt.remote.EventUtil.setSuspended( true );
      spinner._textfield.setValue( "-1" );
      rwt.remote.EventUtil.setSuspended( false );
      domEvent = TestUtil.fireFakeKeyDomEvent( spinner._textfield, "keypress", "-" );
      assertTrue( EventHandlerUtil.wasStopped( domEvent ) );

      spinner.destroy();
    },

    testCopyPasteIsNotBlocked : function() {
      var spinner = this._createDefaultSpinner();
      spinner.focus();

      var ctrl = rwt.event.DomEvent.CTRL_MASK;
      var domEvent = TestUtil.fireFakeKeyDomEvent( spinner._textfield, "keypress", "C", ctrl );
      assertFalse( EventHandlerUtil.wasStopped( domEvent ) );

      spinner.destroy();
    },

    testSendSelectionEvent : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );
      TestUtil.protocolListen( "w3", { "Selection" : true } );

      spinner.setValue( 10 );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertEquals( 10, messages[ 0 ].findSetProperty( "w3", "selection" ) );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w3", "Selection" ) );
      shell.destroy();
    },

    testSendModifyEvent : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );
      TestUtil.protocolListen( "w3", { "Modify" : true } );

      spinner.setValue( 10 );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertNotNull( messages[ 0 ].findNotifyOperation( "w3", "Modify" ) );
      shell.destroy();
    },

    testMergeSendSelectionEvent : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var spinner = this._createSpinnerByProtocol( "w3", "w2" );
      TestUtil.protocolListen( "w3", { "Selection" : true } );

      spinner.setValue( 10 );
      spinner.setValue( 20 );
      spinner.setValue( 30 );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertEquals( 30, messages[ 0 ].findSetProperty( "w3", "selection" ) );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w3", "Selection" ) );
      shell.destroy();
    },

    testTextColor : function() {
      var spinner = this._createDefaultSpinner();
      spinner.setTextColor( "#FF0000" );
      var style = spinner._textfield.getInputElement().style;
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      spinner.setTextColor( "#00FF00" );
      assertEquals( [ 0, 255, 0 ], rwt.util.Colors.stringToRgb( style.color ) );
      spinner.destroy();
    },

    testTextColor_byChangingEnabled_withoutUserColor : function() {
      var spinner = this._createDefaultSpinner();
      this._fakeAppearance( "#00FF00", "#FF0000" );
      var style = spinner._textfield.getInputElement().style;

      spinner.setEnabled( false );
      TestUtil.flush();
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      spinner.setEnabled( true );
      TestUtil.flush();
      assertEquals( [ 0, 255, 0 ], rwt.util.Colors.stringToRgb( style.color ) );
      spinner.destroy();
    },

    testTextColor_byChangingEnabled_withUnchangedUserColor : function() {
      var spinner = this._createDefaultSpinner();
      this._fakeAppearance( "#00FF00", "#FF0000" );
      spinner.setTextColor( "#0000FF" );
      var style = spinner._textfield.getInputElement().style;

      spinner.setEnabled( false );
      TestUtil.flush();
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      spinner.setEnabled( true );
      TestUtil.flush();
      assertEquals( [ 0, 0, 255 ], rwt.util.Colors.stringToRgb( style.color ) );
      spinner.destroy();
    },

    testTextColor_byChangingEnabled_withChangedUserColor : function() {
      var spinner = this._createDefaultSpinner();
      this._fakeAppearance( "#00FF00", "#FF0000" );
      spinner.setTextColor( "#0000FF" );
      var style = spinner._textfield.getInputElement().style;

      spinner.setEnabled( false );
      TestUtil.flush();
      assertEquals( [ 255, 0, 0 ], rwt.util.Colors.stringToRgb( style.color ) );

      spinner.setTextColor( "#FF00FF" );
      spinner.setEnabled( true );
      TestUtil.flush();
      assertEquals( [ 255, 0, 255 ], rwt.util.Colors.stringToRgb( style.color ) );
      spinner.destroy();
    },

    testSetDirection_setsReverseChildrenOrder : function() {
      var spinner = this._createDefaultSpinner();

      spinner.setDirection( "rtl" );
      TestUtil.flush();

      assertTrue( spinner.getReverseChildrenOrder() );
      spinner.destroy();
    },

    testSetDirection_addsStateToSubWidgets : function() {
      var spinner = this._createDefaultSpinner();

      spinner.setDirection( "rtl" );
      TestUtil.flush();

      assertTrue( spinner._upbutton.hasState( "rwt_RIGHT_TO_LEFT" ) );
      assertTrue( spinner._downbutton.hasState( "rwt_RIGHT_TO_LEFT" ) );
      assertTrue( spinner._textfield.hasState( "rwt_RIGHT_TO_LEFT" ) );
      spinner.destroy();
    },

    //////////
    // Helpers

    _createDefaultSpinner : function() {
      var spinner = new rwt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 0, 60, 5, 30 );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Spinner" );
      rwt.remote.ObjectRegistry.add( "w3", spinner, handler );
      TestUtil.flush();
      return spinner;
    },

    _fakeAppearance : function( defaultTextColor, disabledTextColor) {
      TestUtil.fakeAppearance( "spinner", {
        "style" : function( states ) {
          return {
            textColor : states.disabled ? disabledTextColor : defaultTextColor
          };
        }
      } );
      TestUtil.fakeAppearance( "spinner-text-field", {
        "style" : function( states ) {
          return {
            textColor : states.disabled ? disabledTextColor : defaultTextColor
          };
        }
      } );
    },

    _createSpinnerByProtocol : function( id, parentId, style ) {
      Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : style ? style : [],
          "parent" : parentId
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    }

  }

} );

}() );
