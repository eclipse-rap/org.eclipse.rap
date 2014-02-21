/*******************************************************************************
 * Copyright (c) 2010, 2014 EclipseSource and others.
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

      assertEquals( [ "Selection", "DefaultSelection" ], handler.events );
    },

    testCreateSpinnerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Spinner );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "spinner", widget.getAppearance() );
      assertTrue( widget.getEditable() );
      assertFalse( widget.getWrap() );
      assertEquals( 0, widget.getMin() );
      assertEquals( 100, widget.getMax() );
      assertEquals( 0, widget.getValue() );
      assertEquals( 0, widget.getDigits() );
      shell.destroy();
      widget.destroy();
    },

    testCreateSpinnerWithWrapAdndReadOnlyByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [ "READ_ONLY", "WRAP" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertFalse( widget.getEditable() );
      assertTrue( widget.getWrap() );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "minimum" : 50
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 50, widget.getMin() );
      shell.destroy();
      widget.destroy();
    },

    testSetMaximumByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "maximum" : 150
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 150, widget.getMax() );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumBiggerThanCurrentMaximumByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "minimum" : 150,
          "maximum" : 200
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 150, widget.getMin() );
      assertEquals( 200, widget.getMax() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "selection" : 50
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 50, widget.getValue() );
      shell.destroy();
      widget.destroy();
    },

    testSetDigitsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "digits" : 2
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 2, widget.getDigits() );
      shell.destroy();
      widget.destroy();
    },

    testSetIncrementByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "increment" : 5
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 5, widget.getIncrementAmount() );
      assertEquals( 5, widget.getWheelIncrementAmount() );
      shell.destroy();
      widget.destroy();
    },

    testSetPageIncrementByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "pageIncrement" : 20
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 20, widget.getPageIncrementAmount() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextLimitByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "textLimit" : 3
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 3, widget._textfield.getMaxLength() );
      shell.destroy();
      widget.destroy();
    },

    testSetDecimalSeparatorLimitByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "decimalSeparator" : ","
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( ",", widget.getDecimalSeparator() );
      shell.destroy();
      widget.destroy();
    },

    testGetManager : function() {
      var spinner = new rwt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 59, 60, 5, 20 );
      spinner.setZIndex( 299 );
      spinner.setTabIndex( 58 );
      assertTrue( spinner.getManager() instanceof rwt.util.Range );
      spinner.destroy();
    },

    testDispose : function() {
      var spinner = new rwt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 59, 60, 5, 20 );
      TestUtil.flush();
      spinner.destroy();
      TestUtil.flush();
      assertTrue( spinner.isDisposed() );
    },

    testAcceptNumbersOnly : function() {
      var spinner = new rwt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 0, 60, 5, 30 );
      TestUtil.flush();
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
      var spinner = new rwt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 0, 60, 5, 30 );
      TestUtil.flush();
      spinner.focus();

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
      var spinner = new rwt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 0, 60, 5, 30 );
      TestUtil.flush();
      spinner.focus();

      var ctrl = rwt.event.DomEvent.CTRL_MASK;
      var domEvent = TestUtil.fireFakeKeyDomEvent( spinner._textfield, "keypress", "C", ctrl );
      assertFalse( EventHandlerUtil.wasStopped( domEvent ) );

      spinner.destroy();
    },

    testSendSelectionEvent : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      var widget = ObjectRegistry.getObject( "w3" );

      widget.setValue( 10 );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertEquals( 10, messages[ 0 ].findSetProperty( "w3", "selection" ) );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w3", "Selection" ) );
      shell.destroy();
    },

    testMergeSendSelectionEvent : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      var widget = ObjectRegistry.getObject( "w3" );

      widget.setValue( 10 );
      widget.setValue( 20 );
      widget.setValue( 30 );
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertEquals( 30, messages[ 0 ].findSetProperty( "w3", "selection" ) );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w3", "Selection" ) );
      shell.destroy();
    }

  }

} );

}() );
