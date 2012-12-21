/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.SpinnerTest", {
  extend : rwt.qx.Object,

  construct : function() {
    org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
  },

  members : {

    testCreateSpinnerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
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
      assertFalse( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testCreateSpinnerWithWrapAdndReadOnlyByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [ "READ_ONLY", "WRAP" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertFalse( widget.getEditable() );
      assertTrue( widget.getWrap() );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "minimum" : 50
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 50, widget.getMin() );
      shell.destroy();
      widget.destroy();
    },

    testSetMaximumByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "maximum" : 150
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 150, widget.getMax() );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumBiggerThanCurrentMaximumByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
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
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 150, widget.getMin() );
      assertEquals( 200, widget.getMax() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "selection" : 50
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 50, widget.getValue() );
      shell.destroy();
      widget.destroy();
    },

    testSetDigitsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "digits" : 2
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 2, widget.getDigits() );
      shell.destroy();
      widget.destroy();
    },

    testSetIncrementByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "increment" : 5
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 5, widget.getIncrementAmount() );
      assertEquals( 5, widget.getWheelIncrementAmount() );
      shell.destroy();
      widget.destroy();
    },

    testSetPageIncrementByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "pageIncrement" : 20
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 20, widget.getPageIncrementAmount() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextLimitByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "textLimit" : 3
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 3, widget._textfield.getMaxLength() );
      shell.destroy();
      widget.destroy();
    },

    testSetDecimalSeparatorLimitByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "decimalSeparator" : ","
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( ",", widget.getDecimalSeparator() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testGetManager : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var spinner = new rwt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 59, 60, 5, 20 );
      spinner.setZIndex( 299 );
      spinner.setTabIndex( 58 );
      assertTrue( spinner.getManager() instanceof rwt.util.Range );
      spinner.destroy();
    },

    testDispose : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var spinner = new rwt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 59, 60, 5, 20 );
      TestUtil.flush();
      spinner.destroy();
      TestUtil.flush();
      assertTrue( spinner.isDisposed() );
    },

    testSendSelectionEvent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );

      widget.setValue( 10 );
      TestUtil.forceInterval( rwt.remote.Server.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertEquals( 10, messages[ 0 ].findSetProperty( "w3", "selection" ) );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w3", "Selection" ) );
      shell.destroy();
    },

    testMergeSendSelectionEvent : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );

      widget.setValue( 10 );
      widget.setValue( 20 );
      widget.setValue( 30 );
      TestUtil.forceInterval( rwt.remote.Server.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var messages = TestUtil.getMessages();
      assertEquals( 30, messages[ 0 ].findSetProperty( "w3", "selection" ) );
      assertNotNull( messages[ 0 ].findNotifyOperation( "w3", "Selection" ) );
      shell.destroy();
    }

  }

} );